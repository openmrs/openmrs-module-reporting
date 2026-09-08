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

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.PatientToEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.PersonToEncounterDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.dataset.column.definition.RowPerObjectColumnDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * DataSetDefinition for Producing a DataSet that has one row per Encounter
 * @see DataSetDefinition
 */
@Localized("reporting.EncounterDataSetDefinition")
public class EncounterDataSetDefinition extends RowPerObjectDataSetDefinition {
	
    public static final long serialVersionUID = 1L;
    
    //***** PROPERTIES *****
    
    @ConfigurationProperty
    private List<Mapped<? extends EncounterQuery>> rowFilters;
 
    //***** CONSTRUCTORS *****
    
    /**
     * Default Constructor
     */
    public EncounterDataSetDefinition() {
    	super();
    }

	/**
	 * Public constructor
	 */
	public EncounterDataSetDefinition(String name) { 
		super(name);
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @see RowPerObjectDataSetDefinition#getSupportedDataDefinitionTypes()
	 */
	@Override
	public List<Class<? extends DataDefinition>> getSupportedDataDefinitionTypes() {
		List<Class<? extends DataDefinition>> l = new ArrayList<Class<? extends DataDefinition>>();
		l.add(EncounterDataDefinition.class);
		l.add(PatientDataDefinition.class);
		l.add(PersonDataDefinition.class);
		return l;
	}
	
	/**
	 * Adds a new Column Definition given the passed parameters
	 */
	public void addColumn(String name, DataDefinition dataDefinition, String mappings,  DataConverter... converters) {
        if (dataDefinition == null) {
            throw new IllegalArgumentException("Cannot add a null dataDefinition as a column on a DSD");
        } else if (dataDefinition instanceof EncounterDataDefinition) {
            getColumnDefinitions().add(new RowPerObjectColumnDefinition(name, dataDefinition, mappings, converters));
        } else if (dataDefinition instanceof PatientDataDefinition) {
            EncounterDataDefinition edd = new PatientToEncounterDataDefinition((PatientDataDefinition) dataDefinition);
            getColumnDefinitions().add(new RowPerObjectColumnDefinition(name, edd, mappings, converters));
        } else if (dataDefinition instanceof PersonDataDefinition) {
            EncounterDataDefinition edd = new PersonToEncounterDataDefinition((PersonDataDefinition) dataDefinition);
            getColumnDefinitions().add(new RowPerObjectColumnDefinition(name, edd, mappings, converters));
        } else {
            throw new IllegalArgumentException("Unable to add data definition of type " + dataDefinition.getClass().getSimpleName());
        }
    }

	/**
	 * @see RowPerObjectDataSetDefinition#addColumns(String, RowPerObjectDataSetDefinition, String, TimeQualifier, Integer, DataConverter...)
	 */
	@Override
	public void addColumns(String name, RowPerObjectDataSetDefinition dataSetDefinition, String mappings, 
						   TimeQualifier whichValues, Integer numberOfValues, DataConverter... converters) {
		
		// TODO Implement this
	}

	/**
	 * Add a new row filter with the passed parameter mappings
	 */
	public void addRowFilter(Mapped<? extends EncounterQuery> filter) {
		getRowFilters().add(filter);
	}

	/**
	 * Add a new row filter with the passed parameter mappings
	 */
	public void addRowFilter(EncounterQuery filter, String mappings) {
		addRowFilter(new Mapped<EncounterQuery>(filter, ParameterizableUtil.createParameterMappings(mappings)));
	}
	
    //***** PROPERTY ACCESS *****
	
	/**
	 * @return the rowFilters
	 */
	public List<Mapped<? extends EncounterQuery>> getRowFilters() {
		if (rowFilters == null) {
			rowFilters = new ArrayList<Mapped<? extends EncounterQuery>>();
		}
		return rowFilters;
	}

	/**
	 * @param rowFilters the rowFilters to set
	 */
	public void setRowFilters(List<Mapped<? extends EncounterQuery>> rowFilters) {
		this.rowFilters = rowFilters;
	}
}