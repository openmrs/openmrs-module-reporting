/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.definition.converter;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.SerializedObject;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
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
			CohortDefinition scd = Context.getService(CohortDefinitionService.class).getDefinitionByUuid(so.getUuid());
			log.info("Successfully converted SqlCohortDefinition named '" + scd.getName() + "' to new format");
			return true;
		}
		catch (Exception e) {
			log.warn("Unable to successfully migrate definition with uuid: " + so.getUuid(), e);
			return false;
		}
	}
}
