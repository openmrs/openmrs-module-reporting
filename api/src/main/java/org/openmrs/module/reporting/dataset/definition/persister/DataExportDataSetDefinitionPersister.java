/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.dataset.definition.persister;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.definition.DataExportDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.reporting.AbstractReportObject;
import org.openmrs.reporting.ReportObjectService;
import org.openmrs.reporting.export.DataExportReportObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
        Context.getService(ReportObjectService.class).purgeReportObject(dsd.getDataExport());
    }
}
