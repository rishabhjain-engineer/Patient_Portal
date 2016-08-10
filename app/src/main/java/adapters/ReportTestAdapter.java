package adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hs.userportal.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ashish on 10-Aug-16.
 */
public class ReportTestAdapter extends BaseAdapter {

    private List<HashMap<String, String>> array = new ArrayList<>();
    private Activity act;
    private LayoutInflater inflater;
    private ViewHolder holder = null;

    public ReportTestAdapter(Activity act, List<HashMap<String, String>> array) {
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
            convertView = inflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.txt = (TextView) convertView.findViewById(R.id.txt);
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txt.setText(array.get(position).get("Description"));
        if (array.get(position).get("IsTestCompleted").equals("true")
                && array.get(position).get("IsSampleReceived").equals("true")) {
            if (array.get(position).get("IsPublish").equals("truePAID"))
                holder.img.setImageResource(R.drawable.image1);
            else
                holder.img.setImageResource(R.drawable.image3);

        } else if (array.get(position).get("IsTestCompleted").equals("null")
                && array.get(position).get("IsSampleReceived").equals("true"))
            holder.img.setImageResource(R.drawable.image3);
        else
            holder.img.setImageResource(R.drawable.image2);

        return convertView;
    }

    static class ViewHolder {
        TextView txt;
        ImageView img;
    }
}
