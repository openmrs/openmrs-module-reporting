package org.openmrs.module.reporting.data.obs.definition;

import org.openmrs.module.reporting.data.JoinDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;

public class EncounterToObsDataDefinition extends JoinDataDefinition<EncounterDataDefinition> implements ObsDataDefinition {


    /**
     * Default Constructor
     */
    public EncounterToObsDataDefinition() {
        super();
    }

    /**
     * Default Constructor
     */
    public EncounterToObsDataDefinition(EncounterDataDefinition joinedDataDefinition) {
        super(joinedDataDefinition);
    }

    /**
     * Constructor to populate name
     */
    public EncounterToObsDataDefinition(String name, EncounterDataDefinition joinedDataDefinition) {
        super(name, joinedDataDefinition);
    }

    /**
     * @see JoinDataDefinition#getJoinedDefinitionType()
     */
    @Override
    public Class<EncounterDataDefinition> getJoinedDefinitionType() {
        return EncounterDataDefinition.class;
    }

}
