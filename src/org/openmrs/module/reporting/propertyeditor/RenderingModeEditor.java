package org.openmrs.module.reporting.propertyeditor;

import java.beans.PropertyEditorSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.service.ReportService;


public class RenderingModeEditor extends PropertyEditorSupport {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		String renderClass = text;
		String renderArg = null;
		if (renderClass.indexOf("!") > 0) {
			int ind = renderClass.indexOf("!");
			renderArg = renderClass.substring(ind + 1);
			renderClass = renderClass.substring(0, ind);
		}
		ReportRenderer renderer = Context.getService(ReportService.class).getReportRenderer(renderClass);
		if (renderer == null) {
			setValue(null);
		} else {
			RenderingMode ret = new RenderingMode();
			ret.setRenderer(renderer);
			ret.setArgument(renderArg);
			setValue(ret);
		}
	}
	
	@Override
	public String getAsText() {
	    RenderingMode ret = (RenderingMode) getValue();
	    return ret == null ? null : ret.getDescriptor();
	}

}
