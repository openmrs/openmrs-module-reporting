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
package org.openmrs.module.dataset.definition.evaluator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.annotation.Handler;
import org.openmrs.api.APIException;
import org.openmrs.api.OrderService.ORDER_STATUS;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.LabEncounterDataSet;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.LabEncounterDataSetDefinition;
import org.openmrs.module.evaluation.EvaluationContext;

/**
 * The logic that evaluates a {@link LabOrderDataSetDefinition} 
 * and produces an {@link LabOrderDataSet}
 * 
 * @see LabOrderDataSetDefinition
 * @see LabOrderDataSet
 */
@Handler(supports={LabEncounterDataSetDefinition.class})
public class LabEncounterDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * Public constructor
	 */
	public LabEncounterDataSetEvaluator() { }	
	
	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	public DataSet<?> evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) {
				
		LabEncounterDataSetDefinition definition = 
			(LabEncounterDataSetDefinition) dataSetDefinition;

		//Cohort cohort = context.getBaseCohort();
		//if (cohort == null)
		//	throw new APIException("Cohort cannot be empty");
		
		Location location = (Location)
			context.getParameterValue("location");

		Date startDate = (Date) context.getParameterValue("startDate");
		Date endDate = (Date) context.getParameterValue("endDate");
		
		log.info("Location=" + location + ", startDate=" + startDate  + ", endDate=" + endDate);
		
		List<Encounter> encounters = 
			getLabEncounters(location, startDate, endDate);
				
		log.info("Encounters found: " + encounters.size());
		
		return new LabEncounterDataSet(definition, context, encounters);		
	}

	
	/**
	 * Convenience method taken from the simplelabentry module.  
	 * 
	 * FIXME Need to move the report over to the simplelabentry module.
	 * 
     * @see org.openmrs.module.simplelabentry.SimpleLabEntryService#getOrders(OrderType, Concept, Location, Date, ORDER_STATUS, List<Patient>)
     */
	//public List<Order> getLabOrders(Concept concept, Location location, Date orderDate, ORDER_STATUS status, List<Patient> patients) {
	public List<Encounter> getLabEncounters(Location location, Date startDate, Date endDate) { 	
		Map<Date, Encounter> encountersMap = new TreeMap<Date, Encounter>();
						
		// Only show lab orders
		List<OrderType> orderTypes = new ArrayList<OrderType>();
		orderTypes.add(getOrderType());
				
		List<Order> orders = 
			Context.getOrderService().getOrders(
					Order.class, 
					null, 
					null,
					ORDER_STATUS.NOTVOIDED, 
					null, 
					null, 
					orderTypes);

		
		// FIXME This should be done in the service OR dao layer
		for (Order order : orders) {
			Encounter encounter = order.getEncounter();
			Date encounterDate = order.getEncounter().getEncounterDatetime();

			// If location does not match
			if (location != null && !location.equals(encounter.getLocation())) {
				continue; 
			}
			// If encounter date is before the given start date
			if (startDate == null || encounterDate.before(startDate)) { 				
				continue;
			}
			// If encounter date is after the given end date
			if (endDate == null ||  encounterDate.after(endDate)) { 
				continue;
			}			
			// Should filter encounters that do not have any observations
			if (encounter.getObs().isEmpty()) { 
				continue;
			}			
			encountersMap.put(encounter.getDateCreated(), encounter);
		}
		
		
		// Create a new ordered list
		List<Encounter> encounterList = new ArrayList<Encounter>();
		encounterList.addAll(encountersMap.values());		
		
		return encounterList;
	}

	
	public OrderType getOrderType() { 
		
		// Retrieve proper OrderType for Lab Orders
		OrderType orderType = null;
		String orderTypeId = 
			Context.getAdministrationService().getGlobalProperty("simplelabentry.labOrderType");
		
		if (orderTypeId != null) {
			try {
				orderType = 
					Context.getOrderService().getOrderType(Integer.valueOf(orderTypeId));
			}
			catch (Exception e) {}
		}
		if (orderType == null) {
			throw new RuntimeException("Unable to retrieve LabOrders since the OrderType of <" + orderTypeId + "> is invalid.");
		}
		
		return orderType;
	}
	
	
}
