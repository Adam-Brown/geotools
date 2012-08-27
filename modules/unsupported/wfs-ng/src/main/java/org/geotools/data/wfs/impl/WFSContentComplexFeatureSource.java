package org.geotools.data.wfs.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

import javax.xml.namespace.QName;

import org.geotools.data.Query;
import org.geotools.data.store.ContentComplexFeatureSource;
import org.geotools.data.store.WFSContentComplexFeatureCollection;
import org.geotools.data.wfs.internal.GetFeatureRequest;
import org.geotools.data.wfs.internal.WFSClient;
import org.geotools.data.wfs.internal.parsers.XmlComplexFeatureParser;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class WFSContentComplexFeatureSource extends ContentComplexFeatureSource {
	
	private Name typeName;
	private WFSClient client;
	private WFSContentDataAccess dataAccess;
	
	protected WFSContentComplexFeatureSource(Query query) {
		super(query);
	}
	
	public WFSContentComplexFeatureSource(Name typeName, WFSClient client, WFSContentDataAccess dataAccess) {
		super(null);
		this.typeName = typeName;
		this.client = client;
		this.dataAccess = dataAccess;
	}
	
	@Override
	public FeatureCollection<FeatureType, Feature> getFeatures(Filter filter)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FeatureCollection<FeatureType, Feature> getFeatures()
			throws IOException {
		return getFeatures(joinQuery(Query.ALL));		
	}
	
	@Override
	public FeatureCollection<FeatureType, Feature> getFeatures(Query query)
			throws IOException {
		this.query = joinQuery(query);

		GetFeatureRequest request = client.createGetFeatureRequest();
		FeatureType schema = dataAccess.getSchema(typeName);
		QName name = dataAccess.getRemoteTypeName(typeName); // new QName("urn:cgi:xmlns:CGI:GeoSciML:2.0", "Borehole", ":");
		request.setTypeName(new QName(query.getTypeName())); // "gsml:Borehole"

		request.setFullType(schema);
		request.setFilter(query.getFilter());
		request.setPropertyNames(query.getPropertyNames());
        request.setSortBy(query.getSortBy());
        
        String srsName = null;
	  	CoordinateReferenceSystem crs = query.getCoordinateSystem();
	  	if (null != crs) {
	  		System.err.println("Warning: don't forget to set the query CRS");
	  	}

	  	request.setSrsName(srsName);

		InputStream stream = request.getFinalURL().openStream();

		// Step 6 - parse it.
		XmlComplexFeatureParser parser = new XmlComplexFeatureParser(stream, schema, name);

		Queue<Feature> features = new LinkedList<Feature>();
		// Parse must be called once for each feature.
		Feature feature;
		while ((feature = parser.parse()) != null) {
			features.add(feature);
		}

		return new WFSContentComplexFeatureCollection(features);
	}
}



/*private final WFSClient client;
	private final Name typeName;

	public WFSContentComplexFeatureSource(
			final Name typeName,
			final WFSClient client) {
		this.client = client;
		this.typeName = typeName;
	}
	
	@Override
	public FeatureCollection<FeatureType, Feature> getFeatures(Filter filter)
			throws IOException {
		return getFeatures(new Query(getSchema().getName().getLocalPart(), filter));
	}

	@Override
	public FeatureCollection<FeatureType, Feature> getFeatures(Query query)
			throws IOException {
		query = joinQuery(query);
		
		this.client

		// TODO: Make the calls here, look how "WFSContentFeatureSource.getReaderInternal(...)" creates 
		// a GetFeatureRequest
		XmlComplexFeatureParser parser = new XmlComplexFeatureParser(stream, schema, name);

		System.out.println(String.format("There are %d features.", parser.getNumberOfFeatures()));

		// Parse must be called once for each feature.
        Feature feature;
        while ((feature = parser.parse()) != null) {
        	System.out.println(feature);
        }

		throw new RuntimeException("Not implemented");
	}

	@Override
	public FeatureCollection<FeatureType, Feature> getFeatures()
			throws IOException {
		Query query = joinQuery(Query.ALL);
        return getFeatures(query );
	}
	
	private GetFeatureRequest createGetFeature(Query query, ResultType resultType)
		throws IOException {
		GetFeatureRequest request = client.createGetFeatureRequest();

        final WFSContentDataAccess dataStore = getDataStore();

        final QName remoteTypeName = dataStore.getRemoteTypeName(this.typeName);
        final SimpleFeatureType remoteSimpleFeatureType;
        remoteSimpleFeatureType = dataStore.getRemoteSimpleFeatureType(remoteTypeName);

        request.setTypeName(remoteTypeName);
        request.setFullType(remoteSimpleFeatureType);

        request.setFilter(query.getFilter());
        request.setResultType(resultType);
        int maxFeatures = query.getMaxFeatures();
        if (Integer.MAX_VALUE > maxFeatures) {
            request.setMaxFeatures(maxFeatures);
        }
        // let the request decide request.setOutputFormat(outputFormat);
        request.setPropertyNames(query.getPropertyNames());
        request.setSortBy(query.getSortBy());

        String srsName = null;
        CoordinateReferenceSystem crs = query.getCoordinateSystem();
        if (null != crs) {
            System.err.println("TODO: don't forget to set the query CRS");
        }
        
        request.setSrsName(srsName);
        return request;
	}*/







