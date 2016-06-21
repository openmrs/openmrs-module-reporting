package org.openmrs.module.reporting.definition.library.implementerconfigured;

import java.io.File;

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.springframework.stereotype.Component;

@Component
public class ImplementerConfiguredCohortDefinitionLibrary extends BaseImplementerConfiguredDefinitionLibrary<CohortDefinition> {

	public ImplementerConfiguredCohortDefinitionLibrary() {
		super(CohortDefinition.class, "cohort");
	}

	@Override
	protected CohortDefinition sqlDefinition(String sql) {
		return new SqlCohortDefinition(sql);
	}

}