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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterProvider;
import org.openmrs.Obs;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.CohortUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.column.ObsColumnDescriptor;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterAndObsDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterAndObsDataSetDefinition.ColumnDisplayFormat;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.BasicEncounterQuery;
import org.openmrs.module.reporting.query.encounter.service.EncounterQueryService;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * The logic that evaluates a {@link EncounterAndObsDataSetDefinition} and
 * produces an {@link DataSet}
 * 
 * @see EncounterAndObsDataSetDefinition
 */
//@Handler(supports = { EncounterAndObsDataSetDefinition.class })
public class EncounterAndObsDataSetEvaluator implements DataSetEvaluator {

	protected static final Log log = LogFactory.getLog(EncounterAndObsDataSetEvaluator.class);

	private static final String EMPTY = "";

	@Autowired
	EncounterQueryService encounterQueryService;

	@Autowired
	EvaluationService evaluationService;

	/**
	 * Public constructor
	 */
	public EncounterAndObsDataSetEvaluator() {
	}

	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 * @should evaluate an EncounterAndObsDataSetDefinition
	 */
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {

		EncounterAndObsDataSetDefinition definition = (EncounterAndObsDataSetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);
		context = ObjectUtil.nvl(context, new EvaluationContext());
		Cohort cohort = context.getBaseCohort();
		if (context.getLimit() != null) {
			CohortUtil.limitCohort(cohort, context.getLimit());
		}

		// Retrieve the rows for the dataset

		BasicEncounterQuery encounterQuery = new BasicEncounterQuery();
		encounterQuery.setWhich(definition.getWhichEncounterQualifier());
		encounterQuery.setWhichNumber(definition.getQuantity());
		encounterQuery.setEncounterTypes(definition.getEncounterTypes());
		encounterQuery.setForms(definition.getForms());
		encounterQuery.setOnOrBefore(definition.getEncounterDatetimeOnOrBefore());
		encounterQuery.setOnOrAfter(definition.getEncounterDatetimeOnOrAfter());

		EncounterQueryResult encounterIdRows = encounterQueryService.evaluate(encounterQuery, context);

		HqlQueryBuilder q = new HqlQueryBuilder();
		EncounterEvaluationContext eec = new EncounterEvaluationContext();
		eec.setBaseEncounters(encounterIdRows);
		q.from(Encounter.class, "e").whereEncounterIn("e.encounterId", eec);
		List<Encounter> encounters = evaluationService.evaluateToList(q, Encounter.class);

		// Determine what columns to display in the dataset

		List<EncounterAndObsDataSetDefinition.ObsOptionalColumn> optionalColumns = definition.getOptionalColumns();
		List<PatientIdentifierType> patientIdentifierTypes = definition.getPatientIdentifierTypes();
		List<EncounterAndObsDataSetDefinition.ColumnDisplayFormat> columnDisplayFormat = definition.getColumnDisplayFormat();
		Integer maxColumnHeaderWidth = definition.getMaxColumnHeaderWidth();
		
		if (patientIdentifierTypes == null) {
			patientIdentifierTypes = new ArrayList<PatientIdentifierType>();
		}
		
		if (columnDisplayFormat == null) {
			columnDisplayFormat = new ArrayList<ColumnDisplayFormat>();
		}
		
		if(columnDisplayFormat.size() == 0) {
			columnDisplayFormat.add(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.ID);
		}

		// section index should be added here
		
		// Store all encounters within a data structure that keeps track of obs, obs groups, and their occurrences
		// in order that column headers are unique and children obs are grouped by their parent
		Map<Encounter, Map<ObsColumnDescriptor, Obs>> populatedFieldMap = populateFieldMap(encounters);
		
		// Keeps track of the column headers for all obs-related columns
		Set<ObsColumnDescriptor> allColumns = new TreeSet<ObsColumnDescriptor>();

		for(Encounter encounter : encounters) {
			Map<ObsColumnDescriptor, Obs> obsInEncounter = populatedFieldMap.get(encounter);
			// Not all encounters will have data for all columns but 
			// each encounter row should have all column headers 
			// so encounters line up properly under a common set of column headers
			allColumns.addAll(obsInEncounter.keySet());
		}
		
		// add the data to the DataSet
		return addData(dataSet, encounters, patientIdentifierTypes, optionalColumns, columnDisplayFormat, maxColumnHeaderWidth, allColumns, populatedFieldMap);
	}

