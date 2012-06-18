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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.common.ObjectUtil;

/**
 * Renders an appropriate display label, given a class name
 */
public class DisplayLabelTag extends BodyTagSupport {
	
	public static final long serialVersionUID = 1L;
	
	private final Log log = LogFactory.getLog(getClass());
	
	//***** PROPERTIES *****
	
	private String type;
	private String var;
	
	/**
	 * @see BodyTagSupport#doStartTag()
	 */
	public int doStartTag() throws JspException {
		try {
			Class<?> c = Context.loadClass(type);
			String label = MessageUtil.getDisplayLabel(c);
			if (ObjectUtil.isNull(var)) {
				pageContext.getOut().write(label);
			}
			else {
				pageContext.setAttribute(var, label);
			}
		} 
		catch (Exception e) { 
			log.error("Unable to write timespan to output", e);
		}
		reset();
		return SKIP_BODY;
	}
	
	/**
	 * Reset values
	 */
	private void reset() {
		type = null;
		var = null;
    }

	//***** PROPERTY ACCESS *****

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the var
	 */
	public String getVar() {
		return var;
	}

	/**
	 * @param var the var to set
	 */
	public void setVar(String var) {
		this.var = var;
	}
}
