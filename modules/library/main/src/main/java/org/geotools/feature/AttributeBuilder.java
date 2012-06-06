package org.geotools.feature;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.opengis.feature.Association;
import org.opengis.feature.Attribute;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.Property;
import org.opengis.feature.type.AssociationDescriptor;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Copying some of this implementation from gt-app-schema. Some of it can't be moved here because it has
 * dependencies on other libraries which gt-main cannot include.
 * 
 * @author bro879
 *
 */
public class AttributeBuilder {
	/**
     * Factory used to create attributes
     */
	FeatureFactory attributeFactory;

	/**
	 * Namespace context.
	 */
	String namespace;

	/**
     * Type of complex attribute being built. This field is mutually exclusive with
     * {@link #descriptor}
     */
    AttributeType type;

    /**
     * Descriptor of complex attribute being built. This field is mutually exclusive with
     * {@link #type}
     */
    AttributeDescriptor descriptor;

    /**
     * Contained properties (associations + attributes)
     */
    List<Property> properties;

    public AttributeBuilder(FeatureFactory attributeFactory, AttributeType type) {
    	this.attributeFactory = attributeFactory;
    	this.type = type;
    }
    
    public AttributeBuilder(FeatureFactory attributeFactory, AttributeDescriptor descriptor) {
    	this.attributeFactory = attributeFactory;
    	this.descriptor = descriptor;
    }
    
