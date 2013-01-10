package org.openmrs.module.reporting.web.datasets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.htmlwidgets.web.WidgetUtil;
import org.openmrs.module.reporting.common.DelimitedKeyComparator;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorAndDimensionDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorAndDimensionDataSetDefinition.CohortIndicatorAndDimensionSpecification;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.definition.DefinitionContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.propertyeditor.IndicatorEditor;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

@Controller
public class CohortIndicatorAndDimensionDataSetEditor {

	@InitBinder
    public void initBinder(WebDataBinder binder) { 
    	binder.registerCustomEditor(Indicator.class, new IndicatorEditor());
    }

	@RequestMapping("/module/reporting/datasets/cohortIndicatorAndDimensionDatasetEditor")
	public void editDataSet(ModelMap model, @RequestParam(value="uuid", required=false) String uuid) {
		DataSetDefinition dsd = new CohortIndicatorAndDimensionDataSetDefinition();
		if (uuid != null) {
			dsd = DefinitionContext.getDataSetDefinitionService().getDefinitionByUuid(uuid);
		}
		model.addAttribute("dsd", dsd);
	}
	
	@RequestMapping("/module/reporting/datasets/cohortIndicatorAndDimensionAddSpecification")
	public String addSpecification(@RequestParam("dsdUuid") String dsdUuid,
							@RequestParam("index") Integer index,
	                        @RequestParam("indicatorNumber") String indicatorNumber,
	                        @RequestParam(value="label", required=false) String label,
	                        @RequestParam("indicator") CohortIndicator indicator,
	                        @RequestParam(value="dimensions", required=false) List<String> dimensions,
	                        WebRequest request) throws Exception {

		DataSetDefinition dsd = DefinitionContext.getDataSetDefinitionService().getDefinitionByUuid(dsdUuid);
		CohortIndicatorAndDimensionDataSetDefinition cidsd = (CohortIndicatorAndDimensionDataSetDefinition)dsd;
		
		CohortIndicatorAndDimensionSpecification spec = null;
		if (index == null) {
			spec = new CohortIndicatorAndDimensionDataSetDefinition.CohortIndicatorAndDimensionSpecification();
			cidsd.addSpecification(spec);
		}
		else {
			spec = cidsd.getSpecifications().get(index);
		}
		spec.setIndicatorNumber(indicatorNumber);
		spec.setLabel(label);
		
		Mapped<CohortIndicator> mapped = new Mapped<CohortIndicator>();
		mapped.setParameterizable(indicator);
		
    	for (Parameter p : indicator.getParameters()) {
    		String valueType = request.getParameterValues("valueType_"+p.getName())[0];
    		String[] value = request.getParameterValues(valueType+"Value_"+p.getName());
    		if (value != null && value.length > 0) {
	    		Object paramValue = null;
	    		if (StringUtils.isEmpty(valueType) || valueType.equals("fixed")) {
	    			String fixedValueString = OpenmrsUtil.join(Arrays.asList(value), ",");
	    			paramValue = WidgetUtil.parseInput(fixedValueString, p.getType());
	    		}
	    		else {
	    			paramValue = "${"+value[0]+"}";
	    		}
	    		if (paramValue != null) {
	    			mapped.addParameterMapping(p.getName(), paramValue);
	    		}
    		}
    	}
		spec.setIndicator(mapped);
		
		// Parse the dimension parameters to organize them, keeping them in order they are defined
		Map<String, List<String>> dimensionOptions = new LinkedHashMap<String, List<String>>();
		if (ObjectUtil.notNull(dimensions)) {
			for (String dimensionKey : cidsd.getDimensions().keySet()) {
				CohortDefinitionDimension dimension = cidsd.getDimensions().get(dimensionKey).getParameterizable();
				for (String option : dimension.getOptionKeys()) {
					if (dimensions.contains(dimensionKey + "^" + option)) {
						List<String> l = dimensionOptions.get(dimensionKey);
						if (l == null) {
							l = new ArrayList<String>();
							dimensionOptions.put(dimensionKey, l);
						}
						l.add(option);	
					}
				}
			}
		}
		spec.setDimensionOptions(dimensionOptions);

		Collections.sort(cidsd.getSpecifications(), new Comparator<CohortIndicatorAndDimensionSpecification>() {
			private DelimitedKeyComparator c = new DelimitedKeyComparator();
			public int compare(CohortIndicatorAndDimensionSpecification s1, CohortIndicatorAndDimensionSpecification s2) {
				return c.compare(s1.getIndicatorNumber(), s2.getIndicatorNumber());
			}
		});
		
		DefinitionContext.getDataSetDefinitionService().saveDefinition(cidsd);
		
		return "redirect:/module/reporting/closeWindow.htm";
	}
	
	@RequestMapping("/module/reporting/datasets/cohortIndicatorAndDimensionRemoveIndicator")
	public String removeIndicator(@RequestParam("dsdUuid") String dsdUuid,
	                           	  @RequestParam("index") Integer index) {
		
		DataSetDefinition dsd = DefinitionContext.getDataSetDefinitionService().getDefinitionByUuid(dsdUuid);
		CohortIndicatorAndDimensionDataSetDefinition cdsd = (CohortIndicatorAndDimensionDataSetDefinition) dsd;
		cdsd.removeSpecification(index);
		DefinitionContext.getDataSetDefinitionService().saveDefinition(cdsd);
		
		return "redirect:cohortIndicatorAndDimensionDatasetEditor.form?uuid=" + dsdUuid;
	}
	
	@RequestMapping("/module/reporting/datasets/cohortIndicatorAndDimensionRemoveDimension")
	public String removeDimension(@RequestParam("uuid") String uuid,
	                              @RequestParam("key") String key) {
		
		DataSetDefinition dsd = DefinitionContext.getDataSetDefinitionService().getDefinitionByUuid(uuid);
		CohortIndicatorAndDimensionDataSetDefinition cdsd = (CohortIndicatorAndDimensionDataSetDefinition) dsd;
		cdsd.removeDimension(key);
		DefinitionContext.getDataSetDefinitionService().saveDefinition(cdsd);
		
		return "redirect:cohortIndicatorAndDimensionDatasetEditor.form?uuid=" + uuid;
	}
}
