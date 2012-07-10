package org.geotools.feature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.geotools.feature.type.AttributeDescriptorImpl;
import org.geotools.feature.type.AttributeTypeImpl;
import org.geotools.feature.type.ComplexTypeImpl;
import org.geotools.feature.type.FeatureTypeImpl;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;

public class FakeTypes {
	// *** Taken from XSSchema ***
	public static final AttributeType ANYTYPE_TYPE = 
		new AttributeTypeImpl(
		/* name:         */ new NameImpl("http://www.w3.org/2001/XMLSchema", "anyType"), 
		/* binding:      */ java.lang.Object.class, 
		/* identified:   */ false,
		/* abstract:     */ false, 
		/* restrictions: */ Collections.<Filter> emptyList(), 
		/* superType:    */ null, 
		/* description:  */ null);
	
	public static final AttributeType ANYSIMPLETYPE_TYPE = 
		new AttributeTypeImpl(
		/* name:         */ new NameImpl("http://www.w3.org/2001/XMLSchema", "anySimpleType"), 
		/* binding:      */ java.lang.Object.class,
		/* identified:   */ false,
		/* abstract:     */ false,
		/* restrictions: */ Collections.<Filter> emptyList(),
		/* superType:    */ ANYTYPE_TYPE, 
		/* description:  */ null);

	public static final AttributeType STRING_TYPE = 
		new AttributeTypeImpl(
		/* name:         */ new NameImpl("http://www.w3.org/2001/XMLSchema", "string"),
		/* binding:      */ String.class,
		/* identified:   */ false,
		/* abstract:     */ false,
		/* restrictions: */ Collections.<Filter> emptyList(),
		/* superType:    */ ANYSIMPLETYPE_TYPE,
		/* description:  */ null);
	
	public static final AttributeType BOOLEAN_TYPE = 
		new AttributeTypeImpl(
		/* name:         */ new NameImpl("http://www.w3.org/2001/XMLSchema", "boolean"),
		/* binding:      */ Boolean.class,
		/* identified:   */ false,
		/* abstract:     */ false,
		/* restrictions: */ Collections.<Filter> emptyList(),
		/* superType:    */ ANYSIMPLETYPE_TYPE,
		/* description:  */ null);
	// ***************************
	
	// *** Taken from GMLSchema ***
	public static final AttributeType GEOMETRYPROPERTYTYPE_TYPE = build_GEOMETRYPROPERTYTYPE_TYPE();
    
    private static AttributeType build_GEOMETRYPROPERTYTYPE_TYPE() {
        AttributeType builtType;
        builtType = new AttributeTypeImpl(
            new NameImpl("http://www.opengis.net/gml","GeometryPropertyType"), com.vividsolutions.jts.geom.Geometry.class, false,
            false, Collections.<Filter>emptyList(), FakeTypes.ANYTYPE_TYPE, null
        );
        
        return builtType;
    }

    public static final AttributeType NULLTYPE_TYPE = build_NULLTYPE_TYPE();

    private static AttributeType build_NULLTYPE_TYPE() {
        AttributeType builtType;
        builtType = new AttributeTypeImpl(
            new NameImpl("http://www.opengis.net/gml","NullType"), java.lang.Object.class, false,
            false, Collections.<Filter>emptyList(), FakeTypes.ANYSIMPLETYPE_TYPE, null
        );

        return builtType;
    }
    // ***************************
	
	// *** Mocked up Bridge example, this one is just made up, it's very basic *** 
	public static class Bridge {
		public static final String BRIDGE_NAMESPACE = "urn:Bridge:Test:1.1";
		public static final Name NAME_BridgeType = new NameImpl(BRIDGE_NAMESPACE, ":", "BridgeType");
		public static final Name NAME_bridgeName = new NameImpl(BRIDGE_NAMESPACE, ":", "bridgeName");
		public static final Name NAME_location = new NameImpl(BRIDGE_NAMESPACE, ":", "location");
		public static final Name NAME_description = new NameImpl(BRIDGE_NAMESPACE, ":", "description");
		
		public static final AttributeDescriptor BRIDGENAME_DESCRIPTOR = 
			new AttributeDescriptorImpl(
			/* type: 		 */ STRING_TYPE,
			/* name: 		 */ NAME_bridgeName, 
			/* min: 		 */ 0,
			/* max:  		 */ 1,
			/* isNillable:   */ false,
			/* defaultValue: */	null);
		
	    public static final AttributeDescriptor LOCATION_DESCRIPTOR =
	    	new AttributeDescriptorImpl(
	    	/* type: 		 */ GEOMETRYPROPERTYTYPE_TYPE, 
	    	/* name: 		 */ NAME_location, 
	    	/* min: 		 */ 1, 
	    	/* max:  		 */ 1, 
	    	/* isNillable:   */ false, 
	    	/* defaultValue: */	null);
	    
	    
	    public static final AttributeDescriptor DESCRIPTION_DESCRIPTOR = 
	    	new AttributeDescriptorImpl(
	    	/* type: 		 */ STRING_TYPE, 
	    	/* name: 		 */ NAME_description, 
	    	/* min: 		 */ 1, 
	    	/* max:  		 */ 1, 
	    	/* isNillable:   */ true,
	    	/* defaultValue: */	null);
	    
