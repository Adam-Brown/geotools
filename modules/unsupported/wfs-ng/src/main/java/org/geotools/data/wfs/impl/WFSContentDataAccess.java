package org.geotools.data.wfs.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.geotools.data.DataAccess;
import org.geotools.data.FeatureSource;
import org.geotools.data.ServiceInfo;
import org.geotools.data.complex.config.EmfAppSchemaReader;
import org.geotools.data.complex.config.FeatureTypeRegistry;
import org.geotools.data.store.ContentComplexFeatureSource;
import org.geotools.data.wfs.internal.DescribeFeatureTypeRequest;
import org.geotools.data.wfs.internal.WFSClient;
import org.geotools.feature.NameImpl;
import org.geotools.xml.AppSchemaCache;
import org.geotools.xml.AppSchemaResolver;
import org.geotools.xml.SchemaIndex;
import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;

public class WFSContentDataAccess implements DataAccess<FeatureType, Feature> {
    /**
     * The Web feature service client object.
     */
    private final WFSClient client;

    /**
     * The schema reader used to take describe feature URL and turn it into a schema index.
     */
    private EmfAppSchemaReader schemaParser;

    /**
     * Collection of feature type descriptors.
     */
    private FeatureTypeRegistry typeRegistry;

    private File cacheLocation;

    // TODO: Refactor - Copied from WFSContentDataStore
    private final Map<Name, QName> names;

    // END Refactor;

    // TODO: Refactor - Copied from ContentDataStore
    /**
     * namespace URL of the datastore itself, or default namespace
     */
    protected String namespaceURI;

    /**
     * The namespace URL of the datastore.
     * 
     * @return The namespace URL, may be <code>null</code>.
     */
    public String getNamespaceURI() {
        return namespaceURI;
    }

    /**
     * Sets the namespace URI of the datastore.
     * <p>
     * This will be used to qualify the entries or types of the datastore.
     * </p>
     * 
     * @param namespaceURI The namespace URI, may be <code>null</code>.
     */
    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

    // END Refactor;

    /**
     * The WFS capabilities document.
     * 
     * @param capabilities
     */
    public WFSContentDataAccess(final WFSClient client) {
        this.client = client;
        this.names = new ConcurrentHashMap<Name, QName>();
    }

    @Override
    public ServiceInfo getInfo() {
        // TODO: Refactor - Taken from WFSContentDataStore
        return this.client.getInfo();
    }

    @Override
    public void createSchema(FeatureType featureType) throws IOException {
        // TODO: Refactor - Copied from WFS_1_1_0_DataStore
        throw new UnsupportedOperationException("WFS DataStore does not support createSchema");
    }

    @Override
    public void updateSchema(Name typeName, FeatureType featureType) throws IOException {
        // TODO: Refactor - Copied from WFS_1_1_0_DataStore
        throw new UnsupportedOperationException("WFS does not support update schema");
    }

    @Override
    public List<Name> getNames() throws IOException {
        // the WFSContentDataStore version inherits an implementation of this
        // method from ContentDataStore,
        // that method calls getTypeNames which calls an abstract method (i.e.
        // one that's implemented in
        // WFSContentDataStore) called createTypeNames(). createTypeNames, as
        // implemented in WFSContentDataStore,
        // uses client to 'getRemoteTypeNames()'.

        // TODO: Refactor - Copied from ContentDataStore.
        Set<QName> remoteTypeNames = client.getRemoteTypeNames();
        List<Name> names = new ArrayList<Name>(remoteTypeNames.size());
        for (QName remoteTypeName : remoteTypeNames) {
            Name typeName = new NameImpl(remoteTypeName);
            names.add(typeName);
            this.names.put(typeName, remoteTypeName);
        }

        return names;
        // END Refactor;
    }

    @Override
    public FeatureType getSchema(Name name) throws IOException {

    	// If there are no values in this.names it probably means that getNames hasn't been called yet.
    	if (this.names.size() == 0) {
    		this.getNames();
    	}
    	    	
        // Generate the URL for the feature request:
        // -----------------------------------------
        DescribeFeatureTypeRequest describeFeatureTypeRequest = client
                .createDescribeFeatureTypeRequest();
        QName qname = this.names.get(name);
        describeFeatureTypeRequest.setTypeName(qname);
        URL describeRequestURL = describeFeatureTypeRequest.getFinalURL();

        // Create type registry and add the schema to it:
        // ----------------------------------------------
        FeatureTypeRegistry typeRegistry = this.getFeatureTypeRegistry();
        SchemaIndex schemaIndex = this.getSchemaParser().parse(describeRequestURL);
        typeRegistry.addSchemas(schemaIndex);

        // Create the attribute type and cast it as a FeatureType:
        // -------------------------------------------------------
        AttributeDescriptor attributeDescriptor = typeRegistry.getDescriptor(name, null); // TODO: do I need to set the CRS?
        return (FeatureType) attributeDescriptor.getType();
    }

    /**
     * Sets the location of the cache folder to be used by app-schema-resolver.
     * 
     * @param cacheLocation the folder to use as the cache.
     */
    public void setCacheLocation(File cacheLocation) {
        this.cacheLocation = cacheLocation;
    }

    @Override
    public FeatureSource<FeatureType, Feature> getFeatureSource(Name typeName) throws IOException {
    	// There is an implementation of this in ContentDataStore which gets inherited by WFSContentDataStore.
    	FeatureSource<FeatureType, Feature> contentComplexFeatureSource = new ContentComplexFeatureSource();
        return contentComplexFeatureSource;
    }

    @Override
    public void dispose() {
        this.schemaParser = null;
        this.typeRegistry = null;
    }

    /**
     * Get the schema parser, creating it first if necessary.
     * 
     * @return the schema parser. Guaranteed non-null.
     */
    private EmfAppSchemaReader getSchemaParser() {
        if (this.schemaParser == null) {
            this.schemaParser = EmfAppSchemaReader.newInstance();

            AppSchemaResolver appSchemaResolver;
            if (this.cacheLocation == null) {
                appSchemaResolver = new AppSchemaResolver();
            } else {
                appSchemaResolver = new AppSchemaResolver(
                	new AppSchemaCache(
                			this.cacheLocation, 
                			/* download: */ true, 
                			/* keepQuery: */ true));
            }

            this.schemaParser.setResolver(appSchemaResolver);
        }

        return this.schemaParser;
    }

    /**
     * Get the type registry, creating it first if necessary.
     * 
     * @return the type registry. Guaranteed non-null.
     */
    private FeatureTypeRegistry getFeatureTypeRegistry() {
        if (this.typeRegistry == null) {
            this.typeRegistry = new FeatureTypeRegistry();
        }

        return this.typeRegistry;
    }
}
