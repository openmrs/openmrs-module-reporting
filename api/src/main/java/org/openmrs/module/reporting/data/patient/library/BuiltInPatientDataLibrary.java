/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.patient.library;

import org.openmrs.PatientIdentifier;
import org.openmrs.PersonName;
import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.common.VitalStatus;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PersonToPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PreferredIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.data.person.definition.VitalStatusDataDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Basic set of patient data columns
 */
@Component
public class BuiltInPatientDataLibrary extends BaseDefinitionLibrary<PatientDataDefinition> {

    public static final String PREFIX = "reporting.library.patientDataDefinition.builtIn.";

    @Override
    public Class<? super PatientDataDefinition> getDefinitionType() {
        return PatientDataDefinition.class;
    }

    @Override
    public String getKeyPrefix() {
        return PREFIX;
    }

    @DocumentedDefinition("patientId")
    public PatientDataDefinition getPatientId() {
        return new PatientIdDataDefinition();
    }

    @DocumentedDefinition("preferredName.familyName")
    public PatientDataDefinition getPreferredFamilyName() {
        return getPreferredName("familyName");
    }

    @DocumentedDefinition("preferredName.familyName2")
    public PatientDataDefinition getPreferredFamilyName2() {
        return getPreferredName("familyName2");
    }

    @DocumentedDefinition("preferredName.givenName")
    public PatientDataDefinition getPreferredGivenName() {
        return getPreferredName("givenName");
    }

    @DocumentedDefinition("preferredName.middleName")
    public PatientDataDefinition getPreferredMiddleName() {
        return getPreferredName("middleName");
    }

	@DocumentedDefinition("birthdate")
	public PatientDataDefinition getBirthdate() {
		return getBirthdateConverted();
	}

    @DocumentedDefinition("birthdate.ymd")
    public PatientDataDefinition getBirthdateYmd() {
        return convert(getBirthdateConverted(), new BirthdateConverter("yyyy-MM-dd"));
    }

    @DocumentedDefinition("birthdate.estimated")
    public PatientDataDefinition getBirthdateEstimated() {
        return convert(getBirthdateConverted(), new PropertyConverter(Birthdate.class, "estimated"));
    }

    @DocumentedDefinition("ageOnDate.fullYears")
    public PatientDataDefinition getAgeOnDateYears() {
        return getAgeOnEffectiveDate(new PropertyConverter(Age.class, "fullYears"));
    }

    @DocumentedDefinition("ageOnDate.fullMonths")
    public PatientDataDefinition getAgeOnDateMonths() {
        return getAgeOnEffectiveDate(new PropertyConverter(Age.class, "fullMonths"));
    }

    @DocumentedDefinition("ageAtStart")
    public PatientDataDefinition getAgeAtStart() {
        ConvertedPatientDataDefinition ageOnDate = new ConvertedPatientDataDefinition();
        ageOnDate.addParameter(new Parameter("startDate", "reporting.parameter.startDate", Date.class));
        ageOnDate.setDefinitionToConvert(Mapped.<PatientDataDefinition>map(getAgeOnEffectiveDate(), "effectiveDate=${startDate}"));
        return ageOnDate;
    }

    @DocumentedDefinition("ageAtEnd")
    public PatientDataDefinition getAgeAtEnd() {
        ConvertedPatientDataDefinition ageOnDate = new ConvertedPatientDataDefinition();
        ageOnDate.addParameter(new Parameter("endDate", "reporting.parameter.endDate", Date.class));
        ageOnDate.setDefinitionToConvert(Mapped.<PatientDataDefinition>map(getAgeOnEffectiveDate(), "effectiveDate=${endDate}"));
        return ageOnDate;
    }

    @DocumentedDefinition("gender")
    public PatientDataDefinition getGender() {
        return new PersonToPatientDataDefinition(new GenderDataDefinition());
    }

    @DocumentedDefinition("vitalStatus.dead")
    public PatientDataDefinition getVitalStatusDead() {
        return getVitalStatus(new PropertyConverter(VitalStatus.class, "dead"));
    }

    @DocumentedDefinition("vitalStatus.deathDate")
    public PatientDataDefinition getVitalStatusDeathDate() {
        return getVitalStatus(new PropertyConverter(VitalStatus.class, "deathDate"));
    }

    @DocumentedDefinition("preferredIdentifier.identifier")
    public PatientDataDefinition getPreferredIdentifierIdentifier() {
        return getPreferredIdentifier(new PropertyConverter(PatientIdentifier.class, "identifier"));
    }

    @DocumentedDefinition("preferredIdentifier.location")
    public PatientDataDefinition getPreferredIdentifierLocation() {
        return getPreferredIdentifier(new PropertyConverter(PatientIdentifier.class, "location"), new ObjectFormatter());
    }

    // helpers

	protected PatientDataDefinition convert(PatientDataDefinition pdd, DataConverter... converters) {
		return new ConvertedPatientDataDefinition(pdd, converters);
	}

	protected PatientDataDefinition convert(PersonDataDefinition pdd, DataConverter... converters) {
		return new ConvertedPatientDataDefinition(new PersonToPatientDataDefinition(pdd), converters);
	}

	private PatientDataDefinition getBirthdateConverted(DataConverter... converters) {
		return convert(new BirthdateDataDefinition(), converters);
	}

    protected PatientDataDefinition getAgeOnEffectiveDate(DataConverter... converters) {
        AgeDataDefinition ageDataDefinition = new AgeDataDefinition();
        ageDataDefinition.addParameter(new Parameter("effectiveDate", "reporting.parameter.effectiveDate", Date.class));
		return convert(ageDataDefinition, converters);
    }

    protected PatientDataDefinition getVitalStatus(DataConverter... converters) {
        return convert(new VitalStatusDataDefinition(), converters);
    }

	protected PatientDataDefinition getPreferredIdentifier(DataConverter... converters) {
        return convert(new PreferredIdentifierDataDefinition(), converters);
    }

	protected PatientDataDefinition getPreferredName(String property) {
        return convert(new PreferredNameDataDefinition(), new PropertyConverter(PersonName.class, property));
    }
}
