package org.openmrs.module.reporting.web.reports;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.evaluation.parameter.Parameterizable;
import org.openmrs.module.util.ParameterizableUtil;
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
    public String saveMappedParameters(ModelMap model, HttpServletRequest request,
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
    	
    	for (Object p : request.getParameterMap().keySet()) {
    		System.out.println(p + ": " + Arrays.asList(request.getParameterMap().get(p)));
    	}
    	
    	return "redirect:reportEditor.form?uuid="+parentUuid;
    }
}
