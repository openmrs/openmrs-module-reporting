package org.openmrs.module.reporting.web.reports;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.evaluation.parameter.Parameterizable;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.service.ReportService;
import org.openmrs.module.util.ParameterizableUtil;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MappedEditor {

	protected static Log log = LogFactory.getLog(MappedEditor.class);
	
	/**
	 * Default Constructor
	 */
	public MappedEditor() { }

    /**
     * Retrieves either an existing or new report to edit
     */
    @RequestMapping("/module/reporting/reports/chooseParameterizable")
    public ModelMap chooseParameterizable(ModelMap model, HttpServletRequest request,
    		@RequestParam(required=true, value="parentType") Class<? extends Parameterizable> parentType,
    		@RequestParam(required=true, value="parentUuid") String parentUuid,
            @RequestParam(required=true, value="childType") Class<? extends Parameterizable> childType,
            @RequestParam(required=false, value="childUuid") String childUuid) {
    	
    	model.put("parentType", parentType);
    	Parameterizable parent = ParameterizableUtil.getParameterizable(parentUuid, parentType);
    	model.put("parentValue", parent);
    	
    	model.put("childType", childType);
    	Parameterizable child = ParameterizableUtil.getParameterizable(childUuid, childType);
    	model.put("childValue", child);
    	
        return model;
    }
    
    /**
     * Retrieves either an existing or new report to edit
     */
    @RequestMapping("/module/reporting/reports/mapParameters")
    public ModelMap mapParameters(ModelMap model, HttpServletRequest request,
    		@RequestParam(required=true, value="parentType") Class<? extends Parameterizable> parentType,
    		@RequestParam(required=true, value="parentUuid") String parentUuid,
            @RequestParam(required=true, value="childType") Class<? extends Parameterizable> childType,
            @RequestParam(required=false, value="childUuid") String childUuid) {
    	
    	model.put("parentType", parentType);
    	Parameterizable parent = ParameterizableUtil.getParameterizable(parentUuid, parentType);
    	model.put("parentValue", parent);
    	
    	model.put("childType", childType);
    	Parameterizable child = ParameterizableUtil.getParameterizable(childUuid, childType);
    	model.put("childValue", child);

        return model;
    }
    
    /**
     * Saves mapped parameters
     */
    @RequestMapping("/module/reporting/reports/saveMappedParameters")
    public ModelMap saveMappedParameters(ModelMap model, HttpServletRequest request,
    		@RequestParam(required=true, value="parentType") Class<? extends Parameterizable> parentType,
    		@RequestParam(required=true, value="parentUuid") String parentUuid,
            @RequestParam(required=true, value="childType") Class<? extends Parameterizable> childType,
            @RequestParam(required=false, value="childUuid") String childUuid) {
    	
    	Parameterizable parent = ParameterizableUtil.getParameterizable(parentUuid, parentType);
    	Parameterizable child = ParameterizableUtil.getParameterizable(childUuid, childType);
    	
    	System.out.println("Parent: " + parent);
    	System.out.println("Child: " + child);
    	
    	Map<String, String> params = new HashMap<String, String>();
    	for (Parameter p : child.getParameters()) {

    		String linkedParameter = request.getParameterValues("linkedParameter_"+p.getName())[0];
    		if (StringUtils.isNotEmpty(linkedParameter)) {
    			params.put(p.getName(), "${"+linkedParameter+"}");
    		}
    		else {
    			String[] fixedValue = request.getParameterValues("fixedValue_"+p.getName());
    			if (fixedValue !=null && fixedValue.length > 0) {
    				params.put(p.getName(), OpenmrsUtil.join(Arrays.asList(fixedValue), ","));
    			}
    		}
    	}
    	
    	// TODO: Replace parent/child with object/property model
    	// Hard-coding this now to confirm it works on report -> baseCohortDefinition
    	ReportDefinition rptDef = (ReportDefinition) parent;
    	CohortDefinition cohortDef = (CohortDefinition) child;
    	rptDef.setBaseCohortDefinition(new Mapped<CohortDefinition>(cohortDef, params));
    	Context.getService(ReportService.class).saveReportDefinition(rptDef);
    	
    	return model;
    }
}
