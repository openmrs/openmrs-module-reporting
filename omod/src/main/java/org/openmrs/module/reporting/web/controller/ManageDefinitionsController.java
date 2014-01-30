package org.openmrs.module.reporting.web.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.PatientToEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.PersonToEncounterDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataSetDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PersonToPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeAtDateOfOtherDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.MultiPeriodIndicatorDataSetDefinition;
import org.openmrs.module.reporting.definition.DefinitionContext;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.query.Query;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.person.definition.PersonQuery;
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
		
		List<Class<? extends Definition>> allTypes = new ArrayList<Class<? extends Definition>>();
		if (DataDefinition.class.isAssignableFrom(type)) {
			allTypes.add(PersonDataDefinition.class);
			allTypes.add(PatientDataDefinition.class);
			allTypes.add(EncounterDataDefinition.class);
		}
		else if (Query.class.isAssignableFrom(type)) {
			allTypes.add(PersonQuery.class);
			allTypes.add(CohortDefinition.class);
			allTypes.add(EncounterQuery.class);
			//allTypes.add(ObsQuery.class);  TODO: None of these are implemented yet
		}
		model.addAttribute("allTypes", allTypes);
		
		List<Class<? extends Definition>> hiddenDefinitions = new ArrayList<Class<? extends Definition>>();
		hiddenDefinitions.add(CohortIndicatorDataSetDefinition.class);
		hiddenDefinitions.add(MultiPeriodIndicatorDataSetDefinition.class);
		hiddenDefinitions.add(EncounterDataSetDefinition.class);
		hiddenDefinitions.add(PatientDataSetDataDefinition.class);
		hiddenDefinitions.add(AgeAtDateOfOtherDataDefinition.class);
		hiddenDefinitions.add(PersonToPatientDataDefinition.class);
		hiddenDefinitions.add(PersonToEncounterDataDefinition.class);
		hiddenDefinitions.add(PatientToEncounterDataDefinition.class);
		model.addAttribute("hiddenDefinitions", hiddenDefinitions);
		
		// Get all Definitions
		boolean retired = includeRetired != null && includeRetired.booleanValue();
		List<? extends Definition> definitions = DefinitionContext.getAllDefinitions(type, retired);
		
		// Construct a Map of Definition Type to List of Definitions
		Map<Class<? extends Definition>, List<Definition>> defsByType = 
			new TreeMap<Class<? extends Definition>, List<Definition>>(new DefinitionNameComparator());
		
		// Initialize the Map with all known supported types
		for (Class<? extends Definition> supportedType : DefinitionContext.getDefinitionService(type).getDefinitionTypes()) {
			if (!hiddenDefinitions.contains(supportedType)) {
				defsByType.put(supportedType, new ArrayList<Definition>());
			}
		}
		
		// Add all saved Definitions to the Map
		for (Definition d : definitions) {
			List<Definition> l = defsByType.get(d.getClass());
			if (l != null) {
				l.add(d);
			}
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
