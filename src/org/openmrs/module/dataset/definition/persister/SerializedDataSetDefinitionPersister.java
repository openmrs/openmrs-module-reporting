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
import org.openmrs.api.db.SerializedObjectDAO;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.serialization.OpenmrsSerializer;

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
    // Properties
    //****************
	
	private SerializedObjectDAO dao = null;
	private OpenmrsSerializer serializer = null;

    //****************
    // Instance methods
    //****************
    
	/**
     * @see DataSetDefinitionPersister#getDataSetDefinition(Integer)
     */
    public DataSetDefinition getDataSetDefinition(Integer id) {
    	return dao.getObject(DataSetDefinition.class, id);
    }
    
	/**
     * @see DataSetDefinitionPersister#getDataSetDefinitionByUuid(String)
     */
    public DataSetDefinition getDataSetDefinitionByUuid(String uuid) {
    	return dao.getObjectByUuid(DataSetDefinition.class, uuid);
    }

	/**
     * @see DataSetDefinitionPersister#getAllDataSetDefinitions(boolean)
     */
    public List<DataSetDefinition> getAllDataSetDefinitions(boolean includeRetired) {
    	return dao.getAllObjects(DataSetDefinition.class, includeRetired);
    }

	/**
     * @see DataSetDefinitionPersister#getDataSetDefinitionByName(String, boolean)
     */
    public List<DataSetDefinition> getDataSetDefinitions(String name, boolean exactMatchOnly) {
    	return dao.getAllObjectsByName(DataSetDefinition.class, name, exactMatchOnly);
    }
    
	/**
     * @see DataSetDefinitionPersister#saveDataSetDefinition(DataSetDefinition)
     */
    public DataSetDefinition saveDataSetDefinition(DataSetDefinition dataSetDefinition) {
    	return dao.saveObject(dataSetDefinition, serializer);
    }

	/**
     * @see DataSetDefinitionPersister#purgeDataSetDefinition(DataSetDefinition)
     */
    public void purgeDataSetDefinition(DataSetDefinition dataSetDefinition) {
    	dao.purgeObject(dataSetDefinition.getId());
    }

    //****************
    // Property access
    //****************
	
    /**
     * @return the dao
     */
    public SerializedObjectDAO getDao() {
    	return dao;
    }

    /**
     * @param dao the dao to set
     */
    public void setDao(SerializedObjectDAO dao) {
    	this.dao = dao;
    }
    
    /**
     * @return the serializer
     */
    public OpenmrsSerializer getSerializer() {
    	return serializer;
    }
	
    /**
     * @param serializer the serializer to set
     */
    public void setSerializer(OpenmrsSerializer serializer) {
    	this.serializer = serializer;
    }
}
