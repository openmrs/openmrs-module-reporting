package org.openmrs.module.reporting.data.obs.definition;

import org.openmrs.module.reporting.data.JoinDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;

public class PatientToObsDataDefinition extends JoinDataDefinition<PatientDataDefinition> implements ObsDataDefinition {


    /**
     * Default Constructor
     */
    public PatientToObsDataDefinition() {
        super();
    }

    /**
     * Default Constructor
     */
    public PatientToObsDataDefinition(PatientDataDefinition joinedDataDefinition) {
        super(joinedDataDefinition);
    }

    /**
     * Constructor to populate name
     */
    public PatientToObsDataDefinition(String name, PatientDataDefinition joinedDataDefinition) {
        super(name, joinedDataDefinition);
    }

    /**
     * @see JoinDataDefinition#getJoinedDefinitionType()
     */
    @Override
    public Class<PatientDataDefinition> getJoinedDefinitionType() {
        return PatientDataDefinition.class;
    }

}

