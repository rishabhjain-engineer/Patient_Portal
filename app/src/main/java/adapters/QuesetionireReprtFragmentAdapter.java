package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.hs.userportal.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ayaz on 27/1/17.
 */

public class QuesetionireReprtFragmentAdapter extends BaseAdapter {

    private Context mContext;
    List<String> mQuestionList = new ArrayList<String>();

    public QuesetionireReprtFragmentAdapter(Context context, List<String> countryList) {
        mContext = context;
        mQuestionList = countryList;
    }

    @Override
    public int getCount() {
        return mQuestionList.size();
    }

    @Override
    public Object getItem(int position) {
        return mQuestionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        CheckBox mCheckBox;
        TextView questionTextview;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.fragmnet_questionire_view, parent, false);
            holder = new ViewHolder();
            holder.mCheckBox = (CheckBox) convertView.findViewById(R.id.report_fragmment_check_box);
            holder.questionTextview = (TextView) convertView.findViewById(R.id.report_fragmment_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.questionTextview.setText(mQuestionList.get(position));
        return convertView;
    }

}
