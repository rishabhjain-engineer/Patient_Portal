package com.hs.userportal;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import adapters.Package_filterExpandableAdapter;
import adapters.PackagesAdapter;
import config.StaticHolder;
import me.kaede.tagview.TagView;
import networkmngr.ConnectionDetector;
import utils.AdapterHelper;

/**
 * Created by rahul2 on 10/20/2015.
 */
public class Packages extends ActionBarActivity implements PackagesAdapter.Package_btnListener {

    private ListView packagelist;
    private ArrayList<String> centerArray, categoryName_arry;
    private Package_filterExpandableAdapter filter_adapter;
    private Dialog filterDialog;
    private ArrayList<HashMap<String, String>> Homepackagelist = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> finalOrderedList = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> finalOrderedListAlways = new ArrayList<HashMap<String, String>>();
    private PackagesAdapter adapterpackage;
    // private SegmentedGroup segmented, segment_pckg_instant;
    //-----------------------------------------------------------------------------------------
    private ProgressBar progress_id;
    private String testString = "";
    //filter variables--------------------------
    private ListView category_listId, centerFilter_list, genderFilter_list;
    private ImageButton categoryimg_sign, center_img_sign, gender_img_sign;
    private LinearLayout categorylinear_id, center_linearTxt_id, genderTxtLinearId, select_category, select_gender, select_centre;
    private String[] genderArray;
    private ArrayAdapter category_adapter, diagnostick_adapter, gender_adapter;
    private String sortgenderString, sortDiagnostickString, sortCtegoryString;
    private int categryToggle = 0, centerToggle = 0, genderToggle = 0;
    private RelativeLayout apply_id;
    private Button clear;
    private TextView sort_cat_text, sort_gender_text, sort_centre_text, text_close, warning, sort_gendername_text, sort_centrename_text, sort_category_text;
    private ScrollView scroll_down;
    private boolean filterApplied = false;
    private EditText etSearch;
    private boolean loadingMore = true;
    private String[] default_listvalues = {"LIVER FUNCTION TEST", "KIDNEY FUNCTION TEST", "THYROID PROFILE", "LIPID PROFILE", "CBC", "BLOOD SUGAR FASTING"};
    private String[] popular_packages = {"Diagno Labs", "Thyrocare", "GenX Diagnostics", "Special Packages"};
    private ArrayList<String> selectedItems;
    private LinearLayout botm_defaultcheck_prc;
    private String testname, ContactNo;
    private TagView tagView;
    private ArrayList<String> tagList = new ArrayList<String>();
    private ConnectionDetector con;
    private List<String> testnameList = new ArrayList<String>();
    private Menu menu1;
    private View v;
    private ProgressDialog progressDialog;
    private float myRating = 0;
    // Button upload_btn;
    private SharedPreferences sharedpreferences, sharedPreferences;
    private static final String MyPREFERENCES = "MyPrefs";
    private static final String name = "nameKey";
    private static final String pass = "passwordKey";
    private RequestQueue queue;
    private JsonObjectRequest jr;
    private JSONObject sendData;
    private ArrayList<HashMap<String, String>> packageAlllist;
    private ProgressDialog progress;
    private ArrayList<String> parentItems = new ArrayList<String>();
    private ArrayList<Object> childItems = new ArrayList<Object>();
    //-----------------------------------------------------------------
    private ArrayList<HashMap<String, String>> prioritybaselist;

