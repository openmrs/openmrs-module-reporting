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
package org.openmrs.module.reporting.indicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.SimpleIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * test class for testing evaluation of SQLIndicators
 */
public class SqlIndicatorTest  extends BaseModuleContextSensitiveTest {

	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}
	
	@Test
	public void sqlIndicator_shouldEvaluateSqlIndicator() throws Exception { 
		
			//build indicators
			SqlIndicator indicator = new SqlIndicator();
			String sql = "SELECT distinct(251) as res from patient";
			indicator.setSql(sql);
			
			SqlIndicator indicator2 = new SqlIndicator();
			String sql2 = "SELECT distinct(0.7154) as res2 from patient";
			indicator2.setSql(sql2);
			
			//build simpleDataSetDefintiion
			SimpleIndicatorDataSetDefinition d = new SimpleIndicatorDataSetDefinition();
			d.addColumn("indicator_1", "indicator_1_label", new Mapped<Indicator>(indicator, null));
			d.addColumn("indicator_2", "indicator_2_label", new Mapped<Indicator>(indicator2, null));
			
			//build a report
			ReportDefinition rd = new ReportDefinition();
			rd.addDataSetDefinition(d, null);
			
			//run the report
			ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
			ReportData data = rds.evaluate(new Mapped<ReportDefinition>(rd, null), new EvaluationContext());
			SimpleDataSet ds = (SimpleDataSet) data.getDataSets().values().iterator().next();
			
			//unpack the report
			DataSetRow row = ds.getRows().iterator().next();
			for (Map.Entry<String, Object> column : row.getColumnValuesByKey().entrySet() ){
				if (column.getKey().equals("indicator_1"))
					Assert.assertTrue(column.getValue().toString().equals("251"));
				if (column.getKey().equals("indicator_2"))
					Assert.assertTrue(column.getValue().toString().equals("0.7154"));
			}	
	}
	
	//TODO:  test divide by zero
	@Test
	public void sqlIndicator_shouldEvaluateSqlIndicatorDivideByZero() throws Exception { 
		
			//build indicators
			SqlIndicator indicator = new SqlIndicator();
			String sql = "SELECT distinct(4736) as res from patient";
			indicator.setSql(sql);

			String sql2 = "SELECT distinct(0) as res2 from patient";
			indicator.setDenominatorSql(sql2);
			
			//build simpleDataSetDefintiion
			SimpleIndicatorDataSetDefinition d = new SimpleIndicatorDataSetDefinition();
			d.addColumn("indicator_1", "indicator_1_label", new Mapped<Indicator>(indicator, null));
			
			//build a report
			ReportDefinition rd = new ReportDefinition();
			rd.addDataSetDefinition(d, null);
			
			//run the report
			ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
			ReportData data = rds.evaluate(new Mapped<ReportDefinition>(rd, null), new EvaluationContext());
			SimpleDataSet ds = (SimpleDataSet) data.getDataSets().values().iterator().next();
			
			//unpack the report
			DataSetRow row = ds.getRows().iterator().next();
			Assert.assertTrue(row.getColumnValuesByKey().entrySet().iterator().next().getValue().toString().contains("N/A")); //represents divide by 0
	}
	
	
	@Test
	public void sqlIndicator_shouldEvaluateSqlIndicatorNullNumerator() throws Exception { 
		
			//build indicators
			SqlIndicator indicator = new SqlIndicator();
			String sql = "SELECT distinct(null) as res from patient";
			indicator.setSql(sql);

			String sql2 = "SELECT distinct(55) as res2 from patient";
			indicator.setDenominatorSql(sql2);
			
			//build simpleDataSetDefintiion
			SimpleIndicatorDataSetDefinition d = new SimpleIndicatorDataSetDefinition();
			d.addColumn("indicator_1", "indicator_1_label", new Mapped<Indicator>(indicator, null));
			
			//build a report
			ReportDefinition rd = new ReportDefinition();
			rd.addDataSetDefinition(d, null);
			
			//run the report
			ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
			ReportData data = rds.evaluate(new Mapped<ReportDefinition>(rd, null), new EvaluationContext());
			SimpleDataSet ds = (SimpleDataSet) data.getDataSets().values().iterator().next();
			
			//unpack the report
			DataSetRow row = ds.getRows().iterator().next();
			Assert.assertTrue(row.getColumnValuesByKey().entrySet().iterator().next().getValue().toString().contains("NaN")); //represents missing data
	}

	
	@Test
	public void sqlIndicator_shouldEvaluateSqlIndicatorNullDenominator() throws Exception { 
		
			//build indicators
			SqlIndicator indicator = new SqlIndicator();
			String sql = "SELECT distinct(55) as res from patient";
			indicator.setSql(sql);

			String sql2 = "SELECT distinct(null) as res2 from patient";
			indicator.setDenominatorSql(sql2);
			
			//build simpleDataSetDefintiion
			SimpleIndicatorDataSetDefinition d = new SimpleIndicatorDataSetDefinition();
			d.addColumn("indicator_1", "indicator_1_label", new Mapped<Indicator>(indicator, null));
			
			//build a report
			ReportDefinition rd = new ReportDefinition();
			rd.addDataSetDefinition(d, null);
			
			//run the report
			ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
			ReportData data = rds.evaluate(new Mapped<ReportDefinition>(rd, null), new EvaluationContext());
			SimpleDataSet ds = (SimpleDataSet) data.getDataSets().values().iterator().next();
			
			//unpack the report
			DataSetRow row = ds.getRows().iterator().next();
			Assert.assertTrue(row.getColumnValuesByKey().entrySet().iterator().next().getValue().toString().contains("N/A"));  //represents divide by 0
	}
	
	//TODO:  test that parameters work
	@Test
	public void sqlIndicator_shouldEvaluateSqlIndicatorUsesParameters() throws Exception {
			
			List<Parameter> params= new ArrayList<Parameter>();
			params.add(new Parameter("numValue", "num label", Integer.class));
			params.add(new Parameter("denValue", "den label", Integer.class));

			Map<String, Object> paramMappings = ParameterizableUtil.createParameterMappings("numValue=${numValue},denValue=${denValue}");

			//build a report
			ReportDefinition rd = new ReportDefinition();
			rd.addParameters(params);

			SimpleIndicatorDataSetDefinition d = new SimpleIndicatorDataSetDefinition();
			d.addParameters(params);
			rd.addDataSetDefinition(d, paramMappings);

			SqlIndicator indicator = new SqlIndicator();
			indicator.addParameters(params);
			indicator.setSql("SELECT patient_id from patient where patient_id = :numValue");
			indicator.setDenominatorSql("SELECT patient_id from patient where patient_id = :denValue");

			d.addColumn("indicator_1", "indicator_1_label", new Mapped<Indicator>(indicator, ParameterizableUtil.createParameterMappings("numValue=${numValue},denValue=${denValue}")));

			EvaluationContext context = new EvaluationContext();
			context.addParameterValue("numValue", new Integer(6));
			context.addParameterValue("denValue", new Integer(24));
			
			//run the report
			ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
			ReportData data = rds.evaluate(rd, context);
			SimpleDataSet ds = (SimpleDataSet) data.getDataSets().values().iterator().next();
			
			//unpack the report
			DataSetRow row = ds.getRows().iterator().next();
			Assert.assertTrue(row.getColumnValuesByKey().entrySet().iterator().next().getValue().toString().contains("25%"));  //represents divide by 0
	}
	
	
	@Test
	public void sqlIndicator_shouldEvaluateSqlIndicatorDecimals() throws Exception { 
		
			//build indicators
			SqlIndicator indicator = new SqlIndicator();
			String sql = "SELECT distinct(.222) as res from patient";
			indicator.setSql(sql);
			
			String sql2 = "SELECT distinct(.44) as res2 from patient";
			indicator.setDenominatorSql(sql2);
			
			//build simpleDataSetDefintiion
			SimpleIndicatorDataSetDefinition d = new SimpleIndicatorDataSetDefinition();
			d.addColumn("indicator_1", "indicator_1_label", new Mapped<Indicator>(indicator, null));
			
			//build a report
			ReportDefinition rd = new ReportDefinition();
			rd.addDataSetDefinition(d, null);
			
			//run the report
			ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
			String decimalError = "";
			try {
				ReportData data = rds.evaluate(new Mapped<ReportDefinition>(rd, null), new EvaluationContext()); //throws Exception because we can't currently pass decimals as numerators or denominators
			} catch (Exception ex){
				decimalError = ex.getCause().getMessage();
			}
			Assert.assertTrue(decimalError.contains("FRACTION indicator type is not currently supported by SimpleIndicatorResult"));
	}
	
	@Test
	public void sqlIndicator_shouldNotAllowQueriesThatReturnMoreThanOneColumn() throws Exception { 
		
			//build indicators
			SqlIndicator indicator = new SqlIndicator();
			String sql = "SELECT distinct(.222) as res, 33 as res2 from patient";  ///2 columns returned by sql.
			indicator.setSql(sql);

			
			//build simpleDataSetDefintiion
			SimpleIndicatorDataSetDefinition d = new SimpleIndicatorDataSetDefinition();
			d.addColumn("indicator_1", "indicator_1_label", new Mapped<Indicator>(indicator, null));
			
			//build a report
			ReportDefinition rd = new ReportDefinition();
			rd.addDataSetDefinition(d, null);
			
			//run the report
			ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
			String errorCaught = "";
			try {
				ReportData data = rds.evaluate(new Mapped<ReportDefinition>(rd, null), new EvaluationContext()); //throws Exception because we can't currently pass decimals as numerators or denominators
			} catch (Exception ex){
				errorCaught = ex.getCause().getMessage();
			}
			Assert.assertTrue(errorCaught.contains("The query that you're using in your indicator should only return 1 column"));
	}
	
	@Test
	public void sqlIndicator_shouldNotAllowQueriesThatReturnMoreThanOneRow() throws Exception { 
		
			//build indicators
			SqlIndicator indicator = new SqlIndicator();
			String sql = "SELECT person_id from person";  ///returns more than 1 row
			indicator.setSql(sql);

			
			//build simpleDataSetDefintiion
			SimpleIndicatorDataSetDefinition d = new SimpleIndicatorDataSetDefinition();
			d.addColumn("indicator_1", "indicator_1_label", new Mapped<Indicator>(indicator, null));
			
			//build a report
			ReportDefinition rd = new ReportDefinition();
			rd.addDataSetDefinition(d, null);
			
			//run the report
			ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
			String errorCaught = "";
			try {
				ReportData data = rds.evaluate(new Mapped<ReportDefinition>(rd, null), new EvaluationContext()); //throws Exception because we can't currently pass decimals as numerators or denominators
			} catch (Exception ex){
				errorCaught = ex.getCause().getMessage();
			}
			Assert.assertTrue(errorCaught.contains("The query that you're using in your indicator should only return 1 row."));
	}
}
