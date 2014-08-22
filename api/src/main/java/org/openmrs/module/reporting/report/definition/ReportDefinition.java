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
package org.openmrs.module.reporting.report.definition;

import org.openmrs.Cohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.BaseDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class represents the metadata that describes a particular report that can be evaluated.
 * A {@link ReportDefinition} will typically be evaluated upon a base {@link Cohort} in the context
 * of an {@link EvaluationContext}.  Evaluating a report generally means evaluating all of the
 * {@link DataSetDefinition}s it contains, resulting in a {@link ReportData}.
 * 
 * @see {@link ReportDefinitionService#evaluate(ReportDefinition, EvaluationContext)}
 */
@Localized("reporting.ReportDefinition")
public class ReportDefinition extends BaseDefinition {
	
	public static final long serialVersionUID = 1L;
	
	//***********************
	// PROPERTIES
	//***********************
	
	private Integer id;
	
	@ConfigurationProperty
	private Mapped<? extends CohortDefinition> baseCohortDefinition;
	
	@ConfigurationProperty
	private Map<String, Mapped<? extends DataSetDefinition>> dataSetDefinitions;
	
	//***********************
	// CONSTRUCTORS
	//***********************
	
	public ReportDefinition() { }
	
	//***********************
	// INSTANCE METHODS
	//***********************
	
	
	/**
	 * Convenience method to check for existing dataset definitions.
	 * 
	 * @return	
	 * 		true 	If report definition has at least one dataset definition.
	 * 		false	If report definition has no dataset definitions. 
	 */
	public boolean hasDataSetDefinitions() { 
		return !getDataSetDefinitions().isEmpty();
	}
	
	/**
	 * @param definition the DataSetDefinition to add
	 */
	public void addDataSetDefinition(DataSetDefinition definition, Map<String, Object> mappings) {
		addDataSetDefinition(definition.getName(), new Mapped<DataSetDefinition>(definition, mappings));
	}	

	/**
	 * @param definition the DataSetDefinition to add
	 */
	public void addDataSetDefinition(String key, DataSetDefinition definition, Map<String, Object> mappings) {
		addDataSetDefinition(key, new Mapped<DataSetDefinition>(definition, mappings));
	}
	
	/**
	 * @param definition the Mapped<DataSetDefinition> to add
	 */
	public void addDataSetDefinition(String key, Mapped<? extends DataSetDefinition> definition) {
		getDataSetDefinitions().put(key, definition);
	}
	
	/** @see Object#equals(Object) */
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ReportDefinition) {
			ReportDefinition p = (ReportDefinition) obj;
			if (this.getUuid() != null) {
				return (this.getUuid().equals(p.getUuid()));
			}
		}
		return this == obj;
	}
	
	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (getUuid() == null ? 0 : 31 * getUuid().hashCode());
	}
	
	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
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
	public Mapped<? extends CohortDefinition> getBaseCohortDefinition() {
		return baseCohortDefinition;
	}

	/**
	 * @param baseCohortDefinition the baseCohortDefinition to set
	 */
	public void setBaseCohortDefinition(Mapped<? extends CohortDefinition> baseCohortDefinition) {
		this.baseCohortDefinition = baseCohortDefinition;
	}
	
	/**
	 * @param cohortDefinition the baseCohortDefinition to set
	 * @param cohortDefinition the baseCohortDefinition to set
	 */
	public void setBaseCohortDefinition(CohortDefinition cohortDefinition, Map<String, Object> mappings) { 
		this.baseCohortDefinition = new Mapped<CohortDefinition>(cohortDefinition, mappings);
	}

	/**
	 * @return the dataSetDefinitions
	 */
	public Map<String, Mapped<? extends DataSetDefinition>> getDataSetDefinitions() {
		if (dataSetDefinitions == null) {
			dataSetDefinitions = new LinkedHashMap<String, Mapped<? extends DataSetDefinition>>();
		}
		return dataSetDefinitions;
	}

	/**
	 * @param dataSetDefinitions the dataSetDefinitions to set
	 */
	public void setDataSetDefinitions(Map<String, Mapped<? extends DataSetDefinition>> dataSetDefinitions) {
		this.dataSetDefinitions = dataSetDefinitions;
	}
}