    protected void onCreate(Bundle savedInstancestate) {
        super.onCreate(savedInstancestate);
        setContentView(R.layout.packages);
        sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ActionBar action = getSupportActionBar();
        action.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1DBBE3")));
        action.setIcon(new ColorDrawable(Color.parseColor("#1DBBE3")));
        action.setDisplayHomeAsUpEnabled(true);

        final Dialog overlay_dialog = new Dialog(Packages.this, R.style.DialogSlideAnim);
        overlay_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // overlay_dialog.setCancelable(false);
        overlay_dialog.setCanceledOnTouchOutside(false);
        overlay_dialog.setContentView(R.layout.overlay_pkg);
        Button btn_continue = (Button) overlay_dialog.findViewById(R.id.btn_continue);
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlay_dialog.dismiss();
            }
        });
        overlay_dialog.show();

      /*  IndividualLabTest.fa.finish();*/

        // Helper hp=new Helper();
        // String location=hp.getLocationFromCoordinates();


        initializeObjct();
        progress = new ProgressDialog(Packages.this);

        progress.setCancelable(false);
        //progress.setTitle("Logging in...");
        progress.setMessage("Please wait...");
        progress.setIndeterminate(true);
        progress.show();
        gettingHomePackages();


    }

    public void initializeObjct() {

        packagelist = (ListView) findViewById(R.id.packagelist);
        centerArray = new ArrayList<String>();
        categoryName_arry = new ArrayList<String>();

        View footerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.loadingmore, null, false);
        progress_id = (ProgressBar) footerView.findViewById(R.id.progress_id);
        packagelist.addFooterView(footerView);
        //segment_pckg_instant = (SegmentedGroup) findViewById(R.id.segment_pckg_instant);

        tagView = (TagView) findViewById(R.id.tagview);
        botm_defaultcheck_prc = (LinearLayout) findViewById(R.id.botm_defaultcheck_prc);
        warning = (TextView) findViewById(R.id.warning);
        progress_id.setVisibility(View.GONE);
        //   upload_btn=(Button) findViewById(R.id.upload_btn);
        con = new ConnectionDetector(Packages.this);
        queue = Volley.newRequestQueue(this);


        botm_defaultcheck_prc = (LinearLayout) findViewById(R.id.botm_defaultcheck_prc);


        prioritybaselist = new ArrayList<HashMap<String, String>>();
        packageAlllist = new ArrayList<HashMap<String, String>>();

        packagelist.setOnScrollListener(new AbsListView.OnScrollListener() {

//useless here, skip!

            @Override

            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
//dumdumdum
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
//what is the bottom iten that is visible
                int lastInScreen = firstVisibleItem + visibleItemCount;
//is the bottom item visible & not loading more already ? Load more !
                if ((lastInScreen == finalOrderedList.size() + 1) && (loadingMore == true)) {
                    loadingMore = false;
                    progress_id.setVisibility(View.VISIBLE);
                    //  gettingAllpackge();
                    Timer timer = new Timer();
                    timer.schedule(new RemindTask(), 2 * 1000);

                }
            }
        });
    }

    class RemindTask extends TimerTask {
        public void run() {


                    //finalOrderedListAlways.clear();
                    //finalOrderedListAlways.addAll(finalOrderedList);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  //  finalOrderedList.clear();//StaticHolder.allPackageslist
                    finalOrderedList.clear();
                    int scrollto=finalOrderedListAlways.size();
                    finalOrderedListAlways.addAll(StaticHolder.allPackageslist);
                    finalOrderedList.addAll(finalOrderedListAlways);



                    adapterpackage = new PackagesAdapter(Packages.this, finalOrderedList);
                    adapterpackage.setpackage_btnListener(Packages.this);
                    adapterpackage.setonPkg_DetailsButtonClickListner(Packages.this);
                    packagelist.setAdapter(adapterpackage);
                   // packagelist.setVerticalScrollbarPosition(scrollto);
                    packagelist.setSelection(scrollto);
                    // smoothScrollToPosition(int position)
                    //  adapterpackage.notifyDataSetChanged();
                  //  finalOrderedListAlways.addAll(finalOrderedList);
                  //  StaticHolder.allPackageslist.clear();
                   // StaticHolder.allPackageslist.addAll(finalOrderedList);
                    StaticHolder.finalOrderedListAlways.clear();
                    StaticHolder.finalOrderedListAlways.addAll(finalOrderedListAlways);
                    progress_id.setVisibility(View.GONE);
                    loadingMore = false;
                }
            });
        }
    }

    public ArrayList<HashMap<String, String>> sorting_package_ArrayByPriority(ArrayList<HashMap<String, String>> prioritybaselist) {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        int totalpriority = prioritybaselist.size();
        int priorityflag = 1;
        HashMap<String, String> hmap;
        do {
            for (int i = 0; i < prioritybaselist.size(); i++) {
                if (String.valueOf(priorityflag).equalsIgnoreCase(prioritybaselist.get(i).get("Priority"))) {
                    hmap = new HashMap<String, String>();
                    hmap.put("PackageId", prioritybaselist.get(i).get("PackageId"));
                    hmap.put("TestName", prioritybaselist.get(i).get("TestName"));
                    hmap.put("CentreName", prioritybaselist.get(i).get("CentreName"));
                    hmap.put("CentreId", prioritybaselist.get(i).get("CentreId"));
                    hmap.put("Logo", prioritybaselist.get(i).get("Logo"));
                    hmap.put("NoofPerameter", prioritybaselist.get(i).get("NoofPerameter"));
                    hmap.put("HomePriority", prioritybaselist.get(i).get("HomePriority"));
                    hmap.put("PackageType", prioritybaselist.get(i).get("PackageType"));
                    hmap.put("PackageName", prioritybaselist.get(i).get("PackageName"));
                    hmap.put("Priority", prioritybaselist.get(i).get("Priority"));
                    hmap.put("TestId", prioritybaselist.get(i).get("TestId"));
                    hmap.put("TestPriority", prioritybaselist.get(i).get("TestPriority"));
                    hmap.put("Price", prioritybaselist.get(i).get("Price"));
                    hmap.put("Discount", prioritybaselist.get(i).get("Discount"));
                    list.add(hmap);

                }
            }
            priorityflag++;
        } while (priorityflag <= totalpriority);
        return list;
    }

    public ArrayList<HashMap<String, String>> sortBy_Centerid_withTestPriority(ArrayList<HashMap<String, String>> prioritybaselist) {
        ArrayList<HashMap<String, String>> orderedlist1 = new ArrayList<HashMap<String, String>>();
        int position = 0, count = 0, flag = 0;

        int i = count;
        for (i = 0; i < prioritybaselist.size(); i++) {
            if (i == prioritybaselist.size() - 1) {
                count = prioritybaselist.size() - 1;
                position = prioritybaselist.size() - 1;
                ArrayList<HashMap<String, String>> sorted = sortByTeastUptoPosition(count, position, prioritybaselist);
                orderedlist1.addAll(sorted);

            } else {
                if (prioritybaselist.get(position).get("PackageName").equalsIgnoreCase(prioritybaselist.get(position + 1).get("PackageName"))) {

                    position++;

                } else {

                    ArrayList<HashMap<String, String>> sorted = sortByTeastUptoPosition(count, position, prioritybaselist);
                    orderedlist1.addAll(sorted);
                    position++;
                    count = position;
                }
            }
        }


        return orderedlist1;
    }

    public ArrayList<HashMap<String, String>> sortByTeastUptoPosition(int startfrom, int upto, ArrayList<HashMap<String, String>> prioritybaselist) {

        ArrayList<HashMap<String, String>> orderedlist = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> hmap;

        int priorityvalue = 1;
        do {
            for (int i = startfrom; i <= upto; i++) {
                if (String.valueOf(priorityvalue).equalsIgnoreCase(prioritybaselist.get(i).get("TestPriority"))) {
                    hmap = new HashMap<String, String>();
                    hmap.put("PackageId", prioritybaselist.get(i).get("PackageId"));
                    hmap.put("TestName", prioritybaselist.get(i).get("TestName"));
                    hmap.put("CentreName", prioritybaselist.get(i).get("CentreName"));
                    hmap.put("CentreId", prioritybaselist.get(i).get("CentreId"));
                    hmap.put("Logo", prioritybaselist.get(i).get("Logo"));
                    hmap.put("NoofPerameter", prioritybaselist.get(i).get("NoofPerameter"));
                    hmap.put("HomePriority", prioritybaselist.get(i).get("HomePriority"));
                    hmap.put("PackageType", prioritybaselist.get(i).get("PackageType"));
                    hmap.put("PackageName", prioritybaselist.get(i).get("PackageName"));
                    hmap.put("Priority", prioritybaselist.get(i).get("Priority"));
                    hmap.put("TestId", prioritybaselist.get(i).get("TestId"));
                    hmap.put("TestPriority", prioritybaselist.get(i).get("TestPriority"));
                    hmap.put("Price", prioritybaselist.get(i).get("Price"));
                    hmap.put("Discount", prioritybaselist.get(i).get("Discount"));
                    orderedlist.add(hmap);

                }
            }
            priorityvalue++;
        } while (priorityvalue <= upto + 1);

        return orderedlist;
    }


    // menu--------------------------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.default_loginshow, menu);
        menu1 = menu;

        v = (View) menu.findItem(R.id.action_search).getActionView();


        etSearch = (EditText) v.findViewById(R.id.etSearchtxt_action);// etSearch is used for searching packages
        String slp1 = "earch packages";
        etSearch.setHint("S" + slp1.toLowerCase());
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        ImageView deletsearch = (ImageView) v.findViewById(R.id.deletsearch);

        deletsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);

                menu1.findItem(R.id.action_search).collapseActionView();

                if (etSearch.getVisibility() == View.VISIBLE) {
                    etSearch.setText("");
                }

            }

        });
        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int start, int before, int count) {
                String text = etSearch.getText().toString().toLowerCase(Locale.getDefault());
                if (adapterpackage != null && text != null) {
                    adapterpackage.filter(text);

                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });


        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);

       /* searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {


                //menu1.getItem(1).setVisible(false);
                getActionBar().setDisplayHomeAsUpEnabled(false);
                getActionBar().setHomeButtonEnabled(false);
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                return true; // KEEP IT TO TRUE OR IT DOESN'T OPEN !!

            }

            @SuppressLint("NewApi")
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do whatever you ne
                // menu1.getItem(1).setVisible(true);
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(v.getWindowToken(), 0);
                getActionBar().setDisplayHomeAsUpEnabled(true);
                getActionBar().setHomeButtonEnabled(true);
                // menu.getItem(1).setVisible(true);
                return true; // OR FALSE IF YOU DIDN'T WANT IT TO CLOSE!
            }
        });*/

        MenuItemCompat.setOnActionExpandListener(
                searchMenuItem, new MenuItemCompat.OnActionExpandListener() {

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        menu1.getItem(1).setVisible(false);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        getSupportActionBar().setHomeButtonEnabled(false);
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                                .toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        menu1.getItem(1).setVisible(true);
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                                .hideSoftInputFromWindow(v.getWindowToken(), 0);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        getSupportActionBar().setHomeButtonEnabled(true);
                        return true;
                    }
                });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                super.onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;

            case R.id.action_search:
                // menu1.getItem(1).setVisible(false);
               getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setHomeButtonEnabled(false);
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                // etSearchtxt_action.requestFocus();
                etSearch.requestFocus();
                // search action
                return true;

            case R.id.action_filter:


                if (filterDialog == null) {
                    filterDialog = new Dialog(Packages.this, R.style.DialogSlideAnim);
                    filterDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    filterDialog.setCancelable(true);
                    filterDialog.setContentView(R.layout.package_filter);
                    categorylinear_id = (LinearLayout) filterDialog.findViewById(R.id.categry_id);
                    center_linearTxt_id = (LinearLayout) filterDialog.findViewById(R.id.centerFilter_Id);
                    genderTxtLinearId = (LinearLayout) filterDialog.findViewById(R.id.genderFilter_Id);
                    categoryimg_sign = (ImageButton) filterDialog.findViewById(R.id.categoryimg_sign);
                    center_img_sign = (ImageButton) filterDialog.findViewById(R.id.center_img_sign);
                    gender_img_sign = (ImageButton) filterDialog.findViewById(R.id.gender_img_sign);
                    category_listId = (ListView) filterDialog.findViewById(R.id.category_listId);
                    centerFilter_list = (ListView) filterDialog.findViewById(R.id.centerFilter_list);
                    genderFilter_list = (ListView) filterDialog.findViewById(R.id.genderFilter_list);
                    apply_id = (RelativeLayout) filterDialog.findViewById(R.id.apply_id);
                    sort_cat_text = (TextView) filterDialog.findViewById(R.id.sort_cat_text);
                    sort_centre_text = (TextView) filterDialog.findViewById(R.id.sort_centre_text);
                    sort_gender_text = (TextView) filterDialog.findViewById(R.id.sort_gender_text);
                    text_close = (TextView) filterDialog.findViewById(R.id.text_close);
                    select_category = (LinearLayout) filterDialog.findViewById(R.id.select_category);
                    select_centre = (LinearLayout) filterDialog.findViewById(R.id.select_centre);
                    select_gender = (LinearLayout) filterDialog.findViewById(R.id.select_gender);
                    sort_category_text = (TextView) filterDialog.findViewById(R.id.sort_category_text);
                    sort_centrename_text = (TextView) filterDialog.findViewById(R.id.sort_centrename_text);
                    sort_gendername_text = (TextView) filterDialog.findViewById(R.id.sort_gendername_text);
                    scroll_down = (ScrollView) filterDialog.findViewById(R.id.scroll_down);
                    scroll_down.post(new Runnable() {
                        @Override
                        public void run() {
                            scroll_down.fullScroll(ScrollView.FOCUS_UP);
                        }
                    });
                    clear = (Button) filterDialog.findViewById(R.id.clear);


                }
                for (int i = 0; i < StaticHolder.finalOrderedListAlways.size(); i++) {
                    String packagename = StaticHolder.finalOrderedListAlways.get(i).get("PackageName");
                    categoryName_arry.add(packagename);
                    centerArray.add(StaticHolder.finalOrderedListAlways.get(i).get("CentreName"));
                }
                categoryName_arry = new ArrayList<String>(new LinkedHashSet<String>(categoryName_arry));
                centerArray = new ArrayList<String>(new LinkedHashSet<String>(centerArray));
                Collections.sort(categoryName_arry, new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return s1.compareToIgnoreCase(s2);
                    }
                });
                Collections.sort(centerArray, new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return s1.compareToIgnoreCase(s2);
                    }
                });
                genderArray = new String[]{"Both", "Female", "Male"};

                //Adaper for categorylist
                category_adapter = new ArrayAdapter<String>(Packages.this,
                        android.R.layout.simple_list_item_single_choice, android.R.id.text1,
                        categoryName_arry) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                        text.setTextSize(14);
                        return view;
                    }
                };
                category_listId.setChoiceMode(ListView.CHOICE_MODE_SINGLE);//sortgenderString, sortDiagnostickString, sortCtegoryString;
                // Assign adapter to ListView
                category_listId.setAdapter(category_adapter);
                AdapterHelper.getListViewSize(category_listId);
                if (sortCtegoryString != null) {
                    int position = 0;
                    for (int i = 0; i < categoryName_arry.size(); i++) {
                        if (categoryName_arry.get(i).equalsIgnoreCase(sortCtegoryString)) {
                            position = i;
                        }
                    }
                    category_listId.setItemChecked(position, true);
                }

                //Adaper for DiagnostickCenter
                diagnostick_adapter = new ArrayAdapter<String>(Packages.this,
                        android.R.layout.simple_list_item_single_choice, android.R.id.text1,
                        centerArray) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                        text.setTextSize(14);
                        return view;
                    }
                };


                centerFilter_list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                // Assign adapter to ListView
                centerFilter_list.setAdapter(diagnostick_adapter);
                AdapterHelper.getListViewSize(centerFilter_list);
                if (sortDiagnostickString != null) {
                    int position = 0;
                    for (int i = 0; i < centerArray.size(); i++) {
                        if (centerArray.get(i).equalsIgnoreCase(sortDiagnostickString)) {
                            position = i;
                        }
                    }
                    centerFilter_list.setItemChecked(position, true);
                }

                //Adaper for Gender
                gender_adapter = new ArrayAdapter<String>(Packages.this,
                        android.R.layout.simple_list_item_single_choice, android.R.id.text1, genderArray) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                        text.setTextSize(14);
                        return view;
                    }
                };
                genderFilter_list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                // Assign adapter to ListView
                genderFilter_list.setAdapter(gender_adapter);
                AdapterHelper.getListViewSize(genderFilter_list);
                if (sortgenderString != null) {
                    int position = 0;
                    for (int i = 0; i < genderArray.length; i++) {
                        if (genderArray[i].equalsIgnoreCase(sortgenderString)) {
                            position = i;
                        }
                    }
                    genderFilter_list.setItemChecked(position, true);
                }
                //centerToggle=0,genderToggle=0;
                categorylinear_id.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (categryToggle == 1) {
                            category_listId.setVisibility(View.VISIBLE);
                            select_category.setVisibility(View.GONE);
                            categoryimg_sign.setBackgroundResource(R.drawable.minus);
                            categryToggle = 0;
                        } else if (categryToggle == 0) {
                            category_listId.setVisibility(View.GONE);
                            if (sortCtegoryString != null) {
                                select_category.setVisibility(View.VISIBLE);
                                sort_category_text.setText(sortCtegoryString);
                            }
                            categoryimg_sign.setBackgroundResource(R.drawable.plus);
                            categryToggle = 1;
                        }
                    }
                });
                sort_cat_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (categryToggle == 1) {
                            category_listId.setVisibility(View.VISIBLE);
                            select_category.setVisibility(View.GONE);
                            categoryimg_sign.setBackgroundResource(R.drawable.minus);
                            categryToggle = 0;
                        } else if (categryToggle == 0) {
                            category_listId.setVisibility(View.GONE);
                            if (sortCtegoryString != null) {
                                select_category.setVisibility(View.VISIBLE);
                                sort_category_text.setText(sortCtegoryString);
                            }
                            categoryimg_sign.setBackgroundResource(R.drawable.plus);
                            categryToggle = 1;
                        }
                    }
                });
                categoryimg_sign.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (categryToggle == 1) {
                            category_listId.setVisibility(View.VISIBLE);
                            select_category.setVisibility(View.GONE);
                            categoryimg_sign.setBackgroundResource(R.drawable.minus);
                            categryToggle = 0;
                        } else if (categryToggle == 0) {
                            category_listId.setVisibility(View.GONE);
                            if (sortCtegoryString != null) {
                                select_category.setVisibility(View.VISIBLE);
                                sort_category_text.setText(sortCtegoryString);
                            }
                            categoryimg_sign.setBackgroundResource(R.drawable.plus);
                            categryToggle = 1;
                        }
                    }
                });
                select_category.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (categryToggle == 1) {
                            category_listId.setVisibility(View.VISIBLE);
                            select_category.setVisibility(View.GONE);
                            categoryimg_sign.setBackgroundResource(R.drawable.minus);
                            categryToggle = 0;
                        } else if (categryToggle == 0) {
                            category_listId.setVisibility(View.GONE);
                            categoryimg_sign.setBackgroundResource(R.drawable.plus);
                            categryToggle = 1;
                        }
                    }
                });
                sort_category_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (categryToggle == 1) {
                            category_listId.setVisibility(View.VISIBLE);
                            select_category.setVisibility(View.GONE);
                            categoryimg_sign.setBackgroundResource(R.drawable.minus);
                            categryToggle = 0;
                        } else if (categryToggle == 0) {
                            category_listId.setVisibility(View.GONE);
                            categoryimg_sign.setBackgroundResource(R.drawable.plus);
                            categryToggle = 1;
                        }
                    }
                });
                center_linearTxt_id.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (centerToggle == 1) {
                            centerFilter_list.setVisibility(View.VISIBLE);
                            select_centre.setVisibility(View.GONE);
                            center_img_sign.setBackgroundResource(R.drawable.minus);
                            centerToggle = 0;
                        } else if (centerToggle == 0) {
                            centerFilter_list.setVisibility(View.GONE);
                            center_img_sign.setBackgroundResource(R.drawable.plus);
                            if (sortDiagnostickString != null) {
                                select_centre.setVisibility(View.VISIBLE);
                                sort_centrename_text.setText(sortDiagnostickString);
                            }
                            centerToggle = 1;
                        }
                    }
                });
                sort_centre_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (centerToggle == 1) {
                            centerFilter_list.setVisibility(View.VISIBLE);
                            select_centre.setVisibility(View.GONE);
                            center_img_sign.setBackgroundResource(R.drawable.minus);
                            centerToggle = 0;
                        } else if (centerToggle == 0) {
                            centerFilter_list.setVisibility(View.GONE);
                            center_img_sign.setBackgroundResource(R.drawable.plus);
                            if (sortDiagnostickString != null) {
                                select_centre.setVisibility(View.VISIBLE);
                                sort_centrename_text.setText(sortDiagnostickString);
                            }
                            centerToggle = 1;
                        }
                    }
                });
                center_img_sign.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (centerToggle == 1) {
                            centerFilter_list.setVisibility(View.VISIBLE);
                            select_centre.setVisibility(View.GONE);
                            center_img_sign.setBackgroundResource(R.drawable.minus);
                            centerToggle = 0;
                        } else if (centerToggle == 0) {
                            centerFilter_list.setVisibility(View.GONE);
                            center_img_sign.setBackgroundResource(R.drawable.plus);
                            if (sortDiagnostickString != null) {
                                select_centre.setVisibility(View.VISIBLE);
                                sort_centrename_text.setText(sortDiagnostickString);
                            }
                            centerToggle = 1;
                        }
                    }
                });
                select_centre.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (centerToggle == 1) {
                            centerFilter_list.setVisibility(View.VISIBLE);
                            select_centre.setVisibility(View.GONE);
                            center_img_sign.setBackgroundResource(R.drawable.minus);
                            centerToggle = 0;
                        } else if (centerToggle == 0) {
                            centerFilter_list.setVisibility(View.GONE);
                            center_img_sign.setBackgroundResource(R.drawable.plus);
                            centerToggle = 1;
                        }
                    }
                });
                sort_centrename_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (centerToggle == 1) {
                            centerFilter_list.setVisibility(View.VISIBLE);
                            select_centre.setVisibility(View.GONE);
                            center_img_sign.setBackgroundResource(R.drawable.minus);
                            centerToggle = 0;
                        } else if (centerToggle == 0) {
                            centerFilter_list.setVisibility(View.GONE);
                            center_img_sign.setBackgroundResource(R.drawable.plus);
                            centerToggle = 1;
                        }
                    }
                });
                genderTxtLinearId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (genderToggle == 1) {
                            genderFilter_list.setVisibility(View.VISIBLE);
                            select_gender.setVisibility(View.GONE);
                            gender_img_sign.setBackgroundResource(R.drawable.minus);
                            scroll_down.post(new Runnable() {
                                @Override
                                public void run() {
                                    scroll_down.fullScroll(ScrollView.FOCUS_DOWN);
                                }
                            });
                            genderToggle = 0;
                        } else if (genderToggle == 0) {
                            genderFilter_list.setVisibility(View.GONE);
                            gender_img_sign.setBackgroundResource(R.drawable.plus);
                            if (sortgenderString != null) {
                                select_gender.setVisibility(View.VISIBLE);
                                sort_gendername_text.setText(sortgenderString);
                            }
                            genderToggle = 1;
                        }
                    }
                });
                sort_gender_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (genderToggle == 1) {
                            genderFilter_list.setVisibility(View.VISIBLE);
                            select_gender.setVisibility(View.GONE);
                            gender_img_sign.setBackgroundResource(R.drawable.minus);
                            scroll_down.post(new Runnable() {
                                @Override
                                public void run() {
                                    scroll_down.fullScroll(ScrollView.FOCUS_DOWN);
                                }
                            });
                            genderToggle = 0;
                        } else if (genderToggle == 0) {
                            genderFilter_list.setVisibility(View.GONE);
                            gender_img_sign.setBackgroundResource(R.drawable.plus);
                            if (sortgenderString != null) {
                                select_gender.setVisibility(View.VISIBLE);
                                sort_gendername_text.setText(sortgenderString);
                            }
                            genderToggle = 1;
                        }
                    }
                });
                gender_img_sign.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (genderToggle == 1) {
                            genderFilter_list.setVisibility(View.VISIBLE);
                            select_gender.setVisibility(View.GONE);
                            gender_img_sign.setBackgroundResource(R.drawable.minus);
                            scroll_down.post(new Runnable() {
                                @Override
                                public void run() {
                                    scroll_down.fullScroll(ScrollView.FOCUS_DOWN);
                                }
                            });
                            genderToggle = 0;
                        } else if (genderToggle == 0) {
                            genderFilter_list.setVisibility(View.GONE);
                            gender_img_sign.setBackgroundResource(R.drawable.plus);
                            if (sortgenderString != null) {
                                select_gender.setVisibility(View.VISIBLE);
                                sort_gendername_text.setText(sortgenderString);
                            }
                            genderToggle = 1;
                        }
                    }
                });
                select_centre.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (genderToggle == 1) {
                            genderFilter_list.setVisibility(View.VISIBLE);
                            select_gender.setVisibility(View.GONE);
                            gender_img_sign.setBackgroundResource(R.drawable.minus);
                            scroll_down.post(new Runnable() {
                                @Override
                                public void run() {
                                    scroll_down.fullScroll(ScrollView.FOCUS_DOWN);
                                }
                            });
                            genderToggle = 0;
                        } else if (genderToggle == 0) {
                            genderFilter_list.setVisibility(View.GONE);
                            gender_img_sign.setBackgroundResource(R.drawable.plus);
                            genderToggle = 1;
                        }
                    }
                });
                sort_gendername_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (genderToggle == 1) {
                            genderFilter_list.setVisibility(View.VISIBLE);
                            select_gender.setVisibility(View.GONE);
                            gender_img_sign.setBackgroundResource(R.drawable.minus);
                            scroll_down.post(new Runnable() {
                                @Override
                                public void run() {
                                    scroll_down.fullScroll(ScrollView.FOCUS_DOWN);
                                }
                            });
                            genderToggle = 0;
                        } else if (genderToggle == 0) {
                            genderFilter_list.setVisibility(View.GONE);
                            gender_img_sign.setBackgroundResource(R.drawable.plus);
                            genderToggle = 1;
                        }
                    }
                });
                apply_id.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new RunAsyncfor_packgFilter().execute();
                    }
                });

                clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        category_listId.clearChoices();
                        genderFilter_list.clearChoices();
                        centerFilter_list.clearChoices();
                        category_adapter.notifyDataSetChanged();
                        diagnostick_adapter.notifyDataSetChanged();
                        gender_adapter.notifyDataSetChanged();
                        finalOrderedList.clear();
                        finalOrderedList.addAll(finalOrderedListAlways);
                        sortgenderString = null;
                        sortDiagnostickString = null;
                        sortCtegoryString = null;
                        select_gender.setVisibility(View.GONE);
                        select_category.setVisibility(View.GONE);
                        select_centre.setVisibility(View.GONE);
                    }
                });

                text_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        filterDialog.dismiss();
                    }
                });

                category_listId.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                        SparseBooleanArray checked = category_listId.getCheckedItemPositions();
                        selectedItems = new ArrayList<String>();
                        for (int i = 0; i < checked.size(); i++) {
                            // Item position in adapter
                            int position1 = checked.keyAt(i);
                            // Add sport if it is checked i.e.) == TRUE!
                            if (checked.valueAt(i)) {
                                sortCtegoryString = categoryName_arry.get(position1);
                                // searchTestNameList(adapter_multi.getItem(position));
                            }
                        }
                    }
                });
                centerFilter_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                        SparseBooleanArray checked = centerFilter_list.getCheckedItemPositions();
                        selectedItems = new ArrayList<String>();
                        for (int i = 0; i < checked.size(); i++) {
                            // Item position in adapter
                            int position1 = checked.keyAt(i);
                            // Add sport if it is checked i.e.) == TRUE!
                            if (checked.valueAt(i)) {
                                sortDiagnostickString = centerArray.get(position1);
                                // searchTestNameList(adapter_multi.getItem(position));
                            }
                        }
                    }
                });
                genderFilter_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                        SparseBooleanArray checked = genderFilter_list.getCheckedItemPositions();
                        selectedItems = new ArrayList<String>();
                        for (int i = 0; i < checked.size(); i++) {
                            // Item position in adapter
                            int position1 = checked.keyAt(i);
                            // Add sport if it is checked i.e.) == TRUE!
                            if (checked.valueAt(i)) {
                                sortgenderString = genderArray[position1];
                                // searchTestNameList(adapter_multi.getItem(position));
                            }
                        }
                    }
                });
                filterDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void showPackgDialog() {
        final Dialog dialog = new Dialog(Packages.this);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before

        dialog.setContentView(R.layout.popular_packages);
        dialog.setTitle("SORT BY PACKAGE");

        // set the custom dialog components - text, image and button
        final ListView packglist = (ListView) dialog.findViewById(R.id.packglist);
        LayoutInflater inflater = getLayoutInflater();


        final ArrayAdapter adapter_multi = new ArrayAdapter<String>(Packages.this,
                android.R.layout.simple_list_item_multiple_choice, android.R.id.text1,
                popular_packages);


        packglist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


        // Assign adapter to ListView
        packglist.setAdapter(adapter_multi);
        //  gettingLocation();
        packglist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                SparseBooleanArray checked = packglist.getCheckedItemPositions();
                selectedItems = new ArrayList<String>();
                for (int i = 0; i < checked.size(); i++) {
                    // Item position in adapter
                    int position1 = checked.keyAt(i);
                    // Add sport if it is checked i.e.) == TRUE!
                    if (checked.valueAt(i)) {
                        selectedItems.add(popular_packages[position1]);
                        // searchTestNameList(adapter_multi.getItem(position));
                    }
                }
               /* if (selectedItems.size() > 0) {
                    botm_defaultcheck_prc.setBackgroundResource(R.drawable.button_checkprices);
                } else {
                    botm_defaultcheck_prc.setBackgroundResource(R.drawable.button_selector_square1);
                }*/
            }
        });


        dialog.show();
    }


    @Override
    public void onPkg_DetailsButtonClickListner(int position, String value) {
        Intent i = new Intent(Packages.this, Pkg_TabActivity.class);
        String mrp_label = String.valueOf(Math.round(Float.parseFloat(finalOrderedList.get(position).get("Price"))));
        String offer_label = String.valueOf(Math.round(Float.parseFloat(finalOrderedList.get(position).get("Discount")) * 100) + " % OFF");
        NumberFormat formatter = new DecimalFormat("#0.00");
        String disc = formatter.format(Float.parseFloat(finalOrderedList.get(position).get("Discount")) * 100);
        int finalprice = Math.round(Float.parseFloat(finalOrderedList.get(position).get("Price"))) - (Math.round((Float.parseFloat(finalOrderedList.get(position).get("Price")) * (Float.parseFloat(finalOrderedList.get(position).get("Discount"))))));
        //  String gh= finalOrderedList.get(position).get("TestName");
        String testname1 = finalOrderedList.get(position).get("TestName").replaceAll(" ", "-");
        String testname= testname1.replaceAll("\\.", "-");
        String labname = finalOrderedList.get(position).get("CentreName").replaceAll(" ", "-");
        String id = finalOrderedList.get(position).get("CentreId");
        String no_param = finalOrderedList.get(position).get("NoofPerameter");
        //   String hj=testname + "-" + labname;
        i.putExtra("testname", testname + "-" + labname);
        i.putExtra("testlabel", testname);
        i.putExtra("offerlabel", disc + "% OFF");
        i.putExtra("mrplabel", mrp_label);
        i.putExtra("finalprice", String.valueOf(finalprice));
        i.putExtra("no_param", no_param);
        i.putExtra("id", id);
        i.putExtra("checktestname", finalOrderedList.get(position).get("TestName"));
        i.putExtra("LabName", finalOrderedList.get(position).get("CentreName"));
        i.putExtra("CentreId", finalOrderedList.get(position).get("CentreId"));
        i.putExtra("PackageName", finalOrderedList.get(position).get("PackageName"));
        int posfinally=-1;
        for(int j=0;j<StaticHolder.finalOrderedListAlways.size();j++){
            if(finalOrderedList.get(position).get("TestId").equalsIgnoreCase(StaticHolder.finalOrderedListAlways.get(j).get("TestId"))){
                posfinally=j;
                break;
            }
        }
        i.putExtra("position",posfinally);

        i.putExtra("promocode", finalOrderedList.get(position).get("PromoCode"));
        i.putExtra("promo_amt", finalOrderedList.get(position).get("Amount"));
        i.putExtra("promo_AmountInPercentage", finalOrderedList.get(position).get("AmountInPercentage"));
        System.out.println(testname + "-" + labname);
        i.putExtra("IndividualTestName", finalOrderedList.get(position).get("TestName"));
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);


    }

    @Override
    public void onButtonClickListner(String value, final int position) {

        Intent intent = new Intent(Packages.this, IndividualLabTest.class);
        try {
            intent.putExtra("TestString", finalOrderedList.get(position).get("TestName"));
            intent.putExtra("PatientId", "");
            intent.putExtra("Area", "");
            intent.putExtra("Rating", "2.0");
            intent.putExtra("promocode", finalOrderedList.get(position).get("PromoCode"));
            intent.putExtra("promo_amt", finalOrderedList.get(position).get("Amount"));
            intent.putExtra("promo_AmountInPercentage", finalOrderedList.get(position).get("AmountInPercentage"));
            intent.putExtra("LabName", finalOrderedList.get(position).get("CentreName"));
            intent.putExtra("CenterId", finalOrderedList.get(position).get("CentreId"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
      /* Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);*/
    }

    protected void onResume() {
        super.onResume();
        if (Helper.authentication_flag == true) {
            finish();
        }
        try {
      /*  if (IndividualLabTest.fa != null) {---------------------------------------------------------------------decomment once individual.java has been fixed-----------------------------------
            IndividualLabTest.fa.finish();
        }*/
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        }
    }



    public void gettingHomePackages() {
        sendData = new JSONObject();
        StaticHolder sttc_holdr = new StaticHolder(Packages.this, StaticHolder.Services_static.HomePackage);
        String url = sttc_holdr.request_Url();
        jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                // System.out.println(response);
                finalOrderedList.clear();
                try {
                    String packagedata = response.getString("d");
                    JSONObject cut = new JSONObject(packagedata);
                    JSONArray packagearray = cut.getJSONArray("Table");
                    HashMap<String, String> hmap;
                    for (int i = 0; i < packagearray.length(); i++) {
                        hmap = new HashMap<String, String>();
                        JSONObject jobj = packagearray.getJSONObject(i);
                        String PackageId = jobj.getString("PackageId");
                        String TestName = jobj.getString("TestName");
                        String CentreName = jobj.getString("CentreName");
                        String CentreId = jobj.getString("CentreId");
                        //  String Promocode = jobj.getString("PromoCode");

                        if (jobj.has("Amount")) {
                            String Amount = jobj.getString("Amount");
                            hmap.put("Amount", Amount);
                        } else {
                            hmap.put("Amount", "");
                        }
                        if (jobj.has("AmountInPercentage")) {
                            String AmountInPercentage = jobj.getString("AmountInPercentage");
                            hmap.put("AmountInPercentage", AmountInPercentage);
                        } else {
                            hmap.put("AmountInPercentage", "");
                        }
                        if (jobj.has("duplicatecount")) {
                            String duplicatecount = jobj.getString("duplicatecount");
                            hmap.put("duplicatecount", duplicatecount);
                        } else {
                            hmap.put("duplicatecount", "");
                        }

                        //  String Logo = jobj.getString("Logo");
                        //  String gender = jobj.getString("Gender");
                        if (jobj.has("Logo")) {
                            String Logo = jobj.getString("Logo");
                            hmap.put("Logo", Logo);
                        } else {
                            hmap.put("Logo", "");
                        }
                        if (jobj.has("PromoCode")) {
                            String Promocode = jobj.getString("PromoCode");
                            hmap.put("PromoCode", Promocode);
                        } else {
                            hmap.put("PromoCode", "");
                        }
                        if (jobj.has("Gender")) {
                            String gender = jobj.getString("Gender");
                            hmap.put("Gender", gender);
                        } else {
                            hmap.put("Gender", "");
                        }
                        if (jobj.has("TestDescription")) {
                            String TestDescription = jobj.getString("TestDescription");
                            hmap.put("TestDescription", TestDescription);
                        } else {
                            hmap.put("TestDescription", "");
                        }
                        String HomePriority = jobj.getString("HomePriority");
                        String NoofPerameter = jobj.getString("NoofPerameter");
                        String PackageType = jobj.getString("PackageType");
                        String PackageName = jobj.getString("PackageName");
                        String Priority = jobj.getString("Priority");
                        String TestId = jobj.getString("TestId");
                        String TestPriority = jobj.getString("TestPriority");
                        String Price = jobj.getString("Price");
                        String Discount = jobj.getString("Discount");
                        hmap.put("PackageId", PackageId);
                        hmap.put("TestName", TestName);
                        hmap.put("CentreName", CentreName);
                        hmap.put("CentreId", CentreId);
                        //  hmap.put("Logo", Logo);
                        hmap.put("Discount", Discount);
                        // hmap.put("Gender", gender);
                        hmap.put("NoofPerameter", NoofPerameter);
                        hmap.put("HomePriority", HomePriority);
                        hmap.put("PackageType", PackageType);
                        hmap.put("PackageName", PackageName);
                        hmap.put("Priority", Priority);
                        hmap.put("TestId", TestId);
                        hmap.put("TestPriority", TestPriority);
                        hmap.put("Price", Price);
                        //  hmap.put("PromoCode", Promocode);

                        //  hmap.put("AmountInPercentage", AmountInPercentage);

                        hmap.put("getOff", "false");

                        Homepackagelist.add(hmap);

                    }

                    // gettingAllpackge();
                    progress.dismiss();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finalOrderedList.addAll(Homepackagelist);
                            adapterpackage = new PackagesAdapter(Packages.this, finalOrderedList);
                            adapterpackage.setpackage_btnListener(Packages.this);
                            adapterpackage.setonPkg_DetailsButtonClickListner(Packages.this);
                            packagelist.setAdapter(adapterpackage);
                            finalOrderedListAlways.addAll(finalOrderedList);

                        }
                    });

                   // finalOrderedListAlways.addAll(StaticHolder.allPackageslist);
                    StaticHolder.finalOrderedListAlways.clear();
                    StaticHolder.finalOrderedListAlways.addAll(Homepackagelist);
                    StaticHolder.finalOrderedListAlways.addAll(StaticHolder.allPackageslist);
                            //StaticHolder.allPackageslist.clear();
                           /// StaticHolder.allPackageslist.addAll(finalOrderedList);



                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "Error while loading packages, Try Later", Toast.LENGTH_SHORT).show();
                progress.dismiss();
                finish();
            }
        }) {

        };
        queue.add(jr);
    }

    class RunAsyncfor_packgFilter extends AsyncTask<Void, Void, String> {
        private ProgressDialog progress;


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            progress = new ProgressDialog(Packages.this);

            progress.setCancelable(false);
            //progress.setTitle("Logging in...");
            progress.setMessage("Please wait...");
            progress.setIndeterminate(true);
            progress.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            if (sortCtegoryString != null && sortDiagnostickString != null && sortgenderString != null) {
                filterApplied = true;
                sortPackageFilter(sortCtegoryString, sortDiagnostickString, sortgenderString);

                //  filterDialog.dismiss();
            } else if (sortCtegoryString != null && sortDiagnostickString != null && sortgenderString == null) {
                filterApplied = true;
                sortPackageFilter(sortCtegoryString, sortDiagnostickString, "");

                // filterDialog.dismiss();
            } else if (sortCtegoryString != null && sortDiagnostickString == null && sortgenderString != null) {
                filterApplied = true;
                sortPackageFilter(sortCtegoryString, "", sortgenderString);

                //  filterDialog.dismiss();
            } else if (sortCtegoryString == null && sortDiagnostickString != null && sortgenderString != null) {
                filterApplied = true;
                sortPackageFilter("", sortDiagnostickString, sortgenderString);

              //  filterDialog.dismiss();
            } else if (sortCtegoryString != null && sortDiagnostickString == null && sortgenderString == null) {
                filterApplied = true;
                sortPackageFilter(sortCtegoryString, "", "");

                // filterDialog.dismiss();
            } else if (sortCtegoryString == null && sortDiagnostickString != null && sortgenderString == null) {
                filterApplied = true;
                sortPackageFilter("", sortDiagnostickString, "");

                 //filterDialog.dismiss();
            } else if (sortCtegoryString == null && sortDiagnostickString == null && sortgenderString != null) {
                filterApplied = true;
                sortPackageFilter("", "", sortgenderString);

                // filterDialog.dismiss();
            } else {
                sortPackageFilter("", "", "");


            }
           filterDialog.dismiss();
            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progress.dismiss();
            filterDialog.dismiss();
            adapterpackage = new PackagesAdapter(Packages.this, finalOrderedList);
            adapterpackage.setpackage_btnListener(Packages.this);
            adapterpackage.setonPkg_DetailsButtonClickListner(Packages.this);
            packagelist.setAdapter(adapterpackage);

            if (finalOrderedList.size() == 0) {
                packagelist.setVisibility(View.GONE);
                warning.setVisibility(View.VISIBLE);
            } else {
                packagelist.setVisibility(View.VISIBLE);
                warning.setVisibility(View.GONE);
            }
        }

    }

    public void sortPackageFilter(String categorystr, String centerstr, String gender) {

        //  finalOrderedListAlways.addAll(finalOrderedList);
        if(filterApplied==true) {
            finalOrderedListAlways.clear();
            finalOrderedListAlways.addAll(StaticHolder.finalOrderedListAlways);
            loadingMore = false;
        }
        if (categorystr != "" && centerstr != "" && gender != "") {
            finalOrderedList.clear();
            finalOrderedList.addAll(finalOrderedListAlways);
            int n = finalOrderedList.size();
            for (int i = n - 1; i >= 0; i--) {
                String CentreName = finalOrderedListAlways.get(i).get("CentreName");
                String PackageName = finalOrderedListAlways.get(i).get("PackageName");
                String Gender = finalOrderedListAlways.get(i).get("Gender");
                if (!(CentreName.equalsIgnoreCase(centerstr) && PackageName.equalsIgnoreCase(categorystr) && Gender.equalsIgnoreCase(gender))) {
                    finalOrderedList.remove(i);

                }

            }

        } else if (categorystr != "" && centerstr != "" && gender == "") {
            finalOrderedList.clear();
            finalOrderedList.addAll(finalOrderedListAlways);
            int n = finalOrderedList.size();
            for (int i = n - 1; i >= 0; i--) {
                String CentreName = finalOrderedListAlways.get(i).get("CentreName");
                String PackageName = finalOrderedListAlways.get(i).get("PackageName");
                //  String Gender=finalOrderedListAlways.get(i).get("Gender");
                if (!(CentreName.equalsIgnoreCase(centerstr) && PackageName.equalsIgnoreCase(categorystr))) {
                    finalOrderedList.remove(i);

                }

            }

        } else if (categorystr != "" && centerstr == "" && gender != "") {
            finalOrderedList.clear();
            finalOrderedList.addAll(finalOrderedListAlways);
            int n = finalOrderedList.size();
            for (int i = n - 1; i >= 0; i--) {
                String PackageName = finalOrderedListAlways.get(i).get("PackageName");
                String Gender = finalOrderedListAlways.get(i).get("Gender");
                if (!(PackageName.equalsIgnoreCase(categorystr) && Gender.equalsIgnoreCase(gender))) {
                    finalOrderedList.remove(i);

                }

            }

        } else if (categorystr == "" && centerstr != "" && gender != "") {
            finalOrderedList.clear();
            finalOrderedList.addAll(finalOrderedListAlways);
            int n = finalOrderedList.size();
            for (int i = n - 1; i >= 0; i--) {
                String CentreName = finalOrderedListAlways.get(i).get("CentreName");
                // String PackageName=finalOrderedListAlways.get(i).get("PackageName");
                String Gender = finalOrderedListAlways.get(i).get("Gender");
                if (!(CentreName.equalsIgnoreCase(centerstr) && Gender.equalsIgnoreCase(gender))) {
                    finalOrderedList.remove(i);

                }

            }

        } else if (categorystr != "" && centerstr == "" && gender == "") {
            finalOrderedList.clear();
            finalOrderedList.addAll(finalOrderedListAlways);
            int n = finalOrderedList.size();
            for (int i = n - 1; i >= 0; i--) {
                String PackageName = finalOrderedListAlways.get(i).get("PackageName");
                if (!(PackageName.equalsIgnoreCase(categorystr))) {
                    finalOrderedList.remove(i);

                }

            }

        } else if (categorystr == "" && centerstr != "" && gender == "") {
            finalOrderedList.clear();
            finalOrderedList.addAll(finalOrderedListAlways);
            int n = finalOrderedList.size();
            for (int i = n - 1; i >= 0; i--) {
                String CentreName = finalOrderedListAlways.get(i).get("CentreName");

                if (!(CentreName.equalsIgnoreCase(centerstr))) {
                    finalOrderedList.remove(i);

                }

            }

        } else if (categorystr == "" && centerstr == "" && gender != "") {
            finalOrderedList.clear();
            finalOrderedList.addAll(finalOrderedListAlways);
            int n = finalOrderedList.size();
            for (int i = n - 1; i >= 0; i--) {

                String Gender = finalOrderedListAlways.get(i).get("Gender");
                if (!(Gender.equalsIgnoreCase(gender))) {
                    finalOrderedList.remove(i);

                }

            }


        } else {

          //  filterDialog.dismiss();

        }


    }
}