	/**
	 * This method returns a DataSet with the values for the report. Each
	 * encounter is stored in a data structure with each column header @see
	 * org.openmrs.module.reporting.common.ColumnDisplayFormat and corresponding
	 * obs. Column headers must be unique to avoid clobbering. As such, duplicate
	 * concepts are given a unique occurrence number. Top level obs are handled
	 * in this method whereas children are handled
	 * @return DataSet with columnheaders and values filled in for the report
	 */
	public Map<Encounter, Map<ObsColumnDescriptor, Obs>> populateFieldMap(List<Encounter> encounters) {

		// Create a map to keep track of obs and their occurrences for each encounter
		// TODO: Can this be optimized?
		Map<Encounter, Map<ObsColumnDescriptor, Obs>> allEncounters = new HashMap<Encounter, Map<ObsColumnDescriptor, Obs>>();

		for (Encounter e : encounters) {

			// go through each encounter and track the column header with each obs and at each level (parent, child)
			Map<Obs, Integer> fieldMap = new HashMap<Obs, Integer>();
			Map<ObsColumnDescriptor, Obs> obsInEncounter = new HashMap<ObsColumnDescriptor, Obs>();
	
			addEncounterToFieldMap(fieldMap, e);

			// build the column header based on the full path (child obs, obs parent, grandparent, etc.)
			// iterate over all lowest child obs
			for (Obs obs : e.getObs()) {

				// lowest child
				if (obs != null) {
					
					// construct the column header based on parents and their
					// occurrence number at a given level
					ObsColumnDescriptor obsKey = new ObsColumnDescriptor(obs, fieldMap);

					// link obs column header to obs itself
					obsInEncounter.put(obsKey, obs);
				}
			}

			allEncounters.put(e, obsInEncounter);
		}

		// Add all encounters to the report data set
		return allEncounters;
	}
	
	/**
	 *  Iterate over all encounters to determine occurrence number based on
	 *  concept duplication at same level
	 *  For instance, an obs group 'functional review of symptoms' might
	 *  occur twice in the same form
	 *  In this case, it is important to maintain which children belong to
	 *  which parent
	 * 
	 * @param fieldMap
	 * @param e the encounter to add to the fieldMap
	 * @return fieldMap
	 */
	public static Map<Obs, Integer> addEncounterToFieldMap(Map<Obs, Integer> fieldMap, Encounter e) {
		Map<Concept, Integer> parentMap = new HashMap<Concept, Integer>();

		for (Obs obs : e.getObsAtTopLevel(false)) {

			Integer occurrence = (parentMap.get(obs.getConcept()) != null) ? parentMap.get(obs.getConcept()) : 0;
			occurrence++;
			parentMap.put(obs.getConcept(), occurrence);

			// Add top level obs to the fieldMap
			fieldMap.put(obs, occurrence);

			// Add children observations to the fieldMap recursively.
			// fieldMap will have each obs with the occurrence number
			// for a given concept on a given level of the tree structure
			addChildrenObsToFieldMap(fieldMap, obs);
		}
		return fieldMap;
	}

	/**
	 * Column headers must be unique to avoid clobbering and to ensure it is
	 * clear which columns are grouped together by obsgroup. As such, duplicate
	 * concepts at the parent level or duplicate concepts in the same obs group
	 * are given a unique occurrence number. For instance, in an obs group
	 * Functional Review Of Symptoms, there might be two child obs with the
	 * concept Symptom Present. To distinguish them, one is given an occurrence
	 * number of 1 and the next an occurrence number of 2.
	 * 
	 * @param fieldMap
	 * @param obs
	 * @return Map<Obs, Integer> the filled in fieldMap
	 */
	public static Map<Obs, Integer> addChildrenObsToFieldMap(Map<Obs, Integer> fieldMap, Obs obs) {

		if (obs != null && obs.isObsGrouping()) {

			Set<Obs> childSet = obs.getGroupMembers();
			List<Obs> children = new ArrayList<Obs>(childSet);

			Map<Concept, Integer> childMap = new HashMap<Concept, Integer>();
			for (Obs o : children) {
				// check for duplicate concepts among this batch of children
				Integer occurrence = (childMap.get(o.getConcept()) != null) ? childMap.get(o.getConcept()) : 0;
				occurrence++;
				childMap.put(o.getConcept(), occurrence);

				fieldMap.put(o, occurrence);
				addChildrenObsToFieldMap(fieldMap, o);
			}
		}

		return fieldMap;
	}
	
