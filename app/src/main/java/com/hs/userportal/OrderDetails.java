package com.hs.userportal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import org.json.JSONException;
import org.json.JSONObject;

import config.StaticHolder;

/**
 * Created by ashish on 10/28/2015.
 */
public class OrderDetails extends ActionBarActivity {

    private String OrderId, OrderDate, LabName, address,
            TestName, OrderStatus, SamplePickupstatus, scroll_position;
    private int GrandTotal, SubTotal, Discount, YourPrice, promodiscount;
    private TextView order_id, order_date, lab_name, sample_pic_add, grand_total, test_name, amount,
            sub_total, discount, your_price, promo_amount;
    private RelativeLayout promo_lay;
    private LinearLayout viewReportLinear_id, resend_confirmation, invoice;
    private ProgressDialog progressDialog;
    private JsonObjectRequest jr;
    JSONObject confirm_data;
    private JSONArray orderHistoryarray;
    private RequestQueue queue;
    private Button cancel_btn;
    private JSONObject sendData;
    int checknull = 0;
    String type, msg;
    SharedPreferences sharedPreferences;
    String patientId, cancel_reason;
   /* private ArrayList<HashMap<String, String>> cancellist = new ArrayList<HashMap<String, String>>();*/

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderdetails);
        setupActionBar();
        getExtra_data();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        initUI();
        queue = Volley.newRequestQueue(this);
        order_id.setText(OrderId);
        if (!OrderDate.equalsIgnoreCase("null")) {
            order_date.setText(OrderDate);
        } else {
            order_date.setText("-");
        }

        lab_name.setText(LabName);
        if (!address.equalsIgnoreCase("null") && (!address.equalsIgnoreCase(""))) {
            sample_pic_add.setText(address);
        } else {
            sample_pic_add.setText("-");
        }

        test_name.setText(TestName);
        amount.setText(getIntent().getStringExtra("perTextActualPrice_str"));
        if (!String.valueOf(YourPrice).equalsIgnoreCase("0")) {
            grand_total.setText("₹ " + String.valueOf(YourPrice));
        } else {
            grand_total.setText("₹ 0");

        }
        if (!String.valueOf(SubTotal).equalsIgnoreCase("0")) {
            sub_total.setText("₹ " + String.valueOf(SubTotal));
        } else {
            sub_total.setText("-");
        }
        if (!String.valueOf(Discount).equalsIgnoreCase("0")) {
            discount.setText("₹ " + String.valueOf(Discount));
        } else {
            discount.setText("-");
        }
        if (!String.valueOf(YourPrice).equalsIgnoreCase("0")) {
            your_price.setText("₹ " + String.valueOf(YourPrice));
        } else {
            your_price.setText("₹ 0");
            //  checknull=1;
        }

        if (OrderStatus.equalsIgnoreCase("0")) {
            cancel_btn.setBackgroundColor(Color.parseColor("#ED2727"));
            cancel_btn.setText("ORDER CANCELLED");
            cancel_btn.setEnabled(false);
            viewReportLinear_id.setEnabled(false);
            resend_confirmation.setEnabled(false);
        } else if (OrderStatus.equalsIgnoreCase("1") && SamplePickupstatus.equalsIgnoreCase("0")) {
            cancel_btn.setBackgroundColor(Color.parseColor("#ED2727"));
            cancel_btn.setText("ORDER CANCELLED");
            cancel_btn.setEnabled(false);
            viewReportLinear_id.setEnabled(false);
            resend_confirmation.setEnabled(false);
        } else if (OrderStatus.equalsIgnoreCase("1") && SamplePickupstatus.equalsIgnoreCase("1")) {
            cancel_btn.setBackgroundColor(Color.parseColor("#65A366"));
            cancel_btn.setText("ORDER COMPLETED");
            cancel_btn.setEnabled(false);
        }

        //your_price.setText("₹ " + String.valueOf(YourPrice));
        if (promodiscount != 0) {
            promo_lay.setVisibility(View.VISIBLE);
            promo_amount.setText("₹ " + String.valueOf(promodiscount));
        }

        viewReportLinear_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//if(checknull==0)
                gettingOrderDetailsData();

            }
        });

        resend_confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checknull == 0)
                    showSignInSignUp();
            }
        });

        invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checknull == 0) {
                    Intent in = new Intent(OrderDetails.this, Invoice.class);
                    in.putExtra("order_id", OrderId);
                    startActivity(in);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderDetails.this);

                alertDialogBuilder
                        .setMessage("Are you sure you want to cancel this Order.")
                        .setCancelable(true)
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();
                                    }
                                }
                        )
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                cancel_order();
                            }
                        });


                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);

                textView.setTextSize(16);
                textView.setGravity(Gravity.LEFT);
                Button btnPositive = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
                btnPositive.setTextSize(17);
                Button btnNegative = alertDialog.getButton(Dialog.BUTTON_NEGATIVE);
                btnNegative.setTextSize(17);*/


              /*  HashMap<String, String> hmap;
                for (int i = 0; i < OrderHistory.order_listarr.size(); i++) {
                    if (OrderId.equalsIgnoreCase(OrderHistory.order_listarr.get(i).get("OrderId"))) {
                        if (OrderHistory.order_listarr.get(i).get("OrderStatus").equalsIgnoreCase("1")
                                && (!OrderHistory.order_listarr.get(i).get("SamplePickupstatus").equalsIgnoreCase("0"))) {
                            hmap = new HashMap<String, String>();
                            hmap.put("TestName", OrderHistory.order_listarr.get(i).get("TestName"));
                            hmap.put("OrderStatus", OrderHistory.order_listarr.get(i).get("OrderStatus"));
                            hmap.put("SamplePickupstatus", OrderHistory.order_listarr.get(i).get("SamplePickupstatus"));
                            cancellist.add(hmap);
                        } else if (OrderHistory.order_listarr.get(i).get("OrderStatus").equalsIgnoreCase("1")
                                && (!OrderHistory.order_listarr.get(i).get("SamplePickupstatus").equalsIgnoreCase("1"))) {

                        }

                    }
                }*/
                // ArrayList<HashMap<String, String>> chklist= cancellist;

                final Dialog reason_dialog = new Dialog(OrderDetails.this);
                reason_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                //setting custom layout to dialog
                reason_dialog.setContentView(R.layout.cancelorder_dialog);
                Button proceed_btn = (Button) reason_dialog.findViewById(R.id.proceed_btn);
                final RadioGroup radio_grp = (RadioGroup) reason_dialog.findViewById(R.id.radio_grp);
                radio_grp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton rb = (RadioButton) group.findViewById(checkedId);
                        if (null != rb && checkedId > -1) {
                            cancel_reason = rb.getText().toString();
                        }
                    }
                });

                proceed_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (cancel_reason != null) {
                            /*Toast.makeText(OrderDetails.this, cancel_reason, Toast.LENGTH_SHORT).show();*/
                            cancel_order();
                            reason_dialog.dismiss();
                        } else {
                            Toast.makeText(OrderDetails.this,"Please Select atleast one reason for Cancelling the order.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                reason_dialog.show();


            }
        });

    }

    private void cancel_order() {
        sendData = new JSONObject();
        final ProgressDialog progressdialog = new ProgressDialog(OrderDetails.this);
        progressdialog.setMessage("Cancelling....");
        progressdialog.setCancelable(false);
        progressdialog.setCanceledOnTouchOutside(false);
        progressdialog.show();
        try {
            patientId = sharedPreferences.getString("ke", "");
            sendData.put("UserId", patientId);
            sendData.put("OrderId", OrderId);
            sendData.put("Reason", cancel_reason.trim());
            StaticHolder holder = new StaticHolder(OrderDetails.this, StaticHolder.Services_static.CancelOrder);
            String url = holder.request_Url();
            jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String server_response = response.getString("d");
                        if (server_response.equalsIgnoreCase("Success")) {
                            msg = "Your Order No. " + OrderId + " has been cancelled.";
                            cancel_btn.setBackgroundColor(Color.parseColor("#ED2727"));
                            cancel_btn.setText("ORDER CANCELLED");
                            viewReportLinear_id.setEnabled(false);
                            resend_confirmation.setEnabled(false);
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderDetails.this);
                            alertDialogBuilder
                                    .setMessage(msg)
                                    .setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();

                                }
                            });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                            textView.setTextSize(16);
                            textView.setGravity(Gravity.LEFT);
                            Button btnPositive = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
                            btnPositive.setTextSize(17);
                        } else {
                            msg = "Please Try Again !";
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderDetails.this);
                            alertDialogBuilder
                                    .setMessage(msg)
                                    .setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                            textView.setTextSize(16);
                            textView.setGravity(Gravity.LEFT);
                            Button btnPositive = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
                            btnPositive.setTextSize(17);
                        }
                    } catch (JSONException je) {
                        je.printStackTrace();
                    }
                    progressdialog.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressdialog.dismiss();
                    Toast.makeText(getBaseContext(), "Some Error Occured.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException je) {
            je.printStackTrace();
        }
        int socketTimeout1 = 30000;
        RetryPolicy policy1 = new DefaultRetryPolicy(socketTimeout1, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jr.setRetryPolicy(policy1);
        queue.add(jr);
    }

    public void showSignInSignUp() {
        // custom dialog
        final Dialog dialog = new Dialog(OrderDetails.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before

        dialog.setContentView(R.layout.resend_popup);
        // dialog.setTitle("Title...");

        // set the custom dialog components - text, image and button
        RelativeLayout signIn = (RelativeLayout) dialog.findViewById(R.id.relSignIn);
        RelativeLayout signUp = (RelativeLayout) dialog.findViewById(R.id.relSignUp);
        RelativeLayout cancel = (RelativeLayout) dialog.findViewById(R.id.relCancel);
        TextView tvSignIn = (TextView) dialog.findViewById(R.id.tvSignIn);
        TextView tvSignUp = (TextView) dialog.findViewById(R.id.tvSignUp);
        TextView title_dialog = (TextView) dialog.findViewById(R.id.title_dialog);
        tvSignIn.setText(Helper.resend_sms);
        tvSignUp.setText(Helper.resend_email);

        // if button is clicked, close the custom dialog
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                type = "SMS";
                resend_confirmation();
                // finish();
                //showLoginDialog();
                // Intent main = new Intent(LocationClass.this,
                // MainActivity.class);
                // startActivity(main);
             /*   Intent main = new Intent(OrderDetails.this, MainActivity.class);
                main.putExtra("fromActivity", "signinMaplab");

                startActivity(main);*/

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                type = "Email";

                resend_confirmation();

                dialog.dismiss();
                /*Intent i = new Intent(OrderDetails.this, Register.class);
                i.putExtra("FromLocation", true);
                startActivity(i);
*/
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void setupActionBar() {
        ActionBar action = getSupportActionBar();
        action.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3cbed8")));
        action.setIcon(new ColorDrawable(Color.parseColor("#3cbed8")));
        action.setDisplayHomeAsUpEnabled(true);
    }

    private void getExtra_data() {
        Intent i = getIntent();
        try {
            OrderId = i.getStringExtra("OrderId");
            OrderDate = i.getStringExtra("OrderDate");
            LabName = i.getStringExtra("LabName");
            address = i.getStringExtra("Address");
            GrandTotal = i.getIntExtra("GrandTotal", 0);
            SubTotal = i.getIntExtra("SubTotal", 0);
            Discount = i.getIntExtra("Discount", 0);
            YourPrice = i.getIntExtra("YourPrice", 0);
            TestName = i.getStringExtra("TestName");
            promodiscount = i.getIntExtra("promo_codeDiscount", 0);
            OrderStatus = i.getStringExtra("OrderStatus");
            SamplePickupstatus = i.getStringExtra("SamplePickupstatus");
            scroll_position = i.getStringExtra("scroll_position");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initUI() {
        promo_lay = (RelativeLayout) findViewById(R.id.promo_lay);
        order_id = (TextView) findViewById(R.id.order_id);
        promo_amount = (TextView) findViewById(R.id.promo_amount);
        order_date = (TextView) findViewById(R.id.order_date);
        lab_name = (TextView) findViewById(R.id.lab_name);
        sample_pic_add = (TextView) findViewById(R.id.sample_pic_add);
        grand_total = (TextView) findViewById(R.id.grand_total);
        // test_no = (TextView) findViewById(R.id.test_no);
        test_name = (TextView) findViewById(R.id.test_name);
        amount = (TextView) findViewById(R.id.amount);
        sub_total = (TextView) findViewById(R.id.sub_total);
        discount = (TextView) findViewById(R.id.discount);
        your_price = (TextView) findViewById(R.id.your_price);
        queue = Volley.newRequestQueue(this);
        viewReportLinear_id = (LinearLayout) findViewById(R.id.viewReportLinear_id);
        resend_confirmation = (LinearLayout) findViewById(R.id.resend_confirmation);
        invoice = (LinearLayout) findViewById(R.id.invoice);
        cancel_btn = (Button) findViewById(R.id.cancel_btn);
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


              /*  super.onBackPressed();*/
                onBackPressed();
               /* overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);*/
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
       /* Intent i = new Intent(OrderDetails.this, OrderHistory.class);
        i.putExtra("scroll_position", scroll_position);
        startActivity(i);*/
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    public void resend_confirmation() {

        progressDialog = new ProgressDialog(OrderDetails.this);
        progressDialog.setMessage("Sending Confirmation via " + type + "....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        try {
            String patientID = PreferenceManager.getDefaultSharedPreferences(this).getString("ke", "");
            confirm_data = new JSONObject();
            confirm_data.put("type", type.trim());
            confirm_data.put("orderid", OrderId.trim());
            confirm_data.put("UserId", patientID);
            System.out.println(confirm_data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(confirm_data);
        StaticHolder sttc_holdr = new StaticHolder(OrderDetails.this, StaticHolder.Services_static.IsContactNoExists);
        String url = sttc_holdr.request_Url();
        jr = new JsonObjectRequest(Request.Method.POST, url, confirm_data, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                  /*  if (response == null || !con.isConnectingToInternet()) {
                        Toast.makeText(getBaseContext(), "Connection Failed !", Toast.LENGTH_SHORT).show();
                    } else {*/
                System.out.println(response);


                String imageData = null;
                try {
                    imageData = response.getString("d");
                    System.out.println(imageData);
                    if (imageData.equalsIgnoreCase("success")) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderDetails.this);
                        alertDialogBuilder
                                .setMessage(type + " Sent Successfully .")
                                .setCancelable(true)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, close
                                        // current activity
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderDetails.this);
                        alertDialogBuilder
                                .setMessage(response.getString("d"))
                                .setCancelable(true)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, close
                                        // current activity
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();

                // }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Please Try Again !", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        int socketTimeout1 = 30000;
        RetryPolicy policy1 = new DefaultRetryPolicy(socketTimeout1, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jr.setRetryPolicy(policy1);
        queue.add(jr);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Helper.authentication_flag == true) {
            finish();
        }
        new Authentication(OrderDetails.this, "Common", "onresume").execute();

    }

    private void gettingOrderDetailsData() {
        progressDialog = new ProgressDialog(OrderDetails.this);
        progressDialog.setMessage("Loading ...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        try {
            sendData = new JSONObject();

            sendData.put("OrderId", OrderId);
            //  sendData.put("OrderId", "O1500002782");
            //  System.out.println(getIntent().getStringExtra("order_id"));
            StaticHolder sttc_holdr = new StaticHolder(OrderDetails.this, StaticHolder.Services_static.GetFilesForOrderID);
            String url = sttc_holdr.request_Url();
            jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    progressDialog.dismiss();

                    System.out.println(response);

                    String imageData = null;
                    try {
                        imageData = response.getString("d");
                        String PromoCodeId = null;
                        JSONObject cut = new JSONObject(imageData);
                        orderHistoryarray = cut.getJSONArray("Table");
                        if (orderHistoryarray.length() != 0) {
                            for (int i = 0; i < orderHistoryarray.length(); i++) {
                                JSONObject obj = orderHistoryarray.getJSONObject(i);
                                // String OrderhistoryId = obj.getString("OrderhistoryId");
                                String UserId = obj.getString("UserId");
                                String OrderId = obj.getString("OrderId");
                                // String TestId = obj.getString("TestId");
                                String OrderDateTime = obj.getString("OrderDateTime");
                                String OrderAmount = obj.getString("OrderAmount");
                                String OrderDiscount = obj.getString("OrderDiscount");
                                String OrderBillingAmount = obj.getString("OrderBillingAmount");
                                String BillingAddress = obj.getString("BillingAddress");
                                String PromoCodeDiscount = obj.getString("PromoCodeDiscount");
                                String DiscountInPercentage = obj.getString("DiscountInPercentage");
                                String OrderFiles = obj.getString("OrderFiles");
                                String OrderFilesUrlThumb = obj.getString("OrderFilesUrlThumb");
                                String OrderFilesUrl = obj.getString("OrderFilesUrl");
                                if (OrderFilesUrl.equalsIgnoreCase(null) || OrderFilesUrl.equalsIgnoreCase("null")) {
                                    Toast.makeText(OrderDetails.this, "Reports aren’t available yet. Please check back later.", Toast.LENGTH_LONG).show();
                                } else {
                                    Intent in = new Intent(OrderDetails.this, ViewOrderReport.class);
                                    in.putExtra("order_id", OrderId);
                                    in.putExtra("lab_name", LabName);
                                    in.putExtra("OrderDate", OrderDate);
                                    in.putExtra("Order_address", address);
                                    in.putExtra("TestName", TestName);
                                    startActivity(in);
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                }

                                //  {"d":"{\"Table\":[{\"UserId\":\"1b7a9845-b7a3-465a-815a-5de6a933b009\",\"OrderId\":\"O1500002782\",\"OrderDateTime\":\"2015-11-19T12:46:11.453\",\"OrderAmount\":9200.0000,\"OrderDiscount\":3473.0000,\"OrderBillingAmount\":5727.0000,\"BillingAddress\":\"B-12, (Laxmi Nagar, New Delhi, India)\",\"PromoCodeId\":\"61b083eb-f41b-4e16-a6ab-db14089386a2\",\"PromoCodeDiscount\":595.0000,\
                                // "DiscountInPercentage\":null,\"OrderFiles\":\"03112015141902Penguins.jpg,03112015141935coiason29july08.pdf\",\"OrderFilesUrl\":\"1b7a9845-b7a3-465a-815a-5de6a933b009/FileVault/O1500002326/Penguins_03112015141902.jpg,1b7a9845-b7a3-465a-815a-5de6a933b009/FileVault/O1500002326/coiason29july08_03112015141935.pdf\",
                                // \"OrderFilesUrlThumb\":\"1b7a9845-b7a3-465a-815a-5de6a933b009/FileVault/O1500002326/Penguins_03112015141902_thumb.jpg,\"}]}"}

                                // hmap.put("OrderhistoryId",OrderhistoryId);
                            }
                        } else {
                            Toast.makeText(OrderDetails.this, "Reports aren’t available yet. Please check back late.", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                    // }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Some error occurred .Please Try Again !", Toast.LENGTH_SHORT).show();
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
}
