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
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.column.SimpleDataSetColumn;

/**
 * Definition of a dataset that produces one-row-per-obs. Output might look like: patientId,
 * question, questionConceptId, answer, answerConceptId, obsDatetime, encounterId 123,
 * "WEIGHT (KG)", 5089, 70, null, "2007-05-23", 2345 123, "OCCUPATION", 987, "STUDENT", 988,
 * "2008-01-30", 2658
 * 
 * @see RowPerObsDataSet
 */
public class ObsDataSetDefinition extends BaseDataSetDefinition {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	
	private Collection<Concept> questions;
	
	private CohortDefinition filter;
	
	private Date fromDate;
	
	private Date toDate;

	/**
	 * Default constructor
	 */
	public ObsDataSetDefinition() {
		questions = new HashSet<Concept>();
	}
	
	/**
	 * Public constructor
	 * 
	 * @param name
	 * @param description
	 * @param questions
	 */
	public ObsDataSetDefinition(String name, String description, Set<Concept> questions) { 
		this.setName(name);
		this.setDescription(description);
		this.setQuestions(questions);
	}
	
	
	private static Class[] columnDatatypes = {
		Integer.class, // patientId
		String.class, // question concept name
		Integer.class, // question concept id
		Object.class, // answer
		Integer.class, // answer concept id
		Date.class, // obsDatetime
		Integer.class, // encounterId
		Integer.class // obsGroupId
	};
	
	private static String[] columnKeys = { 
		"patientId", 
		"question", 
		"questionConceptId",
		"answer", 
		"answerConceptId", 
		"obsDatetime", 
		"encounterId", 
		"obsGroupId" 
	};	
	
	
	/**
	 * @see org.openmrs.report.DataSetDefinition#getColumnDatatypes()
	 */
	@SuppressWarnings("unchecked")
	public List<Class> getColumnDatatypes() {
		return Arrays.asList(columnDatatypes);
	}
	
	/**
	 * @see org.openmrs.module.datasetDefinition#getName()
	 */
	public List<String> getColumnKeys() {
		return Arrays.asList(columnKeys);
	}
	
	/**
	 * @see org.openmrs.module.datasetDefinition#setName(java.lang.String)
	 */
    public List<DataSetColumn> getColumns() {    	
    	List<DataSetColumn> columns = new ArrayList<DataSetColumn>();
    	for (int i = 0; i < columnKeys.length; i++) {     		
    		DataSetColumn column = 
    			new SimpleDataSetColumn(columnKeys[i], columnKeys[i], columnDatatypes[i]);
    		columns.add(column);
    	}
    	return columns;

	}
   
	/**
	 * @see org.openmrs.module.evaluation.parameter.Parameterizable#getParameters()
	 */
	public List<Parameter> getParameters() {
		return new ArrayList<Parameter>();
	}
	
	/**
	 * @return the filter
	 */
	public CohortDefinition getFilter() {
		return filter;
	}
	
	/**
	 * @param filter the filter to set
	 */
	public void setFilter(CohortDefinition filter) {
		this.filter = filter;
	}
	
	/**
	 * @return the fromDate
	 */
	public Date getFromDate() {
		return fromDate;
	}
	
	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	
	/**
	 * @return the questions
	 */
	public Collection<Concept> getQuestions() {
		return questions;
	}
	
	/**
	 * @param questions the questions to set
	 */
	public void setQuestions(Collection<Concept> questions) {
		this.questions = questions;
	}
	
	/**
	 * @return the toDate
	 */
	public Date getToDate() {
		return toDate;
	}
	
	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
}
