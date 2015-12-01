package org.openmrs.module.reporting.common;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.test.Verifies;

/**
 * Tests for the DateRange class
 */
public class DateRangeTest {
	
	/**
	 * @see {@link DateRange#format(DateRange,String,String)}
	 */
	@Test
	@Verifies(value = "should return the passed date range formatted in interval notation", method = "format(DateRange,String,String)")
	public void format_shouldReturnThePassedDateRangeFormattedInIntervalNotation() throws Exception {
		DateRange dr1 = new DateRange(DateUtil.getDateTime(2007, 10, 1), true, DateUtil.getDateTime(2008, 11, 20), true);
		Assert.assertEquals("[2007-10-01,2008-11-20]", DateRange.format(dr1, "yyyy-MM-dd", "*"));
		
		DateRange dr2 = new DateRange(DateUtil.getDateTime(2007, 10, 1), false, null, false);
		Assert.assertEquals("(10/01/2007,*)", DateRange.format(dr2, "MM/dd/yyyy", "*"));
	}

	/**
	 * @see {@link DateRange#isAfter(DateRange,Date)}
	 */
	@Test
	@Verifies(value = "should return true if the passed date is after the date range", method = "isAfter(DateRange,Date)")
	public void isAfter_shouldReturnTrueIfThePassedDateIsAfterTheDateRange() throws Exception {
		DateRange dr = DateRange.parse("[2007-01-01,2008-01-01)", "yyyy-MM-dd", "*");
		Assert.assertTrue(DateRange.isAfter(dr, DateUtil.getDateTime(2008, 1, 1)));
		Assert.assertTrue(DateRange.isAfter(dr, DateUtil.getDateTime(2008, 1, 2)));
	}

	/**
	 * @see {@link DateRange#isAfter(DateRange,Date)}
	 */
	@Test
	@Verifies(value = "should return false if the passed date is not after the passed date range", method = "isAfter(DateRange,Date)")
	public void isAfter_shouldReturnFalseIfThePassedDateIsNotAfterThePassedDateRange() throws Exception {
		DateRange dr = DateRange.parse("[2007-01-01,2008-01-01)", "yyyy-MM-dd", "*");
		Assert.assertFalse(DateRange.isAfter(dr, DateUtil.getDateTime(2007, 12, 31)));
		Assert.assertFalse(DateRange.isAfter(dr, DateUtil.getDateTime(2007, 1, 1)));
		Assert.assertFalse(DateRange.isAfter(dr, DateUtil.getDateTime(2006, 1, 1)));
	}

	/**
	 * @see {@link DateRange#isBefore(DateRange,Date)}
	 */
	@Test
	@Verifies(value = "should return true if the passed date is before the date range", method = "isBefore(DateRange,Date)")
	public void isBefore_shouldReturnTrueIfThePassedDateIsBeforeTheDateRange() throws Exception {
		DateRange dr = DateRange.parse("(2007-01-01,2008-01-01)", "yyyy-MM-dd", "*");
		Assert.assertTrue(DateRange.isBefore(dr, DateUtil.getDateTime(2007, 1, 1)));
		Assert.assertTrue(DateRange.isBefore(dr, DateUtil.getDateTime(2006, 12, 31)));
	}

	/**
	 * @see {@link DateRange#isBefore(DateRange,Date)}
	 */
	@Test
	@Verifies(value = "should return false if the passed date is not before the passed date range", method = "isBefore(DateRange,Date)")
	public void isBefore_shouldReturnFalseIfThePassedDateIsNotBeforeThePassedDateRange() throws Exception {
		DateRange dr = DateRange.parse("[2007-01-01,2008-01-01)", "yyyy-MM-dd", "*");
		Assert.assertFalse(DateRange.isBefore(dr, DateUtil.getDateTime(2007, 1, 1)));
		Assert.assertFalse(DateRange.isBefore(dr, DateUtil.getDateTime(2007, 6, 1)));
		Assert.assertFalse(DateRange.isBefore(dr, DateUtil.getDateTime(2009, 1, 1)));
	}

