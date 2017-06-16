package adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hs.userportal.R;

import java.util.ArrayList;

/**
 * Created by rishabh on 16/6/17.
 */

public class TestListAdapter extends RecyclerView.Adapter<TestListAdapter.MyViewHolder>{

  //private String testName ;
    private ArrayList<String> testname ;

    public TestListAdapter(ArrayList<String> testname) {
        this.testname = testname ;
    }

    @Override
    public TestListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.testlist_single_row,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view) ;
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(TestListAdapter.MyViewHolder holder, int position) {

        holder.tvTestName.setText(testname.get(position));

    }

    @Override
    public int getItemCount() {
        return testname.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTestName ;
        public MyViewHolder(View itemView) {
            super(itemView);

            tvTestName = (TextView) itemView.findViewById(R.id.testname);
        }
    }

}
