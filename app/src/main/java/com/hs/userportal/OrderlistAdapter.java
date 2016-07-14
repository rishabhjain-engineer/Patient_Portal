package com.hs.userportal;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itextpdf.text.pdf.parser.Line;

import java.util.List;

/**
 * Created by ashish on 10/27/2015.
 */
public class OrderlistAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<OrderList> orderlist;

    public OrderlistAdapter(Activity activity, List<OrderList> orderlist) {
        this.activity = activity;
        this.orderlist = orderlist;
    }

    @Override
    public int getCount() {
        return orderlist.size();
    }

    @Override
    public Object getItem(int location) {
        return orderlist.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.order_itemnew, null);
        }
       /* TextView order_no = (TextView) convertView.findViewById(R.id.order_no);
        TextView pkgname = (TextView) convertView.findViewById(R.id.pkgname);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView amount = (TextView) convertView.findViewById(R.id.amount);
        TextView test_list = (TextView) convertView.findViewById(R.id.test_list);*/
        TextView order_no = (TextView) convertView.findViewById(R.id.ordernumber_id);
        TextView pkgname = (TextView) convertView.findViewById(R.id.pkgname);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView amount = (TextView) convertView.findViewById(R.id.netamount_id);
        TextView test_list = (TextView) convertView.findViewById(R.id.test_list);
        TextView order_status = (TextView) convertView.findViewById(R.id.order_status);
        LinearLayout change = (LinearLayout) convertView.findViewById(R.id.change);
        OrderList ordr = orderlist.get(position);

        order_no.setText(ordr.getOrderId());
        String strArr[] = ordr.getOrderDateTime().split("\\s");
        if (strArr[0].equalsIgnoreCase("null")) {
            date.setText("-");
        } else {
            date.setText(strArr[0]);
        }
        String OrderBillingAmount = null;
        int OrderBilling = 0;
        if (ordr.getOrderBillingAmount() != "null") {
            OrderBilling = (int) Math.round(Double.parseDouble(ordr.getOrderBillingAmount()));
            OrderBillingAmount = String.valueOf(OrderBilling);
        } else {
            OrderBillingAmount = "-";
        }
        try {
            if (ordr.getPromoCodeDiscount() != "null" && ordr.getPromoCodeDiscount() != null) {
                int promo = (int) Math.round(Double.parseDouble(ordr.getPromoCodeDiscount()));
                OrderBillingAmount = String.valueOf(OrderBilling - promo);
            } else if (ordr.getDiscountInPercentage() != "null") {
                double calculateamontprice = OrderBilling * (1 - ((int) Math.round(Double.parseDouble(ordr.getDiscountInPercentage()))) / 100);
                OrderBillingAmount = String.valueOf((int) Math.round(calculateamontprice));
            }
        } catch (NumberFormatException ne) {
            ne.printStackTrace();
        }
        if (!OrderBillingAmount.equalsIgnoreCase("-")) {
            OrderBillingAmount = "₹ " + OrderBillingAmount;
        }
        amount.setText(OrderBillingAmount);
        pkgname.setText(ordr.getCentreName());
        if (ordr.getOrderStatus().equalsIgnoreCase("1") && ordr.getSamplePickupstatus().equalsIgnoreCase("null")) {
            order_status.setText("PENDING");
            order_status.setTextColor(Color.parseColor("#fe8534"));
        } else if (ordr.getOrderStatus().equalsIgnoreCase("1") && ordr.getSamplePickupstatus().equalsIgnoreCase("0")) {
            order_status.setText("CANCELLED");
            order_status.setTextColor(Color.parseColor("#ED2727"));
        } else if (ordr.getOrderStatus().equalsIgnoreCase("0")) {
            order_status.setText("CANCELLED");
            order_status.setTextColor(Color.parseColor("#ED2727"));
        } else if (ordr.getOrderStatus().equalsIgnoreCase("1") && ordr.getSamplePickupstatus().equalsIgnoreCase("1")) {
            order_status.setText("COMPLETED");
            order_status.setTextColor(Color.parseColor("#65A366"));
        }
        test_list.setText(ordr.getTestName());
        return convertView;
    }
}
