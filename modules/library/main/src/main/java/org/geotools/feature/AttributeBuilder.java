package org.geotools.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

//import org.geotools.data.complex.ComplexFeatureConstants;
//import org.geotools.data.complex.config.NonFeatureTypeProxy;
import org.geotools.feature.type.AttributeDescriptorImpl;
import org.geotools.feature.type.ComplexTypeImpl;
import org.geotools.feature.type.GeometryDescriptorImpl;
import org.opengis.feature.Association;
import org.opengis.feature.Attribute;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.type.AssociationDescriptor;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Copying some of this implementation from gt-app-schema. Some of it can't be moved here because it has
 * dependencies on other libraries which gt-main cannot include.
 * 
 * @author bro879
 *
 */
public class AttributeBuilder {
	// Region: fields
    /**
     * Factory used to create attributes.
     */
    protected FeatureFactory attributeFactory;
    
    /**
     * Type of complex attribute being built. This field is mutually exclusive with
     * {@link #descriptor}.
     */
    protected AttributeType type;

    /**
     * Descriptor of complex attribute being built. This field is mutually exclusive with
     * {@link #type}.
     */
    protected AttributeDescriptor descriptor;
    
    /**
     * Contained properties (associations + attributes)
     */
    protected List properties;
    
    /**
     * The crs of the attribute.
     */
    protected CoordinateReferenceSystem crs;

    /**
     * Namespace context.
     */
    protected String namespace;
    
    /**
     * Default geometry of the feature.
     */
    protected Object defaultGeometry;
    

    // Region: getters & setters
    /**
     * @return The type of the attribute being built.
     */
    public AttributeType getType() {
        return type;
    }

    /**
     * Sets the type of the attribute being built.
     * <p>
     * When building a complex attribute, this type is used a reference to obtain the types of
     * contained attributes.
     * </p>
     */
    public void setType(AttributeType type) {
        this.type = type;
        this.descriptor = null;
    }
    
    /**
     * Sets the descriptor of the attribute being built.
     * <p>
     * When building a complex attribute, this type is used a reference to obtain the types of
     * contained attributes.
     * </p>
     */
    public void setDescriptor(AttributeDescriptor descriptor) {
        this.descriptor = descriptor;
        this.type = (AttributeType) descriptor.getType();
    }

    /**
     * @return The coordinate reference system of the feature, or null if not set.
     */
    public CoordinateReferenceSystem getCRS() {
        return crs;
    }
    
    /**
     * Sets the coordinate reference system of the built feature.
     */
    public void setCRS(CoordinateReferenceSystem crs) {
        this.crs = crs;
    }

    /**
     * @return The default geometry of the feature.
     */
    public Object getDefaultGeometry() {
        return this.defaultGeometry;
    }
    
    /**
     * Sets the default geometry of the feature.
     */
    public void setDefaultGeometry(Object defaultGeometry) {
        this.defaultGeometry = defaultGeometry;
    }
    
    /**
     * Convenience accessor for properties list which does the null check.
     */
    protected List getProperties() {
        if (properties == null) {
            properties = new ArrayList();
        }

        return properties;
    }

    
    // Region: constructor
    public AttributeBuilder(FeatureFactory attributeFactory)
    {
    	this.attributeFactory = attributeFactory;
    }

    
    // Region: public methods
    /**
     * Adds an attribute to the complex attribute being built. <br>
     * <p>
     * This method uses the type supplied in {@link #setType(AttributeType)} in order to determine
     * the attribute type.
     * </p>
     *
     * @param id
     *                The id of the attribute.
     * @param name
     *                The name of the attribute.
     * @param value
     *                The value of the attribute.
     *
     */
    public Attribute add(String id, Object value, Name name) {
        AttributeDescriptor descriptor = getAttributeDescriptorFor(name);
        Attribute attribute = create(value, null, descriptor, id);
        getProperties().add(attribute);
        return attribute;
    }

    /**
     * Create complex attribute
     * 
     * @param value
     * @param type
     * @param descriptor
     * @param id
     * @return
     */
    public ComplexAttribute createComplexAttribute(Object value, ComplexType type, AttributeDescriptor descriptor, String id) {
        return descriptor != null
    		? attributeFactory.createComplexAttribute((Collection) value, descriptor, id) 
    		: attributeFactory.createComplexAttribute((Collection) value, type, id);
    }

    /**
     * Builds the attribute.
     * <p>
     * The class of the attribute built is determined from its type set with
     * {@link #setType(AttributeType)}.
     * </p>
     * 
     * @return The build attribute.
     */
    public Attribute build() {
        return build(null);
    }

