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
package org.openmrs.module.cohort.definition.persister;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.db.SerializedObject;
import org.openmrs.api.db.SerializedObjectDAO;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.SerializedObjectCohortDefinition;
import org.openmrs.serialization.OpenmrsSerializer;

/**
 * This class returns CohortDefinitions that have been Serialized to the database
 * This class is annotated as a Handler that supports all CohortDefinition classes
 * Specifying no order on this indicates that this is the default means of Persisting 
 * a CohortDefinition.  To override this behavior, any additional CohortDefinitionPersister
 * should specify the order field on the Handler annotation.
 */
@Handler(supports={CohortDefinition.class})
public class SerializedCohortDefinitionPersister implements CohortDefinitionPersister {
	
	protected static Log log = LogFactory.getLog(SerializedCohortDefinitionPersister.class);
	
    //****************
    // Constructor
    //****************
	private SerializedCohortDefinitionPersister() { }
	
    //****************
    // Properties
    //****************
	
	private SerializedObjectDAO dao = null;
	private OpenmrsSerializer serializer = null;
	
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
	
	
    //****************
    // Instance methods
    //****************

	/**
     * @see CohortDefinitionPersister#getCohortDefinition(Integer)
     */
    public CohortDefinition getCohortDefinition(Integer id) {
    	SerializedObject so = dao.getSerializedObject(id);
    	try {
    		return dao.convertSerializedObject(CohortDefinition.class, so);
    	}
    	catch (Exception e) {
    		return new SerializedObjectCohortDefinition(so);
    	}
    }
    
	/**
     * @see CohortDefinitionPersister#getCohortDefinitionByUuid(String)
     */
    public CohortDefinition getCohortDefinitionByUuid(String uuid) {
    	SerializedObject so = dao.getSerializedObjectByUuid(uuid);
    	try {
    		return dao.convertSerializedObject(CohortDefinition.class, so);
    	}
    	catch (Exception e) {
    		return new SerializedObjectCohortDefinition(so);
    	}
    }

	/**
     * @see CohortDefinitionPersister#getAllCohortDefinitions(boolean)
     */
    public List<CohortDefinition> getAllCohortDefinitions(boolean includeRetired) {
    	List<CohortDefinition> ret = new ArrayList<CohortDefinition>();
    	for (SerializedObject so : dao.getAllSerializedObjects(CohortDefinition.class, includeRetired)) {
        	try {
        		ret.add(dao.convertSerializedObject(CohortDefinition.class, so));
        	}
        	catch (Exception e) {
        		ret.add(new SerializedObjectCohortDefinition(so));
        	}
    	}
    	return ret;
    }

	/**
     * @see CohortDefinitionPersister#getCohortDefinitionByName(String, boolean)
     */
    public List<CohortDefinition> getCohortDefinitions(String name, boolean exactMatchOnly) {
    	List<CohortDefinition> ret = new ArrayList<CohortDefinition>();
    	for (SerializedObject so : dao.getAllSerializedObjectsByName(CohortDefinition.class, name, exactMatchOnly)) {
        	try {
        		ret.add(dao.convertSerializedObject(CohortDefinition.class, so));
        	}
        	catch (Exception e) {
        		ret.add(new SerializedObjectCohortDefinition(so));
        	}
    	}
    	return ret;
    }
    
	/**
     * @see CohortDefinitionPersister#saveCohortDefinition(CohortDefinition)
     */
    public CohortDefinition saveCohortDefinition(CohortDefinition cohortDefinition) {
    	if (cohortDefinition instanceof SerializedObjectCohortDefinition) {
    		SerializedObjectCohortDefinition socd = (SerializedObjectCohortDefinition)cohortDefinition;
    		CohortDefinition newDef = dao.convertSerializedObject(CohortDefinition.class, socd.toSerializedObject());
    		return dao.saveObject(newDef);
    	}
    	return dao.saveObject(cohortDefinition, serializer);
    }

	/**
     * @see CohortDefinitionPersister#purgeCohortDefinition(CohortDefinition)
     */
    public void purgeCohortDefinition(CohortDefinition cohortDefinition) {
    	dao.purgeObject(cohortDefinition.getId());
    }
}
