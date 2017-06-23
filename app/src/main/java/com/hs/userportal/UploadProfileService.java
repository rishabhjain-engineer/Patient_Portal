package com.hs.userportal;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ProgressEvent;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.readystatesoftware.simpl3r.UploadIterruptedException;

import org.json.JSONObject;

import java.io.File;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import config.StaticHolder;
import ui.SignInActivity;
import utils.PreferenceHelper;

public class UploadProfileService extends IntentService {

    private static final String ARG_FILE_PATH = "file_path";
    private static final String UPLOAD_STATE_CHANGED_ACTION = "com.readystatesoftware.simpl3r.example.UPLOAD_STATE_CHANGED_ACTION";
    private static final String UPLOAD_CANCELLED_ACTION = "com.readystatesoftware.simpl3r.example.UPLOAD_CANCELLED_ACTION";
    private static final String S3KEY_EXTRA = "s3key";
    private static final String PERCENT_EXTRA = "percent";
    private static final String MSG_EXTRA = "msg";
    private static final String uploadfrom = "uploadfrom";
    private JSONObject sendData;
    private String patientId;
    private static final int NOTIFY_ID_UPLOAD = 1337;
    private RequestQueue queue1, queue2;
    private AmazonS3Client s3Client;
    private Uploader1 uploader;
    private JsonObjectRequest jr1, jr2;
    private NotificationManager nm;
    private final Handler handler = new Handler();
    private String fname, afterDecode, uplodfrm,oldimage,oldthumbimage;
    protected PreferenceHelper mPreferenceHelper;

    public UploadProfileService() {
        super("simpl3r-example-upload");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        s3Client = new AmazonS3Client(new BasicAWSCredentials(getString(R.string.s3_access_key), getString(R.string.s3_secret)));
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        IntentFilter f = new IntentFilter();
        f.addAction(UPLOAD_CANCELLED_ACTION);
        registerReceiver(uploadCancelReceiver, f);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
       // SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
       // patientId = sharedPreferences.getString("ke", "");
        mPreferenceHelper = PreferenceHelper.getInstance();
        patientId = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID);
        String filePath = intent.getStringExtra(ARG_FILE_PATH);
        uplodfrm = intent.getStringExtra(uploadfrom);
        oldimage=intent.getStringExtra("oldimage");
        oldthumbimage=intent.getStringExtra("oldthumbimage");
        if(oldimage==null || oldimage.equals("null")){
            oldimage="";
        }
        if(oldthumbimage==null || oldthumbimage.equals("null")){
            oldthumbimage="";
        }
        String add_path = intent.getStringExtra("add_path");
        File fileToUpload = new File(filePath);
        final String s3ObjectKey = md5(filePath);
        String s3BucketName = getString(R.string.s3_bucket);
        String path;
        if(add_path!=null || add_path.equalsIgnoreCase("")){
            path = patientId + "/";
        }else{
            path = patientId + "/";
        }

        final String msg = "Uploading " + s3ObjectKey + "...";
        String splt[] = filePath.split("/");
        String imagename1 = splt[splt.length - 1];
        Calendar cal = Calendar.getInstance();
        fname = imagename1.substring(0, imagename1.length() - 4)
                + String.valueOf(cal.getTimeInMillis())
                + ".jpg";
        // create a new uploader for this file

        uploader = new Uploader1(this, s3Client, s3BucketName, s3ObjectKey, fileToUpload, path,fname);

        // listen for progress updates and broadcast/notify them appropriately
        uploader.setProgressListener(new Uploader1.UploadProgressListener() {
            @Override
            public void progressChanged(ProgressEvent progressEvent,
                                        long bytesUploaded, int percentUploaded) {

                Notification notification = buildNotification(msg, percentUploaded);
                nm.notify(NOTIFY_ID_UPLOAD, notification);
                broadcastState(s3ObjectKey, percentUploaded, msg);
            }
        });

        // broadcast/notify that our upload is starting
        Notification notification = buildNotification(msg, 0);
        nm.notify(NOTIFY_ID_UPLOAD, notification);
        broadcastState(s3ObjectKey, 0, msg);

