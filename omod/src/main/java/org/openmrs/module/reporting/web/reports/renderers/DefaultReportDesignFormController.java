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

package org.openmrs.module.reporting.web.reports.renderers;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.propertyeditor.MappedEditor;
import org.openmrs.module.reporting.propertyeditor.ReportDefinitionEditor;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;

import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

public class DefaultReportDesignFormController extends SimpleFormController implements Validator {

	private transient Log log = LogFactory.getLog( this.getClass() );
	
	/**
	 * @see BaseCommandController#initBinder(HttpServletRequest, ServletRequestDataBinder)
	 */
	@SuppressWarnings("unchecked")
	protected void initBinder( HttpServletRequest request, ServletRequestDataBinder binder ) throws Exception {
		super.initBinder( request, binder );
		String[] allowedFields = new String[ 6 ];
		int i = 0;
		// not all the fields are required so we only check for those that have values
		Enumeration<String> parameters = request.getParameterNames();
		while( parameters.hasMoreElements() ) {
			String parameter = (String) parameters.nextElement();
			if ( StringUtils.hasText( request.getParameter( parameter ) ) ) {
				allowedFields[ i ] = parameter;
			}
			i++;
		}
		binder.setAllowedFields( allowedFields );
		binder.registerCustomEditor( Mapped.class, new MappedEditor() );
		binder.registerCustomEditor( ReportDefinition.class, new ReportDefinitionEditor() );
	}
	
	@SuppressWarnings( "rawtypes" )
	public boolean supports( Class c ) {
		return c == ReportDesign.class;
	}
	
	@Override
	public void validate(Object commandObject, Errors errors) {
		ReportDesign command = (ReportDesign) commandObject;
		ValidationUtils.rejectIfEmpty( errors, "name", "error.required", new Object[] { "This parameter" } );
		ValidationUtils.rejectIfEmpty( errors, "reportDefinition", "error.required", new Object[] { "This parameter" });
		ValidationUtils.rejectIfEmpty( errors, "rendererType", "error.required", new Object[] { "This parameter" } );
	}

	@Override
	protected Object formBackingObject( HttpServletRequest request ) throws Exception {
		ReportDesign command = new ReportDesign();
		if ( Context.isAuthenticated() ) {
			// Get the uuid of the reportDesign, if supplied
			ReportService rs = Context.getService( ReportService.class );
			String reportDesignUuid = ( String )request.getParameter( "reportDesignUuid" );
			String reportDefinitionUuid = ( String )request.getParameter( "reportDefinitionUuid" );
			if ( StringUtils.hasText( reportDesignUuid ) ) {
				command = rs.getReportDesignByUuid( reportDesignUuid );
			} else {
				if ( StringUtils.hasText( reportDefinitionUuid ) ) {
					command.setReportDefinition( Context.getService( ReportDefinitionService.class ).getDefinitionByUuid( reportDefinitionUuid ) );
				}
			}
		}
		return command;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object commandObject, BindException errors) throws Exception {
		ReportDesign command = (ReportDesign) commandObject;
		ReportService rs = Context.getService(ReportService.class);
		
		   	
    	
     	MultipartHttpServletRequest mpr = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> files = (Map<String, MultipartFile>)mpr.getFileMap();
     	Set<String> foundResources = new HashSet<String>();
     	for (String paramName : files.keySet()) {
     		try {
	     		String[] split = paramName.split("\\.", 2);
	     		if (split.length == 2 && split[0].equals("resources")) {
	     			ReportDesignResource resource = null;
	     			if (split[1].startsWith("new")) {
	     				resource = new ReportDesignResource();
	     			}
	     			else {
	     				foundResources.add(split[1]);
	     				resource = command.getResourceByUuid(split[1]);
	     			}
	     			MultipartFile file = files.get(paramName);
	     			String fileName = file.getOriginalFilename();
	     			if (StringUtils.hasText(fileName)) {
		     			int index = fileName.lastIndexOf(".");
		     			resource.setReportDesign(command);
		     			resource.setContentType(file.getContentType());
		     			resource.setName(fileName.substring(0, index));
		     			resource.setExtension(fileName.substring(index+1));
		     			resource.setContents(file.getBytes());
		     			command.getResources().add(resource);
	     			}
	     		}
     		}
     		catch (Exception e) {
     			throw new RuntimeException("Unable to add resource to design.", e);
     		}
     	}

     	for (Iterator<ReportDesignResource> i = command.getResources().iterator(); i.hasNext();) {
     		ReportDesignResource r = i.next();
     		if (r.getId() != null && !foundResources.contains(r.getUuid())) {
     			i.remove();
     		}
     	}

     	command = rs.saveReportDesign( command );
		return new ModelAndView(new RedirectView("../manageReportDesigns.form" ));
	}
	
}
