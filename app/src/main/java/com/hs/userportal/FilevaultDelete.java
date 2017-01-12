package com.hs.userportal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Map;

import config.StaticHolder;

public class FilevaultDelete extends ActionBarActivity {

	JSONArray jarray;
	NetworkImageView mNetworkImageView;
	private ImageAdapter imageAdapter;
	private boolean[] thumbnailsselection;
	int count;
	JsonObjectRequest jr;
	ImageLoader mImageLoader;
	ArrayList<String> thumbImage = new ArrayList<String>();
	ArrayList<String> imageId = new ArrayList<String>();
	String imageIdsToBeSent = "";
	RequestQueue queue;
	JSONObject sendData;
	int pos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filevaultdelete);

		Intent z = getIntent();
		String jarr = z.getStringExtra("image");
		pos = z.getIntExtra("pos", 0);
		try {
			jarray = new JSONArray(jarr);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.count = jarray.length();
		this.thumbnailsselection = new boolean[this.count];
		thumbnailsselection[pos] = true;

		try {
			for (int i = 0; i < jarray.length(); i++)

			{

				// imageId.add(jarray.getJSONObject(i)
				// .getString("ImageId"));
				thumbImage.add(jarray.getJSONObject(i).getString("ThumbImage"));

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		GridView imagegrid = (GridView) findViewById(R.id.PhoneImageGrid);
		imageAdapter = new ImageAdapter();
		mImageLoader = MyVolleySingleton.getInstance(FilevaultDelete.this)
				.getImageLoader();
		imagegrid.setAdapter(imageAdapter);

		final Button selectBtn = (Button) findViewById(R.id.selectBtn);
		selectBtn.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				// TODO Auto-generated method stub

				AlertDialog dialog = new AlertDialog.Builder(
						FilevaultDelete.this).create();
				dialog.setTitle("Delete Images");
				dialog.setMessage("Are you sure you want to delete selected files?");
				dialog.setCancelable(false);
				dialog.setButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						for (int i = 0; i < thumbnailsselection.length; i++) {

							if (thumbnailsselection[i]) {

								try {
									imageId.add(jarray.getJSONObject(i)
											.getString("ImageId"));
									imageIdsToBeSent = imageIdsToBeSent
											+ jarray.getJSONObject(i)
													.getString("ImageId") + ",";
									System.out.println(i);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}

						}

						System.out.println(imageIdsToBeSent);

						queue = Volley.newRequestQueue(FilevaultDelete.this);

						sendData = new JSONObject();
						try {
							sendData.put("ImageId", imageIdsToBeSent);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						/*String url = Services.init
								+ "/PatientModule/PatientService.asmx/DeletePatientFiles";*/
						StaticHolder sttc_holdr=new StaticHolder(FilevaultDelete.this,StaticHolder.Services_static.DeletePatientFiles);
						String url=sttc_holdr.request_Url();
						jr = new JsonObjectRequest(Request.Method.POST, url,
								sendData, new Response.Listener<JSONObject>() {
									@Override
									public void onResponse(JSONObject response) {
										System.out.println(response);

										try {
											Toast.makeText(
													FilevaultDelete.this,
													response.getString("d")
															+ " Items Deleted",
													Toast.LENGTH_SHORT).show();
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

										finish();

									}
								}, new Response.ErrorListener() {
									@Override
									public void onErrorResponse(
											VolleyError error) {

										System.out.println("Error");

									}
								}) {
							@Override
							public Map<String, String> getHeaders()
									throws AuthFailureError {
								Map<String, String> headers = new HashMap<String, String>();
								headers.put("Cookie", Services.hoja);
								return headers;
							}
						};
						queue.add(jr);

					}
				});
				dialog.show();

			}
		});

	}

	public class ImageAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public ImageAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return count;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.filevaultdeleteitem,
						null);
				holder.imageview = (ImageView) convertView
						.findViewById(R.id.thumbImage);
				holder.checkbox = (CheckBox) convertView
						.findViewById(R.id.itemCheckBox);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.checkbox.setId(position);
			holder.imageview.setId(position);
			holder.checkbox.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// TODO Auto-generated method stub
					CheckBox cb = (CheckBox) v;
					int id = cb.getId();
					if (thumbnailsselection[id]) {
						cb.setChecked(false);
						thumbnailsselection[id] = false;
					} else {
						cb.setChecked(true);
						thumbnailsselection[id] = true;
					}
				}
			});

			// holder.imageview.setOnClickListener(new OnClickListener() {
			//
			// public void onClick(View v) {
			// // TODO Auto-generated method stub
			//
			// CheckBox cb = (CheckBox) v;
			// int id = cb.getId();
			// if (thumbnailsselection[id]) {
			// cb.setChecked(false);
			// thumbnailsselection[id] = false;
			// } else {
			// cb.setChecked(true);
			// thumbnailsselection[id] = true;
			//
			// }
			//
			// }
			// });
			// holder.imageview.setImageBitmap(thumbnails[position]);

			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x;

			mNetworkImageView = (NetworkImageView) holder.imageview;
			mNetworkImageView.getLayoutParams().width = width / 2;
			mNetworkImageView.getLayoutParams().height = mNetworkImageView
					.getLayoutParams().width;
			mNetworkImageView.setDefaultImageResId(R.drawable.ic_empty);
			mNetworkImageView.setErrorImageResId(R.drawable.ic_error);
			mNetworkImageView.setAdjustViewBounds(true);
			mNetworkImageView.setImageUrl("https://files.healthscion.com/"
					+ thumbImage.get(position), mImageLoader);

			holder.checkbox.setChecked(thumbnailsselection[position]);
			holder.id = position;
			return convertView;
		}
	}

	class ViewHolder {
		ImageView imageview;
		CheckBox checkbox;
		int id;
	}

}
