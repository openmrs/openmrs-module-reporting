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
package org.openmrs.module.reporting.dataset.definition;

import org.openmrs.reporting.export.DataExportReportObject;

/**
 * Definition of a dataset that produces one-row-per-obs. Output might look like: 
 * 
 * patientId, question, questionConceptId, answer, answerConceptId, obsDatetime, encounterId 
 * 123, "WEIGHT (KG)", 5089, 70, null, "2007-05-23", 2345 
 * 123, "OCCUPATION", 987, "STUDENT", 988, "2008-01-30", 2658
 * 
 * @see DataExportDataSet
 */
@SuppressWarnings("deprecation")
public class DataExportDataSetDefinition extends BaseDataSetDefinition {
	
    private static final long serialVersionUID = -2572061676651616176L;
	
    //***** PROPERTIES *****
    
	private DataExportReportObject dataExport;
	
	//***** CONSTRUCTORS *****
	
	/**
	 * Default public constructor 
	 */
	public DataExportDataSetDefinition() { 
		super();
	}
	
	/**
	 *  Full constructor
	 */
	public DataExportDataSetDefinition(DataExportReportObject dataExport) { 
		this.dataExport = dataExport;
	}
	
	//***** INSTANCE METHODS *****

    /**
	 * @see BaseDataSetDefinition#getId()
	 */
	@Override
	public Integer getId() {
		return getDataExport().getId();
	}
	
	/**
	 * @see BaseOpenmrsObject#getUuid()
	 */
	@Override
	public String getUuid() {
		return getDataExport().getUuid();
	}
	
	/**
	 * @see BaseOpenmrsMetadata#getName()
	 */
	@Override
	public String getName() {
		return getDataExport().getName();
	}

	/**
	 * @see BaseOpenmrsMetadata#getDescription()
	 */
	@Override
	public String getDescription() {
		return getDataExport().getDescription();
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the dataExport
	 */
	public DataExportReportObject getDataExport() {
		return dataExport;
	}

	/**
	 * @param dataExport the dataExport to set
	 */
	public void setDataExport(DataExportReportObject dataExport) {
		this.dataExport = dataExport;
	}
}
