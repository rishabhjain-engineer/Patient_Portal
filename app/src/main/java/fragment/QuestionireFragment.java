package fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hs.userportal.LocationClass;
import com.hs.userportal.MapLabDetails;
import com.hs.userportal.R;
import com.hs.userportal.Services;
import com.readystatesoftware.simpl3r.UploadIterruptedException;
import com.readystatesoftware.simpl3r.Uploader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.QuestionireParser;
import utils.PreferenceHelper;
import utils.QuestionReportPageService;

/**
 * Created by ayaz on 27/1/17.
 */

public class QuestionireFragment extends Fragment {
    private Activity mActivity;
    private SharedPreferences sharedPreferences;
    private String patientId;
    private ImageView mUploadImageView;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLERY = 2;
    private static Uri Imguri;
    private String mQuesString, mPath;

    private String pic = "", picname = "";//, oldfile = "Nofile", oldfile1 = "Nofile";
    private static List<QuestionireParser.QuestionDetail> mQuestionDetailsList;
    private static int mPosition;
    private static final String TAG = "QuestionireFragment";
    private PreferenceHelper mPreferenceHelper;
    private static final int REQUEST_CAMERA = 0;

    public static QuestionireFragment newInstance(int pos) {
        mPosition = pos;
        QuestionireFragment fragment = new QuestionireFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_questionire, container, false);

        mActivity = getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        patientId = sharedPreferences.getString("ke", "");

        mPreferenceHelper = (PreferenceHelper) PreferenceHelper.getInstance();
        mQuestionDetailsList = QuestionireParser.getQuestionDetailListStatus1();
        Log.i(TAG, "QuestionireFragment mQuestionDetailsList: " + mQuestionDetailsList.size());
        Log.i(TAG, "position : " + mPosition);
        TextView questionTextView = (TextView) view.findViewById(R.id.question_tv);