    /**
      * Builds the attribute.
     * <p>
     * The class of the attribute built is determined from its type set with
     * {@link #setType(AttributeType)}.
     * </p>
     * 
     * @param id
     *                The id of the attribute, or null.
     * 
     * @return The build attribute.
     */
    public Attribute build(String id) {
        Attribute built = create(getProperties(), type, descriptor, id);

        // FIXME
        // // if geometry, set the crs
        // if (built instanceof GeometryAttribute) {
        // ((GeometryAttribute) built).getDescriptor().setCRS(getCRS());
        // }

        // if feature, set crs and default geometry
        if (built instanceof Feature) {
            Feature feature = (Feature) built;
            // FIXME feature.setCRS(getCRS());
            if (defaultGeometry != null) {
                for (Iterator itr = feature.getProperties().iterator(); itr.hasNext();) {
                    Attribute att = (Attribute) itr.next();
                    if (att instanceof GeometryAttribute) {
                        if (defaultGeometry.equals(att.getValue())) {
                            feature.setDefaultGeometryProperty((GeometryAttribute) att);
                        }
                    }

                }
            }
        }
        
        getProperties().clear();
        return built;
    }

    /**
     * Resets the builder to its initial state, the same state it is in directly after being
     * instantiated.
    */
    public void init() {
		descriptor = null;
		type = null;
		properties = null;
		crs = null;
		defaultGeometry = null;
	}

    
    // Region: protected methods
    /**
     * Factors out attribute creation code, needs to be called with either one of type or descriptor.
     */
    protected Attribute create(Object value, AttributeType type, AttributeDescriptor descriptor, String id) {
        if (descriptor != null) {
            type = (AttributeType) descriptor.getType();
        }

        if (type instanceof FeatureType) {
            return descriptor != null ? 
        		attributeFactory.createFeature((Collection) value, descriptor, id) : 
        		attributeFactory.createFeature((Collection) value, (FeatureType) type, id);
        } else if (type instanceof ComplexType) {
            return createComplexAttribute((Collection) value, (ComplexType) type, descriptor, id);
        } else if (type instanceof GeometryType) {
            return attributeFactory.createGeometryAttribute(value, (GeometryDescriptor) descriptor, id, getCRS());
        } else {
            return attributeFactory.createAttribute(value, descriptor, id);
        }
    }

    protected AttributeDescriptor getAttributeDescriptorFor(Name name) {
        PropertyDescriptor descriptor = findDescriptor((ComplexType) type, name);

        if (descriptor == null) {
            String msg = "Could not locate attribute: " + name + " in type: " + type.getName();
            throw new IllegalArgumentException(msg);
        }

        if (!(descriptor instanceof AttributeDescriptor)) {
            String msg = name + " references a non attribute";
            throw new IllegalArgumentException(msg);
        }

        return (AttributeDescriptor) descriptor;
    }
    
    
    // -------------------------------------------------------------------------
    // Region: taken from Types.java
    // -------------------------------------------------------------------------
    /**
     * Find a descriptor, taking in to account supertypes AND substitution groups
     * 
     * @param parentType type
     * @param name name of descriptor
     * @return descriptor, null if not found
     */
    public static PropertyDescriptor findDescriptor(ComplexType parentType, Name name) {
        //get list of descriptors from types and all supertypes
        List<PropertyDescriptor> descriptors = descriptors(parentType);
        
        //find matching descriptor
        for (Iterator<PropertyDescriptor> it = descriptors.iterator(); it.hasNext();) {
            PropertyDescriptor d = it.next(); 
            if (d.getName().equals(name)) {
                return d;
            } 
        }

        // nothing found, perhaps polymorphism?? let's loop again
        for (Iterator<PropertyDescriptor> it = descriptors.iterator(); it.hasNext();) {
            List<AttributeDescriptor> substitutionGroup = (List<AttributeDescriptor>) it.next().getUserData().get("substitutionGroup");
            if (substitutionGroup != null){
                for (Iterator<AttributeDescriptor> it2 = substitutionGroup.iterator(); it2.hasNext();) {
                    AttributeDescriptor d = it2.next(); 
                    if (d.getName().equals(name)) { //BINGOOO !!
                        return d;                            
                    }
                }
            }        
        }
        
        return null;
    }
    
    /**
     * Returns the set of all descriptors of a complex type, including from supertypes.
     * 
     * @param type
     *            The type, non null.
     * 
     * @return The list of all descriptors.
     */
    public static List<PropertyDescriptor> descriptors(ComplexType type) {
        // get list of descriptors from types and all supertypes
        List<PropertyDescriptor> children = new ArrayList<PropertyDescriptor>();
        ComplexType loopType = type;
        while (loopType != null) { 
            children.addAll(loopType.getDescriptors());
            loopType = loopType.getSuper() instanceof ComplexType ? (ComplexType) loopType.getSuper() : null;
        }
        return children;
    }
}



































