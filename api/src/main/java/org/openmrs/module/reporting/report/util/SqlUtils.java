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
package org.openmrs.module.reporting.report.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Cohort;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.parameter.ParameterException;
import org.openmrs.module.reporting.IllegalDatabaseAccessException;
/**
 * Provides access to a variety of common SQL functionality
 */
public class SqlUtils {
	
	/**
	 * Binds the given paramMap to the query by replacing all named parameters (e.g. :paramName)
	 * with their corresponding values in the parameter map. TODO copied from
	 * HibernateCohortQueryDAO
	 * 
	 * @param connection
	 * @param query
	 * @param paramMap
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
    public static PreparedStatement prepareStatement(Connection connection, String query, Map<String, Object> paramMap) throws SQLException {
		
		PreparedStatement statement;
		if (!isSelectQuery(query)) {
			throw new IllegalDatabaseAccessException();
		}
		boolean containParams = query.indexOf(":") > 0;
		if (containParams) {
			
			// the first process is replacing the :paramName with ?
			// implementation taken from: http://www.javaworld.com/javaworld/jw-04-2007/jw-04-jdbc.html?page=2
			Map<String, List<Integer>> params = new HashMap<String, List<Integer>>();
			StringBuffer parsedQuery = new StringBuffer();
			
			int index = 1;
			for (int i = 0; i < query.length(); i++) {
				
				// we can use charAt here, but we might need to append "(?, ?, ?)" when the where parameter is a list
				// http://stackoverflow.com/questions/178479/alternatives-for-java-sql-preparedstatement-in-clause-issue
				// http://www.javaranch.com/journal/200510/Journal200510.jsp#a2
				String s = query.substring(i, i + 1);
				
				if (StringUtils.equals(s, ":") && i + 1 < query.length()
				        && Character.isJavaIdentifierStart(query.charAt(i + 1))) {
					// we already make sure that (i + 1) is a valid character, now check the next one after (i + 1)
					int j = i + 2;
					while (j < query.length() && Character.isJavaIdentifierPart(query.charAt(j)))
						j++;
					
					String name = query.substring(i + 1, j);
					Object paramValue = paramMap.get(name);
					
					// are we dealing with collection or not
					int size = 1;
					if (paramValue != null)
						if (Cohort.class.isAssignableFrom(paramValue.getClass()))
							size = ((Cohort) paramValue).getSize();
						else if (Collection.class.isAssignableFrom(paramValue.getClass()))
							size = ((Collection<?>) paramValue).size();
					
					// skip until the end of the param name
					i += name.length();
					
					String[] sqlParams = new String[size];
					for (int k = 0; k < sqlParams.length; k++) {
						sqlParams[k] = "?";
						// record the location of the parameter in the sql statemet
						List<Integer> indexList = params.get(name);
						if (indexList == null) {
							indexList = new LinkedList<Integer>();
							params.put(name, indexList);
						}
						indexList.add(new Integer(index));
						index++;
					}
					s = StringUtils.join(sqlParams, ",");
					
					// for the "IN" query, we need to add bracket
					if (size > 1)
						s = "(" + s + ")";
				}
				
				parsedQuery.append(s);
			}
			
			// the query string contains parameters, re-create the prepared statement with the new parsed query string
			statement = connection.prepareStatement(parsedQuery.toString());
			
			// Iterate over parameters and bind them to the Query object
			for (String paramName : paramMap.keySet()) {
				
				Object paramValue = paramMap.get(paramName);
				
				// Indicates whether we should bind this parameter in the query 
				// Make sure parameter value is not null
				if (paramValue == null) {
					// TODO Should try to convert 'columnName = null' to 'columnName IS NULL'  
					throw new ParameterException(
					        "Cannot bind an empty value to parameter "
					                + paramName
					                + ". "
					                + "Please provide a real value or use the 'IS NULL' constraint in your query (e.g. 'table.columnName IS NULL').");
				}
				
				int i = 0;
				List<Integer> positions = params.get(paramName);
				if (positions != null) {
					// Cohort (needs to be first, otherwise it will resolve as OpenmrsObject)
					if (Cohort.class.isAssignableFrom(paramValue.getClass())) {
						Cohort cohort = (Cohort) paramValue;
						for (Integer patientId : cohort.getMemberIds()) {
							statement.setInt(positions.get(i++), patientId);
						}
					}
					// OpenmrsObject (e.g. Location)
					else if (OpenmrsObject.class.isAssignableFrom(paramValue.getClass())) {
						for (Integer position : positions) {
							statement.setInt(position, ((OpenmrsObject) paramValue).getId());
						}
					}
					// List<OpenmrsObject> (e.g. List<Location>)
					else if (List.class.isAssignableFrom(paramValue.getClass())) {
						// If first element in the list is an OpenmrsObject
						if (OpenmrsObject.class.isAssignableFrom(((List<?>) paramValue).get(0).getClass())) {
							List<Integer> openmrsObjectIds = SqlUtils.openmrsObjectIdListHelper((List<OpenmrsObject>) paramValue);
							for (Integer openmrsObjectId : openmrsObjectIds) {
								statement.setInt(positions.get(i++), openmrsObjectId);
							}
						}
						// a List of Strings, Integers?
						else {
							List<String> strings = SqlUtils.objectListHelper((List<Object>) paramValue);
							for (String string : strings) {
								statement.setString(positions.get(i++), string);
							}
						}
					}
					// java.util.Date and subclasses
					else if (paramValue instanceof Date) {
						for (Integer position : positions) {
							statement.setDate(position, new java.sql.Date(((Date) paramValue).getTime()));
						}
					}
					else if (paramValue instanceof Integer || paramValue instanceof Long) {
						for (Integer position : positions) {
							statement.setLong(position, (Integer)paramValue);
						}
					}
					// String, et al (this might break since this is a catch all for all other classes)
					else {
						for (Integer position : positions) {
							statement.setString(position, new String(paramValue.toString()));
						}
					}
				}
			}
		} else
			statement = connection.prepareStatement(query);
		
		return statement;
	}
	
	/**
	 * TODO Move this to a reporting utility class or to core.
	 * 
	 * @param list a list of OpenmrsObjects
	 * @return null if passed null or an empty list, otherwise returns a list of the ids of the
	 *         OpenmrsObjects in list
	 */
	public static List<Integer> openmrsObjectIdListHelper(List<? extends OpenmrsObject> list) {
		if (list == null || list.size() == 0)
			return null;
		List<Integer> ret = new ArrayList<Integer>();
		for (OpenmrsObject o : list)
			ret.add(o.getId());
		return ret;
	}
	
	/**
	 * TODO Move this to a reporting utility class or to core.
	 * 
	 * @param list a list of Objects
	 * @return null if passed null or an empty list, otherwise returns a list of Object.toString()
	 */
	public static List<String> objectListHelper(List<? extends Object> list) {
		if (list == null || list.size() == 0)
			return null;
		List<String> results = new ArrayList<String>();
		for (Object object : list)
			results.add(object.toString());
		
		return results;
	}

    /**
     * Used to  check if a  query is a  select  query or if it is a  update/insert/delete/drop or select into  query.
     * This is used to prevent queries that tries to perform  database  modifications
     */
    public static boolean isSelectQuery(String query) {

		List<String> updateWords = Arrays.asList("insert", "update", "delete", "alter", "drop", "create", "rename", "into");
		for (String statement : query.trim().split(";")) {
			String s = statement.toLowerCase().trim();
			if (ObjectUtil.notNull(s)) {
				if (!s.startsWith("select")) {
					return false;
				}
				for (String word : s.split("\\s")) {
					if (updateWords.contains(word)) {
						return false;
					}
				}
			}
		}
		return true;
	}

}
