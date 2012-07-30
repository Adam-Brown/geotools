package org.geotools.feature.wrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.geotools.feature.AttributeImpl;
import org.geotools.feature.ComplexAttributeImpl;
import org.geotools.feature.FakeTypes;
import org.geotools.feature.FakeTypes.Mine;
import org.geotools.feature.MineType;
import org.opengis.feature.Attribute;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.filter.identity.Identifier;
import org.geotools.feature.FeatureImpl;
import org.geotools.filter.identity.FeatureIdImpl;
import org.geotools.filter.identity.GmlObjectIdImpl;

public class FeatureWrapperTest {
    private static Feature getFeature() {
        // AttributeImpl:mineName<string id=mineName_1>=Pieces of Eight - Admiral Hill
        Attribute mineName = new AttributeImpl("Pieces of Eight - Admiral Hill", Mine.mineNAME_DESCRIPTOR, new GmlObjectIdImpl("mineName"));

        // AttributeImpl:isPreferred<boolean id=isPreferred_1>=true, 
        Attribute isPreferred = new AttributeImpl(true, Mine.ISPREFERRED_DESCRIPTOR, new GmlObjectIdImpl("isPreferred"));

        Collection<Property> MineNameTypeProperties = new ArrayList<Property>();
        MineNameTypeProperties.add(mineName);
        MineNameTypeProperties.add(isPreferred);
        
        // ComplexAttributeImpl:MineNameType=
        ComplexAttribute MineNameType = new ComplexAttributeImpl(MineNameTypeProperties, Mine.MINENAMETYPE_TYPE, null);
        
        Collection<Property> MineNameProperties = new ArrayList<Property>();
        MineNameProperties.add(MineNameType);
        
        // ComplexAttributeImpl:MineName<MineNameType id=MINENAMETYPE_TYPE_1>=
        ComplexAttribute MineName = new ComplexAttributeImpl(MineNameProperties, Mine.MINENAME_DESCRIPTOR, new GmlObjectIdImpl("MineName"));
        
        Collection<Property> MineNamePropertyProperties = new ArrayList<Property>();
        MineNamePropertyProperties.add(MineName);
        
        // ComplexAttributeImpl:MineNamePropertyType=
        ComplexAttribute MineNamePropertyType = new ComplexAttributeImpl(MineNamePropertyProperties, Mine.MINENAMEPROPERTYTYPE_TYPE, null);        
        
        Collection<Property> MineProperties = new ArrayList<Property>();
        MineProperties.add(MineNamePropertyType);        
        
        // FeatureImpl:MineType<MineType id=Mine>=
        Feature mine = new FeatureImpl(MineProperties, Mine.MINETYPE_TYPE, new FeatureIdImpl("Mine"));
        
        return mine;        
    }

    @Test
    public void wrap_validFeature_returnsWrappedFeature() {
        // Arrange
        Feature mine = getFeature();
        
        // Act
        MineType wrappedMine = FeatureWrapper.Wrap(mine, MineType.class);
        
        // Assert
        Assert.assertEquals("Pieces of Eight - Admiral Hill", wrappedMine.firstName);
    }
    
    @Test
    public void wrap_invalidFeature_throwsXXXXX() {
        // Arrange
        Collection<Property> properties = new ArrayList<Property>();
        Feature mine = new FeatureImpl(properties, Mine.MINETYPE_TYPE, new FeatureIdImpl("Invalid mine."));
        
        // Act
        MineType wrappedMine = FeatureWrapper.Wrap(mine, MineType.class);
        
        // Assert
        Assert.assertEquals("Pieces of Eight - Admiral Hill", wrappedMine.firstName);
    }    
}























