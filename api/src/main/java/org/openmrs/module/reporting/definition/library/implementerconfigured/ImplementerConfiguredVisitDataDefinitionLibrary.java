package org.openmrs.module.reporting.definition.library.implementerconfigured;

import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.SqlEncounterDataDefinition;
import org.openmrs.module.reporting.data.visit.definition.SqlVisitDataDefinition;
import org.openmrs.module.reporting.data.visit.definition.VisitDataDefinition;
import org.springframework.stereotype.Component;

@Component
public class ImplementerConfiguredVisitDataDefinitionLibrary extends BaseImplementerConfiguredDefinitionLibrary<VisitDataDefinition> {

	public ImplementerConfiguredVisitDataDefinitionLibrary() {
		super(VisitDataDefinition.class, "visitData");
		setSqlDefinitionClass(SqlVisitDataDefinition.class);
	}

}
