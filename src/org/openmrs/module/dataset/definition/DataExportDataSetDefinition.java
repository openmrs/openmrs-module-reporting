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
package org.openmrs.module.dataset.definition;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.column.SimpleDataSetColumn;
import org.openmrs.reporting.export.DataExportReportObject;
import org.openmrs.reporting.export.ExportColumn;

/**
 * Definition of a dataset that produces one-row-per-obs. Output might look like: 
 * 
 * patientId, question, questionConceptId, answer, answerConceptId, obsDatetime, encounterId 
 * 123, "WEIGHT (KG)", 5089, 70, null, "2007-05-23", 2345 
 * 123, "OCCUPATION", 987, "STUDENT", 988, "2008-01-30", 2658
 * 
 * @see DataExportDataSet
 */
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
	 * 
	 *  TODO Is this ok or should we add a method like convertDataExport()
	 *  We could also just have getters pull the name, description from the 
	 *  data export object
	 */
	public DataExportDataSetDefinition(DataExportReportObject dataExport) { 
		this.dataExport = dataExport;
	}
	
	/**
	 * @see org.openmrs.module.dataset.definition.DataSetDefinition#getColumns()
	 */
    public List<DataSetColumn> getColumns() {
		List<DataSetColumn> columns = new ArrayList<DataSetColumn>();
		for (ExportColumn exportColumn : dataExport.getColumns()) {
			String colName = exportColumn.getColumnName();
			// TODO: Add and retrieve dataType from ExportColumn
			columns.add(new SimpleDataSetColumn(colName, colName, String.class));
		}
		return columns;
	}

    /**
	 * @see org.openmrs.module.dataset.definition.DataSetDefinition#getId()
     */
    public Integer getId() { 
    	return this.dataExport.getReportObjectId();  	
    }    
    
    /**
	 * @see org.openmrs.module.dataset.definition.DataSetDefinition#getName()
     */
    public String getName() { 
    	return this.dataExport.getName();    	
    }

    /**
	 * @see org.openmrs.module.dataset.definition.DataSetDefinition#getDescription()
     */
    public String getDescription() { 
    	return this.dataExport.getDescription();    	
    }
    
	/**
	 * @see org.openmrs.module.dataset.definition.DataSetDefinition#getColumnDatatypes()
	 */
	public List<Class> getColumnDatatypes() {		
		// TODO Need to iterate over getColumns() and return data types
		return null;
	}
	
	/**
	 * @see org.openmrs.module.dataset.definition.DataSetDefinition#getColumnKeys()
	 */
	public List<String> getColumnKeys() {
		// TODO Need to iterate over getColumns() and return keys
		return null;
	}	

	/**
     * @see org.openmrs.module.evaluation.parameter.Parameterizable#getParameters()
     */
    public List<Parameter> getParameters() {
	    return new ArrayList<Parameter>();
    }	
    
    
    /**
     * Returns the data export object that backs this dataset definition.
     * @return	the data export object that backs this dataset definition
     */
    public DataExportReportObject getDataExportReportObject() {
    	return this.dataExport;
    }
    
    /**
     * Sets the data export model.
     * @param dataExport
     */
    public void setDataExportReportObject(DataExportReportObject dataExport) { 
    	this.dataExport = dataExport;
    }
    
    
}
