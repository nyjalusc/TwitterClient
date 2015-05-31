package com.codepath.apps.MySimpleTweets.Helpers;

import com.activeandroid.util.SQLiteUtils;

public class DbHelper {
    // Deletes all data from the database
    public static void clearDb() {
        SQLiteUtils.execSql("DELETE FROM Tweets");
        SQLiteUtils.execSql("DELETE FROM Users");
    }
}
