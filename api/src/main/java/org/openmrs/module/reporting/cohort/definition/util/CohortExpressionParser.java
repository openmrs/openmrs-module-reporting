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
package org.openmrs.module.reporting.cohort.definition.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.api.PatientSetService;
import org.openmrs.api.PatientSetService.BooleanOperator;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.Cohorts;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.MissingDependencyException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

/**
 * This class encapsulates the parsing logic necessary to take a String
 * expression and parse it into a List<Object>.  The parser
 */
public class CohortExpressionParser {
	
	protected static final Log log = LogFactory.getLog(CohortExpressionParser.class);
	
	private static final List<String> andWords = Arrays.asList("and","intersection","*");
	private static final List<String> orWords = Arrays.asList("or","union","+");
	private static final List<String> notWords = Arrays.asList("not","!");
	private static final List<Character> openParenthesesWords = Arrays.asList('(','[','{');
	private static final List<Character> closeParenthesesWords = Arrays.asList(')',']','}');
	private static final List<Character> characterWords = Arrays.asList('+','!','(','[','{',')',']','}');
	
	public static boolean supports(Class<?> type) {
		return getSupportedTypes().contains(type);
	}
	
	public static List<Class<?>> getSupportedTypes() {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		classes.add(Integer.class);
		classes.add(BooleanOperator.class);
		classes.add(CohortDefinition.class);
		classes.add(List.class);
		return classes;
	}
	
	/**
	 * @return the Cohort evaluated from the passed CompositionCohortDefinition and EvaluationContext
	 * @throws EvaluationException
	 */
	public static Cohort evaluate(CompositionCohortDefinition composition, EvaluationContext context) throws EvaluationException {
		List<Object> tokens = CohortExpressionParser.parseIntoTokens(composition.getCompositionString());
		return CohortExpressionParser.evaluate(tokens, composition, context);
	}
	
