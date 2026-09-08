/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.report.renderer;

import org.openmrs.api.context.Context;

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

    /**
     * Create a new RenderingMode from a descriptor
     * @param descriptor
     */
	public RenderingMode(String descriptor) {
	    this();
	    try {
            if (descriptor != null) {
                String[] split = descriptor.split("!", 2);
                Class<? extends ReportRenderer> rendererType = (Class<? extends ReportRenderer>) Context.loadClass(split[0]);
                setRenderer(rendererType.newInstance());
                setArgument(split.length == 2 ? split[1] : "");
                setLabel(rendererType.getSimpleName());
            }
        }
        catch (Exception e) {
	        throw new IllegalArgumentException("Unable to load rendering mode from descriptor", e);
        }
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
