package org.openmrs.module.reporting.web.datasets;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlwidgets.web.WidgetUtil;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.MultiParameterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.definition.DefinitionContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MultiParameterDataSetEditor {
	
	@RequestMapping("/module/reporting/datasets/multiParameterDataSetEditor")
	public void showForm(ModelMap model,
						 @RequestParam(value="uuid", required=false) String uuid) {
		List<DataSetDefinition> allDefinitions = Context.getService(DataSetDefinitionService.class).getAllDefinitions(false);
		if (uuid == null) {			
			model.addAttribute("definition", new MultiParameterDataSetDefinition());
		} else {
			DataSetDefinition def = Context.getService(DataSetDefinitionService.class).getDefinitionByUuid(uuid);
			allDefinitions.remove(def);
			if (def instanceof MultiParameterDataSetDefinition) {
				model.addAttribute("definition", def);
			} else {
				throw new RuntimeException("This definition is not of the right class");
			} 
		}
		model.addAttribute("availableDefinitions", allDefinitions);
	}

	@RequestMapping("/module/reporting/datasets/multiParameterAddIteration")
	public String addIteration(@RequestParam("dsdUuid") String dsdUuid,
								  @RequestParam("index") Integer index) {

		DataSetDefinition dsd = DefinitionContext.getDataSetDefinitionService().getDefinitionByUuid(dsdUuid);
		MultiParameterDataSetDefinition pdsd = (MultiParameterDataSetDefinition) dsd;
		if (pdsd.getIterations() == null) {
			pdsd.setIterations(new ArrayList<Map<String, Object>>());
		}
		pdsd.getIterations().add(new HashMap<String, Object>());
		DefinitionContext.getDataSetDefinitionService().saveDefinition(pdsd);

		return "redirect:multiParameterDataSetEditor.form?uuid=" + dsdUuid;
	}

	@RequestMapping("/module/reporting/datasets/multiParameterRemoveIteration")
	public String removeIteration(@RequestParam("dsdUuid") String dsdUuid,
							   @RequestParam("index") Integer index) {
		DataSetDefinition dsd = DefinitionContext.getDataSetDefinitionService().getDefinitionByUuid(dsdUuid);
		MultiParameterDataSetDefinition pdsd = (MultiParameterDataSetDefinition) dsd;
		if (pdsd.getIterations() != null) {
			pdsd.getIterations().remove((int) index);
			DefinitionContext.getDataSetDefinitionService().saveDefinition(pdsd);
		}
		return "redirect:multiParameterDataSetEditor.form?uuid=" + dsdUuid;
	}

	@RequestMapping("/module/reporting/datasets/multiParameterEditIterationParameter")
	public String editIterationParameter(@RequestParam("dsdUuid") String dsdUuid,
										 @RequestParam("iteration") Integer iteration,
										 @RequestParam("parameterName") String parameterName,
										 WebRequest request) {
		DataSetDefinition dsd = DefinitionContext.getDataSetDefinitionService().getDefinitionByUuid(dsdUuid);
		MultiParameterDataSetDefinition pdsd = (MultiParameterDataSetDefinition) dsd;
		if (pdsd.getIterations() != null) {
			String valueType = request.getParameterValues("valueType")[0];
			String[] value = request.getParameterValues(valueType + "Value");
			Parameter p = pdsd.getBaseDefinition().getParameter(parameterName);
			if (value != null && value.length > 0) {
				Object paramValue = null;
				if (StringUtils.isEmpty(valueType) || valueType.equals("fixed")) {
					String fixedValueString = OpenmrsUtil.join(Arrays.asList(value), ",");
					paramValue = WidgetUtil.parseInput(fixedValueString, p.getType());
				}
				else {
					paramValue = "${" + value[0] + "}";
				}
				if (paramValue != null) {
					pdsd.getIterations().get(iteration).put(parameterName, paramValue);
				}
			}
			DefinitionContext.getDataSetDefinitionService().saveDefinition(pdsd);
		}

		return "redirect:/module/reporting/closeWindow.htm";
	}

	@RequestMapping("/module/reporting/datasets/multiParameterChangeBaseDefinition")
	public String changeBaseDefinition(@RequestParam("dsdUuid") String dsdUuid,
										 @RequestParam("baseDefinitionUuid") String baseDefinitionUuid) {
		DataSetDefinition dsd = DefinitionContext.getDataSetDefinitionService().getDefinitionByUuid(dsdUuid);
		MultiParameterDataSetDefinition pdsd = (MultiParameterDataSetDefinition) dsd;

		DataSetDefinition baseDsd = DefinitionContext.getDataSetDefinitionService().getDefinitionByUuid(baseDefinitionUuid);
		pdsd.setBaseDefinition(baseDsd);
		pdsd.getIterations().clear();
		DefinitionContext.getDataSetDefinitionService().saveDefinition(pdsd);

		return "redirect:multiParameterDataSetEditor.form?uuid=" + dsdUuid;
	}
}
