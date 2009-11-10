/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.common;

import java.util.Date;

import org.openmrs.module.util.DateUtil;

/**
 * Represents a Date Range that may or may not include end points
 */
public class DateRange {
	
	//***********************
	// PROPERTIES
	//***********************
	
	private Date startDate;
	private Date endDate;
	private boolean inclusiveOfStart = true;
	private boolean inclusiveOfEnd = true;
	
	//***********************
	// CONSTRUCTORS
	//***********************
	
	/**
	 * Default Constructor
	 */
	public DateRange() { }
	
	/**
	 * Constructor which takes in a start and end date
	 * and is inclusive of both end dates
	 */
	public DateRange(Date startDate, Date endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	/**
	 * Full Constructor
	 */
	public DateRange(Date startDate, boolean inclusiveOfStart, Date endDate, boolean inclusiveOfEnd) {
		this(startDate, endDate);
		this.inclusiveOfStart = inclusiveOfStart;
		this.inclusiveOfEnd = inclusiveOfEnd;
	}
	
	//***********************
	// STATIC METHODS
	//***********************
	
	/**
	 * Returns a boolean indicating whether the passed Date is before the passed DateRange
	 */
	public static boolean isBefore(DateRange dateRange, Date d) {
		if (dateRange.getStartDate() != null) {
			return d.before(dateRange.getStartDate());
		}
		return false;
	}
	
	/**
	 * Returns a boolean indicating whether the passed Date is within the passed DateRange
	 */
	public static boolean isWithin(DateRange dateRange, Date d) {
		return !isBefore(dateRange, d) && !isAfter(dateRange, d);
	}
	
	/**
	 * Returns a boolean indicating whether the passed Date is after the passed DateRange
	 */
	public static boolean isAfter(DateRange dateRange, Date d) {
		if (dateRange.getEndDate() != null) {
			return d.after(dateRange.getEndDate());
		}
		return false;
	}
	
	/**
	 * Returns the DateRange formatted in Interval Notation
	 */
	public static String format(DateRange dateRange, String format, String nullReplacement) {
		String sd = DateUtil.formatDate(dateRange.getStartDate(), format, nullReplacement);
		String ed = DateUtil.formatDate(dateRange.getEndDate(), format, nullReplacement);
		String prefix = dateRange.inclusiveOfStart ? "[" : "(";
		String suffix = dateRange.inclusiveOfEnd ? "]" : ")";
		return prefix + sd + "," + ed + suffix;
	}
	
	//***********************
	// INSTANCE METHODS
	//***********************
	
	/** 
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return format(this, "dd/MMM/yyyy", "*");
	}
	
	/** 
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof DateRange) {
			DateRange that = (DateRange) obj;
			String thisFormat = format(this, "yyyy-MM-dd", "*");
			String thatFormat = format(that, "yyyy-MM-dd", "*");
			return thisFormat.equals(thatFormat);
		}
		return this == obj;
	}

	/** 
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return format(this, "yyyy-MM-dd", "*").hashCode();
	}
	
	//***********************
	// PROPERTY ACCESS
	//***********************

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}
	
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the inclusiveOfStart
	 */
	public boolean isInclusiveOfStart() {
		return inclusiveOfStart;
	}

	/**
	 * @param inclusiveOfStart the inclusiveOfStart to set
	 */
	public void setInclusiveOfStart(boolean inclusiveOfStart) {
		this.inclusiveOfStart = inclusiveOfStart;
	}

	/**
	 * @return the inclusiveOfEnd
	 */
	public boolean isInclusiveOfEnd() {
		return inclusiveOfEnd;
	}

	/**
	 * @param inclusiveOfEnd the inclusiveOfEnd to set
	 */
	public void setInclusiveOfEnd(boolean inclusiveOfEnd) {
		this.inclusiveOfEnd = inclusiveOfEnd;
	}
}
