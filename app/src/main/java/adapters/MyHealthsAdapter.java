package adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hs.userportal.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


/**
 * Created by ashish on 10/20/2015.
 */


public class MyHealthsAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<HashMap<String, String>> OrderReportList;

    public MyHealthsAdapter(Activity activity, ArrayList<HashMap<String, String>> SortList) {
        this.activity = activity;
        OrderReportList = SortList;
    }

    public MyHealthsAdapter(Activity activity) {
        this.activity = activity;
    }

    public void setListData(ArrayList<HashMap<String, String>> SortList) {
        OrderReportList = SortList;
    }

    @Override
    public int getCount() {
        return OrderReportList.size();
    }

    @Override
    public Object getItem(int location) {
        return OrderReportList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.myhealth_adapter, null);
        TextView wt_heading = (TextView) convertView.findViewById(R.id.wt_heading);
        TextView fromdate_id = (TextView) convertView.findViewById(R.id.fromdate_id);
        wt_heading.setText(OrderReportList.get(position).get("weight"));
        String date[] = OrderReportList.get(position).get("fromdate").split("T");
        fromdate_id.setText(parseDateToddMMyyyy(date[0]));

        return convertView;
    }

    private String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "dd-MM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
}
