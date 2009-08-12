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
package org.openmrs.module.cohort.definition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Drug;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.OpenmrsObject;
import org.openmrs.OrderType;
import org.openmrs.PersonAttributeType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.PatientSetService.PatientLocationMethod;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.configuration.ConfigurationProperty;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;

public class SampleCohortDefinition extends BaseCohortDefinition {

    private static final long serialVersionUID = 1L;
    
	//***** PROPERTIES *****
    @ConfigurationProperty private Integer testInteger = 10;
    @ConfigurationProperty private int testInt = 5;
    @ConfigurationProperty private Long testLong = 20L;
    @ConfigurationProperty private Double testDouble = 10.5;
    @ConfigurationProperty private Float testFloat = 20.5F;
    @ConfigurationProperty private String testString = "Test String";
    @ConfigurationProperty private Character testCharacter = 'M';
    @ConfigurationProperty private PatientLocationMethod testEnum = PatientLocationMethod.LATEST_ENCOUNTER;
    @ConfigurationProperty private Date testDate = new Date();
    @ConfigurationProperty private Boolean testBoolean = Boolean.TRUE;

    @ConfigurationProperty private Location testLocation = random(Context.getLocationService().getAllLocations());
    @ConfigurationProperty private EncounterType testEncounterType = random(Context.getEncounterService().getAllEncounterTypes());
    @ConfigurationProperty private Program testProgram = random(Context.getProgramWorkflowService().getAllPrograms());
    @ConfigurationProperty private ProgramWorkflow testProgramWorkflow = random(testProgram == null ? null : testProgram.getAllWorkflows());
    @ConfigurationProperty private ProgramWorkflowState testProgramWorkflowState = random(testProgramWorkflow == null ? null : testProgramWorkflow.getStates());
    @ConfigurationProperty private Drug testDrug = random(Context.getConceptService().getAllDrugs());
    @ConfigurationProperty private Form testForm = random(Context.getFormService().getAllForms());
    @ConfigurationProperty private OrderType testOrderType = random(Context.getOrderService().getAllOrderTypes());
    @ConfigurationProperty private PersonAttributeType testPersonAttributeType = random(Context.getPersonService().getAllPersonAttributeTypes());
    @ConfigurationProperty private Cohort cohortType = random(Context.getCohortService().getAllCohorts());
    @ConfigurationProperty private CohortDefinition cohortDefinitionType = random(Context.getService(CohortDefinitionService.class).getAllCohortDefinitions(false));
    
    @ConfigurationProperty private List<Location> testList = randomList(Context.getLocationService().getAllLocations());
    
    /**
     * Utility method
     * @param <T>
     */
    protected <T extends OpenmrsObject> T random(Collection<T> items) {
    	if (items == null) {
    		return null;
    	}
    	int num = items.size();
    	int rand = (int)Math.round(Math.random()*num);
    	if (rand == num) {
    		return null;
    	}
    	int ct = 0;
    	for (T item : items) {
    		if (ct++ == rand) {
    			return item;
    		}
    	}
    	return null;
    }
    
    protected <T extends OpenmrsObject> List<T> randomList(Collection<T> items) {
    	int num = (int)Math.round(Math.random()*3);
    	if (num == 0) {
    		return null;
    	}
    	List<T> ret = new ArrayList<T>();
    	for (int i=0; i<num; i++) {
    		T item = random(items);
    		if (item != null) {
    			ret.add(item);
    		}
    	}
    	return ret;
    }
	
	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public SampleCohortDefinition() {
		super();
	}

	/**
	 * @return the testInt
	 */
	public int getTestInt() {
		return testInt;
	}
	
	/**
	 * @param testInt the testInt to set
	 */
	public void setTestInt(int testInt) {
		this.testInt = testInt;
	}

	/**
	 * @return the testInteger
	 */
	public Integer getTestInteger() {
		return testInteger;
	}

	/**
	 * @param testInteger the testInteger to set
	 */
	public void setTestInteger(Integer testInteger) {
		this.testInteger = testInteger;
	}

	/**
	 * @return the testLong
	 */
	public Long getTestLong() {
		return testLong;
	}

	/**
	 * @param testLong the testLong to set
	 */
	public void setTestLong(Long testLong) {
		this.testLong = testLong;
	}

	/**
	 * @return the testDouble
	 */
	public Double getTestDouble() {
		return testDouble;
	}

	/**
	 * @param testDouble the testDouble to set
	 */
	public void setTestDouble(Double testDouble) {
		this.testDouble = testDouble;
	}

	/**
	 * @return the testFloat
	 */
	public Float getTestFloat() {
		return testFloat;
	}

	/**
	 * @param testFloat the testFloat to set
	 */
	public void setTestFloat(Float testFloat) {
		this.testFloat = testFloat;
	}

	/**
	 * @return the testString
	 */
	public String getTestString() {
		return testString;
	}

	/**
	 * @param testString the testString to set
	 */
	public void setTestString(String testString) {
		this.testString = testString;
	}

