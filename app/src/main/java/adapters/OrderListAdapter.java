package adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hs.userportal.R;

/**
 * Created by rishabh on 20/6/17.
 */

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.MyViewHolder> {

    private String testname ;
    private OrderListTouched listener ;

    public OrderListAdapter(OrderListTouched listener , String testname) {
        this.testname = testname ;
        this.listener = listener ;
    }

    @Override
    public OrderListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderlist,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(OrderListAdapter.MyViewHolder holder, int position) {

        holder.tvOrderTestName.setText(testname);
        holder.llRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.orderListTouched();
            }
        });
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView tvOrderTestName ;
        private LinearLayout llRow ;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvOrderTestName = (TextView) itemView.findViewById(R.id.orderlist_tv);
            llRow = (LinearLayout) itemView.findViewById(R.id.row);
        }
    }

    public interface OrderListTouched {
        public void orderListTouched() ;
    }
}
