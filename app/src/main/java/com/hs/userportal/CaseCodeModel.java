package com.hs.userportal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rishabh on 15/6/17.
 */

public class CaseCodeModel {

    private String caseCode;
    private String locationName;  // Name of lab , where patient visited for sample
    private String referrerName;  // who refer to this particular Lab .
    private String dateandTime ;
    private String caseID ;
    private String testLocationID ;
    private List<TestNames> listOfTestNamesInCaseCode = new ArrayList<>();   // one CaseCode can have multiple TEST done .
    private TestNames testNameObject;


    public CaseCodeModel(String caseCode) {
        this.caseCode = caseCode;
        testNameObject = new TestNames();
        listOfTestNamesInCaseCode.add(testNameObject);
    }

    public String getCaseCode() {
        return caseCode;
    }


    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getReferrerName() {
        return referrerName;
    }

    public void setReferrerName(String referrerName) {
        this.referrerName = referrerName;
    }

    public TestNames getTestNamesObject() {
        return testNameObject;
    }


    public void createNewTestNameObject(){
        testNameObject = new TestNames();
        listOfTestNamesInCaseCode.add(testNameObject);
    }

    public List<TestNames> getListOfTestNamesInCaseCode () {
        return listOfTestNamesInCaseCode;
    }
    public String getCaseID() {
        return caseID;
    }

    public void setCaseID(String caseID) {
        this.caseID = caseID;
    }

    public String gettestLocationID() {
        return testLocationID;
    }

    public void settestLocationID(String locationID) {
        this.testLocationID = locationID;
    }

    public String getDateandTime() {
        return dateandTime;
    }

    public void setDateandTime(String dateandTime) {
        this.dateandTime = dateandTime;
    }

}
