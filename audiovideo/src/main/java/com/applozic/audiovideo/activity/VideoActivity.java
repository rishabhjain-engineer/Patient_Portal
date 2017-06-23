package com.applozic.audiovideo.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.applozic.audiovideo.GetDoctorCredentials;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.twilio.video.CameraCapturer;
import com.twilio.video.VideoView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import applozic.com.audiovideo.R;


public class VideoActivity extends AudioCallActivityV2 {
    private static final String TAG = VideoActivity.class.getName();
    private LinearLayout videoOptionlayout;
    private TextView mSymptomsTextView, mNoteTextView;
    private ImageView showFilesIv;
    private JSONArray values;
    public static String hoja = "";
    private static final String OPEN_DOCTOR_PRESCRIPTION = "com.hs.userportal.ui.DoctorPrescriptionActivity";
    private static final String OPEN_PATIENT_PAST_VISIT = "com.hs.userportal.ui.PastVisitActivity";
    public VideoActivity() {
        super(true);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        showFilesIv = (ImageView) findViewById(R.id.show_files);
        showFilesIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File fileReport = new File("/storage/emulated/0/Lab Pdf/Mr. Sunil  Raireport.pdf");

                new GetFileFromAWS().execute();

              //  String pathName = "https://files.healthscion.com/file";

                PackageManager packageManager = getPackageManager();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setType("application/pdf");
                List list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

                if (list.size() > 0 && fileReport.isFile()) {

                    Intent objIntent = new Intent(Intent.ACTION_VIEW);
                    ///////
                    Uri uri = null;
                    //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    Method m = null;
                    try {
                        m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                        m.invoke(null);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    uri = Uri.fromFile(fileReport);
                    /*} else {
                        uri = FileProvider.getUriForFile(ReportRecords.this, getApplicationContext().getPackageName() + ".provider", fileReport);
                    }*/
                    /////
                    objIntent.setDataAndType(uri, "application/pdf");
                    objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(objIntent);//Staring the pdf viewer
                } else if (!fileReport.isFile()) {
                    Log.v("ERROR!!!!", "OOPS2");
                } else if (list.size() <= 0) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(VideoActivity.this);
                    dialog.setTitle("PDF Reader not found");
                    dialog.setMessage("A PDF Reader was not found on your device. The Report is saved at " + fileReport.getAbsolutePath());
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            }
        });
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        init();

        contactName = (TextView) findViewById(R.id.contact_name);
        //profileImage = (ImageView) findViewById(R.id.applozic_audio_profile_image);
        txtCount = (TextView) findViewById(R.id.applozic_audio_timer);

        mSymptomsTextView = (TextView) findViewById(R.id.symptoms);
        mNoteTextView = (TextView) findViewById(R.id.notes);
        Intent intent = getIntent();
        String symptoms = intent.getStringExtra("symptoms");
        String notes = intent.getStringExtra("notes");

        if (TextUtils.isEmpty(symptoms)) {
            mSymptomsTextView.setText("Symptoms:");
        } else {
            mSymptomsTextView.setText("Symptoms: \n" + symptoms);
        }

        if (TextUtils.isEmpty(notes)) {
            mNoteTextView.setText("Notes:");
        } else {
            mNoteTextView.setText("Notes: \n" + notes);
        }


        contactName.setText(contactToCall.getDisplayName());
        pauseVideo = true;

        primaryVideoView = (VideoView) findViewById(R.id.primary_video_view);
        thumbnailVideoView = (VideoView) findViewById(R.id.thumbnail_video_view);

        videoStatusTextView = (TextView) findViewById(R.id.video_status_textview);
        videoStatusTextView.setVisibility(View.GONE);

        connectActionFab = (FloatingActionButton) findViewById(R.id.call_action_fab);
        switchCameraActionFab = (FloatingActionButton) findViewById(R.id.switch_camera_action_fab);
        localVideoActionFab = (FloatingActionButton) findViewById(R.id.local_video_action_fab);
        muteActionFab = (FloatingActionButton) findViewById(R.id.mute_action_fab);
        speakerActionFab = (FloatingActionButton) findViewById(R.id.speaker_action_fab);
        videoOptionlayout = (LinearLayout) findViewById(R.id.video_call_option);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.video_container);

        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideShowWithAnimation();
                return false;
            }
        });

        final LinearLayout videoContainer = (LinearLayout) findViewById(R.id.video_container_ll);
        final LinearLayout textContainer = (LinearLayout) findViewById(R.id.text_container);
        final LinearLayout bottomLinearLayout = (LinearLayout) findViewById(R.id.bottom_Linear_layout);
        //videoContainer.setOnTouchListener(VideoActivity.this);
        //videoContainer.getRootView().setOnDragListener(VideoActivity.this);

        bottomLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textContainer.setVisibility(View.VISIBLE);
                bottomLinearLayout.setVisibility(View.GONE);
                float value = 200;
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                final float px = value * (metrics.densityDpi / 160f);
                FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) videoContainer.getLayoutParams();
                params2.height = (int) px;
                params2.width = (int) px;
                params2.gravity = Gravity.RIGHT;
                videoContainer.bringToFront();
                videoContainer.setLayoutParams(params2);
                FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) textContainer.getLayoutParams();
                params1.height = LinearLayout.LayoutParams.MATCH_PARENT;
                params1.width = LinearLayout.LayoutParams.MATCH_PARENT;
                textContainer.setLayoutParams(params1);
                connectActionFab.setVisibility(View.GONE);
                thumbnailVideoView.setVisibility(View.GONE);
                contactName.setVisibility(View.GONE);
                hideShowOnExpandAndCollapse(true);
            }
        });


        /*videoContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) textContainer.getLayoutParams();
                params1.weight = 3.0f;
                textContainer.setLayoutParams(params1);

                LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) videoContainer.getLayoutParams();
                params2.weight = 7.0f;
                videoContainer.setLayoutParams(params2);
            }
        });

        textContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) videoContainer.getLayoutParams();
                params1.weight = 3.0f;
                videoContainer.setLayoutParams(params1);

                LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) textContainer.getLayoutParams();
                params2.weight = 7.0f;
                textContainer.setLayoutParams(params2);
            }
        });*/
        float value = 200;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        final float px = value * (metrics.densityDpi / 160f);


        videoContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textContainer.setVisibility(View.GONE);
                bottomLinearLayout.setVisibility(View.VISIBLE);
                FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) videoContainer.getLayoutParams();
                params1.height = LinearLayout.LayoutParams.MATCH_PARENT;
                params1.width = LinearLayout.LayoutParams.MATCH_PARENT;
                videoContainer.setLayoutParams(params1);
                FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) textContainer.getLayoutParams();
                params2.height = (int) px;
                params2.width = (int) px;
                params2.gravity = Gravity.RIGHT;
                textContainer.bringToFront();
                textContainer.setLayoutParams(params2);
                connectActionFab.setVisibility(View.VISIBLE);
                thumbnailVideoView.setVisibility(View.VISIBLE);
                contactName.setVisibility(View.VISIBLE);
                hideShowOnExpandAndCollapse(false);
            }
        });

        textContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) videoContainer.getLayoutParams();
                params2.height = (int) px;
                params2.width = (int) px;
                params2.gravity = Gravity.RIGHT;
                videoContainer.bringToFront();
                videoContainer.setLayoutParams(params2);
                FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) textContainer.getLayoutParams();
                params1.height = LinearLayout.LayoutParams.MATCH_PARENT;
                params1.width = LinearLayout.LayoutParams.MATCH_PARENT;
                textContainer.setLayoutParams(params1);
                connectActionFab.setVisibility(View.GONE);
                thumbnailVideoView.setVisibility(View.GONE);
                contactName.setVisibility(View.GONE);
                hideShowOnExpandAndCollapse(true);

            }
        });

        /*
         * Enable changing the volume using the up/down keys during a conversation
         */
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        /*
         * Needed for setting/abandoning audio focus during call
         */
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        /*
         * Check camera and microphone permissions. Needed in Android M.
         */
        if (!checkPermissionForCameraAndMicrophone()) {
            requestPermissionForCameraAndMicrophone();
        } else {
            createAudioAndVideoTracks();
            intializeUI();
            initializeApplozic();
        }

    }

    private void hideShowWithAnimation() {

        //Camera Actions
        if (switchCameraActionFab.isShown()) {
            switchCameraActionFab.hide();
        } else {
            switchCameraActionFab.show();

        }
        //Mute Actions
        if (muteActionFab.isShown()) {
            muteActionFab.hide();
        } else {
            muteActionFab.show();

        }

        if (localVideoActionFab.isShown()) {
            localVideoActionFab.hide();
        } else {
            localVideoActionFab.show();
        }

        if (speakerActionFab.isShown()) {
            speakerActionFab.hide();
        } else {
            speakerActionFab.show();
        }
    }

    private void hideShowOnExpandAndCollapse(boolean value) {
        if (value) {
            //Camera Actions
            if (switchCameraActionFab.isShown()) {
                switchCameraActionFab.hide();
            }
            //Mute Actions
            if (muteActionFab.isShown()) {
                muteActionFab.hide();
            }

            if (localVideoActionFab.isShown()) {
                localVideoActionFab.hide();
            }

            if (speakerActionFab.isShown()) {
                speakerActionFab.hide();
            }
        } else {
            //Camera Actions
            if (!switchCameraActionFab.isShown()) {
                switchCameraActionFab.show();
            }
            //Mute Actions
            if (!muteActionFab.isShown()) {
                muteActionFab.show();
            }

            if (!localVideoActionFab.isShown()) {
                localVideoActionFab.show();
            }

            if (!speakerActionFab.isShown()) {
                speakerActionFab.show();
            }
        }
    }


    @Override
    public void initializeApplozic() {
        super.initializeApplozic();

    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ConversationActivity.isPatient) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(OPEN_PATIENT_PAST_VISIT);
            startActivity(intent);
        } else {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(OPEN_DOCTOR_PRESCRIPTION);
            startActivity(intent);
        }
    }

    /*
     * The initial state when there is no active conversation.
     */
    @Override
    protected void setDisconnectAction() {
        super.setDisconnectAction();
        if (isFrontCamAvailable(getBaseContext())) {
            switchCameraActionFab.show();
            switchCameraActionFab.setOnClickListener(switchCameraClickListener());
        } else {
            switchCameraActionFab.hide();
        }
        localVideoActionFab.show();
        localVideoActionFab.setOnClickListener(localVideoClickListener());
    }

    private View.OnClickListener switchCameraClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraCapturer != null) {
                    CameraCapturer.CameraSource cameraSource = cameraCapturer.getCameraSource();
                    cameraCapturer.switchCamera();
                    if (thumbnailVideoView.getVisibility() == View.VISIBLE) {
                        thumbnailVideoView.setMirror(cameraSource == CameraCapturer.CameraSource.BACK_CAMERA);
                    } else {
                        primaryVideoView.setMirror(cameraSource == CameraCapturer.CameraSource.BACK_CAMERA);
                    }
                }
            }
        };
    }

    private View.OnClickListener localVideoClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Enable/disable the local video track
                 */
                if (localVideoTrack != null) {
                    boolean enable = !localVideoTrack.isEnabled();
                    localVideoTrack.enable(enable);
                    int icon;
                    if (enable) {
                        icon = R.drawable.ic_videocam_green_24px;
                        switchCameraActionFab.show();
                    } else {
                        icon = R.drawable.ic_videocam_off_red_24px;
                        switchCameraActionFab.hide();
                    }
                    localVideoActionFab.setImageDrawable(
                            ContextCompat.getDrawable(VideoActivity.this, icon));
                }
            }
        };
    }


    public boolean isFrontCamAvailable(Context context) {

        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            return true;
        } else {
            return false;
        }
    }

   /* public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(null, shadowBuilder, view, 0);
            view.setVisibility(View.INVISIBLE);
            return true;
        } else {
            return false;
        }
    }

    public boolean onDrag(View v, DragEvent event) {

        switch (event.getAction()) {
            case DragEvent.ACTION_DROP:

                float X = event.getX();
                float Y = event.getY();

                View view = (View) event.getLocalState();
                view.setX(X-(view.getWidth()/2));
                view.setY(Y-(view.getHeight()/2));
                view.setVisibility(View.VISIBLE);

            default:
                break;
        }
        return true;
    }*/

   private class GetFileFromAWS extends AsyncTask<Void,Void,Void>{

       @Override
       protected Void doInBackground(Void... params) {

           postData();
           return null;
       }
   }

    public void postData() {


        String getDoctorID = GetDoctorCredentials.getDoctorID() ;
        Log.e("Rishabh", "getDocID in app := "+getDoctorID);

       /* JSONObject data = new JSONObject();
        try {
            data.put("doctorId",getDoctorID);
        } catch (JSONException je) {
            je.printStackTrace();
        }*/


        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://192.168.1.11/WebServices/LabService.asmx/GetPatientInfo");
        httppost.setHeader("Content-type", "application/json");
        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("Cookie", hoja);

        /*try {
            httppost.setEntity(new StringEntity(data.toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair("doctorId", getDoctorID));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line+ "\n");
            }

            JSONObject receiveData = new JSONObject(sb.toString());

            // Log.i("TEST DETAILS", receiveData.toString());

            String p = receiveData.get("d").toString();
            Log.e("Rishabh","p := "+p);
            JSONObject temp = new JSONObject(p);
            values = temp.getJSONArray("Table");
            Log.e("Rishabh","values  "+values.toString() );

        }  catch (IOException e) {
            Log.e("Rishabh","e io:= "+e);
        } catch (JSONException e) {
            Log.e("Rishabh","je io:= "+e);
        }
    }

}
