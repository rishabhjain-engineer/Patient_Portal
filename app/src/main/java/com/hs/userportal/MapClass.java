package com.hs.userportal;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MapClass extends ActionBarActivity {

//	GoogleMap googleMap;
    private int locationCount = 1;
	private JSONArray gpsarray, centreArray;
	private JSONObject sendData;
	private RequestQueue queue;
	private static String pic_Map = null;
	private String picname = "";
	private ByteArrayOutputStream byteArrayOutputStream;
	private byte[] byteArray;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int PICK_FROM_GALLERY = 2;
	private JsonObjectRequest jr;
	private ProgressDialog progress;
	private String[] parts;
	private String lat = "";
	private String CentreName, CentreArea;
	private String lng = "";
	private String CentreId;
	private TextView tvLabName, tvEmail, tvContact, tvAddress, tvRadio, tvPath;
	private TextView monday, tuesday, wednesday, thursday, friday, saturday, sunday;
	private ProgressBar progressCircle;
	private FrameLayout frame;
	private LinearLayout linear, layoutHours, layoutOpen;
	private Double currentlat, currentlng, latitude, longitude;
	private List<SortList> sortListMap;
	private Button upload_btnMap;
	private SharedPreferences sharedpreferences;
	private Uri Imguri;

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);

		queue = Volley.newRequestQueue(this);
		frame = (FrameLayout) findViewById(R.id.frameLayout);
		layoutOpen = (LinearLayout) findViewById(R.id.layoutOpen);
		layoutHours = (LinearLayout) findViewById(R.id.layoutHours);
		linear = (LinearLayout) findViewById(R.id.linearLayout);
		tvEmail = (TextView) findViewById(R.id.etEmail);
		tvContact = (TextView) findViewById(R.id.etContact);
		tvAddress = (TextView) findViewById(R.id.etArea);
		tvRadio = (TextView) findViewById(R.id.etRadio);
		tvPath = (TextView) findViewById(R.id.etPath);

		monday = (TextView) findViewById(R.id.etMonday);
		tuesday = (TextView) findViewById(R.id.etTuesday);
		wednesday = (TextView) findViewById(R.id.etWednesday);
		thursday = (TextView) findViewById(R.id.etThursday);
		friday = (TextView) findViewById(R.id.etFriday);
		saturday = (TextView) findViewById(R.id.etSaturday);
		sunday = (TextView) findViewById(R.id.etSunday);
		upload_btnMap = (Button) findViewById(R.id.upload_btnMap);
		upload_btnMap.setVisibility(View.GONE);

		/*// ActionBar action = getActionBar();
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1DBBE3")));
		getActionBar().setIcon(new ColorDrawable(Color.parseColor("#1DBBE3")));
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Actionbar with search option

		// enable ActionBar app icon to behave as action to toggle nav drawer

		getActionBar().setHomeButtonEnabled(true);
		// getActionBar().setDisplayShowTitleEnabled(false);

		// removing app icon from Actionbar
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));*/
		ActionBar action = getSupportActionBar();
		action.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1DBBE3")));
		action.setIcon(new ColorDrawable(Color.parseColor("#1DBBE3")));
		action.setDisplayHomeAsUpEnabled(true);


		upload_btnMap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (LocationClass.patientId == null) {
					Toast.makeText(getBaseContext(), "Please Sign in !", Toast.LENGTH_SHORT).show();

				} else {

					AlertDialog.Builder builder = new AlertDialog.Builder(MapClass.this);

					builder.setTitle("Choose Image Source");
					builder.setItems(new CharSequence[] { "Pick from Gallery", "Take from Camera" },
							new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0:

								Intent intent = new Intent(Intent.ACTION_PICK,
										MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
								intent.setType("image/*");
								startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FROM_GALLERY);

								break;

							case 1:

								// Intent takePictureIntent = new
								// Intent(MediaStore.ACTION_IMAGE_CAPTURE);
								// if
								// (takePictureIntent.resolveActivity(getPackageManager())
								// != null)
								// {
								//
								// startActivityForResult(takePictureIntent,PICK_FROM_CAMERA);
								//
								// }

								File photo = null;
								Intent intent1 = new Intent("android.media.action.IMAGE_CAPTURE");
								if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
									photo = new File(Environment.getExternalStorageDirectory(), "test.jpg");
								} else {
									photo = new File(getCacheDir(), "test.jpg");
								}
								if (photo != null) {
									intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
									Imguri = Uri.fromFile(photo);
									startActivityForResult(intent1, PICK_FROM_CAMERA);
								}

								break;

							default:
								break;
							}
						}
					});
					builder.show();
				}
			}
		});

		sharedpreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

		progressCircle = (ProgressBar) findViewById(R.id.progressBar);

		Intent i = getIntent();
		currentlat = i.getDoubleExtra("lat", 28.56);
		currentlng = i.getDoubleExtra("lng", 77.23);

		/*googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentlat, currentlng)));

		googleMap.animateCamera(CameraUpdateFactory.zoomTo(1));*/

		sortListMap = new ArrayList<SortList>();

		for (int j = 0; j < LocationClass.sortList.size(); j++) {
			sortListMap.add(LocationClass.sortList.get(j));
		}

		// Getting Google Play availability status
		/*int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());*/

		// Showing status
	/*	if (status != ConnectionResult.SUCCESS) { // Google Play Services are
			// not available

			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
			dialog.show();

		}*/ /*else { // Google Play Services are available

			for (int k = 0; k < sortListMap.size(); k++) {

				latitude = sortListMap.get(k).getLat();
				longitude = sortListMap.get(k).getLng();
				CentreName = sortListMap.get(k).getName();
				CentreArea = sortListMap.get(k).getArea();

				Location locationA = new Location("point A");
				locationA.setLatitude(latitude);
				locationA.setLongitude(longitude);
				Location locationB = new Location("point B");
				locationB.setLatitude(currentlat);
				locationB.setLongitude(currentlng);

				drawMarker(new LatLng(latitude, longitude), CentreName, CentreArea);

			}
*/
			/*googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentlat, currentlng)));

			googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));*/

			// progress = ProgressDialog.show(this, "", "Loading..", true);
			// sendData = new JSONObject();
			//
			// String url =
			// "http://clinic02.cloudchowk.com:1024/LabService.asmx/GetGPSCoordinates";
			//
			// jr = new JsonObjectRequest(Request.Method.POST, url, sendData,
			// new Response.Listener<JSONObject>() {
			// @Override
			// public void onResponse(JSONObject response) {
			//
			// System.out.println(response);
			//
			// try {
			// String imageData = response.getString("d");
			// JSONObject cut = new JSONObject(imageData);
			// gpsarray = cut.getJSONArray("Table");
			//
			// if (gpsarray.length() != 0) {
			//
			// for (int i = 0; i < gpsarray.length(); i++) {
			//
			// try {
			// String latlong =
			// gpsarray.getJSONObject(i).getString("GPSCoordinates");
			// CentreName = gpsarray.getJSONObject(i).getString("CentreName");
			// CentreArea = gpsarray.getJSONObject(i).getString("AreaName");
			// parts = latlong.split(",");
			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			//
			// lat = parts[0].trim();
			// lng = parts[1].trim();
			//
			// // Drawing marker on the map
			//
			//
			// Location locationA = new Location("point A");
			// locationA.setLatitude(Double.parseDouble(lat));
			// locationA.setLongitude(Double.parseDouble(lng));
			// Location locationB = new Location("point B");
			// locationB.setLatitude(currentlat);
			// locationB.setLongitude(currentlng);
			//
			// float distancemeter = locationA.distanceTo(locationB);
			// if(distancemeter<3000)
			// {
			//
			// drawMarker(new LatLng(Double.parseDouble(lat),
			// Double.parseDouble(lng)),CentreName,CentreArea);
			//
			// }
			//
			//
			//
			// }
			//
			//
			// googleMap.moveCamera(CameraUpdateFactory
			// .newLatLng(new LatLng(currentlat,currentlng)));
			//
			// googleMap.animateCamera(CameraUpdateFactory
			// .zoomTo(13));
			// }
			//
			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			//
			// progress.dismiss();
			//
			// }
			// }, new Response.ErrorListener() {
			// @Override
			// public void onErrorResponse(VolleyError error) {
			// System.out.println(error);
			// progress.dismiss();
			// }
			// }) {
			//
			// };
			// queue.add(jr);

			// Enabling MyLocation Layer of Google Map
			//googleMap.setMyLocationEnabled(true);

		}

		// googleMap.setOnMapClickListener(new OnMapClickListener() {
		//
		// @Override
		// public void onMapClick(LatLng point) {
		// locationCount++;
		//
		// googleMap.clear();
		// drawMarkerpoint(point);
		// Double pointLat = point.latitude;
		// Double pointLon = point.longitude;
		//
		// for (int i = 0; i < gpsarray.length(); i++) {
		//
		// try {
		// String latlong =
		// gpsarray.getJSONObject(i).getString("GPSCoordinates");
		// CentreName = gpsarray.getJSONObject(i).getString("CentreName");
		// CentreArea = gpsarray.getJSONObject(i).getString("AreaName");
		// parts = latlong.split(",");
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// lat = parts[0].trim();
		// lng = parts[1].trim();
		//
		// // Drawing marker on the map
		// //drawMarker(new LatLng(Double.parseDouble(lat),
		// Double.parseDouble(lng)));
		//
		// Location locationA = new Location("point A");
		// locationA.setLatitude(Double.parseDouble(lat));
		// locationA.setLongitude(Double.parseDouble(lng));
		// Location locationB = new Location("point B");
		// locationB.setLatitude(pointLat);
		// locationB.setLongitude(pointLon);
		//
		// float distancemeter = locationA.distanceTo(locationB);
		// if(distancemeter<3000)
		// {
		//
		// drawMarker(new LatLng(Double.parseDouble(lat),
		// Double.parseDouble(lng)),CentreName,CentreArea);
		//
		// }
		//
		// System.out.println(distancemeter + " mts");
		//
		// }
		//
		//
		// }
		// });

		/*googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {

				for (int i = 0; i < sortListMap.size(); i++) {

					if (sortListMap.get(i).getName().equalsIgnoreCase(marker.getTitle())
							&& sortListMap.get(i).getArea().equalsIgnoreCase(marker.getSnippet())) {

						CentreId = sortListMap.get(i).getCenterID();

						System.out.println(CentreId);

						// if(sharedpreferences.getBoolean("openLocation",
						// false))
						// {
						Intent i1 = new Intent(MapClass.this, MapLabDetails.class);
						i1.putExtra("id", CentreId);
						startActivity(i1);

					}

				}

			}
		});*/

		// googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
		//
		// @Override
		// public boolean onMarkerClick(Marker arg0) {
		// // TODO Auto-generated method stub
		//
		// for (int i = 0; i < sortListMap.size(); i++) {
		//
		// if (sortListMap.get(i).getName().equalsIgnoreCase(arg0.getTitle())
		// && sortListMap.get(i).getArea().equalsIgnoreCase(arg0.getSnippet()))
		// {
		//
		// CentreId = sortListMap.get(i).getCenterID();
		//
		// System.out.println(CentreId);
		//
		// if(sharedpreferences.getBoolean("openLocation", false))
		// {
		// Intent i1 = new Intent(MapClass.this, MapLabDetails.class);
		// i1.putExtra("id", CentreId);
		// startActivity(i1);
		//
		// }
		//
		// }
		//
		// }
		//
		// return false;
		// }
		// });

	/*}*/

	/*private void drawMarker(LatLng point, String title, String Area) {
		// Creating an instance of MarkerOptions
		MarkerOptions markerOptions = new MarkerOptions();

		markerOptions.snippet(Area);
		markerOptions.position(point);
		markerOptions.title(title);

		// Adding marker on the Google Map
		googleMap.addMarker(markerOptions);
	}

	private void drawMarkerpoint(LatLng point) {
		// Creating an instance of MarkerOptions
		MarkerOptions markerOptions = new MarkerOptions();

		// Setting latitude and longitude for the marker
		markerOptions.position(point);
		markerOptions.title(point.toString());
		markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		// Adding marker on the Google Map
		googleMap.addMarker(markerOptions);
	}*/

	private void GetLabData() {
		progressCircle.setVisibility(View.VISIBLE);
		linear.setVisibility(View.GONE);

		try {
			sendData = new JSONObject();
			sendData.put("CentreId", CentreId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url = "http://clinic02.cloudchowk.com:1024/LabService.asmx/GetCentreData";

		jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {

				System.out.println(response);
				progressCircle.setVisibility(View.GONE);
				linear.setVisibility(View.VISIBLE);

				try {
					String imageData = response.getString("d");
					JSONObject cut = new JSONObject(imageData);
					centreArray = cut.getJSONArray("Table");
					tvEmail.setText(centreArray.getJSONObject(0).getString("Website").equals("NA") ? "Not Available"
							: centreArray.getJSONObject(0).getString("Website"));
					tvContact.setText(centreArray.getJSONObject(0).getString("ContactNoMobile").equals("NA")
							? "Not Available" : centreArray.getJSONObject(0).getString("ContactNoMobile"));
					tvAddress.setText(centreArray.getJSONObject(0).getString("FullAddress") + ", "
							+ centreArray.getJSONObject(0).getString("AreaName") + ", "
							+ centreArray.getJSONObject(0).getString("CityName") + ", "
							+ centreArray.getJSONObject(0).getString("StateName") + ", "
							+ centreArray.getJSONObject(0).getString("CountryName"));
					tvRadio.setText(centreArray.getJSONObject(0).getString("RadiologyService").equals("true")
							? "Available" : "Not Available");
					tvPath.setText(centreArray.getJSONObject(0).getString("PathologyService").equals("true")
							? "Available" : "Not Available");

					if (centreArray.getJSONObject(0).getString("TwentyFour").equals("true")) {

						layoutHours.setVisibility(View.GONE);
						layoutOpen.setVisibility(View.VISIBLE);
					} else {

						layoutOpen.setVisibility(View.GONE);
						layoutHours.setVisibility(View.VISIBLE);
						monday.setText(centreArray.getJSONObject(0).getString("MondayTime"));
						tuesday.setText(centreArray.getJSONObject(0).getString("TuesdayTime"));
						wednesday.setText(centreArray.getJSONObject(0).getString("WednesdayTime"));
						thursday.setText(centreArray.getJSONObject(0).getString("ThursdayTime"));
						friday.setText(centreArray.getJSONObject(0).getString("FridayTime"));
						saturday.setText(centreArray.getJSONObject(0).getString("SaturdayTime"));
						sunday.setText(centreArray.getJSONObject(0).getString("SundayTime"));
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				System.out.println(error);
				progressCircle.setVisibility(View.GONE);
				linear.setVisibility(View.VISIBLE);

			}
		}) {

		};
		queue.add(jr);
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();

	}

	public String getPath(Uri uri, Activity activity) {

		String[] projection = { MediaColumns.DATA };
		Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);

	}

	@Override
	protected void onStart() {
		super.onStart();
		IntentFilter f = new IntentFilter();
		f.addAction(UploadService.UPLOAD_STATE_CHANGED_ACTION);
		registerReceiver(uploadStateReceiver, f);
	}

	@Override
	protected void onStop() {
		unregisterReceiver(uploadStateReceiver);
		super.onStop();
	}

	private BroadcastReceiver uploadStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle b = intent.getExtras();
			// Toast.makeText(Filevault.this,"Image Uploaded Successfully"
			// ,Toast.LENGTH_LONG ).show();
			System.out.println(b.getString(UploadService.MSG_EXTRA));
			int percent = b.getInt(UploadService.PERCENT_EXTRA);
			/*
			 * bar.setIndeterminate(percent < 0); bar.setProgress(percent);
			 */
		}
	};

	private String getPathFromContentUri(Uri uri) {
		String path = uri.getPath();
		if (uri.toString().startsWith("content://")) {
			String[] projection = { MediaColumns.DATA };
			ContentResolver cr = getApplicationContext().getContentResolver();
			Cursor cursor = cr.query(uri, projection, null, null, null);
			if (cursor != null) {
				try {
					if (cursor.moveToFirst()) {
						path = cursor.getString(0);
					}
				} finally {
					cursor.close();
				}
			}

		}
		return path;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		try {
			if (requestCode == PICK_FROM_GALLERY) {

				Uri selectedImageUri = data.getData();

				String path = getPathFromContentUri(selectedImageUri);
				System.out.println(path);

				File imageFile = new File(path);

				long check = ((imageFile.length() / 1024));
				if (check < 2500) {

					Intent intent = new Intent(MapClass.this, UploadService.class);
					intent.putExtra(UploadService.ARG_FILE_PATH, path);
					startService(intent);

					System.out.println("After Service");

					String tempPath = getPath(selectedImageUri, MapClass.this);
					Bitmap bm;
					BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
					btmapOptions.inSampleSize = 4;
					bm = BitmapFactory.decodeFile(tempPath, btmapOptions);

					if (bm != null) {

						System.out.println("in onactivity");
						byteArrayOutputStream = new ByteArrayOutputStream();
						bm.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
						byteArray = byteArrayOutputStream.toByteArray();

						pic_Map = Base64.encodeToString(byteArray, Base64.DEFAULT);
						picname = "b.jpg";
						pic_Map = "data:image/jpeg;base64," + pic_Map;

						// sendData = new JSONObject();
						// sendData.put("PatientId", id);
						// sendData.put("File", pic);
						// sendData.put("FileName", picname);
						// System.out.println(sendData);
						//
						// RequestQueue queue1 = Volley.newRequestQueue(this);
						// String url = Services.init
						// +
						// "/PatientModule/PatientService.asmx/PatientFileVault";
						//
						// JsonObjectRequest jr1 = new JsonObjectRequest(
						// Request.Method.POST, url, sendData,
						// new Response.Listener<JSONObject>() {
						// @Override
						// public void onResponse(JSONObject response) {
						//
						// System.out.println(response);
						// try {
						//
						// thumbImage.clear();
						// imageName.clear();
						//
						// Toast.makeText(getApplicationContext(),
						// response.getString("d"),
						// Toast.LENGTH_SHORT).show();
						// queue.add(jr);
						// } catch (JSONException e) {
						// // TODO Auto-generated catch block
						// e.printStackTrace();
						// }
						//
						// }
						// }, new Response.ErrorListener() {
						// @Override
						// public void onErrorResponse(VolleyError error) {
						//
						// System.out.println("error in onactivity:"
						// + error);
						//
						// }
						// }) {
						// @Override
						// public Map<String, String> getHeaders()
						// throws AuthFailureError {
						// Map<String, String> headers = new HashMap<String,
						// String>();
						// headers.put("Cookie", Services.hoja);
						// System.out
						// .println("Services hoja:" + Services.hoja);
						// return headers;
						// }
						// };
						// queue1.add(jr1);

					}

				} else {

					Toast.makeText(this, "Image should be less than 2.5 mb.", Toast.LENGTH_LONG).show();

				}

			}

			super.onActivityResult(requestCode, resultCode, data);
			if (requestCode == PICK_FROM_CAMERA) {

				Uri selectedImageUri = Imguri;
				String path = getPathFromContentUri(selectedImageUri);
				System.out.println(path);
				File imageFile = new File(path);
				long check = ((imageFile.length() / 1024));

				if (check < 2500) {

					Intent intent = new Intent(MapClass.this, UploadService.class);
					intent.putExtra(UploadService.ARG_FILE_PATH, path);
					startService(intent);

					ContentResolver cr = getContentResolver();
					Bitmap bitmap;
					bitmap = MediaStore.Images.Media.getBitmap(cr, selectedImageUri);

					byteArrayOutputStream = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
					byteArray = byteArrayOutputStream.toByteArray();
					pic_Map = Base64.encodeToString(byteArray, Base64.DEFAULT);
					pic_Map = "data:image/jpeg;base64," + pic_Map;
					picname = "camera.jpg";

				} else {
					Toast.makeText(this, "Image should be less than 2.5 mb.", Toast.LENGTH_LONG).show();
				}

				// Bundle extras = data.getExtras();
				// Bitmap bitmap1 = (Bitmap) extras.get("data");
				//
				// // bitmap = Bitmap.createScaledBitmap(bitmap1, 250, 250,
				// true);
				//
				// byteArrayOutputStream = new ByteArrayOutputStream();
				// bitmap1.compress(Bitmap.CompressFormat.PNG, 100,
				// byteArrayOutputStream);
				// byteArray = byteArrayOutputStream.toByteArray();
				// pic = Base64.encodeToString(byteArray, Base64.DEFAULT);
				//
				// pic = "data:image/jpeg;base64," + pic;
				// picname = "camera.jpg";
				//
				// sendData = new JSONObject();
				// sendData.put("PatientId", id);
				// sendData.put("File", pic);
				// sendData.put("FileName", picname);
				//
				// RequestQueue queue1 = Volley.newRequestQueue(this);
				// String url = Services.init
				// + "/PatientModule/PatientService.asmx/PatientFileVault";
				//
				// JsonObjectRequest jr1 = new JsonObjectRequest(
				// Request.Method.POST, url, sendData,
				// new Response.Listener<JSONObject>() {
				// @Override
				// public void onResponse(JSONObject response) {
				//
				// System.out.println(response);
				// try {
				//
				// thumbImage.clear();
				// imageName.clear();
				//
				// queue.add(jr);
				// Toast.makeText(getApplicationContext(),
				// response.getString("d"),
				// Toast.LENGTH_SHORT).show();
				//
				// } catch (JSONException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				//
				//
				// }
				// }, new Response.ErrorListener() {
				// @Override
				// public void onErrorResponse(VolleyError error) {
				//
				// }
				// }) {
				// @Override
				// public Map<String, String> getHeaders()
				// throws AuthFailureError {
				// Map<String, String> headers = new HashMap<String, String>();
				// headers.put("Cookie", Services.hoja);
				// return headers;
				// }
				// };
				// queue1.add(jr1);

			}

		} catch (Exception e) {

		}
		/* super.onActivityResult(requestCode, resultCode, data); */
	}

	// @Override
	/*public boolean onCreateOptionsMenu(Menu menu) {
		//	 Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
*/

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			super.onBackPressed();			
		}
		return true;
	}
}
