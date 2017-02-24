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
    private int mSize =0 ;

    public PastVisitAdapter(Activity act) {
        this.act = act;
    }

    public void setData(List<HashMap<String, String>> array){
        this.array = array;
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
            convertView = inflater.inflate(R.layout.pastvisit_item, null);
            holder = new ViewHolder();
            holder.label = (TextView) convertView.findViewById(R.id.label);
            holder.value = (TextView) convertView.findViewById(R.id.value);
            holder.labName = (TextView) convertView.findViewById(R.id.labName);
            holder.test_list = (TextView) convertView.findViewById(R.id.test_list);
            holder.amount = (TextView) convertView.findViewById(R.id.amount);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.id_code = (TextView) convertView.findViewById(R.id.id_code);
            holder.date_heading = (TextView) convertView.findViewById(R.id.date_heading);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (array.get(position).get("TYPE").equalsIgnoreCase("Lab")) {
            holder.id_code.setText("CASE CODE");
            holder.date_heading.setText("ADVISE DATE");
            holder.label.setText(array.get(position).get("CaseCode"));
            holder.value.setText(array.get(position).get("TimeStamp"));
            holder.labName.setText(array.get(position).get("ApplicationName"));
            holder.test_list.setText(array.get(position).get("TestName"));
            Double amount_req = Double.valueOf(array.get(position).get("PaidAmount"));
            String subTotal = "₹ " + String.format("%.2f", amount_req);
            holder.amount.setText(subTotal);
            String discString = array.get(position).get("DiscountAmount");
            float disc = 0, paid;
            if (discString.matches(".*\\d.*")) {
                disc = Float.parseFloat(discString);
                paid = Float.parseFloat(array.get(position).get(
                        "ActualAmount"))
                        - Float.parseFloat(array.get(position).get("InitialAmount")) - disc;
            } else {
                paid = Float.parseFloat(array.get(position).get("PaidAmount"))
                        - Float.parseFloat(array.get(position).get("InitialAmount"));
            }
            if (paid <= 0) {
                holder.status.setText("PAID");
                holder.status.setTextColor(Color.parseColor("#008000"));
            } else {
                holder.status.setText("DUE");
                holder.status.setTextColor(Color.parseColor("#FF0000"));
            }
        } else {
            holder.id_code.setText("ORDER NUMBER");
            holder.date_heading.setText("ORDER PLACED ON");
            holder.label.setText(array.get(position).get("OrderId"));
            holder.value.setText(array.get(position).get("TimeStamp"));
            holder.labName.setText(array.get(position).get("CentreName"));
            holder.test_list.setText(array.get(position).get("TestName"));

            String OrderBillingAmount = null;
            int OrderBilling = 0;
            if (array.get(position).get("OrderBillingAmount") != "null") {
                OrderBilling = (int) Math.round(Double.parseDouble(array.get(position).get("OrderBillingAmount")));
                OrderBillingAmount = String.valueOf(OrderBilling);
            } else {
                OrderBillingAmount = "-";
            }
            try {
                if (array.get(position).get("PromoCodeDiscount") != "null" && array.get(position).get("PromoCodeDiscount") != null) {
                    int promo = (int) Math.round(Double.parseDouble(array.get(position).get("PromoCodeDiscount")));
                    OrderBillingAmount = String.valueOf(OrderBilling - promo);
                } else if (array.get(position).get("DiscountInPercentage") != "null") {
                    double calculateamontprice = OrderBilling * (1 - ((int) Math.round(Double.parseDouble(array.get(position).get("DiscountInPercentage")))) / 100);
                    OrderBillingAmount = String.valueOf((int) Math.round(calculateamontprice));
                }
            } catch (NumberFormatException ne) {
                ne.printStackTrace();
            }
            if (!OrderBillingAmount.equalsIgnoreCase("-")) {
                OrderBillingAmount = "₹ " + OrderBillingAmount;
            }
            holder.amount.setText(OrderBillingAmount);
            if (array.get(position).get("OrderStatus").equalsIgnoreCase("1") && array.get(position).get("SamplePickupstatus").equalsIgnoreCase("null")) {
                holder.status.setText("PENDING");
                holder.status.setTextColor(Color.parseColor("#fe8534"));
            } else if (array.get(position).get("OrderStatus").equalsIgnoreCase("1") && array.get(position).get("SamplePickupstatus").equalsIgnoreCase("false")) {
                holder.status.setText("CANCELLED");
                holder.status.setTextColor(Color.parseColor("#ED2727"));
            } else if (array.get(position).get("OrderStatus").equalsIgnoreCase("0")) {
                holder.status.setText("CANCELLED");
                holder.status.setTextColor(Color.parseColor("#ED2727"));
            } else if (array.get(position).get("OrderStatus").equalsIgnoreCase("1") && array.get(position).get("SamplePickupstatus").equalsIgnoreCase("true")) {
                holder.status.setText("COMPLETED");
                holder.status.setTextColor(Color.parseColor("#65A366"));
            }
        }

        return convertView;
    }

    static class ViewHolder {
        TextView label, value, labName, test_list, amount, status, id_code, date_heading;
    }
}
