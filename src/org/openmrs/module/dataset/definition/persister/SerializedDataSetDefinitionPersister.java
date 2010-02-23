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

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.definition.service.SerializedDefinitionService;

/**
 * This class returns DataSetDefinitions that have been Serialized to the database
 * This class is annotated as a Handler that supports all DataSetDefinition classes
 * Specifying no order on this indicates that this is the default means of Persisting 
 * a DataSetDefinition.  To override this behavior, any additional DataSetDefinitionPersister
 * should specify the order field on the Handler annotation.
 */
@Handler(supports={DataSetDefinition.class}, order=100)
public class SerializedDataSetDefinitionPersister implements DataSetDefinitionPersister {

    //****************
    // Constructor
    //****************
	private SerializedDataSetDefinitionPersister() { }
	
    //****************
    // Instance methods
    //****************
	
	/**
	 * Utility method that returns the SerializedDefinitionService
	 */
	public SerializedDefinitionService getService() {
		return Context.getService(SerializedDefinitionService.class);
	}

	/**
     * @see DataSetDefinitionPersister#getDataSetDefinition(Integer)
     */
    public DataSetDefinition getDataSetDefinition(Integer id) {
    	return getService().getDefinition(DataSetDefinition.class, id);
    }
    
	/**
     * @see DataSetDefinitionPersister#getDataSetDefinitionByUuid(String)
     */
    public DataSetDefinition getDataSetDefinitionByUuid(String uuid) {
     	return getService().getDefinitionByUuid(DataSetDefinition.class, uuid);
    }

	/**
     * @see DataSetDefinitionPersister#getAllDataSetDefinitions(boolean)
     */
    public List<DataSetDefinition> getAllDataSetDefinitions(boolean includeRetired) {
     	return getService().getAllDefinitions(DataSetDefinition.class, includeRetired);
    }
    
	/**
	 * @see DataSetDefinitionPersister#getNumberOfDataSetDefinitions(boolean)
	 */
	public int getNumberOfDataSetDefinitions(boolean includeRetired) {
    	return getService().getNumberOfDefinitions(DataSetDefinition.class, includeRetired);
	}

	/**
     * @see DataSetDefinitionPersister#getDataSetDefinitionByName(String, boolean)
     */
    public List<DataSetDefinition> getDataSetDefinitions(String name, boolean exactMatchOnly) {
    	return getService().getDefinitions(DataSetDefinition.class, name, exactMatchOnly);
    }
    
	/**
     * @see DataSetDefinitionPersister#saveDataSetDefinition(DataSetDefinition)
     */
    public DataSetDefinition saveDataSetDefinition(DataSetDefinition dataSetDefinition) {
     	return getService().saveDefinition(dataSetDefinition);
    }

	/**
     * @see DataSetDefinitionPersister#purgeDataSetDefinition(DataSetDefinition)
     */
    public void purgeDataSetDefinition(DataSetDefinition dataSetDefinition) {
    	getService().purgeDefinition(dataSetDefinition);
    }
}