	    /**
	     * The schema of the sample feature type.
	     */
	    private static final List<PropertyDescriptor> BRIDGETYPE_SCHEMA = new ArrayList<PropertyDescriptor>() { 
	    	{
	            add(BRIDGENAME_DESCRIPTOR);
	            add(LOCATION_DESCRIPTOR);
	            add(DESCRIPTION_DESCRIPTOR);
	        }
	    };
	    
	    /**
	     * The type of the sample feature.
	     */
	    public static final FeatureType BRIDGE_TYPE = new FeatureTypeImpl(
    		NAME_BridgeType,
			BRIDGETYPE_SCHEMA,
			null,
			false,
			Collections.<Filter> emptyList(),
			NULLTYPE_TYPE, // TODO: Is this OK? The example from SampleDataAccessData had ABSTRACTFEATURETYPE_TYPE but I just want to say that this doesn't have any parent class...
			null);
	}

	// *** EarthResource's Mine ***
	public static class Mine {
		public static final String MINE_NAMESPACE = "urn:cgi:xmlns:GGIC:EarthResource:1.1";
		
		public static final QName NAME_Mine = new QName(MINE_NAMESPACE, "Mine", "er");
		
		public static final Name NAME_mineName = new NameImpl(MINE_NAMESPACE, "mineName");
		
		public static final Name NAME_MineName = new NameImpl(MINE_NAMESPACE, "MineName");
		
		public static final Name NAME_MineType = new NameImpl(MINE_NAMESPACE, "MineType");
		
		public static final Name NAME_isPreferred = new NameImpl(MINE_NAMESPACE, "isPreferred");
		
		public static final Name NAME_MineNameType = new NameImpl(MINE_NAMESPACE, "MineNameType");
		
		public static final Name NAME_MineNamePropertyType = new NameImpl(MINE_NAMESPACE, "MineNamePropertyType");
	
		// (1)
		public static final AttributeDescriptor ISPREFERRED_DESCRIPTOR =
			new AttributeDescriptorImpl(
			/* type:         */ BOOLEAN_TYPE,
			/* name:         */ NAME_isPreferred,
			/* min:          */ 1,
			/* max:          */ 1,
			/* isNillable:   */ false,
			/* defaultValue: */ false); 
	
		// (2)
		public static final AttributeDescriptor mineNAME_DESCRIPTOR =
			new AttributeDescriptorImpl(
			/* type:         */ STRING_TYPE,
			/* name:         */ NAME_mineName,
			/* min:          */ 1,
			/* max:          */ 1,
			/* isNillable:   */ false,
			/* defaultValue: */ null);
	
		public static ArrayList<PropertyDescriptor> MINENAMETYPE_SCHEMA = new ArrayList<PropertyDescriptor>() {
			{
				add(ISPREFERRED_DESCRIPTOR);
				add(mineNAME_DESCRIPTOR);
			}
		};
		
		// (3)
		public static final ComplexType MINENAMETYPE_TYPE = 
			new ComplexTypeImpl(
			/* name:         */ NAME_MineNameType,
			/* properties:   */ MINENAMETYPE_SCHEMA,
			/* identified:   */ false, 
			/* isAbstract:   */ false,
			/* restrictions: */ Collections.<Filter> emptyList(),
			/* superType:    */ ANYTYPE_TYPE,
			/* description:  */ null);
	
		// (4)
		public static final AttributeDescriptor MINENAME_DESCRIPTOR =
			new AttributeDescriptorImpl(
			/* type:         */ MINENAMETYPE_TYPE,
			/* name:         */ NAME_MineName,
			/* min:          */ 1,
			/* max:          */ 1,
			/* isNillable:   */ false,
			/* defaultValue: */ null);
			
		public static ArrayList<PropertyDescriptor> MINENAMEPROPERTYTYPE_SCHEMA = new ArrayList<PropertyDescriptor>() {
			{
				add(MINENAME_DESCRIPTOR);
			}
		};
			
		// (5)
		public static final ComplexType MINENAMEPROPERTYTYPE_TYPE = 
			new ComplexTypeImpl(
			/* name:         */ NAME_MineNamePropertyType,
			/* properties:   */ MINENAMEPROPERTYTYPE_SCHEMA,
			/* identified:   */ false,
			/* isAbstract:   */ false,
			/* restrictions: */ Collections.<Filter> emptyList(),
			/* superType:    */ ANYTYPE_TYPE,
			/* description:  */ null);
		
		// (6)
		public static final AttributeDescriptor MINEmineNAME_DESCRIPTOR = 
			new AttributeDescriptorImpl(
			/* type:         */ MINENAMEPROPERTYTYPE_TYPE,
			/* name:         */ NAME_mineName,
			/* min:          */ 1,
			/* max:          */ Integer.MAX_VALUE,
			/* isNillable:   */ false,
			/* defaultValue: */ null);
	
		public static ArrayList<PropertyDescriptor> MINETYPE_SCHEMA = new ArrayList<PropertyDescriptor>() {
			{
				add(MINEmineNAME_DESCRIPTOR);
			}
		};
		
		// (7)
		public static final FeatureType MINETYPE_TYPE = 
			new FeatureTypeImpl(
			/* name:            */ NAME_MineType,
			/* properties:      */ MINETYPE_SCHEMA, // This is only a subset of mine information, the real one has some other details but they've been omitted here.
			/* defaultGeometry: */ null,
			/* isAbstract:      */ false,
			/* restrictions:    */ Collections.<Filter> emptyList(),
			/* superType:       */ ANYTYPE_TYPE, // In real life it's actually a MiningFeatureType but I don't think it matters.
			/* description:     */ null);
	}
}






















