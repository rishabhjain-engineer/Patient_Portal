package adapters;

import android.app.Activity;
import android.text.TextUtils;
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
        TextView acronyms;
        TextView date;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.list_view_vaccine_appearence, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.vaccine_name_tv);
            holder.acronyms = (TextView) convertView.findViewById(R.id.vaccine_acronyms);
            holder.date = (TextView) convertView.findViewById(R.id.vaccine_aget_at);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (!TextUtils.isEmpty(mVaccineDetailsesList.get(position).getVaccineName())) {
            holder.name.setText(mVaccineDetailsesList.get(position).getVaccineName());
        }

        if (!TextUtils.isEmpty(mVaccineDetailsesList.get(position).getVaccineNameInShort())) {
            holder.acronyms.setText((mVaccineDetailsesList.get(position).getVaccineNameInShort()));
        }


        holder.date.setText((mVaccineDetailsesList.get(position).getAgeAt()) +"");


        return convertView;
    }
}
