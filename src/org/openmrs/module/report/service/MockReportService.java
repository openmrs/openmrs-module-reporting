package org.openmrs.module.report.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.APIException;
import org.openmrs.module.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.cohort.definition.LocationCohortDefinition;
import org.openmrs.module.dataset.definition.CohortDataSetDefinition;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.indicator.CohortIndicator;
import org.openmrs.module.indicator.aggregation.CountAggregator;
import org.openmrs.module.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.report.ReportSchema;
import org.openmrs.util.OpenmrsUtil;

/**
 * Base Implementation of the ReportService API
 */
public class MockReportService extends BaseReportService implements ReportService {

	private transient Log log = LogFactory.getLog(this.getClass());
	
	private List<ReportSchema> reportSchemas = new ArrayList<ReportSchema>();
	
	/**
	 * Default constructor
	 */
	public MockReportService() { 
		initializeService();
	}

	/**
	 * @see ReportService#saveReportSchema(ReportSchema)
	 */
	public ReportSchema saveReportSchema(ReportSchema reportSchema) throws APIException {		
		// Just assumes it's new
		reportSchemas.add(reportSchema);
		serializeReportSchemas();		
		return reportSchema;
	}
	
	/**
	 * @see ReportService#getReportSchema(Integer)
	 */
	public ReportSchema getReportSchema(Integer reportSchemaId) throws APIException {
		for (ReportSchema reportSchema : reportSchemas) { 
			if (reportSchema.getId().equals(reportSchemaId)) { 
				return reportSchema;				
			}
		}
		return new ReportSchema();
	}

	/**
	 * @see ReportService#getReportSchemaByUuid(String)
	 */
	public ReportSchema getReportSchemaByUuid(String uuid) throws APIException {
		for (ReportSchema reportSchema : reportSchemas) { 
			if (reportSchema.getUuid().equals(uuid)) { 
				return reportSchema;
			}
		}
		return new ReportSchema();
	}
	
	/**
	 * @see ReportService#getReportSchemas()
	 */
	public List<ReportSchema> getReportSchemas() throws APIException {
		return reportSchemas;
	}
	
	/**
	 * @see ReportService#deleteReportSchema(ReportSchema)
	 */
	public void deleteReportSchema(ReportSchema reportSchema) {
		throw new APIException("not implemented yet");
	}	
	
	/**
	 * Initializes the service by de-serializing report schemas from the filesystem.
	 * 
	 * @throws Exception
	 */
	public void initializeService() { 		
		reportSchemas.add(this.getCohortReportSchema());
		reportSchemas.add(this.getIndicatorReportSchema());
		this.serializeReportSchemas();
	}

	
	
	
	public void deserializeReportSchemas() { 
		try { 			
			File directory = 
				OpenmrsUtil.getDirectoryInApplicationDataDirectory("reports/schemas");
			
			// Iterate over the 
			for (String filename : directory.list()) { 			
				log.info("filename: " + filename);
				
				String contents = null;
				if (filename.endsWith(".ser")) {
					contents = OpenmrsUtil.getFileAsString(new File(directory, filename));
					log.info("Xml report schema: " + contents);
					reportSchemas.add( deserializeReportSchema(new File(directory, filename)) );
				}
				
			}
		} catch (Exception e) { 
			log.error("Unable to de-serialize report schemas from the fileystem", e);
		}		
	}

	public void serializeReportSchemas() { 		
		try { 
			
			File directory = 
				OpenmrsUtil.getDirectoryInApplicationDataDirectory("reports/schemas");			
			
			for (ReportSchema schema : reportSchemas) { 
				serializeReportSchema(directory, schema);
			}			
		} catch (Exception e) { 
			log.error("Unable to serialize report schemas to the filesystem", e);
		}
	}
	
	
	public void serializeReportSchema(File directory, ReportSchema reportSchemaObj) throws Exception { 		
		ObjectOutputStream oos = null;
		try { 
			File dest = new File(directory, reportSchemaObj.getName() + ".ser");			
			FileOutputStream fos = new FileOutputStream(dest);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(reportSchemaObj);
		} catch (Exception e) { 
			log.error("Error serializing report schema", e);			
		} finally { 
			oos.flush();
			oos.close();
		}
	}
	
