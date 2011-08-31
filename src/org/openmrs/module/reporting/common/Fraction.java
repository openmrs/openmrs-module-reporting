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

import java.io.Serializable;
import java.text.NumberFormat;

/**
 * Represents a Fractional Number 
 */
public class Fraction extends Number implements Comparable<Fraction>, Serializable {

	public static final long serialVersionUID = 1L;
	
	//***** PROPERTIES *****
	
	private final int numerator;
	private final int denominator;
	
	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public Fraction(int numerator, int denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
	}
	
	//***** PROPERTY ACCESS *****
	
	/**
	 * @return the numerator
	 */
	public int getNumerator() {
		return numerator;
	}

	/**
	 * @return the denominator
	 */
	public int getDenominator() {
		return denominator;
	}
	
	//***** STATIC METHODS *****
	
	/**
	 * @should return the greatest common divisor between 2 numbers
	 */
	public static int gcd(int n1, int n2) {
		if (n2 == 0) { return n1; }
		return gcd(n2, n1 % n2);
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @should return a new fraction reduced to lowest form
	 */
	public Fraction reduce() {
		int gcd = gcd(numerator, denominator);
		return new Fraction((int)numerator/gcd, (int)denominator/gcd);
	}
	
	/**
	 * @param decimalPlaces the number of decimal points to include
	 * @return this Fraction formatted as a percentage
	 * @should return a percentage to the correct precision
	 */
	public String toPercentString(int decimalPlaces) {
		if (denominator == 0) { return "N/A"; }
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMaximumFractionDigits(decimalPlaces);
		return nf.format(doubleValue());
	}

	/**
	* @see Number#doubleValue()
	*/
	@Override
	public double doubleValue() {
		return (double)numerator / (double)denominator;
	}

	/**
	* @see Number#floatValue()
	*/
	@Override
	public float floatValue() {
		return (float)doubleValue();
	}

	/**
	* @see Number#intValue()
	*/
	@Override
	public int intValue() {
		return (int)doubleValue();
	}

	/**
	* @see Number#longValue()
	*/
	@Override
	public long longValue() {
		return (long)doubleValue();
	}
	
	/**
	* @see Comparable#compareTo(Object)
	* @should compare two fractions numerically
	 */
	public int compareTo(Fraction that) {
		long n1 = this.numerator * that.denominator;
		long n2 = this.denominator * that.numerator;
		return ((n1 < n2) ? -1 : (n1 > n2 ? 1 : 0));
	}

	/**
	* @see Object#equals(Object)
	* @should return true if two fractions represent the same numerical value
	*/
	@Override
	public boolean equals(Object that) {
		if (this == that) { return true; }
		if (that != null && that instanceof Fraction) {
			return this.compareTo((Fraction)that) == 0;
		}
		return false;
	}

	/**
	* @see Object#hashCode()
	*/
	@Override
	public int hashCode() {
		Fraction f = this.reduce();
		return 31 * (31 * 7 + f.getNumerator()) + f.getDenominator();
	}

	/**
	* @see Object#toString()
	* @should return a string representation of the fraction
	* @should allow representation of fractions with 0 denominators
	*/
	@Override
	public String toString() {
		return toPercentString(1) + " (" + numerator + " / " + denominator + ")";
	}
}
