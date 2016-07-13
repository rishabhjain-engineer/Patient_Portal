package com.cloudchowk.patient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import adapters.PackagesAdapter;
import config.StaticHolder;

/**
 * Created by ashish on 10/23/2015.
 */
public class OtherPackages  extends Activity implements PackagesAdapter.Package_btnListener {
    String testname, check_test_name;
    JSONObject sendData, receiveData;
   // Services service;
    private PackagesAdapter adapterpackage;
    JSONArray subArray;
    ProgressDialog progress;
    String testdetails,CentreId;
    ListView packagelist;
    ArrayList<HashMap<String,String>> otherpackglist=new ArrayList<HashMap<String,String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otherpackages);
        packagelist = (ListView) findViewById(R.id.other_pkg_list);
        Intent i = getIntent();
        testname = i.getStringExtra("testname");
        check_test_name = i.getStringExtra("check_test_name");
        CentreId = i.getStringExtra("id");
      //  System.out.println("check_test_name" + check_test_name + "array" + StaticHolder.allPackageslist.size());
      /*  new LoadPolicy().execute();*/
        HashMap<String, String> hmap;
        for(int j=0;j<StaticHolder.finalOrderedListAlways.size();j++){
            if(StaticHolder.finalOrderedListAlways.get(j).get("CentreId").equalsIgnoreCase(CentreId)&&(!StaticHolder.finalOrderedListAlways.get(j).get("TestName").equalsIgnoreCase(check_test_name))){
                hmap=new HashMap<String,String>();
                hmap.put("PackageId", StaticHolder.finalOrderedListAlways.get(j).get("PackageId"));
                hmap.put("TestName", StaticHolder.finalOrderedListAlways.get(j).get("TestName"));
                hmap.put("CentreName", StaticHolder.finalOrderedListAlways.get(j).get("CentreName"));
                hmap.put("CentreId", StaticHolder.finalOrderedListAlways.get(j).get("CentreId"));
                hmap.put("Logo", StaticHolder.finalOrderedListAlways.get(j).get("Logo"));
                hmap.put("NoofPerameter", StaticHolder.finalOrderedListAlways.get(j).get("NoofPerameter"));
                hmap.put("HomePriority", StaticHolder.finalOrderedListAlways.get(j).get("HomePriority"));
                hmap.put("PackageType", StaticHolder.finalOrderedListAlways.get(j).get("PackageType"));
                hmap.put("PackageName", StaticHolder.finalOrderedListAlways.get(j).get("PackageName"));
                hmap.put("Priority", StaticHolder.finalOrderedListAlways.get(j).get("Priority"));
                hmap.put("TestId", StaticHolder.finalOrderedListAlways.get(j).get("TestId"));
                hmap.put("TestPriority", StaticHolder.finalOrderedListAlways.get(j).get("TestPriority"));
                hmap.put("Price", StaticHolder.finalOrderedListAlways.get(j).get("Price"));
                hmap.put("Discount", StaticHolder.finalOrderedListAlways.get(j).get("Discount"));
                hmap.put("PromoCode", StaticHolder.finalOrderedListAlways.get(j).get("PromoCode"));
                hmap.put("Amount", StaticHolder.finalOrderedListAlways.get(j).get("Amount"));
                hmap.put("AmountInPercentage", StaticHolder.finalOrderedListAlways.get(j).get("AmountInPercentage"));
                hmap.put("duplicatecount", StaticHolder.finalOrderedListAlways.get(j).get("duplicatecount"));
                hmap.put("TestDescription", StaticHolder.finalOrderedListAlways.get(j).get("TestDescription"));
                hmap.put("getOff", "false");

                otherpackglist.add(hmap);
            }
        }
        adapterpackage = new PackagesAdapter(this, otherpackglist);
        adapterpackage.setpackage_btnListener(OtherPackages.this);
        adapterpackage.setonPkg_DetailsButtonClickListner(OtherPackages.this);
        packagelist.setAdapter(adapterpackage);
    }
    @Override
    public void onPkg_DetailsButtonClickListner(int position, String value) {

        Intent i = new Intent(OtherPackages.this, Pkg_TabActivity.class);

        String mrp_label = String.valueOf(Math.round(Float.parseFloat(otherpackglist.get(position).get("Price"))));
        NumberFormat formatter = new DecimalFormat("#0.00");
        String offer_label=formatter.format(Float.parseFloat(otherpackglist.get(position).get("Discount")) * 100);
        //   String offer_label = String.valueOf(Math.round(Float.parseFloat(otherpackglist.get(position).get("Discount")) * 100) + " % OFF");
        int finalprice=Math.round(Float.parseFloat(otherpackglist.get(position).get("Price")))-(Math.round((Float.parseFloat(otherpackglist.get(position).get("Price"))*(Float.parseFloat(otherpackglist.get(position).get("Discount"))))));
        String testname1 = otherpackglist.get(position).get("TestName").replaceAll(" ", "-");
        String testname= testname1.replaceAll("\\.", "-");
        String labname = otherpackglist.get(position).get("CentreName").replaceAll(" ", "-");
        String id = otherpackglist.get(position).get("CentreId");
        i.putExtra("testname", testname + "-" + labname);
        i.putExtra("id",id);
        i.putExtra("CenterId",id);
        i.putExtra("checktestname", otherpackglist.get(position).get("TestName"));
        i.putExtra("IndividualTestName", otherpackglist.get(position).get("TestName"));
        i.putExtra("testlabel", testname);
        i.putExtra("offerlabel", offer_label+ "% OFF");
        i.putExtra("mrplabel", mrp_label);
        i.putExtra("finalprice", String.valueOf(finalprice));
        try {
            i.putExtra("promocode", otherpackglist.get(position).get("PromoCode"));
            i.putExtra("promo_amt", otherpackglist.get(position).get("Amount"));
            i.putExtra("promo_AmountInPercentage", otherpackglist.get(position).get("AmountInPercentage"));
        } catch (Exception e) {
            e.printStackTrace();
        }
       int post=-1;
            for(int j=0;j<StaticHolder.finalOrderedListAlways.size();j++){
                if(StaticHolder.finalOrderedListAlways.get(j).get("TestId").equalsIgnoreCase(otherpackglist.get(position).get("TestId"))){
                    post=j;
                    break;
                }

        }
        i.putExtra("position",post);
       // i.putExtra("fromActivity","otherpackages");
        System.out.println(testname + "-" + labname);
        startActivity(i);
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
        finish();
       // }
    }

    @Override
    public void onButtonClickListner(String value, final int position){

        String testname = otherpackglist.get(position).get("TestName");
        String labname = otherpackglist.get(position).get("CentreName");
        String id = otherpackglist.get(position).get("CentreId");
        Intent intent = new Intent(OtherPackages.this, IndividualLabTest.class);
        try {
            intent.putExtra("TestString",testname);
            intent.putExtra("PatientId", "");
            intent.putExtra("Area", "");
            intent.putExtra("Rating","");
            intent.putExtra("LabName", labname);
            intent.putExtra("CenterId",id);
            intent.putExtra("promocode", otherpackglist.get(position).get("PromoCode"));
            intent.putExtra("promo_amt", otherpackglist.get(position).get("Amount"));
            intent.putExtra("promo_AmountInPercentage", otherpackglist.get(position).get("AmountInPercentage"));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
       /* Intent i = new Intent(OtherPackages.this, Booking_Info.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
    }

    public void onBackPressed() {
       getParent().onBackPressed();
      //  overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }

}
