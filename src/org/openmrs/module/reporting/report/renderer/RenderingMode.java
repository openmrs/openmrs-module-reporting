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
package org.openmrs.module.reporting.report.renderer;

/**
 * Represents a mode in which a @see org.openmrs.report.ReportRenderer can run. A simple renderer like
 * a CSV renderer can probably only render in one mode. A more sophisticated renderer might be able
 * to render multiple modes, which would show up with different labels, and at different weights in
 * a drop-down list. In this case a renderer would use the String argument of this class to determine
 * which mode was selected by the user. A higher sortWeight (i.e. closer to Integer.MAX_VALUE) will
 * typically appear at the top of a select list.
 * 
 */
public class RenderingMode implements Comparable<RenderingMode> {
	
	//***** PROPERTIES *****
	
	private ReportRenderer renderer;
	private String label;
	private String argument;
	private Integer sortWeight;
	
	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public RenderingMode() {
		argument = "";
		sortWeight = 0;
	}
	
	/**
	 * Full Constructor
	 */
	public RenderingMode(ReportRenderer renderer, String label, String argument, Integer sortWeight) {
		this.renderer = renderer;
		this.label = label;
		this.argument = argument;
		this.sortWeight = sortWeight;
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * Higher sortWeight comes first
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(RenderingMode other) {
		int temp = other.sortWeight.compareTo(sortWeight);
		if (temp == 0) {
			temp = other.label.compareTo(label);
		}
		return temp;
	}

	/**
	 * @return the renderer's classname, followed by !argument (if the argument is specified)
	 */
	public String getDescriptor() {
		String ret = getRenderer().getClass().getName();
		if (getArgument() != null) {
			ret += "!" + getArgument();
		}
		return ret;
    }

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return getDescriptor();
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		RenderingMode that = (RenderingMode)obj;
		return this.getDescriptor().equals(that.getDescriptor());
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getDescriptor().hashCode();
	}

	/**
	 * @return the renderer
	 */
	public ReportRenderer getRenderer() {
		return renderer;
	}

	/**
	 * @param renderer the renderer to set
	 */
	public void setRenderer(ReportRenderer renderer) {
		this.renderer = renderer;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the argument
	 */
	public String getArgument() {
		return argument;
	}

	/**
	 * @param argument the argument to set
	 */
	public void setArgument(String argument) {
		this.argument = argument;
	}

	/**
	 * @return the sortWeight
	 */
	public Integer getSortWeight() {
		return sortWeight;
	}

	/**
	 * @param sortWeight the sortWeight to set
	 */
	public void setSortWeight(Integer sortWeight) {
		this.sortWeight = sortWeight;
	}
}
