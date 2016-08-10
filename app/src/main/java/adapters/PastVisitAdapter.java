package adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hs.userportal.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ashish on 10-Aug-16.
 */
public class PastVisitAdapter extends BaseAdapter {

    private List<HashMap<String, String>> array = new ArrayList<>();
    private Activity act;
    private LayoutInflater inflater;
    private ViewHolder holder = null;

    public PastVisitAdapter(Activity act, List<HashMap<String, String>> array) {
        this.array = array;
        this.act = act;
    }

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(int location) {
        return array.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row, null);
            holder = new ViewHolder();
            holder.label = (TextView) convertView.findViewById(R.id.label);
            holder.value = (TextView) convertView.findViewById(R.id.value);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.label.setText(array.get(position).get("rowid"));
        holder.value.setText(array.get(position).get("col_1"));
        return convertView;
    }

    static class ViewHolder {
        TextView label, value;
    }
}
