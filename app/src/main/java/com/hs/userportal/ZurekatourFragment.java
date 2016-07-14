package com.hs.userportal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ZurekatourFragment extends Fragment {

	public static final String POSITION_KEY = "FragmentPositionKey";
	private int position;
	LinearLayout tour2, tour3, tour5, tour0_1, tour0, tour1, tour6;
	ImageView image;
	TextView welcome, skip_scr1, skip_scr2, skip_scr3, skip_scr4, skip_scr5, skip_scr6;
	Button exit, bTour_Zureka, bTour_Continue, tour, bLabTour;
	RelativeLayout buttonbar;
	static int current_Screencheck = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public static ZurekatourFragment newInstance(Bundle args) {
		ZurekatourFragment fragment = new ZurekatourFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		position = getArguments().getInt(POSITION_KEY);
		View view = inflater.inflate(R.layout.zurekatour, container, false);

		//buttonbar = (RelativeLayout) view.findViewById(R.id.buttonbar);
		welcome = (TextView) view.findViewById(R.id.tvWelcome);
		skip_scr1 = (TextView) view.findViewById(R.id.skip_imgview0);
		skip_scr2 = (TextView) view.findViewById(R.id.skip);
		skip_scr3 = (TextView) view.findViewById(R.id.skip_imgview1);
		skip_scr4 = (TextView) view.findViewById(R.id.skip_imgview2);
		skip_scr5 = (TextView) view.findViewById(R.id.skip_imgview3);
		skip_scr6 = (TextView) view.findViewById(R.id.skip_imgview4);
		// back_option = (TextView) view.findViewById(R.id.back_option);
		image = (ImageView) view.findViewById(R.id.imageView0);

		tour0 = (LinearLayout) view.findViewById(R.id.tour0);
		tour1 = (LinearLayout) view.findViewById(R.id.tour1);
		tour2 = (LinearLayout) view.findViewById(R.id.tour2);
		tour3 = (LinearLayout) view.findViewById(R.id.tour3);
		tour5 = (LinearLayout) view.findViewById(R.id.tour5);
		tour6 =(LinearLayout)  view.findViewById(R.id.tour6);
		tour0_1 = (LinearLayout) view.findViewById(R.id.tour0_1);

		tour0.setVisibility(View.GONE);
		tour1.setVisibility(View.GONE);
		tour2.setVisibility(View.GONE);
		tour3.setVisibility(View.GONE);
		tour5.setVisibility(View.GONE);
		tour6.setVisibility(View.GONE);
		tour0_1.setVisibility(view.GONE);

		if (position == 0) {
			tour0.setVisibility(View.VISIBLE);
			welcome.setText("Welcome, " + SampleCirclesDefault.name);
		} else if (position == 1) {
			tour1.setVisibility(View.VISIBLE);
		} else if (position == 2) {
			tour0_1.setVisibility(View.VISIBLE);
		} else if (position == 3) {
			tour2.setVisibility(View.VISIBLE);
		} else if (position == 4) {
			tour3.setVisibility(View.VISIBLE);
		} else if (position == 5) {
			tour5.setVisibility(View.VISIBLE);
		} else if(position == 6){
			tour6.setVisibility(View.VISIBLE);
		}
		tour = (Button) view.findViewById(R.id.bTour);
		bTour_Zureka = (Button) view.findViewById(R.id.bTour_Zureka);
		bTour_Continue = (Button) view.findViewById(R.id.bTour_Continue);
		bLabTour = (Button) view.findViewById(R.id.bLabTour);
		// ((SampleCirclesDefault) getActivity()).setCurrentItem(1, true);

		tour.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((SampleCirclesDefault) getActivity()).setCurrentItem(1, true);
			}
		});

		if (current_Screencheck == 0) {
			((SampleCirclesDefault) getActivity()).setCurrentItem(3, true);
		} else {

		}

		exit = (Button) view.findViewById(R.id.bExitTut);
		exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});

		bTour_Zureka.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				SampleCirclesDefault.walk = "Zurekatour";
				SampleCirclesDefault.transition = null;
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
				SampleCirclesDefault.transition =null;
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

		skip_scr4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				getActivity().finish();

			}
		});

		skip_scr5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				getActivity().finish();

			}
		});
		skip_scr6.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				getActivity().finish();

			}
		});
		/*
		 * back_option.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * 
		 * ((SampleCirclesDefault) getActivity()).setCurrentItem(2, true);
		 * 
		 * } });
		 */
		
		bLabTour.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// ((SampleCirclesDefault) getActivity()).setCurrentItem(3,
				// true);
				SampleCirclesDefault.walk = "Labtour";
				SampleCirclesDefault.transition = "opposite";
				((SampleCirclesDefault) getActivity()).refresh_opposite();
				Log.e("fragmap", SampleCirclesDefault.walk);

			}
		});
		++current_Screencheck;
		return view;
	}

}
