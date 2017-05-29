package base;

/**
 * Created by ayaz on 12/4/17.
 */
import android.util.Log;
import android.widget.Toast;

import com.applozic.mobicomkit.api.account.register.RegisterUserClientService;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.hs.userportal.AppAplication;

import networkmngr.NetworkChangeListener;
import utils.PreferenceHelper;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
        if (MobiComUserPreference.getInstance(this).isRegistered()) {
            try {
                new RegisterUserClientService(this).updatePushNotificationId(refreshedToken);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        PreferenceHelper preferenceHelper = PreferenceHelper.getInstance();
        preferenceHelper.setString(PreferenceHelper.PreferenceKey.FCM_DEVICE_TOKEN, token);
        if(NetworkChangeListener.getNetworkStatus().isConnected()){
            new UserDeviceAsyncTask().execute();
        }else{
            Toast.makeText(AppAplication.getAppContext(), "No internet connection. Please retry.", Toast.LENGTH_SHORT).show();
        }
    }
}