package com.hs.userportal;

import java.util.Comparator;

/**
 * Created by android1 on 25/2/17.
 */

public class GraphDetailValueAndDate{
    private String value;
    private String date;
    private String caseCode;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCaseCode() {
        return caseCode;
    }

    public void setCaseCode(String caseCode) {
        this.caseCode = caseCode;
    }

   /* @Override
    public int compareTo(GraphDetailValueAndDate o) {
        return this.date.compareTo(o.getDate());
    }*/

    /*public static class GraphDetailValueAndDateComparator implements Comparator<GraphDetailValueAndDate> {

        @Override
        public int compare(GraphDetailValueAndDate o1, GraphDetailValueAndDate o2) {
            return o1.getDate().compareTo(o2.getDate());
        }
    }*/
}
