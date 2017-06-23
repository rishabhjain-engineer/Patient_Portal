package com.hs.userportal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import ui.BaseActivity;

public class ExpandImage extends BaseActivity {
    private TouchImageView image;
    private String imgname = "", imgnamereceived = "";
    private final String path = Environment.getExternalStorageDirectory()
            .toString()
            + "/"
            + Environment.DIRECTORY_DCIM
            + "/Patient Portal";
    private ProgressBar prog;
    private Intent z;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expandimage);

        setupActionBar();

        z = getIntent();
        imgname = z.getStringExtra("image");
        imgnamereceived = z.getStringExtra("imagename");
        String[] image_namereceived = imgnamereceived.split("/");
        int len = image_namereceived.length;
        System.out.println(imgname);

        if(image_namereceived[len-1]!= null){
            mActionBar.setTitle(image_namereceived[len-1]);
            imgnamereceived = image_namereceived[len-1];
        }else{
            mActionBar.setTitle(imgnamereceived);
        }


        image = (TouchImageView) findViewById(R.id.img);
        prog = (ProgressBar) findViewById(R.id.prog);
        image.setTag(R.drawable.ic_empty);
        final ImageLoader imageLoader = MyVolleySingleton.getInstance(
                ExpandImage.this).getImageLoader();
        imageLoader.get(imgname, ImageLoader.getImageListener(image, R.drawable.box, R.drawable.ic_error));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save, menu);

        if (z.hasExtra("from")) {
            MenuItem share = (MenuItem) menu.findItem(R.id.share);
            MenuItem save = (MenuItem) menu.findItem(R.id.action_save_image);
            share.setVisible(false);
            save.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                super.onBackPressed();
                return true;

            case R.id.share:

                BitmapDrawable btmpDr1 = (BitmapDrawable) image.getDrawable();
                final Bitmap newbitMap1 = btmpDr1.getBitmap();
                if (newbitMap1 != null) {

                    Bitmap icon = newbitMap1;
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/jpeg");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    File f = new File(Environment.getExternalStorageDirectory()
                            + File.separator + /*"temporary_file.jpg"*/imgnamereceived);
                    try {
                        f.createNewFile();
                        FileOutputStream fo = new FileOutputStream(f);
                        fo.write(bytes.toByteArray());
                        fo.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    share.putExtra(Intent.EXTRA_SUBJECT, imgnamereceived);
                    share.putExtra(Intent.EXTRA_STREAM,
                            Uri.parse("file:///sdcard/" +/*"temporary_file.jpg"*/imgnamereceived));
                    startActivity(Intent.createChooser(share, "Share Image"));

                }
                return true;

            case R.id.action_save_image:

                BitmapDrawable btmpDr = (BitmapDrawable) image.getDrawable();
                final Bitmap newbitMap = btmpDr.getBitmap();

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:

                                if (newbitMap != null) {
                                    Calendar cal = Calendar.getInstance();
                                    File myDir = new File(path);
                                    myDir.mkdirs();
                                    String fname = /*"Image-"
                                    + *//*String.valueOf(cal.getTimeInMillis())
									+ ".jpg";*/imgnamereceived;
                                    File file = new File(myDir, fname);
                                    if (file.exists())
                                        file.delete();
                                    try {
                                        FileOutputStream out = new FileOutputStream(
                                                file);
                                        newbitMap.compress(Bitmap.CompressFormat.JPEG,
                                                90, out);
                                        out.flush();
                                        out.close();
                                        Toast.makeText(ExpandImage.this,
                                                "Image saved on " + path,
                                                Toast.LENGTH_SHORT).show();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    if (Build.VERSION.SDK_INT >= 19) {
                                        Intent mediaScanIntent = new Intent(
                                                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                        File f = new File(path, fname);
                                        Uri contentUri = Uri.fromFile(f);
                                        mediaScanIntent.setData(contentUri);
                                        sendBroadcast(mediaScanIntent);
                                    } else {
                                        sendBroadcast(new Intent(
                                                Intent.ACTION_MEDIA_MOUNTED,
                                                Uri.parse("file://"
                                                        + Environment
                                                        .getExternalStorageDirectory())));
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Select Image first", Toast.LENGTH_SHORT)
                                            .show();
                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                Toast.makeText(ExpandImage.this, "Cancel", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to save?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
