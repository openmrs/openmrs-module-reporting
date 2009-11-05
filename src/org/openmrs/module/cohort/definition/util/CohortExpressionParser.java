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
package org.openmrs.module.cohort.definition.util;

import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.api.PatientSetService;
import org.openmrs.api.PatientSetService.BooleanOperator;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.cohort.definition.CompoundCohortDefinition;
import org.openmrs.module.cohort.definition.InverseCohortDefinition;
import org.openmrs.module.cohort.definition.history.CohortDefinitionHistory;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Mapped;

/**
 * This class encapsulates the parsing logic necessary to take a String
 * expression and parse it into a List<Object>.  The parser
 */
public class CohortExpressionParser {
	
	protected static final Log log = LogFactory.getLog(CohortExpressionParser.class);
	
	private static List<String> andWords = Arrays.asList("and","intersection","*");
	private static List<String> orWords = Arrays.asList("or","union","+");
	private static List<String> notWords = Arrays.asList("not","!");
	private static List<Character> openParenthesesWords = Arrays.asList('(','[','{');
	private static List<Character> closeParenthesesWords = Arrays.asList(')',']','}');
	private static List<Character> characterWords = Arrays.asList('+','!','(','[','{',')',']','}');
	
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
	 * Recursively traverse the List<Object> phrase to produce a (possibly nested) CompoundCohortDefinition
	 * If another List<Object> is found in the list, recursively evaluate it in place
	 * If anything in this list is a number, replace it with the relevant filter from the history
	 * @param phrase
	 * @param history
	 * @param context
	 * @return CohortDefinition
	 */
	@SuppressWarnings("unchecked")
	public static CohortDefinition evaluate(List<Object> phrase, CohortDefinitionHistory history) {
		log.debug("Starting with " + phrase);
		List<Object> use = new ArrayList<Object>();
		for (Object o : phrase) {
			if (o instanceof List) {
				use.add(evaluate((List<Object>) o, history));
			}
			else if (o instanceof Integer) {
				use.add(history.getSearchHistory().get((Integer) o - 1));
			}
			else {
				use.add(o);
			}
		}
		
		// base case. All elements are CohortDefinition or BooleanOperator.
		log.debug("Base case with " + use);
		
		// first, replace all [..., NOT, CohortDefinition, ...] with [ ..., InvertedCohortDefinition, ...]
		boolean invertTheNext = false;
		for (ListIterator<Object> i = use.listIterator(); i.hasNext();) {
			Object o = i.next();
			if (o instanceof BooleanOperator) {
				if ((BooleanOperator) o == BooleanOperator.NOT) {
					i.remove();
					invertTheNext = !invertTheNext;
				} else {
					if (invertTheNext) 
						throw new RuntimeException("Can't have NOT AND. Test() should have failed");
				}
			} else {
				if (invertTheNext) {
					i.set(new InverseCohortDefinition((CohortDefinition) o));
					invertTheNext = false;
				}
			}
		}
		
		log.debug("Finished with NOTs: " + use);
		
		// Now all we have left are CohortDefinition, AND, OR
		// eventually go with left-to-right precedence, and we can combine runs of the same operator into a single one
		//     1 AND 2 AND 3 -> AND(1, 2, 3)
		//     1 AND 2 OR 3 -> OR(AND(1, 2), 3)
		// for now a hack so we take the last operator in the run, and apply that to all filters
		//     for example 1 AND 2 OR 3 -> OR(1, 2, 3)
		if (use.size() == 1) {
			return (CohortDefinition) use.get(0);
		}
		
		BooleanOperator bo = BooleanOperator.AND;
		List<CohortDefinition> args = new ArrayList<CohortDefinition>();
		for (Object o : use) {
			if (o instanceof BooleanOperator) {
				bo = (BooleanOperator) o;
			}
			else {
				args.add((CohortDefinition) o);
			}
			if (args.size() == 2) {
				CohortDefinition pf = null; // TODO: fix this new CompoundCohortDefinition(bo, args); 
				args = new ArrayList<CohortDefinition>();
				args.add(pf);
			}
		}
		
		if (args.size() != 1) {
			throw new IllegalArgumentException("Unable to parse expression. Parsed " + phrase + " to " + args);
		}
		return (CohortDefinition)args.get(0);
	}
	
	
	/**
	 * Recursively traverse the List<Object> phrase to produce a (possibly nested) CompoundCohortDefinition
	 * If another List<Object> is found in the list, recursively evaluate it in place
	 * If anything in this list is a key into searches, replace it with the relevant filter from searches
	 * @param phrase
	 * @param searches
	 * @param context
	 * @return Cohort
	 */
	@SuppressWarnings("unchecked")
	public static Cohort evaluate(List<Object> phrase, CompositionCohortDefinition composition, EvaluationContext context) {
		log.debug("Starting with " + phrase);
		List<Object> use = new ArrayList<Object>();
		for (Object o : phrase) {
			if (o instanceof List) {
				use.add(evaluate((List<Object>) o, composition, context));
			}
			else if (o instanceof String) {
				use.add(composition.getSearches().get((String) o));
			}
			else if (o instanceof Integer) { 
				use.add(composition.getSearches().get(o.toString()));
			}
			else {
				use.add(o);
			}
		}
		
		// base case. All elements are CohortDefinition or BooleanOperator.
		log.debug("Base case with " + use);
		
		// first, replace all [..., NOT, CohortDefinition, ...] with [ ..., InvertedCohortDefinition, ...]
		boolean invertTheNext = false;
		for (ListIterator<Object> i = use.listIterator(); i.hasNext();) {
			Object o = i.next();
			if (o instanceof BooleanOperator) {
				if ((BooleanOperator) o == BooleanOperator.NOT) {
					i.remove();
					invertTheNext = !invertTheNext;
				} else {
					if (invertTheNext) 
						throw new RuntimeException("Can't have NOT AND. Test() should have failed");
				}
			} else {
				if (invertTheNext) {
					i.set(InverseCohortDefinition.invert((Mapped<CohortDefinition>) o));
					invertTheNext = false;
				}
			}
		}
		
		log.debug("Finished with NOTs: " + use);
		
		// Now all we have left are CohortDefinition, AND, OR
		// eventually go with left-to-right precedence, and we can combine runs of the same operator into a single one
		//     1 AND 2 AND 3 -> AND(1, 2, 3)
		//     1 AND 2 OR 3 -> OR(AND(1, 2), 3)
		// for now a hack so we take the last operator in the run, and apply that to all filters
		//     for example 1 AND 2 OR 3 -> OR(1, 2, 3)
		if (use.size() == 1) {
			return Context.getService(CohortDefinitionService.class).evaluate((Mapped<CohortDefinition>) use.get(0), context);
		}
		
		BooleanOperator bo = BooleanOperator.AND;
		List<Cohort> args = new ArrayList<Cohort>();
		for (Object o : use) {
			if (o instanceof BooleanOperator) {
				bo = (BooleanOperator) o;
			}
			else if (o instanceof Cohort) {
				// straight pass-through
				args.add((Cohort) o);
			} else {
				args.add(Context.getService(CohortDefinitionService.class).evaluate((Mapped<CohortDefinition>) o, context));
			}
		}
		
		Cohort ret = null;
		for (Cohort cohort : args) {
			if (ret == null) {
				ret = cohort;
			} else if (bo == BooleanOperator.AND) {
				ret = Cohort.intersect(ret, cohort);
			} else {
				ret = Cohort.union(ret, cohort);
			}
		}
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
	
	/**
	 * @return Whether this search requires a history against which to evaluate it
	 */
	@SuppressWarnings("unchecked")
	protected static boolean requiresHistory(List<Object> list) {
		for (Object o : list) {
			if (o instanceof Integer) {
				return true;
			}
			else if (o instanceof List) {
				if (requiresHistory((List) o)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Object> preprocessTokens(List<Object> tokens, CohortDefinitionHistory history) {

		if (history == null && requiresHistory(tokens)) {
			throw new IllegalArgumentException("You can't evaluate this search without a history");
		}
		
		List<Object> ret = new ArrayList<Object>();
		
		for (Object o : tokens) {
			if (!supports(o.getClass())) {
				throw new RuntimeException("Unable to handle: " + o.getClass());
			} 
			if (o instanceof List) {
				ret.add(preprocessTokens((List) o, history));
			}
			else if (o instanceof Integer) {
				ret.add(history.getSearchHistory().get((Integer) o - 1));
			}
			else {
				ret.add(o);
			}
		}
		return ret;
	}
	
	/**
	 * TODO:  THIS WAS TAKEN FROM COHORTUTIL.  NEED TO SEE HOW IT FITS WITH REST OF CODE AND WHETHER
	 * IT CAN BE CONSOLODATED, AND THE APPROACH WE WANT TO TAKE
	 * I CHANGED THIS TO USE PATIENT FILTERS INSTEAD OF PATIENT SEARCHES.
	 * I ALSO CHANGED IT SUCH THAT YOU CAN EITHER TRY TO LOAD A FILTER BY NAME, OR BY CLASS.  ONLY
	 * FILTERS LOADED BY CLASS CAN HAVE PARAMETERS SET ON THEM.  ONCE AN INSTANTIATED FILTER EXISTS
	 * THE WAY TO CHANGE PARAMETER VALUES AT RUNTIME IS TO USE AN EVALUATION CONTEXT, SO WE ARE NOT
	 * MODIFYING THE UNDERLYING PARAMETER VALUES OF THE CohortDefinition CLASS.
	 * 
	 * Parses an input string like: [Male] and [Adult] and
	 * [EnrolledInHivOnDate|program="1"|untilDate="${report.startDate}"] Names between brackets are
	 * treated as saved PatientSearch objects with that name. Parameter values for those loaded
	 * searches are specified after a | The following are handled like they would be in a cohort
	 * builder composition search: ( ) and or not
	 * 
	 * @param spec
	 * @return A CohortDefinition (currently always a PatientSearch) parsed from the spec string.
	 */
	public static CohortDefinition parse(String spec) {
		List<Object> tokens = new ArrayList<Object>();
		{
			StringBuilder thisElement = null;
			for (int i = 0; i < spec.length(); ++i) {
				char c = spec.charAt(i);
				switch (c) {
					case '(':
					case ')':
						if (thisElement != null) {
							tokens.add(thisElement.toString().trim());
							thisElement = null;
						}
						tokens.add("" + c);
						break;
					case ' ':
					case '\t':
					case '\n':
						if (thisElement != null)
							thisElement.append(c);
						break;
					case '[':
						if (thisElement != null)
							tokens.add(thisElement.toString().trim());
						thisElement = new StringBuilder();
						thisElement.append(c);
						break;
					default:
						if (thisElement == null)
							thisElement = new StringBuilder();
						thisElement.append(c);
						if (c == ']') {
							tokens.add(thisElement.toString().trim());
							thisElement = null;
						}
						break;
				}
			}
			if (thisElement != null)
				tokens.add(thisElement.toString().trim());
		}
		for (ListIterator<Object> i = tokens.listIterator(); i.hasNext();) {
			Object o = i.next();
			if (o instanceof String) {
				String s = (String) o;
				if (s.startsWith("[") && s.endsWith("]")) {
					s = s.substring(1, s.length() - 1);
					String name = null;
					Map<String, String> paramValues = new HashMap<String, String>();
					StringTokenizer st = new StringTokenizer(s, "|");
					while (st.hasMoreTokens()) {
						String t = st.nextToken();
						if (name == null) {
							name = t;
						} else {
							int ind = t.indexOf('=');
							if (ind < 0)
								throw new IllegalArgumentException("The fragment '" + t + "' in " + s + " has no =");
							paramValues.put(t.substring(0, ind), t.substring(ind + 1));
						}
					}
					if (name == null) {
						throw new IllegalArgumentException("Could not find a filter name in " + s);
					}
					/* TODO: The commented section below needs fixing...
					CohortDefinition filter = null; // TODO: Fix this...Context.getReportService().getCohortDefinitionByName(name);
					if (filter == null) {
						try {
							Class<?> type = Context.loadClass(name);
							filter = (CohortDefinition) type.newInstance();
							for (Map.Entry<String, String> e : paramValues.entrySet()) {
								boolean isExpression = EvaluationUtil.isExpression(e.getValue());
								// TODO: Fix this: filter.addParameter(new Parameter());
							}
						}
						catch (Exception e) {
							throw new IllegalArgumentException("Unable to load filter named: " + name);
						}
					}
					*/
				}
			}
		}
		
		List<Object> tokenList = CohortExpressionParser.parseIntoTokens(tokens);
		return CohortExpressionParser.evaluate(tokenList, null);
	}
}
