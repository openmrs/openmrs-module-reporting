/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
