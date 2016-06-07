package org.openmrs.module.reporting.data.visit.definition;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class SqlVisitDataDefinition extends BaseDataDefinition implements VisitDataDefinition {

	public static final long serialVersionUID = 1L;

	@ConfigurationProperty
	private String sql;

	@Override
	public Class<?> getDataType() {
		return Object.class;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

}