    public Attribute build(String id) {
    	return null;
    }
    
//    public Attribute add(
//   		final String id,
//   		final Object value, 
//   		final Name name,
//   		final AttributeType type) {
//    }
}

	
//	I started copying this from the app-schema version but it's getting unwieldy; some
//	  of the code uses other libraries but I don't really understand what it does and if it's
//	  needed. I'm going to let my own requirements drive construction of this version and use
//	  the original as a guide to the API and to provide direction where needed.
//
//	/**
//     * Factory used to create attributes
//     */
//    FeatureFactory attributeFactory;
//
//    /**
//     * Namespace context.
//     */
//    String namespace;
//
//    /**
//     * Type of complex attribute being built. This field is mutually exclusive with
//     * {@link #descriptor}
//     */
//    AttributeType type;
//
//    /**
//     * Descriptor of complex attribute being built. This field is mutually exclusive with
//     * {@link #type}
//     */
//    AttributeDescriptor descriptor;
//
//    /**
//     * Contained properties (associations + attributes)
//     */
//    List properties;
//
//    /**
//     * The crs of the attribute.
//     */
//    CoordinateReferenceSystem crs;
//
//    /**
//     * Default geometry of the feature.
//     */
//    Object defaultGeometry;
//    
//    public AttributeBuilder(FeatureFactory attributeFactory) {
//    	this.setFeatureFactory(attributeFactory);
//    }
//
//    //
//    // Injection
//    //
//    // Used to inject dependencies we need during construction time.
//    //
//    /**
//     * Returns the underlying attribute factory.
//     */
//    public FeatureFactory getFeatureFactory() {
//        return attributeFactory;
//    }
//
//    /**
//     * Sets the underlying attribute factory.
//     */
//    public void setFeatureFactory(FeatureFactory attributeFactory) {
//        this.attributeFactory = attributeFactory;
//    }
//
//    //
//    // State
//    //
//
//    /**
//     * Resets the builder to its initial state, the same state it is in directly after being
//     * instantiated.
//     */
//    public void init() {
//    	descriptor = null;
//        type = null;
//        properties = null;
//        crs = null;
//        defaultGeometry = null;
//    }
//    
////    /**
////     * Resets the state of the builder based on a previously built attribute.
////     * <p>
////     * This method is useful when copying another attribute.
////     * </p>
////     */
////    public void init(Attribute attribute) {
////        init();
////
////        descriptor = attribute.getDescriptor();
////        type = attribute.getType();
////
////        if (attribute instanceof ComplexAttribute) {
////            ComplexAttribute complex = (ComplexAttribute) attribute;
////            Collection properties = (Collection) complex.getValue();
////            for (Iterator itr = properties.iterator(); itr.hasNext();) {
////                Property property = (Property) itr.next();
////                if (property instanceof Attribute) {
////                    Attribute att = (Attribute) property;
////                    add(att.getIdentifier() == null ? null : att.getIdentifier().toString(), att
////                            .getValue(), att.getName());
////                } else if (property instanceof Association) {
////                    Association assoc = (Association) property;
////                    associate(assoc.getValue(), assoc.getName());
////                }
////            }
////        }
////
////        if (attribute instanceof Feature) {
////            Feature feature = (Feature) attribute;
////
////            if (feature.getDefaultGeometryProperty() != null) {
////                if (crs == null) {
////                    crs = feature.getDefaultGeometryProperty().getType()
////                            .getCoordinateReferenceSystem();
////                }
////                defaultGeometry = feature.getDefaultGeometryProperty().getValue();
////            }
////        }
////    }
//    
//	/**
//	 * This namespace will be used when constructing attribute names.
//	 */
//	public void setNamespaceURI(String namespace) {
//	    this.namespace = namespace;
//	}
//	
//	/**
//	 * This namespace will be used when constructing attribute names.
//	 * 
//	 * @return namespace will be used when constructing attribute names.
//	 */
//	public String getNamespaceURI() {
//	    return namespace;
//	}
//	
//	/**
//     * Sets the type of the attribute being built.
//     * <p>
//     * When building a complex attribute, this type is used a reference to obtain the types of
//     * contained attributes.
//     * </p>
//     */
//    public void setType(AttributeType type) {
//        this.type = type;
//        this.descriptor = null;
//    }
//
//    /**
//     * Sets the descriptor of the attribute being built.
//     * <p>
//     * When building a complex attribute, this type is used a reference to obtain the types of
//     * contained attributes.
//     * </p>
//     */
//    public void setDescriptor(AttributeDescriptor descriptor) {
//        this.descriptor = descriptor;
//        this.type = (AttributeType) descriptor.getType();
//    }
//
//    /**
//     * @return The type of the attribute being built.
//     */
//    public AttributeType getType() {
//        return type;
//    }
//
//    // Feature specific methods
//    /**
//     * Sets the coordinate reference system of the built feature.
//     */
//    public void setCRS(CoordinateReferenceSystem crs) {
//        this.crs = crs;
//    }
//
//    /**
//     * @return The coordinate reference system of the feature, or null if not set.
//     */
//    public CoordinateReferenceSystem getCRS() {
//        return crs;
//    }
//
//    /**
//     * Sets the default geometry of the feature.
//     */
//    public void setDefaultGeometry(Object defaultGeometry) {
//        this.defaultGeometry = defaultGeometry;
//    }
//
//    /**
//     * @return The default geometry of the feature.
//     */
//    public Object getDefaultGeometry() {
//        return defaultGeometry;
//    }
//    
//    
//    /**
//     * Adds an association to the complex attribute being built. <br>
//     * <p>
//     * This method uses the result of {@link #getNamespaceURI()} to build a qualified attribute
//     * name.
//     * </p>
//     * <p>
//     * This method uses the type supplied in {@link #setType(AttributeType)} in order to determine
//     * the association type.
//     * </p>
//     * 
//     * @param value
//     *                The value of the association, an attribute.
//     * @param name
//     *                The name of the association.
//     */
//    public void associate(Attribute value, String name) {
//        associate(value, name, namespace);
//    }
//    
//    /**
//     * Adds an association to the complex attribute being built. <br>
//     * <p>
//     * This method uses the type supplied in {@link #setType(AttributeType)} in order to determine
//     * the association type.
//     * </p>
//     * 
//     * @param value
//     *                The value of the association, an attribute.
//     * @param name
//     *                The name of the association.
//     * @param namespaceURI
//     *                The namespace of the association
//     */
//    public void associate(Attribute attribute, String name, String namespaceURI) {
//        associate(attribute, new NameImpl(namespaceURI, name));
//    }
//    
//    /**
//     * Adds an association to the complex attribute being built. <br>
//     * <p>
//     * This method uses the type supplied in {@link #setType(AttributeType)} in order to determine
//     * the association type.
//     * </p>
//     * 
//     * @param value
//     *                The value of the association, an attribute.
//     * @param name
//     *                The name of the association.
//     * @param namespaceURI
//     *                The namespace of the association
//     */
//    public void associate(Attribute value, Name name) {
//        AssociationDescriptor descriptor = associationDescriptor(name);
//        Association association = attributeFactory.createAssociation(value, descriptor);
//        properties().add(association);
//    }
//
//    
//    protected AssociationDescriptor associationDescriptor(Name name) {
//        PropertyDescriptor descriptor = Types.descriptor((ComplexType) type, name);
//
//        if (descriptor == null) {
//            throw new IllegalArgumentException(
//          		"Could not locate association: " + name + " in type: " + type.getName());
//        }
//        else if (!(descriptor instanceof AssociationDescriptor)) {
//            throw new IllegalArgumentException(
//            	name + " references a non association");
//        }
//        else
//        {
//        	return (AssociationDescriptor) descriptor;
//        }
//    }
































































