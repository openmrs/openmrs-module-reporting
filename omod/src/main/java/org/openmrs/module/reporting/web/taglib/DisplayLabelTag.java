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
