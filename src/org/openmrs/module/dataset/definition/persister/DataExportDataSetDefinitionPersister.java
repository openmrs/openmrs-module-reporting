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

import java.util.List;
import java.util.UUID;
import java.util.Vector;

//import org.openmrs.annotation.Handler;
import org.jfree.util.Log;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.definition.DataExportDataSetDefinition;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.reporting.AbstractReportObject;
import org.openmrs.reporting.ReportObjectService;
import org.openmrs.reporting.export.DataExportReportObject;

/**
 * This class returns DataSetDefinitions that have been Serialized to the database
 * This class is annotated as a Handler that supports all DataSetDefinition classes
 * Specifying no order on this indicates that this is the default means of Persisting 
 * a DataSetDefinition.  To override this behavior, any additional DataSetDefinitionPersister
 * should specify the order field on the Handler annotation.
 */
//@Handler(supports={DataExportDataSetDefinition.class})
public class DataExportDataSetDefinitionPersister implements DataSetDefinitionPersister {
		
	
	// ================================================================ Constructors ==
	
	/**
	 * Public constructor
	 */
	public DataExportDataSetDefinitionPersister() { }
	
	
	
	
	// ============================================================ Instance Methods ==
    
	/**
     * @see org.openmrs.module.dataset.persister.DataSetDefinitionPersister#canPersist(java.lang.Class)
     */
    public Boolean canPersist(Class<? extends DataSetDefinition> clazz) {
    	return (clazz.isAssignableFrom(DataExportDataSetDefinition.class));
    }

    
    
    
	/**
     * @see org.openmrs.module.dataset.persister.DataSetDefinitionPersister#getAllDataSetDefinitions()
     */
    public List<DataSetDefinition> getAllDataSetDefinitions() {
    	return getAllDataSetDefinitions(false);
    }
	
	/**
     * @see DataSetDefinitionPersister#getAllDataSetDefinitions(boolean)
     */
    public List<DataSetDefinition> getAllDataSetDefinitions(boolean includeRetired) {    	
    	return getDataSetDefinitions();	
    }

    /**
     * Get all report definitions from the database.
     * 
     * TODO Should we cache the data exports so we're not always getting them from the database?
     * 
     * @return
     */
    public List<DataSetDefinition> getDataSetDefinitions() { 
	    List <DataSetDefinition> dataSetDefinitions = new Vector<DataSetDefinition>();
	    
	    // Get all data exports in the system
    	List<AbstractReportObject> dataExports = 
	    	Context.getService(ReportObjectService.class).getReportObjectsByType("Data Export");
    	
	    // Iterate through the report definitions and wrap each with a BIRT report
    	for (AbstractReportObject obj : dataExports) { 
    		DataExportReportObject dataExport = (DataExportReportObject) obj;
    		
    		DataExportDataSetDefinition dataExportDefinition = 
    			new DataExportDataSetDefinition(dataExport);
    		    		
    		dataSetDefinitions.add(dataExportDefinition);    		
    	}    	
    	return dataSetDefinitions;
    } 
	    
    
	/**
     * @see org.openmrs.module.dataset.persister.DataSetDefinitionPersister#getDataSetDefinition(java.util.UUID)
     */
    public DataSetDefinition getDataSetDefinition(UUID uuid) {
    	throw new APIException("Data exports cannot be located by UUID");
    }
    
    
	/**
     * @see DataSetDefinitionPersister#getDataSetDefinition(java.lang.Integer)
     */
    public DataSetDefinition getDataSetDefinition(Integer id) {    	
    	DataExportReportObject dataExport =
    		(DataExportReportObject) Context.getService(ReportObjectService.class).getReportObject(id);

    	return (dataExport != null) ? 
    			new DataExportDataSetDefinition(dataExport) : null;
    }
    
	/**
     * @see DataSetDefinitionPersister#getDataSetDefinitionByName(java.lang.String)
     */
    public DataSetDefinition getDataSetDefinitionByName(String name) {
    	DataSetDefinition definition = null;

    	// TODO How should we handle the case where we have multiple data sets with the same name    	
    	for(DataSetDefinition temp : getAllDataSetDefinitions()) {  
    		Log.info("Name: " + name + " Temp: " + temp.getName());
    		if (temp.getName().equalsIgnoreCase(name)) { 
    			definition = temp;
    			break;
    		}
    	}
    	return definition;
    }
    
	/**
     * @see DataSetDefinitionPersister#saveDataSetDefinition(org.openmrs.dataset.definition.DataSetDefinition)
     */
    public DataSetDefinition saveDataSetDefinition(DataSetDefinition dataSetDefinition) {
    	
    	// TODO Remove this because it should not be necessary
    	if (!(dataSetDefinition instanceof DataExportDataSetDefinition)) { 
    		throw new APIException("Cannot save dataset definition of type: " + dataSetDefinition.getClass());
    	}
    	
    	// Cast the dataset definition 
    	DataExportDataSetDefinition dataExportDefinition = 
    		(DataExportDataSetDefinition) dataSetDefinition;
    	
    	// Save the data export to the database 
    	DataExportReportObject dataExport = 
    		(DataExportReportObject) Context.getService(ReportObjectService.class).saveReportObject(
    			dataExportDefinition.getDataExportReportObject());
    	
		dataExportDefinition.setDataExportReportObject(dataExport);
		
		return dataExportDefinition;
    }

	/**
     * @see DataSetDefinitionPersister#purgeDataSetDefinition(org.openmrs.dataset.definition.DataSetDefinition)
     */
    public void purgeDataSetDefinition(DataSetDefinition dataSetDefinition) {
    	
    	// TODO Remove this because it should not be necessary
    	if (!(dataSetDefinition instanceof DataExportDataSetDefinition)) { 
    		throw new APIException("Cannot purge dataset definition of type: " + dataSetDefinition.getClass());
    	}
    	
    	// Cast the dataset definition
    	DataExportDataSetDefinition dataExportDefinition = 
    		(DataExportDataSetDefinition) dataSetDefinition;
    	
    	// Remove the data export from the system
    	Context.getService(ReportObjectService.class).purgeReportObject(
    		dataExportDefinition.getDataExportReportObject());    	
    }


}
