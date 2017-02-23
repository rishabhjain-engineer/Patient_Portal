package utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rishabh on 23/2/17.
 */

public class Utils {

    public static boolean isDateValid(String startingDate, String endingDate, String myFormatString) {
        boolean isDateValid = false;
        try {
            SimpleDateFormat df = new SimpleDateFormat(myFormatString);
            Date endDate = df.parse(endingDate);                     // End Date ; Rishabh
            Date startDate = df.parse(startingDate);               // Start Date ; Rishabh

            if (endDate.after(startDate)){            // end date is grater than starting date
                isDateValid =  true;
            }
        }catch (Exception e){
        }
        Log.e("Rishabh", " Function value := "+isDateValid);
        return isDateValid;
    }

}
