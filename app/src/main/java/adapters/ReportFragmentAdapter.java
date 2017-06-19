package adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hs.userportal.CaseCodeModel;
import com.hs.userportal.R;
import com.hs.userportal.TestNames;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rishabh on 16/6/17.
 */

public class ReportFragmentAdapter extends RecyclerView.Adapter<ReportFragmentAdapter.MyViewHolder> {

    private List<CaseCodeModel> adapterCaseCodeObjectList = new ArrayList<>();
    private Context context;
    private TestListAdapter testListAdapter;
    private RecyclerView recyclerViewTestList;
    private RecyclerView.LayoutManager layoutManager;
    private List<TestNames> testNamesArrayList = new ArrayList<>();
    private ArrayList<String> nameOfTestList = new ArrayList<>();
    private TestListAdapter.OnRowTouchAction listener ;


    public ReportFragmentAdapter(Context context, List<CaseCodeModel> list, TestListAdapter.OnRowTouchAction listener) {

        this.context = context;
        this.listener = listener ;
        adapterCaseCodeObjectList = list;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reportfragment_single_row, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        nameOfTestList.clear();

        layoutManager = new LinearLayoutManager(context);

        holder.tvLocationName.setText(adapterCaseCodeObjectList.get(position).getLocationName());
        holder.tvAdviceDate.setText(adapterCaseCodeObjectList.get(position).getDateandTime());
        holder.tvReferredBy.setText(adapterCaseCodeObjectList.get(position).getReferrerName());
        holder.tvCaseCode.setText(adapterCaseCodeObjectList.get(position).getCaseCode());

        holder.lvTestList.setHasFixedSize(true);
        holder.lvTestList.setLayoutManager(layoutManager);

        testNamesArrayList = adapterCaseCodeObjectList.get(position).getListOfTestNamesInCaseCode();

       /* for (int i = 0; i < testNamesArrayList.size(); i++) {
            nameOfTestList.add(testNamesArrayList.get(i).getDescription());
        }*/

        testListAdapter = new TestListAdapter(testNamesArrayList , listener , adapterCaseCodeObjectList.get(position).getCaseID() );
        holder.lvTestList.setAdapter(testListAdapter);



    }

    @Override
    public int getItemCount() {
        return adapterCaseCodeObjectList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvLocationName;
        private TextView tvAdviceDate;
        private TextView tvReferredBy;
        private TextView tvCaseCode;
        private RecyclerView lvTestList;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvLocationName = (TextView) itemView.findViewById(R.id.application_name_tv);
            tvAdviceDate = (TextView) itemView.findViewById(R.id.advice_date_value_tv);
            tvReferredBy = (TextView) itemView.findViewById(R.id.referred_by_value_tv);
            tvCaseCode = (TextView) itemView.findViewById(R.id.casecode_value_tv);
            lvTestList = (RecyclerView) itemView.findViewById(R.id.testnames_recyler_view);


        }
    }
}