	/**
	 * Adds the column headers and column data to the DataSet
	 * 
	 * @param dataSet
	 * @param encounters
	 * @param patientIdentifierTypes
	 * @param optionalColumns
	 * @param columnDisplayFormat
	 * @param maxColumnHeaderWidth
	 * @param allColumns
	 * @param fieldMap
	 * @return
	 */
	public DataSet addData(SimpleDataSet dataSet, List<Encounter> encounters, List<PatientIdentifierType> patientIdentifierTypes, List<EncounterAndObsDataSetDefinition.ObsOptionalColumn> optionalColumns, List<EncounterAndObsDataSetDefinition.ColumnDisplayFormat> columnDisplayFormat, Integer maxColumnHeaderWidth, Set<ObsColumnDescriptor> allColumns, Map<Encounter, Map<ObsColumnDescriptor, Obs>> fieldMap) {
		for (Encounter encounter : encounters) {
			
			DataSetRow row = new DataSetRow();

			List<String> providerNames = new ArrayList<String>();
			for (EncounterProvider ep : encounter.getEncounterProviders()) {
				providerNames.add(ep.getProvider().getName());
			}

			// Add the standard columns for encounters
			DataSetColumn c1 = new DataSetColumn(ObjectUtil.trimStringIfNeeded("ENCOUNTER_ID", maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded("ENCOUNTER_ID", maxColumnHeaderWidth), Integer.class);
			row.addColumnValue(c1, encounter.getEncounterId());
			DataSetColumn c2 = new DataSetColumn(ObjectUtil.trimStringIfNeeded("ENCOUNTER_DATETIME", maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded("ENCOUNTER_DATETIME", maxColumnHeaderWidth), String.class);
			row.addColumnValue(c2, encounter.getEncounterDatetime().toString());
			DataSetColumn c3 = new DataSetColumn(ObjectUtil.trimStringIfNeeded("LOCATION", maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded("LOCATION", maxColumnHeaderWidth), String.class);
			row.addColumnValue(c3, (encounter.getLocation() != null) ? encounter.getLocation().getName() : EMPTY);
			DataSetColumn c4 = new DataSetColumn(ObjectUtil.trimStringIfNeeded("PROVIDER", maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded("PROVIDER", maxColumnHeaderWidth), String.class);
			row.addColumnValue(c4, OpenmrsUtil.join(providerNames, ", "));
			DataSetColumn c5 = new DataSetColumn(ObjectUtil.trimStringIfNeeded("INTERNAL_PATIENT_ID", maxColumnHeaderWidth), ObjectUtil.trimStringIfNeeded("INTERNAL_PATIENT_ID", maxColumnHeaderWidth), Integer.class);
			row.addColumnValue(c5, encounter.getPatient() != null ? encounter.getPatient().getPatientId() : EMPTY);

			if (patientIdentifierTypes != null) {
				for (PatientIdentifierType pit : patientIdentifierTypes) {
					List<PatientIdentifier> patientIdentifiers = encounter.getPatient().getPatientIdentifiers(pit);
					
					StringBuffer sbPatientIdentifiers = new StringBuffer();
					int count = 0;
					for (PatientIdentifier patientIdentifier : patientIdentifiers) {
						if (count > 0) {
							sbPatientIdentifiers.append(", ");
						}
						sbPatientIdentifiers.append(patientIdentifier.toString());
						count++;
					}

					DataSetColumn c6 = new DataSetColumn(pit.getName(), ObjectUtil.trimStringIfNeeded(pit.getName(), maxColumnHeaderWidth), String.class);
					row.addColumnValue(c6, sbPatientIdentifiers.toString());
				}
			}

			Map<ObsColumnDescriptor, Obs> obsInEncounter = fieldMap.get(encounter);
			
			// Look up all obs for a given encounter based on column headers for all encounters
			for (ObsColumnDescriptor columnKey : allColumns) {

				Obs obs = obsInEncounter.get(columnKey);
				String columnName = columnKey.format(columnDisplayFormat, maxColumnHeaderWidth);
				DataSetColumn obsDsc = new DataSetColumn(columnName, columnName, String.class);

				StringBuffer columnValue = new StringBuffer();
				if (obs != null && obs.getValueCoded() != null) {
					if (columnDisplayFormat.contains(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.ID)) {
						columnValue.append(obs.getValueCoded());
					}

					if (columnDisplayFormat.contains(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.ID) && columnDisplayFormat.contains(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.BEST_SHORT_NAME)) {
						columnValue.append("_");
					}

					if (columnDisplayFormat.contains(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.BEST_SHORT_NAME)) {
						String conceptName = obs.getValueAsString(Context.getLocale());
						columnValue.append(maxColumnHeaderWidth != null && conceptName.length() > maxColumnHeaderWidth - columnValue.length() ? conceptName.substring(0, maxColumnHeaderWidth
								- columnValue.length() - 1) : conceptName);
					}
					row.addColumnValue(obsDsc, (obs != null) ? columnValue.toString() : EMPTY);
				} else {
					row.addColumnValue(obsDsc, (obs != null) ? obs.getValueAsString(Context.getLocale()) : EMPTY);
				}

				String dateColumnName = columnKey.format(columnDisplayFormat, maxColumnHeaderWidth != null ? maxColumnHeaderWidth - 5 : null);
				DataSetColumn obsDscDate = new DataSetColumn(dateColumnName + "_DATE", dateColumnName + "_DATE", String.class);
				row.addColumnValue(obsDscDate, (obs != null) ? obs.getObsDatetime().toString() : EMPTY);

				String parentColumnName = columnKey.format(columnDisplayFormat, maxColumnHeaderWidth != null ? maxColumnHeaderWidth - 7 : null);
				DataSetColumn obsDscParent = new DataSetColumn(parentColumnName + "_PARENT", parentColumnName + "_PARENT", String.class);
				row.addColumnValue(obsDscParent, (obs != null && obs.getObsGroup() != null) ? obs.getObsGroup().getId() : EMPTY);

				if (optionalColumns != null) {

					if (optionalColumns.contains(EncounterAndObsDataSetDefinition.ObsOptionalColumn.VALUE_MODIFIER)) {
						String valModColumnName = columnKey.format(columnDisplayFormat, maxColumnHeaderWidth != null ? maxColumnHeaderWidth - 10 : null);
						DataSetColumn obsDscValueModifier = new DataSetColumn(valModColumnName + "_VALUE_MOD", valModColumnName + "_VALUE_MOD",
								String.class);
						row.addColumnValue(obsDscValueModifier, (obs != null) ? obs.getValueModifier() : EMPTY);
					}
					if (optionalColumns.contains(EncounterAndObsDataSetDefinition.ObsOptionalColumn.ACCESSION_NUMBER)) {
						String accessionNumColumnName = columnKey.format(columnDisplayFormat, maxColumnHeaderWidth != null ? maxColumnHeaderWidth - 14 : null);
						DataSetColumn obsDscAccessionNumber = new DataSetColumn(accessionNumColumnName + "_ACCESSION_NUM", accessionNumColumnName
								+ "_ACCESSION_NUM", String.class);
						row.addColumnValue(obsDscAccessionNumber, (obs != null) ? obs.getAccessionNumber() : EMPTY);
					}
					if (optionalColumns.contains(EncounterAndObsDataSetDefinition.ObsOptionalColumn.COMMENT)) {
						String commentColumnName = columnKey.format(columnDisplayFormat, maxColumnHeaderWidth != null ? maxColumnHeaderWidth - 8 : null);
						DataSetColumn obsDscComment = new DataSetColumn(commentColumnName + "_COMMENT", commentColumnName + "_COMMENT",
								String.class);
						row.addColumnValue(obsDscComment, (obs != null) ? obs.getComment() : EMPTY);
					}
				}
			}
			
			dataSet.addRow(row);
		}
		return dataSet;
		
	}
}