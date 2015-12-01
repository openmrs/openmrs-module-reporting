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
package org.openmrs.module.reporting.dataset.definition.persister;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.definition.DataExportDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reportingcompatibility.service.ReportingCompatibilityService;
import org.openmrs.reporting.AbstractReportObject;
import org.openmrs.reporting.ReportObjectService;
import org.openmrs.reporting.export.DataExportReportObject;

/**
 * Class which manages persistence of a DataExportDataSetDefinition using legacy tables
 */
@Handler(supports={DataExportDataSetDefinition.class}, order=50)
@SuppressWarnings("deprecation")
public class DataExportDataSetDefinitionPersister implements DataSetDefinitionPersister {
		
	/**
	 * Public constructor
	 */
	public DataExportDataSetDefinitionPersister() { }
    
	/**
     * @see DataSetDefinitionPersister#getDefinition(Integer)
     */
    public DataSetDefinition getDefinition(Integer id) {
    	ReportObjectService ros = Context.getService(ReportObjectService.class);
    	DataExportReportObject dataExport = (DataExportReportObject) ros.getReportObject(id);
    	return (dataExport != null) ? new DataExportDataSetDefinition(dataExport) : null;
    }
    
	/** 
	 * @see DataSetDefinitionPersister#getDefinitionByUuid(String)
	 */
	public DataSetDefinition getDefinitionByUuid(String uuid) {	
    	for(DataSetDefinition dsd : getAllDefinitions(false)) {  // NOTE: This is very slow.  We could speed it up significantly with a custom dao
    		if (dsd.getUuid() != null && dsd.getUuid().equals(uuid)) {
    			return dsd;
    		}
    	}
    	return null;
	}

	/**
     * @see DataSetDefinitionPersister#getAllDefinitions(boolean)
     */
    public List<DataSetDefinition> getAllDefinitions(boolean includeRetired) {
    	List <DataSetDefinition> dataSetDefinitions = new Vector<DataSetDefinition>();
    	if (ModuleFactory.getStartedModulesMap().containsKey("reportingcompatibility") &&
    		ReportingConstants.GLOBAL_PROPERTY_INCLUDE_DATA_EXPORTS()) {
		    ReportObjectService ros = Context.getService(ReportObjectService.class);
		    List<AbstractReportObject> dataExports = ros.getReportObjectsByType("Data Export");
	    	
	    	for (AbstractReportObject obj : dataExports) { 
	    		DataExportReportObject dataExport = (DataExportReportObject) obj;
	    		dataExport.setUuid(obj.getUuid());	// hack to get uuids into data exports
	    		dataSetDefinitions.add(new DataExportDataSetDefinition(dataExport));    		
	    	}   
    	}
    	return dataSetDefinitions;
    }    
    
	/**
	 * @see DataSetDefinitionPersister#getNumberOfDefinitions(boolean)
	 */
	public int getNumberOfDefinitions(boolean includeRetired) {
		if (ModuleFactory.getStartedModulesMap().containsKey("reportingcompatibility")) {
			ReportObjectService ros = Context.getService(ReportObjectService.class);
		    List<AbstractReportObject> dataExports = ros.getReportObjectsByType("Data Export");
		    return dataExports.size();
		}
		return 0;
	}
    
	/**
     * @see DataSetDefinitionPersister#getDefinitions(String, boolean)
     */
    public List<DataSetDefinition> getDefinitions(String name, boolean exactMatchOnly) {
    	List<DataSetDefinition> ret = new ArrayList<DataSetDefinition>();	
    	for(DataSetDefinition dsd : getAllDefinitions(false)) {
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
     * @see DataSetDefinitionPersister#saveDefinition(DataSetDefinition)
     */
    public DataSetDefinition saveDefinition(DataSetDefinition dataSetDefinition) {
    	DataExportDataSetDefinition dsd = (DataExportDataSetDefinition) dataSetDefinition;
    	ReportObjectService ros = Context.getService(ReportObjectService.class);
    	DataExportReportObject dataExport = (DataExportReportObject) ros.saveReportObject(dsd.getDataExport());
    	dsd.setDataExport(dataExport);
		return dsd;
    }

	/**
     * @see DataSetDefinitionPersister#purgeDefinition(DataSetDefinition)
     */
    public void purgeDefinition(DataSetDefinition dataSetDefinition) {
    	DataExportDataSetDefinition dsd = (DataExportDataSetDefinition) dataSetDefinition;
    	Context.getService(ReportingCompatibilityService.class).deleteReportObject(dsd.getDataExport().getId()); 	
    }
}
