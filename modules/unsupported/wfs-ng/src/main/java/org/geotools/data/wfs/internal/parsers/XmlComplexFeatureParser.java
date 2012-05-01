package org.geotools.data.wfs.internal.parsers;

import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.namespace.QName;
import org.geotools.data.DataSourceException;
import org.geotools.feature.type.AttributeTypeImpl;
import org.geotools.feature.type.ComplexFeatureTypeImpl;
import org.geotools.feature.type.ComplexTypeImpl;
import org.geotools.util.Converters;
import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class XmlComplexFeatureParser extends XmlFeatureParser<Feature, FeatureType> {

	private final Map<String, PropertyDescriptor> expectedProperties;

	public XmlComplexFeatureParser(
			InputStream getFeatureResponseStream,
			FeatureType targetType,
			QName featureDescriptorName)
			throws IOException {
		super (getFeatureResponseStream, targetType, featureDescriptorName);

		// TODO: this casting is temporary - there's no getDescriptors() method on the FeatureType interface
		// so I have to read it as a ComplexFeatureTypeImpl to access that method. Not sure why the method
		// isn't declared in the interface. Will have to check but that might be a good way to fix this issue.

		// TODO: I'm not sure which of these I'm meant to use; getDescriptors() or getTypeDescriptors()
		//Collection<PropertyDescriptor> descriptors = ((ComplexFeatureTypeImpl) targetType).getDescriptors();
		Collection<PropertyDescriptor> descriptors = ((ComplexFeatureTypeImpl) targetType).getTypeDescriptors();

		// System.out.println(targetType);
		expectedProperties = new TreeMap<String, PropertyDescriptor>(String.CASE_INSENSITIVE_ORDER);
		for (PropertyDescriptor descriptor : descriptors) {
			this.expectedProperties.put(descriptor.getName().getLocalPart(), descriptor);
		}
	}

	@Override
	public Feature parse() throws IOException {
		final String fid;
		try {
			fid = seekFeature();
			// System.out.println("seekFeature() has returned: " + fid); /* DEBUG */

			if (fid == null) {
				return null;
			}

			int tagType;
            String tagNs;
            String tagName;
            Object attributeValue;

            while (true) {
                tagType = parser.next();
                if (XmlPullParser.END_DOCUMENT == tagType) {
                    close();
                    return null;
                }

                tagNs = parser.getNamespace();
                tagName = parser.getName();
                if (END_TAG == tagType && featureNamespace.equals(tagNs) && featureName.equals(tagName)) {
                    // found end of current feature
                	break;
                }

                if (START_TAG == tagType) {
                	PropertyDescriptor descriptor = expectedProperties.get(tagName);

                	if (descriptor != null) {
	                	// TODO: The simple one just has simple objects passed in here like strings or whatever
	                	// what should I pass in for complex features? Or should you create the complex feature part here
	                	// and then pass that in?
                		if (tagName.compareTo("mineName") == 0) {
	                        attributeValue = parseAttributeValue();
	                        // builder.set(descriptor.getLocalName(), attributeValue);
		                }
                	}
                }
            }
		}
		catch (XmlPullParserException e) {
	        throw new DataSourceException(e);
	    }

		return null;
	}

	// TODO: maybe this should be called parsePropertyValue?
	// I changed AttributeDescriptor and AttributeType to PropertyXXX
	// The 'else' block is what needs the work
	private Object parseAttributeValue() throws XmlPullParserException, IOException {
	    final String name = parser.getName();
	    final PropertyDescriptor attribute = expectedProperties.get(name);
	    final PropertyType type = attribute.getType();

	    ComplexTypeImpl complexType = (ComplexTypeImpl)type;
	    System.out.println("type: " + type);

	    Object parsedValue;
	    if (type instanceof GeometryType) {
	        parser.nextTag();

	        try {
	            parsedValue = parseGeom();
	        } catch (NoSuchAuthorityCodeException e) {
	            throw new DataSourceException(e);
	        } catch (FactoryException e) {
	            throw new DataSourceException(e);
	        }
	    } else {
	        String rawTextValue = parser.nextText();

	    	// String rawTextValue = ""; // parser.; // maybe need to make it recursive if it knows there's going to be more tags in it or something... check with others.
	        System.out.println(rawTextValue);

	        Class binding = type.getBinding();
	        parsedValue = Converters.convert(rawTextValue, binding);
	    }

	    return parsedValue;
	}
}
























