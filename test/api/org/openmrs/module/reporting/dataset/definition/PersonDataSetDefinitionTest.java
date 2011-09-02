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

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.column.converter.AgeConverter;
import org.openmrs.module.reporting.dataset.column.definition.ColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.person.AgeColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.person.GenderColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.person.PersonIdColumnDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Test the evaluation of the PatientDataSetDefinition
 */
public class PersonDataSetDefinitionTest extends BaseModuleContextSensitiveTest {

	protected static Log log = LogFactory.getLog(PersonDataSetDefinitionTest.class);

	@Test
	public void evaluate_shouldEvaluateDataSetDefinition() throws Exception {
		EvaluationContext context = new EvaluationContext();
		Calendar cal = Calendar.getInstance();
		context.addParameterValue("endDate", cal.getTime());
		cal.add(Calendar.MONTH, -12);
		context.addParameterValue("startDate", cal.getTime());
		
		RowPerPersonDataSetDefinition d = new RowPerPersonDataSetDefinition();
		
		d.addColumnDefinition(new PersonIdColumnDefinition("Person ID"));
		d.addColumnDefinition(new GenderColumnDefinition("Sexe"));
		
		AgeColumnDefinition ageAtStart = new AgeColumnDefinition("Age At Start", new AgeConverter());
		ageAtStart.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));
		d.addColumnDefinition(ageAtStart, "effectiveDate=${startDate}");

		AgeColumnDefinition ageAtEnd = new AgeColumnDefinition("Age At End", new AgeConverter());
		ageAtEnd.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));
		d.addColumnDefinition(ageAtEnd, "effectiveDate=${endDate}");

		DataSet dataset = Context.getService(DataSetDefinitionService.class).evaluate(d, context);
		
		StringBuilder output = new StringBuilder();
		for (Mapped<? extends ColumnDefinition> c : d.getColumnDefinitions()) {
			output.append(c.getParameterizable().getName() + "\t");
		}
		output.append("\n");
		int rowNum = 0;
		for (Iterator<DataSetRow> i = dataset.iterator(); i.hasNext();) {
			DataSetRow r = i.next();
			if (rowNum++ == 0) {
				for (DataSetColumn c : r.getColumnValues().keySet()) {
					output.append(c.getLabel() + "\t");
				}
				output.append("\n");
			}
			for (DataSetColumn c : r.getColumnValues().keySet()) {
				output.append(r.getColumnValue(c) + "\t");
			}
			output.append("\n");
		}
		System.out.println(output.toString());
	}
}