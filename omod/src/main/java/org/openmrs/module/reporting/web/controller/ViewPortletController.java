package org.openmrs.module.reporting.web.controller;

import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
public class ViewPortletController {

    private static final List<String> ACCEPTED_URLS = Arrays.asList(
            "baseCohortIndicator",
            "baseMetadata",
            "cohort",
            "cohortIndicatorAndDimensionSpecification",
            "currentReportHeader",
            "errorReports",
            "manageReportQueue",
            "mappedProperty",
            "multiParameterIterationParameterEdit",
            "parameter",
            "patientGraphs",
            "queuedReports",
            "reportDesignForm",
            "reportHistory",
            "reportList",
            "reportProcessorForm",
            "reportRequests",
            "runReport",
            "savedReports",
            "scheduledReports"
    );

    @RequestMapping("/module/reporting/viewPortlet.htm")
    public void view(Model model,
                       @RequestParam("id") String id,
                       @RequestParam("url") String url,
                       @RequestParam(value="parameters", required=false) String parameters) {

        if (Context.isAuthenticated() && ACCEPTED_URLS.contains(url)) {
            model.addAttribute("id", id);
            model.addAttribute("url", url);
            model.addAttribute("parameters", parameters);
        }
        else {
            throw new APIException("Error trying to view portlet at url: " + url);
        }

    }
	
}
