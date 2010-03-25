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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.openmrs.test.Verifies;

/**
 * Testing the cohort definition persister.  
 */
public class DateUtilTest {
	
	protected Log log = LogFactory.getLog(this.getClass());

	@Test
	public void shouldReturnInTheFuture() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.SECOND, +1);		
		Assert.assertEquals("(in the future?)", DateUtil.getTimespan(now, calendar.getTime()));
	}
	
	@Test
	public void shouldReturnOneSecondAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.SECOND, -1);		
		Assert.assertEquals("one second ago", DateUtil.getTimespan(now, calendar.getTime()));
	}

	
	@Test
	public void shouldReturnThirtySecondsAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.SECOND, -30);		
		Assert.assertEquals("30 seconds ago", DateUtil.getTimespan(now, calendar.getTime()));
	}

	
	@Test
	public void shouldReturnAnHourAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.MINUTE, -40);		
		Assert.assertEquals("40 minutes ago", DateUtil.getTimespan(now, calendar.getTime()));
	}
	
	@Test
	public void shouldReturnOneHourAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.MINUTE, -65);		
		Assert.assertEquals("an hour ago", DateUtil.getTimespan(now, calendar.getTime()));
	}
	
	@Test
	public void shouldReturnSixHoursAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.HOUR, -6);		
		Assert.assertEquals("6 hours ago", DateUtil.getTimespan(now, calendar.getTime()));
	}

	
	@Test
	public void shouldReturnYesterday() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		Assert.assertEquals("yesterday", DateUtil.getTimespan(now, calendar.getTime()));
	}

	@Test
	public void shouldReturnTenDaysAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.DAY_OF_MONTH, -10);
		Assert.assertEquals("10 days ago", DateUtil.getTimespan(now, calendar.getTime()));
	}
	
	@Test
	public void shouldReturnOneMonthAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.MONTH, -1);
		Assert.assertEquals("one month ago", DateUtil.getTimespan(now, calendar.getTime()));
	}

	@Test
	public void shouldReturnFiveMonthsAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.MONTH, -5);
		Assert.assertEquals("5 months ago", DateUtil.getTimespan(now, calendar.getTime()));
	}	
	@Test
	public void shouldReturnOneYearAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.YEAR, -1);
		Assert.assertEquals("one year ago", DateUtil.getTimespan(now, calendar.getTime()));
	}
	
	@Test
	public void shouldReturnTenYearsAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.YEAR, -10);
		Assert.assertEquals("10 years ago", DateUtil.getTimespan(now, calendar.getTime()));
	}

	/**
     * @see {@link DateUtil#getTimespan(Date,Date,null)}
     * 
     */
    @Test
    @Verifies(value = "should correctly handle daylight savings time", method = "getTimespan(Date,Date,null)")
    public void getTimespan_shouldCorrectlyHandleDaylightSavingsTime() throws Exception {
    	// USA has daylight saving time.
    	// in 2009 DST started March 8 and ended November 1
    	
	    Calendar cal = new GregorianCalendar(Locale.US);
	    cal.set(Calendar.YEAR, 2009);
	    cal.set(Calendar.DAY_OF_MONTH, 25);

	    cal.set(Calendar.MONTH, Calendar.FEBRUARY);
	    Date feb25 = cal.getTime();
	    
	    cal.set(Calendar.MONTH, Calendar.MARCH);
	    Date mar25 = cal.getTime();
	    
	    cal.set(Calendar.MONTH, Calendar.APRIL);
	    Date apr25 = cal.getTime();
	    
	    cal.set(Calendar.MONTH, Calendar.OCTOBER);
	    Date oct25 = cal.getTime();
	    
	    cal.set(Calendar.MONTH, Calendar.NOVEMBER);
	    Date nov25 = cal.getTime();
	    
	    cal.set(Calendar.MONTH, Calendar.DECEMBER);
	    Date dec25 = cal.getTime();
	    
	    Assert.assertEquals("one month ago", DateUtil.getTimespan(mar25, feb25));
	    Assert.assertEquals("one month ago", DateUtil.getTimespan(apr25, mar25));
	    Assert.assertEquals("one month ago", DateUtil.getTimespan(nov25, oct25));
	    Assert.assertEquals("one month ago", DateUtil.getTimespan(dec25, nov25));
    }

	/**
     * @see {@link DateUtil#getTimespan(Date,Date,null)}
     */
    @Test
    @Verifies(value = "should say one month ago even though february is short", method = "getTimespan(Date,Date,null)")
    public void getTimespan_shouldSayOneMonthAgoEvenThoughFebruaryIsShort() throws Exception {
    	Assert.assertEquals("one month ago", DateUtil.getTimespan(DateUtil.getDateTime(2009, 3, 15), DateUtil.getDateTime(2009, 2, 15)));
    }	
	
}