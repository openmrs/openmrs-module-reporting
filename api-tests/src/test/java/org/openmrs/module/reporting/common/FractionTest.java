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

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;
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
		Assertions.assertEquals(8, Fraction.gcd(24, 32));
		Assertions.assertEquals(1, Fraction.gcd(13, 27));
		Assertions.assertEquals(12, Fraction.gcd(12, 12));
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
		Assertions.assertTrue(f1.compareTo(f2) > 0);
		Assertions.assertTrue(f2.compareTo(f3) == 0);
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
		Assertions.assertFalse(f1.equals(f2));
		Assertions.assertTrue(f2.equals(f3));
	}

	/**
	 * @see {@link Fraction#reduce()}
	 */
	@Test
	@Verifies(value = "should return a new fraction reduced to lowest form", method = "reduce()")
	public void reduce_shouldReturnANewFractionReducedToLowestForm() throws Exception {
		Fraction f1 = new Fraction(21, 35);
		Assertions.assertEquals(21, f1.getNumerator());
		Assertions.assertEquals(35, f1.getDenominator());
		f1 = f1.reduce();
		Assertions.assertEquals(3, f1.getNumerator());
		Assertions.assertEquals(5, f1.getDenominator());
	}

	/**
	 * @see {@link Fraction#toPercentString(int)}
	 */
	@Test
	@Verifies(value = "should return a percentage to the correct precision", method = "toPercentString(int)")
	public void toPercentString_shouldReturnAPercentageToTheCorrectPrecision()throws Exception {
		Fraction f1 = new Fraction(32, 62);
		Assertions.assertEquals("51.6%", f1.toPercentString(1));
	}

	/**
	 * @see {@link Fraction#toString()}
	 */
	@Test
	@Verifies(value = "should return a string representation of the fraction", method = "toString()")
	public void toString_shouldReturnAStringRepresentationOfTheFraction() throws Exception {
		Fraction f1 = new Fraction(32, 62);
		Assertions.assertEquals("51.6% (32 / 62)", f1.toString());
	}


	/**
	 * @see {@link Fraction#toString()}
	 */
	@Test
	@Verifies(value = "should allow representation of fractions with 0 denominators", method = "toString()")
	public void toString_shouldAllowRepresentationOfFractionsWith0Denominators() throws Exception {
		Fraction f = new Fraction(5,0);
		Assertions.assertEquals("N/A (5 / 0)", f.toString());
	}
}