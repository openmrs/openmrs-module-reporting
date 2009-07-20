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
package org.openmrs.module.dataset.definition.persister;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.definition.DataExportDataSetDefinition;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.reporting.AbstractReportObject;
import org.openmrs.reporting.ReportObjectService;
import org.openmrs.reporting.export.DataExportReportObject;

/**
 * Class which manages persistence of a DataExportDataSetDefinition using legacy tables
 */
@Handler(supports={DataExportDataSetDefinition.class}, order=50)
public class DataExportDataSetDefinitionPersister implements DataSetDefinitionPersister {
		
	/**
	 * Public constructor
	 */
	public DataExportDataSetDefinitionPersister() { }
    
	/**
     * @see DataSetDefinitionPersister#getDataSetDefinition(Integer)
     */
    public DataSetDefinition getDataSetDefinition(Integer id) {
    	ReportObjectService ros = Context.getService(ReportObjectService.class);
    	DataExportReportObject dataExport = (DataExportReportObject) ros.getReportObject(id);
    	return (dataExport != null) ? new DataExportDataSetDefinition(dataExport) : null;
    }
    
	/** 
	 * @see DataSetDefinitionPersister#getDataSetDefinitionByUuid(String)
	 */
	public DataSetDefinition getDataSetDefinitionByUuid(String uuid) {	
		// As a temporary hack, infer uuid from Class + id for saved DataSetDefinitions
    	for(DataSetDefinition dsd : getAllDataSetDefinitions(false)) {
    		String inferredUuid = dsd.getClass() + ":" + dsd.getId();
    		if (inferredUuid.equalsIgnoreCase(uuid)) {
    			return dsd;
    		}
    	}
    	return null;
	}

	/**
     * @see DataSetDefinitionPersister#getAllDataSetDefinitions(boolean)
     */
    public List<DataSetDefinition> getAllDataSetDefinitions(boolean includeRetired) {
	    
    	// Get all data exports in the system
	    ReportObjectService ros = Context.getService(ReportObjectService.class);
	    List<AbstractReportObject> dataExports = ros.getReportObjectsByType("Data Export");
    	
	    // Iterate through the report definitions and wrap each with a BIRT report
    	List <DataSetDefinition> dataSetDefinitions = new Vector<DataSetDefinition>();
    	for (AbstractReportObject obj : dataExports) { 
    		DataExportReportObject dataExport = (DataExportReportObject) obj;
    		dataSetDefinitions.add(new DataExportDataSetDefinition(dataExport));    		
    	}    	
    	return dataSetDefinitions;
    }     
    
	/**
     * @see DataSetDefinitionPersister#getDataSetDefinitions(String, boolean)
     */
    public List<DataSetDefinition> getDataSetDefinitions(String name, boolean exactMatchOnly) {
    	List<DataSetDefinition> ret = new ArrayList<DataSetDefinition>();	
    	for(DataSetDefinition dsd : getAllDataSetDefinitions(false)) {
    		if (dsd.getName() != null) {
    			if (exactMatchOnly) {
    				if (dsd.getName().equalsIgnoreCase(name)) {
    					ret.add(dsd);
    				}
    			}
    			else {
    				if (dsd.getName().toUpperCase().contains(name.toUpperCase())) {
    					ret.add(dsd);
    				}
    			}
    		}
    	}
    	return ret;
    }
    
	/**
     * @see DataSetDefinitionPersister#saveDataSetDefinition(DataSetDefinition)
     */
    public DataSetDefinition saveDataSetDefinition(DataSetDefinition dataSetDefinition) {

    	DataExportDataSetDefinition dsd = (DataExportDataSetDefinition) dataSetDefinition;
    	
    	// Save the data export to the database 
    	DataExportReportObject dataExport = 
    		(DataExportReportObject) Context.getService(ReportObjectService.class).saveReportObject(
    				dsd.getDataExportReportObject());
    	
    	dsd.setDataExportReportObject(dataExport);
		
		return dsd;
    }

	/**
     * @see DataSetDefinitionPersister#purgeDataSetDefinition(DataSetDefinition)
     */
    public void purgeDataSetDefinition(DataSetDefinition dataSetDefinition) {
    	DataExportDataSetDefinition dsd = (DataExportDataSetDefinition) dataSetDefinition;
    	Context.getService(ReportObjectService.class).purgeReportObject(dsd.getDataExportReportObject());    	
    }
}
