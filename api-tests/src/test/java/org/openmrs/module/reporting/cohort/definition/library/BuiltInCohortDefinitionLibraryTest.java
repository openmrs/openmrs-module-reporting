/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort.definition.library;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.EncounterType;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.BirthAndDeathCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.MappedParametersCohortDefinition;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openmrs.module.reporting.common.ReportingMatchers.hasParameter;

/**
 *
 */
public class BuiltInCohortDefinitionLibraryTest {

    private BuiltInCohortDefinitionLibrary library;

    @Before
    public void setUp() throws Exception {
        library = new BuiltInCohortDefinitionLibrary();
    }

    @Test
    public void testGetMales() throws Exception {
        GenderCohortDefinition males = library.getMales();
        assertTrue(GenderCohortDefinition.class.isAssignableFrom(males.getClass()));
        assertThat(males.getParameters().size(), is(0));
        assertThat(males.getMaleIncluded(), is(true));
        assertThat(males.getFemaleIncluded(), is(false));
        assertThat(males.getUnknownGenderIncluded(), is(false));
    }

    @Test
    public void testGetFemales() throws Exception {
        GenderCohortDefinition females = library.getFemales();
        assertTrue(GenderCohortDefinition.class.isAssignableFrom(females.getClass()));
        assertThat(females.getParameters().size(), is(0));
        assertThat(females.getMaleIncluded(), is(false));
        assertThat(females.getFemaleIncluded(), is(true));
        assertThat(females.getUnknownGenderIncluded(), is(false));
    }

    @Test
    public void testGetUnknownGender() throws Exception {
        GenderCohortDefinition unknownGender = library.getUnknownGender();
        assertTrue(GenderCohortDefinition.class.isAssignableFrom(unknownGender.getClass()));
        assertThat(unknownGender.getParameters().size(), is(0));
        assertThat(unknownGender.getMaleIncluded(), is(false));
        assertThat(unknownGender.getFemaleIncluded(), is(false));
        assertThat(unknownGender.getUnknownGenderIncluded(), is(true));
    }

    @Test
    public void testGetUpToAgeOnDate() throws Exception {
        AgeCohortDefinition upToAgeOnDate = library.getUpToAgeOnDate();
        assertTrue(AgeCohortDefinition.class.isAssignableFrom(upToAgeOnDate.getClass()));
        assertThat(upToAgeOnDate, hasParameter("effectiveDate", Date.class));
        assertThat(upToAgeOnDate, hasParameter("maxAge", Integer.class));
        assertThat(upToAgeOnDate, hasProperty("maxAgeUnit", is(DurationUnit.YEARS)));
    }

    @Test
    public void testGetAtLeastAgeOnDate() throws Exception {
        AgeCohortDefinition atLeastAgeOnDate = library.getAtLeastAgeOnDate();
        assertTrue(AgeCohortDefinition.class.isAssignableFrom(atLeastAgeOnDate.getClass()));
        assertThat(atLeastAgeOnDate, hasParameter("effectiveDate", Date.class));
        assertThat(atLeastAgeOnDate, hasParameter("minAge", Integer.class));
        assertThat(atLeastAgeOnDate, hasProperty("minAgeUnit", is(DurationUnit.YEARS)));
    }
    
    @Test
    public void testGetAgeInRangeOnDate() throws Exception {
        AgeCohortDefinition ageInRangeOnDate = library.getAgeInRangeOnDate();
        assertThat(ageInRangeOnDate, hasParameter("effectiveDate", Date.class));
        assertThat(ageInRangeOnDate, hasParameter("minAge", Integer.class));
        assertThat(ageInRangeOnDate, hasProperty("minAgeUnit", is(DurationUnit.YEARS)));
        assertThat(ageInRangeOnDate, hasParameter("maxAge", Integer.class));
        assertThat(ageInRangeOnDate, hasProperty("maxAgeUnit", is(DurationUnit.YEARS)));
    }
    
    @Test
    public void testGetAnyEncounterDuringPeriod() throws Exception {
        CohortDefinition cd = library.getAnyEncounterDuringPeriod();
        assertThat(cd, hasParameter("startDate", Date.class));
        assertThat(cd, hasParameter("endDate", Date.class));
        assertTrue(cd instanceof MappedParametersCohortDefinition);
        Mapped<CohortDefinition> wrapped = ((MappedParametersCohortDefinition) cd).getWrapped();
        assertTrue(wrapped.getParameterizable() instanceof EncounterCohortDefinition);
        assertThat((String) wrapped.getParameterMappings().get("onOrAfter"), is("${startDate}"));
        assertThat((String) wrapped.getParameterMappings().get("onOrBefore"), is("${endDate}"));
    }

    @Test
    public void testGetAnyEncounterOfTypesDuringPeriod() throws Exception {
        CohortDefinition cd = library.getAnyEncounterOfTypesDuringPeriod();
        assertThat(cd, hasParameter("startDate", Date.class));
        assertThat(cd, hasParameter("endDate", Date.class));
        assertThat(cd, hasParameter("encounterTypes", EncounterType.class, List.class));
        assertTrue(cd instanceof MappedParametersCohortDefinition);
        Mapped<CohortDefinition> wrapped = ((MappedParametersCohortDefinition) cd).getWrapped();
        assertTrue(wrapped.getParameterizable() instanceof EncounterCohortDefinition);
        assertThat((String) wrapped.getParameterMappings().get("onOrAfter"), is("${startDate}"));
        assertThat((String) wrapped.getParameterMappings().get("onOrBefore"), is("${endDate}"));
        assertThat((String) wrapped.getParameterMappings().get("encounterTypeList"), is("${encounterTypes}"));
    }
    
    @Test
    public void testGetBornDuringPeriod() throws Exception {
        CohortDefinition cd = library.getBornDuringPeriod();
        assertTrue(cd instanceof MappedParametersCohortDefinition);
        assertTrue(((MappedParametersCohortDefinition) cd).getWrapped().getParameterizable() instanceof BirthAndDeathCohortDefinition);
        assertThat(cd, hasParameter("startDate", Date.class));
        assertThat(cd, hasParameter("endDate", Date.class));
    }
    
    @Test
    public void testGetDiedDuringPeriod() throws Exception {
        CohortDefinition cd = library.getDiedDuringPeriod();
        assertTrue(cd instanceof MappedParametersCohortDefinition);
        assertTrue(((MappedParametersCohortDefinition) cd).getWrapped().getParameterizable() instanceof BirthAndDeathCohortDefinition);
        assertThat(cd, hasParameter("startDate", Date.class));
        assertThat(cd, hasParameter("endDate", Date.class));
    }
}
