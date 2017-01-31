package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hs.userportal.R;

import java.util.ArrayList;
import java.util.List;

import adapters.QuesetionireReprtFragmentAdapter;

/**
 * Created by Ayaz on 27/1/17.
 */

public class QuestionireReportFragment extends Fragment {
    private QuesetionireReprtFragmentAdapter mQuesetionireReprtFragmentAdapter;

    public static QuestionireReportFragment newInstance() {
        QuestionireReportFragment fragment = new QuestionireReportFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_questionire_report, container, false);
        ListView listView = (ListView) view.findViewById(R.id.questionire_report_list_view);
        mQuesetionireReprtFragmentAdapter = new QuesetionireReprtFragmentAdapter(getActivity());
        listView.setAdapter(mQuesetionireReprtFragmentAdapter);
        return view;
    }
}