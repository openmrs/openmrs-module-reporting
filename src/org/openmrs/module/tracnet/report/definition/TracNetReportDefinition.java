package org.openmrs.module.tracnet.report.definition;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.ConceptSet;
import org.openmrs.Drug;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.indicator.util.IndicatorUtil;
import org.openmrs.module.report.PeriodIndicatorReportDefinition;
import org.openmrs.module.report.PeriodIndicatorReportUtil;
import org.openmrs.module.reporting.ReportingConstants;


/**
 * A thin wrapper around a ReportDefinition that gives it startDate, endDate, and location parameters,
 * and a single {@link CohortIndicatorDataSetDefinition} by default.
 * 
 * @see CohortIndicatorDataSetDefinition
 * @see PeriodIndicatorReportUtil
 */
public class TracNetReportDefinition extends PeriodIndicatorReportDefinition {
	
	public static final String DEFAULT_DATASET_KEY = "tracNetDataSet";
	
	/**
	 * Contructor - do not call super()
	 */
	public TracNetReportDefinition() {
		// a single CohortIndicatorDataSetDefinition
		setupDataSetDefinition();
	}
		
	
	/**
	 * Ensure this report has a data set definition
	 */
	public void setupDataSetDefinition() {
		// Create new dataset definition 
		CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
		dataSetDefinition.setName(getName() + " Data Set");
		dataSetDefinition.addParameter(ReportingConstants.START_DATE_PARAMETER);
		dataSetDefinition.addParameter(ReportingConstants.END_DATE_PARAMETER);
		
		// Add dataset definition to report definition
		addDataSetDefinition(DEFAULT_DATASET_KEY, dataSetDefinition, IndicatorUtil.getDefaultParameterMappings());
    }
	

	/**
	 * 
	 * @param drugSetName	represents the drug set (e.g. ANTIRETROVIRAL DRUGS)
	 * @return
	 */
	// 
	public static List<Drug> getDrugsByDrugSetName(String drugSetName) { 		
		List<Drug> firstLineDrugs = new ArrayList<Drug>();
		Concept arvDrugs = Context.getConceptService().getConceptByName(drugSetName);
		List<ConceptSet> drugSets = Context.getConceptService().getConceptSetsByConcept(arvDrugs);				
	    if (drugSets != null) {
	    	for (ConceptSet drugSet : drugSets) {	
	    		List<Drug> drugs = Context.getConceptService().getDrugsByConcept(drugSet.getConcept());
	    		if (drugs != null)
	    			firstLineDrugs.addAll(drugs);
			}
	    }
	    return firstLineDrugs;
	}			
	
	/**
	 * 
	 * @param drugName	represents the actual drug (e.g LOPINAVIR AND RITONAVIR)
	 * @return
	 */
	public static List<Drug> getDrugByConceptName(String conceptName) { 		
		Concept secondLineDrugConcept = Context.getConceptService().getConceptByName(conceptName);
		return Context.getConceptService().getDrugsByConcept(secondLineDrugConcept);
	}
	
	
}
