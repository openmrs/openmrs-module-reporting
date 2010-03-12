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

import junit.framework.Assert;

import org.junit.Test;

/**
 * Testing the Fraction class
 */
public class FractionTest {

	@Test
	public void shouldComputeGCD() { 
		int n1 = 24;
		int n2 = 32;
		Assert.assertEquals(8, Fraction.gcd(n1, n2));
	}
	
	@Test
	public void shouldAllowZeroDenominator() { 
		Fraction f = new Fraction(5,0);
		Assert.assertEquals("5 / 0", f.toString());
	}
	
	@Test
	public void shouldReduceFraction() { 
		Fraction f1 = new Fraction(21, 35);
		Assert.assertEquals(21, f1.getNumerator());
		Assert.assertEquals(35, f1.getDenominator());
		f1 = f1.reduce();
		Assert.assertEquals(3, f1.getNumerator());
		Assert.assertEquals(5, f1.getDenominator());
	}
	
	@Test
	public void shouldCompareFractions() { 
		Fraction f1 = new Fraction(7, 8);
		Fraction f2 = new Fraction(3, 4);
		Fraction f3 = new Fraction(12, 16);
		
		Assert.assertFalse(f1.equals(f2));
		Assert.assertTrue(f2.equals(f3));
		Assert.assertTrue(f1.compareTo(f2) > 0);
		Assert.assertTrue(f2.compareTo(f3) == 0);
	}
	
	@Test
	public void shouldFormatFraction() { 
		Fraction f1 = new Fraction(32, 62);
		Assert.assertEquals("32 / 62", f1.toString());
	}
	
	@Test
	public void shouldFormatPercent() { 
		Fraction f1 = new Fraction(32, 62);
		Assert.assertEquals("51.6%", f1.toPercentString(1));
	}
}