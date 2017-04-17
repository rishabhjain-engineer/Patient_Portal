package utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hs.userportal.Directory;
import com.hs.userportal.R;
import com.hs.userportal.UploadService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import config.StaticHolder;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by Rishabh on 16/04/17.
 */

public class RepositoryUtils {

    private static PreferenceHelper mPreferenceHelper;
    private static String patientId = null;

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

    public static void uploadFile(Uri fileUri, Activity activity, Directory directory, String uploadFrom, int totalUri) {

        Log.e("Rishabh", "uri count := UTILS "+totalUri );
        String path = getPathFromContentUri(fileUri, activity);
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
            if (directory.hasFile(imageFile.getName())) {
                exhistimg = "true";

            }
            stringcheck = imageFile.getName();
            /*for (int i = 0; i < thumbImage.size(); i++) {
                String listsplitstr[] = thumbImage.get(i).get("Personal3").split("/");
                if (listsplitstr[listsplitstr.length - 1].contains(chosenimg.substring(0, chosenimg.length() - 4))) {
                    if (leangth < listsplitstr[listsplitstr.length - 1].length()) {

                        stringcheck = listsplitstr[listsplitstr.length - 1];
                        leangth = listsplitstr[listsplitstr.length - 1].length();
                        exhistimg = "true";
                    }

                }
            }*/
            Intent intent = new Intent(activity, UploadService.class);
            intent.putExtra(UploadService.ARG_FILE_PATH, path);
            intent.putExtra("add_path", directory.getDirectoryPath());
            intent.putExtra(UploadService.uploadfrom, uploadFrom);
            intent.putExtra("exhistimg", exhistimg);
            intent.putExtra("stringcheck", stringcheck);
            intent.putExtra("numberofuri",totalUri );
            activity.startService(intent);

           /* System.out.println("After Service");

            String tempPath = getPath(fileUri, activity);
            Bitmap bm;
            BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
            btmapOptions.inSampleSize = 4;
            bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
            // vault_adapter.notifyDataSetChanged();
            if (bm != null) {

                System.out.println("in onactivity");
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                String pic = Base64.encodeToString(byteArray, Base64.DEFAULT);
                String picname = "b.jpg";
                pic = "data:image/jpeg;base64," + pic;
                //  vault_adapter.notifyDataSetChanged();

            }*/

        } else {

            Toast.makeText(activity, "Image should be less than 10 mb.", Toast.LENGTH_LONG).show();

        }


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

    private static boolean folder_name_exists(String trim, Directory directory) {
        return directory.searchFolderName(trim);
    }

    public interface onActionComplete {
        public void onFolderCreated(Directory directory);
    }
}
