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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.definition.service.SerializedDefinitionService;

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
    // Instance methods
    //****************
	
	/**
	 * Utility method that returns the SerializedDefinitionService
	 */
	public SerializedDefinitionService getService() {
		return Context.getService(SerializedDefinitionService.class);
	}

	/**
     * @see CohortDefinitionPersister#getCohortDefinition(Integer)
     */
    public CohortDefinition getCohortDefinition(Integer id) {
    	return getService().getDefinition(CohortDefinition.class, id);
    }
    
	/**
     * @see CohortDefinitionPersister#getCohortDefinitionByUuid(String)
     */
    public CohortDefinition getCohortDefinitionByUuid(String uuid) {
     	return getService().getDefinitionByUuid(CohortDefinition.class, uuid);
    }

	/**
     * @see CohortDefinitionPersister#getAllCohortDefinitions(boolean)
     */
    public List<CohortDefinition> getAllCohortDefinitions(boolean includeRetired) {
     	return getService().getAllDefinitions(CohortDefinition.class, includeRetired);
    }
    
	/**
	 * @see CohortDefinitionPersister#getNumberOfCohortDefinitions(boolean)
	 */
	public int getNumberOfCohortDefinitions(boolean includeRetired) {
    	return getService().getNumberOfDefinitions(CohortDefinition.class, includeRetired);
	}

	/**
     * @see CohortDefinitionPersister#getCohortDefinitionByName(String, boolean)
     */
    public List<CohortDefinition> getCohortDefinitions(String name, boolean exactMatchOnly) {
    	return getService().getDefinitions(CohortDefinition.class, name, exactMatchOnly);
    }
    
	/**
     * @see CohortDefinitionPersister#saveCohortDefinition(CohortDefinition)
     */
    public CohortDefinition saveCohortDefinition(CohortDefinition cohortDefinition) {
     	return getService().saveDefinition(cohortDefinition);
    }

	/**
     * @see CohortDefinitionPersister#purgeCohortDefinition(CohortDefinition)
     */
    public void purgeCohortDefinition(CohortDefinition cohortDefinition) {
    	getService().purgeDefinition(cohortDefinition);
    }
}
