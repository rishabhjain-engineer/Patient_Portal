package com.hs.userportal;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import adapters.Custom_profile_adapter;
import networkmngr.NetworkChangeListener;
import ui.BaseActivity;
import ui.ProfileContainerActivity;
import utils.Utils;

public class Work extends BaseActivity {

    private AlertDialog alertDialog, alert;
    private static int month2, year2, day2, month1, year1, day1 , mTempFromYearValue , mTempToYearValue;
    private Calendar c;
    private int i = 0;
    private EditText wo, ad, pi, mDesignationEt, mRoleEt;
    private static EditText from, to;
    //AutoCompleteTextView ar;
    private EditText ci, st, co;
    private String id;
    private String[] nationlist;
    private ArrayList<HashMap<String, String>> toeditFieldlist = new ArrayList<HashMap<String, String>>();
    private String countryval = "", stateval = "", cityval = "";
    private ListView lv;
    private Button add;
    private Services service;
    private ProgressDialog progress, ghoom;
    private JSONObject sendData1, receiveData1, work, sendwork, newdata, receiveData, sendData;
    private JSONArray subArray, temparray, subArrayTr, newarray, newarray1, newarray2, subArray1;
    private Date date1, date2, datecurrent;
    private String PatientHistoryId = "", CategoryId, Name, Address, cityName, stateName, CountryName, Pincode, fromdate, todate;
    private JSONArray workarray;
    private ArrayAdapter<String> adapter1;
    private ArrayList<String> area = new ArrayList<String>();
    private ArrayList<String> country = new ArrayList<String>();
    private ArrayList<String> state = new ArrayList<String>();
    private ArrayList<String> countrylist = new ArrayList<String>();
    private ArrayList<String> countryids = new ArrayList<String>();
    private ArrayList<String> city = new ArrayList<String>();
    private ArrayList<String> pin = new ArrayList<String>();
    private CheckBox present;
    private ScrollView scroll_id;
    private Custom_profile_adapter m_adapter;
    private ArrayList<String> m_listItems = new ArrayList<String>();
    private ArrayList<String> patienthistorylist = new ArrayList<String>();
    private String checkedit = "";
    private int selection;
    private String[] monthArray = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    private String mFromMonth, mFromYear, mToMonth, mToYear, mFinalFromDate, mFinalToDate;
    private static String mFromCompValue = null, mToCompValue = null ,  mTempFromMonthValue   , mTempFromMonthYearValue , mTempToMonthYearValue , mTempToMonthValue;
    private TextView mFromDateNotRemembered , mToDateNotRemembered;
    private LinearLayout mDateEditTextContainerLL, mFromDateSpinnerContainerLL,mToDateSpinnerContainerLL, mEditBoxContainer;
    private boolean mIsNotRemembered = false, mIsDateValid = true, mIsPresentDateCheck = true , mIsFromDateSpinnerVisible = false , mIsToDateSpinnerVisible = false ;

