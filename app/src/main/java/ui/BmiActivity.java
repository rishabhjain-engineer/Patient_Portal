package ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.LineChart;
import com.hs.userportal.MiscellaneousTasks;
import com.hs.userportal.R;
import com.hs.userportal.Services;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapters.MyHealthsAdapter;

/**
 * Created by Rishabh on 15/2/17.
 */

public class BmiActivity extends BaseActivity {

    private WebView weight_graphView;
    private ListView weight_listId;
    private Button bsave;
    private String id;
    private TextView wt_heading;
    private JSONObject sendData;
    private String parenthistory_ID;
    private JsonObjectRequest jr;
    private RequestQueue queue;
    private MiscellaneousTasks misc;
    private List<String> chartValues = new ArrayList<String>();
    private List<String> chartValues1 = new ArrayList<String>();
    private List<String> chartDates = new ArrayList<String>();
    private Services service;
    private ProgressDialog progress;
    private MyHealthsAdapter adapter;
    private ArrayList<HashMap<String, String>> weight_contentlists = new ArrayList<HashMap<String, String>>();
    private LineChart linechart;
    private int maxYrange = 0;
    private double mMaxWeight = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);
        setupActionBar();
        mActionBar.setTitle("BMI");

    }
}
