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
package org.openmrs.module.reporting.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.DuplicateTagException;
import org.openmrs.module.reporting.definition.DefinitionTag;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validates the {@link DefinitionTag} class.
 */
@Handler(supports = { DefinitionTag.class }, order = 50)
public class DefinitionTagValidator implements Validator {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Determines if the command object being submitted is a valid type
	 * 
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public boolean supports(Class c) {
		return DefinitionTag.class.isAssignableFrom(c);
	}
	
	/**
	 * Checks the form object for any inconsistencies/errors
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 *      org.springframework.validation.Errors)
	 * @should fail if the object is null
	 * @should fail if the definition tag is null
	 * @should fail if the definition uuid is null
	 * @should fail if the definition type is null
	 * @should fail if the definition already has the tag
	 * @should pass validation if all fields are correct
	 * @should fail if tag or type or definition uuid has zero length
	 * @should fail if tag or type or definition uuid is a white space character
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void validate(Object obj, Errors errors) {
		if (obj == null)
			throw new IllegalArgumentException("The parameter obj should not be null");
		
		DefinitionTag definitionTag = (DefinitionTag) obj;
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "tag", "error.required", new Object[] { Context
		        .getMessageSourceService().getMessage("reporting.Report.tag") });
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "definitionUuid", "error.required",
		    new Object[] { "Definition uuid" });
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "definitionType", "error.required",
		    new Object[] { "Definition type" });
		
		if (!errors.hasErrors()) {
			Class<? extends Definition> type = (Class<? extends Definition>) ReportUtil.loadClass(definitionTag
			        .getDefinitionType());
			DefinitionService definitionService = ReportUtil.getDefinitionServiceForType(type);
			if (definitionService.hasTag(definitionTag.getDefinitionUuid(), definitionTag.getTag())) {
				throw new DuplicateTagException(Context.getMessageSourceService().getMessage(
				    "reporting.Report.error.duplicateTag", new Object[] { definitionTag.getTag() }, null));
			}
		}
	}
}
