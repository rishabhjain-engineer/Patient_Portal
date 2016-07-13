package adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cloudchowk.patient.PackageTestDetails;
import com.cloudchowk.patient.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ashish on 3/23/2016.
 */
public class Report_Adapter extends BaseAdapter {

    private JSONArray reportarray;
    private Activity activity;
    ViewHolder holder = null;
    private LayoutInflater inflater;
    int flag = 0;
    private ArrayList<HashMap<String, String>> test_list = new ArrayList<HashMap<String, String>>();
    private String secondary_cat;

    public Report_Adapter(JSONArray reportarray, Activity activity) {
        this.reportarray = reportarray;
        int size = reportarray.length();
        this.activity = activity;
        for (int i = 0; i < size; i++) {
            try {
                if (reportarray.getJSONObject(i).getString("ActualProfileName").equalsIgnoreCase(reportarray.getJSONObject(i).getString("NewProfileName"))) {
                    HashMap<String, String> hmap = new HashMap<String, String>();
                    hmap.put("ResultType", reportarray.getJSONObject(i).getString("ResultType"));
                    hmap.put("Description", reportarray.getJSONObject(i).getString("Description"));
                    hmap.put("RangeFrom", reportarray.getJSONObject(i).getString("RangeFrom"));
                    hmap.put("RangeTo", reportarray.getJSONObject(i).getString("RangeTo"));
                    hmap.put("ResultValue", reportarray.getJSONObject(i).getString("ResultValue"));
                    hmap.put("UnitCode", reportarray.getJSONObject(i).getString("UnitCode"));
                    hmap.put("RangeValue", reportarray.getJSONObject(i).getString("RangeValue"));
                    hmap.put("View_Part", "First");
                    test_list.add(hmap);
                }
            } catch (JSONException jse) {
                jse.printStackTrace();
            }
        }

        for (int k = 0; k < reportarray.length(); k++) {
            try {
                if (!reportarray.getJSONObject(k).getString("ActualProfileName").equalsIgnoreCase(reportarray.getJSONObject(k).getString("NewProfileName"))) {
                    HashMap<String, String> hmap = new HashMap<String, String>();
                    hmap.put("ResultType", reportarray.getJSONObject(k).getString("ResultType"));
                    hmap.put("Description", reportarray.getJSONObject(k).getString("Description"));
                    hmap.put("RangeFrom", reportarray.getJSONObject(k).getString("RangeFrom"));
                    hmap.put("RangeTo", reportarray.getJSONObject(k).getString("RangeTo"));
                    hmap.put("ResultValue", reportarray.getJSONObject(k).getString("ResultValue"));
                    hmap.put("UnitCode", reportarray.getJSONObject(k).getString("UnitCode"));
                    hmap.put("RangeValue", reportarray.getJSONObject(k).getString("RangeValue"));
                    hmap.put("View_Part", "Second");
                    if (flag == 0) {
                        hmap.put("FLAG", "first_time");
                        flag = 1;
                    } else {
                        hmap.put("FLAG", "");
                    }
                    test_list.add(hmap);
                    secondary_cat = reportarray.getJSONObject(k).getString("ActualProfileName");
                }
            } catch (JSONException jse) {
                jse.printStackTrace();
            }
        }


    }

    @Override
    public int getCount() {
        return test_list.size();
    }

