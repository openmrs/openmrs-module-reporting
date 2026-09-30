/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.indicator.aggregation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.test.Verifies;

/**
 * Tests for classes in the aggregation package
 */
public class AggregationTest {
	
	/**
	 * @see {@link MeanAggregator#compute(Collection)}
	 */
	@Test(expected = RuntimeException.class)
	@Verifies(value = "should calculate mean with null", method = "compute(Collection)")
	public void shouldCalulateMeanWithNull() {
		MeanAggregator ma = new MeanAggregator();
		ma.compute(null);
	}
	
	/**
	 * @see {@link MeanAggregator#compute(Collection)}
	 */
	@Test
	@Verifies(value = "should calculate mean with empty set", method = "compute(Collection)")
	public void shouldCalulateMeanWithEmptySet() {
		MeanAggregator ma = new MeanAggregator();
		Collection<Number> c = new LinkedHashSet<Number>();
		Assert.assertTrue(ma.compute(c).equals(Double.valueOf(0) / 0));
	}
	
	/**
	 * @see {@link MeanAggregator#compute(Collection)}
	 */
	@Test
	@Verifies(value = "should calculate mean with single val", method = "compute(Collection)")
	public void shouldCalulateMeanWithSingleVal() {
		MeanAggregator ma = new MeanAggregator();
		Collection<Number> c = new LinkedHashSet<Number>();
		c.add(1);
		Assert.assertTrue(ma.compute(c).equals(Double.valueOf(1)));
	}
	
	/**
	 * @see {@link MeanAggregator#compute(Collection)}
	 */
	@Test
	public void shouldCalulateMean() {
		MeanAggregator ma = new MeanAggregator();
		Collection<Number> c = new LinkedHashSet<Number>();
		c.add(4);
		c.add(1);
		c.add(2);
		c.add(5);
		Assert.assertTrue(ma.compute(c).equals(Double.valueOf(3)));
	}
	
	/**
	 * @see {@link MedianAggregator#compute(Collection)}
	 */
	@Test(expected = RuntimeException.class)
	@Verifies(value = "should calculate median with null", method = "compute(Collection)")
	public void shouldCalulateMedianWithNull() {
		MedianAggregator ma = new MedianAggregator();
		ma.compute(null);
	}
	
	/**
	 * @see {@link MedianAggregator#compute(Collection)}
	 */
	@Test
	@Verifies(value = "should calculate median of empty set", method = "compute(Collection)")
	public void shouldCalulateMedianOfEmptySet() {
		Collection<Number> c = new LinkedHashSet<Number>();
		MedianAggregator ma = new MedianAggregator();
		Assert.assertTrue(ma.compute(c).equals(Double.valueOf(0) / 0));
	}
	
	/**
	 * @see {@link MedianAggregator#compute(Collection)}
	 */
	@Test
	@Verifies(value = "should calculate median with single entry", method = "compute(Collection)")
	public void shouldCalulateMedianWithSingleEntry() {
		Collection<Number> c = new LinkedHashSet<Number>();
		c.add(1);
		MedianAggregator ma = new MedianAggregator();
		Assert.assertTrue(ma.compute(c).equals(1));
	}
	
	/**
	 * @see {@link MedianAggregator#compute(Collection)}
	 */
	@Test
	@Verifies(value = "should calculate median with odd entries", method = "compute(Collection)")
	public void shouldCalulateMedianWithOddEntries() {
		Collection<Number> c = new LinkedHashSet<Number>();
		c.add(0.2);
		c.add(5);
		c.add(1);
		MedianAggregator ma = new MedianAggregator();
		Assert.assertTrue(ma.compute(c).equals(1));
	}
	
	/**
	 * @see {@link MedianAggregator#compute(Collection)}
	 */
	@Test
	@Verifies(value = "should calculate median with even entries", method = "compute(Collection)")
	public void shouldCalulateMedianWithEvenEntries() {
		Collection<Number> c = new LinkedHashSet<Number>();
		c.add(0.2);
		c.add(1);
		c.add(5);
		c.add(2);
		MedianAggregator ma = new MedianAggregator();
		Assert.assertTrue(ma.compute(c).equals(1.5));
	}
	
	/**
	 * @see {@link ModeAggregator#compute(Collection)}
	 */
	@Test(expected = RuntimeException.class)
	@Verifies(value = "ModeAggregator should throw exception with null list", method = "compute(Collection)")
	public void modeAggregator_shouldThrowExceptionWithNullList() {
		ModeAggregator ma = new ModeAggregator();
		ma.compute(null);
	}
	
	/**
	 * @see {@link ModeAggregator#compute(Collection)}
	 */
	@Test(expected = RuntimeException.class)
	@Verifies(value = "ModeAggregator should throw exception with empty list", method = "compute(Collection)")
	public void modeAggregator_shouldThrowExceptionWithEmptyList() {
		ModeAggregator ma = new ModeAggregator();
		ma.compute(Collections.<Number>emptyList());
	}
	
	/**
	 * @see {@link MeanAggregator#compute(Collection)}
	 */
	@Test
	@Verifies(value = "ModeAggregator should calculate mode", method = "compute(Collection)")
	public void modeAggregator_shouldCalculateMode() {
		ModeAggregator ma = new ModeAggregator();
		Collection<Number> c = new ArrayList<Number>();
		c.add(10);
		c.add(10);
		c.add(4);
		c.add(10);
		c.add(5);
		c.add(3);
		Assert.assertTrue(ma.compute(c).equals(10));
	}
}
