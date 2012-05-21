package org.geotools.feature.complex;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.geotools.feature.NameImpl;
import org.geotools.feature.type.AttributeDescriptorImpl;
import org.geotools.feature.type.AttributeTypeImpl;
import org.geotools.feature.type.FeatureTypeImpl;
import org.geotools.feature.type.PropertyDescriptorImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import junit.framework.TestCase;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import junit.framework.TestCase;


public class ComplexFeatureBuilderTests extends TestCase {
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
	
	@Before
	protected void setUp() {

	}

	@Test
	public void test_append_invalidName_throwsIllegalArgumentException() {
		// Arrange
		ComplexFeatureBuilder builder = new ComplexFeatureBuilder(BRIDGE_TYPE);

		// Act
		try {
			builder.append(new NameImpl("invalid_descriptor_name"), "test");
			fail("Expected IllegalArgumentException but it wasn't thrown.");
		}
		catch (IllegalArgumentException iae) {
			String expectedMessage = "The name 'invalid_descriptor_name' is not a valid descriptor name for the type 'urn:Bridge:Test:1.1:Bridge'.";
			if (iae.getMessage().compareTo(expectedMessage) != 0) {
				fail("Expected IllegalArgumentExceptionMessage to say: '" + expectedMessage + "' but got: '" + iae.getMessage() + "'");
			}

			// Assert (This is the expected exception).
			return; 
		}
		catch (Exception e) {
			fail("Expected IllegalArgumentException but it wasn't thrown; got " + e.getClass() + " instead. " + e.getMessage());
		}
	}
	
	/**
	 * 
	 */
	@Test
	public void test_append_validNameInvalidValueClass_throwsIllegalArgumentException() {
		// Arrange
		ComplexFeatureBuilder builder = new ComplexFeatureBuilder(BRIDGE_TYPE);
		
		// Act
		try {
			builder.append(BRIDGE_NAME, 1); // Passing an int instead of string.
			fail("Expected IllegalArgumentException but it wasn't thrown.");
		}
		catch (IllegalArgumentException iae) {
			String expectedMessage = "The value provided is an object of 'class java.lang.Integer' but the method expects an object of 'class java.lang.String'.";
			if (iae.getMessage().compareTo(expectedMessage) != 0) {
				fail("Expected IllegalArgumentExceptionMessage to say: '" + expectedMessage + "' but got: '" + iae.getMessage() + "'");
			}
			
			// Assert (This is the expected exception).
			return; 
		}
		catch (Exception e) {
			fail("Expected IllegalArgumentException but it wasn't thrown; got " + e.getClass() + " instead. " + e.getMessage());
		}
	}

	@Test
	public void test_append_validNameButNullValue_throwsIllegalArgumentException() {
		// Arrange
		ComplexFeatureBuilder builder = new ComplexFeatureBuilder(BRIDGE_TYPE);

		// Act
		try {
			builder.append(BRIDGE_NAME, null); // Passing a null reference for a non-nillable type.
			fail("Expected IllegalArgumentException but it wasn't thrown.");
		}
		catch (IllegalArgumentException iae) {
			String expectedMessage = "The value provided is a null reference but the property descriptor 'AttributeDescriptorImpl urn:Bridge:Test:1.1:bridgeName <string:String> 0:1' is non-nillable.";
			if (iae.getMessage().compareTo(expectedMessage) != 0) {
				fail("Expected IllegalArgumentExceptionMessage to say: '" + expectedMessage + "' but got: '" + iae.getMessage() + "'");
			}

			// Assert (This is the expected exception).
			return; 
		}
		catch (Exception e) {
			fail("Expected IllegalArgumentException but it wasn't thrown; got " + e.getClass() + " instead. " + e.getMessage());
		}
	}

	@Test
	public void test_append_validNameValidValue_valueShouldBeAddedToTheMap() {
		// Arrange
		ComplexFeatureBuilder builder = new ComplexFeatureBuilder(BRIDGE_TYPE);
		
		// Act
		String expectedValue = "London Bridge";
		builder.append(BRIDGE_NAME, expectedValue);
		Object actualValue = builder.values.get(BRIDGE_NAME).get(0).getValue();

		// Assert	
		Assert.assertSame(expectedValue, actualValue);
	}
	
