package ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.applozic.audiovideo.activity.AudioCallActivityV2;
import com.applozic.audiovideo.activity.VideoActivity;
import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.hs.userportal.Directory;
import com.hs.userportal.R;
import com.hs.userportal.UploadService;
import com.hs.userportal.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import fragment.RepositoryFreshFragment;
import networkmngr.NetworkChangeListener;
import utils.RepositoryUtils;

/**
 * Created by ayaz on 6/6/17.
 */

public class SymptomsActivity extends BaseActivity {
    private Activity mActivity;
    String[] permissionsRequired = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private static final int MY_PERMISSIONS_REQUEST_READ_FILE = 5;
    private int PICK_FROM_GALLERY = 1;
    private static final int PICK_FROM_CAMERA = 2;
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;
    private String mCoversationType;
    private Uri selectedImageUri;
    private String selectedPath;
    private SymptomsDialog mSymptomsDialog;
    private TextView mSymptomsTextView;
    private String symptomsArry[] = {"Pain",
            "Anxiety",
            "Fatigue",
            "Headache",
            "Infection",
            "Depression",
            "Diabtees mellitus",
            "Shortnes of breath",
            "Skin Rash",
            "Swelling",
            "Stress",
            "Fever",
            "Weight Loss",
            "Common Cold",
            "Diarrhea",
            "Allergy",
            "Vomiting",
            "Dizziness",
            "Abdominal Pain",
            "Itch",
            "Joint pain",
            "Constipation",
            "Chest pain",
            "Weight Gain",
            "Muscle Pain",
            "Bleeding",
            "Ashtama",
            "Sore Throat",
            "HyperTension",
            "Hair Loss",
            "Migraine",
            "Blood Pressure",
            "Blindness"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms);
        setupActionBar();
        mActionBar.setTitle("Symptoms");
        mActivity = this;
        permissionStatus = mActivity.getSharedPreferences("permissionStatus", MODE_PRIVATE);
        mCoversationType = getIntent().getStringExtra("chatType");
        Button continueButton = (Button) findViewById(R.id.continue_button);
        continueButton.setOnClickListener(mOnClickListener);

        Button attatchButton = (Button) findViewById(R.id.attach_button);
        attatchButton.setOnClickListener(mOnClickListener);


        Arrays.sort(symptomsArry);
        mSymptomsTextView = (TextView) findViewById(R.id.symptoms_tv);
        mSymptomsTextView.setText("Please choose symptoms");
        mSymptomsTextView.setOnClickListener(mOnClickListener);

