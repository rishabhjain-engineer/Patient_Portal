package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.askerov.dynamicgrid.BaseDynamicGridAdapter;

import java.util.List;

import com.hs.userportal.R;

/**
 * Created by ayaz on 29/3/17.
 */

public class DashboardActivityAdapter extends BaseDynamicGridAdapter {
    public DashboardActivityAdapter(Context context, List<String> items, int columnCount) {
        super(context, items, columnCount);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CountryListViewHolder holder;
        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.dashboard_grid_view, null);
            holder = new CountryListViewHolder(convertView);
            convertView.setTag(holder);

        } else {

            holder = (CountryListViewHolder) convertView.getTag();
        }

        holder.titleText.setText(getItem(position).toString());
        holder.image.setImageResource(R.drawable.ic_launcher);
        return convertView;
    }

    private class CountryListViewHolder {
        private TextView titleText;
        private ImageView image;

        private CountryListViewHolder(View view) {
            titleText = (TextView) view.findViewById(R.id.title);
            image = (ImageView) view.findViewById(R.id.image);
        }

    }
}
