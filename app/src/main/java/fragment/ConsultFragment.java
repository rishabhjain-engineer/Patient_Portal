package fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.applozic.audiovideo.activity.VideoActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.hs.userportal.Authentication;
import com.hs.userportal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapters.ConsultFragmentAdapter;
import adapters.PastVisitFirstAdapter;
import config.StaticHolder;
import models.DoctorDetails;
import models.PastVisitFirstModel;
import networkmngr.NetworkChangeListener;
import ui.DashBoardActivity;
import ui.DoctorDetailsActivity;
import ui.DoctorPrescriptionActivity;
import ui.PastVisitActivity;
import ui.PastVisitFirstActivity;
import utils.AppConstant;
import utils.PreferenceHelper;

/**
 * Created by ayaz on 2/6/17.
 */

public class ConsultFragment extends Fragment {
    private ListView mListView;
    private ConsultFragmentAdapter mConsultFragmentAdapter;
    private List<DoctorDetails> mDoctorDetailsList = new ArrayList<>();
    private Button mConsultNow;
    private TextView past_visits;
    private static RequestQueue mRequestQueue;
    protected PreferenceHelper mPreferenceHelper;
    private ProgressDialog mProgressDialog;
    private Activity mActivity;
    private PastVisitFirstAdapter mPastVisitFirstAdapter;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consult, null);
        mRequestQueue = Volley.newRequestQueue(getActivity());
        mPreferenceHelper = PreferenceHelper.getInstance();
        TextView hederTitle = (TextView) view.findViewById(R.id.header_title_tv);
        mListView = (ListView) view.findViewById(R.id.consult_doctor_list);
        mConsultNow = (Button) view.findViewById(R.id.consult_now);
        past_visits = (TextView) view.findViewById(R.id.past_visits_tv);
        mActivity = getActivity();
        mPastVisitFirstAdapter = new PastVisitFirstAdapter(mActivity);
        if (ConversationActivity.isPatient) {
            past_visits.setVisibility(View.VISIBLE);
        } else {
            past_visits.setVisibility(View.GONE);
        }
        past_visits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
                    Toast.makeText(mActivity, "No internet connection. Please retry.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getActivity(), PastVisitFirstActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }
            }
        });

        ImageView backImage = (ImageView) view.findViewById(R.id.back_image);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                Intent intent = new Intent(getActivity(), DashBoardActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setIndeterminate(true);
        mConsultNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
                    Toast.makeText(mActivity, "No internet connection. Please retry.", Toast.LENGTH_SHORT).show();
                } else {
                    mProgressDialog.show();
                    mConsultNow.setClickable(false);
                    getConsultId();
                }
               /* Intent intent = null;
                if (AppConstant.isPatient) {
                    intent = new Intent(getActivity(), PastVisitActivity.class);
                } else {
                    intent = new Intent(getActivity(), DoctorPrescriptionActivity.class);
                }
                intent.putExtra("chatType", "video");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);*/
            }
        });

        mConsultFragmentAdapter = new ConsultFragmentAdapter(getActivity());

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> view, View arg1, int position, long arg3) {
                DoctorDetails doctorDetails = (DoctorDetails) mListView.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), DoctorDetailsActivity.class);
                intent.putExtra("doctorDetail", doctorDetails);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
        hederTitle.setText("Find a doctor");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DoctorDetails doctorDetails3 = new DoctorDetails();
        doctorDetails3.setDoctorName("Sajat");
        doctorDetails3.setLocation("Sector 22, Noida");
        doctorDetails3.setMedicineType("Family Medicine");
        doctorDetails3.setDoctorImage(R.drawable.ayaz);
        doctorDetails3.setAboutDoctor("Medical School - State University of New York, Downstate Medical Center, Doctor of Medicine\n" +
                "State University of New York, Downstate Medical Center (Residency)\n" +
                "State University of New York, Downstate Medical Center, Fellowship in Gastroenterology\n");
        mDoctorDetailsList.add(doctorDetails3);

       /* DoctorDetails doctorDetails1 = new DoctorDetails();
        doctorDetails1.setDoctorName("Ayaz");
        doctorDetails1.setLocation("Aminabad, Lucknow");
        doctorDetails1.setMedicineType("Family Medicine");
        doctorDetails1.setDoctorImage(R.drawable.ayaz);
        doctorDetails1.setAboutDoctor("Medical School - State University of New York, Downstate Medical Center, Doctor of Medicine\n" +
                "State University of New York, Downstate Medical Center (Residency)\n" +
                "State University of New York, Downstate Medical Center, Fellowship in Gastroenterology\n");
        mDoctorDetailsList.add(doctorDetails1);

        DoctorDetails doctorDetails2 = new DoctorDetails();
        doctorDetails2.setDoctorName("Rishabh");
        doctorDetails2.setLocation("GTB Nagar, Delhi");
        doctorDetails2.setMedicineType("Family Medicine");
        doctorDetails2.setDoctorImage(R.drawable.update);
        doctorDetails2.setAboutDoctor("Medical School - State University of New York, Downstate Medical Center, Doctor of Medicine\n" +
                "State University of New York, Downstate Medical Center (Residency)\n" +
                "State University of New York, Downstate Medical Center, Fellowship in Gastroenterology\n");
        mDoctorDetailsList.add(doctorDetails2);*/

        mConsultFragmentAdapter.setData(mDoctorDetailsList);
        mListView.setAdapter(mConsultFragmentAdapter);


    }

    private String mConsultID;

    private void getConsultId() {
        StaticHolder static_holder = new StaticHolder(getActivity(), StaticHolder.Services_static.ConsultAddSymptoms);
        String url = static_holder.request_Url();
        JSONObject data = new JSONObject();
        try {
            data.put("patientId", AppConstant.getPatientID());
            data.put("symptoms", "");
            data.put("patientNotes", "");
            data.put("consultId", JSONObject.NULL);
            data.put("doctorId", AppConstant.getDoctorId());
        } catch (JSONException je) {
            je.printStackTrace();
        }
        JsonObjectRequest symptomsJsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    mConsultNow.setClickable(true);
                    Log.i("GetMember", "Received Data: " + response);
                    String consultId = response.getString("d");
                    consultId = consultId.replaceAll("^\"|\"$", ""); // replacing consultID ""
                    mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.CONSULT_ID, consultId);
                    mConsultID = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.CONSULT_ID);
                    mProgressDialog.dismiss();
                    Intent videoCallIntent = new Intent(getActivity(), VideoActivity.class);
                    videoCallIntent.putExtra("CONTACT_ID", AppConstant.getpatienDoctorId());
                    startActivity(videoCallIntent);
                    getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                } catch (JSONException je) {
                    mProgressDialog.dismiss();
                    je.printStackTrace();
                    Toast.makeText(getActivity(), "Some error occurred.Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                mConsultNow.setClickable(true);
                mProgressDialog.dismiss();
                Toast.makeText(getActivity(), "Some error occurred.Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
        mRequestQueue.add(symptomsJsonObjectRequest);

    }

    private List<PastVisitFirstModel> mPastVisitedPatientList = new ArrayList<>();
    private void getPastVisitedPatientList() {
        mPastVisitedPatientList.clear();
        StaticHolder static_holder = new StaticHolder(getActivity(), StaticHolder.Services_static.PastPatientList);
        String url = static_holder.request_Url();
        JSONObject data = new JSONObject();
        try {
            data.put("doctorId", AppConstant.getDoctorId());
        } catch (JSONException je) {
            je.printStackTrace();
        }
        JsonObjectRequest symptomsJsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String data = response.getString("d");
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray jsonArray = jsonObject.getJSONArray("Table");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        PastVisitFirstModel pastVisitFirstModel = new PastVisitFirstModel();
                        pastVisitFirstModel.setDoctorName(jsonObject1.isNull("DoctorName") ? "" : jsonObject1.optString("DoctorName"));
                        pastVisitFirstModel.setConsultTime(jsonObject1.isNull("ConsultTime") ? "" : jsonObject1.optString("ConsultTime"));
                        pastVisitFirstModel.setPayment(jsonObject1.isNull("Payment") ? "" : jsonObject1.optString("Payment"));
                        pastVisitFirstModel.setPrescription(jsonObject1.isNull("Prescription") ? "" : jsonObject1.optString("Prescription"));
                        pastVisitFirstModel.setConsultId(jsonObject1.isNull("ConsultId") ? "" : jsonObject1.optString("ConsultId"));
                        mPastVisitedPatientList.add(pastVisitFirstModel);
                    }
                    mPastVisitFirstAdapter.setData(mPastVisitedPatientList);
                    mProgressDialog.dismiss();
                    mListView.setAdapter(mPastVisitFirstAdapter);
                    mPastVisitFirstAdapter.notifyDataSetChanged();
                } catch (JSONException je) {
                    mProgressDialog.dismiss();
                    je.printStackTrace();
                    Toast.makeText(mActivity, "Some error occurred.Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toast.makeText(mActivity, "Some error occurred.Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
        mRequestQueue.add(symptomsJsonObjectRequest);
    }

    private List<PastVisitFirstModel> mPastVisitFirstModels = new ArrayList<>();
    private void getPastVisitedDoctorList() {
        mPastVisitFirstModels.clear();
        StaticHolder static_holder = new StaticHolder(getActivity(), StaticHolder.Services_static.PastVisitList);
        String url = static_holder.request_Url();
        JSONObject data = new JSONObject();
        try {
            data.put("patientId", AppConstant.getPatientID());
        } catch (JSONException je) {
            je.printStackTrace();
        }
        JsonObjectRequest symptomsJsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String data = response.getString("d");
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray jsonArray = jsonObject.getJSONArray("Table");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        PastVisitFirstModel pastVisitFirstModel = new PastVisitFirstModel();
                        pastVisitFirstModel.setDoctorName(jsonObject1.isNull("DoctorName") ? "" : jsonObject1.optString("DoctorName"));
                        pastVisitFirstModel.setConsultTime(jsonObject1.isNull("ConsultTime") ? "" : jsonObject1.optString("ConsultTime"));
                        pastVisitFirstModel.setPayment(jsonObject1.isNull("Payment") ? "" : jsonObject1.optString("Payment"));
                        pastVisitFirstModel.setPrescription(jsonObject1.isNull("Prescription") ? "" : jsonObject1.optString("Prescription"));
                        pastVisitFirstModel.setConsultId(jsonObject1.isNull("ConsultId") ? "" : jsonObject1.optString("ConsultId"));
                        mPastVisitFirstModels.add(pastVisitFirstModel);
                    }
                    mPastVisitFirstAdapter.setData(mPastVisitFirstModels);
                    mProgressDialog.dismiss();
                    mListView.setAdapter(mPastVisitFirstAdapter);
                    mPastVisitFirstAdapter.notifyDataSetChanged();
                } catch (JSONException je) {
                    mProgressDialog.dismiss();
                    je.printStackTrace();
                    Toast.makeText(mActivity, "Some error occurred.Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toast.makeText(mActivity, "Some error occurred.Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
        mRequestQueue.add(symptomsJsonObjectRequest);
    }


}
