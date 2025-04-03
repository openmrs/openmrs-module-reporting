/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
