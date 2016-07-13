package com.cloudchowk.patient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Helper {
    public static String canExit;
    public static String resend_sms;
    public static String resend_email;
    public static String resend_name;
    public static String locationFromCoordinates;
    public static Double defaultLat, defaultLong;
    public static List<String> list = new ArrayList<String>();
    public static boolean authentication_flag = false;
    public static String fromactivity;
    public static int check_contact_number =0;
    public static ArrayList<String> folder_path = new ArrayList<String>();
    public static ArrayList<HashMap<String, String>> main_S3Objects = new ArrayList<HashMap<String, String>>();
    public ArrayList<HashMap<String,String>> sortHashList(ArrayList<HashMap<String,String>> list,String key){

        //  ArrayList<HashMap<String,String>> returnlist=new ArrayList<HashMap<String, String>>();
        for(int i=0;i<list.size()-1;i++){


            for(int j=i;j<list.size();j++){
                String first=list.get(i).get(key);
                String second=list.get(j).get(key);
                if(first.contains("/")){
                    String splitfirst []=first.split("/");
                    first=splitfirst[splitfirst.length-1];
                }
                if(second.contains("/")){
                    String splitsecond []=second.split("/");
                    second=splitsecond[splitsecond.length-1];
                }

                if(first.compareToIgnoreCase(second)>0){//it means first is greater than second
                    HashMap<String,String> firstitem=list.get(i);
                    HashMap<String,String> seconditem=list.get(j);
                    //swap position first with second
                    list.add(i,seconditem);
                    list.add(j,firstitem);
                    list.remove(i+1);
                    list.remove(j+1);


                }
            }
        }

        return list;
    }

    public ArrayList<HashMap<String,String>> sortHashListByDate(ArrayList<HashMap<String,String>> list){

        //  ArrayList<HashMap<String,String>> returnlist=new ArrayList<HashMap<String, String>>();
        for(int i=0;i<list.size()-1;i++){


            for(int j=i;j<list.size();j++){
                try {
                    String first=list.get(i).get("from");
                    String second=list.get(j).get("from");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Date date1 = sdf.parse(first);
                    Date date2 = sdf.parse(second);

                    if(date1.compareTo(date2)<0){//it means first is greater than second
                        HashMap<String,String> firstitem=list.get(i);
                        HashMap<String,String> seconditem=list.get(j);
                        //swap position first with second

                        list.add(i,seconditem);
                        list.add(j,firstitem);
                        list.remove(i+1);
                        list.remove(j+1);


                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        return list;
    }
}
