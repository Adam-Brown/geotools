package org.geotools.feature.complex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.AttributeImpl;
import org.geotools.feature.FeatureBuilder;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;

public class ComplexFeatureBuilder extends FeatureBuilder<Feature, FeatureType> {
	// TODO: the example of AppSchemaFeatureFactory doesn't use the Map, it just creates a list
	// of properties. I've added the map so that I can group properties by name to make sure
	// that I don't allow the user to add more than they're supposed to. Make sure this is OK...
	Map<Name, ArrayList<Property>> values = new HashMap<Name, ArrayList<Property>>();

	public ComplexFeatureBuilder(FeatureType featureType) {
        this(featureType, CommonFactoryFinder.getFeatureFactory(null));
    }

	protected ComplexFeatureBuilder(FeatureType featureType, FeatureFactory factory) {
		super(featureType, factory);
	}

	@Override
	public Feature buildFeature(String id) {
		// Instantiate if null:
		id = id == null ? FeatureBuilder.createDefaultFeatureId() : id;

		// Validate the values against the featureType; we need to make sure that requirements are honoured:
		for (PropertyDescriptor propertyDescriptor : super.featureType.getDescriptors()) {
			Name name = propertyDescriptor.getName();

			// Create a List of Properties for this name if we don't already have one:
			if (!values.containsKey(name)) {
				values.put(name, new ArrayList<Property>());
			}

			// Get the List of Properties:
			List<Property> properties = values.get(name);
			
			// See if there's a mismatch between the number of properties and minOccurs value:
			int minOccurs = propertyDescriptor.getMinOccurs();
			int numberOfProperties = properties.size();

			if (numberOfProperties < minOccurs) {
				// If the value is nillable anyway then just default it null: 
				if (propertyDescriptor.isNillable()) {
//
//					do {
//						System.out.println("Adding null here for " + name);
//
//						// TODO: problem with the types here.
//
//						properties.add(new AttributeImpl(propertyDescriptor.getType().getBinding().cast(null), propertyDescriptor));
//						numberOfProperties++;
//					} while (numberOfProperties < minOccurs);
				}
				
				// TODO: I was wondering if you could have an if-else here to try to apply default values if they're set..
				// it seems like a good idea but the only problem is that they're only present on the AttributeDescriptors...
				else {
					throw new IllegalStateException(
						String.format(
							"Failed to build feature '%s'; its property '%s' requires at least %s occurrence(s) but number of occurrences was %s.",
							featureType.getName(),
							name,
							minOccurs,
							numberOfProperties));
				}
			}
		}

		// Merge the Map<String, ArrayList<Property>> into one collection of properties:
		Collection<Property> properties = new ArrayList<Property>();
		for (Name key : values.keySet()) {
			properties.addAll(values.get(key));
		}

		return factory.createFeature(properties, this.featureType, id);	
	}

	public void append(Name name, Object value) {
		PropertyDescriptor propertyDescriptor = featureType.getDescriptor(name);

		// The 'name' must exist in the type, if not, throw an exception:
		if (propertyDescriptor == null) {
			throw new IllegalArgumentException(
				String.format(
					"The name '%s' is not a valid descriptor name for the type '%s'.",
					name,
					this.featureType.getName()));
		}

		Object convertedValue;

		Class<?> expectedClass = propertyDescriptor.getType().getBinding();
		if (value != null) {
			Class<?> providedClass = value.getClass();

			// Make sure that the provided class and the expected class match or that the expectedClass is a base class of the providedClass:
			if (!providedClass.equals(expectedClass) && !expectedClass.isAssignableFrom(providedClass)) {
				throw new IllegalArgumentException(
					String.format(
						"The value provided is an object of '%s' but the method expects an object of '%s'.", 
						providedClass,
						expectedClass));
			}

			convertedValue = super.convert(value, propertyDescriptor);
		}
		else { // value == null
			if (propertyDescriptor.isNillable()) {
				convertedValue = expectedClass.cast(null);
			}
			else {
				// A null reference has been provided for a non-nillable type.
				throw new IllegalArgumentException(
					String.format(
						"The value provided is a null reference but the property descriptor '%s' is non-nillable.",
						propertyDescriptor));
			}
		}

		// At this point the converted value has been set so we must persist it to the object's state:
		ArrayList<Property> valueList;

		if (values.containsKey(name)) {
			valueList = values.get(name);
			
			// Make sure that the list isn't already at capacity:
			int maxOccurs = propertyDescriptor.getMaxOccurs();
			if (valueList.size() == maxOccurs) {
				throw new IndexOutOfBoundsException(
					String.format(
						"You can't add another object with the name of '%s' because you already have the maximum number (%s) allowed by the property descriptor.",
						name,
						maxOccurs));
			}
		}
		else {
			valueList = new ArrayList<Property>();
			values.put(name, valueList);
		}

		// Create the AttributeImpl or XXX depending on whether or not it's a 
		
		// TODO: I don't really know if it's even possible for there to be an attribute descriptor at this level -
		// won't it always be Complex?
		
		// TODO: I think this part is wrong... shouldn't you be passing in pre-built attributes / properties?
		if (AttributeDescriptor.class.isAssignableFrom(propertyDescriptor.getClass())) {
			valueList.add(new AttributeImpl(convertedValue, (AttributeDescriptor)propertyDescriptor, null)); // TODO: Null?
		}
		else {
			
		}
	}
}












