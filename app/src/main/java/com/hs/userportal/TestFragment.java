package com.cloudchowk.patient;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
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

public class
		TestFragment extends Fragment {

	public static final String POSITION_KEY = "FragmentPositionKey";
	private int position;
	LinearLayout tour1, tour2, tour3, tour4, tour0, tour5;
	ImageView image;
	TextView welcome;
	Button tour, exit;
	RelativeLayout buttonbar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);

	}

	public static TestFragment newInstance(Bundle args) {
		TestFragment fragment = new TestFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		position = getArguments().getInt(POSITION_KEY);
		View view = inflater.inflate(R.layout.tour, container, false);

		final Animation animTranslate = AnimationUtils.loadAnimation(
				getActivity(), R.anim.slide_up);
		final Animation animFade = AnimationUtils.loadAnimation(getActivity(),
				R.anim.fade_in);

		//buttonbar = (RelativeLayout) view.findViewById(R.id.buttonbar);
		welcome = (TextView) view.findViewById(R.id.tvWelcome);
		image = (ImageView) view.findViewById(R.id.imageView0);

		tour0 = (LinearLayout) view.findViewById(R.id.tour0);
		tour1 = (LinearLayout) view.findViewById(R.id.tour1);
		tour2 = (LinearLayout) view.findViewById(R.id.tour2);
		tour3 = (LinearLayout) view.findViewById(R.id.tour3);
		tour4 = (LinearLayout) view.findViewById(R.id.tour4);
		tour5 = (LinearLayout) view.findViewById(R.id.tour5);

		tour0.setVisibility(View.GONE);
		tour1.setVisibility(View.GONE);
		tour2.setVisibility(View.GONE);
		tour3.setVisibility(View.GONE);
		tour4.setVisibility(View.GONE);
		tour5.setVisibility(View.GONE);

		if (position == 0) {
			// view = inflater.inflate(R.layout.tour, container, false);
			tour0.setVisibility(View.VISIBLE);

			welcome.setText("Welcome, "+SampleCirclesDefault.name);
			
		} else if (position == 1) {
			// view = inflater.inflate(R.layout.tour, container, false);
			tour1.setVisibility(View.VISIBLE);
		} else if (position == 2) {
			// view = inflater.inflate(R.layout.tour, container, false);
			tour2.setVisibility(View.VISIBLE);
		} else if (position == 3) {
			// view = inflater.inflate(R.layout.tour, container, false);
			tour3.setVisibility(View.VISIBLE);
		} else if (position == 4) {
			// view = inflater.inflate(R.layout.tour, container, false);
			tour4.setVisibility(View.VISIBLE);
		} else if (position == 5) {
			// view = inflater.inflate(R.layout.tour, container, false);
			tour5.setVisibility(View.VISIBLE);
		}

		tour = (Button) view.findViewById(R.id.bTour);
		exit = (Button) view.findViewById(R.id.bExitTut);

		exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				getActivity().finish();

			}
		});

		tour.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((SampleCirclesDefault) getActivity()).setCurrentItem(1, true);
			}
		});
		return view;

	}
}