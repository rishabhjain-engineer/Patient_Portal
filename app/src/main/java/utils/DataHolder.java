package utils;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.cloudchowk.patient.R;

import java.util.ArrayList;

/**
 * Created by ashish on 4/21/2016.
 */
public class DataHolder {

    private int selected;
    private ArrayAdapter<CharSequence> adapter;

    public DataHolder(Context parent, ArrayList<String> options) {
        adapter = new ArrayAdapter(parent, R.layout.spinner_item, options);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item/*android.R.layout.simple_spinner_dropdown_item*/);

    }

    public ArrayAdapter<CharSequence> getAdapter() {
        return adapter;
    }

    public String getText() {
        return (String) adapter.getItem(selected);
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

}
