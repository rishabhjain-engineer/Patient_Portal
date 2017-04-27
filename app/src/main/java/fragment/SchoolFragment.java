package fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hs.userportal.GraphDetailValueAndDate;
import com.hs.userportal.R;
import com.hs.userportal.Services;
import com.hs.userportal.VaccineDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import adapters.SchoolFragmentAdapter;
import config.StaticHolder;
import models.Schools;
import networkmngr.NetworkChangeListener;
import ui.StudentsDetailActivity;
import ui.VaccineActivity;
import ui.VaccineEditActivity;
import utils.PreferenceHelper;

/**
 * Created by ayaz on 26/4/17.
 */

public class SchoolFragment extends Fragment {
    private Activity mActivity;
    private PreferenceHelper mPreferenceHelper;
    private Services service;
    protected ListView mSchoolListView;
    private List<Schools> mSchoolsList = new ArrayList<>();
    private SchoolFragmentAdapter mSchoolFragmentAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_school, null);
        mActivity = getActivity();
        mPreferenceHelper = PreferenceHelper.getInstance();
        service = new Services(mActivity);
        TextView hederTitle = (TextView) view.findViewById(R.id.header_title_tv);
        hederTitle.setText("School");
        mSchoolListView = (ListView) view.findViewById(R.id.school_list_view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (NetworkChangeListener.getNetworkStatus().isConnected()) {
            sendrequest();
        } else {
            Toast.makeText(mActivity, "No Internet Connection", Toast.LENGTH_LONG).show();
        }

        mSchoolListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> view, View arg1, int position, long arg3) {

                Schools selectedItem = (Schools) mSchoolListView.getItemAtPosition(position);

                Intent intent = new Intent(mActivity, StudentsDetailActivity.class);
                intent.putExtra("DateOfExamination", selectedItem.getDateOfExamination());
                //intent.putExtra("DoctorName", selectedItem.getDoctorName());
                //intent.putExtra("DoctorDesignation", selectedItem.getDoctorDesignation());
                intent.putExtra("staffId", selectedItem.getStaffId());

                startActivity(intent);

            }
        });
    }

    private ProgressDialog mProgressDialog;
    private RequestQueue mRequestQueue;

    private void sendrequest() {
        mRequestQueue = Volley.newRequestQueue(mActivity);

        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Loading details...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        JSONObject sendData = new JSONObject();
        try {
            String id = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID);
            sendData.put("patientId", "97E9496B-8630-4D61-9F13-D7E95C0AD6A7");
            //sendData.put("patientId", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StaticHolder staticHolder = new StaticHolder(mActivity, StaticHolder.Services_static.GetSchoolDoctorList);
        String url = staticHolder.request_Url();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mProgressDialog.dismiss();
                String data = null;
                mSchoolsList.clear();
                try {
                    data = response.optString("d");
                    JSONObject cut = new JSONObject(data);
                    JSONArray jsonArray = cut.optJSONArray("Table");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.optJSONObject(i);
                            Schools schools = new Schools();
                            schools.setDateOfExamination(jsonObject.optString("DateOfExamination"));
                            schools.setDoctorName(jsonObject.optString("DoctorName"));
                            schools.setDoctorDesignation(jsonObject.optString("DoctorDesignation"));
                            schools.setStaffId(jsonObject.optString("staffId"));
                            mSchoolsList.add(schools);
                        }
                        List<Schools> sortedList = sortListByDate(mSchoolsList);
                        mSchoolFragmentAdapter = new SchoolFragmentAdapter(mActivity, sortedList);
                        mSchoolListView.setAdapter(mSchoolFragmentAdapter);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toast.makeText(mActivity, "Some error occurred. Please try again later.", Toast.LENGTH_SHORT).show();

            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }

    private List<Schools> sortListByDate(List<Schools> list) {

        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = i; j < list.size(); j++) {
                try {
                    String first = list.get(i).getDateOfExamination();
                    String second = list.get(j).getDateOfExamination();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date date1 = simpleDateFormat.parse(first);
                    Date date2 = simpleDateFormat.parse(second);

                    if (date1.compareTo(date2) < 0) {
                        Schools firstitem = list.get(i);
                        Schools seconditem = list.get(j);
                        //swap position first with second
                        list.add(i, seconditem);
                        list.add(j, firstitem);
                        list.remove(i + 1);
                        list.remove(j + 1);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }
}