	@Test
	public void test_append_exceedMaxOccursLimit_throwsIndexOutOfBoundsException() {
		// Arrange
		ComplexFeatureBuilder builder = new ComplexFeatureBuilder(BRIDGE_TYPE);
		builder.append(BRIDGE_NAME, "London Bridge");

		// Act
		try {
			builder.append(BRIDGE_NAME, "One too many names"); // Too many names
			fail("Expected IndexOutOfBoundsException but it wasn't thrown.");
		}
		catch (IndexOutOfBoundsException iae) {
			String expectedMessage = "You can't add another object with the name of 'urn:Bridge:Test:1.1:bridgeName' because you already have the maximum number (1) allowed by the property descriptor.";
			if (iae.getMessage().compareTo(expectedMessage) != 0) {
				fail("Expected IndexOutOfBoundsException to say: '" + expectedMessage + "' but got: '" + iae.getMessage() + "'");
			}

			// Assert (This is the expected exception).
			return; 
		}
		catch (Exception e) {
			fail("Expected IndexOutOfBoundsException but it wasn't thrown; got " + e.getClass() + " instead. " + e.getMessage());
		}
	}

	@Test
	public void test_buildFeature_validInput_buildsFeature() {
		// Arrange
		ComplexFeatureBuilder builder = new ComplexFeatureBuilder(BRIDGE_TYPE);

		String expectedBridgeName = "London Bridge";
		Point expectedLocation = gm.createPoint(new Coordinate(1, 3));
		String expectedDescription = "description";

		builder.append(BRIDGE_NAME, expectedBridgeName);
		builder.append(LOCATION, expectedLocation);
		builder.append(DESCRIPTION, expectedDescription);

		// Act
		Feature feature = builder.buildFeature("id");

		// Assert
		assertNotNull(feature);
		assertSame(expectedBridgeName, feature.getProperty(BRIDGE_NAME).getValue());
		assertSame(expectedLocation, feature.getProperty(LOCATION).getValue());
		assertSame(expectedDescription, feature.getProperty(DESCRIPTION).getValue());
	}

	@Test
	public void test_buildFeature_missingDescription_descriptionDefaultsToNull() {
		// Arrange
		ComplexFeatureBuilder builder = new ComplexFeatureBuilder(BRIDGE_TYPE);

		builder.append(BRIDGE_NAME, "London Bridge");
		builder.append(LOCATION, gm.createPoint(new Coordinate(1, 3)));

		// Act
		Feature feature = builder.buildFeature("id");

		// Assert
		assertNull(feature.getProperty(DESCRIPTION).getValue());
	}

	@Test
	public void test_buildFeature_noLocationSet_throwsIllegalStateException() {
		// Arrange
		ComplexFeatureBuilder builder = new ComplexFeatureBuilder(BRIDGE_TYPE);

		// Deliberately not setting location
		builder.append(BRIDGE_NAME, "London Bridge");

		// Act
		try {
			Feature feature = builder.buildFeature("id");
			fail("expected an exception");
		}
		catch (IllegalStateException ise) {
			String expectedMessage = "Failed to build feature 'urn:Bridge:Test:1.1:Bridge'; its property 'urn:Bridge:Test:1.1:location' requires at least 1 occurrence(s) but number of occurrences was 0.";
			if (ise.getMessage().compareTo(expectedMessage) != 0) {
				fail("Expected IllegalStateException to say: '" + expectedMessage + "' but got: '" + ise.getMessage() + "'");
			}

			// Assert (This is the expected exception).
			return;
		}
		catch (Exception e) {
			fail("Expected IllegalStateException but it wasn't thrown; got " + e.getClass() + " instead. " + e.getMessage());
		}
	}

	@Test
	public void test_buildFeature_validCyclicType_buildsFeature() {
		// Arrange
		ComplexFeatureBuilder builder = new ComplexFeatureBuilder(GEOLOGICALUNITTYPE_TYPE);

		builder.append(OCCURRENCE_NAME, );

		// here's the difficulty with cyclic types

		// Act
		Feature feature = builder.buildFeature("id");

		// Assert
		assertNotNull(feature);
		assertSame(expectedBridgeName, feature.getProperty(BRIDGE_NAME).getValue());
		assertSame(expectedLocation, feature.getProperty(LOCATION).getValue());
		assertSame(expectedDescription, feature.getProperty(DESCRIPTION).getValue());
	}	
}








 
 














