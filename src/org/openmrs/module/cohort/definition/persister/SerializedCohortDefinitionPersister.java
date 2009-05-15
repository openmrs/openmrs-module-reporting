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

import java.util.List;

import org.openmrs.annotation.Handler;
import org.openmrs.api.APIException;
import org.openmrs.api.db.SerializedObjectDAO;
import org.openmrs.module.cohort.definition.CohortDefinition;

/**
 * This class returns CohortDefinitions that have been Serialized to the database
 * This class is annotated as a Handler that supports all CohortDefinition classes
 * Specifying no order on this indicates that this is the default means of Persisting 
 * a CohortDefinition.  To override this behavior, any additional CohortDefinitionPersister
 * should specify the order field on the Handler annotation.
 */
@Handler(supports={CohortDefinition.class})
public class SerializedCohortDefinitionPersister implements CohortDefinitionPersister {

    //****************
    // Properties
    //****************
	
	private SerializedObjectDAO dao = null;

    //****************
    // Instance methods
    //****************
    
	/**
     * @see CohortDefinitionPersister#getCohortDefinition(java.lang.Integer)
     */
    public CohortDefinition getCohortDefinition(Integer id) {
    	return dao.getObject(CohortDefinition.class, id);
    }

	/**
     * @see CohortDefinitionPersister#getAllCohortDefinitions(boolean)
     */
    public List<CohortDefinition> getAllCohortDefinitions(boolean includeRetired) {
    	return dao.getAllObjects(CohortDefinition.class, includeRetired);
    }

	/**
     * @see CohortDefinitionPersister#getCohortDefinitionByName(java.lang.String)
     */
    public CohortDefinition getCohortDefinitionByName(String name) {
    	List<CohortDefinition> defs = dao.getAllObjectsByName(CohortDefinition.class, name);
    	if (defs != null && !defs.isEmpty()) {
    		if (defs.size() > 1) {
    			throw new APIException("More than one CohortDefinition is saved with name <" + name + ">");
    		}
    		return defs.get(0);
    	}
    	return null;
    }
    
	/**
     * @see CohortDefinitionPersister#saveCohortDefinition(org.openmrs.module.cohort.definition.CohortDefinition)
     */
    public CohortDefinition saveCohortDefinition(CohortDefinition cohortDefinition) {
    	return dao.saveObject(cohortDefinition);
    }

	/**
     * @see CohortDefinitionPersister#purgeCohortDefinition(org.openmrs.module.cohort.definition.CohortDefinition)
     */
    public void purgeCohortDefinition(CohortDefinition cohortDefinition) {
    	dao.purgeObject(cohortDefinition.getId());
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
}
