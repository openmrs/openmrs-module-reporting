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
