package org.openmrs.module.reporting.web.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.definition.DefinitionContext;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.web.controller.mapping.DefinitionMappingHandler;
import org.openmrs.util.HandlerUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ManageDefinitionsController {
	
	protected static Log log = LogFactory.getLog(ManageDefinitionsController.class);
	
	/**
	 * Manage Definitions Controller
	 */
    @RequestMapping("/module/reporting/definition/manageDefinitions")
    public void manageDefinitions(
    		@RequestParam(required=true, value="type") Class<? extends Definition> type,
    		@RequestParam(required=false, value="includeRetired") Boolean includeRetired,
    		ModelMap model) {
    	
    	// Get all Definitions
    	boolean retired = includeRetired != null && includeRetired.booleanValue();
    	List<? extends Definition> definitions = DefinitionContext.getAllDefinitions(type, retired);
    	
    	// Construct a Map of Definition Type to List of Definitions
    	Map<Class<? extends Definition>, List<Definition>> defsByType = 
    		new TreeMap<Class<? extends Definition>, List<Definition>>(new DefinitionNameComparator());
    	
    	// Initialize the Map with all known supported types
    	for (Class<? extends Definition> supportedType : DefinitionContext.getDefinitionService(type).getDefinitionTypes()) {
    		defsByType.put(supportedType, new ArrayList<Definition>());
    	}
    	
    	// Add all saved Definitions to the Map
    	for (Definition d : definitions) {
    		List<Definition> l = defsByType.get(d.getClass());
    		if (l == null) {
    			l = new ArrayList<Definition>();
    			defsByType.put(d.getClass(), l);
    		}
    		l.add(d);
    	}
    	
    	model.addAttribute("type", type);
    	model.addAttribute("definitions", defsByType);
    	model.addAttribute("allDefinitions", definitions);
    }
    
	/**
	 * Edit Definition Controller
	 */
    @RequestMapping("/module/reporting/definition/editDefinition")
    public String editDefinition(
    		@RequestParam(required=true, value="type") Class<? extends Definition> type,
    		@RequestParam(required=false, value="uuid") String uuid,
    		ModelMap model) {
    	
    	DefinitionMappingHandler handler = HandlerUtil.getPreferredHandler(DefinitionMappingHandler.class, type);
    	if (ObjectUtil.isNull(uuid)) {
    		return "redirect:" + handler.getCreateUrl(type);
    	}
    	else {
    		Definition d = DefinitionContext.getDefinitionService(type).getDefinitionByUuid(uuid);
    		return "redirect:" + handler.getEditUrl(d);
    	}
    }
    
	/**
	 * Purge Definition Controller
	 */
    @RequestMapping("/module/reporting/definition/purgeDefinition")
    public String purgeDefinition(
    		@RequestParam(required=true, value="type") Class<? extends Definition> type,
    		@RequestParam(required=true, value="uuid") String uuid,
    		ModelMap model) {

    	DefinitionContext.purgeDefinition(type, uuid);
    	return "redirect:manageDefinitions.form?type="+type.getName();
    }
    
	/**
	 * Retire Definition Controller
	 */
    @RequestMapping("/module/reporting/definition/retireDefinition")
    public String retireDefinition(
    		@RequestParam(required=true, value="type") Class<? extends Definition> type,
    		@RequestParam(required=true, value="uuid") String uuid,
    		ModelMap model) {

    	Definition d = DefinitionContext.getDefinitionService(type).getDefinitionByUuid(uuid);
    	DefinitionContext.retireDefinition(d);
    	return "redirect:manageDefinitions.form?type="+type.getName();
    }
    
	/**
	 * Retire Definition Controller
	 */
    @RequestMapping("/module/reporting/definition/unretireDefinition")
    public String unretireDefinition(
    		@RequestParam(required=true, value="type") Class<? extends Definition> type,
    		@RequestParam(required=true, value="uuid") String uuid,
    		ModelMap model) {

    	Definition d = DefinitionContext.getDefinitionService(type).getDefinitionByUuid(uuid);
    	DefinitionContext.unretireDefinition(d);
    	return "redirect:manageDefinitions.form?type="+type.getName();
    }
    
    /**
     * Comparator which orders Definitions based on their Display Label
     */
    public class DefinitionNameComparator implements Comparator<Class<? extends Definition>> {
		/**
		 * @see Comparator#compare(Object, Object)
		 */
		public int compare(Class<? extends Definition> o1, Class<? extends Definition> o2) {
			String key1 = MessageUtil.getDisplayLabel(o1);
			String key2 = MessageUtil.getDisplayLabel(o2);
			return key1.compareTo(key2);
		}
    }
}
