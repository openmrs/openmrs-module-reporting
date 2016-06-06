package org.openmrs.module.reporting.definition.library;

import java.io.File;

import org.openmrs.api.SerializationService;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;

public class ImplementerConfiguredCohortDefinitionLibrary extends BaseImplementerConfiguredDefinitionLibrary<CohortDefinition> {

	public ImplementerConfiguredCohortDefinitionLibrary(SerializationService serializationService, File directory) {
		super(CohortDefinition.class, "cohort", serializationService, directory);
	}

	@Override
	protected CohortDefinition sqlDefinition(String sql) {
		return new SqlCohortDefinition(sql);
	}

}
