package adapters;

/**
 * Created by android1 on 12/6/17.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.hs.userportal.R;

import java.util.ArrayList;
import java.util.List;

import models.Symptoms;

public class SymptomsAdapter extends BaseAdapter {

    private List<Symptoms> mSymptomsList = new ArrayList<>();
    private Activity mActivity;

    public SymptomsAdapter(Activity activity, List<Symptoms> symptomsList) {
        mActivity = activity;
        mSymptomsList = symptomsList;
    }

    public void setData(List<Symptoms> stringsList) {
        mSymptomsList = stringsList;
    }

    @Override
    public int getCount() {
        return mSymptomsList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    private class ViewHolder {
        TextView name;
        CheckBox checkBox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SymptomsAdapter.ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.symptoms_single_item_view, null);

            holder = new SymptomsAdapter.ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.symptoms_name);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(holder);

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    CheckBox cb = (CheckBox) view;
                    Symptoms symptoms = (Symptoms) cb.getTag();
                    symptoms.setChecked(cb.isChecked());
                }
            });
        } else {
            holder = (SymptomsAdapter.ViewHolder) convertView.getTag();
        }

        Symptoms symptoms = mSymptomsList.get(position);
        holder.name.setText(symptoms.getName());
        holder.checkBox.setChecked(symptoms.isChecked());
        holder.checkBox.setTag(symptoms);

        return convertView;

    }
}
