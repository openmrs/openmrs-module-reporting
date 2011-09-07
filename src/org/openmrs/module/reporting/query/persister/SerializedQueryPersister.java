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
package org.openmrs.module.reporting.query.persister;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.definition.service.SerializedDefinitionService;
import org.openmrs.module.reporting.query.Query;

/**
 * This class returns Querys that have been Serialized to the database
 * This class is annotated as a Handler that supports all Query classes
 * Specifying no order on this indicates that this is the default means of Persisting 
 * a Query.  To override this behavior, any additional QueryPersister
 * should specify the order field on the Handler annotation.
 */
@Handler(supports={Query.class})
public class SerializedQueryPersister implements QueryPersister {
	
	protected static Log log = LogFactory.getLog(SerializedQueryPersister.class);
	
    //****************
    // Constructor
    //****************
	protected SerializedQueryPersister() { }
	
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
     * @see QueryPersister#getQuery(Integer)
     */
    public Query getQuery(Integer id) {
    	return getService().getDefinition(Query.class, id);
    }
    
	/**
     * @see QueryPersister#getQueryByUuid(String)
     */
    public Query getQueryByUuid(String uuid) {
     	return getService().getDefinitionByUuid(Query.class, uuid);
    }

	/**
     * @see QueryPersister#getAllQuerys(boolean)
     */
    public List<Query> getAllQuerys(boolean includeRetired) {
     	return getService().getAllDefinitions(Query.class, includeRetired);
    }
    
	/**
	 * @see QueryPersister#getNumberOfQuerys(boolean)
	 */
	public int getNumberOfQuerys(boolean includeRetired) {
    	return getService().getNumberOfDefinitions(Query.class, includeRetired);
	}

	/**
     * @see QueryPersister#getQueryByName(String, boolean)
     */
    public List<Query> getQuerys(String name, boolean exactMatchOnly) {
    	return getService().getDefinitions(Query.class, name, exactMatchOnly);
    }
    
	/**
     * @see QueryPersister#saveQuery(Query)
     */
    public Query saveQuery(Query definition) {
     	return getService().saveDefinition(definition);
    }

	/**
     * @see QueryPersister#purgeQuery(Query)
     */
    public void purgeQuery(Query definition) {
    	getService().purgeDefinition(definition);
    }
}
