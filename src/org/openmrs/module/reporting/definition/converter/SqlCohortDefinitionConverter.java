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
package org.openmrs.module.reporting.definition.converter;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.SerializedObject;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.DefinitionContext;
import org.openmrs.module.reporting.definition.service.SerializedDefinitionService;

/**
 * Converts legacy SqlCohortDefinitions into the latest supported format
	Before:
     ...
	  <queryDefinition class="org.openmrs.module.reporting.query.definition.SqlQueryDefinition" id="3" retired="false">
	    <parameters id="4"/>
	    <queryString>SELECT distinct patient_id FROM patient WHERE patient_id = :patientId</queryString>
	  </queryDefinition>
	  ...
	
	After:
	  ...
	  <query>SELECT distinct patient_id FROM patient WHERE patient_id = :patientId</query>
	  ...
 */
@Handler
public class SqlCohortDefinitionConverter implements DefinitionConverter {
	
	protected static Log log = LogFactory.getLog(SqlCohortDefinitionConverter.class);

	/**
	 * @see DefinitionConverter#getInvalidDefinitions()
	 */
	public List<SerializedObject> getInvalidDefinitions() {
    	SerializedDefinitionService service = Context.getService(SerializedDefinitionService.class);
    	return service.getInvalidDefinitions(SqlCohortDefinition.class, true);
	}
	
	/**
	 * @see DefinitionConverter#convert()
	 * @should convert legacy definitions to latest format
	 */
	public boolean convertDefinition(SerializedObject so) {
		
		String xml = so.getSerializedData();
		log.debug("Starting xml: " + xml);
		
		try {
			int qStart = xml.indexOf("<queryString>") + 13;
			int qEnd = xml.indexOf("</queryString>");
			String queryString = xml.substring(qStart, qEnd);
			log.debug("Retrieved query string: " + queryString);
			
			StringBuilder newXml = new StringBuilder();
			newXml.append(xml.substring(0, xml.indexOf("<queryDefinition ")));
			newXml.append("<query>"+queryString+"</query>");
			newXml.append(xml.substring(xml.indexOf("</queryDefinition>") + 18, xml.length()));
			log.debug("Ending xml: " + newXml);
			
			so.setSerializedData(newXml.toString());
			Context.getService(SerializedDefinitionService.class).saveSerializedDefinition(so);
			
			// Confirm this works
			SqlCohortDefinition scd = DefinitionContext.getDefinitionByUuid(SqlCohortDefinition.class, so.getUuid());
			log.info("Successfully converted SqlCohortDefinition named '" + scd.getName() + "' to new format");
			return true;
		}
		catch (Exception e) {
			log.warn("Unable to successfully migrate definition with uuid: " + so.getUuid(), e);
			return false;
		}
	}
}
