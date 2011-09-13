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
package org.openmrs.module.reporting.evaluation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.evaluation.caching.CachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.NoCachingStrategy;
import org.openmrs.module.reporting.evaluation.parameter.ParameterException;

/**
 * Provides utility methods useful for Evaluation
 */
public class EvaluationUtil {
	
	private static Log log = LogFactory.getLog(EvaluationUtil.class);
	
	public static final String EXPRESSION_START = "${";
	public static final String EXPRESSION_END = "}";
	public static final String FORMAT_SEPARATOR = "\\|";
	
	/**
	 * Returns true if the passed String is an expression that is capable of being evaluated
	 * @param s the String to check
	 * @return true if the passed String is an expression that is capable of being evaluated
	 */
	public static boolean isExpression(String s) {
		return s != null && s.startsWith(EXPRESSION_START) && s.endsWith(EXPRESSION_END);
	}
	
	/**
	 * Returns the passed String, removing the expression start and end delimiters
	 * @param s the original string
	 * @return the passed String, removing the expression start and end delimiters
	 */
	public static String stripExpression(String s) {
		if (isExpression(s)) {
			s = s.substring(EXPRESSION_START.length(), s.length()-EXPRESSION_END.length());
		}
		return s;
	}
	
	/**
	 * @see EvaluationUtil#evaluateExpression(String, Map<String, Object>, Class)
	 */
	public static Object evaluateExpression(String expression, EvaluationContext context) throws ParameterException {
		return evaluateExpression(expression, context.getParameterValues());
	}
	
	/**
	 * This method will parse the passed expression and return a value based on the following
	 * criteria:<br/>
	 * <ul>
	 * <li>Any string that matches a passed parameter will be replaced by the value of that parameter
	 * <li>If this date is followed by an expression, it will attempt to evaluate this by
	 * incrementing/decrementing days/weeks/months/years as specified</li>
	 * <li>Examples: Given 2 parameters:
	 * <ul>
	 * <li>report.startDate = java.util.Date with value of [2007-01-10]
	 * <li>report.gender = "male"
	 * </ul>
	 * The following should result:<br/>
	 * <br/>
	 * <pre>
	 * evaluateExpression("${report.startDate}") -> "2007-01-10" as Date
	 * evaluateExpression("${report.startDate+5d}") -> "2007-01-15" as Date
	 * evaluateExpression("${report.startDate-1w}") -> "2007-01-03" as Date
	 * evaluateExpression("${report.startDate+3m}") -> "2007-04-15" as Date
	 * evaluateExpression("${report.startDate+1y}") -> "2008-01-10" as Date
	 * <pre>
	 * </ul>
	 * 
	 * @param expression
	 * @return value for given expression, as an <code>Object</code>
	 * @throws ParameterException
	 */
	public static Object evaluateExpression(String expression, Map<String, Object> parameters) throws ParameterException {
		return evaluateExpression(expression, parameters, EXPRESSION_START, EXPRESSION_END);
	}

	/**
	 */
	public static Object evaluateExpression(String expression, Map<String, Object> parameters, 
						 					String expressionPrefix, String expressionSuffix) throws ParameterException {

		while (expression != null) {
			String newExpression = expression;
			
			int startIndex = expression.indexOf(expressionPrefix);
			int endIndex = expression.indexOf(expressionSuffix, startIndex+1);
			StringBuilder sb = new StringBuilder();
			if (startIndex != -1 && endIndex != -1) {
				
				String e = expression.substring(startIndex + expressionPrefix.length(), endIndex);
				Object replacement = evaluateParameterExpression(e, parameters);
				
				if (startIndex == 0 && endIndex == expression.length()-1) {
					return replacement;
				}
				
				sb.append(expression.substring(0, startIndex));
				sb.append(ObjectUtil.format(replacement));
				sb.append(expression.substring(endIndex + expressionSuffix.length()));
				newExpression = sb.toString();
			}
			
			if (newExpression.equals(expression)) {
				return newExpression;
			}
			expression = newExpression;
		}
		return null;
	}
		
