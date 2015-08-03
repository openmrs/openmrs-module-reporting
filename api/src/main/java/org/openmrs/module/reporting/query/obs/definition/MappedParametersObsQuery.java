package org.openmrs.module.reporting.query.obs.definition;

import org.openmrs.Obs;
import org.openmrs.module.reporting.query.MappedParametersQuery;

import java.util.Map;

/**
 * Allows you to easily expose an ObsQuery with different names for its parameters
 * (which typically must be the same as @ConfigurationProperty-annotated properties.
 */
public class MappedParametersObsQuery extends MappedParametersQuery<ObsQuery, Obs> implements ObsQuery {

    public MappedParametersObsQuery() { }

	public MappedParametersObsQuery(ObsQuery toWrap, Map<String, String> renamedParameters) {
		super(toWrap, renamedParameters);
	}
}
