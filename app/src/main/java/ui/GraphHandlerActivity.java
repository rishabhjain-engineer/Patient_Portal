package ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.hs.userportal.R.id.date;

/**
 * Created by ayaz on 23/2/17.
 */

public class GraphHandlerActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    protected JSONArray getJsonForDaily(String dateString1, String dateString2) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = dateFormat.parse(dateString1);
            date2 = dateFormat.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        JSONArray jsonArray = new JSONArray();
        while (!calendar.after(cal2)) {
            Date dateToConvert = calendar.getTime();
            long epoch = dateToConvert.getTime();
            JSONArray innerJsonArray = new JSONArray();
            innerJsonArray.put(epoch);
            jsonArray.put(innerJsonArray);

            calendar.add(Calendar.DATE, 1);
        }
        return jsonArray;
    }
    //[1399787880000, 1447669500000, 1448928000000, 1451606400000, 1454284800000, 1456790400000, 1459468800000, 1468195946000];

    protected JSONArray getJsonForMonthly(String date1, String date2) {
        if (!date1.startsWith("01")) {
            String dateArray[] = date1.split("/");
            String month = dateArray[1];
            int mont = Integer.parseInt(month);
            mont = mont + 1;
            date1 = "01/" + mont + "/" + dateArray[2];
        }

        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
        Calendar beginCalendar = Calendar.getInstance();
        Calendar finishCalendar = Calendar.getInstance();
        try {
            beginCalendar.setTime(formater.parse(date1));
            finishCalendar.setTime(formater.parse(date2));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONArray jsonArray = new JSONArray();
        while (beginCalendar.before(finishCalendar)) {
            String dateInString = formater.format(beginCalendar.getTime()).toUpperCase();
            Log.i("ayaz", "Date: " + date);
            Date date = null;
            try {
                date = formater.parse(dateInString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long epoch = date.getTime();
            JSONArray innerJsonArray = new JSONArray();
            innerJsonArray.put(epoch);
            jsonArray.put(innerJsonArray);
            beginCalendar.add(Calendar.MONTH, 1);
        }

        return jsonArray;
    }
}
