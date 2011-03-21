package org.openmrs.module.reporting.common;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.test.Verifies;

/**
 * Tests for the DelimitedKeyComparator class
 */
public class DelimitedKeyComparatorTest {
	
	/**
	 * @see {@link DelimitedKeyComparator#compare(String,String)}
	 */
	@Test
	@Verifies(value = "should compare two strings", method = "compare(String,String)")
	public void format_shouldCompareTwoStrings() throws Exception {
		
		DelimitedKeyComparator c =  new DelimitedKeyComparator();
		
		Assert.assertEquals(1, "2".compareTo("10"));
		Assert.assertEquals(-1, c.compare("2", "10"));
		
		Assert.assertEquals(1, "2".compareTo("1"));
		Assert.assertEquals(1, c.compare("2", "1"));

		Assert.assertEquals(1, "2.B".compareTo("10.A"));
		Assert.assertEquals(-1, c.compare("2.B", "10.A"));
		
		Assert.assertEquals(1, "2-B".compareTo("10-A"));
		Assert.assertEquals(-1, c.compare("2-B", "10-A"));
		
		Assert.assertEquals(1, "2_B".compareTo("10_A"));
		Assert.assertEquals(-1, c.compare("2_B", "10_A"));
	}
}
