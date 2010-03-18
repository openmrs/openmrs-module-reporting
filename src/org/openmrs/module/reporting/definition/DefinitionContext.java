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
package org.openmrs.module.reporting.definition;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.dimension.service.DimensionService;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;

/**
 * Provides convenient access to common Definition Services
 */
public class DefinitionContext {
	
	protected static Log log = LogFactory.getLog(DefinitionContext.class);
	private static List<DefinitionService<?>> definitionServices;
	
	/**
	 * @returns the DefinitionService registered for the passed Definition type
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Definition> DefinitionService<T> getDefinitionService(Class<T> definitionType) {
		for (DefinitionService<?> d : Context.getRegisteredComponents(DefinitionService.class)) {
			if (d.getDefinitionType().isAssignableFrom(definitionType)) {
				return (DefinitionService<T>) d;
			}
		}
		throw new IllegalArgumentException("No DefinitionService found that is registered for type " + definitionType);
	}
	
	/**
	 * @return the Definition with the given uuid
	 * @see DefinitionService#getDefinitionByUuid(String)
	 */
	public static <T extends Definition> T getDefinitionByUuid(Class<T> definitionType, String uuid) {
		return getDefinitionService(definitionType).getDefinitionByUuid(uuid);
	}
	
	/**
	 * @return all definitions of the passed type, including retired if specified by the includeRetired parameter
	 * @see DefinitionService#getAllDefinitions(boolean)
	 */
	public static <T extends Definition> List<T> getAllDefinitions(Class<T> definitionType, boolean includeRetired) {
		return getDefinitionService(definitionType).getAllDefinitions(includeRetired);
	}

	/**
	 * Saves the passed Definition
	 * @see DefinitionService#saveDefinition(Definition)
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Definition> T saveDefinition(T definition) {
		Class<T> c = (Class<T>)definition.getClass();
		return getDefinitionService(c).saveDefinition(definition);
	}
	
	/**
	 * Evaluates the passed Mapped Definition for the given EvaluationContext
	 * @see DefinitionService#evaluate(Mapped<Definition>, EvaluationContext)
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Definition> Evaluated<T> evaluate(Mapped<? extends T> definition, EvaluationContext context) {
		Class<T> c = (Class<T>)definition.getClass();
		return getDefinitionService(c).evaluate(definition, context);
	}
	
	/**
	 * Evaluates the passed Definition for the given EvaluationContext
	 * @see DefinitionService#evaluate(Definition, EvaluationContext)
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Definition> Evaluated<T> evaluate(T definition, EvaluationContext context) {
		Class<T> c = (Class<T>)definition.getClass();
		return getDefinitionService(c).evaluate(definition, context);
	}
	
	/**
	 * @return the ReportDefinitionService
	 */
	public static ReportDefinitionService getReportDefinitionService() {
		return Context.getService(ReportDefinitionService.class);
	}
	
	/**
	 * @return the DataSetDefinitionService
	 */
	public static DataSetDefinitionService getDataSetDefinitionService() {
		return Context.getService(DataSetDefinitionService.class);
	}
	
	/**
	 * @return the IndicatorService
	 */
	public static IndicatorService getIndicatorService() {
		return Context.getService(IndicatorService.class);
	}
	
	/**
	 * @return the DimensionService
	 */
	public static DimensionService getDimensionService() {
		return Context.getService(DimensionService.class);
	}
	
	/**
	 * @return the CohortDefinitionService
	 */
	public static CohortDefinitionService getCohortDefinitionService() {
		return Context.getService(CohortDefinitionService.class);
	}

	/**
	 * @return the definitionServices
	 */
	public List<DefinitionService<?>> getDefinitionServices() {
		if (definitionServices == null) {
			definitionServices = new ArrayList<DefinitionService<?>>();
		}
		return definitionServices;
	}

	/**
	 * @param definitionServices the definitionServices to set
	 * NOTE, THIS ADDS, IT DOES NOT SET
	 */
	public void setDefinitionServices(List<DefinitionService<?>> definitionServices) {
		getDefinitionServices().addAll(definitionServices);
	}
	
	/**
	 * @param definitionService the definitionService to add
	 */
	public void addDefinitionService(DefinitionService<?> definitionService) {
		getDefinitionServices().add(definitionService);
	}
}
