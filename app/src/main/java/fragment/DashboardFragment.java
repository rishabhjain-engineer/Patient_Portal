package fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.hs.userportal.MyHealth;
import com.hs.userportal.R;

import org.askerov.dynamicgrid.DynamicGridView;

import java.util.ArrayList;
import java.util.List;

import adapters.DashboardActivityAdapter;
import networkmngr.NetworkChangeListener;
import ui.BaseActivity;
import ui.DashBoardActivity;

/**
 * Created by android1 on 3/4/17.
 */

public class DashboardFragment extends Fragment{
    private DynamicGridView mDaDynamicGridView;
    private List<String> mList = new ArrayList<>();
    private GridView mGridView;
    private Activity mActivity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, null);
        mGridView = (GridView) view.findViewById(R.id.grid_view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();

        DashboardActivityAdapter dashboardActivityAdapter = new DashboardActivityAdapter(mActivity);
        mGridView.setAdapter(dashboardActivityAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> view, View arg1, int position, long arg3) {
                if (position == 0) {
                    goToHealth(position);
                } else if (position == 1) {
                    goToHealth(position);
                } else if (position == 2) {
                    ((BaseActivity)mActivity).showAlertMessage("Comming Soon");
                } else if (position == 3) {
                    ((BaseActivity)mActivity).showAlertMessage("Comming Soon");
                } else if (position == 4) {
                    ((BaseActivity)mActivity).showAlertMessage("Comming Soon");
                }
            }
        });
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
}
