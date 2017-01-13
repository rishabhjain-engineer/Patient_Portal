package com.hs.userportal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Medical extends FragmentActivity {

	static String allergy = "";
	String showlist, list_retrieved;
	int i = 0;
	ListView l;
	ArrayList<String> list = new ArrayList<String>();
	Button add, finish, back;
	ProgressDialog progress, ghoom;
	Services service;
	String id;
	JSONObject sendData, receiveData, medical, sendmedical, sendData1,
			receiveData1;
	JSONArray subArray, temparray;
	AlertDialog alertDialog, alert;
	// static JSONArray arraymed;
	JSONArray medicalarray;
	String vacid;
	// ArrayList<String> vaclist = new ArrayList<String>();
	// ArrayList<String> vacretrieve = new ArrayList<String>();
	// ArrayList<String> vacidlist = new ArrayList<String>();
	static ArrayList<String> selectlist = new ArrayList<String>();
	ArrayList<String> alllist = new ArrayList<String>();
	ArrayAdapter<String> adapter1;
	ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.medical);
		service = new Services(Medical.this);
		l = (ListView) findViewById(R.id.listView1);
		add = (Button) findViewById(R.id.bSend);
		update.arraymed = new JSONArray();
		finish = (Button) findViewById(R.id.Button2);
		back = (Button) findViewById(R.id.bBack);
		Intent z = getIntent();
		id = z.getStringExtra("id");
		adapter = new ArrayAdapter<String>(Medical.this,
				android.R.layout.simple_list_item_1, selectlist);

		new BackgroundProcess().execute();

		l.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				// TODO Auto-generated method stub

				allergy = "";
				alert = new AlertDialog.Builder(Medical.this).create();
				;

				alert.setTitle("Options");
				alert.setMessage("Delete Allergies");

				alert.setButton(AlertDialog.BUTTON_POSITIVE, "Delete",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int id) {

								update.arraymed = new JSONArray();

								selectlist.remove(arg2);
								//Utility.setListViewHeightBasedOnChildren(l);
								adapter.notifyDataSetChanged();
								adapter.notifyDataSetInvalidated();
								temparray = new JSONArray();

								System.out.println(selectlist.size());

								allergy = TextUtils.join(",", selectlist);

								medical = new JSONObject();

								try {

									medical.put("VaccineID","9b430cb0-75a0-4192-af27-32d1fd7ffdf5");
									medical.put("AllergiesName", allergy);
									medical.put("FromDate", "");

								}

								catch (JSONException e) {

									e.printStackTrace();
								}
								update.arraymed.put(medical);

							}
						});

				alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int id) {

								dialog.dismiss();

							}
						});

				alert.show();

			}

		});

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TabActivity tabs = (TabActivity) getParent();
				tabs.getTabHost().setCurrentTab(5);
			}
		});

		finish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// update.arraymed = new JSONArray();

				new submitchange().execute();

			}
		});

		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				sendData = new JSONObject();
				new AllergyAsynctask(sendData).execute();