	/**
	 * This method will parse the passed expression and return a value based on the following
	 * criteria:<br/>
	 * <ul>
	 * <li>Any string that matches a passed parameter will be replaced by the value of that parameter
	 * <li>If this date is followed by an expression, it will attempt to evaluate this by
	 * incrementing/decrementing days/weeks/months/years as specified</li>
	 * <li>Examples: Given 2 parameters:
	 * <ul>
	 * <li>report.startDate = java.util.Date with value of [2007-01-10]
	 * <li>report.gender = "male"
	 * </ul>
	 * The following should result:<br/>
	 * <br/>
	 * <pre>
	 * evaluateParameterExpression("report.startDate") -> "2007-01-10" as Date
	 * <pre>
	 * </ul>
	 * 
	 * @param expression
	 * @return value for given expression, as an <code>Object</code>
	 * @throws ParameterException
	 */
	public static Object evaluateParameterExpression(String expression, Map<String, Object> parameters) throws ParameterException {
		
		log.debug("evaluateParameterExpression(): " + expression);

		log.debug("Starting expression: " + expression);
		String[] paramAndFormat = expression.split(FORMAT_SEPARATOR, 2);
		Object paramValueToFormat = null;
		
		// First try to handle Date operations
		try {
			String dateStr = paramAndFormat[0];
			String[] split = dateStr.split("[+-]");
			if (split.length > 1) {
				String parameterName = split[0].trim();
				Object paramVal = parameters.get(parameterName);
	
				if (paramVal == null || !(paramVal instanceof Date)) {
					log.warn("Expression appears to be a Date operation expression, but the parameter value is actually " + paramVal == null ? "null" : paramVal.getClass());
				}
				else {
					Calendar cal = Calendar.getInstance();
					cal.setTime((Date)paramVal);
					int runningLength = split[0].length();
					for (int i=1; i<split.length; i++) {
						int multiplier = dateStr.charAt(runningLength) == '-' ? -1 : 1;
						int num = multiplier * Integer.parseInt(split[i].substring(0, split[i].length()-1));
						String fld = split[i].substring(split[i].length()-1, split[i].length()).toLowerCase();
						num *= "w".equals(fld) ? 7 : 1;
						int field = "h".equals(fld) ? Calendar.HOUR : "m".equals(fld) ? Calendar.MONTH : "y".equals(fld) ? Calendar.YEAR : Calendar.DATE;
						cal.add(field, num);
						runningLength += split[i].length() + 1;
					}
					paramValueToFormat = cal.getTime();
					log.debug("Calculated date of: " + paramValueToFormat);
				}
			}
		}
		catch (Exception e) {
			log.debug(e.getMessage());
			throw new ParameterException("Error parsing dates in expression: " + paramAndFormat[0]);
		}
		
		paramValueToFormat = ObjectUtil.nvl(paramValueToFormat, parameters.get(paramAndFormat[0]));
		if (ObjectUtil.isNull(paramValueToFormat)) {
			if (parameters.containsKey(paramAndFormat[0])) {
				return paramValueToFormat;
			}
			else {
				return expression;
			}
		}
		log.debug("Evaluated to: " + paramValueToFormat);

		// Attempt to format the evaluated value if appropriate
		if (paramAndFormat.length == 2) {
			if (paramValueToFormat instanceof Date) {
				DateFormat df = new SimpleDateFormat(paramAndFormat[1]);
				return df.format((Date)paramValueToFormat);
			}
			else {
				log.debug("Attempting to format by calling method: " + paramAndFormat[1]);
				try {
					return paramValueToFormat.getClass().getMethod(paramAndFormat[1]).invoke(paramValueToFormat);
				}
				catch (Exception e) {
					log.debug(e.getMessage()); // Don't throw an error here...
				}
			}
		}

		return paramValueToFormat;
	}
	
	/**
	 * @return the Cache key for the given definition class
	 */
	public static String getCacheKey(Definition definition) {
		String cacheKey = null;
		Caching caching = definition.getClass().getAnnotation(Caching.class);
		if (caching != null && caching.strategy() != NoCachingStrategy.class) {
			try {
				CachingStrategy strategy = caching.strategy().newInstance();
				cacheKey = strategy.getCacheKey(definition);
			}
			catch (Exception e) {
				log.warn("An error occurred while attempting to access the cache.", e);
			}
		}
		return cacheKey;
	}
}
