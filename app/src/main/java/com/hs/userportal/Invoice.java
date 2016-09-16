package com.hs.userportal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import config.StaticHolder;

/**
 * Created by ashish on 11/24/2015.
 */
public class Invoice extends ActionBarActivity {

    private WebView invoice_web;
    private ProgressDialog progressDialog;
    JSONObject sendreport_data;
    private JsonObjectRequest jr;
    private RequestQueue queue;
    String imageData = null, path;

    protected void onCreate(Bundle savedBundle) {
        super.onCreate(savedBundle);
        setContentView(R.layout.invoice);

       // String orderid =getIntent().getStringExtra("order_id");

        ActionBar action = getSupportActionBar();
        action.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3cbed8")));
        action.setIcon(new ColorDrawable(Color.parseColor("#3cbed8")));
        action.setDisplayHomeAsUpEnabled(true);
        initUI();
        generateinvoice();
    }

    private void initUI() {
        invoice_web = (WebView) findViewById(R.id.invoice_web);
        queue = Volley.newRequestQueue(this);
    }

    public void generateinvoice() {
        progressDialog = new ProgressDialog(Invoice.this);
        progressDialog.setMessage("Loading....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        try {
            String patientID = PreferenceManager.getDefaultSharedPreferences(this).getString("ke", "");
            sendreport_data = new JSONObject();
            sendreport_data.put("OrderId", getIntent().getStringExtra("order_id").trim());
            sendreport_data.put("UserId", patientID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        StaticHolder sttc_holdr=new StaticHolder(Invoice.this,StaticHolder.Services_static.GenerateInvoice);
        String url=sttc_holdr.request_Url();
        jr = new JsonObjectRequest(Request.Method.POST, url, sendreport_data, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
            try {
                    imageData = response.getString("d");
                    new MakePdfByHtml().execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.invoice, menu);
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
                finish();
                return true;
            case R.id.share_id:

                File file = new File(path);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("application/pdf");
                intent.putExtra(Intent.EXTRA_SUBJECT,
                        "Invoice for "+getIntent().getStringExtra("order_id"));
                Uri uri = Uri.parse("file://" + file);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }


    private class MakePdfByHtml extends AsyncTask<String, String, String> {
        protected String doInBackground(String... urls) {
            createPdf();
            return "a";
        }


        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            invoice_web.getSettings().setJavaScriptEnabled(true);
            invoice_web.getSettings().setLoadWithOverviewMode(true);
            invoice_web.getSettings().setBuiltInZoomControls(true);
            invoice_web.setInitialScale(100);
            invoice_web.getSettings().setUseWideViewPort(true);
            invoice_web.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            invoice_web.setScrollbarFadingEnabled(false);
            invoice_web.loadData(imageData, "text/html; charset=utf-8", "UTF-8");
         /* Uri myUri = Uri.parse(path);
          web.loadUrl("https://docs.google.com/gview?embedded=true&url=" + myUri);*/
           /* File file = new File(path);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);*/
        }
    }

    public void createPdf() {
        try {

            File folder = new File(Environment.getExternalStorageDirectory().toString() + "/Zureka");
            folder.mkdirs();
            String extStorageDirectory = folder.toString();
            File file = new File(extStorageDirectory, "Invoice "+getIntent().getStringExtra("order_id")+".pdf");
            path = file.getAbsolutePath();//Environment.getExternalStorageDirectory()+"/htmlfiles/testpdf1.pdf";
          /* Document document = new Document(PageSize.LETTER);
            PdfWriter pdfWriter = PdfWriter.getInstance
                    (document, new FileOutputStream(path));

            document.open();

            document.addCreationDate();


            XMLWorkerHelper worker = XMLWorkerHelper.getInstance();*/
            String str = "<html><head></head><body>"+imageData+"</body></html>";

            //String str ="";
           /* worker.parseXHtml(pdfWriter, document, new StringReader(str));
            document.close();*/
            Document doc = new Document();


            //We convert the string to a byte array, so we can input it to the XMLWorker instance

            InputStream in = new ByteArrayInputStream(str.getBytes());



            //We write the file to a app accesbile location

            PdfWriter pdf = PdfWriter.getInstance(doc, new FileOutputStream(path));



            //open the document to write

            doc.open();



            //parser and write the file

            XMLWorkerHelper.getInstance().parseXHtml(pdf, doc,in);



            //close things before it gets messey

            doc.close();

            in.close();


        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Helper.authentication_flag==true){
            finish();
        }
    }
}
