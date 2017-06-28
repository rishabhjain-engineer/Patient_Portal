package adapters;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hs.userportal.R;

import java.util.ArrayList;
import java.util.List;

import models.DoctorDetails;
import models.PastVisitedPatientModel;

/**
 * Created by ayaz on 25/6/17.
 */

public class PastVisitedPatientAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<PastVisitedPatientModel> mPastVisitedPatientModelList = new ArrayList<PastVisitedPatientModel>();

    public PastVisitedPatientAdapter(Activity activity) {
        mActivity = activity;
    }


    @Override
    public int getCount() {
        return mPastVisitedPatientModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPastVisitedPatientModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    private class ViewHolder {
        TextView patientName;
        TextView requestTime;
        TextView consultTime;
        TextView consultMode;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PastVisitedPatientAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.patient_single_row_view, parent, false);
            holder = new PastVisitedPatientAdapter.ViewHolder();
            holder.patientName = (TextView) convertView.findViewById(R.id.patient_name);
            holder.requestTime = (TextView) convertView.findViewById(R.id.request_time);
            holder.consultTime = (TextView) convertView.findViewById(R.id.consult_time);
            holder.consultMode = (TextView) convertView.findViewById(R.id.consult_mode);
            convertView.setTag(holder);
        } else {
            holder = (PastVisitedPatientAdapter.ViewHolder) convertView.getTag();
        }

        PastVisitedPatientModel pastVisitedPatientModel = mPastVisitedPatientModelList.get(position);

        holder.patientName.setText(pastVisitedPatientModel.getPatientName());
        String requestTime = pastVisitedPatientModel.getRequestTime();
        if (!TextUtils.isEmpty(requestTime)) {
            String timeArray[] = requestTime.split("T");
            requestTime = timeArray[0];
        }
        holder.requestTime.setText(requestTime);
        if (TextUtils.isEmpty(pastVisitedPatientModel.getConsultTime())) {
            holder.consultTime.setVisibility(View.GONE);
        } else {
            holder.consultTime.setVisibility(View.VISIBLE);
            holder.consultTime.setText(pastVisitedPatientModel.getConsultTime());
        }
        if (TextUtils.isEmpty(pastVisitedPatientModel.getConsultMode())) {
            holder.consultMode.setVisibility(View.GONE);
        } else {
            holder.consultMode.setVisibility(View.VISIBLE);
            holder.consultMode.setText(pastVisitedPatientModel.getConsultMode());
        }
        return convertView;
    }

    public void setData(List<PastVisitedPatientModel> pastVisitedPatientModelList) {
        mPastVisitedPatientModelList = pastVisitedPatientModelList;
    }
}

