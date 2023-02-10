package uk.ac.wlv.augmentedmemory;

import android.util.Log;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewReminderProcessor {
    private String UnprocessedReminder;
    private String day;
    private String month;

    public NewReminderProcessor(String UnprocessedReminder) {
        this.UnprocessedReminder = UnprocessedReminder;
        dateProcess();
    }

    private void dateProcess(){
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] monthNames = dfs.getMonths(); //January February etc...
        String[] monthNames1 = dfs.getShortMonths(); //Jan Feb etc...
        Matcher m = Pattern.compile("[0-9]+th").matcher(UnprocessedReminder);//matches 12th
        //Matcher m = p.matcher("the 12th of september");
        if (m.find()) {
            Matcher m1 = Pattern.compile("[0-9]+").matcher(UnprocessedReminder);//matches 12
            if(m1.find()){
                Log.d("www",m1.group(0));
                day = m1.group(0);
            }
        }

            int count = 0;
        for(String month: monthNames){
            if (UnprocessedReminder.contains(month) || UnprocessedReminder.contains(monthNames1[count])){
                Log.d("www",monthNames1[count]);
                this.month = monthNames1[count].toUpperCase();
            }
            count++;
        }
        combine();
    }
    private void combine(){
        String strDate = day+", "+month+" "+"2023";
        Log.d("www",strDate);
        SimpleDateFormat fm1 = new SimpleDateFormat("dd, MMM yyyy");
        Date doodle = new Date();
        try {
            doodle = fm1.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("www", String.valueOf(doodle));
    }
}