	/**
	 * Recursively traverse the List<Object> phrase to produce a (possibly nested) CompoundCohortDefinition
	 * If another List<Object> is found in the list, recursively evaluate it in place
	 * If anything in this list is a key into searches, replace it with the relevant filter from searches
	 * @throws EvaluationException 
	 */
	@SuppressWarnings("unchecked")
	public static Cohort evaluate(List<Object> tokens, CompositionCohortDefinition composition, EvaluationContext context) throws EvaluationException {
		
		log.debug("Evaluating: " + tokens + " for searches: " + composition.getSearches());
		List<Object> use = new ArrayList<Object>();
		for (Object o : tokens) {
			log.debug("Checking token: " + o);
			if (o instanceof List) {
				log.debug("This is a list, evaluate it as a group...");
				Cohort result = evaluate((List<Object>) o, composition, context);
				log.debug(o + " evaluated to: " + result.size());
				use.add(result);
			}
			else if (o instanceof String || o instanceof Integer) {
				log.debug("This refers to a Search, try to find it...");
				Mapped<CohortDefinition> cd = composition.getSearches().get(o.toString());
				if (cd == null || cd.getParameterizable() == null) {
					throw new MissingDependencyException(o.toString());
				}
				log.debug("Found search: " + cd);
				Cohort result;
				try {
					result = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
				} catch (Exception ex) {
					throw new EvaluationException(o.toString(), ex);
				}
				log.debug("This evaluated to: " + result.size());
				use.add(result);
			}
			else {
				log.debug("This refers to an operator: " + o);
				use.add(o);
			}
		}
		log.debug("Converted tokens to Cohorts and Operators: " + use);
		
		log.debug("Inverting all [..., NOT, Cohort, ...] combinations");
		boolean invertTheNext = false;
		for (ListIterator<Object> i = use.listIterator(); i.hasNext();) {
			Object o = i.next();
			log.debug("Looking at element: " + o);
			if (o instanceof BooleanOperator) {
				if ((BooleanOperator) o == BooleanOperator.NOT) {
					i.remove();
					invertTheNext = !invertTheNext;
					log.debug("This is a NOT, so removing it and invert the next = " + invertTheNext);
				} else {
					if (invertTheNext) {
						throw new RuntimeException("Invalid expression string, cannot have a NOT followed by an AND");
					}
				}
			} 
			else {
				if (invertTheNext) {
					log.debug("Need to invert this...");
					if (o instanceof Cohort) {
						Cohort baseCohort = context.getBaseCohort();
						Cohort currentCohort = (Cohort)o;
						if (baseCohort == null) {
							baseCohort = Cohorts.allPatients(context);
						}
						log.debug("Originally a Cohort of size " + currentCohort.size());
						log.debug("With base Cohort of size " + baseCohort.size());
						Cohort invertedCohort = Cohort.subtract(baseCohort, currentCohort);
						log.debug("Makes a new Cohort is of size " + baseCohort.size());
						i.set(invertedCohort);
					}
					else {
						throw new RuntimeException("There is no method implemented for inverting a " + o.getClass());
					}
					invertTheNext = false;
				}
			}
		}
		log.debug("NOT conversion complete.  Now have: " + use);
		
		log.debug("Iterating across all Cohorts and Operators...");
		Cohort ret = null;
		BooleanOperator operator = BooleanOperator.AND;
		for (Object o : use) {
			if (o instanceof BooleanOperator) {
				operator = (BooleanOperator)o;
				log.debug("New operator: " + operator);
			}
			else if (o instanceof Cohort) {
				Cohort c = (Cohort)o;
				log.debug("Found Cohort: " + c.getSize());
				if (ret == null) {
					ret = c;
					log.debug("Setting this as starting Cohort for return.");
				}
				else {
					if (operator == BooleanOperator.AND) {
						ret = Cohort.intersect(ret, c);
						log.debug("AND this in to get: " + ret.getSize());
					}
					else if (operator == BooleanOperator.OR) {
						ret = Cohort.union(ret, c);
						log.debug("OR this in to get: " + ret.getSize());
					}
					else {
						throw new RuntimeException("Unable to handle BooleanOperator: " + operator);
					}
				}
			}
			else {
				throw new RuntimeException("Can only handle Cohorts and Operators.  Unable to handle class: " + o.getClass());
			}
		}
		log.debug("Done.  Returning: " + (ret == null ? null : ret.getSize()));
		return ret;
	}
	
	
	/**
	 * Elements in this list can be: an Integer, indicating a 1-based index into a search history a
	 * BooleanOperator (AND, OR, NOT) a CohortDefinition a PatientSearch another List of the same form,
	 * which indicates a parenthetical expression
	 */
	public static List<Object> parseIntoTokens(String expression) {

		List<Object> tokens = new ArrayList<Object>();
		try {
			StreamTokenizer st = new StreamTokenizer(new StringReader(expression));
			for (Character c : characterWords) {
				st.ordinaryChar(c);
			}
			while (st.nextToken() != StreamTokenizer.TT_EOF) {
				if (st.ttype == StreamTokenizer.TT_NUMBER) {
					Integer thisInt = new Integer((int) st.nval);
					if (thisInt < 1) {
						log.error("number < 1");
						return null;
					}
					tokens.add(thisInt);
				} else if (openParenthesesWords.contains(Character.valueOf((char) st.ttype))) {
					tokens.add("(");
				} else if (closeParenthesesWords.contains(Character.valueOf((char) st.ttype))) {
					tokens.add(")");
				} else if (st.ttype == StreamTokenizer.TT_WORD) {
					tokens.add(st.sval);
				}
			}
			return parseIntoTokens(tokens);
		}
		catch (Exception ex) {
			log.error("Error in description string: " + expression, ex);
			return null;
		}
	}
	
	public static List<Object> parseIntoTokens(List<Object> tokens) {
		List<Object> currentLine = new ArrayList<Object>();
		try {
			Stack<List<Object>> stack = new Stack<List<Object>>();
			for (Object token : tokens) {
				if (token instanceof String) {
					String s = (String) token;
					String lower = s.toLowerCase();
					if (andWords.contains(lower)) {
						currentLine.add(PatientSetService.BooleanOperator.AND);
					} 
					else if (orWords.contains(lower)) {
						currentLine.add(PatientSetService.BooleanOperator.OR);
					} 
					else if (notWords.contains(lower)) {
						currentLine.add(PatientSetService.BooleanOperator.NOT);
					} 
					else {
						if (s.length() == 1) {
							char c = s.charAt(0);
							if (openParenthesesWords.contains(c)) {
								stack.push(currentLine);
								currentLine = new ArrayList<Object>();
							} 
							else if (closeParenthesesWords.contains(c)) {
								List<Object> l = stack.pop();
								l.add(currentLine);
								currentLine = l;
							}
							else {
								currentLine.add(s);
							}
						}
						else {
							currentLine.add(s);
						}
					}
				}
				else if (supports(token.getClass())) {
					currentLine.add(token);
				} 
				else {
					throw new IllegalArgumentException("Unknown class in token list: " + token.getClass());
				}
			}
		}
		catch (Exception ex) {
			log.error("Error in token list", ex);
			return null;
		}
		return currentLine;
	}

}
