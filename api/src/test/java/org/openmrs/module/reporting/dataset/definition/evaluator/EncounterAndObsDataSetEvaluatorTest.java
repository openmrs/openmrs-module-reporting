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
package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterAndObsDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.test.Verifies;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Tests the evaluation of an EncounterAndObsDataSetEvaluator
 */
@Ignore
@SkipBaseSetup
public class EncounterAndObsDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {
    
    protected static final String XML_BASE_DATASET = "org/openmrs/module/reporting/include/EncounterAndObsTestBaseDataset.xml";
    
    protected static final String XML_ENCOUNTER_DATASET = "org/openmrs/module/reporting/include/EncounterAndObsTestEncounterDataset.xml";
    
    protected static final String XML_OBS_GROUP_DATASET = "org/openmrs/module/reporting/include/EncounterAndObsTestObsGroupDataset.xml";
    
    protected static final String XML_MULTI_OBS_GROUP_DATASET = "org/openmrs/module/reporting/include/EncounterAndObsTestMultiObsGroupDataset.xml";
    
    protected static final String XML_FORM_DATASET = "org/openmrs/module/reporting/include/EncounterAndObsTestFormDataset.xml";
    
	@Before
	public void setup() throws Exception {
		executeDataSet(INITIAL_XML_DATASET_PACKAGE_PATH);
		authenticate();
		executeDataSet(XML_BASE_DATASET);
	}
	
