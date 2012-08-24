package org.geotools.feature.collection;

import java.util.NoSuchElementException;
import java.util.Queue;

import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;

public class ComplexFeatureIteratorImpl implements FeatureIterator<Feature> {

	private Queue<Feature> features;

	public ComplexFeatureIteratorImpl(Queue<Feature> features) {
		this.features = features;
	}

	@Override
	public boolean hasNext() {
		return features.size() > 0;
	}

	@Override
	public Feature next() throws NoSuchElementException {
		return this.features.remove();
	}

	@Override
	public void close() {
	}
}
