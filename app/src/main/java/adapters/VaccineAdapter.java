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
        //TextView date;
        RelativeLayout itemDetailContainer;
        TextView headerTv;
        ImageView mTickedImagView;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.list_view_vaccine_appearence, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.vaccine_name_tv);
            holder.acronyms = (TextView) convertView.findViewById(R.id.vaccine_acronyms);
            holder.itemDetailContainer = (RelativeLayout) convertView.findViewById(R.id.item_detail_container);
            holder.headerTv = (TextView) convertView.findViewById(R.id.header);
            holder.mTickedImagView = (ImageView) convertView.findViewById(R.id.ticked_image_view);
            //holder.date = (TextView) convertView.findViewById(R.id.vaccine_aget_at);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        VaccineDetails vaccineDetails = mVaccineDetailsesList.get(position);
        holder.mTickedImagView.setTag(position);
        if (vaccineDetails.isHeader()) {
            holder.itemDetailContainer.setVisibility(View.GONE);
            holder.headerTv.setVisibility(View.VISIBLE);
            holder.headerTv.setText(vaccineDetails.getHeaderString());
        } else {
            holder.itemDetailContainer.setVisibility(View.VISIBLE);
            holder.headerTv.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(vaccineDetails.getVaccineName())) {
                holder.name.setText(vaccineDetails.getVaccineName());
            }

            //show Dose Next to vaccine Name, if Vaccine Name is empty then show dose only
            String vaccineNameWithDose = "";

            if (TextUtils.isEmpty(vaccineDetails.getVaccineNameInShort()) && TextUtils.isEmpty(vaccineDetails.getVaccineDose())) {
                //DO nothing
            } else if (!TextUtils.isEmpty(vaccineDetails.getVaccineNameInShort()) && TextUtils.isEmpty(vaccineDetails.getVaccineDose())) {
                vaccineNameWithDose = vaccineDetails.getVaccineNameInShort();
            } else if (TextUtils.isEmpty(vaccineDetails.getVaccineNameInShort()) && !TextUtils.isEmpty(vaccineDetails.getVaccineDose())) {
                vaccineNameWithDose = vaccineDetails.getVaccineDose();
            } else {
                vaccineNameWithDose = vaccineDetails.getVaccineNameInShort() + " - " + vaccineDetails.getVaccineDose();
            }
            holder.acronyms.setText((vaccineNameWithDose));
            if(!TextUtils.isEmpty(vaccineDetails.getVaccineDateTime())){
                holder.mTickedImagView.setVisibility(View.VISIBLE);
            }else{
                holder.mTickedImagView.setVisibility(View.GONE);
            }
            //holder.date.setText((mVaccineDetailsesList.get(position).getAgeAt()) +"");
        }
        return convertView;
    }

    public void setData(List<VaccineDetails> vaccineDetailsList){
        mVaccineDetailsesList = vaccineDetailsList;
    }
}
