package com.hs.userportal;

/**
 * Created by ayaz on 6/3/17.
 */

public class VaccineDetails {

    private String vaccineName;
    private String vaccineID;
    private String vaccineNameInShort;
    private int ageAt;
    private int ageTo;
    private String VaccineDose;
    private String VaccineDoseType;
    private String vaccineComment;
    private String vaccineDateTime;
    private String DoctorNotes;
    private String patientVaccineId;

    public String getVaccineName() {
        return vaccineName;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    public String getVaccineID() {
        return vaccineID;
    }

    public void setVaccineID(String vaccineID) {
        this.vaccineID = vaccineID;
    }

    public String getVaccineNameInShort() {
        return vaccineNameInShort;
    }

    public void setVaccineNameInShort(String vaccineNameInShort) {
        this.vaccineNameInShort = vaccineNameInShort;
    }

    public int getAgeAt() {
        return ageAt;
    }

    public void setAgeAt(int ageAt) {
        this.ageAt = ageAt;
    }

    public int getAgeTo() {
        return ageTo;
    }

    public void setAgeTo(int ageTo) {
        this.ageTo = ageTo;
    }

    public String getVaccineDose() {
        return VaccineDose;
    }

    public void setVaccineDose(String vaccineDose) {
        VaccineDose = vaccineDose;
    }

    public String getVaccineDoseType() {
        return VaccineDoseType;
    }

    public void setVaccineDoseType(String vaccineDoseType) {
        VaccineDoseType = vaccineDoseType;
    }

    public String getVaccineComment() {
        return vaccineComment;
    }

    public void setVaccineComment(String vaccineComment) {
        this.vaccineComment = vaccineComment;
    }

    public String getVaccineDateTime() {
        return vaccineDateTime;
    }

    public void setVaccineDateTime(String vaccineDateTime) {
        this.vaccineDateTime = vaccineDateTime;
    }

    public String getDoctorNotes() {
        return DoctorNotes;
    }

    public void setDoctorNotes(String doctorNotes) {
        DoctorNotes = doctorNotes;
    }

    public String getPatientVaccineId() {
        return patientVaccineId;
    }

    public void setPatientVaccineId(String patientVaccineId) {
        this.patientVaccineId = patientVaccineId;
    }
}
