package com.example.android.technews;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Event {
    private static final String LOG_TAG = Event.class.getSimpleName();

    private String mSection;
    private String mTitle;
    private String mAuthor;
    private String mPublishDateTime;
    private String mUrl;

    public Event(String section, String title, String author, String publishDate, String url) {
        mSection = section;
        mTitle = title;
        mUrl = url;
        mAuthor = author;

        // Split the date string up into a date and time variable
        int index = publishDate.indexOf("T");
        int length = publishDate.length();
        String date = publishDate.substring(0, index);
        String time = publishDate.substring(index + 1, length-1);
        String publish = date + " " + time;

        // Format the date and time strings for the layout
        mPublishDateTime = formatDate(publish);
    }

    public String formatDate(String inputDate) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd MMM, yyyy -- h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date;
        String str = null;

        try {
            date = inputFormat.parse(inputDate);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Failed to parse the date string");
        }
        return str;
    }

    public String getSection() {return mSection;}
    public String getNewsTitle() {return mTitle;}
    public String getAuthor() {return mAuthor;}
    public String getPublishDateTime() {return mPublishDateTime;}
    public String getUrl() {return mUrl;}
}
