package org.geotools.feature;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.geotools.feature.type.AttributeDescriptorImpl;
import org.geotools.feature.type.AttributeTypeImpl;
import org.geotools.feature.type.ComplexTypeImpl;
import org.geotools.feature.type.FeatureTypeImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.Attribute;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;

public class AttributeBuilderTest {
		
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
	
	// My mine representation
	public static final String MINE_NAMESPACE = "PlaceHolderMineNamespace";
	
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

	public static ArrayList<PropertyDescriptor> MINENAMETYPE_SCHEMA = new ArrayList<PropertyDescriptor>();
	
	static {
		MINENAMETYPE_SCHEMA.add(ISPREFERRED_DESCRIPTOR);
		MINENAMETYPE_SCHEMA.add(mineNAME_DESCRIPTOR);
	}
	
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
		
	public static ArrayList<PropertyDescriptor> MINENAMEPROPERTYTYPE_SCHEMA = new ArrayList<PropertyDescriptor>();
		
	static {
		MINENAMEPROPERTYTYPE_SCHEMA.add(MINENAME_DESCRIPTOR);
	}
		
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

	public static ArrayList<PropertyDescriptor> MINETYPE_SCHEMA = 
		new ArrayList<PropertyDescriptor>();
	
	static {
		MINETYPE_SCHEMA.add(MINEmineNAME_DESCRIPTOR);
	}
	
	// (7)
	public static final ComplexType MINETYPE_TYPE = 
		new ComplexTypeImpl(
		/* name:         */ NAME_MineType,
		/* properties:   */ MINETYPE_SCHEMA,
		/* identified:   */ true, 
		/* isAbstract:   */ false,
		/* restrictions: */ Collections.<Filter> emptyList(),
		/* superType:    */ ANYTYPE_TYPE, // In real life it's actually a MiningFeatureType but I don't think it matters.
		/* description:  */ null);

	// Helper method - TODO: where's the best place to put this, is it the best approach?
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

	@Test
	public void add_validArguments_returnsAttributeImpl() {
		// Arrange
		AttributeBuilder builder = new AttributeBuilder(new LenientFeatureFactoryImpl());
		builder.setType(MINENAMETYPE_TYPE);

		// Act
		Attribute mineName = builder.add(
			"test_id",
			"Sharlston Colliery",
			NAME_mineName);
		
		// Assert
		Assert.assertEquals(
			"AttributeImpl:mineName<string id=test_id>=Sharlston Colliery", 
			mineName.toString());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void add_invalidName_throws() throws Exception {
		// Arrange
		AttributeBuilder builder = new AttributeBuilder(new LenientFeatureFactoryImpl());
		builder.setType(MINENAMETYPE_TYPE);

		// Act
		try {
			builder.add(
			"test_id",
			"Sharlston Colliery",
			new NameImpl(MINE_NAMESPACE, "INVALID_NAME")); // Intentionally invalid name.
		}
		catch (IllegalArgumentException iae) {
			assertExceptionMessage(
				iae,
				"Could not locate attribute: PlaceHolderMineNamespace:INVALID_NAME in type: PlaceHolderMineNamespace:MineNameType");		
		}
	}

	@Test
	public void build_typeIsMineNameTypeAndAddedDataIsValid_buildsAComplexAttributeImpl() {
		// Arrange
		AttributeBuilder builder = new AttributeBuilder(new LenientFeatureFactoryImpl());
		builder.setType(MINENAMETYPE_TYPE);

		builder.add(
			"test_id",
			"Sharlston Colliery",
			NAME_mineName);

		builder.add(
			"test_id 1",
			true,
			NAME_isPreferred);

		// Act
		Attribute MineName = builder.build();

		// Assert
		Assert.assertEquals(
			"ComplexAttributeImpl:MineNameType=[AttributeImpl:mineName<string id=test_id>=Sharlston Colliery, AttributeImpl:isPreferred<boolean id=test_id 1>=true]", 
			MineName.toString());
	}

	@Test
	public void build_typeIsMineTypeAndAddedDataIsValid_buildsAComplexAttributeImpl() {
		// Arrange
		AttributeBuilder builder = new AttributeBuilder(new LenientFeatureFactoryImpl());
		builder.setType(MINENAMETYPE_TYPE);

		builder.add(
			"test_id",
			"Sharlston Colliery",
			NAME_mineName);

		builder.add(
			"test_id 1",
			true,
			NAME_isPreferred);

		Attribute mineNameType = builder.build();

		Collection<Property> properties = new ArrayList<Property>();
		properties.add(mineNameType);
		
		builder.init();
		builder.setType(MINETYPE_TYPE);

		builder.add(
			"test_id 2",
			properties, 
			NAME_mineName);
		
		Attribute mine = builder.build();
	
		// Assert
		Assert.assertEquals(
			"ComplexAttributeImpl:MineType=[ComplexAttributeImpl:mineName<MineNamePropertyType id=test_id 2>=[ComplexAttributeImpl:MineNameType=[AttributeImpl:mineName<string id=test_id>=Sharlston Colliery, AttributeImpl:isPreferred<boolean id=test_id 1>=true]]]", 
			mine.toString());
	}
}

































