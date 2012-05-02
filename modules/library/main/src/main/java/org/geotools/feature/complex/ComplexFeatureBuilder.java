package org.geotools.feature.complex;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureBuilder;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;

public class ComplexFeatureBuilder extends FeatureBuilder<Feature, FeatureType> {

	public ComplexFeatureBuilder(FeatureType featureType) {
        this(featureType, CommonFactoryFinder.getFeatureFactory(null));
    }

	protected ComplexFeatureBuilder(FeatureType featureType, FeatureFactory factory) {
		super(featureType, factory);
	}

	@Override
	public Feature buildFeature(String id) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void set(String name, Object value) {
		throw new RuntimeException("Not implemented");
	}
}