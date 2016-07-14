package com.hs.userportal;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import adapters.Custom_profile_adapter;

public class Travel extends FragmentActivity {

    String checkedit = "", PatientHistoryId = "";
    ArrayList<String> patienthistorylist = new ArrayList<String>();
    String[] nationlist;
    private EditText ci, st, co, description;
    static int month2, year2, day2, month1, year1, day1;
    Calendar c;
    ArrayList<HashMap<String, String>> toeditFieldlist = new ArrayList<HashMap<String, String>>();


    AlertDialog alertDialog, alert;
    int i = 0;
    EditText wo, pi;
    static EditText from, to;
    String countryval = "", stateval = "", cityval = "";
    private ScrollView scroll_id;
    ListView lv;
    CheckBox present;
    Date date1, date2, datecurrent;
    Button add;
    Services service;
    ProgressDialog progress, ghoom;
    JSONObject sendData1, receiveData1, receiveData, sendData;
    JSONArray subArray, temparray, subArrayTr, subArray1, newarray1, newarray2, newarray;
    // static JSONArray arraytravel;
    // JSONArray arraytravel;
    // JSONArray arrayper;
    // JSONArray arraymed;
    JSONArray travelarray;
    ArrayAdapter<String> adapter1;


    ArrayList<String> countrylist = new ArrayList<String>();
    ArrayList<String> countryids = new ArrayList<String>();
    String id;
    Custom_profile_adapter m_adapter;
    ArrayList<String> m_listItems = new ArrayList<String>();
    int selection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travelnew);

        c = Calendar.getInstance();
        year2 = c.get(Calendar.YEAR);
        month2 = c.get(Calendar.MONTH);
        day2 = c.get(Calendar.DAY_OF_MONTH);
        year1 = c.get(Calendar.YEAR);
        month1 = c.get(Calendar.MONTH);
        day1 = c.get(Calendar.DAY_OF_MONTH);
        scroll_id=(ScrollView)findViewById(R.id.scroll_id);
        update.arraytravel = new JSONArray();
        // arrayper = new JSONArray();
        // arraymed = new JSONArray();

        service = new Services(Travel.this);
        //wo = (EditText) findViewById(R.id.etContact);
        description = (EditText) findViewById(R.id.etSubject);
        //ar = (AutoCompleteTextView) findViewById(R.id.editText4);
        ci = (EditText) findViewById(R.id.editText5);
        st = (EditText) findViewById(R.id.editText6);
        co = (EditText) findViewById(R.id.editText7);
        pi = (EditText) findViewById(R.id.editText8);
        from = (EditText) findViewById(R.id.etName);
        to = (EditText) findViewById(R.id.editText9);
        lv = (ListView) findViewById(R.id.list);
        add = (Button) findViewById(R.id.bAdd);
        //  back = (Button) findViewById(R.id.bBack);
        //   next = (Button) findViewById(R.id.bNext);
        //finish = (Button) findViewById(R.id.bFin);
        present = (CheckBox) findViewById(R.id.cbPresentWork);
        Intent z = getIntent();
        id = z.getStringExtra("id");

      /*  m_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, m_listItems);*/
        m_adapter=new Custom_profile_adapter(this,toeditFieldlist,"Travel");

        new Authentication(Travel.this, "Travel", "").execute();
        //  new BackgroundProcess().execute();

        co.setInputType(InputType.TYPE_NULL);
        co.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                nationlist = new String[countrylist.size()];
                for (int i = 0; i < countrylist.size(); i++) {
                    nationlist[i] = countrylist.get(i);
                }
                final ArrayAdapter<String> nationadapter = new ArrayAdapter<String>(
                        Travel.this, android.R.layout.simple_spinner_dropdown_item, nationlist);

                if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    AlertDialog.Builder genderBuilder = new AlertDialog.Builder(Travel.this)
                            .setTitle("Select Nationality")
                            .setAdapter(nationadapter, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    co.setText(nationlist[which]
                                            .toString());
                                    selection = which;
                                    dialog.dismiss();
                                    InputMethodManager imm = (InputMethodManager) Travel.this.getSystemService(Service.INPUT_METHOD_SERVICE);
                                    imm.showSoftInput(pi, 0);
                                   from.requestFocus();
                                }
                            });
                    AlertDialog genderAlert = genderBuilder.create();
                    genderAlert.show();
                    genderAlert.getListView().setSelection(selection);
                }
                return false;
            }
        });
      /*  pi.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    // Some logic here.


                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(pi.getWindowToken(), 0);


                    from.requestFocus();
                }
                return false;  // Focus will change according to the actionId
            }
        });*/
        st.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    // Some logic here.

                    co.requestFocus();
                    nationlist = new String[countrylist.size()];
                    for (int i = 0; i < countrylist.size(); i++) {
                        nationlist[i] = countrylist.get(i);
                    }
                    final ArrayAdapter<String> nationadapter = new ArrayAdapter<String>(
                            Travel.this, android.R.layout.simple_spinner_dropdown_item, nationlist);
                    AlertDialog.Builder genderBuilder = new AlertDialog.Builder(Travel.this)
                            .setTitle("Select Nationality")
                            .setAdapter(nationadapter, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    co.setText(nationlist[which]
                                            .toString());
                                    selection = which;
                                    dialog.dismiss();


                                    new Handler().postDelayed(new Runnable() {


                                        @Override
                                        public void run() {
                                            // This method will be executed once the timer is over
                                            // Start your app main activity
                                            InputMethodManager imm = (InputMethodManager) Travel.this.getSystemService(Service.INPUT_METHOD_SERVICE);
                                            imm.showSoftInput(pi, 0);
                                            pi.requestFocus();
                                        }
                                    }, 3);
                                }
                            });
                    AlertDialog genderAlert = genderBuilder.create();
                    genderAlert.show();
                    genderAlert.getListView().setSelection(selection);
                    return true; // Focus will do whatever you put in the logic.
                }
                return false;  // Focus will change according to the actionId
            }
        });



        present.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (present.isChecked()) {
                   // to.setTextColor(Color.parseColor("#D3D3D3"));

                    to.setText("");
                    to.setVisibility(View.INVISIBLE);
                } else{
                    to.setVisibility(View.VISIBLE);
                    to.setText("");
                }
            }
        });
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    final int arg2, long arg3) {
                // TODO Auto-generated method stub
                //final String itemstring = m_listItems.get(arg2);
                alert = new AlertDialog.Builder(Travel.this).create();


                alert.setTitle("Alert");
                alert.setMessage("Please select an option.");

                alert.setButton(AlertDialog.BUTTON_POSITIVE, "Delete",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {


                                PatientHistoryId = patienthistorylist.get(arg2);
                                patienthistorylist.remove(arg2);
                                toeditFieldlist.remove(arg2);
                                m_listItems.remove(arg2);
                                Utility.setListViewHeightBasedOnChildren(lv);
                                m_adapter.notifyDataSetChanged();
                                m_adapter.notifyDataSetInvalidated();
                                checkedit = "delete";
                                new BackgroundProcess().execute();

                            }
                        });

                alert.setButton(AlertDialog.BUTTON_NEUTRAL, "Edit",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                                    String pin=toeditFieldlist.get(arg2).get("postaladdress");
                                  pin=pin.replace("-","");
                                  pin=pin.replace("\n","");
                                   pin=pin.replace(",","");
                                  pin=pin.replace(" ","");
                                    pi.setText(pin);


                                PatientHistoryId = patienthistorylist.get(arg2);
                                description.setText(toeditFieldlist.get(arg2).get("name"));

                                String city=toeditFieldlist.get(arg2).get("city");
                                city=city.replace("-","");
                                city=city.replace(",","");
                                city=city.replace("\n", "");

                                ci.setText(city.trim());

                                String state=toeditFieldlist.get(arg2).get("state");
                                state=state.replace("-","");
                                state=state.replace(",","");
                                state=state.replace("\n", "");

                                st.setText(state.trim());

                                if (toeditFieldlist.get(arg2).get("to").contains("PRESENT")) {
                                    to.setText("");
                                } else {
                                    to.setText(toeditFieldlist.get(arg2).get("to"));
                                }
                                from.setText(toeditFieldlist.get(arg2).get("from"));

                                String cont=toeditFieldlist.get(arg2).get("country");
                                cont=cont.replace("-", "");
                                cont=cont.replace("\n", "");
                                cont=cont.replace(",", "");
                                co.setText(cont.trim());


                                checkedit = "edit";
                                add.setText("UPDATE");

                                try {
                                    String [] fromdialog=toeditFieldlist.get(arg2).get("from").split("/");
                                    year1=Integer.parseInt(fromdialog[2]);
                                    month1=Integer.parseInt(fromdialog[1])-1;
                                    day1=Integer.parseInt(fromdialog[0]);

                                    String [] fromdialog1=toeditFieldlist.get(arg2).get("to").split("/");
                                    year2=Integer.parseInt(fromdialog1[2]);
                                    month2=Integer.parseInt(fromdialog1[1])-1;
                                    day2=Integer.parseInt(fromdialog1[0]);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }catch (ArrayIndexOutOfBoundsException ex){
                                    ex.printStackTrace();
                                }

                                scroll_id.post(new Runnable() {
                                    public void run() {
                                        // scroll_id.scrollTo(0, scroll_id.getBottom());
                                        scroll_id.fullScroll(ScrollView.FOCUS_UP);
                                    }
                                });

                               /* m_listItems.remove(arg2);
                                toeditFieldlist.remove(arg2);

                                Utility.setListViewHeightBasedOnChildren(lv);
                                m_adapter.notifyDataSetChanged();
                                m_adapter.notifyDataSetInvalidated();*/
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
        add.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                try {

                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    month = month + 1;
                    String formattedMonth = "" + month;
                    String formattedDayOfMonth = "" + day;
                    if (month < 10) {
                        formattedMonth = "0" + month;
                    }
                    if (day < 10) {

                        formattedDayOfMonth = "0" + day;
                    }
                    String currentdate = String.valueOf(day) + "/" + String.valueOf(month) + "/" + String.valueOf(year);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    date1 = sdf.parse(from.getText().toString());

                    datecurrent = sdf.parse(currentdate);
                    if(to.getText().toString().equals("")){
                        date2=null;
                    }else {
                        date2 = sdf.parse(to.getText().toString());
                    }
                } catch (Exception e) {
                }


                if (from.getText().toString().equals("")
                        || description.getText().toString().equals("") || co.getText().toString().equals("")
                        ) {
                    alertDialog = new AlertDialog.Builder(Travel.this).create();

                    // Setting Dialog Title
                    alertDialog.setTitle("Message");

                    // Setting Dialog Message
                    alertDialog.setMessage("No field can be left Blank");

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
                } else if (date2 != null && (date1.compareTo(date2) > 0
                        || date1.compareTo(date2) == 0)) {

                    alertDialog = new AlertDialog.Builder(Travel.this).create();

                    // Setting Dialog Title
                    alertDialog.setTitle("Message");

                    // Setting Dialog Message
                    alertDialog
                            .setMessage("From-Date cannot be equal or greater than To-Date.");

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

                } else if (date1.compareTo(datecurrent) >= 0) {
                    alertDialog = new AlertDialog.Builder(Travel.this).create();

                    // Setting Dialog Title
                    alertDialog.setTitle("Message");

                    // Setting Dialog Message
                    alertDialog
                            .setMessage("From-Date cannot be equal or greater than Current Date.");

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
                } else if (date2 != null && date2.compareTo(datecurrent) > 0) {
                    alertDialog = new AlertDialog.Builder(Travel.this).create();

                    // Setting Dialog Title
                    alertDialog.setTitle("Message");

                    // Setting Dialog Message
                    alertDialog
                            .setMessage("To-Date cannot be greater than Current Date.");

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
                } else if (!pi.getText().toString().equals("") && pi.getText().toString().length() < 4) {
                    alertDialog = new AlertDialog.Builder(Travel.this).create();

                    // Setting Dialog Title
                    alertDialog.setTitle("Message");

                    // Setting Dialog Message
                    alertDialog
                            .setMessage("Postal code should be greater than three digits");

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
                } else {

                    //lv.setAdapter(m_adapter);
                    //  String education=educationspinner.getSelectedItem().toString();

                   /* HashMap hmap = new HashMap<String, String>();
                    hmap.put("name", description.getText().toString());
                    hmap.put("address", "");
                    hmap.put("city", ci.getText().toString());
                    hmap.put("state", st.getText().toString());
                    hmap.put("country", co.getText().toString());
                    hmap.put("postaladdress", pi.getText().toString());
                    hmap.put("from", from.getText().toString());
                    hmap.put("to", to.getText().toString());
                    toeditFieldlist.add(hmap);*/
                    //  patienthistorylist.add("");
                    String input;
                    if (!to.getText().toString().equals("")) {
                        input = description.getText().toString() + "\n"
                                + ci.getText().toString() + ","
                                + st.getText().toString() +
                                "\n"+co.getText().toString()
                                + "-"
                                + pi.getText().toString() + "\n"
                                + from.getText().toString() + "-"
                                + to.getText().toString();
                    } else {
                        input = description.getText().toString() + "\n"
                                + ci.getText().toString() + ","
                                + st.getText().toString() +
                               "\n"+co.getText().toString()
                                + "-"
                                + pi.getText().toString() + "\n"
                                + from.getText().toString();
                    }
                    if (null != input && input.length() > 0) {

                        if (m_listItems.size() == 0) {


                            new submitchange().execute();
                        } else {
                            int value = 0;
                            for (i = 0; i < m_listItems.size(); i++) {

                                String item = m_listItems.get(i).trim().replace(" ", "");
                                String input1 = input.trim().replace(" ", "");
                                item = item.replace("-", "");
                                input1 = input1.replace("-", "");
                                item = item.replace("PRESENT", "");
                                input1 = input1.replace("PRESENT", "");
                                item=item.replace(",","");
                                input1=input1.replace(",","");
                                item=item.replace("\n","");
                                input1=input1.replace("\n","");
                                try {
                                    if (input1.equalsIgnoreCase(item)
                                            ) {
                                        value++;

                                    }
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                            }
                            if (value == 0) {
                               /* m_listItems.add(input);
                                m_adapter.notifyDataSetChanged();*/
                                new submitchange().execute();
                            }else{
                                Toast.makeText(getApplicationContext(),"Duplicate entries not allowed!",Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                }
                Utility.setListViewHeightBasedOnChildren(lv);
            }
        });


        from.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");

            }
        });


        from.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasfocus) {
                // TODO Auto-generated method stub

                if (hasfocus) {
                    DialogFragment newFragment = new DatePickerFragment();
                    newFragment.show(getSupportFragmentManager(), "datePicker");

                }

            }
        });

        to.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                DialogFragment newFragment = new DatePickerFragment1();
                newFragment.show(getSupportFragmentManager(), "datePicker");

            }
        });

        to.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasfocus) {
                // TODO Auto-generated method stub

                if (hasfocus) {
                    DialogFragment newFragment = new DatePickerFragment1();
                    newFragment.show(getSupportFragmentManager(), "datePicker");

                }

            }
        });

    }

    class submitchange extends AsyncTask<Void, Void, Void> {

        String Name, address1, cityName, stateName, CountryName, Pincode, fromdate, todate, message;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            ghoom = new ProgressDialog(Travel.this);
            ghoom.setCancelable(false);
            ghoom.setMessage("Loading...");
            ghoom.setIndeterminate(true);
            Name = description.getText().toString();
            // address1=ad.getText().toString();
            cityName = ci.getText().toString();
            stateName = st.getText().toString();
            CountryName = co.getText().toString();
            Pincode = pi.getText().toString();
            fromdate = from.getText().toString();
            todate = to.getText().toString();

            ghoom.show();
            /*Work.this.runOnUiThread(new Runnable() {
                public void run() {

				}
			});*/
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (checkedit.equals("edit")) {
                if (message.equals("success")) {
                    ghoom.dismiss();
                    Toast.makeText(getApplicationContext(), "Your changes have been saved!", Toast.LENGTH_SHORT).show();
                    //	wo.setText("");
                    description.setText("");
                    ci.setText("");
                    st.setText("");
                    co.setText("");
                    pi.setText("");
                    from.setText("");
                    to.setText("");

                    checkedit = "";
                    PatientHistoryId = "";
                    add.setText("ADD");
                    year2 = c.get(Calendar.YEAR);
                    month2 = c.get(Calendar.MONTH);
                    day2 = c.get(Calendar.DAY_OF_MONTH);
                    year1 = c.get(Calendar.YEAR);
                    month1 = c.get(Calendar.MONTH);
                    day1 = c.get(Calendar.DAY_OF_MONTH);

                    present.setChecked(false);
                    new BackgroundProcess().execute();
                } else {
                    String data;
                    try {

                        data = receiveData1.getString("d");
                        JSONObject cut = new JSONObject(data);
                        JSONArray workarray = cut.getJSONArray("Table");
                        if (workarray.length() > 0) {
                            patienthistorylist.clear();
                            toeditFieldlist.clear();
                        }
                        HashMap<String, String> hmap;
                        for (i = 0; i < workarray.length(); i++)

                        {
                            String todatenull = workarray.getJSONObject(i).getString("todate");
                            if (todatenull.equals("null")) {
                                todatenull = "PRESENT";
                            }
                            String postal_null = workarray.getJSONObject(i).getString("Pincode");
                            if (postal_null.equalsIgnoreCase("null")) {
                                postal_null = "";
                            }
                            String cityname = workarray.getJSONObject(i).getString("cityName");
                            if (cityname.equalsIgnoreCase("")) {
                                cityname = "";
                            } else {
                                cityname = cityname + ", ";
                            }
                            String state_name = workarray.getJSONObject(i).getString("stateName");
                            if (state_name.equalsIgnoreCase("")) {
                                state_name = "";
                            } else {
                                state_name = state_name + "\n";
                            }

                            hmap = new HashMap<String, String>();
                            hmap.put("name", workarray.getJSONObject(i).getString("Name"));
                            hmap.put("address", workarray.getJSONObject(i).getString(
                                    "Address"));
                            hmap.put("city", cityname);
                            hmap.put("state", state_name);
                            hmap.put("country", workarray.getJSONObject(i).getString("CountryName"));
                            hmap.put("PatientHistoryId", workarray.getJSONObject(i).getString("PatientHistoryId"));
                            hmap.put("postaladdress", postal_null);
                            hmap.put("from", workarray.getJSONObject(i).getString(
                                    "fromdate"));
                            hmap.put("to", todatenull);
                            toeditFieldlist.add(hmap);
                        }
                        new Helper().sortHashListByDate(toeditFieldlist);
                        for (i = 0; i < toeditFieldlist.size(); i++)

                        {
                            patienthistorylist.add(toeditFieldlist.get(i).get("PatientHistoryId"));
                            String todatenull = toeditFieldlist.get(i).get("to");

                            if (todatenull != "") {
                                m_listItems.add(toeditFieldlist.get(i).get(
                                        "name") + "\n"
                                        + toeditFieldlist.get(i).get(
                                        "city").trim()
                                        + toeditFieldlist.get(i).get(
                                        "state") + toeditFieldlist.get(i).get("country")
                                        +", "
                                        + toeditFieldlist.get(i).get("postaladdress")+"\n"
                                        + toeditFieldlist.get(i).get(
                                        "from")
                                        + "-"
                                        + todatenull);
                            } /*else {
                            m_listItems.add(workarray.getJSONObject(i).getString(
                                    "Name")
                                    + "\n"
                                    + cityname.trim()
                                    + state_name
                                    + postal_null
                                    + workarray.getJSONObject(i).getString(
                                    "fromdate"));
                        }*/

                        }
                        lv.setAdapter(m_adapter);
                        Utility.setListViewHeightBasedOnChildren(lv);
                        m_adapter.notifyDataSetChanged();

                        //	wo.setText("");
                        description.setText("");
                        ci.setText("");
                        st.setText("");
                        co.setText("");
                        pi.setText("");
                        from.setText("");
                        to.setText("");
                        year2 = c.get(Calendar.YEAR);
                        month2 = c.get(Calendar.MONTH);
                        day2 = c.get(Calendar.DAY_OF_MONTH);
                        year1 = c.get(Calendar.YEAR);
                        month1 = c.get(Calendar.MONTH);
                        day1 = c.get(Calendar.DAY_OF_MONTH);
                    } catch (JSONException e1) {

                        e1.printStackTrace();

                    }
                    ghoom.dismiss();
                    checkedit = "";
                }
            } else if (message.equals("success")) {
                ghoom.dismiss();
                Toast.makeText(getApplicationContext(),
                        "Your changes have been saved!", Toast.LENGTH_SHORT)
                        .show();
                checkedit = "";
                PatientHistoryId = "";
                description.setText("");
                ci.setText("");
                st.setText("");
                co.setText("");
                pi.setText("");
                from.setText("");
                to.setText("");
                year2 = c.get(Calendar.YEAR);
                month2 = c.get(Calendar.MONTH);
                day2 = c.get(Calendar.DAY_OF_MONTH);
                year1 = c.get(Calendar.YEAR);
                month1 = c.get(Calendar.MONTH);
                day1 = c.get(Calendar.DAY_OF_MONTH);
                new BackgroundProcess().execute();
                //finish();
            } else {
                ghoom.dismiss();
                Toast.makeText(getApplicationContext(),
                        "Your changes could not be saved!", Toast.LENGTH_SHORT)
                        .show();
                checkedit = "";
            }


        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            /*countryids.clear();
            countrylist.clear();*/
            JSONObject sendwork = new JSONObject();
            JSONObject senddata = new JSONObject();
            String country_id = "";
            for (int i = 0; i < countrylist.size(); i++) {
                if (CountryName.equals(countrylist.get(i))) {
                    country_id = countryids.get(i);
                }
            }

            try {
                if (checkedit.equalsIgnoreCase("edit")) {
                    sendwork.put("Name", Name);
                    sendwork.put("Address", "");
                    sendwork.put("cityName", cityName);
                    sendwork.put("stateName", stateName);

                    sendwork.put("CountryId", country_id);
                    sendwork.put("Pincode", Pincode);
                    sendwork.put("fromdate", fromdate);
                    /*if(todate.equals("")){
                        sendwork.put("todate", JSONObject.NULL);
                    }else{*/
                    sendwork.put("todate", todate);
                    // }

                    sendwork.put("profileParameter", "travel");
                    sendwork.put("PatientHistoryId", PatientHistoryId);
                    JSONArray jarray = new JSONArray();
                    jarray.put(sendwork);

                    senddata.put("otherDetails", jarray);
                    senddata.put("UserId", id);
                    senddata.put("typeselect", "travel");
                    senddata.put("statusType", "edit");
                } else {
                    sendwork.put("Name", Name);
                    sendwork.put("Address", "");
                    sendwork.put("cityName", cityName);
                    sendwork.put("stateName", stateName);
                    sendwork.put("CountryId", country_id);
                    sendwork.put("Pincode", Pincode);
                    sendwork.put("fromdate", fromdate);
                    sendwork.put("todate", todate);
                    sendwork.put("profileParameter", "travel");
                    sendwork.put("PatientHistoryId", "");
                    JSONArray jarray = new JSONArray();
                    jarray.put(sendwork);

                    senddata.put("otherDetails", jarray);
                    senddata.put("UserId", id);
                    senddata.put("typeselect", "travel");
                    senddata.put("statusType", "");
                }
                receiveData1 = service.saveOtherDetail(senddata);
                message = receiveData1.getString("d");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker


            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year1, month1, day1);
        }

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // Do something with the date chosen by the user
            month1 = monthOfYear;
            day1 = dayOfMonth;
            year1 = year;
            int month = monthOfYear + 1;
            String formattedMonth = "" + month;
            String formattedDayOfMonth = "" + dayOfMonth;

            if (month < 10) {

                formattedMonth = "0" + month;
            }
            if (dayOfMonth < 10) {

                formattedDayOfMonth = "0" + dayOfMonth;
            }
            from.setText(formattedDayOfMonth + "/" + formattedMonth + "/" + year);
           // to.requestFocus();

        }
    }


    class BackgroundProcess extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(Travel.this);
            progress.setCancelable(false);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);
            progress.show();
            /*Work.this.runOnUiThread(new Runnable() {
				public void run() {

				}
			});*/
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //co.setText(countrylist.get(0));
            String data;
            try {
                if (checkedit.equals("delete")) {
                    Toast.makeText(getApplicationContext(), "Successfully deleted", Toast.LENGTH_LONG).show();
                    checkedit = "";
                } else {
                    data = receiveData1.getString("d");
                    JSONObject cut = new JSONObject(data);
                    JSONArray workarray = cut.getJSONArray("Table");
                    if (workarray.length() > 0) {
                        patienthistorylist.clear();
                        toeditFieldlist.clear();
                        m_listItems.clear();
                    }
                    HashMap<String, String> hmap;

                    for (i = 0; i < workarray.length(); i++)

                    {
                        //PatientHistoryId,CategoryId,Name,Address,cityName,stateName,CountryName,Pincode,fromdate,todate;
                       // patienthistorylist.add(workarray.getJSONObject(i).getString("PatientHistoryId"));
                        String todatenull = workarray.getJSONObject(i).getString("todate").trim();
                        if (todatenull.equals("null")) {
                            todatenull = "PRESENT";
                        }
                        String postal_null = workarray.getJSONObject(i).getString("Pincode").trim();
                        if (postal_null.equalsIgnoreCase("null")) {
                            postal_null = "";
                        }
                        String cityname = workarray.getJSONObject(i).getString("cityName").trim();
                        if (cityname.equalsIgnoreCase("")) {
                            cityname = "";
                        } else {
                            cityname = cityname + ", ";
                        }
                        String state_name = workarray.getJSONObject(i).getString("stateName").trim();
                        if (state_name.equalsIgnoreCase("")) {
                            state_name = "";
                        } else {
                            state_name = state_name + "\n";
                        }

                        hmap = new HashMap<String, String>();
                        hmap.put("name", workarray.getJSONObject(i).getString("Name").trim());
                        hmap.put("address", workarray.getJSONObject(i).getString(
                                "Address").trim());
                        hmap.put("city", cityname);
                        hmap.put("state", state_name);
                        hmap.put("country", workarray.getJSONObject(i).getString("CountryName").trim());
                        hmap.put("PatientHistoryId", workarray.getJSONObject(i).getString("PatientHistoryId").trim());
                        hmap.put("postaladdress", postal_null);
                        hmap.put("from", workarray.getJSONObject(i).getString(
                                "fromdate").trim());
                        hmap.put("to", todatenull);
                        toeditFieldlist.add(hmap);
                    }
                    new Helper().sortHashListByDate(toeditFieldlist);
                    for (i = 0; i < toeditFieldlist.size(); i++)

                    {
                        patienthistorylist.add(toeditFieldlist.get(i).get("PatientHistoryId"));
                        String todatenull = toeditFieldlist.get(i).get("to");

                        if (todatenull != "") {
                            m_listItems.add(toeditFieldlist.get(i).get(
                                    "name") + "\n"
                                    + toeditFieldlist.get(i).get(
                                    "city").trim()
                                    + toeditFieldlist.get(i).get(
                                    "state") + toeditFieldlist.get(i).get("country")
                                    +", "
                                    + toeditFieldlist.get(i).get("postaladdress")+"\n"
                                    + toeditFieldlist.get(i).get(
                                    "from")
                                    + "-"
                                    + todatenull);
                        } /*else {
                            m_listItems.add(workarray.getJSONObject(i).getString(
                                    "Name")
                                    + "\n"
                                    + cityname.trim()
                                    + state_name
                                    + postal_null
                                    + workarray.getJSONObject(i).getString(
                                    "fromdate"));
                        }*/

                    }
                    lv.setAdapter(m_adapter);
                    Utility.setListViewHeightBasedOnChildren(lv);
                    m_adapter.notifyDataSetChanged();

                }
            } catch (JSONException e1) {

                e1.printStackTrace();

            }
            progress.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            if (checkedit.equals("delete")) {
                sendData = new JSONObject();
                try {
                    sendData.put("patientHistoryId", PatientHistoryId);
                    receiveData1 = service.deleteSingularDetails(sendData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else {
                sendData = new JSONObject();
                receiveData = service.countrylist(sendData);
                System.out.println(receiveData);

                try {
                    countryids.clear();
                    countrylist.clear();
                    String qw = receiveData.getString("d");
                    JSONObject cut = new JSONObject(qw);
                    newarray = cut.getJSONArray("Table");
                    for (i = 0; i < newarray.length(); i++)

                    {
                        countryids.add(newarray.getJSONObject(i).getString(
                                "CountryId"));
                        countrylist.add(newarray.getJSONObject(i).getString("Name"));

                    }
                    countryval = countryids.get(0);

                    System.out.println(countryval);

                } catch (JSONException e1) {

                    e1.printStackTrace();

                }

                sendData1 = new JSONObject();
                try {

                    sendData1.put("UserId", id);
                    sendData1.put("profileParameter", "travel");
                    sendData1.put("htype", "");
                } catch (JSONException e) {

                    e.printStackTrace();
                }

                receiveData1 = service.patienBasicDetails(sendData1);
                System.out.println(receiveData1);
            }
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
                totalHeight += listItem.getMeasuredHeight() + 40;
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight
                    + (listView.getDividerHeight()
                    * (listAdapter.getCount() - 1) + 40);
            listView.setLayoutParams(params);
        }
    }

    public static class DatePickerFragment1 extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker


            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year2, month2, day2);
        }

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // Do something with the date chosen by the user
            month2 = monthOfYear;
            day2 = dayOfMonth;
            year2 = year;
            int month = monthOfYear + 1;
            String formattedMonth = "" + month;
            String formattedDayOfMonth = "" + dayOfMonth;

            if (month < 10) {

                formattedMonth = "0" + month;
            }
            if (dayOfMonth < 10) {

                formattedDayOfMonth = "0" + dayOfMonth;
            }
            to.setText(formattedDayOfMonth + "/" + formattedMonth + "/" + year);


        }
    }

    @Override
    public void onBackPressed() {
        this.getParent().onBackPressed();
    }

    public void startBackgroundprocess() {
        new BackgroundProcess().execute();
    }
}
