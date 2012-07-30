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
import org.opengis.feature.ComplexAttribute;
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

    private static final Name FAKEABSTRACTFEATURETYPE_NAME = new NameImpl(BASALT_NAMESPACE_URI,
            ":", "FakeAbstractFeatureType");

    private static final Name MAPPEDFEATURETYPE_NAME = new NameImpl(BASALT_NAMESPACE_URI, ":",
            "MappedFeatureType");

    private static final Name GEOLOGICALUNITTYPE_NAME = new NameImpl(BASALT_NAMESPACE_URI, ":",
            "GeologicalUnitType");

    private static final Name OCCURRENCE_NAME = new NameImpl(BASALT_NAMESPACE_URI, ":",
            "occurrence");

    private static final Name SPECIFICATION_NAME = new NameImpl(BASALT_NAMESPACE_URI, ":",
            "specification");

    private static final Name BASALTVEIN_NAME = new NameImpl(BASALT_NAMESPACE_URI, ":",
            "BasaltVein");

    // Create the FakeAbstractFeatureType
    private static final FeatureType FAKEABSTRACTFEATURETYPE_TYPE = new FeatureTypeImpl(
            FAKEABSTRACTFEATURETYPE_NAME, Collections.<PropertyDescriptor> emptyList(), null, true,
            Collections.<Filter> emptyList(), FakeTypes.NULLTYPE_TYPE, null);

    // Create the MappedFeatureType
    private static final List<PropertyDescriptor> MAPPEDFEATURETYPE_SCHEMA = new ArrayList<PropertyDescriptor>() {
        {
            add(new AttributeDescriptorImpl(FAKEABSTRACTFEATURETYPE_TYPE, SPECIFICATION_NAME, 1, 1,
                    false, null));
        }
    };

    private static final FeatureType MAPPEDFEATURETYPE_TYPE = new FeatureTypeImpl(
            MAPPEDFEATURETYPE_NAME, MAPPEDFEATURETYPE_SCHEMA, null, false,
            Collections.<Filter> emptyList(), FakeTypes.NULLTYPE_TYPE, null);

    // Create the GeologicalUnitType
    private static final List<PropertyDescriptor> GEOLOGICALUNITTYPE_SCHEMA = new ArrayList<PropertyDescriptor>() {
        {
            add(new AttributeDescriptorImpl(MAPPEDFEATURETYPE_TYPE, OCCURRENCE_NAME, 0,
                    Integer.MAX_VALUE, false, null));
        }
    };

    private static final FeatureType GEOLOGICALUNITTYPE_TYPE = new FeatureTypeImpl(
            GEOLOGICALUNITTYPE_NAME, GEOLOGICALUNITTYPE_SCHEMA, null, false,
            Collections.<Filter> emptyList(), FAKEABSTRACTFEATURETYPE_TYPE, null);

    private static ComplexAttribute getAMineNameProperty(String name, boolean isPreferred) {
        AttributeBuilder builder = new AttributeBuilder(new LenientFeatureFactoryImpl());
        builder.setType(FakeTypes.Mine.MINENAMETYPE_TYPE);

        builder.add("isPreferred_testId", isPreferred, FakeTypes.Mine.NAME_isPreferred);

        builder.add("mineName_testId", name, FakeTypes.Mine.NAME_mineName);

        final Attribute mineName = builder.build();

        Collection<Attribute> mineNames = new ArrayList<Attribute>();
        mineNames.add(mineName);

        builder.init();
        builder.setType(FakeTypes.Mine.MINENAMEPROPERTYTYPE_TYPE);

        builder.add("MineName_testId", mineNames, FakeTypes.Mine.NAME_MineName);

        return (ComplexAttribute) builder.build();
    }

    private static GeometryFactory gm = new GeometryFactory();

    @Test(expected = IllegalArgumentException.class)
    public void append_invalidName_throwsIllegalArgumentException() throws Exception {
        // Arrange
        ComplexFeatureBuilder builder = new ComplexFeatureBuilder(FakeTypes.Mine.MINETYPE_TYPE);

        // Act
        try {
            builder.append(new NameImpl("invalid_descriptor_name"), null);
        } catch (IllegalArgumentException iae) {
            ExceptionChecker
                    .assertExceptionMessage(
                            iae,
                            "The name 'invalid_descriptor_name' is not a valid descriptor name for the type 'urn:org:example:MineType'.");
        }
    }

    /**
     * @throws Exception
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void append_validNameInvalidValueClass_throwsIllegalArgumentException() throws Exception {
        // Arrange
        ComplexFeatureBuilder builder = new ComplexFeatureBuilder(FakeTypes.Mine.MINETYPE_TYPE);

        // Act
        try {
            builder.append(FakeTypes.Mine.NAME_mineName, new AttributeImpl("Test",
                    FakeTypes.ANYSIMPLETYPE_TYPE, null)); // Passing in simple type instead of a mineName.
        } catch (IllegalArgumentException iae) {
            ExceptionChecker
                    .assertExceptionMessage(
                            iae,
                            "The value provided contains an object of 'class java.lang.Object' but the method expects an object of 'interface java.util.Collection'.");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void append_validNameButNullValue_throwsIllegalArgumentException() throws Exception {
        // Arrange
        ComplexFeatureBuilder builder = new ComplexFeatureBuilder(FakeTypes.Mine.MINETYPE_TYPE);

        // Act
        try {
            builder.append(FakeTypes.Mine.NAME_mineName, null); // Passing a null reference for a non-nillable type.
        } catch (IllegalArgumentException iae) {
            ExceptionChecker
                    .assertExceptionMessage(
                            iae,
                            "The value provided is a null reference but the property descriptor 'AttributeDescriptorImpl urn:org:example:mineName <MineNamePropertyType:Collection> 1:3' is non-nillable.");
        }
    }

    @Test
    public void append_validNameValidValue_valueShouldBeAddedToTheMap() {
        // Arrange
        ComplexFeatureBuilder builder = new ComplexFeatureBuilder(FakeTypes.Mine.MINETYPE_TYPE);
        ComplexAttribute mineNameProperty = getAMineNameProperty("mine 1", true);

        // Act
        builder.append(FakeTypes.Mine.NAME_mineName, mineNameProperty);
        Object actualValue = builder.values.get(FakeTypes.Mine.NAME_mineName).get(0);

        // Assert
        Assert.assertSame(mineNameProperty, actualValue);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void append_exceedMaxOccursLimit_throwsIndexOutOfBoundsException() throws Exception {
        // Arrange
        ComplexFeatureBuilder builder = new ComplexFeatureBuilder(FakeTypes.Mine.MINETYPE_TYPE);
        builder.append(FakeTypes.Mine.NAME_mineName, getAMineNameProperty("mine 1", true));
        builder.append(FakeTypes.Mine.NAME_mineName, getAMineNameProperty("mine 2", false));
        builder.append(FakeTypes.Mine.NAME_mineName, getAMineNameProperty("mine 3", false));

        // Act
        try {
            builder.append(FakeTypes.Mine.NAME_mineName, getAMineNameProperty("mine 4", false)); // Add it once too many times.
        } catch (IndexOutOfBoundsException ioobe) {
            ExceptionChecker
                    .assertExceptionMessage(
                            ioobe,
                            "You can't add another object with the name of 'urn:org:example:mineName' because you already have the maximum number (3) allowed by the property descriptor.");
        }
    }

    @Test(expected = IllegalStateException.class)
    public void buildFeature_noLocationSet_throwsIllegalStateException() throws Exception {
        // Arrange
        ComplexFeatureBuilder builder = new ComplexFeatureBuilder(FakeTypes.Mine.MINETYPE_TYPE);

        // Deliberately not setting urn:org:example:mineName

        // Act
        try {
            builder.buildFeature("id");
        } catch (IllegalStateException ise) {
            ExceptionChecker
                    .assertExceptionMessage(
                            ise,
                            "Failed to build feature 'urn:org:example:MineType'; its property 'urn:org:example:mineName' requires at least 1 occurrence(s) but number of occurrences was 0.");
        }
    }

    @Test
    public void buildFeature_validCyclicType_buildsFeature() {
        // Arrange
        ComplexFeatureBuilder builder = new ComplexFeatureBuilder(GEOLOGICALUNITTYPE_TYPE);

//        builder.append(OCCURRENCE_NAME, );

        // Act
        Feature feature = builder.buildFeature("id");

        // Assert
        fail("Unfinished");
    }

    @Test
    public void build_typeIsMineTypeAndAddedDataIsValid_buildsFeature() {
        // Arrange
        ComplexAttribute mineNameProperty = getAMineNameProperty("Sharlston Colliery", true);

        // Act
        ComplexFeatureBuilder complexFeatureBuilder = new ComplexFeatureBuilder(
                FakeTypes.Mine.MINETYPE_TYPE);

        complexFeatureBuilder.append(FakeTypes.Mine.NAME_mineName, mineNameProperty);

        Feature mine = complexFeatureBuilder.buildFeature("er.mine.S0000001");

        // Assert
        Assert.assertEquals(
                "FeatureImpl:MineType<MineType id=er.mine.S0000001>=[ComplexAttributeImpl:MineNamePropertyType=[ComplexAttributeImpl:MineName<MineNameType id=MineName_testId>=[ComplexAttributeImpl:MineNameType=[AttributeImpl:isPreferred<boolean id=isPreferred_testId>=true, AttributeImpl:mineName<string id=mineName_testId>=Sharlston Colliery]]]]",
                mine.toString());
    }
}
