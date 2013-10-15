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

package org.openmrs.module.reporting.data.patient.library;

import org.openmrs.PatientIdentifier;
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
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.VitalStatusDataDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.springframework.stereotype.Component;

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

    @DocumentedDefinition("birthdate.ymd")
    public PatientDataDefinition getBirthdateYmd() {
        return getBirthdate(new BirthdateConverter("yyyy-MM-dd"));
    }

    @DocumentedDefinition("birthdate.estimated")
    public PatientDataDefinition getBirthdateEstimated() {
        return getBirthdate(new PropertyConverter(Birthdate.class, "estimated"));
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

    private PatientDataDefinition getBirthdate(DataConverter... converters) {
        return new ConvertedPatientDataDefinition(
                new PersonToPatientDataDefinition(
                        new BirthdateDataDefinition()),
                converters);
    }

    private PatientDataDefinition getVitalStatus(DataConverter... converters) {
        return new ConvertedPatientDataDefinition(
                new PersonToPatientDataDefinition(
                        new VitalStatusDataDefinition()),
                converters);
    }

    private PatientDataDefinition getPreferredIdentifier(DataConverter... converters) {
        return new ConvertedPatientDataDefinition(
                new PreferredIdentifierDataDefinition(),
                converters);
    }


}
