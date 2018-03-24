/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SimplePatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.indicator.dimension.CohortIndicatorAndDimensionResult;
import org.openmrs.module.reporting.report.ReportData;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class ReportDashboardController {

	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Constructor
	 */
	public ReportDashboardController() { }
	
	/**
	 * Registers custom editors for fields of the command class.
	 * 
	 * @param binder
	 */
    @InitBinder
    public void initBinder(WebDataBinder binder) { 
    	binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), false)); 
    }    


    @RequestMapping("/module/reporting/dashboard/viewCohortDataSet")
    public String viewCohortDataSet(
    		@RequestParam(required=false, value="savedDataSetKey") String savedDataSetKey,
    		@RequestParam(required=false, value="savedColumnKey") String savedColumnKey,   		
    		@RequestParam(required=false, value="applyDataSetId") String applyDataSetId,
    		@RequestParam(required=false, value="limit") Integer limit,
    		HttpServletRequest request,
    		ModelMap model) throws EvaluationException { 
    	    
    	
		ReportData reportData = (ReportData) request.getSession().getAttribute(ReportingConstants.OPENMRS_REPORT_DATA);
    
		for (Map.Entry<String, DataSet> e : reportData.getDataSets().entrySet()) {
			if (e.getKey().equals(savedDataSetKey)) { 
				
				MapDataSet mapDataSet = (MapDataSet) e.getValue();
				
				DataSetColumn dataSetColumn = mapDataSet.getMetaData().getColumn(savedColumnKey);
				model.addAttribute("selectedColumn", dataSetColumn);
				
				Object result = mapDataSet.getData(dataSetColumn);
				Cohort selectedCohort = null;
				if (result instanceof CohortIndicatorAndDimensionResult) {
					CohortIndicatorAndDimensionResult cidr = (CohortIndicatorAndDimensionResult) mapDataSet.getData(dataSetColumn);
					selectedCohort = cidr.getCohortIndicatorAndDimensionCohort();
				}
				else if (result instanceof Cohort) {
					selectedCohort = (Cohort) result;
				} 

				model.addAttribute("selectedCohort", selectedCohort);

				// Evaluate the default patient dataset definition
				DataSetDefinition dsd = null;
				if (applyDataSetId != null) {
					try {
						dsd = Context.getService(DataSetDefinitionService.class).getDefinition(applyDataSetId, null);
					} catch (Exception ex) { 
						log.error("exception getting dataset definition", ex);				
					}
				}
				
				if (dsd == null) {
					SimplePatientDataSetDefinition d = new SimplePatientDataSetDefinition();
					d.addPatientProperty("patientId");
					List<PatientIdentifierType> types = ReportingConstants.GLOBAL_PROPERTY_PREFERRED_IDENTIFIER_TYPES();
					if (!types.isEmpty()) {
						d.setIdentifierTypes(types);
					}
					d.addPatientProperty("givenName");
					d.addPatientProperty("familyName");
					d.addPatientProperty("age");
					d.addPatientProperty("gender");
					dsd = d;
				}
				
				EvaluationContext evalContext = new EvaluationContext();
				if (limit != null && limit > 0) 
					evalContext.setLimit(limit);
				evalContext.setBaseCohort(selectedCohort);
				
				DataSet patientDataSet = Context.getService(DataSetDefinitionService.class).evaluate(dsd, evalContext);
				model.addAttribute("dataSet", patientDataSet);
		    	model.addAttribute("dataSetDefinition", dsd);
				
			}
		}
    	// Add all dataset definition to the request (allow user to choose)
    	model.addAttribute("dataSetDefinitions", Context.getService(DataSetDefinitionService.class).getAllDefinitions(false));
		
    	return "/module/reporting/dashboard/cohortDataSetDashboard";
    }
}
