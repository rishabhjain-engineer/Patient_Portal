package fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.hs.userportal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapters.PastVisitFirstAdapter;
import adapters.PastVisitedPatientAdapter;
import config.StaticHolder;
import models.PastVisitedPatientModel;
import ui.DashBoardActivity;
import ui.PastVisitedPatientDetailActivity;
import utils.AppConstant;
import utils.PreferenceHelper;

/**
 * Created by ayaz on 25/6/17.
 */

public class PatientListFragment extends Fragment {
    private ListView mListView;
    private Button mConsultNow;
    private TextView past_visits;
    private static RequestQueue mRequestQueue;
    protected PreferenceHelper mPreferenceHelper;
    private ProgressDialog mProgressDialog;
    private Activity mActivity;
    private PastVisitedPatientAdapter mPastVisitedPatientAdapter;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consult, null);
        past_visits = (TextView) view.findViewById(R.id.past_visits_tv);
        past_visits.setVisibility(View.GONE);
        mConsultNow = (Button) view.findViewById(R.id.consult_now);
        mConsultNow.setVisibility(View.GONE);
        mActivity = getActivity();
        mRequestQueue = Volley.newRequestQueue(getActivity());
        mPreferenceHelper = PreferenceHelper.getInstance();
        TextView hederTitle = (TextView) view.findViewById(R.id.header_title_tv);
        hederTitle.setText("Find a patient");
        mListView = (ListView) view.findViewById(R.id.consult_doctor_list);
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
        mProgressDialog.show();
        getPastVisitedPatientList();
        mPastVisitedPatientAdapter = new PastVisitedPatientAdapter(mActivity);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> view, View arg1, int position, long arg3) {
                PastVisitedPatientModel pastVisitedPatientModel = (PastVisitedPatientModel) mListView.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), PastVisitedPatientDetailActivity.class);
                intent.putExtra("patientDetail", pastVisitedPatientModel);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
        return view;
    }

    private List<PastVisitedPatientModel> mPastVisitedPatientList = new ArrayList<>();

    private void getPastVisitedPatientList() {
        mPastVisitedPatientList.clear();
        StaticHolder static_holder = new StaticHolder(getActivity(), StaticHolder.Services_static.PastPatientList);
        String url = static_holder.request_Url();
        JSONObject data = new JSONObject();
        try {
            data.put("doctorId", AppConstant.getDoctorId() );
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
                        PastVisitedPatientModel pastVisitedPatientModel = new PastVisitedPatientModel();
                        pastVisitedPatientModel.setPatientName(jsonObject1.isNull("DoctorName") ? "" : jsonObject1.optString("DoctorName"));
                        pastVisitedPatientModel.setConsultTime(jsonObject1.isNull("ConsultTime") ? "" : jsonObject1.optString("ConsultTime"));
                        pastVisitedPatientModel.setPayment(jsonObject1.isNull("Payment") ? "" : jsonObject1.optString("Payment"));
                        pastVisitedPatientModel.setPrescription(jsonObject1.isNull("Prescription") ? "" : jsonObject1.optString("Prescription"));
                        pastVisitedPatientModel.setConsultId(jsonObject1.isNull("ConsultId") ? "" : jsonObject1.optString("ConsultId"));
                        mPastVisitedPatientList.add(pastVisitedPatientModel);
                    }
                    mPastVisitedPatientAdapter.setData(mPastVisitedPatientList);
                    mProgressDialog.dismiss();
                    mListView.setAdapter(mPastVisitedPatientAdapter);
                    mPastVisitedPatientAdapter.notifyDataSetChanged();
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