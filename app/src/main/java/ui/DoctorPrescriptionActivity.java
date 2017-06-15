package ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.applozic.audiovideo.activity.AudioCallActivityV2;
import com.applozic.audiovideo.activity.VideoActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.hs.userportal.R;

/**
 * Created by ayaz on 15/6/17.
 */

public class DoctorPrescriptionActivity extends BaseActivity {
    private String mCoversationType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_prescription);
        setupActionBar();
        mActionBar.hide();
        mCoversationType = getIntent().getStringExtra("chatType");
        ImageView backImage = (ImageView) findViewById(R.id.back_image);
        TextView headerTitleTv = (TextView) findViewById(R.id.header_title_tv);
        headerTitleTv.setText("Doctor Prescription");
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Intent intent = null;
        if (mCoversationType.equalsIgnoreCase("audio")) {
            intent = new Intent(DoctorPrescriptionActivity.this, AudioCallActivityV2.class);
            intent.putExtra("CONTACT_ID", "be2ce808-6250-4874-a239-31d60d1d8567");
        } else if (mCoversationType.equalsIgnoreCase("video")) {
            intent = new Intent(DoctorPrescriptionActivity.this, VideoActivity.class);
            intent.putExtra("CONTACT_ID", "be2ce808-6250-4874-a239-31d60d1d8567");
        } else if (mCoversationType.equalsIgnoreCase("chat")) {
            intent = new Intent(DoctorPrescriptionActivity.this, ConversationActivity.class);
            intent.putExtra("CONTACT_ID", "be2ce808-6250-4874-a239-31d60d1d8567");
            intent.putExtra(ConversationUIService.DISPLAY_NAME, "shalini"); //put it for displaying the title.
            intent.putExtra(ConversationUIService.TAKE_ORDER, true); //Skip chat list for showing on back press
        }
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
