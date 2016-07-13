package com.cloudchowk.patient;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TourFragment extends Fragment {

	public static final String POSITION_KEY = "FragmentPositionKey";
	private int position;
	LinearLayout tour1, tour2, tour3, tour4, tour0, tour5, tour0_1;
	ImageView image;
	TextView welcome, skip_scr1, skip_scr2, skip_scr3;
	Button tour, exit, bTour_Zureka, bTour_Continue;
	RelativeLayout buttonbar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	public static TourFragment newInstance(Bundle args) {
		TourFragment fragment = new TourFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		position = getArguments().getInt(POSITION_KEY);
		View view = inflater.inflate(R.layout.tour, container, false);

		//buttonbar = (RelativeLayout) view.findViewById(R.id.buttonbar);
		welcome = (TextView) view.findViewById(R.id.tvWelcome);
		skip_scr1 = (TextView) view.findViewById(R.id.skip_imgview0);
		skip_scr2 = (TextView) view.findViewById(R.id.skip);
		skip_scr3 = (TextView) view.findViewById(R.id.skip_imgview1);
		image = (ImageView) view.findViewById(R.id.imageView0);

		tour0 = (LinearLayout) view.findViewById(R.id.tour0);
		tour1 = (LinearLayout) view.findViewById(R.id.tour1);
		/*
		 * tour2 = (LinearLayout) view.findViewById(R.id.tour2); tour3 =
		 * (LinearLayout) view.findViewById(R.id.tour3); tour4 = (LinearLayout)
		 * view.findViewById(R.id.tour4); tour5 = (LinearLayout)
		 * view.findViewById(R.id.tour5);
		 */
		tour0_1 = (LinearLayout) view.findViewById(R.id.tour0_1);

		tour0.setVisibility(View.GONE);
		tour1.setVisibility(View.GONE);
		/*
		 * tour2.setVisibility(View.GONE); tour3.setVisibility(View.GONE);
		 * tour4.setVisibility(View.GONE); tour5.setVisibility(View.GONE);
		 */
		tour0_1.setVisibility(view.GONE);

		if (position == 0) {
			// view = inflater.inflate(R.layout.tour, container, false);
			tour0.setVisibility(View.VISIBLE);
			welcome.setText("Welcome, " + SampleCirclesDefault.name);
		} else if (position == 1) {
			// view = inflater.inflate(R.layout.tour, container, false);
			tour1.setVisibility(View.VISIBLE);
		} else if (position == 2) {
			tour0_1.setVisibility(View.VISIBLE);
		} /*
			 * else if (position == 3) { // view =
			 * inflater.inflate(R.layout.tour, container, false);
			 * tour2.setVisibility(View.VISIBLE); } else if (position == 4) { //
			 * view = inflater.inflate(R.layout.tour, container, false);
			 * tour3.setVisibility(View.VISIBLE); } else if (position == 5) { //
			 * view = inflater.inflate(R.layout.tour, container, false);
			 * tour4.setVisibility(View.VISIBLE); } else if (position == 6) { //
			 * view = inflater.inflate(R.layout.tour, container, false);
			 * tour5.setVisibility(View.VISIBLE); }
			 */
		tour = (Button) view.findViewById(R.id.bTour);
		bTour_Zureka = (Button) view.findViewById(R.id.bTour_Zureka);
		bTour_Continue = (Button) view.findViewById(R.id.bTour_Continue);
		/*
		 * exit = (Button) view.findViewById(R.id.bExitTut);
		 * exit.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub getActivity().finish(); } });
		 */
		tour.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((SampleCirclesDefault) getActivity()).setCurrentItem(1, true);
			}
		});
		bTour_Zureka.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				SampleCirclesDefault.walk = "Zurekatour";
				((SampleCirclesDefault) getActivity()).refresh1();
				Log.e("fragmap", SampleCirclesDefault.walk);
			}
		});
		bTour_Continue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// ((SampleCirclesDefault) getActivity()).setCurrentItem(3,
				// true);
				SampleCirclesDefault.walk = "Labtour";
				((SampleCirclesDefault) getActivity()).refresh();
				Log.e("fragmap", SampleCirclesDefault.walk);

			}
		});
	
		skip_scr1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				getActivity().finish();

			}
		});

		skip_scr2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				getActivity().finish();

			}
		});
		skip_scr3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				getActivity().finish();

			}
		});

		return view;
	}

}