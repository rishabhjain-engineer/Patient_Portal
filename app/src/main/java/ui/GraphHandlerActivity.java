package ui;

import android.os.Bundle;
import android.support.annotation.IntegerRes;
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

import static com.hs.userportal.R.drawable.calendar;
import static com.hs.userportal.R.id.date;

/**
 * Created by ayaz on 23/2/17.
 */

public class GraphHandlerActivity extends BaseActivity {

    private List<String> mDateList = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setDateList(List<String> dateList) {
        mDateList = dateList;
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
        boolean prevLoopRun = true, nextLoopRun = true;
        try {
            date1 = dateFormat.parse(startDate);
            date2 = dateFormat.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar beforePresentDate = Calendar.getInstance();
        beforePresentDate.setTime(date1);
        beforePresentDate.add(Calendar.DATE, -1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        JSONArray jsonArray = new JSONArray();
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY && !mDateList.contains(startDate)) {
            prevLoopRun = false;
        }
        while (beforePresentDate.before(calendar) && prevLoopRun == true) {
            boolean isToQuit = false;
            if (beforePresentDate.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                Date dateToConver = beforePresentDate.getTime();
                long epoch = dateToConver.getTime();
                Log.i("Weekly", "Date: " + dateToConver);
                jsonArray.put(epoch);
                isToQuit = true;
            }
            if (isToQuit) {
                break;
            }
            beforePresentDate.add(Calendar.DATE, -1);
        }
        while (!calendar.after(cal2)) {
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                Date dateToConver = calendar.getTime();
                long epoch = dateToConver.getTime();
                Log.i("Weekly", "Date: " + dateToConver);
                jsonArray.put(epoch);
            }
            calendar.add(Calendar.DATE, 1);
        }
        if (cal2.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY && !mDateList.contains(endDate)) {
            nextLoopRun = false;
        }
        while (calendar.after(cal2) && nextLoopRun == true) {
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

        boolean isFirstDateOfMonth = false;
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");

        String dateArray[] = date1.split("/");
        String dayInString = dateArray[0];
        String monthInString = dateArray[1];
        String yearInString = dateArray[2];

        int dayInInt = Integer.parseInt(dayInString);
        int monthInInt = Integer.parseInt(monthInString);
        int yearInInt = Integer.parseInt(yearInString);

        if (dayInInt == 1) {
            isFirstDateOfMonth = true;
        }

        if (isFirstDateOfMonth && mDateList.contains(date1)) {
            if (monthInInt == 1) {
                monthInInt = 12;
                yearInInt = yearInInt - 1;
            } else {
                monthInInt = monthInInt - 1;
            }
        }

        if (monthInInt < 10) {
            date1 = "01/0" + monthInInt + "/" + yearInInt;

        } else {
            date1 = "01/" + monthInInt + "/" + yearInInt;
        }


        String dateArray2[] = date2.split("/");
        String dayInString2 = dateArray2[0];
        String monthInString2 = dateArray2[1];
        String yearInString2 = dateArray2[2];
        int dayInInt2 = Integer.parseInt(dayInString2);
        int monthInInt2 = Integer.parseInt(monthInString2);
        int yearInInt2 = Integer.parseInt(yearInString2);

        if (dayInInt2 == 1 && mDateList.contains(date2)) {
            if (monthInInt2 == 12) {
                monthInInt2 = 2;
                yearInInt2 = yearInInt2 + 1;
            } else if (monthInInt2 == 11) {
                monthInInt2 = 1;
                yearInInt2 = yearInInt2 + 1;
            } else {
                monthInInt2 = monthInInt2 + 2;
            }
        } else {
            if (monthInInt2 == 12) {
                monthInInt2 = 1;
                yearInInt2 = yearInInt2 + 1;
            } else {
                monthInInt2 = monthInInt2 + 1;
            }
        }

        if (monthInInt2 < 10) {
            date2 = "01/0" + monthInInt2 + "/" + yearInInt2;
        } else {
            date2 = "01/" + monthInInt2 + "/" + yearInInt2;
        }

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
                Log.i("monthly", "monthly: " + date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long epoch = date.getTime();
            //   JSONArray innerJsonArray = new JSONArray();
            jsonArray.put(epoch);
            // jsonArray.put(innerJsonArray);
            beginCalendar.add(Calendar.MONTH, 1);
        }
        return jsonArray;
    }

    protected JSONArray getJsonForQuaterly(String date1, String date2) {

        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");

        String dateArray1[] = date1.split("/");
        String dayInString1 = dateArray1[0];
        String monthInString1 = dateArray1[1];
        String yearInString1 = dateArray1[2];
        int dayInInt1 = Integer.parseInt(dayInString1);
        int monthInInt1 = Integer.parseInt(monthInString1);
        int yearInInt1 = Integer.parseInt(yearInString1);

        if (mDateList.contains(date1) && dayInInt1 == 1 && (monthInInt1 == 1 || monthInInt1 == 4 || monthInInt1 == 7 || monthInInt1 == 10)){
            if (monthInInt1 == 1) {
                monthInInt1 = 10;
                yearInInt1 = yearInInt1 - 1;
            } else if (monthInInt1 == 4) {
                monthInInt1 = 1;
            } else if (monthInInt1 == 7) {
                monthInInt1 = 4;
            } else if (monthInInt1 == 10) {
                monthInInt1 = 7;
            }
        } else {
            if (monthInInt1 <= 3) {
                monthInInt1 = 1;
            } else if (monthInInt1 <= 6) {
                monthInInt1 = 4;
            } else if (monthInInt1 <= 9) {
                monthInInt1 = 7;
            } else if (monthInInt1 <= 12) {
                monthInInt1 = 10;
            }
        }

        if (monthInInt1 < 10) {
            date1 = "01/0" + monthInInt1 + "/" + yearInInt1;
        } else {
            date1 = "01/" + monthInInt1 + "/" + yearInInt1;
        }

        String dateArray2[] = date2.split("/");
        String dayInString2 = dateArray2[0];
        String monthInString2 = dateArray2[1];
        String yearInString2 = dateArray2[2];
        int dayInInt2 = Integer.parseInt(dayInString2);
        int monthInInt2 = Integer.parseInt(monthInString2);
        int yearInInt2 = Integer.parseInt(yearInString2);

        if (mDateList.contains(date2) && (monthInInt1 == 1 || monthInInt1 == 4 || monthInInt1 == 7 || monthInInt1 == 10) && dayInInt2 == 1) {
            if (monthInInt2 == 1) {
                monthInInt2 = 7;
            } else if (monthInInt2 == 4) {
                monthInInt2 = 10;
            } else if (monthInInt2 == 7) {
                monthInInt2 = 1;
                yearInInt2 = yearInInt2 + 1;
            } else if (monthInInt2 == 10) {
                monthInInt2 = 4;
                yearInInt2 = yearInInt2 + 1;
            }
        } else {
            /*if (monthInInt2 <= 3) {
                monthInInt2 = 4;
            } else if (monthInInt2 <= 6) {
                monthInInt2 = 7;
            } else if (monthInInt2 <= 9) {
                monthInInt2 = 10;
            } else if (monthInInt2 <= 12) {
                monthInInt2 = 1;
                yearInInt2 = yearInInt2 + 1;
            }*/
            
            //Taking Next Quarter e.g 6 july then we will show upto 1 october not till 1 july only
            if (monthInInt2 == 1) {
                monthInInt2 = 7;
            } else if (monthInInt2 == 4) {
                monthInInt2 = 10;
            } else if (monthInInt2 == 7) {
                monthInInt2 = 1;
                yearInInt2 = yearInInt2 + 1;
            } else if (monthInInt2 == 10) {
                monthInInt2 = 4;
                yearInInt2 = yearInInt2 + 1;
            }
        }

        if (monthInInt2 < 10) {
            date2 = "01/0" + monthInInt2 + "/" + yearInInt2;
        } else {
            date2 = "01/" + monthInInt2 + "/" + yearInInt2;
        }
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
                Log.i("Quaterly", "quaterly: " + date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long epoch = date.getTime();
            jsonArray.put(epoch);
            beginCalendar.add(Calendar.MONTH, 3);
        }
        return jsonArray;

    }

    protected JSONArray getJsonForSemiAnnually(String date1, String date2) {

         /*//One previous and one later semiYear is Included
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
        date2 = "01/" + monthInInt2 + "/" + yearInInt2;*/

        String dateArray1[] = date1.split("/");
        String monthInString1 = dateArray1[1];
        String yearInString1 = dateArray1[2];
        int monthInInt1 = Integer.parseInt(monthInString1);
        int yearInInt1 = Integer.parseInt(yearInString1);
        if (monthInInt1 <= 6) {
            monthInInt1 = 1;
        } else {
            monthInInt1 = 7;
        }
        date1 = "01/" + monthInInt1 + "/" + yearInInt1;
        String dateArray2[] = date2.split("/");
        String monthInString2 = dateArray2[1];
        String yearInString2 = dateArray2[2];
        int monthInInt2 = Integer.parseInt(monthInString2);
        int yearInInt2 = Integer.parseInt(yearInString2);
        if (monthInInt2 <= 6) {
            monthInInt2 = 7;
        } else {
            monthInInt2 = 1;
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
        //yearInInt1 = yearInInt1 - 1;  //For one year before
        date1 = "01/01/" + yearInInt1;

        String dateArray2[] = date2.split("/");
        String yearInString2 = dateArray2[2];
        int yearInInt2 = Integer.parseInt(yearInString2);
        //yearInInt2 = yearInInt2 + 2;  //For one year after
        yearInInt2 = yearInInt2 + 1;    //For current year
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

    private boolean isLastDateOfMonth(String date1) {

        String date2 = null;
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
        Calendar beginCalendar = Calendar.getInstance();
        try {
            beginCalendar.setTime(formater.parse(date1));
            beginCalendar.set(Calendar.DAY_OF_MONTH, beginCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));

            date2 = formater.format(beginCalendar.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        boolean result = false;
        if (date1 != null && date2 != null) {
            String date1Array[] = date1.split("/");
            String date2Array[] = date2.split("/");
            int day1Date = Integer.parseInt(date1Array[0]);
            int day2Date = Integer.parseInt(date2Array[0]);
            if (day1Date == day2Date) {
                result = true;
            }
        }
        return result;
    }

    protected JSONArray getJsonForMonthlyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy(String date1, String date2) {

        boolean isFirstDateOfMonth = false;

        String lastDate0fMonth = null ;
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
        Date rangeFromMonth =null ;
        Date rangeToMonth = null ;
        Date firstdateofmonth = null ;
        Date lastdateofmonth = null ;
        try {
            rangeFromMonth = formater.parse(date1);
            rangeToMonth = formater.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dateArray[] = date1.split("/");
        String dayInString = dateArray[0];
        String monthInString = dateArray[1];
        String yearInString = dateArray[2];
        int dayInInt = Integer.parseInt(dayInString);
        int monthInInt = Integer.parseInt(monthInString);
        int yearInInt = Integer.parseInt(yearInString);
       /* if (dayInInt == 1) {  //Not going to back one month if it is month's initial date
            if (monthInInt == 1) {
                monthInInt = 12;
                yearInInt = yearInInt - 1;
            } else {
                monthInInt = monthInInt - 1;
            }
        }*/
        if(monthInInt < 10){
            date1 = "01/0" + monthInInt + "/" + yearInInt;

        }else{
            date1 = "01/" + monthInInt + "/" + yearInInt;
        }

        String dateArray2[] = date2.split("/");
        String dayInString2 = dateArray2[0];
        String monthInString2 = dateArray2[1];
        String yearInString2 = dateArray2[2];
        int monthInInt2 = Integer.parseInt(monthInString2);
        int yearInInt2 = Integer.parseInt(yearInString2);
        //int mont = Integer.parseInt(month);
        if (isLastDateOfMonth(date2)) {
            if (monthInInt2 == 12) {
                monthInInt2 = 1;
                yearInInt2 = yearInInt2 + 1;
            } else if (monthInInt2 == 11) {
                monthInInt2 = 12;
            } else {
                monthInInt2 = monthInInt2 + 1;
            }
        } else {
            if (monthInInt2 == 12) {
                monthInInt2 = 1;
                yearInInt2 = yearInInt2 + 1;
            } else {
                monthInInt2 = monthInInt2 + 1;
            }
        }

        if(monthInInt2 <10 ){
            date2 = "01/0" + monthInInt2 + "/" + yearInInt2;

        }else{
            date2 = "01/" + monthInInt2 + "/" + yearInInt2;
        }


        Calendar endOfMonthDate = Calendar.getInstance();
        endOfMonthDate.setTime(rangeToMonth);
        endOfMonthDate.set(Calendar.DAY_OF_MONTH, endOfMonthDate.getActualMaximum(Calendar.DAY_OF_MONTH));
        lastDate0fMonth = formater.format(endOfMonthDate.getTime()) ;

        Calendar beforePresentMonth = Calendar.getInstance();
        Calendar afterEndMonth = Calendar.getInstance();
        try {
            firstdateofmonth =  formater.parse(date1);
            lastdateofmonth =  formater.parse(lastDate0fMonth);
            beforePresentMonth.setTime(formater.parse(date1));
            afterEndMonth.setTime(formater.parse(date2));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        beforePresentMonth.add(Calendar.MONTH, -1);
        afterEndMonth.add(Calendar.MONTH, +1);
        Calendar beginCalendar = Calendar.getInstance();
        Calendar finishCalendar = Calendar.getInstance();
        try {
            beginCalendar.setTime(formater.parse(date1));
            finishCalendar.setTime(formater.parse(date2));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONArray jsonArray = new JSONArray();
        if(mDateList.contains(date1) && rangeFromMonth == firstdateofmonth ){

                String dateInString = formater.format(beforePresentMonth.getTime()).toUpperCase();
            Date date = null;
                try {
                    date = formater.parse(dateInString);
                    Log.i("monthly", "monthly: " + date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long epoch = date.getTime();
            jsonArray.put(epoch);
        }

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
            jsonArray.put(epoch);
            beginCalendar.add(Calendar.MONTH, 1);
        }

        if(mDateList.contains(date2) && rangeToMonth == lastdateofmonth){

            String dateInString = formater.format(afterEndMonth.getTime()).toUpperCase();
            Date date = null;
            try {
                date = formater.parse(dateInString);
                Log.i("monthly", "monthly: " + date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long epoch = date.getTime();
            jsonArray.put(epoch);
        }
        return jsonArray;
               /* // from first-1 to last+1

        String dateArray[] = date1.split("/");

        String monthInString = dateArray[1];

        String yearInString = dateArray[2];

        int monthInInt = Integer.parseInt(monthInString);

        int yearInInt = Integer.parseInt(yearInString);

        //int mont = Integer.parseInt(month);

        if (monthInInt == 1) {

            monthInInt = 12;

            yearInInt = yearInInt - 1;

        } else {

            monthInInt = monthInInt - 1;

            ;

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


        date2 = "01/" + monthInInt2 + "/" + yearInInt2;*/

    }

    protected JSONArray getJsonForQuaterlyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy(String date1, String date2) {

        Date RangeToQuarter = null;  // set RangeFrom date of quarter
        Date RangeFromQuarter = null;   // set RangeTo date of quarter
        Date intialDateofFirstQuarter = null;       // First Date of the month
        Date lastDateofFirstQuarter = null;         // Last date of the month
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");

        try {
            RangeFromQuarter = formater.parse(date1);
            RangeToQuarter = formater.parse(date2);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        String dateArray1[] = date1.split("/");
        String dayInString1 = dateArray1[0];
        String monthInString1 = dateArray1[1];
        String yearInString1 = dateArray1[2];
        int dayInInt1 = Integer.parseInt(dayInString1);
        int monthInInt1 = Integer.parseInt(monthInString1);
        int yearInInt1 = Integer.parseInt(yearInString1);
        if (dayInInt1 == 1) {
            if (monthInInt1 == 1) {
                monthInInt1 = 12;
                yearInInt1 = yearInInt1 - 1;
            } else if (monthInInt1 == 4) {
                monthInInt1 = 1;
            } else if (monthInInt1 == 07) {
                monthInInt1 = 4;
            } else if (monthInInt1 == 10) {
                monthInInt1 = 7;
            }
        }

        if (monthInInt1 < 10) {
            date1 = "01/0" + monthInInt1 + "/" + yearInInt1;
        } else {
            date1 = "01/" + monthInInt1 + "/" + yearInInt1;
        }

        ///////////////////////////////////////////////////////////////////////
        String dateArray2[] = date2.split("/");
        String monthInString2 = dateArray2[1];
        String yearInString2 = dateArray2[2];
        int monthInInt2 = Integer.parseInt(monthInString2);
        int yearInInt2 = Integer.parseInt(yearInString2);

        if (isLastDateOfMonth(date2)) {
            if (monthInInt2 <= 3) {
                monthInInt2 = 7;
            } else if (monthInInt2 <= 6) {
                monthInInt2 = 10;
            } else if (monthInInt2 <= 9) {
                monthInInt2 = 1;
                yearInInt2 = yearInInt2 + 1;
            } else if (monthInInt2 <= 12) {
                monthInInt2 = 4;
                yearInInt2 = yearInInt2 + 1;
            }
        } else {
            if (monthInInt2 <= 3) {
                monthInInt2 = 4;
            } else if (monthInInt2 <= 6) {
                monthInInt2 = 7;
            } else if (monthInInt2 <= 9) {
                monthInInt2 = 10;
            } else if (monthInInt2 <= 12) {
                monthInInt2 = 1;
                yearInInt2 = yearInInt2 + 1;
            }
        }

        if (monthInInt2 < 10) {
            date2 = "01/0" + monthInInt2 + "/" + yearInInt2;
        } else {
            date2 = "01/" + monthInInt2 + "/" + yearInInt2;
        }

        Calendar beforeQuarterMonth = Calendar.getInstance();
        Calendar afterQuarterMonth = Calendar.getInstance();
        Calendar beginCalendar = Calendar.getInstance();
        Calendar finishCalendar = Calendar.getInstance();
        try {

            intialDateofFirstQuarter = formater.parse(date1);
            beginCalendar.setTime(formater.parse(date1));
            finishCalendar.setTime(formater.parse(date2));
            beforeQuarterMonth.setTime(formater.parse(date1));
            afterQuarterMonth.setTime(formater.parse(date2));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        beforeQuarterMonth.add(Calendar.MONTH, -4);
        afterQuarterMonth.add(Calendar.MONTH, +4);
        Calendar endOfMonthDate = Calendar.getInstance();
        endOfMonthDate.setTime(RangeToQuarter);
        endOfMonthDate.set(Calendar.DAY_OF_MONTH, endOfMonthDate.getActualMaximum(Calendar.DAY_OF_MONTH));
        String lastDateofMonth = formater.format(endOfMonthDate.getTime());
        try {
            lastDateofFirstQuarter = formater.parse(lastDateofMonth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = new JSONArray();
        if (mDateList.contains(date1) && intialDateofFirstQuarter == RangeFromQuarter) {   // comparing : firstdateofselectedmonth with Rangefromdate
            String dateInString = formater.format(beforeQuarterMonth.getTime()).toUpperCase();
            Date date = null;
            try {
                date = formater.parse(dateInString);
                Log.i("monthly", "monthly: " + date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long epoch = date.getTime();
            jsonArray.put(epoch);
        }
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
        if (mDateList.contains(date2) && lastDateofFirstQuarter == RangeToQuarter) {    // comparing : firstdateofselectedmonth with Rangefromdate

            String dateInString = formater.format(afterQuarterMonth.getTime()).toUpperCase();
            Date date = null;
            try {
                date = formater.parse(dateInString);
                Log.i("monthly", "monthly: " + date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long epoch = date.getTime();
            jsonArray.put(epoch);
        }
        return jsonArray;

                /* //One previous and one later quater is Included

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

        date2 = "01/" + monthInInt2 + "/" + yearInInt2;*/

    }

}
