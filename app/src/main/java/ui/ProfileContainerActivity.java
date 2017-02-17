package ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.hs.userportal.Profile;
import com.hs.userportal.R;
import com.hs.userportal.Work;
import com.hs.userportal.residence;
import com.hs.userportal.update;

/**
 * Created by ayaz on 17/2/17.
 */

public class ProfileContainerActivity extends BaseActivity {

    private String mID, pass, pic, picname, fbLinked, fbLinkedID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_container);
        setupActionBar();
        mActionBar.setTitle("Profile");

        Intent i = getIntent();
        mID = i.getStringExtra("id");
        pass = i.getStringExtra("pass");
        pic = i.getStringExtra("pic");
        picname = i.getStringExtra("picname");
        fbLinked = i.getStringExtra("fbLinked");
        fbLinkedID = i.getStringExtra("fbLinkedID");

        LinearLayout basicContainerLL = (LinearLayout) findViewById(R.id.basic_container);
        LinearLayout residenceContainer = (LinearLayout) findViewById(R.id.residence_container);
        LinearLayout workContainer = (LinearLayout) findViewById(R.id.work_container);

        basicContainerLL.setOnClickListener(mClickListener);
        residenceContainer.setOnClickListener(mClickListener);
        workContainer.setOnClickListener(mClickListener);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.basic_container) {
                Intent intent = new Intent(ProfileContainerActivity.this, update.class);
                Log.i("ayaz", "ID: "+mID);
                intent.putExtra("id", mID);
                intent.putExtra("pass", pass);
                intent.putExtra("pic", pic);
                intent.putExtra("picname", picname);
                intent.putExtra("fbLinked", fbLinked);
                intent.putExtra("fbLinkedID", fbLinkedID);
                startActivity(intent);

            } else if (id == R.id.residence_container) {
                Intent intent = new Intent(ProfileContainerActivity.this, residence.class);
                Log.i("ayaz", "ID: "+mID);
                intent.putExtra("id", mID);
              /*  intent.putExtra("pass", pass);
                intent.putExtra("pic", pic);
                intent.putExtra("picname", picname);
                intent.putExtra("fbLinked", fbLinked);
                intent.putExtra("fbLinkedID", fbLinkedID);*/
                startActivity(intent);

            } else if (id == R.id.work_container) {
                Intent intent = new Intent(ProfileContainerActivity.this, Work.class);
                Log.i("ayaz", "ID: "+mID);
                intent.putExtra("id", mID);
               /* intent.putExtra("pass", pass);
                intent.putExtra("pic", pic);
                intent.putExtra("picname", picname);
                intent.putExtra("fbLinked", fbLinked);
                intent.putExtra("fbLinkedID", fbLinkedID);*/
                startActivity(intent);

            }
        }
    };
}
