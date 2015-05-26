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
    @Column(name = "uidStr")
    private String uidStr;
    @Column(name = "body")
    private String body;
    @Column(name = "created_at")
    private String createdAt;
    @Column(name = "user", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private User user;
    @Column(name = "image", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private Image image;
    @Column(name="retweet_count")
    private int retweetCount;
    @Column(name="favorite_count")
    private int favoriteCount;
    @Column(name="retweeted")
    // True if retweeted by the current user
    private boolean retweeted;
    @Column(name="favorited")
    private boolean favorited;
    @Column(name="retweetIdStr")
    private String retweetidStr;
    // if originalTweet != null, then the current tweet is a retweet of the original tweet
    // If null then the current tweet is not a retweet
    @Column(name="originalTweet")
    private Tweet originalTweet;

    public Tweet getOriginalTweet() {
        return originalTweet;
    }

    public boolean hasOriginalTweet() {
        return (originalTweet != null ? true : false);
    }
    private RelativeDate relativeDate;

    public String getUidStr() {
        return uidStr;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
        Log.d("DEBUG", "Set favorite Count to: " + favoriteCount);
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public Image getImage() {
        return image;
    }

    public User getUser() {
        return user;
    }

    public void setRetweetIdStr(String retweetId) {
        this.retweetidStr = retweetId;
    }

    public String getRetweetidStr() {
        return retweetidStr;
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

    public String getRetweetCount() {
        return retweetCount + "";
    }

    public String getFavoritesCount() {
        return favoriteCount + "";
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public boolean isFavorited() {
        return favorited;
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
            tweet.uidStr = jsonObject.getString("id_str");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            tweet.image = Image.fromJSON(jsonObject.getJSONObject("entities"));
            tweet.retweetCount = jsonObject.getInt("retweet_count");
            tweet.favoriteCount = jsonObject.getInt("favorite_count");
            tweet.retweeted = jsonObject.getBoolean("retweeted");
            tweet.favorited = jsonObject.getBoolean("favorited");
            if (jsonObject.getJSONObject("retweeted_status") != null) {
                // Get the tweet object of the original tweet
                tweet.originalTweet = fromJSON(jsonObject.getJSONObject("retweeted_status"));
            }
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
                    if (tweet != null) {
                        tweets.add(tweet);
                    }
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
            // Return null to avoid populating duplicate tweets in the listview
            // This will make the logic simpler because the UI should simply show whatever
            // is saved in the db. Handling od duplicate tweets is done here.
            return null;
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

    public static int countTweets() {
        return new Select()
                .from(Tweet.class)
                .count();
    }

    public static Tweet getTweetWithId(long uid) {
        return new Select().from(Tweet.class).where("uid= ?", uid).executeSingle();
    }
}
