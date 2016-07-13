package com.cloudchowk.patient;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import android.support.v4.util.LruCache;

public class MyLruBitmapCache extends LruCache<String, Bitmap> implements
		ImageCache

{
	public MyLruBitmapCache(int maxSize) {
		super(maxSize);
	}

	public MyLruBitmapCache(Context ctx) {
		this(getCacheSize(ctx));
	}

	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}

	@Override
	public Bitmap getBitmap(String url) {
		return get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		put(url, bitmap);
	}

	public static int getCacheSize(Context ctx) {
		final DisplayMetrics displayMetrics = ctx.getResources()
				.getDisplayMetrics();
		final int screenWidth = displayMetrics.widthPixels;
		final int screenHeight = displayMetrics.heightPixels;
		// 4 bytes per pixel
		final int screenBytes = screenWidth * screenHeight * 4;

		return screenBytes * 3;
	}
}