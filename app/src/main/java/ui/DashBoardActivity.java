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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mDaDynamicGridView = (DynamicGridView) findViewById(R.id.gridview);

        mList = getList_item();

        mDaDynamicGridView.setAdapter(new DashboardActivityAdapter(this, mList, 2));

        mDaDynamicGridView.setOnDragListener(new DynamicGridView.OnDragListener() {
            @Override
            public void onDragStarted(int position) {
                Log.e("position", "drag started at position " + position);
            }

            @Override
            public void onDragPositionsChanged(int oldPosition, int newPosition) {
                Log.d("drag position", String.format("drag item position changed from %d to %d", oldPosition, newPosition));
            }
        });

        mDaDynamicGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mDaDynamicGridView.startEditMode(position);
                return true;
            }
        });

        mDaDynamicGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(DashBoardActivity.this, parent.getAdapter().getItem(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public List<String> getList_item(){
        mList.add("New Delhi");
        mList.add("Maharashtra");
        mList.add("West Bengal");
        mList.add("Madhya Pradesh");
        mList.add("Bihar");
        mList.add("Uttar Pradesh");
        mList.add("Punjab");
        mList.add("Assam");
        mList.add("Goa");
        mList.add("Gujrat");
        mList.add("Kerala");
        mList.add("Rajasthan");

        return mList;
    }

    @Override
    public void onBackPressed() {
        if (mDaDynamicGridView.isEditMode()) {
            mDaDynamicGridView.stopEditMode();
        } else {
            super.onBackPressed();
        }
    }
}
