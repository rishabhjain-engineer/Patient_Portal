package com.hs.userportal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import config.StaticHolder;

/**
 * Created by rahul2 on 11/20/2015.
 */
public class ViewOrderReport extends ActionBarActivity {
    private TextView lab_nameid, order_date, order_id, biling_address, promocodeid;
    private LinearLayout promolinear;
    private ProgressDialog progressDialog;
    private JsonObjectRequest jr;
    private JSONArray orderHistoryarray;
    private RequestQueue queue;
    private GridView gridView;
    int count;
    JSONObject sendreport_data;
    private ImageAdapter imageAdapter;
    ImageLoader mImageLoader;
    private JSONObject sendData;
    private String testName, OrderFilesUrl, OrderFilesUrlThumb;
    static ArrayList<String> reportthumbImage = new ArrayList<String>();
    static ArrayList<String> report_url = new ArrayList<String>();
    NetworkImageView mNetworkImageView;
    ArrayList<HashMap<String, String>> viewOrderreportList = new ArrayList<HashMap<String, String>>();

    protected void onCreate(Bundle savedBundle) {
        super.onCreate(savedBundle);
        setContentView(R.layout.view_order_report);

        ActionBar action = getSupportActionBar();
        action.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3cbed8")));
        action.setIcon(new ColorDrawable(Color.parseColor("#3cbed8")));
        action.setDisplayHomeAsUpEnabled(true);
        reportthumbImage.clear();
        report_url.clear();
        mImageLoader = MyVolleySingleton.getInstance(ViewOrderReport.this).getImageLoader();
        initObj();
        gettingOrderDetailsData();

    }

    private void initObj() {
        lab_nameid = (TextView) findViewById(R.id.lab_nameid);
        order_date = (TextView) findViewById(R.id.order_date);
        promolinear = (LinearLayout) findViewById(R.id.promolinear);
        order_id = (TextView) findViewById(R.id.order_id);
        biling_address = (TextView) findViewById(R.id.biling_address);
        testName = getIntent().getStringExtra("");
        promocodeid = (TextView) findViewById(R.id.promocodeid);
        gridView = (GridView) findViewById(R.id.gridView);
        queue = Volley.newRequestQueue(this);
        lab_nameid.setText(getIntent().getStringExtra("lab_name"));
        order_date.setText(getIntent().getStringExtra("OrderDate"));
        biling_address.setText(getIntent().getStringExtra("Order_address"));
        order_id.setText(getIntent().getStringExtra("order_id"));

/*       String [] split_thumb=OrderFilesUrlThumb.split(",") ;
        String [] split_url=OrderFilesUrl.split(",") ;
        for(int i=0;i<split_thumb.length;i++){
            reportthumbImage.add(split_thumb[i]);
        }
        for(int i=0;i<split_url.length;i++){
            report_url.add(split_url[i]);
        }
        */
    }

