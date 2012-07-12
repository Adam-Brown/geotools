package org.geotools.feature.wrapper;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.geotools.feature.NameImpl;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.Name;

public abstract class FeatureWrapper {
	
	private ComplexAttribute underlyingComplexAttribute;
	
	public ComplexAttribute getUnderlyingComplexAttribute() {
		return this.underlyingComplexAttribute;
	}
	
	public void setUnderlyingComplexAttribute(ComplexAttribute underlyingComplexAttribute) {
		this.underlyingComplexAttribute = underlyingComplexAttribute;
	}
	
//	/**
//	 * Attempt to wrap the feature in a FeatureWrapper class.
//	 * @param feature
//	 * 				The feature to wrap.
//	 * @param clazz
//	 * 				The class you want the feature to be wrapped as. (This will be the type that is returned).
//	 * @return
//	 * 				An object of T which is the wrapped feature.
//	 */				
	public static <T extends FeatureWrapper> T Wrap(ComplexAttribute complexAttribute, Class<T> clazz) {
		try {
			// Create a new instance of the class:
			T wrapper = clazz.newInstance();
			wrapper.setUnderlyingComplexAttribute(complexAttribute);
			
			String defaultNamespace = null;
			String defaultSeparator = null;
			
			// Get class-level XSDMapping:
			XSDMapping classLevelXSDMapping =  clazz.getAnnotation(XSDMapping.class);
			if (classLevelXSDMapping != null) {
				defaultNamespace = classLevelXSDMapping.namespace();
				defaultSeparator = classLevelXSDMapping.separator();
			}
					
			for (Field field : clazz.getFields()) {
				XSDMapping xsdMapping = field.getAnnotation(XSDMapping.class);
				
				if (xsdMapping != null) {
					String namespace = xsdMapping.namespace().equals("") ? defaultNamespace : xsdMapping.namespace();
					String separator = xsdMapping.separator().equals("") ? defaultSeparator : xsdMapping.separator();

					Name xsdName = new NameImpl(namespace, separator, xsdMapping.local());
					Class<?> fieldType = field.getType();
					
					// What kind of field is it?
					if (FeatureWrapper.class.isAssignableFrom(fieldType)) { 
						// The field's type is actually a FeatureWrapper itself so we need to recurse.

						// Because we know it's a FeatureWrapper it's safe to assume that the value is
						// a complex attribute.
						
						// The featureWrapperAttribute is like: ComplexAttributeImpl:MineName<MineNameType id=MINENAMETYPE_TYPE_1>=[...]
						ComplexAttribute featureWrapperAttribute = (ComplexAttribute)complexAttribute.getProperty(xsdName);
						
						// We get the name of its type and then use that name to access the actual property, which then gets wrapped:
						Name typeName = featureWrapperAttribute.getType().getName();
						ComplexAttribute nestedComplexAttribute = (ComplexAttribute)featureWrapperAttribute.getProperty(typeName);
						
						FeatureWrapper property = Wrap(nestedComplexAttribute, (Class<FeatureWrapper>)fieldType);
						field.set(wrapper, property);
					}
					else if (xsdMapping.collection()) {
						// Collections aren't too dissimilar, you just have to build up an array list which gets set as the field's value.
						
						// What is the collection actually of?
						// All this line is doing is taking a type like: Collection<MineNamePropertyType> and giving me MineNamePropertyType.
						Class<?> collectionType = (Class<?>)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
												
						ArrayList<Object> collection = new ArrayList<Object>();
						if (FeatureWrapper.class.isAssignableFrom(collectionType)) {
							// The collection is complex.
							for (Property property : complexAttribute.getProperties(xsdName)) {
								collection.add(Wrap((ComplexAttribute)property, (Class<FeatureWrapper>)collectionType));
							}
						}
						else {
							// The collection is simple.
							for (Property property : complexAttribute.getProperties(xsdName)) {
								collection.add(property.getValue());
							}
						}
						
						field.set(wrapper, collection);
					}
					else { //TODO: can I just assume it's a simple type?
						// Look for this field in the complexAttribute:
						Property property = complexAttribute.getProperty(xsdName);
						field.set(wrapper, property.getValue());
					}
				}
			}
			
			return wrapper;
		}
		catch (IllegalAccessException iae) {
			System.out.println(iae);
		} catch (InstantiationException e) {
			System.out.println(e);
		}

		return null;
	}
}
