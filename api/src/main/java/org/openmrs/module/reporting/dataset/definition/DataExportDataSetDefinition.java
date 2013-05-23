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

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.reporting.export.DataExportReportObject;

/**
 * Definition of a dataset that runs a Data Export (of the sort created in the reportingcompatibility
 * module.
 * @see DataExportDataSet
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@SuppressWarnings("deprecation")
@Localized("reporting.DataExportDataSetDefinition")
public class DataExportDataSetDefinition extends BaseDataSetDefinition {
	
    public static final long serialVersionUID = -2572061676651616176L;
	
    //***** PROPERTIES *****
    
    @ConfigurationProperty
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
		return (getDataExport() == null ? null : getDataExport().getId());
	}

	/**
	 * @see BaseOpenmrsObject#getUuid()
	 */
	@Override
	public String getUuid() {
		return (getDataExport() == null ? null : getDataExport().getUuid());
	}

	/**
	 * @see BaseOpenmrsMetadata#getName()
	 */
	@Override
	public String getName() {
		return (getDataExport() == null ? null : getDataExport().getName());
	}

	/**
	 * @see BaseOpenmrsMetadata#getDescription()
	 */
	@Override
	public String getDescription() {
		return (getDataExport() == null ? null : getDataExport().getDescription());
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
