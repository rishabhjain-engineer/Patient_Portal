package com.hs.userportal;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;

public class DeviceJudge {

	private static String tag="MainActivity";

	public static boolean isTabletDevice(Context activityContext) {
	    // Verifies if the Generalized Size of the device is XLARGE to be
	    // considered a Tablet
	    boolean xlarge = ((activityContext.getResources().getConfiguration().screenLayout & 
	                        Configuration.SCREENLAYOUT_SIZE_MASK) == 
	                        Configuration.SCREENLAYOUT_SIZE_XLARGE);

	    // If XLarge, checks if the Generalized Density is at least MDPI
	    // (160dpi)
	    if (xlarge) {
	        DisplayMetrics metrics = new DisplayMetrics();
	        Activity activity = (Activity) activityContext;
	        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

	        // MDPI=160, DEFAULT=160, DENSITY_HIGH=240, DENSITY_MEDIUM=160,
	        // DENSITY_TV=213, DENSITY_XHIGH=320
	        if (metrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT
	                || metrics.densityDpi == DisplayMetrics.DENSITY_HIGH
	                || metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM
	                || metrics.densityDpi == DisplayMetrics.DENSITY_TV
	                || metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH) {

	            // Yes, this is a tablet!
	            return true;
	        }
	    }

	    // No, this is not a tablet!
	    return false;
	}
	public boolean getStreamType(){
		//åˆ¤æ–·æ˜¯å�¦ç‚ºé�©ç”¨é«˜ç•«è³ªè¨Šæº�
		//Version Reference-->  http://developer.android.com/reference/android/os/Build.VERSION_CODES.html
		boolean isHLS=false;
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH /** Ice cream sandwich is 4.0 */){
		    // Do something for froyo and above versions
			isHLS=true;
		} else{
		    // do something for phones running an SDK before froyo
			return isHLS;
		}
		return isHLS;
	}
	 public static String getDisplayDpi(Activity activity){
		 String dpi="XHDPI";
		 DisplayMetrics metrics = new DisplayMetrics();
		 activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		 switch(metrics.densityDpi){
		      case DisplayMetrics.DENSITY_LOW:
		    	       Log.v(tag,"LDPI");
		    	             dpi="LDPI";
		                 break;
		      case DisplayMetrics.DENSITY_MEDIUM:
		    	       Log.v(tag,"MDPI");
		    	       		dpi="MDPI";
		    	             break;
		      case DisplayMetrics.DENSITY_HIGH:
		    	       Log.v(tag,"HDPI");
		    	       		dpi="HDPI";
		    	  			 break;
		      case DisplayMetrics.DENSITY_XHIGH:
		    	       Log.v(tag,"XHDPI");
		    	       		dpi="XHDPI";
		    	             break;
		      case DisplayMetrics.DENSITY_XXHIGH:
		    	  	   Log.v(tag,"XXHDPI");		
		    	  	        dpi="XXHDPI";
		    	             break;
		 }
		 return dpi;
	 }
	 public static int getScreenWidthSize(Activity activity){
		 DisplayMetrics metrics = new DisplayMetrics();
		 activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		 int height=metrics.heightPixels;
		 int width=metrics.widthPixels;
		 //Log.v(tag,"Width="+String.valueOf(width)+" height="+String.valueOf(height));
	     return width;
	 }
	 public static int getScreenHeightSize(Activity activity){
		 DisplayMetrics metrics = new DisplayMetrics();
		 activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		 int height=metrics.heightPixels;
		 int width=metrics.widthPixels;
		// Log.v(tag,"Width="+String.valueOf(width)+" height="+String.valueOf(height));
	     return height;
	 }
}
