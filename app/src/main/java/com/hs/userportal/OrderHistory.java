package com.hs.userportal;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import config.StaticHolder;
import networkmngr.ConnectionDetector;

/**
 * Created by ashish on 10/27/2015.
 */
public class OrderHistory extends ActionBarActivity {

    JSONArray orderarray;
    ConnectionDetector con;
    private ListView order_list;
    JsonObjectRequest jr;
    String patientID;
    RequestQueue queue;
    ProgressDialog progressDialog;
    String scroll_position = null;
    private OrderlistAdapter adapter;
    ArrayList<HashMap<String, String>> order_listarr = new ArrayList<HashMap<String, String>>();
    JSONObject sendData;
    List<OrderList> sortList = new ArrayList<OrderList>();
   // ArrayList<HashMap<String, String>> family = new ArrayList<>();
    private EditText select_member;
    private int check_fill =0;
    private String checkID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderhistory);
        queue = Volley.newRequestQueue(this);

        order_list = (ListView) findViewById(R.id.order_list);
        adapter = new OrderlistAdapter(OrderHistory.this, sortList);
        order_list.setAdapter(adapter);
        select_member = (EditText) findViewById(R.id.select_member);
        select_member.setInputType(InputType.TYPE_NULL);
        ActionBar action = getSupportActionBar();
        action.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1DBBE3")));
        action.setIcon(new ColorDrawable(Color.parseColor("#1DBBE3")));
        action.setDisplayHomeAsUpEnabled(true);
        Intent i = getIntent();
        scroll_position = i.getStringExtra("scroll_position");
       // family = (ArrayList<HashMap<String, String>>) i.getSerializableExtra("family");
        patientID = PreferenceManager.getDefaultSharedPreferences(this).getString("ke", "");
      /*  if(check_fill==0) {
            HashMap<String, String> hmap = new HashMap<>();
            hmap.put("Image", logout.image_parse);
            hmap.put("FirstName", "My Order");
            hmap.put("LastName", "History");
            hmap.put("FamilyMemberId", patientID);
            family.add(hmap);
        }*/
        check_fill=1;

        select_member.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    showdialog();
                }
                return false;
            }
        });
        order_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(OrderHistory.this, OrderDetails.class);
                i.putExtra("OrderId", sortList.get(position).getOrderId());
                i.putExtra("OrderDate", sortList.get(position).getOrderDateTime());
                i.putExtra("LabName", sortList.get(position).getCentreName());
                i.putExtra("Address", sortList.get(position).getBillingAddress());
                try {

                    i.putExtra("GrandTotal", (int) Math.round(Double.parseDouble(sortList.get(position).getOrderBillingAmount())));
                    i.putExtra("SubTotal", (int) Math.round(Double.parseDouble(sortList.get(position).getOrderActualAmount())));
                    i.putExtra("Discount", (int) Math.round(Double.parseDouble(sortList.get(position).getOrderDiscount())));
                    if (!sortList.get(position).getPromoCodeDiscount().equals("null") && sortList.get(position).getPromoCodeDiscount() != null) {
                        double bilingamnt = Double.parseDouble(sortList.get(position).getOrderBillingAmount()) - Double.parseDouble(sortList.get(position).getPromoCodeDiscount());
                        i.putExtra("YourPrice", (int) Math.round(bilingamnt));
                        i.putExtra("promo_codeDiscount", (int) Math.round(Double.parseDouble(sortList.get(position).getPromoCodeDiscount())));

                    } else if (!sortList.get(position).getDiscountInPercentage().equals("null") && sortList.get(position).getDiscountInPercentage() != null) {
                        double bilingamnt = (Double.parseDouble(sortList.get(position).getOrderBillingAmount())) * (1 - ((int) Math.round(Double.parseDouble(sortList.get(position).getDiscountInPercentage()))) / 100);
                        i.putExtra("YourPrice", (int) Math.round(bilingamnt));
                        i.putExtra("promo_codeDiscount", (int) Math.round((Double.parseDouble(sortList.get(position).getOrderBillingAmount())) * (Double.parseDouble(sortList.get(position).getDiscountInPercentage())) / 100));
                    } else {
                        i.putExtra("YourPrice", (int) Math.round(Double.parseDouble(sortList.get(position).getOrderBillingAmount())));
                        i.putExtra("promo_codeDiscount", 0);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                i.putExtra("TestName", sortList.get(position).getTestName());
                i.putExtra("perTextActualPrice_str", sortList.get(position).getStr_peractual_amnt());
                i.putExtra("OrderStatus", sortList.get(position).getOrderStatus());
                i.putExtra("SamplePickupstatus", sortList.get(position).getSamplePickupstatus());
                i.putExtra("scroll_position", String.valueOf(position));
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });
        progressDialog = new ProgressDialog(OrderHistory.this);
        progressDialog.setMessage("Loading....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        getOrderList();
        if (scroll_position != null) {
            timerDelayRunForScroll(900);
        }
    }

    public void timerDelayRunForScroll(long time) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    order_list.setSelection(Integer.parseInt(scroll_position));
                } catch (Exception e) {
                }
            }
        }, time);
    }

    public void getOrderList() {

        try {
            sendData = new JSONObject();
            patientID = PreferenceManager.getDefaultSharedPreferences(this).getString("ke", "");
            Intent i = getIntent();
            String checkid = i.getStringExtra("id");
            if (checkid != null) {
                patientID = checkid;
            }if(checkid==null && checkID!=null){
                patientID = checkID;
            } if (patientID != null) {
                sendData.put("userId", patientID);//   //patientID //"825D9C5A-4CF3-4440-BFE9-810E39CADDC1"
            }
            StaticHolder sttc_holdr = new StaticHolder(OrderHistory.this, StaticHolder.Services_static.GetOrderHistoryDetails);
            String url = sttc_holdr.request_Url();
            jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    String imageData = null;
                    JSONObject obj = null;
                    try {
                        imageData = response.getString("d");

                        JSONObject cut = new JSONObject(imageData);
                        orderarray = cut.getJSONArray("Table");
                        if (orderarray.length() != 0) {
                            for (int i = 0; i < orderarray.length(); i++) {
                                obj = orderarray.getJSONObject(i);
                                HashMap<String, String> hmap = new HashMap<String, String>();
                                hmap.put("BillingAddress", obj.getString("BillingAddress"));//
                                hmap.put("PromoCodeDiscount", obj.getString("PromoCodeDiscount"));
                                hmap.put("CentreName", obj.getString("CentreName"));
                                hmap.put("DiscountInPercentage", obj.getString("DiscountInPercentage"));

                                if (obj.getString("OrderActualAmount") != null)
                                    hmap.put("OrderActualAmount", obj.getString("OrderActualAmount"));
                                if (obj.getString("OrderBillingAmount") != null)
                                    hmap.put("OrderBillingAmount", obj.getString("OrderBillingAmount"));
                                hmap.put("OrderDateTime", obj.getString("OrderDateTime"));

                                if (obj.getString("OrderDiscount") != null)
                                    hmap.put("OrderDiscount", obj.getString("OrderDiscount"));


                                hmap.put("OrderId", obj.getString("OrderId"));
                                hmap.put("TestName", obj.getString("TestName"));
                                hmap.put("TestId", obj.getString("TestId"));
                                //  hmap.put("OrderhistoryId", obj.getString("OrderhistoryId"));
                                hmap.put("UserId", obj.getString("UserId"));
                                if (obj.has("OrderStatus") && obj.getString("OrderStatus") != null) {//missing on live--------------------
                                    hmap.put("OrderStatus", obj.getString("OrderStatus"));
                                } else {
                                    hmap.put("OrderStatus", "");
                                }
                                if (obj.has("SamplePickupstatus")) { //missing on live--------------------
                                    hmap.put("SamplePickupstatus", obj.getString("SamplePickupstatus"));
                                } else {
                                    hmap.put("SamplePickupstatus", "");
                                }
                                order_listarr.add(hmap);

                                   /* OrderList ordr = new OrderList();

                                    ordr.setBillingAddress(obj.getString("BillingAddress"));
                                    ordr.setCentreName(obj.getString("CentreName"));
                                    if(obj.getString("OrderActualAmount")!=null)
                                       ordr.setOrderActualAmount(obj.getString("OrderActualAmount"));
                                    if(obj.getString("OrderBillingAmount")!=null)
                                        ordr.setOrderBillingAmount(obj.getString("OrderBillingAmount"));
                                    ordr.setOrderDateTime(obj.getString("OrderDateTime"));
                                    if(obj.getString("OrderDiscount")!=null)
                                        ordr.setOrderDiscount(obj.getString("OrderDiscount"));

                                    ordr.setOrderId(obj.getString("OrderId"));
                                    ordr.setTestName(obj.getString("TestName"));
                                    ordr.setTestId(obj.getString("TestId"));
                                    ordr.setOrderhistoryId(obj.getString("OrderhistoryId"));
                                    ordr.setUserId(obj.getString("UserId"));
                                    sortList.add(ordr);*/
                            }


                            // boolean flag=false;

//-------------------------------------------------------- combine two tests of same coupon_id or order_id --------------------------------------------------------------------------------------//

                            for (int i = 0; i < order_listarr.size() - 1; i++) {
                                StringBuffer str = new StringBuffer();
                                StringBuffer str_peractual_amnt = new StringBuffer();
                                int j = 1;
                                double orderactualamunt = 0.0;
                                if (order_listarr.get(i).get("OrderActualAmount") != null && (!order_listarr.get(i).get("OrderActualAmount").equalsIgnoreCase("null"))) {
                                    orderactualamunt = Double.parseDouble(order_listarr.get(i).get("OrderActualAmount"));

                                    str.append(j + ". " + order_listarr.get(i).get("TestName"));

                                    Double pp = Double.parseDouble(order_listarr.get(i).get("OrderActualAmount").toString());
                                    str_peractual_amnt.append("₹ " + String.valueOf(pp.intValue()));
                                    for (int k = i + 1; k < order_listarr.size(); k++) {
                                        String hji = order_listarr.get(i).get("OrderId");
                                        String hjk = order_listarr.get(k).get("OrderId");
                                        if (order_listarr.get(i).get("OrderId").equals(order_listarr.get(k).get("OrderId"))) {
                                            j++;

                                            str.append("\n" + j + ". " + order_listarr.get(k).get("TestName"));
                                            Double pp1 = Double.parseDouble(order_listarr.get(k).get("OrderActualAmount").toString());
                                            str_peractual_amnt.append("\n₹ " + String.valueOf(pp1.intValue()));
                                            orderactualamunt = orderactualamunt + Double.parseDouble(order_listarr.get(k).get("OrderActualAmount").toString());
                                            order_listarr.remove(k);
                                            k--;

                                        }
                                    }
                                } else {
                                    str.append(order_listarr.get(i).get("TestName"));
                                }
                                OrderList ordr = new OrderList();

                                ordr.setBillingAddress(order_listarr.get(i).get("BillingAddress"));
                                ordr.setCentreName(order_listarr.get(i).get("CentreName"));
                                ordr.setPromoCodeDiscount(order_listarr.get(i).get("PromoCodeDiscount"));
                                ordr.setDiscountInPercentage(order_listarr.get(i).get("DiscountInPercentage"));
                                ordr.setOrderActualAmount(String.valueOf(Math.round(orderactualamunt)));
                                ordr.setOrderBillingAmount(order_listarr.get(i).get("OrderBillingAmount"));
                                ordr.setOrderDateTime(order_listarr.get(i).get("OrderDateTime"));
                                ordr.setOrderDiscount(order_listarr.get(i).get("OrderDiscount"));
                                ordr.setOrderId(order_listarr.get(i).get("OrderId"));
                                ordr.setTestName(str.toString());
                                ordr.setStr_peractual_amnt(str_peractual_amnt.toString());
                                ordr.setTestId(order_listarr.get(i).get("TestId"));
                                ordr.setUserId(order_listarr.get(i).get("UserId"));
                                ordr.setOrderStatus(order_listarr.get(i).get("OrderStatus"));
                                ordr.setSamplePickupstatus(order_listarr.get(i).get("SamplePickupstatus"));
                                sortList.add(ordr);
                                   /* for(int k=0;k<i;k++) {
                                        if (order_listarr.get(i).get("OrderId").equals(order_listarr.get(k).get("OrderId"))) {
                                            flag=true;
                                            break;

                                        }
                                    }
                                    if(flag==true){
                                      //  str.append(ordr.getOrderId());
                                        break;
                                    }
                                    else if (order_listarr.get(i).get("OrderId").equals(order_listarr.get(i+1).get("OrderId"))) {

                                        str.append(j + ". " + order_listarr.get(i).get("TestName"));

                                        j++;
                                    }else{
                                        OrderList ordr = new OrderList();

                                        ordr.setBillingAddress(order_listarr.get(i).get("BillingAddress"));
                                        ordr.setCentreName(order_listarr.get(i).get("CentreName"));
                                        if(obj.getString("OrderActualAmount")!=null)
                                            ordr.setOrderActualAmount(obj.getString("OrderActualAmount"));
                                        if(obj.getString("OrderBillingAmount")!=null)
                                            ordr.setOrderBillingAmount(obj.getString("OrderBillingAmount"));
                                        ordr.setOrderDateTime(obj.getString("OrderDateTime"));
                                        if(obj.getString("OrderDiscount")!=null)
                                            ordr.setOrderDiscount(obj.getString("OrderDiscount"));

                                        ordr.setOrderId(obj.getString("OrderId"));
                                        ordr.setTestName(obj.getString("TestName"));
                                        ordr.setTestId(obj.getString("TestId"));
                                        ordr.setOrderhistoryId(obj.getString("OrderhistoryId"));
                                        ordr.setUserId(obj.getString("UserId"));
                                        sortList.add(ordr);*/
                                // }


                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                    adapter.notifyDataSetChanged();
                    // }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error loading labs!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        int socketTimeout1 = 30000;
        RetryPolicy policy1 = new DefaultRetryPolicy(socketTimeout1, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jr.setRetryPolicy(policy1);
        queue.add(jr);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ordermenu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                super.onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Helper.authentication_flag == true) {
            finish();
        }
        new Authentication(OrderHistory.this, "Common", "onresume").execute();

    }

    public void showdialog() {
        final Dialog overlay_dialog = new Dialog(OrderHistory.this);
        overlay_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);//SOFT_INPUT_STATE_ALWAYS_HIDDEN
        overlay_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        overlay_dialog.setCanceledOnTouchOutside(true);
        overlay_dialog.setContentView(R.layout.select_member_order);
        ListView list_member = (ListView) overlay_dialog.findViewById(R.id.list_member);
        /*list_member.setAdapter(new Order_family_adapter(OrderHistory.this, family, logout.image_parse));
        list_member.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkID = family.get(position).get("FamilyMemberId");
                overlay_dialog.dismiss();
                getOrderList();
            }
        });*/
        overlay_dialog.show();
    }
}
