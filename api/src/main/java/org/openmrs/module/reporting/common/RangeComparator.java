package org.openmrs.module.reporting.common;

/**
 * Represents comparisons that can be performed upon ordered values, e.g. Numeric or Datetime
 * @see SetComparator
 */
public enum RangeComparator {
	LESS_THAN("<"), LESS_EQUAL("<="), EQUAL("="), GREATER_EQUAL(">="), GREATER_THAN(">");
	
	public final String sqlRep;
	
	RangeComparator(String sqlRep) {
		this.sqlRep = sqlRep;
	}
	
	public String getSqlRepresentation() {
		return sqlRep;
	}
}