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
package org.openmrs.module.reporting.dataset.definition;

import java.util.Date;
import java.util.Iterator;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.RowPerObjectDataSet;
import org.openmrs.module.reporting.dataset.column.converter.AgeConverter;
import org.openmrs.module.reporting.dataset.column.converter.DateConverter;
import org.openmrs.module.reporting.dataset.column.definition.ColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.patient.PatientIdColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.person.AgeColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.person.BirthdateColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.person.GenderColumnDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Test the evaluation of the PatientDataSetDefinition
 */
public class PatientDataSetDefinitionTest extends BaseModuleContextSensitiveTest {

	protected static Log log = LogFactory.getLog(PatientDataSetDefinitionTest.class);
	
	@Test
	public void evaluate_shouldExportASimpleProperty() throws Exception {
		RowPerPatientDataSetDefinition d = new RowPerPatientDataSetDefinition();
		d.addColumnDefinition(new GenderColumnDefinition("Sexe"));
		RowPerObjectDataSet dataset = (RowPerObjectDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, getEvaluationContext());
		Assert.assertEquals("M", dataset.getColumnValue(2, "Sexe"));
	}
	
	@Test
	public void evaluate_shouldExportAConvertedProperty() throws Exception {
		RowPerPatientDataSetDefinition d = new RowPerPatientDataSetDefinition();
		d.addColumnDefinition(new BirthdateColumnDefinition("birthdate", new DateConverter("dd/MMM/yyyy")));
		RowPerObjectDataSet dataset = (RowPerObjectDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, getEvaluationContext());
		Assert.assertEquals("08/Apr/1975", dataset.getColumnValue(2, "birthdate"));
	}
	
	@Test
	public void evaluate_shouldExportAnInternalConvertedProperty() throws Exception {
		RowPerPatientDataSetDefinition d = new RowPerPatientDataSetDefinition();
		d.addColumnDefinition(new PatientIdColumnDefinition("EMR ID"));
		
		AgeColumnDefinition ageAtStart = new AgeColumnDefinition("Age At Start", new AgeConverter());
		ageAtStart.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));
		d.addColumnDefinition(ageAtStart, "effectiveDate=${startDate}");

		AgeColumnDefinition ageAtEnd = new AgeColumnDefinition("Age At End", new AgeConverter());
		ageAtEnd.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));
		d.addColumnDefinition(ageAtEnd, "effectiveDate=${endDate}");
		
		RowPerObjectDataSet dataset = (RowPerObjectDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, getEvaluationContext());
		Assert.assertEquals(35, dataset.getColumnValue(2, "Age At Start"));
		Assert.assertEquals(36, dataset.getColumnValue(2, "Age At End"));
		outputDataSet(dataset);
	}
	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}
	
	public EvaluationContext getEvaluationContext() {
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("startDate", DateUtil.getDateTime(2010, 7, 1));
		context.addParameterValue("endDate", DateUtil.getDateTime(2011, 6, 30));
		return context;
	}
	
	public void outputDataSet(RowPerObjectDataSet dataset) {
		StringBuilder output = new StringBuilder();
		for (Mapped<? extends ColumnDefinition> c : dataset.getDefinition().getColumnDefinitions()) {
			output.append(c.getParameterizable().getName() + "\t");
		}
		output.append("\n");
		for (Iterator<DataSetRow> i = dataset.iterator(); i.hasNext();) {
			DataSetRow r = i.next();
			for (DataSetColumn c : r.getColumnValues().keySet()) {
				output.append(r.getColumnValue(c) + "\t");
			}
			output.append("\n");
		}
		System.out.println(output.toString());
	}
}