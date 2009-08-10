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
package org.openmrs.module.cohort.definition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.evaluation.parameter.Param;
import org.openmrs.util.OpenmrsUtil;

/**
 * Filter based on PatientProgramStates
 */
public class ProgramStateCohortDefinition extends DateRangeCohortDefinition {

    private static final long serialVersionUID = 1L;
    
	//***** PROPERTIES *****
	
	@Param(required=true)
	private Program program;
	
	@Param(required=false)
	private List<ProgramWorkflowState> stateList;

	//***** CONSTRUCTORS *****

	/**
	 * Default constructor
	 */
	public ProgramStateCohortDefinition() {
		super();
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {

		StringBuilder ret = new StringBuilder();
		ret.append("Patients in program ");
		
		if (program != null) {
			ret.append(program.getName());		
		}
		
		if (stateList != null && stateList.size() > 0) {
			Map<ProgramWorkflow, Set<ProgramWorkflowState>> map = new HashMap<ProgramWorkflow, Set<ProgramWorkflowState>>();
			for (ProgramWorkflowState state : stateList) {
				ProgramWorkflow workflow = state.getProgramWorkflow();
				OpenmrsUtil.addToSetMap(map, workflow, state);
			}
			boolean first = true;
			for (Map.Entry<ProgramWorkflow, Set<ProgramWorkflowState>> e : map.entrySet()) {
				ret.append(first ? "with " : "or ");
				first = false;
				try {
					ret.append(e.getKey().getConcept().getPreferredName(Context.getLocale()).getName());
				}
				catch (NullPointerException ex) {
					ret.append("CONCEPT?");
				}
				if (e.getValue().size() == 1) {
					Concept c = e.getValue().iterator().next().getConcept();
					ret.append(" of " + c.getPreferredName(Context.getLocale()).getName());
				}
				else {
					ret.append(" in [ ");
					for (Iterator<ProgramWorkflowState> i = e.getValue().iterator(); i.hasNext();) {
						Concept c = i.next().getConcept();
						ret.append(c.getPreferredName(Context.getLocale()).getName());
						if (i.hasNext()) {
							ret.append(" , ");
						}
					}
					ret.append(" ]");
				}
			}
		}
		ret.append(getDateRangeDescription());
		return ret.toString();
	}

	//***** PROPERTY ACCESS *****
	
    /**
     * @return the program
     */
    public Program getProgram() {
    	return program;
    }

    /**
     * @param program the program to set
     */
    public void setProgram(Program program) {
    	this.program = program;
    }
	
    /**
     * @return the stateList
     */
    public List<ProgramWorkflowState> getStateList() {
    	return stateList;
    }
	
    /**
     * @param stateList the stateList to set
     */
    public void setStateList(List<ProgramWorkflowState> stateList) {
    	this.stateList = stateList;
    }
}
