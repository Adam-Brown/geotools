package org.geotools.data.wfs.internal.parsers;

import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.namespace.QName;
import org.geotools.data.DataSourceException;
import org.geotools.feature.AttributeBuilder;
import org.geotools.feature.LenientFeatureFactoryImpl;
import org.geotools.feature.NameImpl;
import org.geotools.feature.complex.ComplexFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.type.AttributeTypeImpl;
import org.geotools.feature.type.ComplexFeatureTypeImpl;
import org.geotools.feature.type.ComplexTypeImpl;
import org.geotools.util.Converters;
import org.opengis.feature.Attribute;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class XmlComplexFeatureParser extends XmlFeatureParser<FeatureType, Feature> {

	private final Map<String, PropertyDescriptor> expectedProperties;

	private final ComplexFeatureBuilder featureBuilder;

//	private final AttributeBuilder attributeBuilder;

	public XmlComplexFeatureParser(
			InputStream getFeatureResponseStream,
			FeatureType targetType,
			QName featureDescriptorName)
			throws IOException {
		super (getFeatureResponseStream, targetType, featureDescriptorName);

		this.featureBuilder = new ComplexFeatureBuilder(this.targetType);

		// TODO: this casting is temporary - there's no getDescriptors() method on the FeatureType interface
		// so I have to read it as a ComplexFeatureTypeImpl to access that method. Not sure why the method
		// isn't declared in the interface. Will have to check but that might be a good way to fix this issue.

		// TODO: I'm not sure which of these I'm meant to use; getDescriptors() or getTypeDescriptors()
		//Collection<PropertyDescriptor> descriptors = ((ComplexFeatureTypeImpl) targetType).getDescriptors();
		Collection<PropertyDescriptor> descriptors = ((ComplexFeatureTypeImpl) targetType).getTypeDescriptors();

		System.out.println(targetType);
		expectedProperties = new TreeMap<String, PropertyDescriptor>(String.CASE_INSENSITIVE_ORDER);
		for (PropertyDescriptor descriptor : descriptors) {
			this.expectedProperties.put(descriptor.getName().getLocalPart(), descriptor);
		}
	}
	
//	@Override
//	public Feature parse() throws IOException {
//		final String fid;
//		try {
//			fid = seekFeature();
//
//			if (fid == null) {
//				return null;
//			}
//
//			int tagType;
//            String tagNs;
//            String tagName;
//            Object attributeValue;
//
//            while (true) {
//            	tagType = parser.next();
//                if (XmlPullParser.END_DOCUMENT == tagType) {
//                    close();
//                    return null;
//                }
//
//                tagNs = parser.getNamespace();
//                tagName = parser.getName();
//                
//                if (tagType == END_TAG && featureNamespace.equals(tagNs) && featureName.equals(tagName)) {
//                    // found end of current feature
//                	break;
//                }
//                else if (tagType == START_TAG) {
//                	// (6) MINEmineNAME_DESCRIPTOR
//                	PropertyDescriptor descriptor = expectedProperties.get(tagName);
//
//                	if (descriptor != null) {
//                		if (tagName.compareTo("mineName") == 0) {
//                			System.out.println("(6) MINEmineNAME_DESCRIPTOR: " + descriptor);
//
//                			// Look at the underlying java type of the descriptor's type and use
//	            			// that to determine how to construct the attributes.
//
//                			// 'type' corresponds to (5) MINENAMEPROPERTYTYPE_TYPE
//	                		PropertyType type = descriptor.getType();
//
//	                		if (type instanceof ComplexType) {
//	                			// If the type is Complex then we need to parse it as such:
//		            			attributeValue = parseComplexAttribute((ComplexType)type);
//		            			System.out.println(attributeValue);
//		            		}
//
////	                 		featureBuilder.append(name, propertyValue);
//	            			System.exit(0);
//                		}
//	                }
//                }
//            }
//		}
//		catch (XmlPullParserException e) {
//	        throw new DataSourceException(e);
//	    }
//
//		return featureBuilder.buildFeature(fid);
//	}

	@Override
	public Feature parse() throws IOException {
		final String fid;
		try {
			fid = seekFeature();

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
                
                if (tagType == END_TAG && featureNamespace.equals(tagNs) && featureName.equals(tagName)) {
                    // found end of current feature
                	break;
                }
                else if (tagType == START_TAG) {
                	// (6) MINEmineNAME_DESCRIPTOR
                	PropertyDescriptor descriptor = expectedProperties.get(tagName);

                	if (descriptor != null) {
                		if (tagName.compareTo("mineName") == 0) {
                			System.out.println("(6) MINEmineNAME_DESCRIPTOR: " + descriptor);

                			// Look at the underlying java type of the descriptor's type and use
	            			// that to determine how to construct the attributes.

                			// 'type' corresponds to (5) MINENAMEPROPERTYTYPE_TYPE
	                		PropertyType type = descriptor.getType();

	                		if (type instanceof ComplexType) {
	                			// If the type is Complex then we need to parse it as such:
	                			String id = parser.getAttributeValue("http://www.opengis.net/gml", "id");
		            			attributeValue = parseComplexAttribute((ComplexType)type, id);
		            			System.out.println(attributeValue);
		            		}

//	                 		featureBuilder.append(name, propertyValue);
	            			System.exit(0);
                		}
	                }
                }
            }
		}
		catch (XmlPullParserException e) {
	        throw new DataSourceException(e);
	    }

		return featureBuilder.buildFeature(fid);
	}

	private Object parseComplexAttribute(ComplexType complexType, String eid) throws XmlPullParserException, IOException {
		// TODO: Adam, should the LenientFeatureFactoryImpl be injected instead of being hard-coded?
		AttributeBuilder attributeBuilder = new AttributeBuilder(new LenientFeatureFactoryImpl());  
		attributeBuilder.setType(complexType);
		
		// The string should be the name of the type, list of objects which can be used as a collection.
		Hashtable<Name, Collection<Property>> multivaluedData = new Hashtable<Name, Collection<Property>>();
		
		// Get the next tag from the parser and see what type its name corresponds to:
		do
		{
			int tagType = parser.next();

			if (XmlPullParser.END_DOCUMENT == tagType) {
				close();
                return null;
            }
			else if (tagType == XmlPullParser.START_TAG) {
				NameImpl name = new NameImpl(parser.getNamespace(), parser.getName());
				
				System.out.println(name);
				
				// Get the id, if it's set:
    			// TODO Adam: Should this really be hard-coded like this?
    			String id = parser.getAttributeValue("http://www.opengis.net/gml", "id");

// TODO Adam: how should I get these attributes into the objects?
//				int attributeCount = parser.getAttributeCount();
//
//				System.out.println(tab(1, "Name: " + name));
//				System.out.println(tab(1, "Attributes: " + attributeCount));
//
//				for (int i = 0; i < attributeCount; i++) {
//					System.out.println(tab(2, parser.getAttributeNamespace(i) + ":" + parser.getAttributeName(i) + " = " + parser.getAttributeValue(i)));
//				}

    			// Get the descriptor
				// (4) MINENAME_DESCRIPTOR
				PropertyDescriptor descriptor = complexType.getDescriptor(name);

				// (3) MINENAMETYPE_TYPE
        		PropertyType type = descriptor.getType();

        		if (type instanceof ComplexType) {
        			// Recurses here.
        			Property property = (Property)parseComplexAttribute((ComplexType)type, id);        			
        			
        			if (!multivaluedData.keySet().contains(name)) {
        				multivaluedData.put(name, new ArrayList<Property>());
        			}

        			// Just add it to the multivaluedData for now, we add them to the attribute builder later on.
        			multivaluedData.get(name).add(property);
       			}
        		else if (type instanceof AttributeType) {
        			Object attributeValue = super.parseAttributeValue((AttributeDescriptor)descriptor);
                    attributeBuilder.add(id, attributeValue, name);
        		}
			}
			else if (tagType == END_TAG) {
            	break;
            }
		} while (true);
		
		for (Name name : multivaluedData.keySet()) {
			// TODO Adam: what about the id?
			attributeBuilder.add(eid, multivaluedData.get(name), name);
		}
		
		return attributeBuilder.build();
	}
}























