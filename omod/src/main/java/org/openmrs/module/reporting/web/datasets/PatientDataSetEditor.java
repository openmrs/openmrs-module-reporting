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
package org.openmrs.module.reporting.web.datasets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlwidgets.web.WidgetUtil;
import org.openmrs.module.htmlwidgets.web.html.Option;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.dataset.column.definition.RowPerObjectColumnDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.propertyeditor.MappedEditor;
import org.openmrs.module.reporting.web.util.ParameterUtil;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;


/**
 * For creating and editing {@link PatientDataSetDefinition}s
 */
@Controller
public class PatientDataSetEditor {
	
	public final static String DSD_ATTR = "PatientDataSetEditor_dsd";
	public final static String IS_UNSAVED_ATTR = "PatientDataSetEditor_is_unsaved"; 
	public final static String SUCCESS_URL_ATTR = "PatientDataSetEditor_successUrl";
	public final static String DISCARD_URL_ATTR = "PatientDataSetEditor_discardUrl";
	
	Log log = LogFactory.getLog(getClass());
	
	@ModelAttribute("dsd")
	public PatientDataSetDefinition getDataSetDefinition(@RequestParam(required=false, value="uuid") String uuid,
	                                                     @RequestParam(required=false) String successUrl, 
	                                                     @RequestParam(required=false) String discardUrl, HttpSession session) {
		PatientDataSetDefinition dsd = getFromSession(session);
		if (dsd != null && uuid != null && !uuid.equals(dsd.getUuid())) {
			removeFromSession(session);
			dsd = null;
		}
		
		if (dsd == null) {
			if (uuid != null) {
				dsd = (PatientDataSetDefinition) Context.getService(DataSetDefinitionService.class).getDefinitionByUuid(uuid);
				if (dsd == null)
					throw new RuntimeException("No PatientDataSetDefinition found with uuid " + uuid);
				putInSession(session, dsd, false);
			} else {
				dsd = new PatientDataSetDefinition();
				dsd.setName("Untitled");
				putInSession(session, dsd, true);
			}
		}
		
		if (!StringUtils.isBlank(successUrl)) {
			session.setAttribute(SUCCESS_URL_ATTR, successUrl);
		}
		if (!StringUtils.isBlank(discardUrl)) {
			session.setAttribute(DISCARD_URL_ATTR, discardUrl);
		}
		
		return dsd;
	}
	
	@ModelAttribute("parameterTypes")
	public List<Option> getAvailableParameterTypes() {
		return ParameterUtil.getSupportedTypes();
	}
	
	@ModelAttribute("parameterCollectionTypes")
	public List<Option> getAvailableParameterCollectionTypes() {
		return ParameterUtil.getSupportedCollectionTypes();
	}
	
    private void putInSession(HttpSession session, PatientDataSetDefinition dsd, boolean isUnsaved) {
	    session.setAttribute(DSD_ATTR, dsd);
	    session.setAttribute(IS_UNSAVED_ATTR, isUnsaved);
    }

	private void removeFromSession(HttpSession session) {
	    session.removeAttribute(DSD_ATTR);
	    session.removeAttribute(IS_UNSAVED_ATTR);
	    session.removeAttribute(SUCCESS_URL_ATTR);
	    session.removeAttribute(DISCARD_URL_ATTR);
    }

	private PatientDataSetDefinition getFromSession(HttpSession session) {
		try {
			return (PatientDataSetDefinition) session.getAttribute(DSD_ATTR);
		} catch (ClassCastException ex) {
			// module has been reloaded
			removeFromSession(session);
			return null;
		}
    }

	@RequestMapping("/module/reporting/datasets/patientDataSetEditor")
	public void showDataset(HttpSession session,
	                        Model model) {
		model.addAttribute("unsaved", session.getAttribute(IS_UNSAVED_ATTR));
		model.addAttribute("dsd", session.getAttribute(DSD_ATTR));
		model.addAttribute("dataDefinitionTypes", PatientDataDefinition.class.getName() + "," + PersonDataDefinition.class.getName());
	}
	
	@RequestMapping(value="/module/reporting/datasets/patientDataSetEditor-nameDescription", method=RequestMethod.POST)
	public String changeNameAndDescription(@RequestParam("name") String name,
	                                       @RequestParam("description") String description,
	                                       HttpSession session) {
		PatientDataSetDefinition dsd = getFromSession(session);
		dsd.setName(name);
		dsd.setDescription(description);
		putInSession(session, dsd, true);
		return "redirect:patientDataSetEditor.form";
	}

	@RequestMapping(value="/module/reporting/datasets/patientDataSetEditor-addParam", method=RequestMethod.POST)
	public String addParameter(@RequestParam(value="collectionType", required=false) Class<? extends Collection<?>> collectionType,
	                              @RequestParam(value="parameterType", required=false) Class<?> parameterType,
	                              @RequestParam(value="name", required=false) String name,
	                              @RequestParam(value="label", required=false) String label,
	                              @RequestParam(value="widgetConfiguration", required=false) String widgetConfiguration,
	                              HttpSession session) {
		if (parameterType == null || name == null || label == null) {
			session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Type, Name, and Label are required");
			return "redirect:patientDataSetEditor.form";
		}
		
		Properties widgetConfig = null;
    	if (ObjectUtil.notNull(widgetConfiguration)) {
    		widgetConfig = WidgetUtil.parseInput(widgetConfiguration, Properties.class);
    	}
    	
    	PatientDataSetDefinition dsd = getFromSession(session);
    	dsd.addParameter(new Parameter(name, label, parameterType, collectionType, null, widgetConfig));
    	putInSession(session, dsd, true);
		return "redirect:patientDataSetEditor.form";
	}
		
