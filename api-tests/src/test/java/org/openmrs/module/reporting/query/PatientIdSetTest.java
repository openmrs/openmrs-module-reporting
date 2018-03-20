package org.openmrs.module.reporting.query;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.module.reporting.cohort.CohortUtil;
import org.openmrs.module.reporting.cohort.PatientIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.hamcrest.core.Is.is;

public class PatientIdSetTest extends BaseModuleContextSensitiveTest {


    @Test
    public void testPatientIdSetAsCohort() {

        Cohort patientIdSet = new PatientIdSet(4,5);

        Assert.assertThat(patientIdSet.size(), is(2));
        Assert.assertThat(patientIdSet.getSize(), is(2));
        Assert.assertTrue(patientIdSet.contains(4));
        Assert.assertTrue(patientIdSet.contains(5));
        Assert.assertTrue(patientIdSet.getMemberIds().contains(4));
        Assert.assertTrue(patientIdSet.getMemberIds().contains(5));
        Assert.assertFalse(patientIdSet.isEmpty());
        Assert.assertThat(patientIdSet.getCommaSeparatedPatientIds(), is("4,5"));  // TODO is this order dependent

        patientIdSet.addMember(6);
        Assert.assertThat(patientIdSet.getSize(), is(3));
        Assert.assertTrue(patientIdSet.contains(6));

        Cohort patientIdSet2 = new PatientIdSet(4,5);
        Cohort union = CohortUtil.union(patientIdSet, patientIdSet2);
        Assert.assertThat(union.getSize(), is(3));
        Assert.assertTrue(union.contains(4));
        Assert.assertTrue(union.contains(5));
        Assert.assertTrue(union.contains(6));

        Cohort intersect = CohortUtil.intersect(patientIdSet, patientIdSet2);
        Assert.assertThat(intersect.getSize(), is(1));
        Assert.assertTrue(intersect.contains(5));
        Assert.assertFalse(intersect.contains(4));
        Assert.assertFalse(intersect.contains(6));

        Cohort subtract = CohortUtil.subtract(patientIdSet, patientIdSet2);
        Assert.assertThat(subtract.getSize(), is(1));
        Assert.assertTrue(subtract.contains(6));

    }

}
