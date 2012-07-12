package org.geotools.data.wfs.internal.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

import javax.xml.namespace.QName;
import org.geotools.data.DataSourceException;
import org.geotools.feature.AttributeBuilder;
import org.geotools.feature.AttributeImpl;
import org.geotools.feature.FakeTypes;
import org.geotools.feature.LenientFeatureFactoryImpl;
import org.geotools.feature.NameImpl;
import org.geotools.feature.complex.ComplexFeatureBuilder;
import org.geotools.filter.identity.GmlObjectIdImpl;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class XmlComplexFeatureParser extends XmlFeatureParser<FeatureType, Feature> {

	private final ComplexFeatureBuilder featureBuilder;

	public XmlComplexFeatureParser(
			InputStream getFeatureResponseStream,
			FeatureType targetType,
			QName featureDescriptorName)
			throws IOException {
		super (getFeatureResponseStream, targetType, featureDescriptorName);
		this.featureBuilder = new ComplexFeatureBuilder(this.targetType);
	}

	@Override
	public Feature parse() throws IOException {
		final String fid;

		try {
			// Get the feature id or return null if there isn't one:
			if ((fid = seekFeature()) == null) {
				return null;
			}

			// Loop through each attribute
            while (true) {
            	int tagType = parser.next();
                String tagNs = parser.getNamespace();
                String tagName = parser.getName();
                Property attributeValue;

                if (tagType == XmlPullParser.END_TAG &&
                	featureNamespace.equals(tagNs) &&
                	featureName.equals(tagName)) {
                	break;
                }

                if (tagType == XmlPullParser.START_TAG) {
                	NameImpl name = new NameImpl(parser.getNamespace(), parser.getName());

                	PropertyDescriptor descriptor = targetType.getDescriptor(name);
                	if (descriptor != null) {
                		// TODO Adam: Should this really be hard-coded like this?
                		String id = parser.getAttributeValue("http://www.opengis.net/gml", "id");

//                		// TODO: what if you had a href at this level??
//                		String href = parser.getAttributeValue("http://www.w3.org/1999/xlink", "href");
//                		System.out.println("href: " + href);

           		        // Look at the underlying java type of the descriptor's type and use
            			// that to determine how to construct the attributes.
               			PropertyType type = descriptor.getType();
                		if (type instanceof ComplexType) {
                			// If the type is Complex then we need to parse it as such:
                		    attributeValue = parseComplexAttribute((ComplexType)type);
		            		featureBuilder.append(name, attributeValue);
			            }
                		else if (type instanceof AttributeType) {
                			attributeValue = new AttributeImpl(
            					super.parseAttributeValue((AttributeDescriptor)descriptor),
            					(AttributeType)type, 
            					new GmlObjectIdImpl(id));

                			featureBuilder.append(name, attributeValue);
                		}
                	}
                }
                else if (tagType == XmlPullParser.END_DOCUMENT) {
                	close();
                	return null;
                }
            }
		} catch (XmlPullParserException e) {
			throw new DataSourceException(e);
		}

		return featureBuilder.buildFeature(fid);
	}
	
	private ComplexAttribute parseComplexAttribute(ComplexType complexType) throws XmlPullParserException, IOException {
		// TODO: Adam, should the LenientFeatureFactoryImpl be injected instead of being hard-coded?
		AttributeBuilder attributeBuilder = new AttributeBuilder(new LenientFeatureFactoryImpl());  
		attributeBuilder.setType(complexType);
		String id = null;
		Hashtable<Name, Collection<Property>> multivaluedData = new Hashtable<Name, Collection<Property>>();
		
		while (true)
		{
			int tagType = parser.next();
            
            if (tagType == XmlPullParser.END_TAG) {
            	break;
            }
			else if (tagType == XmlPullParser.START_TAG) {
				NameImpl name = new NameImpl(parser.getNamespace(), parser.getName());
				
				// Get the id, if it's set:
    			// TODO Adam: Should this really be hard-coded like this?
    			id = parser.getAttributeValue("http://www.opengis.net/gml", "id");
//        		String href = parser.getAttributeValue("http://www.w3.org/1999/xlink", "href");
//        		
//        		if (href != null) {
//        			System.out.println(href);
//        		}

        		PropertyDescriptor descriptor = complexType.getDescriptor(name);
        		if (descriptor == null) {
        			continue;
        		}

        		PropertyType type = descriptor.getType();

        		if (type instanceof ComplexType) {
        			Property property = this.parseComplexAttribute((ComplexType)type);

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
			else if (tagType == XmlPullParser.END_DOCUMENT) {
				close();
                return null;
            }
		}

		for (Name name : multivaluedData.keySet()) {
			attributeBuilder.add(id, multivaluedData.get(name), name);
		}

		return (ComplexAttribute)attributeBuilder.build();
	}
}























