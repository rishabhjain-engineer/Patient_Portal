package adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hs.userportal.R;

import java.util.ArrayList;
import java.util.List;

import ui.DashBoardActivity;

/**
 * Created by ayaz on 29/3/17.
 */

public class DashboardActivityAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<String> mList = new ArrayList<>();
    private List<Integer> mImageList = new ArrayList<>();

    public DashboardActivityAdapter(Activity dashBoardActivity) {
        mActivity = dashBoardActivity;

        mList.add("Health Card");
        mList.add("Vitals");
        mList.add("Discounts");
        mList.add("Health Coach");
        mList.add("Alerts");
        mList.add("");

        mImageList.add(R.drawable.health_card);
        mImageList.add(R.drawable.vitals);
        mImageList.add(R.drawable.discounts_144);
        mImageList.add(R.drawable.coach_144);
        mImageList.add(R.drawable.alerts_144);
        mImageList.add(R.drawable.whitebackground);
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        TextView name;
        ImageView image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.dashboard_grid_view, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.image_text_view);
            holder.image = (ImageView) convertView.findViewById(R.id.image_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(mList.get(position));
        holder.image.setImageResource(mImageList.get(position));
        return convertView;
    }
}
