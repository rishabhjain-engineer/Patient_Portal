package com.hs.userportal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

class Authentication extends AsyncTask<Void, Void, Void> {
	private Activity context;
	private String authentication="";
	private String whichbtn,activityname;
	
	public Authentication(Activity context, String activityname , String whichbtnclick){
		this.context=context;
		this.whichbtn=whichbtnclick;
		this.activityname=activityname;
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();

	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub

		try {
			JSONObject sendData = new JSONObject();
			Services service = new Services(context);
			JSONObject receiveData = service.IsUserAuthenticated(sendData);
			System.out.println("IsUserAuthenticated: " + receiveData);
			authentication = receiveData.getString("d");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(Exception ex){
			ex.printStackTrace();
		}

		return null;
	}

	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		try {

			if (!authentication.equals("true")) {

				AlertDialog dialog = new AlertDialog.Builder(context).create();
				dialog.setTitle("Session timed out!");
				dialog.setMessage("Session expired. Please login again.");
				dialog.setCancelable(false);
				dialog.setButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						SharedPreferences sharedpreferences = context.getSharedPreferences("MyPrefs", context.MODE_PRIVATE);
						Editor editor = sharedpreferences.edit();
						editor.clear();
						editor.commit();
						dialog.dismiss();
						
						/*Intent in=new Intent(context,MainActivity.class);
						context.startActivity(in);*/
						Helper.authentication_flag=true;

						context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
						context.finish();

					}
				});
				dialog.show();

			}else {
                if (activityname.equals("IndividualLabTest") && whichbtn.equals("requestpickup")) {
                    ((IndividualLabTest) context).requestpickup();
                } else if (activityname.equals("IndividualLabTest") && whichbtn.equals("generatecoupon")) {
                    ((IndividualLabTest) context).generatecoupon();

                } else if (activityname.equals("IndividualLabTest") && whichbtn.equals("getdetailmenu")) {
                    ((IndividualLabTest) context).detail_andsampleTask("getdetail");


                } else if (activityname.equals("MapLabDetails") && whichbtn.equals("getdetailmenu")) {
                    ((MapLabDetails) context).detail_andsampleTask("getdetail");


                } else if (activityname.equals("MapLabDetails") && whichbtn.equals("getoffer")) {
                    ((MapLabDetails) context).getoffer();

                } else if (activityname.equals("MapLabDetails") && whichbtn.equals("samplerequest")) {
                    ((MapLabDetails) context).detail_andsampleTask("samplebutton");

                } else if (activityname.equals("MapLabDetails") && whichbtn.equals("getcouponevnt")) {
                    ((MapLabDetails) context).getcouponevnt();

                } else if (activityname.equals("update") && whichbtn.equals("")) {
                    ((update) context).startBackgroundprocess();
                } else if (activityname.equals("residence") && whichbtn.equals("")) {
                    ((residence) context).startBackgroundprocess();
                } else if (activityname.equals("Work") && whichbtn.equals("")) {
                    ((Work) context).startBackgroundprocess();
                } else if (activityname.equals("Education") && whichbtn.equals("")) {
                    ((Education) context).startBackgroundprocess();
                } else if (activityname.equals("Travel") && whichbtn.equals("")) {
                    ((Travel) context).startBackgroundprocess();
                } else if (activityname.equals("MyHealth") && whichbtn.equals("")) {
                    ((MyHealth) context).startBackgroundProcess();
                } else if (activityname.equals("Height") && whichbtn.equals("")) {
                    ((Height) context).startBackgroundprocess();
                } else if (activityname.equals("Weight") && whichbtn.equals("")) {
                    ((Weight) context).startBackgroundprocess();
                } else if (activityname.equals("Filevault") && whichbtn.equals("")) {
                    ((Filevault) context).createLockFolder();
                } else if (activityname.equals("Filevault2") && whichbtn.equals("")) {
                    ((Filevault2) context).startBackgroundprocess();
                } else if (activityname.equals("MyFamily") && whichbtn.equals("")) {
                    ((MyFamily) context).LoadFamilyMembers();
                } else if (activityname.equalsIgnoreCase("ReportRecords") && whichbtn.equals("")) {
                    ((ReportRecords) context).startBackgroundProcess();
                }
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
}
