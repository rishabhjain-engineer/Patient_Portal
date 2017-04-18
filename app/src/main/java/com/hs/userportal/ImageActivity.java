package com.hs.userportal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import networkmngr.NetworkChangeListener;

/**
 * Created by rishabh on 6/4/17.
 */

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        ImageView imageView = (ImageView) findViewById(R.id.image);

        if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
            Toast.makeText(this, "No internet connection. Please retry", Toast.LENGTH_SHORT).show();
        } else {
            String url = getIntent().getStringExtra("ImagePath");
            Glide.with(this)
                    .load(url)
                    .crossFade()
                    .into(imageView);

        }
    }

}
