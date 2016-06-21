package org.openmrs.module.reporting.definition.library.implementerconfigured;

import java.io.File;

import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.springframework.stereotype.Component;

@Component
public class ImplementerConfiguredPatientDataDefinitionLibrary extends BaseImplementerConfiguredDefinitionLibrary<PatientDataDefinition> {

	public ImplementerConfiguredPatientDataDefinitionLibrary() {
		super(PatientDataDefinition.class, "patientData");
		setSqlDefinitionClass(SqlPatientDataDefinition.class);
	}

}
