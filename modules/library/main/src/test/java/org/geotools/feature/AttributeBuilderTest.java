package org.geotools.feature;

import java.util.ArrayList;
import java.util.Collection;

import org.geotools.ExceptionChecker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.opengis.feature.Attribute;
import org.opengis.feature.Property;


public class AttributeBuilderTest {
	
	private AttributeBuilder builder;
	
	@Before
    public void setUp() {
		this.builder = new AttributeBuilder(new LenientFeatureFactoryImpl());
    }
	
	@After
    public void tearDown() {
		this.builder = null;
    }	
	
	@Test
	public void setType_validType_descriptorIsSetToNull() {
		// Act
		builder.setType(FakeTypes.ANYSIMPLETYPE_TYPE);

		// Assert
		Assert.assertNull(builder.getDescriptor());
	}
	
	@Test
	public void setDescriptor_validDescriptor_typeIsSetToDescriptorsType() {
		// Act
		builder.setDescriptor(FakeTypes.Mine.mineNAME_DESCRIPTOR);

		// Assert
		Assert.assertSame(
				FakeTypes.Mine.mineNAME_DESCRIPTOR.getType(), 
			builder.getType());
	}

	@Test
	public void add_validArguments_returnsAttributeImpl() {
		// Arrange
		builder.setType(FakeTypes.Mine.MINENAMETYPE_TYPE);

		// Act
		Attribute mineName = builder.add(
			"test_id",
			"Sharlston Colliery",
			FakeTypes.Mine.NAME_mineName);

		// Assert
		Assert.assertEquals(
			"AttributeImpl:mineName<string id=test_id>=Sharlston Colliery", 
			mineName.toString());
	}
	
	@Test
	public void add_validArguments_attributeIsAddedToProperties() {
		// Arrange
		builder.setType(FakeTypes.Mine.MINENAMETYPE_TYPE);

		// Act
		Attribute mineName = builder.add(
			"test_id",
			"Sharlston Colliery",
			FakeTypes.Mine.NAME_mineName);

		// Assert
		Assert.assertTrue(builder.getProperties().contains(mineName));
	}

	@Test(expected=IllegalArgumentException.class)
	public void add_invalidName_throwsIllegalArgumentException() throws Exception {
		// Arrange
		builder.setType(FakeTypes.Mine.MINENAMETYPE_TYPE);

		// Act
		try {
			builder.add(
			"test_id",
			"Sharlston Colliery",
			new NameImpl(FakeTypes.Mine.MINE_NAMESPACE, "INVALID_NAME")); // Intentionally invalid name.
		}
		catch (IllegalArgumentException iae) {
			ExceptionChecker.assertExceptionMessage(
				iae,
				"Could not locate attribute: urn:cgi:xmlns:GGIC:EarthResource:1.1:INVALID_NAME in type: urn:cgi:xmlns:GGIC:EarthResource:1.1:MineNameType");		
		}
	}

	@Test
	public void build_typeIsMineNameTypeAndAddedDataIsValid_buildsAComplexAttributeImpl() {
		// Arrange
		builder.setType(FakeTypes.Mine.MINENAMETYPE_TYPE);

		builder.add(
			"test_id 1",
			true,
			FakeTypes.Mine.NAME_isPreferred);
		
		builder.add(
			"test_id",
			"Sharlston Colliery",
			FakeTypes.Mine.NAME_mineName);

		// Act
		Attribute MineName = builder.build();

		// Assert
		Assert.assertEquals(
			"ComplexAttributeImpl:MineNameType=[AttributeImpl:isPreferred<boolean id=test_id 1>=true, AttributeImpl:mineName<string id=test_id>=Sharlston Colliery]", 
			MineName.toString());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//
//	@Test
//	public void build_typeIsMineTypeAndAddedDataIsValid_buildsAComplexAttributeImpl() {
//		// Arrange
//		builder.setType(FakeTypes.Mine.MINENAMETYPE_TYPE);
//
//		builder.add(
//			"test_id",
//			"Sharlston Colliery",
//			FakeTypes.Mine.NAME_mineName);
//
//		builder.add(
//			"test_id 1",
//			true,
//			FakeTypes.Mine.NAME_isPreferred);
//
//		Attribute mineNameType = builder.build();
//
//		Collection<Property> properties = new ArrayList<Property>();
//		properties.add(mineNameType);
//		
//		builder.init();
//		builder.setType(FakeTypes.Mine.MINETYPE_TYPE);
//
//		builder.add(
//			"test_id 2",
//			properties, 
//			FakeTypes.Mine.NAME_mineName);
//		
//		Attribute mine = builder.build("er.mine.S0000001");
//	
//		// Assert
//		Assert.assertEquals(
//			"FeatureImpl:MineType<MineType id=er.mine.S0000001>=[ComplexAttributeImpl:mineName<MineNamePropertyType id=test_id 2>=[ComplexAttributeImpl:MineNameType=[AttributeImpl:mineName<string id=test_id>=Sharlston Colliery, AttributeImpl:isPreferred<boolean id=test_id 1>=true]]]", 
//			mine.toString());
//	}
}

































