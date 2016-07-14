package com.hs.userportal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class WalthroughFragment extends Fragment {

	public static final String POSITION_KEY = "FragmentPositionKey";
	private int position;
	LinearLayout walk1, walk2,/* walk3,*/ walk4;
	RelativeLayout walk0;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);

	}

	public static WalthroughFragment newInstance(Bundle args) {
		WalthroughFragment fragment = new WalthroughFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		position = getArguments().getInt(POSITION_KEY);
		View view = inflater.inflate(R.layout.walk, container, false);
		
		walk0 = (RelativeLayout) view.findViewById(R.id.walk0);
		walk1 = (LinearLayout) view.findViewById(R.id.walk1);
		walk2 = (LinearLayout) view.findViewById(R.id.walk2);
		//walk3 = (LinearLayout) view.findViewById(R.id.walk3);
        walk4 = (LinearLayout) view.findViewById(R.id.walk4);

		walk0.setVisibility(View.GONE);
		walk1.setVisibility(View.GONE);
		walk2.setVisibility(View.GONE);
	//	walk3.setVisibility(View.GONE);
        walk4.setVisibility(View.GONE);

		if (position == 0) {
			// view = inflater.inflate(R.layout.walk, container, false);
			walk0.setVisibility(View.VISIBLE);
			walk1.setVisibility(View.GONE);
			walk2.setVisibility(View.GONE);
		//	walk3.setVisibility(View.GONE);
            walk4.setVisibility(View.GONE);
		}else if (position == 1) {
			// view = inflater.inflate(R.layout.walk, container, false);
			walk1.setVisibility(View.VISIBLE);
			walk0.setVisibility(View.GONE);
			walk2.setVisibility(View.GONE);
		//	walk3.setVisibility(View.GONE);
            walk4.setVisibility(View.GONE);
		} else if (position == 2) {
			// view = inflater.inflate(R.layout.walk, container, false);
			walk2.setVisibility(View.VISIBLE);
			walk0.setVisibility(View.GONE);
			walk1.setVisibility(View.GONE);
		//	walk3.setVisibility(View.GONE);
            walk4.setVisibility(View.GONE);
		} else if (position == 3) {
			// view = inflater.inflate(R.layout.walk, container, false);
			walk4.setVisibility(View.VISIBLE);
			walk1.setVisibility(View.GONE);
			walk2.setVisibility(View.GONE);
			//walk3.setVisibility(View.GONE);
			walk0.setVisibility(View.GONE);
		}/*else if(position == 4){
            walk4.setVisibility(View.VISIBLE);
            walk1.setVisibility(View.GONE);
            walk2.setVisibility(View.GONE);
            walk3.setVisibility(View.GONE);
            walk0.setVisibility(View.GONE);
        }*/
		return view;

	}
}