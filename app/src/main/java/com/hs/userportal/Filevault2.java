package com.hs.userportal;

import android.Manifest;
import android.accounts.Account;
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
import android.content.pm.PackageManager;
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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import adapters.Folder_adapter;
import adapters.Vault_adapter;
import adapters.Vault_delete_adapter;
import config.StaticHolder;
import fragment.RepositoryFragment;
import networkmngr.NetworkChangeListener;
import ui.BaseActivity;
import ui.DashBoardActivity;
import utils.NavFolder;
import utils.PreferenceHelper;

/**
 * Created by ashish on 2/15/2016.
 */
public class Filevault2 extends BaseActivity {

    private ImageLoader mImageLoader;
    private ByteArrayOutputStream byteArrayOutputStream;
    private NetworkImageView mNetworkImageView;
    private JSONObject sendData, receiveData;
    private JSONArray subArrayImage, S3Objects_arr;
    private ProgressDialog pd;
    private ArrayList<HashMap<String, String>> vault_data;
    private ArrayList<HashMap<String, String>> S3Objects;
    private ArrayList<HashMap<String, String>> S3Objects_folder;
    private ArrayList<HashMap<String, String>> S3Objects_details;
    private static ArrayList<HashMap<String, String>> thumbImage = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> thumbImage_folder = new ArrayList<HashMap<String, String>>();
    private static ArrayList<String> imageName = new ArrayList<String>();
    private static ArrayList<String> imageNamewithpdf = new ArrayList<String>();
    private Button upload;
    private static String id = null;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLERY = 2;
    private StringBuffer path_buffer;
    private Bitmap bitmap;
    private static String pic = null;
    private String picname = "";
    private static JsonObjectRequest jr;
    private JsonObjectRequest jr2, jr3, jr4, s3jr;
    private static ImageAdapter imageAdapter;
    private boolean[] thumbnailsselection;
    private int count;
    private Activity activity = Filevault2.this;
    private static Menu menu_toggle;
    private ArrayList<String> imageId = new ArrayList<String>();
    private String imageIdsToBeSent = "", mCurrentPhotoPath = null;
    private static RequestQueue queue;
    private RequestQueue queue2;
    private RequestQueue queue3;
    private RequestQueue req;
    private ListView vault_list;
    private int ipos = 0;
    private Services service;
    private GridView gridView;
    private String[] rem_dup_folder;
    private boolean check_load;
    private int check = 0, MY_PERMISSIONS_REQUEST =1;
    private ProgressDialog progress;
    private RelativeLayout list_header, list_header2;
    private byte[] byteArray;
    private static boolean view_list = false;
    private SharedPreferences sharedPreferences;
    private String list_operation, Folder_Clicked, HashKey, Folder_Clicked_folder;
    private static Vault_adapter vault_adapter;
    private Vault_delete_adapter vault_delete_adapter;
    private ProgressBar bar;
    private String path_folder = "";
    private String first_timefolderclicked = "";
    private String hash_keyvalue, patientId;
    private static String view_show;
    private static ArrayList<String> folder_path = new ArrayList<String>();
    private NotificationHandler nHandler;
    private final static Handler handler = new Handler();
    private Menu menu;
    private int path_iterator = 0;
    private static Activity self;
    private static String refresh_folderclicked, refresh_hash_keyvalue, refresh_first_timefolderclicked;
    private static int refresh_path_iterator;
    private static boolean refresh_view_show = false;
    private static Context mContext;
    private ArrayList<String> refresh_folder_path = new ArrayList<String>();
    private static ArrayList<String> static_refresh_folder_path = new ArrayList<String>();
    private ArrayList<HashMap<String, String>> moveFolder_vault2 = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> moveFolder1_vault2 = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> alias_thumbImage_folder = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> dialog_folder = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> moveFolder_navigate = new ArrayList<HashMap<String, String>>();
    private static ArrayList<String> folder_vault2_path = new ArrayList<String>();
    private TextView warning_msg, path_indicator;
    private boolean toggle_move = false;
    private Folder_adapter folder_adapter;
    private int checkdialog = 0;
    private int back_clicked_move = 0;
    private int check_grid = 0;
    private Helper mhelper;
    private String[] path_back_nav = new String[thumbImage.size()];
    private boolean path_cleared = false;
    private boolean start_navigate = false, mPermissionGranted, mIsSdkLessThanM = true;
    private int check_para = 0, select_times = 0, show_menu1 = 0, show_menu = 0, root_reached = 0;
    private int position_scroll = 0;
    private static final int REQUEST_CAMERA = 0;

    private EditText mSearchBarEditText ;

    private ImageView mSearchBarImageView , mFooterDashBoardImageView ,mFooterReportsImageView,mFooterFamilyImageView,mFooterAccountImageView;
    private LinearLayout mFooterDashBoard , mFooterReports, mFooterFamily , mFooterAccount ;

