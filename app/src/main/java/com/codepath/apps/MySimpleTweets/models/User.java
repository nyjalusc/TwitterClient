package com.codepath.apps.MySimpleTweets.models;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.codepath.apps.MySimpleTweets.Helpers.RelativeIntegers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

@Table(name = "Users")
public class User extends Model implements Serializable {
    @Column(name = "uid", unique = true, index = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long uid; // Unique id of the user
    @Column(name = "name")
    private String name;
    @Column(name = "screen_name")
    private String screenName;
    @Column(name = "profile_image_url")
    private String profileImageUrl;
    @Column(name = "profile_banner_url")
    private String profileBannerUrl;
    @Column(name = "tag_line")
    private String tagLine;
    @Column(name = "followers_count")
    private String followersCount;
    @Column(name = "following_count")
    private String followingsCount;
    @Column(name = "tweets_count")
    private String tweetsCount;
    @Column(name = "profile_background_color")
    private String profileBackgroundColor;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return "@" + screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getProfileBannerUrl() {
        return profileBannerUrl;
    }

    public String getTagLine() {
        return tagLine;
    }

    public String getFollowersCount() {
        return RelativeIntegers.format(Long.parseLong(followersCount)).toUpperCase();
    }

    // Used for setting color of toolbar; By default setting it to 25% transparency
    public String getProfileBackgroundColor() {
        return "#4D" + profileBackgroundColor;
    }

    // Same as "followings" count
    public String getFriendsCount() {
        return RelativeIntegers.format(Long.parseLong(followingsCount)).toUpperCase();
    }

    // Formats the integers to K thousand, M millions, B billions etc.
    public String getTweetsCount() {
        return RelativeIntegers.format(Long.parseLong(tweetsCount)).toUpperCase();
    }

    public static User fromJSON(JSONObject json) {
        User user = new User();
        try {
            user.name = json.getString("name");
            user.uid = json.getLong("id");
            user.screenName = json.getString("screen_name");
            user.profileImageUrl = json.getString("profile_image_url");
            user.tagLine = json.getString("description");
            user.followersCount = json.getString("followers_count");
            user.followingsCount = json.getString("friends_count");
            user.tweetsCount = json.getString("statuses_count");
            user.profileBackgroundColor = json.getString("profile_background_color");
            if (!json.isNull("profile_banner_url")) {
                user.profileBannerUrl = json.getString("profile_banner_url");
            }
            // Save if New User
            user = saveIfNewUser(user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    // Save user object if it doesn't already exist
    private static User saveIfNewUser(User user) {
        User existingUser =
                new Select().from(User.class).where("uid= ?", user.uid).executeSingle();
        if (existingUser != null) {
            // The existing user's id will be stored in the foreign column of parent table.
            return existingUser;
        }
        user.save();
        Log.d("DEBUG", "saved user: " + user.screenName);
        return user;
    }

    // Reads all tweets from the database
    public static List<User> getAllUsers() {
        List<User> result = new Select()
                .from(User.class)
                .execute();
        Log.d("DEBUG", "Objects read from the db:" + result.size());
        return result;
    }

    // Reads all tweets from the database
    public static User getUser(String screenName) {
        User result = new Select()
                .from(User.class)
                .where("screen_name= ?", screenName)
                .executeSingle();
        return result;
    }

}
