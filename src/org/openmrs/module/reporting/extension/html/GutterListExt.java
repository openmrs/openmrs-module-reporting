package org.openmrs.module.reporting.extension.html;

import org.openmrs.module.Extension;

public class GutterListExt extends Extension {

	@Override
	public MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}

	public String getLabel() {
		return "reporting.title";
	}

	public String getUrl() {
		return "module/reporting/manageDashboard.form";
	}

	/**
	 * Returns the required privilege in order to see this section. Can be a
	 * comma delimited list of privileges. If the default empty string is
	 * returned, only an authenticated user is required
	 * 
	 * @return Privilege string
	 */
	public String getRequiredPrivilege() {
		return "Manage Reports";
	}

}
