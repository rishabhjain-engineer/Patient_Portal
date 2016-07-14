package com.hs.userportal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import adapters.Folder_adapter;
import adapters.Vault_adapter;
import adapters.Vault_delete_adapter;
import config.StaticHolder;
import utils.NavFolder;

public class Filevault extends ActionBarActivity {

    ImageLoader mImageLoader;
    ByteArrayOutputStream byteArrayOutputStream;
    NetworkImageView mNetworkImageView;
    JSONObject sendData, receiveData;
    private static Context mContext;
    JSONArray subArrayImage, S3Objects_arr;
    ProgressDialog pd;
    static boolean check_load = false;
    private ArrayList<HashMap<String, String>> vault_data;
    private ArrayList<HashMap<String, String>> S3Objects;
    private ArrayList<HashMap<String, String>> S3Objects_folder;
    private ArrayList<HashMap<String, String>> S3Objects_details;
    // static ArrayList<String> thumbImage = new ArrayList<String>();
   public static ArrayList<HashMap<String, String>> thumbImage = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> thumbImage_folder = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> alias_thumbImage_folder = new ArrayList<HashMap<String, String>>();
    static ArrayList<String> imageName = new ArrayList<String>();
    static ArrayList<String> imageNamewithpdf = new ArrayList<String>();
    public static ArrayList<HashMap<String, String>> originalVaultlist = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> showing_Vaultlist = new ArrayList<HashMap<String, String>>();
    Button upload;
    static String id = null;
    final Handler handler = new Handler();
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLERY = 2;
    Bitmap bitmap;
    static String pic = null;
    String picname = "";
    static JsonObjectRequest jr;
    JsonObjectRequest jr2, jr3, jr4;
    static JsonObjectRequest s3jr;
    private JsonObjectRequest lock_folder;
    private ImageAdapter imageAdapter;
    private boolean[] thumbnailsselection;
    int count;
   public static Menu menu_toggle;
    ArrayList<String> imageId = new ArrayList<String>();
    String imageIdsToBeSent = "";
    RequestQueue queue;
    RequestQueue queue2;
    RequestQueue queue3;
    static RequestQueue req;
    private ListView vault_list;
    int ipos = 0;
    Services service;
    GridView gridView;
    int check = 0;
    int check_grid = 0;
    ProgressDialog progress;
    private RelativeLayout list_header, list_header2;
    byte[] byteArray;
    private boolean view_list = false;
    SharedPreferences sharedPreferences;
    private String list_operation, patientId, Folder_Clicked, HashKey;
    private Vault_adapter vault_adapter;
    private Vault_delete_adapter vault_delete_adapter;
    ProgressBar bar;
    private boolean toggle_move = false;
    NotificationHandler nHandler;
    private ArrayList<HashMap<String, String>> moveFolder1 = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> moveFolder2 = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> dialog_folder = new ArrayList<HashMap<String, String>>();
    private int back_clicked_move = 0;
    private static ArrayList<String> folder_path = new ArrayList<String>();
    static final String path = Environment.getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_DCIM
            + "/Patient Portal";
   public static Uri Imguri;
    static boolean refresh_vault1 = true;
    String[] rem_dup_folder;
    String check_view = "";
    int checkdialog = 0;
    private Folder_adapter folder_adapter;
    private ArrayList<HashMap<String, String>> moveFolder_navigate = new ArrayList<HashMap<String, String>>();
    public static Context file_vaultcontxt;
    private TextView warning_msg;
    private int position_scroll = 0;
    private int check_para = 0, select_times = 0, show_menu1 = 0, show_menu = 0;

    /*@Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putParcelable("myObj", menu_toggle);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filevault);
        ActionBar action = getSupportActionBar();
        action.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1DBBE3")));
        action.setIcon(new ColorDrawable(Color.parseColor("#1DBBE3")));
        action.setDisplayHomeAsUpEnabled(true);
        Intent z = getIntent();
        id = z.getStringExtra("id");
        mContext = Filevault.this;
        file_vaultcontxt = Filevault.this;
        upload = (Button) findViewById(R.id.upload);
        gridView = (GridView) findViewById(R.id.gridView);
        vault_list = (ListView) findViewById(R.id.vault_list);
        list_header = (RelativeLayout) findViewById(R.id.list_header);
        list_header2 = (RelativeLayout) findViewById(R.id.list_header2);
        sendData = new JSONObject();
        service = new Services(Filevault.this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        patientId = sharedPreferences.getString("ke", "");
        warning_msg = (TextView) findViewById(R.id.warning_msg);
        //refresh_vault1 = view_list;
        new Authentication(Filevault.this, "Filevault", "").execute();


       /* jr = new JsonObjectRequest(Method.POST, url, sendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                System.out.println(response);
                thumbImage.clear();
                imageName.clear();
                vault_data = new ArrayList<HashMap<String, String>>();
                try {
                    String imageData = response.getString("d");
                    JSONObject cut = new JSONObject(imageData);
                    subArrayImage = cut.getJSONArray("Table");
                    HashMap<String, String> hmap;
                    for (int i = 0; i < subArrayImage.length(); i++) {
                        hmap = new HashMap<String, String>();
                        imageNamewithpdf.add(subArrayImage.getJSONObject(i).getString("ImageName"));
                        imageName.add(subArrayImage.getJSONObject(i).getString("Image"));
                        thumbImage.add(subArrayImage.getJSONObject(i).getString("ThumbImage"));
                        hmap.put("ImageId", subArrayImage.getJSONObject(i).getString("ImageId"));
                        hmap.put("ImageName", subArrayImage.getJSONObject(i).getString("ImageName"));
                        hmap.put("ThumbImage", subArrayImage.getJSONObject(i).getString("ThumbImage"));
                        hmap.put("TimeStamp", subArrayImage.getJSONObject(i).getString("TimeStamp"));
                        hmap.put("Image", subArrayImage.getJSONObject(i).getString("Image"));
                        vault_data.add(hmap);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                mImageLoader = MyVolleySingleton.getInstance(Filevault.this).getImageLoader();
                count = subArrayImage.length();
                thumbnailsselection = new boolean[count];
                imageAdapter = new ImageAdapter();
                gridView.setAdapter(imageAdapter);
                vault_adapter = new Vault_adapter(Filevault.this, vault_data, false);
                vault_list.setAdapter(vault_adapter);
                if (view_list) {
                    list_header.setVisibility(View.VISIBLE);
                    list_header2.setVisibility(View.GONE);
                }
                vault_adapter.notifyDataSetChanged();
                progress.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println("GetPatientFilesNew: " + error);
                progress.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Cookie", Services.hoja);
                return headers;
            }
        };
        queue.add(jr);*/

		/*url =  Services.init+"Patient/loadVaultMobile";*/
       /* StaticHolder sttc_holdr1 = new StaticHolder(Filevault.this, StaticHolder.Services_static.loadVaultMobile);
        String url1 = sttc_holdr1.request_Url();
        StringRequest myReq = new StringRequest(Method.POST, url1, createloadVaultSuccessListener(),
                createloadVaultErrorListener()) {

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("PatientId", id);

                return params;
            }

            ;
        };
        queue.add(myReq);*/

		/*url = Services.init+"Patient/getDistinctTags";*/
      /*  StaticHolder sttc_holdr2 = new StaticHolder(Filevault.this, StaticHolder.Services_static.getDistinctTags);
        String url2 = sttc_holdr2.request_Url();
        StringRequest myReq1 = new StringRequest(Method.POST, url2, createDistinctTagsSuccessListener(),
                createDistinctTagsErrorListener()) {

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("PatientId", id);
                params.put("NewTag", "false");
                params.put("tagId", "");

                return params;
            }

            ;
        };
        queue.add(myReq1);*/

