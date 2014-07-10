package org.openmrs.module.reporting.web.datasets;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlwidgets.web.WidgetUtil;
import org.openmrs.module.reporting.cohort.Cohorts;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.ReflectionUtil;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.definition.configuration.Property;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.CsvReportRenderer;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.renderer.SimpleHtmlReportRenderer;
import org.openmrs.module.reporting.report.renderer.TsvReportRenderer;
import org.openmrs.module.reporting.report.renderer.XlsReportRenderer;
import org.openmrs.module.reporting.report.renderer.XmlReportRenderer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ManageDatasetsController {

	protected Log log = LogFactory.getLog(this.getClass());

    /**
     * Shows a dataset definition given a uuid (or an id and class name). 
     * 
     * @param id
     * 			The identifier of the dataset definition
     * @param uuid
     * 			The universally unique identifier of the dataset definition
     * @param className
     * 			The class name of the dataset to be created/retrieved
     * @param cohortSize
     * 			The size of the cohort used to preview the dataset
     * @param action
     * 			The action to be taken
     * @param model
     * 			The model map used to hold the dataset definition and preview dataset
     * @return
     * 		The name of the JSP to present to the user
     */
	@RequestMapping("/module/reporting/datasets/editDataSet")
    public String editDataSet(
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam(required=false, value="type") Class<? extends DataSetDefinition> type,
            @RequestParam(required=false, value="cohortSize") Integer cohortSize,
            @RequestParam(required=false, value="action") String action,
    		ModelMap model) {
	
		DataSetDefinitionService service = Context.getService(DataSetDefinitionService.class);
		DataSetDefinition dsd = service.getDefinition(uuid, type);
	 	model.addAttribute("dataSetDefinition", dsd);
	
	 	List<Property> properties = DefinitionUtil.getConfigurationProperties(dsd);
	 	model.addAttribute("configurationProperties", properties);
	 	Map<String, List<Property>> groups = new LinkedHashMap<String, List<Property>>();
	 	for (Property p : properties) {
	 		List<Property> l = groups.get(p.getGroup());
	 		if (l == null) {
	 			l = new ArrayList<Property>();
	 			groups.put(p.getGroup(), l);
	 		}
	 		l.add(p);
	 	}
	 	model.addAttribute("groupedProperties", groups);
	
	    return "/module/reporting/datasets/datasetEditor";
    }
    
	/**
	 * Purges a data set from the database
	 */
	@RequestMapping("/module/reporting/datasets/removeDataSet")
    public String removeDataSet(
    		@RequestParam(required=false, value="id") Integer id,
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam(required=false, value="type") String type,
    		ModelMap model) {

    	DataSetDefinition dataSetDefinition = getDataSetDefinition(uuid, type, id);
    	if (dataSetDefinition != null) {
    		Context.getService(DataSetDefinitionService.class).purgeDefinition(dataSetDefinition);
    	}
    	return "redirect:/module/reporting/definition/manageDefinitions.form?type=org.openmrs.module.reporting.dataset.definition.DataSetDefinition";    	    	
    } 
    
    /**
     * Save DataSetDefinition
     */
    @RequestMapping("/module/reporting/datasets/saveDataSet")
    @SuppressWarnings("unchecked")
    public String saveDataSet(
    		@RequestParam(required=false, value="uuid") String uuid,
            @RequestParam(required=false, value="type") Class<? extends DataSetDefinition> type,
            @RequestParam(required=true, value="name") String name,
            @RequestParam("description") String description,
            HttpServletRequest request,
    		ModelMap model
    ) {
    	
    	DataSetDefinitionService service = Context.getService(DataSetDefinitionService.class);
    	DataSetDefinition dataSetDefinition = service.getDefinition(uuid, type);
    	dataSetDefinition.setName(name);
    	dataSetDefinition.setDescription(description);
    	dataSetDefinition.getParameters().clear();
    	
    	for (Property p : DefinitionUtil.getConfigurationProperties(dataSetDefinition)) {
    		String fieldName = p.getField().getName();
    		String prefix = "parameter." + fieldName;
    		String valParamName =  prefix + ".value"; 
    		boolean isParameter = "t".equals(request.getParameter(prefix+".allowAtEvaluation"));
    		
    		Object valToSet = WidgetUtil.getFromRequest(request, valParamName, p.getField());
    		
    		Class<? extends Collection<?>> collectionType = null;
    		Class<?> fieldType = p.getField().getType();   		
			if (ReflectionUtil.isCollection(p.getField())) {
				collectionType = (Class<? extends Collection<?>>)p.getField().getType();
				fieldType = (Class<?>)ReflectionUtil.getGenericTypes(p.getField())[0];
			}
			
			if (isParameter) {
				ReflectionUtil.setPropertyValue(dataSetDefinition, p.getField(), null);
				String paramLabel = ObjectUtil.nvlStr(request.getParameter(prefix + ".label"), fieldName);
				Parameter param = new Parameter(fieldName, paramLabel, fieldType, collectionType, valToSet);
				dataSetDefinition.addParameter(param);
			}
			else {
				ReflectionUtil.setPropertyValue(dataSetDefinition, p.getField(), valToSet);
			}
    	}
    	
    	log.debug("Saving: " + dataSetDefinition);
    	Context.getService(DataSetDefinitionService.class).saveDefinition(dataSetDefinition);

        return "redirect:/module/reporting/definition/manageDefinitions.form?type=org.openmrs.module.reporting.dataset.definition.DataSetDefinition";
    }
    
    
    /**
     * View Data Set
     * @throws EvaluationException 
     */
	@RequestMapping("/module/reporting/datasets/viewDataSet")
    public void viewDataset(
    		@RequestParam(required=false, value="id") Integer id,
            @RequestParam(required=false, value="type") String type,
            @RequestParam(required=false, value="cohortId") String cohortId,
            @RequestParam(required=false, value="dataSetId") String dataSetId,
            @RequestParam(required=false, value="indicatorId") String indicatorId,
            @RequestParam(required=false, value="limit") Integer limit,
    		ModelMap model
     	) throws EvaluationException {

	    	
    	// Step 1 
    	// Find the dataset definition and if it's still null, 
       	// then we should use a default dataset definition
       	DataSetDefinition dataSetDefinition = getDataSetDefinition(dataSetId, type, id);    	
    	model.addAttribute("dataSetDefinition", dataSetDefinition);

    	// Step 2 
    	// If we have both the cohort and the dataset, we should display the evaluated dataset
    	// Find the cohort definition and evaluate the base cohort
		// Set the cohort to use when evaluating the dataset
	    if (cohortId != null && dataSetId != null) { 
    		Cohort cohort = evaluateCohort(cohortId);
	    	    		
    		log.debug("evaluated cohort: " + cohort.size());
    		
    		model.addAttribute("cohort", cohort);
	    	model.addAttribute("cohortDefinition", 
	    			Context.getService(CohortDefinitionService.class).getDefinitionByUuid(cohortId));    		    	

	    	// If we don't have a cohort yet, just get all patients
	    	if (cohort == null || cohortId.equals("0")) 
	    		cohort = Cohorts.allPatients(null);

	    	
	    	// Step 3 Set up the evaluation context
	    	EvaluationContext context = new EvaluationContext();
	    	context.setLimit(limit);
	    	context.setBaseCohort(cohort);	 
	    	
    		log.debug("just before dataset evaluation: " + context.getBaseCohort().size());

    		// Step 4 Evaluate the dataset
	    	model.addAttribute("dataSet", 
	    			Context.getService(DataSetDefinitionService.class).evaluate(dataSetDefinition, context));
	    	
	    	// TODO Put dataset in the session so we can download it later
	    	
    	}
    	
    	// The following two attributes should ALWAYS be added to the request 
    	
    	// Add all cohort definition to the request (allow user to choose)
    	model.addAttribute("cohortDefinitions", 
    			Context.getService(CohortDefinitionService.class).getAllDefinitions(false)); 

    	// Add all dataset definition to the request (allow user to choose)
    	model.addAttribute("dataSetDefinitions", 
    			Context.getService(DataSetDefinitionService.class).getAllDefinitions(false)); 
    }    
    
    /**
     * 
     * @param model
     * @return
     */
	@RequestMapping("/module/reporting/datasets/downloadDataSet")
    public void downloadDataset(
    		@RequestParam(required=false, value="id") Integer id,
            @RequestParam(required=false, value="cohortId") String cohortId,
            @RequestParam(required=false, value="dataSetId") String dataSetId,
            @RequestParam(required=false, value="type") String type,
            @RequestParam(required=false, value="format") String format,
            @RequestParam(required=false, value="limit") Integer limit,
            @RequestParam(required=false, value="username") String username,
            @RequestParam(required=false, value="password") String password,
            HttpServletResponse response) {
    	 
       	try { 
       		// Hack to allow external client to download data 
       		// (need to find better solution for this)
       		if (username != null && password != null) 
       			Context.authenticate(username, password);
       		
       		// Step 1 
       		// Retrieve the dataset definition
           	DataSetDefinition dataSetDefinition = getDataSetDefinition(dataSetId, type, id); 
           	if (dataSetDefinition == null)
           		throw new APIException("The dataset definition that you selected could not be found.");           	
    		
    		// Step 2
    		// Evaluate cohort
    		Cohort baseCohort = evaluateCohort(cohortId);

           	// Step 3
           	// Create evaluation context for running report
    		EvaluationContext context = new EvaluationContext();
    		context.setBaseCohort(baseCohort);	 
    		context.setLimit(limit);
    			    
    		// Step 4
	    	// Evaluate dataset report
	    	ReportDefinition reportDefinition = new ReportDefinition();
	    	reportDefinition.addDataSetDefinition(dataSetDefinition.getName(), dataSetDefinition, null);
	    	ReportData reportData = Context.getService(ReportDefinitionService.class).evaluate(reportDefinition, context);

	    	// Step 5 
	    	// Render using one of the given formats
	    	// FIXME This should be taken care of by the rendering handler framework
	    	ReportRenderer renderer = new CsvReportRenderer();
	    	if ("xml".equalsIgnoreCase(format)) { 
	    		renderer = new XmlReportRenderer();
	    	} else if ("tsv".equalsIgnoreCase(format)) { 
	    		renderer = new TsvReportRenderer();
	    	} else if ("xls".equalsIgnoreCase(format)) { 
	    		renderer = new XlsReportRenderer();
	    	} else if ("html".equalsIgnoreCase(format)) { 
	    		renderer = new SimpleHtmlReportRenderer();
	    	}
	    	
	    	// Step 6
	    	// Write rendered data to response
            RenderingMode renderingMode = new RenderingMode(renderer, "", "", 0);
            ReportRequest request = new ReportRequest(Mapped.noMappings(reportDefinition), null, renderingMode, ReportRequest.Priority.NORMAL, null);
            response.setContentType(renderer.getRenderedContentType(request));
			response.setHeader("Content-Disposition", "attachment; filename=\"" + dataSetDefinition.getName() + "." + format + "\"");  	    	
	    	renderer.render(reportData, null, response.getOutputStream());
	    	response.getOutputStream().close();
       	} 
       	catch (Exception e) { 
       		log.error("Exception ocurred while downloading dataset ", e);
       	}
    }
    
    /**
     * Retrieve DataSetDefinition
     */
    public DataSetDefinition getDataSetDefinition(String uuid, String className, Integer id) {     	
    	DataSetDefinitionService service = Context.getService(DataSetDefinitionService.class);
    	
    	DataSetDefinition dataSetDefinition = null;
    	if (uuid != null) { 
			log.debug("Retrieving dataset definition by uuid " + uuid);
    		dataSetDefinition = service.getDefinitionByUuid(uuid);    	
    	}

    	
    	log.debug("Dataset definition: " + dataSetDefinition);

    	// FIXME This is used because we current cannot use UUIDs to locate data export dataset definitions
    	// If we cannot find the dataset by uuid or if no uuid was specified, then try to retrieve it by ID
    	if (dataSetDefinition == null || dataSetDefinition.getUuid() == null) {     		
			try {
				if (className != null) { 
					@SuppressWarnings("unchecked")
					Class<? extends DataSetDefinition> type = (Class<? extends DataSetDefinition>) Context.loadClass(className);
					dataSetDefinition = service.getDefinition(type, id); 
				}
			} 
			catch (ClassNotFoundException e) {
				log.error("Unable to retrieve dataset definition by id and type: ", e);
			}    	
    	}
    	return dataSetDefinition;    	
    } 
    
    /**
     * Evaluates a cohort 
     * TODO Move to service layer 
     * @throws EvaluationException 
     */
    public Cohort evaluateCohort(String uuid) throws EvaluationException {
    	EvaluationContext evaluationContext = new EvaluationContext();
        Cohort cohort = Cohorts.allPatients(evaluationContext);
        evaluationContext.setBaseCohort(cohort);
    	CohortDefinition cohortDefinition = null;    	
    	if (StringUtils.isNotEmpty(uuid)) {
    		cohortDefinition =
    			Context.getService(CohortDefinitionService.class).getDefinitionByUuid(uuid);
    		if (cohortDefinition != null) {
    			try {
	    			cohort =
	    				Context.getService(CohortDefinitionService.class).evaluate(cohortDefinition, evaluationContext);
    			} catch (Exception ex) {
    				throw new EvaluationException("cohort", ex);
    			}
    		}
    	}
    	return cohort;
    } 
}
