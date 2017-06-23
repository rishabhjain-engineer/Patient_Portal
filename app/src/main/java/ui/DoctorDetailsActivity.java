package ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
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
import utils.AppConstant;

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

        ImageView backImage = (ImageView) findViewById(R.id.back_image);
        TextView headerTitleTv = (TextView) findViewById(R.id.header_title_tv);
        headerTitleTv.setText("Doctor Details");
        backImage.setOnClickListener(mOnClickListener);

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


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (R.id.audio_call == id) {
                if (ConversationActivity.isPatient) {
                    decesionAlertDialog("audio");
                } else {
                    audioCall();
                }
            } else if (R.id.video_call == id) {
                if (ConversationActivity.isPatient) {
                    decesionAlertDialog("video");
                } else {
                    videoCall();
                }
            } else if (R.id.chat == id) {
                if (ConversationActivity.isPatient) {
                    decesionAlertDialog("chat");
                } else {
                    chat();
                }
            } else if (R.id.back_image == id) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        }
    };

    private void decesionAlertDialog(final String string) {
        final Dialog dialog = new Dialog(DoctorDetailsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.unsaved_alert_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);

        TextView message = (TextView) dialog.findViewById(R.id.message);
        message.setText("Do you want to provide additional information ? ");
        dialog.setCanceledOnTouchOutside(true);
        TextView okBTN = (TextView) dialog.findViewById(R.id.btn_ok);
        okBTN.setText("Later");
        TextView stayButton = (TextView) dialog.findViewById(R.id.stay_btn);
        stayButton.setText("Yes");

        okBTN.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (string.equalsIgnoreCase("audio")) {
                    audioCall();
                    dialog.dismiss();
                } else if (string.equalsIgnoreCase("video")) {
                    videoCall();
                    dialog.dismiss();
                } else if (string.equalsIgnoreCase("chat")) {
                    chat();
                    dialog.dismiss();
                }
                /*Intent intent = null;
                if (AppConstant.isPatient) {
                    intent = new Intent(DoctorDetailsActivity.this, PastVisitActivity.class);
                } else {
                    intent = new Intent(DoctorDetailsActivity.this, DoctorPrescriptionActivity.class);
                }
                intent.putExtra("chatType", string);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                dialog.dismiss();*/
            }
        });
        stayButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorDetailsActivity.this, SymptomsActivity.class);
                intent.putExtra("chatType", string);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void videoCall() {
        Intent videoCallIntent = new Intent(DoctorDetailsActivity.this, VideoActivity.class);
        videoCallIntent.putExtra("CONTACT_ID", AppConstant.getPatientID());
        startActivity(videoCallIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    private void audioCall() {
        Intent audioCallIntent = new Intent(DoctorDetailsActivity.this, AudioCallActivityV2.class);
        audioCallIntent.putExtra("CONTACT_ID", AppConstant.getPatientID());
        startActivity(audioCallIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    private void chat() {
        Intent intent = new Intent(DoctorDetailsActivity.this, ConversationActivity.class);
        intent.putExtra(ConversationUIService.USER_ID, AppConstant.getPatientID());
        intent.putExtra(ConversationUIService.DISPLAY_NAME, AppConstant.getpatienDoctorName()); //put it for displaying the title.
        intent.putExtra(ConversationUIService.TAKE_ORDER, true); //Skip chat list for showing on back press
        startActivity(intent);
    }
}
