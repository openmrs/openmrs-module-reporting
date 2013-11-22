package org.openmrs.module.reporting.query.obs.definition;

import org.openmrs.Obs;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.query.BaseQuery;

/**
 * ObsQuery that gets all obs (respecting the EvaluationContext's base cohort and base obs
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class AllObsQuery extends BaseQuery<Obs> implements ObsQuery {

    public static final long serialVersionUID = 1L;

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "All Obs Query";
    }

}
