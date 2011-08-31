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
package org.openmrs.module.reporting.web.model;

import java.util.List;

import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

/**
 * Indicator report bean 
 */
public class IndicatorReportForm {
	
    public static final long serialVersionUID = 1L;
    
    //***** PROPERTIES *****
    List<Indicator> indicators = null;    
    ReportDefinition reportDefinition = null;
    
    
    //***** CONSTRUCTORS *****
    public IndicatorReportForm() { }


	public List<Indicator> getIndicators() {
		return indicators;
	}


	public void setIndicators(List<Indicator> indicators) {
		this.indicators = indicators;
	}


	public ReportDefinition getReportDefinition() {
		return reportDefinition;
	}


	public void setReportDefinition(ReportDefinition reportDefinition) {
		this.reportDefinition = reportDefinition;
	}

    
    
    
    
    

}