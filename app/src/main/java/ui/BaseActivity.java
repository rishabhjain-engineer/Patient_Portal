package ui;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.hs.userportal.R;
import com.hs.userportal.TabsActivity;
import com.hs.userportal.update;

/**
 * Created by android1 on 19/1/17.
 */

public class BaseActivity extends AppCompatActivity {

    protected ActionBar mActionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupActionBar() {
        mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1da17f")));
        mActionBar.setIcon(new ColorDrawable(Color.parseColor("#3cbed8")));
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Hides the soft on screen keyboard.
     *
     * @return True : If request was executed successfully. It may return false if it fails in attempt
     * to hide keyboard.
     */
    public boolean hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return hideSoftKeyboard();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showUnsavedAlertDialog(String message, String cancelText, String okText) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.unsaved_alert_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView messageTv = (TextView) dialog.findViewById(R.id.message);
        Button okBTN = (Button) dialog.findViewById(R.id.btn_ok);
        Button stayButton = (Button) dialog.findViewById(R.id.stay_btn);
        messageTv.setText(message);
        if (TextUtils.isEmpty(cancelText)) {
            stayButton.setVisibility(View.GONE);
        } else {
            stayButton.setVisibility(View.VISIBLE);
            stayButton.setText(cancelText);
            stayButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        if (TextUtils.isEmpty(okText)) {
            okBTN.setVisibility(View.GONE);
        } else {
            okBTN.setVisibility(View.VISIBLE);
            okBTN.setText(okText);
            okBTN.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    update.Imguri = null;
                    finish();
                }
            });
        }
        dialog.show();
    }

}
