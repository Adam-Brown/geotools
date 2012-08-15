package org.geotools.data.wfs.internal;

import java.io.IOException;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import com.vividsolutions.jts.geom.GeometryFactory;

public interface GetParser<F extends Feature> {
    /**
     * Returns the number of features if advertised by the server and the parser was able to get that information for example from the
     * {@code wfs:FeatureCollection} "numberOfFeatures" xml attribute, or {@code -1} if unknown.
     * 
     * @return number of features advertised by server, or {@code -1} if unknown
     */
    public int getNumberOfFeatures();

    /**
     * @return the next feature in the stream or {@code null} if there are no more features to parse.
     */
    F parse() throws IOException;

    void close() throws IOException;

    public FeatureType getFeatureType();

    public void setGeometryFactory(GeometryFactory geometryFactory);
}
