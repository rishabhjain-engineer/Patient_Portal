package ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;

import com.hs.userportal.R;

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
        webView.loadUrl(path);

    }
}
