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

import config.QuestionireParser;

/**
 * Created by ayaz on 27/1/17.
 */

public class QuesetionireReprtFragmentAdapter extends BaseAdapter {

    private Context mContext;
    List<QuestionireParser.QuestionDetail> mQuestionList = new ArrayList<QuestionireParser.QuestionDetail>();

    public QuesetionireReprtFragmentAdapter(Context context) {
        mContext = context;
        mQuestionList = QuestionireParser.getQuestionDetailListStatus0();
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
        TextView questionTextview;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.fragmnet_questionire_view, parent, false);
            holder = new ViewHolder();
            holder.questionTextview = (TextView) convertView.findViewById(R.id.report_fragmment_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.questionTextview.setText(mQuestionList.get(position).getQuestion());
        return convertView;
    }

}
