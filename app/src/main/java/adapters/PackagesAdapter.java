package adapters;

/**
 * Created by ashish on 10/20/2015.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.hs.userportal.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class PackagesAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<HashMap<String, String>> SortList;
    static ArrayList<HashMap<String, String>> privatearray;
    ViewHolder holder = null;

    Animation leftAnimation;
    Animation rightAnimation;

    public PackagesAdapter(Activity activity, ArrayList<HashMap<String, String>> SortList) {
        this.activity = activity;
        this.SortList = SortList;
        privatearray = new ArrayList<HashMap<String, String>>();
        privatearray.addAll(SortList);
        leftAnimation = AnimationUtils.loadAnimation(activity, R.anim.reverse_left);
        rightAnimation = AnimationUtils.loadAnimation(activity, R.anim.reverse_right);

    }

    static class ViewHolder {
        TextView test_param, pkgname, mrp, dicount, price, test_discription, btn_extra_off, lab_name;
        LinearLayout btn_testdetail, btn_testdetail1;
        ImageView lab_img;
    }

    @Override
    public int getCount() {
        return SortList.size();
    }

    @Override
    public Object getItem(int location) {
        return SortList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.package_row_new1, null);
            holder = new ViewHolder();
            holder.pkgname = (TextView) convertView.findViewById(R.id.pkgname);
            holder.lab_name = (TextView) convertView.findViewById(R.id.lab_name);
            holder.test_discription = (TextView) convertView.findViewById(R.id.test_discriptiontext);
            holder.lab_img = (ImageView) convertView.findViewById(R.id.lab_img);
            holder.test_param = (TextView) convertView.findViewById(R.id.test_param);
            holder.mrp = (TextView) convertView.findViewById(R.id.mrp);
            holder.dicount = (TextView) convertView.findViewById(R.id.dicount);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.btn_testdetail = (LinearLayout) convertView.findViewById(R.id.btn_testdetail);
            holder.btn_testdetail1 = (LinearLayout) convertView.findViewById(R.id.btn_testdetail1);
            holder.btn_extra_off = (TextView) convertView.findViewById(R.id.exta_discount);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.pkgname.setText(SortList.get(position).get("TestName").toUpperCase());
        holder.test_param.setText(SortList.get(position).get("NoofPerameter"));

        if (SortList.get(position).get("PackageType").equalsIgnoreCase("1")) {

            holder.btn_testdetail.setVisibility(View.VISIBLE);
            holder.btn_testdetail1.setVisibility(View.GONE);
        } else {
            holder.btn_testdetail.setVisibility(View.VISIBLE);
            holder.btn_testdetail1.setVisibility(View.VISIBLE);
        }
        holder.test_discription.setText(SortList.get(position).get("TestDescription"));
        Float payprice = Float.parseFloat(SortList.get(position).get("Price")) * (1 - (Float.parseFloat(SortList.get(position).get("Discount"))));

        holder.mrp.setText(String.valueOf(Math.round(Float.parseFloat(SortList.get(position).get("Price")))));
        holder.price.setText("₹ " + String.valueOf(Math.round(payprice)));
        if (/*SortList.get(position).get("duplicatecount").equalsIgnoreCase("null") || SortList.get(position).get("duplicatecount") == null ||*/ SortList.get(position).get("PromoCode").equalsIgnoreCase("null")|| SortList.get(position).get("PromoCode")== null){
            holder.btn_extra_off.setVisibility(View.GONE);
        } else if(!SortList.get(position).get("PromoCode").equalsIgnoreCase("null")|| SortList.get(position).get("PromoCode")!= null){
            holder.btn_extra_off.setVisibility(View.VISIBLE);
        }
        // int finalprice=Math.round(Float.parseFloat(SortList.get(position).get("Price")))-(Math.round((Float.parseFloat(SortList.get(position).get("Price"))*(Float.parseFloat(SortList.get(position).get("Discount"))))));
        holder.dicount.setText(String.valueOf(Math.round(Float.parseFloat(SortList.get(position).get("Price")) - payprice)));
        String imagstr = null;
        String split[] = SortList.get(position).get("Logo").split(",");
        if (split.length == 2) {
            imagstr = split[1];
        } else {
            imagstr = split[0];
        }
        if (imagstr != null) {
            holder.lab_name.setVisibility(View.GONE);
            byte[] decodedString = Base64.decode(imagstr, Base64.DEFAULT);
            BitmapFactory.Options opt = new BitmapFactory.Options();
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, opt);
            if (decodedByte != null) {
                holder.lab_img.setVisibility(View.VISIBLE);

                holder.lab_img.setImageBitmap(decodedByte);
                //
            } else {
                holder.lab_img.setVisibility(View.GONE);
            }
        }
        if (SortList.get(position).get("Logo").equals("") || SortList.get(position).get("Logo").equals(" ")) {
            holder.lab_name.setVisibility(View.VISIBLE);
            holder.lab_img.setVisibility(View.GONE);
            holder.lab_name.setText(SortList.get(position).get("CentreName"));
        } else {
            holder.lab_name.setVisibility(View.GONE);
            holder.lab_img.setVisibility(View.VISIBLE);
        }
        holder.btn_testdetail1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onPkg_DetailsButtonClickListner(position, SortList.get(position).get("TestName"));
                }
            }
        });
        holder.btn_testdetail.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onButtonClickListner("BOOK TEST", position);
                }
            }
        });
        holder.btn_extra_off.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SortList.get(position).get("PromoCode").equalsIgnoreCase("null") || SortList.get(position).get("PromoCode") != null) {
                    String promo = SortList.get(position).get("PromoCode");
                    int length_promo = promo.length();
                    if (!SortList.get(position).get("AmountInPercentage").equalsIgnoreCase("null") || SortList.get(position).get("AmountInPercentage") != null) {
                        String amtpercent = SortList.get(position).get("AmountInPercentage");
                        String message = "Apply " + promo + " to avail extra " + amtpercent + "% off.";
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                        alertDialogBuilder
                                .setMessage(message)
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
                    } else if (!SortList.get(position).get("Amount").equalsIgnoreCase("null") || SortList.get(position).get("Amount") != null) {
                        String amt = SortList.get(position).get("Amount");
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                        String message = "Apply " + promo + " to avail extra " + amt + "% off.";
                        alertDialogBuilder
                                .setMessage(message)
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
                    }

                }
            }
        });


        return convertView;
    }

    // Home Package Button
    Package_btnListener customListner;

    public interface Package_btnListener {

        public void onButtonClickListner(String text, int pos);

        public void onPkg_DetailsButtonClickListner(int position, String value);
    }

    public void setpackage_btnListener(Package_btnListener listner) {
        this.customListner = listner;
    }

    public void setonPkg_DetailsButtonClickListner(Package_btnListener listner) {
        this.customListner = listner;
    }


    public void filter(String charText) {
        HashMap<String, String> hmap;
        charText = charText.toLowerCase(Locale.getDefault());
        SortList.clear();
        if (charText.length() == 0) {
            this.SortList.addAll(privatearray);
        } else {
            for (int i = 0; i < this.privatearray.size(); i++) {//TestDescription
                if (privatearray.get(i).get("TestName").toLowerCase(Locale.getDefault()).contains(charText) || privatearray.get(i).get("PackageName").toLowerCase(Locale.getDefault()).contains(charText)
                        || privatearray.get(i).get("CentreName").toLowerCase(Locale.getDefault()).contains(charText) || privatearray.get(i).get("TestDescription").toLowerCase(Locale.getDefault()).contains(charText)) {
                    HashMap<String, String> hmap1 = privatearray.get(i);
                    SortList.add(hmap1);


                }
            }
        }
        this.notifyDataSetChanged();
    }
}
