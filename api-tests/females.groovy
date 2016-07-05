import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;

class FemalesCohortDefinition extends GenderCohortDefinition {

    public FemalesCohortDefinition() {
        setFemaleIncluded(true);
    }

}