       /* ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_appearence, symptomsArry);
        adapter.setDropDownViewResource(R.layout.spinner_appearence);
        symptomsSpinner.setAdapter(adapter);
        symptomsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.continue_button) {
                if (mCoversationType.equalsIgnoreCase("audio")) {
                    Intent audioCallIntent = new Intent(SymptomsActivity.this, AudioCallActivityV2.class);
                    audioCallIntent.putExtra("CONTACT_ID", "0ac5fc1d-39aa-4636-b3b5-530d5b570fdc");
                    startActivity(audioCallIntent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                } else if (mCoversationType.equalsIgnoreCase("video")) {
                    Intent videoCallIntent = new Intent(SymptomsActivity.this, VideoActivity.class);
                    videoCallIntent.putExtra("CONTACT_ID", "0ac5fc1d-39aa-4636-b3b5-530d5b570fdc");
                    startActivity(videoCallIntent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                } else if (mCoversationType.equalsIgnoreCase("chat")) {
                    Intent intent = new Intent(SymptomsActivity.this, ConversationActivity.class);
                    if (ApplozicClient.getInstance(SymptomsActivity.this).isContextBasedChat()) {
                        intent.putExtra(ConversationUIService.CONTEXT_BASED_CHAT, true);
                    }
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }else{

                }
            } else if (id == R.id.attach_button) {
                if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
                    Toast.makeText(mActivity, "No internet connection. Please retry", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            } else if (id == R.id.symptoms_tv) {
                mSymptomsDialog = new SymptomsDialog(SymptomsActivity.this, symptomsArry);
                mSymptomsDialog.show();
            }

        }
    };

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void uploadFile() {
        final Dialog dialog = new Dialog(SymptomsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.uploadfile_alertbox);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        TextView title = (TextView) dialog.findViewById(R.id.title);
        TextView item1 = (TextView) dialog.findViewById(R.id.item1_tv);
        item1.setVisibility(View.GONE);
        TextView item2 = (TextView) dialog.findViewById(R.id.item2_tv);

        title.setText("Alert");
        item2.setText("Upload files");


        item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                askRunTimePermissions();
            }
        });

        dialog.show();
    }

    void askRunTimePermissions() {

        if (ActivityCompat.checkSelfPermission(mActivity, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(mActivity, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permissionsRequired[0]) || ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permissionsRequired[1])) {
                //Show Information about why you need the permission
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mActivity);
                builder.setTitle("Need multiple permissions");
                builder.setMessage("This app needs camera and storage permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(mActivity, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mActivity);
                builder.setTitle("Need multiple permissions");
                builder.setMessage("This app needs camera and storage permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", mActivity.getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(mActivity.getBaseContext(), "Go to permissions to grant  camera and storage", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(mActivity, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.commit();
        } else {
            //You already have the permission, just go ahead.
            proceedAfterPermission();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }
            if (allgranted) {
                proceedAfterPermission();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permissionsRequired[1])) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mActivity);
                builder.setTitle("Need multiple permissions");
                builder.setMessage("This app needs camera and storage permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(mActivity, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(mActivity.getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_READ_FILE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
            }
        }
    }


    private void proceedAfterPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SymptomsActivity.this);
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Take from gallery", "Take from Camera"},
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        switch (which) {
                            case 0:
                                openGallery(10);
                                break;
                            case 1:

                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, 100);
                                break;

                            default:
                                break;
                        }
                    }
                });
        builder.show();
    }


    public void openGallery(int req_code) {

        Intent intent = new Intent();

        intent.setType("image/*");

        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select file to upload "), req_code);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == RESULT_OK) {
            if (data.getData() != null) {
                selectedImageUri = data.getData();
            } else {
                Log.d("selectedPath1 : ", "Came here its null !");
                Toast.makeText(getApplicationContext(), "failed to get Image!", Toast.LENGTH_LONG).show();
            }

            if (requestCode == 100 && resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                selectedPath = getPath(selectedImageUri);
                //preview.setImageURI(selectedImageUri);
                Log.d("selectedPath1 : ", selectedPath);

            }

            if (requestCode == 10)

            {

                selectedPath = getPath(selectedImageUri);
                //preview.setImageURI(selectedImageUri);
                Log.d("selectedPath1 : ", selectedPath);

            }

        }

    }


    public String getPath(Uri uri) {

        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = managedQuery(uri, projection, null, null, null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);

    }

    public class SymptomsDialog extends Dialog implements View.OnClickListener {

        private ListView list;
        private EditText filterText = null;
        ArrayAdapter<String> adapter = null;
        private static final String TAG = "CityList";

        public SymptomsDialog(Context context, String[] cityList) {
            super(context);

            /** Design the dialog in main.xml file */
            setContentView(R.layout.symptoms_alert_dialog);
            this.setTitle("Select City");
            filterText = (EditText) findViewById(R.id.symptoms_search);
            filterText.addTextChangedListener(filterTextWatcher);
            list = (ListView) findViewById(R.id.symptoms_list);
            adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, cityList);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                    mSymptomsTextView.setText(list.getItemAtPosition(position).toString());
                    mSymptomsDialog.dismiss();
                }
            });
        }

        @Override
        public void onClick(View v) {

        }

        private TextWatcher filterTextWatcher = new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }
        };

        @Override
        public void onStop() {
            filterText.removeTextChangedListener(filterTextWatcher);
        }
    }
}