    private void gettingOrderDetailsData() {
        progressDialog = new ProgressDialog(ViewOrderReport.this);
        progressDialog.setMessage("Loading....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        try {
            sendData = new JSONObject();

            sendData.put("OrderId", getIntent().getStringExtra("order_id"));
            //  sendData.put("OrderId", "O1500002782");
            System.out.println(getIntent().getStringExtra("order_id"));
            StaticHolder sttc_holdr=new StaticHolder(ViewOrderReport.this,StaticHolder.Services_static.GetFilesForOrderID);
            String url=sttc_holdr.request_Url();
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
                                HashMap<String, String> hmap = new HashMap<String, String>();
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
                                OrderFilesUrlThumb = obj.getString("OrderFilesUrlThumb");
                                OrderFilesUrl = obj.getString("OrderFilesUrl");
                                if (OrderFilesUrl.equalsIgnoreCase(null) || OrderFilesUrl.equalsIgnoreCase("null")) {
                                    finish();
                                    Toast.makeText(ViewOrderReport.this, "Reports aren’t available yet. Please check back later.", Toast.LENGTH_LONG).show();
                                }

                                //  {"d":"{\"Table\":[{\"UserId\":\"1b7a9845-b7a3-465a-815a-5de6a933b009\",\"OrderId\":\"O1500002782\",\"OrderDateTime\":\"2015-11-19T12:46:11.453\",\"OrderAmount\":9200.0000,\"OrderDiscount\":3473.0000,\"OrderBillingAmount\":5727.0000,\"BillingAddress\":\"B-12, (Laxmi Nagar, New Delhi, India)\",\"PromoCodeId\":\"61b083eb-f41b-4e16-a6ab-db14089386a2\",\"PromoCodeDiscount\":595.0000,\
                                // "DiscountInPercentage\":null,\"OrderFiles\":\"03112015141902Penguins.jpg,03112015141935coiason29july08.pdf\",\"OrderFilesUrl\":\"1b7a9845-b7a3-465a-815a-5de6a933b009/FileVault/O1500002326/Penguins_03112015141902.jpg,1b7a9845-b7a3-465a-815a-5de6a933b009/FileVault/O1500002326/coiason29july08_03112015141935.pdf\",
                                // \"OrderFilesUrlThumb\":\"1b7a9845-b7a3-465a-815a-5de6a933b009/FileVault/O1500002326/Penguins_03112015141902_thumb.jpg,\"}]}"}

                                // hmap.put("OrderhistoryId",OrderhistoryId);
                                hmap.put("UserId", UserId);
                                hmap.put("OrderId", OrderId);
                                hmap.put("OrderAmount", OrderAmount);
                                hmap.put("OrderDateTime", OrderDateTime);
                                hmap.put("OrderFiles", OrderFiles);
                                hmap.put("OrderDiscount", OrderDiscount);
                                hmap.put("OrderBillingAmount", OrderBillingAmount);
                                hmap.put("BillingAddress", BillingAddress);
                                hmap.put("PromoCodeId", PromoCodeId);
                                hmap.put("PromoCodeDiscount", PromoCodeDiscount);
                                hmap.put("DiscountInPercentage", DiscountInPercentage);
                                hmap.put("OrderFilesUrl", OrderFilesUrl);
                                hmap.put("OrderFilesUrlThumb", OrderFilesUrlThumb);


                                viewOrderreportList.add(hmap);


                                // order_id, order_date, lab_name, sample_pic_add, grand_total, test_name
                                //       , amount, sub_total, discount, your_price,promo_amount;
                                imageAdapter = new ImageAdapter();
                                gridView.setAdapter(imageAdapter);
                            }
                            if (PromoCodeId != null && (!PromoCodeId.equals(null))) {
                                promolinear.setVisibility(View.VISIBLE);
                                promocodeid.setText(PromoCodeId);
                            } else {
                                promolinear.setVisibility(View.GONE);
                            }
                        } else {
                            finish();
                            Toast.makeText(ViewOrderReport.this, "Reports aren’t available yet. Please check back later.", Toast.LENGTH_LONG).show();
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


    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public ImageAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            String[] split_thumb = OrderFilesUrlThumb.split(",");
            String[] split_url = OrderFilesUrl.split(",");
            for (int i = 0; i < split_thumb.length; i++) {
                reportthumbImage.add(split_thumb[i]);
            }
            for (int i = 0; i < split_url.length; i++) {
                report_url.add(split_url[i]);
            }
            count = report_url.size();

        }