	@RequestMapping(value="/module/reporting/datasets/patientDataSetEditor-removeParam", method=RequestMethod.POST)
	public String removeParameter(@RequestParam("name") String name,
	                              HttpSession session) {
		PatientDataSetDefinition dsd = getFromSession(session);
		dsd.removeParameter(name);
		putInSession(session, dsd, true);
		return "redirect:patientDataSetEditor.form";
	}
	
	@RequestMapping(value="/module/reporting/datasets/patientDataSetEditor-addColumn", method=RequestMethod.POST)
	public String addColumn(@RequestParam(value="label", required=false) String label,
	                        @RequestParam(value="columnDefinition", required=false) String columnDefinition,
	                        HttpSession session) {
		if (StringUtils.isBlank(label) || StringUtils.isBlank(columnDefinition)) {
			session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Label and Definition are required");
			return "redirect:patientDataSetEditor.form";
		}
		
    	PatientDataSetDefinition dsd = getFromSession(session);
    	for (RowPerObjectColumnDefinition col : dsd.getColumnDefinitions()) {
    		if (label.equals(col.getName())) {
    			session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "There is already a column named '" + label + "'");
    			return "redirect:patientDataSetEditor.form";
    		}
    	}
		
		MappedEditor editor = new MappedEditor();
		editor.setAsText(columnDefinition);
		Mapped<DataDefinition> mappedDef = (Mapped<DataDefinition>) editor.getValue();
		
    	dsd.addColumn(label, mappedDef.getParameterizable(), mappedDef.getParameterMappings());
    	putInSession(session, dsd, true);
		return "redirect:patientDataSetEditor.form";
	}
	
	@RequestMapping(value="/module/reporting/datasets/patientDataSetEditor-removeColumn", method=RequestMethod.POST)
	public String removeColumn(@RequestParam("name") String name,
	                           HttpSession session) {
		PatientDataSetDefinition dsd = getFromSession(session);
		dsd.removeColumnDefinition(name);
		putInSession(session, dsd, true);
		return "redirect:patientDataSetEditor.form";
	}
	
	@RequestMapping(value="/module/reporting/datasets/patientDataSetEditor-addFilter", method=RequestMethod.POST)
	public String addFilter(@RequestParam(value="filterDefinition", required=false) String filterDefinition,
	                        HttpSession session) {
		if (StringUtils.isBlank(filterDefinition)) {
			session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Definition is required");
			return "redirect:patientDataSetEditor.form";
		}
		
    	PatientDataSetDefinition dsd = getFromSession(session);

		MappedEditor editor = new MappedEditor();
		editor.setAsText(filterDefinition);
		Mapped<CohortDefinition> mappedDef = (Mapped<CohortDefinition>) editor.getValue();
		dsd.addRowFilter(mappedDef);
		
    	putInSession(session, dsd, true);
		return "redirect:patientDataSetEditor.form";
	}
	
	@RequestMapping(value="/module/reporting/datasets/patientDataSetEditor-removeFilter", method=RequestMethod.POST)
	public String removeFilter(@RequestParam("filterIndex") int index,
	                           HttpSession session) {
		PatientDataSetDefinition dsd = getFromSession(session);
		dsd.getRowFilters().remove(index);
		putInSession(session, dsd, true);
		return "redirect:patientDataSetEditor.form";
	}
	
	@RequestMapping(value="/module/reporting/datasets/patientDataSetEditor-sortColumns", method=RequestMethod.POST)
	public String sortColumns(WebRequest request,
	                          HttpSession session) {
		final List<String> columnOrder = new ArrayList<String>();
		int i = 0;
		while (true) {
			String colName = request.getParameter("column" + i);
			if (colName == null)
				break;
			columnOrder.add(colName);
			++i;
		}
		
		PatientDataSetDefinition dsd = getFromSession(session);
		Collections.sort(dsd.getColumnDefinitions(), new Comparator<RowPerObjectColumnDefinition>() {
			public int compare(RowPerObjectColumnDefinition left, RowPerObjectColumnDefinition right) {
				Integer leftIndex = columnOrder.indexOf(left.getName());
				Integer rightIndex = columnOrder.indexOf(right.getName());
				return leftIndex.compareTo(rightIndex);
            }
		});
		putInSession(session, dsd, true);
		return "redirect:patientDataSetEditor.form";
	}
	
	@RequestMapping(value="/module/reporting/datasets/patientDataSetEditor-save", method=RequestMethod.POST)
	public String save(HttpSession session) {
		PatientDataSetDefinition dsd = getFromSession(session);
		String successUrl = (String) session.getAttribute(SUCCESS_URL_ATTR);
		
		Context.getService(DataSetDefinitionService.class).saveDefinition(dsd);
		
		removeFromSession(session);
		
		if (!StringUtils.isBlank(successUrl)) {
			return "redirect:" + successUrl;
		} else {
			return "redirect:patientDataSetEditor.form?uuid=" + dsd.getUuid();
		}
	}
	
	@RequestMapping(value="/module/reporting/datasets/patientDataSetEditor-discard", method=RequestMethod.POST)
	public String discard(HttpSession session) {
		String discardUrl = (String) session.getAttribute(DISCARD_URL_ATTR);
		
		removeFromSession(session);
		
		if (!StringUtils.isBlank(discardUrl)) {
			return "redirect:" + discardUrl;
		} else {
			return "redirect:/module/reporting/definition/manageDefinitions.form?type=org.openmrs.module.reporting.dataset.definition.DataSetDefinition";
		}
	}
	
}
