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
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptSet;
import org.openmrs.Obs;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.dataset.column.ConceptDataSetColumn;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.column.SimpleDataSetColumn;

/**
 * Definition of a dataset that produces one-row-per-lab encounter table. 
 *
 * @see EncounterDataSet
 */
public class LabEncounterDataSetDefinition extends BaseDataSetDefinition {
	
	private Log log = LogFactory.getLog(getClass());
	
	
    // Constants 
	public static final String PATIENT_ID		= "patient_id";
	public static final String ENCOUNTER_ID		= "encounter_id";
	public static final String LAB_ORDER_ID 	= "lab_order_id";
	public static final String LAB_ORDER_DATE 	= "lab_order_date";
	public static final String LAB_RESULT_ID 	= "lab_result_id";
	public static final String LAB_RESULT_DATE 	= "lab_result_date";
	
	// Lab sets to include 
	private Collection<Integer> concepts;
	
	/**
	 * Constructor
	 */
	public LabEncounterDataSetDefinition() {
		concepts = new Vector<Integer>();
	}
	
	public LabEncounterDataSetDefinition(List<Integer> concepts) { 
		this.concepts = concepts;
	}
	
		
	private static String [] defaultColumnKeys = { 		
		PATIENT_ID, 
		ENCOUNTER_ID, 
		//LAB_ORDER_ID,
		LAB_ORDER_DATE,
		
	};
		
	private static Class [] defaultColumnDatatypes = { 
		Integer.class, 
		Integer.class, 
		//Integer.class, 
		Date.class, 
	};
		
	
	/**
     * @see org.openmrs.module.dataset.definition.DataSetDefinition#getColumns()
     * TODO Move to BaseDataSetDefinition
	 */
    public List<DataSetColumn> getColumns() {    	
    	List<DataSetColumn> columns = new ArrayList<DataSetColumn>();
    	for (int i = 0; i < defaultColumnKeys.length; i++) {     		
    		DataSetColumn column = 
    			new SimpleDataSetColumn(defaultColumnKeys[i], defaultColumnDatatypes[i]);
    		columns.add(column);
    	}
    	for(Integer conceptId : concepts) { 
    		try { 
    			Concept concept = Context.getConceptService().getConcept(conceptId);
    			// TODO Need to convert concept data type to data type    			
    			if (concept.isSet()) {    	
    				
    				List<ConceptSet> conceptSets = 
    					Context.getConceptService().getConceptSetsByConcept(concept);
    				
    				
    				for (ConceptSet childConcept : conceptSets) {     					
    	    			DataSetColumn column = 
    	    				new ConceptDataSetColumn(childConcept.getConcept());    	    			
    	    			columns.add(column);    	    			
    				} 
    				
    				/*
    				for (ConceptSet childConcept : concept.getConceptSets()) {     					
    	    			DataSetColumn column = 
    	    				new ConceptDataSetColumn(childConcept.getConcept());    	    			
    	    			columns.add(column);    	    			
    				} 
    				*/ 				
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
}
