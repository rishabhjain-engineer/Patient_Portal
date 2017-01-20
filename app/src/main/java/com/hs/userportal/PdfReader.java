package com.hs.userportal;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

@SuppressLint("SetJavaScriptEnabled")
public class PdfReader extends ActionBarActivity {

	private WebView webview_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pdfreader);

		ActionBar action = getSupportActionBar();
		action.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3cbed8")));
		action.setIcon(new ColorDrawable(Color.parseColor("#3cbed8")));
		action.setDisplayHomeAsUpEnabled(true);

		// Actionbar with search option

		// enable ActionBar app icon to behave as action to toggle nav drawer

		
		// getActionBar().setDisplayShowTitleEnabled(false);

		// removing app icon from Actionbar
		action.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

		Intent in = getIntent();
		String pdfpath = in.getStringExtra("image_url");
		String pdfname = in.getStringExtra("imagename");
		webview_id = (WebView) findViewById(R.id.webview_id);
		webview_id.getSettings().setJavaScriptEnabled(true);
		webview_id.getSettings().setBuiltInZoomControls(true);
		webview_id.loadUrl("https://docs.google.com/gview?embedded=true&url=" + pdfpath);

		// new DownloadFile().execute(pdfpath, pdfname);
	}

	

	/*
	 * public void download(View v) { new
	 * DownloadFile().execute("http://maven.apache.org/maven-1.x/maven.pdf",
	 * "maven.pdf"); }
	 */
	public void view(View v) {

	}

	private class DownloadFile extends AsyncTask<String, Void, String> {
		String fileName;

		@Override
		protected String doInBackground(String... strings) {
			String fileUrl = strings[0]; // ->https://files.healthscion.com/62181ffc-6f94-4b83-9334-395b8cb0960d/FileVault/d_2015-05-13
											// (1).pdf
			fileName = strings[1]; // ->d_2015-05-13 (1).pdf
			String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
			File folder = new File(extStorageDirectory, "filevaltpdf");
			folder.mkdir();

			File pdfFile = new File(folder, fileName);

			try {
				pdfFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			FileDownloader.downloadFile(fileUrl, pdfFile);
			return null;
		}

		protected void onPostExecute(String result) {

			File pdfFile = new File(Environment.getExternalStorageDirectory() + "/filevaltpdf/" + fileName); // ->
																												// filename
																												// =
																												// maven.pdf
			Uri path = Uri.fromFile(pdfFile);
			Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
			pdfIntent.setDataAndType(path, "application/pdf");
			pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			try {
				startActivity(pdfIntent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(PdfReader.this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
			}

		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			super.onBackPressed();
		}
		return true;
	}

}