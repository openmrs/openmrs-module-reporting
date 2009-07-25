package org.openmrs.module.reporting.web.widget.html;

/**
 * This represents one or more Radio Buttons
 */
public class RadioWidget extends CodedWidget {

	/** 
	 * @see BaseWidget#configureAttributes()
	 */
	@Override
	public void configureAttributes() {
		setAttribute("type", "radio");
		setAttribute("onmousedown", "this.__chk = this.checked;");
		setAttribute("onclick", "this.checked = !this.__chk;");
	}
}