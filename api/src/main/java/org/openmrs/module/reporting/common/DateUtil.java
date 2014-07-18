package org.openmrs.module.reporting.common;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A utility class for common date operations
 */
public class DateUtil {
		
	protected static Log log = LogFactory.getLog(DateUtil.class);
	
	// Common periods of time
	final static int DAILY = 0;
	final static int WEEKLY = 1;
	final static int MONTHLY = 2;
	final static int QUARTERLY = 3;
	final static int ANNUALLY = 4;
	
	
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
	public static Date getDateTime(int year, int mon, int day, int hr, int min, int sec, int ms) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, mon-1);
		c.set(Calendar.DATE, day);
		c.set(Calendar.HOUR_OF_DAY, hr);
		c.set(Calendar.MINUTE, min);
		c.set(Calendar.SECOND, sec);
		c.set(Calendar.MILLISECOND, ms);
		return c.getTime();
	}
	
	/**
	 * Returns the passed date, at midnight
	 */
	public static Date getDateTime(int year, int mon, int day) {
		return getDateTime(year, mon, day, 0, 0, 0, 0);
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
	 * Returns the last second of the day if the hour/minute/second/ms of the passed date are all zero,
	 * otherwise just returns the passed date
	 */
	public static Date getEndOfDayIfTimeExcluded(Date d) {
		if (d != null) {
			Date startOfDay = getStartOfDay(d);
			if (d.compareTo(startOfDay) == 0) {
				return getEndOfDay(d);
			}
		}
		return d;
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
	
	public static String getTimespan(Date now, Date then) {
		return getTimespan(now, then, true);
	}
	
	/**
	 * Returns a string that represents the time span that has elapsed 
	 * between the given dates (e.g. one hour ago, 5 weeks ago).  
	 * 
	 * @param now
	 * @param then
	 * @return	a string that represents the timespan between two dates
	 * 
	 * @should correctly handle daylight savings time
	 * @should say one month ago even though february is short
	 */
	public static String getTimespan(Date now, Date then, boolean showAgoWord) {
		MessageSourceService mss = Context.getMessageSourceService();

		if (now == null || then == null) { 
			return "";
		}
		
		// Time span between two dates (in seconds)
		long delta = (now.getTime() - then.getTime()) / MILLISECOND;
		
		// do some adjustments for that fact that (1) February is short, and there's a <30 check below, and (2) Daylight Savings Time exists 
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		long dstOffsetDifference = cal.get(Calendar.DST_OFFSET);
		cal.setTime(then);
		boolean wasFebruary = cal.get(Calendar.MONTH) == Calendar.FEBRUARY;
		dstOffsetDifference -= cal.get(Calendar.DST_OFFSET);
		delta += dstOffsetDifference / MILLISECOND;
		
		String suffix = showAgoWord ? " " + mss.getMessage("reporting.dateUtil.ago") : "";

		if (delta < 0) { 
			return mss.getMessage("reporting.dateUtil.inTheFuture");
		}
		if (delta < 1 * MINUTE) {
			return (delta / SECOND) == 1 ? mss.getMessage("reporting.dateUtil.oneSecond") + suffix : (delta / SECOND) + " " + mss.getMessage("reporting.dateUtil.seconds") + suffix;
		}
		if (delta < 2 * MINUTE) {
			return mss.getMessage("reporting.dateUtil.aMinute") + suffix;
		}
		if (delta < 45 * MINUTE) {
			return (delta / MINUTE) +  " " + mss.getMessage("reporting.dateUtil.minutes") + suffix;
		}
		if (delta < 90 * MINUTE) {
			return mss.getMessage("reporting.dateUtil.anHour") + suffix;
		}
		if (delta < 24 * HOUR) {
			return (delta / HOUR) +  " " + mss.getMessage("reporting.dateUtil.hours") + suffix;
		}
		if (delta < 48 * HOUR && showAgoWord) {
			return mss.getMessage("reporting.dateUtil.yesterday");
		}
		if ((delta < 28 * DAY) || (delta < 30 * DAY && !wasFebruary)) {
			return (delta / DAY) + " " + mss.getMessage("reporting.dateUtil.days") + suffix;
		}
		if (delta < 12 * MONTH) {
			int months = (int) (delta / (DAY * 30));
			return months <= 1 ? mss.getMessage("reporting.dateUtil.oneMonth") + suffix : months + " " + mss.getMessage("reporting.dateUtil.months") + suffix;
		} else {
			int years = (int) (delta / (DAY * 365));
			return years <= 1 ? mss.getMessage("reporting.dateUtil.oneYear") + suffix : years + " " + mss.getMessage("reporting.dateUtil.years") + suffix;
		}
	}

	/**
	 * Utility method to format a date in the given format
	 * @param d the date to format
	 * @return a String representing the date in the passed format
	 */
	public static String formatDate(Date d, String format) {
		return formatDate(d, format, "");
	}
	
	/**
	 * Utility method to parse a date in the given format
	 * @param s the string to parse
	 * @param format the date format
	 * @return a Date representing the date in the passed format
	 */
	public static Date parseDate(String s, String format) {
		DateFormat df = new SimpleDateFormat(format);
		try {
			return df.parse(s);
		}
		catch (Exception e) {
			throw new RuntimeException("Cannot parse " + s + " into a date using format " + format);
		}
	}

    /**
     * @param date
     * @return date, parsed as "yyyy-MM-dd"
     */
    public static Date parseYmd(String date) {
        return parseDate(date, "yyyy-MM-dd");
    }

    /**
     * @param date like "2008-08-18 14:09:05.0" or "2008-08-18 14:09:05" or "2008-08-18"
     * @return
     */
    public static Date parseYmdhms(String date) {
        try {
            return DateUtils.parseDate(date, "yyyy-MM-dd HH:mm:ss.S", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd");
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date", e);
        }
    }

    /**
	 * Utility method to format a date in the given format
	 * @param d the date to format
	 * @param format the DateFormat to use
	 * @param defaultIfNull the value to return if the passed date is null
	 * @return a String representing the date in the passed format
	 */
	public static String formatDate(Date d, String format, String defaultIfNull) {
		if (d != null) {
			DateFormat df = new SimpleDateFormat(format);
			return df.format(d);
		}
		return defaultIfNull;
	}
	
	
	/**
	 * Utility method to determine the number of hours between two dates (rounding down)
	 * 
	 * @param a
	 * @param b
	 * @return the number of hours between a and b
	 */
	public static int getHoursBetween(Date a, Date b) {
		long diff = (b.getTime() - a.getTime()) / MILLISECOND;
		if (diff < 0)
			diff = -diff;
		diff /= HOUR;
		return (int) diff;
	}

	
	/**
	 * 
	 * @param currentDate
	 * @param period
	 * @return
	 */
	public static Date getStartOfPeriod(Date currentDate, int period) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		switch (period) { 		
			case DAILY: 
				return getStartOfDay(currentDate);
			case WEEKLY:								
				return getStartOfWeek(currentDate);
			case MONTHLY: 
				return getStartOfMonth(currentDate);
			case QUARTERLY:				
				return getStartOfQuarter(currentDate);
			case ANNUALLY:
				return getStartOfYear(currentDate);
		}
		return currentDate;
		
	}
	
	
	public static Date getStartOfWeek(Date currentDate) { 		
		return getStartOfCalendarPeriod(currentDate, Calendar.DAY_OF_WEEK);
	}
		
	public static Date getStartOfMonth(Date currentDate) { 
		return getStartOfCalendarPeriod(currentDate, Calendar.DAY_OF_MONTH);
	}

	public static Date getStartOfQuarter(Date currentDate) { 
		throw new APIException("Not implemented yet");
	}	

	public static Date getStartOfYear(Date currentDate) { 
		return getStartOfCalendarPeriod(currentDate, Calendar.DAY_OF_YEAR);
	}
	
	public static Date getStartOfCalendarPeriod(Date currentDate, int field) { 
		if (currentDate == null) 
			throw new APIException("Please specify a date");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		calendar.set(field, calendar.getActualMinimum(field));
		return calendar.getTime();
	}
	
	/**
	 * 
	 * @param currentDate
	 * @param period
	 * @return
	 */
	public static Date getEndOfPeriod(Date currentDate, int period) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		switch (period) { 		
			case DAILY: 
				return getEndOfDay(currentDate);
			case WEEKLY:								
				return getEndOfWeek(currentDate);
			case MONTHLY: 
				return getEndOfMonth(currentDate);
			case QUARTERLY:				
				return getEndOfQuarter(currentDate);
			case ANNUALLY:
				return getEndOfYear(currentDate);
		}
		return currentDate;
		
	}

	public static Date adjustDate(Date dateToAdjust, int numToAdjust, int fieldToAdjust) {
		Calendar c = Calendar.getInstance();
		c.setTime(dateToAdjust);
		c.add(fieldToAdjust, numToAdjust);
		return c.getTime();
	}

	public static Date adjustDate(Date dateToAdjust, int numToAdjust, DurationUnit numUnits) {
		return adjustDate(dateToAdjust, numToAdjust * numUnits.getFieldQuantity(), numUnits.getCalendarField());
	}
	
	public static Date getEndOfWeek(Date currentDate) { 		
		return getEndOfCalendarPeriod(currentDate, Calendar.DAY_OF_WEEK);
	}
		
	public static Date getEndOfMonth(Date currentDate) { 
		return getEndOfCalendarPeriod(currentDate, Calendar.DAY_OF_MONTH);
	}

	public static Date getEndOfQuarter(Date currentDate) { 
		throw new APIException("Not implemented yet");
	}	

	public static Date getEndOfYear(Date currentDate) { 
		return getEndOfCalendarPeriod(currentDate, Calendar.DAY_OF_YEAR);
	}
	
	public static Date getEndOfCalendarPeriod(Date currentDate, int field) { 
		if (currentDate == null) 
			throw new APIException("Please specify a date");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		calendar.set(field, calendar.getActualMaximum(field));
		return calendar.getTime();
	}	
	
	public static Map<String, Date> getPeriodDates(Integer year, Integer quarter, Integer month) {
		
		// Validate input and construct start and end months
		int startMonth = 1;
		int endMonth = 12;
		
		
		// if the year is null, we don't have start and end dates, want to query from the beginning of time until today
		if (year == null && month == null && quarter == null) {
			Map<String, Date> periodDates = new HashMap<String, Date>();;
			periodDates.put("startDate", null);
			periodDates.put("endDate", new Date());
			
			return periodDates;
		}
		
		if (year == null || year < 1900 || year > 2100) {
			throw new IllegalArgumentException("Please enter a valid year");
		}
		
		if (quarter != null) {
			if (quarter < 1 || quarter > 4) {
				throw new IllegalArgumentException("Please enter a valid quarter (1-4)");
			}
			if (month != null) {
				throw new IllegalArgumentException("Please enter either a quarter or a month");
			}
			endMonth = quarter*3;
			startMonth = endMonth-2;
		}
		if (month != null) {
			if (month < 1 || month > 12) {
				throw new IllegalArgumentException("Please enter a valid month (1-12)");
			}
			startMonth = month;
			endMonth = month;
		}
		
		Map<String, Date> periodDates = new HashMap<String, Date>();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, startMonth-1);
		c.set(Calendar.DATE, 1);
		periodDates.put("startDate", c.getTime());
		c.set(Calendar.MONTH, endMonth-1);
		c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
		periodDates.put("endDate", c.getTime());
		
		return periodDates;
	}
	
	/**
	 * @return true if two dates match after being formatted
	 */
	public static boolean datesMatchWithFormat(Date d1, Date d2, String format) {
		if (d1 != null && d2 != null) {
			if (format != null) {
				DateFormat df = new SimpleDateFormat(format);
				return df.format(d1).equals(df.format(d2));
			}
			else {
				return d1.equals(d2);
			}
		}
		return false;
	}

	/**
	 * @return the full number of months between the two passed dates
	 */
	public static int monthsBetween(Date d1, Date d2) {
		int count = 0;
		Calendar c = Calendar.getInstance();
		c.setTime((d1.before(d2) ? d1 : d2));
		c.add(Calendar.MONTH, 1);
		Date compareDate = (d1.before(d2) ? d2 : d1);
		while (c.getTime().compareTo(compareDate) <= 0) {
			count++;
			c.add(Calendar.MONTH, 1);
		}
		return count;
	}

	/**
	 * @return a formatted display of time elapsed between fromTime and toTime including hours, minutes, seconds, and milliseconds
	 */
	public static String getTimeElapsed(long duration) {
		StringBuilder sb = new StringBuilder();
		int hrs = (int) (duration / (1000*60*60));
		if (hrs > 0) {
			sb.append(hrs + "(h) ");
			duration = duration - (hrs * 1000*60*60);
		}
		int mins = (int) (duration / (1000*60));
		if (mins > 0) {
			sb.append(mins + "(m) ");
			duration = duration - (mins * 1000*60);
		}
		int secs = (int) (duration / 1000);
		if (secs > 0) {
			sb.append(secs + "(s) ");
			duration = duration - (secs * 1000);
		}
		sb.append(duration + "(ms)");
		return sb.toString();
	}
}
