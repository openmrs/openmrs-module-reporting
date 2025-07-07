/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.report;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;

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
	private Set<ReportProcessorConfiguration> reportProcessors;

	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public ReportDesign() {}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * Returns the resource with the given name for this ReportDesign
	 * @param name the ReportDesignResource name to match
	 * @return ReportDesignResource
	 */
	public ReportDesignResource getResourceByName(String name) {
		for (ReportDesignResource r : getResources()) {
			if (StringUtils.equals(r.getName(), name)) {
				return r;
			}
		}
		return null;
	}
	
	/**
	 * Returns the resource with the given uuid for this ReportDesign
	 * @param uuid the ReportDesignResource uuid to match
	 * @return ReportDesignResource
	 */
	public ReportDesignResource getResourceByUuid(String uuid) {
		for (ReportDesignResource r : getResources()) {
			if (StringUtils.equals(r.getUuid(), uuid)) {
				return r;
			}
		}
		return null;
	}
	
	/**
	 * Adds a ReportDesignResource
	 * @param resource the ReportDesignResource to add
	 */
	public void addResource(ReportDesignResource resource) {
		getResources().add(resource);
	}
	
	/**
	 * Adds a property with the given name and value
	 */
	public void addPropertyValue(String name, String value) {
		getProperties().put(name, value);
	}
	
	/**
	 * Returns the property value given the passed name
	 */
	public String getPropertyValue(String name, String defaultValue) {
		if (getProperties() != null) {
			return getProperties().getProperty(name, defaultValue);
		}
		return defaultValue;
	}
	
	/** @see Object#equals(Object) */
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ReportDesign) {
			ReportDesign p = (ReportDesign) obj;
			if (this.getUuid() != null) {
				return (this.getUuid().equals(p.getUuid()));
			}
		}
		return this == obj;
	}
	
	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (getUuid() == null ? 0 : 31 * getUuid().hashCode());
	}
	
	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}
	
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
		if (properties == null) {
			properties = new Properties();
		}
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
	
	/**
	 * @return the reportProcessors
	 */
	public Set<ReportProcessorConfiguration> getReportProcessors() {
		if (reportProcessors == null) {
			reportProcessors = new HashSet<ReportProcessorConfiguration>();
		}
		return reportProcessors;
	}
	
	/**
	 * @param reportProcessor adds the processor
	 */
	public void addReportProcessor(ReportProcessorConfiguration reportProcessor) {
		getReportProcessors().add(reportProcessor);
		reportProcessor.setReportDesign(this);
	}
 
	/**
	 * @param reportProcessors the reportProcessors to set
	 */
	public void setReportProcessors(Set<ReportProcessorConfiguration> reportProcessors) {
		this.reportProcessors = reportProcessors;
	}
}
