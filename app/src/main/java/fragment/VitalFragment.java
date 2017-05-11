package fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hs.userportal.Allergy;
import com.hs.userportal.AppAplication;
import com.hs.userportal.Authentication;
import com.hs.userportal.MyHealth;
import com.hs.userportal.R;
import com.hs.userportal.Services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import config.StaticHolder;
import networkmngr.NetworkChangeListener;
import ui.BaseActivity;
import ui.HealthCommonActivity;
import ui.VaccineActivity;
import utils.AppConstant;
import utils.PreferenceHelper;

/**
 * Created by ayaz on 10/4/17.
 */

public class VitalFragment extends Fragment {
    private Activity mActivity;
    private PreferenceHelper mPreferenceHelper;
    private TextView heighttxt_id, alergytxtid, bloodID, weight_latest, height_latest, allergies, mBpTvValue, mBmiTvValue;
    private EditText blood_group;
    private String id, show_blood, bgroup, height, weight, mBp;
    private LinearLayout bgHeader, weightLayout, heightLayout, allergyLayout, mBmiContainer, mBpContainer, mVaccineContainer;
    private Services service;
    private RequestQueue send_request;
    private ProgressDialog progress;
    private String[] bloodList = {"O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-"};
    private int allergy_no = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.myhealth, null);
        mActivity = getActivity();
        mPreferenceHelper = PreferenceHelper.getInstance();
        service = new Services(mActivity);
        TextView hederTitle = (TextView) view.findViewById(R.id.header_title_tv);
        hederTitle.setText("Vitals");
        mBpTvValue = (TextView) view.findViewById(R.id.bp_tv);
        mBmiTvValue = (TextView) view.findViewById(R.id.bmi_tv_2);
        heighttxt_id = (TextView) view.findViewById(R.id.heighttxt_id);
        alergytxtid = (TextView) view.findViewById(R.id.allergytxtid);
        blood_group = (EditText) view.findViewById(R.id.blood_group);
        bloodID = (TextView) view.findViewById(R.id.bloodID);
        weight_latest = (TextView) view.findViewById(R.id.weight_latest);
        height_latest = (TextView) view.findViewById(R.id.height_latest);
        allergies = (TextView) view.findViewById(R.id.allergies);
        bgHeader = (LinearLayout) view.findViewById(R.id.bgHeader);
        weightLayout = (LinearLayout) view.findViewById(R.id.weightLayout);
        heightLayout = (LinearLayout) view.findViewById(R.id.heightLayout);
        allergyLayout = (LinearLayout) view.findViewById(R.id.allergyLayout);
        mBmiContainer = (LinearLayout) view.findViewById(R.id.bmi_container);
        mBpContainer = (LinearLayout) view.findViewById(R.id.bp_container);
        mVaccineContainer = (LinearLayout) view.findViewById(R.id.vaccine_container);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
            Toast.makeText(mActivity, "No internet connection. Please retry.", Toast.LENGTH_SHORT).show();
        } else {
            new Authentication(mActivity, "MyHealth", "").execute();
        }
        id = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID);

        bgHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkChangeListener.getNetworkStatus().isConnected()) {
                    showdialog();
                } else {
                    Toast.makeText(AppAplication.getAppContext(), "No internet connection. Please retry.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        weightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent in = new Intent(MyHealth.this, Weight.class);
                if (((BaseActivity) mActivity).isSessionExist()) {
                    if (NetworkChangeListener.getNetworkStatus().isConnected()) {
                        Intent in = new Intent(mActivity, HealthCommonActivity.class);
                        in.putExtra("id", id);
                        in.putExtra("forWeight", true);
                        startActivity(in);
                        mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    } else {
                        Toast.makeText(AppAplication.getAppContext(), "No internet connection. Please retry.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        heightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent in = new Intent(MyHealth.this, Height.class);
                if (((BaseActivity) mActivity).isSessionExist()) {
                    if (NetworkChangeListener.getNetworkStatus().isConnected()) {
                        Intent in = new Intent(mActivity, HealthCommonActivity.class);
                        in.putExtra("id", id);
                        in.putExtra("forHeight", true);
                        startActivity(in);
                        mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    } else {
                        Toast.makeText(AppAplication.getAppContext(), "No internet connection. Please retry.", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        allergyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((BaseActivity) mActivity).isSessionExist()) {
                    if (NetworkChangeListener.getNetworkStatus().isConnected()) {
                        Intent in = new Intent(mActivity, Allergy.class);
                        in.putExtra("id", id);
                        startActivity(in);
                        mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    } else {
                        Toast.makeText(AppAplication.getAppContext(), "No internet connection. Please retry.", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        mBmiContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent in = new Intent(MyHealth.this, BmiActivity.class);
                if (((BaseActivity) mActivity).isSessionExist()) {
                    if (NetworkChangeListener.getNetworkStatus().isConnected()) {
                        Intent in = new Intent(mActivity, HealthCommonActivity.class);
                        in.putExtra("id", id);
                        in.putExtra("forBmi", true);
                        startActivity(in);
                        mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    } else {
                        Toast.makeText(AppAplication.getAppContext(), "No internet connection. Please retry.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        mBpContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent in = new Intent(MyHealth.this, BpActivity.class);
                if (((BaseActivity) mActivity).isSessionExist()) {
                    if (NetworkChangeListener.getNetworkStatus().isConnected()) {
                        Intent in = new Intent(mActivity, HealthCommonActivity.class);
                        in.putExtra("id", id);
                        in.putExtra("forBp", true);
                        startActivity(in);
                        mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    } else {
                        Toast.makeText(AppAplication.getAppContext(), "No internet connection. Please retry.", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        mVaccineContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((BaseActivity) mActivity).isSessionExist()) {
                    if (NetworkChangeListener.getNetworkStatus().isConnected()) {
                        AppConstant.isToRefereshVaccine = true;
                        Intent intent = new Intent(mActivity, VaccineActivity.class);
                        startActivity(intent);
                        mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    } else {
                        Toast.makeText(AppAplication.getAppContext(), "No internet connection. Please retry.", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        /*if (position == 0) {
            bgHeader.setVisibility(View.VISIBLE);
            mVaccineContainer.setVisibility(View.VISIBLE);
            allergyLayout.setVisibility(View.VISIBLE);

            heightLayout.setVisibility(View.GONE);
            weightLayout.setVisibility(View.GONE);
            mBmiContainer.setVisibility(View.GONE);
            mBpContainer.setVisibility(View.GONE);
            findViewById(R.id.height_sepraor).setVisibility(View.GONE);
            findViewById(R.id.weight_seprator).setVisibility(View.GONE);
            findViewById(R.id.bp_seprator).setVisibility(View.GONE);
            findViewById(R.id.bmi_seprator).setVisibility(View.GONE);

        } else {
            bgHeader.setVisibility(View.GONE);
            mVaccineContainer.setVisibility(View.GONE);
            allergyLayout.setVisibility(View.GONE);
            findViewById(R.id.blood_group_seprator).setVisibility(View.GONE);
            findViewById(R.id.vaccine_seprator).setVisibility(View.GONE);
            findViewById(R.id.alergy_seprator).setVisibility(View.GONE);

            heightLayout.setVisibility(View.VISIBLE);
            weightLayout.setVisibility(View.VISIBLE);
            mBmiContainer.setVisibility(View.VISIBLE);
            mBpContainer.setVisibility(View.VISIBLE);
        }*/
    }

    public void onRestart() {
        if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
            Toast.makeText(mActivity, "No internet connection. Please retry.", Toast.LENGTH_SHORT).show();
        } else {
            new VitalFragment.BackgroundProcess().execute();
        }
    }

    public void showdialog() {
        final Dialog overlay_dialog = new Dialog(mActivity);
        overlay_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);// SOFT_INPUT_STATE_VISIBLE
        overlay_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        overlay_dialog.setCanceledOnTouchOutside(true);
        overlay_dialog.setContentView(R.layout.edit_popup);
        Button send_request = (Button) overlay_dialog.findViewById(R.id.send_request);
        final ImageView cancel_dialog = (ImageView) overlay_dialog.findViewById(R.id.cancel_dialog);
        final EditText bGroup = (EditText) overlay_dialog.findViewById(R.id.bGroup);
        TextView titleTv = (TextView) overlay_dialog.findViewById(R.id.textView6);
        if (TextUtils.isEmpty(blood_group.getEditableText().toString())) {
            bGroup.setText("Edit");
            titleTv.setText("Edit");
        } else {
            titleTv.setText("Update");
            bGroup.setText(blood_group.getEditableText().toString());
        }
        /*InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(bGroup.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        bGroup.requestFocus();*/

        cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlay_dialog.dismiss();
            }
        });

        send_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String blood = bGroup.getText().toString();
                if (blood == "" || blood.equals("")) {
                    Toast.makeText(mActivity, "Please enter Blood group.", Toast.LENGTH_SHORT).show();
                } else {
                    overlay_dialog.dismiss();
                    sendrequest(blood, id);
                }
            }
        });

        final ArrayAdapter<String> bloodadapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_dropdown_item, bloodList);

        bGroup.setInputType(InputType.TYPE_NULL);
        bGroup.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP)

                {
                    new AlertDialog.Builder(mActivity)
                            .setTitle("Select Blood Group")
                            .setAdapter(bloodadapter,
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            bGroup.setText(bloodList[which].toString());

                                            dialog.dismiss();
                                        }
                                    }).create().show();

                }
                return false;
            }
        });

        overlay_dialog.show();
    }

    public void sendrequest(final String blood, String id) {
        send_request = Volley.newRequestQueue(mActivity);
        progress = new ProgressDialog(mActivity);
        progress.setCancelable(false);
        progress.setMessage("Updating Blood group...");
        progress.setIndeterminate(true);

        if (blood != "" && (!blood.equals(""))) {
            progress.show();
            JSONObject sendData = new JSONObject();
            try {
                sendData.put("UserId", id);
                sendData.put("bloodgroup", blood.trim());
            } catch (JSONException je) {
                je.printStackTrace();
            }
            StaticHolder sttc_holdr = new StaticHolder(mActivity, StaticHolder.Services_static.Updatepatientbloodgroup);
            String url = sttc_holdr.request_Url();
            JsonObjectRequest jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                     /*System.out.println(response);*/
                    progress.dismiss();
                    if (TextUtils.isEmpty(blood)) {
                        blood_group.setText("-");
                    } else {
                        blood_group.setText(bgroup);
                    }
                    try {
                        String data = response.getString("d");
                        if (!data.equalsIgnoreCase("Success")) {
                            Toast.makeText(mActivity, data, Toast.LENGTH_SHORT).show();
                        } else {
                            new VitalFragment.BackgroundProcess().execute();
                        }
                    } catch (JSONException je) {
                        je.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progress.dismiss();
                    Toast.makeText(mActivity, "Some error occurred. Please try again later.", Toast.LENGTH_SHORT).show();

                }
            });
            send_request.add(jr);
        }
    }

    public void startBackgroundProcess() {
        if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
            Toast.makeText(mActivity, "No internet connection. Please retry.", Toast.LENGTH_SHORT).show();
        } else {
            new VitalFragment.BackgroundProcess().execute();
        }

    }

    class BackgroundProcess extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress;
        JSONObject receiveData1;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(mActivity);
            progress.setCancelable(false);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);
            progress.show();


        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (TextUtils.isEmpty(bgroup)) {
                blood_group.setText("-");
            } else {
                blood_group.setText(bgroup);
            }
            if (!height.equalsIgnoreCase("null")) {
                height_latest.setText(height + " cm");
            } else {
                height_latest.setText("-");
            }
            if (!weight.equalsIgnoreCase("null")) {
                weight_latest.setText(weight + " Kg");
            } else {
                weight_latest.setText("-");
            }
            if (allergy_no != 0) {
                allergies.setText(String.valueOf(allergy_no));
            } else {
                allergies.setText("-");
            }
            if (!TextUtils.isEmpty(mBp) && !mBp.equalsIgnoreCase("null")) {
                mBpTvValue.setText(mBp);
            } else {
                mBpTvValue.setText("-");
            }

            if (!TextUtils.isEmpty(mBp) && !mBp.equalsIgnoreCase("null")) {
                mBpTvValue.setText(mBp);
            } else {
                mBpTvValue.setText("-");
            }

            if (!TextUtils.isEmpty(height) && !height.equalsIgnoreCase("null") && !TextUtils.isEmpty(weight) && !weight.equalsIgnoreCase("null")) {
                double weightInDouble = Double.parseDouble(weight);
                double heightInDouble = Double.parseDouble(height);
                double bmi = ((weightInDouble) / (heightInDouble * heightInDouble) * 10000);
                DecimalFormat df = new DecimalFormat("#.##");
                // double time = Double.valueOf(df.format(bmi));
                String value = df.format(bmi);
                mBmiTvValue.setText(value);
            }

            progress.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject sendData1 = new JSONObject();
            try {

                sendData1.put("UserId", id);
                receiveData1 = service.getpatientHistoryDetails(sendData1);
                String data = receiveData1.getString("d");
                JSONObject cut = new JSONObject(data);
                JSONArray jsonArray = cut.getJSONArray("Table");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    bgroup = obj.getString("BloodGroup");
                    height = obj.getString("height");
                    weight = obj.getString("weight");
                    mBp = obj.getString("BP");

                    String alergyString = obj.getString("allergiesName");
                    if (TextUtils.isEmpty(alergyString) || alergyString.equalsIgnoreCase("null")) {
                        allergy_no = 0;
                    } else {
                        String[] array = alergyString.split(",");
                        allergy_no = array.length;
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
                progress.dismiss();
            }
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
            Toast.makeText(mActivity, "No internet connection. Please retry.", Toast.LENGTH_SHORT).show();
        } else {
            new VitalFragment.BackgroundProcessResume().execute();
        }
    }

    class BackgroundProcessResume extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress;
        JSONObject receiveData1;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (TextUtils.isEmpty(bgroup)) {
                blood_group.setText("-");
            } else {
                blood_group.setText(bgroup);
            }
            if (!height.equalsIgnoreCase("null")) {
                height_latest.setText(height + " cm");
            } else {
                height_latest.setText("-");
            }
            if (!weight.equalsIgnoreCase("null")) {
                weight_latest.setText(weight + " Kg");
            } else {
                weight_latest.setText("-");
            }
            if (allergy_no != 0) {
                allergies.setText(String.valueOf(allergy_no));
            } else {
                allergies.setText("-");
            }

            if (allergy_no != 0) {
                allergies.setText(String.valueOf(allergy_no));
            } else {
                allergies.setText("-");
            }

            if (!TextUtils.isEmpty(mBp) && !mBp.equalsIgnoreCase("null")) {
                mBpTvValue.setText(mBp);
            } else {
                mBpTvValue.setText("-");
            }

            if (!TextUtils.isEmpty(height) && !height.equalsIgnoreCase("null") && !TextUtils.isEmpty(weight) && !weight.equalsIgnoreCase("null")) {
                double weightInDouble = Double.parseDouble(weight);
                double heightInDouble = Double.parseDouble(height);
                double bmi = ((weightInDouble) / (heightInDouble * heightInDouble) * 10000);
                DecimalFormat df = new DecimalFormat("#.##");
                // double time = Double.valueOf(df.format(bmi));
                String value = df.format(bmi);
                mBmiTvValue.setText(value);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject sendData1 = new JSONObject();
            try {

                sendData1.put("UserId", id);
                receiveData1 = service.getpatientHistoryDetails(sendData1);
                String data = receiveData1.getString("d");
                JSONObject cut = new JSONObject(data);
                JSONArray jsonArray = cut.getJSONArray("Table");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    bgroup = obj.getString("BloodGroup");
                    height = obj.getString("height");
                    weight = obj.getString("weight");
                    mBp = obj.optString("BP");
                    String alergyString = obj.getString("allergiesName");
                    if (TextUtils.isEmpty(alergyString) || alergyString.equalsIgnoreCase("null")) {
                        allergy_no = 0;
                    } else {
                        String[] array = alergyString.split(",");
                        allergy_no = array.length;
                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
