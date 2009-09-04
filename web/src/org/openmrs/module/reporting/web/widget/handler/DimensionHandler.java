package org.openmrs.module.reporting.web.widget.handler;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.indicator.dimension.Dimension;
import org.openmrs.module.indicator.service.IndicatorService;
import org.openmrs.module.reporting.web.widget.WidgetConfig;
import org.openmrs.module.reporting.web.widget.html.CodedWidget;
import org.openmrs.module.reporting.web.widget.html.Option;

@Handler(supports={Dimension.class}, order=50)
public class DimensionHandler extends CodedHandler {
	
	/**
	 * @see org.openmrs.module.reporting.web.widget.handler.CodedHandler#populateOptions(org.openmrs.module.reporting.web.widget.WidgetConfig, org.openmrs.module.reporting.web.widget.html.CodedWidget)
	 */
	@Override
	public void populateOptions(WidgetConfig config, CodedWidget widget) {
		for (Dimension d : Context.getService(IndicatorService.class).getAllDimensions(false)) {
			widget.addOption(new Option(d.getUuid(), d.getName(), null, d), config);
		}
	}
	
	/**
	 * @see org.openmrs.module.reporting.web.widget.handler.WidgetHandler#parse(java.lang.String, java.lang.Class)
	 */
	@Override
	public Object parse(String input, Class<?> type) {
		return Context.getService(IndicatorService.class).getDimensionByUuid(input);
	}
	
}
