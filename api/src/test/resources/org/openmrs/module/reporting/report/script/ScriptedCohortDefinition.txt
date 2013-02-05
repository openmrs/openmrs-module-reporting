import org.openmrs.Cohort
import org.openmrs.api.context.Context

cohort = new Cohort();
patients = Context.getPatientService().getAllPatients();
for (patient in patients) {
	cohort.addMember(patient.getPatientId());
}
return cohort;
