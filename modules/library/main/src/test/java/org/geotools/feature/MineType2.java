package org.geotools.feature;

import java.util.ArrayList;
import java.util.Collection;

import org.geotools.feature.wrapper.FeatureWrapper;
import org.geotools.feature.wrapper.XSDMapping;

// This is just like MineType but it's modified to cater for some tests. See FeatureWrapperTest.java.


// This demonstrates how to extend FeatureWrapper to get a strongly-typed object to represent a feature.
@XSDMapping(namespace = "urn:org:example", separator = ":")
public class MineType2 extends FeatureWrapper {
    @XSDMapping(local = "MineNamePropertyType")
    public ArrayList<MineNamePropertyType> MineNameProperties; // ArrayLists are allowed for multivalued types.
}