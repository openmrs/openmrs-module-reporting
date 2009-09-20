package org.openmrs.module.reporting.web.widget.html;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.openmrs.module.reporting.web.widget.WidgetConfig;

/**
 * This represents one or more Check-boxes
 */
public class CheckboxWidget extends CodedWidget {

	/** 
	 * @see CodedWidget#render(WidgetConfig)
	 */
	@Override
	public void render(WidgetConfig config, Writer w) throws IOException {
		config.setFixedAttribute("type", "checkbox");
		super.render(config, w);
	}
	
	/**
	 * @see CodedWidget#isSelected(Option, Object)
	 */
	@Override
	public boolean isSelected(Option option, Object value) {
		if (ObjectUtils.equals(option.getValue(), value) ||
				ObjectUtils.equals(option.getCode(), value)) {
			return true;
		}
		else if (value instanceof Collection) {
			return ((Collection<?>) value).contains(option.getValue()) ||
				((Collection<?>) value).contains(option.getCode());
		}
		else if (value instanceof Object[]) {
			List<Object> l = Arrays.asList((Object[]) value);
			if (l != null) {
				return l.contains(option.getValue()) || l.contains(option.getCode());
			}
		}
		return false;
	}
}