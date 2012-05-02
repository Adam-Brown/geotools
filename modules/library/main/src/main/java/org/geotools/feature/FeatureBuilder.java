package org.geotools.feature;

import org.geotools.data.DataUtilities;
import org.geotools.util.Converters;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;

public abstract class FeatureBuilder<F extends Feature, FT extends FeatureType> {
	/** the feature type */
    protected FT featureType;

    /** the feature factory */
    protected FeatureFactory factory;

    public abstract F buildFeature(String id);

//	public abstract F buildFeature(String id, Object[] values); 

    public abstract void set(String name, Object value);
    
    protected FeatureBuilder(FT featureType, FeatureFactory factory)
    {
    	this.featureType = featureType;
        this.factory = factory;
    }
    
    /**
     * Returns the feature type used by this builder as a feature template
     * @return
     */
    public FT getFeatureType() {
        return featureType;
    }
    
    protected Object convert(Object value, AttributeDescriptor descriptor) {
        // make sure the type of the value and the binding of the type match up
        if ( value != null ) {
            Class<?> target = descriptor.getType().getBinding(); 
            Object converted = Converters.convert(value, target);
            if(converted != null)
                value = converted;
        } else {
            //if the content is null and the descriptor says isNillable is false, 
            // then set the default value
            if (!descriptor.isNillable()) {
                value = descriptor.getDefaultValue();
                if ( value == null ) {
                    //no default value, try to generate one
                    value = DataUtilities.defaultValue(descriptor.getType().getBinding());
                }
            }
        }
        return value;
    }
}
