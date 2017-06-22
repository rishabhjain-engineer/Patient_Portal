package com.applozic.audiovideo;

import android.util.Log;

/**
 * Created by rishabh on 22/6/17.
 */

public class GetDoctorCredentials {

    public static String getDoctorID() {
        Log.e("Rishabh","doc id:= "+mdoctorID);
        return mdoctorID;
    }

    public static void setDoctorID(String doctorID) {
        mdoctorID = doctorID;
    }

    private  static String mdoctorID ;

}
