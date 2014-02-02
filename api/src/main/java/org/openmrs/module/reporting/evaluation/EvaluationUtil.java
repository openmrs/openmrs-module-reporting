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

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.evaluation.caching.CachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.NoCachingStrategy;
import org.openmrs.module.reporting.evaluation.parameter.ParameterException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides utility methods useful for Evaluation
 */
public class EvaluationUtil {
	
	private static Log log = LogFactory.getLog(EvaluationUtil.class);
	
	public static final String EXPRESSION_START = "${";
	public static final String EXPRESSION_END = "}";
	public static final String FORMAT_SEPARATOR = "\\|";

    /*
     * ([a-zA-Z_0-9.]+)                      ... (group 1) word made of letters, _, or dot, e.g. "report.start_date"
     *
     * ((?:\s*[+-/*]\s*\d*\.?\d+[a-zA-Z]*+)+) ... (group 2)
     *  (?:                               )+    ... means this occurs at least once, but isn't counted as a group
     *     \s*[+-/*]\s*                         ... optional whitespace, operator [+-/*], optional whitespace
     *                 \d*\.?\d+                ... captures either #.# or #
     *                          [a-zA-Z]*+      ... optional unit (*+ means possessive, zero or more times)
     */
    private static Pattern expressionPattern = Pattern.compile("([a-zA-Z_0-9.]+)((?:\\s*[+-/*]\\s*\\d*\\.?\\d+[a-zA-Z]*+)+)");

    /*
     * ([+-/*])                           ... (group 1) single-character operator
     *         \s*                        ... optional whitespace
     *            (\d*\.?\d+)             ... (group 2) captures either #.# or #
     *                       ([a-zA-Z]*+) ... (group 3) optional unit (*+ means possessive, zero or more times)
     */
    private static Pattern operationPattern = Pattern.compile("([+-/*])\\s*(\\d*\\.?\\d+)([a-zA-Z]*+)");

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
		Map<String, Object> params = new HashMap<String, Object>(context.getParameterValues());
		params.putAll(context.getContextValues());
		return evaluateExpression(expression, params);
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
	 * @throws org.openmrs.module.reporting.evaluation.parameter.ParameterException
	 */
	public static Object evaluateParameterExpression(String expression, Map<String, Object> parameters) throws ParameterException {
		
		log.debug("evaluateParameterExpression(): " + expression);

		log.debug("Starting expression: " + expression);
		String[] paramAndFormat = expression.split(FORMAT_SEPARATOR, 2);
		Object paramValueToFormat = null;

        try {
            Matcher matcher = expressionPattern.matcher(paramAndFormat[0]);
            if (matcher.matches()) {
                String parameterName = matcher.group(1);
                paramValueToFormat = parameters.get(parameterName);
                if (paramValueToFormat == null) {
                    log.debug("Looked like an expression but the parameter value is null");
                } else {
                    String operations = matcher.group(2);
                    Matcher opMatcher = operationPattern.matcher(operations);
                    while (opMatcher.find()) {
                        String op = opMatcher.group(1);
                        String number = opMatcher.group(2);
                        String unit = opMatcher.group(3).toLowerCase();
                        if (paramValueToFormat instanceof Date) {
                            if (!op.matches("[+-]")) {
                                throw new IllegalArgumentException("Dates only support the + and - operators");
                            }
                            Integer numAsInt;
                            try {
                                numAsInt = Integer.parseInt(number);
                            } catch (NumberFormatException ex) {
                                throw new IllegalArgumentException("Dates do not support arithmetic with floating-point values");
                            }

                            if ("-".equals(op)) {
                                numAsInt = -numAsInt;
                            }
                            if ("w".equals(unit)) {
                                unit = "d";
                                numAsInt *= 7;
                            }
                            if ("ms".equals(unit)) {
                                paramValueToFormat = DateUtils.addMilliseconds((Date) paramValueToFormat, numAsInt);
                            } else if ("s".equals(unit)) {
                                paramValueToFormat = DateUtils.addSeconds((Date) paramValueToFormat, numAsInt);
                            } else if ("h".equals(unit)) {
                                paramValueToFormat = DateUtils.addHours((Date) paramValueToFormat, numAsInt);
                            } else if ("m".equals(unit)) {
                                paramValueToFormat = DateUtils.addMonths((Date) paramValueToFormat, numAsInt);
                            } else if ("y".equals(unit)) {
                                paramValueToFormat = DateUtils.addYears((Date) paramValueToFormat, numAsInt);
                            } else if ("".equals(unit) || "d".equals(unit)) {
                                paramValueToFormat = DateUtils.addDays((Date) paramValueToFormat, numAsInt);
                            } else {
                                throw new IllegalArgumentException("Unknown unit: " + unit);
                            }
                        }
                        else { // assume it's a number
                            if (!"".equals(unit)) {
                                throw new IllegalArgumentException("Can't specify units in a non-date expression");
                            }
                            if (paramValueToFormat instanceof Integer && number.matches("\\d+")) {
                                Integer parsed = Integer.parseInt(number);
                                if ("+".equals(op)) {
                                    paramValueToFormat = ((Integer) paramValueToFormat) + parsed;
                                } else if ("-".equals(op)) {
                                    paramValueToFormat = ((Integer) paramValueToFormat) - parsed;
                                } else if ("*".equals(op)) {
                                    paramValueToFormat = ((Integer) paramValueToFormat) * parsed;
                                } else if ("/".equals(op)) {
                                    paramValueToFormat = ((Integer) paramValueToFormat) / parsed;
                                } else {
                                    throw new IllegalArgumentException("Unknown operator " + op);
                                }
                            } else {
                                // since one or both are decimal values, do double arithmetic
                                Double parsed = Double.parseDouble(number);
                                if ("+".equals(op)) {
                                    paramValueToFormat = ((Number) paramValueToFormat).doubleValue() + parsed;
                                } else if ("-".equals(op)) {
                                    paramValueToFormat = ((Number) paramValueToFormat).doubleValue() - parsed;
                                } else if ("*".equals(op)) {
                                    paramValueToFormat = ((Number) paramValueToFormat).doubleValue() * parsed;
                                } else if ("/".equals(op)) {
                                    paramValueToFormat = ((Number) paramValueToFormat).doubleValue() / parsed;
                                } else {
                                    throw new IllegalArgumentException("Unknown operator " + op);
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            log.debug(e.getMessage());
            throw new ParameterException("Error handling expression: " + paramAndFormat[0], e);
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
			paramValueToFormat = ObjectUtil.format(paramValueToFormat, paramAndFormat[1]);
		}

		return paramValueToFormat;
	}
	
	/**
	 * @return the Cache key for the given definition class
	 */
	public static String getCacheKey(Definition definition, EvaluationContext context) {
		String cacheKey = null;
		Caching caching = definition.getClass().getAnnotation(Caching.class);
		if (caching != null && caching.strategy() != NoCachingStrategy.class) {
			try {
				CachingStrategy strategy = caching.strategy().newInstance();
				cacheKey = strategy.getCacheKey(definition, context);
			}
			catch (Exception e) {
				log.warn("An error occurred while attempting to access the cache.", e);
			}
		}
		return cacheKey;
	}
}
