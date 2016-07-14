package com.hs.userportal;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class Personal extends FragmentActivity {

	int i = 0;
	EditText weight, height;
	static EditText checkdate;
	ListView lv;
	ProgressDialog progress,ghoom;
	Button add, finish, back, next;
	Services service;
	AlertDialog alertDialog, alert;
	JSONObject sendData1, receiveData1, personal, sendpersonal;
	// static JSONArray arrayper;
	// JSONArray arraymed;
	JSONArray personalarray, temparray;
	ArrayAdapter<String> m_adapter;
	ArrayList<String> m_listItems = new ArrayList<String>();
	String id;
	@Override
	protected void onCreate(Bundle avedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(avedInstanceState);
		setContentView(R.layout.personal);
		service = new Services(Personal.this);
		update.arrayper = new JSONArray();
		// arraymed = new JSONArray();
		weight = (EditText) findViewById(R.id.etSubject);
		height = (EditText) findViewById(R.id.etContact);
		checkdate = (EditText) findViewById(R.id.editText4);
		add = (Button) findViewById(R.id.bSend);
		back = (Button) findViewById(R.id.bBack);
		next = (Button) findViewById(R.id.bNext);
		finish = (Button) findViewById(R.id.Button2);
		lv = (ListView) findViewById(R.id.list);
		Intent z = getIntent();
		id = z.getStringExtra("id");
		m_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, m_listItems);
		// lv.setAdapter(m_adapter);

		
		
		new BackgroundProcess().execute();
		
		
		

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				// TODO Auto-generated method stub

				alert = new AlertDialog.Builder(Personal.this).create();
				;

				alert.setTitle("Options");
				alert.setMessage("Delete or Edit Height and Weight");

				alert.setButton(AlertDialog.BUTTON_POSITIVE, "Delete",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int id) {

								m_listItems.remove(arg2);
								Utility.setListViewHeightBasedOnChildren(lv);
								m_adapter.notifyDataSetChanged();
								m_adapter.notifyDataSetInvalidated();
								temparray = new JSONArray();

								try {

									for (int i = 0; i < update.arrayper
											.length(); i++)

									{

										if (i != arg2)
											temparray.put(update.arrayper
													.get(i));

									}
								} catch (Exception e) {
									e.printStackTrace();
								}

								update.arrayper = new JSONArray();

								try {

									for (int i = 0; i < temparray.length(); i++) {

										update.arrayper.put(temparray.get(i));

										System.out.println(update.arrayper);

									}
								} catch (Exception e) {
									e.printStackTrace();
								}

							}
						});

				alert.setButton(AlertDialog.BUTTON_NEUTRAL, "Edit",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int id) {

								m_listItems.remove(arg2);
								Utility.setListViewHeightBasedOnChildren(lv);
								m_adapter.notifyDataSetChanged();
								m_adapter.notifyDataSetInvalidated();
								temparray = new JSONArray();

								try {

									for (int i = 0; i < update.arrayper
											.length(); i++)

									{

										if (i != arg2)
											temparray.put(update.arrayper
													.get(i));

									}
								} catch (Exception e) {
									e.printStackTrace();
								}

								try {
									weight.setText(update.arrayper
											.getJSONObject(arg2).getString(
													"Weight"));
									height.setText(update.arrayper
											.getJSONObject(arg2).getString(
													"Height"));
									checkdate.setText(update.arrayper
											.getJSONObject(arg2).getString(
													"FromDate"));
								}

								catch (JSONException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}

								update.arrayper = new JSONArray();

								try {

									for (int i = 0; i < temparray.length(); i++) {

										update.arrayper.put(temparray.get(i));

										System.out.println(update.arrayper);

									}
								} catch (Exception e) {
									e.printStackTrace();
								}

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
				tabs.getTabHost().setCurrentTab(4);
			}
		});

		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TabActivity tabs = (TabActivity) getParent();
				tabs.getTabHost().setCurrentTab(6);
			}
		});

		finish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				new submitchange().execute();
				
				
			}
		});

		add.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (weight.getText().toString().equals("")
						|| checkdate.getText().toString().equals("")
						|| height.getText().toString().equals("")) {

					alertDialog = new AlertDialog.Builder(Personal.this)
							.create();

					// Setting Dialog Title
					alertDialog.setTitle("Message");

					// Setting Dialog Message
					alertDialog.setMessage("No field can be left blank!");

					// Setting OK Button
					alertDialog.setButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {

									// TODO Add your code for the button here.
								}
							});
					// Showing Alert Message
					alertDialog.show();

				}

				else {

					
					lv.setAdapter(m_adapter);
					String input = "Weight: "+ weight.getText().toString()+", Height: "+height.getText().toString()+" Last Checked on: "+checkdate.getText().toString();
							

					if (null != input && input.length() > 0) {

						if (m_listItems.size() == 0) {
							m_listItems.add(input);
							m_adapter.notifyDataSetChanged();
							personal = new JSONObject();
							try {

								personal.put("Weight", weight.getText()
										.toString());
								personal.put("Height", height.getText()
										.toString());
								personal.put("FromDate", checkdate.getText()
										.toString());

							}

							catch (JSONException e) {

								e.printStackTrace();
							}

							update.arrayper.put(personal);

						}
						int value = 0;
						for (i = 0; i < m_listItems.size(); i++) {

							if (input.equals(m_listItems.get(i))) {
								value++;

							}

						}
						if (value == 0) {
							m_listItems.add(input);
							m_adapter.notifyDataSetChanged();
							personal = new JSONObject();
							try {

								personal.put("Weight", weight.getText()
										.toString());
								personal.put("Height", height.getText()
										.toString());
								personal.put("FromDate", checkdate.getText()
										.toString());

							}

							catch (JSONException e) {

								e.printStackTrace();
							}

							update.arrayper.put(personal);
						}
					}

				}
				Utility.setListViewHeightBasedOnChildren(lv);
			}

		});

		checkdate.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				DialogFragment newFragment = new DatePickerFragment();
				newFragment.show(getSupportFragmentManager(), "datePicker");

			}
		});

		checkdate.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasfocus) {
				// TODO Auto-generated method stub

				if (hasfocus) {
					DialogFragment newFragment = new DatePickerFragment();
					newFragment.show(getSupportFragmentManager(), "datePicker");

				}

			}
		});

	}

	
	class submitchange extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			ghoom = new ProgressDialog(Personal.this);
			ghoom.setCancelable(false);
			ghoom.setMessage("Loading...");
			ghoom.setIndeterminate(true);
			Personal.this.runOnUiThread(new Runnable() {
				public void run() {
					ghoom.show();
				}
			});
		}
		
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			String rd = receiveData1.toString();
			if(rd.contains("MyTable"))
			{
				Toast.makeText(getApplicationContext(),
					"Your changes have been saved",
					Toast.LENGTH_SHORT).show();
				
			finish();
			}
			
			
			else
			{
				Toast.makeText(getApplicationContext(),
						"Your changes could not be saved",
						Toast.LENGTH_SHORT).show();
			}
			ghoom.dismiss();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			sendpersonal = new JSONObject();

			try {

				sendpersonal.put("BasicResult", update.arraybasic);
				sendpersonal.put("ResultResidence", update.arrayres);
				sendpersonal.put("ResultEducational", update.arrayedu);
				sendpersonal.put("ResultWork", update.arraywork);
				sendpersonal.put("ResultTravel", update.arraytravel);
				sendpersonal.put("ResultPersonal", update.arrayper);
				sendpersonal.put("ResultMedical", update.arraymed);
				sendpersonal.put("SaveOrUpdate", "Update");

				System.out.println(sendpersonal);

			}

			catch (JSONException e) {
				e.printStackTrace();
			}
			receiveData1 = service.register(sendpersonal);
			System.out.println(receiveData1);
			// System.out.println(arrayper.length());
			
			return null;
		}
	}
	
	class BackgroundProcess extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progress = new ProgressDialog(Personal.this);
			progress.setCancelable(false);
			progress.setMessage("Loading...");
			progress.setIndeterminate(true);
			Personal.this.runOnUiThread(new Runnable() {
				public void run() {
					progress.show();
				}
			});
		}
		
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			

			String data;
			try {

				data = receiveData1.getString("d");
				JSONObject cut = new JSONObject(data);
				personalarray = cut.getJSONArray("Table");

				for (i = 0; i < personalarray.length(); i++)

				{
					// System.out.println("haha0");
					String abc = personalarray.getJSONObject(i).getString(
							"CategoryName");

					personal = new JSONObject();

					if (abc.equals("Personal")) {

						// System.out.println("haha");

						personal.put("Weight", personalarray.getJSONObject(i)
								.getString("Weight"));
						personal.put("Height", personalarray.getJSONObject(i)
								.getString("Height"));
						personal.put("FromDate", personalarray.getJSONObject(i)
								.getString("FromDate"));

						update.arrayper.put(personal);

						lv.setAdapter(m_adapter);
						m_listItems.add("Weight: "
								+ personalarray.getJSONObject(i)
										.getString("Weight")
								+ ", Height: "
								+ personalarray.getJSONObject(i)
										.getString("Height")
								+ " Last Checked on: "
								+ personalarray.getJSONObject(i).getString(
										"FromDate"));
						Utility.setListViewHeightBasedOnChildren(lv);
						m_adapter.notifyDataSetChanged();

					}

				}

			} catch (JSONException e1) {

				e1.printStackTrace();

			}
			progress.dismiss();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			sendData1 = new JSONObject();
			try {

				sendData1.put("PatientId", id);
				sendData1.put("CategoryName", "");

			}

			catch (JSONException e) {

				e.printStackTrace();
			}

			receiveData1 = service.patienthistory(sendData1);
			System.out.println(receiveData1);// TODO Auto-generated method stub
			
			return null;
		}
		
	}
	
	public static class Utility {
		public static void setListViewHeightBasedOnChildren(ListView listView) {
			ListAdapter listAdapter = listView.getAdapter();

			if (listAdapter == null) {
				// pre-condition
				return;
			}

			int totalHeight = listView.getPaddingTop()
					+ listView.getPaddingBottom();
			for (int i = 0; i < listAdapter.getCount(); i++) {
				View listItem = listAdapter.getView(i, null, listView);
				if (listItem instanceof ViewGroup) {
					listItem.setLayoutParams(new LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
				}
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight() + 30;
			}

			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalHeight
					+ (listView.getDividerHeight()
							* (listAdapter.getCount() - 1) + 30);
			listView.setLayoutParams(params);
		}
	}

	public static class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
			// Do something with the date chosen by the user
			 
			    int month = monthOfYear + 1;
			    String formattedMonth = "" + month;
			    String formattedDayOfMonth = "" + dayOfMonth;

			    if(month < 10){

			        formattedMonth = "0" + month;
			    }
			    if(dayOfMonth < 10){

			        formattedDayOfMonth = "0" + dayOfMonth;
			    }
			    checkdate.setText(formattedDayOfMonth + "-" + formattedMonth + "-" + year);

			
		
		
		}
	}
	@Override
	  public void onBackPressed() {
	    this.getParent().onBackPressed();   
	  }
}
