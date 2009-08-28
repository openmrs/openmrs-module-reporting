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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.OrderService.ORDER_STATUS;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.DataSetRow;
import org.openmrs.module.dataset.SimpleDataSet;
import org.openmrs.module.dataset.column.ConceptDataSetColumn;
import org.openmrs.module.dataset.column.DataSetColumn;
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
				
		LabEncounterDataSetDefinition definition = (LabEncounterDataSetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(definition, context);
		
		Location location = (Location) context.getParameterValue("location");
		Date startDate = (Date) context.getParameterValue("startDate");
		Date endDate = (Date) context.getParameterValue("endDate");
		log.info("Location=" + location + ", startDate=" + startDate  + ", endDate=" + endDate);
		
		List<Encounter> encounters = getLabEncounters(location, startDate, endDate);
		log.info("Encounters found: " + encounters.size());
		
		for (Encounter e : encounters) {
			
			DataSetRow<Object> row = new DataSetRow<Object>();
			row.addColumnValue(LabEncounterDataSetDefinition.ENCOUNTER_ID, e.getEncounterId());
			row.addColumnValue(LabEncounterDataSetDefinition.PATIENT_ID, e.getPatientId());
			row.addColumnValue(LabEncounterDataSetDefinition.LAB_ORDER_DATE, e.getEncounterDatetime());

			for (DataSetColumn column : definition.getColumns()) { 
				if (column instanceof ConceptDataSetColumn) {
					ConceptDataSetColumn conceptColumn = (ConceptDataSetColumn) column;
					
					/* FIXME Quick hack to get a desired observation by concept. This currently returns the first observation found.  
					 * I'm assuming that the Dao orders by date, but need to test this out.  
					 * Need to implement a more elegant solution for returning the most recent observation
					 */
					Obs obs = null;
					for (Obs current : e.getObs()) { 
						// TODO This only works when comparing conceptId, not concepts
						if (current.getConcept().getConceptId().equals(conceptColumn.getConcept().getConceptId())) { 	
							// Just making sure this is the most recent observation
							// if obs is null then we know is the first in the list
							// otherwise check which observation came first (based on obs date time) 
							if (obs == null || obs.getObsDatetime().compareTo(current.getObsDatetime()) < 0) { 
								obs = current;
							}
						}
					}
					obs = (obs != null ? obs : new Obs());
					row.addColumnValue(column, obs.getValueAsString(Context.getLocale()));
					dataSet.addRow(row);
				}
			}
		}
		return dataSet;
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