	/**
	 * @return the testCharacter
	 */
	public Character getTestCharacter() {
		return testCharacter;
	}

	/**
	 * @param testCharacter the testCharacter to set
	 */
	public void setTestCharacter(Character testCharacter) {
		this.testCharacter = testCharacter;
	}

	/**
	 * @return the testEnum
	 */
	public PatientLocationMethod getTestEnum() {
		return testEnum;
	}

	/**
	 * @param testEnum the testEnum to set
	 */
	public void setTestEnum(PatientLocationMethod testEnum) {
		this.testEnum = testEnum;
	}

	/**
	 * @return the testDate
	 */
	public Date getTestDate() {
		return testDate;
	}

	/**
	 * @param testDate the testDate to set
	 */
	public void setTestDate(Date testDate) {
		this.testDate = testDate;
	}

	/**
	 * @return the testBoolean
	 */
	public Boolean getTestBoolean() {
		return testBoolean;
	}

	/**
	 * @param testBoolean the testBoolean to set
	 */
	public void setTestBoolean(Boolean testBoolean) {
		this.testBoolean = testBoolean;
	}

	/**
	 * @return the testLocation
	 */
	public Location getTestLocation() {
		return testLocation;
	}

	/**
	 * @param testLocation the testLocation to set
	 */
	public void setTestLocation(Location testLocation) {
		this.testLocation = testLocation;
	}

	/**
	 * @return the testEncounterType
	 */
	public EncounterType getTestEncounterType() {
		return testEncounterType;
	}

	/**
	 * @param testEncounterType the testEncounterType to set
	 */
	public void setTestEncounterType(EncounterType testEncounterType) {
		this.testEncounterType = testEncounterType;
	}

	/**
	 * @return the testProgram
	 */
	public Program getTestProgram() {
		return testProgram;
	}

	/**
	 * @param testProgram the testProgram to set
	 */
	public void setTestProgram(Program testProgram) {
		this.testProgram = testProgram;
	}

	/**
	 * @return the testProgramWorkflow
	 */
	public ProgramWorkflow getTestProgramWorkflow() {
		return testProgramWorkflow;
	}

	/**
	 * @param testProgramWorkflow the testProgramWorkflow to set
	 */
	public void setTestProgramWorkflow(ProgramWorkflow testProgramWorkflow) {
		this.testProgramWorkflow = testProgramWorkflow;
	}

	/**
	 * @return the testProgramWorkflowState
	 */
	public ProgramWorkflowState getTestProgramWorkflowState() {
		return testProgramWorkflowState;
	}

	/**
	 * @param testProgramWorkflowState the testProgramWorkflowState to set
	 */
	public void setTestProgramWorkflowState(ProgramWorkflowState testProgramWorkflowState) {
		this.testProgramWorkflowState = testProgramWorkflowState;
	}

	/**
	 * @return the testDrug
	 */
	public Drug getTestDrug() {
		return testDrug;
	}

	/**
	 * @param testDrug the testDrug to set
	 */
	public void setTestDrug(Drug testDrug) {
		this.testDrug = testDrug;
	}

	/**
	 * @return the testForm
	 */
	public Form getTestForm() {
		return testForm;
	}

	/**
	 * @param testForm the testForm to set
	 */
	public void setTestForm(Form testForm) {
		this.testForm = testForm;
	}

	/**
	 * @return the testOrderType
	 */
	public OrderType getTestOrderType() {
		return testOrderType;
	}

	/**
	 * @param testOrderType the testOrderType to set
	 */
	public void setTestOrderType(OrderType testOrderType) {
		this.testOrderType = testOrderType;
	}

	/**
	 * @return the testPersonAttributeType
	 */
	public PersonAttributeType getTestPersonAttributeType() {
		return testPersonAttributeType;
	}

	/**
	 * @param testPersonAttributeType the testPersonAttributeType to set
	 */
	public void setTestPersonAttributeType(PersonAttributeType testPersonAttributeType) {
		this.testPersonAttributeType = testPersonAttributeType;
	}

	/**
	 * @return the testList
	 */
	public List<Location> getTestList() {
		return testList;
	}

	/**
	 * @param testList the testList to set
	 */
	public void setTestList(List<Location> testList) {
		this.testList = testList;
	}

	/**
	 * @return the cohortType
	 */
	public Cohort getCohortType() {
		return cohortType;
	}

	/**
	 * @param cohortType the cohortType to set
	 */
	public void setCohortType(Cohort cohortType) {
		this.cohortType = cohortType;
	}

	/**
	 * @return the cohortDefinitionType
	 */
	public CohortDefinition getCohortDefinitionType() {
		return cohortDefinitionType;
	}

	/**
	 * @param cohortDefinitionType the cohortDefinitionType to set
	 */
	public void setCohortDefinitionType(CohortDefinition cohortDefinitionType) {
		this.cohortDefinitionType = cohortDefinitionType;
	}
}
