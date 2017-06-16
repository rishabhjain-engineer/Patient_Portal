package com.hs.userportal;

/**
 * Created by rishabh on 15/6/17.
 */

public class TestNames {

    private String Description ;  // Name of the Test
    private boolean isSampleReceived ;
    private boolean isTestCompleted ;
    private boolean isPublished ;
    private String investigationID ;
    private String testID ;

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public boolean isSampleReceived() {
        return isSampleReceived;
    }

    public void setSampleReceived(boolean sampleReceived) {
        isSampleReceived = sampleReceived;
    }

    public boolean isTestCompleted() {
        return isTestCompleted;
    }

    public void setTestCompleted(boolean testCompleted) {
        isTestCompleted = testCompleted;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
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
