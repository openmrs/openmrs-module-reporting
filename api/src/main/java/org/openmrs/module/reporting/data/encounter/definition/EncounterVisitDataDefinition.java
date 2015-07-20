package org.openmrs.module.reporting.data.encounter.definition;

import org.openmrs.Visit;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.EncounterVisitDataDefinition")
public class EncounterVisitDataDefinition extends BaseDataDefinition implements EncounterDataDefinition {
    public static final long serialVersionUID = 1L;

    /**
     * Default Constructor
     */
    public EncounterVisitDataDefinition() {
        super();
    }

    /**
     * Constructor to populate name only
     */
    public EncounterVisitDataDefinition(String name) {
        super(name);
    }
    //***** INSTANCE METHODS *****

    /**
     * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
     */
    public Class<?> getDataType() {
        return Visit.class;
    }
}
