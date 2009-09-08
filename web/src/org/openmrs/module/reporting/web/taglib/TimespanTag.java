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

import java.io.IOException;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.module.util.DateUtil;

public class TimespanTag extends BodyTagSupport {
	
	public static final long serialVersionUID = 1L;
	
	private final Log log = LogFactory.getLog(getClass());
	
	private Date now = null;
	private Date then = null;
	
	
	public int doStartTag() throws JspException {
		try { 
			if (now == null) { 
				now = new Date();
			}
			if (then == null) { 
				throw new APIException("Timespan is expecting at least 'then' date");
			}
			
			pageContext.getOut().write("<span class=\"timespan\"> " + DateUtil.getTimespan(now, then) + " </span>");
		} catch(IOException e) { 
			log.error("Unable to write timespan to output", e);
		}		
		return SKIP_BODY;
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
	
	
	
	
	
	
}
