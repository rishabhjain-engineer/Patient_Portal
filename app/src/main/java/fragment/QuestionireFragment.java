package fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.hs.userportal.WalthroughFragment;
import com.hs.userportal.update;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

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
                AlertDialog.Builder AlertDialogBuilder = new AlertDialog.Builder(mActivity);
                AlertDialogBuilder.setTitle("Alert!");
                AlertDialogBuilder.setItems(new CharSequence[]{"Upload Files"}, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                chooseImage();
                                break;
                            default:
                                break;
                        }
                    }
                });
                AlertDialogBuilder.show();
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

}
