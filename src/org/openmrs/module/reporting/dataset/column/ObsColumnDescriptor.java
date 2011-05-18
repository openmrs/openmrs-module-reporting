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
package org.openmrs.module.reporting.dataset.column;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.definition.EncounterAndObsDataSetDefinition;

/**
 * Keeps track of the column header names, storing Concept and occurrence 
 * (at each level of obs if this obs has an obs group
 *
 */
public class ObsColumnDescriptor implements Comparable<ObsColumnDescriptor> {

	private List<Concept> conceptHeirarchy;
	private List<Integer> conceptOccurrenceHeirarchy;
	
	public ObsColumnDescriptor() {
		super();
	}
	
	public ObsColumnDescriptor(Obs o, Map<Obs, Integer> fieldMap) {
		if (o != null)
			do {
				int occurrence = -1;

				if (fieldMap.get(o) != null) {
					occurrence = fieldMap.get(o);
				}
				
				if (occurrence == -1) {
					occurrence = 1;
				}

				this.addConceptToHeirarchy(o.getConcept(), occurrence);
				o = o.getObsGroup();
			} while (o != null);
	}

	/**Adds the Concept and occurrence number for one obs group level
	 * 
	 * @param concept
	 * @param occurrence
	 */
	public void addConceptToHeirarchy(Concept concept, int occurrence) {
		this.getConceptHeirarchy().add(0, concept);
		this.getConceptOccurrenceHeirarchy().add(0, occurrence);
	}

	public int compareTo(ObsColumnDescriptor otherObsColumnDescriptor) {

		List<Concept> otherConceptHeirarchy = ((ObsColumnDescriptor) otherObsColumnDescriptor).getConceptHeirarchy();
		List<Integer> otherConceptOccurrenceHeirarchy = ((ObsColumnDescriptor) otherObsColumnDescriptor).getConceptOccurrenceHeirarchy();
		
		int maxHeirarchySize = Math.max(this.getConceptHeirarchy().size(), otherConceptHeirarchy.size());
		
		for(int i=0; i < maxHeirarchySize; i++) {
			Concept concept = this.getConceptHeirarchy().get(i);
			Concept otherConcept = otherConceptHeirarchy.get(i);
			Integer conceptOccurrence = this.getConceptOccurrenceHeirarchy().get(i);
			Integer otherConceptOccurrence = otherConceptOccurrenceHeirarchy.get(i);
			
			if(concept == null)
				return -1;
			if(otherConcept == null)
				return 1;
			if(concept.getConceptId() > otherConcept.getConceptId()) {
				return 1;
			} else if(concept.getConceptId() < otherConcept.getConceptId()) {
				return -1;
			} else {
				if(conceptOccurrence > otherConceptOccurrence) {
					return 1;
				} else if (conceptOccurrence < otherConceptOccurrence) {
					return -1;
				}
			}
			// otherwise, this level has equal values so proceed to the next child level
		}
		return 0;
	}

	/**
	 * @return the conceptHeirarchy
	 */
	public List<Concept> getConceptHeirarchy() {
		if (conceptHeirarchy == null) {
			conceptHeirarchy = new ArrayList<Concept>();
		}
		return conceptHeirarchy;
	}

	/**
	 * @return the conceptOccurrenceHeirarchy
	 */
	public List<Integer> getConceptOccurrenceHeirarchy() {
		if (conceptOccurrenceHeirarchy == null) {
			conceptOccurrenceHeirarchy = new ArrayList<Integer>();
		}
		return conceptOccurrenceHeirarchy;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		return this.toString().hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ObsColumnDescriptor other = (ObsColumnDescriptor) obj;
		if(!this.toString().equals(other.toString())) {
			return false;
		}

		return true;
	}

	/** 
	 * @see java.lang.Object#toString()
	 * 
	 * Uses ConceptIds for the column header
	 */
	public String toString() {
		StringBuffer obsKey = new StringBuffer();
		for(int i=0; i < this.getConceptHeirarchy().size(); i++) {
			Concept concept = this.getConceptHeirarchy().get(i);
			Integer conceptOccurrence = this.getConceptOccurrenceHeirarchy().get(i);
			
			obsKey.append(concept.getConceptId());
			if(conceptOccurrence > 1) {
				obsKey.append("_").append(conceptOccurrence);
			}
			
			if(i != this.getConceptHeirarchy().size()-1) {
				obsKey.append("|");
			}
		}
		
		String result = obsKey.toString();
		
		// Replace unwanted characters and change case to upper
		result = result.replaceAll("\\s", "_");
		result = result.replaceAll("-", "_");
		result = result.toUpperCase();
		
		return result;
	}
	
	/** Overloads the standard toString() method.
	 * Allows for different formatting of headers.
	 * @param columnDisplayFormat
	 * @param maxColumnWidth
	 * @return
	 */
	public String format(List<EncounterAndObsDataSetDefinition.ColumnDisplayFormat> columnDisplayFormat, Integer maxColumnWidth) {
		
		// Make sure that the column name fits in the column
		int numberOfConcepts = this.getConceptHeirarchy().size();
		int conceptSize = 0;
		
		if(columnDisplayFormat.size() == 1 && columnDisplayFormat.contains(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.ID)) {
			if(maxColumnWidth != null && maxColumnWidth - this.toString().length() < 0) {
				throw new RuntimeException(
						"Maximum width for column with value '"+this.toString()+"' has been exceeded by "+ -(maxColumnWidth - this.toString().length())+" characters.");
			}
		}
		
		if(maxColumnWidth != null) {
			if(columnDisplayFormat.contains(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.ID) && columnDisplayFormat.contains(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.BEST_SHORT_NAME)) {
				// account for the '_' between concepts
				maxColumnWidth = maxColumnWidth - (numberOfConcepts + this.toString().length());
			}
			
			if(maxColumnWidth < 0 || (columnDisplayFormat.size() == 1 && columnDisplayFormat.contains(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.ID))) {
				return this.toString();
			}
		
			conceptSize = (int) Math.floor(maxColumnWidth / numberOfConcepts);
		}
	
		StringBuffer columnName = new StringBuffer();
		for(int i=0; i < this.getConceptHeirarchy().size(); i++) {
			Concept concept = this.getConceptHeirarchy().get(i);
			Integer conceptOccurrence = this.getConceptOccurrenceHeirarchy().get(i);
			
			if (columnDisplayFormat.contains(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.ID)) {
				columnName.append(concept.getConceptId());
			}
			
			if (columnDisplayFormat.contains(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.ID) && columnDisplayFormat.contains(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.BEST_SHORT_NAME)) {
				columnName.append("_");
			}
			
			String conceptName = concept.getBestShortName(Context.getLocale()).toString();

			if (columnDisplayFormat.contains(EncounterAndObsDataSetDefinition.ColumnDisplayFormat.BEST_SHORT_NAME)) {
				columnName.append(maxColumnWidth != null && conceptName.length() > conceptSize ? conceptName.substring(0, conceptSize) : conceptName);
			}
			
			if(conceptOccurrence > 1) {
				columnName.append("_").append(conceptOccurrence);
			}
			
			if(i != this.getConceptHeirarchy().size()-1) {
				columnName.append("|");
			}
		}
		
		String result = columnName.toString();
		
		// Replace unwanted characters and change case to upper
		result = result.replaceAll("\\s", "_");
		result = result.replaceAll("-", "_");
		result = result.toUpperCase();
		
		return result;
	}
}
