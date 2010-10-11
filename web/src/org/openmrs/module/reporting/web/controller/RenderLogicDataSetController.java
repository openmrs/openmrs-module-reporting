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
 package org.openmrs.module.reporting.web.controller;

import javax.servlet.http.HttpSession;

import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.LazyPageableDataSet;
import org.openmrs.module.reporting.report.ReportData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RenderLogicDataSetController {

	@RequestMapping("/module/reporting/reports/renderLogicDataSet")
	public String showReport(Model model,
	                       HttpSession session,
	                       @RequestParam(required=false, value="start") Integer start,
	                       @RequestParam(required=false, value="size") Integer size) {
		if (start == null)
			start = 0;
		if (size == null || size < 0)
			size = 25;

		String renderArg = (String) session.getAttribute(ReportingConstants.OPENMRS_REPORT_ARGUMENT);
		ReportData data = null;
		try {
			data = (ReportData) session.getAttribute(ReportingConstants.OPENMRS_REPORT_DATA);
		} catch (ClassCastException ex) {
			// pass
		}
		if (data == null)
			return "redirect:../dashboard/index.form";

		LazyPageableDataSet dataSet = (LazyPageableDataSet) data.getDataSets().get(renderArg);
		
		int cohortSize = dataSet.getCohortSize();
		int startOfLast = cohortSize - cohortSize % size; 

		model.addAttribute("columns", dataSet.getMetaData());
		model.addAttribute("rows", dataSet.rowsForCohortSubset(start, size));
		model.addAttribute("start", start);
		model.addAttribute("size", size);
		model.addAttribute("startOfLast", startOfLast);
		model.addAttribute("cohortSize", cohortSize);
		return null;
	}
	
}
