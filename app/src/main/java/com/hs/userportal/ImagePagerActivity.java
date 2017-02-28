package com.hs.userportal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ImagePagerActivity extends ActionBarActivity {

	private static final String STATE_POSITION = "STATE_POSITION";
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ViewPager pager;
	private int pagerPosition;
	private String saveImages, newImageName;
	private String[] saveImageName;
	private ArrayList<String> imageName = new ArrayList<String>();
	private static int currentPage;
	private TextView tvImageName;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_pager);

		ActionBar action = getSupportActionBar();
		action.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#3cbed8")));
		action.setIcon(new ColorDrawable(Color.parseColor("#3cbed8")));
		action.setDisplayHomeAsUpEnabled(true);

		Intent name = getIntent();
		imageName = name.getStringArrayListExtra("ImageName");
		
		tvImageName = (TextView) findViewById(R.id.imageName);

		Bundle bundle = getIntent().getExtras();
		assert bundle != null;
		String[] imageUrls = bundle.getStringArray(Constants.Extra.IMAGES);
		pagerPosition = bundle.getInt(Constants.Extra.IMAGE_POSITION, 0);

		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}

		imageLoader = ImageLoader.getInstance();
		//imageLoader.init(ImageLoaderConfiguration.createDefault(this));

		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error)
				.resetViewBeforeLoading(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(new ImagePagerAdapter(imageUrls));
		pager.setCurrentItem(pagerPosition);
		tvImageName.setText(imageName.get(pagerPosition));

		pager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageScrollStateChanged(int state) {
			}

			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
			}

			public void onPageSelected(int position) {
				// Check if this is the page you want.
				currentPage = position;
				System.out.println("Position Up: " + currentPage);
				tvImageName.setText(imageName.get(position));
				
			}
		});

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, pager.getCurrentItem());
	}

	private class ImagePagerAdapter extends PagerAdapter {

		String[] images;
		LayoutInflater inflater;

		ImagePagerAdapter(String[] images) {
			this.images = images;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return images.length;
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.item_pager_image,
					view, false);
			assert imageLayout != null;
			ImageView imageView = (ImageView) imageLayout
					.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) imageLayout
					.findViewById(R.id.loading);

			System.out.println("Position Down: " + position);
			saveImages = images[position];
			saveImageName = new String[imageName.size()];
			saveImageName = imageName.toArray(saveImageName);
			newImageName = saveImageName[position];

			imageLoader.displayImage(images[position], imageView, options,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							spinner.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							String message = null;
							switch (failReason.getType()) {
							case IO_ERROR:
								message = "Input/Output error";
								break;
							case DECODING_ERROR:
								message = "Image can't be decoded";
								break;
							case NETWORK_DENIED:
								message = "Downloads are denied";
								break;
							case OUT_OF_MEMORY:
								message = "Out Of Memory error";
								break;
							case UNKNOWN:
								message = "Unknown error";
								break;
							}
							Toast.makeText(ImagePagerActivity.this, message,
									Toast.LENGTH_SHORT).show();

							spinner.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							spinner.setVisibility(View.GONE);
						}
					});

			view.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}
		
	}

	public void saveImage(Bitmap myBitmap) {

		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/cloudchowk");

		if (!myDir.exists()) {
			myDir.mkdirs();
		}

		// String fname = "Image.jpg";
		File file = new File(myDir, newImageName);
		if (file.exists())
			file.delete();
		try {
			FileOutputStream out = new FileOutputStream(file);
			myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
			Toast.makeText(getApplicationContext(), "Image saved succesfully!",
					Toast.LENGTH_SHORT).show();
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
					Uri.parse("file://"
							+ Environment.getExternalStorageDirectory())));
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Image can't be saved",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:
//			Intent backNav = new Intent(getApplicationContext(),
//					ImageGridActivity.class);
//			backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//			startActivity(backNav);
//			return true;
			
			finish();

			// case R.id.action_share:
			//
			// return true;

		case R.id.action_save:

			imageLoader.loadImage(saveImages, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingComplete(String imageUri, View view,
						Bitmap loadedImage) {
					saveImage(loadedImage);
				}
			});
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
//		Intent backNav = new Intent(getApplicationContext(),
//				ImageGridActivity.class);
		imageLoader.stop();
//		backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//		startActivity(backNav);
		finish();
	}
}