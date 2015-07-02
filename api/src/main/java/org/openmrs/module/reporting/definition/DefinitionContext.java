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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.dimension.service.DimensionService;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;

import java.util.Date;
import java.util.List;

/**
 * Provides convenient access to common Definition Services
 */
public class DefinitionContext {
	
	protected static Log log = LogFactory.getLog(DefinitionContext.class);
	
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
	 * Purges a Definition by uuid for the given type
	 */
	public static <T extends Definition> void purgeDefinition(Class<T> definitionType, String uuid) {
		getDefinitionService(definitionType).purgeDefinition(getDefinitionByUuid(definitionType, uuid));
	}

	/**
	 * Retires a Definition
	 */
	public static void retireDefinition(Definition definition) {
		definition.setRetired(true);
		definition.setRetiredBy(Context.getAuthenticatedUser());
		definition.setDateRetired(new Date());
		saveDefinition(definition);
	}

	/**
	 * Restores a previously retired Definition
	 */
	public static void unretireDefinition(Definition definition) {
		definition.setRetired(false);
		definition.setRetiredBy(null);
		definition.setDateRetired(null);
		saveDefinition(definition);
	}
	
	/**
	 * Evaluates the passed Mapped Definition for the given EvaluationContext
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Definition> Evaluated<T> evaluate(Mapped<? extends T> definition, EvaluationContext context) throws EvaluationException {
		Class<T> c = (Class<T>)definition.getParameterizable().getClass();
		return getDefinitionService(c).evaluate(definition, context);
	}
	
	/**
	 * Evaluates the passed Definition for the given EvaluationContext
	 * @see DefinitionService#evaluate(Definition, EvaluationContext)
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Definition> Evaluated<T> evaluate(T definition, EvaluationContext context) throws EvaluationException {
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
}