        if (mQuestionDetailsList.size() > 0) {
            try {
                questionTextView.setText("Upload your " + mQuestionDetailsList.get(mPosition).getQuestion2() + " report");
            } catch (IndexOutOfBoundsException e) {
                Log.i(TAG, "IndexOutOfBoundsException");
            }
        }
        LinearLayout uploadReportContainerLL = (LinearLayout) view.findViewById(R.id.upload_report_container);
        mUploadImageView = (ImageView) view.findViewById(R.id.image_view);
        uploadReportContainerLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // chooseImage();
                uploadImage();
            }
        });
        return view;
    }

    private void uploadImage() {
        final PackageManager pm = mActivity.getPackageManager();

        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) && pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle("Choose Image Source");
            builder.setItems(new CharSequence[]{"Photo Library", "Take from Camera"},
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                                    try {
                                        intent.putExtra("return-data", true);
                                        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FROM_GALLERY);
                                    } catch (ActivityNotFoundException e) {
                                    }
                                    break;
                                case 1:
                                    File photo = null;
                                    Intent intent1 = new Intent("android.media.action.IMAGE_CAPTURE");
                                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                        photo = new File(Environment.getExternalStorageDirectory(), "test.jpg");
                                    } else {
                                        photo = new File(mActivity.getCacheDir(), "test.jpg");
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

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle("Choose Image Source");
            builder.setItems(new CharSequence[]{"Photo Library"},
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                                    intent.putExtra("crop", "true");
                                    intent.putExtra("aspectX", 1);
                                    intent.putExtra("aspectY", 1);
                                    intent.putExtra("outputX", 250);
                                    intent.putExtra("outputY", 250);
                                    try {
                                        intent.putExtra("return-data", true);
                                        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FROM_GALLERY);
                                    } catch (ActivityNotFoundException e) {

                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
            builder.show();
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == PICK_FROM_GALLERY) {
                Uri selectedImageUri = data.getData();
                String path = getPathFromContentUri(selectedImageUri);
                File imageFile = new File(path);
                long check = ((imageFile.length() / 1024));
                if (check < 10000) {
                    Intent intent = new Intent(mActivity, QuestionReportPageService.class);
                    mPath = path;
                    mQuesString = mQuestionDetailsList.get(mPosition).getQuestion();
                    /*intent.putExtra(QuestionReportPageService.ARG_FILE_PATH, path);
                    intent.putExtra("add_path", "");
                    intent.putExtra("Question", mQuestionDetailsList.get(mPosition).getQuestion());
                    intent.putExtra(QuestionReportPageService.uploadfrom, "");*/
                    /*intent.putExtra("exhistimg", exhistimg);
                    intent.putExtra("stringcheck", stringcheck);*/
                    // mActivity.startService(intent);
                    new QuestionireReportAsyncTask().execute();
                    String tempPath = getPath(selectedImageUri, mActivity);
                    Bitmap bm;
                    BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                    btmapOptions.inSampleSize = 4;
                    bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
                    if (bm != null) {
                        ByteArrayOutputStream byteArrayOutputStream;
                        byte[] byteArray;
                        byteArrayOutputStream = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byteArray = byteArrayOutputStream.toByteArray();
                        pic = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        picname = "b.jpg";
                        pic = "data:image/jpeg;base64," + pic;
                    }
                } else {
                    Toast.makeText(mActivity, "Image should be less than 10 mb.", Toast.LENGTH_LONG).show();
                }
            }

            if (requestCode == PICK_FROM_CAMERA) {
                Uri selectedImageUri = Imguri;
                String path = getPathFromContentUri(selectedImageUri);
                System.out.println(path);
                File imageFile = new File(path);
                long check = ((imageFile.length() / 1024));
                if (check < 2500) {
                    if (check != 0) {
                        mPath = path;
                        mQuesString = mQuestionDetailsList.get(mPosition).getQuestion();
                       /* Intent intent = new Intent(mActivity, QuestionReportPageService.class);
                        intent.putExtra(QuestionReportPageService.ARG_FILE_PATH, path);
                        intent.putExtra("add_path", "");
                        intent.putExtra("Question", mQuestionDetailsList.get(mPosition).getQuestion());
                        intent.putExtra(QuestionReportPageService.uploadfrom, "");*/
                        /*intent.putExtra("exhistimg", exhistimg);
                        intent.putExtra("stringcheck", stringcheck);*/
                        // mActivity.startService(intent);
                        new QuestionireReportAsyncTask().execute();
                        ContentResolver cr = mActivity.getContentResolver();
                        Bitmap bitmap;
                        bitmap = MediaStore.Images.Media.getBitmap(cr, selectedImageUri);
                        ByteArrayOutputStream byteArrayOutputStream;
                        byte[] byteArray;
                        byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                        byteArray = byteArrayOutputStream.toByteArray();
                        pic = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        pic = "data:image/jpeg;base64," + pic;
                        picname = "camera.jpg";
                    }
                } else {
                    Toast.makeText(mActivity, "Image should be less than 2.5 mb.", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getPathFromContentUri(Uri uri) {
        String path = uri.getPath();
        if (uri.toString().startsWith("content://")) {
            String[] projection = {MediaStore.MediaColumns.DATA};
            ContentResolver cr = mActivity.getContentResolver();
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

    private String getPath(Uri uri, Activity activity) {

        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);

    }


    private class QuestionireReportAsyncTask extends AsyncTask<Void, Void, Void> {
        public static final String ARG_FILE_PATH = "file_path";
        public static final String UPLOAD_STATE_CHANGED_ACTION = "com.readystatesoftware.simpl3r.example.UPLOAD_STATE_CHANGED_ACTION";
        public static final String UPLOAD_CANCELLED_ACTION = "com.readystatesoftware.simpl3r.example.UPLOAD_CANCELLED_ACTION";
        public static final String S3KEY_EXTRA = "s3key";
        public static final String PERCENT_EXTRA = "percent";
        public static final String MSG_EXTRA = "msg";
        public static final String uploadfrom = "uploadfrom";

        private JSONObject sendData;
        private String patientId;
        private static final int NOTIFY_ID_UPLOAD = 1337;
        private RequestQueue queue1, queue2;
        private AmazonS3Client s3Client;
        private Uploader uploader;
        private JsonObjectRequest jr1, jr2;
        private NotificationManager nm;
        private final Handler handler = new Handler();
        private String fname, afterDecode, uplodfrm;
        private String add_path;
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Please Wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
            s3Client = new AmazonS3Client(new BasicAWSCredentials(getString(R.string.s3_access_key), getString(R.string.s3_secret)));
        }

        @Override
        protected Void doInBackground(Void... params) {

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
            patientId = sharedPreferences.getString("ke", "");

        /*try {
            exhistimg = intent.getStringExtra("exhistimg");
            stringcheck = intent.getStringExtra("stringcheck");
        } catch (Exception e) {
            e.printStackTrace();
            exhistimg = "";
            stringcheck = "";
        }*/
            File fileToUpload = new File(mPath);

            final String s3ObjectKey = md5(mPath);
            String s3BucketName = getString(R.string.s3_bucket);
            final String path;
        /*if (add_path.equalsIgnoreCase("")) {
            if (uplodfrm.equalsIgnoreCase("notfilevault")) {
                path = patientId + "/" + "FileVault/Personal/Prescription/";
            } else {
                path = patientId + "/" + "FileVault/Personal/";
            }
        } else {
            path = patientId + "/" + "FileVault/Personal/" + add_path + "/";  //Path = "6fbbd98b-65e5-468e-98ee-741903caeea2/FileVault/Personal/Reports/";
        }*/
            path = patientId + "/FileVault/Personal/Reports/";

            final String msg = "Uploading " + s3ObjectKey + "...";
            // create a new uploader for this file
            String splt[] = mPath.split("/");
            String imagename = splt[splt.length - 1];
            Calendar cal = Calendar.getInstance();

            if (uplodfrm != null && uplodfrm.equalsIgnoreCase("notfilevault")) {
                fname = imagename;
            } else {
            /*if (exhistimg!=null&&exhistimg != "" && exhistimg.equalsIgnoreCase("true")) {
                fname = stringcheck.substring(0, stringcheck.length() - 4) + "_1.jpg";
            } else {*/
                fname = imagename.substring(0, imagename.length() - 4) + ".jpg";
                // }
            }
            uploader = new Uploader(mActivity, s3Client, s3BucketName, s3ObjectKey, fileToUpload, path, fname);
            // listen for progress updates and broadcast/notify them appropriately
            try {
                String s3Location = uploader.start(); // initiate the upload
                String[] parts = s3Location.split("com/" + "");
                System.out.println(parts[1].trim());
                String sendurl = parts[1].trim();

                afterDecode = URLDecoder.decode(sendurl, "UTF-8");//62181ffc-6f94-4b83-9334-395b8cb0960d/FileVault/251bc0e4-6fd0-4bf4-894c-c3bb99dde05e.jpg
                String[] file_name = afterDecode.split("/");
                int len = file_name.length;

                if (afterDecode.endsWith(".jpg")) {
                    // afterDecode = afterDecode.substring(0, afterDecode.length() - 4);
                }
                sendData = new JSONObject();
                sendData.put("PatientId", mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID));
                //sendData.put("ImageName", file_name[len - 1]);
                sendData.put("Question", mQuesString);
                sendData.put("Path", path);

                queue1 = Volley.newRequestQueue(mActivity);
                Log.i("UpdateQuizPath", "Send Data:"+sendData);
                // String url1 = "http://192.168.1.11/WebServices/Labservice.asmx/UpdateQuizPath";
                String url1 = "https://api.healthscion.com/WebServices/Labservice.asmx/UpdateQuizPath";
                jr1 = new JsonObjectRequest(
                        Request.Method.POST, url1, sendData,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.i("UpdateQuizPath", "Respons Data:"+response);

                                try {
                                    if (uplodfrm != null && uplodfrm.equalsIgnoreCase("notfilevault")) {
                                        LocationClass.pic = null;
                                        MapLabDetails.pic_maplab = null;
                                        //uploadPrescriptionMail();
                                        Toast.makeText(mActivity, response.getString("d"), Toast.LENGTH_SHORT).show();
                                    } else {

                                        Toast.makeText(mActivity, response.getString("d"), Toast.LENGTH_SHORT).show();
                                        if (response.getString("d").equalsIgnoreCase("success")) {
                                        } else {
                                            Toast.makeText(mActivity, response.getString("d"), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders()
                            throws AuthFailureError {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Cookie", Services.hoja);
                        System.out
                                .println("Services hoja:" + Services.hoja);
                        return headers;
                    }
                };

                jr1.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue1.add(jr1);


            } catch (UploadIterruptedException uie) {
                //broadcastState(s3ObjectKey, -1, "User interrupted");
            } catch (Exception e) {
                e.printStackTrace();
                //broadcastState(s3ObjectKey, -1, "Error: " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            mUploadImageView.setImageResource(R.drawable.addmorereports);
        }
    }

    private String md5(String s) {
        try {
            // create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void takePhoto() {
        File photo = null;
        Intent intent1 = new Intent("android.media.action.IMAGE_CAPTURE");
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            photo = new File(Environment.getExternalStorageDirectory(), "test.jpg");
        } else {
            photo = new File(mActivity.getCacheDir(), "test.jpg");
        }
        if (photo != null) {
            intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
            Imguri = Uri.fromFile(photo);
            startActivityForResult(intent1, PICK_FROM_CAMERA);
        }
    }


    /**
     * Method to check permission
     */
    void checkCameraPermission() {
        boolean isGranted;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.
            requestCameraPermission();
        } else {
            takePhoto();
        }
    }

    /**
     * Method to request permission for camera
     */
    private void requestCameraPermission() {
        // Camera permission has not been granted yet. Request it directly.
        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            Log.i(TAG, "Received response for Camera permission request.");
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                takePhoto();
            } else {
                //Permission not granted
                Toast.makeText(mActivity, "You need to grant camera permission to use camera", Toast.LENGTH_LONG).show();
            }
        }
    }
}
