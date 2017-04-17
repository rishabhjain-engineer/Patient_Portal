package fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.hs.userportal.MyHealth;
import com.hs.userportal.R;
import com.hs.userportal.Services;
import com.hs.userportal.logout;

import org.askerov.dynamicgrid.DynamicGridView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapters.DashboardActivityAdapter;
import networkmngr.NetworkChangeListener;
import ui.BaseActivity;
import ui.DashBoardActivity;
import utils.PreferenceHelper;

/**
 * Created by android1 on 3/4/17.
 */

public class DashboardFragment extends Fragment {
    private DynamicGridView mDaDynamicGridView;
    private List<String> mList = new ArrayList<>();
    private GridView mGridView;
    private Activity mActivity;
    private RelativeLayout mInitialTextViewContainer, mScoreTextViewContainer;
    private LinearLayout mHomepageQuizContaner;
    private TextView mScoreUpperTextView, mScoreLowerTextView;
    private ImageView mHomePageVitalsImageView, mMenuButton;
    private String mScore, mFact, mPathOfGlobalIndex, mUserId;
    private Services service;
    private PreferenceHelper mPreferenceHelper;
    private Menu menu1;
    private DashboardActivityAdapter mDashboardActivityAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, null);
        mGridView = (GridView) view.findViewById(R.id.grid_view);
        mHomepageQuizContaner = (LinearLayout) view.findViewById(R.id.quiz_container);
        mInitialTextViewContainer = (RelativeLayout) view.findViewById(R.id.initial_textview_container);
        mScoreTextViewContainer = (RelativeLayout) view.findViewById(R.id.score_textview_container);
        mScoreUpperTextView = (TextView) view.findViewById(R.id.uppertv);
        mScoreLowerTextView = (TextView) view.findViewById(R.id.middletv);
        mMenuButton = (ImageView) view.findViewById(R.id.menuimgbtn);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        mActivity = getActivity();
        mPreferenceHelper = PreferenceHelper.getInstance();
        service = new Services(mActivity);
        mUserId = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID);

        mDashboardActivityAdapter = new DashboardActivityAdapter(mActivity);
        mGridView.setAdapter(mDashboardActivityAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> view, View arg1, int position, long arg3) {
                if (position == 0) {
                    ((DashBoardActivity) mActivity).openReportFragment();
                } else if (position == 1) {
                    ((DashBoardActivity) mActivity).openVitalFragment();
                } else if (position == 2) {
                    ((DashBoardActivity) mActivity).openFamilyFragment();
                } else if (position == 3) {
                    ((DashBoardActivity) mActivity).openRepositoryFragment();
                }
            }
        });

        mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashBoardActivity) mActivity).openAccountFragment();
            }
        });

        PreferenceHelper preferenceHelper = PreferenceHelper.getInstance();
        final String userId = preferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID);
        mInitialTextViewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(logout.this, GraphHandlerWebViewActivity.class);
                intent.putExtra("path", mPathOfGlobalIndex + id);
                startActivityForResult(intent, 2);*/
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mPathOfGlobalIndex + userId));
                    startActivity(browserIntent);
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(mActivity, "Please install chrome", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mActivity.getMenuInflater().inflate(R.menu.home_menu, menu);
        menu1 = menu;

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                return true;

            case R.id.add:
                ((DashBoardActivity) mActivity).openAccountFragment();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void goToHealth(int position) {
        if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
            Toast.makeText(mActivity, "No Internet Connection", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(mActivity, MyHealth.class);
            intent.putExtra("position", position);
            intent.putExtra("show_blood", "yes");
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        new MyHealthAsync().execute();
        new GetUserGradeAsync().execute();
        if (Build.VERSION.SDK_INT >= 19 && !NotificationManagerCompat.from(mActivity).areNotificationsEnabled()) {
            ((BaseActivity)mActivity).showNotificationAlertMessage();
        }
    }

    private String height, weight, bgroup, mBp;
    private boolean isShowGreenVitalsImage;

    private class MyHealthAsync extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress;
        JSONObject receiveData1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(mActivity);
            progress.setCancelable(false);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);
            progress.show();
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progress.dismiss();
            if (mDashboardActivityAdapter != null) {
                if (isShowGreenVitalsImage) {
                    mDashboardActivityAdapter.setFlagForImage(true);
                } else {
                    mDashboardActivityAdapter.setFlagForImage(false);
                }
                mDashboardActivityAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject sendData1 = new JSONObject();
            try {
                sendData1.put("UserId", mUserId);
                receiveData1 = service.getpatientHistoryDetails(sendData1);
                String data = receiveData1.getString("d");
                JSONObject cut = new JSONObject(data);
                JSONArray jsonArray = cut.getJSONArray("Table");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    bgroup = obj.optString("BloodGroup");
                    height = obj.optString("height");
                    weight = obj.optString("weight");
                    mBp = obj.optString("BP");
                    String alergyString = obj.getString("allergiesName");
                    if (!TextUtils.isEmpty(alergyString) && !TextUtils.isEmpty(bgroup) && !TextUtils.isEmpty(height) && !TextUtils.isEmpty(weight) && !TextUtils.isEmpty(mBp) &&
                            !alergyString.equalsIgnoreCase("null") && !bgroup.equalsIgnoreCase("null") && !height.equalsIgnoreCase("null") && !weight.equalsIgnoreCase("null") && !mBp.equalsIgnoreCase("null")) {
                        isShowGreenVitalsImage = true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                progress.dismiss();
            }
            return null;
        }
    }

    private boolean isToLoadData;

    private class GetUserGradeAsync extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress;
        JSONObject receiveData1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(mActivity);
            progress.setCancelable(false);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);
            isToLoadData = false;
            progress.show();
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progress.dismiss();
            if (isToLoadData) {
                /*Intent intent = new Intent(logout.this, GraphHandlerWebViewActivity.class);
                intent.putExtra("path", mPathOfGlobalIndex + id);
                startActivityForResult(intent, 2);*/
                mInitialTextViewContainer.setVisibility(View.VISIBLE);
                mScoreTextViewContainer.setVisibility(View.GONE);
            } else {
                mInitialTextViewContainer.setVisibility(View.GONE);
                mScoreTextViewContainer.setVisibility(View.VISIBLE);
                mScoreUpperTextView.setText(mScore);
                mScoreLowerTextView.setText(mFact);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject sendData1 = new JSONObject();
            try {
                sendData1.put("PatientId", mUserId);
                // sendData1.put("PatientId", "442454B7-7CEA-48B7-8472-DBE7D7DC0D93");
                receiveData1 = service.getUserGrade(sendData1);
                String data = receiveData1.optString("d");
                JSONObject cut = new JSONObject(data);
                JSONArray jsonArray = cut.optJSONArray("Table");
                if (jsonArray != null) {
                    JSONObject jsonObject = jsonArray.optJSONObject(0);
                    mPathOfGlobalIndex = jsonObject.optString("Path");
                    mScore = jsonObject.optString("Score");
                    mFact = jsonObject.optString("Fact");
                    if (!TextUtils.isEmpty(mPathOfGlobalIndex) && mPathOfGlobalIndex.contains("globalhealthindex")) {
                        isToLoadData = true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                progress.dismiss();
            }
            return null;
        }
    }
}
