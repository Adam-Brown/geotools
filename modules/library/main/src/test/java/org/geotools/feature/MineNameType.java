package org.geotools.feature;

import org.geotools.feature.wrapper.FeatureWrapper;
import org.geotools.feature.wrapper.XSDMapping;
import org.opengis.feature.ComplexAttribute;

@XSDMapping(namespace = "urn:cgi:xmlns:GGIC:EarthResource:1.1", separator = ":")
public class MineNameType extends FeatureWrapper {
	
	@XSDMapping(local = "isPreferred")
	public boolean isPreferred;
	
	@XSDMapping(local = "mineName")
	public String mineName;
	
	@Override
	public String toString() {
		return String.format("isPreferred: %s, mineName: %s", this.isPreferred, this.mineName);
	}
}
