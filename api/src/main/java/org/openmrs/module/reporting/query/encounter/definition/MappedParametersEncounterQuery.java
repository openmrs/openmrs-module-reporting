package org.openmrs.module.reporting.query.encounter.definition;

import org.openmrs.Encounter;
import org.openmrs.module.reporting.query.MappedParametersQuery;

import java.util.Map;

/**
 * Allows you to easily expose an EncounterQuery with different names for its parameters
 * (which typically must be the same as @ConfigurationProperty-annotated properties.
 */
public class MappedParametersEncounterQuery extends MappedParametersQuery<EncounterQuery, Encounter> implements EncounterQuery {

    public MappedParametersEncounterQuery() { }

	public MappedParametersEncounterQuery(EncounterQuery toWrap, Map<String, String> renamedParameters) {
		super(toWrap, renamedParameters);
	}
}
