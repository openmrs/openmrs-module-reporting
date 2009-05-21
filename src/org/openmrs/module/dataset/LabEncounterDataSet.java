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
package org.openmrs.module.dataset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.dataset.column.ConceptDataSetColumn;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.column.SimpleDataSetColumn;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.dataset.definition.LabEncounterDataSetDefinition;

/**
 * A dataset with one-row-per-encounter.
 * 
 * @see LabDataSetDefinition
 */
public class LabEncounterDataSet implements DataSet<Object> {
	
	private Log log = LogFactory.getLog(getClass());
		
	private LabEncounterDataSetDefinition definition;
	
	private EvaluationContext evaluationContext;
	
	private List<Encounter> encounters;
	
	public LabEncounterDataSet(LabEncounterDataSetDefinition definition, EvaluationContext context, List<Encounter> encounters) { 
		this.definition = definition;
		this.evaluationContext = context;
		this.encounters = encounters;
		
	}
	
	/**
	 * This is wrapped around (List<Obs>).iterator() This implementation is NOT thread-safe, so do
	 * not access the wrapped iterator.
	 */
	class HelperIterator implements Iterator<Map<DataSetColumn, Object>> {
		
		private Iterator<Encounter> iter;
		
		public HelperIterator(Iterator<Encounter> iter) {
			this.iter = iter;
		}
		
		/**
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return iter.hasNext();
		}
		
		/**
		 * @see java.util.Iterator#next()
		 */
		public Map<DataSetColumn, Object> next() {
			
			Map<DataSetColumn, Object> vals = new HashMap<DataSetColumn, Object>();
			//Locale locale = Context.getLocale();
			
			// Add default values for the encounter dataset
			// TODO These need to be added as columns to the dataset definition
			// TODO We need a way to sync these up
			Encounter encounter = iter.next();
				
			// Build the dataset row
			vals.put(new SimpleDataSetColumn(EncounterDataSetDefinition.ENCOUNTER_ID), 
					encounter.getEncounterId());
			vals.put(new SimpleDataSetColumn(LabEncounterDataSetDefinition.PATIENT_ID),
					encounter.getPatientId());
			vals.put(new SimpleDataSetColumn(LabEncounterDataSetDefinition.LAB_ORDER_ID), 
					"not implemented yet");
			vals.put(new SimpleDataSetColumn(LabEncounterDataSetDefinition.LAB_ORDER_DATE), 
					encounter.getEncounterDatetime());

			for (DataSetColumn column : definition.getColumns()) { 
				if (column instanceof ConceptDataSetColumn) {
					ConceptDataSetColumn conceptColumn = 
						(ConceptDataSetColumn) column;
										
					// FIXME Quick hack to get this working
					Obs value = 
						findObsByConcept(encounter, 
								((ConceptDataSetColumn) column).getConcept() );
					
					vals.put(column, value.getValueAsString(Context.getLocale()));
				}
				
			}

			
			return vals;
		}
		
		/**
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			iter.remove();
		}
		
	}
	
	/**
	 * @see org.openmrs.module.dataset.api.DataSet#iterator()
	 */
	public Iterator<Map<DataSetColumn, Object>> iterator() {
		return new HelperIterator(encounters.iterator());
	}
	
	
	/**
	 * 
	 * @return
	 */
	public List<DataSetColumn> getColumns() { 
		return definition.getColumns();
	}
	
	
	/**
	 * @return the encounters 
	 */
	public List<Encounter> getEncounters() {
		return encounters;
	}
		
	/**
	 * @return the definition
	 */
	public DataSetDefinition getDataSetDefinition() {
		return definition;
	}
		
	/**
	 * @see org.openmrs.module.dataset.DataSet#getEvaluationContext()
	 */
	public EvaluationContext getEvaluationContext() {
		return evaluationContext;
	}

	/**
	 * Convenience method used to get a desired observation by concept. This method
	 * currently returns the first observation found.  I'm assuming that the Dao 
	 * orders by date, but need to test this out.  
	 * 
	 * TODO Needs to be refactored and moved to a more appropriate place (maybe the evaluator).
	 * TODO Need to implement a more elegant solution for returning the most recent observation
	 * 
	 * @param encounter	
	 * @param concept	the concept to find 
	 * @return	the first observation with the given concept
	 */
	public Obs findObsByConcept(Encounter encounter, Concept concept) {
		Obs obs = null;
		log.info("Encounter " + encounter.getEncounterId() + " " + encounter.getObs());
		for (Obs current : encounter.getObs()) { 
			// TODO This only works when comparing conceptId, not concepts
			if (current.getConcept().getConceptId().equals(concept.getConceptId())) { 	
				// Just making sure this is the most recent observation
				//   if obs is null then we know temp is the first in the list
				//   otherwise which check which observation came first (based on obsdatetime) 
				if (obs == null || obs.getObsDatetime().compareTo(current.getObsDatetime()) < 0) { 
					obs = current;
				}
			}
		}
		return obs != null ? obs : new Obs();

	}	
		
}


