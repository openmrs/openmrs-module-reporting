package org.openmrs.module.reporting.web.reports;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.evaluation.parameter.BaseParameterizable;
import org.openmrs.module.util.ParameterizableUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BaseParameterizableEditor {

	protected static Log log = LogFactory.getLog(BaseParameterizableEditor.class);
	
	/**
	 * Default Constructor
	 */
	public BaseParameterizableEditor() { }
    
    /**
     * Saves a BaseOpenmrsMetadata object
     */
    @RequestMapping("/module/reporting/reports/saveBaseParameterizable")
    public String saveBaseParameterizable(ModelMap model, HttpServletRequest request,
    		@RequestParam(required=true, value="type") Class<? extends BaseParameterizable> type,
    		@RequestParam(required=true, value="uuid") String uuid,
    		@RequestParam(required=true, value="name") String name,
    		@RequestParam(required=true, value="description") String description){
    	
    	BaseParameterizable p = null;
    	if (StringUtils.isNotEmpty(uuid)) {
    		p = (BaseParameterizable)ParameterizableUtil.getParameterizable(uuid, type);
    	}
    	else {
    		try {
    			p = type.newInstance();
    		}
    		catch (Exception e) {
    			throw new IllegalArgumentException("Cannot instantiate a new " + type, e);
    		}
    	}
    	p.setName(name);
    	p.setDescription(description);
    	ParameterizableUtil.saveParameterizable(p);
    	
    	return "redirect:/module/reporting/closeWindow.htm";
    }
}
