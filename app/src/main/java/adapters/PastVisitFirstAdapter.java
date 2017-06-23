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

import models.PastVisitDoctorModel;

/**
 * Created by ayaz on 20/6/17.
 */

public class PastVisitFirstAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<PastVisitDoctorModel> mPastVisitFirstModels = new ArrayList<PastVisitDoctorModel>();

    public PastVisitFirstAdapter(Activity activity) {
        mActivity = activity;
    }


    @Override
    public int getCount() {
        return mPastVisitFirstModels.size();
    }

    @Override
    public Object getItem(int position) {
        return mPastVisitFirstModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    private class ViewHolder {
        TextView doctorName;
        TextView consultTime;
        TextView payment;
        TextView prescription;
        ImageView doctorPic;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PastVisitFirstAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.pastt_visit_first_single_item_appearence, parent, false);
            holder = new PastVisitFirstAdapter.ViewHolder();
            holder.doctorName = (TextView) convertView.findViewById(R.id.doctor_name);
            holder.consultTime = (TextView) convertView.findViewById(R.id.consult_time);
            holder.payment = (TextView) convertView.findViewById(R.id.payment);
            holder.prescription = (TextView) convertView.findViewById(R.id.prescription);
            holder.doctorPic = (ImageView) convertView.findViewById(R.id.doctor_image_view);
            convertView.setTag(holder);
        } else {
            holder = (PastVisitFirstAdapter.ViewHolder) convertView.getTag();
        }

        PastVisitDoctorModel pastVisitFirstModel = mPastVisitFirstModels.get(position);

        if (!TextUtils.isEmpty(pastVisitFirstModel.getDoctorName())) {
            holder.doctorName.setText(pastVisitFirstModel.getDoctorName());
        }

        if (!TextUtils.isEmpty(pastVisitFirstModel.getConsultTime())) {
            holder.consultTime.setText(pastVisitFirstModel.getConsultTime());
        }
        if (!TextUtils.isEmpty(pastVisitFirstModel.getPayment())) {
            holder.payment.setText(pastVisitFirstModel.getPayment());
        }
        if (!TextUtils.isEmpty(pastVisitFirstModel.getPrescription())) {
            holder.prescription.setText(pastVisitFirstModel.getPrescription());
        }
        // holder.doctorPic.setImageResource(pastVisitFirstModel.getD());

        return convertView;
    }

    public void setData(List<PastVisitDoctorModel> pastVisitFirstModels) {
        mPastVisitFirstModels = pastVisitFirstModels;
    }
}
