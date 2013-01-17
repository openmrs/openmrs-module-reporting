package org.openmrs.module.reporting.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.api.context.Context;
import org.openmrs.module.htmlwidgets.web.WidgetUtil;
import org.openmrs.module.reporting.definition.DefinitionContext;
import org.openmrs.module.reporting.definition.DefinitionSummary;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.propertyeditor.MappedEditor;
import org.openmrs.module.reporting.web.taglib.FormatTag;
import org.openmrs.module.reporting.web.util.AjaxUtil;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GetMappedAsStringController {

	@RequestMapping("/module/reporting/widget/getMappedAsString")
	public void getMappedAsString(Model model,
	                              HttpServletRequest request,
	                              @RequestParam("valueType") String valueTypeClassnames,
	                              @RequestParam("saveCallback") String saveCallback,
	                              @RequestParam("cancelCallback") String cancelCallback,
	                              @RequestParam(required=false, value="removeCallback") String removeCallback,
	                              @RequestParam(required=false, value="initialUuid") String initialUuid,
	                              @RequestParam(required=false, value="valueUuid") String valueUuid,
	                              @RequestParam(required=false, value="label") String label,
	                              @RequestParam(required=false, value="action") String action) throws Exception {
		// TODO allow list of parameters (maybe with types) to be passed in
		List<DefinitionSummary> list = new ArrayList<DefinitionSummary>();
		Class<Definition> clazz = null;
		
		if (valueUuid == null && initialUuid != null)
			valueUuid = initialUuid;
		
		String[] names = valueTypeClassnames.split(",");
		for(String name : names) {
		clazz = (Class<Definition>) Context.loadClass(name);
		list.addAll( DefinitionContext.getDefinitionService(clazz).getAllDefinitionSummaries(true));
		}
		model.addAttribute("valueOptions", list);
		
		Definition selectedValue = null;
		List<Parameter> selectedValParams = Collections.emptyList();
		if (valueUuid != null) {
			selectedValue = DefinitionContext.getDefinitionByUuid(clazz, valueUuid);
			if (selectedValue != null && selectedValue.getParameters() != null)
				selectedValParams = selectedValue.getParameters();
			Map<String, Object> chosenMappings = new LinkedHashMap<String, Object>();
			model.addAttribute("selectedValue", selectedValue);
			model.addAttribute("chosenMappings", chosenMappings);

			if (selectedValParams != null) {
				for (Parameter p : selectedValParams) {
					String style = request.getParameter("chooseStyle" + p.getName());
					Object mappedValue = null;
					if ("fixed".equals(style)) {
						String[] value = request.getParameterValues("fixedValue_"+p.getName());
						String fixedValueString = OpenmrsUtil.join(Arrays.asList(value), ",");
		    			mappedValue = WidgetUtil.parseInput(fixedValueString, p.getType());
					} else { // "complex"
						String expression = request.getParameter("complexValue_" + p.getName());
						if (expression != null)
							mappedValue = "${" + expression + "}";
					}
					if (mappedValue != null) {
						chosenMappings.put(p.getName(), mappedValue);
					}
				}
			}
			// action != null means they have actually pressed save. (we don't want an initial
			// value with no parameters to be immediately chosen when the dialog is first opened)
			if (action != null && chosenMappings.size() == selectedValParams.size()) {
				MappedEditor editor = new MappedEditor();

				editor.setValue(new Mapped<Definition>(selectedValue, chosenMappings));
				model.addAttribute("serializedResult", editor.getAsText());
				Map<String, String> params = new LinkedHashMap<String, String>();

				for (Map.Entry<String, Object> e : chosenMappings.entrySet()) {
					params.put(e.getKey(), FormatTag.format(e.getValue()));
				}
				Map<String, Object> json = new LinkedHashMap<String, Object>();
				json.put("parameterizable", selectedValue.getName());
				json.put("parameterizableUuid", selectedValue.getUuid());
				json.put("parameterMappings", params);
				model.addAttribute("jsResult", AjaxUtil.toJson(json));
			}
		}
	}

}
