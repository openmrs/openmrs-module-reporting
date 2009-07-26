package org.openmrs.module.reporting.web.widget.html;

import java.io.IOException;
import java.io.Writer;

public class TextWidget extends BaseWidget {

	/** 
	 * @see BaseWidget#configure()
	 */
	@Override
	public void configure() {
		String textValue = getDefaultValue() == null ? "" : getDefaultValue().toString();
    	setAttribute("type","text", false);
    	setAttribute("value", textValue, false);
    	setAttribute("size", "20", true);
	}

	/** 
	 * @see Widget#render(Writer)
	 */
	public void render(Writer w) throws IOException {
		HtmlUtil.renderSimpleTag(w, "input", getAttributes());
	}
}