//				receiveData = service.allergy(sendData);
				System.out.println(receiveData);
				/*try {

					alllist.clear();
					String data = receiveData.getString("d");
					JSONObject cut = new JSONObject(data);
					subArray = cut.getJSONArray("Table");
					for (i = 0; i < subArray.length(); i++)

					{
						alllist.add(subArray.getJSONObject(i).getString(
								"AlertName"));
						System.out.println(alllist.get(i));

					}

				}

				catch (JSONException e) {

					e.printStackTrace();
				}

				Intent i = new Intent(getApplicationContext(),
						allergylist.class);
				i.putStringArrayListExtra("list", alllist);
				i.putStringArrayListExtra("select", selectlist);
				startActivity(i);*/

				// showlist = all.getText().toString();
				// adapter = new ArrayAdapter<String>(Medical.this,
				// android.R.layout.simple_list_item_1, list);
				// l.setAdapter(adapter);
				//
				// if (list.size() == 0) {
				// list.add(showlist);
				// adapter.notifyDataSetChanged();
				//
				// medical = new JSONObject();
				// try {
				//
				// medical.put("VaccineID", "");
				// medical.put("AllergiesName", all.getText().toString());
				// medical.put("FromDate","");
				//
				// }
				//
				// catch (JSONException e) {
				//
				// e.printStackTrace();
				// }
				//
				// update.arraymed.put(medical);
				//
				// }
				//
				// int value = 0;
				// for (i = 0; i < list.size(); i++) {
				//
				// if (showlist.equals(list.get(i))) {
				// value++;
				//
				// }
				//
				// }
				// if (value == 0) {
				// list.add(showlist);
				// adapter.notifyDataSetChanged();
				//
				// medical = new JSONObject();
				// try {
				//
				// medical.put("VaccineID", "");
				// medical.put("AllergiesName", all.getText().toString());
				// medical.put("FromDate","");
				//
				// }
				//
				// catch (JSONException e) {
				//
				// e.printStackTrace();
				// }
				//
				// update.arraymed.put(medical);
				// }

				//Utility.setListViewHeightBasedOnChildren(l);
			}

		});

	}

	class AllergyAsynctask extends AsyncTask<Void , Void, Void>{
		JSONObject dataToSend;

		public AllergyAsynctask(JSONObject sendData) {
			dataToSend = sendData;
		}

		@Override
		protected Void doInBackground(Void... params) {
			receiveData = service.allergy(dataToSend);
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			try {

				alllist.clear();
				String data = receiveData.getString("d");
				JSONObject cut = new JSONObject(data);
				subArray = cut.getJSONArray("Table");
				for (i = 0; i < subArray.length(); i++)

				{
					alllist.add(subArray.getJSONObject(i).getString(
							"AlertName"));
					System.out.println(alllist.get(i));

				}

			}

			catch (JSONException e) {

				e.printStackTrace();
			}

			Intent i = new Intent(getApplicationContext(),
					allergylist.class);
			i.putStringArrayListExtra("list", alllist);
			i.putStringArrayListExtra("select", selectlist);
			startActivity(i);
		}
	}

	class submitchange extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			ghoom = new ProgressDialog(Medical.this);
			ghoom.setCancelable(false);
			ghoom.setMessage("Loading...");
			ghoom.setIndeterminate(true);
			Medical.this.runOnUiThread(new Runnable() {
				public void run() {
					ghoom.show();
				}
			});
		}

		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			String rd = receiveData1.toString();
			if (rd.contains("MyTable")) {
				Toast.makeText(getApplicationContext(),
						"Your changes have been saved!", Toast.LENGTH_SHORT)
						.show();

				finish();
			}

			else {
				Toast.makeText(getApplicationContext(),
						"Your changes could not be saved!", Toast.LENGTH_SHORT)
						.show();
			}
			ghoom.dismiss();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			sendmedical = new JSONObject();

			try {

				sendmedical.put("BasicResult", update.arraybasic);
				sendmedical.put("ResultResidence", update.arrayres);
				sendmedical.put("ResultEducational", update.arrayedu);
				sendmedical.put("ResultWork", update.arraywork);
				sendmedical.put("ResultTravel", update.arraytravel);
				sendmedical.put("ResultPersonal", update.arrayper);
				sendmedical.put("ResultMedical", update.arraymed);
				sendmedical.put("SaveOrUpdate", "Update");

				System.out.println(sendmedical);

			}

			catch (JSONException e) {
				e.printStackTrace();
			}
			receiveData1 = service.register(sendmedical);
			System.out.println(receiveData1);
			// System.out.println(arraymed.length());

			return null;
		}
	}

	class BackgroundProcess extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progress = new ProgressDialog(Medical.this);
			progress.setCancelable(false);
			progress.setMessage("Loading...");
			progress.setIndeterminate(true);
			Medical.this.runOnUiThread(new Runnable() {
				public void run() {
					progress.show();
				}
			});
		}

		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			String data;
			try {

				selectlist.clear();
				data = receiveData1.getString("d");
				JSONObject cut = new JSONObject(data);
				medicalarray = cut.getJSONArray("Table");
				// vacretrieve.clear();
				for (i = 0; i < medicalarray.length(); i++)

				{
					// System.out.println("haha0");
					String abc = medicalarray.getJSONObject(i).getString(
							"CategoryName");

					medical = new JSONObject();

					if (abc.equals("MedicalHistory")) {

						// System.out.println("haha");

						medical.put("VaccineID",
								"9b430cb0-75a0-4192-af27-32d1fd7ffdf5");
						medical.put("AllergiesName", medicalarray
								.getJSONObject(i).getString("AllergiesName"));
						medical.put("FromDate", "");

						update.arraymed.put(medical);

						l.setAdapter(adapter);

						List<String> items = Arrays.asList(medicalarray
								.getJSONObject(i).getString("AllergiesName")
								.split("\\s*,\\s*"));
						for (int h = 0; h < items.size(); h++) {
							if (!selectlist.contains(items.get(h)))
								selectlist.add(items.get(h));
						}

						// if(!selectlist.contains(medicalarray.getJSONObject(i).getString("AllergiesName")))
						// selectlist.add(medicalarray.getJSONObject(i).getString("AllergiesName"));
						//Utility.setListViewHeightBasedOnChildren(l);
						adapter.notifyDataSetChanged();

					}

				}

			} catch (JSONException e1) {

				e1.printStackTrace();

			}
			progress.dismiss();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			sendData1 = new JSONObject();
			try {

				sendData1.put("PatientId", id);
				sendData1.put("CategoryName", "");

			}

			catch (JSONException e) {

				e.printStackTrace();
			}

			receiveData1 = service.patienthistory(sendData1);
			System.out.println(receiveData1);
			return null;
		}

	}

