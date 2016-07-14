package adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.hs.userportal.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rahul2 on 3/31/2016.
 */
public class Custom_profile_adapter extends BaseAdapter {
    Activity activity;
    ArrayList<HashMap<String, String>> list;
    private LayoutInflater inflater;
    private String whichactivity;
    public Custom_profile_adapter(Activity activity,  ArrayList<HashMap<String, String>> list,String whichactivity){
        this.activity = activity;
        this.list = list;
        this.whichactivity=whichactivity;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int location) {
        return list.get(location);

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
            convertView = inflater.inflate(R.layout.custom_profile_raw, null);

        TextView top1 = (TextView)convertView.findViewById(R.id.top1);
        TextView top2 = (TextView)convertView.findViewById(R.id.top2);
        TextView top3 = (TextView)convertView.findViewById(R.id.top3);
        TextView top4 = (TextView)convertView.findViewById(R.id.top4);
        TextView top5 = (TextView)convertView.findViewById(R.id.top5);

     //   TextView defaults=(TextView)convertView.findViewById(R.id.defaults);
        if(whichactivity.equalsIgnoreCase("Education")||whichactivity.equalsIgnoreCase("Work")) {

            top1.setText(list.get(position).get("name"));
            top2.setText(list.get(position).get("address"));
            top3.setText(list.get(position).get("city") + ", " + list.get(position).get("state"));
            if(!list.get(position).get("postaladdress").equals("")) {
                top4.setText(list.get(position).get("country") + ", " + list.get(position).get("postaladdress"));
            }else{
                top4.setText(list.get(position).get("country"));
            }
            top5.setText(list.get(position).get("from") + " - " + list.get(position).get("to"));

        }else  if(whichactivity.equalsIgnoreCase("Residence")){

            //top1.setTypeface(null, Typeface.NORMAL);
            top1.setText(list.get(position).get("name"));
            top2.setText(list.get(position).get("address"));
            top3.setText(list.get(position).get("city") + ", " + list.get(position).get("state"));
            if(!list.get(position).get("postaladdress").equals("")) {
                top4.setText(list.get(position).get("country") + ", " + list.get(position).get("postaladdress"));
            }else{
                top4.setText(list.get(position).get("country"));
            }
            top5.setText(list.get(position).get("from") + " - " + list.get(position).get("to"));

        }else if(whichactivity.equalsIgnoreCase("Travel")){
              top1.setTypeface(null, Typeface.NORMAL);
            top1.setVisibility(View.GONE);
            top2.setText(list.get(position).get("name"));
            String city=list.get(position).get("city");
      if(list.get(position).get("city").equals("")&&list.get(position).get("state").equals("")){
          top3.setVisibility(View.GONE);
      }else{
          top3.setVisibility(View.VISIBLE);
          if(list.get(position).get("state").equals("")){
              city.replace(",","");
          }
      }
            top3.setText(city + list.get(position).get("state").replace("\n",""));
            if(!list.get(position).get("postaladdress").equals("")) {
                top4.setText(list.get(position).get("country") + ", " + list.get(position).get("postaladdress"));
            }else{
                top4.setText(list.get(position).get("country"));
            }
            top5.setText(list.get(position).get("from") + " - " + list.get(position).get("to"));
        }


        return convertView;
    }
}