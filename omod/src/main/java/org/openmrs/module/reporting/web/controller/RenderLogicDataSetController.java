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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.DataSetRow;
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
		// the c:forEach tag is supposed to handle an iterator as its 'items', but that doesn't seem
		// to work, so I'm converting this to a List instead
		model.addAttribute("rows", iteratorToList(dataSet.rowsForCohortSubset(start, size)));
		model.addAttribute("start", start);
		model.addAttribute("size", size);
		model.addAttribute("startOfLast", startOfLast);
		model.addAttribute("cohortSize", cohortSize);
		return null;
	}

	private List<DataSetRow> iteratorToList(Iterator<DataSetRow> iterator) {
	    List<DataSetRow> ret = new ArrayList<DataSetRow>();
	    while (iterator.hasNext())
	    	ret.add(iterator.next());
	    return ret;
    }
	
}
