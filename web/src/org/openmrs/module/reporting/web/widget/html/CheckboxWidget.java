package org.openmrs.module.reporting.web.widget.html;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * This represents one or more Radio Buttons
 */
public class CheckboxWidget extends CodedWidget {

	/** 
	 * @see BaseWidget#configure()
	 */
	@Override
	public void configure() {
		setAttribute("type", "checkbox", false);
	}
	
	/**
	 * @see CodedWidget#isSelected(Option)
	 */
	@Override
	public boolean isSelected(Option option) {
		Object v = getDefaultValue();
		if (option.getValue() == null) {
			return v == null;
		}
		if (v instanceof Collection) {
			return ((Collection<?>) v).contains(option.getValue());
		}
		if (v instanceof Object[]) {
			List<Object> l = Arrays.asList((Object[]) v);
			if (l != null) {
				return l.contains(option.getValue());
			}
		}
		return option.getValue().equals(getDefaultValue());
	}
}