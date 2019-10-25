/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.UserService;
import org.openmrs.api.VisitService;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.reporting.cohort.definition.VisitCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class VisitCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {;

    @Autowired
    LocationService locationService;

    @Autowired
    ConceptService conceptService;

    @Autowired
    UserService userService;

    @Autowired
    VisitService visitService;

    @Autowired
    CohortDefinitionService cohortDefinitionService;

    @Autowired
    TestDataManager data;

    VisitCohortDefinition cd;

    VisitType someVisitType;

    @Before
    public void setUp() throws Exception {
        cd = new VisitCohortDefinition();

        someVisitType = new VisitType();
        someVisitType.setName("Some visit type");
        visitService.saveVisitType(someVisitType);
    }

    @Test
    public void testEvaluateWithNoProperties() throws Exception {
        Cohort c = cohortDefinitionService.evaluate(cd, null);
        assertThat(c.size(), is(2));
    }

    @Test
    public void testEvaluateWithManyProperties() throws Exception {
        setManyProperties();

        Cohort c = cohortDefinitionService.evaluate(cd, null);
        assertThat(c.size(), is(1));
        assertThat(c.getMemberIds(), containsInAnyOrder(2));
    }

    @Test
    public void testEvaluateInverse() throws Exception {
        setManyProperties();
        cd.setReturnInverse(true);

        Cohort c = cohortDefinitionService.evaluate(cd, null);
        assertThat(c.size(), is(3));
        assertThat(c.getMemberIds(), not(containsInAnyOrder(2)));
    }

    @Test
    public void shouldIncludeVisit_ifActiveVisitRangeWithinVisit() throws Exception {

        Patient patient = data.randomPatient().birthdate("1975-05-27").save();
        // early dates to avoid active visits in standard test dataset
        Visit visit = data.visit()
                .started("1999-01-01")
                .stopped("1999-02-02")
                .visitType(someVisitType)
                .patient(patient)
                .save();

        cd.setActiveOnOrAfter(DateUtil.parseDate("1999-01-10", "yyyy-MM-dd"));
        cd.setActiveOnOrBefore(DateUtil.parseDate("1999-01-15", "yyyy-MM-dd"));

        Cohort c = cohortDefinitionService.evaluate(cd, null);
        assertThat(c.size(), is(1));
        assertThat(c.getMemberIds(), containsInAnyOrder(patient.getId()));

    }

    @Test
    public void shouldIncludeVisit_ifActiveVisitRangeStartBeforeVisitAndRangeEndDuringVisit() throws Exception {

        Patient patient = data.randomPatient().birthdate("1975-05-27").save();
        // early dates to avoid active visits in standard test dataset
        Visit visit = data.visit()
                .started("1999-01-01")
                .stopped("1999-02-02")
                .visitType(someVisitType)
                .patient(patient)
                .save();

        cd.setActiveOnOrAfter(DateUtil.parseDate("1998-12-10", "yyyy-MM-dd"));
        cd.setActiveOnOrBefore(DateUtil.parseDate("1999-01-15", "yyyy-MM-dd"));

        Cohort c = cohortDefinitionService.evaluate(cd, null);
        assertThat(c.size(), is(1));
        assertThat(c.getMemberIds(), containsInAnyOrder(patient.getId()));

    }

    @Test
    public void shouldIncludeVisit_ifActiveVisitRangeStartDuringVisitAndRangeEndAfterVisit() throws Exception {

        Patient patient = data.randomPatient().birthdate("1975-05-27").save();
        // early dates to avoid active visits in standard test dataset
        Visit visit = data.visit()
                .started("1999-01-01")
                .stopped("1999-02-02")
                .visitType(someVisitType)
                .patient(patient)
                .save();

        cd.setActiveOnOrAfter(DateUtil.parseDate("1999-01-10", "yyyy-MM-dd"));
        cd.setActiveOnOrBefore(DateUtil.parseDate("1999-02-15", "yyyy-MM-dd"));

        Cohort c = cohortDefinitionService.evaluate(cd, null);
        assertThat(c.size(), is(1));
        assertThat(c.getMemberIds(), containsInAnyOrder(patient.getId()));

    }

    @Test
    public void shouldIncludeVisit_ifActiveVisitRangeStartBeforeVisitAndRangeEndAfterVisit() throws Exception {

        Patient patient = data.randomPatient().birthdate("1975-05-27").save();
        // early dates to avoid active visits in standard test dataset
        Visit visit = data.visit()
                .started("1999-01-01")
                .stopped("1999-02-02")
                .visitType(someVisitType)
                .patient(patient)
                .save();

        cd.setActiveOnOrAfter(DateUtil.parseDate("1998-12-10", "yyyy-MM-dd"));
        cd.setActiveOnOrBefore(DateUtil.parseDate("1999-02-15", "yyyy-MM-dd"));

        Cohort c = cohortDefinitionService.evaluate(cd, null);
        assertThat(c.size(), is(1));
        assertThat(c.getMemberIds(), containsInAnyOrder(patient.getId()));

    }

    @Test
    public void shouldIncludeVisit_ifActiveVisitRangeEndSameAsVisitStart() throws Exception {

        Patient patient = data.randomPatient().birthdate("1975-05-27").save();
        // early dates to avoid active visits in standard test dataset
        Visit visit = data.visit()
                .started("1999-01-01")
                .stopped("1999-02-02")
                .visitType(someVisitType)
                .patient(patient)
                .save();

        cd.setActiveOnOrAfter(DateUtil.parseDate("1998-12-01", "yyyy-MM-dd"));
        cd.setActiveOnOrBefore(DateUtil.parseDate("1999-01-01", "yyyy-MM-dd"));

        Cohort c = cohortDefinitionService.evaluate(cd, null);
        assertThat(c.size(), is(1));
        assertThat(c.getMemberIds(), containsInAnyOrder(patient.getId()));

    }

    @Test
    public void shouldIncludeVisit_ifActiveVisitRangeStartSameAsVisitEnd() throws Exception {

        Patient patient = data.randomPatient().birthdate("1975-05-27").save();
        // early dates to avoid active visits in standard test dataset
        Visit visit = data.visit()
                .started("1999-01-01")
                .stopped("1999-02-02")
                .visitType(someVisitType)
                .patient(patient)
                .save();

        cd.setActiveOnOrAfter(DateUtil.parseDate("1999-02-02", "yyyy-MM-dd"));
        cd.setActiveOnOrBefore(DateUtil.parseDate("1999-03-01", "yyyy-MM-dd"));

        Cohort c = cohortDefinitionService.evaluate(cd, null);
        assertThat(c.size(), is(1));
        assertThat(c.getMemberIds(), containsInAnyOrder(patient.getId()));

    }


    @Test
    public void shouldNotIncludeVisit_ifActiveVisitRangeStartBeforeVisitAndRangeEndBeforeVisit() throws Exception {

        Patient patient = data.randomPatient().birthdate("1975-05-27").save();
        // early dates to avoid active visits in standard test dataset
        Visit visit = data.visit()
                .started("1999-01-01")
                .stopped("1999-02-02")
                .visitType(someVisitType)
                .patient(patient)
                .save();

        cd.setActiveOnOrAfter(DateUtil.parseDate("1998-12-10", "yyyy-MM-dd"));
        cd.setActiveOnOrBefore(DateUtil.parseDate("1998-12-15", "yyyy-MM-dd"));

        Cohort c = cohortDefinitionService.evaluate(cd, null);
        assertThat(c.size(), is(0));
    }

    @Test
    public void shouldIncludeVisit_ifActiveVisitRangeStartAfterVisitAndRangeEndAfterVisit() throws Exception {

        Patient patient = data.randomPatient().birthdate("1975-05-27").save();
        // early dates to avoid active visits in standard test dataset
        Visit visit = data.visit()
                .started("1999-01-01")
                .stopped("1999-02-02")
                .visitType(someVisitType)
                .patient(patient)
                .save();

        cd.setActiveOnOrAfter(DateUtil.parseDate("2000-12-10", "yyyy-MM-dd"));
        cd.setActiveOnOrBefore(DateUtil.parseDate("2000-12-15", "yyyy-MM-dd"));

        Cohort c = cohortDefinitionService.evaluate(cd, null);
        assertThat(c.size(), is(0));
    }

    @Test
    public void shouldIncludeVisit_ifActiveVisitRangeStartAfterVisitStartAndVisitCurrentlyActive() throws Exception {

        Patient patient = data.randomPatient().birthdate("1975-05-27").save();
        // early dates to avoid active visits in standard test dataset
        Visit visit = data.visit()
                .started("1999-01-01")
                .visitType(someVisitType)
                .patient(patient)
                .save();

        cd.setActiveOnOrAfter(DateUtil.parseDate("2000-12-10", "yyyy-MM-dd"));
        cd.setActiveOnOrBefore(DateUtil.parseDate("2000-12-15", "yyyy-MM-dd"));

        Cohort c = cohortDefinitionService.evaluate(cd, null);
        assertThat(c.size(), is(1));
        assertThat(c.getMemberIds(), containsInAnyOrder(patient.getId()));
    }

    @Test
    public void shouldNotIncludeVisit_ifActiveVisitRangeEndBeforeVisitStartAndVisitCurrentlyActive() throws Exception {

        Patient patient = data.randomPatient().birthdate("1975-05-27").save();
        // early dates to avoid active visits in standard test dataset
        Visit visit = data.visit()
                .started("1999-01-01")
                .visitType(someVisitType)
                .patient(patient)
                .save();

        cd.setActiveOnOrAfter(DateUtil.parseDate("1998-12-10", "yyyy-MM-dd"));
        cd.setActiveOnOrBefore(DateUtil.parseDate("1998-12-15", "yyyy-MM-dd"));

        Cohort c = cohortDefinitionService.evaluate(cd, null);
        assertThat(c.size(), is(0));
    }

    private void setManyProperties() {
        cd.setStartedOnOrAfter(DateUtil.parseDate("2005-01-01", "yyyy-MM-dd"));
        cd.setStartedOnOrBefore(DateUtil.parseDate("2005-01-01", "yyyy-MM-dd"));

        cd.setLocationList(asList(locationService.getLocation(1)));
        cd.setIndicationList(asList(conceptService.getConcept(5497)));

        cd.setCreatedBy(userService.getUser(1));
        cd.setCreatedOnOrAfter(DateUtil.parseDate("2005-01-01", "yyyy-MM-dd"));
        cd.setCreatedOnOrBefore(DateUtil.parseDate("2005-01-01", "yyyy-MM-dd"));
    }
}
