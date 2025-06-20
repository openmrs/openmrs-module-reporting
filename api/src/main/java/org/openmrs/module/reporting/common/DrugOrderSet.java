/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.common;

import java.util.Collection;
import java.util.HashSet;

import org.openmrs.DrugOrder;

/**
 * Represents a Collection of Drug Orders
 */
public class DrugOrderSet extends HashSet<DrugOrder> {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Default Constructor
	 */
	public DrugOrderSet() {
		super();
	}
	
	/**
	 * Constructor that takes in a Collection of DrugOrders
	 */
	public DrugOrderSet(Collection<DrugOrder> drugOrders) {
		super(drugOrders);
	}
}
