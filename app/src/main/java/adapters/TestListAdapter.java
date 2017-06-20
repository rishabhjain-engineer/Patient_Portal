package adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hs.userportal.R;
import com.hs.userportal.TestNames;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rishabh on 16/6/17.
 */

public class TestListAdapter extends RecyclerView.Adapter<TestListAdapter.MyViewHolder>{

  //private String testName ;
    private List<TestNames> testname ;
    private String caseID ;
    private OnRowTouchAction listener ;

    public TestListAdapter(List<TestNames> testname , OnRowTouchAction listener , String caseid) {
        this.testname = testname ;
        this.listener = listener ;
        caseID = caseid ;
    }

    @Override
    public TestListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.testlist_single_row,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view) ;
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(TestListAdapter.MyViewHolder holder, final int position) {

        holder.tvTestName.setText(testname.get(position).getDescription());
        if(("Green".equalsIgnoreCase(testname.get(position).getColor()))){
            holder.ivColor.setImageResource(R.drawable.green_arrow);
        }else if("Blue".equalsIgnoreCase(testname.get(position).getColor())) {
            holder.ivColor.setImageResource(0);
            holder.ivColor.setImageResource(R.drawable.blue_arrow);

        }else {
            holder.ivColor.setImageResource(0);
            holder.ivColor.setImageResource(R.drawable.pink_arrow);
        }

        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.onTestNameTouched(caseID , position, testname.get(position).getColor());

            }
        });

        int size = position+1 ;
        if(size == testname.size()) {
            holder.separator.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return testname.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTestName ;
        private ImageView ivColor ;
        private LinearLayout row ;
        private View separator;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvTestName = (TextView) itemView.findViewById(R.id.testname);
            ivColor = (ImageView) itemView.findViewById(R.id.color_imageview);
            row = (LinearLayout) itemView.findViewById(R.id.testnames_row);
            separator = itemView.findViewById(R.id.seprator) ;
        }
    }

    public interface OnRowTouchAction {

        public void onTestNameTouched(String caseID , int positionOfTestNameList, String color) ;
    }

}
