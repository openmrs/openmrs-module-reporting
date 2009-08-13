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
package org.openmrs.module.report;

import java.util.Map;

import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.evaluation.EvaluationContext;

/**
 * Report Data obtained from evaluating a ReportDefinition with a given EvaluationContext.
 */
public class ReportData {
	
	private ReportDefinition reportDefinition;
	
	private EvaluationContext evaluationContext;
	
	@SuppressWarnings("unchecked")
	private Map<String, DataSet> dataSets;
	
	public ReportData() {
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, DataSet> getDataSets() {
		return dataSets;
	}
	
	@SuppressWarnings("unchecked")
	public void setDataSets(Map<String, DataSet> dataSets) {
		this.dataSets = dataSets;
	}
	
	/**
	 * @return Returns the EvaluationContext that was used to obtain this ReportData.
	 */
	public EvaluationContext getEvaluationContext() {
		return evaluationContext;
	}
	
	/**
	 * Saves the EvaluationContext that was used to obtain this ReportData.
	 * 
	 * @param evaluationContext
	 */
	public void setEvaluationContext(EvaluationContext evaluationContext) {
		this.evaluationContext = evaluationContext;
	}
	
	public ReportDefinition getReportDefinition() {
		return reportDefinition;
	}
	
	public void setReportDefinition(ReportDefinition reportDefinition) {
		this.reportDefinition = reportDefinition;
	}
	
}
