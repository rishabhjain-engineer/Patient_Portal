package adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hs.userportal.OrderDetailsModel;
import com.hs.userportal.OrderTestNames;
import com.hs.userportal.R;

import java.util.List;

/**
 * Created by rishabh on 20/6/17.
 */

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.MyViewHolder> {

    private List<OrderTestNames> orderTestNames;
    private OrderListTouched listener ;
    private OrderDetailsModel orderDetailsModelObject ;

    public OrderListAdapter(OrderDetailsModel object, OrderListTouched listener , List<OrderTestNames> orderTestNames) {
    this. orderTestNames = orderTestNames ;
        this.listener = listener ;
        orderDetailsModelObject = object ;
    }

    @Override
    public OrderListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderlist,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(OrderListAdapter.MyViewHolder holder, int position) {

        holder.tvOrderTestName.setText(orderTestNames.get(position).getOrderTestNames());
        holder.llRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.orderListTouched(orderDetailsModelObject);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderTestNames.size();
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
        public void orderListTouched(OrderDetailsModel object) ;
    }
}
