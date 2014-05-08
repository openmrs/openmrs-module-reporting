package org.openmrs.module.reporting.data.visit.definition;

import org.openmrs.module.reporting.data.JoinDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;

/**
 * Adapter class for exposing a Patient Data Definition as an Visit Data Definition
 */
public class PatientToVisitDataDefinition extends JoinDataDefinition<PatientDataDefinition> implements VisitDataDefinition {

    /**
     * Default Constructor
     */
    public PatientToVisitDataDefinition() {
        super();
    }

    /**
     * Default Constructor
     */
    public PatientToVisitDataDefinition(PatientDataDefinition joinedDataDefinition) {
        super(joinedDataDefinition);
    }

    /**
     * Constructor to populate name
     */
    public PatientToVisitDataDefinition(String name, PatientDataDefinition joinedDataDefinition) {
        super(name, joinedDataDefinition);
    }


    @Override
    public Class<PatientDataDefinition> getJoinedDefinitionType() {
        return PatientDataDefinition.class;
    }
}
