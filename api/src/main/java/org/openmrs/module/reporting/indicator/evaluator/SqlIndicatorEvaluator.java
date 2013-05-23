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
package org.openmrs.module.reporting.indicator.evaluator;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.IllegalDatabaseAccessException;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterException;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.SimpleIndicatorResult;
import org.openmrs.module.reporting.indicator.SqlIndicator;
import org.openmrs.module.reporting.report.util.SqlUtils;
import org.openmrs.util.DatabaseUpdater;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *  The evaluator that evaluates {@link SqlIndicator}.  Returns a {@link SimpleIndicatorResult}.
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Handler(supports={SqlIndicator.class})
public class SqlIndicatorEvaluator implements IndicatorEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());	
	
	@Autowired
	private SessionFactory sessionFactory;
	

	 public SimpleIndicatorResult evaluate(Indicator indicator, EvaluationContext context) throws EvaluationException {

		 SqlIndicator sqlIndicator = (SqlIndicator) indicator;

		 SimpleIndicatorResult result = new SimpleIndicatorResult();
		 result.setIndicator(indicator);
		 result.setContext(context);
		 String sql = sqlIndicator.getSql();

		 //TODO:  EVERYTHING FROM HERE DOWN IS A HACK PENDING REPORT-380
		 //this validates, prepares, and runs the query, and set the numerator and denominator on the result object
		 validateQuery(sql, context.getParameterValues());
		 executeSql(sql, context.getParameterValues(), result, "numerator");
		 if (sqlIndicator.getDenominatorSql() != null)
			 executeSql(new String(sqlIndicator.getDenominatorSql()), context.getParameterValues(), result, "denominator");	 
		 return result;
	 }
	 

	 
	 /**
	  *   EVERYTHING BELOW CAN BE REPLACED, PENDING REPORT-380
	  * 
	  */
	 
	 private void executeSql(String sql, Map<String, Object> paramMap, SimpleIndicatorResult result, String resultType) throws EvaluationException {
			Connection connection = null;
			try {
				connection = sessionFactory.getCurrentSession().connection();
				ResultSet resultSet = null;
				
				PreparedStatement statement = SqlUtils.prepareStatement(connection, sql.toString(), paramMap);
				boolean queryResult = statement.execute();

				if (!queryResult) {
					throw new EvaluationException("Unable to evaluate sql query");
				}
				resultSet = statement.getResultSet();
				
				int numRows = 1;
				while (resultSet.next()) {
					if (numRows > 1)
						throw new RuntimeException("The query that you're using in your indicator should only return 1 row.");
					if (resultType.equals("numerator")){
						if (resultSet.getObject(1) == null)
							continue;						
						else if (Math.rint(resultSet.getDouble(1)) == resultSet.getDouble(1))  //if not decimal
							result.setNumeratorResult(resultSet.getInt(1));
						else
							result.setNumeratorResult(BigDecimal.valueOf(resultSet.getDouble(1)));
					}
					else if (resultType.equals("denominator") && !resultSet.wasNull())
						result.setDenominatorResult(resultSet.getInt(1));
					numRows ++;
					try {
						resultSet.getObject(2);
						throw new RuntimeException("The query that you're using in your indicator should only return 1 column.");
					} catch (SQLException ex){
						//pass
					}
				}
			}
			catch (IllegalDatabaseAccessException ie) {
				throw ie;
			}
			catch (Exception e) {
				throw new EvaluationException("Unable to evaluate sql query", e);
			}
	 }
	 
	 /**
	  * TODO:  SHOULD BE REPLACED BY REPORT-380
	  */
	 private void validateQuery(String sql, Map<String, Object> paramMap){
		 if (sql == null || sql.equals("")) 
				throw new ReportingException("SQL query string is required");
			if (!SqlUtils.isSelectQuery(sql)) {
				throw new IllegalDatabaseAccessException();
			}
	    	List<Parameter> parameters = getNamedParameters(sql);    	
	    	for (Parameter parameter : parameters) { 
	    		Object parameterValue = paramMap.get(parameter.getName());
	    		if (parameterValue == null) 
	    			throw new ParameterException("Must specify a value for the parameter [" +  parameter.getName() + "]");    		
	    	}	
	 }
	 
	 /**
	  * TODO:  SHOULD BE REPLACED BY REPORT-380
	  */
	 private List<Parameter> getNamedParameters(String sqlQuery) {
		List<Parameter> parameters = new ArrayList<Parameter>();

		// TODO Need to move regex code into a utility method 
		Pattern pattern = Pattern.compile("\\:\\w+\\b");
		Matcher matcher = pattern.matcher(sqlQuery);

		while (matcher.find()) {			
			// Index is 1 because we need to strip off the colon (":")
			String parameterName = matcher.group().substring(1);			
			Parameter parameter = new Parameter();			
			parameter.setName(parameterName);
			parameter.setLabel(parameterName);
			if (parameterName.toLowerCase().contains("date")) {
				parameter.setType(Date.class);
			}
			else {
				parameter.setType(String.class);
			}
			parameters.add(parameter);
		}		
		return parameters;
	 }
	
}
