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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.jfree.util.Log;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.column.SimpleDataSetColumn;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.EncounterDataSetDefinition;

/**
 * A dataset with one-row-per-encounter.
 * 
 * @see EncounterDataSetDefinition
 */
public class EncounterDataSet implements DataSet<Object> {
	
	private EncounterDataSetDefinition definition;
	
	private EvaluationContext evaluationContext;
	
	private List<Encounter> encounters;
	
	public EncounterDataSet(EncounterDataSetDefinition definition, EvaluationContext context, List<Encounter> encounters) { 
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
			vals.put(new SimpleDataSetColumn(EncounterDataSetDefinition.ENCOUNTER_TYPE), 
				encounter.getEncounterType().getName());
			vals.put(new SimpleDataSetColumn(EncounterDataSetDefinition.FORM), 
				(encounter.getForm() != null) ? encounter.getForm().getName() : "none");
			vals.put(new SimpleDataSetColumn(EncounterDataSetDefinition.LOCATION), 
				encounter.getLocation().getName());
			vals.put(new SimpleDataSetColumn(EncounterDataSetDefinition.PERSON), 
				encounter.getPatient().getPatientId());
			vals.put(new SimpleDataSetColumn(EncounterDataSetDefinition.GENDER), 
				encounter.getPatient().getGender());	
			vals.put(new SimpleDataSetColumn(EncounterDataSetDefinition.AGE), 
				encounter.getPatient().getAge());

			// TODO It's not possible to retrieve ALL observations because we wouldn't know 
			// the number of columns for the dataset definition.  			
			//for (Obs obs : encounter.getAllObs()) { 
			//	String question = obs.getConcept().getBestName(locale).getName();
			//	vals.put(question, obs.getValueAsString(locale));				
			//	vals.put(question + " Datetime", obs.getObsDatetime());				
			//	vals.put(question + " Obs Group", obs.getObsGroup());
			//}
			
			// For now, we need to allow the user to specify which observations they want to see
			// for each encounter.
			//Map<DataSetColumn, Object> ret = new HashMap<DataSetColumn, Object>();
			//for (DataSetColumn column : definition.getColumns()) {
			//	ret.put(column, vals.get(column.getKey()));
			//}
			//return ret;
			
			
			//Map<DataSetColumn, Object> ret = new HashMap<DataSetColumn, Object>();
			//for (DataSetColumn column : definition.getColumns()) {
			//	ret.put(column, vals.get(column.getKey()));
			//}
			//return ret;
			
			
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
	 * Convenience method for JSTL method.  
	 * TODO This will be removed once we get a decent solution for the dataset iterator solution.  
	 */
	public Iterator<Map<DataSetColumn, Object>> getIterator() {
		return iterator();
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
		
}
