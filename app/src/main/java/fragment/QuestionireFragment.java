package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hs.userportal.R;
import com.hs.userportal.WalthroughFragment;

/**
 * Created by ayaz on 27/1/17.
 */

public class QuestionireFragment extends Fragment {

    public static QuestionireFragment newInstance() {
        QuestionireFragment fragment = new QuestionireFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_questionire, container, false);
        return view;
    }
}
