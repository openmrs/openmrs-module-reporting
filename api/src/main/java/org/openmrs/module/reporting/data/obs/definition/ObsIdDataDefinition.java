package org.openmrs.module.reporting.data.obs.definition;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Gets Obs.obsId
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class ObsIdDataDefinition extends BaseDataDefinition implements ObsDataDefinition {

    public static final long serialVersionUID = 1L;

    @Override
    public Class<?> getDataType() {
        return Integer.class;
    }

}
