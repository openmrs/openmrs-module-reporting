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

import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.column.SimpleDataSetColumn;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.ProgramDataSetDefinition;

/**
 *
 */
public class ProgramDataSet implements DataSet<Object> {
	
	private ProgramDataSetDefinition definition;
	
	private EvaluationContext evaluationContext;
	
	private List<PatientProgram> data;
	
	public ProgramDataSet() {
	}
	
	/**
	 * This is wrapped around (List<Obs>).iterator() This implementation is NOT thread-safe, so do
	 * not access the wrapped iterator.
	 */
	class HelperIterator implements Iterator<Map<DataSetColumn, Object>> {
		
		private Iterator<PatientProgram> iter;
		
		public HelperIterator(Iterator<PatientProgram> iter) {
			this.iter = iter;
		}
		
		public boolean hasNext() {
			return iter.hasNext();
		}
		
		public Map<DataSetColumn, Object> next() {
			Locale locale = Context.getLocale();
			PatientProgram pp = iter.next();
			Map<DataSetColumn, Object> vals = new HashMap<DataSetColumn, Object>();
			vals.put(new SimpleDataSetColumn("patientId"), pp.getPatient().getPatientId());
			vals.put(new SimpleDataSetColumn("programName"), pp.getProgram().getConcept().getName(locale, false).getName());
			vals.put(new SimpleDataSetColumn("programId"), pp.getProgram().getProgramId());
			vals.put(new SimpleDataSetColumn("enrollmentDate"), pp.getDateEnrolled());
			vals.put(new SimpleDataSetColumn("completionDate"), pp.getDateCompleted());
			vals.put(new SimpleDataSetColumn("patientProgramId"), pp.getPatientProgramId());
			return vals;
		}
		
		public void remove() {
			iter.remove();
		}
		
	}
	
	public DataSetDefinition getDataSetDefinition() {
		return definition;
	}
	
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
	
	public EvaluationContext getEvaluationContext() {
		return evaluationContext;
	}
	
	public List<PatientProgram> getData() {
		return data;
	}
	
	public void setData(List<PatientProgram> data) {
		this.data = data;
	}
	
	public void setDataSetDefinition(ProgramDataSetDefinition definition) {
		this.definition = definition;
	}
	
	public void setEvaluationContext(EvaluationContext evaluationContext) {
		this.evaluationContext = evaluationContext;
	}
}
