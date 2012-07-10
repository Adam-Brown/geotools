package org.geotools.feature.complex;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.geotools.ExceptionChecker;
import org.geotools.feature.AttributeBuilder;
import org.geotools.feature.AttributeImpl;
import org.geotools.feature.FakeTypes;
import org.geotools.feature.LenientFeatureFactoryImpl;
import org.geotools.feature.NameImpl;
import org.geotools.feature.type.AttributeDescriptorImpl;
import org.geotools.feature.type.FeatureTypeImpl;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.Attribute;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

public class ComplexFeatureBuilderTest {
    // EXTRUSIVE BASALT VEIN TYPE DEFINITION
    private static final String BASALT_NAMESPACE_URI = "urn:ExtrusiveBasaltVein:Test:1.1";
    private static final Name FAKEABSTRACTFEATURETYPE_NAME = new NameImpl(BASALT_NAMESPACE_URI, ":", "FakeAbstractFeatureType");
    private static final Name MAPPEDFEATURETYPE_NAME = new NameImpl(BASALT_NAMESPACE_URI, ":", "MappedFeatureType");
    private static final Name GEOLOGICALUNITTYPE_NAME = new NameImpl(BASALT_NAMESPACE_URI, ":", "GeologicalUnitType");
    private static final Name OCCURRENCE_NAME = new NameImpl(BASALT_NAMESPACE_URI, ":", "occurrence");
    private static final Name SPECIFICATION_NAME = new NameImpl(BASALT_NAMESPACE_URI, ":", "specification");
    private static final Name BASALTVEIN_NAME = new NameImpl(BASALT_NAMESPACE_URI, ":", "BasaltVein");

