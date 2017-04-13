package base;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.hs.userportal.AppAplication;
import com.hs.userportal.Services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.PreferenceHelper;

/**
 * Created by ayaz on 13/4/17.
 */

public class UserDeviceAsyncTask extends AsyncTask {

    private JSONObject dataToSend, receiveData1;
    private PreferenceHelper preferenceHelper;
    private String userId;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        preferenceHelper = PreferenceHelper.getInstance();
        String devicetoken = preferenceHelper.getString(PreferenceHelper.PreferenceKey.DEVICE_TOKEN);
        userId = preferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID);
        dataToSend = new JSONObject();
        try {
            dataToSend.put("devicetoken", devicetoken);
            if (TextUtils.isEmpty(userId)) {
                dataToSend.put("userid", JSONObject.NULL);
            } else {
                dataToSend.put("userid", userId);
            }
            dataToSend.put("type", "Android");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected Object doInBackground(Object[] params) {
        Services service = new Services(AppAplication.getAppContext());
        receiveData1 = service.saveUserDevice(dataToSend);
        String data = receiveData1.optString("d");
        if(!TextUtils.isEmpty(data) && data.contains("success") && !TextUtils.isEmpty(userId)){
            preferenceHelper.setString(PreferenceHelper.PreferenceKey.ON_DASH_BOARD_DEVICE_TKEN_SEND, "true");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }
}
