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

	public WFSContentComplexFeatureSource(Name typeName, WFSClient client,
			WFSContentDataAccess dataAccess) {
		super(null);
		this.typeName = typeName;
		this.client = client;
		this.dataAccess = dataAccess;
	}

	@Override
	public FeatureCollection<FeatureType, Feature> getFeatures(Filter filter)
			throws IOException {
		return getFeatures( new Query(this.typeName.toString(), filter ) );
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
		QName name = dataAccess.getRemoteTypeName(typeName);
		request.setTypeName(new QName(query.getTypeName()));

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
		XmlComplexFeatureParser parser = new XmlComplexFeatureParser(stream,
				schema, name);

		Queue<Feature> features = new LinkedList<Feature>();
		// Parse must be called once for each feature.
		Feature feature;
		while ((feature = parser.parse()) != null) {
			features.add(feature);
		}

		return new WFSContentComplexFeatureCollection(features);
	}
}
