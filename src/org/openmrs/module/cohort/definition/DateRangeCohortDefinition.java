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
package org.openmrs.module.cohort.definition;

import java.text.DateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.util.OpenmrsUtil;

public abstract class DateRangeCohortDefinition extends BaseCohortDefinition {
	
	private static final long serialVersionUID = 1L;
	protected final transient Log log = LogFactory.getLog(getClass());
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty
	private Integer withinLastDays;
	
	@ConfigurationProperty
	private Integer withinLastMonths;
	
	@ConfigurationProperty
	private Integer untilDaysAgo;
	
	@ConfigurationProperty
	private Integer untilMonthsAgo;
	
	@ConfigurationProperty
	private Date sinceDate;
	
	@ConfigurationProperty
	private Date untilDate;
	
	//***** CONSTRUCTORS *****
	
    /**
     * Default Constructor
     */
	public DateRangeCohortDefinition() {
		super();
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * Convenience method to return whether or not the date parameters represent a "current case"
	 * @return boolean
	 */
	public boolean isCurrentCase(EvaluationContext context) {
		return 	(withinLastDays == null || withinLastDays == 0) && 
				(withinLastMonths == null || withinLastMonths == 0);
	}
	
	/**
	 * Helper method to return the from date as
	 * computed from the various input options
	 * @return Date reprenting the effective fromDate
	 */
	public Date getCalculatedFromDate(EvaluationContext context) {
		return OpenmrsUtil.fromDateHelper(null, withinLastDays, withinLastMonths, 
										  untilDaysAgo, untilMonthsAgo, sinceDate, untilDate);
	}
	
	/**
	 * Helper method to return the to date as
	 * computed from the various input options
	 * @return Date reprenting the effective toDate
	 */
	public Date getCalculatedToDate(EvaluationContext context) {
		return OpenmrsUtil.toDateHelper(null, withinLastDays, withinLastMonths, 
										  untilDaysAgo, untilMonthsAgo, sinceDate, untilDate);
	}
	
	/**
	 * Helper method to return a toString() representation of the DateRange filter parameters
	 * @return
	 */
	public String getDateRangeDescription() {
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Context.getLocale());
		
		StringBuffer ret = new StringBuffer();
		if (!isCurrentCase(null)) {
			if (withinLastDays != null || withinLastMonths != null) {
				ret.append(" within the last");
				if (withinLastMonths != null) {
					ret.append(" " + withinLastMonths + " month(s)");
				}
				if (withinLastMonths != null) {
					ret.append(" " + withinLastMonths + " day(s)");
				}
			}
			if (untilDaysAgo != null) {
				ret.append(" until " + untilDaysAgo + " day(s) ago");
			}
			if (untilMonthsAgo != null) {
				ret.append(" until " + untilMonthsAgo + " month(s) ago");
			}
		}
		if (sinceDate != null) {
			ret.append(" since " + df.format(sinceDate));
		}
		if (untilDate != null) {
			ret.append(" until " + df.format(untilDate));
		}
		return ret.toString();
	}
	
	//***** PROPERTY ACCESS *****

    /**
     * @return the withinLastDays
     */
    public Integer getWithinLastDays() {
    	return withinLastDays;
    }
	
    /**
     * @param withinLastDays the withinLastDays to set
     */
    public void setWithinLastDays(Integer withinLastDays) {
    	this.withinLastDays = withinLastDays;
    }

    /**
     * @return the withinLastMonths
     */
    public Integer getWithinLastMonths() {
    	return withinLastMonths;
    }
	
    /**
     * @param withinLastMonths the withinLastMonths to set
     */
    public void setWithinLastMonths(Integer withinLastMonths) {
    	this.withinLastMonths = withinLastMonths;
    }
	
    /**
     * @return the untilDaysAgo
     */
    public Integer getUntilDaysAgo() {
    	return untilDaysAgo;
    }
	
    /**
     * @param untilDaysAgo the untilDaysAgo to set
     */
    public void setUntilDaysAgo(Integer untilDaysAgo) {
    	this.untilDaysAgo = untilDaysAgo;
    }
	
    /**
     * @return the untilMonthsAgo
     */
    public Integer getUntilMonthsAgo() {
    	return untilMonthsAgo;
    }
	
    /**
     * @param untilMonthsAgo the untilMonthsAgo to set
     */
    public void setUntilMonthsAgo(Integer untilMonthsAgo) {
    	this.untilMonthsAgo = untilMonthsAgo;
    }
	
    /**
     * @return the sinceDate
     */
    public Date getSinceDate() {
    	return sinceDate;
    }
	
    /**
     * @param sinceDate the sinceDate to set
     */
    public void setSinceDate(Date sinceDate) {
    	this.sinceDate = sinceDate;
    }
	
    /**
     * @return the untilDate
     */
    public Date getUntilDate() {
    	return untilDate;
    }

    /**
     * @param untilDate the untilDate to set
     */
    public void setUntilDate(Date untilDate) {
    	this.untilDate = untilDate;
    }
}
