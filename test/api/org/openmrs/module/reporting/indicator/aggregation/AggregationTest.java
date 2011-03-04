package org.openmrs.module.reporting.indicator.aggregation;

import java.util.Collection;
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
    public void shouldCalulateMeanWithNull(){
        MeanAggregator ma = new MeanAggregator();
        ma.compute(null);
    }
    
	/**
	 * @see {@link MeanAggregator#compute(Collection)}
	 */
    @Test
    @Verifies(value = "should calculate mean with empty set", method = "compute(Collection)")
    public void shouldCalulateMeanWithEmptySet(){
        MeanAggregator ma = new MeanAggregator();
        Collection<Number> c = new LinkedHashSet<Number>();
        Assert.assertTrue(ma.compute(c).equals(Double.valueOf(0)/0));
    }
    
	/**
	 * @see {@link MeanAggregator#compute(Collection)}
	 */
    @Test
    @Verifies(value = "should calculate mean with single val", method = "compute(Collection)")
    public void shouldCalulateMeanWithSingleVal(){
        MeanAggregator ma = new MeanAggregator();
        Collection<Number> c = new LinkedHashSet<Number>();
        c.add(1);
        Assert.assertTrue(ma.compute(c).equals(Double.valueOf(1)));
    }
    
	/**
	 * @see {@link MeanAggregator#compute(Collection)}
	 */
    @Test
    public void shouldCalulateMean(){
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
    public void shouldCalulateMedianWithNull(){
        MedianAggregator ma = new MedianAggregator();
        ma.compute(null);
    }
    
	/**
	 * @see {@link MedianAggregator#compute(Collection)}
	 */
    @Test
    @Verifies(value = "should calculate median of empty set", method = "compute(Collection)")
    public void shouldCalulateMedianOfEmptySet(){
        Collection<Number> c = new LinkedHashSet<Number>();
        MedianAggregator ma = new MedianAggregator();
        Assert.assertTrue(ma.compute(c).equals(Double.valueOf(0)/0));
    }
    
	/**
	 * @see {@link MedianAggregator#compute(Collection)}
	 */
    @Test
    @Verifies(value = "should calculate median with single entry", method = "compute(Collection)")
    public void shouldCalulateMedianWithSingleEntry(){
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
    public void shouldCalulateMedianWithOddEntries(){
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
    public void shouldCalulateMedianWithEvenEntries(){
        Collection<Number> c = new LinkedHashSet<Number>();
        c.add(0.2);
        c.add(1);
        c.add(5);
        c.add(2);
        MedianAggregator ma = new MedianAggregator();
        Assert.assertTrue(ma.compute(c).equals(1.5));
    }
}
