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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.evaluation.parameter.ParameterException;

/**
 * Provides utility methods useful for Evaluation
 */
public class EvaluationUtil {
	
	private static Log log = LogFactory.getLog(EvaluationUtil.class);
	
	public static final String EXPRESSION_START = "${";
	public static final String EXPRESSION_END = "}";
	public static final String FORMAT_SEPARATOR = "\\|";
	public static final Pattern DATE_OPERATION_PATTERN = Pattern.compile("([\\w\\W]*)([+-])(\\d{1,})([dwmy])");
	
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
						 					String expressionPrefix, String expressionPostfix) throws ParameterException {

		while (expression != null) {
			String newExpression = expression;
			
			int startIndex = expression.indexOf(expressionPrefix);
			int endIndex = expression.indexOf(expressionPostfix, startIndex+1);
			StringBuilder sb = new StringBuilder();
			if (startIndex != -1 && endIndex != -1) {
				
				String e = expression.substring(startIndex + expressionPrefix.length(), endIndex);
				Object replacement = evaluateParameterExpression(e, parameters);
				
				if (startIndex == 0 && endIndex == expression.length()-1) {
					return replacement;
				}
				
				sb.append(expression.substring(0, startIndex));
				sb.append(replacement.toString());
				sb.append(expression.substring(endIndex + expressionPostfix.length()));
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
		
		log.info("evaluateParameterExpression(): " + expression);

		log.debug("Starting expression: " + expression);
		String[] paramAndFormat = expression.split(FORMAT_SEPARATOR, 2);
		Object paramValueToFormat = null;
		
		// First try to handle Date operations
		try {
			Matcher matcher = DATE_OPERATION_PATTERN.matcher(paramAndFormat[0]);
			while (matcher.find()) {
				
				log.debug("Found date expression of: " + matcher.group(0));
				String parameterName = matcher.group(1);
				Object paramVal = parameters.get(parameterName);
				
				if (paramVal == null) {
					throw new ParameterException("Unable to find matching parameter value (" + paramVal + ") for parameter " + parameterName);
				} else if (!(paramVal instanceof Date)) { 
					throw new ParameterException("Invalid class for parameter value " + paramVal + ", expected: " + Date.class + ", actual: " + paramVal.getClass().getName());
				}
	
				int num = ("-".equals(matcher.group(2)) ? -1 : 1) * Integer.parseInt(matcher.group(3));
				String fld = matcher.group(4).toLowerCase();
				num = "w".equals(fld) ? num * 7 : num;
				int field = "m".equals(fld) ? Calendar.MONTH : "y".equals(fld) ? Calendar.YEAR : Calendar.DATE;
					
				Calendar cal = Calendar.getInstance();
				cal.setTime((Date) paramVal);
				cal.add(field, num);
					
				paramValueToFormat = cal.getTime();
				log.debug("Calculated date of: " + paramValueToFormat);
			}
		}
		catch (Exception e) {
			log.debug(e.getMessage());
			throw new ParameterException("Error parsing dates in expression: " + paramAndFormat[0]);
		}
		
		// If this is not a Date operation, handle generally
		if (paramValueToFormat == null) {
			paramValueToFormat = parameters.get(paramAndFormat[0]);
			if (paramValueToFormat == null) {
				return expression;			
			}
			log.debug("Evaluated to: " + paramValueToFormat);
		}

		// Attempt to format the evaluated value if appropriate
		if (paramAndFormat.length == 2) {
			if (paramValueToFormat instanceof Date) {
				DateFormat df = new SimpleDateFormat(paramAndFormat[1]);
				return df.format((Date)paramValueToFormat);
			}
			else {
				log.debug("Attempting to format by calling method: " + paramAndFormat[1]);
				try {
					Object formattedValue = paramValueToFormat.getClass().getMethod(paramAndFormat[1]).invoke(paramValueToFormat);
					return formattedValue == null ? null : formattedValue.toString();
				}
				catch (Exception e) {
					log.debug(e.getMessage());
					throw new ParameterException("Error trying to call " + paramAndFormat[1] + 
												 " on class " + paramValueToFormat.getClass(), e);
				}
			}
		}

		return paramValueToFormat;
	}
}
