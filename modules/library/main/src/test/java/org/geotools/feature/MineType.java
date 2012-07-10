package org.geotools.feature;

import java.util.ArrayList;
import java.util.Collection;

import org.geotools.feature.wrapper.FeatureWrapper;
import org.geotools.feature.wrapper.XSDMapping;

// This demonstrates how to extend FeatureWrapper to get a strongly-typed object to represent a feature.
@XSDMapping(namespace = "urn:cgi:xmlns:GGIC:EarthResource:1.1", separator = ":")
public class MineType extends FeatureWrapper {
	@XSDMapping(local = "MineNamePropertyType", collection = true)
	public ArrayList<MineNamePropertyType> MineNameProperties;
	
	public String getPreferredName() { 
		for (MineNamePropertyType mineNameProperty : MineNameProperties) {
			
			System.out.println(mineNameProperty);
			
			
			
			if (mineNameProperty.MineName.isPreferred) {
				return mineNameProperty.MineName.mineName;
			}
		}
		
		return "";
	}
}