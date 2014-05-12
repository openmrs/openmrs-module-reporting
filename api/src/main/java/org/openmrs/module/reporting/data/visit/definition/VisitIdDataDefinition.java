package org.openmrs.module.reporting.data.visit.definition;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Visit ID Column
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class VisitIdDataDefinition extends BaseDataDefinition implements VisitDataDefinition {

    public static final long serialVersionUID = 1L;

    /**
     * Default Constructor
     */
    public VisitIdDataDefinition() {
        super();
    }

    /**
     * Constructor to populate name only
     */
    public VisitIdDataDefinition(String name) {
        super(name);
    }

    //***** INSTANCE METHODS *****

    /**
     * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
     */
    public Class<?> getDataType() {
        return Integer.class;
    }
}
