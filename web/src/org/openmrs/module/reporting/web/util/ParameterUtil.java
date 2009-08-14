package org.openmrs.module.reporting.web.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openmrs.annotation.Handler;
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
import org.openmrs.module.reporting.web.widget.handler.WidgetHandler;
import org.openmrs.module.reporting.web.widget.html.Option;
import org.openmrs.util.HandlerUtil;


public class ParameterUtil {

	/** 
	 * FIXME Not sure if this is where these methods belong.
	 */
	@SuppressWarnings("unchecked")
	public static List<Option> getSupportedCollectionTypes() {
		List<Option> collectionTypes = new ArrayList<Option>();
		collectionTypes.add(new Option(null, List.class.getSimpleName(), null, List.class.getName()));						
		collectionTypes.add(new Option(null, Set.class.getSimpleName(), null, Set.class.getName()));	
		return collectionTypes;
	}
	
	
	/** 
	 * FIXME Not sure if this is where these methods belong.
	 */
	public static List<Option> getSupportedTypes() {
		List<Option> ret = new ArrayList<Option>();
		for (WidgetHandler e : HandlerUtil.getHandlersForType(WidgetHandler.class, null)) {
			Handler handlerAnnotation = e.getClass().getAnnotation(Handler.class);
			if (handlerAnnotation != null) {
				Class<?>[] types = handlerAnnotation.supports();
				if (types != null) {
					for (Class<?> type : types) {						
						ret.add(new Option(null, type.getSimpleName(), null, type.getName()));						
					}
				}
			}
		}
		return ret;
	}
	
}