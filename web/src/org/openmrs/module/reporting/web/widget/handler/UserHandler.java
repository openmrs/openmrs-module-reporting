/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.reporting.web.widget.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.web.widget.WidgetConfig;
import org.openmrs.module.reporting.web.widget.html.CodedWidget;
import org.openmrs.module.reporting.web.widget.html.Option;

/**
 * WidgetHandler for Users
 */
@Handler(supports={User.class}, order=50)
public class UserHandler extends CodedHandler {
	
	/**
	 * @see CodedHandler#setDefaults(WidgetConfig)
	 */
	@Override
	protected void setDefaults(WidgetConfig config) {
		if (StringUtils.isEmpty(config.getFormat())) {
			config.setFormat("ajax");
		}
		String roleParam = "";
		String roleCsv = config.getAttributeValue("roles");
		if (StringUtils.isNotEmpty(roleCsv)) {
			roleParam = "?roles="+roleCsv;
		}
		config.setDefaultAttribute("ajaxUrl", "/module/reporting/widget/userSearch.form"+roleParam);
	}

	/** 
	 * @see CodedHandler#populateOptions(WidgetConfig, CodedWidget)
	 */
	@Override
	public void populateOptions(WidgetConfig config, CodedWidget widget) {
		
		if (StringUtils.isNotEmpty(config.getFormat()) && !"ajax".equals(config.getFormat())) {
			
			String roleCsv = config.getAttributeValue("roles");
			List<User> users = null;
			if (StringUtils.isNotEmpty(roleCsv)) {
				users = new ArrayList<User>();
				for (String roleName : roleCsv.split(",")) {
					Role role = Context.getUserService().getRole(roleName);
					users.addAll(Context.getUserService().getUsersByRole(role));
				}
			}
			else {
				users = Context.getUserService().getAllUsers();
			}

			for (User u : users) {
				widget.addOption(new Option(u.getUuid(), getUserDisplay(u, config), null, u), config);
			}
		}
		else if (config.getDefaultValue() != null && StringUtils.isNotEmpty(config.getDefaultValue().toString())) {
			User u = (User) config.getDefaultValue();
			widget.addOption(new Option(u.getUuid(), getUserDisplay(u, config), null, u), config);
		}
	}
	
	/** 
	 * @see WidgetHandler#parse(String, Class<?>)
	 */
	@Override
	public Object parse(String input, Class<?> type) {
		return Context.getUserService().getUserByUuid(input);
	}
	
	/**
	 * Returns how to display the user name given the configuration
	 * @param config
	 * @return
	 */
	protected String getUserDisplay(User u, WidgetConfig config) {
		return u.getFamilyName() + ", " + u.getGivenName();
	}
}
