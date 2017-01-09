package com.hs.userportal;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class TabsActivity extends TabActivity {

	private static TabHost tabHost;
	private static TabHost.TabSpec spec;
	private static Intent intent;
	private static LayoutInflater inflater;
    LinearLayout footer;
	RelativeLayout hide_tool;
	HorizontalScrollView mHorizontalScrollView;
	AlertDialog alert;
    private ImageView cal_me,back_pic;
	private View tab;
	private TextView label;
	private TextView divider,pkg_title;
	int tabWidth;
	int currentTab;
	private View previousView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
        cal_me = (ImageView) findViewById(R.id.cal_me);
        cal_me.setVisibility(View.GONE);
		hide_tool = (RelativeLayout) findViewById(R.id.hide_tool);
		hide_tool.setVisibility(View.VISIBLE);
        footer = (LinearLayout) findViewById(R.id.footer);
        footer.setVisibility(View.GONE);
        back_pic = (ImageView) findViewById(R.id.back_pic);
        pkg_title = (TextView) findViewById(R.id.pkg_title);
        pkg_title.setText("Update Details");
        pkg_title.setTypeface(null, Typeface.BOLD);
		final ProgressDialog dial = new ProgressDialog(TabsActivity.this);
		dial.setMessage("Please Wait...");

		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setContentView(R.layout.overlay);
		dialog.setCanceledOnTouchOutside(true);
		// for dismissing anywhere you touch
		View masterView = dialog.findViewById(R.id.overlay);
		masterView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});
		dialog.show();

		float density = getResources().getDisplayMetrics().density;
		tabWidth = (int) (120 * density);

		// Get inflator so we can start creating the custom view for tab
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// Get tab manager
		tabHost = getTabHost();

		// This converts the custom tab view we created and injects it into the
		// tab widget
		tab = inflater.inflate(R.layout.tab, getTabWidget(), false);
		// Mainly used to set the weight on the tab so each is equally wide
		// Add some text to the tab
		label = (TextView) tab.findViewById(R.id.tabLabel);
		label.setText("Basic");
		// Show a thick line under the selected tab (there are many ways to show
		// which tab is selected, I chose this)
		divider = (TextView) tab.findViewById(R.id.tabSelectedDivider);
		divider.setVisibility(View.VISIBLE);
		// Intent whose generated content will be added to the tab content area
		intent = new Intent(this, update.class);
		// Just some data for the tab content activity to use (just for
		// demonstrating changing content)
		// Finalize the tabs specification
		spec = tabHost.newTabSpec("update").setIndicator(tab)
				.setContent(intent);
		// Tab Content

		Intent i = getIntent();
		String id = i.getStringExtra("id");
		String pass = i.getStringExtra("pass");
		String pic = i.getStringExtra("pic");
		String picname = i.getStringExtra("picname");
		String fbLinked = i.getStringExtra("fbLinked");
		String fbLinkedID = i.getStringExtra("fbLinkedID");
		String hide_footer = i.getStringExtra("hide_footer");

		
		intent.putExtra("id", id);
		intent.putExtra("pass", pass);
		intent.putExtra("pic", pic);
		intent.putExtra("picname", picname);
		intent.putExtra("fbLinked", fbLinked);
		intent.putExtra("fbLinkedID", fbLinkedID);
		// Add the tab to the tab manager
		tabHost.addTab(spec);

		// Tab 2
		tab = inflater.inflate(R.layout.tab, getTabWidget(), false);
		label = (TextView) tab.findViewById(R.id.tabLabel);
		label.setText("Residence");
		intent = new Intent(this, residence.class);
		spec = tabHost.newTabSpec("residence").setIndicator(tab)
				.setContent(intent);
		intent.putExtra("id", id);
		tabHost.addTab(spec);

		// Tab 3
		tab = inflater.inflate(R.layout.tab, getTabWidget(), false);
		label = (TextView) tab.findViewById(R.id.tabLabel);
		label.setText("Work");
		intent = new Intent(this, Work.class);
		spec = tabHost.newTabSpec("work").setIndicator(tab).setContent(intent);
		intent.putExtra("id", id);
		tabHost.addTab(spec);


		// Tab 4
		tab = inflater.inflate(R.layout.tab, getTabWidget(), false);
		label = (TextView) tab.findViewById(R.id.tabLabel);
		label.setText("Education");
		intent = new Intent(this, Education.class);
		spec = tabHost.newTabSpec("education").setIndicator(tab)
				.setContent(intent);
		intent.putExtra("id", id);
		tabHost.addTab(spec);

		// Tab 5
		tab = inflater.inflate(R.layout.tab, getTabWidget(), false);
		label = (TextView) tab.findViewById(R.id.tabLabel);
		label.setText("Travel");
		intent = new Intent(this, Travel.class);
		spec = tabHost.newTabSpec("travel").setIndicator(tab)
				.setContent(intent);
		intent.putExtra("id", id);
		tabHost.addTab(spec);

		// Tab 6
        //-------------------hide it -----------------
		/*tab = inflater.inflate(R.layout.tab, getTabWidget(), false);
		label = (TextView) tab.findViewById(R.id.tabLabel);
		label.setText("My Health");
		intent = new Intent(this, MyHealth.class);
		spec = tabHost.newTabSpec("myhealth").setIndicator(tab)
				.setContent(intent);
		intent.putExtra("id", id);
		intent.putExtra("show_blood", "no");
		tabHost.addTab(spec);*/



        pkg_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        back_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

		// Tab 7
		/*tab = inflater.inflate(R.layout.tab, getTabWidget(), false);
		label = (TextView) tab.findViewById(R.id.tabLabel);
		label.setText("Medical");
		intent = new Intent(this, Medical.class);
		spec = tabHost.newTabSpec("medical").setIndicator(tab)
				.setContent(intent);
		intent.putExtra("id", id);
		tabHost.addTab(spec);
*/
		// Setting custom width for the tabs
		final int width = tabWidth;
		tabHost.getTabWidget().getChildAt(0).getLayoutParams().width = width;
		tabHost.getTabWidget().getChildAt(1).getLayoutParams().width = width;
		tabHost.getTabWidget().getChildAt(2).getLayoutParams().width = width;
		tabHost.getTabWidget().getChildAt(3).getLayoutParams().width = width;
		tabHost.getTabWidget().getChildAt(4).getLayoutParams().width = width;
		//tabHost.getTabWidget().getChildAt(5).getLayoutParams().width = width;
		//tabHost.getTabWidget().getChildAt(6).getLayoutParams().width = width;

		for (int ii = 0; ii < tabHost.getTabWidget().getTabCount(); ii++) {
			tabHost.getTabWidget().getChildTabViewAt(ii).getLayoutParams().width = (int) tabWidth;
		}
		final double screenWidth = getWindowManager().getDefaultDisplay()
				.getWidth();
		mHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
		
		previousView = tabHost.getCurrentView();

		// Listener to detect when a tab has changed. I added this just to show
		// how you can change UI to emphasize the selected tab
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tag) {

				int nrOfShownCompleteTabs = ((int) (Math.floor(screenWidth
						/ tabWidth) - 1) / 2) * 2;
				int remainingSpace = (int) ((screenWidth - tabWidth - (tabWidth * nrOfShownCompleteTabs)) / 2);

				int a = (int) (tabHost.getCurrentTab() * tabWidth);
				int b = (int) ((int) (nrOfShownCompleteTabs / 2) * tabWidth);
				int offset = (a - b) - remainingSpace;

				mHorizontalScrollView.scrollTo(offset, 0);
				// reset some styles
				clearTabStyles();
				View tabView = null;

				// Use the "tag" for the tab spec to determine which tab is
				// selected
				if (tag.equals("update")) {
					tabView = getTabWidget().getChildAt(0);
				} else if (tag.equals("residence")) {
					tabView = getTabWidget().getChildAt(1);

				} else if (tag.equals("education")) {
					tabView = getTabWidget().getChildAt(3);

				} else if (tag.equals("work")) {
					tabView = getTabWidget().getChildAt(2);

				} else if (tag.equals("travel")) {
					tabView = getTabWidget().getChildAt(4);

				}/* else if (tag.equals("myhealth")) {
					tabView = getTabWidget().getChildAt(5);

				}*/ /*else if (tag.equals("medical")) {
					tabView = getTabWidget().getChildAt(6);
				}*/

				tabView.findViewById(R.id.tabSelectedDivider).setVisibility(
						View.VISIBLE);

				View currentView = tabHost.getCurrentView();

				currentView = tabHost.getCurrentView();
				if (tabHost.getCurrentTab() > currentTab) {
					previousView.setAnimation(outToLeftAnimation());
					currentView.setAnimation(inFromRightAnimation());
				} else {
					previousView.setAnimation(outToRightAnimation());
					currentView.setAnimation(inFromLeftAnimation());
				}
				previousView = currentView;
				currentTab = tabHost.getCurrentTab();
			}

			private Animation inFromLeftAnimation() {
				// TODO Auto-generated method stub
				Animation inFromLeft = new TranslateAnimation(
						Animation.RELATIVE_TO_PARENT, -1.0f,
						Animation.RELATIVE_TO_PARENT, 0.0f,
						Animation.RELATIVE_TO_PARENT, 0.0f,
						Animation.RELATIVE_TO_PARENT, 0.0f);
				return setProperties(inFromLeft);
			}

			private Animation outToRightAnimation() {
				// TODO Auto-generated method stub
				Animation outToRight = new TranslateAnimation(
						Animation.RELATIVE_TO_PARENT, 0.0f,
						Animation.RELATIVE_TO_PARENT, 1.0f,
						Animation.RELATIVE_TO_PARENT, 0.0f,
						Animation.RELATIVE_TO_PARENT, 0.0f);
				return setProperties(outToRight);
			}

			private Animation inFromRightAnimation() {
				// TODO Auto-generated method stub
				Animation inFromRight = new TranslateAnimation(
						Animation.RELATIVE_TO_PARENT, 1.0f,
						Animation.RELATIVE_TO_PARENT, 0.0f,
						Animation.RELATIVE_TO_PARENT, 0.0f,
						Animation.RELATIVE_TO_PARENT, 0.0f);
				return setProperties(inFromRight);
			}

			private Animation setProperties(Animation animation) {
				// TODO Auto-generated method stub
				animation.setDuration(240);
				animation.setInterpolator(new AccelerateInterpolator());
				return animation;
			}

			private Animation outToLeftAnimation() {
				// TODO Auto-generated method stub
				Animation outtoLeft = new TranslateAnimation(
						Animation.RELATIVE_TO_PARENT, 0.0f,
						Animation.RELATIVE_TO_PARENT, -1.0f,
						Animation.RELATIVE_TO_PARENT, 0.0f,
						Animation.RELATIVE_TO_PARENT, 0.0f);
				return setProperties(outtoLeft);
			}
		});

	}

	private void clearTabStyles() {
		for (int i = 0; i < getTabWidget().getChildCount(); i++) {
			tab = getTabWidget().getChildAt(i);
			tab.findViewById(R.id.tabSelectedDivider).setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:
			Intent backNav = new Intent(getApplicationContext(), logout.class);
			backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(backNav);
			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			return true;

		case R.id.action_home:

			alert = new AlertDialog.Builder(TabsActivity.this).create();

			alert.setTitle("Message");
			alert.setMessage("Any unsaved changes will be lost. Are you sure you want to go back?");

			alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int id) {

							Intent backNav = new Intent(
									getApplicationContext(), logout.class);
							backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							startActivity(backNav);

						}
					});

			alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Stay",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int id) {

							dialog.dismiss();

						}
					});

			alert.show();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {

		alert = new AlertDialog.Builder(TabsActivity.this).create();

		alert.setTitle("Message");
		alert.setMessage("Any unsaved changes will be lost. Are you sure you want to go back?");

		alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {

						/*Intent backNav = new Intent(getApplicationContext(),
								logout.class);
						backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						startActivity(backNav);*/
						dialog.dismiss();
                         TabsActivity.super.onBackPressed();
						overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
						update.Imguri=null;
						finish();

					}
				});

		alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Stay",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {

						dialog.dismiss();

					}
				});

		alert.show();

	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		
		this.unregisterReceiver(this.mConnReceiver);
		
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
        if(Helper.authentication_flag==true){
            finish();
        }
		this.registerReceiver(this.mConnReceiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
		
		super.onResume();
	}

	private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			boolean noConnectivity = intent.getBooleanExtra(
					ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
			String reason = intent
					.getStringExtra(ConnectivityManager.EXTRA_REASON);
			boolean isFailover = intent.getBooleanExtra(
					ConnectivityManager.EXTRA_IS_FAILOVER, false);

			NetworkInfo currentNetworkInfo = (NetworkInfo) intent
					.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			NetworkInfo otherNetworkInfo = (NetworkInfo) intent
					.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

			if (!currentNetworkInfo.isConnected()) {

				//showAppMsg();
				Toast.makeText(TabsActivity.this, "Network Problem, Please check your net.", Toast.LENGTH_LONG).show();
				/*Intent i = new Intent(getApplicationContext(), java.lang.Error.class);
				startActivity(i);*/
			}
		}
	};

}