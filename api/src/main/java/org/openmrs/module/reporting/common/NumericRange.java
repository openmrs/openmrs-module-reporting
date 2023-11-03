/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.common;

/**
 * Represents a Numeric Range that may or may not include end points
 */
public class NumericRange {
	
	//***********************
	// PROPERTIES
	//***********************
	
	private Number lowerBound;
	private Number upperBound;
	private boolean inclusiveOfLower = true;
	private boolean inclusiveOfUpper = true;
	
	//***********************
	// CONSTRUCTORS
	//***********************
	
	/**
	 * Default Constructor
	 */
	public NumericRange() { }
	
	/**
	 * Constructor which takes in a lower and upper bound, inclusive of both
	 */
	public NumericRange(Number lowerBound, Number upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}
	
	/**
	 * Full Constructor
	 */
	public NumericRange(Number lowerBound, boolean inclusiveOfLower, Number upperBound, boolean inclusiveOfUpper) {
		this(lowerBound, upperBound);
		this.inclusiveOfLower = inclusiveOfLower;
		this.inclusiveOfUpper = inclusiveOfUpper;
	}
	
	//***********************
	// STATIC METHODS
	//***********************
	
	/**
	 * Returns a boolean indicating whether the passed double is before the passed NumericRange
	 */
	public static boolean isLessThan(NumericRange range, double d) {
		if (range.getLowerBound() != null) {
			if (range.isInclusiveOfLower()) {
				return d < range.getLowerBound().doubleValue();
			}
			else {
				return d <= range.getLowerBound().doubleValue();
			}
		}
		return false;
	}
	
	/**
	 * Returns a boolean indicating whether the passed Date is within the passed DateRange
	 */
	public static boolean isWithin(NumericRange range, double d) {
		return !isLessThan(range, d) && !isGreaterThan(range, d);
	}
	
	/**
	 * Returns a boolean indicating whether the passed double is before the passed NumericRange
	 */
	public static boolean isGreaterThan(NumericRange range, double d) {
		if (range.getUpperBound() != null) {
			if (range.isInclusiveOfUpper()) {
				return d > range.getUpperBound().doubleValue();
			}
			else {
				return d >= range.getUpperBound().doubleValue();
			}
		}
		return false;
	}
	
	/**
	 * Returns the DateRange formatted in Interval Notation
	 */
	public static String format(NumericRange range, String nullReplacement) {
		String prefix = range.isInclusiveOfLower() ? "[" : "(";
		String suffix = range.isInclusiveOfUpper() ? "]" : ")";
		return prefix + range.getLowerBound() + "," + range.getUpperBound() + suffix;
	}
	
	//***********************
	// INSTANCE METHODS
	//***********************
	
	/** 
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return format(this, "*");
	}
	
	/** 
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof NumericRange) {
			NumericRange that = (NumericRange) obj;
			String thisFormat = format(this, "*");
			String thatFormat = format(that, "*");
			return thisFormat.equals(thatFormat);
		}
		return this == obj;
	}

	/** 
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return format(this, "*").hashCode();
	}
	
	//***********************
	// PROPERTY ACCESS
	//***********************

	/**
	 * @return the lowerBound
	 */
	public Number getLowerBound() {
		return lowerBound;
	}

	/**
	 * @param lowerBound the lowerBound to set
	 */
	public void setLowerBound(Number lowerBound) {
		this.lowerBound = lowerBound;
	}

	/**
	 * @return the upperBound
	 */
	public Number getUpperBound() {
		return upperBound;
	}

	/**
	 * @param upperBound the upperBound to set
	 */
	public void setUpperBound(Number upperBound) {
		this.upperBound = upperBound;
	}

	/**
	 * @return the inclusiveOfLower
	 */
	public boolean isInclusiveOfLower() {
		return inclusiveOfLower;
	}

	/**
	 * @param inclusiveOfLower the inclusiveOfLower to set
	 */
	public void setInclusiveOfLower(boolean inclusiveOfLower) {
		this.inclusiveOfLower = inclusiveOfLower;
	}

	/**
	 * @return the inclusiveOfUpper
	 */
	public boolean isInclusiveOfUpper() {
		return inclusiveOfUpper;
	}

	/**
	 * @param inclusiveOfUpper the inclusiveOfUpper to set
	 */
	public void setInclusiveOfUpper(boolean inclusiveOfUpper) {
		this.inclusiveOfUpper = inclusiveOfUpper;
	}
}
