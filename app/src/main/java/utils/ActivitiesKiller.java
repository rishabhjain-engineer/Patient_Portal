package utils;

import android.app.Activity;

/**
 * Created by ashish on 2/23/2016.
 */
public class ActivitiesKiller
{
    public static void activityKiller(Activity activity)
    {
        if(activity != null)
        {
            activity.finish();
        }
    }
}
