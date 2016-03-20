package org.openmrs.module.reporting.web.datasets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlwidgets.web.WidgetUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.ReflectionUtil;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.definition.configuration.Property;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ManageDatasetsController {

	protected Log log = LogFactory.getLog(this.getClass());

    /**
     * Controller for the edit data set page
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
}
