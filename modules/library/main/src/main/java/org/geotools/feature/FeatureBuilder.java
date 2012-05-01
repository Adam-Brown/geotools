package org.geotools.feature;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;

public abstract class FeatureBuilder<F extends Feature, FT extends FeatureType> {
	/** the feature type */
    protected FT featureType;

    /** the feature factory */
    protected FeatureFactory factory;

//	public abstract FT getFeatureType();

    public abstract F buildFeature(String id);

//	public abstract F buildFeature(String id, Object[] values); 

    public abstract void set(String name, Object value);
}
