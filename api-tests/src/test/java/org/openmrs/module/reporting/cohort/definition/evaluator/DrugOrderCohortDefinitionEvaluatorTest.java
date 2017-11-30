/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.reporting.cohort.definition.evaluator;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.CareSetting;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.DrugOrderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DrugOrderCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
  protected static final String TEST_DATA = "org/openmrs/module/reporting/include/ReportTestDataset-openmrs-1.10.xml";
  private DrugOrderCohortDefinition cohortDefinition;
  protected final Logger log = LoggerFactory.getLogger(DrugOrderCohortDefinitionEvaluatorTest.class);

  @Before
  public void setup() throws Exception {
    cohortDefinition = new DrugOrderCohortDefinition();
    executeDataSet(TEST_DATA);
  }

  @After
  public void tearDown() {
    cohortDefinition = null;
  }

  @Test
  public void evaluate_shouldReturnAllPatients() throws Exception {
    Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
    log.info( "\n Orders for Patient with ID 2"
        + Context.getService(OrderService.class).getAllOrdersByPatient(Context.getPatientService().getPatient(2)));
    log.info("\n Orders for Patient with ID 7"
        + Context.getService(OrderService.class).getAllOrdersByPatient(Context.getPatientService().getPatient(7)));
    log.info("\n Orders for Patient with ID 8"
        + Context.getService(OrderService.class).getAllOrdersByPatient(Context.getPatientService().getPatient(8)));
    log.info("\n Orders for Patient with ID 21"
        + Context.getService(OrderService.class).getAllOrdersByPatient(Context.getPatientService().getPatient(21)));
    log.info("\n Orders for Patient with ID 22"
        + Context.getService(OrderService.class).getAllOrdersByPatient(Context.getPatientService().getPatient(22)));
    Assert.assertTrue(cohort.contains(2));
    Assert.assertTrue(cohort.contains(7));
    Assert.assertTrue(cohort.contains(8));
    Assert.assertTrue(cohort.contains(21));
    Assert.assertTrue(cohort.contains(22));
    Assert.assertEquals(5, cohort.size());
  }
 
  @Test
  public void evaluate_shouldReturnAllPatientsCurrentlyActiveOnDrugs() throws Exception {
    cohortDefinition.setOnlyCurrentlyActive(true);
    Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
    Assert.assertTrue(cohort.contains(2));
    Assert.assertTrue(cohort.contains(21));
    Assert.assertTrue(cohort.contains(22));
    Assert.assertEquals(3, cohort.size());
  }

  @Test
  public void evaluate_shouldReturnAllPatientsCurrentlyNotActiveOnDrugs() throws Exception {
    cohortDefinition.setOnlyCurrentlyActive(false);
    Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
    Assert.assertTrue(cohort.contains(8));
    Assert.assertEquals(1, cohort.size());
  }

  @Test
  public void evaluate_shouldReturnAllPatientsThatHaveTakenAnyofListedDrugs() throws Exception {
    List drugSetList = new ArrayList<Concept>();
    drugSetList.add(new Concept(88));
    drugSetList.add(new Concept(792));
    cohortDefinition.setDrugSets(drugSetList);
    cohortDefinition.setWhich("ANY");
    Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
    Assert.assertTrue(cohort.contains(2));
    Assert.assertTrue(cohort.contains(7));
    Assert.assertEquals(2, cohort.size());

  }

  @Test
  public void evaluate_shouldReturnAllPatientsThatHaveNeverTakenListedDrugs() throws Exception {
    List drugSetList = new ArrayList<Concept>();
    drugSetList.add(new Concept(88));
    drugSetList.add(new Concept(792));
    cohortDefinition.setDrugSets(drugSetList);
    cohortDefinition.setWhich("NONE");
    Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
    Assert.assertTrue(cohort.contains(8));
    Assert.assertTrue(cohort.contains(21));
    Assert.assertTrue(cohort.contains(22));
    Assert.assertEquals(3, cohort.size());
  }

  @Test
  public void evaluate_shouldReturnAllPatientsThatHaveTakenAllListedDrugs() throws Exception {
    List drugSetList = new ArrayList<Concept>();
    drugSetList.add(new Concept(88));
    drugSetList.add(new Concept(792));
    cohortDefinition.setDrugSets(drugSetList);
    cohortDefinition.setWhich("ALL");
    Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
    Assert.assertTrue(cohort.contains(2));
    Assert.assertEquals(1, cohort.size());
  }

  @Test
  public void evaluate_shouldReturnAllPatientsTakingDrugsBeforeSpecifiedDate() throws Exception {
    cohortDefinition.setActivatedOnOrBefore(DateUtil.getDateTime(2008, 8, 2));
    Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
    Assert.assertTrue(cohort.contains(2));
    Assert.assertTrue(cohort.contains(22));
    Assert.assertEquals(2, cohort.size());
  }

  @Test
  public void evaluate_shouldReturnAllPatientsTakingDrugsAfterSpecifiedDate() throws Exception {
    cohortDefinition.setActivatedOnOrAfter(DateUtil.getDateTime(2008, 8, 10));
    Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
    Assert.assertTrue(cohort.contains(2));
    Assert.assertTrue(cohort.contains(7));
    Assert.assertEquals(2, cohort.size());
  }

  @Test
  public void evaluate_shouldReturnAllPatientsTakingAnyDrugWithinADateRange() throws Exception {
    cohortDefinition.setActivatedOnOrAfter(DateUtil.getDateTime(2008, 8, 1));
    cohortDefinition.setActivatedOnOrBefore(DateUtil.getDateTime(2008, 8, 8));
    Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
    Assert.assertTrue(cohort.contains(2));
    Assert.assertTrue(cohort.contains(7));
    Assert.assertTrue(cohort.contains(8));
    Assert.assertTrue(cohort.contains(21));
    Assert.assertTrue(cohort.contains(22));
    Assert.assertEquals(5, cohort.size());
  }

  @Test
  public void evaluate_shouldReturnAllPatientsTakingSpecifiedDrugBeforeDate() throws Exception {
    List drugSetList = new ArrayList<Concept>();
    drugSetList.add(new Concept(88));
    cohortDefinition.setDrugSets(drugSetList);
    cohortDefinition.setActivatedOnOrBefore(DateUtil.getDateTime(2008, 8, 2));
    Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
    Assert.assertTrue(cohort.contains(2));
  }

  @Test
  public void evaluate_shouldReturnAllInSpecifiedCareSetting() throws Exception {
    CareSetting careSetting = Context.getService(OrderService.class).getCareSetting(1);
    cohortDefinition.setCareSetting(careSetting);
    Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, null);
    Assert.assertTrue(cohort.contains(2));
    Assert.assertTrue(cohort.contains(7));
    Assert.assertTrue(cohort.contains(8));
    Assert.assertTrue(cohort.contains(21));
    Assert.assertTrue(cohort.contains(22));
    Assert.assertEquals(5, cohort.size());

  }
}
