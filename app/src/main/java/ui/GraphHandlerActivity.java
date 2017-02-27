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

import utils.AppConstant;

import static com.hs.userportal.R.id.date;

/**
 * Created by ayaz on 23/2/17.
 */

public class GraphHandlerActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //[1399787880000, 1447669500000, 1448928000000, 1451606400000, 1454284800000, 1456790400000, 1459468800000, 1468195946000];

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
        long epoch = 0;
        boolean isToAddFirsValue = true;
        while (!calendar.after(cal2)) {
            Date dateToConvert = calendar.getTime();
            Log.i("Daily", "Date: " + dateToConvert);
            epoch = dateToConvert.getTime();
            // JSONArray innerJsonArray = new JSONArray();
            jsonArray.put(epoch);
            //jsonArray.put(innerJsonArray);

            calendar.add(Calendar.DATE, 1);
        }
        return jsonArray;
    }

    protected JSONArray getJsonForWeekly(String startDate, String endDate) {

        //Old: Start from next Monday after first date till Monday before last date (if last date isn’t a Monday)
        //New: Start from Monday before first date till Monday right after last date

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = dateFormat.parse(startDate);
            date2 = dateFormat.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        JSONArray jsonArray = new JSONArray();
        while (!calendar.after(cal2)) {
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                Date dateToConver = calendar.getTime();
                long epoch = dateToConver.getTime();
                Log.i("Weekly", "Date: " + dateToConver);
                jsonArray.put(epoch);
            }
            calendar.add(Calendar.DATE, 1);
        }

        while (calendar.after(cal2)) {
            boolean isToQuit = false;
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                Date dateToConver = calendar.getTime();
                long epoch = dateToConver.getTime();
                Log.i("Weekly", "Date: " + dateToConver);
                jsonArray.put(epoch);
                isToQuit = true;
            }
            if (isToQuit) {
                break;
            }
            calendar.add(Calendar.DATE, 1);
        }
        Log.i("ayaz", "json: " + jsonArray);
        return jsonArray;
    }

    protected JSONArray getJsonForMonthly(String date1, String date2) {

        // from first-1 to last+1
        String dateArray[] = date1.split("/");
        String monthInString = dateArray[1];
        String yearInString = dateArray[2];
        int monthInInt = Integer.parseInt(monthInString);
        int yearInInt = Integer.parseInt(yearInString);
        //int mont = Integer.parseInt(month);
        if (monthInInt == 1) {
            monthInInt = 12;
            yearInInt = yearInInt - 1;
        }else{
            monthInInt =   monthInInt -1;;
        }
        date1 = "01/" + monthInInt + "/" + yearInInt;

        String dateArray2[] = date2.split("/");
        String monthInString2 = dateArray2[1];
        String yearInString2 = dateArray2[2];
        int monthInInt2 = Integer.parseInt(monthInString2);
        int yearInInt2 = Integer.parseInt(yearInString2);
        //int mont = Integer.parseInt(month);
        if (monthInInt2 == 12) {
            monthInInt2 = 2;
            yearInInt2 = yearInInt2 + 1;
        } else if (monthInInt2 == 11) {
            monthInInt2 = 1;
            yearInInt2 = yearInInt2 + 1;
        } else {
            monthInInt2 = monthInInt2 + 2;
        }

        date2 = "01/" + monthInInt2 + "/" + yearInInt2;


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
            Log.i("ayaz", "Date: " + dateInString);
            Date date = null;
            try {
                date = formater.parse(dateInString);
                Log.i("monthly", "monthly: " + date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long epoch = date.getTime();
            //JSONArray innerJsonArray = new JSONArray();
            //innerJsonArray.put(epoch);
            //jsonArray.put(innerJsonArray);
            jsonArray.put(epoch);
            beginCalendar.add(Calendar.MONTH, 1);
        }

        return jsonArray;
    }

    protected JSONArray getJsonForQuaterly(String date1, String date2) {
        String dateArray1[] = date1.split("/");
        String monthInString1 = dateArray1[1];
        String yearInString1 = dateArray1[2];
        int monthInInt1 = Integer.parseInt(monthInString1);
        int yearInInt1 = Integer.parseInt(yearInString1);
        if (monthInInt1 <= 3) {
            monthInInt1 = 10;
            yearInInt1 = yearInInt1 - 1;
        } else if (monthInInt1 <= 6) {
            monthInInt1 = 1;
        } else if (monthInInt1 <= 9) {
            monthInInt1 = 4;
        } else if (monthInInt1 <= 12) {
            monthInInt1 = 7;
        }
        date1 = "01/" + monthInInt1 + "/" + yearInInt1;
        ///////////////////////////////////////////////////////////////////////

        String dateArray2[] = date2.split("/");
        String monthInString2 = dateArray2[1];
        String yearInString2 = dateArray2[2];
        int monthInInt2 = Integer.parseInt(monthInString2);
        int yearInInt2 = Integer.parseInt(yearInString2);

        if (monthInInt2 <= 3) {
            monthInInt2 = 07;
        } else if (monthInInt2 <= 6) {
            monthInInt2 = 10;
        } else if (monthInInt2 <= 9) {
            monthInInt2 = 1;
            yearInInt2 = yearInInt2 + 1;
        } else if (monthInInt2 <= 12) {
            monthInInt2 = 4;
            yearInInt2 = yearInInt2 + 1;
        }
        date2 = "01/" + monthInInt2 + "/" + yearInInt2;




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
            Date date = null;
            try {
                date = formater.parse(dateInString);
                Log.i("quaterly", "quaterly: " + date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long epoch = date.getTime();
            // JSONArray innerJsonArray = new JSONArray();
            jsonArray.put(epoch);
            // jsonArray.put(innerJsonArray);
            beginCalendar.add(Calendar.MONTH, 3);
        }
        return jsonArray;

    }

    protected JSONArray getJsonForSemiAnnually(String date1, String date2) {

        String dateArray1[] = date1.split("/");
        String monthInString1 = dateArray1[1];
        String yearInString1 = dateArray1[2];
        int monthInInt1 = Integer.parseInt(monthInString1);
        int yearInInt1 = Integer.parseInt(yearInString1);
        if (monthInInt1 <= 6) {
            monthInInt1 = 7;
            yearInInt1 = yearInInt1 - 1;
        } else {
            monthInInt1 = 1;
        }
        date1 = "01/" + monthInInt1 + "/" + yearInInt1;

        //increasing 2 , due to before in while loop

        String dateArray2[] = date2.split("/");
        String monthInString2 = dateArray2[1];
        String yearInString2 = dateArray2[2];
        int monthInInt2 = Integer.parseInt(monthInString2);
        int yearInInt2 = Integer.parseInt(yearInString2);
        if (monthInInt2 <= 6) {
            monthInInt2 = 1;
            yearInInt2 = yearInInt2 + 1;
        } else {
            monthInInt2 = 7;
            yearInInt2 = yearInInt2 + 1;
        }
        date2 = "01/" + monthInInt2 + "/" + yearInInt2;

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
            Date date = null;
            try {
                date = formater.parse(dateInString);
                Log.i("semi", "semi: " + date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long epoch = date.getTime();
            //     JSONArray innerJsonArray = new JSONArray();
            jsonArray.put(epoch);
            //   jsonArray.put(innerJsonArray);
            beginCalendar.add(Calendar.MONTH, 6);
        }
        return jsonArray;

    }

    protected JSONArray getJsonForYearly(String date1, String date2) {

        String dateArray1[] = date1.split("/");
        String yearInString1 = dateArray1[2];
        int yearInInt1 = Integer.parseInt(yearInString1);
        yearInInt1 = yearInInt1 - 1;
        date1 = "01/01/" + yearInInt1;

        String dateArray2[] = date2.split("/");
        String yearInString2 = dateArray2[2];
        int yearInInt2 = Integer.parseInt(yearInString2);
        yearInInt2 = yearInInt2 + 2;
        date2 = "01/01/" + yearInInt2;

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
            Date date = null;
            try {
                date = formater.parse(dateInString);
                Log.i("yearly", "yearly: " + date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long epoch = date.getTime();
            //   JSONArray innerJsonArray = new JSONArray();
            jsonArray.put(epoch);
            // jsonArray.put(innerJsonArray);
            beginCalendar.add(Calendar.YEAR, 1);
        }
        return jsonArray;

    }


  /*  protected JSONArray getInitialJsonForMonthly(String date1, String date2) {

        if(date1.contains("T")){
            date1 = date1.replaceAll("-", "/");
            date2 = date1.replaceAll("-", "/");

            int  indexOfT = date1.indexOf("T");
            date1 = date1.substring(0, indexOfT);
            date2 = date2.substring(0,indexOfT);
        }
        String dateArray[] = date1.split("/");
        String month = dateArray[1];
        int mont = Integer.parseInt(month);
        mont = mont + 1;
        //date1 = "01/" + mont + "/" + dateArray[0];
        date1 = dateArray[0] + "/" + mont + "/01";
        SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd");
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
            Date date = null;
            try {
                date = formater.parse(dateInString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.i("ayaz", "Date: " + date);
            long epoch = date.getTime();
            JSONArray innerJsonArray = new JSONArray();
            innerJsonArray.put(epoch);
            jsonArray.put(innerJsonArray);
            beginCalendar.add(Calendar.MONTH, 1);
        }

        return jsonArray;
    }*/
}
