package adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.cloudchowk.patient.MyVolleySingleton;
import com.cloudchowk.patient.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ashish on 4/25/2016.
 */
public class Order_family_adapter extends BaseAdapter {
    Activity activity;
    ArrayList<HashMap<String, String>> family_member;
    private LayoutInflater inflater;
    private ImageLoader mImageLoader;


    public Order_family_adapter(Activity activity, ArrayList<HashMap<String, String>> family_member, String image_parse) {
        this.activity = activity;
        this.family_member = family_member;
        mImageLoader = MyVolleySingleton.getInstance(activity).getImageLoader();
    }

    @Override
    public int getCount() {
        return family_member.size();
    }

    @Override
    public Object getItem(int location) {
        return family_member.get(location);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.order_family, null);
        NetworkImageView image = (NetworkImageView) convertView.findViewById(R.id.icon);
        TextView folder_name = (TextView) convertView.findViewById(R.id.name);
        folder_name.setText(family_member.get(position).get("FirstName")+" "+ family_member.get(position).get("LastName"));
        image.setDefaultImageResId(R.drawable.dashpic_update);
        image.setAdjustViewBounds(true);
        image.setImageUrl("https://files.cloudchowk.com/" + family_member.get(position)
                .get("Image"), mImageLoader);
        return convertView;
    }
}
