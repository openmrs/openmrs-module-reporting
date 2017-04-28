package org.openmrs.module.reporting;

import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.PersonName;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GenerateTestDataForLocationHierachyTests {

    private Patient firstPatient;
    private Patient secondPatient;

    private Location firstLocation;
    private Location secondLocation;

    private Encounter firstEncounter;
    private Encounter secondEncounter;

    private PatientProgram firstPatientProgram;
    private PatientProgram secondPatientProgram;

    private int firstPatientId;
    private int secondPatientId;

    private int firstLocationId;
    private int secondLocationId;

    private int firstEncounterId;
    private int secondEncounterId;

    private int firstPatientProgramId;
    private int secondPatientProgramId;

    public void generateTestPatients() {
        final PatientService patientService = Context.getPatientService();
        firstPatient = new Patient();
        firstPatient.setVoided(false);
        firstPatient.addName(getTestPersonName("John", "Doe"));
        firstPatient.setCreator(Context.getUserService().getUser(1));
        firstPatient.setDateCreated(parseDate("2006-01-18 00:00:00.0"));
        firstPatient.setVoidReason("");
        firstPatient.setGender("Male");
        firstPatient.addIdentifier(getTestPatientIdentifier("100-X"));
        firstPatientId = patientService.savePatient(firstPatient).getId();

        secondPatient = new Patient();
        secondPatient.setVoided(false);
        secondPatient.addName(getTestPersonName("Mary", "Doe"));
        secondPatient.setCreator(Context.getUserService().getUser(1));
        secondPatient.setDateCreated(parseDate("2006-01-18 00:00:00.0"));
        secondPatient.setVoidReason("");
        secondPatient.setGender("Female");
        secondPatient.addIdentifier(getTestPatientIdentifier("200-x"));
        secondPatientId = patientService.savePatient(secondPatient).getId();
    }

    private PatientIdentifier getTestPatientIdentifier(String indentifier) {
        PatientIdentifierType identifierType = new PatientIdentifierType();
        identifierType.setName("My Identifier type");
        identifierType.setRetired(false);
        identifierType.setDateCreated(parseDate("2006-01-18 00:00:00.0"));
        identifierType.setDescription("My Identifier type");
        Context.getPatientService().savePatientIdentifierType(identifierType);

        Location identifierLocation = new Location();
        identifierLocation.setName("Identifier location");
        identifierLocation.setDescription("Identifier location");
        identifierLocation.setDateCreated(parseDate("2009-08-15 15:28:54.0"));
        identifierLocation.setRetired(false);
        identifierLocation.setUuid(getRandomUuid());
        Context.getLocationService().saveLocation(identifierLocation);

        PatientIdentifier patientIdentifier = new PatientIdentifier(indentifier, identifierType, identifierLocation);
        return patientIdentifier;
    }

    private PersonName getTestPersonName(String givenName, String familyName) {
        final PersonName name = new PersonName();
        name.setGivenName(givenName);
        name.setFamilyName(familyName);
        return name;
    }

    public void generateTestLocations() {
        final LocationService locationService = Context.getLocationService();
        firstLocation = new Location();
        firstLocation.setName("Some Where");
        firstLocation.setDescription("Some Where");
        firstLocation.setDateCreated(parseDate("2009-08-15 15:28:54.0"));
        firstLocation.setRetired(false);
        firstLocation.setUuid(getRandomUuid());
        firstLocationId = locationService.saveLocation(firstLocation).getId();

        secondLocation = new Location();
        secondLocation.setName("Another Place");
        secondLocation.setDescription("Another Place");
        secondLocation.setDateCreated(parseDate("2009-08-15 15:28:54.0"));
        secondLocation.setRetired(false);
        secondLocation.setUuid(getRandomUuid());
        secondLocation.addChildLocation(firstLocation);
        secondLocationId = locationService.saveLocation(secondLocation).getId();
    }

    public void generateTestEncounters() {
        final EncounterService encounterService = Context.getEncounterService();
        firstEncounter = new Encounter();
        firstEncounter.setEncounterType(encounterService.getEncounterType(6));
        firstEncounter.setPatient(firstPatient);
        firstEncounter.setLocation(firstLocation);
        firstEncounter.setForm(Context.getFormService().getForm(1));
        firstEncounter.setEncounterDatetime(parseDate("2013-09-19 00:00:00.0"));
        firstEncounter.setCreator(Context.getUserService().getUser(1));
        firstEncounter.setDateCreated(parseDate("2012-08-19 18:34:40.0"));
        firstEncounter.setVoided(false);
        firstEncounter.setVoidReason("");
        firstEncounter.setUuid(getRandomUuid());
        firstEncounterId = encounterService.saveEncounter(firstEncounter).getId();

        secondEncounter = new Encounter();
        secondEncounter.setEncounterType(encounterService.getEncounterType(6));
        secondEncounter.setPatient(secondPatient);
        secondEncounter.setLocation(secondLocation);
        secondEncounter.setForm(Context.getFormService().getForm(1));
        secondEncounter.setEncounterDatetime(parseDate("2013-09-19 00:00:00.0"));
        secondEncounter.setCreator(Context.getUserService().getUser(1));
        secondEncounter.setDateCreated(parseDate("2012-08-19 18:34:40.0"));
        secondEncounter.setVoided(false);
        secondEncounter.setVoidReason("");
        secondEncounter.setUuid(getRandomUuid());
        secondEncounterId = encounterService.saveEncounter(secondEncounter).getEncounterId();
    }

    public void generateTestPatientPrograms() {
        final ProgramWorkflowService pws = Context.getProgramWorkflowService();
        firstPatientProgram = new PatientProgram();
        firstPatientProgram.setPatient(firstPatient);
        firstPatientProgram.setProgram(pws.getProgram(1));
        firstPatientProgram.setLocation(firstLocation);
        firstPatientProgram.setDateEnrolled(parseDate("2011-05-15 00:00:00.0"));
        firstPatientProgram.setCreator(Context.getUserService().getUser(1));
        firstPatientProgram.setDateCreated(parseDate("2011-04-01 00:00:00.0"));
        firstPatientProgram.setChangedBy(Context.getUserService().getUser(1));
        firstPatientProgram.setVoided(false);
        firstPatientProgram.setUuid(getRandomUuid());
        firstPatientProgramId = pws.savePatientProgram(firstPatientProgram).getId();

        secondPatientProgram = new PatientProgram();
        secondPatientProgram.setPatient(secondPatient);
        secondPatientProgram.setProgram(pws.getProgram(1));
        secondPatientProgram.setLocation(secondLocation);
        secondPatientProgram.setDateEnrolled(parseDate("2011-05-15 00:00:00.0"));
        secondPatientProgram.setCreator(Context.getUserService().getUser(1));
        secondPatientProgram.setDateCreated(parseDate("2011-04-01 00:00:00.0"));
        secondPatientProgram.setChangedBy(Context.getUserService().getUser(1));
        secondPatientProgram.setVoided(false);
        secondPatientProgram.setUuid(getRandomUuid());
        secondPatientProgramId = pws.savePatientProgram(secondPatientProgram).getId();
    }

    public int getFirstPatientId() {
        return firstPatientId;
    }

    public int getSecondPatientId() {
        return secondPatientId;
    }

    public int getFirstLocationId() {
        return firstLocationId;
    }

    public int getSecondLocationId() {
        return secondLocationId;
    }

    public int getFirstEncounterId() {
        return firstEncounterId;
    }

    public int getSecondEncounterId() {
        return secondEncounterId;
    }

    public Patient getFirstPatient() {
        return firstPatient;
    }

    public int getFirstPatientProgramId() {
        return firstPatientProgramId;
    }

    public int getSecondPatientProgramId() {
        return secondPatientProgramId;
    }

    private Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
        }
        catch (ParseException ex) {
            return null;
        }
    }

    private String getRandomUuid() {
        return java.util.UUID.randomUUID().toString();
    }
}
