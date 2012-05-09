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
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * test class for testing evaluation of SQLIndicators
 */
public class SqlIndicatorTest  extends BaseModuleContextSensitiveTest {

	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
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
				//System.out.println(column.getKey() + " " + ((Number) column.getValue()).toString());
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
		
			Map<String,Object> paramValues = new HashMap<String,Object>(); //the actual values we pass in at runtime by adding to EvaluationContext
			paramValues.put("numValue", Integer.valueOf(55));
			paramValues.put("denValue", Integer.valueOf(110));
			
			List<Parameter> params= new ArrayList<Parameter>();
			params.add(new Parameter("numValue", "num label", Integer.class)); //the local parameter to global parameter mappings
			params.add(new Parameter("denValue", "den lablel", Integer.class));
			
			
			//build indicators
			SqlIndicator indicator = new SqlIndicator();
			String sql = "SELECT distinct(:numValue) as res from patient";
			indicator.setSql(sql);

			String sql2 = "SELECT distinct(:denValue) as res2 from patient";
			indicator.setDenominatorSql(sql2);
			indicator.setParameters(params);
			
			//build simpleDataSetDefintiion
			SimpleIndicatorDataSetDefinition d = new SimpleIndicatorDataSetDefinition();
			d.setParameters(params);
			d.addColumn("indicator_1", "indicator_1_label", new Mapped<Indicator>(indicator, ParameterizableUtil.createParameterMappings("numValue=${numValue},denValue=${denValue}")));
			
			//build a report
			ReportDefinition rd = new ReportDefinition();
			rd.addDataSetDefinition(d, null);
			rd.setParameters(params);
			
			//run the report
			ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
			EvaluationContext ec = new EvaluationContext();
			ec.setParameterValues(paramValues);//send values to all the params
			ReportData data = rds.evaluate(new Mapped<ReportDefinition>(rd, ParameterizableUtil.createParameterMappings("numValue=${numValue},denValue=${denValue}")), ec);
			SimpleDataSet ds = (SimpleDataSet) data.getDataSets().values().iterator().next();
			
			//unpack the report
			DataSetRow row = ds.getRows().iterator().next();
			//System.out.println(row.getColumnValuesByKey().entrySet().iterator().next().getValue().toString());
			Assert.assertTrue(row.getColumnValuesByKey().entrySet().iterator().next().getValue().toString().contains("50%"));  //represents divide by 0
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
			//System.out.println(decimalError);
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
