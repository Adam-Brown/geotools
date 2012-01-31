/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.data.wfs.internal.v1_1;

import java.util.Set;

import org.geotools.data.wfs.internal.WFSOperationType;
import org.geotools.filter.v1_0.OGCConfiguration;
import org.geotools.xml.Configuration;

/**
 * WFS strategy for GeoServer WFS 1.1 where the geoserver version is lower than 2.0.
 * 
 * <p>
 * <ul>
 * <li>State {@code text/gml; subtype=gml/3.1.1} instead of {@code text/xml; subtype=gml/3.1.1} as
 * default output format.
 * <li>Can only reliably parse Filter 1.0
 * </p>
 * 
 * @source $URL$
 */
public class GeoServerPre200Strategy extends StrictWFS_1_1_Strategy {

    private static Configuration filter_1_0_0_Configuration = new OGCConfiguration();

    private static final String GEOSERVER_WRONG_FORMAT_NAME = "text/gml; subtype=gml/3.1.1";

    /**
     * GeoServer versions prior to 2.0 state {@code text/gml; subtype=gml/3.1.1} instead of
     * {@code text/xml; subtype=gml/3.1.1}
     */
    @Override
    public String getDefaultOutputFormat(WFSOperationType op) {
        try {
            return super.getDefaultOutputFormat(op);
        } catch (IllegalArgumentException e) {
            Set<String> supportedOutputFormats = getSupportedGetFeatureOutputFormats();
            if (supportedOutputFormats.contains(GEOSERVER_WRONG_FORMAT_NAME)) {
                return StrictWFS_1_1_Strategy.DEFAULT_OUTPUT_FORMAT;
            }
            throw new IllegalArgumentException("Server does not support '" + DEFAULT_OUTPUT_FORMAT
                    + "' output format: " + supportedOutputFormats);
        }
    }

    /**
     * GeoServer versions lower than 2.0 can only parse Filter 1.0
     */
    @Override
    protected Configuration getFilterConfiguration() {
        return filter_1_0_0_Configuration;
    }

}