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
package org.openmrs.module.evaluation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.evaluation.parameter.ParameterException;

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
	 * @param s - the String to check
	 * @return - true if the passed String is an expression that is capable of being evaluated
	 */
	public static boolean isExpression(String s) {
		return s != null && s.startsWith(EXPRESSION_START) && s.endsWith(EXPRESSION_END);
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
	public static Object evaluateExpression(String expression, Map<String, Object> parameterValues, 
											Class<?> clazz) throws ParameterException {
		
		log.info("evaluateExpression(): " + expression + " " + parameterValues);
		
		if (expression == null) {
			log.warn("evaluateExpression returning null.");
			return null;
		}
		
		List<Object> elements = new ArrayList<Object>();
		StringBuilder curr = new StringBuilder();
		
		// Iterate over the expression and create a List of elements
		char[] chars = expression.toCharArray();
		for (int i=0; i<chars.length; i++) {
			char c = chars[i];
			boolean isStartOfExpr = (c == '$' && chars.length > (i+1) && chars[i+1] == '{');
			if (isStartOfExpr || c == '}') {
				if (curr.length() > 0) {
					if (c == '}') {
						elements.add(evaluateParameterExpression(curr.toString(), parameterValues, clazz));
					}
					else {
						elements.add(curr.toString());
					}
					curr = new StringBuilder();
				}
				if (isStartOfExpr) {
					i++;
				}
			}
			else {
				curr.append(c);
			}
		}
		if (curr.length() > 0) {
			elements.add(curr.toString());
		}
		
		// If only one element was evaluated, return that element
		if (elements.size() == 1) {
			return elements.get(0);
		}
		// Otherwise, return the String concatenation of all elements
		else {
			if (clazz != null && clazz != String.class) {
				throw new ParameterException("Unable to evaluate " + expression + " to a " + clazz);
			}
			StringBuilder sb = new StringBuilder();
			for (Object o : elements) {
				sb.append(o.toString());
			}
			return sb.toString();
		}
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
	public static Object evaluateParameterExpression(String expression, Map<String, Object> parameterValues,
													 Class<?> clazz) throws ParameterException {
		
		log.info("evaluateParameterExpression(): " + expression + " " + parameterValues);

		log.debug("Starting expression: " + expression);
		String[] paramAndFormat = expression.split(FORMAT_SEPARATOR, 2);
		Object paramValueToFormat = null;
		
		// First try to handle Date operations
		try {
			Matcher matcher = DATE_OPERATION_PATTERN.matcher(paramAndFormat[0]);
			while (matcher.find()) {
				
				log.debug("Found date expression of: " + matcher.group(0));
				String parameterName = matcher.group(1);
				Object paramVal = parameterValues.get(parameterName);
				
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
			paramValueToFormat = parameterValues.get(paramAndFormat[0]);
			if (paramValueToFormat == null) {
				throw new ParameterException("Unable to find matching parameter value (" + paramValueToFormat + ") for expression " + paramAndFormat[0]);				
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
