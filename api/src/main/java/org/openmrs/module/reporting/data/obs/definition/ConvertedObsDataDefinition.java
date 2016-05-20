package org.openmrs.module.reporting.data.obs.definition;

import org.openmrs.module.reporting.data.ConvertedDataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;

public class ConvertedObsDataDefinition extends ConvertedDataDefinition<ObsDataDefinition> implements ObsDataDefinition {

    /**
     * Default Constructor
     */
    public ConvertedObsDataDefinition() {
        super();
    }

    public ConvertedObsDataDefinition(String name, ObsDataDefinition definitionToConvert, DataConverter... converters) {
        super(name, definitionToConvert, converters);
    }

    public ConvertedObsDataDefinition(ObsDataDefinition definitionToConvert, DataConverter... converters) {
        super(null, definitionToConvert, converters);
    }

}
