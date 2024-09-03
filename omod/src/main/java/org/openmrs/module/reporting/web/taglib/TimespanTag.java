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

import java.io.IOException;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.common.DateUtil;

public class TimespanTag extends BodyTagSupport {
	
	public static final long serialVersionUID = 1L;
	
	private final Log log = LogFactory.getLog(getClass());
	
	private Date now = null;
	private Date then = null;
	private Boolean showAgoWord = true;
	
	
	public int doStartTag() throws JspException {
		try { 
			if (now == null) { 
				now = new Date();
			}
			if (then != null) { 
				if (showAgoWord == null) {
					showAgoWord = true;
				}
				pageContext.getOut().write("<span class=\"timespan\"> " + DateUtil.getTimespan(now, then, showAgoWord) + " </span>");
			}
		} catch(IOException e) { 
			log.error("Unable to write timespan to output", e);
		}
		reset();
		return SKIP_BODY;
	}


	private void reset() {
	    now = null;
	    then = null;
	    showAgoWord = true;
    }


	public Date getNow() {
		return now;
	}


	public void setNow(Date now) {
		this.now = now;
	}


	public Date getThen() {
		return then;
	}


	public void setThen(Date then) {
		this.then = then;
	}


    public Boolean getShowAgoWord() {
    	return showAgoWord;
    }


    public void setShowAgoWord(Boolean showAgoWord) {
    	this.showAgoWord = showAgoWord;
    }
	
}
