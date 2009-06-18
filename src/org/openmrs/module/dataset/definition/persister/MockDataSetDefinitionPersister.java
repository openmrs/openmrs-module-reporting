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
package org.openmrs.module.dataset.definition.persister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.openmrs.annotation.Handler;
import org.openmrs.module.dataset.definition.CohortDataSetDefinition;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.dataset.definition.ObsDataSetDefinition;
import org.openmrs.module.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.dataset.definition.ProgramDataSetDefinition;

/**
 * This class returns DataSetDefinitions that are persisted in memory.
 */
@Handler(supports={DataSetDefinition.class})
public class MockDataSetDefinitionPersister implements DataSetDefinitionPersister {

	// Keep track of the primary keys handed out
	Integer primaryKeySequence = new Integer(1);	
	
	List<DataSetDefinition> dataSetDefinitions = new ArrayList<DataSetDefinition>();	
	Map<Integer,DataSetDefinition> indexById = new HashMap<Integer,DataSetDefinition>();	
	Map<String,DataSetDefinition> indexByUuid = new HashMap<String,DataSetDefinition>();
	
	
	public MockDataSetDefinitionPersister() { 
		initializePersister();		
	}
	
	
	/**
     * @see DataSetDefinitionPersister#getDataSetDefinition(Integer)
     */
    public DataSetDefinition getDataSetDefinition(Integer id) {
    	return indexById.get(id);
    }
    
	/**
     * @see DataSetDefinitionPersister#getDataSetDefinitionByUuid(String)
     */
    public DataSetDefinition getDataSetDefinitionByUuid(String uuid) {
    	return indexByUuid.get(uuid);
    }

	/**
     * @see DataSetDefinitionPersister#getAllDataSetDefinitions(boolean)
     */
    public List<DataSetDefinition> getAllDataSetDefinitions(boolean includeRetired) {
    	return dataSetDefinitions;
    }

	/**
     * @see DataSetDefinitionPersister#getDataSetDefinitionByName(String, boolean)
     */
    public List<DataSetDefinition> getDataSetDefinitions(String name, boolean exactMatchOnly) {
    	return dataSetDefinitions;
    }
    
	/**
     * @see DataSetDefinitionPersister#saveDataSetDefinition(DataSetDefinition)
     */
    public DataSetDefinition saveDataSetDefinition(DataSetDefinition dataSetDefinition) {

    	// Set values
    	dataSetDefinition.setId(primaryKeySequence++);
    	dataSetDefinition.setUuid(UUID.randomUUID().toString());

    	// Add the dataset definition to the list 
    	dataSetDefinitions.add(dataSetDefinition);
    	
    	// Index the dataset definition
    	indexById.put(dataSetDefinition.getId(), dataSetDefinition);
    	indexByUuid.put(dataSetDefinition.getUuid(), dataSetDefinition);
    	return dataSetDefinition;
    }

	/**
     * @see DataSetDefinitionPersister#purgeDataSetDefinition(DataSetDefinition)
     */
    public void purgeDataSetDefinition(DataSetDefinition dataSetDefinition) {    	
    	indexById.remove(dataSetDefinition.getId());
    	indexByUuid.remove(dataSetDefinition.getUuid());
    	dataSetDefinitions.remove(dataSetDefinition);    	
    }

    /**
     * Convenience method to initialize the dataset definitions 
     */
    public void initializePersister() { 
    	
    	DataSetDefinition patientDataSetDefinition = new PatientDataSetDefinition("Sample patient dataset", "");
    	DataSetDefinition encounterDataSetDefinition = new EncounterDataSetDefinition("Sample encounter dataset", "", null, null, null, null);
    	DataSetDefinition obsDataSetDefinition = new ObsDataSetDefinition("Sample observation dataset", "", null);
    	DataSetDefinition cohortDataSetDefinition = new CohortDataSetDefinition("Sample cohort dataset", "");
    	DataSetDefinition cohortIndicatorDataSetDefinition = new CohortIndicatorDataSetDefinition("Sample cohort indicator dataset", "", null, null, null);
    	DataSetDefinition programDataSetDefinition = new ProgramDataSetDefinition("Sample program dataset", "", null, null);
    	//DataSetDefinition logicDataSetDefinition = new LogicDataSetDefinition("Sample logic dataset", "");
    	
    	
    	saveDataSetDefinition(cohortDataSetDefinition);
    	saveDataSetDefinition(cohortIndicatorDataSetDefinition);
    	saveDataSetDefinition(encounterDataSetDefinition);
    	saveDataSetDefinition(obsDataSetDefinition);
    	saveDataSetDefinition(patientDataSetDefinition);
    	saveDataSetDefinition(programDataSetDefinition);
    	
    }
    
    
}
