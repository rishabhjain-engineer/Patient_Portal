package adapters;

/**
 * Created by rahul2 on 12/11/2015.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.Toast;

import com.cloudchowk.patient.R;

import java.util.ArrayList;

public class Package_filterExpandableAdapter
extends BaseExpandableListAdapter {

    private Activity activity;
    private ArrayList<Object> childtems;
    private LayoutInflater inflater;
    private ArrayList<String> parentItems, child;

    public Package_filterExpandableAdapter(ArrayList<String> parents, ArrayList<Object> childern) {
        this.parentItems = parents;
        this.childtems = childern;
    }

    public void setInflater(LayoutInflater inflater, Activity activity) {
        this.inflater = inflater;
        this.activity = activity;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        child = (ArrayList<String>) childtems.get(groupPosition);

        CheckBox check = null;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.childgroup_raw, null);
        }

        check = (CheckBox) convertView.findViewById(R.id.checkBox);
        check.setText(child.get(childPosition));

        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(activity, child.get(childPosition),
                        Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.parent_expandableview, null);
        }

        ((CheckedTextView) convertView).setText(parentItems.get(groupPosition));
        ((CheckedTextView) convertView).setChecked(isExpanded);

        return convertView;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return ((ArrayList<String>) childtems.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public int getGroupCount() {
        return parentItems.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

}