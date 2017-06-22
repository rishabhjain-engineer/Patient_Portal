package adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.applozic.audiovideo.activity.VideoActivity;
import com.hs.userportal.R;

import java.util.ArrayList;
import java.util.List;

import ui.DashBoardActivity;
import utils.AppConstant;
import utils.PreferenceHelper;

/**
 * Created by ayaz on 29/3/17.
 */

public class DashboardActivityAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<String> mList = new ArrayList<>();
    private List<Integer> mImageList = new ArrayList<>();

    public DashboardActivityAdapter(Activity dashBoardActivity) {
        mActivity = dashBoardActivity;

        if (VideoActivity.isPatient) {
            mList.add("Consults");
            mList.add("Reports");
            mList.add("Vitals");
            mList.add("Repository");
            mList.add("Family");

            mImageList.add(R.drawable.ic);
            mImageList.add(R.drawable.homepage_reports);
            mImageList.add(R.drawable.homepage_vital_green);
            mImageList.add(R.drawable.homepage_repository);
            mImageList.add(R.drawable.homepage_family);

            PreferenceHelper preferenceHelper = PreferenceHelper.getInstance();
            if ("3".equalsIgnoreCase(preferenceHelper.getString(PreferenceHelper.PreferenceKey.PATIENT_BUSINESS_FLAG))) {
                mList.add("School");
                mImageList.add(R.drawable.school_icon);
            }
        } else {
            mList.add("Consults");
            mImageList.add(R.drawable.ic);
        }
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
        if (position == 1) {
            if (isShowGreenVitalsImage) {
                holder.image.setImageResource(mImageList.get(position));
            } else {
                holder.image.setImageResource(R.drawable.homepage_vital_red);
            }
        } else {
            holder.image.setImageResource(mImageList.get(position));
        }


        return convertView;
    }

    private boolean isShowGreenVitalsImage;

    public void setFlagForImage(boolean value) {
        isShowGreenVitalsImage = value;
    }
}
