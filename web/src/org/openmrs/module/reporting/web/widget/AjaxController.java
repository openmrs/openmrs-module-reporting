package org.openmrs.module.reporting.web.widget;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.common.ReflectionUtil;
import org.openmrs.module.reporting.evaluation.EvaluationUtil;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

// I don't think this was ever completed, and I've created another AjaxController bean
// @Controller
public class AjaxController {

	protected static Log log = LogFactory.getLog(AjaxController.class);
    
    /**
     * Portlet Loading
     */
    @RequestMapping("/module/reporting/widget/mappedProperty")
    public void loadWidget(ModelMap model, HttpServletRequest request, HttpServletResponse response, 
		    		@RequestParam(required=true, value="id") String id,
		    		@RequestParam(required=true, value="type") Class<? extends Parameterizable> type,
		    		@RequestParam(required=true, value="property") String property,
		    		@RequestParam(required=false, value="currentKey") String currentKey,
		    		@RequestParam(required=false, value="uuid") String uuid,
		    		@RequestParam(required=false, value="mappedUuid") String mappedUuid) throws Exception {
    	
    	response.setContentType("text/html");
    	ServletOutputStream out = response.getOutputStream();
    	
    	// Get parent if uuid supplied
    	Parameterizable parent = null;
    	if (StringUtils.isNotEmpty(uuid)) {
    		parent = ParameterizableUtil.getParameterizable(uuid, type);
    	}

		// Get generic type of the Mapped property
		Field f = ReflectionUtil.getField(type, property);
		Class<?> fieldType = ReflectionUtil.getFieldType(f);
		boolean isList = List.class.isAssignableFrom(fieldType);
		boolean isMap = Map.class.isAssignableFrom(fieldType);
		
		Class<? extends Parameterizable> mappedType = ParameterizableUtil.getMappedType(type, property);
       	Parameterizable mappedChild = null;
       	Map<String, Object> mappings = new HashMap<String, Object>();

		if (StringUtils.isNotEmpty(uuid)) {
	       	if (StringUtils.isEmpty(mappedUuid)) {
	       		Mapped<Parameterizable> mapped = ParameterizableUtil.getMappedProperty(parent, property, currentKey);
	       		if (mapped != null) {
	       			mappedChild = mapped.getParameterizable();
	       			mappings = mapped.getParameterMappings();
	       		}
	       	}
	       	else if (mappedUuid != null) {
	       		mappedChild = ParameterizableUtil.getParameterizable(mappedUuid, mappedType);
	       	}
	       	
	       	Map<String, String> mappedParams = new HashMap<String, String>();
	       	Map<String, String> complexParams = new HashMap<String, String>();
	       	Map<String, String> fixedParams = new HashMap<String, String>();
	       	Map<String, Set<String>> allowedParams = new HashMap<String, Set<String>>();
       	
	       	if (mappedChild != null) {
				for (Parameter p : mappedChild.getParameters()) {
					Object mappedObjVal = mappings.get(p.getName());
					
					Set<String> allowed  = new HashSet<String>();
					for (Parameter parentParam : parent.getParameters()) {
						if (p.getType() == parentParam.getType()) {
							allowed.add(parentParam.getName());
						}
					}
					allowedParams.put(p.getName(), allowed);
					
					if (mappedObjVal != null && mappedObjVal instanceof String) {
						String mappedVal = (String) mappedObjVal;
						if (EvaluationUtil.isExpression(mappedVal)) {
							mappedVal = EvaluationUtil.stripExpression(mappedVal);
							if (parent.getParameter(mappedVal) != null) {
								mappedParams.put(p.getName(), mappedVal);
							}
							else {
								complexParams.put(p.getName(), mappedVal);
							}
						}
						else {
							fixedParams.put(p.getName(), mappedVal);
						}
					}
				}
	       	}
		}
		
		/*
		out.print(mappedType.getSimpleName() + ":" );
		Widget w = Widget

		<td><wgt:widget id="parameterizableSelector${model.id}" name="mappedUuid" type="${model.mappedType.name}" defaultValue="${model.mappedObj}"/></td>
	</tr>

		
		*/
		
    }
}
