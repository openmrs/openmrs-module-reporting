package org.openmrs.module.reporting.query.obs.definition;

import org.openmrs.Obs;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.query.BaseQuery;

/**
 * Cohort Definition-based Encounter Query
 */
public class PatientObsQuery extends BaseQuery<Obs> implements ObsQuery {


    @ConfigurationProperty(required=true)
    private CohortDefinition patientQuery;

    //***** CONSTRUCTORS *****

    /**
     * Default Constructor
     */
    public PatientObsQuery() {
        super();
    }

    /**
     * Full Constructor
     */
    public PatientObsQuery(CohortDefinition patientQuery) {
        setPatientQuery(patientQuery);
    }

    //***** INSTANCE METHODS *****

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Obs Patient Query";
    }



    //***** PROPERTY ACCESS *****

    /**
     * @return the patientQuery
     */
    public CohortDefinition getPatientQuery() {
        return patientQuery;
    }

    /**
     * @param patientQuery the patientQuery to set
     */
    public void setPatientQuery(CohortDefinition patientQuery) {
        this.patientQuery = patientQuery;
        this.setParameters(patientQuery.getParameters());
    }

}
