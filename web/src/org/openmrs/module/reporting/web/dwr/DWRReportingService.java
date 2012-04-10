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
package org.openmrs.module.reporting.web.dwr;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.report.util.ReportUtil;

public class DWRReportingService {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Processes requests to cancel a queued {@link ReportRequest}
	 * 
	 * @param id the id of the {@link ReportRequest} to purge
	 * @return true if the request was successfully purged otherwise false
	 */
	public boolean purgeReportRequest(Integer id) {
		if (id != null) {
			ReportService rs = Context.getService(ReportService.class);
			ReportRequest request = rs.getReportRequest(id);
			if (request != null) {
				try {
					rs.purgeReportRequest(request);
					return true;
				}
				catch (Exception e) {
					//
				}
			}
		}
		return false;
	}
	
	/**
	 * Process requests to add tags to definitions
	 * 
	 * @param uuid the uuid of the definition to apply the tag
	 * @param tag the tag to add
	 * @param definitionType the type of the definition to apply the tag
	 * @return true if the tag was successfully added otherwise false
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean addTag(String uuid, String tag, String definitionType) {
		if (StringUtils.isNotBlank(uuid) && StringUtils.isNotBlank(tag) && StringUtils.isNotBlank(definitionType)) {
			Class<? extends Definition> type = (Class<? extends Definition>) ReportUtil.loadClass(definitionType);
			DefinitionService definitionService = ReportUtil.getDefinitionServiceForType(type);
			Definition definition = definitionService.getDefinition(uuid, type);
			return definitionService.addTagToDefinition(definition, tag);
		}
		return false;
	}
	
	/**
	 * Process requests to remove tags from definitions
	 * 
	 * @param uuid the uuid of the definition from which to remove the tag
	 * @param tag the tag to remove
	 * @param definitionType the type of the definition from which to remove the tag
	 * @return true if the tag was successfully removed otherwise false
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean removeTag(String uuid, String tag, String definitionType) {
		if (StringUtils.isNotBlank(uuid) && StringUtils.isNotBlank(tag) && StringUtils.isNotBlank(definitionType)) {
			Class<? extends Definition> type = (Class<? extends Definition>) ReportUtil.loadClass(definitionType);
			DefinitionService definitionService = ReportUtil.getDefinitionServiceForType(type);
			Definition definition = definitionService.getDefinition(uuid, type);
			definitionService.removeTagFromDefinition(definition, tag);
			return true;
		}
		return false;
	}
}
