package com.hs.userportal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseArray;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.StaticHolder;
import dev.dworks.libs.astickyheader.SimpleSectionedGridAdapter;
import dev.dworks.libs.astickyheader.SimpleSectionedGridAdapter.Section;

public class Filevaultcase extends ActionBarActivity {

	private ImageLoader mImageLoader;
	private GridView grid;
	private ImageAdapter mAdapter;
	private ArrayList<Section> sections = new ArrayList<Section>();
	private NetworkImageView mNetworkImageView;
	private JsonObjectRequest jr;
	private JSONArray subArrayImage;
	private ArrayList<String> thumbImage = new ArrayList<String>();
	private ArrayList<String> imageName = new ArrayList<String>();
	private ArrayList<String> imageNametobesent = new ArrayList<String>();
	private ArrayList<String> uniquecaseid = new ArrayList<String>();
	private ArrayList<Integer> numberincaseid = new ArrayList<Integer>();
	private ArrayList<Integer> pos = new ArrayList<Integer>();
	private RequestQueue queue;
	private String id = "";
	private JSONObject sendData;
	private ProgressDialog progress;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grid_case);
		ActionBar action = getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		grid = (GridView) findViewById(R.id.grid);

		Intent z = getIntent();
		id = z.getStringExtra("id");
		queue = Volley.newRequestQueue(this);

		sendData = new JSONObject();
		try {
			sendData.put("PatientId", id);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		progress = ProgressDialog.show(this, "",
			    "Loading..", true);
			
		/*String url = Services.init+"/PatientModule/PatientService.asmx/GetPatientImagesInCase";*/
		StaticHolder sttc_holdr=new StaticHolder(Filevaultcase.this,StaticHolder.Services_static.GetPatientImagesInCase);
		String url=sttc_holdr.request_Url();
		jr = new JsonObjectRequest(Request.Method.POST, url, sendData,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {

						System.out.println(response);

						try {
							String imageData = response.getString("d");
							JSONObject cut = new JSONObject(imageData);
							subArrayImage = cut.getJSONArray("Table");

							subArrayImage = sortJsonArray(subArrayImage);
							System.out.println(subArrayImage);

							uniquecaseid.add(0, "Case: "+subArrayImage.getJSONObject(0).getString("CaseCode")+","+subArrayImage.getJSONObject(0).getString("adviseDate").substring(0, 10));
							numberincaseid.add(0, 0);
							for (int i = 0; i < subArrayImage.length(); i++)

							{

								imageName.add(subArrayImage.getJSONObject(i)
										.getString("Image"));
								thumbImage.add(subArrayImage.getJSONObject(i)
										.getString("ThumbImage"));
								imageNametobesent.add(subArrayImage.getJSONObject(i)
										.getString("ImageName"));

								if (i > 0) {
									if (!subArrayImage
											.getJSONObject(i)
											.getString("CaseCode")
											.equals(subArrayImage
													.getJSONObject(i - 1)
													.getString("CaseCode"))) {

										uniquecaseid.add("Case: "+subArrayImage.getJSONObject(i).getString("CaseCode")+","+subArrayImage.getJSONObject(i).getString("adviseDate").substring(0, 10));
										numberincaseid.add(i);

									}
								}
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						mImageLoader = MyVolleySingleton.getInstance(
								Filevaultcase.this).getImageLoader();
						initControls();
						progress.dismiss();
						// grid.setAdapter(new ImageAdapter());

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

						System.out.println("Error");

					}
				}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("Cookie", Services.hoja);
				return headers;
			}
		};
		queue.add(jr);

//		grid.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//
//				System.out.println(position);
//
//				Intent i = new Intent(Filevaultcase.this, ExpandImage.class);
//				i.putExtra(
//						"image",
//						"https://files.healthscion.com/"+ imageName.get(position));
//				startActivity(i);
//
//			}
//		});

	}

	public static JSONArray sortJsonArray(JSONArray array) throws JSONException {
		List<JSONObject> jsons = new ArrayList<JSONObject>();
		for (int i = 0; i < array.length(); i++) {
			jsons.add(array.getJSONObject(i));
		}
		Collections.sort(jsons, new Comparator<JSONObject>() {
			@Override
			public int compare(JSONObject lhs, JSONObject rhs) {
				String lid = "";
				String rid = "";
				try {
					lid = lhs.getString("adviseDate");
					rid = rhs.getString("adviseDate");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Here you could parse string id to integer and then compare.
				return lid.compareTo(rid);
			}
		});
		return new JSONArray(jsons);
	}

	private void initControls() {

		String[] mHeaderNames = uniquecaseid.toArray(new String[uniquecaseid.size()]);
		// String[] mHeaderNames = { "case 1", "case 2", "case 3" };
		Integer[] mHeaderPositions = numberincaseid.toArray(new Integer[numberincaseid.size()]);
		// Integer[] mHeaderPositions = { 0, 4, 7 };
		mAdapter = new ImageAdapter();
		for (int i = 0; i < mHeaderPositions.length; i++) {
			sections.add(new Section(mHeaderPositions[i], mHeaderNames[i]));
		}
		SimpleSectionedGridAdapter simpleSectionedGridAdapter = new SimpleSectionedGridAdapter(
				this, mAdapter, R.layout.grid_casewise_itemheader,
				R.id.header_layout, R.id.header);
		simpleSectionedGridAdapter.setGridView(grid);
		simpleSectionedGridAdapter
				.setSections(sections.toArray(new Section[0]));
		grid.setAdapter(simpleSectionedGridAdapter);
	}



	static class ViewHolder {
		ImageView imageView;
	}

	private class ImageAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return imageName.size();
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
		public View getView(final int position, View convertView, ViewGroup parent) {

			View view = convertView;
			final ViewHolder gridViewImageHolder;
			// check to see if we have a view
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.grid_casewise_item,
						parent, false);
				gridViewImageHolder = new ViewHolder();
				gridViewImageHolder.imageView = (ImageView) view
						.findViewById(R.id.networkImageView);
				view.setTag(gridViewImageHolder);
			} else {
				gridViewImageHolder = (ViewHolder) view.getTag();
			}

			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x;

			mNetworkImageView = (NetworkImageView) gridViewImageHolder.imageView;
			mNetworkImageView.getLayoutParams().width = width / 2;
			mNetworkImageView.getLayoutParams().height = mNetworkImageView
					.getLayoutParams().width;
			mNetworkImageView.setDefaultImageResId(R.drawable.box);
			mNetworkImageView.setErrorImageResId(R.drawable.ic_error);
			mNetworkImageView.setAdjustViewBounds(true);
			mNetworkImageView.setImageUrl("https://files.healthscion.com/"
					+ thumbImage.get(position), mImageLoader);
			
			
			mNetworkImageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					try {
						sendData.put("image", thumbImage);
						System.out.println(sendData);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Intent i = new Intent(Filevaultcase.this, ExpandImage.class);
					i.putExtra("image","https://files.healthscion.com/"+ imageName.get(position));
					i.putExtra("imagename",imageNametobesent.get(position));
					
					startActivity(i);
					
					
				}
			});
			
			return view;

		}

	}

	// { 0, 6};

	public static class ViewHolder1 {
		@SuppressWarnings("unchecked")
		public static <T extends View> T get(View view, int id) {
			SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
			if (viewHolder == null) {
				viewHolder = new SparseArray<View>();
				view.setTag(viewHolder);
			}
			View childView = viewHolder.get(id);
			if (childView == null) {
				childView = view.findViewById(id);
				viewHolder.put(id, childView);
			}

			return (T) childView;
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
			super.onBackPressed();
			return true;

		case R.id.action_home:

			Intent intent = new Intent(getApplicationContext(), logout.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
}