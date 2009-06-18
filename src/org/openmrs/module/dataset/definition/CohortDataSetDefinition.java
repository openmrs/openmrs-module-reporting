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
package org.openmrs.module.dataset.definition;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.openmrs.Cohort;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.dataset.CohortDataSet;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.column.SimpleDataSetColumn;
import org.openmrs.module.evaluation.parameter.Mapped;

/**
 * Metadata that defines a CohortDataSet. (I.e. a list of cohorts, each of which has a name)
 * <p>
 * For example a CohortDatasetDefinition might represent:<br/>
 * "1. Total # of Patients" -> (CohortDefinition) everyone <br/>
 * "1.a. Male Adults" -> (CohortDefinition) Male AND Adult<br/>
 * "1.b. Female Adults" -> (CohortDefinition) Female AND Adult<br/>
 * "1.c. Male Children" -> (CohortDefinition) Male AND NOT Adult<br/>
 * "1.d. Female Children" -> (CohortDefinition) Female AND NOT Adult ...
 * 
 * @see CohortDataSet
 * @see CohortDataSetEvaluator
 */
public class CohortDataSetDefinition extends BaseDataSetDefinition {
	
	private static final long serialVersionUID = -658417752199413012L;
	
	// TODO Consolidate down to DataSetColumn
	//private Map<String, String> descriptions;
	private Map<String, Mapped<CohortDefinition>> strategies;
	
	/**
	 * Default constructor
	 */
	public CohortDataSetDefinition() {
		strategies = new LinkedHashMap<String, Mapped<CohortDefinition>>();
		//descriptions = new LinkedHashMap<String, String>();
	}
	
	/**
	 * Public constructor
	 * 
	 * @param name
	 * @param description
	 * @param questions
	 */
	public CohortDataSetDefinition(String name, String description) { 
		this();
		this.setName(name);
		this.setDescription(description);
	}	
	
	
	

	/**
	 * Add the given cohort as a "column" to this definition with the given key. The name is also
	 * added as the description.
	 * 
	 * @param name key to refer by which to refer to this cohort
	 * @param cohortDefinition	the cohort definition
	 */
	public void addStrategy(String name, CohortDefinition cohortDefinition, String mappings) {
		addStrategy(name, name, new Mapped<CohortDefinition>(cohortDefinition, mappings));
	}
    
    
	/**
	 * Add the given cohort as a "column" to this definition with the given key. The name is also
	 * added as the description.
	 * 
	 * @param name key to refer by which to refer to this cohort
	 * @param cohortDefinition The patients for this column
	 */
	public void addStrategy(String name, Mapped<CohortDefinition> cohortDefinition) {
		addStrategy(name, name, cohortDefinition);
	}	
	
	/**
	 * Add the given cohort as a "column" to this definition with the given key and the given
	 * description.
	 * 
	 * @param name
	 * @param description
	 * @param cohortDefinition
	 */
	public void addStrategy(String name, String description, Mapped<CohortDefinition> cohortDefinition) {
		strategies.put(name, cohortDefinition);
		//descriptions.put(name, description);
	}
	
	/**
	 * @see org.openmrs.module.dataset.definition.DataSetDefinition#getColumnKeys()
	 */
	public List<String> getColumnKeys() {
		return new Vector<String>(strategies.keySet());
	}
	
	/**
	 * @see org.openmrs.module.dataset.definition.DataSetDefinition#getColumnDatatypes()
	 */
	@SuppressWarnings("unchecked")
	public List<Class> getColumnDatatypes() {
		//return (List<Class>) Collections.nCopies(strategies.size(), Cohort.class);
		List<Class> ret = new ArrayList<Class>();
		for (int i = strategies.size(); i > 0; --i)
			ret.add(Cohort.class);
		return ret;
	}
	

	/**
	 * Sets a description for the cohort name if it exists. Returns true if a cohort exists with the
	 * specified name else returns false.
	 * 
	 * @param name
	 * @param description
	 * @return true if a cohort exists with the specified name, false otherwise
	public boolean setDescription(String name, String description) {
		if (strategies.containsKey(name)) {
			descriptions.put(name, description);
			return true;
		}
		return false;
	}
	 */
	
	/**
	 * Returns a description for the @param cohort strategy name.
	 * 
	 * @param name
	 * @return
	public String getDescription(String name) {
		return descriptions.get(name);
	}
	 */
	
	/**
	 * Returns the map of cohort strategy names, descriptions.
	 * 
	 * @return a <code>Map<String, String></code> of the strategy names and descriptions
	public Map<String, String> getDescriptions() {
		return this.descriptions;
	}
	 */
	
	/**
	 * Get the key-value pairs of names to defined cohorts
	 * 
	 * @return
	 */
	public Map<String, Mapped<CohortDefinition>> getStrategies() {
		return strategies;
	}
	
	/**
	 * Set the key-value pairs of names to cohorts
	 * 
	 * @param strategies
	 */
	public void setStrategies(Map<String, Mapped<CohortDefinition>> strategies) {
		this.strategies = strategies;
	}
	
	/**
	 * Set the key-value pairs of names to cohort descriptions
	 * 
	 * @param descriptions
	public void setDescriptions(Map<String, String> descriptions) {
		this.descriptions = descriptions;
	}
	 */
	
	/**
     * @see org.openmrs.module.dataset.definition.DataSetDefinition#getColumns()
     */
    public List<DataSetColumn> getColumns() {
    	List<DataSetColumn> columns = new ArrayList<DataSetColumn>();
    	for (String key : strategies.keySet()) {
    		CohortDefinition cd = 
    			strategies.get(key).getParameterizable();
    		columns.add(new SimpleDataSetColumn(key, cd.getName(), Cohort.class));
    		//columns.add(new CohortDataSetColumn(key, strategies.get(key)));
       	}
    	return columns;
    }
	
    
	
	
}
