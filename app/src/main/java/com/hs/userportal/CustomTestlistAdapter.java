package com.hs.userportal;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;



import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomTestlistAdapter extends BaseAdapter {
	private Activity activity;
	private LayoutInflater inflater;
	private List<TestModel> TestList;
	private static List<TestModel> privatearray;
	public static boolean searchtxt=false;
	private int recomd_show = 0;

	public CustomTestlistAdapter(Activity activity, List<TestModel> TestList) {
		this.activity = activity;
		this.TestList = TestList;
		privatearray = new ArrayList<TestModel>();
		privatearray.addAll(TestList);

	}

	@Override
	public int getCount() {
		return TestList.size();
	}

	@Override
	public Object getItem(int location) {
		return TestList.get(location);
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
			convertView = inflater.inflate(R.layout.test_list_item, null);
		try {
			if (TestList.size() != 0) {
				TestModel m = TestList.get(position);

				TextView tvName = (TextView) convertView.findViewById(R.id.tvName1);
				TextView tvrecommend = (TextView) convertView.findViewById(R.id.tvName);

				TextView tie_up = (TextView) convertView.findViewById(R.id.tie_up);
				TextView tvprice = (TextView) convertView.findViewById(R.id.tvActualPrice);

				TextView finalprice = (TextView) convertView.findViewById(R.id.tvYourPrice);
				RelativeLayout recomend_lay = (RelativeLayout) convertView.findViewById(R.id.recomend_lay);

				TextView tvdisc = (TextView) convertView.findViewById(R.id.tvDiscountPer);// discount
																							// precentage
				TextView discount_amnt = (TextView) convertView.findViewById(R.id.discount_amnt);// discount
																									// amount
																									// show

				TextView tvArea = (TextView) convertView.findViewById(R.id.tvArea);
				TextView tvRating = (TextView) convertView.findViewById(R.id.tvStar);
				TextView tvDistance = (TextView) convertView.findViewById(R.id.tvDistance);
				TextView tvTestCount = (TextView) convertView.findViewById(R.id.tvTestCount);
				Button bookTest = (Button) convertView.findViewById(R.id.bBook);

				bookTest.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (customTestListner != null) {
							customTestListner.onTestButtonClickListner(position, "");
						}
					}
				});
				String discount = m.getDiscount();// ( Upto 48.57% ) -
				// â‚¹190.0
				String disrupee = m.getDiscountRuppes();
				String finalpriceval = m.getFinalPrice();
				int finalpriceval1 = (int) Math.round(Double.parseDouble(finalpriceval));
				int discountr = (int) Math.round(Double.parseDouble(disrupee));
				int price = (int) Math.round(Double.parseDouble(m.getPrice()));
				String TieUpWithLab = m.getTieUpWithLab();

				tvName.setText(m.getLabName());
				tvprice.setText("₹   " + price);
				tvdisc.setText("(Up to " + m.getDiscount() + " OFF)");
				// tvdisc.setText(Html.fromHtml("<font color='#008000'> (upto
				// "+m.getDiscount()+"% OFF)</font>"));
				discount_amnt.setText("₹   " + discountr);
				finalprice.setText("₹   " + finalpriceval1);
				tvArea.setText(m.getArea());
				tvTestCount.setText("Offers " + m.getCentreTest() + "/" + m.getTotalTest() + " test(s)");

				double roundOff = 0.0;
				try {
					roundOff = m.getDistance().matches((".*[a-kA-Z0-9]+.*"))
							? Math.round(Float.parseFloat(m.getDistance()) * 100.0) / 100.0 : 0.0;
				} catch (NullPointerException ne) {
					roundOff = 0.0;
				}

				double rating = Double.valueOf(m.getRating());
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
				tvDistance.setText(roundOff + "Km");

				if ((position == 0 || position == 1)&&searchtxt==false) {

					recomend_lay.setVisibility(View.VISIBLE);
					tvrecommend.setVisibility(View.VISIBLE);
				//	tvrecommend.setText("Sample pickup across "+split[split.length-2]);
				} else {
					tvrecommend.setVisibility(View.INVISIBLE);
				}

				tvRating.setText(m.getRating());
				
				if(searchtxt == true){
					recomend_lay.setVisibility(View.GONE);
				}

				if (TieUpWithLab.equalsIgnoreCase("1")) {
					recomend_lay.setVisibility(View.VISIBLE);
					tie_up.setVisibility(View.VISIBLE);
					try {
						String[] split = m.getCompleteAddress().split(",");
						tie_up.setText("Sample pickup across " + split[split.length - 2]);
					}catch (NullPointerException ex){
						ex.printStackTrace();
					}
				} else if ((TieUpWithLab.equalsIgnoreCase("0"))) {
					tie_up.setVisibility(View.GONE);
					if(position!=0 && position!=1){
						recomend_lay.setVisibility(View.GONE);
					}
				} 
			}
		} catch (ArrayIndexOutOfBoundsException ae) {
			ae.printStackTrace();
		}

		return convertView;
	}

	customTestButtonListener customTestListner;

	public interface customTestButtonListener {
		public void onTestButtonClickListner(int position, String value);
	}

	public void setCustomTestButtonListner(customTestButtonListener listener) {
		this.customTestListner = listener;
	}

	public void filterInstantBook(String charText) {
		// TODO Auto-generated method stub
		charText = charText.toLowerCase(Locale.getDefault());
		TestList.clear();
		if (charText.length() == 0) {
			TestList.addAll(privatearray);
			searchtxt=false;
		} else {
			for (TestModel c : privatearray) {
				if (c.getLabName().toLowerCase(Locale.getDefault()).contains(charText)) {
					TestList.add(c);
				}
			}
			searchtxt=true;
		}
		notifyDataSetChanged();
	}

}
