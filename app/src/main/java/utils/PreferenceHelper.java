package utils;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;

import com.hs.userportal.AppAplication;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class PreferenceHelper {

    private static PreferenceHelper INSTANCE = null;
    private SharedPreferences mSharedPreferences = null;
    private static final String PREFERENCE_FILE_NAME = "patient_pref_file";

    public static PreferenceHelper getInstance(){
        if(INSTANCE == null){
            INSTANCE = new PreferenceHelper();
        }
        return INSTANCE;
    }

    private PreferenceHelper(){
        mSharedPreferences = AppAplication.getAppContext().getSharedPreferences(PREFERENCE_FILE_NAME, 0);
    }

    public enum PreferenceKey{

        IS_ALL_QUESTION_ASKED("false"),

        PASSWORD("null"),

        QUESTION_COUNT("10"),

        USER_ID("null"),

        USER("null"),

        PATIENT_CODE("null"),

        PASS("null"),

        USER_NAME("null"),

        FROM_DATE("null"),

        MESSAGE_AT_SIGN_IN_UP("null"),

        FACE_BOOK_ID("null"),

        SESSION_ID("null"),

        FCM_DEVICE_TOKEN("null"),

        ON_DASH_BOARD_DEVICE_TKEN_SEND("false"),

        PATIENT_BUSINESS_FLAG(""),

        CONSULT_ID("null"),

        TO_DATE("null");

        private String defaultValue;

        private PreferenceKey(String key) {
        }

        public String getDefaultValue() {
            return defaultValue;
        }
    }

    public String getString(PreferenceKey key){
        return mSharedPreferences.getString(key.name(), "");
    }

    public void setString(PreferenceKey key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key.name(), value);
        editor.apply();
    }

    public boolean getBoolen(PreferenceKey key){
        return mSharedPreferences.getBoolean(key.name(), false);
    }

    public void setBoolean(PreferenceKey key, boolean value){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key.name(), value);
        editor.commit();
    }

    public int getInt(PreferenceKey key){
        return mSharedPreferences.getInt(key.name(), 0);
    }

    public void setInt(PreferenceKey key, int value){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key.name(), value);
        editor.commit();
    }
}
