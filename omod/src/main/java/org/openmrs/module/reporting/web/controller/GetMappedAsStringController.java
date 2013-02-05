package org.openmrs.module.reporting.web.controller;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

	private static Log log = LogFactory.getLog(GetMappedAsStringController.class);

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
		Map<String, DefinitionSummary> sortedDefinitions = new TreeMap<String, DefinitionSummary>();
		Definition selectedValue = null;

		if (valueUuid == null && initialUuid != null) {
			valueUuid = initialUuid;
		}
		
		if (valueTypeClassnames != null) {
			for (String className : valueTypeClassnames.split(",")) {
				try {
					Class<Definition> type = (Class<Definition>) Context.loadClass(className);
					for (DefinitionSummary d : DefinitionContext.getDefinitionService(type).getAllDefinitionSummaries(true)) {
						sortedDefinitions.put(d.getName(), d);
					};
					if (valueUuid != null && selectedValue == null) {
						selectedValue = DefinitionContext.getDefinitionByUuid(type, valueUuid);
					}
				}
				catch (Exception e) {
					log.warn("Error adding definitions of type: " + className, e);
				}
			}
		}
		model.addAttribute("valueOptions", sortedDefinitions.values());
		model.addAttribute("selectedValue", selectedValue);

		List<Parameter> selectedValParams = Collections.emptyList();
		if (selectedValue != null && selectedValue.getParameters() != null) {
			selectedValParams = selectedValue.getParameters();
			Map<String, Object> chosenMappings = new LinkedHashMap<String, Object>();

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
