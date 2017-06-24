package adapters;

import android.app.Activity;
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

/**
 * Created by ayaz on 25/6/17.
 */

public class PastVisitedPatientAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<DoctorDetails> mDoctorDetailsList = new ArrayList<DoctorDetails>();

    public PastVisitedPatientAdapter(Activity activity) {
        mActivity = activity;
    }


    @Override
    public int getCount() {
        return mDoctorDetailsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDoctorDetailsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    private class ViewHolder {
        TextView patientName;
        TextView patientLocation;
        TextView patientMedicineType;
        ImageView patientPic;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PastVisitedPatientAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.consult_single_item_list_view, parent, false);
            holder = new PastVisitedPatientAdapter.ViewHolder();
            holder.patientName = (TextView) convertView.findViewById(R.id.doctor_name);
            holder.patientLocation = (TextView) convertView.findViewById(R.id.city);
            holder.patientMedicineType = (TextView) convertView.findViewById(R.id.medicine_type);
            holder.patientPic = (ImageView) convertView.findViewById(R.id.doctor_image_view);
            convertView.setTag(holder);
        } else {
            holder = (PastVisitedPatientAdapter.ViewHolder) convertView.getTag();
        }

        DoctorDetails doctorDetails = mDoctorDetailsList.get(position);

        holder.patientName.setText(doctorDetails.getDoctorName());
        holder.patientLocation.setText(doctorDetails.getLocation());
        holder.patientMedicineType.setText(doctorDetails.getMedicineType());
        holder.patientPic.setImageResource(doctorDetails.getDoctorImage());

        return convertView;
    }

    public void setData(List<DoctorDetails> doctorDetailsList) {
        mDoctorDetailsList = doctorDetailsList;
    }
}