    @Override
    public Object getItem(int location) {
        return test_list.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView test_name, result_value, reference_range, secondary;
        WebView webview;
        int flag = 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.report_row, null);
            holder = new ViewHolder();
            holder.test_name = (TextView) convertView.findViewById(R.id.test_name);
            holder.result_value = (TextView) convertView.findViewById(R.id.result_value);
            holder.reference_range = (TextView) convertView.findViewById(R.id.reference_range);
            holder.webview = (WebView) convertView.findViewById(R.id.webview);
            holder.secondary = (TextView) convertView.findViewById(R.id.secondary);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {
           /* for (int z = 0; z < reportarray.length(); z++) {*/
            if (test_list.get(position).get("ResultType")
                    .equals("Numerical")) {
                   /* if(z>1){

                       *//* if(!reportarray.getJSONObject(z).getString("NewProfileName").equals(reportarray.getJSONObject(z-1).getString("NewProfileName")))
                        {
                            holder.test_name.setText(reportarray.getJSONObject(z).getString(
                                    "NewProfileName"));
                        }*//*
                    }*/
                if (test_list.get(position).get("View_Part").equalsIgnoreCase("First")) {
                    holder.webview.setVisibility(View.GONE);
                    holder.test_name.setVisibility(View.VISIBLE);
                    holder.result_value.setVisibility(View.VISIBLE);
                    holder.reference_range.setVisibility(View.VISIBLE);
                    holder.secondary.setVisibility(View.GONE);
                    holder.test_name.setText(test_list.get(position).get(
                            "Description"));
                    if (test_list.get(position)
                            .get("RangeFrom").equalsIgnoreCase("null")) {
                        holder.reference_range.setVisibility(View.GONE);
                    } else if (test_list.get(position).
                            get("RangeTo").equalsIgnoreCase("null")) {
                        holder.reference_range.setText("Ref. Range: " + test_list.get(position)
                                .get("RangeFrom"));
                    } else {
                        holder.reference_range.setText("Ref. Range: " + test_list.get(position)
                                .get("RangeFrom") + "-" + test_list.get(position).
                                get("RangeTo"));
                    }
                    float m, n, q;

                    if (test_list.get(position).get("UnitCode").equalsIgnoreCase("null")) {
                        holder.result_value.setText(test_list.get(position).get("ResultValue"));
                    } else {
                        holder.result_value.setText(test_list.get(position).get("ResultValue") + " " + test_list.get(position).get("UnitCode"));
                    }

                    if (!reportarray.getJSONObject(position).getString("Description").equals("ALKALINE PHOSPHATASE")) {
                        m = Float.parseFloat(test_list.get(position).get("ResultValue"));
                        n = Float.parseFloat(test_list.get(position).get("RangeFrom"));
                        q = Float.parseFloat(test_list.get(position).get("RangeTo"));
                        if (m < n || m > q) {
                            holder.result_value.setTextColor(Color.RED);
                        } else {
                            holder.result_value.setTextColor(Color.BLACK);
                        }
                    }
                } else {
                    holder.webview.setVisibility(View.GONE);
                    holder.test_name.setVisibility(View.VISIBLE);
                    holder.result_value.setVisibility(View.VISIBLE);
                    holder.reference_range.setVisibility(View.VISIBLE);

                    if (test_list.get(position).get("FLAG").equalsIgnoreCase("first_time")) {
                        if (secondary_cat.equalsIgnoreCase("null")) {
                            holder.secondary.setVisibility(View.GONE);
                        } else {
                            holder.secondary.setVisibility(View.VISIBLE);
                            holder.secondary.setText(secondary_cat);
                        }
                    } else if (secondary_cat.equalsIgnoreCase("null")) {
                        holder.secondary.setVisibility(View.GONE);
                    } else {
                        holder.secondary.setVisibility(View.GONE);
                    }
                    holder.test_name.setText(test_list.get(position).get(
                            "Description"));
                    if (test_list.get(position)
                            .get("RangeFrom").equalsIgnoreCase("null")) {
                        holder.reference_range.setVisibility(View.GONE);
                    } else if (test_list.get(position).
                            get("RangeTo").equalsIgnoreCase("null")) {
                        holder.reference_range.setText("Ref. Range: " + test_list.get(position)
                                .get("RangeFrom"));
                    } else {
                        holder.reference_range.setText("Ref. Range: " + test_list.get(position)
                                .get("RangeFrom") + "-" + test_list.get(position).
                                get("RangeTo"));
                    }
                    float m, n, q;

                    if (test_list.get(position).get("UnitCode").equalsIgnoreCase("null")) {
                        holder.result_value.setText(test_list.get(position).get("ResultValue"));
                    } else {
                        holder.result_value.setText(test_list.get(position).get("ResultValue") + " " + test_list.get(position).get("UnitCode"));
                    }
                    if (!reportarray.getJSONObject(position).getString("Description").equals("ALKALINE PHOSPHATASE")) {
                        m = Float.parseFloat(test_list.get(position).get("ResultValue"));
                        n = Float.parseFloat(test_list.get(position).get("RangeFrom"));
                        q = Float.parseFloat(test_list.get(position).get("RangeTo"));
                        if (m < n || m > q) {
                            holder.result_value.setTextColor(Color.RED);
                        } else {
                            holder.result_value.setTextColor(Color.BLACK);
                        }
                    }
                }
            } else if (test_list.get(position).get("ResultType")
                    .equals("Words")) {
                   /* if(z>1){
                       *//* if(!reportarray.getJSONObject(z).getString("NewProfileName").equals(reportarray.getJSONObject(z-1).getString("NewProfileName")))
                        {
                            holder.test_name.setText(reportarray.getJSONObject(z).getString(
                                    "NewProfileName"));
                        }*//*
                    }*/
                if (test_list.get(position).get("View_Part").equalsIgnoreCase("First")) {
                    holder.webview.setVisibility(View.GONE);
                    holder.test_name.setVisibility(View.VISIBLE);
                    holder.result_value.setVisibility(View.VISIBLE);
                    holder.reference_range.setVisibility(View.VISIBLE);
                    holder.secondary.setVisibility(View.GONE);
                    holder.test_name.setText(test_list.get(position).get(
                            "Description"));
                    String rangesad = test_list.get(position).get("RangeValue");
                    String resultDS = test_list.get(position).get("ResultValue");
                    if (test_list.get(position).get(
                            "RangeValue").equals("null")) {
                        holder.reference_range.setText("");
                        holder.reference_range.setVisibility(View.GONE);
                    } else {
                        holder.reference_range.setVisibility(View.VISIBLE);
                        holder.reference_range.setText("Reference Range: " + rangesad);
                    }
                    holder.result_value.setVisibility(View.VISIBLE);
                    String check_range = test_list.get(position).get(
                            "ResultValue");
                    holder.result_value.setText(test_list.get(position).get(
                            "ResultValue"));
                    if (!test_list.get(position).get(
                            "RangeValue").equalsIgnoreCase(test_list.get(position).get(
                            "ResultValue")) && test_list.get(position).get(
                            "ResultValue") != "null") {
                        holder.result_value.setTextColor(Color.parseColor("#FF0000"));
                    } else {
                        holder.result_value.setTextColor(Color.parseColor("#1E1E1E"));
                    }
                } else {
                    holder.webview.setVisibility(View.GONE);
                    holder.test_name.setVisibility(View.VISIBLE);
                    holder.result_value.setVisibility(View.VISIBLE);
                    holder.reference_range.setVisibility(View.VISIBLE);

                    if (test_list.get(position).get("FLAG").equalsIgnoreCase("first_time")) {
                        holder.secondary.setVisibility(View.VISIBLE);
                        holder.secondary.setText(secondary_cat);
                    }
                    if (secondary_cat.equalsIgnoreCase("null")) {
                        holder.secondary.setVisibility(View.GONE);
                    } else {
                        holder.secondary.setVisibility(View.GONE);

                    }

                    holder.test_name.setText(test_list.get(position).get(
                            "Description"));
                    if (test_list.get(position)
                            .get("RangeFrom").equalsIgnoreCase("null")) {
                        holder.reference_range.setVisibility(View.GONE);
                    } else if (test_list.get(position).
                            get("RangeTo").equalsIgnoreCase("null")) {
                        holder.reference_range.setText("Ref. Range: " + test_list.get(position)
                                .get("RangeFrom"));
                    } else {
                        holder.reference_range.setText("Ref. Range: " + test_list.get(position)
                                .get("RangeFrom") + "-" + test_list.get(position).
                                get("RangeTo"));
                    }
                    if (test_list.get(position).get("UnitCode").equalsIgnoreCase("null")) {
                        holder.result_value.setText(test_list.get(position).get("ResultValue"));
                    } else {
                        holder.result_value.setText(test_list.get(position).get("ResultValue") + " " + test_list.get(position).get("UnitCode"));
                    }
                    String rangesad = test_list.get(position).get("RangeValue");
                    String resultDS = test_list.get(position).get("ResultValue");
                    if (test_list.get(position).get(
                            "RangeValue").equals("null")) {
                        holder.reference_range.setText("");
                        holder.reference_range.setVisibility(View.GONE);
                    } else {
                        holder.reference_range.setVisibility(View.VISIBLE);
                        holder.reference_range.setText("Reference Range: " + rangesad);
                    }
                    holder.result_value.setVisibility(View.VISIBLE);
                    String check_range = test_list.get(position).get(
                            "ResultValue");
                    holder.result_value.setText(test_list.get(position).get(
                            "ResultValue"));
                    if (!test_list.get(position).get(
                            "RangeValue").equalsIgnoreCase(test_list.get(position).get(
                            "ResultValue")) && test_list.get(position).get(
                            "ResultValue") != "null") {
                        holder.result_value.setTextColor(Color.parseColor("#FF0000"));
                    } else {
                        holder.result_value.setTextColor(Color.parseColor("#1E1E1E"));
                    }
                }
            } else {
                holder.webview.setVisibility(View.VISIBLE);
                holder.test_name.setVisibility(View.VISIBLE);
                holder.result_value.setVisibility(View.GONE);
                holder.reference_range.setVisibility(View.GONE);
                holder.secondary.setVisibility(View.GONE);
                holder.test_name.setText(test_list.get(position).get(
                        "Description"));
                String htmldata = test_list.get(position).get(
                        "ResultValue");
                holder.webview.loadData(htmldata, "text/html; charset=utf-8", "UTF-8");
            }
           /* }*/
        } catch (JSONException je) {
            je.printStackTrace();
        }
        return convertView;
    }
}
