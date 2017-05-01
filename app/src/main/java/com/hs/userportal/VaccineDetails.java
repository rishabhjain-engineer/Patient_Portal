package com.hs.userportal;

import android.text.TextUtils;

import java.util.Comparator;

/**
 * Created by ayaz on 6/3/17.
 */

public class VaccineDetails implements Comparable<VaccineDetails> {

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
    private boolean header;
    private String headerString;
    private String doseFrequency;
    private boolean isSpecialDose;
    private String vaccineNameAndDose;

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

    @Override
    public int compareTo(VaccineDetails o) {
        int value = this.getAgeAt() < o.getAgeAt() ? 1 : (this.getAgeAt() > o.getAgeAt() ? -1 : 0);
        if (value != 0) {
            return value;
        } else {
            String s1 = this.getVaccineNameAndDose();
            String s2 = o.getVaccineNameAndDose();
            int sComp = s1.compareTo(s2);
            return sComp;
        }
    }

    public boolean isHeader() {
        return header;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }

    public String getHeaderString() {
        return headerString;
    }

    public void setHeaderString(String headerString) {
        this.headerString = headerString;
    }

    public String getDoseFrequency() {
        return doseFrequency;
    }

    public void setDoseFrequency(String doseFrequency) {
        this.doseFrequency = doseFrequency;
    }

    public boolean isSpecialDose() {
        return isSpecialDose;
    }

    public void setSpecialDose(boolean specialDose) {
        isSpecialDose = specialDose;
    }

    public String getVaccineNameAndDose() {
        return vaccineNameAndDose;
    }

    public void setVaccineNameAndDose(String vaccineNameAndDose) {
        this.vaccineNameAndDose = vaccineNameAndDose;
    }

    /*public static class VaccineDetailsComparator implements Comparator<VaccineDetails> {

        public int compare(VaccineDetails firstObject, VaccineDetails secondObject) {

            return (firstObject.getAgeAt() - secondObject.getAgeAt());

            if (flag == 0) {
                if (firstObject.getLinkTo() == secondObject.getLinkTo()) {
                    flag = 0;                           //if both are null return 0
                } else if (TextUtils.isEmpty(firstObject.getLinkTo()) || firstObject.getLinkTo().equalsIgnoreCase("null")) {
                    flag = 1;
                } else if (TextUtils.isEmpty(secondObject.getLinkTo()) || secondObject.getLinkTo().equalsIgnoreCase("null")) {
                    flag = -1;
                } else {
                    flag = Integer.parseInt(firstObject.getLinkTo().trim()) - Integer.parseInt(secondObject.getLinkTo().trim());
                }
            }
        }
    }*/
}
