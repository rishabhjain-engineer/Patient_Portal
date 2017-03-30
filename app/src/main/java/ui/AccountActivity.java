package ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.hs.userportal.AboutUs;
import com.hs.userportal.FAQ;
import com.hs.userportal.Help;
import com.hs.userportal.PrivacyPolicy;
import com.hs.userportal.R;
import com.hs.userportal.Services;
import com.hs.userportal.changepass;
import com.hs.userportal.update;

import org.json.JSONException;
import org.json.JSONObject;

import networkmngr.ConnectionDetector;
import utils.AppConstant;
import utils.PreferenceHelper;

/**
 * Created by ayaz on 30/3/17.
 */

public class AccountActivity extends BaseActivity {
    private Services mServices;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        setupActionBar();
        mActionBar.setTitle("Account");
        mServices = new Services(this);
        ListView accountListView = (ListView) findViewById(R.id.account_list_view);

        // Defined Array values to show in ListView
        String[] values = new String[]{"My Profile", "FAQ's", "Feedback", "About Us", "Change Password", "Terms & Conditions", "Logout"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
        accountListView.setAdapter(adapter);
        accountListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    //Profile
                    Intent intent = new Intent(getApplicationContext(), ProfileContainerActivity.class);
                    intent.putExtra("id", id);
                  /*  intent.putExtra("pass", passw);
                    intent.putExtra("pic", pic);
                    intent.putExtra("picname", picname);
                    intent.putExtra("fbLinked", fbLinked);
                    intent.putExtra("fbLinkedID", fbLinkedID);*/
                    startActivity(intent);
                } else if (position == 1) {
                    //FAQ
                    Intent intent = new Intent(getApplicationContext(), FAQ.class);
                    startActivity(intent);
                } else if (position == 2) {
                    //Feedback
                    Intent intentContact = new Intent(getApplicationContext(), Help.class);
                    intentContact.putExtra("id", id);
                    startActivity(intentContact);
                } else if (position == 3) {
                    //About
                    Intent intentAbout = new Intent(getApplicationContext(), AboutUs.class);
                    intentAbout.putExtra("from", "dash");
                    startActivity(intentAbout);
                } else if (position == 4) {
                    //Password
                    Intent change = new Intent(getApplicationContext(), changepass.class);
                    change.putExtra("id", id);
                    startActivity(change);
                } else if (position == 5) {
                    //Terms
                    Intent termsAndCondition = new Intent(AccountActivity.this, PrivacyPolicy.class);
                    startActivity(termsAndCondition);
                } else if (position == 6) {
                    logout();
                }
            }

        });
    }

    private AlertDialog alert;

    private void logout() {
        alert = new AlertDialog.Builder(AccountActivity.this).create();
        alert.setTitle("Message");
        alert.setMessage("Are you sure you want to Logout?");
        alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ConnectionDetector isInternetOn = new ConnectionDetector(AccountActivity.this);
                if (isInternetOn.isConnectingToInternet())
                    new AccountActivity.BackGroundProcessTab().execute();
                else {
                    Toast.makeText(getApplicationContext(), "No Internet connection Try again Later!", Toast.LENGTH_LONG).show();
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

    private class BackGroundProcessTab extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(AccountActivity.this);
            progress.setMessage("Loading...");
            progress.setCancelable(false);
            progress.setIndeterminate(true);
            progress.show();
        }

        protected Void doInBackground(Void... params) {
            JSONObject sendData = new JSONObject();
            try {
                sendData.put("UserId", mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.ID));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            logoutReceivedJsonObject = mServices.LogOut(sendData);
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            SharedPreferences sharedpreferences = getSharedPreferences(AppConstant.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.commit();
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().clear().commit();
            progress.dismiss();
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            intent.putExtra("from logout", "logout");
            startActivity(intent);
            LoginManager.getInstance().logOut();
            mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.FACE_BOOK_ID, null);
            finish();
        }

    }
}
