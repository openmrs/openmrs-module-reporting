package org.openmrs.module.reporting.web.controller;

import java.util.Iterator;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.definition.DefinitionContext;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.InteractiveReportRenderer;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.web.util.AjaxUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AjaxController {
	
	@RequestMapping("/module/reporting/ajax/getRenderingModes")
	public String getRenderingModes(Model model,
	                                @RequestParam("reportDefinitionUuid") String reportUuid,
	                                @RequestParam(value="includeInteractiveRenderers", required=false) Boolean includeInteractive,
	                                @RequestParam(value="includeFileRenderers", required=false) Boolean includeFile) throws Exception {
		if (includeInteractive == null)
			includeInteractive = false;
		if (includeFile == null)
			includeFile = true;
		ReportDefinition rd = DefinitionContext.getDefinitionByUuid(ReportDefinition.class, reportUuid);
		List<RenderingMode> modes = Context.getService(ReportService.class).getRenderingModes(rd);
		if (!includeInteractive || !includeFile) {
			for (Iterator<RenderingMode> i = modes.iterator(); i.hasNext(); ) {
				RenderingMode mode = i.next();
				boolean isInteractive = mode.getRenderer() instanceof InteractiveReportRenderer;
				if ( (!includeInteractive && isInteractive) || (!includeFile && !isInteractive) ) {
					i.remove();
				}
			}
		}
		model.addAttribute("json", AjaxUtil.toJson(modes));
		return "/module/reporting/json";
	}
	
}
