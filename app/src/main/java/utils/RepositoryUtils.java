package utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.v7.widget.RecyclerView;
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
import com.android.volley.toolbox.Volley;
import com.hs.userportal.Directory;
import com.hs.userportal.GalleryReceivedData;
import com.hs.userportal.R;

import org.json.JSONException;
import org.json.JSONObject;

import adapters.RepositoryAdapter;
import config.StaticHolder;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.hs.userportal.LocationClass.patientId;

/**
 * Created by Rishabh on 16/04/17.
 */

public class RepositoryUtils {


    private RepositoryAdapter mRepositoryAdapter;
    private RecyclerView mRecyclerView;
    private RequestQueue queue, queue3;

    public static void createNewFolder(final Activity activity, final Directory directory, final onActionComplete listener) {
        // final Dialog overlay_dialog = new Dialog(Pkg_TabActivity.this, R.style.DialogSlideAnim);
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

    private static boolean folder_name_exists(String trim, Directory directory) {
        return directory.searchFolderName(trim);
    }

    public interface onActionComplete {
        public void onFolderCreated(Directory directory);
    }
}
