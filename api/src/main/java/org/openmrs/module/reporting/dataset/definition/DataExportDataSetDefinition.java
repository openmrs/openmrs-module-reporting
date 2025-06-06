/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
 * @see DataExportDataSetDefinition
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
