package org.openmrs.module.reporting.web.widget.html;

/**
 * This represents one or more Radio Buttons
 */
public class RadioWidget extends CodedWidget {

	/** 
	 * @see BaseWidget#configure()
	 */
	@Override
	public void configure() {
		setAttribute("type", "radio", false);
		setAttribute("onmousedown", "this.__chk = this.checked;", false);
		setAttribute("onclick", "this.checked = !this.__chk;", false);
	}
}