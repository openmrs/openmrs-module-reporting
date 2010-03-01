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
package org.openmrs.module.reporting.dataset.definition;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptSet;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.column.ConceptDataSetColumn;
import org.openmrs.module.reporting.dataset.column.DataSetColumn;
import org.openmrs.module.reporting.dataset.column.SimpleDataSetColumn;

/**
 * Definition of a dataset that produces one-row-per-lab encounter table.
 * @see EncounterDataSet
 */
public class LabEncounterDataSetDefinition extends BaseDataSetDefinition {
	
	private Log log = LogFactory.getLog(getClass());
	
    // ***** FIXED COLUMNS *****
	public static DataSetColumn PATIENT_ID = new SimpleDataSetColumn("patientId", Integer.class);
	public static DataSetColumn ENCOUNTER_ID = new SimpleDataSetColumn("encounter_id", Integer.class);
	public static DataSetColumn LAB_ORDER_DATE = new SimpleDataSetColumn("lab_order_date", Date.class);

	// Lab sets to include 
	private Collection<Integer> concepts;
	
	/**
	 * Default Constructor
	 */
	public LabEncounterDataSetDefinition() {
		super();
	}
	
	/**
	 * Constructor
	 * @param concepts
	 */
	public LabEncounterDataSetDefinition(List<Integer> concepts) {
		this();
		this.concepts = concepts;
	}
	
	/**
     * @see DataSetDefinition#getColumns()
	 */
    public List<DataSetColumn> getColumns() {
    	
    	List<DataSetColumn> columns = Arrays.asList(PATIENT_ID, ENCOUNTER_ID, LAB_ORDER_DATE);
    	for (Integer conceptId : concepts) { 
    		try { 
    			Concept concept = Context.getConceptService().getConcept(conceptId);
    			// TODO Need to convert concept data type to data type    			
    			if (concept.isSet()) {    	
    				List<ConceptSet> conceptSets = Context.getConceptService().getConceptSetsByConcept(concept);
    				for (ConceptSet childConcept : conceptSets) {     					
    	    			DataSetColumn column = new ConceptDataSetColumn(childConcept.getConcept());    	    			
    	    			columns.add(column);    	    			
    				} 	
    			} 
    			else {
	    			columns.add(new ConceptDataSetColumn(concept));
    			}
    		} catch (Exception e) { 
    			log.error("Error occurred while looking up concept / concept set by ID " + conceptId, e);
    			throw new APIException("Invalid concept ID " + conceptId + " : " + e.getMessage(), e);
    		}
    	}    	
    	return columns;
	}
    
    //****** PROPERTY ACCESS ******

	/**
	 * @return the concepts
	 */
	public Collection<Integer> getConcepts() {
		if (concepts == null) {
			concepts = new Vector<Integer>();
		}
		return concepts;
	}

	/**
	 * @param concepts the concepts to set
	 */
	public void setConcepts(Collection<Integer> concepts) {
		this.concepts = concepts;
	}	
}
