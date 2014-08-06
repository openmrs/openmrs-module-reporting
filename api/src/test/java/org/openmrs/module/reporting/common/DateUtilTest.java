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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Testing the cohort definition persister.  
 */
public class DateUtilTest extends BaseModuleContextSensitiveTest {
	
	protected Log log = LogFactory.getLog(this.getClass());

	protected void testMessage(String expected, String actual) {
		StringBuilder expectedMessage = new StringBuilder();
		for (String s : expected.split(" ")) {
			expectedMessage.append(expectedMessage.length() == 0 ? "" : " ").append(MessageUtil.translate(s, s));
		}
		Assert.assertEquals(expectedMessage.toString(), actual);
	}

	@Test
	public void shouldReturnInTheFuture() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.SECOND, +1);		
		testMessage("reporting.dateUtil.inTheFuture", DateUtil.getTimespan(now, calendar.getTime()));
	}
	
	@Test
	public void shouldReturnOneSecondAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.SECOND, -1);		
		testMessage("reporting.dateUtil.oneSecond reporting.dateUtil.ago", DateUtil.getTimespan(now, calendar.getTime()));
	}

	
	@Test
	public void shouldReturnThirtySecondsAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.SECOND, -30);		
		testMessage("30 reporting.dateUtil.seconds reporting.dateUtil.ago", DateUtil.getTimespan(now, calendar.getTime()));
	}

	
	@Test
	public void shouldReturnAnHourAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.MINUTE, -40);		
		testMessage("40 reporting.dateUtil.minutes reporting.dateUtil.ago", DateUtil.getTimespan(now, calendar.getTime()));
	}
	
	@Test
	public void shouldReturnOneHourAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.MINUTE, -65);		
		testMessage("reporting.dateUtil.anHour reporting.dateUtil.ago", DateUtil.getTimespan(now, calendar.getTime()));
	}
	
	@Test
	public void shouldReturnSixHoursAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.HOUR, -6);		
		testMessage("6 reporting.dateUtil.hours reporting.dateUtil.ago", DateUtil.getTimespan(now, calendar.getTime()));
	}

	
	@Test
	public void shouldReturnYesterday() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		testMessage("reporting.dateUtil.yesterday", DateUtil.getTimespan(now, calendar.getTime()));
	}

	@Test
	public void shouldReturnTenDaysAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.DAY_OF_MONTH, -10);
		testMessage("10 reporting.dateUtil.days reporting.dateUtil.ago", DateUtil.getTimespan(now, calendar.getTime()));
	}
	
	@Test
	public void shouldReturnOneMonthAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.MONTH, -1);
		testMessage("reporting.dateUtil.oneMonth reporting.dateUtil.ago", DateUtil.getTimespan(now, calendar.getTime()));
	}

	@Test
	public void shouldReturnFiveMonthsAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.MONTH, -5);
		testMessage("5 reporting.dateUtil.months reporting.dateUtil.ago", DateUtil.getTimespan(now, calendar.getTime()));
	}	
	@Test
	public void shouldReturnOneYearAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.YEAR, -1);
		testMessage("reporting.dateUtil.oneYear reporting.dateUtil.ago", DateUtil.getTimespan(now, calendar.getTime()));
	}
	
	@Test
	public void shouldReturnTenYearsAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.YEAR, -10);
		testMessage("10 reporting.dateUtil.years reporting.dateUtil.ago", DateUtil.getTimespan(now, calendar.getTime()));
	}

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
	    
	    testMessage("reporting.dateUtil.oneMonth reporting.dateUtil.ago", DateUtil.getTimespan(mar25, feb25));
	    testMessage("reporting.dateUtil.oneMonth reporting.dateUtil.ago", DateUtil.getTimespan(apr25, mar25));
	    testMessage("reporting.dateUtil.oneMonth reporting.dateUtil.ago", DateUtil.getTimespan(nov25, oct25));
	    testMessage("reporting.dateUtil.oneMonth reporting.dateUtil.ago", DateUtil.getTimespan(dec25, nov25));
    }

    @Test
    @Verifies(value = "should say one month ago even though february is short", method = "getTimespan(Date,Date,null)")
    public void getTimespan_shouldSayOneMonthAgoEvenThoughFebruaryIsShort() throws Exception {
    	testMessage("reporting.dateUtil.oneMonth reporting.dateUtil.ago", DateUtil.getTimespan(DateUtil.getDateTime(2009, 3, 15), DateUtil.getDateTime(2009, 2, 15)));
    }

    @Test
    public void testParseYmdhms() throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

        assertThat(df.format(DateUtil.parseYmdhms("2008-08-18 14:09:05.1")), is("2008-08-18 14:09:05.1"));
        assertThat(df.format(DateUtil.parseYmdhms("2008-08-18 14:09:05")), is("2008-08-18 14:09:05.0"));
        assertThat(df.format(DateUtil.parseYmdhms("2008-08-18")), is("2008-08-18 00:00:00.0"));
    }

}