    // Create the FakeAbstractFeatureType
    private static final FeatureType FAKEABSTRACTFEATURETYPE_TYPE = new FeatureTypeImpl(
		FAKEABSTRACTFEATURETYPE_NAME,
		Collections.<PropertyDescriptor> emptyList(),
		null,
		true,
		Collections.<Filter> emptyList(),
		FakeTypes.NULLTYPE_TYPE,
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
    	FakeTypes.NULLTYPE_TYPE,
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
    
    private static AttributeImpl londonBridge = new AttributeImpl("London Bridge", FakeTypes.Bridge.BRIDGENAME_DESCRIPTOR, null);
    
    private static AttributeImpl location = new AttributeImpl(gm.createPoint(new Coordinate(1, 3)), FakeTypes.Bridge.LOCATION_DESCRIPTOR, null);
	
    @Test(expected=IllegalArgumentException.class)
	public void append_invalidName_throwsIllegalArgumentException() throws Exception {
		// Arrange
		ComplexFeatureBuilder builder = new ComplexFeatureBuilder(FakeTypes.Mine.MINETYPE_TYPE);

		// Act
		try {
			builder.append(new NameImpl("invalid_descriptor_name"), null);
		}
		catch (IllegalArgumentException iae) {
			ExceptionChecker.assertExceptionMessage(
				iae,
				"The name 'invalid_descriptor_name' is not a valid descriptor name for the type 'urn:cgi:xmlns:GGIC:EarthResource:1.1:MineType'.");
		}
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void append_validNameInvalidValueClass_throwsIllegalArgumentException() throws Exception {
		// Arrange
		ComplexFeatureBuilder builder = new ComplexFeatureBuilder(FakeTypes.Mine.MINETYPE_TYPE);

		// Act
		try {
			builder.append(FakeTypes.Mine.NAME_mineName, londonBridge); // Passing in londonBridge instead of a mineName.
		}
		catch (IllegalArgumentException iae) {
			ExceptionChecker.assertExceptionMessage(
				iae, 
				"The value provided contains an object of 'class java.lang.String' but the method expects an object of 'interface java.util.Collection'.");
		}
	}

	@Test(expected=IllegalArgumentException.class)
	public void append_validNameButNullValue_throwsIllegalArgumentException() throws Exception {
		// Arrange
		ComplexFeatureBuilder builder = new ComplexFeatureBuilder(FakeTypes.Mine.MINETYPE_TYPE);

		// Act
		try {
			builder.append(FakeTypes.Mine.NAME_mineName, null); // Passing a null reference for a non-nillable type.
		}
		catch (IllegalArgumentException iae) {
			ExceptionChecker.assertExceptionMessage(
				iae, 
				"The value provided is a null reference but the property descriptor 'AttributeDescriptorImpl urn:cgi:xmlns:GGIC:EarthResource:1.1:mineName <MineNamePropertyType:Collection> 1:2147483647' is non-nillable.");
		}
	}

	@Test
	public void append_validNameValidValue_valueShouldBeAddedToTheMap() {
		// Arrange
		ComplexFeatureBuilder builder = new ComplexFeatureBuilder(FakeTypes.Bridge.BRIDGE_TYPE);

		// Act
		builder.append(FakeTypes.Bridge.NAME_bridgeName, londonBridge);
		Object actualValue = builder.values.get(FakeTypes.Bridge.NAME_bridgeName).get(0);

		// Assert	
		Assert.assertSame(londonBridge, actualValue);
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
	public void append_exceedMaxOccursLimit_throwsIndexOutOfBoundsException() throws Exception {
		// Arrange
		ComplexFeatureBuilder builder = new ComplexFeatureBuilder(FakeTypes.Bridge.BRIDGE_TYPE);
		builder.append(FakeTypes.Bridge.NAME_bridgeName, londonBridge);

		// Act
		try {
			builder.append(FakeTypes.Bridge.NAME_bridgeName, londonBridge); // Add it once too many times.
		}
		catch (IndexOutOfBoundsException ioobe) {
			ExceptionChecker.assertExceptionMessage(
				ioobe, 
				"You can't add another object with the name of 'urn:Bridge:Test:1.1:bridgeName' because you already have the maximum number (1) allowed by the property descriptor.");
		}
	}

	@Test
	public void buildFeature_validInput_buildsFeature() {
		// Arrange
		ComplexFeatureBuilder builder = new ComplexFeatureBuilder(FakeTypes.Bridge.BRIDGE_TYPE);

		AttributeImpl description = new AttributeImpl(
			"description", 
			FakeTypes.Bridge.DESCRIPTION_DESCRIPTOR, 
			null);

		builder.append(FakeTypes.Bridge.NAME_bridgeName, londonBridge);
		builder.append(FakeTypes.Bridge.NAME_location, location);
		builder.append(FakeTypes.Bridge.NAME_description, description);

		// Act
		Feature feature = builder.buildFeature("id");

		// Assert
		assertNotNull(feature);
		assertSame(londonBridge, feature.getProperty(FakeTypes.Bridge.NAME_bridgeName));
		assertSame(location, feature.getProperty(FakeTypes.Bridge.NAME_location));
		assertSame(description, feature.getProperty(FakeTypes.Bridge.NAME_description));
	}

	@Test
	public void buildFeature_missingDescription_descriptionDefaultsToNull() {
		// TODO: this may not be a valid test because it might not be possible to coalesce to null. 
		
		// Arrange
		ComplexFeatureBuilder builder = new ComplexFeatureBuilder(FakeTypes.Bridge.BRIDGE_TYPE);

		builder.append(FakeTypes.Bridge.NAME_bridgeName, londonBridge);
		builder.append(FakeTypes.Bridge.NAME_location, location);

		// Act
		Feature feature = builder.buildFeature("id");

		// Assert
		assertNull(feature.getProperty(FakeTypes.Bridge.NAME_description).getValue());
	}

	@Test(expected=IllegalStateException.class)
	public void buildFeature_noLocationSet_throwsIllegalStateException() throws Exception {
		// Arrange
		ComplexFeatureBuilder builder = new ComplexFeatureBuilder(FakeTypes.Bridge.BRIDGE_TYPE);

		// Deliberately not setting location
		builder.append(FakeTypes.Bridge.NAME_bridgeName, londonBridge);

		// Act
		try {
			builder.buildFeature("id");
		}
		catch (IllegalStateException ise) {
			ExceptionChecker.assertExceptionMessage(
				ise, 
				"Failed to build feature 'urn:Bridge:Test:1.1:BridgeType'; its property 'urn:Bridge:Test:1.1:location' requires at least 1 occurrence(s) but number of occurrences was 0.");
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
	
	
	@Test
	public void build_typeIsMineTypeAndAddedDataIsValid_buildsAComplexAttributeImpl() {
		// Arrange
		AttributeBuilder builder = new AttributeBuilder(new LenientFeatureFactoryImpl());
		builder.setType(FakeTypes.Mine.MINENAMETYPE_TYPE);

		builder.add(
			"test_id 1",
			true,
			FakeTypes.Mine.NAME_isPreferred);

		builder.add(
			"test_id",
			"Sharlston Colliery",
			FakeTypes.Mine.NAME_mineName);

		final Attribute mineName = builder.build();
		
		Collection<Attribute> mineNames = new ArrayList<Attribute>();
		mineNames.add(mineName);
		
		builder.init();
		builder.setType(FakeTypes.Mine.MINENAMEPROPERTYTYPE_TYPE);
		
		builder.add(
			"test_id",
			mineNames,
			FakeTypes.Mine.NAME_MineName
			);
		
		Attribute mineNameProperty = builder.build();
		
		// Act
		ComplexFeatureBuilder complexFeatureBuilder = new ComplexFeatureBuilder(FakeTypes.Mine.MINETYPE_TYPE);
		
		complexFeatureBuilder.append(
			FakeTypes.Mine.NAME_mineName,
			mineNameProperty);
		
		Feature mine = complexFeatureBuilder.buildFeature("er.mine.S0000001");
	
		// Assert
		Assert.assertEquals(
			"FeatureImpl:MineType<MineType id=er.mine.S0000001>=[ComplexAttributeImpl:MineNameType=[AttributeImpl:mineName<string id=test_id>=Sharlston Colliery, AttributeImpl:isPreferred<boolean id=test_id 1>=true]]", 
			mine.toString());
	}
}






 
 














