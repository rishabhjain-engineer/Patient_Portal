package adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hs.userportal.R;

/**
 * Created by rishabh on 20/6/17.
 */

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.MyViewHolder> {

    private String testname ;

    public OrderListAdapter(String testname) {
        this.testname = testname ;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderlist,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tvOrderTestName.setText(testname);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView tvOrderTestName ;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvOrderTestName = (TextView) itemView.findViewById(R.id.orderlist_tv);
        }
    }
}
