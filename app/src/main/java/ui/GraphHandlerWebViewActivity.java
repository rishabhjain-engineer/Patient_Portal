package ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import com.hs.userportal.MainActivity;
import com.hs.userportal.R;
import com.hs.userportal.SampleCirclesDefault;
import com.hs.userportal.logout;

/**
 * Created by ayaz on 7/4/17.
 */

public class GraphHandlerWebViewActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_handler);
        setupActionBar();
        mActionBar.hide();
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        WebView webView = (WebView) findViewById(R.id.graph_handler_web_view);
        WebSettings settings = webView.getSettings();
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(true);
        settings.setSupportZoom(true);
        settings.setUserAgentString("Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19");
        webView.setInitialScale(1);
        webView.loadUrl(path);
        final ProgressDialog progress = new ProgressDialog(GraphHandlerWebViewActivity.this);
        progress.setCancelable(false);
        progress.setMessage("Loading...");
        progress.setIndeterminate(true);
        progress.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                 progress.dismiss();
            }
        }, 500);

        Button button = (Button) findViewById(R.id.close);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }
}
