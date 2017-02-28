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



/**
 * Created by ashish on 10/20/2015.
 */



public class OrderReportAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<HashMap<String,String>> OrderReportList;
    private String [] testnameArray;


    public OrderReportAdapter(Activity activity, ArrayList<HashMap<String,String>> SortList,String testname) {
        this.activity = activity;
        this.OrderReportList = SortList;


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
            convertView = inflater.inflate(R.layout.view_order_report_row, null);
        TextView testname_id = (TextView) convertView.findViewById(R.id.testname_id);
        TextView orderactual_amnt = (TextView) convertView.findViewById(R.id.orderactual_amnt);
        TextView orderdiscount = (TextView) convertView.findViewById(R.id.orderdiscount);
        TextView ordered_billingamnt = (TextView) convertView.findViewById(R.id.ordered_billingamnt);
        testname_id.setText("ABCD");
        orderactual_amnt.setText(OrderReportList.get(position).get("OrderAmount"));
        orderdiscount.setText(OrderReportList.get(position).get("OrderDiscount"));
        ordered_billingamnt.setText(OrderReportList.get(position).get("OrderBillingAmount"));


       /* TextView pkgname = (TextView) convertView.findViewById(R.id.pkgname);
        TextView labname = (TextView) convertView.findViewById(R.id.labname);
        TextView test_param = (TextView) convertView.findViewById(R.id.test_param);
        TextView mrp = (TextView) convertView.findViewById(R.id.mrp);
        TextView dicount = (TextView) convertView.findViewById(R.id.dicount);
        TextView price = (TextView) convertView.findViewById(R.id.price);
        final TextView testdetail = (TextView) convertView.findViewById(R.id.testdetail);
        LinearLayout btn_testdetail=(LinearLayout)convertView.findViewById(R.id.btn_testdetail);
        pkgname.setText(SortList.get(position).get("TestName").toUpperCase());
        labname.setText(SortList.get(position).get("CentreName").toUpperCase());
        test_param.setText(SortList.get(position).get("NoofPerameter"));
        if(SortList.get(position).get("PackageType").equalsIgnoreCase("1")){
            testdetail.setText("BOOK TEST");
        }else{
            testdetail.setText("TEST DETAILS");
        }
        mrp.setText("₹ "+String.valueOf(Math.round(Float.parseFloat(SortList.get(position).get("Price")))));
        dicount.setText(String.valueOf(Math.round(Float.parseFloat(SortList.get(position).get("Discount")) * 100) + " % OFF"));
        int finalprice=Math.round(Float.parseFloat(SortList.get(position).get("Price")))-(Math.round((Float.parseFloat(SortList.get(position).get("Price"))*(Float.parseFloat(SortList.get(position).get("Discount"))))));
        price.setText("₹ "+String.valueOf(finalprice));

        btn_testdetail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    if(testdetail.getText().toString().equalsIgnoreCase("TEST DETAILS")) {
                        customListner.onPkg_DetailsButtonClickListner(position, SortList.get(position).get("TestName"));
                    }else {
                        customListner.onButtonClickListner("BOOK TEST",position);
                    }
                }
            }
        });*/
        return convertView;
    }

   /* // Home Package Button
    Package_btnListener customListner;

    public interface Package_btnListener {

        public void onButtonClickListner(String text, int pos);

        public void onPkg_DetailsButtonClickListner(int position, String value);
    }
    public void setpackage_btnListener(Package_btnListener listner){
        this.customListner = listner;
    }

    public void setonPkg_DetailsButtonClickListner(Package_btnListener listner){
        this.customListner = listner;
    }


    public void filter(String charText) {
        HashMap<String, String> hmap;
        charText = charText.toLowerCase(Locale.getDefault());
        this.SortList.clear();
        if (charText.length() == 0) {
            this.SortList.addAll(privatearray);
        } else {
            for (int i=0;i< this.privatearray.size();i++) {
                if (privatearray.get(i).get("TestName").toLowerCase(Locale.getDefault()).contains(charText)) {
                    hmap = new HashMap<String, String>();
                    hmap.put("PackageId", privatearray.get(i).get("PackageId"));
                    hmap.put("TestName", privatearray.get(i).get("TestName"));
                    hmap.put("CentreName", privatearray.get(i).get("CentreName"));
                    hmap.put("CentreId", privatearray.get(i).get("CentreId"));
                    hmap.put("Logo", privatearray.get(i).get("Logo"));
                    hmap.put("NoofPerameter", privatearray.get(i).get("NoofPerameter"));
                    hmap.put("HomePriority", privatearray.get(i).get("HomePriority"));
                    hmap.put("PackageType", privatearray.get(i).get("PackageType"));
                    hmap.put("PackageName", privatearray.get(i).get("PackageName"));
                    hmap.put("Priority", privatearray.get(i).get("Priority"));
                    hmap.put("TestId", privatearray.get(i).get("TestId"));
                    hmap.put("TestPriority", privatearray.get(i).get("TestPriority"));
                    hmap.put("Price", privatearray.get(i).get("Price"));
                    hmap.put("Discount", privatearray.get(i).get("Discount"));
                    this.SortList.add(hmap);
                }
            }
        }

        notifyDataSetChanged();

    }*/
}
