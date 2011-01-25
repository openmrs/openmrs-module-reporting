package org.openmrs.module.reporting.web.extension;

import org.openmrs.module.web.extension.LinkExt;


public class GutterListExt extends LinkExt {

	public String getLabel() {
		return "reporting.title";
	}

	public String getUrl() {
		return "module/reporting/dashboard/index.form";
	}

	/**
	 * Returns the required privilege in order to see this section. Can be a
	 * comma delimited list of privileges. If the default empty string is
	 * returned, only an authenticated user is required
	 * 
	 * @return Privilege string
	 */
	public String getRequiredPrivilege() {
		return "View Reports";
	}

}
