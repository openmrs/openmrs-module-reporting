package org.openmrs.module.reporting.report.definition.service;

import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.definition.DefinitionSummary;
import org.openmrs.module.reporting.definition.service.BaseDefinitionService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.definition.service.SerializedDefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.Status;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Base Implementation of the ReportService API
 */
public class ReportDefinitionServiceImpl extends BaseDefinitionService<ReportDefinition> implements ReportDefinitionService {
	
	/**
	 * @see DefinitionService#getDefinitionType()
	 */
	public Class<ReportDefinition> getDefinitionType() {
		return ReportDefinition.class;
	}
	
	/**
	 * @see DefinitionService#getDefinitionTypes()
	 */
	public List<Class<? extends ReportDefinition>> getDefinitionTypes() {
		List<Class<? extends ReportDefinition>> ret = new ArrayList<Class<? extends ReportDefinition>>();
		ret.add(ReportDefinition.class);
		ret.add(PeriodIndicatorReportDefinition.class);
		return ret;
	}
	
	/**
	 * @see ReportDefinitionService#getDefinition(Integer)
	 */
	@Transactional(readOnly=true)
	public ReportDefinition getDefinition(Integer id) {
		return getService().getDefinition(ReportDefinition.class, id);
	}

	/**
	 * @see DefinitionService#getDefinition(Class, Integer)
	 */
	@Transactional(readOnly=true)
	public <D extends ReportDefinition> D getDefinition(Class<D> type, Integer id) throws APIException {
		return getService().getDefinition(type, id);
	}
	
	/**
	 * @see DefinitionService#getDefinitionByUuid(String)
	 */
	@Transactional(readOnly=true)
	public ReportDefinition getDefinitionByUuid(String uuid) throws APIException {
		return getService().getDefinitionByUuid(ReportDefinition.class, uuid);
	}
	
	/**
	 * @see DefinitionService#getAllDefinitions(boolean)
	 */
	@Transactional(readOnly=true)
	public List<ReportDefinition> getAllDefinitions(boolean includeRetired) {
		return getService().getAllDefinitions(ReportDefinition.class, includeRetired);
	}
	
	/**
	 * 
	 * @see BaseDefinitionService#getAllDefinitionSummaries(boolean)
	 */
	@Transactional(readOnly=true)
	public List<DefinitionSummary> getAllDefinitionSummaries(boolean includeRetired) {
	    return getService().getAllDefinitionSummaries(ReportDefinition.class, includeRetired);
	}
	
	/**
	 * @see DefinitionService#getNumberOfDefinitions(boolean)
	 */
	@Transactional(readOnly=true)
	public int getNumberOfDefinitions(boolean includeRetired) {
		return getService().getNumberOfDefinitions(ReportDefinition.class, includeRetired);
	}

	/**
	 * @see DefinitionService#getDefinitions(String, boolean)
	 */
	@Transactional(readOnly=true)
	public List<ReportDefinition> getDefinitions(String name, boolean exactMatchOnly) {
		return getService().getDefinitions(ReportDefinition.class, name, exactMatchOnly);
	}

	/**
	 * @see DefinitionService#saveDefinition(Definition)
	 */
	@Transactional
	public <D extends ReportDefinition> D saveDefinition(D definition) throws APIException {
		return getService().saveDefinition(definition);
	}
	
	/**
	 * @see DefinitionService#purgeDefinition(Definition)
	 * @should purge report designs
	 */
	@Transactional
	public void purgeDefinition(ReportDefinition definition) {
		ReportService reportService = Context.getService(ReportService.class);
		
		for (ReportRequest request : reportService.getReportRequests(definition, null, null, (Status[])null)) {
			reportService.purgeReportRequest(request);
		}
		
		List<ReportDesign> reportDesigns = reportService.getReportDesigns(definition, null, true);
		for (ReportDesign reportDesign : reportDesigns) {
			reportService.purgeReportDesign(reportDesign);
        }
		
		getService().purgeDefinition(definition);
	}
	
	/** 
	 * @see ReportDefinitionService#evaluate(Mapped, EvaluationContext)
	 */
	@Override
	public ReportData evaluate(Mapped<? extends ReportDefinition> definition, EvaluationContext context) throws EvaluationException {
		return (ReportData) super.evaluate(definition, context);
	}
	
	/**
	 * @see ReportDefinitionService#evaluate(ReportDefinition, EvaluationContext)
	 */
	@SuppressWarnings("unchecked")
	public ReportData evaluate(ReportDefinition definition, EvaluationContext context) throws EvaluationException {
		return (ReportData) super.evaluate(definition, context);
	}
	
	/**
	 * Utility method that returns the SerializedDefinitionService
	 */
	protected SerializedDefinitionService getService() {
		return Context.getService(SerializedDefinitionService.class);
	}
}