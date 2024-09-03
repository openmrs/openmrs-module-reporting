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

import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Form;
import org.openmrs.PersonAttributeType;
import org.openmrs.Program;
import org.openmrs.Concept;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.BirthAndDeathCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PersonAttributeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.cohort.definition.MappedParametersCohortDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.TextObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;

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

    @DocumentedDefinition("codedObsSearchAdvanced")
    public CohortDefinition getCodedObsSearchAdvanced() {
        CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
        cd.addParameter(new Parameter("timeModifier", "reporting.parameter.timeModifier", CodedObsCohortDefinition.TimeModifier.class));
        cd.addParameter(new Parameter("question", "reporting.parameter.question", Concept.class));
        cd.addParameter(new Parameter("values", "reporting.parameter.valueList", Concept.class, List.class, null));
        cd.addParameter(new Parameter("onOrAfter", "reporting.parameter.onOrAfter", Date.class));
        cd.addParameter(new Parameter("onOrBefore", "reporting.parameter.onOrBefore", Date.class));
        cd.addParameter(new Parameter("operator", "reporting.parameter.operator", SetComparator.class));
        return cd;
    }

    @DocumentedDefinition("numericObsSearchAdvanced")
    public CohortDefinition getNumericObsSearchAdvanced() {
        NumericObsCohortDefinition cd = new NumericObsCohortDefinition();
        cd.addParameter(new Parameter("timeModifier", "reporting.parameter.timeModifier", NumericObsCohortDefinition.TimeModifier.class));
        cd.addParameter(new Parameter("question", "reporting.parameter.question", Concept.class));
        cd.addParameter(new Parameter("operator1", "reporting.parameter.operator1", RangeComparator.class));
        cd.addParameter(new Parameter("value1", "reporting.parameter.value1", Double.class));
        cd.addParameter(new Parameter("operator2", "reporting.parameter.operator2", RangeComparator.class));
        cd.addParameter(new Parameter("value2", "reporting.parameter.value2", Double.class));
        cd.addParameter(new Parameter("onOrAfter", "reporting.parameter.onOrAfter", Date.class));
        cd.addParameter(new Parameter("onOrBefore", "reporting.parameter.onOrBefore", Date.class));
        return cd;
    }

    @DocumentedDefinition("dateObsSearchAdvanced")
    public CohortDefinition getDateObsSearchAdvanced() {
        DateObsCohortDefinition cd = new DateObsCohortDefinition();
        cd.addParameter(new Parameter("timeModifier", "reporting.parameter.timeModifier", NumericObsCohortDefinition.TimeModifier.class));
        cd.addParameter(new Parameter("question", "reporting.parameter.question", Concept.class));
        cd.addParameter(new Parameter("operator1", "reporting.parameter.operator1", RangeComparator.class));
        cd.addParameter(new Parameter("value1", "reporting.parameter.value1", Date.class));
        cd.addParameter(new Parameter("operator2", "reporting.parameter.operator2", RangeComparator.class));
        cd.addParameter(new Parameter("value2", "reporting.parameter.value2", Date.class));
        cd.addParameter(new Parameter("onOrAfter", "reporting.parameter.onOrAfter", Date.class));
        cd.addParameter(new Parameter("onOrBefore", "reporting.parameter.onOrBefore", Date.class));
        return cd;
    }

    @DocumentedDefinition("textObsSearchAdvanced")
    public CohortDefinition getTextObsSearchAdvanced() {
        TextObsCohortDefinition cd = new TextObsCohortDefinition();
        cd.addParameter(new Parameter("timeModifier", "reporting.parameter.timeModifier", TextObsCohortDefinition.TimeModifier.class));
        cd.addParameter(new Parameter("question", "reporting.parameter.question", Concept.class));
        cd.addParameter(new Parameter("values", "reporting.parameter.valueList", Concept.class, List.class, null));
        cd.addParameter(new Parameter("onOrAfter", "reporting.parameter.onOrAfter", Date.class));
        cd.addParameter(new Parameter("onOrBefore", "reporting.parameter.onOrBefore", Date.class));
        cd.addParameter(new Parameter("operator", "reporting.parameter.operator", SetComparator.class));
        return cd;
    }

    @DocumentedDefinition("encounterSearchAdvanced")
    public CohortDefinition getEncounterSearchAdvanced() {
        EncounterCohortDefinition cd = new EncounterCohortDefinition();
        cd.addParameter(new Parameter("onOrAfter", "reporting.parameter.onOrAfter", Date.class));
        cd.addParameter(new Parameter("onOrBefore", "reporting.parameter.onOrBefore", Date.class));
        cd.addParameter(new Parameter("atLeastCount", "reporting.parameter.atLeastCount", Integer.class));
        cd.addParameter(new Parameter("atMostCount", "reporting.parameter.atMostCount", Integer.class));
        cd.addParameter(new Parameter("encounterTypeList", "reporting.parameter.encounterTypeList", EncounterType.class, List.class, null));
        cd.addParameter(new Parameter("formList", "reporting.parameter.formList", Form.class, List.class, null));
        cd.addParameter(new Parameter("locationList", "reporting.parameter.locationList", Location.class, List.class, null));
        cd.addParameter(new Parameter("timeQualifier", "reporting.parameter.timeQualifier", TimeQualifier.class));
        return cd;
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
        cd.setDied(true);
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

    @DocumentedDefinition("patientsWithEnrollment")
    public CohortDefinition getPatientsWithEnrollment() {
        ProgramEnrollmentCohortDefinition cd = new ProgramEnrollmentCohortDefinition();
        cd.addParameter(new Parameter("programs", "reporting.parameter.programs", Program.class, List.class, null, null, true));
        cd.addParameter(new Parameter("enrolledOnOrAfter", "reporting.parameter.enrolledOnOrAfter", Date.class, null, null, null, false));
        cd.addParameter(new Parameter("enrolledOnOrBefore", "reporting.parameter.enrolledOnOrBefore", Date.class, null, null, null, false));
        cd.addParameter(new Parameter("completedOnOrAfter", "reporting.parameter.completedOnOrAfter", Date.class, null, null, null, false));
        cd.addParameter(new Parameter("completedOnOrBefore", "reporting.parameter.completedOnOrBefore", Date.class, null, null, null, false));
        cd.addParameter(new Parameter("locationList", "reporting.parameter.locationList", Location.class, List.class,  null, null, false));
        return new MappedParametersCohortDefinition(cd,
            "programs", "programs",
            "enrolledOnOrAfter", "enrolledOnOrAfter",
            "enrolledOnOrBefore", "enrolledOnOrBefore",
            "completedOnOrAfter", "completedOnOrAfter",
            "completedOnOrBefore", "completedOnOrBefore",
            "locationList", "locations"
        );
    }

    @DocumentedDefinition("patientsInProgram")
    public CohortDefinition getPatientsInProgram() {
        InProgramCohortDefinition cd = new InProgramCohortDefinition();
        cd.addParameter(new Parameter("programs", "reporting.parameter.programs", Program.class, List.class, null, null, true));
        cd.addParameter(new Parameter("onOrAfter", "reporting.parameter.onOrAfter", Date.class, null, null, null, false));
        cd.addParameter(new Parameter("onOrBefore", "reporting.parameter.onOrBefore", Date.class, null, null, null, false));
        cd.addParameter(new Parameter("onDate", "reporting.parameter.onDate", Date.class, null, null, null, false));
        cd.addParameter(new Parameter("locations", "reporting.parameter.locationList", Location.class, List.class,  null, null, false));

        return new MappedParametersCohortDefinition(cd,
                "programs", "programs",
                "onOrAfter", "onOrAfter",
                "onOrBefore", "onOrBefore",
                "onDate", "onDate",
                "locations", "locations"
        );

    }

    @DocumentedDefinition("patientsWithState")
    public CohortDefinition getPatientsWithState() {
        PatientStateCohortDefinition cd = new PatientStateCohortDefinition();
        cd.addParameter(new Parameter("states", "reporting.parameter.states", ProgramWorkflowState.class, List.class, null));
        cd.addParameter(new Parameter("startedOnOrAfter", "reporting.parameter.startedOnOrAfter", Date.class));
        cd.addParameter(new Parameter("startedOnOrBefore", "reporting.parameter.startedOnOrBefore", Date.class));
        cd.addParameter(new Parameter("endedOnOrAfter", "reporting.parameter.endedOnOrAfter", Date.class));
        cd.addParameter(new Parameter("endedOnOrBefore", "reporting.parameter.endedOnOrBefore", Date.class));
        return cd;
    }

    @DocumentedDefinition("patientsInState")
    public CohortDefinition getPatientsInState() {
        InStateCohortDefinition cd = new InStateCohortDefinition();
        cd.addParameter(new Parameter("states", "reporting.parameter.states", ProgramWorkflowState.class, List.class, null));
        cd.addParameter(new Parameter("onOrAfter", "reporting.parameter.onOrAfter", Date.class));
        cd.addParameter(new Parameter("onOrBefore", "reporting.parameter.onOrBefore", Date.class));
        cd.addParameter(new Parameter("onDate", "reporting.parameter.date", Date.class));
        return cd;
    }
}
