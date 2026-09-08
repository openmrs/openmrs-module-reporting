/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.dataset.definition;

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataSetDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PersonToPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.dataset.column.definition.RowPerObjectColumnDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DataSetDefinition for Producing a DataSet that has one row per Patient
 * @see DataSetDefinition
 */
@Localized("reporting.PatientDataSetDefinition")
public class PatientDataSetDefinition extends RowPerObjectDataSetDefinition {
	
    public static final long serialVersionUID = 1L;
    
    //***** PROPERTIES *****
    
    @ConfigurationProperty
    private List<Mapped<? extends CohortDefinition>> rowFilters;
 
    //***** CONSTRUCTORS *****
    
    /**
     * Default Constructor
     */
    public PatientDataSetDefinition() {
    	super();
    }

	/**
	 * Public constructor
	 */
	public PatientDataSetDefinition(String name) { 
		super(name);
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @see RowPerObjectDataSetDefinition#getSupportedDataDefinitionTypes()
	 */
	@Override
	public List<Class<? extends DataDefinition>> getSupportedDataDefinitionTypes() {
		List<Class<? extends DataDefinition>> l = new ArrayList<Class<? extends DataDefinition>>();
		l.add(PatientDataDefinition.class);
		l.add(PersonDataDefinition.class);
		return l;
	}
		
	/**
	 * Adds a new Column Definition given the passed parameters
	 */
	public void addColumn(String name, DataDefinition dataDefinition, String mappings, DataConverter... converters) {
		addColumn(name, dataDefinition, ParameterizableUtil.createParameterMappings(mappings), converters);
	}
	
	/**
	 * Adds a new Column Definition given the passed parameters
	 */
	public void addColumn(String name, DataDefinition dataDefinition, Map<String, Object> mappings, DataConverter... converters) {
        if (dataDefinition == null) {
            throw new IllegalArgumentException("Trying to add column with null dataDefinition");
        }
        if (dataDefinition instanceof PatientDataDefinition) {
            getColumnDefinitions().add(new RowPerObjectColumnDefinition(name, dataDefinition, mappings, converters));
        } else if (dataDefinition instanceof PersonDataDefinition) {
            PatientDataDefinition pdd = new PersonToPatientDataDefinition((PersonDataDefinition) dataDefinition);
            getColumnDefinitions().add(new RowPerObjectColumnDefinition(name, pdd, mappings, converters));
        } else {
            throw new IllegalArgumentException("Unable to add data definition of type " + dataDefinition.getClass().getSimpleName());
        }
    }

	/**
	 * Adds a the Column Definitions defined in the passed DataSetDefinition
	 */
	public void addColumns(String name, RowPerObjectDataSetDefinition dataSetDefinition, String mappings,
						   TimeQualifier whichValues, Integer numberOfValues, DataConverter... converters) {
		
		// Ensure that the DSD being passed in supports a Patient ID column.  If so, we can join against it
		try {
			RowPerObjectDataSetDefinition def = dataSetDefinition.getClass().newInstance();
			def.addColumn("Patient ID", new PatientIdDataDefinition(), null);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Unable to add columns from " + dataSetDefinition.getClass().getSimpleName(), e);
		}
		
		addColumn(name, new PatientDataSetDataDefinition(dataSetDefinition, whichValues, numberOfValues), mappings, converters);
	}
	
    //***** PROPERTY ACCESS *****
	
	/**
	 * @return the rowFilters
	 */
	public List<Mapped<? extends CohortDefinition>> getRowFilters() {
		if (rowFilters == null) {
			rowFilters = new ArrayList<Mapped<? extends CohortDefinition>>();
		}
		return rowFilters;
	}

	/**
	 * @param rowFilters the rowFilters to set
	 */
	public void setRowFilters(List<Mapped<? extends CohortDefinition>> rowFilters) {
		this.rowFilters = rowFilters;
	}
	
	/**
	 * @param rowFilter the rowFilter to add
	 */
	public void addRowFilter(Mapped<? extends CohortDefinition> rowFilter) {
		getRowFilters().add(rowFilter);
	}
	
	/**
	 * @param rowFilter the rowFilter to add
	 */
	public void addRowFilter(CohortDefinition rowFilter, String parameterMappings) {
		addRowFilter(new Mapped<CohortDefinition>(rowFilter, ParameterizableUtil.createParameterMappings(parameterMappings)));
	}
}