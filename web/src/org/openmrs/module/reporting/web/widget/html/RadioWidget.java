package org.openmrs.module.reporting.web.widget.html;

import java.io.IOException;

import org.openmrs.module.reporting.web.widget.WidgetConfig;

/**
 * This represents one or more Radio Buttons
 */
public class RadioWidget extends CodedWidget {
	
	/** 
	 * @see CodedWidget#render(WidgetConfig)
	 */
	@Override
	public void render(WidgetConfig config) throws IOException {
		config.setFixedAttribute("type", "radio");
		config.setFixedAttribute("onmousedown", "this.__chk = this.checked;");
		config.setFixedAttribute("onclick", "this.checked = !this.__chk;");
		super.render(config);
	}
}