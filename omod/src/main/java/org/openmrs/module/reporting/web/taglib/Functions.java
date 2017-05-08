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
package org.openmrs.module.reporting.web.taglib;

import java.util.Date;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.quartz.CronExpression;

/**
 * Web Functions
 */
public class Functions {
	
	private static final Log log = LogFactory.getLog(Functions.class);

	/**
	 * Provides instance of functionality to jsp pages
	 */
	public static boolean instanceOf(Object o, String className) {
		try {
			Class<?> c = Context.loadClass(className);
			if (c.isAssignableFrom(o.getClass())) {
				return true;
			}
		}
		catch (Exception e) {
			log.warn("Error performing instanceof check.  Object " + o + "; class: " + className, e);
		}
		return false;
	}

	/**
	 * Provides instance of functionality to jsp pages
	 */
	public static Date nextExecutionTime(String cronExpression) {
		if (ObjectUtil.notNull(cronExpression)) {
			try {
				CronExpression cron = new CronExpression(cronExpression);
				return cron.getNextValidTimeAfter(new Date());
			}
			catch (Exception e) {
				log.warn("Error getting next valid time for cron expression " + cronExpression, e);
			}
		}
		return null;
	}

    /**
     * This method will make untrusted strings safe for use as JavaScript strings
     *
     * @param s
     * @return a JS-escaped version of s
     */
    public static String getSafeJsString(String s) {
        return StringEscapeUtils.escapeJavaScript(s);
    }
}
