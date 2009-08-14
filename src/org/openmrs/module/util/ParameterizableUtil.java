package org.openmrs.module.util;

import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.parameter.Parameterizable;
import org.openmrs.module.indicator.Indicator;
import org.openmrs.module.indicator.service.IndicatorService;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.service.ReportService;


public class ParameterizableUtil {

	/**
	 * Retrieves a parameterizable with the given uuid and parameterizable class.
	 * 
	 * @param uuid
	 * @return
	 */
	public static Parameterizable getParameterizable(String uuid, Class<Parameterizable> parameterizableClass) { 
		
		if (DataSetDefinition.class.isAssignableFrom(parameterizableClass)) {
			return Context.getService(DataSetDefinitionService.class).getDataSetDefinitionByUuid(uuid);			
		} 
		else if (CohortDefinition.class.isAssignableFrom(parameterizableClass)) {
			return Context.getService(CohortDefinitionService.class).getCohortDefinitionByUuid(uuid);
		}
		else if (ReportDefinition.class.isAssignableFrom(parameterizableClass)) {
			return Context.getService(ReportService.class).getReportDefinitionByUuid(uuid);						
		}
		else if (Indicator.class.isAssignableFrom(parameterizableClass)) {
			return Context.getService(IndicatorService.class).getIndicatorByUuid(uuid);	
		}
		else { 
			throw new APIException("Unable to save parameterizable type " + parameterizableClass);
		}
		
	}


	/**
	 * Saves the given parameterizable.
	 * 
	 * @param parameterizable
	 * @return
	 */
	public static Parameterizable saveParameterizable(Parameterizable parameterizable) { 

		if (DataSetDefinition.class.isAssignableFrom(parameterizable.getClass())) {
			return Context.getService(DataSetDefinitionService.class).saveDataSetDefinition((DataSetDefinition)parameterizable);			
		} 
		else if (CohortDefinition.class.isAssignableFrom(parameterizable.getClass())) {
			return Context.getService(CohortDefinitionService.class).saveCohortDefinition((CohortDefinition)parameterizable);
		}
		else if (ReportDefinition.class.isAssignableFrom(parameterizable.getClass())) {
			return Context.getService(ReportService.class).saveReportDefinition((ReportDefinition)parameterizable);						
		}
		else if (Indicator.class.isAssignableFrom(parameterizable.getClass())) {
			return Context.getService(IndicatorService.class).saveIndicator((Indicator)parameterizable);	
		}
		else { 
			throw new APIException("Unable to save parameterizable type " + parameterizable.getClass());
		}
		//return parameterizable;
	}
	
}