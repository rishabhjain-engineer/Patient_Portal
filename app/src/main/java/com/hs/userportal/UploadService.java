package com.hs.userportal;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import com.readystatesoftware.simpl3r.Uploader;
import com.readystatesoftware.simpl3r.Uploader.UploadProgressListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import config.StaticHolder;
import fragment.RepositoryFragment;
import ui.SignInActivity;

public class UploadService extends IntentService {

    public static final String ARG_FILE_PATH = "file_path";
    public static final String UPLOAD_STATE_CHANGED_ACTION = "com.readystatesoftware.simpl3r.example.UPLOAD_STATE_CHANGED_ACTION";
    public static final String UPLOAD_CANCELLED_ACTION = "com.readystatesoftware.simpl3r.example.UPLOAD_CANCELLED_ACTION";
    public static final String S3KEY_EXTRA = "s3key";
    public static final String PERCENT_EXTRA = "percent";
    public static final String MSG_EXTRA = "msg";
    public static final String uploadfrom = "uploadfrom";


    private JSONObject sendData;
    private String patientId;
    private static final int NOTIFY_ID_UPLOAD = 1337;
    private RequestQueue queue1, queue2;
    private AmazonS3Client s3Client;
    private Uploader uploader;
    private JsonObjectRequest jr1, jr2;
    private NotificationManager nm;
    private final Handler handler = new Handler();
    private String fname, afterDecode, uplodfrm;
    private String add_path, exhistimg, stringcheck;

    public UploadService() {
        super("simpl3r-example-upload");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        s3Client = new AmazonS3Client(
                new BasicAWSCredentials(getString(R.string.s3_access_key), getString(R.string.s3_secret)));
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        IntentFilter f = new IntentFilter();
        f.addAction(UPLOAD_CANCELLED_ACTION);
        registerReceiver(uploadCancelReceiver, f);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        patientId = sharedPreferences.getString("ke", "");
        String filePath = intent.getStringExtra(ARG_FILE_PATH);
        uplodfrm = intent.getStringExtra(uploadfrom);
        add_path = intent.getStringExtra("add_path");
        try {
            exhistimg = intent.getStringExtra("exhistimg");
            stringcheck = intent.getStringExtra("stringcheck");
        } catch (Exception e) {
            e.printStackTrace();
            exhistimg = "";
            stringcheck = "";
        }
        File fileToUpload = new File(filePath);
        final String s3ObjectKey = md5(filePath);
        String s3BucketName = getString(R.string.s3_bucket);
        final String path;
        if (add_path.equalsIgnoreCase("")) {
            if (uplodfrm.equalsIgnoreCase("notfilevault")) {
                path = patientId + "/" + "FileVault/Personal/Prescription/";
            } else {
                path = patientId + "/" + "FileVault/Personal/";
            }
        } else {
            path = patientId + "/" + "FileVault/Personal/" + add_path + "/";
        }

        final String msg = "Uploading " + s3ObjectKey + "...";

        // create a new uploader for this file
        String splt[] = filePath.split("/");
        String imagename = splt[splt.length - 1];
        Calendar cal = Calendar.getInstance();

        if (uplodfrm != null && uplodfrm.equalsIgnoreCase("notfilevault")) {
            fname = imagename;
        } else {
            if (exhistimg != null && exhistimg != "" && exhistimg.equalsIgnoreCase("true")) {
                fname = stringcheck.substring(0, stringcheck.length() - 4)
                        + "_1.jpg";
            } else {
                fname = imagename.substring(0, imagename.length() - 4)
                        + ".jpg";
            }
        }
        uploader = new Uploader(this, s3Client, s3BucketName, s3ObjectKey, fileToUpload, path, fname);
        // listen for progress updates and broadcast/notify them appropriately
        uploader.setProgressListener(new UploadProgressListener() {
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
            //
            String[] parts = s3Location.split("com/" + "");
            System.out.println(parts[1].trim());
            String sendurl = parts[1].trim();

            afterDecode = URLDecoder.decode(sendurl, "UTF-8");//62181ffc-6f94-4b83-9334-395b8cb0960d/FileVault/251bc0e4-6fd0-4bf4-894c-c3bb99dde05e.jpg
            String[] file_name = afterDecode.split("/");
            int len = file_name.length;

            if (afterDecode.endsWith(".jpg")) {

                // afterDecode = afterDecode.substring(0, afterDecode.length() - 4);

            }

            System.out.println("afterdecode " + afterDecode);


            sendData = new JSONObject();
           /* if (LocationClass.pic != null) {
                sendData.put("File", LocationClass.pic);
            } else if (MapLabDetails.pic_maplab != null) {
                sendData.put("File", MapLabDetails.pic_maplab);

            } else if (Filevault.pic != null) {
                sendData.put("File", Filevault.pic);
            }*/

            sendData.put("PatientId", patientId);
            sendData.put("ImageName", file_name[len - 1]);
            sendData.put("ImageUrl", afterDecode);
            sendData.put("Path", path);
            //sendData.put("File", Filevault.pic);
           /* sendData.put("FileUrl", afterDecode);*/
            /*sendData.put("FileName", fname);*/


            System.out.println(sendData);

            queue1 = Volley.newRequestQueue(this);
            /*String url1 = Services.init
                    + "/PatientModule/PatientService.asmx/PatientFileVaultNew";*/
            // https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/
            String url1 = "https://api.healthscion.com/WebServices/LabService.asmx/UploadImage";
           /* StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.PatientFileVaultNew);
            String url = sttc_holdr.request_Url();*/
            jr1 = new JsonObjectRequest(
                    Request.Method.POST, url1, sendData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            System.out.println(response);
                            try {


                                if (uplodfrm != null && uplodfrm.equalsIgnoreCase("notfilevault")) {
                                    LocationClass.pic = null;
                                    MapLabDetails.pic_maplab = null;
                                    uploadPrescriptionMail();
                                    Toast.makeText(getApplicationContext(),
                                            response.getString("d"),
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    Toast.makeText(getApplicationContext(),
                                            response.getString("d"),
                                            Toast.LENGTH_SHORT).show();
                                    if (response.getString("d").equalsIgnoreCase("success")) {

                                        RepositoryFragment.refresh();
                                        Filevault.Imguri = null;
                                           /* File photo = new File(Environment.getExternalStorageDirectory(), "test.jpg");
                                            photo.delete();*/

                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (!add_path.equalsIgnoreCase("") || uplodfrm.equals("filevault2")) {
                                                    Filevault2.refresh_filevault2();

                                                } else {

                                                }

                                            }
                                        }, 1000);
                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                response.getString("d"),
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }


                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
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


    protected void uploadPrescriptionMail() {
        // TODO Auto-generated method stub

        queue2 = Volley.newRequestQueue(UploadService.this);
            /*String url = "https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/UploadPrescriptionMail";*/
        JSONObject sendData = new JSONObject();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(UploadService.this);
        String patientId = sharedPreferences.getString("ke", "");
        String locationFromCoordinates = LocationClass.locationFromCoordinates;
        try {
            sendData.put("patientId", patientId);
            sendData.put("imagePath", afterDecode /*+ ".jpg"*/);
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

    }


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
