package adapters;

/**
 * Created by ayaz on 12/6/17.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.hs.userportal.R;

import java.util.List;

import models.Symptoms;

public class SymptomsAdapter extends ArrayAdapter<Symptoms> {

    private LayoutInflater inflater;
    private List<Symptoms> mSymptomsList;

    public SymptomsAdapter(Context context, List<Symptoms> symptomsList) {
        super(context, R.layout.symptoms_single_item_view, R.id.rowTextView, symptomsList);
        mSymptomsList = symptomsList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mSymptomsList.size();
    }

    /**
     * Holds child views for one row.
     */
    public static class SymptomsViewHolder {
        private CheckBox checkBox;
        private TextView textView;

        public SymptomsViewHolder(TextView textView, CheckBox checkBox) {
            this.checkBox = checkBox;
            this.textView = textView;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }

        public TextView getTextView() {
            return textView;
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Symptoms symptoms = (Symptoms) mSymptomsList.get(position);

        // The child views in each row.
        CheckBox checkBox;
        TextView textView;

        // Create a new row view
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.symptoms_single_item_view, null);

            // Find the child views.
            textView = (TextView) convertView.findViewById(R.id.rowTextView);
            checkBox = (CheckBox) convertView.findViewById(R.id.CheckBox01);

            // Optimization: Tag the row with it's child views, so we don't have to
            // call findViewById() later when we reuse the row.
            convertView.setTag(new SymptomsViewHolder(textView, checkBox));

            checkBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    Symptoms symptoms = (Symptoms) cb.getTag();
                    symptoms.setChecked(cb.isChecked());
                }
            });
        }
        // Reuse existing row view
        else {
            // Because we use a ViewHolder, we avoid having to call findViewById().
            SymptomsViewHolder viewHolder = (SymptomsViewHolder) convertView.getTag();
            checkBox = viewHolder.getCheckBox();
            textView = viewHolder.getTextView();
        }

        // Tag the CheckBox with the symptoms it is displaying, so that we can
        // access the symptoms in onClick() when the CheckBox is toggled.
        checkBox.setTag(symptoms);

        // Display symptoms data
        checkBox.setChecked(symptoms.isChecked());
        textView.setText(symptoms.getName());

        return convertView;
    }

    public void setData(List<Symptoms> symptomes) {
        mSymptomsList = symptomes;
    }

}