    boolean case1 = false,  case2 = false,  case3 = false, case4 = false ;  // Rishabh : KINDLY ,See Detail of these variable in  ' add.setOnClickListener Section ' .

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.worknew);
        setupActionBar();
        mActionBar.setTitle("Work");
        from = (EditText) findViewById(R.id.et_fromdate);
        to = (EditText) findViewById(R.id.et_todate);
        mDesignationEt = (EditText) findViewById(R.id.designation_et);
        mRoleEt = (EditText) findViewById(R.id.role_et);
        lv = (ListView) findViewById(R.id.list);
        add = (Button) findViewById(R.id.bAdd);
        c = Calendar.getInstance();
        year2 = c.get(Calendar.YEAR);
        month2 = c.get(Calendar.MONTH);
        day2 = c.get(Calendar.DAY_OF_MONTH);
        year1 = c.get(Calendar.YEAR);
        month1 = c.get(Calendar.MONTH);
        day1 = c.get(Calendar.DAY_OF_MONTH);
        update.arraywork = new JSONArray();
        service = new Services(Work.this);
        scroll_id = (ScrollView) findViewById(R.id.scroll_id);
        wo = (EditText) findViewById(R.id.etContact);
        ad = (EditText) findViewById(R.id.etSubject);
        //ar = (AutoCompleteTextView) findViewById(R.id.editText4);
        ci = (EditText) findViewById(R.id.editText5);
        st = (EditText) findViewById(R.id.editText6);
        co = (EditText) findViewById(R.id.editText7);
        pi = (EditText) findViewById(R.id.editText8);

        mFromDateNotRemembered = (TextView) findViewById(R.id.tv_fromdate_not_remember);
        mToDateNotRemembered = (TextView) findViewById(R.id.tv_todate_not_remember);

        mDateEditTextContainerLL = (LinearLayout) findViewById(R.id.L3);

        mFromDateSpinnerContainerLL = (LinearLayout) findViewById(R.id.ll_spinner_fromdateContainer);
        mToDateSpinnerContainerLL = (LinearLayout) findViewById(R.id.ll_spinner_todateContainer);


        mEditBoxContainer = (LinearLayout) findViewById(R.id.layout_container);

        mFromDateNotRemembered.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mIsFromDateSpinnerVisible) {   // Rishabh :  " From Date " spinner is VISIBLE ;
                    mFromDateNotRemembered.setText(R.string.not_remembered);
                    from.setVisibility(View.VISIBLE);
                    mFromDateSpinnerContainerLL.setVisibility(View.GONE);
                    mIsFromDateSpinnerVisible = false ;
                }
                else {   // Rishabh :  " From Date " spinner is  NOT VISIBLE ;

                    mFromDateNotRemembered.setText(R.string.remembered);
                    from.setVisibility(View.GONE);
                    mFromDateSpinnerContainerLL.setVisibility(View.VISIBLE);
                    mIsFromDateSpinnerVisible = true ;
                }

            }
        });

        mToDateNotRemembered.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mIsToDateSpinnerVisible) {   // Rishabh :  " To Date " spinner is VISIBLE ;
                    mToDateNotRemembered.setText(R.string.not_remembered);
                    to.setVisibility(View.VISIBLE);
                    mToDateSpinnerContainerLL.setVisibility(View.GONE);
                    mIsToDateSpinnerVisible = false ;
                }
                else {   // Rishabh :  " To Date " spinner is  NOT VISIBLE ;

                    mToDateNotRemembered.setText(R.string.remembered);
                    to.setVisibility(View.GONE);
                    mToDateSpinnerContainerLL.setVisibility(View.VISIBLE);
                    mIsToDateSpinnerVisible = true ;
                }

            }
        });

       /* mFromDateNotRemembered.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsNotRemembered) {
                    mIsNotRemembered = false;
                    mFromDateNotRemembered.setText(R.string.not_remembered);

                    mIsFromDateSpinnerVisible = false ;
                    from.setVisibility(View.VISIBLE);
                    mFromDateSpinnerContainerLL.setVisibility(View.GONE);


                } else {
                    mFromDateNotRemembered.setText(R.string.remembered);
                    mIsNotRemembered = true;
                    mIsFromDateSpinnerVisible = true ;
                    from.setVisibility(View.GONE);
                    mFromDateSpinnerContainerLL.setVisibility(View.VISIBLE);
                }

            }
        });

        mToDateNotRemembered.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsNotRemembered) {
                    mIsNotRemembered = false;
                    mToDateNotRemembered.setText(R.string.not_remembered);
                    mIsToDateSpinnerVisible = false ;
                    to.setVisibility(View.VISIBLE);
                    mToDateSpinnerContainerLL.setVisibility(View.GONE);


                } else {
                    mToDateNotRemembered.setText(R.string.remembered);
                    mIsNotRemembered = true;
                    mIsToDateSpinnerVisible = true ;
                    to.setVisibility(View.GONE);
                    mToDateSpinnerContainerLL.setVisibility(View.VISIBLE);
                }

            }
        });*/



        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(new Date(currentTimeMillis));
        to.setText(dateString);
        mToCompValue = dateString;


        Spinner fromMonthSpinner = (Spinner) findViewById(R.id.from_month);
        Spinner fromYearSpinner = (Spinner) findViewById(R.id.from_year);
        ArrayAdapter monthArrayAdapter = new ArrayAdapter(Work.this, R.layout.spinner_appearence, monthArray);
        monthArrayAdapter.setDropDownViewResource(R.layout.spinner_appearence);
        //monthArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromMonthSpinner.setAdapter(monthArrayAdapter);
        final ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1900; i <= thisYear; i++) {
            years.add(Integer.toString(i));
        }
        Collections.reverse(years);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_appearence, years);
        adapter.setDropDownViewResource(R.layout.spinner_appearence);
        fromYearSpinner.setAdapter(adapter);

        fromMonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFromMonth = monthArray[position];
                Log.i("ayaz", "mFromMonth: " + mFromMonth);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fromYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFromYear = years.get(position);
                Log.i("ayaz", "mFromYear: " + mFromYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ///////////////////////////////////////
        Spinner toMonthSpinner = (Spinner) findViewById(R.id.to_month);
        Spinner toYesrSpinner = (Spinner) findViewById(R.id.to_year);
        ArrayAdapter monthArrayAdapter1 = new ArrayAdapter(Work.this, R.layout.spinner_appearence, monthArray);
        monthArrayAdapter1.setDropDownViewResource(R.layout.spinner_appearence);
        toMonthSpinner.setAdapter(monthArrayAdapter1);
        ArrayList<String> years1 = new ArrayList<String>();
        int thisYear1 = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1900; i <= thisYear1; i++) {
            years1.add(Integer.toString(i));
        }
        Collections.reverse(years1);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.spinner_appearence, years);
        adapter1.setDropDownViewResource(R.layout.spinner_appearence);
        toYesrSpinner.setAdapter(adapter1);
        toMonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mToMonth = monthArray[position];
                Log.i("ayaz", "mToMonth: " + mToMonth);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        toYesrSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mToYear = years.get(position);
                Log.i("ayaz", "mToYear: " + mToYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //finish = (Button) findViewById(R.id.bFin);
        //back = (Button) findViewById(R.id.bBack);
        //next = (Button) findViewById(R.id.bNext);
//        present = (CheckBox) findViewById(R.id.cbPresentWork);

        Intent z = getIntent();
        id = z.getStringExtra("id");

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
                        Work.this, android.R.layout.simple_spinner_dropdown_item, nationlist);
                if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    AlertDialog.Builder genderBuilder = new AlertDialog.Builder(Work.this)
                            .setTitle("Select Country")
                            .setAdapter(nationadapter, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    co.setText(nationlist[which]
                                            .toString());
                                    selection = which;
                                    dialog.dismiss();
                                    InputMethodManager imm = (InputMethodManager) Work.this.getSystemService(Service.INPUT_METHOD_SERVICE);
                                    imm.showSoftInput(pi, 0);
                                    pi.requestFocus();


                                }
                            });
                    AlertDialog genderAlert = genderBuilder.create();
                    genderAlert.show();
                    genderAlert.getListView().setSelection(selection);
                }
                return false;
            }
        });
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
                            Work.this, android.R.layout.simple_spinner_dropdown_item, nationlist);
                    AlertDialog.Builder genderBuilder = new AlertDialog.Builder(Work.this)
                            .setTitle("Select Country")
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
                                            InputMethodManager imm = (InputMethodManager) Work.this.getSystemService(Service.INPUT_METHOD_SERVICE);
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
    /*	m_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, m_listItems);*/
        m_adapter = new Custom_profile_adapter(this, toeditFieldlist, "Work");

        /*present.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (present.isChecked()) {
                    to.setTextColor(Color.parseColor("#D3D3D3"));

                    to.setText("");
                    to.setVisibility(View.INVISIBLE);
                } else {
                    to.setVisibility(View.VISIBLE);

                    to.setTextColor(Color.parseColor("#000000"));
                }
            }
        });*/

        if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
            Toast.makeText(Work.this, "No internet connection. Please retry", Toast.LENGTH_SHORT).show();
        } else {
            new Authentication(Work.this, "Work", "").execute();
        }
        //new BackgroundProcess().execute();

        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    final int arg2, long arg3) {
                // TODO Auto-generated method stub
                final String itemstring = m_listItems.get(arg2);

                final Dialog dialog = new Dialog(Work.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.alertdialog_allbutton);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                TextView okBTN = (TextView) dialog.findViewById(R.id.btn_ok);
                TextView stayButton = (TextView) dialog.findViewById(R.id.stay_btn);
                TextView editButton = (TextView) dialog.findViewById(R.id.edit_btn);
                dialog.setTitle("Alert");


                stayButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                okBTN.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
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


                editButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        if (mEditBoxContainer.getVisibility() == View.GONE) {
                            mEditBoxContainer.setVisibility(View.VISIBLE);
                        }
                        PatientHistoryId = patienthistorylist.get(arg2);
                        wo.setText(toeditFieldlist.get(arg2).get("name"));

                        String add1 = toeditFieldlist.get(arg2).get("address");
                              /*  add1=add1.replace("-", "");*/
                        add1 = add1.replace("\n", "");

                        ad.setText(add1.trim());

                        String city = toeditFieldlist.get(arg2).get("city");
                        city = city.replace("-", "");
                        city = city.replace(",", "");
                        city = city.replace("\n", "");

                        ci.setText(city.trim());


                        String state = toeditFieldlist.get(arg2).get("state");
                        state = state.replace("-", "");
                        state = state.replace(",", "");
                        state = state.replace("\n", "");

                        st.setText(state.trim());


                        String pin = toeditFieldlist.get(arg2).get("postaladdress");
                        pin = pin.replace("-", "");
                        pin = pin.replace("\n", "");
                        pin = pin.replace(",", "");
                        pin = pin.replace(" ", "");
                        pi.setText(pin);
                        if (toeditFieldlist.get(arg2).get("to").contains("PRESENT")) {
                            to.setText("");
                        } else {
                            to.setText(toeditFieldlist.get(arg2).get("to"));
                        }
                        from.setText(toeditFieldlist.get(arg2).get("from"));
                        String cont = toeditFieldlist.get(arg2).get("country");
                        cont = cont.replace("-", "");
                        cont = cont.replace(",", "");
                        cont = cont.replace("\n", "");
                        co.setText(cont.trim());
                        checkedit = "edit";
                        add.setText("UPDATE");
                        try {
                            String[] fromdialog = toeditFieldlist.get(arg2).get("from").split("/");
                            year1 = Integer.parseInt(fromdialog[2]);
                            month1 = Integer.parseInt(fromdialog[1]) - 1;
                            day1 = Integer.parseInt(fromdialog[0]);

                            String[] fromdialog1 = toeditFieldlist.get(arg2).get("to").split("/");
                            year2 = Integer.parseInt(fromdialog1[2]);
                            month2 = Integer.parseInt(fromdialog1[1]) - 1;
                            day2 = Integer.parseInt(fromdialog1[0]);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            ex.printStackTrace();
                        }
                        scroll_id.post(new Runnable() {
                            public void run() {
                                // scroll_id.scrollTo(0, scroll_id.getBottom());
                                scroll_id.fullScroll(ScrollView.FOCUS_UP);
                            }
                        });
                               /* m_listItems.remove(arg2);
                                patienthistorylist.remove(arg2);
                                toeditFieldlist.remove(arg2);
                                Utility.setListViewHeightBasedOnChildren(lv);
                                m_adapter.notifyDataSetChanged();
                                m_adapter.notifyDataSetInvalidated();*/
                    }
                });
                dialog.show();

            }

        });
        add.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {


                fromdate = mFromMonth + "/" + mFromYear;
                todate = mToMonth + "/" + mToYear;

                mIsDateValid = true ;
                mIsPresentDateCheck = true ;

                if( mIsFromDateSpinnerVisible == false && mIsToDateSpinnerVisible == false) {        // Both Spinner Not Visible        ;  CASE 1
                    String dateFormat = "dd/MM/yyyy" ;
                    mIsDateValid =  Utils.isDateValid(mFromCompValue, mToCompValue, dateFormat);
                    mIsPresentDateCheck = Utils.isFromDateValid(mFromCompValue,  dateFormat);
                    case1 = true ;
                    Log.e("Rishabh" , " Both Spinner Not  Visible ") ;
                    Log.e ("Rishabh " ," Both Spinner Not visible, start end date check : =  "+ mIsDateValid) ;
                    Log.e ("Rishabh " ," Both Spinner Not visible, present date check : =  "+ mIsPresentDateCheck) ;
                }

                else if ( mIsFromDateSpinnerVisible == true && mIsToDateSpinnerVisible == true) {   // Both Spinner Visible          : CASE 2
                    String dateFormat = "MM/yyyy" ;
                    mIsDateValid =  Utils.isDateValid(fromdate, todate, dateFormat);
                    mIsPresentDateCheck = Utils.isFromDateValid(fromdate,  dateFormat);
                    case2 = true ;
                    Log.e("Rishabh" , "Both Spinner Visible ") ;
                    Log.e ("Rishabh " ," start end date check : =  "+ mIsDateValid) ;
                    Log.e ("Rishabh " ," present date check : =  "+ mIsPresentDateCheck) ;
                }

                else if (mIsFromDateSpinnerVisible == true && mIsToDateSpinnerVisible == false) {    // fromdate spinner Visible and todate spinner not Visible     : CASE 3
                    String dateFormat = "MM/yyyy" ;

                    mIsDateValid =  Utils.isDateValid(fromdate, mTempToMonthYearValue, dateFormat);
                    mIsPresentDateCheck = Utils.isFromDateValid(fromdate,  dateFormat);
                    case3 = true ;
                    Log.e("Rishabh " , " Fromdate Spinner Visible and todate spinner not visible ") ;
                    Log.e("Rishabh " , " start end date check : =  "+ mIsDateValid) ;
                    Log.e("Rishabh " , " present date check : =  "+ mIsPresentDateCheck) ;
                }

                else if (mIsFromDateSpinnerVisible == false && mIsToDateSpinnerVisible == true) {   // fromdate spinner Not Visible and todate spinner Visible   : CASE 4
                    String dateFormat = "MM/yyyy" ;
                    mIsDateValid =  Utils.isDateValid(mTempFromMonthValue, todate, dateFormat);
                    mIsPresentDateCheck = Utils.isFromDateValid(mTempFromMonthValue,  dateFormat);
                    case4 = true ;
                    Log.e("Rishabh " , " Fromdate Spinner Visible and todate spinner not visible ") ;
                    Log.e("Rishabh " , " start end date check : =  "+ mIsDateValid) ;
                    Log.e("Rishabh " , " present date check : =  "+ mIsPresentDateCheck) ;
                }

                /*mIsDateValid = true ;
                mIsPresentDateCheck = true ;
                boolean isValid = true ;

                fromdate = mFromMonth + "/" + mFromYear;
                todate = mToMonth + "/" + mToYear;


                if (mIsNotRemembered == false) {
                    isValid = Utils.isDateValid(mFromCompValue, mToCompValue, "dd/MM/yyyy");
                    if (isValid == false) {
                        mIsDateValid = false ;
                    }
                } else {
                    isValid = Utils.isDateValid(fromdate, todate, "MM/yyyy");
                    if (isValid == false) {
                        mIsDateValid = false ;
                    }
                }


                if (mIsNotRemembered == false) {
                    mIsPresentDateCheck = Utils.isFromDateValid(mFromCompValue,  "dd/MM/yyyy");
                    if (mIsPresentDateCheck == false) {
                        mIsPresentDateCheck = false ;

                    }
                } else if(mIsNotRemembered == true){
                    mIsPresentDateCheck = Utils.isFromDateValid(fromdate,  "MM/yyyy");
                    if (mIsPresentDateCheck == false) {
                        mIsPresentDateCheck = false ;
                    }
                }
*/

                if (ad.getText().toString().equals("") || ci.getText().toString().equals("") || co.getText().toString().equals("") || TextUtils.isEmpty(ad.getEditableText().toString()) || wo.getText().toString().equals("") ) {

                    showAlertMessage("Mandatory fields can not be left Blank !");

                } /*else if (date2 != null && (date1.compareTo(date2) > 0
                        || date1.compareTo(date2) == 0)) {

                    alertDialog = new AlertDialog.Builder(Work.this).create();

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
                    alertDialog = new AlertDialog.Builder(Work.this).create();

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
                    alertDialog = new AlertDialog.Builder(Work.this).create();

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




                }*/



                else if (case1 && (from.getText().toString().equals("") || to.getText().toString().equals(""))) {
                    showAlertMessage("Mandatory fields can not be left Blank !");
                }
                else if(case3 && to.getText().toString().equals("")){
                    showAlertMessage("Mandatory fields can not be left Blank !");
                }
                else if(case4 && from.getText().toString().equals("")){
                    showAlertMessage("Mandatory fields can not be left Blank !");
                }
                else if (mIsDateValid == false) {
                    showAlertMessage("Start date must be smaller than End date.");
                } else if (!pi.getText().toString().equals("") && pi.getText().toString().length() < 4) {
                    showAlertMessage("Postal code should be greater than three digits");

                }else if(mIsPresentDateCheck == false) {
                    showAlertMessage("From date cannot be greater than Present date");
                }
                else {
                    mFinalFromDate = 00 + "/" + mFromMonth + "/" + mFromYear;
                    mFinalToDate = 00 + "/" + mToMonth + "/" + mToYear;

                   /* lv.setAdapter(m_adapter);
                    HashMap hmap=new HashMap<String, String>();
                    hmap.put("name",wo.getText().toString());
                    hmap.put("address", ad.getText().toString());
                    hmap.put("city", ci.getText().toString());
                    hmap.put("state", st.getText().toString());
                    hmap.put("country","");
                    hmap.put("postaladdress",pi.getText().toString());
                    hmap.put("from",from.getText().toString());
                    hmap.put("to", to.getText().toString());
                    toeditFieldlist.add(hmap);*/
                    String input;
                    //if(!to.getText().toString().equals("")) {
                    input = wo.getText().toString() + "\n"
                            + ad.getText().toString() + "\n"
                            + ci.getText().toString() + ","
                            + st.getText().toString() + "\n"
                            + co.getText().toString() + "-"
                            + pi.getText().toString() + "\n"
                            + mFinalFromDate + "-"
                            + mFinalToDate;
                   /* }else{
                        input = wo.getText().toString() + "\n"
                                + ad.getText().toString() + "\n"
                                + ci.getText().toString() + ","
                                + st.getText().toString() +  "\n"+
                                co.getText().toString() + "-"
                                + pi.getText().toString() + "\n"
                                +  mFinalFromDate;
                    }*/
                    if (null != input && input.length() > 0) {

                        if (m_listItems.size() == 0) {
                           /* m_listItems.add(input);
                            patienthistorylist.add("");
                            m_adapter.notifyDataSetChanged();*/

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
                                item = item.replace(",", "");
                                input1 = input1.replace(",", "");
                                item = item.replace("\n", "");
                                input1 = input1.replace("\n", "");
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
                            } else {
                                Toast.makeText(getApplicationContext(), "Duplicate entries not allowed!", Toast.LENGTH_SHORT).show();
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

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker

            if(day1 == 0) {
                day1 = day1 + 1 ;
            }
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
            String formattedMonth = "" + month1;
            String formattedDayOfMonth = "" + day1;

            if (month < 10) {

                formattedMonth = "0" + month;
            }
            if (dayOfMonth < 10) {

                formattedDayOfMonth = "0" + dayOfMonth;
            }

            mTempFromMonthValue = formattedMonth ;
            mTempFromYearValue = year ;
            mTempFromMonthYearValue = mTempFromMonthValue + "/" + mTempFromYearValue ;

            Log.e("Rishabh " ,  " m temp from moth year value ; == " +mTempFromMonthYearValue) ;

            from.setText(formattedDayOfMonth + "/" + formattedMonth + "/" + year);
            mFromCompValue = (formattedDayOfMonth + "/" + formattedMonth + "/" + year);

            //   to.requestFocus();

        }
    }

    class submitchange extends AsyncTask<Void, Void, Void> {
        String Name, address1, cityName, stateName, CountryName, Pincode, fromdate, todate, message, roleValue, designationValue;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            ghoom = new ProgressDialog(Work.this);
            ghoom.setCancelable(false);
            ghoom.setMessage("Loading...");
            ghoom.setIndeterminate(true);
            Name = wo.getText().toString();
            address1 = ad.getText().toString();
            cityName = ci.getText().toString();
            stateName = st.getText().toString();
            CountryName = co.getText().toString();
            Pincode = pi.getText().toString();
            //fromdate=from.getText().toString();
            //todate=to.getText().toString();

            /*if (mIsNotRemembered == false) {
                fromdate = mFromCompValue;
                todate = mToCompValue;
            } else if (mIsNotRemembered == true) {
                fromdate = 00 + "/" + mFromMonth + "/" + mFromYear;
                todate = 00 + "/" + mToMonth + "/" + mToYear;
            }
*/

            if (case1){
                fromdate = mFromCompValue;
                todate = mToCompValue;
            }else if(case2) {
                fromdate = 00 + "/" + mFromMonth + "/" + mFromYear;
                todate = 00 + "/" + mToMonth + "/" + mToYear;
            }else if (case3) {
                fromdate = 00 + "/" + mFromMonth + "/" + mFromYear;
                todate = mToCompValue;
            }else if (case4) {
                fromdate = mFromCompValue;
                todate = 00 + "/" + mToMonth + "/" + mToYear;
            }


/*
            fromdate= 01+"/"+mFromMonth+"/"+mFromYear;
            todate=02+"/"+mToMonth+"/"+mToYear;*/

            roleValue = mRoleEt.getEditableText().toString();
            designationValue = mDesignationEt.getEditableText().toString();

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
                    wo.setText("");
                    ad.setText("");
                    ci.setText("");
                    st.setText("");
                    co.setText("");
                    pi.setText("");
                    from.setText("");
                    add.setText("ADD");
                    checkedit = "";
                    PatientHistoryId = "";
                    year2 = c.get(Calendar.YEAR);
                    month2 = c.get(Calendar.MONTH);
                    day2 = c.get(Calendar.DAY_OF_MONTH);
                    year1 = c.get(Calendar.YEAR);
                    month1 = c.get(Calendar.MONTH);
                    day1 = c.get(Calendar.DAY_OF_MONTH);

                    new BackgroundProcess().execute();
                } else {
                    String data;
                    try {

                        data = receiveData1.getString("d");
                        JSONObject cut = new JSONObject(data);
                        workarray = cut.getJSONArray("Table");
                        if (workarray.length() > 0) {
                            patienthistorylist.clear();
                            toeditFieldlist.clear();
                        }
                        HashMap<String, String> hmap;
                        for (i = 0; i < workarray.length(); i++)

                        {
                            //PatientHistoryId,CategoryId,Name,Address,cityName,stateName,CountryName,Pincode,fromdate,todate;
                            String todatenull = workarray.getJSONObject(i).getString("todate");
                            if (todatenull.equals("null")) {
                                todatenull = "PRESENT";
                            }
                            String postal_null = workarray.getJSONObject(i).getString("Pincode");
                            if (postal_null.equalsIgnoreCase("null")) {
                                postal_null = "";
                            }
                            hmap = new HashMap<String, String>();
                            hmap.put("name", workarray.getJSONObject(i).getString("Name"));
                            hmap.put("address", workarray.getJSONObject(i).getString(
                                    "Address"));
                            hmap.put("city", workarray.getJSONObject(i).getString("cityName"));
                            hmap.put("state", workarray.getJSONObject(i).getString("stateName"));
                            hmap.put("country", workarray.getJSONObject(i).getString("CountryName"));
                            hmap.put("PatientHistoryId", workarray.getJSONObject(i).getString("PatientHistoryId"));
                            hmap.put("postaladdress", postal_null);
                            hmap.put("from", workarray.getJSONObject(i).getString("fromdate"));
                            hmap.put("to", todatenull);
                            hmap.put("role", workarray.getJSONObject(i).optString("Work1").trim());
                            hmap.put("designation", workarray.getJSONObject(i).optString("Work2").trim());
                            toeditFieldlist.add(hmap);
                        }
                        new Helper().sortHashListByDate(toeditFieldlist);
                        for (i = 0; i < toeditFieldlist.size(); i++)

                        {
                            patienthistorylist.add(toeditFieldlist.get(i).get("PatientHistoryId"));
                            String todatenull = toeditFieldlist.get(i).get("to");
                            if (todatenull != "") {
                                m_listItems.add(toeditFieldlist.get(i).get("name")
                                        + "\n"
                                        + toeditFieldlist.get(i).get("address")
                                        + "\n"

                                        + toeditFieldlist.get(i).get("city")
                                        + ", "
                                        + toeditFieldlist.get(i).get("state")
                                        + "\n" + toeditFieldlist.get(i).get("country")
                                        + ", "
                                        + toeditFieldlist.get(i).get("postaladdress")
                                        + "\n"
                                        + toeditFieldlist.get(i).get("from")
                                        + "-"
                                        + todatenull);

                            }
                        }
                        lv.setAdapter(m_adapter);
                        Utility.setListViewHeightBasedOnChildren(lv);
                        m_adapter.notifyDataSetChanged();


                        wo.setText("");
                        ad.setText("");
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
                Toast.makeText(getApplicationContext(), "Your changes have been saved!", Toast.LENGTH_SHORT).show();
                wo.setText("");
                ad.setText("");
                ci.setText("");
                st.setText("");
                co.setText("");
                pi.setText("");
                from.setText("");
                to.setText("");

                checkedit = "";
                PatientHistoryId = "";
                year2 = c.get(Calendar.YEAR);
                month2 = c.get(Calendar.MONTH);
                day2 = c.get(Calendar.DAY_OF_MONTH);
                year1 = c.get(Calendar.YEAR);
                month1 = c.get(Calendar.MONTH);
                day1 = c.get(Calendar.DAY_OF_MONTH);
                new BackgroundProcess().execute();
            } else {
                ghoom.dismiss();
                Toast.makeText(getApplicationContext(), "Your changes could not be saved!", Toast.LENGTH_SHORT).show();
                checkedit = "";
            }


        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            /*countryids.clear();
            countrylist.clear();*/
            sendwork = new JSONObject();
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
                    sendwork.put("Address", address1);
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

                    sendwork.put("profileParameter", "work");
                    sendwork.put("PatientHistoryId", PatientHistoryId);
                    JSONArray jarray = new JSONArray();
                    jarray.put(sendwork);

                    senddata.put("otherDetails", jarray);
                    senddata.put("UserId", id);
                    senddata.put("typeselect", "work");
                    senddata.put("statusType", "edit");

                    sendwork.put("Work1", roleValue);
                    sendwork.put("Work2", designationValue);
                } else {
                    sendwork.put("Name", Name);
                    sendwork.put("Address", address1);
                    sendwork.put("cityName", cityName);
                    sendwork.put("stateName", stateName);
                    sendwork.put("CountryId", country_id);
                    sendwork.put("Pincode", Pincode);
                    sendwork.put("fromdate", fromdate);
                    sendwork.put("todate", todate);
                    sendwork.put("profileParameter", "work");
                    sendwork.put("PatientHistoryId", "");
                    //////////////////////////////
                    sendwork.put("Work1", roleValue);
                    sendwork.put("Work2", designationValue);
                    ///////////////////
                    JSONArray jarray = new JSONArray();
                    jarray.put(sendwork);

                    senddata.put("otherDetails", jarray);
                    senddata.put("UserId", id);
                    senddata.put("typeselect", "work");
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

    class BackgroundProcess extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(Work.this);
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
                    workarray = cut.getJSONArray("Table");
                    if (workarray.length() > 0) {
                        patienthistorylist.clear();
                        toeditFieldlist.clear();
                        m_listItems.clear();
                    }
                    HashMap<String, String> hmap;
                    for (i = 0; i < workarray.length(); i++)

                    {
                        //PatientHistoryId,CategoryId,Name,Address,cityName,stateName,CountryName,Pincode,fromdate,todate;
                        //  patienthistorylist.add(workarray.getJSONObject(i).getString("PatientHistoryId"));
                        String todatenull = workarray.getJSONObject(i).getString("todate").trim();
                        if (todatenull.equals("null")) {
                            todatenull = "PRESENT";
                        }
                        String postal_null = workarray.getJSONObject(i).getString("Pincode").trim();
                        if (postal_null.equalsIgnoreCase("null")) {
                            postal_null = "";
                        }
                        hmap = new HashMap<String, String>();
                        hmap.put("name", workarray.getJSONObject(i).getString("Name").trim());
                        hmap.put("address", workarray.getJSONObject(i).getString(
                                "Address").trim());
                        hmap.put("city", workarray.getJSONObject(i).getString("cityName").trim());
                        hmap.put("state", workarray.getJSONObject(i).getString("stateName").trim());
                        hmap.put("country", workarray.getJSONObject(i).getString("CountryName").trim());
                        hmap.put("PatientHistoryId", workarray.getJSONObject(i).getString("PatientHistoryId").trim());
                        hmap.put("postaladdress", postal_null);
                        hmap.put("from", workarray.getJSONObject(i).getString("fromdate").trim());
                        hmap.put("to", todatenull);
                        hmap.put("role", workarray.getJSONObject(i).optString("Work1").trim());
                        hmap.put("designation", workarray.getJSONObject(i).optString("Work2").trim());
                        toeditFieldlist.add(hmap);
                    }
                    new Helper().sortHashListByDate(toeditFieldlist);
                    for (i = 0; i < toeditFieldlist.size(); i++)

                    {
                        patienthistorylist.add(toeditFieldlist.get(i).get("PatientHistoryId"));
                        String todatenull = toeditFieldlist.get(i).get("to");
                        if (todatenull != "") {
                            m_listItems.add(toeditFieldlist.get(i).get("name")
                                    + "\n"
                                    + toeditFieldlist.get(i).get("address")
                                    + "\n"

                                    + toeditFieldlist.get(i).get("city")
                                    + ", "
                                    + toeditFieldlist.get(i).get("state")
                                    + "\n" + toeditFieldlist.get(i).get("country")
                                    + ", "
                                    + toeditFieldlist.get(i).get("postaladdress")
                                    + "\n"
                                    + toeditFieldlist.get(i).get("from")
                                    + "-"
                                    + todatenull);

                        }
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
                    sendData1.put("profileParameter", "work");
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

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
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

            mTempToMonthValue = formattedMonth ;
            mTempToYearValue = year ;
            mTempToMonthYearValue = mTempToMonthValue + "/" + mTempToYearValue ;

            Log.e("Rishabh " , " to date kaa  month year value ;;;  " +mTempToMonthYearValue) ;

            to.setText(formattedDayOfMonth + "/" + formattedMonth + "/" + year);
            mToCompValue = (formattedDayOfMonth + "/" + formattedMonth + "/" + year);

        }
    }

    public void startBackgroundprocess() {
        new BackgroundProcess().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.home, menu);
        getMenuInflater().inflate(R.menu.weightmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;
            case R.id.action_home:
                finish();

                return true;

            case R.id.add:
                if (mEditBoxContainer.getVisibility() == View.VISIBLE) {
                    mEditBoxContainer.setVisibility(View.GONE);
                } else {
                    mEditBoxContainer.setVisibility(View.VISIBLE);
                }

                //finish();
                //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
