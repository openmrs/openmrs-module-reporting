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
package org.openmrs.module.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.service.ReportService;
import org.openmrs.module.serialization.xstream.XStreamShortSerializer;
import org.openmrs.reporting.AbstractReportObject;
import org.openmrs.reporting.ReportObjectService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Testing the cohort definition persister.  
 */
public class DateUtilTest {

	// Logger
	private Log log = LogFactory.getLog(this.getClass());
		
	@Test
	public void shouldReturnOneSecondAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.SECOND, -1);		
		Assert.assertEquals("one second ago", DateUtil.getRelativeTime(now, calendar.getTime()));
	}

	
	@Test
	public void shouldReturnThirtySecondsAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.SECOND, -30);		
		Assert.assertEquals("30 seconds ago", DateUtil.getRelativeTime(now, calendar.getTime()));
	}

	
	@Test
	public void shouldReturnAnHourAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.MINUTE, -40);		
		Assert.assertEquals("40 minutes ago", DateUtil.getRelativeTime(now, calendar.getTime()));
	}
	
	@Test
	public void shouldReturnOneHourAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.MINUTE, -65);		
		Assert.assertEquals("an hour ago", DateUtil.getRelativeTime(now, calendar.getTime()));
	}
	
	@Test
	public void shouldReturnSixHoursAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.HOUR, -6);		
		Assert.assertEquals("6 hours ago", DateUtil.getRelativeTime(now, calendar.getTime()));
	}

	
	@Test
	public void shouldReturnYesterday() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		Assert.assertEquals("yesterday", DateUtil.getRelativeTime(now, calendar.getTime()));
	}

	@Test
	public void shouldReturnTenDaysAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.DAY_OF_MONTH, -10);
		Assert.assertEquals("10 days ago", DateUtil.getRelativeTime(now, calendar.getTime()));
	}
	
	@Test
	public void shouldReturnOneMonthAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.MONTH, -1);
		Assert.assertEquals("one month ago", DateUtil.getRelativeTime(now, calendar.getTime()));
	}

	@Test
	public void shouldReturnFiveMonthsAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.MONTH, -5);
		Assert.assertEquals("5 months ago", DateUtil.getRelativeTime(now, calendar.getTime()));
	}	
	@Test
	public void shouldReturnOneYearAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.YEAR, -1);
		Assert.assertEquals("one year ago", DateUtil.getRelativeTime(now, calendar.getTime()));
	}
	
	@Test
	public void shouldReturnTenYearsAgo() { 
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.add(Calendar.YEAR, -10);
		Assert.assertEquals("10 years ago", DateUtil.getRelativeTime(now, calendar.getTime()));
	}	
	
}