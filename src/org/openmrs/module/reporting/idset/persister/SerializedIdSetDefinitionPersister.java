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
package org.openmrs.module.reporting.idset.persister;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.definition.service.SerializedDefinitionService;
import org.openmrs.module.reporting.idset.definition.IdSetDefinition;

/**
 * This class returns IdSetDefinitions that have been Serialized to the database
 * This class is annotated as a Handler that supports all IdSetDefinition classes
 * Specifying no order on this indicates that this is the default means of Persisting 
 * a IdSetDefinition.  To override this behavior, any additional IdSetDefinitionPersister
 * should specify the order field on the Handler annotation.
 */
@Handler(supports={IdSetDefinition.class})
public class SerializedIdSetDefinitionPersister implements IdSetDefinitionPersister {
	
	protected static Log log = LogFactory.getLog(SerializedIdSetDefinitionPersister.class);
	
    //****************
    // Constructor
    //****************
	protected SerializedIdSetDefinitionPersister() { }
	
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
     * @see IdSetDefinitionPersister#getIdSetDefinition(Integer)
     */
    public IdSetDefinition getIdSetDefinition(Integer id) {
    	return getService().getDefinition(IdSetDefinition.class, id);
    }
    
	/**
     * @see IdSetDefinitionPersister#getIdSetDefinitionByUuid(String)
     */
    public IdSetDefinition getIdSetDefinitionByUuid(String uuid) {
     	return getService().getDefinitionByUuid(IdSetDefinition.class, uuid);
    }

	/**
     * @see IdSetDefinitionPersister#getAllIdSetDefinitions(boolean)
     */
    public List<IdSetDefinition> getAllIdSetDefinitions(boolean includeRetired) {
     	return getService().getAllDefinitions(IdSetDefinition.class, includeRetired);
    }
    
	/**
	 * @see IdSetDefinitionPersister#getNumberOfIdSetDefinitions(boolean)
	 */
	public int getNumberOfIdSetDefinitions(boolean includeRetired) {
    	return getService().getNumberOfDefinitions(IdSetDefinition.class, includeRetired);
	}

	/**
     * @see IdSetDefinitionPersister#getIdSetDefinitionByName(String, boolean)
     */
    public List<IdSetDefinition> getIdSetDefinitions(String name, boolean exactMatchOnly) {
    	return getService().getDefinitions(IdSetDefinition.class, name, exactMatchOnly);
    }
    
	/**
     * @see IdSetDefinitionPersister#saveIdSetDefinition(IdSetDefinition)
     */
    public IdSetDefinition saveIdSetDefinition(IdSetDefinition definition) {
     	return getService().saveDefinition(definition);
    }

	/**
     * @see IdSetDefinitionPersister#purgeIdSetDefinition(IdSetDefinition)
     */
    public void purgeIdSetDefinition(IdSetDefinition definition) {
    	getService().purgeDefinition(definition);
    }
}
