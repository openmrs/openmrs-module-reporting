package org.openmrs.module.reporting.definition.library.implementerconfigured;

import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.SqlEncounterDataDefinition;
import org.springframework.stereotype.Component;

@Component
public class ImplementerConfiguredEncounterDataDefinitionLibrary extends BaseImplementerConfiguredDefinitionLibrary<EncounterDataDefinition> {

	public ImplementerConfiguredEncounterDataDefinitionLibrary() {
		super(EncounterDataDefinition.class, "encounterData");
		setSqlDefinitionClass(SqlEncounterDataDefinition.class);
	}

}
