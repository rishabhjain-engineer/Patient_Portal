package base;

/**
 * Created by ayaz on 12/4/17.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.applozic.mobicomkit.api.notification.MobiComPushReceiver;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hs.userportal.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import ui.DashBoardActivity;
import ui.SignInActivity;
import utils.PreferenceHelper;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            JSONObject jsonObject = new JSONObject(remoteMessage.getData());
            String title = jsonObject.optString("contentTitle");
            String message = jsonObject.optString("message");
            String tickerText = jsonObject.optString("tickerText");
            sendNotification(title, message);
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            //sendNotification(remoteMessage.getNotification().getBody());
            sendNotification("Sciontra", remoteMessage.getNotification().getBody());
        }

        if (MobiComPushReceiver.isMobiComPushNotification(remoteMessage.getData())) {
            MobiComPushReceiver.processMessageAsync(this, remoteMessage.getData());
            return;
        }

    }

    public static final String INTENT_KEY = "THE_QUOTE";
    public void sendNotification(String title, String randomQuote) {
       /* Intent showFullQuoteIntent = new Intent(this, DashBoardActivity.class);
        //showFullQuoteIntent.putExtra(INTENT_KEY, "report");
        int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, uniqueInt, showFullQuoteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic)
                .setContentTitle(title)
                .setContentText(randomQuote)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);*/
        PreferenceHelper preferenceHelper = PreferenceHelper.getInstance();
        String sessionId = preferenceHelper.getString(PreferenceHelper.PreferenceKey.SESSION_ID);
        Intent intent = null;
        if(TextUtils.isEmpty(sessionId)){
            intent = new Intent(this, SignInActivity.class);
        }else{
            intent = new Intent(this, DashBoardActivity.class);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic)
                .setContentTitle(title)
                .setContentText(randomQuote)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int uniqueInteger = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        notificationManager.notify(uniqueInteger/* ID of notification */, notificationBuilder.build());

        //////////////////////////////////
       /* Intent notificationIntent = new Intent(context, YOUR_ACTIVITY.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentIntent(intent)
                .setPriority(PRIORITY_HIGH) //private static final PRIORITY_HIGH = 5;
                .setContentText(message)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());*/
    }

    private void sendNotification1(String messageBody) {
        Intent intent = new Intent(this, DashBoardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic)
                .setContentTitle("FCM Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void sendPushNotification(String message) {
        MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());
        Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("report", "report");
        mNotificationManager.showSmallNotification("message", message, intent);
    }
}