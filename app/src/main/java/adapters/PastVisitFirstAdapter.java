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

import models.PastVisitFirstModel;

/**
 * Created by ayaz on 20/6/17.
 */

public class PastVisitFirstAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<PastVisitFirstModel> mPastVisitFirstModels = new ArrayList<PastVisitFirstModel>();

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

        PastVisitFirstModel pastVisitFirstModel = mPastVisitFirstModels.get(position);

        holder.doctorName.setText(pastVisitFirstModel.getDoctorName());
        holder.consultTime.setText(pastVisitFirstModel.getConsultTime());
        holder.payment.setText(pastVisitFirstModel.getPayment());
        holder.prescription.setText(pastVisitFirstModel.getPrescription());
        // holder.doctorPic.setImageResource(pastVisitFirstModel.getD());

        return convertView;
    }

    public void setData(List<PastVisitFirstModel> pastVisitFirstModels) {
        mPastVisitFirstModels = pastVisitFirstModels;
    }
}
