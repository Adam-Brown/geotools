package org.geotools.feature.complex;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geotools.feature.AttributeImpl;
import org.geotools.feature.NameImpl;
import org.geotools.feature.type.AttributeDescriptorImpl;
import org.geotools.feature.type.AttributeTypeImpl;
import org.geotools.feature.type.FeatureTypeImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import junit.framework.TestCase;
import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

public class ComplexFeatureBuilderTest {
	// -----------------------
	// | Taken from XSSchema |
	public static final AttributeType ANYTYPE_TYPE = new AttributeTypeImpl(new NameImpl(
        "http://www.w3.org/2001/XMLSchema", "anyType"), java.lang.Object.class, false,
        false, Collections.<Filter> emptyList(), null, null);
	
	public static final AttributeType ANYSIMPLETYPE_TYPE = new AttributeTypeImpl(new NameImpl(
        "http://www.w3.org/2001/XMLSchema", "anySimpleType"), java.lang.Object.class,
        false, false, Collections.<Filter> emptyList(), ANYTYPE_TYPE, null);

	public static final AttributeType STRING_TYPE = new AttributeTypeImpl(new NameImpl(
        "http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false,
        Collections.<Filter> emptyList(), ANYSIMPLETYPE_TYPE, null);
	// -----------------------

	// ------------------------
	// | Taken from GMLSchema |
	public static final AttributeType GEOMETRYPROPERTYTYPE_TYPE = build_GEOMETRYPROPERTYTYPE_TYPE();
    
    private static AttributeType build_GEOMETRYPROPERTYTYPE_TYPE() {
        AttributeType builtType;
        builtType = new AttributeTypeImpl(
            new NameImpl("http://www.opengis.net/gml","GeometryPropertyType"), com.vividsolutions.jts.geom.Geometry.class, false,
            false, Collections.<Filter>emptyList(), ANYTYPE_TYPE, null
        );
        
        return builtType;
    }

    public static final AttributeType NULLTYPE_TYPE = build_NULLTYPE_TYPE();

    private static AttributeType build_NULLTYPE_TYPE() {
        AttributeType builtType;
        builtType = new AttributeTypeImpl(
            new NameImpl("http://www.opengis.net/gml","NullType"), java.lang.Object.class, false,
            false, Collections.<Filter>emptyList(), ANYSIMPLETYPE_TYPE, null
        );

        return builtType;
    }
    // ------------------------

    // *****************************************************************************************
    // BRIDGE TYPE DEFINITION
    // 
	public static final String NAMESPACE_URI = "urn:Bridge:Test:1.1";
	private static final Name BRIDGE_NAME = new NameImpl(NAMESPACE_URI, ":", "bridgeName");
    private static final Name LOCATION = new NameImpl(NAMESPACE_URI, ":", "location");
    private static final Name DESCRIPTION = new NameImpl(NAMESPACE_URI, ":", "description");
    public static final AttributeDescriptor BRIDGENAME_DESCRIPTOR = new AttributeDescriptorImpl(STRING_TYPE, BRIDGE_NAME, 0, 1, false, null);
    public static final AttributeDescriptor LOCATION_DESCRIPTOR = new AttributeDescriptorImpl(GEOMETRYPROPERTYTYPE_TYPE, LOCATION, 1, 1, false, null);
    public static final AttributeDescriptor DESCRIPTION_DESCRIPTOR = new AttributeDescriptorImpl(STRING_TYPE, DESCRIPTION, 1, 1, true, null);

    /**
     * The schema of the sample feature type.
     */
    private static final List<PropertyDescriptor> BRIDGE_TYPE_SCHEMA = new ArrayList<PropertyDescriptor>() { 
    	{
            add(BRIDGENAME_DESCRIPTOR);
            add(LOCATION_DESCRIPTOR);
            add(DESCRIPTION_DESCRIPTOR);
        }
    };

    /**
     * The qualified name of the sample feature type.
     */
    public static final Name BRIDGE_TYPE_NAME = new NameImpl(NAMESPACE_URI, "Bridge");

    /**
     * The type of the sample feature.
     */
    public static final FeatureType BRIDGE_TYPE = new FeatureTypeImpl(
		BRIDGE_TYPE_NAME,
		BRIDGE_TYPE_SCHEMA,
		null,
		false,
		Collections.<Filter> emptyList(),
		NULLTYPE_TYPE, // TODO: Is this OK? The example from SampleDataAccessData had ABSTRACTFEATURETYPE_TYPE but I just want to say that this doesn't have any parent class...
		null);
    // *****************************************************************************************

    // EXTRUSIVE BASALT VEIN TYPE DEFINITION
    private static final String BASALT_NAMESPACE_URI = "urn:ExtrusiveBasaltVein:Test:1.1";
    private static final Name FAKEABSTRACTFEATURETYPE_NAME = new NameImpl(BASALT_NAMESPACE_URI, ":", "FakeAbstractFeatureType");
    private static final Name MAPPEDFEATURETYPE_NAME = new NameImpl(BASALT_NAMESPACE_URI, ":", "MappedFeatureType");
    private static final Name GEOLOGICALUNITTYPE_NAME = new NameImpl(BASALT_NAMESPACE_URI, ":", "GeologicalUnitType");
    private static final Name OCCURRENCE_NAME = new NameImpl(BASALT_NAMESPACE_URI, ":", "occurrence");
    private static final Name SPECIFICATION_NAME = new NameImpl(BASALT_NAMESPACE_URI, ":", "specification");
    private static final Name BASALTVEIN_NAME = new NameImpl(NAMESPACE_URI, ":", "BasaltVein");

    // Create the FakeAbstractFeatureType
    private static final FeatureType FAKEABSTRACTFEATURETYPE_TYPE = new FeatureTypeImpl(
		FAKEABSTRACTFEATURETYPE_NAME,
		Collections.<PropertyDescriptor> emptyList(),
		null,
		true,
		Collections.<Filter> emptyList(),
		NULLTYPE_TYPE,
		null);
    
    // Create the MappedFeatureType
    private static final List<PropertyDescriptor> MAPPEDFEATURETYPE_SCHEMA = new ArrayList<PropertyDescriptor>() { 
		{
            add(new AttributeDescriptorImpl(FAKEABSTRACTFEATURETYPE_TYPE, SPECIFICATION_NAME, 1, 1, false, null));
        }
    };

    private static final FeatureType MAPPEDFEATURETYPE_TYPE = new FeatureTypeImpl(
    	MAPPEDFEATURETYPE_NAME,
    	MAPPEDFEATURETYPE_SCHEMA,
    	null,
    	false,
    	Collections.<Filter> emptyList(),
    	NULLTYPE_TYPE,
    	null);

    // Create the GeologicalUnitType
    private static final List<PropertyDescriptor> GEOLOGICALUNITTYPE_SCHEMA = new ArrayList<PropertyDescriptor>() { 
    	{
            add(new AttributeDescriptorImpl(MAPPEDFEATURETYPE_TYPE, OCCURRENCE_NAME, 0, -1, false, null));
        }
    };
    
    private static final FeatureType GEOLOGICALUNITTYPE_TYPE = new FeatureTypeImpl(
   		GEOLOGICALUNITTYPE_NAME,
   		GEOLOGICALUNITTYPE_SCHEMA,
    	null,
    	false,
    	Collections.<Filter> emptyList(),
    	FAKEABSTRACTFEATURETYPE_TYPE,
    	null);

    // *****************************************************************************************
    // 
    private static GeometryFactory gm = new GeometryFactory();
    
    private static AttributeImpl londonBridge = new AttributeImpl("London Bridge", BRIDGENAME_DESCRIPTOR, null);
    
    private static AttributeImpl location = new AttributeImpl(gm.createPoint(new Coordinate(1, 3)), LOCATION_DESCRIPTOR, null);
	
	@Before
	public void setUp() {

	}
	
	private void assertExceptionMessage(Exception exception, String expectedMessage) throws Exception
	{
		String actualMessage = exception.getMessage(); 
		if (actualMessage.compareTo(expectedMessage) != 0) {
			fail(String.format("Expected %s to say: '%s' but got: '%s'",
					exception.getClass().getSimpleName(), 
					expectedMessage, 
					actualMessage));
		}
		
		throw exception;
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void append_invalidName_throwsIllegalArgumentException() throws Exception {
		// Arrange
		ComplexFeatureBuilder builder = new ComplexFeatureBuilder(BRIDGE_TYPE);

		// Act
		try {
			builder.append(new NameImpl("invalid_descriptor_name"), londonBridge);
		}
		catch (IllegalArgumentException iae) {
			assertExceptionMessage(
				iae, 
				"The name 'invalid_descriptor_name' is not a valid descriptor name for the type 'urn:Bridge:Test:1.1:Bridge'.");
		}
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void append_validNameInvalidValueClass_throwsIllegalArgumentException() throws Exception {
		// Arrange
		ComplexFeatureBuilder builder = new ComplexFeatureBuilder(BRIDGE_TYPE);
		
		// Act
		try {
			builder.append(LOCATION, londonBridge); // Passing in londonBridge instead of a location.
		}
		catch (IllegalArgumentException iae) {
			assertExceptionMessage(
				iae, 
				"The value provided contains an object of 'class java.lang.String' but the method expects an object of 'class com.vividsolutions.jts.geom.Geometry'.");
		}
	}

	
	@Test(expected=IllegalArgumentException.class)
	public void append_validNameButNullValue_throwsIllegalArgumentException() throws Exception {
		// Arrange
		ComplexFeatureBuilder builder = new ComplexFeatureBuilder(BRIDGE_TYPE);

		// Act
		try {
			builder.append(BRIDGE_NAME, null); // Passing a null reference for a non-nillable type.
		}
		catch (IllegalArgumentException iae) {
			assertExceptionMessage(
				iae, 
				"The value provided is a null reference but the property descriptor 'AttributeDescriptorImpl urn:Bridge:Test:1.1:bridgeName <string:String> 0:1' is non-nillable.");
		}
	}

	@Test
	public void append_validNameValidValue_valueShouldBeAddedToTheMap() {
		// Arrange
		ComplexFeatureBuilder builder = new ComplexFeatureBuilder(BRIDGE_TYPE);

		// Act
		builder.append(BRIDGE_NAME, londonBridge);
		Object actualValue = builder.values.get(BRIDGE_NAME).get(0);

		// Assert	
		Assert.assertSame(londonBridge, actualValue);
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
	public void append_exceedMaxOccursLimit_throwsIndexOutOfBoundsException() throws Exception {
		// Arrange
		ComplexFeatureBuilder builder = new ComplexFeatureBuilder(BRIDGE_TYPE);
		builder.append(BRIDGE_NAME, londonBridge);

		// Act
		try {
			builder.append(BRIDGE_NAME, londonBridge); // Add it once too many times.
		}
		catch (IndexOutOfBoundsException ioobe) {
			assertExceptionMessage(
				ioobe, 
				"You can't add another object with the name of 'urn:Bridge:Test:1.1:bridgeName' because you already have the maximum number (1) allowed by the property descriptor.");
		}
	}

	@Test
	public void buildFeature_validInput_buildsFeature() {
		// Arrange
		ComplexFeatureBuilder builder = new ComplexFeatureBuilder(BRIDGE_TYPE);

		AttributeImpl description = new AttributeImpl("description", DESCRIPTION_DESCRIPTOR, null);

		builder.append(BRIDGE_NAME, londonBridge);
		builder.append(LOCATION, location);
		builder.append(DESCRIPTION, description);

		// Act
		Feature feature = builder.buildFeature("id");

		// Assert
		assertNotNull(feature);
		assertSame(londonBridge, feature.getProperty(BRIDGE_NAME));
		assertSame(location, feature.getProperty(LOCATION));
		assertSame(description, feature.getProperty(DESCRIPTION));
	}

	@Test
	public void buildFeature_missingDescription_descriptionDefaultsToNull() {
		// TODO: this may not be a valid test because it might not be possible to coalesce to null. 
		
		// Arrange
		ComplexFeatureBuilder builder = new ComplexFeatureBuilder(BRIDGE_TYPE);

		builder.append(BRIDGE_NAME, londonBridge);
		builder.append(LOCATION, location);

		// Act
		Feature feature = builder.buildFeature("id");

		// Assert
		assertNull(feature.getProperty(DESCRIPTION).getValue());
	}

	@Test(expected=IllegalStateException.class)
	public void buildFeature_noLocationSet_throwsIllegalStateException() throws Exception {
		// Arrange
		ComplexFeatureBuilder builder = new ComplexFeatureBuilder(BRIDGE_TYPE);

		// Deliberately not setting location
		builder.append(BRIDGE_NAME, londonBridge);

		// Act
		try {
			builder.buildFeature("id");
		}
		catch (IllegalStateException ise) {
			assertExceptionMessage(
				ise, 
				"Failed to build feature 'urn:Bridge:Test:1.1:Bridge'; its property 'urn:Bridge:Test:1.1:location' requires at least 1 occurrence(s) but number of occurrences was 0.");
		}
	}

	@Test
	public void buildFeature_validCyclicType_buildsFeature() {
		// TODO: figure out the best way to build cyclic types

		// Arrange
		ComplexFeatureBuilder builder = new ComplexFeatureBuilder(GEOLOGICALUNITTYPE_TYPE);

		// builder.append(OCCURRENCE_NAME, );

		// Act
		Feature feature = builder.buildFeature("id");

		// Assert
		fail("Unfinished");
	}	
}






 
 














