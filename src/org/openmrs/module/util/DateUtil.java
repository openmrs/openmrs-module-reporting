package org.openmrs.module.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A utility class for common date operations
 */
public class DateUtil {

	protected static Log log = LogFactory.getLog(DateUtil.class);

	// Added for readability (see below)
	final static int MILLISECOND = 1000;
	final static int SECOND = 1;
	final static int MINUTE = 60 * SECOND;
	final static int HOUR = 60 * MINUTE;
	final static int DAY = 24 * HOUR;
	final static int MONTH = 30 * DAY;

	/**
	 * Returns the passed date, at the specified time
	 */
	public static Date getDateTime(int year, int mon, int day, int hr, int min,
			int sec, int ms) {
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
	public static Date getDateTime(Date d, int hour, int minute, int second,
			int millisecond) {
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
	 * Returns a date that is the very end of the last of the month, given the
	 * passed date and adjustment
	 */
	public static Date getEndOfMonth(Date d, int monthAdjustment) {
		Calendar c = Calendar.getInstance();
		c.setTime(getEndOfDay(d));
		c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
		c.add(Calendar.MONTH, monthAdjustment);
		return c.getTime();
	}

	
	/**
	 * Get a string that represents the time span that has elapsed 
	 * between now and the given date.
	 * 
	 * @param then
	 * @return	a string that represents the timespan between two dates
	 */
	public static String getTimespan(Date then) { 
		return getTimespan(new Date(), then);
	}
	
	/**
	 * Returns a string that represents the time span that has elapsed 
	 * between the given dates (e.g. one hour ago, 5 weeks ago).  
	 * 
	 * @param now
	 * @param then
	 * @return	a string that represents the timespan between two dates
	 */
	public static String getTimespan(Date now, Date then) {

		if (now == null || then == null) { 
			return "";
		}
		
		// Time span between two dates (in seconds)
		long delta = (now.getTime() - then.getTime()) / MILLISECOND;

		if (delta < 0) { 
			return "in the future";
		}
		if (delta < 1 * MINUTE) {
			return (delta / SECOND) == 1 ? "one second ago" : (delta / SECOND) + " seconds ago";
		}
		if (delta < 2 * MINUTE) {
			return "a minute ago";
		}
		if (delta < 45 * MINUTE) {
			return (delta / MINUTE) + " minutes ago";
		}
		if (delta < 90 * MINUTE) {
			return "an hour ago";
		}
		if (delta < 24 * HOUR) {
			return (delta / HOUR) + " hours ago";
		}
		if (delta < 48 * HOUR) {
			return "yesterday";
		}
		if (delta < 30 * DAY) {
			return (delta / DAY) + " days ago";
		}
		if (delta < 12 * MONTH) {
			int months = (int) (delta / (DAY * 30));
			return months <= 1 ? "one month ago" : months + " months ago";
		} else {
			int years = (int) (delta / (DAY * 365));
			return years <= 1 ? "one year ago" : years + " years ago";
		}

	}

	/**
	 * Utility method to format a date in the given format
	 * @param d the date to format
	 * @return a String representing the date in the passed format
	 */
	public static String formatDate(Date d, String format) {
		DateFormat df = new SimpleDateFormat(format);
		return df.format(d);
	}
	
}