    public static final String path = Environment.getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_DCIM + "/Patient Portal";
    public static Uri Imguri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filevault2);
        setupActionBar();
        mPreferenceHelper = PreferenceHelper.getInstance();
        patientId = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID);
        mhelper = new Helper();
        Intent i = getIntent();
        mContext = Filevault2.this;
        self = Filevault2.this;
        Folder_Clicked = i.getStringExtra("Folder_Clicked");
        refresh_folderclicked = Folder_Clicked;
        mActionBar.setTitle(Folder_Clicked);
        check_load = i.getBooleanExtra("check_load", true);
        hash_keyvalue = i.getStringExtra("hash_keyvalue");
        refresh_hash_keyvalue = hash_keyvalue;
        static_refresh_folder_path = refresh_folder_path;
        path_iterator = i.getIntExtra("iterartor", 0);
        first_timefolderclicked = i.getStringExtra("first_timefolderclicked");
        refresh_first_timefolderclicked = first_timefolderclicked;
        view_show = i.getStringExtra("view");
        upload = (Button) findViewById(R.id.upload);
        gridView = (GridView) findViewById(R.id.gridView);
        vault_list = (ListView) findViewById(R.id.vault_list);
        list_header = (RelativeLayout) findViewById(R.id.list_header);
        list_header2 = (RelativeLayout) findViewById(R.id.list_header2);
        mSearchBarImageView = (ImageView) findViewById(R.id.imageview_searchbar_icon);
        mSearchBarEditText = (EditText) findViewById(R.id.et_searchbar);


        mFooterDashBoard = (LinearLayout) findViewById(R.id.footer_dashboard_container);
        mFooterReports = (LinearLayout) findViewById(R.id.footer_reports_container);
        mFooterFamily = (LinearLayout) findViewById(R.id.footer_family_container);
        mFooterAccount = (LinearLayout) findViewById(R.id.footer_account_container);

        mFooterDashBoardImageView = (ImageView) findViewById(R.id.footer_dashboard_imageview);
        mFooterReportsImageView = (ImageView) findViewById(R.id.footer_reports_imageview);
        mFooterFamilyImageView = (ImageView) findViewById(R.id.footer_family_imageview);
        mFooterAccountImageView = (ImageView) findViewById(R.id.footer_account_imageview);


        mFooterDashBoard.setOnClickListener(mOnClickListener);
        mFooterReports.setOnClickListener(mOnClickListener);
        mFooterFamily.setOnClickListener(mOnClickListener);
        mFooterAccount.setOnClickListener(mOnClickListener);

        sendData = new JSONObject();
        service = new Services(Filevault2.this);
     /*   sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        patientId = sharedPreferences.getString("ke", "");*/
        warning_msg = (TextView) findViewById(R.id.warning_msg);
        path_indicator = (TextView) findViewById(R.id.path_indicator);
        try {
            sendData.put("PatientId", patientId);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        nHandler = NotificationHandler.getInstance(this);
        bar = (ProgressBar) findViewById(R.id.pg);
        //progress = ProgressDialog.show(this, "", "Loading..", true);
        mImageLoader = MyVolleySingleton.getInstance(Filevault2.this).getImageLoader();
        queue = Volley.newRequestQueue(this);
        queue3 = Volley.newRequestQueue(this);
        req = Volley.newRequestQueue(this);
        mSearchBarEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                vault_adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        upload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                final Dialog dialog = new Dialog(Filevault2.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.uploadfile_alertbox);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                TextView title = (TextView) dialog.findViewById(R.id.title);
                TextView item1 = (TextView) dialog.findViewById(R.id.item1_tv);
                TextView item2 = (TextView) dialog.findViewById(R.id.item2_tv);
                title.setText("Insert Folder / File");
                item1.setText("Create Folder");
                item2.setText("Upload Files");

                item1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        StringBuffer buffer = new StringBuffer();
                        for (int k = 0; k < mhelper.folder_path.size(); k++) {
                            buffer.append(mhelper.folder_path.get(k) + "/");
                        }
                        Log.v("buffer", buffer.toString());
                        show_dialog(buffer.toString());

                    }
                });

                item2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        askRunTimePermissions() ;
                        chooseimage();

                    }
                });
                dialog.show();
            }
        });
        if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
            Toast.makeText(Filevault2.this, "No internet connection. Please retry", Toast.LENGTH_SHORT).show();
        } else {
       /* if(!check_load){*/
 //           new Authentication(Filevault2.this, "Filevault2", "").execute();
        }


     /*
        Filevault2.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }//public void run() {
        });
*/
          /*  Filevault.check_load = true;*/
      /*  }*/

        vault_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                StringBuffer buffer = new StringBuffer();
                for (int k = 0; k < mhelper.folder_path.size(); k++) {
                    buffer.append(mhelper.folder_path.get(k) + "/");
                }
                Log.v("buffer", buffer.toString());
                String path_finder = first_timefolderclicked + "/" + buffer;
                File imageFile = new File(path);
                String[] make_path = path_finder.toString().split("/");
                StringBuffer path_buffer = new StringBuffer();
                for (int k = 0; k < make_path.length; k++) {
                    if (k == make_path.length - 1) {
                        path_buffer.append(make_path[k]);
                    } else {
                        path_buffer.append(make_path[k] + "/");
                    }
                }
                Log.v("buffer_upload", path_buffer.toString());
                if (!thumbImage.get(position).get("Personal3").contains(".PNG") && !thumbImage.get(position).get("Personal3").contains(".png") &&
                        !thumbImage.get(position).get("Personal3").contains(".jpg")
                        && !thumbImage.get(position).get("Personal3").contains(".JPG") && !thumbImage.get(position).get("Personal3").contains(".pdf")
                        && !thumbImage.get(position).get("Personal3").contains(".xls") && !thumbImage.get(position).get("Personal3").contains(".doc")
                        && !thumbImage.get(position).get("Personal3").contains(".txt")) {
                    Intent i = new Intent(Filevault2.this, Filevault2.class);
                    i.putExtra("Folder_Clicked", thumbImage.get(position).get("Personal3").trim());
                    i.putExtra("hash_keyvalue", S3Objects.get(position).get("hash_keyvalue").trim());
                    i.putExtra("first_timefolderclicked", first_timefolderclicked);
                  /*  folder_path[path_iterator]=thumbImage.get(position).get("Personal3").trim();
                    path_iterator++;*/
                    mhelper.folder_path.add(thumbImage.get(position).get("Personal3").trim());
                    i.putExtra("iterartor", path_iterator);
                    i.putExtra("path", mhelper.folder_path);
                    i.putExtra("view", "List");
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                   /* finish();*/
                } else if (thumbImage.get(position).get("Personal3").contains(".pdf")) {
                    Intent i = new Intent(Filevault2.this, PdfReader.class);
                    if (thumbImage.get(position).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                        i.putExtra("image_url", "https://files.healthscion.com/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20"));
                    } else if (thumbImage.get(position).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                        i.putExtra("image_url", "https://files.healthscion.com/" + path_buffer.toString().replaceAll(" ", "%20") + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20"));
                    } else {
                        i.putExtra("image_url", "https://files.healthscion.com/" + patientId + "/FileVault/Personal/" + path_buffer.toString().replaceAll(" ", "%20").trim() + "/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20"));
                    }
                    String pdf_name = thumbImage.get(position).get("Personal3").replace(patientId + "/FileVault/Personal/", "");
                    i.putExtra("imagename", pdf_name/* thumbImage.get(position)*/);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                  /*  finish();*/
                    try {
                        // Toast.makeText(getBaseContext(), "Opening PDF... ", Toast.LENGTH_SHORT).show();
                        Intent inte = new Intent(Intent.ACTION_VIEW);
                        inte.setDataAndType(
                                Uri.parse("https://files.healthscion.com/" + patientId + "/FileVault/Personal/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20")),
                                "application/pdf");

                        startActivity(inte);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                       /* finish();*/
                    } catch (ActivityNotFoundException e) {
                        // Log.e("Viewer not installed on your device.", e.getMessage());
                    }

                } else if (thumbImage.get(position).get("Personal3").contains(".doc") || thumbImage.get(position).get("Personal3").contains(".docx")) {
                    Intent i = new Intent(Filevault2.this, PdfReader.class);
                    if (thumbImage.get(position).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                        i.putExtra("image_url", "https://files.healthscion.com/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20"));
                    } else if (thumbImage.get(position).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                        i.putExtra("image_url", "https://files.healthscion.com/" + path_buffer.toString().replaceAll(" ", "%20") + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20"));
                    } else {
                        i.putExtra("image_url", "https://files.healthscion.com/" + patientId + "/FileVault/Personal/" + path_buffer.toString().replaceAll(" ", "%20").trim() + "/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20"));
                    }
                    String pdf_name = thumbImage.get(position).get("Personal3").replace(patientId + "/FileVault/Personal/", "");
                    i.putExtra("imagename", pdf_name/* thumbImage.get(position)*/);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    try {
                        // Toast.makeText(getBaseContext(), "Opening DOC... ", Toast.LENGTH_SHORT).show();
                        Intent inte = new Intent(Intent.ACTION_VIEW);
                        inte.setDataAndType(
                                Uri.parse("https://files.healthscion.com/" + patientId + "/FileVault/Personal/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20")),
                                "application/pdf");

                        startActivity(inte);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                      /*  finish();*/
                    } catch (ActivityNotFoundException e) {
                        // Log.e("Viewer not installed on your device.", e.getMessage());
                    }
                } else if (thumbImage.get(position).get("Personal3").contains(".xls") || thumbImage.get(position).get("Personal3").contains(".xlsx")) {
                    Intent i = new Intent(Filevault2.this, PdfReader.class);
                    if (thumbImage.get(position).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                        i.putExtra("image_url", "https://files.healthscion.com/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20"));
                    } else if (thumbImage.get(position).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                        i.putExtra("image_url", "https://files.healthscion.com/" + path_buffer.toString().replaceAll(" ", "%20") + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20"));
                    } else {
                        i.putExtra("image_url", "https://files.healthscion.com/" + patientId + "/FileVault/Personal/" + path_buffer.toString().replaceAll(" ", "%20").trim() + "/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20"));
                    }
                    String pdf_name = thumbImage.get(position).get("Personal3").replace(patientId + "/FileVault/Personal/", "");
                    i.putExtra("imagename", pdf_name/* thumbImage.get(position)*/);
                    startActivity(i);
                    try {
                        // Toast.makeText(getBaseContext(), "Opening xsl... ", Toast.LENGTH_SHORT).show();
                        Intent inte = new Intent(Intent.ACTION_VIEW);
                        inte.setDataAndType(
                                Uri.parse("https://files.healthscion.com/" + patientId + "/FileVault/Personal/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20")),
                                "application/pdf");

                        startActivity(inte);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                      /*  finish();*/
                    } catch (ActivityNotFoundException e) {
                        // Log.e("Viewer not installed on your device.", e.getMessage());
                    }
                } else if (thumbImage.get(position).get("Personal3").contains(".txt")) {
                    Intent i = new Intent(Filevault2.this, PdfReader.class);
                    if (thumbImage.get(position).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                        i.putExtra("image_url", "https://files.healthscion.com/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20"));
                    } else if (thumbImage.get(position).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                        i.putExtra("image_url", "https://files.healthscion.com/" + path_buffer.toString().replaceAll(" ", "%20") + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20"));
                    } else {
                        i.putExtra("image_url", "https://files.healthscion.com/" + patientId + "/FileVault/Personal/" + path_buffer.toString().replaceAll(" ", "%20").trim() + "/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20"));
                    }
                    String pdf_name = thumbImage.get(position).get("Personal3").replace(patientId + "/FileVault/Personal/", "");
                    i.putExtra("imagename", pdf_name/* thumbImage.get(position)*/);
                    startActivity(i);
                    try {
                        // Toast.makeText(getBaseContext(), "Opening xsl... ", Toast.LENGTH_SHORT).show();
                        Intent inte = new Intent(Intent.ACTION_VIEW);
                        inte.setDataAndType(
                                Uri.parse("https://files.healthscion.com/" + patientId + "/FileVault/Personal/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20")),
                                "application/pdf");

                        startActivity(inte);
                    } catch (ActivityNotFoundException e) {
                        // Log.e("Viewer not installed on your device.", e.getMessage());
                    }
                } else {
                    Log.e("Rishabh"  , "Opening jpg image ");
                    Intent i = new Intent(Filevault2.this, ExpandImage.class);
                    String removeonejpg = thumbImage.get(position).get("Personal3");
                    if (thumbImage.get(position).get("Personal3").endsWith(".jpg")) {

                        // removeonejpg = thumbImage.get(position).substring(0, thumbImage.get(position).length() - 4);

                    }

                    if (removeonejpg != null) {
                        String image_url/* = thumbImage.get(position).get("Personal3").replace("_thumb", "")*/;
                        String image_name = thumbImage.get(position).get("Personal3").replace(patientId + "/FileVault/Personal/", "");
   /* if (image_url.contains("/FileVault/")) {
        i.putExtra("image", "https://files.healthscion.com/" + image_url);
    } else {
        i.putExtra("image", "https://files.healthscion.com/" + patientId + "/FileVault/" + image_url);
    }*/
                        Log.e("Rishabh" ,"Image name := "+image_name ) ;
                        if (thumbImage.get(position).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                            Log.e("Rishabh" ,"loop 1") ;
                            image_url = thumbImage.get(position).get("Personal3").replace("_thumb", "");
                        } else if (thumbImage.get(position).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                            Log.e("Rishabh" ,"loop 2 ") ;
                            image_url = path_buffer.toString() + "/" + thumbImage.get(position).get("Personal3").replace("_thumb", "");
                            Log.e("Rishabh ", "image url := "+image_url) ;
                        } else {
                            Log.e("Rishabh" ,"loop 3 ") ;
                            image_url = patientId + "/FileVault/Personal/" + path_buffer.toString() + "/" + thumbImage.get(position).get("Personal3").replace("_thumb", "");
                        }
                        i.putExtra("image", "https://files.healthscion.com/" + image_url.replaceAll(" ", "%20"));
                        i.putExtra("imagename", /*imageNamewithpdf.get(position)*/image_name);
                        startActivity(i);
                    }
                }
            }
        });
        vault_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
                vault_delete_adapter = new Vault_delete_adapter(Filevault2.this, thumbImage, view_list, patientId, thumbnailsselection, "");
                vault_list.setAdapter(vault_delete_adapter);
                  /*  vault_delete_adapter.notifyDataSetChanged();*/
                list_header2.setVisibility(View.VISIBLE);
                list_header.setVisibility(View.GONE);
                toggle_move = true;
                check_para = 1;
                menu_toggle.findItem(R.id.action_delete).setVisible(true);
                menu_toggle.findItem(R.id.save).setVisible(true);
                menu_toggle.findItem(R.id.action_move).setVisible(true);
                menu_toggle.findItem(R.id.action_home).setVisible(false);
                thumbnailsselection[pos] = true;
                return onLongListItemClick(v, pos, id);
            }
        });
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            Intent intent = null ;
            if(viewId == R.id.footer_dashboard_container) {
                mFooterDashBoardImageView.setImageResource(R.drawable.dashboard_active);
                intent = new Intent(Filevault2.this , DashBoardActivity.class);                       // TODO check intent class ..
                startActivity(intent);
            }else if (viewId == R.id.footer_reports_container){
                mFooterReportsImageView.setImageResource(R.drawable.reports_active);
                intent = new Intent(Filevault2.this , lablistdetails.class);                      // TODO check intent class ..
                startActivity(intent);
            }else if(viewId == R.id.footer_family_container){
                mFooterFamilyImageView.setImageResource(R.drawable.family_active);
                intent = new Intent(Filevault2.this , MyFamily.class);                               // TODO check intent class ..
                startActivity(intent);
            }else if(viewId == R.id.footer_account_container){
                mFooterAccountImageView.setImageResource(R.drawable.account_active);
                intent = new Intent(Filevault2.this , Account.class);                                // TODO check intent class ..
                startActivity(intent);
            }
        }
    };

    protected boolean onLongListItemClick(View v, int pos, long id) {
        Log.i("long_press", "onLongListItemClick id=" + id + "position=" + pos);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.delete, menu);
        menu_toggle = menu;
        if (view_show.equalsIgnoreCase("List")) {
            menu_toggle.findItem(R.id.action_listview).setIcon(R.drawable.ic_grid);
        } else {
            menu_toggle.findItem(R.id.action_listview).setIcon(R.drawable.ic_list);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                if (!toggle_move) {
                    if (mhelper.folder_path.size() != 0) {
                        mhelper.folder_path.remove(mhelper.folder_path.size() - 1);
                    }
                    super.onBackPressed();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                } else if (!view_list) {
                    list_header.setVisibility(View.VISIBLE);
                    list_header2.setVisibility(View.GONE);
                    Log.e("Rishabh", "vault adapter called from HOME section of onOptionsMenuItem");
                    vault_adapter = new Vault_adapter(Filevault2.this, thumbImage, false, patientId, "");
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
                    if (mhelper.folder_path.size() != 0) {
                        mhelper.folder_path.remove(mhelper.folder_path.size() - 1);
                    }
                    super.onBackPressed();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
                    StringBuffer buffer = new StringBuffer();
                    for (int k = 0; k < mhelper.folder_path.size(); k++) {
                        buffer.append(mhelper.folder_path.get(k) + "/");
                    }
                    Log.v("buffer", buffer.toString());
              /*  show_dialog(buffer.toString());*/
                    String path_finder = first_timefolderclicked + "/" + buffer;
                    File imageFile = new File(path);
                    String[] make_path = path_finder.toString().split("/");
                    StringBuffer path_buffer = new StringBuffer();
                    for (int i = 0; i < make_path.length; i++) {
                        if (i == make_path.length - 1) {
                            path_buffer.append(make_path[i]);
                        } else {
                            path_buffer.append(make_path[i] + "/");
                        }
                    }
                    Log.v("buffer_upload", path_buffer.toString());
                    vault_delete_adapter = new Vault_delete_adapter(Filevault2.this, thumbImage, view_list, patientId, thumbnailsselection, path_buffer.toString());
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

                /*Intent intent = new Intent(getApplicationContext(), Filevault.class);               // TODO change FILEVAULT class  to Repository Fragment
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);*/
                finish();
                return true;

            case R.id.save:
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
                    Toast.makeText(Filevault2.this, "Please select file(s).", Toast.LENGTH_SHORT).show();
                } /*else if (toggle_move && checkdialog == 0) {
                    StringBuffer buffer = new StringBuffer();

                    for (int k = 0; k < mhelper.folder_path.size(); k++) {
                        buffer.append(mhelper.folder_path.get(k) + "/");
                    }
                    Log.v("buffer", buffer.toString());
                    String path_finder = first_timefolderclicked + "/" + buffer;
                    File imageFile = new File(path);
                    String[] make_path = path_finder.toString().split("/");
                    StringBuffer path_buffer = new StringBuffer();
                    for (int k = 0; k < make_path.length; k++) {
                        if (k == make_path.length - 1) {
                            path_buffer.append(make_path[k]);
                        } else {
                            path_buffer.append(make_path[k] + "/");
                        }
                    }
                    Log.v("buffer_upload", path_buffer.toString());
                    list_header.setVisibility(View.VISIBLE);
                    list_header2.setVisibility(View.GONE);
                    vault_adapter = new Vault_adapter(Filevault2.this, thumbImage, false, patientId, path_buffer.toString());
                    vault_list.setAdapter(vault_adapter);
                    thumbnailsselection = new boolean[thumbImage.size()];
                    toggle_move = false;
                } */ else if (!view_list) {
                    if (!view_list /*&& !toggle_move*/ || checkdialog == 1) {

                        for (int i = 0; i < thumbnailsselection.length; i++) {
                            if (thumbnailsselection[i]) {
                                position_scroll = i;
                            }
                        }

                        StringBuffer buffer = new StringBuffer();

                        for (int k = 0; k < mhelper.folder_path.size(); k++) {
                            buffer.append(mhelper.folder_path.get(k) + "/");
                        }
                        Log.v("buffer", buffer.toString());
                        String path_finder = first_timefolderclicked + "/" + buffer;
                        File imageFile = new File(path);
                        String[] make_path = path_finder.toString().split("/");
                        StringBuffer path_buffer = new StringBuffer();
                        for (int k = 0; k < make_path.length; k++) {
                            if (k == make_path.length - 1) {
                                path_buffer.append(make_path[k]);
                            } else {
                                path_buffer.append(make_path[k] + "/");
                            }
                        }
                        Log.v("buffer_upload", path_buffer.toString());
                        vault_delete_adapter = new Vault_delete_adapter(Filevault2.this, thumbImage, view_list, patientId, thumbnailsselection, path_buffer.toString());
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
                                // toggle_move = false;
                                break;
                            }
                        }

                        if (checkdialog == 1) {

                            AlertDialog dialog = new AlertDialog.Builder(Filevault2.this).create();
                            dialog.setTitle("Save");
                            dialog.setMessage("Are you sure you want to save the selected file(s)?");

                            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int id) {
                                   /* StringBuffer buffer = new StringBuffer();

                                    for (int k = 0; k < mhelper.folder_path.size(); k++) {
                                        buffer.append(mhelper.folder_path.get(k) + "/");
                                    }
                                    Log.v("buffer", buffer.toString());
                                    String path_finder = first_timefolderclicked + "/" + buffer;
                                    String[] make_path = path_finder.toString().split("/");
                                    StringBuffer path_buffer = new StringBuffer();
                                    for (int k = 0; k < make_path.length; k++) {
                                        if (k == make_path.length - 1) {
                                            path_buffer.append(make_path[k]);
                                        } else {
                                            path_buffer.append(make_path[k] + "/");
                                        }
                                    }
                                    Log.v("buffer_upload", path_buffer.toString());
                                    list_header.setVisibility(View.VISIBLE);
                                    list_header2.setVisibility(View.GONE);
                                    vault_adapter = new Vault_adapter(Filevault2.this, thumbImage, false, patientId, path_buffer.toString());
                                    vault_list.setAdapter(vault_adapter);
                                    vault_list.setSelection(position_scroll);
                                    thumbnailsselection = new boolean[thumbImage.size()];
                                    menu_toggle.findItem(R.id.action_move).setVisible(false);
                                    menu_toggle.findItem(R.id.save).setVisible(false);
                                    menu_toggle.findItem(R.id.action_delete).setVisible(false);
                                    menu_toggle.findItem(R.id.action_home).setVisible(true);*/
                                    dialog.dismiss();
                                    //  toggle_move = false;
                                    check_para = 0;

                                }
                            });

                            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    StringBuffer buffer = new StringBuffer();
                                    for (int k = 0; k < mhelper.folder_path.size(); k++) {
                                        buffer.append(mhelper.folder_path.get(k) + "/");
                                    }
                                    Log.v("buffer", buffer.toString());
                                    String path_finder = first_timefolderclicked + "/" + buffer;
                                    File imageFile = new File(path);
                                    String[] make_path = path_finder.toString().split("/");
                                    StringBuffer path_buffer = new StringBuffer();
                                    for (int k = 0; k < make_path.length; k++) {
                                        if (k == make_path.length - 1) {
                                            path_buffer.append(make_path[k]);
                                        } else {
                                            path_buffer.append(make_path[k] + "/");
                                        }
                                    }

                                    ipos = 0;
                                    toggle_move = false;
                                    check_para = 0;
                                    Toast.makeText(Filevault2.this, "Image(s) would be saved on " + path, Toast.LENGTH_SHORT).show();
                                    String imageurl;
                                    for (int i = 0; i < thumbnailsselection.length; i++) {

                                        if (thumbnailsselection[i]) {

                                            Log.v("buffer_upload", path_buffer.toString());
                                            if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                                                //imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                imageurl = thumbImage.get(i).get("Personal3");
                                            } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                                // imageobject.put("Key", path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                                                imageurl = path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3");
                                            } else {
                                                // imageobject.put("Key", patientId + "/FileVault/" + path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                                                imageurl = patientId + "/FileVault/Personal/" + path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3");
                                            }
                                            ImageRequest ir = new ImageRequest("https://files.healthscion.com/" + /*thumbImage.get(i).get("Personal3")*/imageurl,
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

                                                            nHandler.createSimpleNotification(Filevault2.this, ipos, fname);

                                                        }
                                                    }, 0, 0, null, null);

                                            queue3.add(ir);

                                        }

                                    }

                                }
                            });
                            dialog.show();

                        } else {
                            Toast.makeText(Filevault2.this, "Please select file(s).", Toast.LENGTH_SHORT).show();
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

                        AlertDialog dialog = new AlertDialog.Builder(Filevault2.this).create();
                        dialog.setTitle("Save");
                        dialog.setMessage("Are you sure you want to save the selected file(s)?");

                        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                                dialog.dismiss();
                                menu_toggle.findItem(R.id.action_move).setVisible(false);
                                menu_toggle.findItem(R.id.save).setVisible(false);
                                menu_toggle.findItem(R.id.action_delete).setVisible(false);
                                menu_toggle.findItem(R.id.action_home).setVisible(true);
                                toggle_move = false;

                            }
                        });

                        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                StringBuffer buffer = new StringBuffer();
                                for (int k = 0; k < mhelper.folder_path.size(); k++) {
                                    buffer.append(mhelper.folder_path.get(k) + "/");
                                }
                                Log.v("buffer", buffer.toString());
                                String path_finder = first_timefolderclicked + "/" + buffer;
                                File imageFile = new File(path);
                                String[] make_path = path_finder.toString().split("/");
                                StringBuffer path_buffer = new StringBuffer();
                                for (int k = 0; k < make_path.length; k++) {
                                    if (k == make_path.length - 1) {
                                        path_buffer.append(make_path[k]);
                                    } else {
                                        path_buffer.append(make_path[k] + "/");
                                    }
                                }
                                ipos = 0;
                                toggle_move = false;
                                Toast.makeText(Filevault2.this, "Image(s) would be saved on " + path, Toast.LENGTH_SHORT).show();
                                String imageurl;
                                for (int i = 0; i < thumbnailsselection.length; i++) {

                                    if (thumbnailsselection[i]) {

                                        if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                                            //imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                            imageurl = thumbImage.get(i).get("Personal3");
                                        } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                            // imageobject.put("Key", path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                                            imageurl = path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3");
                                        } else {
                                            // imageobject.put("Key", patientId + "/FileVault/" + path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                                            imageurl = patientId + "/FileVault/Personal/" + path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3");
                                        }
                                        ImageRequest ir = new ImageRequest("https://files.healthscion.com/" + /*thumbImage.get(i).get("Personal3")*/imageurl,
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

                                                        nHandler.createSimpleNotification(Filevault2.this, ipos, fname);

                                                    }
                                                }, 0, 0, null, null);

                                        queue3.add(ir);

                                    }

                                }

                            }
                        });
                        dialog.show();

                    } else {
                        Toast.makeText(Filevault2.this, "Please select file(s).", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Filevault2.this, "Please select file(s).", Toast.LENGTH_SHORT).show();
                } /*else if (toggle_move && checkdialog == 0) {
                    StringBuffer buffer = new StringBuffer();
                    for (int k = 0; k < mhelper.folder_path.size(); k++) {
                        buffer.append(mhelper.folder_path.get(k) + "/");
                    }
                    Log.v("buffer", buffer.toString());
                    String path_finder = first_timefolderclicked + "/" + buffer;
                    File imageFile = new File(path);
                    String[] make_path = path_finder.toString().split("/");
                    StringBuffer path_buffer = new StringBuffer();
                    for (int k = 0; k < make_path.length; k++) {
                        if (k == make_path.length - 1) {
                            path_buffer.append(make_path[k]);
                        } else {
                            path_buffer.append(make_path[k] + "/");
                        }
                    }
                    Log.v("buffer_upload", path_buffer.toString());
                    list_header.setVisibility(View.VISIBLE);
                    list_header2.setVisibility(View.GONE);
                    vault_adapter = new Vault_adapter(Filevault2.this, thumbImage, false, patientId, path_buffer.toString());
                    vault_list.setAdapter(vault_adapter);
                    thumbnailsselection = new boolean[thumbImage.size()];
                    toggle_move = false;
                }*/ else if (!view_list) {
                    if (!view_list/* && !toggle_move*/ || checkdialog == 1) {
                        for (int i = 0; i < thumbnailsselection.length; i++) {
                            if (thumbnailsselection[i]) {
                                position_scroll = i;
                            }
                        }
                        StringBuffer buffer = new StringBuffer();
                        for (int k = 0; k < mhelper.folder_path.size(); k++) {
                            buffer.append(mhelper.folder_path.get(k) + "/");
                        }
                        Log.v("buffer", buffer.toString());
                        String path_finder = first_timefolderclicked + "/" + buffer;
                        File imageFile = new File(path);
                        String[] make_path = path_finder.toString().split("/");
                        StringBuffer path_buffer = new StringBuffer();
                        for (int k = 0; k < make_path.length; k++) {
                            if (k == make_path.length - 1) {
                                path_buffer.append(make_path[k]);
                            } else {
                                path_buffer.append(make_path[k] + "/");
                            }
                        }
                        Log.v("buffer_upload", path_buffer.toString());
                        list_header.setVisibility(View.GONE);
                        list_header2.setVisibility(View.VISIBLE);
                        vault_delete_adapter = new Vault_delete_adapter(Filevault2.this, thumbImage, view_list, patientId, thumbnailsselection, path_buffer.toString());
                        vault_list.setAdapter(vault_delete_adapter);
                        vault_list.setSelection(position_scroll);
                        toggle_move = true;
                        //  vault_delete_adapter.notifyDataSetChanged();
                        checkdialog = 0;
                        for (int i = 0; i < thumbnailsselection.length; i++) {
                            if (thumbnailsselection[i]) {
                                checkdialog = 1;
                                // toggle_move = false;
                                break;
                            }
                        }

                        if (checkdialog == 1) {


                            final Dialog dialog = new Dialog(this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.unsaved_alert_dialog);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);
                            TextView messageTv = (TextView) dialog.findViewById(R.id.message);
                            TextView titleTv = (TextView) dialog.findViewById(R.id.title);
                            TextView okBTN = (TextView) dialog.findViewById(R.id.btn_ok);
                            TextView cancelButton = (TextView) dialog.findViewById(R.id.stay_btn);
                            titleTv.setText("Delete");
                            messageTv.setText("Are you sure you want to delete the selected file(s)?");

                            cancelButton.setText("Cancel");
                            cancelButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    check_para = 0;
                                    select_times = 0;
                                }
                            });
                            okBTN.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    {

                                        pd = new ProgressDialog(Filevault2.this);
                                        pd.setMessage("Deleting .....");
                                        pd.show();
                                        toggle_move = false;
                                        JSONArray array = new JSONArray();
                                        check_para = 0;
                                        select_times = 0;
                                        StringBuffer buffer = new StringBuffer();
                                        for (int k = 0; k < mhelper.folder_path.size(); k++) {
                                            buffer.append(mhelper.folder_path.get(k) + "/");
                                        }
                                        Log.v("buffer", buffer.toString());
                                        String path_finder = first_timefolderclicked + "/" + buffer;
                                        File imageFile = new File(path);
                                        String[] make_path = path_finder.toString().split("/");
                                        StringBuffer path_buffer = new StringBuffer();
                                        for (int k = 0; k < make_path.length; k++) {
                                            if (k == make_path.length - 1) {
                                                path_buffer.append(make_path[k]);
                                            } else {
                                                path_buffer.append(make_path[k] + "/");
                                            }
                                        }
                                        Log.v("buffer_upload", path_buffer.toString());
                                        for (int i = 0; i < thumbnailsselection.length; i++) {
                                            JSONObject imageobject = new JSONObject();
                                            if (thumbnailsselection[i]) {
                                                try {
                                                    //imageId.add(patientId+"/FileVault/"+thumbImage.get(i).get("folder_name"));

                                                    if (!thumbImage.get(i).get("Personal3").contains(".PNG") && !thumbImage.get(i).get("Personal3").contains(".png") &&
                                                            !thumbImage.get(i).get("Personal3").contains(".jpg") && !thumbImage.get(i).get("Personal3").contains(".pdf")
                                                            && !thumbImage.get(i).get("Personal3").contains(".xls") && !thumbImage.get(i).get("Personal3").contains(".doc")) {
                                                        if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                                                            imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                        } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                                            imageobject.put("Key", path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                                                        } else {
                                                            imageobject.put("Key", patientId + "/FileVault/Personal/" + path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                                                        }
                                                        imageobject.put("Type", "1");
                                                        imageobject.put("ThumbFile", "");
                                                        imageobject.put("Status", "");
                                                    } else {
                                                        if (thumbImage.get(i).get("Personal3").contains(".png")) {
                                                            String thumbimg = thumbImage.get(i).get("Personal3").replaceAll("\\.png", "_thumb.png");
                                                            if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                                                                imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                            } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                                                imageobject.put("Key", path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                                                            } else {
                                                                imageobject.put("Key", patientId + "/FileVault/Personal/" + path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                                                            }
                                                            imageobject.put("Type", "0");
                                                            if (thumbimg.startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                                                                imageobject.put("ThumbFile", thumbimg);
                                                            } else if (thumbimg.contains(patientId + "/FileVault/Personal/")) {
                                                                imageobject.put("ThumbFile", path_buffer.toString() + "/" + thumbimg);
                                                            } else {
                                                                imageobject.put("ThumbFile", patientId + "/FileVault/Personal/" + path_buffer.toString() + "/" + thumbimg);
                                                            }
                                                            imageobject.put("Status", "");
                                                        } else if (thumbImage.get(i).get("Personal3").contains(".PNG")) {
                                                            String thumbimg = thumbImage.get(i).get("Personal3").replaceAll("\\.PNG", "_thumb.PNG");
                                                            if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                                                                imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                            } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                                                imageobject.put("Key", path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                                                            } else {
                                                                imageobject.put("Key", patientId + "/FileVault/Personal/" + path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                                                            }
                                                            imageobject.put("Type", "0");
                                                            if (thumbimg.startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                                                                imageobject.put("ThumbFile", thumbimg);
                                                            } else if (thumbimg.contains(patientId + "/FileVault/Personal/")) {
                                                                imageobject.put("ThumbFile", path_buffer.toString() + "/" + thumbimg);
                                                            } else {
                                                                imageobject.put("ThumbFile", patientId + "/FileVault/Personal/" + path_buffer.toString() + "/" + thumbimg);
                                                            }
                                                            imageobject.put("Status", "");
                                                        } else if (thumbImage.get(i).get("Personal3").contains(".jpg")) {
                                                            String thumbimg = thumbImage.get(i).get("Personal3").replaceAll("\\.jpg", "_thumb.jpg");
                                                            if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                                                                imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                            } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                                                imageobject.put("Key", path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                                                            } else {
                                                                imageobject.put("Key", patientId + "/FileVault/Personal/" + path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                                                            }
                                                            imageobject.put("Type", "0");
                                                            if (thumbimg.startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                                                                imageobject.put("ThumbFile", thumbimg);
                                                            } else if (thumbimg.contains(patientId + "/FileVault/Personal/")) {
                                                                imageobject.put("ThumbFile", path_buffer.toString() + "/" + thumbimg);
                                                            } else {
                                                                imageobject.put("ThumbFile", patientId + "/FileVault/Personal/" + path_buffer.toString() + "/" + thumbimg);
                                                            }
                                                            imageobject.put("Status", "");
                                                        } else if (thumbImage.get(i).get("Personal3").contains(".JPG")) {
                                                            String thumbimg = thumbImage.get(i).get("Personal3").replaceAll("\\.JPG", "_thumb.JPG");
                                                            if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                                                                imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                            } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                                                imageobject.put("Key", path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                                                            } else {
                                                                imageobject.put("Key", patientId + "/FileVault/Personal/" + path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                                                            }
                                                            imageobject.put("Type", "0");
                                                            if (thumbimg.startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                                                                imageobject.put("ThumbFile", thumbimg);
                                                            } else if (thumbimg.contains(patientId + "/FileVault/Personal/")) {
                                                                imageobject.put("ThumbFile", path_buffer.toString() + "/" + thumbimg);
                                                            } else {
                                                                imageobject.put("ThumbFile", patientId + "/FileVault/Personal/" + path_buffer.toString() + "/" + thumbimg);
                                                            }
                                                            imageobject.put("Status", "");
                                                        } else {
                                                            if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                                                                imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                            } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                                                imageobject.put("Key", path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                                                            } else {
                                                                imageobject.put("Key", patientId + "/FileVault/Personal/" + path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
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

                                        queue2 = Volley.newRequestQueue(Filevault2.this);

                                        sendData = new JSONObject();
                                        try {
                                            sendData.put("ObjectList", array);
                                            sendData.put("UserId", patientId);
                                        } catch (JSONException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }

				/*		String url = Services.init + "PatientModule/PatientService.asmx/DeletePatientFiles";*/
                                        StaticHolder sttc_holdr = new StaticHolder(Filevault2.this, StaticHolder.Services_static.DeleteObject);
                                        String url = sttc_holdr.request_Url();
                                        jr2 = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                System.out.println(response);

                                                try {
                                                    Toast.makeText(Filevault2.this, " Item(s) successfully deleted.", Toast.LENGTH_SHORT)
                                                            .show();
                                                    // S3Objects.clear();
                                                    pd.dismiss();
                                                    RepositoryFragment.refresh();

                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                      /*  Intent i = new Intent(Filevault2.this, Filevault2.class);
                                                        i.putExtra("Folder_Clicked", refresh_folderclicked);
                                                        i.putExtra("hash_keyvalue", refresh_hash_keyvalue);
                                                        i.putExtra("first_timefolderclicked", refresh_first_timefolderclicked);
                                                        i.putExtra("iterartor", refresh_path_iterator);
                                                        i.putExtra("path", refresh_folder_path);
                                                        if (!refresh_view_show) {
                                                            i.putExtra("view", "List");
                                                        } else {
                                                            i.putExtra("view", "Gird");
                                                        }
                                                        startActivity(i);
                                                        finish();*/
                                                            startBackgroundprocess();

                                                        }
                                                    }, 1000);

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
                                                Toast.makeText(Filevault2.this, error.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        queue2.add(jr2);


                                    }
                                    dialog.dismiss();
                                }

                            });

                            dialog.show();
                        } else {
                            Toast.makeText(Filevault2.this, "Please select file(s).", Toast.LENGTH_SHORT).show();
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

                        AlertDialog dialog = new AlertDialog.Builder(Filevault2.this).create();
                        dialog.setTitle("Delete");
                        dialog.setMessage("Are you sure you want to delete the selected file(s)?");

                        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                               /* StringBuffer buffer = new StringBuffer();
                                for (int k = 0; k < mhelper.folder_path.size(); k++) {
                                    buffer.append(mhelper.folder_path.get(k) + "/");
                                }
                                Log.v("buffer", buffer.toString());
                                String path_finder = first_timefolderclicked + "/" + buffer;
                                String[] make_path = path_finder.toString().split("/");
                                StringBuffer path_buffer = new StringBuffer();
                                for (int i = 0; i < make_path.length; i++) {
                                    if (i == make_path.length - 1) {
                                        path_buffer.append(make_path[i]);
                                    } else {
                                        path_buffer.append(make_path[i] + "/");
                                    }
                                }
                                Log.v("buffer_upload", path_buffer.toString());
                                vault_adapter = new Vault_adapter(Filevault2.this, thumbImage, false, patientId, path_buffer.toString());
                                vault_list.setAdapter(vault_adapter);
                                vault_list.setSelection(position_scroll);
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
                                // TODO Auto-generated method stub

                                JSONArray array = new JSONArray();
                                StringBuffer buffer = new StringBuffer();
                                for (int k = 0; k < mhelper.folder_path.size(); k++) {
                                    buffer.append(mhelper.folder_path.get(k) + "/");
                                }
                                Log.v("buffer", buffer.toString());
                                String path_finder = first_timefolderclicked + "/" + buffer;
                                File imageFile = new File(path);
                                String[] make_path = path_finder.toString().split("/");
                                StringBuffer path_buffer = new StringBuffer();
                                for (int k = 0; k < make_path.length; k++) {
                                    if (k == make_path.length - 1) {
                                        path_buffer.append(make_path[k]);
                                    } else {
                                        path_buffer.append(make_path[k] + "/");
                                    }
                                }
                                Log.v("buffer_upload", path_buffer.toString());
                                for (int i = 0; i < thumbnailsselection.length; i++) {
                                    JSONObject imageobject = new JSONObject();
                                    if (thumbnailsselection[i]) {
                                        try {
                                            //imageId.add(patientId+"/FileVault/"+thumbImage.get(i).get("folder_name"));

                                            if (!thumbImage.get(i).get("Personal3").contains(".PNG") && !thumbImage.get(i).get("Personal3").contains(".png") &&
                                                    !thumbImage.get(i).get("Personal3").contains(".jpg") && !thumbImage.get(i).get("Personal3").contains(".pdf")
                                                    && !thumbImage.get(i).get("Personal3").contains(".xls") && !thumbImage.get(i).get("Personal3").contains(".doc")) {
                                                if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                                                    imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                                    imageobject.put("Key", path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                                                } else {
                                                    imageobject.put("Key", patientId + "/FileVault/Personal/" + path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                                                }
                                                imageobject.put("Type", "1");
                                                imageobject.put("ThumbFile", "");
                                                imageobject.put("Status", "");
                                            } else {
                                                if (thumbImage.get(i).get("Personal3").contains(".png")) {
                                                    String thumbimg = thumbImage.get(i).get("Personal3").replaceAll("\\.png", "_thumb.png");
                                                    if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                                                        imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                    } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("Key", path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                                                    } else {
                                                        imageobject.put("Key", patientId + "/FileVault/Personal/" + path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                                                    }
                                                    imageobject.put("Type", "0");
                                                    if (thumbimg.startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                                                        imageobject.put("ThumbFile", thumbimg);
                                                    } else if (thumbimg.contains(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("ThumbFile", path_buffer.toString() + "/" + thumbimg);
                                                    } else {
                                                        imageobject.put("ThumbFile", patientId + "/FileVault/Personal/" + path_buffer.toString() + "/" + thumbimg);
                                                    }
                                                    imageobject.put("Status", "");
                                                } else if (thumbImage.get(i).get("Personal3").contains(".PNG")) {
                                                    String thumbimg = thumbImage.get(i).get("Personal3").replaceAll("\\.PNG", "_thumb.PNG");
                                                    if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                                                        imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                    } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("Key", path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                                                    } else {
                                                        imageobject.put("Key", patientId + "/FileVault/Personal/" + path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                                                    }
                                                    imageobject.put("Type", "0");
                                                    if (thumbimg.startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                                                        imageobject.put("ThumbFile", thumbimg);
                                                    } else if (thumbimg.contains(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("ThumbFile", path_buffer.toString() + "/" + thumbimg);
                                                    } else {
                                                        imageobject.put("ThumbFile", patientId + "/FileVault/Personal/" + path_buffer.toString() + "/" + thumbimg);
                                                    }
                                                    imageobject.put("Status", "");
                                                } else if (thumbImage.get(i).get("Personal3").contains(".jpg")) {
                                                    String thumbimg = thumbImage.get(i).get("Personal3").replaceAll("\\.jpg", "_thumb.jpg");
                                                    if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                                                        imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                    } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("Key", path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                                                    } else {
                                                        imageobject.put("Key", patientId + "/FileVault/Personal/" + path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                                                    }
                                                    imageobject.put("Type", "0");
                                                    if (thumbimg.startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                                                        imageobject.put("ThumbFile", thumbimg);
                                                    } else if (thumbimg.contains(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("ThumbFile", path_buffer.toString() + "/" + thumbimg);
                                                    } else {
                                                        imageobject.put("ThumbFile", patientId + "/FileVault/Personal/" + path_buffer.toString() + "/" + thumbimg);
                                                    }
                                                    imageobject.put("Status", "");
                                                } else if (thumbImage.get(i).get("Personal3").contains(".JPG")) {
                                                    String thumbimg = thumbImage.get(i).get("Personal3").replaceAll("\\.JPG", "_thumb.JPG");
                                                    if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                                                        imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                    } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("Key", path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                                                    } else {
                                                        imageobject.put("Key", patientId + "/FileVault/Personal/" + path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                                                    }
                                                    imageobject.put("Type", "0");
                                                    if (thumbimg.startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                                                        imageobject.put("ThumbFile", thumbimg);
                                                    } else if (thumbimg.contains(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("ThumbFile", path_buffer.toString() + "/" + thumbimg);
                                                    } else {
                                                        imageobject.put("ThumbFile", patientId + "/FileVault/Personal/" + path_buffer.toString() + "/" + thumbimg);
                                                    }
                                                    imageobject.put("Status", "");
                                                } else {
                                                    if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                                                        imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                                                    } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                                        imageobject.put("Key", path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                                                    } else {
                                                        imageobject.put("Key", patientId + "/FileVault/Personal/" + path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
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

                                queue2 = Volley.newRequestQueue(Filevault2.this);

                                sendData = new JSONObject();
                                try {
                                    sendData.put("ObjectList", array);
                                    sendData.put("UserId", patientId);
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

				/*		String url = Services.init + "PatientModule/PatientService.asmx/DeletePatientFiles";*/
                                StaticHolder sttc_holdr = new StaticHolder(Filevault2.this, StaticHolder.Services_static.DeleteObject);
                                String url = sttc_holdr.request_Url();
                                jr2 = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        System.out.println(response);

                                        try {
                                            Toast.makeText(Filevault2.this, "Item(s) successfully deleted.", Toast.LENGTH_SHORT)
                                                    .show();
                                            toggle_move = false;
                                            // S3Objects.clear();
                                            RepositoryFragment.refresh();

                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                   /* Intent i = new Intent(Filevault2.this, Filevault2.class);
                                                    i.putExtra("Folder_Clicked", refresh_folderclicked);
                                                    i.putExtra("hash_keyvalue", refresh_hash_keyvalue);
                                                    i.putExtra("iterartor", refresh_path_iterator);
                                                    i.putExtra("path", refresh_folder_path);
                                                    if (!refresh_view_show) {
                                                        i.putExtra("view", "List");
                                                    } else {
                                                        i.putExtra("view", "Gird");
                                                    }
                                                    startActivity(i);
                                                    finish();*/
                                                    startBackgroundprocess();


                                                }
                                            }, 1000);

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
                                        Toast.makeText(Filevault2.this, error.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                queue2.add(jr2);
                            }
                        });
                        dialog.show();
                    } else {
                        Toast.makeText(Filevault2.this, "Please select file(s).", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;

            case R.id.action_listview:
                if (!view_list) {
                    check_grid = 0;
                    show_menu = 0;
                    view_show = "Gird";
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
                   /* vault_adapter = new Vault_adapter(Filevault.this, thumbImage, false, patientId);
                    vault_list.setAdapter(vault_adapter);*/
                    imageAdapter.notifyDataSetChanged();
                    list_header.setVisibility(View.GONE);
                    list_header2.setVisibility(View.GONE);
                    vault_list.setVisibility(View.GONE);
                    menu_toggle.findItem(R.id.action_listview).setIcon(R.drawable.ic_list);
                    view_list = true;
                    refresh_view_show = true;
                } else {
                    check = 0;
                    show_menu1 = 0;
                    view_show = "List";
                    for (int i = 0; i < thumbnailsselection.length; i++) {
                        if (thumbnailsselection[i]) {
                            show_menu1 = 1;
                            check = 1;
                            break;
                        }
                    }

                    StringBuffer buffer = new StringBuffer();
                    for (int k = 0; k < mhelper.folder_path.size(); k++) {
                        buffer.append(mhelper.folder_path.get(k) + "/");
                    }
                    Log.v("buffer", buffer.toString());
              /*  show_dialog(buffer.toString());*/
                    String path_finder = first_timefolderclicked + "/" + buffer;
                    File imageFile = new File(path);
                    String[] make_path = path_finder.toString().split("/");
                    StringBuffer path_buffer = new StringBuffer();
                    for (int i = 0; i < make_path.length; i++) {
                        if (i == make_path.length - 1) {
                            path_buffer.append(make_path[i]);
                        } else {
                            path_buffer.append(make_path[i] + "/");
                        }
                    }
                    Log.v("buffer_upload", path_buffer.toString());
                    if (show_menu1 == 1) {
                        vault_delete_adapter = new Vault_delete_adapter(Filevault2.this, thumbImage, view_list, patientId, thumbnailsselection, path_buffer.toString());
                        vault_list.setAdapter(vault_delete_adapter);
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
                        Log.e("Rishabh", "On Option Item Selected ; by default listview calling adapter : #1738");
                        vault_adapter = new Vault_adapter(Filevault2.this, thumbImage, false, patientId, path_buffer.toString());
                        vault_list.setAdapter(vault_adapter);
                        vault_list.setVisibility(View.VISIBLE);
                        gridView.setVisibility(View.GONE);
                        menu_toggle.findItem(R.id.action_move).setVisible(false);
                        menu_toggle.findItem(R.id.save).setVisible(false);
                        menu_toggle.findItem(R.id.action_delete).setVisible(false);
                        menu_toggle.findItem(R.id.action_home).setVisible(true);
                    }
                   /* vault_adapter = new Vault_adapter(Filevault2.this, thumbImage, false, patientId, path_buffer.toString());
                    vault_list.setAdapter(vault_adapter);
                    vault_list.setVisibility(View.VISIBLE);
                    gridView.setVisibility(View.GONE);*/
                    menu_toggle.findItem(R.id.action_listview).setIcon(R.drawable.ic_grid);
                    view_list = false;
                    refresh_view_show = false;
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
                    Toast.makeText(Filevault2.this, "Please select file(s).", Toast.LENGTH_SHORT).show();
                } /*else if (toggle_move && checkdialog == 0) {
                    StringBuffer buffer = new StringBuffer();
                    for (int k = 0; k < mhelper.folder_path.size(); k++) {
                        buffer.append(mhelper.folder_path.get(k) + "/");
                    }
                    Log.v("buffer", buffer.toString());
              *//*  show_dialog(buffer.toString());*//*
                    String path_finder = first_timefolderclicked + "/" + buffer;
                    File imageFile = new File(path);
                    String[] make_path = path_finder.toString().split("/");
                    StringBuffer path_buffer = new StringBuffer();
                    for (int i = 0; i < make_path.length; i++) {
                        if (i == make_path.length - 1) {
                            path_buffer.append(make_path[i]);
                        } else {
                            path_buffer.append(make_path[i] + "/");
                        }
                    }
                    Log.v("buffer_upload", path_buffer.toString());
                    list_header.setVisibility(View.VISIBLE);
                    list_header2.setVisibility(View.GONE);
                    vault_adapter = new Vault_adapter(Filevault2.this, thumbImage, false, patientId, path_buffer.toString());
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

                        StringBuffer buffer = new StringBuffer();
                        for (int k = 0; k < mhelper.folder_path.size(); k++) {
                            buffer.append(mhelper.folder_path.get(k) + "/");
                        }
                        Log.v("buffer", buffer.toString());
                        String path_finder = first_timefolderclicked + "/" + buffer;
                        File imageFile = new File(path);
                        String[] make_path = path_finder.toString().split("/");
                        final StringBuffer path_buffer = new StringBuffer();
                        for (int k = 0; k < make_path.length; k++) {
                            if (k == make_path.length - 1) {
                                path_buffer.append(make_path[k]);
                            } else {
                                path_buffer.append(make_path[k] + "/");
                            }
                        }
                        Log.v("buffer_upload", path_buffer.toString());
                        list_header.setVisibility(View.GONE);
                        list_header2.setVisibility(View.VISIBLE);
                        vault_delete_adapter = new Vault_delete_adapter(Filevault2.this, thumbImage, view_list, patientId, thumbnailsselection, path_buffer.toString());
                        vault_list.setAdapter(vault_delete_adapter);
                        vault_list.setSelection(position_scroll);
                        toggle_move = true;
                        //  vault_delete_adapter.notifyDataSetChanged();
                        checkdialog = 0;
                        select_times = 0;
                        for (int i = 0; i < thumbnailsselection.length; i++) {
                            if (thumbnailsselection[i]) {
                                checkdialog = 1;
                                // toggle_move = false;
                                break;
                            }
                        }

                        if (checkdialog == 1) {

                            AlertDialog dialog = new AlertDialog.Builder(Filevault2.this).create();
                            dialog.setTitle("Move");
                            dialog.setMessage("Are you sure you want to move the selected file(s) or folder(s)?");

                            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int id) {

                               /*     StringBuffer buffer = new StringBuffer();
                                    for (int k = 0; k < mhelper.folder_path.size(); k++) {
                                        buffer.append(mhelper.folder_path.get(k) + "/");
                                    }
                                    Log.v("buffer", buffer.toString());
                                    String path_finder = first_timefolderclicked + "/" + buffer;
                                    String[] make_path = path_finder.toString().split("/");
                                    StringBuffer path_buffer = new StringBuffer();
                                    for (int i = 0; i < make_path.length; i++) {
                                        if (i == make_path.length - 1) {
                                            path_buffer.append(make_path[i]);
                                        } else {
                                            path_buffer.append(make_path[i] + "/");
                                        }
                                    }
                                    Log.v("buffer_upload", path_buffer.toString());
                                    list_header.setVisibility(View.GONE);
                                    list_header2.setVisibility(View.VISIBLE);
                                    vault_adapter = new Vault_adapter(Filevault2.this, thumbImage, false, patientId, path_buffer.toString());
                                    vault_list.setAdapter(vault_adapter);
                                    vault_list.setSelection(position_scroll);
                                    thumbnailsselection = new boolean[thumbImage.size()];
                                    menu_toggle.findItem(R.id.action_move).setVisible(false);
                                    menu_toggle.findItem(R.id.save).setVisible(false);
                                    menu_toggle.findItem(R.id.action_delete).setVisible(false);
                                    menu_toggle.findItem(R.id.action_home).setVisible(true);*/
                                    dialog.dismiss();
                                    check_para = 0;
                                    select_times = 0;
                                    // toggle_move = false;

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
                                    moveFolder_vault2 = foldername();

                                    check_para = 0;
                                    select_times = 0;
                                /*JSONArray array = new
                                JSONArray();

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
                                    if (rem_dup_folder != null && moveFolder_vault2.size() != 0) {
                                        for (int r = 0; r < moveFolder_vault2.size(); r++) {
                                            for (int l = 0; l < rem_dup_folder.length; l++) {
                                                if (moveFolder_vault2.size() != 0) {
                                                    if (moveFolder_vault2.get(r).get("folder_name").equalsIgnoreCase(rem_dup_folder[l])) {
                                                        moveFolder_vault2.remove(r);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    alias_thumbImage_folder.addAll(moveFolder_vault2);
                                   /* String[] stockArr = new String[moveFolder_vault2.size()];
                                    stockArr = moveFolder_vault2.toArray(stockArr);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Filevault2.this);
                                    builder.setTitle("Make your selection");
                                    builder.setItems(stockArr, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int item) {
                                            //Toast.makeText(Filevault.this,folder_move.get(item).toString(),Toast.LENGTH_SHORT).show();
                                            move_to_folder(moveFolder_vault2.get(item).toString());
                                        }
                                    });
                                    AlertDialog alert = builder.create();
                                    alert.show();*/

                                    final Dialog move_dialog = new Dialog(Filevault2.this);
                                    move_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    //setting custom layout to dialog
                                    move_dialog.setContentView(R.layout.move_folderlist);
                                    ListView folder_list = (ListView) move_dialog.findViewById(R.id.folder_list);
                                    Button move_btn = (Button) move_dialog.findViewById(R.id.move_btn);
                                    final TextView folder_root = (TextView) move_dialog.findViewById(R.id.folder_root);
                                    folder_root.setText(Folder_Clicked);
                                    folder_root.setCompoundDrawablesWithIntrinsicBounds(R.drawable.fitr_arrow, 0, 0, 0);
                                    folder_root.setEnabled(true);
                                  /*  if(moveFolder_vault2.size()!=0)*/
                                    {
                                        StringBuffer buffer = new StringBuffer();
                                        for (int k = 0; k < mhelper.folder_path.size(); k++) {
                                            buffer.append(mhelper.folder_path.get(k) + "/");
                                        }
                                        Log.v("buffer", buffer.toString());
              /*  show_dialog(buffer.toString());*/
                                        String path_finder = first_timefolderclicked + "/" + buffer;
                                        File imageFile = new File(path);
                                        String[] make_path = path_finder.toString().split("/");
                                        StringBuffer path_buffer = new StringBuffer();
                                        for (int i = 0; i < make_path.length; i++) {
                                            if (i == make_path.length - 1) {
                                                path_buffer.append(make_path[i]);
                                            } else {
                                                path_buffer.append(make_path[i] + "/");
                                            }
                                        }
                                        Log.v("buffer_upload", path_buffer.toString());
                                        folder_adapter = new Folder_adapter(Filevault2.this, moveFolder_vault2, patientId, path_buffer.toString());
                                        folder_list.setAdapter(folder_adapter);
                                    }
                                    folder_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                            if (!thumbImage.get(position).get("Personal3").contains(".PNG") && !thumbImage.get(position).get("Personal3").contains(".png") &&
                                                    !thumbImage.get(position).get("Personal3").contains(".jpg") && !thumbImage.get(position).get("Personal3").contains(".JPG")
                                                    && !thumbImage.get(position).get("Personal3").contains(".pdf")
                                                    && !thumbImage.get(position).get("Personal3").contains(".xls") && !thumbImage.get(position).get("Personal3").contains(".doc")) {
                                                Folder_Clicked_folder = moveFolder_vault2.get(position).get("folder_name");
                                                HashKey = moveFolder_vault2.get(position).get("hash_keyvalue");
                                                folder_vault2_path.clear();
                                                path_back_nav = choose_navfolder();
                                          /*  if (path_back_nav.length == 2) {
                                                String root_add = folder_root.getText().toString().trim();
                                                folder_vault2_path.add(root_add);
                                            }*/
                                                folder_vault2_path.add(Folder_Clicked_folder);
                                                root_reached = 0;
                                                nextdialog();
                                                move_dialog.dismiss();
                                            }
                                        }
                                    });
                                    move_btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (folder_root.getText().toString().trim().equalsIgnoreCase(Folder_Clicked)) {
                                                Toast.makeText(Filevault2.this, "Please select a destination folder different from the current one.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    folder_root.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            path_back_nav = choose_navfolder();
                                            if (path_back_nav.length != 2) {
                                                Folder_Clicked_folder = path_back_nav[path_back_nav.length - 2];
                                            } else {
                                                Folder_Clicked_folder = folder_root.getText().toString().trim();
                                            }
                                            path_back_nav = new String[thumbImage.size()];
                                            if (moveFolder_vault2.size() == 0) {
                                                HashKey = S3Objects.get(0).get("hash_keyvalue");
                                            } else {
                                                HashKey = moveFolder_vault2.get(0).get("hash_keyvalue");
                                            }

                                            String[] split = HashKey.split("Personal");
                                            int haskeynumber = Integer.parseInt(split[split.length - 1]);
                                            int use_haskey = --haskeynumber;
                                            HashKey = "Personal" + String.valueOf(--use_haskey);
                                            if (HashKey.equalsIgnoreCase("Personal2") || HashKey.
                                                    equalsIgnoreCase("Personal1")) {
                                                HashKey = "Personal3";
                                            }
                                            //folder_vault2_path.add(Folder_Clicked);
                                            start_navigate = true;
                                            root_reached = 0;
                                            nextdialog();
                                            move_dialog.dismiss();
                                        }
                                    });
                                    move_dialog.show();
                                }
                            });
                            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(final DialogInterface arg0) {
                                    folder_vault2_path.clear();
                                }
                            });

                            dialog.show();
                        } else {
                            Toast.makeText(Filevault2.this, "Please select file(s).", Toast.LENGTH_SHORT).show();
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

                        AlertDialog dialog = new AlertDialog.Builder(Filevault2.this).create();
                        dialog.setTitle("Move");
                        dialog.setMessage("Are you sure you want to move the selected file(s)or folder(s)?");

                        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                               /* StringBuffer buffer = new StringBuffer();
                                for (int k = 0; k < mhelper.folder_path.size(); k++) {
                                    buffer.append(mhelper.folder_path.get(k) + "/");
                                }
                                Log.v("buffer", buffer.toString());
                                String path_finder = first_timefolderclicked + "/" + buffer;
                                String[] make_path = path_finder.toString().split("/");
                                StringBuffer path_buffer = new StringBuffer();
                                for (int i = 0; i < make_path.length; i++) {
                                    if (i == make_path.length - 1) {
                                        path_buffer.append(make_path[i]);
                                    } else {
                                        path_buffer.append(make_path[i] + "/");
                                    }
                                }
                                Log.v("buffer_upload", path_buffer.toString());
                                vault_adapter = new Vault_adapter(Filevault2.this, thumbImage, false, patientId, path_buffer.toString());
                                vault_list.setAdapter(vault_adapter);
                                vault_list.setSelection(position_scroll);
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
                                moveFolder1_vault2 = foldername();

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
                                if (rem_dup_folder != null && moveFolder1_vault2.size() != 0) {
                                    for (int r = 0; r < moveFolder1_vault2.size(); r++) {
                                        for (int l = 0; l < rem_dup_folder.length; l++) {
                                            if (moveFolder1_vault2.size() != 0) {
                                                if (moveFolder1_vault2.get(r).get("folder_name").equalsIgnoreCase(rem_dup_folder[l])) {
                                                    moveFolder1_vault2.remove(r);
                                                }
                                            }
                                        }
                                    }
                                }
                                alias_thumbImage_folder.addAll(moveFolder1_vault2);
                               /* String[] stockArr = new String[moveFolder1_vault2.size()];
                                stockArr = moveFolder1_vault2.toArray(stockArr);
                                AlertDialog.Builder builder = new AlertDialog.Builder(Filevault2.this);
                                builder.setTitle("Make your selection");
                                builder.setItems(stockArr, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int item) {
                                        move_to_folder(moveFolder1_vault2.get(item).toString());
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();*/

                                final Dialog move_dialog = new Dialog(Filevault2.this);
                                move_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                //setting custom layout to dialog
                                move_dialog.setContentView(R.layout.move_folderlist);
                                ListView folder_list = (ListView) move_dialog.findViewById(R.id.folder_list);
                                Button move_btn = (Button) move_dialog.findViewById(R.id.move_btn);
                                final TextView folder_root = (TextView) move_dialog.findViewById(R.id.folder_root);
                                folder_root.setText(Folder_Clicked);
                                folder_root.setCompoundDrawablesWithIntrinsicBounds(R.drawable.fitr_arrow, 0, 0, 0);
                                folder_root.setEnabled(true);
                                if (moveFolder1_vault2.size() != 0) {
                                    StringBuffer buffer = new StringBuffer();
                                    for (int k = 0; k < folder_vault2_path.size(); k++) {
                                        buffer.append(folder_vault2_path.get(k) + "/");
                                    }
                                    Log.v("buffer", buffer.toString());
                                    String path_finder = first_timefolderclicked + "/" + buffer;
                                    String[] make_path = path_finder.toString().split("/");
                                    StringBuffer path_buffer = new StringBuffer();
                                    for (int i = 0; i < make_path.length; i++) {
                                        if (i == make_path.length - 1) {
                                            path_buffer.append(make_path[i]);
                                        } else {
                                            path_buffer.append(make_path[i] + "/");
                                        }
                                    }
                                    Log.v("buffer_upload", path_buffer.toString());
                                    folder_adapter = new Folder_adapter(Filevault2.this, moveFolder1_vault2, patientId, path_buffer.toString());
                                    folder_list.setAdapter(folder_adapter);
                                }
                                folder_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Folder_Clicked_folder = moveFolder1_vault2.get(position).get("folder_name");
                                        HashKey = moveFolder1_vault2.get(position).get("hash_keyvalue");
                                        folder_vault2_path.clear();
                                        folder_vault2_path.add(Folder_Clicked_folder);
                                        root_reached = 0;
                                        nextdialog();
                                        move_dialog.dismiss();
                                    }
                                });
                                move_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (folder_root.getText().toString().trim().equalsIgnoreCase("Root")) {
                                            Toast.makeText(Filevault2.this, "Unable to move folder on same path.Please Select a destination folder to move.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                folder_root.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        path_back_nav = choose_navfolder();
                                        if (path_back_nav.length != 2) {
                                            Folder_Clicked_folder = path_back_nav[path_back_nav.length - 2];
                                        } else {
                                            Folder_Clicked_folder = folder_root.getText().toString().trim();
                                        }
                                        path_back_nav = new String[thumbImage.size()];
                                        if (moveFolder1_vault2.size() == 0) {
                                            HashKey = S3Objects.get(0).get("hash_keyvalue");
                                        } else {
                                            HashKey = moveFolder1_vault2.get(0).get("hash_keyvalue");
                                        }
                                        String[] split = HashKey.split("Personal");
                                        int haskeynumber = Integer.parseInt(split[split.length - 1]);
                                        int use_haskey = --haskeynumber;
                                        HashKey = "Personal" + String.valueOf(--use_haskey);
                                        if (HashKey.equalsIgnoreCase("Personal2") || HashKey
                                                .equalsIgnoreCase("Personal1")) {
                                            HashKey = "Personal3";
                                        }
                                        //folder_vault2_path.add(Folder_Clicked);
                                        start_navigate = true;
                                        root_reached = 0;
                                        nextdialog();
                                        move_dialog.dismiss();
                                    }
                                });
                                move_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(final DialogInterface arg0) {
                                        folder_vault2_path.clear();
                                    }
                                });
                                move_dialog.show();
                            }
                        });
                        dialog.show();
                    } else {
                        Toast.makeText(Filevault2.this, "Please select file(s).", Toast.LENGTH_SHORT).show();
                    }
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

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
            holder.checkbox.setOnClickListener(new View.OnClickListener() {

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


            holder.folder_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Filevault2.this, Filevault2.class);
                    //intent.putExtra("Folder_Clicked", holder.folder_name.getText().toString().trim());
                    intent.putExtra("Folder_Clicked", holder.folder_name.getText().toString().trim());
                    String check = S3Objects.get(position).get("hash_keyvalue").trim();
                    intent.putExtra("hash_keyvalue", S3Objects.get(position).get("hash_keyvalue").trim());
                    intent.putExtra("view", "Gird");
                    mhelper.folder_path.add(thumbImage.get(position).get("Personal3").trim());
                    intent.putExtra("iterartor", path_iterator);
                    intent.putExtra("path", mhelper.folder_path);
                    intent.putExtra("first_timefolderclicked", first_timefolderclicked);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                   /* S3Objects = new ArrayList<HashMap<String, String>>();
                    S3Objects = new NavFolder( holder.folder_name.getText().toString().trim(), "FileVault3").onFolderClickListener();
                    Show_Data(S3Objects);*/
                   /* finish();*/

                }
            });

            holder.imageview.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    StringBuffer buffer = new StringBuffer();
                    for (int k = 0; k < mhelper.folder_path.size(); k++) {
                        buffer.append(mhelper.folder_path.get(k) + "/");
                    }
                    Log.v("buffer", buffer.toString());
                    String path_finder = first_timefolderclicked + "/" + buffer;
                    File imageFile = new File(path);
                    String[] make_path = path_finder.toString().split("/");
                    StringBuffer path_buffer = new StringBuffer();
                    for (int k = 0; k < make_path.length; k++) {
                        if (k == make_path.length - 1) {
                            path_buffer.append(make_path[k]);
                        } else {
                            path_buffer.append(make_path[k] + "/");
                        }
                    }
                    Log.v("buffer_upload", path_buffer.toString());

                    if (thumbImage.get(position).get("Personal3").contains(".pdf")) {
                        Intent i = new Intent(Filevault2.this, PdfReader.class);
                        if (thumbImage.get(position).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                            i.putExtra("image_url", "https://files.healthscion.com/" + thumbImage.get(position).get("Personal3"));
                        } else if (thumbImage.get(position).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                            i.putExtra("image_url", "https://files.healthscion.com/" + path_buffer.toString().replaceAll(" ", "%20") + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20"));
                        } else {
                            i.putExtra("image_url", "https://files.healthscion.com/" + patientId + "/FileVault/Personal/" + path_buffer.toString().replaceAll(" ", "%20").trim() + "/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20"));
                        }
                        String pdf_name = thumbImage.get(position).get("Personal3").replace(patientId + "/FileVault/Personal/", "");
                        i.putExtra("imagename", pdf_name/* thumbImage.get(position)*/);
                        startActivity(i);
                        try {
                            // Toast.makeText(getBaseContext(), "Opening PDF... ", Toast.LENGTH_SHORT).show();
                            Intent inte = new Intent(Intent.ACTION_VIEW);
                            inte.setDataAndType(
                                    Uri.parse("https://files.healthscion.com/" + patientId + "/FileVault/Personal/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20")),
                                    "application/pdf");

                            startActivity(inte);
                        } catch (ActivityNotFoundException e) {
                            // Log.e("Viewer not installed on your device.", e.getMessage());
                        }

                    } else if (thumbImage.get(position).get("Personal3").contains(".doc") || thumbImage.get(position).get("Personal3").contains(".docx")) {
                        Intent i = new Intent(Filevault2.this, PdfReader.class);
                        if (thumbImage.get(position).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                            i.putExtra("image_url", "https://files.healthscion.com/" + thumbImage.get(position).get("Personal3"));
                        } else if (thumbImage.get(position).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                            i.putExtra("image_url", "https://files.healthscion.com/" + path_buffer.toString().replaceAll(" ", "%20") + thumbImage.get(position).get("Personal3"));
                        } else {
                            i.putExtra("image_url", "https://files.healthscion.com/" + patientId + "/FileVault/Personal/" + path_buffer.toString().replaceAll(" ", "%20").trim() + "/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20"));
                        }
                        String pdf_name = thumbImage.get(position).get("Personal3").replace(patientId + "/FileVault/Personal/", "");
                        i.putExtra("imagename", pdf_name/* thumbImage.get(position)*/);
                        startActivity(i);
                        try {
                            // Toast.makeText(getBaseContext(), "Opening DOC... ", Toast.LENGTH_SHORT).show();
                            Intent inte = new Intent(Intent.ACTION_VIEW);
                            inte.setDataAndType(
                                    Uri.parse("https://files.healthscion.com/" + patientId + "/FileVault/Personal/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20")),
                                    "application/pdf");

                            startActivity(inte);
                        } catch (ActivityNotFoundException e) {
                            // Log.e("Viewer not installed on your device.", e.getMessage());
                        }
                    } else if (thumbImage.get(position).get("Personal3").contains(".xls") || thumbImage.get(position).get("Personal3").contains(".xlsx")) {
                        Intent i = new Intent(Filevault2.this, PdfReader.class);
                        if (thumbImage.get(position).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                            i.putExtra("image_url", "https://files.healthscion.com/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20"));
                        } else if (thumbImage.get(position).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                            i.putExtra("image_url", "https://files.healthscion.com/" + path_buffer.toString().replaceAll(" ", "%20") + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20"));
                        } else {
                            i.putExtra("image_url", "https://files.healthscion.com/" + patientId + "/FileVault/Personal/" + path_buffer.toString().replaceAll(" ", "%20").trim() + "/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20"));
                        }
                        String pdf_name = thumbImage.get(position).get("Personal3").replace(patientId + "/FileVault/Personal/", "");
                        i.putExtra("imagename", pdf_name/* thumbImage.get(position)*/);
                        startActivity(i);
                        try {
                            // Toast.makeText(getBaseContext(), "Opening xsl... ", Toast.LENGTH_SHORT).show();
                            Intent inte = new Intent(Intent.ACTION_VIEW);
                            inte.setDataAndType(
                                    Uri.parse("https://files.healthscion.com/" + patientId + "/FileVault/Personal/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20")),
                                    "application/pdf");

                            startActivity(inte);
                        } catch (ActivityNotFoundException e) {
                            // Log.e("Viewer not installed on your device.", e.getMessage());
                        }
                    } else if (thumbImage.get(position).get("Personal3").contains(".txt")) {
                        Intent i = new Intent(Filevault2.this, PdfReader.class);
                        if (thumbImage.get(position).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                            i.putExtra("image_url", "https://files.healthscion.com/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20"));
                        } else if (thumbImage.get(position).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                            i.putExtra("image_url", "https://files.healthscion.com/" + path_buffer.toString().replaceAll(" ", "%20") + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20"));
                        } else {
                            i.putExtra("image_url", "https://files.healthscion.com/" + patientId + "/FileVault/Personal/" + path_buffer.toString().replaceAll(" ", "%20").trim() + "/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20"));
                        }
                        String pdf_name = thumbImage.get(position).get("Personal3").replace(patientId + "/FileVault/Personal/", "");
                        i.putExtra("imagename", pdf_name/* thumbImage.get(position)*/);
                        startActivity(i);
                        try {
                            // Toast.makeText(getBaseContext(), "Opening xsl... ", Toast.LENGTH_SHORT).show();
                            Intent inte = new Intent(Intent.ACTION_VIEW);
                            inte.setDataAndType(
                                    Uri.parse("https://files.healthscion.com/" + patientId + "/FileVault/Personal/" + thumbImage.get(position).get("Personal3").replaceAll(" ", "%20")),
                                    "application/pdf");

                            startActivity(inte);
                        } catch (ActivityNotFoundException e) {
                            // Log.e("Viewer not installed on your device.", e.getMessage());
                        }
                    } else {


                        Intent i = new Intent(Filevault2.this, ExpandImage.class);
                        String removeonejpg = thumbImage.get(position).get("Personal3");
                        if (thumbImage.get(position).get("Personal3").endsWith(".jpg")) {

                            // removeonejpg = thumbImage.get(position).substring(0, thumbImage.get(position).length() - 4);

                        }

                        if (removeonejpg != null) {
                            String image_url/* = thumbImage.get(position).get("Personal3").replace("_thumb", "")*/;
                            String image_name = thumbImage.get(position).get("Personal3").replace(patientId + "/FileVault/Personal/", "");
                           /* if (image_url.contains("/FileVault/")) {
                                i.putExtra("image", "https://files.healthscion.com/" + image_url);
                            } else {
                                i.putExtra("image", "https://files.healthscion.com/" + patientId + "/FileVault/" + image_url);
                            }*/
                            if (thumbImage.get(position).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                                image_url = thumbImage.get(position).get("Personal3").replace("_thumb", "");
                            } else if (thumbImage.get(position).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                image_url = path_buffer.toString() + "/" + thumbImage.get(position).get("Personal3").replace("_thumb", "");
                            } else {
                                image_url = patientId + "/FileVault/Personal/" + path_buffer.toString() + "/" + thumbImage.get(position).get("Personal3").replace("_thumb", "");
                            }
                            i.putExtra("image", "https://files.healthscion.com/" + image_url.replaceAll(" ", "%20"));
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

                mNetworkImageView.setImageUrl("https://files.healthscion.com/" + thumbImage.get(position), mImageLoader);
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
                        thumbImage.get(position).get("Personal3").equalsIgnoreCase("Bills")) {

                    holder.folder_name.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_folder_protected, 0, 0);
                } else {
                    holder.folder_name.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_folder, 0, 0);
                }
                holder.imageview.setVisibility(View.GONE);
                holder.folder_name.setText(thumbImage.get(position).get("Personal3"));
                /*}*/
            } else {
                StringBuffer buffer = new StringBuffer();
                for (int k = 0; k < mhelper.folder_path.size(); k++) {
                    buffer.append(mhelper.folder_path.get(k) + "/");
                }
                Log.v("buffer", buffer.toString());
                String path_finder = first_timefolderclicked + "/" + buffer;
                File imageFile = new File(path);
                String[] make_path = path_finder.toString().split("/");
                StringBuffer path_buffer = new StringBuffer();
                for (int k = 0; k < make_path.length; k++) {
                    if (k == make_path.length - 1) {
                        path_buffer.append(make_path[k]);
                    } else {
                        path_buffer.append(make_path[k] + "/");
                    }
                }
                Log.v("buffer_upload", path_buffer.toString());

                if (!thumbImage.get(position).get("Personal3").contains(".xls") && !thumbImage.get(position).get("Personal3").contains(".doc") &&
                        !thumbImage.get(position).get("Personal3").contains(".pdf") && !thumbImage.get(position).get("Personal3").contains(".txt")) {
                    String[] pdf_name = thumbImage.get(position).get("Personal3").split("/");
                    int length = pdf_name.length;
                    holder.folder_name.setVisibility(View.GONE);
                    holder.imageview.setVisibility(View.VISIBLE);
                    holder.image_name.setVisibility(View.VISIBLE);
                    holder.image_name.setText(pdf_name[length - 1].toString());
                    String thumbimg = "";
                    String imageurl;
                    if (thumbImage.get(position).get("Personal3").contains(".png")) {
                        thumbimg = thumbImage.get(position).get("Personal3").replaceAll("\\.png", "_thumb.png");
                    } else if (thumbImage.get(position).get("Personal3").contains(".PNG")) {
                        thumbimg = thumbImage.get(position).get("Personal3").replaceAll("\\.PNG", "_thumb.PNG");
                    } else if (thumbImage.get(position).get("Personal3").contains(".jpg")) {
                        thumbimg = thumbImage.get(position).get("Personal3").replaceAll("\\.jpg", "_thumb.jpg");
                    } else if (thumbImage.get(position).get("Personal3").contains(".JPG")) {
                        thumbimg = thumbImage.get(position).get("Personal3").replaceAll("\\.JPG", "_thumb.JPG");
                    }
                    if (thumbImage.get(position).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer)) {
                        // imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                        imageurl = thumbimg;
                    } else if (thumbImage.get(position).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                        //imageobject.put("Key", path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                        imageurl = path_buffer.toString() + "/" + thumbimg;
                    } else {
                        //imageobject.put("Key", patientId + "/FileVault/" + path_buffer.toString() + "/" + thumbImage.get(i).get("Personal3"));
                        imageurl = patientId + "/FileVault/Personal/" + path_buffer.toString() + "/" + thumbimg;
                    }
                    mNetworkImageView.setDefaultImageResId(R.drawable.box);
                    mNetworkImageView.setErrorImageResId(R.drawable.ic_error);
                    mNetworkImageView.setAdjustViewBounds(true);
                    mNetworkImageView.setImageUrl("https://files.healthscion.com/" + imageurl.replaceAll(" ", "%20"), mImageLoader);

                   /* if (!thumbImage.get(position).get("Personal3").contains(patientId + "/FileVault/")) {
                        mNetworkImageView.setImageUrl("https://files.healthscion.com/" + patientId + "/FileVault/" +path_buffer.toString()+"/"+ imageurl, mImageLoader);
                    } else {
                        mNetworkImageView.setImageUrl("https://files.healthscion.com/" +path_buffer.toString()+"/"+ thumbimg, mImageLoader);
                    }*/
                } else if (thumbImage.get(position).get("Personal3").contains(".pdf") &&
                        !thumbImage.get(position).get("Personal3").contains(".xls") && !thumbImage.get(position).get("Personal3").contains(".doc")
                        && !thumbImage.get(position).get("Personal3").contains(".txt")) {
                    String[] pdf_name = thumbImage.get(position).get("Personal3").split("/");
                    int length = pdf_name.length;
                    holder.image_name.setVisibility(View.VISIBLE);
                    holder.image_name.setText(pdf_name[length - 1].toString());
                    holder.folder_name.setVisibility(View.GONE);
                    holder.imageview.setVisibility(View.VISIBLE);
                  /*  mNetworkImageView = (NetworkImageView) holder.imageview;*/
                    mNetworkImageView.setDefaultImageResId(R.drawable.pdfimg);
                    mNetworkImageView.setAdjustViewBounds(true);
                    mNetworkImageView.setImageUrl(null, null);
                } else if ((thumbImage.get(position).get("Personal3").contains(".xls") || thumbImage.get(position).get("Personal3").contains(".xlsx")) &&
                        !thumbImage.get(position).get("Personal3").contains(".pdf") && !thumbImage.get(position).get("Personal3").contains(".doc")
                        && !thumbImage.get(position).get("Personal3").contains(".txt")) {
                    String[] pdf_name = thumbImage.get(position).get("Personal3").split("/");
                    int length = pdf_name.length;
                    holder.image_name.setVisibility(View.VISIBLE);
                    holder.image_name.setText(pdf_name[length - 1].toString());
                    holder.folder_name.setVisibility(View.GONE);
                    holder.imageview.setVisibility(View.VISIBLE);
                   /* mNetworkImageView = (NetworkImageView) holder.imageview;*/
                    mNetworkImageView.setDefaultImageResId(R.drawable.ic_excel);
                    mNetworkImageView.setAdjustViewBounds(true);
                    mNetworkImageView.setImageUrl(null, null);
                } else if ((thumbImage.get(position).get("Personal3").contains(".doc") || thumbImage.get(position).get("Personal3").contains(".docx")) &&
                        !thumbImage.get(position).get("Personal3").contains(".xls") && !thumbImage.get(position).get("Personal3").contains(".pdf")
                        && !thumbImage.get(position).get("Personal3").contains(".txt")) {
                    String[] pdf_name = thumbImage.get(position).get("Personal3").split("/");
                    int length = pdf_name.length;
                    holder.image_name.setVisibility(View.VISIBLE);
                    holder.image_name.setText(pdf_name[length - 1].toString());
                    holder.folder_name.setVisibility(View.GONE);
                    holder.imageview.setVisibility(View.VISIBLE);
                 /*   mNetworkImageView = (NetworkImageView) holder.imageview;*/
                    mNetworkImageView.setDefaultImageResId(R.drawable.ic_doc);
                    mNetworkImageView.setAdjustViewBounds(true);
                    mNetworkImageView.setImageUrl(null, null);
                } else if ((thumbImage.get(position).get("Personal3").contains(".txt")) &&
                        !thumbImage.get(position).get("Personal3").contains(".xls") &&
                        !thumbImage.get(position).get("Personal3").contains(".pdf") && !thumbImage.get(position).get("Personal3").contains(".doc")) {
                    String[] pdf_name = thumbImage.get(position).get("Personal3").split("/");
                    int length = pdf_name.length;
                    holder.image_name.setVisibility(View.VISIBLE);
                    holder.image_name.setText(pdf_name[length - 1].toString());
                    holder.folder_name.setVisibility(View.GONE);
                    holder.imageview.setVisibility(View.VISIBLE);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            if (requestCode == PICK_FROM_GALLERY) {

                Uri selectedImageUri = data.getData();

                String path = getPathFromContentUri(selectedImageUri);
                System.out.println(path);
                StringBuffer buffer = new StringBuffer();
                for (int k = 0; k < mhelper.folder_path.size(); k++) {
                    buffer.append(mhelper.folder_path.get(k) + "/");
                }
                Log.v("buffer", buffer.toString());
              /*  show_dialog(buffer.toString());*/
                String path_finder = first_timefolderclicked + "/" + buffer;
                File imageFile = new File(path);
                String[] make_path = path_finder.toString().split("/");
                StringBuffer path_buffer = new StringBuffer();
                for (int i = 0; i < make_path.length; i++) {
                    if (i == make_path.length - 1) {
                        path_buffer.append(make_path[i]);
                    } else {
                        path_buffer.append(make_path[i] + "/");
                    }
                }
                Log.v("buffer_upload", path_buffer.toString());
                long check = ((imageFile.length() / 1024));
                String splitfo_lenthcheck[] = path.split("/");
                int filenamelength = splitfo_lenthcheck[splitfo_lenthcheck.length - 1].length();
                if (check < 10000 && filenamelength < 99) {

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
                    intent.putExtra("add_path", path_buffer.toString());
                    intent.putExtra("exhistimg", exhistimg);
                    intent.putExtra("stringcheck", stringcheck);
                    startService(intent);

                    System.out.println("After Service");

                    String tempPath = getPath(selectedImageUri, Filevault2.this);
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
                        //vault_adapter.notifyDataSetChanged();
                       /* handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(Filevault2.this, Filevault2.class);
                                i.putExtra("Folder_Clicked", refresh_folderclicked);
                                i.putExtra("hash_keyvalue", refresh_hash_keyvalue);
                                i.putExtra("first_timefolderclicked", refresh_first_timefolderclicked);
                    *//*folder_path[path_iterator]=thumbImage.get(position).get("Personal3").trim();
                    path_iterator++;*//*
                                // folder_path.add(thumbImage.get(position).get("Personal3").trim());
                                i.putExtra("iterartor", refresh_path_iterator);
                                // refresh_path_iterator = path_iterator;
                                i.putExtra("path", refresh_folder_path);
                                // refresh_folder_path = folder_path;
                                i.putExtra("view", "List");
                                startActivity(i);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            }
                        }, 10000);*/
                    }

                } else {

                    Toast.makeText(this, "Image should be less than 10 mb.", Toast.LENGTH_LONG).show();

                }

            }

            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_FROM_CAMERA) {

                File imageFile = null ;
                Uri selectedImageUri;

                if(mIsSdkLessThanM == true){
                    selectedImageUri = Imguri;
                    imageFile = new File(selectedImageUri.getPath());
                    Log.e("Rishabh ", "PICKED FROM CAMERA onActivityResult (LOW SDK) ");
                    Log.e("Rishabh ", "onActivityResult (Camera) : imageFile :=  "+imageFile);
                    Log.e("Rishabh ", "onActivityResult (Camera) : imageFile Path :=  "+imageFile.getPath());

                }else {
                    Uri imageUri = Uri.parse(mCurrentPhotoPath);
                    selectedImageUri = imageUri;
                    imageFile = new File(imageUri.getPath());
                    Log.e("Rishabh ", "PICKED FROM CAMERA onActivityResult (M or N) ");
                    Log.e("Rishabh ", "onActivityResult (Camera) : imageFile :=  "+imageFile);
                    Log.e("Rishabh ", "onActivityResult (Camera) : imageFile Path :=  "+imageFile.getPath());
                }

                String path = getPathFromContentUri(selectedImageUri);
                System.out.println(path);

                long check = ((imageFile.length() / 1024));

                if (check < 10000) {
                    StringBuffer buffer = new StringBuffer();
                    for (int k = 0; k < mhelper.folder_path.size(); k++) {
                        buffer.append(mhelper.folder_path.get(k) + "/");
                    }
                    Log.v("buffer", buffer.toString());
              /*  show_dialog(buffer.toString());*/
                    String path_finder = first_timefolderclicked + "/" + buffer;

                    String[] make_path = path_finder.toString().split("/");
                    StringBuffer path_buffer = new StringBuffer();
                    for (int i = 0; i < make_path.length; i++) {
                        if (i == make_path.length - 1) {
                            path_buffer.append(make_path[i]);
                        } else {
                            path_buffer.append(make_path[i] + "/");
                        }
                    }

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
                    if (check != 0) {

                        preventRotation(path);
                        Intent intent = new Intent(this, UploadService.class);
                        intent.putExtra(UploadService.ARG_FILE_PATH, path);
                        intent.putExtra("add_path", path_buffer.toString());
                        intent.putExtra(UploadService.uploadfrom, "filevault2");
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
                    }
                } else {
                    Toast.makeText(this, "Image should be less than 10 mb.", Toast.LENGTH_LONG).show();
                }


            }

        } catch (Exception e) {

        }

    }

    private String getPathFromContentUri(Uri uri) {
        String path = uri.getPath();
        if (uri.toString().startsWith("content://")) {
            String[] projection = {MediaStore.MediaColumns.DATA};
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

    public String getPath(Uri uri, Activity activity) {

        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
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
            bar.setIndeterminate(percent < 0);
            bar.setProgress(percent);
        }
    };

    private void chooseimage() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Filevault2.this);
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
                                try {
                                    checkCameraPermission();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                               /* // Intent takePictureIntent = new
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
                                    photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "test1.jpg");

                                    boolean b = photo.delete();
                                    photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "test1.jpg");
                                } else {
                                    photo = new File(getCacheDir(), "test.jpg");
                                }
                                if (photo != null) {
                                    intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                                    Imguri = Uri.fromFile(photo);
                                    startActivityForResult(intent1, PICK_FROM_CAMERA);
                                }*/

                                break;

                            default:
                                break;
                        }
                    }
                });
        builder.show();
    }

    public void onBackPressed() {
      /*  mhelper.folder_path.addAll(folder_path);*/
      /*  if (folder_path.size() != 0) {
            folder_path.remove(folder_path.size() - 1);
        }*/
        if (!toggle_move) {
            thumbImage.clear();
            vault_list.setAdapter(null);
            if (mhelper.folder_path.size() != 0) {
                mhelper.folder_path.remove(mhelper.folder_path.size() - 1);
            }
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else if (!view_list) {
            list_header.setVisibility(View.VISIBLE);
            list_header2.setVisibility(View.GONE);
            vault_adapter = new Vault_adapter(Filevault2.this, thumbImage, false, patientId, "");
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
            if (mhelper.folder_path.size() != 0) {
                mhelper.folder_path.remove(mhelper.folder_path.size() - 1);
            }
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }

    }

    public void startBackgroundprocess() {
        S3Objects = new ArrayList<HashMap<String, String>>();
        S3Objects.clear();
        S3Objects = new NavFolder(Folder_Clicked, getIntent().getStringExtra("hash_keyvalue")).onFolderClickListener();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (S3Objects.size() == 0) {
                    startBackgroundprocess();
                } else {
                    Show_Data(S3Objects);
                }
            }
        }, 100);
    }

    public void Show_Data(ArrayList<HashMap<String, String>> list) {
        thumbImage.clear();
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
                thumbImage.add(hmap1);
            }

        }
        StringBuffer buffer = new StringBuffer();
        for (int k = 0; k < mhelper.folder_path.size(); k++) {
            buffer.append(mhelper.folder_path.get(k) + "/");
        }
        Log.v("buffer", buffer.toString());
              /*  show_dialog(buffer.toString());*/
        String path_finder = first_timefolderclicked + "/" + buffer;
        File imageFile = new File(path);
        String[] make_path = path_finder.toString().split("/");
        StringBuffer path_buffer = new StringBuffer();
        for (int i = 0; i < make_path.length; i++) {
            if (i == make_path.length - 1) {
                path_buffer.append(make_path[i]);
            } else {
                path_buffer.append(make_path[i] + "/");
            }
        }
        Log.v("buffer_upload", path_buffer.toString());
        new Helper().sortHashList(thumbImage, "Personal3");
        path_indicator.setText("/" + path_buffer.toString());
        thumbnailsselection = new boolean[thumbImage.size()];
        imageAdapter = new ImageAdapter();
        gridView.setAdapter(imageAdapter);
        vault_adapter = new Vault_adapter(Filevault2.this, thumbImage, false, patientId, path_buffer.toString());
        vault_list.setAdapter(vault_adapter);
        list_header.setVisibility(View.VISIBLE);
        list_header2.setVisibility(View.GONE);
        if (view_show.equalsIgnoreCase("List")) {
            vault_list.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
            warning_msg.setVisibility(View.GONE);
            view_list = false;
            refresh_view_show = false;
        } else {
            gridView.setVisibility(View.VISIBLE);
            vault_list.setVisibility(View.GONE);
            warning_msg.setVisibility(View.GONE);
            list_header.setVisibility(View.GONE);
            list_header2.setVisibility(View.GONE);
            view_list = true;
            refresh_view_show = true;
        }
        if (thumbImage.size() == 0) {
            vault_list.setVisibility(View.GONE);
            gridView.setVisibility(View.GONE);
            warning_msg.setVisibility(View.VISIBLE);
            menu_toggle.findItem(R.id.action_listview).setVisible(true);
            menu_toggle.findItem(R.id.select_all).setVisible(true);
            menu_toggle.findItem(R.id.action_move).setVisible(false);
            menu_toggle.findItem(R.id.action_delete).setVisible(false);
            menu_toggle.findItem(R.id.save).setVisible(false);
            menu_toggle.findItem(R.id.action_home).setVisible(true);
        }
        if (thumbImage.size() != 0) {
            menu_toggle.findItem(R.id.action_listview).setVisible(true);
            menu_toggle.findItem(R.id.select_all).setVisible(true);
            menu_toggle.findItem(R.id.action_move).setVisible(false);
            menu_toggle.findItem(R.id.action_delete).setVisible(false);
            menu_toggle.findItem(R.id.save).setVisible(false);
            menu_toggle.findItem(R.id.action_move).setVisible(false);
            menu_toggle.findItem(R.id.action_home).setVisible(true);
            warning_msg.setVisibility(View.GONE);
        }


      /*  for (int s = 0; s < thumbImage.size(); s++) {
            if (!thumbImage.get(s).get("FileVault2").contains(".PNG") && !thumbImage.get(s).get("FileVault2").contains(".png") &&
                    !thumbImage.get(s).get("FileVault2").contains(".jpg") && !thumbImage.get(s).get("FileVault2").contains(".pdf")
                    && !thumbImage.get(s).get("FileVault2").contains(".xls") && !thumbImage.get(s).get("FileVault2").contains(".doc")) {
                folder_move.add(thumbImage.get(s).get("FileVault2"));
            }
        }*/

    }

    public void show_dialog(String buffer) {
        // final Dialog overlay_dialog = new Dialog(Pkg_TabActivity.this, R.style.DialogSlideAnim);
        final Dialog overlay_dialog = new Dialog(Filevault2.this);
        overlay_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);//SOFT_INPUT_STATE_ALWAYS_HIDDEN
        overlay_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // overlay_dialog.setCancelable(false);
        overlay_dialog.setCanceledOnTouchOutside(true);
        overlay_dialog.setContentView(R.layout.create_folderdialog);
        path_folder = buffer;
        Button btn_continue = (Button) overlay_dialog.findViewById(R.id.create_btn);
        final TextView path = (TextView) overlay_dialog.findViewById(R.id.path);
        if (path_folder.equalsIgnoreCase("")) {
            path.setText(first_timefolderclicked + "/");
        } else {
            path.setText(first_timefolderclicked + "/" + path_folder);
        }
        final EditText folder_name = (EditText) overlay_dialog.findViewById(R.id.folder_name);
        //opening keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(folder_name.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        String[] make_path = path.getText().toString().split("/");
        path_buffer = new StringBuffer();
        for (int i = 0; i < make_path.length; i++) {
            if (i == make_path.length - 1) {
                path_buffer.append(make_path[i]);
            } else {
                path_buffer.append(make_path[i] + "/");
            }
        }
        Log.v("buffer_rrr", path_buffer.toString());
        folder_name.requestFocus();
        Button canceltxt = (Button) overlay_dialog.findViewById(R.id.cancel);
        canceltxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlay_dialog.dismiss();
            }
        });
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progress = new ProgressDialog(Filevault2.this);

                progress.setCancelable(false);
                //progress.setTitle("Logging in...");
                progress.setMessage("Please wait...");
                progress.setIndeterminate(true);

                String folder = folder_name.getText().toString();

                if (folder_name_exists(folder.trim())) {
                    folder_name.setError("A folder already exists with this name.");
                } else if (folder.trim().equalsIgnoreCase("Prescription")
                        || folder.trim().equalsIgnoreCase("Insurance") ||
                        folder.trim().equalsIgnoreCase("Bills")) {
                    folder_name.setError("A folder with this name can't be created.");
                } else if (folder != "" && (!folder.equals(""))) {
                    overlay_dialog.dismiss();
                    progress.show();
                    JSONObject sendData = new JSONObject();
                    try {
                        sendData.put("FolderName", folder);
                        sendData.put("Path", path_buffer.toString());
                        sendData.put("patientId", patientId);

                        Log.e("Rishabh", "Folder Name := "+folder.length());
                        Log.e("Rishabh", "Folder path := "+path_buffer.toString());
                        Log.e("Rishabh", "patient id := "+patientId);

                    } catch (JSONException EX) {
                        EX.printStackTrace();
                    }
                    StaticHolder sttc_holdr = new StaticHolder(Filevault2.this, StaticHolder.Services_static.CreateFolder);
                    String url = sttc_holdr.request_Url();
                    JsonObjectRequest jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            // System.out.println(response);

                            try {
                                String packagedata = response.getString("d");
                                Log.e("Rishabh", "response of create folder := "+packagedata );
                                if (packagedata.equalsIgnoreCase("Error")) {
                                    progress.dismiss();
                                    Toast.makeText(getApplicationContext(), "An error occurred while creating folder.", Toast.LENGTH_SHORT).show();
                                } else if (packagedata.equalsIgnoreCase("Folder exist")) {
                                    progress.dismiss();
                                    Toast.makeText(getApplicationContext(), "A folder already exists with this name.", Toast.LENGTH_SHORT).show();
                                } else if (packagedata.equalsIgnoreCase("Added")) {
                                    progress.dismiss();
                                    Toast.makeText(getApplicationContext(), "Folder created successfully.", Toast.LENGTH_LONG).show();
                                    RepositoryFragment.refresh();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                           /* Intent i = new Intent(Filevault2.this, Filevault2.class);
                                            i.putExtra("Folder_Clicked", refresh_folderclicked);
                                            i.putExtra("hash_keyvalue", refresh_hash_keyvalue);
                                            i.putExtra("first_timefolderclicked", refresh_first_timefolderclicked);

                                            i.putExtra("iterartor", refresh_path_iterator);

                                            i.putExtra("path", refresh_folder_path);

                                            if (!refresh_view_show) {
                                                i.putExtra("view", "List");

                                            } else {
                                                i.putExtra("view", "Gird");
                                            }

                                            startActivity(i);

                                            finish();*/
                                            Log.e("Rishabh", "start background process called from show dialog");
                                            startBackgroundprocess();

                                            /*S3Objects.clear();
                                            thumbImage.clear();*/

                                        }
                                    }, 1000);

                                   /* Intent i = getIntent();
                                    finish();
                                    startActivity(i);*/
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
                    folder_name.setError("Enter correct folder name.");
                    // Toast.makeText(getApplicationContext(), "Enter valid Mobile number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        overlay_dialog.show();
    }

    public void move_to_folder(final String newpath) {

        pd = new ProgressDialog(Filevault2.this);
        pd.setMessage("Moving .....");
        pd.show();
        toggle_move = false;
        String[] check_newpath = newpath.split("/");
        JSONArray array = new JSONArray();
        StringBuffer buffer_path = new StringBuffer();
        for (int k = 0; k < mhelper.folder_path.size(); k++) {
            buffer_path.append(mhelper.folder_path.get(k) + "/");
        }
        Log.v("buffer", buffer_path.toString());
        String path_finder = first_timefolderclicked + "/" + buffer_path;
        File imageFile = new File(path);
        String[] make_path = path_finder.toString().split("/");
        StringBuffer path_buffer_path = new StringBuffer();
        for (int k = 0; k < make_path.length; k++) {
            if (k == make_path.length - 1) {
                path_buffer_path.append(make_path[k]);
            } else {
                path_buffer_path.append(make_path[k] + "/");
            }
        }
        Log.v("buffer_upload", path_buffer_path.toString());

        for (int i = 0; i < thumbnailsselection.length; i++) {
            JSONObject imageobject = new JSONObject();
            if (thumbnailsselection[i]) {
                try {
                    //imageId.add(patientId+"/FileVault/"+thumbImage.get(i).get("folder_name"));
                    if (!thumbImage.get(i).get("Personal3").contains(".PNG") && !thumbImage.get(i).get("Personal3").contains(".png") &&
                            !thumbImage.get(i).get("Personal3").contains(".jpg") && !thumbImage.get(i).get("Personal3").contains(".pdf")
                            && !thumbImage.get(i).get("Personal3").contains(".xls") && !thumbImage.get(i).get("Personal3").contains(".doc")) {
                        if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer_path)) {
                            imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                        } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                            imageobject.put("Key", path_buffer_path.toString() + "/" + thumbImage.get(i).get("Personal3"));
                        } else {
                            imageobject.put("Key", patientId + "/FileVault/Personal/" + path_buffer_path.toString() + "/" + thumbImage.get(i).get("Personal3"));
                        }
                        imageobject.put("Type", "1");
                        imageobject.put("ThumbFile", "");
                        imageobject.put("Status", "");
                    } else {
                        if (thumbImage.get(i).get("Personal3").contains(".png")) {
                            String thumbimg = thumbImage.get(i).get("Personal3").replaceAll("\\.png", "_thumb.png");
                            if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer_path)) {
                                imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                            } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                imageobject.put("Key", path_buffer_path.toString() + "/" + thumbImage.get(i).get("Personal3"));
                            } else {
                                imageobject.put("Key", patientId + "/FileVault/Personal/" + path_buffer_path.toString() + "/" + thumbImage.get(i).get("Personal3"));
                            }
                            imageobject.put("Type", "0");
                            if (thumbimg.startsWith(patientId + "/FileVault/Personal/" + path_buffer_path)) {
                                imageobject.put("ThumbFile", thumbimg);
                            } else if (thumbimg.contains(patientId + "/FileVault/Personal/")) {
                                imageobject.put("ThumbFile", path_buffer_path.toString() + "/" + thumbimg);
                            } else {
                                imageobject.put("ThumbFile", patientId + "/FileVault/Personal/" + path_buffer_path.toString() + "/" + thumbimg);
                            }
                            imageobject.put("Status", "");
                        } else if (thumbImage.get(i).get("Personal3").contains(".PNG")) {
                            String thumbimg = thumbImage.get(i).get("Personal3").replaceAll("\\.PNG", "_thumb.PNG");
                            if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer_path)) {
                                imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                            } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                imageobject.put("Key", path_buffer_path.toString() + "/" + thumbImage.get(i).get("Personal3"));
                            } else {
                                imageobject.put("Key", patientId + "/FileVault/Personal/" + path_buffer_path.toString() + "/" + thumbImage.get(i).get("Personal3"));
                            }
                            imageobject.put("Type", "0");
                            if (thumbimg.startsWith(patientId + "/FileVault/Personal/" + path_buffer_path)) {
                                imageobject.put("ThumbFile", thumbimg);
                            } else if (thumbimg.contains(patientId + "/FileVault/Personal/")) {
                                imageobject.put("ThumbFile", path_buffer_path.toString() + "/" + thumbimg);
                            } else {
                                imageobject.put("ThumbFile", patientId + "/FileVault/Personal/" + path_buffer_path.toString() + "/" + thumbimg);
                            }
                            imageobject.put("Status", "");
                        } else if (thumbImage.get(i).get("Personal3").contains(".jpg")) {
                            String thumbimg = thumbImage.get(i).get("Personal3").replaceAll("\\.jpg", "_thumb.jpg");
                            if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer_path)) {
                                imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                            } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                imageobject.put("Key", path_buffer_path.toString() + "/" + thumbImage.get(i).get("Personal3"));
                            } else {
                                imageobject.put("Key", patientId + "/FileVault/Personal/" + path_buffer_path.toString() + "/" + thumbImage.get(i).get("Personal3"));
                            }
                            imageobject.put("Type", "0");
                            if (thumbimg.startsWith(patientId + "/FileVault/Personal/" + path_buffer_path)) {
                                imageobject.put("ThumbFile", thumbimg);
                            } else if (thumbimg.contains(patientId + "/FileVault/Personal/")) {
                                imageobject.put("ThumbFile", path_buffer_path.toString() + "/" + thumbimg);
                            } else {
                                imageobject.put("ThumbFile", patientId + "/FileVault/Personal/" + path_buffer_path.toString() + "/" + thumbimg);
                            }
                            imageobject.put("Status", "");
                        } else if (thumbImage.get(i).get("Personal3").contains(".JPG")) {
                            String thumbimg = thumbImage.get(i).get("Personal3").replaceAll("\\.JPG", "_thumb.JPG");
                            if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer_path)) {
                                imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                            } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                imageobject.put("Key", path_buffer_path.toString() + "/" + thumbImage.get(i).get("Personal3"));
                            } else {
                                imageobject.put("Key", patientId + "/FileVault/Personal/" + path_buffer_path.toString() + "/" + thumbImage.get(i).get("Personal3"));
                            }
                            imageobject.put("Type", "0");
                            if (thumbimg.startsWith(patientId + "/FileVault/Personal/" + path_buffer_path)) {
                                imageobject.put("ThumbFile", thumbimg);
                            } else if (thumbimg.contains(patientId + "/FileVault/Personal/")) {
                                imageobject.put("ThumbFile", path_buffer_path.toString() + "/" + thumbimg);
                            } else {
                                imageobject.put("ThumbFile", patientId + "/FileVault/Personal/" + path_buffer_path.toString() + "/" + thumbimg);
                            }
                            imageobject.put("Status", "");
                        } else {
                            if (thumbImage.get(i).get("Personal3").startsWith(patientId + "/FileVault/Personal/" + path_buffer_path)) {
                                imageobject.put("Key", thumbImage.get(i).get("Personal3"));
                            } else if (thumbImage.get(i).get("Personal3").contains(patientId + "/FileVault/Personal/")) {
                                imageobject.put("Key", path_buffer_path.toString() + "/" + thumbImage.get(i).get("Personal3"));
                            } else {
                                imageobject.put("Key", patientId + "/FileVault/Personal/" + path_buffer_path.toString() + "/" + thumbImage.get(i).get("Personal3"));
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

        queue2 = Volley.newRequestQueue(Filevault2.this);

        sendData = new JSONObject();
        try {
            sendData.put("ObjectList", array);
            sendData.put("UserId", patientId);
            if (newpath.equals("Root")) {
                sendData.put("NewPath", patientId + "/FileVault/Personal");
            } else if (newpath.equals("")) {
                sendData.put("NewPath", patientId + "/FileVault/Personal");
            } else {
                if (check_newpath.length >= 2 && root_reached != 1 && path_back_nav.length <= 2) {
                    sendData.put("NewPath", patientId + "/FileVault/Personal/" + path_buffer_path.toString() + "/" + newpath);
                } else if (mhelper.folder_path.size() == 0 && root_reached != 1) {
                    sendData.put("NewPath", patientId + "/FileVault/Personal/" + path_buffer_path.toString() + "/" + newpath);
                } else if (check_newpath.length == 1 && mhelper.folder_path.size() == 0 && root_reached == 1) {
                    sendData.put("NewPath", patientId + "/FileVault/Personal/"/*+ path_buffer_path.toString() + "/" */ + newpath);
                } else if (mhelper.folder_path.size() == 0 && root_reached == 1 && check_newpath.length != 1) {
                    sendData.put("NewPath", patientId + "/FileVault/Personal/"/*+ path_buffer_path.toString() + "/" */ + newpath);
                    /*sendData.put("NewPath", patientId + "/FileVault/Personal/"+ path_buffer_path.toString() + "/"  + newpath);*/
                } else {
                    sendData.put("NewPath", patientId + "/FileVault/Personal/"/*+ path_buffer_path.toString() + "/" */ + newpath);
                }
            }

            sendData.put("AbsolutePath", patientId + "/FileVault/Personal/" + path_buffer_path.toString() + "/");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //String url = Services.init + "PatientModule/PatientService.asmx/DeletePatientFiles";
        StaticHolder sttc_holdr = new StaticHolder(Filevault2.this, StaticHolder.Services_static.MoveObject);
        String url = sttc_holdr.request_Url();
        jr2 = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);

                try {
                    if (newpath.equals("") || newpath.equals("Root")) {
                        Toast.makeText(Filevault2.this, "File(s) successfully moved to:" + "Root.", Toast.LENGTH_LONG)
                                .show();
                    } else {
                        Toast.makeText(Filevault2.this, "File(s) successfully moved to:" + newpath + ".", Toast.LENGTH_LONG)
                                .show();
                    }
                    pd.dismiss();
                    //  S3Objects.clear();
                    RepositoryFragment.refresh();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                           /* Intent i = new Intent(Filevault2.this, Filevault2.class);
                            i.putExtra("Folder_Clicked", refresh_folderclicked);
                            i.putExtra("hash_keyvalue", refresh_hash_keyvalue);
                            i.putExtra("first_timefolderclicked", refresh_first_timefolderclicked);
                            i.putExtra("iterartor", refresh_path_iterator);
                            i.putExtra("path", refresh_folder_path);
                            if (!refresh_view_show) {
                                i.putExtra("view", "List");
                            } else {
                                i.putExtra("view", "Gird");
                            }
                            startActivity(i);
                            finish();*/
                            Log.e("Rishabh", "start background process called from move to folder");
                            startBackgroundprocess();

                        }
                    }, 1000);
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
                Toast.makeText(Filevault2.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue2.add(jr2);
    }


    private ArrayList<HashMap<String, String>> foldername() {
        ArrayList<HashMap<String, String>> folder_move = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> hmap;
        for (int s = 0; s < thumbImage.size(); s++) {
            hmap = new HashMap<String, String>();
           /* if (!thumbImage.get(s).get("Personal3").contains(".PNG") && !thumbImage.get(s).get("Personal3").contains(".png") &&
                    !thumbImage.get(s).get("Personal3").contains(".jpg")
                    && !thumbImage.get(s).get("Personal3").contains(".JPG") && !thumbImage.get(s).get("Personal3").contains(".pdf")
                    && !thumbImage.get(s).get("Personal3").contains(".xls") && !thumbImage.get(s).get("Personal3").contains(".doc"))*/
            {
                hmap.put("folder_name", thumbImage.get(s).get("Personal3"));
                hmap.put("hash_keyvalue", S3Objects.get(s).get("hash_keyvalue").trim());
                folder_move.add(hmap);
            }
        }
        return folder_move;
    }

    public static void refresh_filevault2() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                /*Intent i = new Intent(mContext, Filevault2.class);
                i.putExtra("Folder_Clicked", refresh_folderclicked);
                i.putExtra("hash_keyvalue", refresh_hash_keyvalue);
                i.putExtra("first_timefolderclicked", refresh_first_timefolderclicked);
                i.putExtra("iterartor", refresh_path_iterator);
                i.putExtra("path", static_refresh_folder_path);
                if (!refresh_view_show) {
                    i.putExtra("view", "List");
                } else {
                    i.putExtra("view", "Gird");
                }
                mContext.startActivity(i);
                ((Filevault2) mContext).finish();*/
                Log.e("Rishabh", "start background process called from refresh class");
                ((Filevault2) mContext).startBackgroundprocess();
            }
        }, 1000);

    }

    private ArrayList<HashMap<String, String>> folder1() {
        ArrayList<HashMap<String, String>> folder_move = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> hmap;
        for (int s = 0; s < thumbImage_folder.size(); s++) {
            hmap = new HashMap<String, String>();
          /*  if (!thumbImage_folder.get(s).get("Personal3").contains(".PNG") && !thumbImage_folder.get(s).get("Personal3").contains(".png") &&
                    !thumbImage_folder.get(s).get("Personal3").contains(".jpg")
                    && !thumbImage_folder.get(s).get("Personal3").contains(".JPG") && !thumbImage_folder.get(s).get("Personal3").contains(".pdf")
                    && !thumbImage_folder.get(s).get("Personal3").contains(".xls") && !thumbImage_folder.get(s).get("Personal3").contains(".doc")) */
            {
                hmap.put("folder_name", thumbImage_folder.get(s).get("Personal3"));
                try {
                    hmap.put("hash_keyvalue", S3Objects.get(s).get("hash_keyvalue").trim());
                } catch (Exception e) {
                    hmap.put("hash_keyvalue", S3Objects_folder.get(s).get("hash_keyvalue").trim());
                }
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
          /*  if (!thumbImage_folder.get(s).get("Personal3").contains(".PNG") && !thumbImage_folder.get(s).get("Personal3").contains(".png") &&
                    !thumbImage_folder.get(s).get("Personal3").contains(".jpg")
                    && !thumbImage_folder.get(s).get("Personal3").contains(".JPG") && !thumbImage_folder.get(s).get("Personal3").contains(".pdf")
                    && !thumbImage_folder.get(s).get("Personal3").contains(".xls") && !thumbImage_folder.get(s).get("Personal3").contains(".doc"))*/
            {
                hmap.put("folder_name", thumbImage_folder.get(s).get("Personal3"));
                try {
                    hmap.put("hash_keyvalue", S3Objects_folder.get(s).get("hash_keyvalue").trim());
                } catch (IndexOutOfBoundsException ie) {
                    ie.printStackTrace();
                    hmap.put("hash_keyvalue", S3Objects_folder.get(0).get("hash_keyvalue").trim());
                }
                folder_move.add(hmap);
            }
        }
        return folder_move;
    }

    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        *//*folder_path.clear();*//*
        folder_vault2_path.clear();
        mhelper.main_S3Objects.clear();
        finish();
    }*/

    public void nextdialog() {
        final Dialog move_dialog = new Dialog(Filevault2.this);
        move_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setting custom layout to dialog
        move_dialog.setContentView(R.layout.move_folderlist);
        final ListView folder_list = (ListView) move_dialog.findViewById(R.id.folder_list);
        Button move_btn = (Button) move_dialog.findViewById(R.id.move_btn);
        move_btn.setClickable(true);
        final TextView folder_root = (TextView) move_dialog.findViewById(R.id.folder_root);
        folder_root.setClickable(true);
        folder_root.setCompoundDrawablesWithIntrinsicBounds(R.drawable.fitr_arrow, 0, 0, 0);
        folder_root.setEnabled(true);
        final TextView empty_text = (TextView) move_dialog.findViewById(R.id.empty_text);
       /* folder_adapter = new Folder_adapter(Filevault.this,moveFolder1);
        folder_list.setAdapter(folder_adapter);*/
        move_dialog.show();
        path_back_nav = choose_navfolder();
        if (path_back_nav.length == 2 && folder_vault2_path.size() == 0) {
            folder_list.setVisibility(View.VISIBLE);
            empty_text.setVisibility(View.GONE);
            Folder_Clicked_folder = "Root";
            folder_root.setEnabled(false);
            if (mhelper.main_S3Objects.size() == 0) {
     //           ((RepositoryFragment) RepositoryFragment.file_vaultcontxt).foldername();     // TODO replace filevault class to repository fragment
            }
            S3Objects_folder = new ArrayList<HashMap<String, String>>();
            S3Objects_folder.addAll(mhelper.main_S3Objects);
            alias_thumbImage_folder.clear();
            alias_thumbImage_folder.addAll(mhelper.main_S3Objects);
            path_back_nav = new String[thumbImage.size()];
            if (mhelper.main_S3Objects.size() == 0) {
                folder_list.setVisibility(View.GONE);
                empty_text.setVisibility(View.VISIBLE);
            } else {
                StringBuffer buffer = new StringBuffer();
                for (int k = 0; k < mhelper.folder_path.size(); k++) {
                    buffer.append(mhelper.folder_path.get(k) + "/");
                }
                Log.v("buffer", buffer.toString());
                String path_finder = first_timefolderclicked + "/" + buffer;
                String[] make_path = path_finder.toString().split("/");
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
                folder_root.setText("Root");
                folder_root.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                folder_adapter = new Folder_adapter(Filevault2.this, mhelper.main_S3Objects, patientId, "");
                folder_list.setAdapter(folder_adapter);
                remove_duplicate_folder();
                root_reached = 1;
            }
        } else {
            S3Objects_folder = new ArrayList<HashMap<String, String>>();
            S3Objects_folder = new NavFolder(Folder_Clicked_folder, HashKey).onFolderClickListener();
            folder_vault2_path.clear();
            folder_vault2_path.add(Folder_Clicked_folder);
            folder_root.setCompoundDrawablesWithIntrinsicBounds(R.drawable.fitr_arrow, 0, 0, 0);
            folder_root.setEnabled(true);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (S3Objects_folder.size() == 0) {
                        // Toast.makeText(Filevault2.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                        move_dialog.dismiss();
                    } else {
                        thumbImage_folder.clear();
                        Show_Data1(S3Objects_folder);
                        dialog_folder = navigate_folder();
                        remove_duplicate_folder();
                        if (dialog_folder.size() == 0) {
                            folder_list.setVisibility(View.GONE);
                            empty_text.setVisibility(View.VISIBLE);
                        } else {
                            StringBuffer buffer = new StringBuffer();
                            for (int k = 0; k < folder_vault2_path.size(); k++) {
                                buffer.append(folder_vault2_path.get(k) + "/");
                            }
                            Log.v("buffer", buffer.toString());
                            String path_finder = first_timefolderclicked + "/" + buffer;
                            String[] make_path = path_finder.toString().split("/");
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
                            folder_adapter = new Folder_adapter(Filevault2.this, dialog_folder, patientId, path_buffer.toString());
                            folder_list.setAdapter(folder_adapter);
                        }

                    }
                }
            }, 100);
            folder_root.setText(Folder_Clicked_folder);
        }
        folder_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (S3Objects.size() == 0) {
                            // Toast.makeText(Filevault2.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                            move_dialog.dismiss();
                        } else {
                            //dialog_folder = folder1();
                            HashMap<String, Object> obj = (HashMap<String, Object>) folder_adapter.getItem(position);
                            String check_list = (String) obj.get("folder_name");
                            if (!check_list.contains(".PNG") && !check_list.contains(".png") &&
                                    !check_list.contains(".jpg") && !check_list.contains(".JPG")
                                    && !check_list.contains(".pdf")
                                    && !check_list.contains(".xls") && !check_list.contains(".doc")) {
                                if (folder_vault2_path.size() == 0) {
                                    Folder_Clicked_folder = alias_thumbImage_folder.get(position).get("folder_name");
                                } else {
                                    moveFolder_navigate = navigate_folder();
                                    remove_duplicate_folder_navigate();
                                    try {
                                        if (moveFolder_navigate.get(position).get("folder_name") != null) {
                                            Folder_Clicked_folder = moveFolder_navigate.get(position).get("folder_name");
                                        }
                                    } catch (IndexOutOfBoundsException ex) {
                                        ex.printStackTrace();
                                        folder_root.setText(Folder_Clicked_folder);
                                        folder_root.setCompoundDrawablesWithIntrinsicBounds(R.drawable.fitr_arrow, 0, 0, 0);
                                        folder_root.setEnabled(true);
                                        if (moveFolder_navigate.size() == 0) {
                                            folder_list.setVisibility(View.GONE);
                                            empty_text.setVisibility(View.VISIBLE);
                                        } else {
                                            StringBuffer buffer = new StringBuffer();
                                            for (int k = 0; k < folder_vault2_path.size(); k++) {
                                                buffer.append(folder_vault2_path.get(k) + "/");
                                            }
                                            Log.v("buffer", buffer.toString());
                                            String path_finder = first_timefolderclicked + "/" + buffer;
                                            String[] make_path = path_finder.toString().split("/");
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
                                            folder_adapter = new Folder_adapter(Filevault2.this, moveFolder_navigate, patientId, path_buffer.toString());
                                            folder_list.setAdapter(folder_adapter);
                                        }
                                        S3Objects_folder.clear();
                                        S3Objects_folder.addAll(moveFolder_navigate);
                                        return;
                                    }
                                }
                                if (folder_vault2_path.size() == 0) {
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
                                    if (HashKey.equalsIgnoreCase("Personal2") || HashKey
                                            .equalsIgnoreCase("Personal1")) {
                                        HashKey = "Personal3";
                                    }
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
                                S3Objects_folder = new NavFolder(Folder_Clicked_folder, HashKey).onFolderClickListener();
                                Show_Data1(S3Objects_folder);
                                if (!path_cleared && S3Objects_folder.size() == 0) {
                                    dialog_folder = folder1();
                                } else {
                                    dialog_folder = navigate_folder();
                                    remove_duplicate_folder();
                                }
                           /* alias_thumbImage_folder.clear();
                            alias_thumbImage_folder.addAll(dialog_folder);*/
                                folder_vault2_path.add(Folder_Clicked_folder);
                                folder_root.setText(Folder_Clicked_folder);
                                folder_root.setCompoundDrawablesWithIntrinsicBounds(R.drawable.fitr_arrow, 0, 0, 0);
                                folder_root.setEnabled(true);
                                if (dialog_folder.size() == 0) {
                                    folder_list.setVisibility(View.GONE);
                                    empty_text.setVisibility(View.VISIBLE);
                                } else {
                                    StringBuffer buffer = new StringBuffer();
                                    for (int k = 0; k < folder_vault2_path.size(); k++) {
                                        buffer.append(folder_vault2_path.get(k) + "/");
                                    }
                                    Log.v("buffer", buffer.toString());
                                    String path_finder = first_timefolderclicked + "/" + buffer;
                                    String[] make_path = path_finder.toString().split("/");
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
                                    folder_adapter = new Folder_adapter(Filevault2.this, dialog_folder, patientId, path_buffer.toString());
                                    folder_list.setAdapter(folder_adapter);
                                }
                            }
                        }
                    }
                }, 100);
            }
        });

        folder_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path_string = folder_root.getText().toString().trim();
                folder_root.setEnabled(true);
                if (folder_vault2_path.size() == 1 && !path_cleared) {
                    folder_list.setVisibility(View.VISIBLE);
                    empty_text.setVisibility(View.GONE);
                    if (start_navigate) {
                        path_back_nav = choose_navfolder();
                        if (path_back_nav.length != 1) {
                            HashKey = S3Objects_folder.get(0).get("hash_keyvalue");
                            String[] split = HashKey.split("Personal");
                            int haskeynumber = Integer.parseInt(split[split.length - 1]);
                            int use_haskey = --haskeynumber;
                            HashKey = "Personal" + String.valueOf(--use_haskey);
                            path_back_nav = ArrayUtils.remove(path_back_nav, path_back_nav.length - 1);
                            if (HashKey.equalsIgnoreCase("Personal2") || HashKey
                                    .equalsIgnoreCase("Personal1")) {
                                HashKey = "Personal3";
             //                   ((Filevault) RepositoryFragment.file_vaultcontxt).foldername();   // TODO replace filevault class to repository fragment
                                if (mhelper.main_S3Objects.size() == 0) {
                                    folder_list.setVisibility(View.GONE);
                                    empty_text.setVisibility(View.VISIBLE);
                                } else {
                                    folder_list.setVisibility(View.VISIBLE);
                                    empty_text.setVisibility(View.GONE);
                                    folder_root.setText("Root");
                                    folder_root.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                    folder_root.setEnabled(false);
                                    folder_adapter = new Folder_adapter(Filevault2.this, mhelper.main_S3Objects, patientId, "");
                                    folder_list.setAdapter(folder_adapter);
                                    alias_thumbImage_folder.clear();
                                    alias_thumbImage_folder.addAll(mhelper.main_S3Objects);
                                    remove_duplicate_folder();
                                    root_reached = 1;
                                }
                                path_back_nav = new String[thumbImage.size()];
                                folder_vault2_path.clear();
                                return;
                            }
                            path_back_nav = ArrayUtils.remove(path_back_nav, path_back_nav.length - 1);
                            int new_pathlen = path_back_nav.length;
                            Folder_Clicked_folder = path_back_nav[new_pathlen - 1];
                            if (path_back_nav.length != 1) {
                                folder_root.setText(path_back_nav[path_back_nav.length - 1]);
                            } else {
                                folder_root.setText(Folder_Clicked_folder);
                            }
                            thumbImage_folder.clear();
                            S3Objects_folder.clear();
                            S3Objects_folder = new NavFolder(Folder_Clicked_folder, HashKey).onFolderClickListener();
                            Show_Data1(S3Objects_folder);
                            if (path_back_nav.length != 1) {
                                folder_root.setText(path_back_nav[path_back_nav.length - 1]);
                                //start_navigate = false;
                            }
                            dialog_folder = navigate_folder();
                            remove_duplicate_folder();
                            if (dialog_folder.size() == 0) {
                                folder_list.setVisibility(View.GONE);
                                empty_text.setVisibility(View.VISIBLE);
                            } else {
                                StringBuffer buffer = new StringBuffer();
                                for (int k = 0; k < folder_vault2_path.size(); k++) {
                                    buffer.append(folder_vault2_path.get(k) + "/");
                                }
                                Log.v("buffer", buffer.toString());
                                String path_finder = first_timefolderclicked + "/" + buffer;
                                String[] make_path = path_finder.toString().split("/");
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
                                folder_root.setText(Folder_Clicked_folder);
                                folder_adapter = new Folder_adapter(Filevault2.this, dialog_folder, patientId, path_buffer.toString());
                                folder_list.setAdapter(folder_adapter);
                            }
                        } else {
                            start_navigate = false;
                        }
                        return;
                    } else if (moveFolder1_vault2.size() == 0) {
                        StringBuffer buffer = new StringBuffer();
                        for (int k = 0; k < folder_vault2_path.size(); k++) {
                            buffer.append(folder_vault2_path.get(k) + "/");
                        }
                        Log.v("buffer", buffer.toString());
                        String path_finder = first_timefolderclicked + "/" + buffer;
                        String[] make_path = path_finder.toString().split("/");
                        StringBuffer path_buffer = new StringBuffer();
                        for (int i = 0; i < make_path.length; i++) {
                            if (i == make_path.length - 1) {
                                path_buffer.append(make_path[i]);
                            } else {
                                path_buffer.append(make_path[i] + "/");
                            }
                        }
                        Log.v("buffer_upload", path_buffer.toString());
                        folder_adapter = new Folder_adapter(Filevault2.this, moveFolder_vault2, patientId, path_buffer.toString());
                        HashKey = moveFolder_vault2.get(0).get("hash_keyvalue");
                    } else {
                        StringBuffer buffer = new StringBuffer();
                        for (int k = 0; k < folder_vault2_path.size(); k++) {
                            buffer.append(folder_vault2_path.get(k) + "/");
                        }
                        Log.v("buffer", buffer.toString());
                        String path_finder = first_timefolderclicked + "/" + buffer;
                        String[] make_path = path_finder.toString().split("/");
                        StringBuffer path_buffer = new StringBuffer();
                        for (int i = 0; i < make_path.length; i++) {
                            if (i == make_path.length - 1) {
                                path_buffer.append(make_path[i]);
                            } else {
                                path_buffer.append(make_path[i] + "/");
                            }
                        }
                        Log.v("buffer_upload", path_buffer.toString());
                        folder_adapter = new Folder_adapter(Filevault2.this, moveFolder1_vault2, patientId, path_buffer.toString());
                        HashKey = moveFolder1_vault2.get(0).get("hash_keyvalue");
                    }
                    path_back_nav = choose_navfolder();
                    folder_list.setAdapter(folder_adapter);
                    folder_root.setText(path_back_nav[path_back_nav.length - 1]);
                    path_cleared = true;
                    path_back_nav = ArrayUtils.remove(path_back_nav, path_back_nav.length - 1);
                    folder_root.setCompoundDrawablesWithIntrinsicBounds(R.drawable.fitr_arrow, 0, 0, 0);
                    folder_root.setEnabled(true);
                } else if (path_back_nav.length == 1 && !(folder_vault2_path.size() > 1)) {
                    if (mhelper.main_S3Objects.size() == 0) {
   //                     ((Filevault) Filevault.file_vaultcontxt).foldername();  // TODO replace filevault class to repository fragment
                    }
                    if (mhelper.main_S3Objects.size() == 0) {
                        folder_list.setVisibility(View.GONE);
                        empty_text.setVisibility(View.VISIBLE);
                    } else {
                        folder_list.setVisibility(View.VISIBLE);
                        empty_text.setVisibility(View.GONE);
                        folder_root.setText("Root");
                        folder_root.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        folder_root.setEnabled(false);
                        folder_adapter = new Folder_adapter(Filevault2.this, mhelper.main_S3Objects, patientId, "");
                        folder_list.setAdapter(folder_adapter);
                        alias_thumbImage_folder.clear();
                        alias_thumbImage_folder.addAll(mhelper.main_S3Objects);
                        S3Objects_folder.clear();
                        S3Objects_folder.addAll(mhelper.main_S3Objects);
                        remove_duplicate_folder();
                        root_reached = 1;
                    }
                    folder_vault2_path.clear();
                } else if (folder_vault2_path.size() == 0 && path_cleared) {
                    String[] split = HashKey.split("Personal");
                    int new_pathlen = path_back_nav.length;
                    int haskeynumber = Integer.parseInt(split[split.length - 1]);
                    int use_haskey = --haskeynumber;
                    HashKey = "Personal" + String.valueOf(--use_haskey);
                    if (HashKey.equalsIgnoreCase("Personal2") || HashKey
                            .equalsIgnoreCase("Personal1")) {
                        HashKey = "Personal3";
                    }
                    Folder_Clicked_folder = path_back_nav[new_pathlen - 1];
                    path_back_nav = ArrayUtils.remove(path_back_nav, path_back_nav.length - 1);
                    thumbImage_folder.clear();
                    S3Objects_folder.clear();
                    S3Objects_folder = new NavFolder(Folder_Clicked_folder, HashKey).onFolderClickListener();
                    Show_Data1(S3Objects_folder);
                    if (path_back_nav.length != 1) {
                        folder_root.setText(path_back_nav[path_back_nav.length - 1]);
                    }
                    dialog_folder = navigate_folder();
                    remove_duplicate_folder();
                    if (dialog_folder.size() == 0) {
                        folder_list.setVisibility(View.GONE);
                        empty_text.setVisibility(View.VISIBLE);
                    } else {
                        StringBuffer buffer = new StringBuffer();
                        for (int k = 0; k < folder_vault2_path.size(); k++) {
                            buffer.append(folder_vault2_path.get(k) + "/");
                        }
                        Log.v("buffer", buffer.toString());
                        String path_finder = first_timefolderclicked + "/" + buffer;
                        String[] make_path = path_finder.toString().split("/");
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
                        folder_root.setText(Folder_Clicked_folder);
                        folder_adapter = new Folder_adapter(Filevault2.this, dialog_folder, patientId, path_buffer.toString());
                        folder_list.setAdapter(folder_adapter);
                    }

                } else {
                    int folderpath_size = folder_vault2_path.size();
                    if (S3Objects_folder.size() != 0) {
                        back_clicked_move = 0;
                    }
                    HashKey = S3Objects_folder.get(back_clicked_move).get("hash_keyvalue").trim();
                    String[] split = HashKey.split("Personal");
                    int haskeynumber = Integer.parseInt(split[split.length - 1]);
                    int use_haskey = --haskeynumber;
                    HashKey = "Personal" + String.valueOf(--use_haskey);
                    if (folderpath_size > 1) {
                        Folder_Clicked_folder = folder_vault2_path.get(folderpath_size - 2);
                    } else if (folderpath_size == 1) {
                        //Folder_Clicked_folder = folder_vault2_path.get(folderpath_size-1);
                        if (mhelper.main_S3Objects.size() == 0) {
       //                     ((Filevault) Filevault.file_vaultcontxt).foldername();  // // TODO replace filevault class to repository fragment
                        }
                        if (mhelper.main_S3Objects.size() == 0) {
                            folder_list.setVisibility(View.GONE);
                            empty_text.setVisibility(View.VISIBLE);
                        } else {
                            folder_list.setVisibility(View.VISIBLE);
                            empty_text.setVisibility(View.GONE);
                            folder_root.setText("Root");
                            folder_root.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            folder_root.setEnabled(false);
                            folder_adapter = new Folder_adapter(Filevault2.this, mhelper.main_S3Objects, patientId, "");
                            folder_list.setAdapter(folder_adapter);
                            alias_thumbImage_folder.clear();
                            alias_thumbImage_folder.addAll(mhelper.main_S3Objects);
                            remove_duplicate_folder();
                            root_reached = 1;
                        }
                        folder_vault2_path.clear();
                        return;
                    } else {
                        if (HashKey.equalsIgnoreCase("Personal2") || HashKey
                                .equalsIgnoreCase("Personal1")) {
                            HashKey = "Personal3";
                        }
                        int new_pathlen = path_back_nav.length;
                        Folder_Clicked_folder = path_back_nav[new_pathlen - 1];
                        path_back_nav = ArrayUtils.remove(path_back_nav, path_back_nav.length - 1);
                        thumbImage_folder.clear();
                        S3Objects_folder.clear();
                        S3Objects_folder = new NavFolder(Folder_Clicked_folder, HashKey).onFolderClickListener();
                        Show_Data1(S3Objects_folder);
                        if (path_back_nav.length != 1) {
                            folder_root.setText(path_back_nav[path_back_nav.length - 1]);
                        }
                        dialog_folder = navigate_folder();
                        remove_duplicate_folder();
                        if (dialog_folder.size() == 0) {
                            folder_list.setVisibility(View.GONE);
                            empty_text.setVisibility(View.VISIBLE);
                        } else {
                            StringBuffer buffer = new StringBuffer();
                            for (int k = 0; k < folder_vault2_path.size(); k++) {
                                buffer.append(folder_vault2_path.get(k) + "/");
                            }
                            Log.v("buffer", buffer.toString());
                            String path_finder = first_timefolderclicked + "/" + buffer;
                            String[] make_path = path_finder.toString().split("/");
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
                            folder_root.setText(Folder_Clicked_folder);
                            folder_adapter = new Folder_adapter(Filevault2.this, dialog_folder, patientId, path_buffer.toString());
                            folder_list.setAdapter(folder_adapter);
                        }
                        return;
                    }
                    thumbImage_folder.clear();
                    S3Objects_folder.clear();
                    S3Objects_folder = new NavFolder(Folder_Clicked_folder, HashKey).onFolderClickListener();
                    Show_Data1(S3Objects_folder);
                    dialog_folder = navigate_folder();
                    remove_duplicate_folder();
                    if (path_back_nav.length != 0) {
                        path_back_nav = ArrayUtils.remove(path_back_nav, path_back_nav.length - 1);
                    }
                    if (dialog_folder.size() == 0) {
                        folder_list.setVisibility(View.GONE);
                        empty_text.setVisibility(View.VISIBLE);
                    } else {
                        StringBuffer buffer = new StringBuffer();
                        for (int k = 0; k < folder_vault2_path.size(); k++) {
                            buffer.append(folder_vault2_path.get(k) + "/");
                        }
                        Log.v("buffer", buffer.toString());
                        String path_finder = first_timefolderclicked + "/" + buffer;
                        String[] make_path = path_finder.toString().split("/");
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
                        folder_root.setText(Folder_Clicked_folder);
                        folder_adapter = new Folder_adapter(Filevault2.this, dialog_folder, patientId, path_buffer.toString());
                        folder_list.setAdapter(folder_adapter);
                    }
                }
                if (folder_vault2_path.size() != 0) {
                    for (int path = 0; path < folder_vault2_path.size(); path++) {
                   /* if (path_string.equalsIgnoreCase(Folder_Clicked)) {*/
                        folder_vault2_path.remove(path_string);
                   /* }*/
                    }
                }
              /*  }*/
                Log.v("FOLDER_PATH", folder_vault2_path.toString());
                remove_duplicate_folder();

            }
        });

        move_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String check_root = path_indicator.getText().toString().trim();
                StringBuffer buffer = new StringBuffer();
                for (int k = 0; k < folder_vault2_path.size(); k++) {
                    buffer.append(folder_vault2_path.get(k) + "/");
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
                if (check_root.equalsIgnoreCase("/" + path_buffer.toString())) {
                    Toast.makeText(Filevault2.this, "Please select a destination folder different from the current one.", Toast.LENGTH_SHORT).show();
                } else {
                    if (folder_vault2_path.size() == 1) {
                        //move_to_folder(folder_path.get(0));
                        path_back_nav = choose_navfolder();
                        ArrayList<String> folder_vault_path = new ArrayList<String>();
                        String checking = folder_root.getText().toString();
                        if (!checking.equals("Root")) {
                            folder_vault_path.add(folder_root.getText().toString());

                            if (path_back_nav.length == 2) {

                            } else {
                                if (root_reached != 1) {
                                    if (folder_vault2_path.size() == 1 && path_back_nav.length <= 2) {
                                        folder_vault2_path.clear();
                                        for (int p = 1; p < path_back_nav.length; p++) {
                                            if (!path_back_nav[p].equals(folder_vault_path.get(0))) {
                                                folder_vault2_path.add(path_back_nav[p]);
                                            } else {
                                                folder_vault2_path.add(path_back_nav[p]);
                                                break;
                                            }
                                        }
                                        folder_vault2_path.add(checking);
                                    } else {
                                        folder_vault2_path.clear();
                                        for (int p = 1; p < path_back_nav.length - 1; p++) {
                                            if (!path_back_nav[p].equals(folder_vault_path.get(0))) {
                                                folder_vault2_path.add(path_back_nav[p]);
                                            } else {
                                                folder_vault2_path.add(path_back_nav[p]);
                                                break;
                                            }
                                        }
                                    }

                                }
                            }
                            StringBuffer bufferr1 = new StringBuffer();
                            for (int k = 0; k < folder_vault2_path.size(); k++) {
                                bufferr1.append(folder_vault2_path.get(k) + "/");
                            }
                            Log.v("buffer", bufferr1.toString());
                      /*  String path_finder = first_timefolderclicked + "/" + buffer;
                        File imageFile = new File(path);*/
                            String[] make_pathr1 = bufferr1.toString().split("/");
                            StringBuffer path_bufferr1 = new StringBuffer();
                            for (int k = 0; k < make_pathr1.length; k++) {
                                if (k == make_pathr1.length - 1) {
                                    path_bufferr1.append(make_pathr1[k]);
                                } else {
                                    path_bufferr1.append(make_pathr1[k] + "/");
                                }
                            }
                            Log.v("buffer_upload", path_bufferr1.toString());
                            move_to_folder(path_bufferr1.toString());
                            move_dialog.dismiss();
                            //folder_vault2_path.clear();
                        } else {
                            move_to_folder("Root");
                            move_dialog.dismiss();
                            folder_vault2_path.clear();
                        }
                    } else {
                        StringBuffer buffer1 = new StringBuffer();
                        for (int k = 0; k < folder_vault2_path.size(); k++) {
                            buffer1.append(folder_vault2_path.get(k) + "/");
                        }
                        Log.v("buffer", buffer1.toString());
                      /*  String path_finder = first_timefolderclicked + "/" + buffer;
                        File imageFile = new File(path);*/
                        String[] make_path1 = buffer1.toString().split("/");
                        StringBuffer path_buffer1 = new StringBuffer();
                        for (int k = 0; k < make_path1.length; k++) {
                            if (k == make_path1.length - 1) {
                                path_buffer1.append(make_path1[k]);
                            } else {
                                path_buffer1.append(make_path1[k] + "/");
                            }
                        }
                        Log.v("buffer_upload", path_buffer1.toString());
                        move_to_folder(path_buffer1.toString());
                        move_dialog.dismiss();
                        folder_vault2_path.clear();
                    }
                }

            }
        });
    }

    public void Show_Data1(ArrayList<HashMap<String, String>> list) {
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

    public String[] choose_navfolder() {
        String path = path_indicator.getText().toString();
        String[] split = path.split("/");
        return split;
    }

    private ArrayList<HashMap<String, String>> remove_duplicate_folder() {
        if (rem_dup_folder != null /*&& !rem_dup_folder.equalsIgnoreCase("")*/) {
            for (int r = 0; r < dialog_folder.size(); r++) {
                for (int l = 0; l < rem_dup_folder.length; l++) {
                    if (dialog_folder.get(r).get("folder_name").equals(rem_dup_folder[l])) {
                        dialog_folder.remove(r);
                    }
                }
            }
        }
        return dialog_folder;
    }

    private ArrayList<HashMap<String, String>> remove_duplicate_folder_navigate() {
        if (rem_dup_folder != null /*&& !rem_dup_folder.equalsIgnoreCase("")*/) {
            for (int r = 0; r < moveFolder_navigate.size(); r++) {
                for (int l = 0; l < rem_dup_folder.length; l++) {
                    if (moveFolder_navigate.get(r).get("folder_name").equals(rem_dup_folder[l])) {
                        moveFolder_navigate.remove(r);
                    }
                }
            }
        }
        return moveFolder_navigate;
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

    @Override
    protected void onResume() {
        if (Helper.authentication_flag == true) {
            finish();
        } else {
            Log.e("Rishabh", "Start Background Process called from onResume() ");
            startBackgroundprocess();
        }
        super.onResume();
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

    private void takePhoto() {
        mIsSdkLessThanM = true ;
        File photo = null;
        Intent intent1 = new Intent("android.media.action.IMAGE_CAPTURE");
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            photo = new File(Environment.getExternalStorageDirectory(), "test.jpg");
        } else {
            photo = new File(Filevault2.this.getCacheDir(), "test.jpg");
        }
        if (photo != null) {
            intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
            Imguri = Uri.fromFile(photo);
            startActivityForResult(intent1, PICK_FROM_CAMERA);
        }
    }

    void checkCameraPermission() throws IOException {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            startCamera() ;
        }else {
            takePhoto();
        }
    }
    /**
     * Method to check permission
     */
  /*  void checkCameraPermission() {
        boolean isGranted;
        if (ActivityCompat.checkSelfPermission(Filevault2.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.
            requestCameraPermission();
        } else {
            takePhoto();
        }
    }*/

    /**
     * Method to request permission for camera
     */
    private void requestCameraPermission() {
        // Camera permission has not been granted yet. Request it directly.
        ActivityCompat.requestPermissions(Filevault2.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /*if (requestCode == REQUEST_CAMERA) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                takePhoto();
            } else {
                //Permission not granted
                Toast.makeText(Filevault2.this, "You need to grant camera permission to use camera", Toast.LENGTH_LONG).show();
            }
        }*/
        if (requestCode == MY_PERMISSIONS_REQUEST) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = true ;
                Log.e("Rishabh", "Permissions are Granted .");
            } else {
                mPermissionGranted = false ;
                Log.e("Rishabh", "Permissions are not granted .");
            }
        }
    }

    void askRunTimePermissions() {

        int permissionCAMERA = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST);
        }

    }

    void startCamera() throws IOException {
        mIsSdkLessThanM = false ;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("Rishabh ", "IO Exception := " + ex);
                // Error occurred while creating the File
                return;
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(Filevault2.this, BuildConfig.APPLICATION_ID + ".provider", createImageFile());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, PICK_FROM_CAMERA);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
}
