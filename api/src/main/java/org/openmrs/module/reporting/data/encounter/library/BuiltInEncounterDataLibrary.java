/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
