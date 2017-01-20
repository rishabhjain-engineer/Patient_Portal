package com.hs.userportal;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ImageGridActivity extends ActionBarActivity {

	private static String[] imageUrls, thumbimageUrls, pdfUrls;
	private AbsListView listView;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private ArrayList<String> image = new ArrayList<String>();
	private ArrayList<String> imagePath = new ArrayList<String>();
	private ArrayList<String> imageName = new ArrayList<String>();
	private ArrayList<String> imageId = new ArrayList<String>();
	private ArrayList<String> thumbImage = new ArrayList<String>();
	private ArrayList<String> thumbImagePath = new ArrayList<String>();
	private	ArrayList<String> path = new ArrayList<String>();
	private ArrayList<String> pdf = new ArrayList<String>();
	private ArrayList<String> pdfPath = new ArrayList<String>();
	private ArrayList<String> pdfName = new ArrayList<String>();
	private ArrayList<String> pdfId = new ArrayList<String>();
	private ArrayList<String> pdfThumb = new ArrayList<String>();
	private	JSONArray subArrayImage;
	private ProgressDialog progress;
	private JSONObject sendData, receiveData;
	private Services service;
	private String caseid;
	private GridView gridPDF;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_grid);

		Intent z = getIntent();
		caseid = z.getStringExtra("caseid");

		ActionBar action = getSupportActionBar();
		action.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#3cbed8")));
		action.setIcon(new ColorDrawable(Color.parseColor("#3cbed8")));
		action.setDisplayHomeAsUpEnabled(true);

		service = new Services(ImageGridActivity.this);
		progress = new ProgressDialog(ImageGridActivity.this);

		try {
			new ImageLoad().execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));

		listView = (GridView) findViewById(R.id.gridview);
		((GridView) listView).setAdapter(new ImageAdapter());
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				startImagePagerActivity(position);
			}
		});

		gridPDF = (GridView) findViewById(R.id.gridviewPDF);
		gridPDF.setAdapter(new PDFAdapter(this));
		gridPDF.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				startPDFActivity(position);
			}
		});
	}

	public class ImageLoad extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {

			progress = new ProgressDialog(ImageGridActivity.this);
			progress.setCancelable(false);
			progress.setMessage("Loading...");
			progress.setIndeterminate(true);
			ImageGridActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					progress.show();
				}
			});

			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			sendData = new JSONObject();
			try {
				sendData.put("CaseId", caseid);
				System.out.println("case id:"+sendData);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			receiveData = service.ViewImages(sendData);
			System.out.println("Images: " + receiveData);

			String imageData = "";
			try {

				imageId.clear();
				image.clear();
				imageName.clear();
				thumbImage.clear();
				path.clear();

				pdfId.clear();
				pdf.clear();
				pdfName.clear();
				pdfThumb.clear();

				imageData = receiveData.getString("d");
				JSONObject cut = new JSONObject(imageData);
				subArrayImage = cut.getJSONArray("Table");
				for (int i = 0; i < subArrayImage.length(); i++)

				{
					if ((subArrayImage.getJSONObject(i).getString("ImageName")
							.contains("pdf"))) {
						path.add(subArrayImage.getJSONObject(i).getString(
								"Path"));
						pdfPath.add(subArrayImage.getJSONObject(i).getString(
								"Image"));
						pdfId.add(subArrayImage.getJSONObject(i).getString(
								"ImageId"));
						pdfName.add(subArrayImage.getJSONObject(i).getString(
								"ImageName"));
						pdfThumb.add(subArrayImage.getJSONObject(i).getString(
								"ThumbImage"));
						pdf.add(path.get(i)+pdfPath.get(i));
						
					} else {
						path.add(subArrayImage.getJSONObject(i).getString(
								"Path"));
						imagePath.add(subArrayImage.getJSONObject(i).getString(
								"Image"));
						image.add(path.get(i)+imagePath.get(i));
						imageId.add(subArrayImage.getJSONObject(i).getString(
								"ImageId"));
						imageName.add(subArrayImage.getJSONObject(i).getString(
								"ImageName"));
						thumbImagePath.add(subArrayImage.getJSONObject(i)
								.getString("ThumbImage"));
						thumbImage.add(path.get(i)+thumbImagePath.get(i));
					}
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

			thumbimageUrls = thumbImage.toArray(new String[thumbImage.size()]);
			// Bundle bundle = getIntent().getExtras();
			imageUrls = image.toArray(new String[image.size()]);
			pdfUrls = pdf.toArray(new String[pdf.size()]);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progress.dismiss();
		}

	}

	private void startImagePagerActivity(int position) {
		Intent intent = new Intent(this, ImagePagerActivity.class);
		intent.putExtra(Constants.Extra.IMAGES, imageUrls);
		intent.putExtra(Constants.Extra.IMAGE_POSITION, position);
		intent.putExtra("ImageName", imageName);
		startActivity(intent);
	}

	private void startPDFActivity(int position) {

		String[] pdfLink = new String[pdf.size()];
		pdfLink = pdf.toArray(pdfLink);

		String[] currentPdfName = new String[pdfName.size()];
		currentPdfName = pdfName.toArray(currentPdfName);

		Intent intent = new Intent(this, PDFActivity.class);
		intent.putExtra("PDF", pdfLink[position]);
		intent.putExtra("PDFname", currentPdfName[position]);
		startActivity(intent);
	}

	static class ViewHolder {
		ImageView imageView;
		ProgressBar progressBar;
		TextView name;
	}

	public class ImageAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return thumbimageUrls.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			View view = convertView;
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.item_grid_image,
						parent, false);
				holder = new ViewHolder();
				assert view != null;
				holder.name = (TextView) view.findViewById(R.id.name);
				holder.imageView = (ImageView) view.findViewById(R.id.image);
				holder.progressBar = (ProgressBar) view
						.findViewById(R.id.progress);
				
				holder.name.setText(imageName.get(position));
				
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			imageLoader.displayImage(thumbimageUrls[position], holder.imageView,
					options, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							holder.progressBar.setProgress(0);
							holder.progressBar.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							holder.progressBar.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							holder.progressBar.setVisibility(View.GONE);
						}
					}, new ImageLoadingProgressListener() {
						@Override
						public void onProgressUpdate(String imageUri,
								View view, int current, int total) {
							holder.progressBar.setProgress(Math.round(100.0f
									* current / total));
						}
					});

			return view;
		}
	}

	public class PDFAdapter extends BaseAdapter {
		private Context mContext;

		// Keep all Images in array
		public String[] mThumbIds = pdfUrls;

		// Constructor
		public PDFAdapter(Context c) {
			mContext = c;
		}

		@Override
		public int getCount() {
			return mThumbIds.length;
		}

		@Override
		public Object getItem(int position) {
			return mThumbIds[position];
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = new ImageView(mContext);
			imageView.setImageResource(R.drawable.pdf_ico);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
			return imageView;
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
//			Intent backNav = new Intent(getApplicationContext(),
//					lablistdetails.class);
//			backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//			startActivity(backNav);
			
			finish();
			
			return true;

		case R.id.action_home:

			Intent intent = new Intent(getApplicationContext(),
					logout.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
//		Intent backNav = new Intent(getApplicationContext(),
//				lablistdetails.class);
		imageLoader.destroy();
//		backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//		startActivity(backNav);
		finish();
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
				// showAppMsg();
				Toast.makeText(ImageGridActivity.this, "Network Problem, Please check your net.", Toast.LENGTH_LONG).show();
				/*Intent i = new Intent(getApplicationContext(), java.lang.Error.class);
				startActivity(i);*/
			}
		}
	};


}
