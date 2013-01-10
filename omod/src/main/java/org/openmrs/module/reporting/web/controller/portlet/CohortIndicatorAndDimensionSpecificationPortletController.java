package org.openmrs.module.reporting.web.controller.portlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorAndDimensionDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorAndDimensionDataSetDefinition.CohortIndicatorAndDimensionSpecification;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.definition.DefinitionContext;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.util.OpenmrsClassLoader;

/**
 * This Controller loads a CohortIndicatorAndDimensionSpecification for management
 */
public class CohortIndicatorAndDimensionSpecificationPortletController extends ReportingPortletController {
	
	protected final Log log = LogFactory.getLog(getClass());

	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		
		// TODO: Figure out why this is necessary.
		Thread.currentThread().setContextClassLoader(OpenmrsClassLoader.getInstance());
		model.put("portletUUID", UUID.randomUUID().toString().replace("-", ""));

		String dsdUuid = (String)model.get("dsdUuid");	
		DataSetDefinition dsd = DefinitionContext.getDataSetDefinitionService().getDefinitionByUuid(dsdUuid);
		CohortIndicatorAndDimensionDataSetDefinition cidsd = (CohortIndicatorAndDimensionDataSetDefinition)dsd;

		model.put("dsdUuid", cidsd.getUuid());
		model.put("dsd", cidsd);
		
		String index = (String)model.get("index");
		CohortIndicatorAndDimensionSpecification specification = null;
		if (ObjectUtil.notNull(index)) {
			model.put("index", index);
			specification = cidsd.getSpecifications().get(Integer.parseInt(index));
		}
		else {
			specification = new CohortIndicatorAndDimensionDataSetDefinition.CohortIndicatorAndDimensionSpecification();
		}
		model.put("specification", specification);
		
		CohortIndicator mappedObj = null;
		Map<String, Object> mappings = new HashMap<String, Object>();
		
		// If the user has changed the underlying indicator, retrieve it
		String mappedUuid = (String)model.get("mappedUuid");

       	if (StringUtils.isEmpty(mappedUuid)) { // If you are not selecting a new Indicator, get the currently saved specification for the passed index
       		if (specification.getIndicator() != null) {
       			mappedObj = specification.getIndicator().getParameterizable();
    			mappings = specification.getIndicator().getParameterMappings();
    		}
       	}
       	else if (mappedUuid != null) { // If you are selecting a new Indicator, get it
       		mappedObj = (CohortIndicator)DefinitionContext.getIndicatorService().getDefinitionByUuid(mappedUuid);
       	}
       	model.put("mappedObj", mappedObj);
       	model.put("mappings", mappings);

		model.put("allowedParams", ParameterizableUtil.getAllowedMappings(dsd, mappedObj));
		model.putAll(ParameterizableUtil.getCategorizedMappings(dsd, mappedObj, mappings));
		
		List<String> sortedKeys = new ArrayList<String>();
		for (CohortIndicatorAndDimensionSpecification spec : cidsd.getSpecifications()) {
			sortedKeys.add(spec.getIndicatorNumber());
		}
		model.put("sortedKeys", sortedKeys);
	 
		model.put("newIndNum", ObjectUtil.nvlStr(model.get("newIndNum"), specification.getIndicatorNumber()));
		model.put("newLabel", ObjectUtil.nvlStr(model.get("newLabel"), specification.getLabel()));
		
		// Convenience to record those dimensions that should be checked
		// First check whether dimensions have been passed in by the user that may have been changed
		String newDimensions = ObjectUtil.nvlStr(model.get("newDimensions"), "");
		if (ObjectUtil.isNull(newDimensions)) {
			for (Map.Entry<String, List<String>> e : specification.getDimensionOptions().entrySet()) {
				String dim = e.getKey();
				for (String option : e.getValue()) {
					newDimensions += (ObjectUtil.isNull(newDimensions) ? "" : ",") + dim + "^" + option;
				}
			}
		}
		model.put("newDimensions", newDimensions);
	}
}
