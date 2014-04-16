package org.openmrs.module.reporting.data.encounter.evaluator;

import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EncountersForPatientDataEvaluatorTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private TestDataManager data;


    @Test
    public void shouldReturnEncountersInActiveVisit() throws Exception {

        VisitType visitType = new VisitType("Clinical", "Patient Visits the clinic");
        Context.getVisitService().saveVisitType(visitType);
        EncounterType encounterType = new EncounterType("Admission", "Patient admitted for inpatient care");
        Context.getEncounterService().saveEncounterType(encounterType);
        Location location = new Location();
        location.setName("MGH");
        location.setDescription("very good hospital");
        Context.getLocationService().saveLocation(location);

        EvaluationContext context = new EvaluationContext();
        Patient patient = data.randomPatient().save();
        // add an older closed visit
        Visit v1 = data.visit().patient(patient).visitType(visitType).started("2013-10-01 09:30:00").stopped("2013-10-03 09:30:00").location(location).save();
        Encounter e1 =  data.randomEncounter().encounterType(encounterType).visit(v1).patient(patient).encounterDatetime("2013-10-01 10:30:00").location(location).save();
        // add a new active visit
        Visit v2 = data.visit().patient(patient).visitType(visitType).started("2013-12-01 09:30:00").location(location).save();
        Encounter e2 = data.randomEncounter().encounterType(encounterType).visit(v2).patient(patient).encounterDatetime("2013-12-02 10:30:00").location(location).save();

        context.setBaseCohort(new Cohort(Arrays.asList(patient)));

        EncountersForPatientDataDefinition d = new EncountersForPatientDataDefinition();
        d.addType(encounterType);
        // get all patient encounters
        EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate((PatientDataDefinition) d, context);
        assertThat( ((List) pd.getData().get(patient.getId())).size(), is(2));

        // get encounters only in active visit
        d.setOnlyInActiveVisit(true);
        pd = Context.getService(PatientDataService.class).evaluate((PatientDataDefinition) d, context);
        assertThat( ((List) pd.getData().get(patient.getId())).size(), is(1));
        assertThat( (Encounter) ((List) pd.getData().get(patient.getId())).get(0), is(e2));
    }
}
