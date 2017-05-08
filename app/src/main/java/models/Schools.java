package models;

import com.hs.userportal.Services;

import java.io.Serializable;

/**
 * Created by ayaz on 26/4/17.
 */

public class Schools implements Serializable {

    private String dateOfExamination;
    private String doctorName;
    private String doctorDesignation;
    private String staffId;

    public String getDateOfExamination() {
        return dateOfExamination;
    }

    public void setDateOfExamination(String dateOfExamination) {
        this.dateOfExamination = dateOfExamination;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorDesignation() {
        return doctorDesignation;
    }

    public void setDoctorDesignation(String doctorDesignation) {
        this.doctorDesignation = doctorDesignation;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }
}