//	public static class Utility {
//		public static void setListViewHeightBasedOnChildren(ListView listView) {
//			ListAdapter listAdapter = listView.getAdapter();
//
//			if (listAdapter == null) {
//				// pre-condition
//				return;
//			}
//
//			int totalHeight = listView.getPaddingTop()
//					+ listView.getPaddingBottom();
//			for (int i = 0; i < listAdapter.getCount(); i++) {
//				View listItem = listAdapter.getView(i, null, listView);
//				if (listItem instanceof ViewGroup) {
//					listItem.setLayoutParams(new LayoutParams(
//							LayoutParams.WRAP_CONTENT,
//							LayoutParams.WRAP_CONTENT));
//				}
//				listItem.measure(0, 0);
//				totalHeight += listItem.getMeasuredHeight() + 30;
//			}
//
//			ViewGroup.LayoutParams params = listView.getLayoutParams();
//			params.height = totalHeight
//					+ (listView.getDividerHeight()
//							* (listAdapter.getCount() - 1) + 30);
//			listView.setLayoutParams(params);
//		}
//	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// System.out.println(allergy);

		update.arraymed = new JSONArray();
		if (selectlist.size() >= 0) {
			// System.out.println(selectlist.get(1));
			adapter = new ArrayAdapter<String>(Medical.this,
					android.R.layout.simple_list_item_1, selectlist);
			l.setAdapter(adapter);
			//Utility.setListViewHeightBasedOnChildren(l);

			medical = new JSONObject();

			try {

				medical.put("VaccineID", "9b430cb0-75a0-4192-af27-32d1fd7ffdf5");
				medical.put("AllergiesName", allergy);
				medical.put("FromDate", "");

			}

			catch (JSONException e) {

				e.printStackTrace();
			}

			update.arraymed.put(medical);

		}

	}

	@Override
	public void onBackPressed() {
		this.getParent().onBackPressed();
	}
}
