package models;

import android.content.Intent;

import java.io.Serializable;

/**
 * Created by ayaz on 5/6/17.
 */

public class DoctorDetails implements Serializable{
    private String doctorName;
    private String location;
    private String medicineType;
    private Integer doctorImage;
    private String aboutDoctor;

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMedicineType() {
        return medicineType;
    }

    public void setMedicineType(String medicineType) {
        this.medicineType = medicineType;
    }

    public Integer getDoctorImage() {
        return doctorImage;
    }

    public void setDoctorImage(Integer doctorImage) {
        this.doctorImage = doctorImage;
    }

    public String getAboutDoctor() {
        return aboutDoctor;
    }

    public void setAboutDoctor(String aboutDoctor) {
        this.aboutDoctor = aboutDoctor;
    }
}
