package org.openmrs.module.reporting.common;

/**
 * Represents comparisons that can be performed upon sets of unordered values, e.g. Text or Coded
 * @see RangeComparator
 */
public enum SetComparator {
	IN("IN"), NOT_IN("NOT IN");
	
	public final String sqlRep;
	
	SetComparator(String sqlRep) {
		this.sqlRep = sqlRep;
	}
	
	public String getSqlRepresentation() {
		return sqlRep;
	}
}