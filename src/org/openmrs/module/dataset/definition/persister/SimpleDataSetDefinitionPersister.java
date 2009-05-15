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

//import org.openmrs.annotation.Handler;
import org.openmrs.api.APIException;
import org.openmrs.api.db.SerializedObjectDAO;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.dataset.definition.ObsDataSetDefinition;
import org.openmrs.module.dataset.definition.ProgramDataSetDefinition;

/**
 * This class returns DataSetDefinitions that have been Serialized to the database
 * This class is annotated as a Handler that supports all DataSetDefinition classes
 * Specifying no order on this indicates that this is the default means of Persisting 
 * a DataSetDefinition.  To override this behavior, any additional DataSetDefinitionPersister
 * should specify the order field on the Handler annotation.
 */
//@Handler(supports={DataSetDefinition.class})
public class SimpleDataSetDefinitionPersister implements DataSetDefinitionPersister {

    //****************
    // Properties
    //****************
	
	private SerializedObjectDAO dao = null;

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
	
	
    //****************
    // Instance methods
    //****************

	/**
     * @see org.openmrs.module.dataset.persister.DataSetDefinitionPersister#canPersist(java.lang.Class)
     */
    public Boolean canPersist(Class<? extends DataSetDefinition> clazz) {
	    
    	if (clazz.isAssignableFrom(EncounterDataSetDefinition.class) || 
    			clazz.isAssignableFrom(ObsDataSetDefinition.class) || 
    			clazz.isAssignableFrom(ProgramDataSetDefinition.class)) 
    		return true;
    	else 
    		return false;
    	
    }
    
    
	/**
     * @see org.openmrs.module.dataset.persister.DataSetDefinitionPersister#getAllDataSetDefinitions()
     */
    public List<DataSetDefinition> getAllDataSetDefinitions() {
    	throw new APIException("not implemented yet");
    }


	/**
     * @see org.openmrs.module.dataset.persister.DataSetDefinitionPersister#getDataSetDefinition(java.util.UUID)
     */
    public DataSetDefinition getDataSetDefinition(UUID uuid) {
    	throw new APIException("not implemented yet");
    }    
    
	/**
     * @see DataSetDefinitionPersister#getDataSetDefinition(java.lang.Integer)
     */
    public DataSetDefinition getDataSetDefinition(Integer id) {
    	return dao.getObject(DataSetDefinition.class, id);
    }

	/**
     * @see DataSetDefinitionPersister#getAllDataSetDefinitions(boolean)
     */
    public List<DataSetDefinition> getAllDataSetDefinitions(boolean includeRetired) {
    	return dao.getAllObjects(DataSetDefinition.class, includeRetired);
    }

	/**
     * @see DataSetDefinitionPersister#getDataSetDefinitionByName(java.lang.String)
     */
    public DataSetDefinition getDataSetDefinitionByName(String name) {
    	List<DataSetDefinition> defs = dao.getAllObjectsByName(DataSetDefinition.class, name);
    	if (defs != null && !defs.isEmpty()) {
    		if (defs.size() > 1) {
    			throw new APIException("More than one DataSetDefinition is saved with name <" + name + ">");
    		}
    		return defs.get(0);
    	}
    	return null;
    }
    
	/**
     * @see DataSetDefinitionPersister#saveDataSetDefinition(org.openmrs.dataset.definition.DataSetDefinition)
     */
    public DataSetDefinition saveDataSetDefinition(DataSetDefinition dataSetDefinition) {
    	return dao.saveObject(dataSetDefinition);
    }

	/**
     * @see DataSetDefinitionPersister#purgeDataSetDefinition(org.openmrs.dataset.definition.DataSetDefinition)
     */
    public void purgeDataSetDefinition(DataSetDefinition dataSetDefinition) {
    	dao.purgeObject(dataSetDefinition.getId());
    }

	/**
     * @see org.openmrs.module.dataset.persister.DataSetDefinitionPersister#getDatasetDefinitionByName(java.lang.String)
     */
    public DataSetDefinition getDatasetDefinitionByName(String name) {
	    // TODO Auto-generated method stub
	    return null;
    }

	/**
     * @see org.openmrs.module.dataset.persister.DataSetDefinitionPersister#saveDatasetDefinition(org.openmrs.module.dataset.definition.DataSetDefinition)
     */
    public DataSetDefinition saveDatasetDefinition(DataSetDefinition datasetDefinition) {
	    // TODO Auto-generated method stub
	    return null;
    }



}
