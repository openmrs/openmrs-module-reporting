package org.openmrs.module.reporting.data.patient.definition;

import org.openmrs.Drug;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.module.reporting.common.DrugOrderSet;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Created by karol on 28/03/14.
 */
public interface DrugOrdersForPatientDataDefinition extends PatientDataDefinition {

    public void addDrugToInclude(Drug drug);

    public void addDrugConceptSetToInclude(Concept concept);

    public void addDrugConceptToInclude(Concept concept);

    public void setActiveOnDate(Date activeOnDate);

    public void setStartedOnOrBefore(Date startedOnOrBefore);

    public void setStartedOnOrAfter(Date startedOnOrAfter);

    public Date getCompletedOnOrBefore();

    public void setCompletedOnOrBefore(Date completedOnOrBefore);

    public Date getCompletedOnOrAfter() ;

    public void setCompletedOnOrAfter(Date completedOnOrAfter);

    public List<Drug> getDrugsToInclude();

    public List<Concept> getDrugConceptsToInclude();

    public Date getActiveOnDate();

    public Date getStartedOnOrAfter();

    public Date getStartedOnOrBefore();

    public List<Concept> getDrugConceptSetsToInclude();

}
