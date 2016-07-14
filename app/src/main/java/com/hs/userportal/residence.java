package com.hs.userportal;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
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

public class residence extends FragmentActivity {
    String checkedit="";
    ArrayList<String> patienthistorylist=new ArrayList<String>();
    ArrayList<String> m_listItems = new ArrayList<String>();
    ArrayList<HashMap<String,String >> toeditFieldlist=new ArrayList<HashMap<String, String>>();
    CheckBox present;
    Custom_profile_adapter m_adapter;
    int i = 0;


    private EditText city, country, state,add, pincode, house;
    private static EditText from,to;
    ListView l;
    AlertDialog alertDialog;
    AlertDialog alert;
    ProgressDialog progress, ghoom;

    Button addbtn;
    JSONObject sendData, receiveData, sendData1, receiveData1;
    private ScrollView scroll_id;
    Services service;
    JSONArray subArray, temparray, subArray1, newarray, newarray1, newarray2;
    String[] nationlist ;

    JSONArray residearray;
    ArrayAdapter<String> adapter1;
    ArrayList<String> areaa = new ArrayList<String>();
    ArrayList<String> countryy = new ArrayList<String>();
    ArrayList<String> statee = new ArrayList<String>();
    ArrayList<String> cityy = new ArrayList<String>();
    ArrayList<String> pinn = new ArrayList<String>();
    ArrayList<String> list = new ArrayList<String>();
    ArrayList<String> countrylist = new ArrayList<String>();
    ArrayList<String> countryids = new ArrayList<String>();
    SharedPreferences sharedPreferences;
    String showlist, id, countryval = "", stateval = "", cityval = "", patientId;
    ArrayAdapter<String> adapter;
    Date date1, date2,datecurrent;
    private String PatientHistoryId="", Address, cityName, stateName, CountryName, Pincode, dates, Name;
    static int stno,month2,year2,day2,month1,year1,day1;
    Calendar c;
    int selection;

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.residencenew);
       // nationlist = getResources().getStringArray(R.array.national_list);
      /*  for(int i=0;i<nationlist.length;i++){
           // countrylist.add(nationlist[i]);
        }*/
         c = Calendar.getInstance();
         year2 = c.get(Calendar.YEAR);
         month2 = c.get(Calendar.MONTH);
         day2 = c.get(Calendar.DAY_OF_MONTH);
        year1 = c.get(Calendar.YEAR);
        month1 = c.get(Calendar.MONTH);
        day1 = c.get(Calendar.DAY_OF_MONTH);
        adapter = new ArrayAdapter<String>(residence.this,
                android.R.layout.simple_list_item_1, list);
        Intent z = getIntent();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        patientId = sharedPreferences.getString("ke", "");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        id = z.getStringExtra("id");
        addbtn = (Button) findViewById(R.id.bSend);
        scroll_id=(ScrollView)findViewById(R.id.scroll_id);
      //  back = (Button) findViewById(R.id.bBack);
      //  next = (Button) findViewById(R.id.bNext);
       // b1 = (Button) findViewById(R.id.bFin);
        house = (EditText) findViewById(R.id.etHouseNo);
        add = (EditText) findViewById(R.id.etSubject);
        //area = (AutoCompleteTextView) findViewById(R.id.etContact);
        city = (EditText) findViewById(R.id.editText4);
        state = (EditText) findViewById(R.id.editText5);
        country = (EditText) findViewById(R.id.editText6);
        pincode = (EditText) findViewById(R.id.editText7);
        from = (EditText) findViewById(R.id.editText8);
        to = (EditText) findViewById(R.id.etName);
        l = (ListView) findViewById(R.id.listView1);
        present = (CheckBox) findViewById(R.id.cbPresentWork);
        service = new Services(residence.this);
        update.arrayres = new JSONArray();

       /* m_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, m_listItems);*/
        m_adapter=new Custom_profile_adapter(this,toeditFieldlist,"Residence");
        new Authentication(residence.this,"residence","").execute();
       // new BackgroundProcess().execute();

        country.setInputType(InputType.TYPE_NULL);
        country.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                nationlist = new String[countrylist.size()];
                for (int i = 0; i < countrylist.size(); i++) {
                    nationlist[i] = countrylist.get(i);
                }
                final ArrayAdapter<String> nationadapter = new ArrayAdapter<String>(
                        residence.this, android.R.layout.simple_spinner_dropdown_item, nationlist);
                if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    AlertDialog.Builder genderBuilder = new AlertDialog.Builder(residence.this)
                            .setTitle("Select Nationality")
                            .setAdapter(nationadapter, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    country.setText(nationlist[which]
                                            .toString());
                                    selection = which;
                                    dialog.dismiss();
                                    /*InputMethodManager imm = (InputMethodManager) residence.this.getSystemService(Service.INPUT_METHOD_SERVICE);
                                    imm.showSoftInput(pincode, 0);*/
                                    InputMethodManager inputMethodManager =  (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                                    inputMethodManager.toggleSoftInputFromWindow(pincode.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

                                    pincode.requestFocus();
                                }
                            });
                    AlertDialog genderAlert = genderBuilder.create();
                    genderAlert.show();
                    genderAlert.getListView().setSelection(selection);
                }
                return false;
            }
        });

        state.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    // Some logic here.

                    country.requestFocus();
                    nationlist = new String[countrylist.size()];
                    for (int i = 0; i < countrylist.size(); i++) {
                        nationlist[i] = countrylist.get(i);
                    }
                    final ArrayAdapter<String> nationadapter = new ArrayAdapter<String>(
                            residence.this, android.R.layout.simple_spinner_dropdown_item, nationlist);
                    AlertDialog.Builder genderBuilder = new AlertDialog.Builder(residence.this)
                            .setTitle("Select Nationality")
                            .setAdapter(nationadapter, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    country.setText(nationlist[which]
                                            .toString());
                                    selection = which;
                                    dialog.dismiss();


                                    new Handler().postDelayed(new Runnable() {


                                        @Override
                                        public void run() {
                                            // This method will be executed once the timer is over
                                            // Start your app main activity
                                            InputMethodManager imm = (InputMethodManager) residence.this.getSystemService(Service.INPUT_METHOD_SERVICE);
                                            imm.showSoftInput(pincode, 0);
                                            pincode.requestFocus();
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
                    year2 = c.get(Calendar.YEAR);
                    month2 = c.get(Calendar.MONTH)-1;
                    day2 = c.get(Calendar.DAY_OF_MONTH);
                } else {
                    to.setVisibility(View.VISIBLE);
                   to.setText("");
                   // to.setTextColor(Color.parseColor("#000000"));
                }
            }
        });
        l.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    final int arg2, long arg3) {
                // TODO Auto-generated method stub

                alert = new AlertDialog.Builder(residence.this).create();


                alert.setTitle("Alert");
                alert.setMessage("Please select an Option.");

                alert.setButton(AlertDialog.BUTTON_POSITIVE, "Delete",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {


                                PatientHistoryId = patienthistorylist.get(arg2);
                                patienthistorylist.remove(arg2);
                                toeditFieldlist.remove(arg2);
                                m_listItems.remove(arg2);
                                Utility.setListViewHeightBasedOnChildren(l);
                                m_adapter.notifyDataSetChanged();
                                m_adapter.notifyDataSetInvalidated();
                                checkedit = "delete";
                                new BackgroundProcess().execute();

                            }
                        });

                alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                                dialog.dismiss();

                            }
                        });

                alert.setButton(AlertDialog.BUTTON_NEUTRAL, "Edit",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                PatientHistoryId = patienthistorylist.get(arg2);//city, country, state,add, pincode, house,from,to
                                String ad = toeditFieldlist.get(arg2).get("address");
                                house.setText(toeditFieldlist.get(arg2).get("name"));


                                String add1=toeditFieldlist.get(arg2).get("address");
                               /* add1=add1.replace("-", "");*/
                                add1=add1.replace("\n", "");

                                add.setText(add1.trim());

                                String city1=toeditFieldlist.get(arg2).get("city");
                                city1=city1.replace("-","");
                                city1=city1.replace(",","");
                                city1=city1.replace("\n", "");

                                city.setText(city1.trim());


                                String state1=toeditFieldlist.get(arg2).get("state");
                                state1=state1.replace("-","");
                                state1=state1.replace(",","");
                                state1=state1.replace("\n", "");

                                state.setText(state1.trim());




                                String pin=toeditFieldlist.get(arg2).get("postaladdress");
                                pin=pin.replace("-","");
                                pin=pin.replace(",","");
                                pin=pin.replace("\n","");
                                pin=pin.replace(" ","");
                                pincode.setText(pin);


                                if(toeditFieldlist.get(arg2).get("to").contains("PRESENT")){
                                    to.setText("");
                                }else {
                                    to.setText(toeditFieldlist.get(arg2).get("to"));
                                }
                                from.setText(toeditFieldlist.get(arg2).get("from"));
                                String cont=toeditFieldlist.get(arg2).get("country");
                                cont=cont.replace("-", "");
                                cont=cont.replace(",","");
                                cont=cont.replace("\n", "");

                                country.setText(cont.trim());
                                checkedit = "edit";
                                addbtn.setText("UPDATE");
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
                            }
                        });

                alert.show();

            }

        });


        addbtn.setOnClickListener(new OnClickListener() {

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
                    String currentdate=String.valueOf(day)+"/"+String.valueOf(month)+"/"+String.valueOf(year);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    date1 = sdf.parse(from.getText().toString());
                    datecurrent=sdf.parse(currentdate);
                    if(to.getText().toString().equals("")){
                        date2=null;
                    }else {
                        date2 = sdf.parse(to.getText().toString());
                    }

                } catch (Exception e) {
                }

//city, country, state,add, pincode, house,from,to
                if (from.getText().toString().equals("")
                        ||city.getText().toString().equals("")||country.getText().toString().equals("")||state.getText().toString().equals("")||add.getText().toString().equals("")
                        ||house.getText().toString().equals("")) {
                    alertDialog = new AlertDialog.Builder(residence.this).create();

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

                    alertDialog = new AlertDialog.Builder(residence.this).create();

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

                }else if(date1.compareTo(datecurrent)>=0){
                    alertDialog = new AlertDialog.Builder(residence.this).create();

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
                }else if(date2!=null&&date2.compareTo(datecurrent)>0){
                    alertDialog = new AlertDialog.Builder(residence.this).create();

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
                } else if(!pincode.getText().toString().equals("")&&pincode.getText().toString().length()<4) {
                    alertDialog = new AlertDialog.Builder(residence.this).create();

                    // Setting Dialog Title
                    alertDialog.setTitle("Message");

                    // Setting Dialog Message
                    alertDialog
                            .setMessage("Postal code should be greater than three digits.");

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
                   /* l.setAdapter(m_adapter);
                    //  String education=educationspinner.getSelectedItem().toString();
//city, country, state,add, pincode, house,from,to
                    HashMap hmap=new HashMap<String, String>();
                    hmap.put("name",house.getText().toString());
                    hmap.put("address", add.getText().toString());
                    hmap.put("city", city.getText().toString());
                    hmap.put("state", state.getText().toString());
                    hmap.put("country",country.getText().toString());
                    hmap.put("postaladdress",pincode.getText().toString());
                    hmap.put("from",from.getText().toString());
                    hmap.put("to", to.getText().toString());
                    toeditFieldlist.add(hmap);*/
                    String input;
                    if(to.getText().toString().equals("")) {
                        input = house.getText().toString() + "\n"
                                + add.getText().toString() + "\n"
                                + city.getText().toString() + ","
                                + state.getText().toString() +
                                "\n"+country.getText().toString()
                                + "-"
                                + pincode.getText().toString() + "\n"
                                + from.getText().toString();
                    }else{
                        input = house.getText().toString() + "\n"
                                + add.getText().toString() + "\n"
                                + city.getText().toString() + ","
                                + state.getText().toString() +
                                "\n"+country.getText().toString()
                                + pincode.getText().toString() + "\n"
                                + from.getText().toString() + "-"
                                + to.getText().toString();
                    }
                    if (null != input && input.length() > 0) {

                        if (m_listItems.size() == 0) {
                           /* m_listItems.add(input);
                            m_adapter.notifyDataSetChanged();*/

                            new submitchange().execute();
                        } else {
                            int value = 0;
                            for (i = 0; i < m_listItems.size(); i++) {
                                String item=m_listItems.get(i).trim().replace(" ","");
                                String input1=input.trim().replace(" ","");
                                item=item.replace("-","");
                                input1=input1.replace("-","");
                                item=item.replace("PRESENT","");
                                input1=input1.replace("PRESENT","");
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

                                new submitchange().execute();
                            }else{
                                Toast.makeText(getApplicationContext(),"Duplicate entries not allowed!",Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                }
                Utility.setListViewHeightBasedOnChildren(l);
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

    public  static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker


            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year1, month1, day1);
        }

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
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
            from.setText(formattedDayOfMonth + "/" + formattedMonth + "/"
                    + year);

           // to.requestFocus();
        }
    }

    class submitchange extends AsyncTask<Void, Void, Void> {

        String Name,address1,cityName,stateName,CountryName,Pincode,fromdate,todate,message;
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            ghoom = new ProgressDialog(residence.this);
            ghoom.setCancelable(false);
            ghoom.setMessage("Loading...");
            ghoom.setIndeterminate(true);
            Name=house.getText().toString();
            address1=add.getText().toString();
            cityName=city.getText().toString();
            stateName=state.getText().toString();
            CountryName=country.getText().toString();
            Pincode=pincode.getText().toString();
            fromdate=from.getText().toString();
            todate=to.getText().toString();
//city, country, state,add, pincode, house,from,to
            ghoom.show();
			/*Work.this.runOnUiThread(new Runnable() {
				public void run() {

				}
			});*/
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(checkedit.equals("edit")) {
                if (message.equals("success")) {
                    ghoom.dismiss();
                    Toast.makeText(getApplicationContext(), "Your changes have been saved!", Toast.LENGTH_SHORT).show();
                    //	wo.setText("");
                    house.setText("");
                    add.setText("");
                    city.setText("");
                    state.setText("");
                    country.setText("");
                    pincode.setText("");
                    from.setText("");
                    to.setText("");

                    checkedit = "";
                    PatientHistoryId="";
                    addbtn.setText("ADD");
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
                        JSONArray	workarray = cut.getJSONArray("Table");
                        if (workarray.length() > 0) {
                            patienthistorylist.clear();
                            toeditFieldlist.clear();
                        }
                        HashMap<String,String> hmap;
                        for (i = 0; i < workarray.length(); i++)

                        {
                            //PatientHistoryId,CategoryId,Name,Address,cityName,stateName,CountryName,Pincode,fromdate,todate;
                          //  patienthistorylist.add(workarray.getJSONObject(i).getString("PatientHistoryId"));
                            String todatenull = workarray.getJSONObject(i).getString("todate");
                            if (todatenull.equals("null")) {
                                todatenull = "";
                            }
                            hmap=new HashMap<String, String>();
                            hmap.put("name",workarray.getJSONObject(i).getString("Name"));
                            hmap.put("address", workarray.getJSONObject(i).getString(
                                    "Address"));
                            hmap.put("city",workarray.getJSONObject(i).getString("cityName"));
                            hmap.put("state",workarray.getJSONObject(i).getString("stateName"));
                            hmap.put("country",workarray.getJSONObject(i).getString("CountryName"));
                            hmap.put("postaladdress",workarray.getJSONObject(i).getString(
                                    "Pincode"));
                            hmap.put("PatientHistoryId", workarray.getJSONObject(i).getString("PatientHistoryId"));
                            hmap.put("from", workarray.getJSONObject(i).getString(
                                    "fromdate"));
                            hmap.put("to", todatenull);
                            toeditFieldlist.add(hmap);
                        }
                        new Helper().sortHashListByDate(toeditFieldlist);
                        for (i = 0; i < toeditFieldlist.size(); i++)

                        {
                            patienthistorylist.add(toeditFieldlist.get(i).get("PatientHistoryId"));
                            String todatenull=toeditFieldlist.get(i).get("to");
                            if (todatenull != "") {
                                m_listItems.add(toeditFieldlist.get(i).get("name")
                                        + "\n"
                                        + toeditFieldlist.get(i).get("address")
                                        + "\n"

                                        + toeditFieldlist.get(i).get("city")
                                        + ", "
                                        +  toeditFieldlist.get(i).get("state")

                                        +"\n"+toeditFieldlist.get(i).get("country")
                                        +", "
                                        + toeditFieldlist.get(i).get("postaladdress")
                                        + "\n"
                                        +toeditFieldlist.get(i).get("from")
                                        + "-"
                                        + todatenull);
                            }
                            l.setAdapter(m_adapter);
                            Utility.setListViewHeightBasedOnChildren(l);
                            m_adapter.notifyDataSetChanged();

                        }

                        //	wo.setText("");
                        house.setText("");
                        add.setText("");
                        city.setText("");
                        state.setText("");
                        country.setText("");
                        pincode.setText("");
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
            }
            else	if (message.equals("success")) {
                ghoom.dismiss();
                Toast.makeText(getApplicationContext(),
                        "Your changes have been saved!", Toast.LENGTH_SHORT)
                        .show();
                checkedit="";
                PatientHistoryId="";
                house.setText("");
                add.setText("");
                city.setText("");
                state.setText("");
                country.setText("");
                pincode.setText("");
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
            }

            else {
                ghoom.dismiss();
                Toast.makeText(getApplicationContext(),
                        "Your changes could not be saved!", Toast.LENGTH_SHORT)
                        .show();
                checkedit="";
            }


        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            /*countryids.clear();
            countrylist.clear();*/
            JSONObject sendwork = new JSONObject();
            JSONObject senddata=new JSONObject();
            String country_id="";
            for(int i=0;i<countrylist.size();i++){
                if(CountryName.equals(countrylist.get(i))) {
                    country_id=countryids.get(i);
                }
            }

            try {
                if (checkedit.equalsIgnoreCase("edit")) {
                    sendwork.put("Name", Name);
                    sendwork.put("Address", address1);
                    sendwork.put("cityName", cityName);
                    sendwork.put("stateName",  stateName);

                    sendwork.put("CountryId",country_id);
                    sendwork.put("Pincode", Pincode);
                    sendwork.put("fromdate", fromdate);
                    /*if(todate.equals("")){
                        sendwork.put("todate", JSONObject.NULL);
                    }else{*/
                    sendwork.put("todate", todate);
                    // }

                    sendwork.put("profileParameter","residence");
                    sendwork.put("PatientHistoryId",PatientHistoryId);
                    JSONArray jarray=new JSONArray();
                    jarray.put(sendwork);

                    senddata.put("otherDetails",jarray);
                    senddata.put("UserId",id);
                    senddata.put("typeselect","residence");
                    senddata.put("statusType","edit");
                }else{
                    sendwork.put("Name", Name);
                    sendwork.put("Address", address1);
                    sendwork.put("cityName", cityName);
                    sendwork.put("stateName",  stateName);
                    sendwork.put("CountryId",country_id);
                    sendwork.put("Pincode", Pincode);
                    sendwork.put("fromdate", fromdate);
                    sendwork.put("todate", todate);
                    sendwork.put("profileParameter","residence");
                    sendwork.put("PatientHistoryId","");
                    JSONArray jarray=new JSONArray();
                    jarray.put(sendwork);

                    senddata.put("otherDetails",jarray);
                    senddata.put("UserId",id);
                    senddata.put("typeselect","residence");
                    senddata.put("statusType","");
                }
                receiveData1 = service.saveOtherDetail(senddata);
                message = receiveData1.getString("d");
            }  catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

    class BackgroundProcess extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(residence.this);
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
                    Toast.makeText(getApplicationContext(), receiveData1.getString("d"), Toast.LENGTH_SHORT).show();
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
                    HashMap<String,String> hmap;

                    for (i = 0; i < workarray.length(); i++)

                    {
                        //PatientHistoryId,CategoryId,Name,Address,cityName,stateName,CountryName,Pincode,fromdate,todate;
                      //  patienthistorylist.add(workarray.getJSONObject(i).getString("PatientHistoryId"));
                        String todatenull = workarray.getJSONObject(i).getString("todate").trim();
                        if (todatenull.equals("null")) {
                            todatenull = "PRESENT";
                        }
                        String postal_null= workarray.getJSONObject(i).getString("Pincode").trim();
                        if(postal_null.equalsIgnoreCase("null")){
                            postal_null="";
                        }
                        hmap=new HashMap<String, String>();
                        hmap.put("name",workarray.getJSONObject(i).getString("Name").trim());
                        hmap.put("address", workarray.getJSONObject(i).getString(
                                "Address").trim());
                        hmap.put("city",workarray.getJSONObject(i).getString("cityName").trim());
                        hmap.put("state",workarray.getJSONObject(i).getString("stateName").trim());
                        hmap.put("country",workarray.getJSONObject(i).getString("CountryName").trim());
                        hmap.put("PatientHistoryId",workarray.getJSONObject(i).getString("PatientHistoryId").trim());
                        hmap.put("postaladdress",postal_null);
                        hmap.put("from", workarray.getJSONObject(i).getString(
                                "fromdate"));
                        hmap.put("to", todatenull);
                        toeditFieldlist.add(hmap);
                    }
                    new Helper().sortHashListByDate(toeditFieldlist);
                    for (i = 0; i < toeditFieldlist.size(); i++)

                    {
                        patienthistorylist.add(toeditFieldlist.get(i).get("PatientHistoryId"));
                        String todatenull=toeditFieldlist.get(i).get("to");
                        if (todatenull != "") {
                            m_listItems.add(toeditFieldlist.get(i).get("name")
                                    + "\n"
                                    + toeditFieldlist.get(i).get("address")
                                    + "\n"

                                    + toeditFieldlist.get(i).get("city")
                                    + ", "
                                    +  toeditFieldlist.get(i).get("state")

                                    +"\n"+toeditFieldlist.get(i).get("country")
                                    +", "
                                    + toeditFieldlist.get(i).get("postaladdress")
                                    + "\n"
                                    +toeditFieldlist.get(i).get("from")
                                    + "-"
                                    + todatenull);
                        }/*else{
                            m_listItems.add(workarray.getJSONObject(i).getString(
                                    "Name")
                                    + "\n"
                                    + workarray.getJSONObject(i).getString(
                                    "Address")
                                    + "\n"

                                    + workarray.getJSONObject(i).getString("cityName")
                                    + ","
                                    + workarray.getJSONObject(i).getString("stateName")
                                    + "-"
                                    + workarray.getJSONObject(i).getString(
                                    "Pincode")
                                    + "\n"
                                    + workarray.getJSONObject(i).getString(
                                    "fromdate")
                                    );
                        }*/

                    }
                    l.setAdapter(m_adapter);
                    Utility.setListViewHeightBasedOnChildren(l);
                    m_adapter.notifyDataSetChanged();

                }
            }catch(JSONException e1){

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
                    sendData.put("patientHistoryId",PatientHistoryId);
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
                    sendData1.put("profileParameter", "residence");
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

    public static  class DatePickerFragment1 extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker


            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year2, month2, day2);
        }

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // Do something with the date chosen by the user
//month,year,day
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

    public void startBackgroundprocess(){
        new BackgroundProcess().execute();
    }
    /*@Override
    protected void onResume() {
        super.onResume();
        if(Helper.authentication_flag==true){
            this.getParent().onBackPressed();
        }

    }*/
}
