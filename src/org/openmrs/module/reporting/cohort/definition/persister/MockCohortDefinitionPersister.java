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
package org.openmrs.module.reporting.cohort.definition.persister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;

/**
 * This class returns CohortDefinitions that are persisted in memory.
 */
public class MockCohortDefinitionPersister implements CohortDefinitionPersister {

	private Log log = LogFactory.getLog(this.getClass());
	
	// Keep track of the primary keys handed out
	Integer primaryKeySequence = new Integer(1);	
	
	List<CohortDefinition> cohortDefinitions = new ArrayList<CohortDefinition>();	
	Map<Integer,CohortDefinition> indexById = new HashMap<Integer,CohortDefinition>();	
	Map<String,CohortDefinition> indexByUuid = new HashMap<String,CohortDefinition>();	
	
	/**
     * @see CohortDefinitionPersister#getCohortDefinition(Integer)
     */
    public CohortDefinition getCohortDefinition(Integer id) {
    	return indexById.get(id);
    }
    
	/**
     * @see CohortDefinitionPersister#getCohortDefinitionByUuid(String)
     */
    public CohortDefinition getCohortDefinitionByUuid(String uuid) {
    	return indexByUuid.get(uuid);
    }

	/**
     * @see CohortDefinitionPersister#getAllCohortDefinitions(boolean)
     */
    public List<CohortDefinition> getAllCohortDefinitions(boolean includeRetired) {
    	return cohortDefinitions;
    }
    
	/**
	 * @see CohortDefinitionPersister#getNumberOfCohortDefinitions(boolean)
	 */
	public int getNumberOfCohortDefinitions(boolean includeRetired) {
		return cohortDefinitions.size();
	}

	/**
     * @see CohortDefinitionPersister#getCohortDefinitionByName(String, boolean)
     */
    public List<CohortDefinition> getCohortDefinitions(String name, boolean exactMatchOnly) {
    	return cohortDefinitions;
    }
    
	/**
     * @see CohortDefinitionPersister#saveCohortDefinition(CohortDefinition)
     */
    public CohortDefinition saveCohortDefinition(CohortDefinition cohortDefinition) {

    	log.info("Saving cohort definition " + cohortDefinition.getUuid());
    	
    	// Remove the existing cohort definition
    	if (getCohortDefinitionByUuid(cohortDefinition.getUuid())!=null) { 
    		purgeCohortDefinition(cohortDefinition);
    	} 
    	// Otherwise, we set the UUID and identifier of the new cohort definition
    	else { 
    		// Set values
    		cohortDefinition.setId(primaryKeySequence++);
    		cohortDefinition.setUuid(UUID.randomUUID().toString());
    	}
    	
    	// Add the dataset definition to the list 
    	cohortDefinitions.add(cohortDefinition);
    	
    	// Index the dataset definition
    	indexById.put(cohortDefinition.getId(), cohortDefinition);
    	indexByUuid.put(cohortDefinition.getUuid(), cohortDefinition);
    	return cohortDefinition;
    }

	/**
     * @see CohortDefinitionPersister#purgeCohortDefinition(CohortDefinition)
     */
    public void purgeCohortDefinition(CohortDefinition cohortDefinition) {    	
    	log.info("Purging cohort definitions by uuid: " + cohortDefinition.getUuid());
    	indexById.remove(cohortDefinition.getId());
    	indexByUuid.remove(cohortDefinition.getUuid());
    	cohortDefinitions.remove(cohortDefinition);    	
    }   
}