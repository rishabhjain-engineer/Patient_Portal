package fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hs.userportal.Filevault;
import com.hs.userportal.R;
import com.hs.userportal.UploadService;
import com.hs.userportal.WalthroughFragment;
import com.hs.userportal.update;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import config.StaticHolder;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by ayaz on 27/1/17.
 */

public class QuestionireFragment extends Fragment {
    private Activity mActivity;
    private SharedPreferences sharedPreferences;
    private String patientId;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLERY = 2;
    private static Uri Imguri;

    public static QuestionireFragment newInstance() {
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
        LinearLayout uploadReportContainerLL = (LinearLayout) view.findViewById(R.id.upload_report_container);

        uploadReportContainerLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        return view;
    }

    private void chooseImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Pick from Gallery", "Take from Camera"},
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intent.setType("image*//**//*");
                                startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FROM_GALLERY);
                                break;
                            case 1:
                                File photo = null;
                                Intent intent1 = new Intent("android.media.action.IMAGE_CAPTURE");
                                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                    photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "test1.jpg");
                                    boolean b = photo.delete();
                                    String df = "";
                                    photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "test1.jpg");
                                } else {
                                    photo = new File(mActivity.getCacheDir(), "test1.jpg");
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

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("ayaz", "QuestionireFragment onActivityResult");
    }*/

    /*public void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            if (requestCode == PICK_FROM_GALLERY) {
                Uri selectedImageUri = data.getData();
                String path = getPathFromContentUri(selectedImageUri);

                File imageFile = new File(path);
                String path1 = imageFile.getAbsolutePath();
                String splitfo_lenthcheck[] = path1.split("/");
                int filenamelength = splitfo_lenthcheck[splitfo_lenthcheck.length - 1].length();
                long check = ((imageFile.length() / 1024));
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
                    Intent intent = new Intent(mActivity, UploadService.class);
                    intent.putExtra(UploadService.ARG_FILE_PATH, path);
                    intent.putExtra("add_path", "");
                    intent.putExtra(UploadService.uploadfrom, "");
                    intent.putExtra("exhistimg", exhistimg);
                    intent.putExtra("stringcheck", stringcheck);
                    mActivity.startService(intent);

                    System.out.println("After Service");

                    String tempPath = getPath(selectedImageUri, mActivity);
                    Bitmap bm;
                    BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                    btmapOptions.inSampleSize = 4;
                    bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
                    // vault_adapter.notifyDataSetChanged();
                    if (bm != null) {

                        ByteArrayOutputStream byteArrayOutputStream;
                        byte[] byteArray;
                        String pic = null;
                        String picname = "";

                        byteArrayOutputStream = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byteArray = byteArrayOutputStream.toByteArray();

                        pic = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        picname = "b.jpg";
                        pic = "data:image/jpeg;base64," + pic;
                        //  vault_adapter.notifyDataSetChanged();

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

                if (check < 10000) {
                    String[] splitstr;
                    String chosenimg = "";
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
                        mActivity.startService(intent);

                        ByteArrayOutputStream byteArrayOutputStream;
                        byte[] byteArray;
                        String pic = null;
                        String picname = "";

                        ContentResolver cr = mActivity.getContentResolver();
                        Bitmap bitmap;
                        bitmap = MediaStore.Images.Media.getBitmap(cr, selectedImageUri);
                        byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                        byteArray = byteArrayOutputStream.toByteArray();
                        pic = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        pic = "data:image/jpeg;base64," + pic;
                        picname = "camera.jpg";

                        // finish();
                        startActivity(mActivity.getIntent());
                    }
                } else {
                    Toast.makeText(mActivity, "Image should be less than 10 mb.", Toast.LENGTH_LONG).show();
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/

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

    private void preventRotation(String filePath) {
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
}
