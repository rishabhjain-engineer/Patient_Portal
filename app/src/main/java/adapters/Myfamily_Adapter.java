package adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.hs.userportal.MyFamily;
import com.hs.userportal.MyVolleySingleton;
import com.hs.userportal.R;

import java.util.ArrayList;
import java.util.HashMap;

import utils.DataHolder;

/**
 * Created by ashish on 4/20/2016.
 */
public class Myfamily_Adapter extends ArrayAdapter<DataHolder> {

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<HashMap<String, String>> family_arr_list;
    //  private ArrayList<HashMap<String, String>> sorted_list;
    private ViewHolder holder = null;
    private ImageLoader mImageLoader;
    private ArrayList<String> revoke;
    private ArrayList<String> resend;
    private int spinnerPosition, check = 0;
    private ArrayAdapter adapter1, adapter;
    private DataHolder[] spinnerlist_adapter;
    private String patientID;
    // private int k;

    public Myfamily_Adapter(Activity activity, ArrayList<HashMap<String, String>> family_arr_list,
                            DataHolder[] spinnerlist_adapter, String patientID/*, ArrayList<HashMap<String, String>> sorted_list, int k*/) {
        super(activity, 0, spinnerlist_adapter);
        this.activity = activity;
        this.family_arr_list = family_arr_list;
        //this.sorted_list = sorted_list;
       /* revoke = new ArrayList<>();
        resend = new ArrayList<>();
        revoke.add("Revoke Access");
        resend.add("Resend Request");
        resend.add("Cancel Request");*/
        this.spinnerlist_adapter = spinnerlist_adapter;
        int l = spinnerlist_adapter.length;
        int s = family_arr_list.size();
        this.patientID = patientID;
        mImageLoader = MyVolleySingleton.getInstance(activity).getImageLoader();
        //  this.k = k;

    }

    static class ViewHolder {
        TextView member_name, relation_member, test_name, result, accept, deny, head_member,
                test_name_head, blood_group, amount, status, status_header;
        LinearLayout pending_request, amount_header;
        NetworkImageView user_pic;
        Spinner options;
        private DataHolder data;
    }

    @Override
    public int getCount() {
        return family_arr_list.size();
    }

