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

    public static final String getpatienDoctorId() {
        return "233";
    }

    public static final String getPatientID() {
        return "ewrrt";
    }

    public static final String getpatienDoctorName() {
        if (ConversationActivity.isPatient) {
            //372fd208-69b7-44e7-a097-0015f26bd433            2907 Shailza Thakur   Doctor
            return "Rahul Sharma";

        } else {
            //97e9496b-8630-4d61-9f13-d7e95c0ad6a7            4903   Balveer  Patient
            return "Shalini";
        }
    }

    public static final String getDoctorId() {
        return "dfsdf";
    }

}
