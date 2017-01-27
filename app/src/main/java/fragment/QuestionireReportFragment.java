package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hs.userportal.R;

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
        mQuesetionireReprtFragmentAdapter = new QuesetionireReprtFragmentAdapter(getActivity());
        return view;
    }
}