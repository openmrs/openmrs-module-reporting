package org.openmrs.module.reporting.definition.library.implementerconfigured;

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.springframework.stereotype.Component;

@Component
public class ImplementerConfiguredDataSetDefinitionLibrary extends BaseImplementerConfiguredDefinitionLibrary<DataSetDefinition> {

	public ImplementerConfiguredDataSetDefinitionLibrary() {
		super(DataSetDefinition.class, "dataset");
		setSqlDefinitionClass(SqlDataSetDefinition.class);
		setSqlDefinitionProperty("sqlQuery");
	}

}
