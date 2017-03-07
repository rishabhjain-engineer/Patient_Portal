package adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hs.userportal.R;
import com.hs.userportal.VaccineDetails;
import java.util.ArrayList;
import java.util.List;
import ui.VaccineActivity;

/**
 * Created by ayaz on 6/3/17.
 */

public class VaccineAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<VaccineDetails> mVaccineDetailsesList = new ArrayList<VaccineDetails>();

    public VaccineAdapter(VaccineActivity vaccineActivity) {
        mActivity = vaccineActivity;
    }

    public void setVaccineDetailData(List<VaccineDetails> vaccineDetailDataList) {
        mVaccineDetailsesList = vaccineDetailDataList;
    }

    @Override
    public int getCount() {
        return mVaccineDetailsesList.size();
    }

    @Override
    public Object getItem(int position) {
        return mVaccineDetailsesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    private class ViewHolder {
        TextView name;
        TextView ageAt;
        TextView ageTo;
        TextView dose;
        TextView doseType;
        TextView comments;
        TextView date;
        TextView notes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.list_view_vaccine_appearence, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.vaccine_name_tv);
            holder.ageAt = (TextView) convertView.findViewById(R.id.age_at);
            holder.ageTo = (TextView) convertView.findViewById(R.id.age_to);
            holder.dose = (TextView) convertView.findViewById(R.id.dose);
            holder.doseType = (TextView) convertView.findViewById(R.id.dose_type);
            holder.comments = (TextView) convertView.findViewById(R.id.comment);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.notes = (TextView) convertView.findViewById(R.id.doctor_notes);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(mVaccineDetailsesList.get(position).getVaccineName());
        holder.ageAt.setText(mVaccineDetailsesList.get(position).getAgeAt());
        holder.ageTo.setText(mVaccineDetailsesList.get(position).getAgeTo());
        holder.dose.setText(mVaccineDetailsesList.get(position).getVaccineDose());
        holder.doseType.setText(mVaccineDetailsesList.get(position).getVaccineDoseType());
        holder.comments.setText(mVaccineDetailsesList.get(position).getVaccineComment());
        holder.date.setText(mVaccineDetailsesList.get(position).getVaccineDateTime());
        holder.notes.setText(mVaccineDetailsesList.get(position).getDoctorNotes());

        return convertView;
    }
}
