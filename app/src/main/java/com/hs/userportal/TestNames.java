package com.hs.userportal;

import android.util.Log;

/**
 * Created by rishabh on 15/6/17.
 */

public class TestNames {

    private String Description ;  // Name of the Test
    private Boolean isSampleReceived ;
    private Boolean isTestCompleted ;
    private Boolean isPublished ;
    private String investigationID ;
    private String testID ;
    private String labNo ;
    private String color ;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLabNo() {
        return labNo;
    }

    public void setLabNo(String labNo) {
        this.labNo = labNo;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Boolean isSampleReceived() {
        return isSampleReceived;
    }

    public void setSampleReceived(Boolean sampleReceived) {
        isSampleReceived = sampleReceived;
    }

    public Boolean isTestCompleted() {
        return isTestCompleted;
    }

    public void setTestCompleted(Boolean testCompleted) {
        isTestCompleted = testCompleted;
    }

    public Boolean isPublished() {
        return isPublished;
    }

    public void setPublished(Boolean published) {
        isPublished = published;
    }

    public String getInvestigationID() {
        return investigationID;
    }

    public void setInvestigationID(String investigationID) {
        this.investigationID = investigationID;
    }

    public String getTestID() {
        return testID;
    }

    public void setTestID(String testID) {
        this.testID = testID;
    }
}
