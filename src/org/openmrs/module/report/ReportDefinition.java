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
package org.openmrs.module.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openmrs.Cohort;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.BaseParameterizable;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.report.service.ReportService;

/**
 * This class represents the metadata that describes a particular report that can be evaluated.
 * A {@link ReportDefinition} will typically be evaluated upon a base {@link Cohort} in the context
 * of an {@link EvaluationContext}.  Evaluating a report generally means evaluating all of the
 * {@link DataSetDefinition}s it contains, resulting in a {@link ReportData}.
 * 
 * @see {@link ReportService#evaluate(ReportDefinition, Cohort, EvaluationContext)}
 */
public class ReportDefinition extends BaseParameterizable {
	
	private static final long serialVersionUID = 1L;
	
	//***********************
	// PROPERTIES
	//***********************
	
	private Integer id;
	private Mapped<CohortDefinition> baseCohortDefinition;
	private List<Mapped<? extends DataSetDefinition>> dataSetDefinitions = new ArrayList<Mapped<? extends DataSetDefinition>>();
	
	//***********************
	// CONSTRUCTORS
	//***********************
	
	public ReportDefinition() { }
	
	//***********************
	// INSTANCE METHODS
	//***********************
	
	/**
	 * @param definition the Mapped<DataSetDefinition> to add
	 */
	public void addDataSetDefinition(Mapped<? extends DataSetDefinition> definition) {
		if (dataSetDefinitions == null) {
			dataSetDefinitions = new ArrayList<Mapped<? extends DataSetDefinition>>();
		}
		dataSetDefinitions.add(definition);
	}
	
	/**
	 * @param definition the DataSetDefinition to add
	 */
	public void addDataSetDefinition(DataSetDefinition definition, Map<String, String> mappings) {
		addDataSetDefinition(new Mapped<DataSetDefinition>(definition, mappings));
	}

	/**
	 * @param definition the DataSetDefinition to add
	 */
	public void addDataSetDefinition(DataSetDefinition definition, String mappings) {
		addDataSetDefinition(new Mapped<DataSetDefinition>(definition, mappings));
	}
	
	//***********************
	// PROPERTY ACCESS
	//***********************

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the baseCohortDefinition
	 */
	public Mapped<CohortDefinition> getBaseCohortDefinition() {
		return baseCohortDefinition;
	}

	/**
	 * @param baseCohortDefinition the baseCohortDefinition to set
	 */
	public void setBaseCohortDefinition(Mapped<CohortDefinition> baseCohortDefinition) {
		this.baseCohortDefinition = baseCohortDefinition;
	}
	
	/**
	 * @param baseCohortDefinition the baseCohortDefinition to set
	 */
	public void setBaseCohortDefinition(CohortDefinition cohortDefinition, String mappings) { 
		this.baseCohortDefinition = new Mapped<CohortDefinition>(cohortDefinition, mappings);
	}

	/**
	 * @return the dataSetDefinitions
	 */
	public List<Mapped<? extends DataSetDefinition>> getDataSetDefinitions() {
		return dataSetDefinitions;
	}

	/**
	 * @param dataSetDefinitions the dataSetDefinitions to set
	 */
	public void setDataSetDefinitions(List<Mapped<? extends DataSetDefinition>> dataSetDefinitions) {
		this.dataSetDefinitions = dataSetDefinitions;
	}
}
