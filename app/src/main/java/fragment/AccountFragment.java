package fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.hs.userportal.AboutUs;
import com.hs.userportal.FAQ;
import com.hs.userportal.Help;
import com.hs.userportal.MyVolleySingleton;
import com.hs.userportal.PrivacyPolicy;
import com.hs.userportal.R;
import com.hs.userportal.Services;
import com.hs.userportal.changepass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import networkmngr.ConnectionDetector;
import ui.AccountActivity;
import ui.BaseActivity;
import ui.CreditsActivity;
import ui.ProfileContainerActivity;
import ui.SignInActivity;
import utils.AppConstant;
import utils.PreferenceHelper;

/**
 * Created by android1 on 3/4/17.
 */

public class AccountFragment extends Fragment{
    private Services mServices;
    private LinearLayout mFooterDashBoard, mFooterReports, mRepository,  mFooterFamily;
    private ImageLoader mImageLoader;
    private CallbackManager mCallbackManager = null;
    private String facebookPic, userID, id, name;
    private String pic = "", picname = "", thumbpic = "", oldfile = "Nofile", oldfile1 = "Nofile";
    private Activity mActivity;
    private PreferenceHelper mPreferenceHelper;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, null);
        mActivity = getActivity();
        mPreferenceHelper = PreferenceHelper.getInstance();
        id = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID);
        mServices = new Services(mActivity);
        name = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_NAME);
        mCallbackManager = CallbackManager.Factory.create();
        String facebookId = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.FACE_BOOK_ID);
        if (!TextUtils.isEmpty(facebookId)) {
            facebookPic = facebookId;
        }
        ListView accountListView = (ListView) view.findViewById(R.id.account_list_view);

        // Defined Array values to show in ListView
        String[] values = new String[]{"My Profile", "FAQ's", "Feedback", "About Us", "Change Password", "Terms & Conditions", "Credits", "Logout"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_1, android.R.id.text1, values);
        accountListView.setAdapter(adapter);
        accountListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    //Profile
                    Intent intent = new Intent(mActivity, ProfileContainerActivity.class);
                    intent.putExtra("id", mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID));
                    intent.putExtra("pass", mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.PASS));
                    intent.putExtra("pic", pic);
                    intent.putExtra("picname", picname);
                    //intent.putExtra("fbLinked", fbLinked);
                    //intent.putExtra("fbLinkedID", fbLinkedID);
                    startActivity(intent);
                } else if (position == 1) {
                    //FAQ
                    Intent intent = new Intent(mActivity, FAQ.class);
                    startActivity(intent);
                } else if (position == 2) {
                    //Feedback
                    Intent intentContact = new Intent(mActivity, Help.class);
                    intentContact.putExtra("id", id);
                    startActivity(intentContact);
                } else if (position == 3) {
                    //About
                    Intent intentAbout = new Intent(mActivity, AboutUs.class);
                    intentAbout.putExtra("from", "dash");
                    startActivity(intentAbout);
                } else if (position == 4) {
                    //Password
                    Intent change = new Intent(mActivity, changepass.class);
                    change.putExtra("id", id);
                    startActivity(change);
                }  else if (position == 5) {
                    //Terms
                    Intent termsAndCondition = new Intent(mActivity, PrivacyPolicy.class);
                    startActivity(termsAndCondition);
                } else if (position == 6) {
                    //Credits
                    Intent termsAndCondition = new Intent(mActivity, CreditsActivity.class);
                    startActivity(termsAndCondition);
                } else if (position == 7) {
                    //logout
                    logout();
                }
            }

        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private AlertDialog alert;
    private void logout() {
        alert = new AlertDialog.Builder(mActivity).create();
        alert.setTitle("Message");
        alert.setMessage("Are you sure you want to Logout?");
        alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ConnectionDetector isInternetOn = new ConnectionDetector(mActivity);
                if (isInternetOn.isConnectingToInternet())
                    new AccountFragment.LogoutAsync().execute();
                else {
                    Toast.makeText(mActivity, "No Internet connection Try again Later!", Toast.LENGTH_LONG).show();
                }
            }
        });
        alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    private ProgressDialog progress;
    private JSONObject logoutReceivedJsonObject;

    private class LogoutAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(mActivity);
            progress.setMessage("Loading...");
            progress.setCancelable(false);
            progress.setIndeterminate(true);
            progress.show();
        }

        protected Void doInBackground(Void... params) {
            JSONObject sendData = new JSONObject();
            try {
                sendData.put("UserId", mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            logoutReceivedJsonObject = mServices.LogOut(sendData);
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            SharedPreferences sharedpreferences = mActivity.getSharedPreferences(AppConstant.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.commit();
            PreferenceManager.getDefaultSharedPreferences(mActivity).edit().clear().commit();
            progress.dismiss();
            Intent intent = new Intent(mActivity, SignInActivity.class);
            intent.putExtra("from logout", "logout");
            startActivity(intent);
            LoginManager.getInstance().logOut();
            mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.FACE_BOOK_ID, null);
            mActivity.finish();
        }

    }

    private FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    // JSON of FB ID as response.
                    try {
                        //new AccountActivity.FbLinkAsync().execute();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,last_name,first_name,name,email,gender,birthday");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };

    private Dialog fbDialog;
    private JSONObject receiveDataFbLink;
    private class FbLinkAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(mActivity);
            progress.setCancelable(false);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);
            progress.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject sendData = new JSONObject();
            try {

                sendData.put("getfbid", userID);
                sendData.put("userId", id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            receiveDataFbLink = mServices.fblink(sendData);
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progress.dismiss();
            try {
                if (receiveDataFbLink.get("d").equals("Successfully Linked")) {
                    //facebooklink.setVisibility(View.VISIBLE);  //TODO visiblity changed by SPARTANS ( ADDITIONALY )
                    //unlinkmenu = 1;
                    //fbLinked = "true";

                    fbDialog = new Dialog(mActivity, android.R.style.Theme_DeviceDefault_Light_Dialog);
                    fbDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    fbDialog.setCancelable(false);
                    fbDialog.setContentView(R.layout.fbdialog);

                    String url = String.format("https://graph.facebook.com/%s/picture?type=large", userID);
                    InputStream inputStream = null;
                    try {
                        inputStream = new URL(url).openStream();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    // Get dialog widgets references
                    Button btnAccept = (Button) fbDialog.findViewById(R.id.bAccept);
                    Button btnSkip = (Button) fbDialog.findViewById(R.id.bSkip);
                    ImageView fbImage = (ImageView) fbDialog.findViewById(R.id.fbImage);
                    TextView fbName = (TextView) fbDialog.findViewById(R.id.fbName);

                    fbName.setText(name);
                    bitmap = getCroppedBitmap(bitmap);
                    fbImage.setImageBitmap(bitmap);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    pic = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    picname = "b.jpg";
                    pic = "data:image/jpeg;base64," + pic;

                    btnAccept.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {new AccountFragment.FbImagePull().execute();
                            progress.dismiss();
                        }
                    });
                    btnSkip.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            fbDialog.dismiss();
                        }
                    });
                    fbDialog.show();
                } else {
                    ((BaseActivity)mActivity).showAlertMessage("Your Facebook account is already linked with some other Healthscion account!");
                }

            } catch (JSONException e) {
                e.printStackTrace();

            }

        }
    }

    JSONObject receiveFbImageSave;
    private class FbImagePull extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(mActivity);
            progress.setCancelable(false);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);
            progress.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject sendData = new JSONObject();
            try {
                sendData.put("OldFile", oldfile);
                sendData.put("FileName", picname);
                sendData.put("File", pic);
                sendData.put("OldFile1", oldfile1);
                //sendData.put("patientCode", subArray.getJSONObject(0).getString("patientCode").toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            receiveFbImageSave = mServices.UpdateImage(sendData);
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {

                fbDialog.dismiss();
                progress.dismiss();
                if (receiveFbImageSave.getString("d").equals("\"Patient Image updated Successfully\"")) {
                    new AccountFragment.Imagesync().execute();
                } else {
                    Toast.makeText(mActivity, "Profile picture couldn't be updated. Please try again!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progress.dismiss();
        }
    }

    private class Imagesync extends AsyncTask<Void, Void, Void> {

        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(mActivity);
            progress.setCancelable(true);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (progress != null) {
                progress.dismiss();
            }
            //user_pic.setImageBitmap(output);
            // user_pic.setImageUrl(pic.replaceAll(" ", "%20"),mImageLoader);
            //imageProgress.setVisibility(View.INVISIBLE);
            // new BackgroundProcess().execute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONArray subArray = null;
            JSONObject sendData = new JSONObject();
            try {
                sendData.put("PatientId", id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println(sendData);
            JSONObject receiveData = mServices.verify(sendData);
            System.out.println("ImageSync " + receiveData);

            String data;
            try {
                data = receiveData.getString("d");
                JSONObject cut = new JSONObject(data);
                subArray = cut.getJSONArray("Table");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                String abc = subArray.getJSONObject(0).getString("Image");
                String def = subArray.getJSONObject(0).getString("ThumbImage");
                String path = subArray.getJSONObject(0).getString("Path");
                pic = path + abc;
                thumbpic = path + def;


                Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(thumbpic).getContent());
                Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(output);

                final Paint paint = new Paint();
                final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

                paint.setAntiAlias(true);
                canvas.drawARGB(0, 0, 0, 0);
                //  canvas.drawRect(rect,paint);
                canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getHeight() / 2, paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(bitmap, rect, rect, paint);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }

            return null;
        }

    }

    private Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        if (bitmap.getWidth() > bitmap.getHeight()) {
            // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getHeight() / 2, paint);
        } else {
            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        // Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        // return _bmp;
        return output;
    }
}

