package utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
            if (endDate.after(startDate)) {            // end date is grater than starting date
                isDateValid = true;
            }
        } catch (Exception e) {
        }
        Log.e("Rishabh", " Function value := " + isDateValid);
        return isDateValid;
    }

    public static boolean isFromDateValid(String startingDate, String myFormatString) {
        boolean isFromDateValid = false;

        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat(myFormatString);
        String dateString = formatter.format(new Date(currentTimeMillis));
        Log.e("Rishabh ", " Present DATE := " + dateString);

        try {
            SimpleDateFormat df = new SimpleDateFormat(myFormatString);
            Date startDate = df.parse(startingDate);               // Start Date ; Rishabh
            Date presentDate = df.parse(dateString);
            if (startDate.before(presentDate)) {            // from date is greater than present date
                isFromDateValid = true;
            }
        } catch (Exception e) {
        }
        Log.e("Rishabh", " isFromDateValid Function value := " + isFromDateValid);
        return isFromDateValid;
    }

    public static String correctDateFormat(String receivedDate) {
        String date =null;
        String month =null;
        String year =null;
        String correctFormatt [] = receivedDate.toString().split("-") ;
        year = correctFormatt[0];
        month = correctFormatt[1];
        date = correctFormatt[2];
        String correctDate = date + "/" + month + "/" + year ;
        return correctDate ;
    }
}
