package utils;

import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.applozic.mobicommons.people.channel.Conversation;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ayaz on 27/1/17.
 */

public class AppConstant {

    public static final String MyPREFERENCES = "MyPrefs";
    public static String[] mDurationModeArray = {"Daily", "Weekly", "Monthly", "Quarterly", "Semi-Annually", "Annually"};
    public static String[] mDateModeArray = {"exactly", "monthly", "yearly"};
    public static ArrayList<HashMap<String, String>> mFamilyMembersList = new ArrayList<>();
    public static final String AMAZON_URL = "https://files.healthscion.com/";

    public static String EMAIL = "";

    public static String CONTACT_NO = "";

    public static String ID = "";

    public static int WEIGHT_REQUEST_CODE = 1001;
    public static int HEIGHT_REQUEST_CODE = 1002;
    public static int BMI_REQUEST_CODE = 1003;
    public static int BP_REQUEST_CODE = 1004;
    public static int CASECODE_REQUEST_CODE = 1005;

    public static boolean isToRefereshVaccine = true;
    public static String CONSULT_MODE = "video";

    public static final String getPatientName() {
        return "Shalini";
    }
    public static final String getPatientID() {
        return "be2ce808-6250-4874-a239-31d60d1d8567"; //    shalini
    }



    public static final String getDoctorName() {
        return "Rahul Sharma";
    }
    public static final String getDoctorId() {
        return "E276CC08-BEAF-4E65-BFFA-95F035CBEEFD";  // suniltest33 --> DOctor
    }

}
