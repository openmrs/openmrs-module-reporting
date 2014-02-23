package org.openmrs.module.reporting.cohort.definition;

import org.openmrs.Patient;
import org.openmrs.module.reporting.query.MappedParametersQuery;

import java.util.HashMap;
import java.util.Map;

/**
 * Allows you to easily expose a cohort definition with different names for its parameters (which typically must be the
 * same as @ConfigurationProperty-annotated properties.
 */
public class MappedParametersCohortDefinition extends MappedParametersQuery<CohortDefinition, Patient> implements CohortDefinition {

    public MappedParametersCohortDefinition() { }

	public MappedParametersCohortDefinition(CohortDefinition toWrap, Map<String, String> renamedParameters) {
		super(toWrap, renamedParameters);
	}

	/**
	 * Example usage:
	 * new MappedParametersCohortDefinition(encounterCD, "onOrAfter", "startDate", "onOrBefore", "endDate");
	 * @param toWrap
	 * @param renamedParameters must have an even number of entries. Each pair should be the is the original parameter
	 *                          name, followed by the new parameter name
	 */
	public MappedParametersCohortDefinition(CohortDefinition toWrap, String... renamedParameters) {
		super(toWrap, toMap(renamedParameters));
	}

	private static Map<String, String> toMap(String... keysAndValues) {
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < keysAndValues.length; i += 2) {
			map.put(keysAndValues[i], keysAndValues[i + 1]);
		}
		return map;
	}
}