        try {
            String s3Location = uploader.start(); // initiate the upload
            broadcastState(s3ObjectKey, -1, "File successfully uploaded to " + s3Location);
            System.out.println("File successfully uploaded to " + s3Location);
          //  Filevault.refresh();

            String[] parts = s3Location.split("com/" + "");
            System.out.println(parts[1].trim());
            String sendurl = parts[1].trim();

            afterDecode = URLDecoder.decode(sendurl, "UTF-8");//62181ffc-6f94-4b83-9334-395b8cb0960d/FileVault/251bc0e4-6fd0-4bf4-894c-c3bb99dde05e.jpg

            /*if (afterDecode.endsWith(".jpg")) {

                afterDecode = afterDecode.substring(0, afterDecode.length() - 4);

            }
*/
            System.out.println("afterdecode " + afterDecode);

            String imagename = splt[splt.length - 1];
            Calendar cal1 = Calendar.getInstance();
            fname = imagename.substring(0, imagename.length() - 4)
                    + String.valueOf(cal1.getTimeInMillis())
                    + ".jpg";

            sendData = new JSONObject();


            sendData.put("PatientId", patientId);
            sendData.put("OldImagePath", oldimage);
            sendData.put("OldImageThumb",oldthumbimage);
            sendData.put("ImageUrl", afterDecode);
            sendData.put("ImageName", fname);


            System.out.println(sendData);

            queue1 = Volley.newRequestQueue(this);
            /*String url1 = Services.init
					+ "/PatientModule/PatientService.asmx/PatientFileVaultNew";*/

//			String url1 = "http://192.168.1.122:8084/PatientModule/PatientService.asmx/PatientFileVault";
            StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.UploadProfilePic);
            final String url = sttc_holdr.request_Url();
            jr1 = new JsonObjectRequest(Request.Method.POST, url, sendData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Toast.makeText(getApplicationContext(), "Successfully uploaded", Toast.LENGTH_SHORT).show();
                                ((update) update.mcontext).refresh();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("error in onactivity:"
                            + error);

                }
            }) {
                @Override
                public Map<String, String> getHeaders()
                        throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("Cookie", Services.hoja);
                    System.out
                            .println("Services hoja:" + Services.hoja);
                    return headers;
                }
            };

            jr1.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue1.add(jr1);


        } catch (UploadIterruptedException uie) {
            broadcastState(s3ObjectKey, -1, "User interrupted");
        } catch (Exception e) {
            e.printStackTrace();
            broadcastState(s3ObjectKey, -1, "Error: " + e.getMessage());
        }
    }


   /* protected void uploadPrescriptionMail() {
        // TODO Auto-generated method stub

        queue2 = Volley.newRequestQueue(UploadProfileService.this);
			*//*String url = "https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/UploadPrescriptionMail";*//*
        JSONObject sendData = new JSONObject();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(UploadProfileService.this);
        String patientId = sharedPreferences.getString("ke", "");
        String locationFromCoordinates = LocationClass.locationFromCoordinates;
        try {
            sendData.put("patientId", patientId);
            sendData.put("imagePath", afterDecode + ".jpg");
            sendData.put("FileName", fname);
            sendData.put("Area", locationFromCoordinates);
            System.out.println("prescriptiondata" + sendData);
            StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.UploadPrescriptionMail);
            String url = sttc_holdr.request_Url();
            jr2 = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    System.out.println(response);

                    try {

                        if (response.getString("d").equalsIgnoreCase("Details sent successfully.")) {

                            Toast.makeText(getApplicationContext(),
                                    "We have received your prescription. Our representative will be in touch shortly.",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Error in Uploading File . Please check Internet Connection !", Toast.LENGTH_SHORT)
                                    .show();
                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("error in onactivity:" + error);

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("Cookie", Services.hoja);
                    System.out.println("Services hoja:" + Services.hoja);
                    return headers;
                }
            };
            jr2.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue2.add(jr2);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }*/


    @Override
    public void onDestroy() {
        nm.cancel(NOTIFY_ID_UPLOAD);
        unregisterReceiver(uploadCancelReceiver);
        super.onDestroy();
    }

    private void broadcastState(String s3key, int percent, String msg) {
        Intent intent = new Intent(UPLOAD_STATE_CHANGED_ACTION);
        Bundle b = new Bundle();
        b.putString(S3KEY_EXTRA, s3key);
        b.putInt(PERCENT_EXTRA, percent);
        b.putString(MSG_EXTRA, msg);

        intent.putExtras(b);
        sendBroadcast(intent);
    }

    private Notification buildNotification(String msg, int progress) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setWhen(System.currentTimeMillis());
        builder.setTicker(msg);
        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentText(msg);
        builder.setSmallIcon(R.drawable.ic);
        builder.setOngoing(true);
        builder.setProgress(100, progress, false);

        Intent notificationIntent = new Intent(this, SignInActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentIntent(contentIntent);

        return builder.build();
    }

    private BroadcastReceiver uploadCancelReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (uploader != null) {
                uploader.interrupt();
            }
        }
    };

    private String md5(String s) {
        try {
            // create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


}
