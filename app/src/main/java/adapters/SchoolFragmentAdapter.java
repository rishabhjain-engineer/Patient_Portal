package adapters;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hs.userportal.R;
import com.hs.userportal.VaccineDetails;

import java.util.ArrayList;
import java.util.List;

import models.Schools;

/**
 * Created by ayaz on 26/4/17.
 */

public class SchoolFragmentAdapter extends BaseAdapter {

    private Activity mActivity;
    private List<Schools> mSchoolsList = new ArrayList<>();

    public SchoolFragmentAdapter(Activity activity, List<Schools> schoolsList) {
        mSchoolsList = schoolsList;
        mActivity = activity;
    }

    @Override
    public int getCount() {
        return mSchoolsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mSchoolsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        TextView name;
        TextView date;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SchoolFragmentAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.school_single_item_view, parent, false);
            holder = new SchoolFragmentAdapter.ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.doctor_name);
            holder.date = (TextView) convertView.findViewById(R.id.doctor_date);
            convertView.setTag(holder);
        } else {
            holder = (SchoolFragmentAdapter.ViewHolder) convertView.getTag();
        }
        Schools schools = mSchoolsList.get(position);
        holder.name.setText(schools.getDoctorName());
        String date = schools.getDateOfExamination();
        String array[] = date.split("T");
        holder.date.setText(array[0]);
        return convertView;
    }
}
