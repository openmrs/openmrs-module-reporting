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
	
	/* Serial version UID */
    private static final long serialVersionUID = -2572061676651616176L;
	
	/* Data export object */
	private DataExportReportObject dataExport;
	/**
	 * Default public constructor 
	 */
	public DataExportDataSetDefinition() { 
		super();
	}
	
	/**
	 *  Public constructor
	 */
	public DataExportDataSetDefinition(DataExportReportObject dataExport) { 
		this.dataExport = dataExport;
	}

	public String getUuid() { 
		return dataExport.getUuid();
	}
	
	public void setUuid(String uuid) { 
		this.dataExport.setUuid(uuid);
	}
	
    
    /**
	 * @see DataSetDefinition#getId()
     */
    public Integer getId() { 
    	return dataExport.getReportObjectId();  	
    }    
    
    /**
	 * @see DataSetDefinition#getName()
     */
    public String getName() { 
    	return dataExport.getName();    	
    }

    /**
	 * @see DataSetDefinition#getDescription()
     */
    public String getDescription() { 
    	return dataExport.getDescription();    	
    }

    /**
     * Returns the data export object that backs this dataset definition.
     * @return	the data export object that backs this dataset definition
     */
    public DataExportReportObject getDataExportReportObject() {
    	return dataExport;
    }
    
    /**
     * Sets the data export model.
     * @param dataExport
     */
    public void setDataExportReportObject(DataExportReportObject dataExport) { 
    	this.dataExport = dataExport;
    }    
}