        public int getCount() {
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.order_file_row, null);
                holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
                holder.checkbox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);

                convertView.setTag(holder);


            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.checkbox.setId(position);
            holder.imageview.setId(position);
           /* holder.checkbox.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    CheckBox cb = (CheckBox) v;
                    int id = cb.getId();
                    if (thumbnailsselection[id]) {
                        cb.setChecked(false);
                        thumbnailsselection[id] = false;
                    } else {
                        cb.setChecked(true);
                        thumbnailsselection[id] = true;
                    }
                }
            });*/

            holder.imageview.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    if (report_url.get(position).contains(".pdf")) {
                        Intent i = new Intent(ViewOrderReport.this, PdfReader.class);
                        i.putExtra("image_url", "https://files.healthscion.com/" + report_url.get(position));
                        i.putExtra("imagename", report_url.get(position));
                        startActivity(i);
                        /* try{
                               // Toast.makeText(getBaseContext(), "Opening PDF... ", Toast.LENGTH_SHORT).show();
					            Intent inte = new Intent(Intent.ACTION_VIEW);
					            inte.setDataAndType(
					                    Uri.parse("https://files.healthscion.com/"+ imageName.get(position)),
					                    "application/pdf");

					            startActivity(inte);
					            }catch(ActivityNotFoundException e){
					                Log.e("Viewer not installed on your device.", e.getMessage());
					            }*/

                    } else {


                        Intent i = new Intent(ViewOrderReport.this, ExpandImage.class);
                        String removeonejpg = report_url.get(position);
					/*if (imageName.get(position).endsWith(".jpg"))
					   {

						removeonejpg = imageName.get(position).substring(0, imageName.get(position).length() - 4);

					   }*/

                        if (removeonejpg != null) {
                            i.putExtra("image", "https://files.healthscion.com/" + report_url.get(position));
                            i.putExtra("imagename", "");
                            startActivity(i);
                        }
                    }

                }
            });
            // holder.imageview.setImageBitmap(thumbnails[position]);

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            mNetworkImageView = (NetworkImageView) holder.imageview;
            if (report_url.get(position).contains(".pdf")) {
            	/*holder.imageview.setBackgroundResource(R.drawable.pdfimg);*/
            	/*mNetworkImageView.setBackgroundDrawable(Filevault.this.getResources().getDrawable(R.drawable.pdfimg));*/
                mNetworkImageView.getLayoutParams().width = width / 2;
                mNetworkImageView.getLayoutParams().height = mNetworkImageView.getLayoutParams().width;
                mNetworkImageView.setDefaultImageResId(R.drawable.pdfimg);
                mNetworkImageView.setAdjustViewBounds(true);
                mNetworkImageView.setImageUrl(null, null);
            } else {
                mNetworkImageView.setBackgroundResource(0);
                mNetworkImageView.getLayoutParams().width = width / 2;
                mNetworkImageView.getLayoutParams().height = mNetworkImageView.getLayoutParams().width;
                mNetworkImageView.setDefaultImageResId(R.drawable.box);
                mNetworkImageView.setErrorImageResId(R.drawable.ic_error);
                mNetworkImageView.setAdjustViewBounds(true);

                mNetworkImageView.setImageUrl("https://files.healthscion.com/" + reportthumbImage.get(position), mImageLoader);
            }

          /*  holder.checkbox.setChecked(thumbnailsselection[position]);*/
            holder.id = position;
            return convertView;
        }
    }

    class ViewHolder {
        ImageView imageview;
        CheckBox checkbox;
        int id;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.orderreport_menu, menu);
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
            case R.id.action_email:

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewOrderReport.this);

                alertDialogBuilder
                        .setMessage("Do you want your Reports to be mailed to you.")
                        .setCancelable(true)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close
                                // current activity
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close
                                // current activity
                                sendreports();
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void sendreports() {
        progressDialog = new ProgressDialog(ViewOrderReport.this);
        progressDialog.setMessage("Loading....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        try {
            String patientID = PreferenceManager.getDefaultSharedPreferences(this).getString("ke", "");
            sendreport_data = new JSONObject();
            sendreport_data.put("OrderId", getIntent().getStringExtra("order_id").trim());
            sendreport_data.put("EmailId", logout.emailid);
            sendreport_data.put("UserId", patientID);
            System.out.println(sendreport_data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(sendreport_data);
        StaticHolder sttc_holdr=new StaticHolder(ViewOrderReport.this,StaticHolder.Services_static.SendAllReportToUser);
        String url=sttc_holdr.request_Url();
        jr = new JsonObjectRequest(Request.Method.POST,url, sendreport_data, new Response.Listener<JSONObject>() {

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
                    if (imageData.equalsIgnoreCase("ReportSend")) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewOrderReport.this);

                        alertDialogBuilder
                                .setMessage("Reports Sent on Email Successfully.")
                                .setCancelable(true)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, close
                                        // current activity
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();

                    } else {

                    }
                    // JSONObject cut = new JSONObject(imageData);

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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reportthumbImage.clear();
        report_url.clear();
        finish();
    }
}
