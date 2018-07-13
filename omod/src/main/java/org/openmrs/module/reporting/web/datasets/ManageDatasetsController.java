/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.web.datasets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlwidgets.web.WidgetUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.ReflectionUtil;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.definition.DefinitionSummary;
import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.definition.configuration.Property;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ManageDatasetsController {

	protected Log log = LogFactory.getLog(this.getClass());
	
    /**
     * Controller for the edit data set page
     */
	@RequestMapping("/module/reporting/datasets/editDataSet")
    public String editDataSet(
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam(required=false, value="type") Class<? extends DataSetDefinition> type,
            @RequestParam(required=false, value="cohortSize") Integer cohortSize,
            @RequestParam(required=false, value="action") String action,
    		ModelMap model) {
	
		DataSetDefinitionService service = Context.getService(DataSetDefinitionService.class);
		DataSetDefinition dsd = service.getDefinition(uuid, type);
	 	model.addAttribute("dataSetDefinition", dsd);
	
	 	List<Property> properties = DefinitionUtil.getConfigurationProperties(dsd);
	 	model.addAttribute("configurationProperties", properties);
	 	Map<String, List<Property>> groups = new LinkedHashMap<String, List<Property>>();
	 	for (Property p : properties) {
	 		List<Property> l = groups.get(p.getGroup());
	 		if (l == null) {
	 			l = new ArrayList<Property>();
	 			groups.put(p.getGroup(), l);
	 		}
	 		l.add(p);
	 	}
	 	model.addAttribute("groupedProperties", groups);
	
	    return "/module/reporting/datasets/datasetEditor";
    }	
	
	/*
	 * Load Affected Report Definitions.
	 */
	@RequestMapping("/module/reporting/datasets/loadAffectedDatasetDefs")
	@ResponseBody()
	public List<DefinitionSummary> loadAffectedDatasetDefs(@RequestParam(required=true,value="uuid")String uuid){
		List<ReportDefinition> reportDefinitions = Context.getService(ReportDefinitionService.class).getAllDefinitions(false);	
		List<DefinitionSummary> affectedReportDefs = new ArrayList<DefinitionSummary>();
		for (ReportDefinition reportDef : reportDefinitions) {
			List<Mapped<? extends DataSetDefinition>> dsds = new 
					ArrayList<Mapped<? extends DataSetDefinition>>(reportDef.getDataSetDefinitions().values());
			if (!dsds.isEmpty()) {
				for (Mapped<? extends DataSetDefinition> dataSet : dsds) {    
					  if (uuid.equals(dataSet.getUuidOfMappedOpenmrsObject())) {
						  DefinitionSummary affectedRep = new DefinitionSummary(dataSet.getParameterizable());
						  affectedReportDefs.add(affectedRep);
					  }
				}
			} 
		}
		
		return affectedReportDefs;
	}
    
    /**
     * Save DataSetDefinition
     */
    @RequestMapping("/module/reporting/datasets/saveDataSet")
    @SuppressWarnings("unchecked")
    public String saveDataSet(
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam(required=false, value="type") Class<? extends DataSetDefinition> type,
            @RequestParam(required=true, value="name") String name,
            @RequestParam("description") String description,
            HttpServletRequest request,
    		ModelMap model
    ) {
    	
    	DataSetDefinitionService service = Context.getService(DataSetDefinitionService.class);
    	DataSetDefinition dataSetDefinition = service.getDefinition(uuid, type);
    	dataSetDefinition.setName(name);
    	dataSetDefinition.setDescription(description);
    	dataSetDefinition.getParameters().clear();
    	
    	for (Property p : DefinitionUtil.getConfigurationProperties(dataSetDefinition)) {
    		String fieldName = p.getField().getName();
    		String prefix = "parameter." + fieldName;
    		String valParamName =  prefix + ".value"; 
    		boolean isParameter = "t".equals(request.getParameter(prefix+".allowAtEvaluation"));
    		
    		Object valToSet = WidgetUtil.getFromRequest(request, valParamName, p.getField());
    		
    		Class<? extends Collection<?>> collectionType = null;
    		Class<?> fieldType = p.getField().getType();   		
			if (ReflectionUtil.isCollection(p.getField())) {
				collectionType = (Class<? extends Collection<?>>)p.getField().getType();
				fieldType = (Class<?>)ReflectionUtil.getGenericTypes(p.getField())[0];
			}
			
			if (isParameter) {
				ReflectionUtil.setPropertyValue(dataSetDefinition, p.getField(), null);
				String paramLabel = ObjectUtil.nvlStr(request.getParameter(prefix + ".label"), fieldName);
				Parameter param = new Parameter(fieldName, paramLabel, fieldType, collectionType, valToSet);
				dataSetDefinition.addParameter(param);
			}
			else {
				ReflectionUtil.setPropertyValue(dataSetDefinition, p.getField(), valToSet);
			}
    	}
    	
    	log.debug("Saving: " + dataSetDefinition);
    	Context.getService(DataSetDefinitionService.class).saveDefinition(dataSetDefinition);

        return "redirect:/module/reporting/definition/manageDefinitions.form?type=org.openmrs.module.reporting.dataset.definition.DataSetDefinition";
    }
      	
}
