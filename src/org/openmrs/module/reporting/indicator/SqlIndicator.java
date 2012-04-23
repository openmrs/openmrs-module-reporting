package org.openmrs.module.reporting.indicator;

import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.BaseDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

public class SqlIndicator extends BaseIndicator {

	public static final long serialVersionUID = 1L;
	
	@ConfigurationProperty
	private Mapped<QueryString> sql;
	
	@ConfigurationProperty
	private Mapped<QueryString> denominatorSql;

	public Mapped<QueryString> getSql() {
		return sql;
	}

	public void setSql(Mapped<QueryString> sql) {
		this.sql = sql;
	}

	public Mapped<QueryString> getDenominatorSql() {
		return denominatorSql;
	}

	public void setDenominatorSql(Mapped<QueryString> denominatorSql) {
		this.denominatorSql = denominatorSql;
	}

	/**
	 * wrapper class allowing the SqlIndicator.sql property to be Mapped
	 * @author dthomas
	 *
	 */
	public class QueryString extends BaseDefinition {
		
			private String sql;
			private Integer id;

			public QueryString(String sql){
				this.sql = sql;
			}
			
			public Integer getId() {
				return this.id;
			}

			public void setId(Integer id) {
				this.id = id;
			}

			public String getSql() {
				return sql;
			}

			public void setSql(String sql) {
				this.sql = sql;
			}
			
	}
}
