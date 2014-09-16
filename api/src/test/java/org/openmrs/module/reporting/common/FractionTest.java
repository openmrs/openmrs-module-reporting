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

import org.junit.Assert;

import org.junit.Test;
import org.openmrs.test.Verifies;

/**
 * Testing the Fraction class
 */
public class FractionTest {

	/**
	 * @see {@link Fraction#gcd(int,int)}
	 */
	@Test
	@Verifies(value = "should return the greatest common divisor between 2 numbers", method = "gcd(int,int)")
	public void gcd_shouldReturnTheGreatestCommonDivisorBetween2Numbers() throws Exception {
		Assert.assertEquals(8, Fraction.gcd(24, 32));
		Assert.assertEquals(1, Fraction.gcd(13, 27));
		Assert.assertEquals(12, Fraction.gcd(12, 12));
	}

	/**
	 * @see {@link Fraction#compareTo(Fraction)}
	 */
	@Test
	@Verifies(value = "should compare two fractions numerically", method = "compareTo(Fraction)")
	public void compareTo_shouldCompareTwoFractionsNumerically() throws Exception {
		Fraction f1 = new Fraction(7, 8);
		Fraction f2 = new Fraction(3, 4);
		Fraction f3 = new Fraction(12, 16);
		Assert.assertTrue(f1.compareTo(f2) > 0);
		Assert.assertTrue(f2.compareTo(f3) == 0);
	}

	/**
	 * @see {@link Fraction#equals(Object)}
	 */
	@Test
	@Verifies(value = "should return true if two fractions represent the same numerical value", method = "equals(Object)")
	public void equals_shouldReturnTrueIfTwoFractionsRepresentTheSameNumericalValue() throws Exception {
		Fraction f1 = new Fraction(7, 8);
		Fraction f2 = new Fraction(3, 4);
		Fraction f3 = new Fraction(12, 16);
		Assert.assertFalse(f1.equals(f2));
		Assert.assertTrue(f2.equals(f3));
	}

	/**
	 * @see {@link Fraction#reduce()}
	 */
	@Test
	@Verifies(value = "should return a new fraction reduced to lowest form", method = "reduce()")
	public void reduce_shouldReturnANewFractionReducedToLowestForm() throws Exception {
		Fraction f1 = new Fraction(21, 35);
		Assert.assertEquals(21, f1.getNumerator());
		Assert.assertEquals(35, f1.getDenominator());
		f1 = f1.reduce();
		Assert.assertEquals(3, f1.getNumerator());
		Assert.assertEquals(5, f1.getDenominator());
	}

	/**
	 * @see {@link Fraction#toPercentString(int)}
	 */
	@Test
	@Verifies(value = "should return a percentage to the correct precision", method = "toPercentString(int)")
	public void toPercentString_shouldReturnAPercentageToTheCorrectPrecision()throws Exception {
		Fraction f1 = new Fraction(32, 62);
		Assert.assertEquals("51.6%", f1.toPercentString(1));
	}

	/**
	 * @see {@link Fraction#toString()}
	 */
	@Test
	@Verifies(value = "should return a string representation of the fraction", method = "toString()")
	public void toString_shouldReturnAStringRepresentationOfTheFraction() throws Exception {
		Fraction f1 = new Fraction(32, 62);
		Assert.assertEquals("51.6% (32 / 62)", f1.toString());
	}


	/**
	 * @see {@link Fraction#toString()}
	 */
	@Test
	@Verifies(value = "should allow representation of fractions with 0 denominators", method = "toString()")
	public void toString_shouldAllowRepresentationOfFractionsWith0Denominators() throws Exception {
		Fraction f = new Fraction(5,0);
		Assert.assertEquals("N/A (5 / 0)", f.toString());
	}
}