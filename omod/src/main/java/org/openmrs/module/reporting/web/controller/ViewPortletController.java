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

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

@Controller
public class ViewPortletController {

    private static final List<String> ACCEPTED_URLS = Arrays.asList(
            "baseCohortIndicator",
            "baseMetadata",
            "cohortIndicatorAndDimensionSpecification",
            "currentReportHeader",
            "errorReports",
            "manageReportQueue",
            "mappedProperty",
            "multiParameterIterationParameterEdit",
            "parameter",
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
    public void view(Model model, HttpServletRequest request,
                       @RequestParam("id") String id,
                       @RequestParam("url") String url,
                       @RequestParam(value="parameters", required=false) String parameters) {

        if (Context.isAuthenticated() && ACCEPTED_URLS.contains(url)) {
            model.addAttribute("id", id);
            model.addAttribute("url", url);

            // For backwards compatibility, we support passing in a pipe-delimited string of name=value paramaters
            StringBuilder paramConfig = new StringBuilder();
            if (StringUtils.isNotBlank(parameters)) {
                paramConfig.append(parameters);
            }

            // In later versions of Tomcat, the pipe is not an allowed character, so we support normal prefixed parameters
            Enumeration<String> requestParameterNames = request.getParameterNames();
            while (requestParameterNames.hasMoreElements()) {
                String requestParameterName = requestParameterNames.nextElement();
                if (requestParameterName.startsWith("parameters.")) {
                    String paramName = requestParameterName.substring(11);
                    String paramValue = request.getParameter(requestParameterName);
                    if (paramConfig.length() > 0) {
                        paramConfig.append("|");
                    }
                    paramConfig.append(paramName).append("=").append(ObjectUtil.nvlStr(paramValue, ""));
                }
            }

            model.addAttribute("parameters", paramConfig.toString());
        }
        else {
            throw new APIException("Error trying to view portlet at url: " + url);
        }

    }
	
}
