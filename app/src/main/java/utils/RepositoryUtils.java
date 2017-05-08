package utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.hs.userportal.Directory;
import com.hs.userportal.DirectoryFile;
import com.hs.userportal.R;
import com.hs.userportal.SelectableObject;
import com.hs.userportal.UploadService;
import com.hs.userportal.UploadUri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import config.StaticHolder;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by Rishabh on 16/04/17.
 */

public class RepositoryUtils {

    private static PreferenceHelper mPreferenceHelper;
    private static String patientId = null;
    private static UploadUri mUploadUriObject;
    private static List<File> mListOfUploadUri = new ArrayList<>();

    public static void createNewFolder(final Activity activity, final Directory directory, final onActionComplete listener) {
        // final Dialog overlay_dialog = new Dialog(Pkg_TabActivity.this, R.style.DialogSlideAnim);
        mPreferenceHelper = PreferenceHelper.getInstance();
        patientId = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID);
        final Dialog overlay_dialog = new Dialog(activity);
        overlay_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);//SOFT_INPUT_STATE_ALWAYS_HIDDEN
        overlay_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        overlay_dialog.setCanceledOnTouchOutside(true);
        overlay_dialog.setContentView(R.layout.create_folderdialog);
        Button btn_continue = (Button) overlay_dialog.findViewById(R.id.create_btn);
        TextView path = (TextView) overlay_dialog.findViewById(R.id.path);
        path.setVisibility(View.GONE);
        final EditText folder_name = (EditText) overlay_dialog.findViewById(R.id.folder_name);
        //opening keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(folder_name.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        //
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
                final ProgressDialog progress = new ProgressDialog(activity);

                progress.setCancelable(false);
                //progress.setTitle("Logging in...");
                progress.setMessage("Please wait...");
                progress.setIndeterminate(true);

                final String folder = folder_name.getText().toString();
                if (folder_name_exists(folder.trim(), directory)) {
                    folder_name.setError("A folder already exists with this name.");
                } else if (folder != "" && (!folder.equals(""))) {
                    overlay_dialog.dismiss();
                    progress.show();
                    JSONObject sendData = new JSONObject();
                    String imageFilePathInFolder = directory.getDirectoryPath();
                    try {
                        sendData.put("FolderName", folder);
                        sendData.put("Path", directory.getDirectoryPath());
                        sendData.put("patientId", patientId);
                    } catch (JSONException EX) {
                        EX.printStackTrace();
                    }
                    StaticHolder sttc_holdr = new StaticHolder(activity, StaticHolder.Services_static.CreateFolder);
                    String url = sttc_holdr.request_Url();
                    JsonObjectRequest jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            // System.out.println(response);

                            try {
                                String packagedata = response.getString("d");
                                if (packagedata.equalsIgnoreCase("Error")) {
                                    progress.dismiss();
                                    Toast.makeText(activity, "An error occurred while creating folder.", Toast.LENGTH_SHORT).show();
                                } else if (packagedata.equalsIgnoreCase("Folder exist")) {
                                    progress.dismiss();
                                    Toast.makeText(activity, "A folder already exists with this name.", Toast.LENGTH_SHORT).show();
                                } else if (packagedata.equalsIgnoreCase("Added")) {
                                    progress.dismiss();
                                    Toast.makeText(activity, "Folder created successfully.", Toast.LENGTH_LONG).show();
                                    Directory newDirectory = new Directory(folder);
                                    directory.addDirectory(newDirectory);
                                    listener.onFolderCreated(directory);
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
                            Toast.makeText(activity, "Server Connectivity Error, Try Later.", Toast.LENGTH_SHORT).show();

                        }
                    }) {
                    };
                    Volley.newRequestQueue(activity).add(jr);
                } else {
                    folder_name.setError("Enter correct folder name");
                    // Toast.makeText(getApplicationContext(), "Enter valid Mobile number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        overlay_dialog.show();
    }


    /*public static void uploadFile(ArrayList<Uri> fileUri, ArrayList<Uri> filethumbUri, Activity activity, Directory directory, String uploadFrom) {

        for (int i = 0; i < fileUri.size(); i++) {

            mUploadUriObject = new UploadUri(fileUri.get(i));

            mUploadUriObject.setImageUri(fileUri.get(i));

            if (filethumbUri.get(i) != null) {
                mUploadUriObject.setThumbUri(filethumbUri.get(i));
                String imageThumbStoredPath = mUploadUriObject.getThumbUri().getPath();
                File imageThumbFile = new File(imageThumbStoredPath);
                mUploadUriObject.setThumbFile(imageThumbFile);
            } else {
                Log.e("Rishabh", "pdf file -- no thumbnail");
                mUploadUriObject.setThumbUri(null);
                mUploadUriObject.setThumbFile(null);
            }


            String imageStoredPath = getPathFromContentUri(mUploadUriObject.getImageUri(), activity);
            mUploadUriObject.setImagePath(imageStoredPath);

            //String imageThumbStoredPath =  mUploadUriObject.getThumbUri().getPath();

            File imageFile = new File(mUploadUriObject.getImagePath());
            mUploadUriObject.setImageFile(imageFile);


            String imageStoredPath = mUploadUriObject.getImageUri().getPath();


            File imageFile = new File(imageStoredPath);
            mUploadUriObject.setImageFile(imageFile);


            String path1 = mUploadUriObject.getImageFile().getAbsolutePath();
            String splitfo_lenthcheck[] = path1.split("/");
            int filenamelength = splitfo_lenthcheck[splitfo_lenthcheck.length - 1].length();
            long check = ((imageFile.length() / 1024));
            if (check < 10000 && filenamelength < 99) {
                String splitstr[];
                String chosenimg = "";
                String stringcheck = "", exhistimg = "false";
                int leangth = 0;
                if (imageStoredPath.contains("/")) {
                    splitstr = imageStoredPath.split("/");
                    chosenimg = splitstr[splitstr.length - 1];
                }
                if (directory.hasFile(imageFile.getName())) {
                    exhistimg = "true";
                    mUploadUriObject.setExistingImage(exhistimg);
                }
                stringcheck = mUploadUriObject.getImageFile().getName();
                mUploadUriObject.setImageName(stringcheck);

            } else {
                Toast.makeText(activity, "Image should be less than 10 mb.", Toast.LENGTH_LONG).show();
            }
            mListOfUploadUri.add(mUploadUriObject);

        }

        Intent intent = new Intent(activity, UploadService.class);
        intent.putExtra(UploadService.uploadfrom, uploadFrom);
        intent.putExtra("add_path", directory.getDirectoryPath());
        activity.startService(intent);

    }
*/
    public static void uploadFilesToS3(List<File> listOfFiles, Activity activity, Directory directory, String uploadFrom) {

//        String isExisting;
//        String fileName;
        /*for(File file : listOfFiles){
            if(file.getName().contains("_thumb"))
                continue;;

            directory.hasFile(file.getName()){
            }
        }*/

        mListOfUploadUri = listOfFiles;
        Intent intent = new Intent(activity, UploadService.class);
        intent.putExtra(UploadService.uploadfrom, uploadFrom);
        intent.putExtra("add_path", directory.getDirectoryPath());
        activity.startService(intent);

    }

    public static List<File> getUploadUriObjectList() {

        return mListOfUploadUri;
    }

    public static String getPath(Uri uri, Activity activity) {

        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);

    }

    private static String getPathFromContentUri(Uri uri, Activity activity) {
        String path = uri.getPath();
        if (uri.toString().startsWith("content://")) {
            String[] projection = {MediaStore.MediaColumns.DATA};
            ContentResolver cr = activity.getContentResolver();
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

    public static File getThumbnailFile(File mainFile, Activity activity) {
    //    Log.e("RAVI", "Repository utils -- Main file : " + mainFile.getAbsolutePath());
        File thumbnailFile = null;


        try {
            thumbnailFile = createImageFile(activity);
            Bitmap bitmap = BitmapFactory.decodeFile(mainFile.getAbsolutePath());
            if (bitmap != null) {
                Bitmap thumbBitmap = getThumbnailImage(bitmap);
                FileOutputStream fos = new FileOutputStream(thumbnailFile);
                thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                fos.flush();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        String mainFileName = mainFile.getName();
        File file = null;
        //Ravi.jpg
        String[] splitted = mainFileName.split("\\.");
        //Ravi jpg
        splitted[splitted.length - 2] = splitted[splitted.length - 2] + "_thumb";

        String thumbnailFileName = splitted[0] + "." + "jpg";
        file = new File(thumbnailFile.getParent() + "/" + thumbnailFileName);
        boolean renamedFile = thumbnailFile.renameTo(file);


   //     Log.e("RAVI", "Repository utils -- Thumb file : " + file.getAbsolutePath());

        return file;
    }

    public static String getThumbFileName(String fileName) {
        String[] splitted = fileName.split("\\.");
        splitted[splitted.length - 2] = splitted[splitted.length - 2] + "_thumb";

        return splitted[ 0]+"." + "jpg";
    }

    private static Bitmap getThumbnailImage(Bitmap bm) {

        //maintaining min resolution of 150*150;

        int width = bm.getWidth();
        int height = bm.getHeight();

        Bitmap resizedBitmap;
        Bitmap decoded;

        if (width > 250 || height > 250) {

            float aspectRatio;

            //image is too big, resize and compress to 80% quality
            if (width > height) {

                aspectRatio = (float) width / (float) height;
                width = 150;
                height = (int) (width / aspectRatio);

            } else {

                aspectRatio = (float) height / (float) width;
                height = 150;
                width = (int) (height / aspectRatio);

            }

            resizedBitmap = Bitmap.createScaledBitmap(bm, width, height, false);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
            decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));
            bm.recycle();
            resizedBitmap.recycle();

        } else {

            //image is small, just compress to 90% quality
            resizedBitmap = bm;
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
            decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));
            bm.recycle();
            resizedBitmap.recycle();

        }

        return decoded;

    }

    private static File createImageFile(Activity activity) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    private static boolean folder_name_exists(String trim, Directory directory) {
        return directory.searchFolderName(trim);
    }

    public interface onActionComplete {
        void onFolderCreated(Directory directory);
    }

    public static void deleteObjects(List<SelectableObject> listOfSelectedObjects, String patientId, final Activity mActivity, final OnDeleteCompletion listener) {
        JSONArray array = new JSONArray();
        for (SelectableObject object : listOfSelectedObjects) {
            JSONObject imageobject = new JSONObject();
            if (object.getObject() instanceof Directory) {
                try {
                    imageobject.put("Type", "1");
                    imageobject.put("Key", patientId + "/FileVault/Personal/" + ((Directory) object.getObject()).getDirectoryPath());
                    imageobject.put("ThumbFile", "");
                    imageobject.put("Status", "");
                    array.put(imageobject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (object.getObject() instanceof DirectoryFile) {
                try {
                    imageobject.put("Type", "0");
                    imageobject.put("Key", ((DirectoryFile) object.getObject()).getKey());
                    imageobject.put("ThumbFile", ((DirectoryFile) object.getObject()).getThumb());
                    imageobject.put("Status", "");
                    array.put(imageobject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(array);
        RequestQueue queue2 = Volley.newRequestQueue(mActivity);

        JSONObject sendData = new JSONObject();
        try {
            sendData.put("ObjectList", array);
            sendData.put("UserId", patientId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StaticHolder sttc_holdr = new StaticHolder(mActivity, StaticHolder.Services_static.DeleteObject);
        String url = sttc_holdr.request_Url();
        JsonRequest jr2 = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                listener.onSuccessfullyDeleted();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onFailure();
            }
        });
        queue2.add(jr2);
    }


    public static void moveObject(List<SelectableObject> listOfSelectedObjects, String patientId, final Activity mActivity, Directory oldDirectory, Directory newDirectory, final OnMoveCompletion listener) {

        String absolutePath;
        String newPath;

        if (!oldDirectory.getDirectoryPath().equals("")) {
            absolutePath = patientId + "/FileVault/" + "Personal/" + oldDirectory.getDirectoryPath() + "/";
        } else {
            absolutePath = patientId + "/FileVault/" + "Personal/";
        }


        if (!newDirectory.getDirectoryPath().equals("")) {
            newPath = patientId + "/FileVault/" + "Personal/" + newDirectory.getDirectoryPath();
        } else {
            newPath = patientId + "/FileVault/" + "Personal";
        }


        JSONArray jsonArray = new JSONArray();
        for (SelectableObject object : listOfSelectedObjects) {
            if (object.getObject() instanceof DirectoryFile) {
                String fullImagepath = ((DirectoryFile) object.getObject()).getKey();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Key", fullImagepath);
                    jsonObject.put("Status", "");
                    jsonObject.put("ThumbFile", ((DirectoryFile) object.getObject()).getThumb());
                    jsonObject.put("Type", "0");
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        JSONObject sendMoveJsonObject = new JSONObject();
        try {
            sendMoveJsonObject.put("AbsolutePath", absolutePath);
            sendMoveJsonObject.put("NewPath", newPath);
            sendMoveJsonObject.put("UserId", patientId);
            sendMoveJsonObject.put("ObjectList", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue queue2 = Volley.newRequestQueue(mActivity);
        StaticHolder sttc_holdr = new StaticHolder(mActivity, StaticHolder.Services_static.MoveObject);
        String url = sttc_holdr.request_Url();
        JsonRequest jr2 = new JsonObjectRequest(Request.Method.POST, url, sendMoveJsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                listener.onSuccessfullMove();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onFailure();
//                Log.e("Rishabh", "error := " + error);
            }
        });
        queue2.add(jr2);


    }

    public interface OnDeleteCompletion {
        void onSuccessfullyDeleted();

        void onFailure();
    }

    public interface OnMoveCompletion {


        void onSuccessfullMove();

        void onFailure();
    }

}
