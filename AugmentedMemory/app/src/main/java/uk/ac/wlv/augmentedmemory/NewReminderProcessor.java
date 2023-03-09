package uk.ac.wlv.augmentedmemory;

import static java.lang.Integer.parseInt;

import android.util.Log;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewReminderProcessor {
    private String UnprocessedReminder;
    private String day;
    private String month;
    private String time;

    public NewReminderProcessor(String UnprocessedReminder) {
        this.UnprocessedReminder = UnprocessedReminder;
    }

    public String dateTimeprocess(){
        dayProcess();
        monthProcess();
        timeProcess();
        String dateTime = DateCombine();
        return dateTime;
    }

    public String getLocationProcess(){
        String location = locationProcess();
        return location;
    }

    private void dayProcess(){
        Matcher m = Pattern.compile("[0-9]+th").matcher(UnprocessedReminder);//matches 12th
        //Matcher m = p.matcher("the 12th of september");
        if (m.find()) {
            Matcher m1 = Pattern.compile("[0-9]+").matcher(m.group(0));//matches 12
            if(m1.find()){
                //Log.d("www","th "+m1.group(0));
                day = m1.group(0);
            }
        }

        else{
            m = Pattern.compile("[0-9]+nd").matcher(UnprocessedReminder);//matches 12th
            if (m.find()) {
                Matcher m1 = Pattern.compile("[0-9]+").matcher(m.group(0));//matches 12
                if(m1.find()){
                    //Log.d("www","nd "+m1.group(0));
                    day = m1.group(0);
                }
            }

            else{
                m = Pattern.compile("[0-9]+rd").matcher(UnprocessedReminder);//matches 12th
                if (m.find()) {
                    Matcher m1 = Pattern.compile("[0-9]+").matcher(m.group(0));//matches 12
                    if(m1.find()){
                        //Log.d("www","rd "+m1.group(0));
                        day = m1.group(0);
                    }
                }

                else{
                    m = Pattern.compile("[0-9]+st").matcher(UnprocessedReminder);//matches 12th
                    if (m.find()) {
                        Matcher m1 = Pattern.compile("[0-9]+").matcher(m.group(0));//matches 12
                        if(m1.find()){
                            //Log.d("www","st "+m1.group(0));
                            day = m1.group(0);
                        }
                    }
                }
            }
        }
        //if no day is found default to today's date
        if(this.day == null){
            //if it can't find a month default to the current month
            Date date = new Date();
            SimpleDateFormat fm = new SimpleDateFormat("dd");
            this.day = fm.format(date);
        }
    }

    private void monthProcess(){
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] monthNames = dfs.getMonths(); //January February etc...
        String[] monthNames1 = dfs.getShortMonths(); //Jan Feb etc...

        int count = 0;
        for(String month: monthNames){
            if (UnprocessedReminder.contains(month) || UnprocessedReminder.contains(monthNames1[count])){
                this.month = monthNames1[count].toUpperCase();
            }
            count++;
        }
        //if no month is found
        Log.d("WWW","out "+this.month);
        if(this.month == null){
            //if it can't find a month default to the current month
            Date date = new Date();
            SimpleDateFormat fm = new SimpleDateFormat("MMM");
            this.month = fm.format(date);
            Log.d("WWW","in "+this.month);
        }
    }

    private void timeProcess(){
        int pos = UnprocessedReminder.indexOf("at");
        //does it include 'at'
        if (pos != -1) {
            //gets everything after the 'at '
            String time = UnprocessedReminder.substring(pos + 3);
            //includes p.m
            if(time.contains("p.m")){
                int pos1 = time.indexOf(":");
                //if its has a colon (includes minutes so 12:32)
                if(pos1 != -1){
                    //before colon
                    String hours = time.substring(0,pos1);
                    //after colon
                    String minutes = time.substring(pos1 + 1, pos1 + 3);
                    //add 12 hours to all times except 12 as 12pm is 12
                    if(!hours.equals("12")) {
                        int hours24 = parseInt(hours) + 12;
                        time = hours24 + " " + minutes;
                    }
                    //it is equal to 12 so leave it alone and show the time and minutes
                    else{
                        time = hours + " " + minutes;
                    }
                }
                //pm but doesnt have : minutes (example 7 p.m)
                else{
                    //gets the first 2 digits so '7 ' or '11'
                    time = time.substring(0, 2);
                    //gets the second character
                    String secondChar = String.valueOf(time.charAt(1));
                    //if second character is a space
                    if(secondChar.equals(" ")){
                        //remove the space
                        time = time.substring(0,1);
                        //add 12 hours to make it 24 hour times
                        int hours24 = parseInt(time) + 12;
                        time = String.valueOf(hours24);
                    }
                    //second character is not a space so is a digit (10, 11 or 12)
                    else{
                        //if its not equal to 12
                        if(!time.equals("12")){
                            //make it 24 hours by adding 12
                            int hours24 = parseInt(time) + 12;
                            time = String.valueOf(hours24);
                        }
                    }
                    //put the minutes on which is always 00
                    time = time +" "+"00";
                }
            }

            //does not include p.m (so a.m or nothing)
            else{
                //if its has a colon (includes minutes so 7:32)
                int pos1 = time.indexOf(":");
                if(pos1 != -1){
                    //everything before colon
                    String hours = time.substring(0,pos1);
                    //gest the minutes after the colon
                    String minutes = time.substring(pos1 + 1, pos1 + 3);
                    //if hours does not have 2 digits make it 2 digits with a 0 at start
                    if(hours.length() == 1){
                        time = "0"+hours +" "+ minutes;
                    }
                    //if it has 2 digits (10,11,12)
                    else{
                        //if its equal to 12 make it 00 because 12am is 00
                        if(hours.equals("12")) {
                            time = "00 "+minutes;
                        }
                        //10 or 11
                        else{
                            time = hours + " " + minutes;
                        }
                    }
                }

                //not pm but doesnt have : minutes (example 7)
                else{
                    //gets the first 2 digits so '7 ' or '11' for example
                    if(time.length() == 1){
                        time += " ";
                    }
                    time = time.substring(0, 2);
                    //gets the second character
                    String secondChar = String.valueOf(time.charAt(1));
                    //if second character is a space
                    if(secondChar.equals(" ")){
                        //remove the space
                        time = time.substring(0,1);
                    }
                    //if it has 2 digits (10,11 or 12)
                    else{
                        //if it is 12
                        if(time.equals("12")){
                            //make it 00 as 12 am is 00
                            time = "00";
                        }
                    }
                    //give time the minutes which will always be 00
                    time = time +" "+"00";
                    char charArray[] = time.toCharArray();
                    boolean bool = Character.isDigit(charArray[0]);
                    Log.d("www","yeeeeee");
                    if(!bool) {
                        Log.d("www","hoooooo");
                        //if it can't find a time default to 1 hour in the future
                        Date date = new Date();
                        SimpleDateFormat fm = new SimpleDateFormat("HH mm");
                        date.setTime(date.getTime() + 3600000);
                        time = fm.format(date);
                        this.time = fm.format(date);
                    }
                }

            }
            Log.d("www",time);
            this.time = time;
        }
        else {
            //if it can't find a time default to 1 hour in the future
            Date date = new Date();
            SimpleDateFormat fm = new SimpleDateFormat("HH mm");
            date.setTime(date.getTime() + 3600000);
            this.time = fm.format(date);
        }
    }

    private String locationProcess(){
        Matcher m = Pattern.compile("at location.*$").matcher(UnprocessedReminder);//finds 'at location' and words after
        //List<String> words = new ArrayList<>();
        String location = "";

        if (m.find()) {
            m.group(0);
            String sentence = m.group(0);
            String[] words = sentence.split(" ");
            for(int i = 2; i < words.length; i++){
                if(i == 2){
                    location = location + words[i];
                }
                else{
                    location = location+ " " + words[i];
                }

            }

        }
        if(location == ""){
            return null;
        }
        else{
            return location;
        }

    }

    private String DateCombine(){
        String strDate = day+", "+month+" "+"2023, "+time;
        Log.d("www",strDate);
        SimpleDateFormat fm1 = new SimpleDateFormat("dd, MMM yyyy, HH mm");
        Date doodle = new Date();
        try {
            doodle = fm1.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("www", String.valueOf(doodle));
        return strDate;
    }
}
