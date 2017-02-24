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

    protected JSONArray getJsonForWeekly(String date1, String date2) {

        String dateArray[] = date1.split("/");
        String monthInString = dateArray[1];
        String yearInString = dateArray[2];
        int monthInInt = Integer.parseInt(monthInString);
        int yearInInt = Integer.parseInt(yearInString);
        if (monthInInt <= 3) {
            monthInInt = 4;
        } else if (monthInInt <= 6) {
            monthInInt = 7;
        } else if (monthInInt <= 9) {
            monthInInt = 10;
        } else if (monthInInt <= 12) {
            monthInInt = 1;
            yearInInt = yearInInt + 1;
        }

        date1 = "01/" + monthInInt + "/" + yearInInt;

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
                Log.i("Weekly", "Weekly: " + date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long epoch = date.getTime();
            // JSONArray innerJsonArray = new JSONArray();
            jsonArray.put(epoch);
            //  jsonArray.put(innerJsonArray);
            beginCalendar.add(Calendar.MONTH, 6);
        }
        return jsonArray;

    }

    protected JSONArray getJsonForMonthly(String date1, String date2) {
        String dateArray[] = date1.split("/");
        String monthInString = dateArray[1];
        String yearInString = dateArray[2];
        int monthInInt = Integer.parseInt(monthInString);
        int yearInInt = Integer.parseInt(yearInString);
        //int mont = Integer.parseInt(month);
        if (monthInInt == 12) {
            monthInInt = 1;
            yearInInt = yearInInt + 1;
        } else {
            monthInInt = monthInInt + 1;
        }
        date1 = "01/" + monthInInt + "/" + yearInInt;
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

        String dateArray[] = date1.split("/");
        String monthInString = dateArray[1];
        String yearInString = dateArray[2];
        int monthInInt = Integer.parseInt(monthInString);
        int yearInInt = Integer.parseInt(yearInString);
        if (monthInInt <= 3) {
            monthInInt = 4;
        } else if (monthInInt <= 6) {
            monthInInt = 7;
        } else if (monthInInt <= 9) {
            monthInInt = 10;
        } else if (monthInInt <= 12) {
            monthInInt = 1;
            yearInInt = yearInInt + 1;
        }

        date1 = "01/" + monthInInt + "/" + yearInInt;

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

        String dateArray[] = date1.split("/");
        String monthInString = dateArray[1];
        String yearInString = dateArray[2];
        int monthInInt = Integer.parseInt(monthInString);
        int yearInInt = Integer.parseInt(yearInString);
        if (monthInInt <= 6) {
            monthInInt = 7;
        } else {
            monthInInt = 1;
            yearInInt = yearInInt + 1;
        }

        date1 = "01/" + monthInInt + "/" + yearInInt;

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

        String dateArray[] = date1.split("/");
        String yearInString = dateArray[2];
        int yearInInt = Integer.parseInt(yearInString);
        yearInInt = yearInInt + 1;
        date1 = "01/01/" + yearInInt;

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
