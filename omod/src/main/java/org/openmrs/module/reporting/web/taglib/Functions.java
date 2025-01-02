/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
