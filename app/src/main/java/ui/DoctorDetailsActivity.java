package ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.applozic.audiovideo.activity.AudioCallActivityV2;
import com.applozic.audiovideo.activity.VideoActivity;
import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.hs.userportal.R;

import models.DoctorDetails;

/**
 * Created by ayaz on 5/6/17.
 */

public class DoctorDetailsActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_doctor_detail);
        setupActionBar();
        mActionBar.hide();

        TextView doctorName = (TextView) findViewById(R.id.doctor_name);
        TextView doctorLocation = (TextView) findViewById(R.id.city);
        TextView doctorMedicineType = (TextView) findViewById(R.id.medicine_type);
        ImageView doctorPic = (ImageView) findViewById(R.id.doctor_image_view);

        TextView about = (TextView) findViewById(R.id.about);

        Button audioCall = (Button) findViewById(R.id.audio_call);
        Button videoCall = (Button) findViewById(R.id.video_call);
        Button chat = (Button) findViewById(R.id.chat);
        audioCall.setOnClickListener(mOnClickListener);
        videoCall.setOnClickListener(mOnClickListener);
        chat.setOnClickListener(mOnClickListener);


        Intent intent = getIntent();
        DoctorDetails doctorDetails = (DoctorDetails) intent.getSerializableExtra("doctorDetail");
        doctorName.setText(doctorDetails.getDoctorName());
        doctorLocation.setText(doctorDetails.getLocation());
        doctorMedicineType.setText(doctorDetails.getMedicineType());
        doctorPic.setImageResource(doctorDetails.getDoctorImage());
        about.setText(doctorDetails.getAboutDoctor());

    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (R.id.audio_call == id) {
                Intent audioCallIntent = new Intent(DoctorDetailsActivity.this, AudioCallActivityV2.class);
                audioCallIntent.putExtra("CONTACT_ID", "reciverUserId");
                startActivity(audioCallIntent);


            } else if (R.id.video_call == id) {

                Intent videoCallIntent = new Intent(DoctorDetailsActivity.this, VideoActivity.class);
                videoCallIntent.putExtra("CONTACT_ID", "reciverUserId");
                startActivity(videoCallIntent);
            } else if (R.id.chat == id) {
                Intent intent = new Intent(DoctorDetailsActivity.this, ConversationActivity.class);
                if (ApplozicClient.getInstance(DoctorDetailsActivity.this).isContextBasedChat()) {
                    intent.putExtra(ConversationUIService.CONTEXT_BASED_CHAT, true);
                }
                startActivity(intent);
            }
        }
    };
}
