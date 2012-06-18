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

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.LoopTagStatus;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * This tag allows iterating across an range of elements
 */
public class ForEachTag extends BodyTagSupport {
	
	public static final long serialVersionUID = 1232300L;
	private Iterator<?> records;
	private int count;

	//***** PROPERTIES *****
	private Object items;
	private String var;
	private String varStatus;
	
	/**
	 * @see BodyTagSupport#doStartTag()
	 */
	public int doStartTag() {
		records = null;
		count = 0;
		if (items != null) {
			if (items instanceof Iterable<?>) {
				records = ((Iterable<?>) items).iterator();
			}
			else if (items instanceof Object[]) {
				Object[] arr = (Object[]) items;
				records = Arrays.asList(arr).iterator();
			}
			else if (items instanceof Map<?, ?>) {
				records = ((Map<?, ?>) items).entrySet().iterator();
			}
		}
		if (records == null || !records.hasNext()) {
			records = null;
			return SKIP_BODY;
		} 
		else {
			return EVAL_BODY_BUFFERED;
		}
	}
	
	/**
	 * @see javax.servlet.jsp.tagext.BodyTag#doInitBody()
	 */
	public void doInitBody() throws JspException {
		if (records.hasNext()) {
			iterate();
		}
	}
	
	/**
	 * @see javax.servlet.jsp.tagext.IterationTag#doAfterBody()
	 */
	public int doAfterBody() throws JspException {
		if (records.hasNext()) {
			iterate();
			return EVAL_BODY_BUFFERED;
		} 
		else {
			return SKIP_BODY;
		}
	}
	
	/**
	 * Iterates the next object in the iterator
	 */
	private void iterate() {
		final Object obj = records.next();
		count++;
		if (obj != null) {
			if (var != null) {
				pageContext.setAttribute(var, obj);
			}
			if (varStatus != null) {
				LoopTagStatus s = new LoopTagStatus() {
					public boolean isLast() { return !records.hasNext(); }
					public boolean isFirst() { return count == 1; }
					public Integer getStep() { return 1; }
					public int getIndex() { return count-1; }
					public int getCount() { return count; }
					public Integer getBegin() { return null; }
					public Object getCurrent() { return obj; }
					public Integer getEnd() { return null; }
				};
				pageContext.setAttribute(varStatus, s);
			}
		} 
		else {
			if (var != null) {
				pageContext.removeAttribute(var);
			}
			if (varStatus != null) {
				pageContext.removeAttribute(varStatus);
			}
		}
	}
	
	/**
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspException {
		try {
			if (getBodyContent() != null && records != null)
				getBodyContent().writeOut(getBodyContent().getEnclosingWriter());
		}
		catch (java.io.IOException e) {
			throw new JspTagException("IO Error: " + e.getMessage());
		}
		return EVAL_PAGE;
	}

	/**
	 * @return the items
	 */
	public Object getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(Object items) {
		this.items = items;
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

	/**
	 * @return the varStatus
	 */
	public String getVarStatus() {
		return varStatus;
	}

	/**
	 * @param varStatus the varStatus to set
	 */
	public void setVarStatus(String varStatus) {
		this.varStatus = varStatus;
	}
}
