package org.openmrs.module.report.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.indicator.dimension.Dimension;
import org.openmrs.module.indicator.service.IndicatorService;


public class ReportUtil {
	
	
	public static List<InitialDataElement> getInitialDataElements() {
		List<InitialDataElement> ret = new ArrayList<InitialDataElement>();
		ret.add(new InitialDataElement(CohortDefinition.class, "Female") {
			public void apply() {
				GenderCohortDefinition female = new GenderCohortDefinition("F");
				female.setName("Female");
				Context.getService(CohortDefinitionService.class).saveCohortDefinition(female);
			}
		});
		ret.add(new InitialDataElement(CohortDefinition.class, "Male") {
			public void apply() {
				GenderCohortDefinition male = new GenderCohortDefinition("M");
				male.setName("Male");
				Context.getService(CohortDefinitionService.class).saveCohortDefinition(male);
			}
		});
		ret.add(new InitialDataElement(CohortDefinition.class, "Age Range on Date") {
			public void apply() {
				AgeCohortDefinition age = new AgeCohortDefinition();
				age.addParameter(new Parameter("minAge", "minAge", Integer.class));
				age.addParameter(new Parameter("maxAge", "maxAge", Integer.class));
				age.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
				age.setName("Age Range on Date");
				Context.getService(CohortDefinitionService.class).saveCohortDefinition(age);
			}
		});
		ret.add(new InitialDataElement(CohortDefinition.class, "Child on Date") {
			public void apply() {
				AgeCohortDefinition age = new AgeCohortDefinition();
				age.setMaxAge(14);
				age.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
				age.setName("Child on Date");
				Context.getService(CohortDefinitionService.class).saveCohortDefinition(age);
			}
		});
		ret.add(new InitialDataElement(CohortDefinition.class, "Adult on Date") {
			public void apply() {
				AgeCohortDefinition age = new AgeCohortDefinition();
				age.setMinAge(15);
				age.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
				age.setName("Adult on Date");
				Context.getService(CohortDefinitionService.class).saveCohortDefinition(age);
			}
		});
		/*
		for (Program program : Context.getProgramWorkflowService().getAllPrograms()) {
			ProgramStateCohortDefinition def = new ProgramStateCohortDefinition();
			def.setProgram(program);
			def.addParameter(new Parameter("untilDate", "untilDate", Date.class));
			def.setName("Ever in " + program.getName() + " Before Date");
			ret.add(def);
			
			def = new ProgramStateCohortDefinition();
			def.setProgram(program);
			def.addParameter(new Parameter("sinceDate", "sinceDate", Date.class));
			def.addParameter(new Parameter("untilDate", "untilDate", Date.class));
			def.setName("In " + program.getName() + " Between Dates");
			ret.add(def);
		}
		return ret;
		*/
		/*
		ret.add(new InitialDataElement(CohortDefinition.class, "") {
			public void apply() {
				Context.getService(CohortDefinitionService.class).saveCohortDefinition(def);
			}
		});
		*/
		ret.add(new InitialDataElement(Dimension.class, "Gender") {
			public void apply() {
				CohortDefinition female = getCohortDefinition("Female");
				CohortDefinition male = getCohortDefinition("Male");
				if (male == null || female == null) {
					throw new IllegalArgumentException("Cannot crate Gender dimension without Male and Female cohort definitions");
				}
				CohortDefinitionDimension gender = new CohortDefinitionDimension();
			    gender.setName("Gender");
			    gender.addCohortDefinition("female", female, null);
			    gender.addCohortDefinition("male", male, null);
			    Context.getService(IndicatorService.class).saveDimension(gender);
			}
		});
		return ret;
	}
	

	private static CohortDefinition getCohortDefinition(String name) {
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		for (CohortDefinition def : service.getCohortDefinitions(name, true)) {
			return def;
		}
		return null;
    }


	public static abstract class InitialDataElement {
		private Class<?> clazz;
		private String name;
		private Boolean alreadyDone = false;
		
		public InitialDataElement(Class<?> clazz, String name) {
			this.clazz = clazz;
			this.name = name;
		}
		
		public abstract void apply();

		public Class<?> getClazz() {
        	return clazz;
        }

        public void setClazz(Class<?> clazz) {
        	this.clazz = clazz;
        }
	
        public String getName() {
        	return name;
        }

        public void setName(String name) {
        	this.name = name;
        }

        public Boolean getAlreadyDone() {
        	return alreadyDone;
        }

        public void setAlreadyDone(Boolean alreadyDone) {
        	this.alreadyDone = alreadyDone;
        }
        
        public boolean equals(InitialDataElement other) {
        	return clazz.equals(other.clazz) && name.equals(other.name);
        }
		
	}

}
