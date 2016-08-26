package utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.hs.userportal.R;

import java.text.DecimalFormat;

/**
 * Created by ashish on 25-Aug-16.
 */
public class MyMarkerView extends MarkerView {

    private TextView tvContent;
    private Context mcontext;

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        this.mcontext = context;
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;
            tvContent.setText("" + ce.getHigh()/*Utils.formatNumber(ce.getHigh(), 0, true)*/);
        } else {

            tvContent.setText("" + e.getY() /*Utils.formatNumber(e.getY(), 0, true)*/);
        }
    }

    @Override
    public int getXOffset(float xpos) {
        // this will center the marker-view horizontally
        return  -(getWidth() /2);
        // this will center the marker-view horizontally
        /*int min_offset = 50;
        if (xpos < min_offset)
            return 0;

        WindowManager wm = (WindowManager) mcontext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        //For right hand side
        if (metrics.widthPixels - xpos < min_offset)
            return -getWidth();
            //For left hand side
        else if (metrics.widthPixels - xpos < 0)
            return -getWidth();
        return -(getWidth() / 2);*/
    }

    @Override
    public int getYOffset(float ypos) {
        // this will cause the marker-view to be above the selected value
        return -getHeight();
    }
}
