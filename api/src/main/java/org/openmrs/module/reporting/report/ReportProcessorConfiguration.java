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

import java.util.Properties;

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.module.reporting.report.processor.ReportProcessor;

/**
 * Represents a configured ReportProcessor that can be shared across multiple Reports
 */
public class ReportProcessorConfiguration extends BaseOpenmrsMetadata  {
	
	//***** PROPERTIES *****
	
	private Integer id;
	private String processorType;
	private Properties configuration;
	private Boolean runOnSuccess = Boolean.TRUE;
	private Boolean runOnError = Boolean.FALSE;
	private ReportDesign reportDesign;
	private ProcessorMode processorMode = ProcessorMode.DISABLED;
	
	public enum ProcessorMode {
		ON_DEMAND, AUTOMATIC, ON_DEMAND_AND_AUTOMATIC, DISABLED
	}

	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public ReportProcessorConfiguration() {}

	/**
	 * Full Constructor
	 */
	public ReportProcessorConfiguration(String name, Class<? extends ReportProcessor> processorType, Properties configuration, 
										Boolean runOnSuccess, Boolean runOnError) {
		setName(name);
		if (processorType != null) {
			this.processorType = processorType.getName();
		}
		this.configuration = configuration;
		this.runOnSuccess = runOnSuccess;
		this.runOnError = runOnError;
	}

	//***** INSTANCE METHODS *****
	
	/** @see Object#equals(Object) */
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ReportProcessorConfiguration) {
			ReportProcessorConfiguration p = (ReportProcessorConfiguration) obj;
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
	 * @return the processorType
	 */
	public String getProcessorType() {
		return processorType;
	}

	/**
	 * @param processorType the processorType to set
	 */
	public void setProcessorType(String processorType) {
		this.processorType = processorType;
	}

	/**
	 * @return the configuration
	 */
	public Properties getConfiguration() {
		return configuration;
	}

	/**
	 * @param configuration the configuration to set
	 */
	public void setConfiguration(Properties configuration) {
		this.configuration = configuration;
	}

	/**
	 * @return the runOnSuccess
	 */
	public Boolean getRunOnSuccess() {
		return runOnSuccess;
	}

	/**
	 * @param runOnSuccess the runOnSuccess to set
	 */
	public void setRunOnSuccess(Boolean runOnSuccess) {
		this.runOnSuccess = runOnSuccess;
	}

	/**
	 * @return the runOnError
	 */
	public Boolean getRunOnError() {
		return runOnError;
	}

	/**
	 * @param runOnError the runOnError to set
	 */
	public void setRunOnError(Boolean runOnError) {
		this.runOnError = runOnError;
	}

	/**
	 * @return the ReportDesign
	 * NOTE:  if null, this implies that this ReportProcessorConfiguration is global.
	 */
	public ReportDesign getReportDesign() {
		return reportDesign;
	}

	/**
	 * @param reportDesign the reportDesignToSet
	 */
	public void setReportDesign(ReportDesign reportDesign) {
		this.reportDesign = reportDesign;
	}
	
	/**
	 * Convenience method for asking this ReportProcessorConfiguration if it is global, or local to a single ReportDesign
	 * @return true if global, false if specific to a single ReportDesign
	 */
	public boolean isGlobal() {
		return getReportDesign() == null;
	}
	
	/**
	 * @return the processorMode
	 */
	public ProcessorMode getProcessorMode() {
		return processorMode;
	}

	/**
	 * 
	 * @param the processorMode to set
	 */
	public void setProcessorMode(ProcessorMode processorMode) {
		this.processorMode = processorMode;
	}
}