	public ReportSchema deserializeReportSchema(File reportSchemaFile) throws Exception { 
		ObjectInputStream ois = null;
		try { 
			FileInputStream fis = new FileInputStream(reportSchemaFile);
			ois = new ObjectInputStream(fis);
			return (ReportSchema)ois.readObject();
		} catch (Exception e) { 
			log.error("Error deserializing report schema", e);			
		} finally { 
			ois.close();
		}
		return new ReportSchema();
	}

	
	/**
	 * Gets a simple cohort report schema.
	 * @return
	 */
	public ReportSchema getCohortReportSchema() { 
		// Add a very basic cohort report to the report schemas
		AgeCohortDefinition childOnDate = new AgeCohortDefinition();
		childOnDate.setName("Child On Date Cohort Definition");
		childOnDate.setMaxAge(14);
		childOnDate.addParameter(new Parameter("effectiveDate", "Age As of Date", Date.class));		

		CohortDataSetDefinition dsd = new CohortDataSetDefinition();
		dsd.setName("# Children (As Of Date) Dataset");
		dsd.setName("This is a cohort dataset definition used to calculate the number of patients who are children on a specific date");
		dsd.addParameter(new Parameter("cd.startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("cd.endDate", "End Date", Date.class));
		dsd.addStrategy("Children at Start", new Mapped<CohortDefinition>(childOnDate, "effectiveDate=${cd.startDate}"));
		dsd.addStrategy("Children at End", new Mapped<CohortDefinition>(childOnDate, "effectiveDate=${cd.endDate}"));		

		// Create the report schema
		ReportSchema reportSchema = new ReportSchema();
		reportSchema.setId(1);
		reportSchema.setUuid(UUID.randomUUID().toString());
		reportSchema.setName("Simple Cohort Report");
		reportSchema.setDescription("This is a simple report with parameters and a cohort dataset definition");
		reportSchema.addParameter(new Parameter("report.startDate", "Report Start Date", Date.class, null, null));
		reportSchema.addParameter(new Parameter("report.endDate", "Report End Date", Date.class, null, null));
		reportSchema.addDataSetDefinition(new Mapped<DataSetDefinition>(dsd, "cd.startDate=${report.startDate},cd.endDate=${report.endDate}"));
	
		return reportSchema;	
	}
	
	/**
	 * Gets a simple indicator report schema.
	 * 
	 * @return	a simple indicator report schema
	 */
	public ReportSchema getIndicatorReportSchema() { 
				
		ReportSchema reportSchema = new ReportSchema();
		reportSchema.setId(2);
		reportSchema.setUuid(UUID.randomUUID().toString());
		reportSchema.setName("Simple Indicator Report");
		reportSchema.setDescription("This is a simple indicator report with a cohort indicator dataset definition");
		reportSchema.addParameter(new Parameter("report.location", "Report Location", Location.class));
		reportSchema.addParameter(new Parameter("report.reportDate", "Report Date", Date.class));
		
		
		// Add dataset definition
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("Number of patients enrolled at a location by gender and age");
		dsd.addParameter(new Parameter("location", "Location", Location.class));
		dsd.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));
		reportSchema.addDataSetDefinition(dsd, "location=${report.location},effectiveDate=${report.reportDate}");
		
		// Add cohort definition
		LocationCohortDefinition atSite = new LocationCohortDefinition();
		atSite.addParameter(new Parameter("location", "Location: ", Location.class));

		// Add cohort indicator
		CohortIndicator indicator = new CohortIndicator();
		indicator.setName("Number of patients at a particular site");
		indicator.setAggregator(CountAggregator.class);
		indicator.setCohortDefinition(atSite, "location=${indicator.location}");
		indicator.addParameter(new Parameter("indicator.location", "Location", Location.class));
		indicator.addParameter(new Parameter("indicator.effDate", "Date", Date.class));
		indicator.setLogicCriteria(null);
		dsd.addIndicator("patientsAtSite", indicator, "indicator.location=${location},indicator.effDate=${effectiveDate}");
		
		// Defining dimensions
		CohortDefinitionDimension genderDimension = new CohortDefinitionDimension();		
		GenderCohortDefinition males = new GenderCohortDefinition();
		males.setGender("M");		

		// Cohort definition
		GenderCohortDefinition females = new GenderCohortDefinition();
		females.setGender("F");
		genderDimension.addCohortDefinition("male", males, null);
		genderDimension.addCohortDefinition("female", females, null);		

		// Age dimension
		CohortDefinitionDimension ageDimension = new CohortDefinitionDimension();
		ageDimension.addParameter(new Parameter("ageDate", "ageDate", Date.class));		

		// Age (child) cohort definition
		AgeCohortDefinition adult = new AgeCohortDefinition();
		adult.setMinAge(15);
		adult.addParameter(new Parameter("effectiveDate", "Effective Date:", Date.class));
		ageDimension.addCohortDefinition("adult", adult, "effectiveDate=${ageDate}");		

		// Age (adult) cohort definition
		AgeCohortDefinition child = new AgeCohortDefinition();
		child.setMaxAge(14);
		child.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));
		ageDimension.addCohortDefinition("child", child, "effectiveDate=${ageDate}");
		
		// Add dimensions
		dsd.addDimension("gender", genderDimension, null);
		dsd.addDimension("age", ageDimension, "ageDate=${indicator.effDate}");				
		
		// Add columns
		dsd.addColumnSpecification("1.A", "Male Adult", Object.class, "patientsAtSite", "gender=male,age=adult");
		dsd.addColumnSpecification("1.B", "Male Child", Object.class, "patientsAtSite", "gender=male,age=child");
		dsd.addColumnSpecification("2.A", "Female Adult", Object.class, "patientsAtSite", "gender=female,age=adult");
		dsd.addColumnSpecification("2.B", "Female Child", Object.class, "patientsAtSite", "gender=female,age=child");
				
		return reportSchema;
		
	}
	
	
	

}