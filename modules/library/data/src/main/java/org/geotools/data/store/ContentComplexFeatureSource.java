package org.geotools.data.store;

import java.awt.RenderingHints.Key;
import java.io.IOException;
import java.util.Set;

import org.geotools.data.DataAccess;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureListener;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.data.QueryCapabilities;
import org.geotools.data.ResourceInfo;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;

public abstract class ContentComplexFeatureSource implements FeatureSource<FeatureType, Feature>{

	/**
     * The query defining the feature source
     */
    protected Query query;
    
    
    protected ContentComplexFeatureSource(Query query) {
    	this.query = query;
    }
    
  	@Override
	public Name getName() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public ResourceInfo getInfo() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public DataAccess<FeatureType, Feature> getDataStore() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public QueryCapabilities getQueryCapabilities() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void addFeatureListener(FeatureListener listener) {
		throw new RuntimeException("Not implemented");	
	}

	@Override
	public void removeFeatureListener(FeatureListener listener) {
		throw new RuntimeException("Not implemented");	
	}
	
	/**
     * Convenience method for joining a query with the defining query of the 
     * feature source.
     */
    protected Query joinQuery( Query query ) {
        // join the queries
        return DataUtilities.mixQueries(this.query, query, null);
    }

	@Override
	public FeatureType getSchema() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public ReferencedEnvelope getBounds() throws IOException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public ReferencedEnvelope getBounds(Query query) throws IOException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int getCount(Query query) throws IOException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Set<Key> getSupportedHints() {
		throw new RuntimeException("Not implemented");
	}
}
