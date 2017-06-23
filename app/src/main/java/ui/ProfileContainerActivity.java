package ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.hs.userportal.Helper;
import com.hs.userportal.Profile;
import com.hs.userportal.R;
import com.hs.userportal.Services;
import com.hs.userportal.Work;
import com.hs.userportal.residence;
import com.hs.userportal.update;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

import utils.PreferenceHelper;

/**
 * Created by ayaz on 17/2/17.
 */

public class ProfileContainerActivity extends BaseActivity {

    private String fbLinkedID, userId;
    private Services mServices;
    private static int noti = 0;
    private String facebookPic;
    private String pic = "", picname = "", thumbpic = "", oldfile = "Nofile", oldfile1 = "Nofile",  fbLinked = "false";
    public static String notiem = "no", notisms = "no";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_container);
        setupActionBar();
        mActionBar.setTitle("Profile");
        mServices = new Services(this);
        userId = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID);
        LinearLayout basicContainerLL = (LinearLayout) findViewById(R.id.basic_container);
        LinearLayout residenceContainer = (LinearLayout) findViewById(R.id.residence_container);
        LinearLayout workContainer = (LinearLayout) findViewById(R.id.work_container);

        basicContainerLL.setOnClickListener(mClickListener);
        residenceContainer.setOnClickListener(mClickListener);
        workContainer.setOnClickListener(mClickListener);
        if(isSessionExist()){
            new BackgroundProcess().execute();
        }
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.basic_container) {
                if(isSessionExist()){
                    Intent intent = new Intent(ProfileContainerActivity.this, update.class);
                    intent.putExtra("pic", pic);
                    intent.putExtra("picname", picname);
                    intent.putExtra("fbLinked", fbLinked);
                    intent.putExtra("fbLinkedID", fbLinkedID);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                }
            } else if (id == R.id.residence_container) {
                if(isSessionExist()) {
                    Intent intent = new Intent(ProfileContainerActivity.this, residence.class);
                    intent.putExtra("id", mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID));
              /*  intent.putExtra("pass", pass);
                intent.putExtra("pic", pic);
                intent.putExtra("picname", picname);
                intent.putExtra("fbLinked", fbLinked);
                intent.putExtra("fbLinkedID", fbLinkedID);*/
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                }

            } else if (id == R.id.work_container) {
                if(isSessionExist()) {
                    Intent intent = new Intent(ProfileContainerActivity.this, Work.class);
                    intent.putExtra("id", mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID));
               /* intent.putExtra("pass", pass);
                intent.putExtra("pic", pic);
                intent.putExtra("picname", picname);
                intent.putExtra("fbLinked", fbLinked);
                intent.putExtra("fbLinkedID", fbLinkedID);*/
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                }

            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                 overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

   // private ProgressDialog progress;
    private String path;
    JSONObject receiveDataFb, receiveData;
    JSONArray subArray;

    private class BackgroundProcess extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           /* progress = new ProgressDialog(ProfileContainerActivity.this);
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(true);
            progress.setMessage("Please wait...");
            progress.setIndeterminate(true);
            progress.show();*/
        }
        protected Void doInBackground(Void... arg0) {
            JSONObject sendDataFb = new JSONObject();
            try {
                sendDataFb.put("userId", userId);
                receiveDataFb = mServices.GetUserDetails(sendDataFb);
            } catch (Exception e) {
                e.printStackTrace();
                SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();
                finish();
            }
            if (receiveDataFb.toString().equals("{\"d\":\"{}\"}")) {
                SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();
                finish();
            }
            JSONObject sendData = new JSONObject();
            try {
                sendData.put("PatientId", userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            receiveData = mServices.verify(sendData);
            String data;
            try {

                data = receiveData.getString("d");
                JSONObject cut = new JSONObject(data);
                subArray = cut.getJSONArray("Table");
                try {
                    String emailid = subArray.getJSONObject(0).getString("Email");
                    Helper.resend_sms = subArray.getJSONObject(0).getString("ContactNo");
                    Helper.resend_email = emailid;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                path = subArray.getJSONObject(0).getString("Path");
                String img = path + subArray.getJSONObject(0).getString("Image");
                String thumbpic = path + subArray.getJSONObject(0).getString("ThumbImage");

                String oldfile1 = thumbpic;

                if (subArray.getJSONObject(0).getString("Validate").equals("0")) {
                    noti = noti + 1;
                    notiem = "yes";
                }

                if (subArray.getJSONObject(0).getString("validateContactNo").equals("0")) {
                    noti = noti + 1;
                    notisms = "yes";
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        invalidateOptionsMenu();

                    }
                });


            } catch (Exception e) {

                e.printStackTrace();
                SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();
                finish();
            }

            try {
                if (subArray.getJSONObject(0).getString("Image").matches((".*[a-kA-Zo-t]+.*")))
                {
                    String pic = subArray.getJSONObject(0).getString("Image");
                    String picname = subArray.getJSONObject(0).getString("ImageName");
                    if (pic.matches((".*[a-kA-Zo-t]+.*"))) {
                        oldfile = path + pic;
                    }
                } else {
                }
            } catch (Exception e) {
                e.printStackTrace();
                SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();
                finish();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            String fbData;
            try {
                if (noti != 0) {

                } else {

                }
                fbData = receiveDataFb.getString("d");
                JSONObject slice = new JSONObject(fbData);
                JSONArray fbSubArray = slice.getJSONArray("Table");
                if (fbSubArray.getJSONObject(0).getString("FacebookId").equals("") || fbSubArray.getJSONObject(0).getString("FacebookId").equals("null")) {
                    //facebooklink.setVisibility(View.VISIBLE); //TODO commented by spartans  ; to show fb link change visibility ;
                    fbLinked = "false";

                } else {
                    //facebooklink.setVisibility(View.VISIBLE);  //TODO visiblity changed by SPARTANS ( ADDITIONALY )
                    fbLinked = "true";
                    fbLinkedID = fbSubArray.getJSONObject(0).getString("FacebookId");
                }
                //findFamily();
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
           /* if (progress != null) {
                progress.dismiss();
                progress = null;
            }*/


            //   imageProgress.setVisibility(View.VISIBLE);  //////-------------------------------------Commented by us------------------------------
            // setProgressBarIndeterminateVisibility(true);
            new Thread() {
                public void run() {
                    try {

                        if (!subArray.getJSONObject(0).getString("Image").equalsIgnoreCase("null")) {
                            String abc = subArray.getJSONObject(0).getString("Image");
                            String def = subArray.getJSONObject(0).getString("ThumbImage");
                            String path = subArray.getJSONObject(0).getString("Path");
                            pic = path + abc;
                            thumbpic = path + def;
                            Bitmap bitmap;
                            if (abc.contains(".jpg") || abc.contains(".png") || abc.contains(".JPG") || abc.contains(".PNG") || abc.contains(".jpeg") || abc.contains(".JPEG")) {
                                bitmap = BitmapFactory.decodeStream((InputStream) new URL(pic.replaceAll(" ", "%20")).getContent());
                            } else {
                                bitmap = BitmapFactory.decodeStream((InputStream) new URL(pic.replaceAll(" ", "%20")).getContent());
                            }

                            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(output);

                            final Paint paint = new Paint();
                            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

                            paint.setAntiAlias(true);
                            canvas.drawARGB(0, 0, 0, 0);
                            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getHeight() / 2, paint);
                            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                            canvas.drawBitmap(bitmap, rect, rect, paint);
                        } else {
                            Bitmap bitmap;
                            bitmap = BitmapFactory.decodeStream((InputStream) new URL("https://graph.facebook.com/" +facebookPic+ "/picture?type=large").getContent());

                            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(output);

                            final Paint paint = new Paint();
                            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

                            paint.setAntiAlias(true);
                            canvas.drawARGB(0, 0, 0, 0);
                            // canvas.drawRect(rect, paint);
                            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getHeight() / 2, paint);
                            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                            canvas.drawBitmap(bitmap, rect, rect, paint);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();

            new Thread() {
                public void run() {
                    try {

                        JSONObject sendData = new JSONObject();
                        try {
                            sendData.put("ApplicationId", "");
                            sendData.put("DoctorId", "");
                            sendData.put("PatientId", userId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JSONObject receiveDataList = mServices.patientstatus(sendData);

                        System.out.println(receiveDataList);

                        try {
                            String dataList = receiveDataList.optString("d");// {"Table":[]}
                            if(!TextUtils.isEmpty(dataList)){
                                JSONObject cut = new JSONObject(dataList);
                                JSONArray subArrayList = cut.getJSONArray("Table");
                                String caseid = subArrayList.getJSONObject(0).getString("CaseId");
                                String casecode = subArrayList.getJSONObject(0).getString("CaseCode");
                                String dated = subArrayList.getJSONObject(0).getString("TimeStamp");

                                sendData = new JSONObject();
                                try {
                                    sendData.put("CaseId", caseid);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                JSONObject receiveDataList2 = mServices.patientinvestigation(sendData);

                                String data1 = receiveDataList2.getString("d");
                                JSONObject cut1 = new JSONObject(data1);
                                JSONArray subArray = cut1.getJSONArray("Table");

                                JSONArray subArray1 = subArray.getJSONArray(0);
                                for (int i = 0; i < subArray1.length(); i++) {
                                    //testcomplete.add(subArray1.getJSONObject(i).getString("IsTestCompleted"));
                                    //ispublished.add(subArray1.getJSONObject(i).getString("IsPublish"));
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {

                    }
                }
            }.start();

        }

    }
}
