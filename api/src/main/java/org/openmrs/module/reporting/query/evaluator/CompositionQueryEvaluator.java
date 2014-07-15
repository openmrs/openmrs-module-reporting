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
package org.openmrs.module.reporting.query.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.reporting.common.BooleanOperator;
import org.openmrs.module.reporting.definition.evaluator.DefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.MissingDependencyException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.CompositionQuery;
import org.openmrs.module.reporting.query.IdSet;
import org.openmrs.module.reporting.query.Query;
import org.openmrs.module.reporting.query.QueryUtil;

import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

/**
 * Evaluates a CompositionQuery and produces an IdSet
 */
public abstract class CompositionQueryEvaluator<Q extends Query<T>, T extends OpenmrsObject> implements DefinitionEvaluator<Q> {

	protected Log log = LogFactory.getLog(getClass());

	public static final List<String> AND_WORDS = Arrays.asList("and", "intersection", "*");
	public static final List<String> OR_WORDS = Arrays.asList("or","union","+");
	public static final List<String> NOT_WORDS = Arrays.asList("not","!");
	public static final List<Character> OPEN_PARENTHESES_WORDS = Arrays.asList('(','[','{');
	public static final List<Character> CLOSE_PARENTHESES_WORDS = Arrays.asList(')',']','}');
	public static final List<Character> CHARACTER_WORDS = Arrays.asList('+','!','(','[','{',')',']','}');
	public static final List<Class<?>> SUPPORTED_TYPES = Arrays.asList(Integer.class, BooleanOperator.class, Query.class, List.class);

	/**
	 * Default Constructor
	 */
	public CompositionQueryEvaluator() {}

	/**
	 * Implementation classes need to override this method to provide the necessary functionality to evaluate a Query
	 */
	protected abstract IdSet<T> evaluateQuery(Mapped<Q> query, EvaluationContext context) throws EvaluationException;

	/**
	 * Implementation classes need to override this method to return the necessary definition that can be evaluated to all ids of that type
	 */
	protected abstract Q getAllIdQuery();


    protected IdSet<T> evaluateToIdSet(Q compositionQuery, EvaluationContext context) throws EvaluationException {
		CompositionQuery<Q, T> composition = (CompositionQuery<Q, T>) compositionQuery;
		try {
			List<Object> tokens = parseIntoTokens(composition.getCompositionString());
			return evaluateTokens(tokens, composition, context);
		}
		catch (MissingDependencyException ex) {
			String name = composition.getName() != null ? composition.getName() : composition.getCompositionString();
			throw new EvaluationException("sub-query '" + ex.getPropertyThatFailed() + "' of composition '" + name + "'", ex);
		}
	}

	/**
	 * Recursively traverse the List<Object> phrase to produce a (possibly nested) CompositionQuery
	 * If another List<Object> is found in the list, recursively evaluate it in place
	 * If anything in this list is a key into searches, replace it with the relevant filter from searches
	 * @throws EvaluationException
	 */
	@SuppressWarnings("unchecked")
	protected IdSet<T> evaluateTokens(List<Object> tokens, CompositionQuery<Q, T> composition, EvaluationContext context) throws EvaluationException {

		log.debug("Evaluating: " + tokens + " for searches: " + composition.getSearches());
		List<Object> use = new ArrayList<Object>();
		for (Object o : tokens) {
			log.debug("Checking token: " + o);
			if (o instanceof List) {
				log.debug("This is a list, evaluate it as a group...");
				IdSet<T> result = evaluateTokens((List<Object>) o, composition, context);
				log.debug(o + " evaluated to: " + result.getSize());
				use.add(result);
			}
			else if (o instanceof String || o instanceof Integer) {
				log.debug("This refers to a Search, try to find it...");
				Mapped<Q> mappedQuery = composition.getSearches().get(o.toString());
				if (mappedQuery == null || mappedQuery.getParameterizable() == null) {
					throw new MissingDependencyException(o.toString());
				}
				log.debug("Found search: " + mappedQuery);
				IdSet<T> result;
				try {
					result = evaluateQuery(mappedQuery, context);
				}
				catch (Exception ex) {
					throw new EvaluationException(o.toString(), ex);
				}
				log.debug("This evaluated to: " + result.getSize());
				use.add(result);
			}
			else {
				log.debug("This refers to an operator: " + o);
				use.add(o);
			}
		}
		log.debug("Converted tokens to IdSets and Operators: " + use);

		log.debug("Inverting all [..., NOT, IdSet, ...] combinations");
		boolean invertTheNext = false;
		for (ListIterator<Object> i = use.listIterator(); i.hasNext();) {
			Object o = i.next();
			log.debug("Looking at element: " + o);
			if (o instanceof BooleanOperator) {
				if (o == BooleanOperator.NOT) {
					i.remove();
					invertTheNext = !invertTheNext;
					log.debug("This is a NOT, so removing it and invert the next = " + invertTheNext);
				}
				else {
					if (invertTheNext) {
						throw new RuntimeException("Invalid expression string, cannot have a NOT followed by an AND");
					}
				}
			}
			else {
				if (invertTheNext) {
					log.debug("Need to invert this...");
					if (o instanceof IdSet) {
						IdSet<T> toInvert = (IdSet<T>)o;
						IdSet<T> superSet = evaluateQuery(Mapped.noMappings(getAllIdQuery()), context);
						log.debug("Originally an IdSet of size " + toInvert.getSize());
						log.debug("With a superSet for context of size " + superSet.getSize());
						IdSet<T> invertedQuery = QueryUtil.subtract(superSet, toInvert);
						log.debug("Makes a new IdSet is of size " + invertedQuery.getSize());
						i.set(invertedQuery);
					}
					else {
						throw new RuntimeException("There is no method implemented for inverting a " + o.getClass());
					}
					invertTheNext = false;
				}
			}
		}
		log.debug("NOT conversion complete.  Now have: " + use);

		log.debug("Iterating across all Queries and Operators...");
		IdSet<T> ret = null;
		BooleanOperator operator = BooleanOperator.AND;
		for (Object o : use) {
			if (o instanceof BooleanOperator) {
				operator = (BooleanOperator)o;
				log.debug("New operator: " + operator);
			}
			else if (o instanceof IdSet) {
				IdSet<T> c = (IdSet<T>)o;
				log.debug("Found IdSet: " + c.getSize());
				if (ret == null) {
					ret = c;
					log.debug("Setting this as starting IdSet for return.");
				}
				else {
					if (operator == BooleanOperator.AND) {
						ret = QueryUtil.intersect(ret, c);
						log.debug("AND this in to get: " + ret.getSize());
					}
					else if (operator == BooleanOperator.OR) {
						ret = QueryUtil.union(ret, c);
						log.debug("OR this in to get: " + ret.getSize());
					}
					else {
						throw new RuntimeException("Unable to handle BooleanOperator: " + operator);
					}
				}
			}
			else {
				throw new RuntimeException("Can only handle IdSet and Operators.  Unable to handle class: " + o.getClass());
			}
		}
		log.debug("Done.  Returning: " + (ret == null ? null : ret.getSize()));
		return ret;
	}


