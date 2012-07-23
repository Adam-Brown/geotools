package org.geotools.feature;

import java.util.ArrayList;
import java.util.Collection;

import org.geotools.feature.wrapper.FeatureWrapper;
import org.geotools.feature.wrapper.XSDMapping;

// This demonstrates how to extend FeatureWrapper to get a strongly-typed object to represent a feature.
@XSDMapping(namespace = "urn:org:example", separator = ":")
public class MineType extends FeatureWrapper {
	@XSDMapping(local = "MineNamePropertyType")
	public ArrayList<MineNamePropertyType> MineNameProperties; // ArrayLists are allowed for multivalued types.
	
	// You can use path to allow a lower-level value to be set in the current class. This might be useful if you don't want to have
	// to create the whole class tree.
	@XSDMapping(path = "MineNamePropertyType/MineName/MineNameType", local = "mineName")
	public String firstName;

	public String getPreferredName() { // You can add extra methods.
		for (MineNamePropertyType mineNameProperty : MineNameProperties) {
			if (mineNameProperty.MineName.isPreferred) {
				return mineNameProperty.MineName.mineName;
			}
		}

		return "";
	}
}