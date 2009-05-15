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
import java.util.Map;

import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.definition.DataExportDataSetDefinition;

/**
 * A dataset that is based on an OpenMRS data export.
 * 
 * @see DataExportDataSetDefinition
 */
public class DataExportDataSet implements DataSet<Object> {
	
	/* Data export data set definition (includes data export columns) */
	private DataExportDataSetDefinition definition;
	
	/* Evaluation context */
	private EvaluationContext evaluationContext;
	
	/* The actual data generated from the data export */
	private List<Map<String, Object>> data;

	/**
	 * Public constructor
	 */
	@Deprecated
	public DataExportDataSet() { }

	/**
	 * Public constructor
	 */
	public DataExportDataSet(DataExportDataSetDefinition definition) { 
		this.definition = definition;
	}
	
	
	
	/**
	 * @see org.openmrs.module.dataset.api.DataSet#iterator()
	 */
	public Iterator<Map<DataSetColumn, Object>> iterator() {
		return new HelperIterator(data.iterator());
	}
	
	/**
	 * This is wrapped around (List<Map<String, Object>).iterator() 
	 * This implementation is NOT thread-safe, so do not access the wrapped iterator.
	 */
	class HelperIterator implements Iterator<Map<DataSetColumn, Object>> {
		
		private Iterator<Map<String, Object>> iter;
		
		public HelperIterator(Iterator<Map<String, Object>> iter) {
			this.iter = iter;
		}
		
		public boolean hasNext() {
			return iter.hasNext();
		}
		
		public Map<DataSetColumn, Object> next() {
			Map<String, Object> vals = iter.next();
			Map<DataSetColumn, Object> ret = new HashMap<DataSetColumn, Object>();
			for (DataSetColumn c : definition.getColumns()) {
				ret.put(c, vals.get(c.getKey()));
			}
			return ret;
		}
		
		public void remove() {
			iter.remove();
		}
		
	}
	
	/**
	 * @return the data
	 */
	public List<Map<String, Object>> getData() {
		return data;
	}
	
	/**
	 * @param data the data to set
	 */
	public void setData(List<Map<String, Object>> data) {
		this.data = data;
	}
	
	/**
	 * @return the definition
	 */
	public DataExportDataSetDefinition getDataSetDefinition() {
		return definition;
	}
	
	/**
	 * @param definition the definition to set
	 */
	public void setDefinition(DataExportDataSetDefinition definition) {
		this.definition = definition;
	}
	
	/**
	 * @see org.openmrs.module.dataset.DataSet#getEvaluationContext()
	 */
	public EvaluationContext getEvaluationContext() {
		return evaluationContext;
	}
	
	/**
	 * @param evaluationContext the evaluationContext to set
	 */
	public void setEvaluationContext(EvaluationContext evaluationContext) {
		this.evaluationContext = evaluationContext;
	}
}