	/**
	 * @see {@link DateRange#isWithin(DateRange,Date)}
	 */
	@Test
	@Verifies(value = "should return false if the passed date is before the date range", method = "isWithin(DateRange,Date)")
	public void isWithin_shouldReturnFalseIfThePassedDateIsBeforeTheDateRange() throws Exception {
		DateRange dr = DateRange.parse("(2007-01-01,2008-01-01)", "yyyy-MM-dd", "*");
		Assert.assertFalse(DateRange.isWithin(dr, DateUtil.getDateTime(2007, 1, 1)));
		Assert.assertFalse(DateRange.isWithin(dr, DateUtil.getDateTime(2006, 1, 1)));
	}

	/**
	 * @see {@link DateRange#isWithin(DateRange,Date)}
	 */
	@Test
	@Verifies(value = "should return true if the passed date is within the passed date range", method = "isWithin(DateRange,Date)")
	public void isWithin_shouldReturnTrueIfThePassedDateIsWithinThePassedDateRange() throws Exception {
		DateRange dr = DateRange.parse("[2007-01-01,2008-01-01]", "yyyy-MM-dd", "*");
		Assert.assertTrue(DateRange.isWithin(dr, DateUtil.getDateTime(2007, 1, 1)));
		Assert.assertTrue(DateRange.isWithin(dr, DateUtil.getDateTime(2007, 6, 1)));
		Assert.assertTrue(DateRange.isWithin(dr, DateUtil.getDateTime(2008, 1, 1)));
	}

	/**
	 * @see {@link DateRange#isWithin(DateRange,Date)}
	 */
	@Test
	@Verifies(value = "should return false if the passed date is after the passed date range", method = "isWithin(DateRange,Date)")
	public void isWithin_shouldReturnFalseIfThePassedDateIsAfterThePassedDateRange() throws Exception {
		DateRange dr = DateRange.parse("(2007-01-01,2008-01-01)", "yyyy-MM-dd", "*");
		Assert.assertFalse(DateRange.isWithin(dr, DateUtil.getDateTime(2008, 1, 1)));
		Assert.assertFalse(DateRange.isWithin(dr, DateUtil.getDateTime(2009, 1, 1)));
	}

	/**
	 * @see {@link DateRange#parse(String,String,String)}
	 */
	@Test
	@Verifies(value = "should return a new DateRange parsed from interval notation", method = "parse(String,String,String)")
	public void parse_shouldReturnANewDateRangeParsedFromIntervalNotation() throws Exception {
		DateRange dr1 = DateRange.parse("(2007-01-01,2008-01-01)", "yyyy-MM-dd", "*");
		Assert.assertFalse(dr1.isInclusiveOfStart());
		Assert.assertFalse(dr1.isInclusiveOfEnd());
		Assert.assertEquals(DateUtil.getDateTime(2007, 1, 1), dr1.getStartDate());
		Assert.assertEquals(DateUtil.getDateTime(2008, 1, 1), dr1.getEndDate());
		DateRange dr2 = DateRange.parse("(01/01/2007,*]", "MM/dd/yyyy", "*");
		Assert.assertFalse(dr2.isInclusiveOfStart());
		Assert.assertTrue(dr2.isInclusiveOfEnd());
		Assert.assertEquals(DateUtil.getDateTime(2007, 1, 1), dr2.getStartDate());
		Assert.assertNull(dr2.getEndDate());
		DateRange dr3 = DateRange.parse("[*,12/2007]", "MM/yyyy", "*");
		Assert.assertTrue(dr3.isInclusiveOfStart());
		Assert.assertTrue(dr3.isInclusiveOfEnd());
		Assert.assertNull(dr3.getStartDate());
		Assert.assertEquals(DateUtil.getDateTime(2007, 12, 1), dr3.getEndDate());
	}
}