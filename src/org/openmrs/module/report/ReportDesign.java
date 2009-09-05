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
package org.openmrs.module.report;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.module.report.renderer.ReportRenderer;

/**
 * Represents a particular design of a report, which is interpreted
 * by a particular renderer in order to output the report in a specific format
 */
public class ReportDesign extends BaseOpenmrsMetadata  {
	
	//***** PROPERTIES *****
	
	private Integer id;
	private ReportDefinition reportDefinition;
	private Class<? extends ReportRenderer> rendererType;
	private Properties properties;
	private Set<ReportDesignResource> resources;

	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public ReportDesign() {}
	
	//***** PROPERTY ACCESS *****
	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the reportDefinition
	 */
	public ReportDefinition getReportDefinition() {
		return reportDefinition;
	}

	/**
	 * @param reportDefinition the reportDefinition to set
	 */
	public void setReportDefinition(ReportDefinition reportDefinition) {
		this.reportDefinition = reportDefinition;
	}

	/**
	 * @return the rendererType
	 */
	public Class<? extends ReportRenderer> getRendererType() {
		return rendererType;
	}

	/**
	 * @param rendererType the rendererType to set
	 */
	public void setRendererType(Class<? extends ReportRenderer> rendererType) {
		this.rendererType = rendererType;
	}

	/**
	 * @return the properties
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	/**
	 * @return the resources
	 */
	public Set<ReportDesignResource> getResources() {
		if (resources == null) {
			resources = new HashSet<ReportDesignResource>();
		}
		return resources;
	}

	/**
	 * @param resources the resources to set
	 */
	public void setResources(Set<ReportDesignResource> resources) {
		this.resources = resources;
	}
}
