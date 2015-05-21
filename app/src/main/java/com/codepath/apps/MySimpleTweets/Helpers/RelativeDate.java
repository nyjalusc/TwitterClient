package com.codepath.apps.MySimpleTweets.Helpers;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class RelativeDate {
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeFormatter(relativeDate);
    }

    // Formats the string as per the requirement of the UI
    // eg. 33 minutes => 33m
    private String timeFormatter(String relativeDate) {
        String[] words = relativeDate.split(" ");
        StringBuilder result = new StringBuilder();
        result.append(words[0]);
        result.append(words[1].charAt(0));
        return result.toString();
    }
}
