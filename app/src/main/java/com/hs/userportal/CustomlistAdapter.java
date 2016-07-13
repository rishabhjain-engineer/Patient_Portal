package com.cloudchowk.patient;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomlistAdapter extends BaseAdapter  {
    private Activity activity;
    private LayoutInflater inflater;
    private List<SortList> SortList;
    static ArrayList<SortList> privatearray;

    public CustomlistAdapter(Activity activity, List<SortList> SortList) {
        this.activity = activity;
        this.SortList = SortList;
        privatearray = new ArrayList<SortList>();
        privatearray.addAll(SortList);
    }

    @Override
    public int getCount() {
        return SortList.size();
    }

    @Override
    public Object getItem(int location) {
        return SortList.get(location);
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
            convertView = inflater.inflate(R.layout.lab_item, null);

        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvName1 = (TextView) convertView.findViewById(R.id.tvName1);
        TextView tvRating = (TextView) convertView.findViewById(R.id.tvStar);
        TextView tvArea = (TextView) convertView.findViewById(R.id.tvArea);
        TextView tvDistance = (TextView) convertView.findViewById(R.id.tvDistance);
        TextView tvParking = (TextView) convertView.findViewById(R.id.tvParking);
        TextView tvSeating = (TextView) convertView.findViewById(R.id.tvSeating);
        TextView tvWashroom = (TextView) convertView.findViewById(R.id.tvWashroom);
        TextView tvDrinking = (TextView) convertView.findViewById(R.id.tvDrinking);
        LinearLayout relHome = (LinearLayout) convertView.findViewById(R.id.relHome);
        LinearLayout relDetails = (LinearLayout) convertView.findViewById(R.id.relDetails);
        TextView tvOpenNow = (TextView) convertView.findViewById(R.id.tvOpenNow);
        TextView tvDiscount = (TextView) convertView.findViewById(R.id.tvDiscount);
        TextView tvChange = (TextView) convertView.findViewById(R.id.tvChange);
        ImageView navigate_lab =(ImageView) convertView.findViewById(R.id.navigate_lab);

        Typeface tf = Typeface.createFromAsset(activity.getAssets(), "icons.ttf");
        tvParking.setTypeface(tf);
        tvDrinking.setTypeface(tf);
        tvWashroom.setTypeface(tf);
        tvSeating.setTypeface(tf);

        try{
            if(SortList.size()!=0){

                final SortList m = SortList.get(position);

		/*
		 * tvParking.setTypeface(tf); tvDrinking.setTypeface(tf);
		 * tvWashroom.setTypeface(tf); tvSeating.setTypeface(tf);
		 */

                tvParking.setText(R.string.parking);
                tvDrinking.setText(R.string.drinking);
                tvWashroom.setText(R.string.washroom);
                tvSeating.setText(R.string.seating);

                if (m.isParking()) {

                    tvParking.setTextColor(activity.getResources().getColor(R.color.grey_unselected));
                    tvParking.setVisibility(View.VISIBLE);
                } else {
                    tvParking.setVisibility(View.GONE);
                }
                if (m.isDrinking()) {

                    tvDrinking.setTextColor(activity.getResources().getColor(R.color.grey_unselected));
                    tvDrinking.setVisibility(View.VISIBLE);
                } else {
                    tvDrinking.setVisibility(View.GONE);
                }
                if (m.isWashroom()) {

                    tvWashroom.setTextColor(activity.getResources().getColor(R.color.grey_unselected));
                    tvWashroom.setVisibility(View.VISIBLE);
                } else {
                    tvWashroom.setVisibility(View.GONE);
                }
                if (m.isSeating()) {

                    tvSeating.setTextColor(activity.getResources().getColor(R.color.grey_unselected));
                    tvSeating.setVisibility(View.VISIBLE);
                } else {
                    tvSeating.setVisibility(View.GONE);
                }

		/*
		 * tvParking.setTextColor(m.isParking() ?
		 * activity.getResources().getColor(R.color.grey) :
		 * Color.parseColor("#ffffff")); tvDrinking.setTextColor(m.isDrinking()
		 * ? activity.getResources().getColor(R.color.grey) :
		 * Color.parseColor("#ffffff")); tvWashroom.setTextColor(m.isWashroom()
		 * ? activity.getResources().getColor(R.color.grey)
		 * :Color.parseColor("#ffffff")); tvSeating.setTextColor(m.isSeating() ?
		 * activity.getResources().getColor(R.color.grey) :
		 * Color.parseColor("#ffffff"));
		 */
                double rating = Double.valueOf(m.getRating());

                tvName.setText(m.getName());
                tvArea.setText(m.getArea());
                tvRating.setText(m.getRating());

                if (m.getDiscount().matches((".*[a-kA-Z0-9]+.*"))&&(int) Math.round(Double.parseDouble(m.getDiscount()))!=0) {

                   // tvDiscount.setText("Up to " + (int) Math.round(Double.parseDouble(m.getDiscount())) + "% OFF");
                   // tvDiscount.setVisibility(View.VISIBLE);

                } else {
                   // tvDiscount.setText("");
                   // tvDiscount.setVisibility(View.GONE);
                }

                final String testAvailability = m.getAvail();

                if (rating <= 1.0) {
                    tvRating.setBackgroundColor(Color.parseColor("#cb202d"));
                } else if (1.0 < rating && rating <= 1.5) {
                    tvRating.setBackgroundColor(Color.parseColor("#de1d0f"));
                } else if (1.5 < rating && rating <= 2.0) {
                    tvRating.setBackgroundColor(Color.parseColor("#ff7800"));
                } else if (2.0 < rating && rating <= 2.5) {
                    tvRating.setBackgroundColor(Color.parseColor("#ffba00"));
                } else if (2.5 < rating && rating <= 3.0) {
                    tvRating.setBackgroundColor(Color.parseColor("#edd614"));
                } else if (3.0 < rating && rating <= 3.5) {
                    tvRating.setBackgroundColor(Color.parseColor("#9acd32"));
                } else if (3.5 < rating && rating <= 4.0) {
                    tvRating.setBackgroundColor(Color.parseColor("#5ba829"));
                } else if (4.0 < rating && rating <= 4.5) {
                    tvRating.setBackgroundColor(Color.parseColor("#3f7e00"));
                } else if (4.5 < rating && rating < 5.0) {
                    tvRating.setBackgroundColor(Color.parseColor("#3f7e00"));
                } else if (rating == 5.0) {
                    tvRating.setBackgroundColor(Color.parseColor("#305d02"));
                }



                relDetails.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (customListner != null) {
                            customListner.onDetailsButtonClickListner(position, m.getCenterID());

                        }
                    }
                });

                relHome.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (customListner != null) {
                            customListner.onButtonClickListner(m.getAvail(), m.getCenterID(), position);
                        }
                    }
                });
                navigate_lab.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (customListner != null) {
                            customListner.onNavButtonClickListner(position, m.getCenterID());
                        }
                    }
                });


                relHome.setVisibility(m.isHomeColl() ? View.VISIBLE : View.GONE);

                tvOpenNow.setText(m.isOpenNow() ? "Open Now" : "Closed");
                tvOpenNow.setTextColor(m.isOpenNow() ? Color.parseColor("#4CAF50") : Color.parseColor("#FF5252"));

                if(m.getTieUpWithLab().equalsIgnoreCase("1")){
                    tvName1.setVisibility(View.VISIBLE);
                    try {
                        String [] split=m.getCompeteAddress().split(",");
                        tvName1.setText("Sample pickup across " + split[split.length - 2]);
                    }catch(NullPointerException ex){
                        ex.printStackTrace();
                    }

                }else{
                    tvName1.setVisibility(View.GONE);
                }

                double roundOff = Math.round(m.getCo() * 100.0) / 100.0;
                tvDistance.setText(roundOff + " km");

                if (testAvailability.equals("1")) {

                    // Show Check Prices
                    tvChange.setText("CHECK PRICES");
                    relHome.setVisibility(View.GONE);

                } else if (testAvailability.equals("2")) {
                    // Show Generate Coupon
                    relHome.setVisibility(View.GONE);
                   // tvChange.setText("GET COUPON");

                } else if (testAvailability.equals("0")) {

                    // Hide both
                    relHome.setVisibility(View.GONE);
                  /*  relHome.setVisibility(m.isHomeColl() ? View.VISIBLE : View.GONE);
                    tvChange.setText("SAMPLE PICKUP");*/
                }}
        }catch(ArrayIndexOutOfBoundsException ex){
            ex.printStackTrace();
        }
        return convertView;
    }

    // Home Collection Button
    customButtonListener customListner;

    public interface customButtonListener {
        public void onButtonClickListner(String avalbl, String value, int pos);

        public void onDetailsButtonClickListner(int position, String value);

        public void onNavButtonClickListner(int position, String value);
    }

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    // Get Details button
    public void setCustomDetailsButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    //Navigation Button
    public void setNavButtonListner(customButtonListener listner){
        this.customListner = listner;
    }

    public void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        this.SortList.clear();
        if (charText.length() == 0) {
            this.SortList.addAll(privatearray);
        } else {
            for (SortList c : privatearray) {
                if (c.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    this.SortList.add(c);
                }
            }
        }

        notifyDataSetChanged();

    }
}
