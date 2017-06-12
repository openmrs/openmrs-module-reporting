package org.openmrs.module.reporting.data.patient.definition;

import org.openmrs.module.reporting.data.BaseSqlDataDefinition;

/**
 *
 */
public class SqlPatientDataDefinition extends BaseSqlDataDefinition implements PatientDataDefinition {

    @Override
    public Class<?> getDataType() {
        return Object.class;
    }

}
