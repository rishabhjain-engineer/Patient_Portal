package com.hs.userportal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import config.StaticHolder;

public class CouponActivity extends ActionBarActivity {

	private JsonObjectRequest jr;
	private RequestQueue queue;
	private String centerId, patientId;
	private String closeTimeString = "";
	private JSONArray centreArray;
	private JSONObject sendData;
	private TextView tvAddress, tvTime, tvOpen, tvContact, tvLabName, tvVoucherId, tvValidTill, tvPatName, tvPatEmail, tvDiscount, tvRating;
	private TextView tvMon, tvTue, tvWed, tvThu, tvFri, tvSat, tvSun, tvTerms, tvTotalPrice, tvTotalPayable;
	private String patName, patEmail, patVoucher, patDay, maxdiscount, rating,contactNumber;
	private Button bSMS, bEmail;
	private Calendar cal;
	private int checksmswt_email=0;
	private SimpleDateFormat dateFormat, timeFormat;
	private LinearLayout bottom, testPricesDynamic, layTotal;
	private ProgressDialog progressDialog;
	private JSONArray testDetails;
	private boolean isTestNull = true;
	private JSONArray priceArray;
	private LinearLayout discountlin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_coupon);
		layTotal = (LinearLayout) findViewById(R.id.layTotal);
		testPricesDynamic = (LinearLayout) findViewById(R.id.coupon_dynamic);
		bottom = (LinearLayout) findViewById(R.id.bottom);
		tvTerms = (TextView) findViewById(R.id.tvTerms);
		tvTerms.setClickable(true);
		tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
		tvAddress = (TextView) findViewById(R.id.tvAddress);
		tvTime = (TextView) findViewById(R.id.tvTimings);
		tvOpen = (TextView) findViewById(R.id.tvOpen);
		tvContact = (TextView) findViewById(R.id.tvContact);
		tvLabName = (TextView) findViewById(R.id.tvName);
		tvVoucherId = (TextView) findViewById(R.id.tvVoucherId);
		tvValidTill = (TextView) findViewById(R.id.tvValidTill);
		tvPatName = (TextView) findViewById(R.id.tvPatName);
		tvPatEmail = (TextView) findViewById(R.id.tvEmail);
		tvDiscount = (TextView) findViewById(R.id.tvDiscount);
		tvRating = (TextView) findViewById(R.id.tvRating);
		tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);
		tvTotalPayable = (TextView) findViewById(R.id.tvTotalPayable);
		 discountlin=(LinearLayout)findViewById(R.id.promoDiscountlinear);
		tvMon = (TextView) findViewById(R.id.tvMonday);
		tvTue = (TextView) findViewById(R.id.tvTuesday);
		tvWed = (TextView) findViewById(R.id.tvWednesday);
		tvThu = (TextView) findViewById(R.id.tvThursday);
		tvFri = (TextView) findViewById(R.id.tvFriday);
		tvSat = (TextView) findViewById(R.id.tvSaturday);
		tvSun = (TextView) findViewById(R.id.tvSunday);

		bSMS = (Button) findViewById(R.id.bCouponSms);
		bEmail = (Button) findViewById(R.id.bCouponEmail);
		String contact_number;
		Intent z = getIntent();
		centerId = z.getStringExtra("CenterId");
		patientId = z.getStringExtra("PatientId");
		maxdiscount = z.getStringExtra("MaxDiscount");
		rating = z.getStringExtra("Rating");

		if (z.hasExtra("TestPrices")) {
			try {
				priceArray = new JSONArray(z.getStringExtra("TestPrices"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (!z.getStringExtra("TestDetails").equals("Send Null")) {
			try {
				testDetails = new JSONArray(z.getStringExtra("TestDetails"));
				isTestNull = false;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			isTestNull = true;
		}

		queue = Volley.newRequestQueue(this);

		ActionBar action = getSupportActionBar();
		action.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1DBBE3")));
		action.setIcon(new ColorDrawable(Color.parseColor("#1DBBE3")));
		action.setDisplayHomeAsUpEnabled(true);

		cal = Calendar.getInstance();
		timeFormat = new SimpleDateFormat("HH:mm:ss");
		dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		tvRating.setText("" + rating);
		if (!rating.equals("")) {
			double ratingDouble = Double.parseDouble(rating);
			if (ratingDouble <= 1.0) {
				tvRating.setBackgroundColor(Color.parseColor("#cb202d"));
			} else if (1.0 < ratingDouble && ratingDouble <= 1.5) {
				tvRating.setBackgroundColor(Color.parseColor("#de1d0f"));
			} else if (1.5 < ratingDouble && ratingDouble <= 2.0) {
				tvRating.setBackgroundColor(Color.parseColor("#ff7800"));
			} else if (2.0 < ratingDouble && ratingDouble <= 2.5) {
				tvRating.setBackgroundColor(Color.parseColor("#ffba00"));
			} else if (2.5 < ratingDouble && ratingDouble <= 3.0) {
				tvRating.setBackgroundColor(Color.parseColor("#edd614"));
			} else if (3.0 < ratingDouble && ratingDouble <= 3.5) {
				tvRating.setBackgroundColor(Color.parseColor("#9acd32"));
			} else if (3.5 < ratingDouble && ratingDouble <= 4.0) {
				tvRating.setBackgroundColor(Color.parseColor("#5ba829"));
			} else if (4.0 < ratingDouble && ratingDouble <= 4.5) {
				tvRating.setBackgroundColor(Color.parseColor("#3f7e00"));
			} else if (4.5 < ratingDouble && ratingDouble < 5.0) {
				tvRating.setBackgroundColor(Color.parseColor("#3f7e00"));
			} else if (ratingDouble == 5.0) {
				tvRating.setBackgroundColor(Color.parseColor("#305d02"));
			}
		}

		float totalPrice = 0, totalPayable = 0;
		if(priceArray!=null){
			for (int i = 0; i < priceArray.length(); i++) {
				JSONArray pricar=priceArray;
				try {
					LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					final View addView = layoutInflater.inflate(R.layout.dynamic_coupon_row, null);
					final TextView tvTestName = (TextView) addView.findViewById(R.id.tvTestName);
					tvTestName.setText(priceArray.getJSONObject(i).getString("TestName"));
					final TextView tvTestPrice = (TextView) addView.findViewById(R.id.tvTestPrice);
					tvTestPrice.setText("₹ " + priceArray.getJSONObject(i).getString("Price"));
					final TextView tvPayable = (TextView) addView.findViewById(R.id.tvPayable);


					float price = 1 - Float.parseFloat(priceArray.getJSONObject(i).getString("Discount"));
					float finalPrice = Float.parseFloat(priceArray.getJSONObject(i).getString("Price")) * price;
					int finalPricewitoutfloat = (int)Math.round(finalPrice);
					tvPayable.setText("₹ " + finalPricewitoutfloat);



					float testPrice = Float.parseFloat(priceArray.getJSONObject(i).getString("Price"));
					totalPrice += testPrice;
					totalPayable += finalPrice;

					testPricesDynamic.addView(addView, 0);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			/*if(getIntent().getStringExtra("promo_DiscountAmnt")!=null) {
				LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				final View addView = layoutInflater.inflate(R.layout.dynamic_coupon_row, null);
				final TextView tvTestName = (TextView) addView.findViewById(R.id.tvTestName);
				tvTestName.setText("Promo Discount");
				final TextView tvTestPrice = (TextView) addView.findViewById(R.id.tvTestPrice);
				tvTestPrice.setText("");
				final TextView tvPayable = (TextView) addView.findViewById(R.id.tvPayable);

					*//*	float price = 1 - Float.parseFloat(priceArray.getJSONObject(i).getString("Discount"));
						float finalPrice = Float.parseFloat(priceArray.getJSONObject(i).getString("Price")) * price;
						int finalPricewitoutfloat = (int) Math.round(finalPrice);*//*
				tvPayable.setText("₹ " + getIntent().getStringExtra("promo_DiscountAmnt"));

				testPricesDynamic.addView(addView, 0);

				//float testPrice = Float.parseFloat(priceArray.getJSONObject(i).getString("Price"));
				//totalPrice += testPrice;
				totalPayable -= Float.parseFloat(getIntent().getStringExtra("promo_DiscountAmnt"));
			}*/
		}
		if(getIntent().getStringExtra("promo_DiscountAmnt")!=null){

			discountlin.setVisibility(View.VISIBLE);
			 TextView discountamnt = (TextView)findViewById(R.id.discount_amnt);
			discountamnt.setText("₹ " + getIntent().getStringExtra("promo_DiscountAmnt"));
			totalPayable -= Float.parseFloat(getIntent().getStringExtra("promo_DiscountAmnt"));

		}else{
			discountlin.setVisibility(View.GONE);
		}
		if (testPricesDynamic.getChildCount() > 0) {
			layTotal.setVisibility(View.VISIBLE);
			testPricesDynamic.setVisibility(View.VISIBLE);
			tvTotalPrice.setText("₹ " +(int)Math.round(totalPrice));
			tvTotalPayable.setText("₹ " +(int)Math.round(totalPayable) );
		} else {

			layTotal.setVisibility(View.GONE);
			testPricesDynamic.setVisibility(View.GONE);
		}

		getCentreData();

		bSMS.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				sendSMS();

			}
		});

		bEmail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				JSONArray ds=centreArray;
				sendEmail();

			}
		});
		String textori = "•This coupon is for one time use only."
			    + "<br>•This coupon can neither be combined with any other existing offer nor redeemable with cash."
			    + "<br>•At least 12 hours of fasting required for Lipid Profile, ESR Hormones etc. Please contact lab for more details."
			    + "<br>•For any enquiry regarding test contact lab at the phone number(s) mentioned above."
			    + "<br>•Any other charges like doctor fees, assistant charge, etc. if applicable, will be payable to the lab."
			    + "<br>•Please show your doctors prescription to lab for test verification purposes."
			    + "<br>•Please confirm the appointment before going to your lab."
			    + "<br>•If you have availed home collection facility or any other such service, we recommend contacting your lab to confirm the timings for the same."
			    + "<br>•You can collect your test reports from the lab or upon request they can email it to you as well. Alternatively, if the lab is cloudchowk enabled then the test report shall be visible in your personal account once the lab publishes it."
			    + "<br>•If you are booking test through any means then all the terms and conditions listed on   "
			    + "<a href='http://www.zureka.in'>www.zureka.in</a>" + " would apply."
			    + "<br>•In case you feel any difficulty, feel free to reach us anytime at "
			    + "<a href='mailto://support@zureka.in'>support@zureka.in</a>"
			    + "  or on the phone numbers listed on our website.";
		tvTerms.setText(Html.fromHtml(textori));

	}

	public void sendSMS() {

		progressDialog = new ProgressDialog(CouponActivity.this);
		progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
		JSONArray ds=centreArray;
		String url;
		if (!isTestNull) {

			System.out.println("SendCouponWithTestViaSMS");

			sendData = new JSONObject();



			try { ///
				sendData.put("area", centreArray.getJSONObject(0).getString("AreaAddress"));
				sendData.put("patientId", patientId);
				sendData.put("patientName", patName);
				sendData.put("patientEmailId", patEmail);
				sendData.put("labAddress", tvAddress.getText().toString());
				sendData.put("expiryDateTime", tvValidTill.getText().toString());
				sendData.put("voucherNo", tvVoucherId.getText().toString());
				sendData.put("labname", tvLabName.getText().toString());
				sendData.put("todayTime", timeFormat.format(cal.getTime()));
				sendData.put("todayDate", dateFormat.format(cal.getTime()));
				if(centreArray.getJSONObject(0).getString("ContactNoMobile")!=null){
				sendData.put("labContactNo", centreArray.getJSONObject(0).getString("ContactNoLandline")+","+centreArray.getJSONObject(0).getString("ContactNoMobile"));
				}else{
					sendData.put("labContactNo", centreArray.getJSONObject(0).getString("ContactNoLandline"));
				}
				sendData.put("labOpendays", tvTime.getText().toString());
				sendData.put("labClosedDays", closeTimeString);
				sendData.put("labMobileNo", centreArray.getJSONObject(0).getString("ContactNoMobile"));
				//adding new-------------------------------------------------------------------------------------------
				sendData.put("isTwentyfour", centreArray.getJSONObject(0).getString("TwentyFour"));
				//---------------------------------------------------------------------------------------------------
				//sendData.put("area", centreArray.getJSONObject(0).getString("AreaName"));
				sendData.put("testdetails", testDetails);
                if(getIntent().getStringExtra("promo_DiscountAmnt")!=null) {

                    sendData.put("promoCodeDiscount", getIntent().getStringExtra("promo_DiscountAmnt"));
                }else{

                    sendData.put("promoCodeDiscount",0);
                }

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(sendData);

			/*url = "https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/SendCouponWithTestViaSMS";*/
			StaticHolder sttc_holdr=new StaticHolder(CouponActivity.this,StaticHolder.Services_static.SendCouponWithTestViaSMSNew);
			url=sttc_holdr.request_Url();
		//	url = StaticHolder.COUPON_TESTSMS;

		} else {

			System.out.println("SendCouponWithOutTestViaSMS");

			sendData = new JSONObject();//maxdiscount
			try {
				int discountr = (int) Math.round(Double.parseDouble(maxdiscount));
				sendData.put("area", centreArray.getJSONObject(0).getString("AreaAddress"));
				sendData.put("patientId", patientId);
				sendData.put("maxdiscount", String.valueOf(discountr));
				sendData.put("patientName", patName);
				sendData.put("patientEmailId", patEmail);
				sendData.put("labAddress", tvAddress.getText().toString());
				sendData.put("expiryDateTime", tvValidTill.getText().toString());
				sendData.put("voucherNo", tvVoucherId.getText().toString());
				sendData.put("labname", tvLabName.getText().toString());
				sendData.put("todayTime", timeFormat.format(cal.getTime()));
				sendData.put("todayDate", dateFormat.format(cal.getTime()));
				if(centreArray.getJSONObject(0).getString("ContactNoMobile")!=null){
					sendData.put("labContactNo", centreArray.getJSONObject(0).getString("ContactNoLandline")+","+centreArray.getJSONObject(0).getString("ContactNoMobile"));
					}else{
						sendData.put("labContactNo", centreArray.getJSONObject(0).getString("ContactNoLandline"));
					}
				sendData.put("labOpendays", tvTime.getText().toString());
				sendData.put("labClosedDays", closeTimeString);
				//adding new-------------------------------------------------------------------------------------------
				sendData.put("isTwentyfour", centreArray.getJSONObject(0).getString("TwentyFour"));
				//---------------------------------------------------------------------------------------------------
				sendData.put("labMobileNo", centreArray.getJSONObject(0).getString("ContactNoMobile"));
				
				/* <patientId>guid</patientId>
			      <patientName>string</patientName>
			      <patientEmailId>string</patientEmailId>
			      <labAddress>string</labAddress>
			      <expiryDateTime>string</expiryDateTime>
			      <voucherNo>string</voucherNo>
			      <labname>string</labname>
			      <todayTime>string</todayTime>
			      <todayDate>string</todayDate>
			      <labContactNo>string</labContactNo>
			      <labOpendays>string</labOpendays>
			      <labClosedDays>string</labClosedDays>
			      <labMobileNo>string</labMobileNo>
			      <area>string</area>
			      <maxdiscount>string</maxdiscount>
*/
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(sendData);

		/*	url = "https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/SendCouponWithOutTestViaSMS";*/
			StaticHolder sttc_holdr=new StaticHolder(CouponActivity.this,StaticHolder.Services_static.SendCouponWithOutTestViaSMS);
			url=sttc_holdr.request_Url();
           // url = StaticHolder.COUPON_SMS;
		}

		jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {


				//System.out.println(response);

				try {
					//Toast.makeText(getApplicationContext(), response.getString("d"), Toast.LENGTH_SHORT).show();
					Log.i("tag2", response.getString("d"));
					checksmswt_email=1;
					sendEmail();


					//Toast.makeText(getApplicationContext(), response.getString("d"), Toast.LENGTH_SHORT).show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				checksmswt_email=1;
				sendEmail();
				System.out.println(error);
				//Toast.makeText(getApplicationContext(), "SendCouponWithOutTestViaSMS Error!", Toast.LENGTH_SHORT).show();

			}
		}) {

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("Cookie", Services.hoja);
				return headers;
			}
		};

		int socketTimeout1 = 30000;
		RetryPolicy policy1 = new DefaultRetryPolicy(socketTimeout1, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		jr.setRetryPolicy(policy1);
		queue.add(jr);

	}

	public void sendEmail() {
         if(checksmswt_email!=1){
		 progressDialog = new ProgressDialog(CouponActivity.this);
		 progressDialog.setMessage("Loading....");
         progressDialog.setCancelable(false);
		 progressDialog.setCanceledOnTouchOutside(false);
		 progressDialog.show();
         }else{
        	 checksmswt_email=0; 
         }
		JSONArray ds=centreArray;
		String url = "";
		if (!isTestNull) {

			System.out.println("SendCouponWithTestViaEmail");

			sendData = new JSONObject();
			try {

               /* <patientId>guid</patientId>
                <patientName>string</patientName>
                <patientEmailId>string</patientEmailId>
                <labAddress>string</labAddress>
                <expiryDateTime>string</expiryDateTime>
                <voucherNo>string</voucherNo>
                <labname>string</labname>
                <todayTime>string</todayTime>
                <todayDate>string</todayDate>
                <labContactNo>string</labContactNo>
                <labOpendays>string</labOpendays>
                <labClosedDays>string</labClosedDays>
                <mondaytime>string</mondaytime>
                <tuesdaytime>string</tuesdaytime>
                <wednesdaytime>string</wednesdaytime>
                <thursdaytime>string</thursdaytime>
                <fridaytime>string</fridaytime>
                <saturdaytime>string</saturdaytime>
                <sundaytime>string</sundaytime>
                <maxdiscount>string</maxdiscount>
                <labEmailaddress>string</labEmailaddress>
                <testdetails>
                <TestDetails>
                <TestId>guid</TestId>
                <TestName>string</TestName>
                <TestPrice>string</TestPrice>
                <Discount>string</Discount>
                <LabId>string</LabId>
                <PayableAmount>string</PayableAmount>
                </TestDetails>
                <TestDetails>
                <TestId>guid</TestId>
                <TestName>string</TestName>
                <TestPrice>string</TestPrice>
                <Discount>string</Discount>
                <LabId>string</LabId>
                <PayableAmount>string</PayableAmount>
                </TestDetails>
                </testdetails>
                <isTwentyfour>boolean</isTwentyfour>
                <promoCodeDiscount>decimal</promoCodeDiscount>*/


				int discountr = (int) Math.round(Double.parseDouble(maxdiscount));
				sendData.put("patientId", patientId);
				sendData.put("patientName", patName);
				sendData.put("patientEmailId", patEmail);
				sendData.put("labAddress", tvAddress.getText().toString());
				sendData.put("expiryDateTime", tvValidTill.getText().toString());
				sendData.put("voucherNo", tvVoucherId.getText().toString());
				sendData.put("labname", tvLabName.getText().toString());
				sendData.put("todayTime", timeFormat.format(cal.getTime()));
				sendData.put("todayDate", dateFormat.format(cal.getTime()));
				if(centreArray.getJSONObject(0).getString("ContactNoMobile")!=null){
					sendData.put("labContactNo", centreArray.getJSONObject(0).getString("ContactNoLandline")+","+centreArray.getJSONObject(0).getString("ContactNoMobile"));
					}else{
						sendData.put("labContactNo", centreArray.getJSONObject(0).getString("ContactNoLandline"));
					}
				sendData.put("labOpendays", tvTime.getText().toString());
				sendData.put("labClosedDays", closeTimeString);
				
				sendData.put("mondaytime", centreArray.getJSONObject(0).getString("MondayTime"));
				sendData.put("tuesdaytime", centreArray.getJSONObject(0).getString("TuesdayTime"));
				sendData.put("wednesdaytime", centreArray.getJSONObject(0).getString("WednesdayTime"));
				sendData.put("thursdaytime", centreArray.getJSONObject(0).getString("ThursdayTime"));
				sendData.put("fridaytime", centreArray.getJSONObject(0).getString("FridayTime"));
				sendData.put("saturdaytime", centreArray.getJSONObject(0).getString("SaturdayTime"));
				sendData.put("sundaytime", centreArray.getJSONObject(0).getString("SundayTime"));
				sendData.put("maxdiscount", String.valueOf(discountr));
				sendData.put("labEmailaddress", centreArray.getJSONObject(0).getString("EmailAddress"));
				
				sendData.put("testdetails", testDetails);
				sendData.put("isTwentyfour", centreArray.getJSONObject(0).getString("TwentyFour"));

                if(getIntent().getStringExtra("promo_DiscountAmnt")!=null) {

                    sendData.put("promoCodeDiscount", getIntent().getStringExtra("promo_DiscountAmnt"));
                }else{

                    sendData.put("promoCodeDiscount",0);
                }
			

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(sendData);

			//url = "https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/SendCouponWithTestViaEmail";
			/*url = "https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/SendCouponWithTestViaEmail";*/
			StaticHolder sttc_holdr=new StaticHolder(CouponActivity.this,StaticHolder.Services_static.SendCouponWithTestViaEmailNew);
			url=sttc_holdr.request_Url();
          //  url = StaticHolder.COUPON_TESTMAIL;
		} else {

			System.out.println("SendCouponWithOutTestViaEmail");

			sendData = new JSONObject();
			try {
				int discountr = (int) Math.round(Double.parseDouble(maxdiscount));
				sendData.put("patientId", patientId);
				sendData.put("patientName", patName);
				sendData.put("patientEmailId", patEmail);
				sendData.put("labAddress", tvAddress.getText().toString());
				sendData.put("expiryDateTime", tvValidTill.getText().toString());
				sendData.put("voucherNo", tvVoucherId.getText().toString());
				sendData.put("labname", tvLabName.getText().toString());
				sendData.put("todayTime", timeFormat.format(cal.getTime()));
				sendData.put("todayDate", dateFormat.format(cal.getTime()));
				if(centreArray.getJSONObject(0).getString("ContactNoMobile")!=null){
					sendData.put("labContactNo", centreArray.getJSONObject(0).getString("ContactNoLandline")+","+centreArray.getJSONObject(0).getString("ContactNoMobile"));
					}else{
						sendData.put("labContactNo", centreArray.getJSONObject(0).getString("ContactNoLandline"));
					}
				sendData.put("labOpendays", tvTime.getText().toString());
				sendData.put("labClosedDays", closeTimeString);
				sendData.put("discount", String.valueOf(discountr));
				sendData.put("mondaytime", centreArray.getJSONObject(0).getString("MondayTime"));
				sendData.put("tuesdaytime", centreArray.getJSONObject(0).getString("TuesdayTime"));
				sendData.put("wednesdaytime", centreArray.getJSONObject(0).getString("WednesdayTime"));
				sendData.put("thursdaytime", centreArray.getJSONObject(0).getString("ThursdayTime"));
				sendData.put("fridaytime", centreArray.getJSONObject(0).getString("FridayTime"));
				sendData.put("saturdaytime", centreArray.getJSONObject(0).getString("SaturdayTime"));
				sendData.put("sundaytime", centreArray.getJSONObject(0).getString("SundayTime"));
				sendData.put("labEmailaddress", centreArray.getJSONObject(0).getString("EmailAddress"));
				sendData.put("isTwentyfour", centreArray.getJSONObject(0).getString("TwentyFour"));
				
				/* <patientId>guid</patientId>
			      <patientName>string</patientName>
			      <patientEmailId>string</patientEmailId>
			      <labAddress>string</labAddress>
			      <expiryDateTime>string</expiryDateTime>
			      <voucherNo>string</voucherNo>
			      <labname>string</labname>
			      <todayTime>string</todayTime>
			      <todayDate>string</todayDate>
			      <labContactNo>string</labContactNo>
			      <labOpendays>string</labOpendays>
			      <labClosedDays>string</labClosedDays>
			      <mondaytime>string</mondaytime>
			      <tuesdaytime>string</tuesdaytime>
			      <wednesdaytime>string</wednesdaytime>
			      <thursdaytime>string</thursdaytime>
			      <fridaytime>string</fridaytime>
			      <saturdaytime>string</saturdaytime>
			      <sundaytime>string</sundaytime>
			      <labEmailaddress>string</labEmailaddress>
			      <discount>string</discount>
			      <isTwentyfour>boolean</isTwentyfour>*/

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(sendData);
			JSONObject dsf=sendData;
			//http://192.168.1.56:82/WebServices/LabService.asmx
			/*url = "https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/SendCouponWithOutTestViaEmail";*/
			StaticHolder sttc_holdr=new StaticHolder(CouponActivity.this,StaticHolder.Services_static.SendCouponWithOutTestViaEmail);
			url=sttc_holdr.request_Url();
         //   url = StaticHolder.COUPON_EMAIL;
		}
		
		jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {

				progressDialog.dismiss();
				System.out.println(response);

				try {
					
					Log.i("tag1", response.getString("d"));
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CouponActivity.this);


					alertDialogBuilder
					.setMessage(response.getString("d"))
					.setCancelable(true)
					.setPositiveButton("OK",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							// if this button is clicked, close
							// current activity
							dialog.cancel();
						}
					});

					AlertDialog alertDialog = alertDialogBuilder.create();

					// show it
					alertDialog.show();

					//Toast.makeText(getApplicationContext(), response.getString("d"), Toast.LENGTH_SHORT).show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

				progressDialog.dismiss();
				System.out.println(error);
				//Toast.makeText(getApplicationContext(), "ViaEmail Error!", Toast.LENGTH_SHORT).show();
			}
		}) {

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("Cookie", Services.hoja);
				return headers;
			}
		};

		int socketTimeout1 = 30000;
		RetryPolicy policy1 = new DefaultRetryPolicy(socketTimeout1, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		jr.setRetryPolicy(policy1);
		queue.add(jr);

	}

	public void getCentreData() {

		progressDialog = new ProgressDialog(CouponActivity.this);
		progressDialog.setMessage("Loading....");
        progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
		

		sendData = new JSONObject();
		try {
			sendData.put("CentreId", centerId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("sendData: " + sendData);
		/*final String url = "https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/GetCentreData";*/
		StaticHolder sttc_holdr=new StaticHolder(CouponActivity.this,StaticHolder.Services_static.GetCentreData);
		String url1=sttc_holdr.request_Url();
		jr = new JsonObjectRequest(Request.Method.POST, url1, sendData, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {

				bSMS.setEnabled(true);
				bEmail.setEnabled(true);
				generateCoupon();

				// System.out.println(url);
				System.out.println(response);
				String timestring = "";

				try {
					String imageData = response.getString("d");
					JSONObject cut = new JSONObject(imageData);
					centreArray = cut.getJSONArray("Table");
                  //  System.out.printf("centerarray=", centreArray.toString());
					tvLabName.setText(centreArray.getJSONObject(0).getString("CentreName"));
					tvAddress.setText(centreArray.getJSONObject(0).getString("CompleteAddress"));
					//---------------------------------------------------------------------------------------------------------------------------------------------------

					tvMon.setText(centreArray.getJSONObject(0).getString("MondayTime").toString().equals("null")
							? "Not Available" : centreArray.getJSONObject(0).getString("MondayTime"));
					tvTue.setText(centreArray.getJSONObject(0).getString("TuesdayTime").toString().equals("null")
							? "Not Available" : centreArray.getJSONObject(0).getString("TuesdayTime"));
					tvWed.setText(centreArray.getJSONObject(0).getString("WednesdayTime").toString().equals("null")
							? "Not Available" : centreArray.getJSONObject(0).getString("WednesdayTime"));
					tvThu.setText(centreArray.getJSONObject(0).getString("ThursdayTime").toString().equals("null")
							? "Not Available" : centreArray.getJSONObject(0).getString("ThursdayTime"));
					tvFri.setText(centreArray.getJSONObject(0).getString("FridayTime").toString().equals("null")
							? "Not Available" : centreArray.getJSONObject(0).getString("FridayTime"));
					tvSat.setText(centreArray.getJSONObject(0).getString("SaturdayTime").toString().equals("null")
							? "Not Available" : centreArray.getJSONObject(0).getString("SaturdayTime"));
					tvSun.setText(centreArray.getJSONObject(0).getString("SundayTime").toString().equals("null")
							? "Not Available" : centreArray.getJSONObject(0).getString("SundayTime"));

					if (!centreArray.getJSONObject(0).getString("Discount").toString().equals("null")
							|| !centreArray.getJSONObject(0).getString("Discount").toString().equals("")) {
						tvDiscount.setText("THIS COUPON ENTITLES YOU TO A MAXIMUM OF "
								+ centreArray.getJSONObject(0).getString("Discount")
								+ "% DISCOUNT. PLEASE PRESENT THIS TO THE LAB (EITHER PRINT OUT OR SMS).");
					}

					if (centreArray.getJSONObject(0).getString("ContactNoMobile").equals("")) {
						tvContact.setText("Contact Number-");
					} else {
						tvContact.setText(centreArray.getJSONObject(0).getString("ContactNoMobile"));
					}

					if (centreArray.getJSONObject(0).getString("MondayTime").contains(":")) {
						timestring = "Mon,";

					} else {
						closeTimeString = "Mon,";
					}
					if (centreArray.getJSONObject(0).getString("TuesdayTime").contains(":")) {
						timestring = timestring + "Tue,";

					} else {
						closeTimeString = closeTimeString + "Tue,";
					}
					if (centreArray.getJSONObject(0).getString("WednesdayTime").contains(":")) {
						timestring = timestring + "Wed,";

					} else {
						closeTimeString = closeTimeString + "Wed,";
					}
					if (centreArray.getJSONObject(0).getString("ThursdayTime").contains(":")) {
						timestring = timestring + "Thur,";

					} else {
						closeTimeString = closeTimeString + "thur,";
					}
					if (centreArray.getJSONObject(0).getString("FridayTime").contains(":")) {
						timestring = timestring + "Fri,";

					} else {
						closeTimeString = closeTimeString + "Fri,";
					}
					if (centreArray.getJSONObject(0).getString("SaturdayTime").contains(":")) {
						timestring = timestring + "Sat,";

					} else {
						closeTimeString = closeTimeString + "Sat,"; 
					}
					if (centreArray.getJSONObject(0).getString("SundayTime").contains(":")) {
						timestring = timestring + "Sun";

					} else {
						closeTimeString = closeTimeString + "Sun";
					}

					if (timestring.length() < 1) {
						tvTime.setText("Not Available");
					} else {
						tvTime.setText(timestring);

					}

					Calendar calendar = Calendar.getInstance();
					int today = calendar.get(Calendar.DAY_OF_WEEK);
					String CurrentTime = "";

					if (today == 1) {
						CurrentTime = centreArray.getJSONObject(0).getString("MondayTime");
					} else if (today == 2) {
						CurrentTime = centreArray.getJSONObject(0).getString("TuesdayTime");
					} else if (today == 3) {
						CurrentTime = centreArray.getJSONObject(0).getString("WednesdayTime");
					} else if (today == 4) {
						CurrentTime = centreArray.getJSONObject(0).getString("ThursdayTime");
					} else if (today == 5) {
						CurrentTime = centreArray.getJSONObject(0).getString("FridayTime");
					} else if (today == 6) {
						CurrentTime = centreArray.getJSONObject(0).getString("SaturdayTime");
					} else {
						CurrentTime = centreArray.getJSONObject(0).getString("SundayTime");
					}

					if (CurrentTime.length() > 7) {
						String[] parts = CurrentTime.split("-");
						tvOpen.setText("Open Today Till " + parts[1]);

					} else {
						tvOpen.setText("Not Available");
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

				bSMS.setEnabled(false);
				bEmail.setEnabled(false);
				// generateCoupon();
				progressDialog.dismiss();
				System.out.println(error);

			}
		}) {

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("Cookie", Services.hoja);
				return headers;
			}
		};

		int socketTimeout1 = 30000;
		RetryPolicy policy1 = new DefaultRetryPolicy(socketTimeout1, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		jr.setRetryPolicy(policy1);
		queue.add(jr);

	}

	public void generateCoupon() {

		sendData = new JSONObject();
		try {
			int discountr = (int) Math.round(Double.parseDouble(maxdiscount));
			sendData.put("patientId", patientId);
			sendData.put("centreId", centerId);//centerId
			if (isTestNull) {
				sendData.put("testdetails", JSONObject.NULL);
				sendData.put("maxDiscount",String.valueOf(discountr));

			} else {
				sendData.put("testdetails", testDetails);
				sendData.put("maxDiscount",JSONObject.NULL);
			}
			if(getIntent().getStringExtra("promocode_Id")!=null&&getIntent().getStringExtra("promocode_TestId")!=null&&getIntent().getStringExtra("promo_DiscountAmnt")!=null) {
				sendData.put("promoCodeId", getIntent().getStringExtra("promocode_Id"));
				//sendData.put("maxDiscount",JSONObject.NULL);
				sendData.put("promoapplytestid", getIntent().getStringExtra("promocode_TestId"));
				sendData.put("promoCodeDiscount", getIntent().getStringExtra("promo_DiscountAmnt"));
			}else{
				sendData.put("promoCodeId", JSONObject.NULL);
				//sendData.put("maxDiscount",JSONObject.NULL);
				sendData.put("promoapplytestid", JSONObject.NULL);
				sendData.put("promoCodeDiscount",0);
			}

			/*intent.putExtra("promocode_TestId", promocode_TestId);
			intent.putExtra("promocode_Id", promocode_Id);
			intent.putExtra("promo_DiscountAmnt", promo_DiscountAmnt);
			intent.putExtra("promoTotalamnt", promoTotalamnt);

			<maxDiscount>string</maxDiscount>
			<promoCodeId>guid</promoCodeId>
			<promoapplytestid>guid</promoapplytestid>
			<promoCodeDiscount>decimal</promoCodeDiscount>*/
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//String Couponurl = "http://192.168.1.56:82/WebServices/LabService.asmx/GenerateCoupon";
		/*String Couponurl ="https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/GenerateCoupon";*/
		StaticHolder sttc_holdr=new StaticHolder(CouponActivity.this,StaticHolder.Services_static.GenerateCouponNo);
		String url2=sttc_holdr.request_Url();
		jr = new JsonObjectRequest(Request.Method.POST,url2, sendData, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				//{"d":"Dheer,mad.hav@gmail.com,C1500002255,21/08/2015 10:44:01,9968227244,Mr. Dheer  Kumar"}
				//error {"d":",,C1500001122,25\/08\/2015 13:15:59,,"}
				progressDialog.dismiss();
				bottom.setVisibility(View.VISIBLE);

				System.out.println(response);
				try {

					String resp = response.getString("d");
					Log.d("fullname", resp);
					String[] parts = resp.split(",");
					patName = parts[5];
					patEmail = parts[1];
					patVoucher = parts[2];
					patDay = parts[3];
					contactNumber= parts[4];
					tvVoucherId.setText(patVoucher);
					tvValidTill.setText(patDay);
					tvPatName.setText(patName);
					tvPatEmail.setText(contactNumber);

					sendSMS();

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

				progressDialog.dismiss();
				bottom.setVisibility(View.GONE);
				System.out.println(error);
			}
		}) {

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("Cookie", Services.hoja);
				return headers;
			}
		};

		int socketTimeout1 = 30000;
		RetryPolicy policy1 = new DefaultRetryPolicy(socketTimeout1, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		jr.setRetryPolicy(policy1);
		queue.add(jr);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:
			super.onBackPressed();
			/*try{
				if (IndividualLabTest.fa != null) {
					IndividualLabTest.fa.finish();
				}}catch (NullPointerException ne){
				ne.printStackTrace();
			}*/
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
        super.onBackPressed();
		/*try{
			if (IndividualLabTest.fa != null) {
				IndividualLabTest.fa.finish();
			}}catch (NullPointerException ne){
			ne.printStackTrace();
		}*/
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		finish();
	}
}
