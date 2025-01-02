/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort.definition.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openmrs.OpenmrsObject;
import org.openmrs.User;
import org.openmrs.aop.RequiredDataAdvice;
import org.openmrs.api.handler.SaveHandler;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

/**
 * This class deals with {@link SqlCohortDefinition} objects when they are saved via a save*
 * method in an OpenMRS Service. This handler is automatically called by the
 * {@link RequiredDataAdvice} AOP class.
 */
public class SqlCohortDefinitionSaveHandler implements SaveHandler<SqlCohortDefinition> {

	/**
	 * @see SaveHandler#handle(OpenmrsObject, User, Date, String)
	 */
	public void handle(SqlCohortDefinition sqlCohortDefinition, User creator, Date dateCreated, String other) {
		if (sqlCohortDefinition != null) {
		
			// Find all named parameters 
			List<Parameter> parameters = findNamedParameters(sqlCohortDefinition.getQuery());
			
			// For now, just add any newly discovered named parameters
			for (Parameter parameter : parameters) {
				Parameter existingParameter = sqlCohortDefinition.getParameter(parameter.getName());
				if (existingParameter == null) {
					sqlCohortDefinition.addParameter(parameter);
				}
			}
		}
	}
	
	/**
	 * TODO Move to a utility class
	 */
	public List<Parameter> findNamedParameters(String sqlQuery) {		
		List<Parameter> parameters = new ArrayList<Parameter>();
		
		// Simple regular expression parser 
		//
		// Must support:  
		//   column = :paramName
		//   column = :paramName2		
		//
		// Should support: 
		//   column = :paramName::java.lang.Date
		//   column = :paramName2::datetime
		// 
		Pattern pattern = Pattern.compile("\\:\\w+\\b");
		Matcher matcher = pattern.matcher(sqlQuery);

		while (matcher.find()) {			
			// Need to strip off the colon (":")
			String parameterName = matcher.group().substring(1);			
			Parameter parameter = new Parameter();			
			parameter.setName(parameterName);
			parameter.setLabel("Choose a " + parameterName);
			parameter.setType(String.class);	// TODO Need to be able to support more data types!
			parameters.add(parameter);
		}		
		return parameters;
	}
}
