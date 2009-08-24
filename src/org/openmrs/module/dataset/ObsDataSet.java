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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.column.SimpleDataSetColumn;
import org.openmrs.module.dataset.definition.ObsDataSetDefinition;
import org.openmrs.module.evaluation.EvaluationContext;

/**
 * A dataset with one-row-per-obs.
 * @see ObsDataSetDefinition
 */
public class ObsDataSet implements DataSet<Object> {
	
	private ObsDataSetDefinition definition;
	
	private EvaluationContext context;
	
	private List<Obs> data;
	
	public ObsDataSet() { }
	
	/**
	 * This is wrapped around (List<Obs>).iterator() This implementation is NOT thread-safe, so do
	 * not access the wrapped iterator.
	 */
	class HelperIterator implements Iterator<Map<DataSetColumn, Object>> {
		
		private Iterator<Obs> iter;
		
		public HelperIterator(Iterator<Obs> iter) {
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
			Locale locale = Context.getLocale();
			Obs obs = iter.next();
			Map<DataSetColumn, Object> vals = new HashMap<DataSetColumn, Object>();
			vals.put(new SimpleDataSetColumn("patientId"), obs.getPersonId());
			vals.put(new SimpleDataSetColumn("question"), obs.getConcept().getName(locale, false));
			vals.put(new SimpleDataSetColumn("questionConceptId"), obs.getConcept().getConceptId());
			vals.put(new SimpleDataSetColumn("answer"), obs.getValueAsString(locale));
			if (obs.getValueCoded() != null)
				vals.put(new SimpleDataSetColumn("answerConceptId"), obs.getValueCoded());
			vals.put(new SimpleDataSetColumn("obsDatetime"), obs.getObsDatetime());
			if (obs.getEncounter() != null)
				vals.put(new SimpleDataSetColumn("encounterId"), obs.getEncounter().getEncounterId());
			if (obs.getObsGroup() != null)
				vals.put(new SimpleDataSetColumn("obsGroupId"), obs.getObsGroup().getObsId());
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
	 * @see org.openmrs.report.DataSet#iterator()
	 */
	public Iterator<Map<DataSetColumn, Object>> iterator() {
		return new HelperIterator(data.iterator());
	}

	/**
	 * Convenience method for JSTL method.  
	 * TODO This will be removed once we get a decent solution for the dataset iterator solution.  
	 */
	public Iterator<Map<DataSetColumn, Object>> getIterator() {
		return iterator();
	}	
	
	/**
	 * @return the data
	 */
	public List<Obs> getData() {
		return data;
	}
	
	/**
	 * @param data the data to set
	 */
	public void setData(List<Obs> data) {
		this.data = data;
	}
	
	/**
	 * @return the definition
	 */
	public ObsDataSetDefinition getDefinition() {
		return definition;
	}
	
	/**
	 * @param definition the definition to set
	 */
	public void setDefinition(ObsDataSetDefinition definition) {
		this.definition = definition;
	}
	
	/**
	 * @see DataSet#getContext()
	 */
	public EvaluationContext getContext() {
		return context;
	}
	
	/**
	 * @param context the evaluationContext to set
	 */
	public void setContext(EvaluationContext context) {
		this.context = context;
	}
	
}