	/**
	 * @see {@link EncounterAndObsDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate an EncounterAndObsDataSetDefinition and checks that all obs in encounter are in row", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateAnEncounterAndObsDataSetDefinitionAllEncounterObsValuesInReportRow() throws Exception {
		executeDataSet(XML_ENCOUNTER_DATASET);
		executeDataSet(XML_OBS_GROUP_DATASET);
		executeDataSet(XML_FORM_DATASET);
		
		Form form = Context.getFormService().getForm(2);
		Encounter encounter1 = Context.getEncounterService().getEncounter(13);
		Encounter encounter2 = Context.getEncounterService().getEncounter(14);
		encounter1.setForm(form);
		encounter2.setForm(form);
		
		EncounterAndObsDataSetDefinition d = new EncounterAndObsDataSetDefinition();
		
		d.setForms(Collections.singletonList(form));
		d.setColumnDisplayFormat(Collections.singletonList(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.ID));
		Integer maxColumnHeaderWidth = d.getMaxColumnHeaderWidth();
		
		SimpleDataSet result = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, null);
		for (DataSetRow row : result.getRows()) {
			
			Integer encounterId = (Integer) row.getColumnValue("ENCOUNTER_ID");
			Encounter e = Context.getEncounterService().getEncounter(encounterId);
			
			List<String> obsValues = new ArrayList<String>();
			for(DataSetColumn dsc : row.getColumnValues().keySet()) {
				String obsValue = row.getColumnValue(dsc).toString();
				if(StringUtils.isNotEmpty(obsValue)) {
					obsValues.add(obsValue);
				}
			}
			
			for (Obs obs : e.getObs()) {
				Concept obsCodedValue = obs.getValueCoded();
				String obsNonCodedValue = obs.getValueAsString(Context.getLocale());
				
				if((obsCodedValue != null) || (obsNonCodedValue != null && StringUtils.isNotEmpty(obsNonCodedValue))) {
					if(obsCodedValue != null && obsValues.contains(obsCodedValue.toString())) {
						boolean obsRemoved = obsValues.remove(obsCodedValue.toString());
						boolean obsDateRemoved = obsValues.remove(obs.getObsDatetime().toString());
						boolean obsGroupRemoved = false;
						if(obs.getObsGroup() != null) {
							obsGroupRemoved = obsValues.remove(obs.getObsGroup().getId().toString());
						} else {
							obsGroupRemoved = true;
						}
						Assert.assertTrue(obsRemoved && obsDateRemoved && obsGroupRemoved);
					} else if(obsValues.contains(obsNonCodedValue)) {
						boolean obsRemoved = obsValues.remove(obsNonCodedValue);
						boolean obsDateRemoved = obsValues.remove(obs.getObsDatetime().toString());
						boolean obsGroupRemoved = false;
						if(obs.getObsGroup() != null) {
							obsGroupRemoved = obsValues.remove(obs.getObsGroup().getId().toString());
						} else {
							obsGroupRemoved = true;
						}
						Assert.assertTrue(obsRemoved && obsDateRemoved && obsGroupRemoved);
					} else {
						Assert.assertFalse(true);
					}
				}
			}
			
			// standard encounter-based columns
			// no obs leftover in row that doesn't appear in encounter
			Assert.assertTrue(obsValues.size() == 5);
											
			// Standard Columns and Values
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getEncounterId().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("ENCOUNTER_ID").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getEncounterDatetime().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("ENCOUNTER_DATETIME").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getLocation().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("LOCATION").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getProvider().getPersonName().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("PROVIDER").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getPatientId().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("INTERNAL_PATIENT_ID").toString(), maxColumnHeaderWidth));
		}
	}
	
	/**
	 * @see {@link EncounterAndObsDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate an EncounterAndObsDataSetDefinition and checks that all obs in encounter and optional columns are in row", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateAnEncounterAndObsDataSetDefinitionAllEncounterObsValuesWithOptionalColumns() throws Exception {
		executeDataSet(XML_ENCOUNTER_DATASET);
		executeDataSet(XML_OBS_GROUP_DATASET);
		executeDataSet(XML_FORM_DATASET);
		
		Form form = Context.getFormService().getForm(2);
		Encounter encounter1 = Context.getEncounterService().getEncounter(13);
		Encounter encounter2 = Context.getEncounterService().getEncounter(14);
		encounter1.setForm(form);
		encounter2.setForm(form);
		
		EncounterAndObsDataSetDefinition d = new EncounterAndObsDataSetDefinition();
		d.setForms(Collections.singletonList(form));
		d.setColumnDisplayFormat(Collections.singletonList(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.ID));
		
		List<EncounterAndObsDataSetDefinition.ObsOptionalColumn> optionalColumns = new ArrayList<EncounterAndObsDataSetDefinition.ObsOptionalColumn>();
		optionalColumns.add(EncounterAndObsDataSetDefinition.ObsOptionalColumn.ACCESSION_NUMBER);
		optionalColumns.add(EncounterAndObsDataSetDefinition.ObsOptionalColumn.VALUE_MODIFIER);
		optionalColumns.add(EncounterAndObsDataSetDefinition.ObsOptionalColumn.COMMENT);
		d.setOptionalColumns(optionalColumns);
		Integer maxColumnHeaderWidth = d.getMaxColumnHeaderWidth();
		
		SimpleDataSet result = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, null);
		for (DataSetRow row : result.getRows()) {
			
			Integer encounterId = (Integer) row.getColumnValue("ENCOUNTER_ID");
			Encounter e = Context.getEncounterService().getEncounter(encounterId);
			
			List<String> obsValues = new ArrayList<String>();
			for(DataSetColumn dsc : row.getColumnValues().keySet()) {
				
				Object obsValue = row.getColumnValue(dsc);
				if(obsValue != null && obsValue != "") {
					obsValues.add(obsValue.toString());
				}
			}
			
			for (Obs obs : e.getObs()) {
				Concept obsCodedValue = obs.getValueCoded();
				String obsNonCodedValue = obs.getValueAsString(Context.getLocale());
				
				if((obsCodedValue != null) || (obsNonCodedValue != null && StringUtils.isNotEmpty(obsNonCodedValue))) {
					if(obsCodedValue != null && obsValues.contains(obsCodedValue.toString())) {
						boolean obsRemoved = obsValues.remove(obsCodedValue.toString());
						
						Assert.assertTrue(obsRemoved);
					} else if(obsValues.contains(obsNonCodedValue)) {
						boolean obsRemoved = obsValues.remove(obsNonCodedValue);
						
						Assert.assertTrue(obsRemoved);
					} else {
						Assert.assertFalse(true);
					}
										
					boolean obsGroupRemoved = false;
					if(obs.getObsGroup() != null) {
						obsGroupRemoved = obsValues.remove(obs.getObsGroup().getId().toString());
					} else {
						obsGroupRemoved = true;
					}
					
					boolean obsValueModRemoved = false;
					if(obs.getValueModifier() != null) {
						obsValueModRemoved = obsValues.remove(obs.getValueModifier());
					} else {
						obsValueModRemoved = true;
					}
					
					boolean obsDateRemoved = false;
					if(obs.getObsDatetime() != null) {
						obsDateRemoved = obsValues.remove(obs.getObsDatetime().toString());
					} else {
						obsDateRemoved = true;
					}
					
					boolean obsAccessionRemoved = false;
					if(obs.getAccessionNumber() != null) {
						obsAccessionRemoved = obsValues.remove(obs.getAccessionNumber());
					} else {
						obsAccessionRemoved = true;
					}
					
					boolean obsCommentRemoved = false;
					if(obs.getComment() != null) {
						obsCommentRemoved = obsValues.remove(obs.getComment());
					} else {
						obsCommentRemoved = true;
					}
					
					Assert.assertTrue(obsDateRemoved && obsGroupRemoved && obsValueModRemoved && obsAccessionRemoved && obsCommentRemoved);
				}
			}
			
			// standard encounter-based columns
			// no obs leftover in row that doesn't appear in encounter
			Assert.assertTrue(obsValues.size() == 5);
											
			// Standard Columns and Values
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getEncounterId().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("ENCOUNTER_ID").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getEncounterDatetime().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("ENCOUNTER_DATETIME").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getLocation().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("LOCATION").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getProvider().getPersonName().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("PROVIDER").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getPatientId().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("INTERNAL_PATIENT_ID").toString(), maxColumnHeaderWidth));
		}
	}
	
	/**
	 * @see {@link EncounterAndObsDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate an EncounterAndObsDataSetDefinition with filter EncounterType", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateAnEncounterAndObsDataSetDefinitionWithEncounterType() throws Exception {
		executeDataSet(XML_ENCOUNTER_DATASET);
		
		EncounterAndObsDataSetDefinition d = new EncounterAndObsDataSetDefinition();
		EncounterType adultInitial = new EncounterType(10);
		d.setEncounterTypes(Collections.singletonList(adultInitial));
		d.setColumnDisplayFormat(Collections.singletonList(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.ID));
		Integer maxColumnHeaderWidth = d.getMaxColumnHeaderWidth();
		
		SimpleDataSet result = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, null);
		
		// Basic Dimensions of Rows and Columns
		Assert.assertEquals(3, result.getRows().size());
		Assert.assertEquals(14, result.getMetaData().getColumnCount()); // 5 (standard) + 3 (obs) * 3 (value, date, group)
		
		
		for (DataSetRow row : result.getRows()) {
			
			Integer encounterId = (Integer) row.getColumnValue("ENCOUNTER_ID");
			Encounter e = Context.getEncounterService().getEncounter(encounterId);
			
			// Standard Columns and Values
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getEncounterId().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("ENCOUNTER_ID").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getEncounterDatetime().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("ENCOUNTER_DATETIME").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getLocation().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("LOCATION").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getProvider().getPersonName().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("PROVIDER").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getPatientId().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("INTERNAL_PATIENT_ID").toString(), maxColumnHeaderWidth));
		}
	}
	
	/**
	 * @see {@link EncounterAndObsDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate an EncounterAndObsDataSetDefinition with filter multiple EncounterType and multiple Encounters for same Patient", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateAnEncounterAndObsDataSetDefinitionWithEncounterTypes() throws Exception {
		executeDataSet(XML_ENCOUNTER_DATASET);
		
		EncounterAndObsDataSetDefinition d = new EncounterAndObsDataSetDefinition();
		EncounterType adultInitial = new EncounterType(10);
		EncounterType adultReturn = new EncounterType(11);
		List<EncounterType> encounterTypes = new ArrayList<EncounterType>();
		encounterTypes.add(adultInitial);
		encounterTypes.add(adultReturn);
		d.setEncounterTypes(encounterTypes);
		d.setColumnDisplayFormat(Collections.singletonList(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.ID));
		Integer maxColumnHeaderWidth = d.getMaxColumnHeaderWidth();
		
		SimpleDataSet result = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, null);
		
		// Basic Dimensions of Rows and Columns
		Assert.assertEquals(4, result.getRows().size());
		Assert.assertEquals(17, result.getMetaData().getColumnCount()); // 5 (standard) + 4 (obs) * 3 (value, date, group)
		
		
		for (DataSetRow row : result.getRows()) {
			Integer encounterId = (Integer) row.getColumnValue("ENCOUNTER_ID");
			Encounter e = Context.getEncounterService().getEncounter(encounterId);
			
			// Standard Columns and Values
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getEncounterId().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("ENCOUNTER_ID").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getEncounterDatetime().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("ENCOUNTER_DATETIME").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getLocation().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("LOCATION").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getProvider().getPersonName().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("PROVIDER").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getPatientId().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("INTERNAL_PATIENT_ID").toString(), maxColumnHeaderWidth));
		}
	}
	
	/**
	 * @see {@link EncounterAndObsDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate an EncounterAndObsDataSetDefinition with filter Form and Patient Identifier list", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateAnEncounterAndObsDataSetDefinitionWithFormAndPidList() throws Exception {
		executeDataSet(XML_ENCOUNTER_DATASET);
		executeDataSet(XML_FORM_DATASET);
		
		Form form = Context.getFormService().getForm(2);
		
		Encounter encounter1 = Context.getEncounterService().getEncounter(13);
		Encounter encounter2 = Context.getEncounterService().getEncounter(14);
		encounter1.setForm(form);
		encounter2.setForm(form);
		
		EncounterAndObsDataSetDefinition d = new EncounterAndObsDataSetDefinition();
		d.setForms(Collections.singletonList(form));
		d.setColumnDisplayFormat(Collections.singletonList(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.ID));
		d.setPatientIdentifierTypes(Context.getPatientService().getAllPatientIdentifierTypes());
				
		SimpleDataSet result = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, null);
		Assert.assertEquals(2, result.getRows().size());
		Assert.assertEquals(16, result.getMetaData().getColumnCount()); // 5 (standard) + 2 (pid) + 3 (obs) * 3 (value, date, group)
		for (DataSetRow row : result.getRows()) {
					
			Integer patientId = (Integer)row.getColumnValue("INTERNAL_PATIENT_ID");
			Patient p = Context.getPatientService().getPatient(patientId);
			
			PatientIdentifierType openMRSIds = Context.getPatientService().getPatientIdentifierType(1);
			PatientIdentifierType oldIds = Context.getPatientService().getPatientIdentifierType(2);
			
			StringBuffer sbOldIds = new StringBuffer();
			StringBuffer sbOpenMRSIds = new StringBuffer();
			int count = 0;
			for (PatientIdentifier patientIdentifier : p.getPatientIdentifiers(oldIds)) {
				if (count > 0) {
					sbOldIds.append(", ");
				}
				sbOldIds.append(patientIdentifier.toString());
				count++;
			}
			
			count = 0;
			for (PatientIdentifier patientIdentifier : p.getPatientIdentifiers(openMRSIds)) {
				if (count > 0) {
					sbOpenMRSIds.append(", ");
				}
				sbOpenMRSIds.append(patientIdentifier.toString());
				count++;
			}
			
			if(oldIds != null && p.getPatientIdentifiers(oldIds) != null)
				Assert.assertTrue(ObjectUtil.areEqualStr(sbOldIds, row.getColumnValue("Old Identification Number")));
			else
				Assert.assertTrue(ObjectUtil.areEqualStr(null, row.getColumnValue("Old Identification Number")));
			if(openMRSIds != null && p.getPatientIdentifiers(openMRSIds) != null)
				Assert.assertTrue(ObjectUtil.areEqualStr(sbOpenMRSIds.toString(), row.getColumnValue("OpenMRS Identification Number")));
			else
				Assert.assertTrue(ObjectUtil.areEqualStr(null, row.getColumnValue("OpenMRS Identification Number")));
		}
	}
	
	/**
	 * @see {@link EncounterAndObsDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate an EncounterAndObsDataSetDefinition with filter multiple Forms", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateAnEncounterAndObsDataSetDefinitionWithForms() throws Exception {
		executeDataSet(XML_ENCOUNTER_DATASET);
		executeDataSet(XML_FORM_DATASET);
		
		Form form = Context.getFormService().getForm(2);
		Form form2 = Context.getFormService().getForm(3);
		
		Encounter encounter1 = Context.getEncounterService().getEncounter(13);
		Encounter encounter2 = Context.getEncounterService().getEncounter(14);
		encounter1.setForm(form);
		encounter2.setForm(form);
		
		Encounter encounter3 = Context.getEncounterService().getEncounter(17);
		encounter3.setForm(form2);
		
		EncounterAndObsDataSetDefinition d = new EncounterAndObsDataSetDefinition();
		List<Form> forms = new ArrayList<Form>();
		forms.add(form);
		forms.add(form2);
		d.setForms(forms);
		d.setColumnDisplayFormat(Collections.singletonList(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.ID));
		Integer maxColumnHeaderWidth = d.getMaxColumnHeaderWidth();
				
		SimpleDataSet result = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, null);
		Assert.assertEquals(3, result.getRows().size());
		Assert.assertEquals(38, result.getMetaData().getColumnCount()); // 5 (standard)  + 11 (obs) * 3 (value, date, group)
		for (DataSetRow row : result.getRows()) {
					
			Integer encounterId = (Integer) row.getColumnValue("ENCOUNTER_ID");
			Encounter e = Context.getEncounterService().getEncounter(encounterId);
			
			// Standard Columns and Values
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getEncounterId().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("ENCOUNTER_ID").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getEncounterDatetime().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("ENCOUNTER_DATETIME").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getLocation().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("LOCATION").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getProvider().getPersonName().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("PROVIDER").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getPatientId().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("INTERNAL_PATIENT_ID").toString(), maxColumnHeaderWidth));
		}
	}
	
	/**
	 * @see {@link EncounterAndObsDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate an EncounterAndObsDataSetDefinition with Obs Groups in Form", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateAnEncounterAndObsDataSetDefinitionWithObsGroups() throws Exception {
		executeDataSet(XML_ENCOUNTER_DATASET);
		executeDataSet(XML_OBS_GROUP_DATASET);
		executeDataSet(XML_FORM_DATASET);
		
		Form form = Context.getFormService().getForm(2);
		Encounter encounter1 = Context.getEncounterService().getEncounter(13);
		Encounter encounter2 = Context.getEncounterService().getEncounter(14);
		encounter1.setForm(form);
		encounter2.setForm(form);
		
		EncounterAndObsDataSetDefinition d = new EncounterAndObsDataSetDefinition();
		d.setForms(Collections.singletonList(form));
		d.setColumnDisplayFormat(Collections.singletonList(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.ID));
		
		SimpleDataSet result = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, null);
		Assert.assertEquals(2, result.getRows().size());
		// Number of columns may vary depending on order of obs for each encounter
		for (DataSetRow row : result.getRows()) {
			
			Integer patientId = (Integer)row.getColumnValue("INTERNAL_PATIENT_ID");
			if(patientId == 100) {
				Assert.assertTrue("861".equals(row.getColumnValue("1292|1293")) || "16".equals(row.getColumnValue("1292|1293")));
				Assert.assertTrue("861".equals(row.getColumnValue("1292_2|1293")) || "16".equals(row.getColumnValue("1292_2|1293")));
			} else if(patientId == 101) {
				Assert.assertTrue("861".equals(row.getColumnValue("1292|1293")) && "".equals(row.getColumnValue("1292_2|1293")));
			}
		}
	}
	
	/**
	 * @see {@link EncounterAndObsDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate an EncounterAndObsDataSetDefinition with Multi level Obs Groups in Form", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateAnEncounterAndObsDataSetDefinitionWithMultiObsGroups() throws Exception {
		executeDataSet(XML_ENCOUNTER_DATASET);
		executeDataSet(XML_MULTI_OBS_GROUP_DATASET);
		executeDataSet(XML_FORM_DATASET);
		
		Form form = Context.getFormService().getForm(2);
		Encounter encounter1 = Context.getEncounterService().getEncounter(13);
		Encounter encounter2 = Context.getEncounterService().getEncounter(14);
		encounter1.setForm(form);
		encounter2.setForm(form);
		
		EncounterAndObsDataSetDefinition d = new EncounterAndObsDataSetDefinition();
		d.setForms(Collections.singletonList(form));
		d.setColumnDisplayFormat(Collections.singletonList(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.ID));
		
		SimpleDataSet result = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, null);
		Assert.assertEquals(2, result.getRows().size());
		
		for (DataSetRow row : result.getRows()) {
						
			Integer patientId = (Integer)row.getColumnValue("INTERNAL_PATIENT_ID");
			if(patientId == 100) {
				Assert.assertTrue("1414".equals(row.getColumnValue("3040|3025|1441")));
			} else if(patientId == 101) {
				Assert.assertTrue("".equals(row.getColumnValue("3040|3025|1441")));
			}
		}
	}
	
	/**
	 * @see {@link EncounterAndObsDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate an EncounterAndObsDataSetDefinition with filter Form and EncounterType", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateAnEncounterAndObsDataSetDefinitionWithFormAndEncounterType() throws Exception {
		executeDataSet(XML_ENCOUNTER_DATASET);
		executeDataSet(XML_FORM_DATASET);
		
		Form form = Context.getFormService().getForm(2);
		
		Encounter encounter13 = Context.getEncounterService().getEncounter(13); // ET 10
		Encounter encounter15 = Context.getEncounterService().getEncounter(15); // ET 11
		encounter13.setForm(form);
		encounter15.setForm(form);
		
		EncounterAndObsDataSetDefinition d = new EncounterAndObsDataSetDefinition();
		List<Form> forms = new ArrayList<Form>();
		forms.add(form);
		d.setForms(forms);
		
		EncounterType adultInitial = new EncounterType(10);
		List<EncounterType> encounterTypes = new ArrayList<EncounterType>();
		encounterTypes.add(adultInitial);
		d.setEncounterTypes(encounterTypes);
		
		d.setColumnDisplayFormat(Collections.singletonList(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.ID));
		Integer maxColumnHeaderWidth = d.getMaxColumnHeaderWidth();
				
		SimpleDataSet result = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, null);
		Assert.assertEquals(1, result.getRows().size());
		for (DataSetRow row : result.getRows()) {
					
			Integer encounterId = (Integer) row.getColumnValue("ENCOUNTER_ID");
			Encounter e = Context.getEncounterService().getEncounter(encounterId);
			
			// Standard Columns and Values
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getEncounterId().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("ENCOUNTER_ID").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getEncounterDatetime().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("ENCOUNTER_DATETIME").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getLocation().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("LOCATION").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getProvider().getPersonName().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("PROVIDER").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getPatientId().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("INTERNAL_PATIENT_ID").toString(), maxColumnHeaderWidth));
		}
	}
	
	/**
	 * @see {@link EncounterAndObsDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate an EncounterAndObsDataSetDefinition with Date Range", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateAnEncounterAndObsDataSetDefinitionWithDateRange() throws Exception {
		executeDataSet(XML_ENCOUNTER_DATASET);
		
		EncounterAndObsDataSetDefinition d = new EncounterAndObsDataSetDefinition();
		
		SimpleDateFormat sdf =  new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss") ;
		
		Calendar cal = Calendar.getInstance();
		cal.set(2010, 0, 1, 0, 0, 0);
		Date startDate = cal.getTime(); // January 1st, 2010
		cal.add(Calendar.MONTH, 2);
		Date endDate = cal.getTime(); // March 1st, 2010
		endDate = DateUtil.getEndOfDayIfTimeExcluded(endDate); // this happens in the DAO as well
		
		d.setEncounterDatetimeOnOrAfter(startDate);
		d.setEncounterDatetimeOnOrBefore(endDate);
		
		d.setColumnDisplayFormat(Collections.singletonList(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.ID));
		
		SimpleDataSet result = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, null);
		Assert.assertEquals(2, result.getRows().size());
		
		for (DataSetRow row : result.getRows()) {
			
			Date date = sdf.parse(row.getColumnValue("ENCOUNTER_DATETIME").toString());
			Assert.assertTrue(date.after(startDate) && date.before(endDate));
		}
	}
	
	/**
	 * @see {@link EncounterAndObsDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate an EncounterAndObsDataSetDefinition with filter for which Encounters and Quantity", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateAnEncounterAndObsDataSetDefinitionWithWhichEncountersAndQuantity() throws Exception {
		executeDataSet(XML_ENCOUNTER_DATASET);
		
		EncounterAndObsDataSetDefinition d = new EncounterAndObsDataSetDefinition();
		
		EncounterType adultInitial = new EncounterType(10);
		EncounterType adultReturn = new EncounterType(11);
		EncounterType childSpecial = new EncounterType(13);
		List<EncounterType> encounterTypes = new ArrayList<EncounterType>();
		encounterTypes.add(adultInitial);
		encounterTypes.add(adultReturn);
		encounterTypes.add(childSpecial);
		d.setEncounterTypes(encounterTypes);
		
		d.setColumnDisplayFormat(Collections.singletonList(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.ID));
		d.setWhichEncounterQualifier(TimeQualifier.LAST);
		d.setQuantity(2);

		SimpleDataSet result = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, null);
		
		// Basic Dimensions of Rows and Columns
		Assert.assertEquals(5, result.getRows().size());  // test Quantity
		Assert.assertEquals(17, result.getMetaData().getColumnCount()); // 5 (standard) + 4 (obs) * 3 (value, date, group)
	}
	
	/**
	 * @see {@link EncounterAndObsDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate an EncounterAndObsDataSetDefinition with Column Display ID Only", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateAnEncounterAndObsDataSetDefinitionWithColumnIdOnly() throws Exception {
		executeDataSet(XML_ENCOUNTER_DATASET);
		
		EncounterAndObsDataSetDefinition d = new EncounterAndObsDataSetDefinition();
		
		List<EncounterAndObsDataSetDefinition.ColumnDisplayFormat> columnDisplayFormatList = new ArrayList<EncounterAndObsDataSetDefinition.ColumnDisplayFormat>();
		columnDisplayFormatList.add(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.ID);
		d.setColumnDisplayFormat(columnDisplayFormatList);
		
		EncounterType adultInitial = new EncounterType(10);
		EncounterType adultReturn = new EncounterType(11);
		EncounterType childSpecial = new EncounterType(13);
		List<EncounterType> encounterTypes = new ArrayList<EncounterType>();
		encounterTypes.add(adultInitial);
		encounterTypes.add(adultReturn);
		encounterTypes.add(childSpecial);
		d.setEncounterTypes(encounterTypes);
		
		d.setColumnDisplayFormat(Collections.singletonList(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.ID));
		Integer maxColumnHeaderWidth = d.getMaxColumnHeaderWidth();
		
		SimpleDataSet result = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, null);
		Assert.assertEquals(6, result.getRows().size());
		Assert.assertEquals(17, result.getMetaData().getColumnCount());
		for (DataSetRow row : result.getRows()) {
			
			Integer encounterId = (Integer) row.getColumnValue("ENCOUNTER_ID");
			Encounter e = Context.getEncounterService().getEncounter(encounterId);

			for (Obs obs : e.getObs()) {
				if(!obs.isObsGrouping() && obs.getObsGroup() == null) {
					
					Concept concept = obs.getConcept();
					Concept obsCodedValue = obs.getValueCoded();
					String cellValue = (String) row.getColumnValue(concept.getConceptId().toString());
					String obsNonCodedValue = obs.getValueAsString(Context.getLocale());
					
					if(obsCodedValue != null) {
						Assert.assertTrue(cellValue.equals(obsCodedValue.toString()));
					} else {
						Assert.assertTrue(cellValue.equals(obsNonCodedValue));
					}
				}
			}
								
			// Standard Columns and Values
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getEncounterId().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("ENCOUNTER_ID").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getEncounterDatetime().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("ENCOUNTER_DATETIME").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getLocation().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("LOCATION").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getProvider().getPersonName().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("PROVIDER").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getPatientId().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("INTERNAL_PATIENT_ID").toString(), maxColumnHeaderWidth));
		}
	}
	
	/**
	 * @see {@link EncounterAndObsDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate an EncounterAndObsDataSetDefinition with Column Display ConceptName Only", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateAnEncounterAndObsDataSetDefinitionWithColumnConceptNameOnly() throws Exception {
		executeDataSet(XML_ENCOUNTER_DATASET);
		
		EncounterAndObsDataSetDefinition d = new EncounterAndObsDataSetDefinition();
		
		List<EncounterAndObsDataSetDefinition.ColumnDisplayFormat> columnDisplayFormatList = new ArrayList<EncounterAndObsDataSetDefinition.ColumnDisplayFormat>();
		columnDisplayFormatList.add(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.BEST_SHORT_NAME);
		d.setColumnDisplayFormat(columnDisplayFormatList);
		Integer maxColumnHeaderWidth = d.getMaxColumnHeaderWidth();
		
		EncounterType adultInitial = new EncounterType(10);
		EncounterType adultReturn = new EncounterType(11);
		EncounterType childSpecial = new EncounterType(13);
		List<EncounterType> encounterTypes = new ArrayList<EncounterType>();
		encounterTypes.add(adultInitial);
		encounterTypes.add(adultReturn);
		encounterTypes.add(childSpecial);
		d.setEncounterTypes(encounterTypes);
		
		SimpleDataSet result = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, null);
		Assert.assertEquals(6, result.getRows().size());
		Assert.assertEquals(17, result.getMetaData().getColumnCount());
		for (DataSetRow row : result.getRows()) {
			
			Integer encounterId = (Integer) row.getColumnValue("ENCOUNTER_ID");
			Encounter e = Context.getEncounterService().getEncounter(encounterId);

			for (Obs obs : e.getObs()) {
				if(!obs.isObsGrouping() && obs.getObsGroup() == null) {
					Concept concept = obs.getConcept();
					String headerValue = concept.getBestShortName(Context.getLocale()).toString();
						
					// Replace unwanted characters and change case to upper
					headerValue = headerValue.replaceAll("\\s", "_");
					headerValue = headerValue.replaceAll("-", "_");
					headerValue = headerValue.toUpperCase();
					
					String cellValue = (String) row.getColumnValue(headerValue);
					String obsValue = obs.getValueAsString(Context.getLocale());
					
					Assert.assertTrue(cellValue.equals(obsValue));
					
				}
			}
				
			// Standard Columns and Values
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getEncounterId().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("ENCOUNTER_ID").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getEncounterDatetime().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("ENCOUNTER_DATETIME").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getLocation().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("LOCATION").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getProvider().getPersonName().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("PROVIDER").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getPatientId().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("INTERNAL_PATIENT_ID").toString(), maxColumnHeaderWidth));
		}
	}
	
	/**
	 * @see {@link EncounterAndObsDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate an EncounterAndObsDataSetDefinition with Column Display ConceptName and Concept ID", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateAnEncounterAndObsDataSetDefinitionWithColumnConceptNameAndID() throws Exception {
		executeDataSet(XML_ENCOUNTER_DATASET);
		
		EncounterAndObsDataSetDefinition d = new EncounterAndObsDataSetDefinition();
		
		List<EncounterAndObsDataSetDefinition.ColumnDisplayFormat> columnDisplayFormatList = new ArrayList<EncounterAndObsDataSetDefinition.ColumnDisplayFormat>();
		columnDisplayFormatList.add(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.ID);
		columnDisplayFormatList.add(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.BEST_SHORT_NAME);
		d.setColumnDisplayFormat(columnDisplayFormatList);
		
		EncounterType adultInitial = new EncounterType(10);
		EncounterType adultReturn = new EncounterType(11);
		EncounterType childSpecial = new EncounterType(13);
		List<EncounterType> encounterTypes = new ArrayList<EncounterType>();
		encounterTypes.add(adultInitial);
		encounterTypes.add(adultReturn);
		encounterTypes.add(childSpecial);
		d.setEncounterTypes(encounterTypes);
		
		Integer maxColumnHeaderWidth = d.getMaxColumnHeaderWidth();
		
		SimpleDataSet result = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, null);
		Assert.assertEquals(6, result.getRows().size());
		Assert.assertEquals(17, result.getMetaData().getColumnCount());
		for (DataSetRow row : result.getRows()) {
			
			Integer encounterId = (Integer) row.getColumnValue("ENCOUNTER_ID");
			Encounter e = Context.getEncounterService().getEncounter(encounterId);

			for (Obs obs : e.getObs()) {
				if(!obs.isObsGrouping() && obs.getObsGroup() == null) {
					
					Concept obsConcept = obs.getConcept();
					Concept obsCodedValue = obs.getValueCoded();
					String obsNonCodedValue = obs.getValueAsString(Context.getLocale());
					String headerValue = obsConcept.getConceptId() + "_" + obsConcept.getBestShortName(Context.getLocale()).toString();
					
					// Replace unwanted characters and change case to upper
					headerValue = headerValue.replaceAll("\\s", "_");
					headerValue = headerValue.replaceAll("-", "_");
					headerValue = headerValue.toUpperCase();
					
					String cellValue = (String) row.getColumnValue(headerValue);
					String obsValue = "";
					if(obsCodedValue == null) {
						obsValue = obs.getValueAsString(Context.getLocale());
					} else {
						obsValue = obsCodedValue + "_" + obsNonCodedValue;
					}
					
					Assert.assertTrue(cellValue.equals(obsValue));
				}
			}
				
			// Standard Columns and Values
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getEncounterId().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("ENCOUNTER_ID").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getEncounterDatetime().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("ENCOUNTER_DATETIME").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getLocation().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("LOCATION").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getProvider().getPersonName().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("PROVIDER").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getPatientId().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("INTERNAL_PATIENT_ID").toString(), maxColumnHeaderWidth));
		}
	}
	
	/**
	 * @see {@link EncounterAndObsDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate an EncounterAndObsDataSetDefinition with Column Display MaxColumnWidth", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateAnEncounterAndObsDataSetDefinitionWithMaxColumnWidth() throws Exception {
		executeDataSet(XML_ENCOUNTER_DATASET);
		
		EncounterAndObsDataSetDefinition d = new EncounterAndObsDataSetDefinition();
		
		List<EncounterAndObsDataSetDefinition.ColumnDisplayFormat> columnDisplayFormatList = new ArrayList<EncounterAndObsDataSetDefinition.ColumnDisplayFormat>();
		columnDisplayFormatList.add(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.ID);
		columnDisplayFormatList.add(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.BEST_SHORT_NAME);
		d.setColumnDisplayFormat(columnDisplayFormatList);
		d.setMaxColumnHeaderWidth(22);
		int maxColumnWidth = d.getMaxColumnHeaderWidth();
		
		
		EncounterType adultInitial = new EncounterType(10);
		EncounterType adultReturn = new EncounterType(11);
		EncounterType adultSpecial = new EncounterType(12);
		EncounterType childSpecial = new EncounterType(13);
		List<EncounterType> encounterTypes = new ArrayList<EncounterType>();
		encounterTypes.add(adultInitial);
		encounterTypes.add(adultReturn);
		encounterTypes.add(adultSpecial);
		encounterTypes.add(childSpecial);
		d.setEncounterTypes(encounterTypes);
		
		SimpleDataSet result = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, null);
		Assert.assertEquals(7, result.getRows().size());
		Assert.assertEquals(41, result.getMetaData().getColumnCount());
		for (DataSetRow row : result.getRows()) {
			
			for(DataSetColumn dsc : row.getColumnValues().keySet()) {
				String columnName = dsc.getName();
				
				Assert.assertTrue(columnName.length() <= maxColumnWidth);
			}

//			NOT TESTING VALUES, ONLY HEADERS FOR NOW
//			for(Object obj : row.getColumnValues().values()) {
//				String columnValue = obj.toString();
//				Assert.assertTrue(columnValue.length() <= maxColumnWidth);
//			}
		}
	}
	
	/**
	 * @see {@link EncounterAndObsDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate an EncounterAndObsDataSetDefinition with filter EncounterType and BaseCohort", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateAnEncounterAndObsDataSetDefinitionWithEncounterTypeAndBaseCohort() throws Exception {
		executeDataSet(XML_ENCOUNTER_DATASET);
		
		Cohort males = new Cohort();
		males.addMember(50);
		males.addMember(100);
		males.addMember(102);
		
		EvaluationContext ec = new EvaluationContext();
		ec.setBaseCohort(males);
		
		EncounterAndObsDataSetDefinition d = new EncounterAndObsDataSetDefinition();
		
		EncounterType adultInitial = new EncounterType(10);
		d.setEncounterTypes(Collections.singletonList(adultInitial));
		Integer maxColumnHeaderWidth = d.getMaxColumnHeaderWidth();
		
		SimpleDataSet result = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, ec);		
		
		for (DataSetRow row : result.getRows()) {
			Integer encounterId = (Integer) row.getColumnValue("ENCOUNTER_ID");
			Encounter e = Context.getEncounterService().getEncounter(encounterId);
			
			Integer patientId = (Integer)row.getColumnValue("INTERNAL_PATIENT_ID");
			Patient p = Context.getPatientService().getPatient(patientId);
			
			Assert.assertEquals("M", p.getGender());
			
			// Standard Columns and Values
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getEncounterId().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("ENCOUNTER_ID").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getEncounterDatetime().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("ENCOUNTER_DATETIME").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getLocation().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("LOCATION").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getProvider().getPersonName().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("PROVIDER").toString(), maxColumnHeaderWidth));
			Assert.assertEquals(ObjectUtil.trimStringIfNeeded(e.getPatientId().toString(), maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded(row.getColumnValue("INTERNAL_PATIENT_ID").toString(), maxColumnHeaderWidth));
		}
	}
	
	/**
	 * @see {@link EncounterAndObsDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate an EncounterAndObsDataSetDefinition with an empty BaseCohort", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateAnEncounterAndObsDataSetDefinitionWithEmptyBaseCohort() throws Exception {
		executeDataSet(XML_ENCOUNTER_DATASET);
		
		Cohort died = new Cohort();
		
		EvaluationContext ec = new EvaluationContext();
		ec.setBaseCohort(died);
		
		EncounterAndObsDataSetDefinition d = new EncounterAndObsDataSetDefinition();
		
		EncounterType adultInitial = new EncounterType(10);
		d.setEncounterTypes(Collections.singletonList(adultInitial));
		Integer maxColumnHeaderWidth = d.getMaxColumnHeaderWidth();
		
		SimpleDataSet result = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, ec);		
		
		Assert.assertEquals(0, result.getRows().size());
	}
}