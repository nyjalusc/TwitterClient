package com.codepath.apps.MySimpleTweets.models;

import com.codepath.apps.MySimpleTweets.Helpers.RelativeDate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This class is responsible for parsing the JSONObject, deseriablizing it and converting it into a
 * java object.
 */

/**
 *
 */
public class Tweet {
    private String body;
    private long uid; // Unique id for the tweet; Not userid
    private User user;
    private String createdAt;

    RelativeDate relativeDate;

    public User getUser() {
        return user;
    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    // Gets the relative timestamp eg. 45m (45 minutes)
    public String getRelativeTime() {
        relativeDate = new RelativeDate();
        return relativeDate.getRelativeTimeAgo(this.createdAt);
    }

    // Deserialize JSON and build tweet objects
    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(tweetJson);
                if (tweet != null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tweets;
    }
}
