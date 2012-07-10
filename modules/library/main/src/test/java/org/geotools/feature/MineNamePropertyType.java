package org.geotools.feature;

import org.geotools.feature.wrapper.FeatureWrapper;
import org.geotools.feature.wrapper.XSDMapping;

@XSDMapping(namespace = "urn:cgi:xmlns:GGIC:EarthResource:1.1", separator = ":")
public class MineNamePropertyType extends FeatureWrapper {
	@XSDMapping(local = "MineName")
	public MineNameType MineName;
	
	@Override
	public String toString() {
		return String.format("MineName: %s", this.MineName);
	}
}
