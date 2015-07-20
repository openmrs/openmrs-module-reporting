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

package org.openmrs.module.reporting.data.encounter.library;

import org.openmrs.module.reporting.common.AuditInfo;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.encounter.definition.AuditInfoEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.ConvertedEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDatetimeDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterIdDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterLocationDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterTypeDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterVisitDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.PatientToEncounterDataDefinition;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Basic set of encounter data columns
 */
@Component
public class BuiltInEncounterDataLibrary extends BaseDefinitionLibrary<EncounterDataDefinition> {

    public static final String PREFIX = "reporting.library.encounterDataDefinition.builtIn.";

    @Autowired
    BuiltInPatientDataLibrary builtInPatientDataLibrary;

    @Override
    public Class<? super EncounterDataDefinition> getDefinitionType() {
        return EncounterDataDefinition.class;
    }

    @Override
    public String getKeyPrefix() {
        return PREFIX;
    }

    @DocumentedDefinition("encounterId")
    public EncounterDataDefinition getEncounterId() {
        return new EncounterIdDataDefinition();
    }

    @DocumentedDefinition("encounterDatetime")
    public EncounterDataDefinition getEncounterDatetime() {
        return new EncounterDatetimeDataDefinition();
    }

    @DocumentedDefinition("encounterVisit")
    public EncounterDataDefinition getEncounterVisit() { return new EncounterVisitDataDefinition(); }

    @DocumentedDefinition("encounterType.name")
    public EncounterDataDefinition getEncounterTypeName() {
        return new ConvertedEncounterDataDefinition(new EncounterTypeDataDefinition(), new ObjectFormatter());
    }

    @DocumentedDefinition("location.name")
    public EncounterDataDefinition getLocationName() {
        return new ConvertedEncounterDataDefinition(new EncounterLocationDataDefinition(), new ObjectFormatter());
    }

    @DocumentedDefinition("dateCreated")
    public EncounterDataDefinition getDateCreated() {
        return auditInfo(new PropertyConverter(AuditInfo.class, "dateCreated"));
    }

    @DocumentedDefinition("patientId")
    public EncounterDataDefinition getPatientId() {
        return new PatientToEncounterDataDefinition(builtInPatientDataLibrary.getPatientId());
    }

    private ConvertedEncounterDataDefinition auditInfo(DataConverter... converters) {
        return new ConvertedEncounterDataDefinition(new AuditInfoEncounterDataDefinition(), converters);
    }

}