	/**
	 * Elements in this list can be: an Integer, indicating a 1-based index into a search history a
	 * BooleanOperator (AND, OR, NOT) a Query, another List of the same form, which indicates a parenthetical expression
	 */
	public List<Object> parseIntoTokens(String expression) throws EvaluationException {

		List<Object> tokens = new ArrayList<Object>();
		try {
			StreamTokenizer st = new StreamTokenizer(new StringReader(expression));
			for (Character c : CHARACTER_WORDS) {
				st.ordinaryChar(c);
			}
			while (st.nextToken() != StreamTokenizer.TT_EOF) {
				if (st.ttype == StreamTokenizer.TT_NUMBER) {
					Integer thisInt = (int) st.nval;
					if (thisInt < 1) {
						throw new IllegalArgumentException("Invalid number < 1 found");
					}
					tokens.add(thisInt);
				}
				else if (OPEN_PARENTHESES_WORDS.contains(Character.valueOf((char) st.ttype))) {
					tokens.add("(");
				}
				else if (CLOSE_PARENTHESES_WORDS.contains(Character.valueOf((char) st.ttype))) {
					tokens.add(")");
				}
				else if (st.ttype == StreamTokenizer.TT_WORD) {
					tokens.add(st.sval);
				}
			}
			return parseIntoTokens(tokens);
		}
		catch (Exception e) {
			throw new EvaluationException("Unable to parse expression <" + expression + "> into tokens", e);
		}
	}

	/**
	 * Parses the passed tokens into another list of tokens, handling parenthesis
	 */
	protected List<Object> parseIntoTokens(List<Object> tokens) throws EvaluationException {
		List<Object> currentLine = new ArrayList<Object>();
		Stack<List<Object>> stack = new Stack<List<Object>>();
		try {
			for (Object token : tokens) {
				if (token instanceof String) {
					String s = (String) token;
					String lower = s.toLowerCase();
					if (AND_WORDS.contains(lower)) {
						currentLine.add(BooleanOperator.AND);
					}
					else if (OR_WORDS.contains(lower)) {
						currentLine.add(BooleanOperator.OR);
					}
					else if (NOT_WORDS.contains(lower)) {
						currentLine.add(BooleanOperator.NOT);
					}
					else {
						if (s.length() == 1) {
							char c = s.charAt(0);
							if (OPEN_PARENTHESES_WORDS.contains(c)) {
								stack.push(currentLine);
								currentLine = new ArrayList<Object>();
							}
							else if (CLOSE_PARENTHESES_WORDS.contains(c)) {
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
				else if (SUPPORTED_TYPES.contains(token.getClass())) {
					currentLine.add(token);
				}
				else {
					throw new IllegalArgumentException("Unknown class in token list: " + token.getClass());
				}
			}
		}
		catch (Exception e) {
			throw new EvaluationException("Unable to parse tokens into tokens", e);
		}
		return currentLine;
	}
}