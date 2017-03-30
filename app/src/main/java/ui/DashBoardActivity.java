package ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.hs.userportal.R;

import org.askerov.dynamicgrid.DynamicGridView;

import java.util.ArrayList;
import java.util.List;

import adapters.DashboardActivityAdapter;

/**
 * Created by ayaz on 29/3/17.
 */

public class DashBoardActivity extends Activity {
    private DynamicGridView mDaDynamicGridView;
    private List<String> mList = new ArrayList<>();
    private String privatery_id;

    public static String image_parse;
    public static String emailid;
    public static String id;
    public static String notiem = "no", notisms = "no";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }
}
