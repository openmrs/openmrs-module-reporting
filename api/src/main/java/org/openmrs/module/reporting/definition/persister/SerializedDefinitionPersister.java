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
package org.openmrs.module.reporting.definition.persister;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.definition.service.SerializedDefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;

/**
 * This class returns Definitions that have been Serialized to the database
 * This class is abstract as it is meant to be extended by any Persister that wishes to use Serialization
 * as it's Persistence mechanism
 */
public abstract class SerializedDefinitionPersister<T extends Definition> implements DefinitionPersister<T> {
	
	protected static Log log = LogFactory.getLog(SerializedDefinitionPersister.class);
	
    //****************
    // Constructor
    //****************
	protected SerializedDefinitionPersister() { }
	
    //****************
    // Instance methods
    //****************
	
	/**
	 * @return the Base Class that the Persister manages
	 */
	public abstract Class<T> getBaseClass();
	
	/**
	 * Utility method that returns the SerializedDefinitionService
	 */
	public SerializedDefinitionService getService() {
		return Context.getService(SerializedDefinitionService.class);
	}

	/**
     * @see DefinitionPersister#getDefinition(Integer)
     */
    public T getDefinition(Integer id) {
    	return getService().getDefinition(getBaseClass(), id);
    }
    
	/**
     * @see DefinitionPersister#getDefinitionByUuid(String)
     */
    public T getDefinitionByUuid(String uuid) {
     	return getService().getDefinitionByUuid(getBaseClass(), uuid);
    }

	/**
     * @see DefinitionPersister#getAllDefinitions(boolean)
     */
    public List<T> getAllDefinitions(boolean includeRetired) {
     	return getService().getAllDefinitions(getBaseClass(), includeRetired);
    }
    
	/**
	 * @see DefinitionPersister#getNumberOfDefinitions(boolean)
	 */
	public int getNumberOfDefinitions(boolean includeRetired) {
    	return getService().getNumberOfDefinitions(getBaseClass(), includeRetired);
	}

	/**
     * @see DefinitionPersister#getDefinitionByName(String, boolean)
     */
    public List<T> getDefinitions(String name, boolean exactMatchOnly) {
    	return getService().getDefinitions(getBaseClass(), name, exactMatchOnly);
    }
    
	/**
     * @see DefinitionPersister#saveDefinition(Definition)
     */
	definition.setName(definition.getName().replaceAll("&", "and"));
    public T saveDefinition(T definition) {
     	return getService().saveDefinition(definition);
    }

	/**
     * @see DefinitionPersister#purgeDefinition(Definition)
     */
    public void purgeDefinition(T definition) {
    	getService().purgeDefinition(definition);
    }
}