    /* @Override
     public Object getItem(int location) {
         return family_arr_list.get(location);
     }*/
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.myfamily_adapter_new, null);
            holder = new ViewHolder();
            if (spinnerlist_adapter.length != 0) {
                holder.data = spinnerlist_adapter[position];
            }
            holder.member_name = (TextView) convertView.findViewById(R.id.member_name);
            holder.relation_member = (TextView) convertView.findViewById(R.id.relation_member);
            holder.test_name = (TextView) convertView.findViewById(R.id.test_name);
            holder.result = (TextView) convertView.findViewById(R.id.result);
            holder.accept = (TextView) convertView.findViewById(R.id.accept);
            holder.deny = (TextView) convertView.findViewById(R.id.deny);
            holder.head_member = (TextView) convertView.findViewById(R.id.head_member);
            holder.pending_request = (LinearLayout) convertView.findViewById(R.id.pending_request);
            holder.amount_header = (LinearLayout) convertView.findViewById(R.id.amount_header);
            holder.user_pic = (NetworkImageView) convertView.findViewById(R.id.user_pic);
            holder.options = (Spinner) convertView.findViewById(R.id.options);
            holder.blood_group = (TextView) convertView.findViewById(R.id.blood_group);
            holder.test_name_head = (TextView) convertView.findViewById(R.id.test_name_head);
            holder.amount = (TextView) convertView.findViewById(R.id.amount);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.status_header = (TextView) convertView.findViewById(R.id.status_header);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.member_name.setText(family_arr_list.get(position).get("FirstName") + " " + family_arr_list
                .get(position).get("LastName"));
       /* if (family_arr_list.get(position).get("TestName").equals("null") ||
                family_arr_list.get(position).get("TestName").equals("")) {
            holder.test_name.setVisibility(View.GONE);
        } else {
            holder.test_name.setText(family_arr_list.get(position).get("TestName"));
        }
        if (family_arr_list.get(position).get("Result").equals("null") ||
                family_arr_list.get(position).get("Result").equals("")) {
            holder.result.setVisibility(View.GONE);
        } else {
            holder.test_name.setText(family_arr_list.get(position).get("Result"));
        }*/
        if (spinnerlist_adapter.length != 0) {
            holder.options.setAdapter(holder.data.getAdapter());
        }
        if (family_arr_list.get(position).get("IsApproved").equals("true")) {
            holder.pending_request.setVisibility(View.GONE);
            holder.head_member.setVisibility(View.GONE);
            holder.options.setVisibility(View.VISIBLE);
            holder.user_pic.setDefaultImageResId(R.drawable.dashpic_update);
            holder.user_pic.setAdjustViewBounds(true);
            holder.user_pic.setImageUrl("https://files.healthscion.com/" + family_arr_list.get(position)
                    .get("Image"), mImageLoader);
            if (family_arr_list.get(position).get("HM").equals("2")) {
                holder.blood_group.setVisibility(View.VISIBLE);
                String check_bld = family_arr_list.get(position).get("BloodGroup");
                holder.amount_header.setVisibility(View.VISIBLE);
                Double amount_req = 0.0;
                if (family_arr_list.get(position).containsKey("TotalActualAmount")) {
                    String test = family_arr_list.get(position).get("TotalActualAmount") ;
                    if(test != null && !"null".equalsIgnoreCase(test)) {
                        amount_req = Double.valueOf(test);
                    }

                }
                if(amount_req ==0.0){
                    holder.amount_header.setVisibility(View.GONE);
                    holder.amount.setVisibility(View.GONE);
                }else{
                    holder.amount_header.setVisibility(View.VISIBLE);
                    holder.amount.setVisibility(View.VISIBLE);
                    holder.amount.setText("₹ " + String.format("%.2f", amount_req));
                }

                if (family_arr_list.get(position).containsKey("Balance")) {
                    Float balanceCheck  = Float.parseFloat(family_arr_list.get(position).get("Balance"));
                    holder.status_header.setVisibility(View.VISIBLE);
                    if (balanceCheck == 0.00) {
                        holder.status.setText("PAID");
                        holder.status.setTextColor(Color.parseColor("#319731"));
                    } else {
                        holder.status.setText("DUE");
                        holder.status.setTextColor(Color.RED);
                    }
                }else{
                    holder.status_header.setVisibility(View.GONE);
                }

                if (!check_bld.equalsIgnoreCase("")) {
                    holder.blood_group.setText(family_arr_list.get(position).get("BloodGroup"));
                } else {
                    holder.blood_group.setText("-");
                    //holder.blood_group.setVisibility(View.GONE);
                }
                holder.relation_member.setText(family_arr_list.get(position).get("RelationName"));
                if (family_arr_list.get(position).containsKey("IsTestCompletedNew")) {
                  /*  if (family_arr_list.get(position).get("IsTestCompletedNew").equals("1"))*/
                    {
                        holder.test_name.setVisibility(View.VISIBLE);
                        // holder.result.setVisibility(View.VISIBLE);
                        holder.test_name_head.setVisibility(View.VISIBLE);
                        String check_testname = family_arr_list.get(position).get("TestName");
                        if (check_testname.contains("#")) {
                            String[] pdf_name = check_testname.split("#");
                            int length = pdf_name.length;
                            holder.test_name.setText(pdf_name[length - 1].toString());
                        } else {
                            holder.test_name.setText(family_arr_list.get(position).get("TestName"));
                        }
                       /* if (sorted_list.get(position).get("TestName").contains(".")) {
                            String[] split_name = sorted_list.get(position).get("TestName").split(".");
                            if (split_name.length != 0) {
                                holder.test_name.setText(split_name[0]);
                            }
                        } else {*/
                        //holder.test_name.setText(sorted_list.get(position).get("TestName"));
                        // }
                        String result_value = family_arr_list.get(position).get("Result");
                        /*if (result_value.equals("") || result_value.equalsIgnoreCase("null")) {
                            holder.result.setText("Result: " + sorted_list.get(k).get("Result") +
                                    " " + sorted_list.get(k).get("Unit"));
                        }*/

                    }/* else {
                        holder.test_name.setVisibility(View.GONE);
                        //holder.result.setVisibility(View.GONE);
                        holder.test_name_head.setVisibility(View.GONE);
                    }*/
                } else {
                    holder.test_name.setVisibility(View.GONE);
                    // holder.result.setVisibility(View.GONE);
                    holder.test_name_head.setVisibility(View.GONE);
                    holder.relation_member.setText(family_arr_list.get(position).get("Age") + ", " + family_arr_list.get(position).get("Sex"));
                }
            } else {
                holder.test_name.setVisibility(View.GONE);
                // holder.result.setVisibility(View.GONE);
                holder.test_name_head.setVisibility(View.GONE);
                holder.amount_header.setVisibility(View.GONE);
                holder.relation_member.setText(family_arr_list.get(position).get("Age") + ", " + family_arr_list.get(position).get("Sex"));
            }
           /* adapter = new ArrayAdapter(activity, R.layout.spinner_item, revoke);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_itemandroid.R.layout.simple_spinner_dropdown_item);
            holder.options.setAdapter(adapter);*/
        } else {
            holder.amount_header.setVisibility(View.GONE);
            if (family_arr_list.get(position).get("HM").equals("2")) {
                holder.member_name.setText(family_arr_list.get(position).get("PatientCode"));
                holder.relation_member.setText(family_arr_list.get(position).get("RelationName"));
                holder.pending_request.setVisibility(View.GONE);
                holder.options.setVisibility(View.VISIBLE);
                holder.head_member.setVisibility(View.VISIBLE);
                holder.test_name.setVisibility(View.GONE);
                // holder.result.setVisibility(View.GONE);
                holder.test_name_head.setVisibility(View.GONE);
                holder.user_pic.setDefaultImageResId(R.drawable.dashpic_update);
                holder.user_pic.setAdjustViewBounds(true);
               /* adapter1 = new ArrayAdapter(activity, R.layout.spinner_item android.R.layout.simple_spinner_item, resend);
                adapter1.setDropDownViewResource(R.layout.spinner_dropdown_itemandroid.R.layout.simple_spinner_dropdown_item);
                holder.options.setAdapter(adapter1);*/
            } else {
                holder.member_name.setText(family_arr_list.get(position).get("FirstName") + " " + family_arr_list
                        .get(position).get("LastName"));
                holder.relation_member.setText(family_arr_list.get(position).get("Age") + "," + family_arr_list.get(position).get("Sex"));
                holder.user_pic.setDefaultImageResId(R.drawable.dashpic_update);
                holder.user_pic.setAdjustViewBounds(true);
                holder.head_member.setVisibility(View.GONE);
                holder.pending_request.setVisibility(View.VISIBLE);
                holder.test_name.setVisibility(View.GONE);
                //  holder.result.setVisibility(View.GONE);
                holder.test_name_head.setVisibility(View.GONE);
                holder.options.setVisibility(View.GONE);
            }
        }
        // Used to handle events when the user changes the Spinner selection:

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onButton_action_click(position, "1");
            }
        });

        holder.deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onButton_action_click(position, "0");
            }
        });
        holder.options.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                DataHolder hol = spinnerlist_adapter[position];
                hol.setSelected(arg2);
                if (hol.getText().equals("Resend Request")) {

                   /* final AlertDialog alertDialog = new AlertDialog.Builder(
                            activity).create();
                    alertDialog.setMessage("Are you sure you want to resend the request");
                    // Setting OK Button
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String patient_code = family_arr_list.get(position).get("PatientCode");
                            String relation = family_arr_list.get(position).get("RelationName");
                            int repeat_val = 1;
                            ((MyFamily) activity).sendrequest(patient_code, relation, repeat_val);
                        }
                    });
                    alertDialog.setButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });*/

                    AlertDialog.Builder b = new AlertDialog.Builder(activity)
                            .setTitle("Are you sure you want to resend the request?")
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            String patient_code = family_arr_list.get(position).get("PatientCode");
                                            String relation = family_arr_list.get(position).get("RelationName");
                                            int repeat_val = 1;
                                            ((MyFamily) activity).sendrequest(patient_code, relation, repeat_val);
                                        }
                                    }
                            )
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.dismiss();
                                        }
                                    }
                            );
                    b.create();
                    b.show();

                   /* String patient_code = family_arr_list.get(position).get("PatientCode");
                    String relation = family_arr_list.get(position).get("RelationName");
                    int repeat_val = 1;
                    ((MyFamily) activity).sendrequest(patient_code, relation, repeat_val);*/

                } else if (hol.getText().equals("Revoke Access")) {
                  /*  final AlertDialog alertDialog = new AlertDialog.Builder(
                            activity).create();
                    alertDialog.setMessage("Are you sure you want to revoke this member");
                    // Setting OK Button
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String user_id = family_arr_list.get(position).get("FamilyHeadId");
                            if (patientID.equalsIgnoreCase(user_id)) {
                                listener.onButton_action_click(position, "2");
                            } else {
                                listener.onButton_action_click(position, "4");
                            }
                        }
                    });
                    alertDialog.setButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });

                    // Showing Alert Message
                    alertDialog.show();*/

                    AlertDialog.Builder b = new AlertDialog.Builder(activity)
                            .setTitle("Are you sure you want to revoke this member?")
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            String user_id = family_arr_list.get(position).get("FamilyHeadId");
                                            if (patientID.equalsIgnoreCase(user_id)) {
                                                listener.onButton_action_click(position, "2");
                                            } else {
                                                listener.onButton_action_click(position, "4");
                                            }
                                        }
                                    }
                            )
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.dismiss();
                                        }
                                    }
                            );
                    b.create();
                    b.show();

                   /* String user_id = family_arr_list.get(position).get("FamilyHeadId");
                    if (patientID.equalsIgnoreCase(user_id)) {
                        listener.onButton_action_click(position, "2");
                    } else {
                        listener.onButton_action_click(position, "4");
                    }*/

                } else if (hol.getText().equals("Remove Member")) {
                    AlertDialog.Builder b = new AlertDialog.Builder(activity)
                            .setTitle("Are you sure you want to remove this member?")
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            String user_id = family_arr_list.get(position).get("FamilyHeadId");
                                            if (patientID.equalsIgnoreCase(user_id)) {
                                                listener.onButton_action_click(position, "2");
                                            } else {
                                                listener.onButton_action_click(position, "4");
                                            }
                                        }
                                    }
                            )
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.dismiss();
                                        }
                                    }
                            );
                    b.create();
                    b.show();
                } else if (hol.getText().equals("Cancel Request")) {

                   /* final AlertDialog alertDialog = new AlertDialog.Builder(
                            activity).create();
                    alertDialog.setMessage("Are you sure you want to cancel the request");
                    // Setting OK Button
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            listener.onButton_action_click(position, "3");
                        }
                    });
                    alertDialog.setButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    // Showing Alert Message
                    alertDialog.show();*/

                    AlertDialog.Builder b = new AlertDialog.Builder(activity)
                            .setTitle("Are you sure you want to cancel the request?")
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            listener.onButton_action_click(position, "3");
                                        }
                                    }
                            )
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.dismiss();
                                        }
                                    }
                            );
                    b.create();
                    b.show();

                   /* listener.onButton_action_click(position, "3");*/
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });
        return convertView;
    }

    action_button_event listener;

    public interface action_button_event {
        public void onButton_action_click(int position, String check);
    }

    public void getListenerobj(action_button_event listener) {
        this.listener = listener;
    }
}
