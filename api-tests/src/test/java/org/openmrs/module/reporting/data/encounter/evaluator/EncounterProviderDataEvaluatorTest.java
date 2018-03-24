/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.encounter.evaluator;

import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ProviderService;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterProviderDataDefinition;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class EncounterProviderDataEvaluatorTest extends BaseModuleContextSensitiveTest {
    
    
    @Autowired
    private TestDataManager data;
    
    @Autowired
    @Qualifier("encounterService")
    private EncounterService encounterService;
    
    @Autowired
    @Qualifier("providerService")
    private ProviderService providerService;
    
    @Autowired
    private EncounterDataService encounterDataService;

    @Test
    public void shouldReturnEncounterProviderForEncounter() throws Exception {
   
        EncounterRole role = encounterService.getEncounterRole(1);
        Provider provider = saveRandomProvider();

        Patient patient = data.randomPatient().save();
        Encounter enc = data.randomEncounter().patient(patient)
                .encounterDatetime(new Date())
                .provider(role, provider)
                .save();

        EncounterProviderDataDefinition d = new EncounterProviderDataDefinition();
        d.setEncounterRole(role);
        
        EncounterEvaluationContext encounterEvaluationContext = new EncounterEvaluationContext();
        encounterEvaluationContext.setBaseEncounters(new EncounterIdSet(enc.getId()));

        EvaluatedEncounterData ed = encounterDataService.evaluate(d, encounterEvaluationContext);
        
        assertThat(ed.getData().size(), is(1));
        assertThat((Provider) ed.getData().get(enc.getId()), is(provider));
        
    }

    @Test
    public void shouldReturnEncounterProvidersForEncounter() throws Exception {

        EncounterRole role1 = encounterService.getEncounterRole(1);
        EncounterRole role2 = new EncounterRole();
        role2.setName("some role");
        encounterService.saveEncounterRole(role2);

        Provider provider1 = saveRandomProvider();
        Provider provider2 = saveRandomProvider();
        Provider provider3 = saveRandomProvider();

        Patient patient = data.randomPatient().save();
        Encounter enc = data.randomEncounter().patient(patient)
                .encounterDatetime(new Date())
                .provider(role1, provider1)
                .provider(role2, provider2)
                .provider(role1, provider3)
                .save();

        EncounterProviderDataDefinition d = new EncounterProviderDataDefinition();
        d.setEncounterRole(role1);
        d.setSingleProvider(false);

        EncounterEvaluationContext encounterEvaluationContext = new EncounterEvaluationContext();
        encounterEvaluationContext.setBaseEncounters(new EncounterIdSet(enc.getId()));

        EvaluatedEncounterData ed = encounterDataService.evaluate(d, encounterEvaluationContext);

        assertThat(ed.getData().size(), is(1));
        assertThat((List<Provider>) ed.getData().get(enc.getId()), 
                containsInAnyOrder(provider1, provider3));

    }

    @Test
    public void shouldReturnAllEncounterProvidersForEncounterIfNoRoleSpecified() throws Exception {

        EncounterRole role1 = encounterService.getEncounterRole(1);
        EncounterRole role2 = new EncounterRole();
        role2.setName("some role");
        encounterService.saveEncounterRole(role2);

        Provider provider1 = saveRandomProvider();
        Provider provider2 = saveRandomProvider();
        Provider provider3 = saveRandomProvider();

        Patient patient = data.randomPatient().save();
        Encounter enc = data.randomEncounter().patient(patient)
                .encounterDatetime(new Date())
                .provider(role1, provider1)
                .provider(role2, provider2)
                .provider(role1, provider3)
                .save();

        EncounterProviderDataDefinition d = new EncounterProviderDataDefinition();
        d.setSingleProvider(false);

        EncounterEvaluationContext encounterEvaluationContext = new EncounterEvaluationContext();
        encounterEvaluationContext.setBaseEncounters(new EncounterIdSet(enc.getId()));

        EvaluatedEncounterData ed = encounterDataService.evaluate(d, encounterEvaluationContext);

        assertThat(ed.getData().size(), is(1));
        assertThat((List<Provider>) ed.getData().get(enc.getId()),
                containsInAnyOrder(provider1, provider2, provider3));

    }


    @Test
    public void shouldIgnoredVoidedEncounterProviders() throws Exception {

        EncounterRole role1 = encounterService.getEncounterRole(1);
        EncounterRole role2 = new EncounterRole();
        role2.setName("some role");
        encounterService.saveEncounterRole(role2);

        Provider provider1 = saveRandomProvider();
        Provider provider2 = saveRandomProvider();
        Provider provider3 = saveRandomProvider();

        Patient patient = data.randomPatient().save();
        Encounter enc = data.randomEncounter().patient(patient)
                .encounterDatetime(new Date())
                .provider(role1, provider1)
                .provider(role2, provider2)
                .provider(role1, provider3)
                .save();

        // void a provider
        enc.removeProvider(role1, provider3);
        encounterService.saveEncounter(enc);

        EncounterProviderDataDefinition d = new EncounterProviderDataDefinition();
        d.setEncounterRole(role1);
        d.setSingleProvider(false);

        EncounterEvaluationContext encounterEvaluationContext = new EncounterEvaluationContext();
        encounterEvaluationContext.setBaseEncounters(new EncounterIdSet(enc.getId()));

        EvaluatedEncounterData ed = encounterDataService.evaluate(d, encounterEvaluationContext);

        assertThat(ed.getData().size(), is(1));
        assertThat((List<Provider>) ed.getData().get(enc.getId()),
                containsInAnyOrder(provider1));

    }

    @Test(expected = EvaluationException.class)
    public void shouldFailIfEncounterRoleParameterSetToAnotherType() throws Exception {

        EncounterProviderDataDefinition d = new EncounterProviderDataDefinition();
        d.setEncounterRole(new User());
        d.setSingleProvider(false);

        EncounterEvaluationContext encounterEvaluationContext = new EncounterEvaluationContext();
        EvaluatedEncounterData ed = encounterDataService.evaluate(d, encounterEvaluationContext);

    }

    @Test
    public void shouldReturnEncounterProviderForEncounterWhenInPatientContext() throws Exception {

        EncounterRole role = encounterService.getEncounterRole(1);
        Provider provider = saveRandomProvider();

        Patient patient = data.randomPatient().save();
        Encounter enc = data.randomEncounter().patient(patient)
                .encounterDatetime(new Date())
                .provider(role, provider)
                .save();

        EncounterProviderDataDefinition d = new EncounterProviderDataDefinition();
        d.setEncounterRole(role);

        EvaluationContext context = new EvaluationContext();
        context.setBaseCohort(new Cohort(patient.getId().toString()));

        EvaluatedEncounterData ed = encounterDataService.evaluate(d, context);

        assertThat(ed.getData().size(), is(1));
        assertThat((Provider) ed.getData().get(enc.getId()), is(provider));

    }

    @Test
    public void shouldReturnEmptySetIfInputSetEmpty() throws Exception {

        EncounterRole role = encounterService.getEncounterRole(1);

        EncounterProviderDataDefinition d = new EncounterProviderDataDefinition();
        d.setEncounterRole(role);

        EvaluationContext context = new EvaluationContext();
        context.setBaseCohort(new Cohort());

        EvaluatedEncounterData ed = encounterDataService.evaluate(d, context);

        assertThat(ed.getData().size(), is(0));
    }

    private Provider saveRandomProvider() {
        Patient p = data.randomPatient().save();
        return data.randomProvider().person(p).save();
    }

}
