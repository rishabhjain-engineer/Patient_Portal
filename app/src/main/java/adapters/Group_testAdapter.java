package adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hs.userportal.R;

import java.util.List;

/**
 * Created by rahul2 on 4/5/2016.
 */
public class Group_testAdapter extends BaseAdapter {
    Activity activity;
   private List<String> chartDates,chartValues,chartunitlist,casecodeslist;
    private LayoutInflater inflater;
    private String RangeTo,RangeFrom;
    private String whichactivity;
    public Group_testAdapter(Activity activity,  List<String> chartDates,List<String> chartValues,List<String> casecodeslist,List<String> chartunitlist,String RangeFrom,String RangeTo){
        this.activity = activity;
        this.chartDates = chartDates;
        this.chartValues = chartValues;
        this.chartunitlist = chartunitlist;
        this.whichactivity=whichactivity;
        this.casecodeslist=casecodeslist;
      /*  this.check_result_color=check_result_color;*/
        this.RangeFrom=RangeFrom;
        this.RangeTo=RangeTo;
    }

    @Override
    public int getCount() {
        return casecodeslist.size();
    }

    @Override
    public Object getItem(int location) {
        return casecodeslist.get(location);

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
            convertView = inflater.inflate(R.layout.group_test_row, null);
            TextView test_name = (TextView)convertView.findViewById(R.id.test_name);
            TextView result_value = (TextView)convertView.findViewById(R.id.result_value);
            TextView reference_range = (TextView)convertView.findViewById(R.id.reference_range);
            test_name.setText(String.valueOf(position+1)+".  "+chartDates.get(position));
        if(chartunitlist.size()!=0 && chartunitlist.get(position)!=null) {
            result_value.setText(chartValues.get(position) + " " + chartunitlist.get(position));
        }else{
            result_value.setText(chartValues.get(position));
        }
        try {
            if(Float.parseFloat(RangeFrom)>Float.parseFloat(chartValues.get(position))||Float.parseFloat(RangeTo)<Float.parseFloat(chartValues.get(position))){
                result_value.setTextColor(Color.parseColor("#FF2D2D"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
           /* if(check_result_color==true){
                result_value.setTextColor(Color.parseColor("#FF2D2D"));
            }*/
            reference_range.setText("Case code: "+casecodeslist.get(position));



        return convertView;
    }
}