package org.openmrs.module.reporting.web.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.htmlwidgets.web.WidgetUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.ReflectionUtil;
import org.openmrs.module.reporting.definition.DefinitionContext;
import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.definition.configuration.Property;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EditAnnotatedDefinitionController {
	
	protected static Log log = LogFactory.getLog(EditAnnotatedDefinitionController.class);
	
    @ModelAttribute("definition")
    public Definition getDefinition(@RequestParam(required = false, value = "uuid") String uuid,
    								@RequestParam(required = false, value = "type") Class<? extends Definition> type) {
    	Definition d = null;
    	if (ObjectUtil.notNull(uuid)) {
    		d = DefinitionContext.getDefinitionByUuid(type, uuid);
    	}
    	if (d == null) {
    		try {
    			d = type.newInstance();
    		}
    		catch (Exception e) {
    			throw new IllegalArgumentException("Unable to create definition instance of type " + type);
    		}
    	}
		return d;
    }
	
	/**
	 * Basically acts as the formBackingObject() method for saving a CohortDefinition.
	 */
	@RequestMapping("/module/reporting/definition/editAnnotatedDefinition")
	public String editAnnotatedDefinition(ModelMap model, 
										  @RequestParam(required = true, value = "parentType") Class<? extends Definition> parentType,
										  @ModelAttribute("definition") Definition definition) {
		model.addAttribute("parentType", parentType);
		addPropertiesToModel(model, definition);
		return "/module/reporting/definition/editAnnotatedDefinition";
	}
	
	/**
	 * Saves a cohort definition.
	 */
	@RequestMapping("/module/reporting/definition/saveAnnotatedDefinition")
	@SuppressWarnings({"unchecked", "rawtypes"})
	public String saveAnnotatedDefinition(@RequestParam(required = true, value = "parentType") Class<? extends Definition> parentType,
										  @ModelAttribute("definition") Definition definition, 
            						      BindingResult bindingResult, ModelMap model, HttpServletRequest request) {
		
		definition.getParameters().clear();
		for (Property p : DefinitionUtil.getConfigurationProperties(definition)) {
			String fieldName = p.getField().getName();
			String prefix = "parameter." + fieldName;
			String valParamName = prefix + ".value";
			boolean isParameter = "t".equals(request.getParameter(prefix + ".allowAtEvaluation"));
			try {
				Object valToSet = WidgetUtil.getFromRequest(request, valParamName, p.getField());
				Class<? extends Collection<?>> collectionType = null;
				Class<?> fieldType = p.getField().getType();
				if (ReflectionUtil.isCollection(p.getField())) {
					collectionType = (Class<? extends Collection<?>>) p.getField().getType();
					fieldType = (Class<?>) ReflectionUtil.getGenericTypes(p.getField())[0];
				}
				
				if (isParameter) {
					//skip primitive types since they already have a default value
					if (!fieldType.isPrimitive()) {
						ReflectionUtil.setPropertyValue(definition, p.getField(), null);
					} else {
						//use the wrapper class type equivalent so that the WidgetTag doesn't fail
						fieldType = ReflectionUtil.getWrapperMap().get(fieldType.getName());
					}
					String paramLabel = ObjectUtil.nvlStr(request.getParameter(prefix + ".label"), fieldName);
					Properties widgetConfig = (Properties)WidgetUtil.getFromRequest(request, prefix+".widgetConfiguration", Properties.class, (Class)null);
					Parameter param = new Parameter(fieldName, paramLabel, fieldType, collectionType, valToSet, widgetConfig);
					definition.addParameter(param);
				} else {
					ReflectionUtil.setPropertyValue(definition, p.getField(), valToSet);
				}
			}
			catch (NumberFormatException e) {
				bindingResult.rejectValue(fieldName, "reporting.error.invalidNumber", "Only integers are allowed");
			}
		}
	
		// TODO: Add additional validation in
		
		if (bindingResult.hasErrors()) {
			addPropertiesToModel(model, definition);
			model.addAttribute("errors", bindingResult);
			return "/module/reporting/definition/editAnnotatedDefinition";
		}
		
		if ("".equals(definition.getUuid())) {
			definition.setUuid(null);
		}
		
		log.warn("Saving: " + definition);
		DefinitionContext.saveDefinition(definition);
		
		return "redirect:/module/reporting/definition/manageDefinitions.form?type=" + parentType.getName();
	}
	
	private void addPropertiesToModel(ModelMap model, Definition definition) {
		List<Property> properties = DefinitionUtil.getConfigurationProperties(definition);
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
	}
}
