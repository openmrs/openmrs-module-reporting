package org.openmrs.module.util;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A utility class for common date operations
 */
public class DateUtil {
	
	protected static Log log = LogFactory.getLog(DateUtil.class);
	
	/**
	 * Returns the passed date, at the specified time
	 */
	public static Date getDateTime(int year, int mon, int day, int hr, int min, int sec, int ms) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, mon);
		c.set(Calendar.DATE, day);
		c.set(Calendar.HOUR_OF_DAY, hr);
		c.set(Calendar.MINUTE, min);
		c.set(Calendar.SECOND, sec);
		c.set(Calendar.MILLISECOND, ms);
		return c.getTime();
	}
	
	/**
	 * Returns the passed date, at the specified time
	 */
	public static Date getDateTime(Date d, int hour, int minute, int second, int millisecond) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		c.set(Calendar.MILLISECOND, millisecond);
		return c.getTime();
	}
	
	/**
	 * Returns the last second of the day
	 */
	public static Date getEndOfDay(Date d) {
		return getDateTime(d, 23, 59, 59, 999);
	}
	
	/**
	 * Returns a date that represents the very beginning of the passed date
	 */
	public static Date getStartOfDay(Date d) {
		return getDateTime(d, 0, 0, 0, 0);
	}
	
	/**
	 * Returns a date that is the very beginning of the first of the month,
	 * given the passed date and adjustment
	 */
	public static Date getStartOfMonth(Date d, int monthAdjustment) {
		Calendar c = Calendar.getInstance();
		c.setTime(getStartOfDay(d));
		c.set(Calendar.DATE, 1);
		c.add(Calendar.MONTH, monthAdjustment);
		return c.getTime();
	}
	
	/**
	 * Returns a date that is the very end of the last of the month,
	 * given the passed date and adjustment
	 */
	public static Date getEndOfMonth(Date d, int monthAdjustment) {
		Calendar c = Calendar.getInstance();
		c.setTime(getEndOfDay(d));
		c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
		c.add(Calendar.MONTH, monthAdjustment);
		return c.getTime();
	}
}