        upload.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Filevault.this);
                builder1.setTitle("Message");
                builder1.setItems(new CharSequence[]{"Create Folder", "Upload Files"},
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        show_dialog();
                                        break;

                                    case 1:

                                        // Intent takePictureIntent = new
                                        // Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        // if
                                        // (takePictureIntent.resolveActivity(getPackageManager())
                                        // != null)
                                        // {
                                        //
                                        // startActivityForResult(takePictureIntent,PICK_FROM_CAMERA);
                                        //
                                        // }
                                        chooseimage();


                                        break;

                                    default:
                                        break;
                                }
                            }
                        });
                builder1.show();
            }
        });


        vault_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!thumbImage.get(position).get("Personal3").contains(".PNG") && !thumbImage.get(position).get("Personal3").contains(".png") &&
                        !thumbImage.get(position).get("Personal3").contains(".jpg")
                        && !thumbImage.get(position).get("Personal3").contains(".JPG") && !thumbImage.get(position).get("Personal3").contains(".pdf")
                        && !thumbImage.get(position).get("Personal3").contains(".xls") && !thumbImage.get(position).get("Personal3").contains(".doc")
                        && !thumbImage.get(position).get("Personal3").contains(".txt")) {
                    Intent i = new Intent(Filevault.this, Filevault2.class);
                    i.putExtra("Folder_Clicked", thumbImage.get(position).get("Personal3").trim());
                    i.putExtra("hash_keyvalue", "Personal3");
                    i.putExtra("view", "List");
                    i.putExtra("first_timefolderclicked", thumbImage.get(position).get("Personal3").trim());
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else if (thumbImage.get(position).get("Personal3").contains(".pdf")) {
                    Intent i = new Intent(Filevault.this, PdfReader.class);
                    i.putExtra("image_url", "https://files.cloudchowk.com/" + patientId + "/FileVault/Personal/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20"));
                    String pdf_name = thumbImage.get(position).get("Personal3").replace(patientId + "/FileVault/Personal/", "");
                    i.putExtra("imagename", pdf_name/* thumbImage.get(position)*/);
                    startActivity(i);
                    try {
                        // Toast.makeText(getBaseContext(), "Opening PDF... ", Toast.LENGTH_SHORT).show();
                        Intent inte = new Intent(Intent.ACTION_VIEW);
                        inte.setDataAndType(
                                Uri.parse("https://files.cloudchowk.com/" + patientId + "/FileVault/Personal/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20")),
                                "application/pdf");

                        startActivity(inte);
                    } catch (ActivityNotFoundException e) {
                        // Log.e("Viewer not installed on your device.", e.getMessage());
                    }

                } else if (thumbImage.get(position).get("Personal3").contains(".doc") || thumbImage.get(position).get("Personal3").contains(".docx")) {
                    Intent i = new Intent(Filevault.this, PdfReader.class);
                    i.putExtra("image_url", "https://files.cloudchowk.com/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20"));
                    String pdf_name = thumbImage.get(position).get("Personal3").replace(patientId + "/FileVault/Personal/", "");
                    i.putExtra("imagename", pdf_name/* thumbImage.get(position)*/);
                    startActivity(i);
                    try {
                        // Toast.makeText(getBaseContext(), "Opening DOC... ", Toast.LENGTH_SHORT).show();
                        Intent inte = new Intent(Intent.ACTION_VIEW);
                        inte.setDataAndType(
                                Uri.parse("https://files.cloudchowk.com/" + patientId + "/FileVault/Personal/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20")),
                                "application/pdf");

                        startActivity(inte);
                    } catch (ActivityNotFoundException e) {
                        // Log.e("Viewer not installed on your device.", e.getMessage());
                    }
                } else if (thumbImage.get(position).get("Personal3").contains(".xls") || thumbImage.get(position).get("Personal3").contains(".xlsx")) {
                    Intent i = new Intent(Filevault.this, PdfReader.class);
                    i.putExtra("image_url", "https://files.cloudchowk.com/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20"));
                    String pdf_name = thumbImage.get(position).get("Personal3").replace(patientId + "/FileVault/Personal/", "");
                    i.putExtra("imagename", pdf_name/* thumbImage.get(position)*/);
                    startActivity(i);
                    try {
                        // Toast.makeText(getBaseContext(), "Opening xsl... ", Toast.LENGTH_SHORT).show();
                        Intent inte = new Intent(Intent.ACTION_VIEW);
                        inte.setDataAndType(
                                Uri.parse("https://files.cloudchowk.com/" + patientId + "/FileVault/Personal/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20")),
                                "application/pdf");

                        startActivity(inte);
                    } catch (ActivityNotFoundException e) {
                        // Log.e("Viewer not installed on your device.", e.getMessage());
                    }
                } else if (thumbImage.get(position).get("Personal3").contains(".txt")) {
                    Intent i = new Intent(Filevault.this, PdfReader.class);
                    i.putExtra("image_url", "https://files.cloudchowk.com/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20"));
                    String pdf_name = thumbImage.get(position).get("Personal3").replace(patientId + "/FileVault/Personal/", "");
                    i.putExtra("imagename", pdf_name/* thumbImage.get(position)*/);
                    startActivity(i);
                    try {
                        // Toast.makeText(getBaseContext(), "Opening xsl... ", Toast.LENGTH_SHORT).show();
                        Intent inte = new Intent(Intent.ACTION_VIEW);
                        inte.setDataAndType(
                                Uri.parse("https://files.cloudchowk.com/" + patientId + "/FileVault/Personal/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20")),
                                "application/pdf");

                        startActivity(inte);
                    } catch (ActivityNotFoundException e) {
                        // Log.e("Viewer not installed on your device.", e.getMessage());
                    }
                } else {
                    Intent i = new Intent(Filevault.this, ExpandImage.class);
                    String removeonejpg = thumbImage.get(position).get("Personal3");
                    if (thumbImage.get(position).get("Personal3").endsWith(".jpg")) {

                        // removeonejpg = thumbImage.get(position).substring(0, thumbImage.get(position).length() - 4);

                    }

                    if (removeonejpg != null) {
                        String image_url = thumbImage.get(position).get("Personal3").replace("_thumb", "");
                        String image_name = thumbImage.get(position).get("Personal3").replace(patientId + "/FileVault/", "");
                        if (image_url.contains("/FileVault/Personal/")) {
                            i.putExtra("image", "https://files.cloudchowk.com/" + image_url.replaceAll(" ", "%20"));
                        } else {
                            i.putExtra("image", "https://files.cloudchowk.com/" + patientId + "/FileVault/Personal/" + image_url.replaceAll(" ", "%20"));
                        }
                        i.putExtra("imagename", /*imageNamewithpdf.get(position)*/image_name);
                        startActivity(i);
                    }
                }
            }/*else{
                    CheckBox cb = (CheckBox) view.findViewById(R.id.delete);
                    cb.setId(position);
                    int cb_id = cb.getId();
                    if (thumbnailsselection[cb_id]) {
                        cb.setChecked(false);
                        thumbnailsselection[cb_id] = false;
                    } else {
                        cb.setChecked(true);
                        thumbnailsselection[cb_id] = true;
                    }
                }*/
        });
        vault_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
                vault_delete_adapter = new Vault_delete_adapter(Filevault.this, thumbImage, view_list, patientId, thumbnailsselection, "");
                vault_list.setAdapter(vault_delete_adapter);
                  /*  vault_delete_adapter.notifyDataSetChanged();*/
                list_header2.setVisibility(View.VISIBLE);
                list_header.setVisibility(View.GONE);
                toggle_move = true;
                check_para = 1;
                select_times = 1;
                position_scroll = pos;
                menu_toggle.findItem(R.id.action_delete).setVisible(true);
                menu_toggle.findItem(R.id.save).setVisible(true);
                menu_toggle.findItem(R.id.action_move).setVisible(true);
                menu_toggle.findItem(R.id.action_home).setVisible(false);
                HashMap<String, Object> obj = (HashMap<String, Object>) vault_delete_adapter.getItem(pos);
                String check_list = (String) obj.get("Personal3");
                if (check_list.equals("Prescription") ||
                        check_list.equals("Insurance")
                        || check_list.equals("Bills") || check_list.equals("Reports")) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Filevault.this);
                    // set title
                    alertDialogBuilder.setTitle("Alert");
                    // set dialog message
                    alertDialogBuilder.setMessage("Operation not allowed on locked folders.")
                            .setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                } else {
                    thumbnailsselection[pos] = true;
                }
                return onLongListItemClick(v, pos, id);
            }
        });

    }

    protected boolean onLongListItemClick(View v, int pos, long id) {
        Log.i("long_press", "onLongListItemClick id=" + id + "position=" + pos);
        return true;
    }

    private Response.Listener<String> createloadVaultSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("loadVaultMobile " + response);
            }
        };
    }

    private Response.ErrorListener createloadVaultErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("loadVaultMobile " + error);
            }
        };
    }

    private Response.Listener<String> createDistinctTagsSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("DistinctTags " + response);
            }
        };
    }

    private Response.ErrorListener createDistinctTagsErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("DistinctTags " + error);
            }
        };
    }

    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public ImageAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            count = thumbImage.size();
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.filevaultdeleteitem, null);
                holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
                // holder.thumbImage2 = (ImageView) convertView.findViewById(R.id.thumbImage2);
                holder.checkbox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);
                holder.folder_name = (TextView) convertView.findViewById(R.id.folder_name);
                holder.image_name = (TextView) convertView.findViewById(R.id.image_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.checkbox.setId(position);
            holder.imageview.setId(position);
            //  holder.thumbImage2.setId(position);
            holder.checkbox.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    int id = cb.getId();
                    if (thumbnailsselection[id]) {
                        cb.setChecked(false);
                        thumbnailsselection[id] = false;
                    } else {
                        cb.setChecked(true);
                        thumbnailsselection[id] = true;
                    }
                    int show_menu_grid = 0;
                    for (int i = 0; i < thumbnailsselection.length; i++) {
                        if (thumbnailsselection[i]) {
                            show_menu_grid = 1;
                            toggle_move = true;
                            break;
                        } else {
                            toggle_move = false;
                        }
                    }
                    if (show_menu_grid == 1) {
                        menu_toggle.findItem(R.id.action_delete).setVisible(true);
                        menu_toggle.findItem(R.id.save).setVisible(true);
                        menu_toggle.findItem(R.id.action_move).setVisible(true);
                        menu_toggle.findItem(R.id.action_home).setVisible(false);
                    } else {
                        menu_toggle.findItem(R.id.action_delete).setVisible(false);
                        menu_toggle.findItem(R.id.save).setVisible(false);
                        menu_toggle.findItem(R.id.action_move).setVisible(false);
                        menu_toggle.findItem(R.id.action_home).setVisible(true);
                    }
                }

            });

            holder.folder_name.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Filevault.this, Filevault2.class);
                    i.putExtra("Folder_Clicked", holder.folder_name.getText().toString().trim());
                    i.putExtra("check_load", check_load);
                    i.putExtra("view", "Gird");
                    i.putExtra("hash_keyvalue", "Personal3");
                    i.putExtra("first_timefolderclicked", thumbImage.get(position).get("Personal3").trim());
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });

            holder.imageview.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    if (thumbImage.get(position).get("Personal3").contains(".pdf")) {
                        Intent i = new Intent(Filevault.this, PdfReader.class);
                        i.putExtra("image_url", "https://files.cloudchowk.com/" + patientId + "/FileVault/Personal/" + thumbImage.get(position).get("Personal3"));
                        String pdf_name = thumbImage.get(position).get("Personal3").replace(patientId + "/FileVault/Personal/", "");
                        i.putExtra("imagename", pdf_name/* thumbImage.get(position)*/);
                        startActivity(i);
                        try {
                            // Toast.makeText(getBaseContext(), "Opening PDF... ", Toast.LENGTH_SHORT).show();
                            Intent inte = new Intent(Intent.ACTION_VIEW);
                            inte.setDataAndType(
                                    Uri.parse("https://files.cloudchowk.com/" + patientId + "/FileVault/Personal/" + thumbImage.get(position).get("Personal3")),
                                    "application/pdf");

                            startActivity(inte);
                        } catch (ActivityNotFoundException e) {
                            // Log.e("Viewer not installed on your device.", e.getMessage());
                        }

                    } else if (thumbImage.get(position).get("Personal3").contains(".doc") || thumbImage.get(position).get("Personal3").contains(".docx")) {
                        Intent i = new Intent(Filevault.this, PdfReader.class);
                        i.putExtra("image_url", "https://files.cloudchowk.com/" + thumbImage.get(position).get("Personal3"));
                        String pdf_name = thumbImage.get(position).get("Personal3").replace(patientId + "/FileVault/Personal/", "");
                        i.putExtra("imagename", pdf_name/* thumbImage.get(position)*/);
                        startActivity(i);
                        try {
                            // Toast.makeText(getBaseContext(), "Opening DOC... ", Toast.LENGTH_SHORT).show();
                            Intent inte = new Intent(Intent.ACTION_VIEW);
                            inte.setDataAndType(
                                    Uri.parse("https://files.cloudchowk.com/" + patientId + "/FileVault/Personal/" + thumbImage.get(position).get("Personal3")),
                                    "application/pdf");

                            startActivity(inte);
                        } catch (ActivityNotFoundException e) {
                            // Log.e("Viewer not installed on your device.", e.getMessage());
                        }
                    } else if (thumbImage.get(position).get("Personal3").contains(".xls") || thumbImage.get(position).get("Personal3").contains(".xlsx")) {
                        Intent i = new Intent(Filevault.this, PdfReader.class);
                        i.putExtra("image_url", "https://files.cloudchowk.com/" + thumbImage.get(position).get("Personal3"));
                        String pdf_name = thumbImage.get(position).get("Personal3").replace(patientId + "/FileVault/Personal/", "");
                        i.putExtra("imagename", pdf_name/* thumbImage.get(position)*/);
                        startActivity(i);
                        try {
                            // Toast.makeText(getBaseContext(), "Opening xsl... ", Toast.LENGTH_SHORT).show();
                            Intent inte = new Intent(Intent.ACTION_VIEW);
                            inte.setDataAndType(
                                    Uri.parse("https://files.cloudchowk.com/" + patientId + "/FileVault/Personal/" + thumbImage.get(position).get("Personal3")),
                                    "application/pdf");

                            startActivity(inte);
                        } catch (ActivityNotFoundException e) {
                            // Log.e("Viewer not installed on your device.", e.getMessage());
                        }
                    } else if (thumbImage.get(position).get("Personal3").contains(".txt")) {
                        Intent i = new Intent(Filevault.this, PdfReader.class);
                        i.putExtra("image_url", "https://files.cloudchowk.com/" + thumbImage.get(position).get("Personal3"));
                        String pdf_name = thumbImage.get(position).get("Personal3").replace(patientId + "/FileVault/Personal/", "");
                        i.putExtra("imagename", pdf_name/* thumbImage.get(position)*/);
                        startActivity(i);
                        try {
                            // Toast.makeText(getBaseContext(), "Opening xsl... ", Toast.LENGTH_SHORT).show();
                            Intent inte = new Intent(Intent.ACTION_VIEW);
                            inte.setDataAndType(
                                    Uri.parse("https://files.cloudchowk.com/" + patientId + "/FileVault/Personal/" + thumbImage.get(position).get("Personal3")),
                                    "application/pdf");

                            startActivity(inte);
                        } catch (ActivityNotFoundException e) {
                            // Log.e("Viewer not installed on your device.", e.getMessage());
                        }
                    } else {


                        Intent i = new Intent(Filevault.this, ExpandImage.class);
                        String removeonejpg = thumbImage.get(position).get("Personal3");
                        if (thumbImage.get(position).get("Personal3").endsWith(".jpg")) {

                            // removeonejpg = thumbImage.get(position).substring(0, thumbImage.get(position).length() - 4);

                        }

                        if (removeonejpg != null) {
                            String image_url = thumbImage.get(position).get("Personal3").replace("_thumb", "");
                            String image_name = thumbImage.get(position).get("Personal3").replace(patientId + "/FileVault/Personal/", "");
                            if (image_url.contains("/FileVault/Personal/")) {
                                i.putExtra("image", "https://files.cloudchowk.com/" + image_url);
                            } else {
                                i.putExtra("image", "https://files.cloudchowk.com/" + patientId + "/FileVault/Personal/" + image_url);
                            }
                            i.putExtra("imagename", /*imageNamewithpdf.get(position)*/image_name);
                            startActivity(i);
                        }
                    }

                }
            });
            // holder.imageview.setImageBitmap(thumbnails[position]);

         /*   Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;*/
            mNetworkImageView = (NetworkImageView) holder.imageview;
        /*    if (imageNamewithpdf.get(position).contains(".pdf")) {
                holder.imageview.setBackgroundResource(R.drawable.pdfimg);
                mNetworkImageView.setBackgroundDrawable(Filevault.this.getResources().getDrawable(R.drawable.pdfimg));
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

                mNetworkImageView.setImageUrl("https://files.cloudchowk.com/" + thumbImage.get(position), mImageLoader);
            }*/

           /* for (int l = 0; l < S3Objects.size(); l++) {*/
            // String folder_name = S3Objects.get(position).get("folder_name");
            if (!thumbImage.get(position).get("Personal3").contains(".PNG") && !thumbImage.get(position).get("Personal3").contains(".png") &&
                    !thumbImage.get(position).get("Personal3").contains(".jpg")
                    && !thumbImage.get(position).get("Personal3").contains(".JPG") && !thumbImage.get(position).get("Personal3").contains(".pdf")
                    && !thumbImage.get(position).get("Personal3").contains(".xls") && !thumbImage.get(position).get("Personal3").contains(".doc")
                    && !thumbImage.get(position).get("Personal3").contains(".txt")) {
                holder.folder_name.setVisibility(View.VISIBLE);
                if (thumbImage.get(position).get("Personal3").equalsIgnoreCase("Prescription") ||
                        thumbImage.get(position).get("Personal3").equalsIgnoreCase("Insurance") ||
                        thumbImage.get(position).get("Personal3").equalsIgnoreCase("Bills") ||
                        thumbImage.get(position).get("Personal3").equalsIgnoreCase("Reports")) {
                    holder.folder_name.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_folder_protected, 0, 0);
                } else {
                    holder.folder_name.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_folder, 0, 0);
                }
                holder.imageview.setVisibility(View.GONE);
                holder.image_name.setVisibility(View.GONE);
                holder.folder_name.setText(thumbImage.get(position).get("Personal3"));
                /*}*/
            } else {
                if (!thumbImage.get(position).get("Personal3").contains(".xls") && !thumbImage.get(position).get("Personal3").contains(".doc") &&
                        !thumbImage.get(position).get("Personal3").contains(".pdf") && !thumbImage.get(position).get("Personal3").contains(".txt")) {
                    String[] pdf_name = thumbImage.get(position).get("Personal3").split("/");
                    int length = pdf_name.length;
                    holder.folder_name.setVisibility(View.GONE);
                    holder.imageview.setVisibility(View.VISIBLE);
                    holder.image_name.setVisibility(View.VISIBLE);
                    holder.image_name.setText(pdf_name[length - 1].toString());
                    String thumbimg = "";
                    if (thumbImage.get(position).get("Personal3").contains(".png")) {
                        thumbimg = thumbImage.get(position).get("Personal3").replaceAll("\\.png", "_thumb.png");
                    } else if (thumbImage.get(position).get("Personal3").contains(".PNG")) {
                        thumbimg = thumbImage.get(position).get("Personal3").replaceAll("\\.PNG", "_thumb.PNG");
                    } else if (thumbImage.get(position).get("Personal3").contains(".jpg")) {
                        thumbimg = thumbImage.get(position).get("Personal3").replaceAll("\\.jpg", "_thumb.jpg");
                    } else if (thumbImage.get(position).get("Personal3").contains(".JPG")) {
                        thumbimg = thumbImage.get(position).get("Personal3").replaceAll("\\.JPG", "_thumb.JPG");
                    }
                    if (!thumbImage.get(position).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                        mNetworkImageView.setImageUrl("https://files.cloudchowk.com/" + patientId + "/FileVault/Personal/" + thumbimg.replaceAll(" ", "%20"), mImageLoader);
                    } else {
                        mNetworkImageView.setImageUrl("https://files.cloudchowk.com/" + thumbimg.replaceAll(" ", "%20"), mImageLoader);
                    }
                } else if (thumbImage.get(position).get("Personal3").contains(".pdf") &&
                        !thumbImage.get(position).get("Personal3").contains(".xls") && !thumbImage.get(position).get("Personal3").contains(".doc")
                        && thumbImage.get(position).get("Personal3").contains(".txt")) {
                    String[] pdf_name = thumbImage.get(position).get("Personal3").split("/");
                    int length = pdf_name.length;
                    holder.folder_name.setVisibility(View.GONE);
                    holder.imageview.setVisibility(View.VISIBLE);
                    holder.image_name.setVisibility(View.VISIBLE);
                    holder.image_name.setText(pdf_name[length - 1].toString());
                  /*  mNetworkImageView = (NetworkImageView) holder.imageview;*/
                    mNetworkImageView.setDefaultImageResId(R.drawable.pdfimg);
                    mNetworkImageView.setAdjustViewBounds(true);
                    mNetworkImageView.setImageUrl(null, null);
                } else if ((thumbImage.get(position).get("Personal3").contains(".xls") || thumbImage.get(position).get("Personal3").contains(".xlsx")) &&
                        !thumbImage.get(position).get("Personal3").contains(".pdf") && !thumbImage.get(position).get("Personal3").contains(".doc")
                        && !thumbImage.get(position).get("Personal3").contains(".txt")) {
                    holder.folder_name.setVisibility(View.GONE);
                    holder.imageview.setVisibility(View.VISIBLE);
                    String[] pdf_name = thumbImage.get(position).get("Personal3").split("/");
                    int length = pdf_name.length;
                   /* mNetworkImageView = (NetworkImageView) holder.imageview;*/
                    mNetworkImageView.setDefaultImageResId(R.drawable.ic_excel);
                    mNetworkImageView.setAdjustViewBounds(true);
                    mNetworkImageView.setImageUrl(null, null);
                    holder.image_name.setVisibility(View.VISIBLE);
                    holder.image_name.setText(pdf_name[length - 1].toString());
                } else if ((thumbImage.get(position).get("Personal3").contains(".doc") || thumbImage.get(position).get("Personal3").contains(".docx")) &&
                        !thumbImage.get(position).get("Personal3").contains(".xls") && !thumbImage.get(position).get("Personal3").contains(".pdf")
                        && !thumbImage.get(position).get("Personal3").contains(".txt")) {
                    String[] pdf_name = thumbImage.get(position).get("Personal3").split("/");
                    int length = pdf_name.length;
                    holder.folder_name.setVisibility(View.GONE);
                    holder.imageview.setVisibility(View.VISIBLE);
                    holder.image_name.setVisibility(View.VISIBLE);
                    holder.image_name.setText(pdf_name[length - 1].toString());
                 /*   mNetworkImageView = (NetworkImageView) holder.imageview;*/
                    mNetworkImageView.setDefaultImageResId(R.drawable.ic_doc);
                    mNetworkImageView.setAdjustViewBounds(true);
                    mNetworkImageView.setImageUrl(null, null);
                } else if ((thumbImage.get(position).get("Personal3").contains(".txt")) &&
                        !thumbImage.get(position).get("Personal3").contains(".xls") &&
                        !thumbImage.get(position).get("Personal3").contains(".pdf") && !thumbImage.get(position).get("Personal3").contains(".doc")) {
                    String[] pdf_name = thumbImage.get(position).get("Personal3").split("/");
                    int length = pdf_name.length;
                    holder.folder_name.setVisibility(View.GONE);
                    holder.imageview.setVisibility(View.VISIBLE);
                    holder.image_name.setVisibility(View.VISIBLE);
                    holder.image_name.setText(pdf_name[length - 1].toString());
                 /*   mNetworkImageView = (NetworkImageView) holder.imageview;*/
                    mNetworkImageView.setDefaultImageResId(R.drawable.ic_text);
                    mNetworkImageView.setAdjustViewBounds(true);
                    mNetworkImageView.setImageUrl(null, null);
                } else {
                    holder.folder_name.setVisibility(View.GONE);
                    holder.imageview.setVisibility(View.GONE);
                    holder.image_name.setVisibility(View.GONE);
                }
            }

            holder.checkbox.setChecked(thumbnailsselection[position]);
            holder.id = position;
            return convertView;
        }
    }

    class ViewHolder {
        ImageView imageview;
        //  ImageView thumbImage2;
        CheckBox checkbox;
        TextView folder_name;
        TextView image_name;
        int id;
    }

    public String getPath(Uri uri, Activity activity) {

        String[] projection = {MediaColumns.DATA};
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter f = new IntentFilter();
        f.addAction(UploadService.UPLOAD_STATE_CHANGED_ACTION);
        registerReceiver(uploadStateReceiver, f);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(uploadStateReceiver);
        super.onStop();
    }

    private BroadcastReceiver uploadStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            // Toast.makeText(Filevault.this,"Image Uploaded Successfully"
            // ,Toast.LENGTH_LONG ).show();
            System.out.println(b.getString(UploadService.MSG_EXTRA));
            int percent = b.getInt(UploadService.PERCENT_EXTRA);
            if(bar!=null) {
                bar.setIndeterminate(percent < 0);
                bar.setProgress(percent);
            }
        }
    };

    private String getPathFromContentUri(Uri uri) {
        String path = uri.getPath();
        if (uri.toString().startsWith("content://")) {
            String[] projection = {MediaColumns.DATA};
            ContentResolver cr = getApplicationContext().getContentResolver();
            Cursor cursor = cr.query(uri, projection, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        path = cursor.getString(0);
                    }
                } finally {
                    cursor.close();
                }
            }

        }
        return path;
    }

    static void refresh() {

        //   thumbImage.clear();
        imageName.clear();
        originalVaultlist.clear();

     /*   queue.add(jr);*/
        req.add(s3jr);


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            if (requestCode == PICK_FROM_GALLERY) {

                Uri selectedImageUri = data.getData();

                String path = getPathFromContentUri(selectedImageUri);
                System.out.println(path);

                File imageFile = new File(path);
                String path1=imageFile.getAbsolutePath();
                String splitfo_lenthcheck []=path1.split("/");
                int filenamelength=splitfo_lenthcheck[splitfo_lenthcheck.length-1].length();
                long check = ((imageFile.length() / 1024));
                if (check < 10000&&filenamelength<99) {

                    String splitstr[];
                    String chosenimg = "";
                    String stringcheck = "", exhistimg = "false";
                    int leangth = 0;
                    if (path.contains("/")) {
                        splitstr = path.split("/");
                        chosenimg = splitstr[splitstr.length - 1];
                    }
                    for (int i = 0; i < thumbImage.size(); i++) {
                        String listsplitstr[] = thumbImage.get(i).get("Personal3").split("/");
                        if (listsplitstr[listsplitstr.length - 1].contains(chosenimg.substring(0, chosenimg.length() - 4))) {
                            if (leangth < listsplitstr[listsplitstr.length - 1].length()) {

                                stringcheck = listsplitstr[listsplitstr.length - 1];
                                leangth = listsplitstr[listsplitstr.length - 1].length();
                                exhistimg = "true";
                            }

                        }
                    }


                    Intent intent = new Intent(this, UploadService.class);
                    intent.putExtra(UploadService.ARG_FILE_PATH, path);
                    intent.putExtra("add_path", "");
                    intent.putExtra(UploadService.uploadfrom, "");
                    intent.putExtra("exhistimg", exhistimg);
                    intent.putExtra("stringcheck", stringcheck);
                    startService(intent);

                    System.out.println("After Service");

                    String tempPath = getPath(selectedImageUri, Filevault.this);
                    Bitmap bm;
                    BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                    btmapOptions.inSampleSize = 4;
                    bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
                    // vault_adapter.notifyDataSetChanged();
                    if (bm != null) {

                        System.out.println("in onactivity");
                        byteArrayOutputStream = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byteArray = byteArrayOutputStream.toByteArray();

                        pic = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        picname = "b.jpg";
                        pic = "data:image/jpeg;base64," + pic;
                        //  vault_adapter.notifyDataSetChanged();

                    }

                } else {

                    Toast.makeText(this, "Image should be less than 10 mb.", Toast.LENGTH_LONG).show();

                }

            }
            if (requestCode == PICK_FROM_CAMERA) {

                Uri selectedImageUri = Imguri;
                String path = getPathFromContentUri(selectedImageUri);
                System.out.println(path);
                File imageFile = new File(path);
                long check = ((imageFile.length() / 1024));

                if (check < 10000) {
                    String[]  splitstr;
                    String  chosenimg="";
                    String stringcheck = "", exhistimg = "false";
                    int leangth = 0;
                    if (path.contains("/")) {
                      splitstr = imageFile.getAbsolutePath().split("/");
                        chosenimg = splitstr[splitstr.length - 1];
                    }
                    for (int i = 0; i < thumbImage.size(); i++) {
                        String listsplitstr[] = thumbImage.get(i).get("Personal3").split("/");
                        if (listsplitstr[listsplitstr.length - 1].contains(chosenimg.substring(0, chosenimg.length() - 4))) {
                            if (leangth < listsplitstr[listsplitstr.length - 1].length()) {

                                stringcheck = listsplitstr[listsplitstr.length - 1];
                                leangth = listsplitstr[listsplitstr.length - 1].length();
                                exhistimg = "true";
                            }

                        }
                    }
                    if (check != 0) {
                        preventRotation(path);
                        Intent intent = new Intent(this, UploadService.class);
                        intent.putExtra(UploadService.ARG_FILE_PATH, path);
                        intent.putExtra(UploadService.uploadfrom, "");
                        intent.putExtra("add_path", "");
                        intent.putExtra(UploadService.uploadfrom, "");
                        intent.putExtra("exhistimg", exhistimg);
                        intent.putExtra("stringcheck", stringcheck);
                        startService(intent);

                        ContentResolver cr = getContentResolver();
                        Bitmap bitmap;
                        bitmap = MediaStore.Images.Media.getBitmap(cr, selectedImageUri);
                        byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                        byteArray = byteArrayOutputStream.toByteArray();
                        pic = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        pic = "data:image/jpeg;base64," + pic;
                        picname = "camera.jpg";

                       // finish();
                        startActivity(getIntent());
                    }
                } else {
                    Toast.makeText(this, "Image should be less than 10 mb.", Toast.LENGTH_LONG).show();
                }


            }
            super.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete, menu);
        menu_toggle = menu;
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                if (!toggle_move) {
          /*  super.onBackPressed();*/
                    Intent intent = new Intent(getApplicationContext(), logout.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    thumbImage.clear();
                    originalVaultlist.clear();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                } else if (!view_list) {
                    list_header.setVisibility(View.VISIBLE);
                    list_header2.setVisibility(View.GONE);
                    vault_adapter = new Vault_adapter(Filevault.this, thumbImage, false, patientId, "");
                    vault_list.setAdapter(vault_adapter);
                    toggle_move = false;
                    check_para = 0;
                    select_times = 0;
                    menu_toggle.findItem(R.id.action_delete).setVisible(false);
                    menu_toggle.findItem(R.id.save).setVisible(false);
                    menu_toggle.findItem(R.id.action_move).setVisible(false);
                    menu_toggle.findItem(R.id.action_home).setVisible(true);
                    thumbnailsselection = new boolean[thumbImage.size()];
                } else {
           /* super.onBackPressed();*/
                    Intent intent = new Intent(getApplicationContext(), logout.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    thumbImage.clear();
                    originalVaultlist.clear();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                }
                return true;

            case R.id.select_all:

                if (select_times != 0 && !view_list) {
                    if (check % 2 == 0) {

                        for (int i = 0; i < thumbnailsselection.length; i++) {

                            thumbnailsselection[i] = true;
                            menu_toggle.findItem(R.id.action_delete).setVisible(true);
                            menu_toggle.findItem(R.id.save).setVisible(true);
                            menu_toggle.findItem(R.id.action_move).setVisible(true);
                            menu_toggle.findItem(R.id.action_home).setVisible(false);
                        }
                    } else {
                        for (int i = 0; i < thumbnailsselection.length; i++) {

                            thumbnailsselection[i] = false;
                            menu_toggle.findItem(R.id.action_delete).setVisible(false);
                            menu_toggle.findItem(R.id.save).setVisible(false);
                            menu_toggle.findItem(R.id.action_move).setVisible(false);
                            menu_toggle.findItem(R.id.action_home).setVisible(true);
                        }
                    }
                    check++;
                } else if (view_list) {
                    if (check_grid % 2 == 0) {

                        for (int i = 0; i < thumbnailsselection.length; i++) {
                            thumbnailsselection[i] = true;
                            menu_toggle.findItem(R.id.action_delete).setVisible(true);
                            menu_toggle.findItem(R.id.save).setVisible(true);
                            menu_toggle.findItem(R.id.action_move).setVisible(true);
                            menu_toggle.findItem(R.id.action_home).setVisible(false);
                        }
                    } else {
                        for (int i = 0; i < thumbnailsselection.length; i++) {
                            thumbnailsselection[i] = false;
                            menu_toggle.findItem(R.id.action_delete).setVisible(false);
                            menu_toggle.findItem(R.id.save).setVisible(false);
                            menu_toggle.findItem(R.id.action_move).setVisible(false);
                            menu_toggle.findItem(R.id.action_home).setVisible(true);
                        }
                    }
                    check_grid++;
                }
                select_times = 1;
                toggle_move = true;
                check_para = 1;
                imageAdapter = new ImageAdapter();
                gridView.setAdapter(imageAdapter);
                if (!view_list) {
                    vault_delete_adapter = new Vault_delete_adapter(Filevault.this, thumbImage, view_list, patientId, thumbnailsselection, "");
                    vault_list.setAdapter(vault_delete_adapter);
                  /*  vault_delete_adapter.notifyDataSetChanged();*/
                    list_header2.setVisibility(View.VISIBLE);
                    list_header.setVisibility(View.GONE);
                    menu_toggle.findItem(R.id.action_delete).setVisible(true);
                    menu_toggle.findItem(R.id.save).setVisible(true);
                    menu_toggle.findItem(R.id.action_move).setVisible(true);
                    menu_toggle.findItem(R.id.action_home).setVisible(false);
                }
                return true;

            case R.id.action_home:
                // try {
                // File myFile = new File("/sdcard/mysdfile.txt");
                // myFile.createNewFile();
                //
                // FileOutputStream fOut = new FileOutputStream(myFile);
                // OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                // myOutWriter.write(sendData.toString());
                // myOutWriter.close();
                //
                // fOut.close();
                // Toast.makeText(getBaseContext(),
                // "Done writing SD 'mysdfile.txt'",
                // Toast.LENGTH_SHORT).show();
                // } catch (Exception e) {
                // Toast.makeText(getBaseContext(), e.getMessage(),
                // Toast.LENGTH_SHORT).show();
                // }

                Intent intent = new Intent(getApplicationContext(), logout.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
                return true;

            case R.id.save:
                checkdialog = 0;
                for (int i = 0; i < thumbnailsselection.length; i++) {
                    if (thumbnailsselection[i]) {
                        checkdialog = 1;
                        toggle_move = false;
                        break;
                    }
                }
                if (toggle_move && check_para == 1) {
                    Toast.makeText(Filevault.this, "Please select file(s).", Toast.LENGTH_SHORT).show();
                } else if (toggle_move && checkdialog == 0) {
                    list_header.setVisibility(View.VISIBLE);
                    list_header2.setVisibility(View.GONE);
                    vault_adapter = new Vault_adapter(Filevault.this, thumbImage, false, patientId, "");
                    vault_list.setAdapter(vault_adapter);
                    vault_list.setSelection(position_scroll);
                    thumbnailsselection = new boolean[thumbImage.size()];
                    toggle_move = false;
                } else if (!view_list) {
                    if (!view_list /*&& !toggle_move */ || checkdialog == 1) {
                        for (int i = 0; i < thumbnailsselection.length; i++) {
                            if (thumbnailsselection[i]) {
                                position_scroll = i;
                            }
                        }
                        vault_delete_adapter = new Vault_delete_adapter(Filevault.this, thumbImage, view_list, patientId, thumbnailsselection, "");
                        vault_list.setAdapter(vault_delete_adapter);
                        vault_list.setSelection(position_scroll);
                        vault_delete_adapter.notifyDataSetChanged();
                        list_header2.setVisibility(View.VISIBLE);
                        list_header.setVisibility(View.GONE);
                        toggle_move = true;
                        checkdialog = 0;
                        for (int i = 0; i < thumbnailsselection.length; i++) {
                            if (thumbnailsselection[i]) {
                                checkdialog = 1;
                                //toggle_move = false;
                                break;
                            }
                        }

                        if (checkdialog == 1) {

                            AlertDialog dialog = new AlertDialog.Builder(Filevault.this).create();
                            dialog.setTitle("Save");
                            dialog.setMessage("Are you sure you want to save the selected file(s)?");

                            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int id) {
                                   /* vault_adapter = new Vault_adapter(Filevault.this, thumbImage, false, patientId, "");
                                    vault_list.setAdapter(vault_adapter);
                                    vault_list.setSelection(position_scroll);
                                    thumbnailsselection = new boolean[thumbImage.size()];
                                    imageAdapter = new ImageAdapter();
                                    gridView.setAdapter(imageAdapter);
                                    menu_toggle.findItem(R.id.action_move).setVisible(false);
                                    menu_toggle.findItem(R.id.save).setVisible(false);
                                    menu_toggle.findItem(R.id.action_delete).setVisible(false);
                                    menu_toggle.findItem(R.id.action_home).setVisible(true);*/
                                    dialog.dismiss();
                                    check_para = 0;
                                    /*toggle_move = false;*/

                                }
                            });

                            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    ipos = 0;
                                    toggle_move = false;
                                    Toast.makeText(Filevault.this, "Image(s) would be saved on " + path, Toast.LENGTH_SHORT).show();
                                    check_para = 0;
                                    for (int i = 0; i < thumbnailsselection.length; i++) {


                                        if (thumbnailsselection[i]) {

                                            ImageRequest ir = new ImageRequest("https://files.cloudchowk.com/" + thumbImage.get(i).get("Personal3"),
                                                    new Response.Listener<Bitmap>() {

                                                        @Override
                                                        public void onResponse(Bitmap response) {
                                                            String fname = "";

                                                            ipos++;
                                                            final Bitmap newbitMap = response;

                                                            if (newbitMap != null) {
                                                                Calendar cal = Calendar.getInstance();
                                                                File myDir = new File(path);
                                                                myDir.mkdirs();
                                                                fname = "Image-" + String.valueOf(cal.getTimeInMillis()) + ".jpg";
                                                                File file = new File(myDir, fname);
                                                                if (file.exists())
                                                                    file.delete();
                                                                try {
                                                                    FileOutputStream out = new FileOutputStream(file);
                                                                    newbitMap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                                                    out.flush();
                                                                    out.close();

                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }

                                                                if (Build.VERSION.SDK_INT >= 19) {
                                                                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                                                    File f = new File(path, fname);
                                                                    Uri contentUri = Uri.fromFile(f);
                                                                    mediaScanIntent.setData(contentUri);
                                                                    sendBroadcast(mediaScanIntent);
                                                                } else {
                                                                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
                                                                            + Environment.getExternalStorageDirectory())));
                                                                }

                                                            }

                                                            nHandler.createSimpleNotification(Filevault.this, ipos, fname);

                                                        }
                                                    }, 0, 0, null, null);

                                            queue3.add(ir);

                                        }

                                    }

                                }
                            });
                            dialog.show();

                        } else {
                            Toast.makeText(Filevault.this, "Please select file(s).", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    checkdialog = 0;
                    for (int i = 0; i < thumbnailsselection.length; i++) {
                        if (thumbnailsselection[i]) {
                            checkdialog = 1;
                            break;
                        }
                    }

                    if (checkdialog == 1) {

                        AlertDialog dialog = new AlertDialog.Builder(Filevault.this).create();
                        dialog.setTitle("Save");
                        dialog.setMessage("Are you sure you want to save the selected file(s)?");

                        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                               /* vault_adapter = new Vault_adapter(Filevault.this, thumbImage, false, patientId, "");
                                vault_list.setAdapter(vault_adapter);
                                vault_list.setSelection(position_scroll);
                                thumbnailsselection = new boolean[thumbImage.size()];
                                imageAdapter = new ImageAdapter();
                                gridView.setAdapter(imageAdapter);
                                menu_toggle.findItem(R.id.action_move).setVisible(false);
                                menu_toggle.findItem(R.id.save).setVisible(false);
                                menu_toggle.findItem(R.id.action_delete).setVisible(false);
                                menu_toggle.findItem(R.id.action_home).setVisible(true);*/
                                dialog.dismiss();
                                // toggle_move = false;

                            }
                        });

                        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ipos = 0;
                                toggle_move = false;
                                Toast.makeText(Filevault.this, "Image(s) would be saved on " + path, Toast.LENGTH_SHORT).show();

                                for (int i = 0; i < thumbnailsselection.length; i++) {

                                    if (thumbnailsselection[i]) {

                                        ImageRequest ir = new ImageRequest("https://files.cloudchowk.com/" + thumbImage.get(i).get("Personal3"),
                                                new Response.Listener<Bitmap>() {

                                                    @Override
                                                    public void onResponse(Bitmap response) {
                                                        String fname = "";

                                                        ipos++;
                                                        final Bitmap newbitMap = response;

                                                        if (newbitMap != null) {
                                                            Calendar cal = Calendar.getInstance();
                                                            File myDir = new File(path);
                                                            myDir.mkdirs();
                                                            fname = "Image-" + String.valueOf(cal.getTimeInMillis()) + ".jpg";
                                                            File file = new File(myDir, fname);
                                                            if (file.exists())
                                                                file.delete();
                                                            try {
                                                                FileOutputStream out = new FileOutputStream(file);
                                                                newbitMap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                                                out.flush();
                                                                out.close();

                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                            if (Build.VERSION.SDK_INT >= 19) {
                                                                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                                                File f = new File(path, fname);
                                                                Uri contentUri = Uri.fromFile(f);
                                                                mediaScanIntent.setData(contentUri);
                                                                sendBroadcast(mediaScanIntent);
                                                            } else {
                                                                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
                                                                        + Environment.getExternalStorageDirectory())));
                                                            }

                                                        }

                                                        nHandler.createSimpleNotification(Filevault.this, ipos, fname);

                                                    }
                                                }, 0, 0, null, null);

                                        queue3.add(ir);

                                    }

                                }

                            }
                        });
                        dialog.show();

                    } else {
                        Toast.makeText(Filevault.this, "Please select file(s).", Toast.LENGTH_SHORT).show();
                    }

                }
                return true;

            case R.id.action_delete:
                checkdialog = 0;
                toggle_move = false;
                for (int i = 0; i < thumbnailsselection.length; i++) {
                    if (thumbnailsselection[i]) {
                        checkdialog = 1;
                        toggle_move = false;
                        break;
                    }
                }
                if (toggle_move && check_para == 1) {
                    Toast.makeText(Filevault.this, "Please select file(s).", Toast.LENGTH_SHORT).show();
                } /*else if (toggle_move && checkdialog == 0) {
                    list_header.setVisibility(View.VISIBLE);
                    list_header2.setVisibility(View.GONE);
                    vault_adapter = new Vault_adapter(Filevault.this, thumbImage, false, patientId, "");
                    vault_list.setAdapter(vault_adapter);
                    thumbnailsselection = new boolean[thumbImage.size()];
                    toggle_move = false;
                }*/ else if (!view_list) {
                    if (!view_list /*&& !toggle_move*/ || checkdialog == 1) {
                        for (int i = 0; i < thumbnailsselection.length; i++) {
                            if (thumbnailsselection[i]) {
                                position_scroll = i;
                            }
                        }
                        list_header.setVisibility(View.GONE);
                        list_header2.setVisibility(View.VISIBLE);
                        vault_delete_adapter = new Vault_delete_adapter(Filevault.this, thumbImage, view_list, patientId, thumbnailsselection, "");
                        vault_list.setAdapter(vault_delete_adapter);
                        vault_list.setSelection(position_scroll);
                        toggle_move = true;
                        //  vault_delete_adapter.notifyDataSetChanged();
                        //checkdialog = 0;
                        if (checkdialog == 1) {

                            AlertDialog dialog = new AlertDialog.Builder(Filevault.this).create();
                            dialog.setTitle("Delete");
                            dialog.setMessage("Are you sure you want to delete the selected file(s)?");

                            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int id) {
                                    /*vault_adapter = new Vault_adapter(Filevault.this, thumbImage, false, patientId, "");
                                    vault_list.setAdapter(vault_adapter);
                                    vault_list.setSelection(position_scroll);
                                    thumbnailsselection = new boolean[thumbImage.size()];
                                    imageAdapter = new ImageAdapter();
                                    gridView.setAdapter(imageAdapter);*/
                                    dialog.dismiss();
                                    check_para = 0;
                                    select_times = 0;
                                   /* toggle_move = false;
                                    menu_toggle.findItem(R.id.action_move).setVisible(false);
                                    menu_toggle.findItem(R.id.save).setVisible(false);
                                    menu_toggle.findItem(R.id.action_delete).setVisible(false);
                                    menu_toggle.findItem(R.id.action_home).setVisible(true);*/

                                }
                            });
                            toggle_move = true;
                            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    pd = new ProgressDialog(Filevault.this);
                                    pd.setMessage("Deleting .....");
                                    pd.show();
                                    toggle_move = false;
                                    JSONArray array = new JSONArray();
                                    check_para = 0;
                                    select_times = 0;
                                    for (int i = 0; i < thumbnailsselection.length; i++) {
                                        JSONObject imageobject = new JSONObject();
                                        if (thumbnailsselection[i]) {
                                            try {
                                                //imageId.add(patientId+"/FileVault/"+thumbImage.get(i).get("folder_name"));
                                                if (!thumbImage.get(i).get("Personal3").contains(".PNG") && !thumbImage.get(i).get("Personal3").contains(".png") &&
                                                        !thumbImage.get(i).get("Personal3").contains(".jpg") && !thumbImage.get(i).get("Personal3").contains(".pdf")
                                                        && !thumbImage.get(i).get("Personal3").contains(".xls") && !thumbImage.get(i).get("Personal3").contains(".doc")) {
                                                    if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                    } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                    } else {
                                                        imageobject.put("Key", patientId + "/FileVault/Personal/" + thumbImage.get(i).get("Personal3"));
                                                    }
                                                    imageobject.put("Type", "1");
                                                    imageobject.put("ThumbFile", "");
                                                    imageobject.put("Status", "");
                                                } else {
                                                    if (thumbImage.get(i).get("Personal3").contains(".png")) {
                                                        String thumbimg = thumbImage.get(i).get("Personal3").replaceAll("\\.png", "_thumb.png");
                                                        if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/")) {
                                                            imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                        } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                                            imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                        } else {
                                                            imageobject.put("Key", patientId + "/FileVault/Personal/" + thumbImage.get(i).get("Personal3"));
                                                        }
                                                        imageobject.put("Type", "0");
                                                        if (thumbimg.startsWith(patientId + "/FileVault/Personal/")) {
                                                            imageobject.put("ThumbFile", thumbimg);
                                                        } else if (thumbimg.contains(patientId + "/FileVault/Personal/")) {
                                                            imageobject.put("ThumbFile", thumbimg);
                                                        } else {
                                                            imageobject.put("ThumbFile", patientId + "/FileVault/Personal/" + thumbimg);
                                                        }
                                                        imageobject.put("Type", "0");
                                                        imageobject.put("Status", "");
                                                    } else if (thumbImage.get(i).get("Personal3").contains(".PNG")) {
                                                        String thumbimg = thumbImage.get(i).get("Personal3").replaceAll("\\.PNG", "_thumb.PNG");
                                                        if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/")) {
                                                            imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                        } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                                            imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                        } else {
                                                            imageobject.put("Key", patientId + "/FileVault/Personal/" + thumbImage.get(i).get("Personal3"));
                                                        }
                                                        imageobject.put("Type", "0");
                                                        if (thumbimg.startsWith(patientId + "/FileVault/Personal/")) {
                                                            imageobject.put("ThumbFile", thumbimg);
                                                        } else if (thumbimg.contains(patientId + "/FileVault/Personal/")) {
                                                            imageobject.put("ThumbFile", thumbimg);
                                                        } else {
                                                            imageobject.put("ThumbFile", patientId + "/FileVault/Personal/" + thumbimg);
                                                        }
                                                        imageobject.put("Type", "0");
                                                        imageobject.put("Status", "");
                                                    } else if (thumbImage.get(i).get("Personal3").contains(".jpg")) {
                                                        String thumbimg = thumbImage.get(i).get("Personal3").replaceAll("\\.jpg", "_thumb.jpg");
                                                        if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/")) {
                                                            imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                        } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                                            imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                        } else {
                                                            imageobject.put("Key", patientId + "/FileVault/Personal/" + thumbImage.get(i).get("Personal3"));
                                                        }
                                                        imageobject.put("Type", "0");
                                                        if (thumbimg.startsWith(patientId + "/FileVault/Personal/")) {
                                                            imageobject.put("ThumbFile", thumbimg);
                                                        } else if (thumbimg.contains(patientId + "/FileVault/Personal/")) {
                                                            imageobject.put("ThumbFile", thumbimg);
                                                        } else {
                                                            imageobject.put("ThumbFile", patientId + "/FileVault/Personal/" + thumbimg);
                                                        }
                                                        imageobject.put("Type", "0");
                                                        imageobject.put("Status", "");
                                                    } else if (thumbImage.get(i).get("Personal3").contains(".JPG")) {
                                                        String thumbimg = thumbImage.get(i).get("Personal3").replaceAll("\\.JPG", "_thumb.JPG");
                                                        if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/")) {
                                                            imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                        } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                                            imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                        } else {
                                                            imageobject.put("Key", patientId + "/FileVault/Personal/" + thumbImage.get(i).get("Personal3"));
                                                        }
                                                        imageobject.put("Type", "0");
                                                        if (thumbimg.startsWith(patientId + "/FileVault/Personal/")) {
                                                            imageobject.put("ThumbFile", thumbimg);
                                                        } else if (thumbimg.contains(patientId + "/FileVault/Personal/")) {
                                                            imageobject.put("ThumbFile", thumbimg);
                                                        } else {
                                                            imageobject.put("ThumbFile", patientId + "/FileVault/Personal/" + thumbimg);
                                                        }
                                                        imageobject.put("Type", "0");
                                                        imageobject.put("Status", "");
                                                    } else {
                                                        if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/")) {
                                                            imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                        } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                                            imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                        } else {
                                                            imageobject.put("Key", patientId + "/FileVault/Personal/" + thumbImage.get(i).get("Personal3"));
                                                        }
                                                        imageobject.put("Type", "0");
                                                        imageobject.put("ThumbFile", "");
                                                        imageobject.put("Status", "");
                                                    }

                                                }

                                         /*   imageIdsToBeSent = imageIdsToBeSent + subArrayImage.getJSONObject(i).getString("ImageId")
                                                    + ",";
                                            System.out.println(i);*/
                                            } catch (JSONException e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }
                                            array.put(imageobject);
                                        }


                                    }


                                    System.out.println(array);

                                    queue2 = Volley.newRequestQueue(Filevault.this);

                                    sendData = new JSONObject();
                                    try {
                                        sendData.put("ObjectList", array);
                                        sendData.put("UserId", patientId);
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }

				/*		String url = Services.init + "PatientModule/PatientService.asmx/DeletePatientFiles";*/
                                    StaticHolder sttc_holdr = new StaticHolder(Filevault.this, StaticHolder.Services_static.DeleteObject);
                                    String url = sttc_holdr.request_Url();
                                    jr2 = new JsonObjectRequest(Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            System.out.println(response);

                                            try {
                                                Toast.makeText(Filevault.this, " Item(s) successfully deleted.", Toast.LENGTH_SHORT)
                                                        .show();
                                                //  S3Objects.clear();
                                                pd.dismiss();
                                                refresh();
                                            } catch (Exception e) {
                                                // TODO Auto-generated catch
                                                // block
                                                e.printStackTrace();
                                            }

                                            // queue.add(jr);

                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            pd.dismiss();
                                            Toast.makeText(Filevault.this, error.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });/* {
                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        Map<String, String> headers = new HashMap<String, String>();
                                        headers.put("Cookie", Services.hoja);
                                        return headers;
                                    }
                                };*/
                                    queue2.add(jr2);

                                }
                            });
                            dialog.show();
                        } else {
                            Toast.makeText(Filevault.this, "Please select file(s).", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    int checkdialog = 0;
                    for (int i = 0; i < thumbnailsselection.length; i++) {
                        if (thumbnailsselection[i]) {
                            checkdialog = 1;
                            break;
                        }
                    }

                    if (checkdialog == 1) {

                        AlertDialog dialog = new AlertDialog.Builder(Filevault.this).create();
                        dialog.setTitle("Delete");
                        dialog.setMessage("Are you sure you want to delete the selected file(s)?");

                        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                               /* vault_adapter = new Vault_adapter(Filevault.this, thumbImage, false, patientId, "");
                                vault_list.setAdapter(vault_adapter);
                                vault_list.setSelection(position_scroll);
                                imageAdapter = new ImageAdapter();
                                gridView.setAdapter(imageAdapter);
                                thumbnailsselection = new boolean[thumbImage.size()];
                                menu_toggle.findItem(R.id.action_move).setVisible(false);
                                menu_toggle.findItem(R.id.save).setVisible(false);
                                menu_toggle.findItem(R.id.action_delete).setVisible(false);
                                menu_toggle.findItem(R.id.action_home).setVisible(true);*/
                                dialog.dismiss();
                                //toggle_move = false;

                            }
                        });

                        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                JSONArray array = new JSONArray();
                                pd = new ProgressDialog(Filevault.this);
                                pd.setMessage("Deleting .....");
                                pd.show();
                                toggle_move = false;
                                for (int i = 0; i < thumbnailsselection.length; i++) {
                                    JSONObject imageobject = new JSONObject();
                                    if (thumbnailsselection[i]) {

                                        try {
                                            //imageId.add(patientId+"/FileVault/"+thumbImage.get(i).get("folder_name"));

                                            if (!thumbImage.get(i).get("Personal3").contains(".PNG") && !thumbImage.get(i).get("Personal3").contains(".png") &&
                                                    !thumbImage.get(i).get("Personal3").contains(".jpg") && !thumbImage.get(i).get("Personal3").contains(".pdf")
                                                    && !thumbImage.get(i).get("Personal3").contains(".xls") && !thumbImage.get(i).get("Personal3").contains(".doc")) {
                                                if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/")) {
                                                    imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                                    imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                } else {
                                                    imageobject.put("Key", patientId + "/FileVault/Personal/" + thumbImage.get(i).get("Personal3"));
                                                }
                                                imageobject.put("Type", "1");
                                                imageobject.put("ThumbFile", "");
                                                imageobject.put("Status", "");
                                            } else {
                                                if (thumbImage.get(i).get("Personal3").contains(".png")) {
                                                    String thumbimg = thumbImage.get(i).get("Personal3").replaceAll("\\.png", "_thumb.png");
                                                    if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                    } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                    } else {
                                                        imageobject.put("Key", patientId + "/FileVault/Personal/" + thumbImage.get(i).get("Personal3"));
                                                    }
                                                    imageobject.put("Type", "0");
                                                    if (thumbimg.startsWith(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("ThumbFile", thumbimg);
                                                    } else if (thumbimg.contains(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("ThumbFile", thumbimg);
                                                    } else {
                                                        imageobject.put("ThumbFile", patientId + "/FileVault/Personal/" + thumbimg);
                                                    }
                                                    imageobject.put("Type", "0");
                                                    imageobject.put("Status", "");
                                                } else if (thumbImage.get(i).get("Personal3").contains(".PNG")) {
                                                    String thumbimg = thumbImage.get(i).get("Personal3").replaceAll("\\.PNG", "_thumb.PNG");
                                                    if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                    } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                    } else {
                                                        imageobject.put("Key", patientId + "/FileVault/Personal/" + thumbImage.get(i).get("Personal3"));
                                                    }
                                                    imageobject.put("Type", "0");
                                                    if (thumbimg.startsWith(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("ThumbFile", thumbimg);
                                                    } else if (thumbimg.contains(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("ThumbFile", thumbimg);
                                                    } else {
                                                        imageobject.put("ThumbFile", patientId + "/FileVault/Personal/" + thumbimg);
                                                    }
                                                    imageobject.put("Type", "0");
                                                    imageobject.put("Status", "");
                                                } else if (thumbImage.get(i).get("Personal3").contains(".jpg")) {
                                                    String thumbimg = thumbImage.get(i).get("Personal3").replaceAll("\\.jpg", "_thumb.jpg");
                                                    if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                    } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                    } else {
                                                        imageobject.put("Key", patientId + "/FileVault/Personal/" + thumbImage.get(i).get("Personal3"));
                                                    }
                                                    imageobject.put("Type", "0");
                                                    if (thumbimg.startsWith(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("ThumbFile", thumbimg);
                                                    } else if (thumbimg.contains(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("ThumbFile", thumbimg);
                                                    } else {
                                                        imageobject.put("ThumbFile", patientId + "/FileVault/Personal/" + thumbimg);
                                                    }
                                                    imageobject.put("Type", "0");
                                                    imageobject.put("Status", "");
                                                } else if (thumbImage.get(i).get("Personal3").contains(".JPG")) {
                                                    String thumbimg = thumbImage.get(i).get("Personal3").replaceAll("\\.JPG", "_thumb.JPG");
                                                    if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                    } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                    } else {
                                                        imageobject.put("Key", patientId + "/FileVault/Personal/" + thumbImage.get(i).get("Personal3"));
                                                    }
                                                    imageobject.put("Type", "0");
                                                    if (thumbimg.startsWith(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("ThumbFile", thumbimg);
                                                    } else if (thumbimg.contains(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("ThumbFile", thumbimg);
                                                    } else {
                                                        imageobject.put("ThumbFile", patientId + "/FileVault/Personal/" + thumbimg);
                                                    }
                                                    imageobject.put("Type", "0");
                                                    imageobject.put("Status", "");
                                                } else {
                                                    if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                    } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                    } else {
                                                        imageobject.put("Key", patientId + "/FileVault/Personal/" + thumbImage.get(i).get("Personal3"));
                                                    }
                                                    imageobject.put("Type", "0");
                                                    imageobject.put("ThumbFile", "");
                                                    imageobject.put("Status", "");
                                                }
                                            }

                                         /*   imageIdsToBeSent = imageIdsToBeSent + subArrayImage.getJSONObject(i).getString("ImageId")
                                                    + ",";
                                            System.out.println(i);*/
                                        } catch (Exception e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                        array.put(imageobject);
                                    }

                                }

                                System.out.println(array);

                                queue2 = Volley.newRequestQueue(Filevault.this);

                                sendData = new JSONObject();
                                try {
                                    sendData.put("ObjectList", array);
                                    sendData.put("UserId", patientId);
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

				/*		String url = Services.init + "PatientModule/PatientService.asmx/DeletePatientFiles";*/
                                StaticHolder sttc_holdr = new StaticHolder(Filevault.this, StaticHolder.Services_static.DeleteObject);
                                String url = sttc_holdr.request_Url();
                                jr2 = new JsonObjectRequest(Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        System.out.println(response);

                                        try {
                                            Toast.makeText(Filevault.this, "Item(s) successfully deleted.", Toast.LENGTH_SHORT)
                                                    .show();
                                            // S3Objects.clear();
                                            pd.dismiss();
                                            refresh();

                                        } catch (Exception e) {
                                            // TODO Auto-generated catch
                                            // block
                                            e.printStackTrace();
                                        }

                                        // queue.add(jr);

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        pd.dismiss();
                                        Toast.makeText(Filevault.this, error.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                queue2.add(jr2);


                            }
                        });
                        dialog.show();
                    } else {
                        Toast.makeText(Filevault.this, "Please select file(s).", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;

            case R.id.action_listview:
                if (!view_list) {
                    check_grid = 0;
                    show_menu = 0;
                    for (int i = 0; i < thumbnailsselection.length; i++) {
                        if (thumbnailsselection[i]) {
                            show_menu = 1;
                            check_grid = 1;
                            break;
                        }
                    }
                    if (show_menu == 1) {
                        menu_toggle.findItem(R.id.action_move).setVisible(true);
                        menu_toggle.findItem(R.id.save).setVisible(true);
                        menu_toggle.findItem(R.id.action_delete).setVisible(true);
                        menu_toggle.findItem(R.id.action_home).setVisible(false);
                    } else {
                        menu_toggle.findItem(R.id.action_move).setVisible(false);
                        menu_toggle.findItem(R.id.save).setVisible(false);
                        menu_toggle.findItem(R.id.action_delete).setVisible(false);
                        menu_toggle.findItem(R.id.action_home).setVisible(true);
                    }
                    gridView.setVisibility(View.VISIBLE);
                    imageAdapter.notifyDataSetChanged();
                   /* vault_adapter = new Vault_adapter(Filevault.this, thumbImage, false, patientId);
                    vault_list.setAdapter(vault_adapter);*/
                    list_header.setVisibility(View.GONE);
                    list_header2.setVisibility(View.GONE);
                    vault_list.setVisibility(View.GONE);
                    menu_toggle.findItem(R.id.action_listview).setIcon(R.drawable.ic_list);
                    view_list = true;
                    refresh_vault1 = true;
                    check_view = "Grid";
                } else {
                    check = 0;
                    show_menu1 = 0;
                    for (int i = 0; i < thumbnailsselection.length; i++) {
                        if (thumbnailsselection[i]) {
                            show_menu1 = 1;
                            check = 1;
                            break;
                        }
                    }
                    if (show_menu1 == 1) {
                        vault_delete_adapter = new Vault_delete_adapter(Filevault.this, thumbImage, view_list, patientId, thumbnailsselection, "");
                        vault_list.setAdapter(vault_delete_adapter);
                        vault_list.setSelection(position_scroll);
                        menu_toggle.findItem(R.id.action_move).setVisible(true);
                        menu_toggle.findItem(R.id.save).setVisible(true);
                        menu_toggle.findItem(R.id.action_delete).setVisible(true);
                        menu_toggle.findItem(R.id.action_home).setVisible(false);
                        list_header.setVisibility(View.GONE);
                        list_header2.setVisibility(View.VISIBLE);
                        vault_list.setVisibility(View.VISIBLE);
                        gridView.setVisibility(View.GONE);
                    } else {
                        list_header.setVisibility(View.VISIBLE);
                        list_header2.setVisibility(View.GONE);
                        vault_adapter = new Vault_adapter(Filevault.this, thumbImage, false, patientId, "");
                        vault_list.setAdapter(vault_adapter);
                        vault_list.setSelection(position_scroll);
                        vault_list.setVisibility(View.VISIBLE);
                        gridView.setVisibility(View.GONE);
                        menu_toggle.findItem(R.id.action_move).setVisible(false);
                        menu_toggle.findItem(R.id.save).setVisible(false);
                        menu_toggle.findItem(R.id.action_delete).setVisible(false);
                        menu_toggle.findItem(R.id.action_home).setVisible(true);
                    }
                    /*list_header.setVisibility(View.VISIBLE);
                    list_header2.setVisibility(View.GONE);
                    vault_adapter = new Vault_adapter(Filevault.this, thumbImage, false, patientId, "");
                    vault_list.setAdapter(vault_adapter);
                    vault_list.setVisibility(View.VISIBLE);
                    gridView.setVisibility(View.GONE);
                    menu_toggle.findItem(R.id.action_listview).setIcon(R.drawable.ic_grid);*/
                    menu_toggle.findItem(R.id.action_listview).setIcon(R.drawable.ic_grid);
                    view_list = false;
                    refresh_vault1 = false;
                    check_view = "List";
                }

                return true;

            case R.id.action_move:
                checkdialog = 0;
                for (int i = 0; i < thumbnailsselection.length; i++) {
                    if (thumbnailsselection[i]) {
                        checkdialog = 1;
                        toggle_move = false;
                        break;
                    }
                }
                if (toggle_move && check_para == 1) {
                    Toast.makeText(Filevault.this, "Please Select file(s).", Toast.LENGTH_SHORT).show();
                } /*else if (*//*toggle_move &&*//* checkdialog == 0) {
                    list_header.setVisibility(View.VISIBLE);
                    list_header2.setVisibility(View.GONE);
                    vault_adapter = new Vault_adapter(Filevault.this, thumbImage, false, patientId, "");
                    vault_list.setAdapter(vault_adapter);
                    thumbnailsselection = new boolean[thumbImage.size()];
                   // toggle_move = false;
                }*/ else if (!view_list) {
                    if (!view_list/* && !toggle_move*/ || checkdialog == 1) {
                        for (int i = 0; i < thumbnailsselection.length; i++) {
                            if (thumbnailsselection[i]) {
                                position_scroll = i;
                            }
                        }
                        list_header.setVisibility(View.GONE);
                        list_header2.setVisibility(View.VISIBLE);
                        vault_delete_adapter = new Vault_delete_adapter(Filevault.this, thumbImage, view_list, patientId, thumbnailsselection, "");
                        vault_list.setAdapter(vault_delete_adapter);
                        vault_list.setSelection(position_scroll);
                        toggle_move = true;
                        //  vault_delete_adapter.notifyDataSetChanged();
                        checkdialog = 0;
                        for (int i = 0; i < thumbnailsselection.length; i++) {
                            if (thumbnailsselection[i]) {
                                checkdialog = 1;
                                //  toggle_move = false;
                                break;
                            }
                        }
                        if (checkdialog == 1) {

                            AlertDialog dialog = new AlertDialog.Builder(Filevault.this).create();
                            dialog.setTitle("Move");
                            dialog.setMessage("Are you sure you want to move the selected file(s)?");

                            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int id) {
                                   /* list_header.setVisibility(View.VISIBLE);
                                    list_header2.setVisibility(View.GONE);
                                    vault_adapter = new Vault_adapter(Filevault.this, thumbImage, false, patientId, "");
                                    vault_list.setAdapter(vault_adapter);
                                    vault_list.setSelection(position_scroll);
                                    imageAdapter = new ImageAdapter();
                                    gridView.setAdapter(imageAdapter);
                                    thumbnailsselection = new boolean[thumbImage.size()];
                                    menu_toggle.findItem(R.id.action_move).setVisible(false);
                                    menu_toggle.findItem(R.id.save).setVisible(false);
                                    menu_toggle.findItem(R.id.action_delete).setVisible(false);
                                    menu_toggle.findItem(R.id.action_home).setVisible(true);*/
                                    dialog.dismiss();
                                    check_para = 0;
                                    select_times = 0;
                                    //  toggle_move = false;

                                }
                            });

                            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alias_thumbImage_folder.clear();
                                    rem_dup_folder = new String[thumbImage.size()];
                                    for (int i = 0; i < thumbnailsselection.length; i++) {
                                        if (thumbnailsselection[i]) {

                                            //imageId.add(patientId+"/FileVault/"+thumbImage.get(i).get("folder_name"));
                                            if (!thumbImage.get(i).get("Personal3").contains(".PNG") && !thumbImage.get(i).get("Personal3").contains(".png") &&
                                                    !thumbImage.get(i).get("Personal3").contains(".jpg")
                                                    && !thumbImage.get(i).get("Personal3").contains(".JPG") && !thumbImage.get(i).get("Personal3").contains(".pdf")
                                                    && !thumbImage.get(i).get("Personal3").contains(".xls") && !thumbImage.get(i).get("Personal3").contains(".doc")) {
                                                rem_dup_folder[i] = thumbImage.get(i).get("Personal3");
                                            }
                                        }
                                    }
                                    check_para = 0;
                                    select_times = 0;
                                    moveFolder1 = foldername();
                                /*JSONArray array = new JSONArray();

                                for (int i = 0; i < thumbnailsselection.length; i++) {
                                    JSONObject imageobject = new JSONObject();
                                    if (thumbnailsselection[i]) {
                                        try {
                                            //imageId.add(patientId+"/FileVault/"+thumbImage.get(i).get("folder_name"));
                                            if (!thumbImage.get(i).get("FileVault2").contains(".PNG") && !thumbImage.get(i).get("FileVault2").contains(".png") &&
                                                    !thumbImage.get(i).get("FileVault2").contains(".jpg") && !thumbImage.get(i).get("FileVault2").contains(".pdf")
                                                    && !thumbImage.get(i).get("FileVault2").contains(".xls") && !thumbImage.get(i).get("FileVault2").contains(".doc")) {
                                                imageobject.put("Key", patientId + "/FileVault/" + thumbImage.get(i).get("FileVault2"));
                                                imageobject.put("Type", "1");
                                                imageobject.put("ThumbFile", "");
                                                imageobject.put("Status", "");
                                            } else {
                                                if (thumbImage.get(i).get("FileVault2").contains(".png")) {
                                                    String thumbimg = thumbImage.get(i).get("FileVault2").replaceAll("\\.png", "_thumb.png");
                                                    imageobject.put("Key", patientId + "/FileVault/" + thumbImage.get(i).get("FileVault2"));
                                                    imageobject.put("Type", "0");
                                                    imageobject.put("ThumbFile", thumbimg);
                                                    imageobject.put("Status", "");
                                                } else if (thumbImage.get(i).get("FileVault2").contains(".PNG")) {
                                                    String thumbimg = thumbImage.get(i).get("FileVault2").replaceAll("\\.PNG", "_thumb.png");
                                                    imageobject.put("Key", patientId + "/FileVault/" + thumbImage.get(i).get("FileVault2"));
                                                    imageobject.put("Type", "0");
                                                    imageobject.put("ThumbFile", thumbimg);
                                                    imageobject.put("Status", "");
                                                } else if (thumbImage.get(i).get("FileVault2").contains(".jpg")) {
                                                    String thumbimg = thumbImage.get(i).get("FileVault2").replaceAll("\\.jpg", "_thumb.png");
                                                    imageobject.put("Key", patientId + "/FileVault/" + thumbImage.get(i).get("FileVault2"));
                                                    imageobject.put("Type", "0");
                                                    imageobject.put("ThumbFile", thumbimg);
                                                    imageobject.put("Status", "");
                                                } else if (thumbImage.get(i).get("FileVault2").contains(".JPG")) {
                                                    String thumbimg = thumbImage.get(i).get("FileVault2").replaceAll("\\.JPG", "_thumb.png");
                                                    imageobject.put("Key", patientId + "/FileVault/" + thumbImage.get(i).get("FileVault2"));
                                                    imageobject.put("Type", "0");
                                                    imageobject.put("ThumbFile", thumbimg);
                                                    imageobject.put("Status", "");
                                                } else {
                                                    imageobject.put("Key", patientId + "/FileVault/" + thumbImage.get(i).get("FileVault2"));
                                                    imageobject.put("Type", "0");
                                                    imageobject.put("ThumbFile", "");
                                                    imageobject.put("Status", "");
                                                }
                                            }

                                         *//*   imageIdsToBeSent = imageIdsToBeSent + subArrayImage.getJSONObject(i).getString("ImageId")
                                                    + ",";
                                            System.out.println(i);*//*
                                        } catch (JSONException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                        array.put(imageobject);
                                    }


                                }


                                System.out.println(array);

                                queue2 = Volley.newRequestQueue(Filevault.this);

                                sendData = new JSONObject();
                                try {
                                    sendData.put("ObjectList", array);
                                    sendData.put("UserId", patientId);
                                    sendData.put("NewPath","");
                                    sendData.put("AbsolutePath","");
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

				*//*		String url = Services.init + "PatientModule/PatientService.asmx/DeletePatientFiles";*//*
                                StaticHolder sttc_holdr = new StaticHolder(Filevault.this, StaticHolder.Services_static.MoveObject);
                                String url = sttc_holdr.request_Url();
                                jr2 = new JsonObjectRequest(Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        System.out.println(response);

                                        try {
                                            Toast.makeText(Filevault.this," Items Deleted", Toast.LENGTH_SHORT)
                                                    .show();
                                            //  S3Objects.clear();
                                            finish();
                                            startActivity(getIntent());
                                        } catch (Exception e) {
                                            // TODO Auto-generated catch
                                            // block
                                            e.printStackTrace();
                                        }

                                        // queue.add(jr);

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(Filevault.this, error.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });*//* {
                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        Map<String, String> headers = new HashMap<String, String>();
                                        headers.put("Cookie", Services.hoja);
                                        return headers;
                                    }
                                };*//*
                                queue2.add(jr2);*/
                                    if (rem_dup_folder != null/* && !rem_dup_folder.equalsIgnoreCase("")*/) {
                                        for (int r = 0; r < moveFolder1.size(); r++) {
                                            for (int l = 0; l < rem_dup_folder.length; l++) {
                                                if (moveFolder1.get(r).get("folder_name").equalsIgnoreCase(rem_dup_folder[l])) {
                                                    moveFolder1.remove(r);
                                                }
                                            }

                                        }
                                    }
                                    alias_thumbImage_folder.addAll(moveFolder1);
                                /*String[] stockArr = new String[moveFolder1.size()];
                                stockArr = moveFolder1.toArray(stockArr);*/
                                /*AlertDialog.Builder builder = new AlertDialog.Builder(Filevault.this);
                                builder.setTitle("Make your selection");
                                builder.setItems(stockArr, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int item) {
                                        //Toast.makeText(Filevault.this,folder_move.get(item).toString(),Toast.LENGTH_SHORT).show();
                                        move_to_folder(moveFolder1.get(item).toString());
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();*/

                                    final Dialog move_dialog = new Dialog(Filevault.this);
                                    move_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    //setting custom layout to dialog
                                    move_dialog.setContentView(R.layout.move_folderlist);
                                    ListView folder_list = (ListView) move_dialog.findViewById(R.id.folder_list);
                                    Button move_btn = (Button) move_dialog.findViewById(R.id.move_btn);
                                    final TextView folder_root = (TextView) move_dialog.findViewById(R.id.folder_root);
                                    folder_root.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                    folder_root.setEnabled(false);
                                    folder_adapter = new Folder_adapter(Filevault.this, moveFolder1, patientId, "");
                                    folder_list.setAdapter(folder_adapter);
                                    folder_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            if (!thumbImage.get(position).get("Personal3").contains(".PNG") && !thumbImage.get(position).get("Personal3").contains(".png") &&
                                                    !thumbImage.get(position).get("Personal3").contains(".jpg") && !thumbImage.get(position).get("Personal3").contains(".JPG")
                                                    && !thumbImage.get(position).get("Personal3").contains(".pdf")
                                                    && !thumbImage.get(position).get("Personal3").contains(".xls") && !thumbImage.get(position).get("Personal3").contains(".doc")) {
                                                Folder_Clicked = moveFolder1.get(position).get("folder_name");
                                                HashKey = moveFolder1.get(position).get("hash_keyvalue");
                                                nextdialog();
                                                move_dialog.dismiss();
                                            }
                                        }
                                    });
                                    move_btn.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (folder_root.getText().toString().trim().equalsIgnoreCase("Root")) {
                                                Toast.makeText(Filevault.this, "Please select a destination folder different from the current one.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    move_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(final DialogInterface arg0) {
                                            //folder_path.clear();
                                        }
                                    });

                                    move_dialog.show();
                                }
                            });
                            dialog.show();
                        } else

                        {
                            Toast.makeText(Filevault.this, "Please select file(s).", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    checkdialog = 0;
                    for (int i = 0; i < thumbnailsselection.length; i++) {
                        if (thumbnailsselection[i]) {
                            checkdialog = 1;
                            break;
                        }
                    }

                    if (checkdialog == 1) {

                        AlertDialog dialog = new AlertDialog.Builder(Filevault.this).create();
                        dialog.setTitle("Move");
                        dialog.setMessage("Are you sure you want to move the selected file(s) or Folder(s)?");

                        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                               /* vault_adapter = new Vault_adapter(Filevault.this, thumbImage, false, patientId, "");
                                vault_list.setAdapter(vault_adapter);
                                vault_list.setSelection(position_scroll);
                                imageAdapter = new ImageAdapter();
                                gridView.setAdapter(imageAdapter);
                                thumbnailsselection = new boolean[thumbImage.size()];
                                menu_toggle.findItem(R.id.action_move).setVisible(false);
                                menu_toggle.findItem(R.id.save).setVisible(false);
                                menu_toggle.findItem(R.id.action_delete).setVisible(false);
                                menu_toggle.findItem(R.id.action_home).setVisible(true);*/
                                dialog.dismiss();
                                //toggle_move = false;

                            }
                        });

                        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alias_thumbImage_folder.clear();
                                rem_dup_folder = new String[thumbImage.size()];
                                for (int i = 0; i < thumbnailsselection.length; i++) {
                                    if (thumbnailsselection[i]) {

                                        //imageId.add(patientId+"/FileVault/"+thumbImage.get(i).get("folder_name"));
                                        if (!thumbImage.get(i).get("Personal3").contains(".PNG") && !thumbImage.get(i).get("Personal3").contains(".png") &&
                                                !thumbImage.get(i).get("Personal3").contains(".jpg")
                                                && !thumbImage.get(i).get("Personal3").contains(".JPG") && !thumbImage.get(i).get("Personal3").contains(".pdf")
                                                && !thumbImage.get(i).get("Personal3").contains(".xls") && !thumbImage.get(i).get("Personal3").contains(".doc")) {
                                            rem_dup_folder[i] = thumbImage.get(i).get("Personal3");
                                        }
                                    }
                                }
                                moveFolder2 = foldername();

                               /* JSONArray array = new JSONArray();

                                for (int i = 0; i < thumbnailsselection.length; i++) {
                                    JSONObject imageobject = new JSONObject();
                                    if (thumbnailsselection[i]) {

                                        try {
                                            //imageId.add(patientId+"/FileVault/"+thumbImage.get(i).get("folder_name"));

                                            if (!thumbImage.get(i).get("FileVault2").contains(".PNG") && !thumbImage.get(i).get("FileVault2").contains(".png") &&
                                                    !thumbImage.get(i).get("FileVault2").contains(".jpg") && !thumbImage.get(i).get("FileVault2").contains(".pdf")
                                                    && !thumbImage.get(i).get("FileVault2").contains(".xls") && !thumbImage.get(i).get("FileVault2").contains(".doc")) {
                                                imageobject.put("Key", patientId + "/FileVault/"  + thumbImage.get(i).get("FileVault2"));
                                                imageobject.put("Type", "1");
                                                imageobject.put("ThumbFile", "");
                                                imageobject.put("Status", "");
                                            } else {
                                                if (thumbImage.get(i).get("FileVault2").contains(".png")) {
                                                    String thumbimg = thumbImage.get(i).get("FileVault2").replaceAll("\\.png", "_thumb.png");
                                                    imageobject.put("Key", patientId + "/FileVault/" + thumbImage.get(i).get("FileVault2"));
                                                    imageobject.put("Type", "0");
                                                    imageobject.put("ThumbFile", thumbimg);
                                                    imageobject.put("Status", "");
                                                } else if (thumbImage.get(i).get("FileVault2").contains(".PNG")) {
                                                    String thumbimg = thumbImage.get(i).get("FileVault2").replaceAll("\\.PNG", "_thumb.png");
                                                    imageobject.put("Key", patientId + "/FileVault/" + thumbImage.get(i).get("FileVault2"));
                                                    imageobject.put("Type", "0");
                                                    imageobject.put("ThumbFile", thumbimg);
                                                    imageobject.put("Status", "");
                                                } else if (thumbImage.get(i).get("FileVault2").contains(".jpg")) {
                                                    String thumbimg = thumbImage.get(i).get("FileVault2").replaceAll("\\.jpg", "_thumb.png");
                                                    imageobject.put("Key", patientId + "/FileVault/" + thumbImage.get(i).get("FileVault2"));
                                                    imageobject.put("Type", "0");
                                                    imageobject.put("ThumbFile", thumbimg);
                                                    imageobject.put("Status", "");
                                                } else if (thumbImage.get(i).get("FileVault2").contains(".JPG")) {
                                                    String thumbimg = thumbImage.get(i).get("FileVault2").replaceAll("\\.JPG", "_thumb.png");
                                                    imageobject.put("Key", patientId + "/FileVault/" + thumbImage.get(i).get("FileVault2"));
                                                    imageobject.put("Type", "0");
                                                    imageobject.put("ThumbFile", thumbimg);
                                                    imageobject.put("Status", "");
                                                } else {
                                                    imageobject.put("Key", patientId + "/FileVault/" + thumbImage.get(i).get("FileVault2"));
                                                    imageobject.put("Type", "0");
                                                    imageobject.put("ThumbFile", "");
                                                    imageobject.put("Status", "");
                                                }
                                            }

                                         *//*   imageIdsToBeSent = imageIdsToBeSent + subArrayImage.getJSONObject(i).getString("ImageId")
                                                    + ",";
                                            System.out.println(i);*//*
                                        } catch (Exception e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                        array.put(imageobject);
                                    }

                                }

                                System.out.println(array);

                                queue2 = Volley.newRequestQueue(Filevault.this);

                                sendData = new JSONObject();
                                try {
                                    sendData.put("ObjectList", array);
                                    sendData.put("UserId", patientId);
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

				*//*		String url = Services.init + "PatientModule/PatientService.asmx/DeletePatientFiles";*//*
                                StaticHolder sttc_holdr = new StaticHolder(Filevault.this, StaticHolder.Services_static.DeleteObject);
                                String url = sttc_holdr.request_Url();
                                jr2 = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        System.out.println(response);

                                        try {
                                            Toast.makeText(Filevault.this, " Items Deleted", Toast.LENGTH_SHORT)
                                                    .show();
                                            // S3Objects.clear();
                                            finish();
                                            startActivity(getIntent());

                                        } catch (Exception e) {
                                            // TODO Auto-generated catch
                                            // block
                                            e.printStackTrace();
                                        }

                                        // queue.add(jr);

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(Filevault.this, error.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                queue2.add(jr2);
*/
                                if (rem_dup_folder != null /*&& !rem_dup_folder.equalsIgnoreCase("")*/) {
                                    for (int r = 0; r < moveFolder2.size(); r++) {
                                        for (int l = 0; l < rem_dup_folder.length; l++) {
                                            if (moveFolder2.get(r).get("folder_name").equalsIgnoreCase(rem_dup_folder[l])) {
                                                moveFolder2.remove(r);
                                                break;
                                            }
                                        }
                                    }
                                }
                                alias_thumbImage_folder.addAll(moveFolder2);
                               /* String[] stockArr = new String[moveFolder2.size()];
                                stockArr = moveFolder2.toArray(stockArr);
                                AlertDialog.Builder builder = new AlertDialog.Builder(Filevault.this);
                                builder.setTitle("Make your selection");
                                builder.setItems(stockArr, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int item) {
                                        move_to_folder(moveFolder2.get(item).toString());
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();*/
                                final Dialog move_dialog = new Dialog(Filevault.this);
                                move_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                //setting custom layout to dialog
                                move_dialog.setContentView(R.layout.move_folderlist);
                                Button move_btn = (Button) move_dialog.findViewById(R.id.move_btn);
                                ListView folder_list = (ListView) move_dialog.findViewById(R.id.folder_list);
                                final TextView folder_root = (TextView) move_dialog.findViewById(R.id.folder_root);
                                folder_root.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                folder_root.setEnabled(false);
                                folder_adapter = new Folder_adapter(Filevault.this, moveFolder2, patientId, "");
                                folder_list.setAdapter(folder_adapter);
                                folder_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        if (!thumbImage.get(position).get("Personal3").contains(".PNG") && !thumbImage.get(position).get("Personal3").contains(".png") &&
                                                !thumbImage.get(position).get("Personal3").contains(".jpg") && !thumbImage.get(position).get("Personal3").contains(".JPG")
                                                && !thumbImage.get(position).get("Personal3").contains(".pdf")
                                                && !thumbImage.get(position).get("Personal3").contains(".xls") && !thumbImage.get(position).get("Personal3").contains(".doc")) {
                                            Folder_Clicked = moveFolder2.get(position).get("folder_name");
                                            HashKey = moveFolder2.get(position).get("hash_keyvalue");
                                            nextdialog();
                                            move_dialog.dismiss();
                                        }
                                    }
                                });
                                move_btn.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (folder_root.getText().toString().trim().equalsIgnoreCase("Root")) {
                                            Toast.makeText(Filevault.this, "Please select a destination folder different from the current one.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                move_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(final DialogInterface arg0) {
                                        // folder_path.clear();
                                    }
                                });
                                move_dialog.show();
                            }
                        });
                        dialog.show();
                    } else {
                        Toast.makeText(Filevault.this, "Please select file(s).", Toast.LENGTH_SHORT).show();
                    }
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void chooseimage() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Filevault.this);
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Pick from Gallery", "Take from Camera"},
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:

                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FROM_GALLERY);

                                break;

                            case 1:

                                // Intent takePictureIntent = new
                                // Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                // if
                                // (takePictureIntent.resolveActivity(getPackageManager())
                                // != null)
                                // {
                                //
                                // startActivityForResult(takePictureIntent,PICK_FROM_CAMERA);
                                //
                                // }


                                File photo = null;
                                Intent intent1 = new Intent("android.media.action.IMAGE_CAPTURE");
                                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                    photo =  new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "test1.jpg");

                                      boolean b=photo.delete();
                                     String df="";
                                    photo =  new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "test1.jpg");
                                } else {
                                    photo = new File(getCacheDir(), "test1.jpg");
                                }
                                if (photo != null) {
                                    intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                                    Imguri = Uri.fromFile(photo);
                                    startActivityForResult(intent1, PICK_FROM_CAMERA);
                                }

                                break;

                            default:
                                break;
                        }
                    }
                });
        builder.show();

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // thumbImage.clear();
        // imageName.clear();
        // queue.add(jr);
        Helper help = new Helper();
        help.folder_path.clear();
        if (Helper.authentication_flag == true) {
            finish();
        }

    }

    public void onBackPressed() {
        if (!toggle_move) {
          /*  super.onBackPressed();*/
            Intent intent = new Intent(getApplicationContext(), logout.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            thumbImage.clear();
            originalVaultlist.clear();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        } else if (!view_list) {
            list_header.setVisibility(View.VISIBLE);
            list_header2.setVisibility(View.GONE);
            vault_adapter = new Vault_adapter(Filevault.this, thumbImage, false, patientId, "");
            vault_list.setAdapter(vault_adapter);
            toggle_move = false;
            check_para = 0;
            select_times = 0;
            menu_toggle.findItem(R.id.action_delete).setVisible(false);
            menu_toggle.findItem(R.id.save).setVisible(false);
            menu_toggle.findItem(R.id.action_move).setVisible(false);
            menu_toggle.findItem(R.id.action_home).setVisible(true);
            thumbnailsselection = new boolean[thumbImage.size()];
        } else {
           /* super.onBackPressed();*/
            Intent intent = new Intent(getApplicationContext(), logout.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            thumbImage.clear();
            originalVaultlist.clear();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        }
    }

   /* private  void s3objects() {
        String url = "http://192.168.1.202:86/WebServices/LabService.asmx/GetAllObjectFromS3";
        JSONObject s3data = new JSONObject();
        try {
            s3data.put("Key", "");
            s3data.put("patientId", patientId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        s3jr = new JsonObjectRequest(Method.POST, url, s3data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String data = response.getString("d");
                    JSONObject j = new JSONObject(data);
                    S3Objects_arr = j.getJSONArray("S3Objects");
                    S3Objects_details = new ArrayList<HashMap<String, String>>();
                    HashMap<String, String> hmap;
                    HashMap<String, String> hmap_details;
                    S3Objects = new ArrayList<HashMap<String, String>>();
                    for (int i = 0; i < S3Objects_arr.length(); i++) {
                        JSONObject json_obj = S3Objects_arr.getJSONObject(i);
                        hmap_details = new HashMap<String, String>();
                        hmap_details.put("LastModified", json_obj.getString("LastModified"));
                        hmap_details.put("Size", json_obj.getString("Size"));
                        String Folder = json_obj.getString("Key");
                        hmap_details.put("Key", json_obj.getString("Key"));
                        S3Objects_details.add(hmap_details);

                        String[] key_split = Folder.split("/");
                        hmap = new HashMap<String, String>();

                        for (int k = 1; k < key_split.length; k++) {

                            if (!key_split[k].contains(".PNG") && !key_split[k].contains(".jpg") && !key_split[k].contains(".xls")
                                    && !key_split[k].contains(".doc")) {
                                hmap.put(key_split[1] + k, key_split[k]);//it is folder


                            } else {
                                hmap.put(key_split[1] + k, Folder);//it is thumb image
                            }

                        }
                        hmap.put("LastModified", json_obj.getString("LastModified"));
                        hmap.put("Size", json_obj.getString("Size"));
                        S3Objects.add(hmap);
                    }

                    originalVaultlist.addAll(S3Objects);
                    firsttime_fileShow(originalVaultlist);
                    HashMap<String, String> hmap1;
                    for (int i = 0; i < S3Objects.size(); i++) {
                        if (!(S3Objects.get(i).get("folder_name").contains("_thumb.png") ||
                                S3Objects.get(i).get("folder_name").contains("_thumb.jpg") ||
                                S3Objects.get(i).get("folder_name").contains("_thumb.PNG") ||
                                S3Objects.get(i).get("folder_name").contains("_thumb.JPG"))) {
                            hmap1 = new HashMap<String, String>();
                            hmap1.put("FileVault2", S3Objects.get(i).get("folder_name"));
                            hmap1.put("LastModified", S3Objects.get(i).get("LastModified"));
                            hmap1.put("Size", S3Objects.get(i).get("Size"));
                            thumbImage.add(hmap1);
                        }

                    }
                    thumbnailsselection = new boolean[thumbImage.size()];
                    imageAdapter = new ImageAdapter();
                    gridView.setAdapter(imageAdapter);
                    gridView.setVisibility(View.GONE);
                    vault_adapter = new Vault_adapter(Filevault.this, thumbImage, false, patientId);
                    vault_list.setAdapter(vault_adapter);
                    list_header.setVisibility(View.VISIBLE);
                    list_header2.setVisibility(View.GONE);
                    vault_list.setVisibility(View.VISIBLE);
                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        req.add(s3jr);
    }*/

    public void createLockFolder() {
        req = Volley.newRequestQueue(this);
        StaticHolder sttc_holdr = new StaticHolder(Filevault.this, StaticHolder.Services_static.CreateLockFolder);
        String url = sttc_holdr.request_Url();
        JSONObject data = new JSONObject();
        JSONArray array_folders = new JSONArray();
        array_folders.put("Prescription");
        array_folders.put("Insurance");
        array_folders.put("Bills");
        array_folders.put("Reports");
        try {
            data.put("list", array_folders);
            data.put("patientId", patientId);
        } catch (JSONException je) {
            je.printStackTrace();
        }
        lock_folder = new JsonObjectRequest(Method.POST, url, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                startBackgroundprocess();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        req.add(lock_folder);
    }

    public void startBackgroundprocess() {
        try {
            sendData.put("PatientId", id);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        nHandler = NotificationHandler.getInstance(this);
        bar = (ProgressBar) findViewById(R.id.pg);
        queue = Volley.newRequestQueue(this);
        queue3 = Volley.newRequestQueue(this);
        req = Volley.newRequestQueue(this);
        mImageLoader = MyVolleySingleton.getInstance(Filevault.this).getImageLoader();
        StaticHolder sttc_holdr = new StaticHolder(Filevault.this, StaticHolder.Services_static.GetAllObjectFromS3);
        String url = sttc_holdr.request_Url();
        JSONObject s3data = new JSONObject();
       pd = new ProgressDialog(Filevault.this);
        pd.setMessage("Loading Vault .....");
        pd.setCanceledOnTouchOutside(false);

                pd.show();


        try {
            s3data.put("Key", "");
            s3data.put("patientId", patientId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        s3jr = new JsonObjectRequest(Method.POST, url, s3data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String data = response.getString("d");
                    JSONObject j = new JSONObject(data);
                    S3Objects_arr = j.getJSONArray("S3Objects");
                    thumbImage.clear();
                    S3Objects_details = new ArrayList<HashMap<String, String>>();
                    HashMap<String, String> hmap;
                    HashMap<String, String> hmap_details;
                    S3Objects = new ArrayList<HashMap<String, String>>();
                    for (int i = 0; i < S3Objects_arr.length(); i++) {
                        JSONObject json_obj = S3Objects_arr.getJSONObject(i);
                        hmap_details = new HashMap<String, String>();
                        hmap_details.put("LastModified", json_obj.getString("LastModified"));
                        hmap_details.put("Size", json_obj.getString("Size"));
                        String Folder = json_obj.getString("Key");
                        hmap_details.put("Key", json_obj.getString("Key"));
                        S3Objects_details.add(hmap_details);

                        String[] key_split = Folder.split("/");
                        hmap = new HashMap<String, String>();

                        for (int k = 2; k < key_split.length; k++) {

                            if (!key_split[k].contains(".PNG") && !key_split[k].contains(".jpg") && !key_split[k].contains(".xls")
                                    && !key_split[k].contains(".doc") && !key_split[k].contains(".png")) {
                                hmap.put(key_split[2] + k, key_split[k]);//it is folder
                            } else {
                                hmap.put(key_split[2] + k, Folder);//it is thumb image
                            }
                        }
                        hmap.put("LastModified", json_obj.getString("LastModified"));
                        hmap.put("Size", json_obj.getString("Size"));
                        S3Objects.add(hmap);
                    }
                 /* Collections.sort(S3Objects, new Comparator<HashMap<String, String>>() {

                      @Override
                      public int compare(HashMap<String, String> lhs,
                                         HashMap<String, String> rhs) {
                          // Do your comparison logic here and retrn accordingly.
                          return lhs.get("Personal3").compareTo(rhs.get("Personal3"));
                      }
                  });*/

                    for (int list = 0; list < S3Objects.size(); list++) {
                        if (S3Objects.get(list).get("Personal3").equalsIgnoreCase("ZurekaTempPatientConfig")) {
                            S3Objects.remove(list);
                        }

                    }
                    originalVaultlist.addAll(S3Objects);

                    if ((pd != null) && pd.isShowing()) {
                        pd.dismiss();
                    }

                    firsttime_fileShow(originalVaultlist);

                    HashMap<String, String> hmap1;
                    for (int i = 0; i < S3Objects.size(); i++) {
                        String check = S3Objects.get(i).get("folder_name");
                        if (check != null) {
                            if (!(S3Objects.get(i).get("folder_name").contains("_thumb.png") ||
                                    S3Objects.get(i).get("folder_name").contains("_thumb.jpg") ||
                                    S3Objects.get(i).get("folder_name").contains("_thumb.PNG") ||
                                    S3Objects.get(i).get("folder_name").contains("_thumb.JPG"))) {
                                hmap1 = new HashMap<String, String>();
                                hmap1.put("Personal3", S3Objects.get(i).get("folder_name"));
                                hmap1.put("LastModified", S3Objects.get(i).get("LastModified"));
                                hmap1.put("Size", S3Objects.get(i).get("Size"));
                                thumbImage.add(hmap1);
                            }
                        }
                    }
                    new Helper().sortHashList(thumbImage, "Personal3");
                    thumbnailsselection = new boolean[thumbImage.size()];
                    imageAdapter = new ImageAdapter();
                    gridView.setAdapter(imageAdapter);
                    vault_adapter = new Vault_adapter(Filevault.this, thumbImage, false, patientId, "");
                    vault_list.setAdapter(vault_adapter);
                    alias_foldername();
                    if (check_view.equalsIgnoreCase("")) {
                        gridView.setVisibility(View.GONE);
                        list_header.setVisibility(View.VISIBLE);
                        list_header2.setVisibility(View.GONE);
                        vault_list.setVisibility(View.VISIBLE);
                    } else if (check_view.equalsIgnoreCase("List")) {
                        gridView.setVisibility(View.GONE);
                        warning_msg.setVisibility(View.GONE);
                        list_header.setVisibility(View.VISIBLE);
                        list_header2.setVisibility(View.GONE);
                        vault_list.setVisibility(View.VISIBLE);
                    } else if (check_view.equalsIgnoreCase("Grid")) {
                        gridView.setVisibility(View.VISIBLE);
                        list_header.setVisibility(View.GONE);
                        warning_msg.setVisibility(View.GONE);
                        list_header2.setVisibility(View.GONE);
                        vault_list.setVisibility(View.GONE);
                    } else if (thumbImage.size() == 0) {
                        vault_list.setVisibility(View.GONE);
                        gridView.setVisibility(View.GONE);
                        warning_msg.setVisibility(View.VISIBLE);
                    }
                    if (S3Objects.size() == 0) {
                        vault_list.setVisibility(View.GONE);
                        gridView.setVisibility(View.GONE);
                        warning_msg.setVisibility(View.VISIBLE);
                        menu_toggle.findItem(R.id.action_listview).setVisible(false);
                        menu_toggle.findItem(R.id.select_all).setVisible(false);
                        menu_toggle.findItem(R.id.action_move).setVisible(false);
                        menu_toggle.findItem(R.id.action_delete).setVisible(false);
                        menu_toggle.findItem(R.id.save).setVisible(false);
                        menu_toggle.findItem(R.id.action_home).setVisible(true);
                    }
                    if (S3Objects.size() != 0) {
                        menu_toggle.findItem(R.id.action_listview).setVisible(true);
                        menu_toggle.findItem(R.id.select_all).setVisible(true);
                        menu_toggle.findItem(R.id.action_move).setVisible(false);
                        menu_toggle.findItem(R.id.action_delete).setVisible(false);
                        menu_toggle.findItem(R.id.save).setVisible(false);
                        menu_toggle.findItem(R.id.action_move).setVisible(false);
                        menu_toggle.findItem(R.id.action_home).setVisible(true);
                        warning_msg.setVisibility(View.GONE);
                    }
                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
            }
        });
        int socketTimeout1 = 50000;
        RetryPolicy policy1 = new DefaultRetryPolicy(socketTimeout1, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        s3jr.setRetryPolicy(policy1);
        req.add(s3jr);

    }

    public void firsttime_fileShow(ArrayList<HashMap<String, String>> origin_list) {
        S3Objects.clear();
        HashMap<String, String> hmap;
        try {
            for (int i = 0; i < origin_list.size(); i++) {
                String foldername = origin_list.get(i).get("Personal3");
                int s3length = S3Objects.size();
                boolean check = false;
                if (i == 0) {
                    hmap = new HashMap<String, String>();
                    hmap.put("folder_name", foldername);
                    hmap.put("hash_keyvalue", "Personal3");
                    hmap.put("LastModified", originalVaultlist.get(i).get("LastModified"));
                    hmap.put("Size", originalVaultlist.get(i).get("Size"));
                    S3Objects.add(hmap);
                } else {
                    for (int j = 0; j < s3length; j++) {
                        if (S3Objects.get(j).get("folder_name").equalsIgnoreCase(foldername)) {
                            //origin_list.remove(i);
                            check = true;
                        }
                    }
                    if (check == false) {

                        hmap = new HashMap<String, String>();
                        hmap.put("folder_name", foldername);
                        hmap.put("hash_keyvalue", "Personal3");
                        hmap.put("LastModified", originalVaultlist.get(i).get("LastModified"));
                        hmap.put("Size", originalVaultlist.get(i).get("Size"));
                        S3Objects.add(hmap);
                    }
                }
            }
            for (int i = 0; i < S3Objects.size(); i++) {
                if (S3Objects.get(i).get("folder_name").equalsIgnoreCase("ZurekaTempPatientConfig")) {
                    S3Objects.remove(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
      /*  Collections.sort(S3Objects, new Comparator<HashMap<String, String>>() {

            @Override
            public int compare(HashMap<String, String> lhs,
                               HashMap<String, String> rhs) {
                // Do your comparison logic here and retrn accordingly.
                return lhs.get("folder_name").compareTo(rhs.get("folder_name"));
            }
        });*/
      /*  ArrayList<HashMap<String, String>> arr1 = S3Objects;*/
    }

  /*  @Override
    protected void onDestroy() {
        super.onDestroy();
        thumbImage.clear();
        originalVaultlist.clear();
        folder_path.clear();
        Imguri = null;
        new Helper().main_S3Objects.clear();
        finish();
    }*/

    public void show_dialog() {
        // final Dialog overlay_dialog = new Dialog(Pkg_TabActivity.this, R.style.DialogSlideAnim);
        final Dialog overlay_dialog = new Dialog(Filevault.this);
        overlay_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);//SOFT_INPUT_STATE_ALWAYS_HIDDEN
        overlay_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // overlay_dialog.setCancelable(false);
        overlay_dialog.setCanceledOnTouchOutside(true);
        overlay_dialog.setContentView(R.layout.create_folderdialog);
        Button btn_continue = (Button) overlay_dialog.findViewById(R.id.create_btn);
        TextView path = (TextView) overlay_dialog.findViewById(R.id.path);
        path.setVisibility(View.GONE);
        final EditText folder_name = (EditText) overlay_dialog.findViewById(R.id.folder_name);
        //opening keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(folder_name.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        //
        folder_name.requestFocus();
        Button canceltxt = (Button) overlay_dialog.findViewById(R.id.cancel);
        canceltxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                overlay_dialog.dismiss();
            }
        });
        btn_continue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progress = new ProgressDialog(Filevault.this);

                progress.setCancelable(false);
                //progress.setTitle("Logging in...");
                progress.setMessage("Please wait...");
                progress.setIndeterminate(true);

                String folder = folder_name.getText().toString();
                if (folder_name_exists(folder.trim())) {
                    folder_name.setError("A folder already exists with this name.");
                } else if (folder != "" && (!folder.equals(""))) {
                    overlay_dialog.dismiss();
                    progress.show();
                    JSONObject sendData = new JSONObject();
                    try {
                        sendData.put("FolderName", folder);
                        sendData.put("Path", "");
                        sendData.put("patientId", patientId);
                    } catch (JSONException EX) {
                        EX.printStackTrace();
                    }
                    StaticHolder sttc_holdr = new StaticHolder(Filevault.this, StaticHolder.Services_static.CreateFolder);
                    String url = sttc_holdr.request_Url();
                    JsonObjectRequest jr = new JsonObjectRequest(Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            // System.out.println(response);

                            try {
                                String packagedata = response.getString("d");
                                if (packagedata.equalsIgnoreCase("Error")) {
                                    progress.dismiss();
                                    Toast.makeText(getApplicationContext(), "An error occurred while creating folder.", Toast.LENGTH_SHORT).show();
                                } else if (packagedata.equalsIgnoreCase("Folder exist")) {
                                    progress.dismiss();
                                    Toast.makeText(getApplicationContext(), "A folder already exists with this name.", Toast.LENGTH_SHORT).show();
                                } else if (packagedata.equalsIgnoreCase("Added")) {
                                    progress.dismiss();
                                    Toast.makeText(getApplicationContext(), "Folder created successfully.", Toast.LENGTH_LONG).show();
                                    refresh();
                                }


                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progress.dismiss();
                            overlay_dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Server Connectivity Error, Try Later.", Toast.LENGTH_SHORT).show();

                        }
                    }) {
                    };
                    queue.add(jr);
                } else {
                    folder_name.setError("Enter correct folder name");
                    // Toast.makeText(getApplicationContext(), "Enter valid Mobile number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        overlay_dialog.show();
    }

    public void move_to_folder(final String newpath) {

        pd = new ProgressDialog(Filevault.this);
        pd.setMessage("Moving .....");
        pd.show();
        toggle_move = false;
        JSONArray array = new JSONArray();

        for (int i = 0; i < thumbnailsselection.length; i++) {
            JSONObject imageobject = new JSONObject();
            if (thumbnailsselection[i]) {
                try {
                    //imageId.add(patientId+"/FileVault/"+thumbImage.get(i).get("folder_name"));
                    if (!thumbImage.get(i).get("Personal3").contains(".PNG") && !thumbImage.get(i).get("Personal3").contains(".png") &&
                            !thumbImage.get(i).get("Personal3").contains(".jpg") && !thumbImage.get(i).get("Personal3").contains(".pdf")
                            && !thumbImage.get(i).get("Personal3").contains(".xls") && !thumbImage.get(i).get("Personal3").contains(".doc")) {
                        // rem_dup_folder = thumbImage.get(i).get("Personal3");
                        if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/")) {
                            imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                        } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                            imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                        } else {
                            imageobject.put("Key", patientId + "/FileVault/Personal/" + thumbImage.get(i).get("Personal3"));
                        }
                        imageobject.put("Type", "1");
                        imageobject.put("ThumbFile", "");
                        imageobject.put("Status", "");
                    } else {
                        if (thumbImage.get(i).get("Personal3").contains(".png")) {
                            String thumbimg = thumbImage.get(i).get("Personal3").replaceAll("\\.png", "_thumb.png");
                            if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/")) {
                                imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                            } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                            } else {
                                imageobject.put("Key", patientId + "/FileVault/Personal/" + thumbImage.get(i).get("Personal3"));
                            }
                            imageobject.put("Type", "0");
                            if (thumbimg.startsWith(patientId + "/FileVault/Personal/")) {
                                imageobject.put("ThumbFile", thumbimg);
                            } else if (thumbimg.contains(patientId + "/FileVault/Personal/")) {
                                imageobject.put("ThumbFile", thumbimg);
                            } else {
                                imageobject.put("ThumbFile", patientId + "/FileVault/Personal/" + thumbimg);
                            }
                            imageobject.put("Status", "");
                        } else if (thumbImage.get(i).get("Personal3").contains(".PNG")) {
                            String thumbimg = thumbImage.get(i).get("Personal3").replaceAll("\\.PNG", "_thumb.PNG");
                            if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/")) {
                                imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                            } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                            } else {
                                imageobject.put("Key", patientId + "/FileVault/Personal/" + "/" + thumbImage.get(i).get("Personal3"));
                            }
                            imageobject.put("Type", "0");
                            if (thumbimg.startsWith(patientId + "/FileVault/Personal/")) {
                                imageobject.put("ThumbFile", thumbimg);
                            } else if (thumbimg.contains(patientId + "/FileVault/Personal/")) {
                                imageobject.put("ThumbFile", thumbimg);
                            } else {
                                imageobject.put("ThumbFile", patientId + "/FileVault/Personal/" + thumbimg);
                            }
                            imageobject.put("Status", "");
                        } else if (thumbImage.get(i).get("Personal3").contains(".jpg")) {
                            String thumbimg = thumbImage.get(i).get("Personal3").replaceAll("\\.jpg", "_thumb.jpg");
                            if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/")) {
                                imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                            } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                            } else {
                                imageobject.put("Key", patientId + "/FileVault/Personal/" + thumbImage.get(i).get("Personal3"));
                            }
                            imageobject.put("Type", "0");
                            if (thumbimg.startsWith(patientId + "/FileVault/Personal/")) {
                                imageobject.put("ThumbFile", thumbimg);
                            } else if (thumbimg.contains(patientId + "/FileVault/Personal/")) {
                                imageobject.put("ThumbFile", thumbimg);
                            } else {
                                imageobject.put("ThumbFile", patientId + "/FileVault/Personal/" + thumbimg);
                            }
                            imageobject.put("Status", "");
                        } else if (thumbImage.get(i).get("Personal3").contains(".JPG")) {
                            String thumbimg = thumbImage.get(i).get("Personal3").replaceAll("\\.JPG", "_thumb.JPG");
                            if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/")) {
                                imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                            } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                            } else {
                                imageobject.put("Key", patientId + "/FileVault/Personal/" + thumbImage.get(i).get("Personal3"));
                            }
                            imageobject.put("Type", "0");
                            if (thumbimg.startsWith(patientId + "/FileVault/Personal/")) {
                                imageobject.put("ThumbFile", thumbimg);
                            } else if (thumbimg.contains(patientId + "/FileVault/Personal/")) {
                                imageobject.put("ThumbFile", thumbimg);
                            } else {
                                imageobject.put("ThumbFile", patientId + "/FileVault/Personal/" + thumbimg);
                            }
                            imageobject.put("Status", "");
                        } else {
                            if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/")) {
                                imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                            } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                            } else {
                                imageobject.put("Key", patientId + "/FileVault/Personal/" + thumbImage.get(i).get("Personal3"));
                            }
                            imageobject.put("Type", "0");
                            imageobject.put("ThumbFile", "");
                            imageobject.put("Status", "");
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                array.put(imageobject);
            }


        }


        System.out.println(array);

        queue2 = Volley.newRequestQueue(Filevault.this);

        sendData = new JSONObject();
        try {
            sendData.put("ObjectList", array);
            sendData.put("UserId", patientId);
            sendData.put("NewPath", patientId + "/FileVault/Personal/" + newpath);
            sendData.put("AbsolutePath", patientId + "/FileVault/Personal/");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //String url = Services.init + "PatientModule/PatientService.asmx/DeletePatientFiles";
        StaticHolder sttc_holdr = new StaticHolder(Filevault.this, StaticHolder.Services_static.MoveObject);
        String url = sttc_holdr.request_Url();
        jr2 = new JsonObjectRequest(Method.POST, url, sendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);

                try {
                    if (response.getString("d").toString().equalsIgnoreCase("success")) {
                        Toast.makeText(Filevault.this, "File(s) successfully moved to: " + newpath, Toast.LENGTH_LONG)
                                .show();
                    } else {
                        Toast.makeText(Filevault.this, response.getString("d").toString(), Toast.LENGTH_LONG)
                                .show();
                    }
                    pd.dismiss();
                    //  S3Objects.clear();
                    refresh();

                } catch (Exception e) {
                    // TODO Auto-generated catch
                    // block
                    e.printStackTrace();
                }

                // queue.add(jr);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(Filevault.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue2.add(jr2);
    }

    public ArrayList<HashMap<String, String>> foldername() {
        ArrayList<HashMap<String, String>> folder_move = new ArrayList<HashMap<String, String>>();
        alias_thumbImage_folder.clear();
        HashMap<String, String> hmap;
        for (int s = 0; s < thumbImage.size(); s++) {
            hmap = new HashMap<String, String>();
            /*if (!thumbImage.get(s).get("Personal3").contains(".PNG") && !thumbImage.get(s).get("Personal3").contains(".png") &&
                    !thumbImage.get(s).get("Personal3").contains(".jpg") && !thumbImage.get(s).get("Personal3").contains(".JPG")
                    && !thumbImage.get(s).get("Personal3").contains(".pdf")
                    && !thumbImage.get(s).get("Personal3").contains(".xls") && !thumbImage.get(s).get("Personal3").contains(".doc"))*/
            {
                hmap.put("folder_name", thumbImage.get(s).get("Personal3"));
                hmap.put("hash_keyvalue", S3Objects.get(s).get("hash_keyvalue").trim());
                folder_move.add(hmap);
            }
        }
        Helper helper = new Helper();
        if (helper.main_S3Objects.size() == 0) {
            helper.main_S3Objects.addAll(folder_move);
        }
        return folder_move;
    }

    public void alias_foldername() {
        ArrayList<HashMap<String, String>> folder_move = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> hmap;
        for (int s = 0; s < thumbImage.size(); s++) {
            hmap = new HashMap<String, String>();
         /*   if (!thumbImage.get(s).get("Personal3").contains(".PNG") && !thumbImage.get(s).get("Personal3").contains(".png") &&
                    !thumbImage.get(s).get("Personal3").contains(".jpg")
                    && !thumbImage.get(s).get("Personal3").contains(".JPG") && !thumbImage.get(s).get("Personal3").contains(".pdf")
                    && !thumbImage.get(s).get("Personal3").contains(".xls") && !thumbImage.get(s).get("Personal3").contains(".doc"))*/
            {
                hmap.put("folder_name", thumbImage.get(s).get("Personal3"));
                hmap.put("hash_keyvalue", S3Objects.get(s).get("hash_keyvalue").trim());
                folder_move.add(hmap);
            }
        }
        Helper helper = new Helper();
        if (helper.main_S3Objects.size() == 0) {
            helper.main_S3Objects.addAll(folder_move);
        }
    }

    private ArrayList<HashMap<String, String>> folder1() {
        ArrayList<HashMap<String, String>> folder_move = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> hmap;
        for (int s = 0; s < thumbImage_folder.size(); s++) {
            hmap = new HashMap<String, String>();
           /* if (!thumbImage_folder.get(s).get("Personal3").contains(".PNG") && !thumbImage_folder.get(s).get("Personal3").contains(".png") &&
                    !thumbImage_folder.get(s).get("Personal3").contains(".jpg")
                    && !thumbImage_folder.get(s).get("Personal3").contains(".JPG") && !thumbImage_folder.get(s).get("Personal3").contains(".pdf")
                    && !thumbImage_folder.get(s).get("Personal3").contains(".xls") && !thumbImage_folder.get(s).get("Personal3").contains(".doc"))*/
            {
                hmap.put("folder_name", thumbImage_folder.get(s).get("Personal3"));
                hmap.put("hash_keyvalue", S3Objects.get(s).get("hash_keyvalue").trim());
                folder_move.add(hmap);
            }
        }
        return folder_move;
    }

    private ArrayList<HashMap<String, String>> navigate_folder() {
        ArrayList<HashMap<String, String>> folder_move = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> hmap;
        for (int s = 0; s < thumbImage_folder.size(); s++) {
            hmap = new HashMap<String, String>();
           /* if (!thumbImage_folder.get(s).get("Personal3").contains(".PNG") && !thumbImage_folder.get(s).get("Personal3").contains(".png") &&
                    !thumbImage_folder.get(s).get("Personal3").contains(".jpg")
                    && !thumbImage_folder.get(s).get("Personal3").contains(".JPG") && !thumbImage_folder.get(s).get("Personal3").contains(".pdf")
                    && !thumbImage_folder.get(s).get("Personal3").contains(".xls") && !thumbImage_folder.get(s).get("Personal3").contains(".doc"))*/
            {
                hmap.put("folder_name", thumbImage_folder.get(s).get("Personal3"));
                hmap.put("hash_keyvalue", S3Objects_folder.get(s).get("hash_keyvalue").trim());
                folder_move.add(hmap);
            }
        }
        return folder_move;
    }

    public void nextdialog() {
        final Dialog move_dialog = new Dialog(Filevault.this);
        move_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setting custom layout to dialog
        move_dialog.setContentView(R.layout.move_folderlist);
        final ListView folder_list = (ListView) move_dialog.findViewById(R.id.folder_list);
        Button move_btn = (Button) move_dialog.findViewById(R.id.move_btn);
        move_btn.setClickable(true);
        final TextView folder_root = (TextView) move_dialog.findViewById(R.id.folder_root);
        folder_root.setClickable(true);
        folder_root.setCompoundDrawablesWithIntrinsicBounds(R.drawable.fitr_arrow, 0, 0, 0);
        final TextView empty_text = (TextView) move_dialog.findViewById(R.id.empty_text);
       /* folder_adapter = new Folder_adapter(Filevault.this,moveFolder1);
        folder_list.setAdapter(folder_adapter);*/
        move_dialog.show();
        S3Objects_folder = new ArrayList<HashMap<String, String>>();
        S3Objects_folder = new NavFolder(Folder_Clicked, HashKey).onFolderClickListener();
        folder_root.setEnabled(true);
        folder_path.clear();
        folder_path.add(Folder_Clicked);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (S3Objects.size() == 0) {
                    Toast.makeText(Filevault.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    thumbImage_folder.clear();
                    Show_Data(S3Objects_folder);
                    dialog_folder = folder1();
                    if (dialog_folder.size() == 0) {
                        folder_list.setVisibility(View.GONE);
                        empty_text.setVisibility(View.VISIBLE);
                    } else {
                        StringBuffer buffer = new StringBuffer();
                        for (int k = 0; k < folder_path.size(); k++) {
                            buffer.append(folder_path.get(k) + "/");
                        }
                        Log.v("buffer", buffer.toString());
                        //String path_finder = first_timefolderclicked + "/" + buffer;
                        String[] make_path = buffer.toString().split("/");
                        StringBuffer path_buffer = new StringBuffer();
                        for (int i = 0; i < make_path.length; i++) {
                            if (i == make_path.length - 1) {
                                path_buffer.append(make_path[i]);
                            } else {
                                path_buffer.append(make_path[i] + "/");
                            }
                        }
                        Log.v("buffer_upload", path_buffer.toString());
                        folder_list.setVisibility(View.VISIBLE);
                        empty_text.setVisibility(View.GONE);
                        folder_adapter = new Folder_adapter(Filevault.this, dialog_folder, patientId, path_buffer.toString());
                        folder_list.setAdapter(folder_adapter);
                    }

                }
            }
        }, 100);
        folder_root.setText(Folder_Clicked);

        folder_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (S3Objects.size() == 0) {
                            Toast.makeText(Filevault.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                            move_dialog.dismiss();
                        } else {
                            //dialog_folder = folder1();
                            HashMap<String, Object> obj = (HashMap<String, Object>) folder_adapter.getItem(position);
                            String check_list = (String) obj.get("folder_name");
                            if (!check_list.contains(".PNG") && !check_list.contains(".png") &&
                                    !check_list.contains(".jpg") && !check_list.contains(".JPG")
                                    && !check_list.contains(".pdf")
                                    && !check_list.contains(".xls") && !check_list.contains(".doc")) {
                                folder_root.setEnabled(true);
                                if (folder_path.size() == 0) {
                                    Folder_Clicked = alias_thumbImage_folder.get(position).get("folder_name");
                                } else {
                                    moveFolder_navigate = navigate_folder();
                                    Folder_Clicked = moveFolder_navigate.get(position).get("folder_name");
                                }
                                if (folder_path.size() == 0) {
                                    if (S3Objects_folder.size() > 1) {
                                        HashKey = S3Objects_folder.get(0).get("hash_keyvalue").trim();
                                    } else if (S3Objects_folder.size() == 1) {
                                        HashKey = S3Objects_folder.get(0).get("hash_keyvalue").trim();
                                    } else {
                                        HashKey = S3Objects_folder.get(position).get("hash_keyvalue").trim();
                                    }
                                    String[] split = HashKey.split("Personal");
                                    int haskeynumber = Integer.parseInt(split[split.length - 1]);
                                    HashKey = "Personal" + String.valueOf(--haskeynumber);
                                } else {
                                    if (S3Objects_folder.size() == 1) {
                                        HashKey = S3Objects_folder.get(0).get("hash_keyvalue").trim();
                                    } else if (S3Objects_folder.size() > 1) {
                                        HashKey = S3Objects_folder.get(0).get("hash_keyvalue").trim();
                                    } else {
                                        HashKey = S3Objects_folder.get(position).get("hash_keyvalue").trim();
                                    }
                                }
                                thumbImage_folder.clear();
                                S3Objects_folder.clear();
                                S3Objects_folder = new NavFolder(Folder_Clicked, HashKey).onFolderClickListener();
                                Show_Data(S3Objects_folder);
                                dialog_folder = folder1();
                           /* alias_thumbImage_folder.clear();
                            alias_thumbImage_folder.addAll(dialog_folder);*/
                                folder_path.add(Folder_Clicked);
                                folder_root.setText(Folder_Clicked);
                                folder_root.setCompoundDrawablesWithIntrinsicBounds(R.drawable.fitr_arrow, 0, 0, 0);

                                StringBuffer buffer = new StringBuffer();
                                for (int k = 0; k < folder_path.size(); k++) {
                                    buffer.append(folder_path.get(k) + "/");
                                }
                                Log.v("buffer", buffer.toString());
                                //String path_finder = first_timefolderclicked + "/" + buffer;
                                String[] make_path = buffer.toString().split("/");
                                StringBuffer path_buffer = new StringBuffer();
                                for (int i = 0; i < make_path.length; i++) {
                                    if (i == make_path.length - 1) {
                                        path_buffer.append(make_path[i]);
                                    } else {
                                        path_buffer.append(make_path[i] + "/");
                                    }
                                }
                                Log.v("buffer_upload", path_buffer.toString());

                                if (dialog_folder.size() == 0) {
                                    folder_list.setVisibility(View.GONE);
                                    empty_text.setVisibility(View.VISIBLE);
                                } else {
                                    folder_list.setVisibility(View.VISIBLE);
                                    empty_text.setVisibility(View.GONE);
                                    folder_adapter = new Folder_adapter(Filevault.this, dialog_folder, patientId, path_buffer.toString());
                                    folder_list.setAdapter(folder_adapter);
                                }
                            }
                        }
                    }
                }, 100);
            }
        });
        folder_root.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  if (folder_path.size() == 1) {
                    folder_list.setVisibility(View.VISIBLE);
                    empty_text.setVisibility(View.GONE);
                    if (moveFolder1.size() == 0) {
                        folder_adapter = new Folder_adapter(Filevault.this, moveFolder2);
                    } else {
                        folder_adapter = new Folder_adapter(Filevault.this, moveFolder1);
                    }
                } else {*/
                String path_string = folder_root.getText().toString().trim();
                if (folder_path.size() == 1) {
                    folder_list.setVisibility(View.VISIBLE);
                    empty_text.setVisibility(View.GONE);
                    StringBuffer buffer = new StringBuffer();
                    for (int k = 0; k < folder_path.size(); k++) {
                        buffer.append(folder_path.get(k) + "/");
                    }
                    Log.v("buffer", buffer.toString());
                    //String path_finder = first_timefolderclicked + "/" + buffer;
                    String[] make_path = buffer.toString().split("/");
                    StringBuffer path_buffer = new StringBuffer();
                    for (int i = 0; i < make_path.length; i++) {
                        if (i == make_path.length - 1) {
                            path_buffer.append(make_path[i]);
                        } else {
                            path_buffer.append(make_path[i] + "/");
                        }
                    }
                    Log.v("buffer_upload", path_buffer.toString());
                    if (moveFolder1.size() == 0) {
                        folder_adapter = new Folder_adapter(Filevault.this, moveFolder2, patientId, path_buffer.toString());
                    } else {
                        folder_adapter = new Folder_adapter(Filevault.this, moveFolder1, patientId, path_buffer.toString());
                    }
                    folder_list.setAdapter(folder_adapter);
                    folder_root.setText("Root");
                    folder_root.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    folder_root.setEnabled(false);
                } else {
                    folder_root.setEnabled(true);
                    int folderpath_size = folder_path.size();
                    if (S3Objects_folder.size() != 0) {
                        back_clicked_move = 0;
                    }
                    HashKey = S3Objects_folder.get(back_clicked_move).get("hash_keyvalue").trim();
                    String[] split = HashKey.split("Personal");
                    int haskeynumber = Integer.parseInt(split[split.length - 1]);
                    int use_haskey = --haskeynumber;
                    HashKey = "Personal" + String.valueOf(--use_haskey);
                    if (folderpath_size != 0) {
                        Folder_Clicked = folder_path.get(folderpath_size - 2);
                    } else {

                    }
                    thumbImage_folder.clear();
                    S3Objects_folder.clear();
                    S3Objects_folder = new NavFolder(Folder_Clicked, HashKey).onFolderClickListener();
                    Show_Data(S3Objects_folder);
                    dialog_folder = folder1();
                    if (dialog_folder.size() == 0) {
                        folder_list.setVisibility(View.GONE);
                        empty_text.setVisibility(View.VISIBLE);
                    } else {
                        StringBuffer buffer = new StringBuffer();
                        for (int k = 0; k < folder_path.size(); k++) {
                            buffer.append(folder_path.get(k) + "/");
                        }
                        Log.v("buffer", buffer.toString());
                        //String path_finder = first_timefolderclicked + "/" + buffer;
                        String[] make_path = buffer.toString().split("/");
                        StringBuffer path_buffer = new StringBuffer();
                        for (int i = 0; i < make_path.length; i++) {
                            if (i == make_path.length - 1) {
                                path_buffer.append(make_path[i]);
                            } else {
                                path_buffer.append(make_path[i] + "/");
                            }
                        }
                        Log.v("buffer_upload", path_buffer.toString());
                        folder_list.setVisibility(View.VISIBLE);
                        empty_text.setVisibility(View.GONE);
                        folder_root.setText(Folder_Clicked);
                        folder_adapter = new Folder_adapter(Filevault.this, dialog_folder, patientId, path_buffer.toString());
                        folder_list.setAdapter(folder_adapter);
                    }
                }
                for (int path = 0; path < folder_path.size(); path++) {
                   /* if (path_string.equalsIgnoreCase(Folder_Clicked)) {*/
                    folder_path.remove(path_string);
                   /* }*/
                }

              /*  }*/
                Log.v("FOLDER_PATH", folder_path.toString());
            }
        });
        move_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String check_root = folder_root.getText().toString().trim();
                if (check_root.equalsIgnoreCase("Root")) {
                    Toast.makeText(Filevault.this, "Unable to move folder on same path.Please Select a destination folder to move.", Toast.LENGTH_SHORT).show();
                } else {
                    if (folder_path.size() == 1) {
                        move_to_folder(folder_path.get(0));
                        folder_path.clear();
                        move_dialog.dismiss();
                    } else {
                        StringBuffer buffer = new StringBuffer();
                        for (int k = 0; k < folder_path.size(); k++) {
                            buffer.append(folder_path.get(k) + "/");
                        }
                        Log.v("buffer", buffer.toString());
                      /*  String path_finder = first_timefolderclicked + "/" + buffer;
                        File imageFile = new File(path);*/
                        String[] make_path = buffer.toString().split("/");
                        StringBuffer path_buffer = new StringBuffer();
                        for (int k = 0; k < make_path.length; k++) {
                            if (k == make_path.length - 1) {
                                path_buffer.append(make_path[k]);
                            } else {
                                path_buffer.append(make_path[k] + "/");
                            }
                        }
                        Log.v("buffer_upload", path_buffer.toString());
                        move_to_folder(path_buffer.toString());
                        move_dialog.dismiss();
                        folder_path.clear();
                    }
                }

            }
        });
    }

    public void Show_Data(ArrayList<HashMap<String, String>> list) {
        HashMap<String, String> hmap1;
        String duplicate_folder = "";
        for (int i = 0; i < list.size(); i++) {
            if (!(list.get(i).get("folder_name").contains("_thumb.png") ||
                    list.get(i).get("folder_name").contains("_thumb.jpg") ||
                    list.get(i).get("folder_name").contains("_thumb.PNG") ||
                    list.get(i).get("folder_name").contains("_thumb.JPG")) &&
                    !list.get(i).get("folder_name").equalsIgnoreCase(duplicate_folder)
                    && !list.get(i).get("folder_name").equalsIgnoreCase("ZurekaTempPatientConfig")) {
                duplicate_folder = list.get(i).get("folder_name");
                hmap1 = new HashMap<String, String>();
                hmap1.put("Personal3", list.get(i).get("folder_name"));
                hmap1.put("LastModified", list.get(i).get("LastModified"));
                hmap1.put("Size", list.get(i).get("Size"));
                thumbImage_folder.add(hmap1);
            }

        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    @Override
    public void onPause() {
        super.onPause();

        if ((pd != null) && pd.isShowing())
            pd.dismiss();
        pd = null;
    }

    public void preventRotation(String filePath) {
        try {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();

            bmOptions.inJustDecodeBounds = false;

            bmOptions.inPurgeable = true;


            Bitmap cameraBitmap = BitmapFactory.decodeFile(filePath);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            cameraBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            ExifInterface exif = new ExifInterface(filePath);

            float rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            System.out.println(rotation);


            float rotationInDegrees = exifToDegrees(rotation);

            System.out.println(rotationInDegrees);


            Matrix matrix = new Matrix();

            matrix.postRotate(rotationInDegrees);
            Bitmap scaledBitmap = Bitmap.createBitmap(cameraBitmap);
            Bitmap rotatedBitmap = Bitmap.createBitmap(cameraBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            FileOutputStream fos = new FileOutputStream(filePath);
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static float exifToDegrees(float exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private boolean folder_name_exists(String folder_name) {
        boolean check = false;
        for (int i = 0; i < thumbImage.size(); i++) {
            if ((thumbImage.get(i).get("Personal3").equalsIgnoreCase(folder_name))) {
                check = true;
                break;
            }
        }
        return check;
    }
}
