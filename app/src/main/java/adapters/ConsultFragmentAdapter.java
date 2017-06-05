package adapters;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hs.userportal.R;
import com.hs.userportal.VaccineDetails;

import java.util.ArrayList;
import java.util.List;

import models.DoctorDetails;
import ui.VaccineActivity;

/**
 * Created by ayaz on 5/6/17.
 */

public class ConsultFragmentAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<DoctorDetails> mDoctorDetailsList = new ArrayList<DoctorDetails>();

    public ConsultFragmentAdapter(Activity activity) {
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
        TextView doctorName;
        TextView doctorLocation;
        TextView doctorMedicineType;
        ImageView doctorPic;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ConsultFragmentAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.consult_single_item_list_view, parent, false);
            holder = new ConsultFragmentAdapter.ViewHolder();
            holder.doctorName = (TextView) convertView.findViewById(R.id.doctor_name);
            holder.doctorLocation = (TextView) convertView.findViewById(R.id.city);
            holder.doctorMedicineType = (TextView) convertView.findViewById(R.id.medicine_type);
            holder.doctorPic = (ImageView) convertView.findViewById(R.id.doctor_image_view);
            convertView.setTag(holder);
        } else {
            holder = (ConsultFragmentAdapter.ViewHolder) convertView.getTag();
        }

        DoctorDetails doctorDetails = mDoctorDetailsList.get(position);

        holder.doctorName.setText(doctorDetails.getDoctorName());
        holder.doctorLocation.setText(doctorDetails.getLocation());
        holder.doctorMedicineType.setText(doctorDetails.getMedicineType());
        holder.doctorPic.setImageResource(doctorDetails.getDoctorImage());

        return convertView;
    }

    public void setData(List<DoctorDetails> doctorDetailsList) {
        mDoctorDetailsList = doctorDetailsList;
    }
}
