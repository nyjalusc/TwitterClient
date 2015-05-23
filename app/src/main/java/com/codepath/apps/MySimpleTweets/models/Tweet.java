package com.codepath.apps.MySimpleTweets.models;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.codepath.apps.MySimpleTweets.Helpers.RelativeDate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for parsing the JSONObject, deseriablizing it and converting it into a
 * java object.
 */

@Table(name = "Tweets")
public class Tweet extends Model implements Serializable {
    @Column(name = "uid", unique = true, index = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long uid; // Unique id for the tweet; Not userid
    @Column(name = "body")
    private String body;
    @Column(name = "created_at")
    private String createdAt;
    @Column(name = "user", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private User user;

    private RelativeDate relativeDate;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
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
                    tweet = saveIfNewTweet(tweet);
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tweets;
    }

    // Save tweet if it doesn't already exist;
    private static Tweet saveIfNewTweet(Tweet tweet) {
        Tweet existingTweet =
                new Select().from(Tweet.class).where("uid= ?", tweet.uid).executeSingle();
        if (existingTweet != null) {
            return existingTweet;
        }
        // It is important to note that user object is saved before the parent object gets saved
        tweet.save();
        return tweet;
    }

    // Reads all tweets from the database
    public static List<Tweet> getAllTweets() {
        List<Tweet> result = new Select()
                .from(Tweet.class)
                .execute();
        Log.d("DEBUG", "Objects read from the db:" + result.size());
        return result;
    }
}
