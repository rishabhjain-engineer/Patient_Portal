package com.applozic.audiovideo.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import applozic.com.audiovideo.R;

/**
 * Created by rishabh on 23/6/17.
 */

public class OpenImageActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_image_open);
        ImageView imageView = (ImageView) findViewById(R.id.image);

            String url = getIntent().getStringExtra("ImagePath");
            Glide.with(this)
                    .load(url)
                    .crossFade()
                    .into(imageView);


    }
}
