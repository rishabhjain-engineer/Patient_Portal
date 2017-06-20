package utils;

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
    public static boolean isPatient = true;
    public static String CONSULT_MODE = null;
}
