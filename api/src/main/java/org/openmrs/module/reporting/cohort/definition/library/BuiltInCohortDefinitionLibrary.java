/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.reporting.cohort.definition.library;

import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Form;
import org.openmrs.PersonAttributeType;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.BirthAndDeathCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PersonAttributeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.MappedParametersCohortDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Basic set of cohort definitions
 */
@Component
public class BuiltInCohortDefinitionLibrary extends BaseDefinitionLibrary<CohortDefinition> {

    public static final String PREFIX = "reporting.library.cohortDefinition.builtIn.";

    @Override
    public Class<? super CohortDefinition> getDefinitionType() {
        return CohortDefinition.class;
    }

    @Override
    public String getKeyPrefix() {
        return PREFIX;
    }

    @DocumentedDefinition(value = "males")
    public GenderCohortDefinition getMales() {
        GenderCohortDefinition males = new GenderCohortDefinition();
        males.setMaleIncluded(true);
        return males;
    }

    @DocumentedDefinition(value = "females")
    public GenderCohortDefinition getFemales() {
        GenderCohortDefinition females = new GenderCohortDefinition();
        females.setFemaleIncluded(true);
        return females;
    }

    @DocumentedDefinition(value = "unknownGender")
    public GenderCohortDefinition getUnknownGender() {
        GenderCohortDefinition unknownGender = new GenderCohortDefinition();
        unknownGender.setUnknownGenderIncluded(true);
        return unknownGender;
    }

    @DocumentedDefinition("upToAgeOnDate")
    public AgeCohortDefinition getUpToAgeOnDate() {
        AgeCohortDefinition cd = new AgeCohortDefinition();
        cd.addParameter(new Parameter("effectiveDate", "reporting.parameter.effectiveDate", Date.class));
        cd.addParameter(new Parameter("maxAge", "reporting.parameter.maxAgeInYears", Integer.class));
        return cd;
    }

    @DocumentedDefinition("atLeastAgeOnDate")
    public AgeCohortDefinition getAtLeastAgeOnDate() {
        AgeCohortDefinition cd = new AgeCohortDefinition();
        cd.addParameter(new Parameter("effectiveDate", "reporting.parameter.effectiveDate", Date.class));
        cd.addParameter(new Parameter("minAge", "reporting.parameter.minAgeInYears", Integer.class));
        return cd;
    }

    @DocumentedDefinition("ageRangeOnDate")
    public AgeCohortDefinition getAgeInRangeOnDate() {
        AgeCohortDefinition cd = new AgeCohortDefinition();
        cd.addParameter(new Parameter("effectiveDate", "reporting.parameter.effectiveDate", Date.class));
        cd.addParameter(new Parameter("minAge", "reporting.parameter.minAgeInYears", Integer.class));
        cd.addParameter(new Parameter("maxAge", "reporting.parameter.maxAgeInYears", Integer.class));
        return cd;
    }

    @DocumentedDefinition("anyEncounterDuringPeriod")
    public CohortDefinition getAnyEncounterDuringPeriod() {
        EncounterCohortDefinition cd = new EncounterCohortDefinition();
        cd.addParameter(new Parameter("onOrAfter", "reporting.parameter.onOrAfter", Date.class));
        cd.addParameter(new Parameter("onOrBefore", "reporting.parameter.onOrBefore", Date.class));
        return new MappedParametersCohortDefinition(cd, "onOrAfter", "startDate", "onOrBefore", "endDate");
    }

    @DocumentedDefinition("encounterSearchAdvanced")
    public CohortDefinition getAnyEncounterDuringPeriodWithOccurrence() {
        EncounterCohortDefinition cd = new EncounterCohortDefinition();
        cd.addParameter(new Parameter("onOrAfter", "reporting.parameter.startDate", Date.class));
        cd.addParameter(new Parameter("onOrBefore", "reporting.parameter.endDate", Date.class));
        cd.addParameter(new Parameter("atLeastCount", "reporting.parameter.atleast", Integer.class));
        cd.addParameter(new Parameter("atMostCount", "reporting.parameter.atMost", Integer.class));
        cd.addParameter(new Parameter("encounterTypeList", "reporting.parameter.encounterTypeList", EncounterType.class, List.class, null));
        cd.addParameter(new Parameter("formList", "reporting.parameter.formList", Form.class, List.class, null));
        cd.addParameter(new Parameter("locationList", "reporting.parameter.location", Location.class, List.class, null));
        return new MappedParametersCohortDefinition(cd, "onOrAfter", "startDate", "onOrBefore", "endDate", "encounterTypeList", "encounterTypes", "locationList", "locations", "atLeastCount", "atLeast", "atMostCount", "atMost", "formList", "forms");
    }

    @DocumentedDefinition("anyEncounterOfTypesDuringPeriod")
    public CohortDefinition getAnyEncounterOfTypesDuringPeriod() {
        EncounterCohortDefinition cd = new EncounterCohortDefinition();
        cd.addParameter(new Parameter("onOrAfter", "reporting.parameter.startDate", Date.class));
        cd.addParameter(new Parameter("onOrBefore", "reporting.parameter.endDate", Date.class));
        cd.addParameter(new Parameter("encounterTypeList", "reporting.parameter.encounterTypeList", EncounterType.class, List.class, null));
        return new MappedParametersCohortDefinition(cd, "onOrAfter", "startDate", "onOrBefore", "endDate", "encounterTypeList", "encounterTypes");
    }

    @DocumentedDefinition("bornDuringPeriod")
    public CohortDefinition getBornDuringPeriod() {
        BirthAndDeathCohortDefinition cd = new BirthAndDeathCohortDefinition();
        cd.addParameter(new Parameter("bornOnOrAfter", "reporting.parameter.startDate", Date.class));
        cd.addParameter(new Parameter("bornOnOrBefore", "reporting.parameter.endDate", Date.class));
        return new MappedParametersCohortDefinition(cd, "bornOnOrAfter", "startDate", "bornOnOrBefore", "endDate");
    }

    @DocumentedDefinition("diedDuringPeriod")
    public CohortDefinition getDiedDuringPeriod() {
        BirthAndDeathCohortDefinition cd = new BirthAndDeathCohortDefinition();
        cd.addParameter(new Parameter("diedOnOrAfter", "reporting.parameter.startDate", Date.class));
        cd.addParameter(new Parameter("diedOnOrBefore", "reporting.parameter.endDate", Date.class));
        return new MappedParametersCohortDefinition(cd, "diedOnOrAfter", "startDate", "diedOnOrBefore", "endDate");
    }

    @DocumentedDefinition("personWithAttribute")
    public CohortDefinition getPersonWithAttribute() {
        PersonAttributeCohortDefinition cd = new PersonAttributeCohortDefinition();
        cd.addParameter(new Parameter("attributeType", "reporting.parameter.attributeType", PersonAttributeType.class));
        cd.addParameter(new Parameter("values", "reporting.parameter.values", String.class, List.class, null));
        return cd;
    }
}
