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
import java.util.Vector;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.CohortDAO;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.StaticCohortDefinition;

/**
 * This class provides access to {@link org.openmrs.module.cohort} objects that are saved in the cohort
 * table, but exposes them as {@link CohortDefinition}
 */
@Handler(supports={StaticCohortDefinition.class}, order=100)
public class StaticCohortDefinitionPersister implements CohortDefinitionPersister {
	
    //****************
    // Properties
    //****************
	
	private CohortDAO dao = null;

    //****************
    // Instance methods
    //****************

	/**
     * @see CohortDefinitionPersister#getCohortDefinition(java.lang.Integer)
     */
    public CohortDefinition getCohortDefinition(Integer id) {
    	return new StaticCohortDefinition(Context.getCohortService().getCohort(id));
    }
    
	/**
     * @see CohortDefinitionPersister#getCohortDefinitionByUuid(java.lang.String)
     */
    public CohortDefinition getCohortDefinitionByUuid(String uuid) {
    	return new StaticCohortDefinition(Context.getCohortService().getCohortByUuid(uuid));
    }
    
	/**
     * @see CohortDefinitionPersister#getAllCohortDefinitions(boolean)
     */
    public List<CohortDefinition> getAllCohortDefinitions(boolean includeRetired) {
		List<CohortDefinition> ret = new Vector<CohortDefinition>();
		for (Cohort c : Context.getCohortService().getAllCohorts(includeRetired)) {
			ret.add(new StaticCohortDefinition(c));
		}
		return ret;
    }

	/**
     * @see CohortDefinitionPersister#getCohortDefinitionByName(java.lang.String)
     */
    public CohortDefinition getCohortDefinitionByName(String name) {
    	Cohort c = Context.getCohortService().getCohort(name);
    	if (c != null) {
    		return new StaticCohortDefinition(c);
    	}
    	return null;
    }
    
	/**
     * @see CohortDefinitionPersister#saveCohortDefinition(CohortDefinition)
     */
    public CohortDefinition saveCohortDefinition(CohortDefinition cohortDefinition) {
    	if (cohortDefinition != null) {
	    	if (cohortDefinition instanceof StaticCohortDefinition) {
				StaticCohortDefinition def = (StaticCohortDefinition) cohortDefinition;
				Context.getCohortService().saveCohort(def.getCohort());
				return def;
	    	}
	    	else {
	    		throw new APIException("Unable to save CohortDefinition of type: " + cohortDefinition.getClass());
	    	}
    	}
    	return null;
    }

	/**
     * @see CohortDefinitionPersister#purgeCohortDefinition(CohortDefinition)
     */
    public void purgeCohortDefinition(CohortDefinition cohortDefinition) {
    	if (cohortDefinition instanceof StaticCohortDefinition) {
			StaticCohortDefinition def = (StaticCohortDefinition) cohortDefinition;
			Context.getCohortService().purgeCohort(def.getCohort());
    	}
    	else {
    		throw new APIException("Unable to purge CohortDefinition of type: " + cohortDefinition.getClass());
    	}
    }
    
    //****************
    // Property Access
    //****************
	
    /**
     * @return the dao
     */
    public CohortDAO getDao() {
    	return dao;
    }
	
    /**
     * @param dao the dao to set
     */
    public void setDao(CohortDAO dao) {
    	this.dao = dao;
    }
}
