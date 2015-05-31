package com.codepath.apps.MySimpleTweets.models;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

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
    @Column(name = "tag_line")
    private String tagLine;
    @Column(name = "followers_count")
    private String followersCount;
    @Column(name = "following_count")
    private String followingsCount;

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
        return user;
    }

    public String getTagLine() {
        return tagLine;
    }

    public String getFollowersCount() {
        return followersCount;
    }

    public String getFriendsCount() {

        return followingsCount;
    }

    // Reads all tweets from the database
    public static List<User> getAllUsers() {
        List<User> result = new Select()
                .from(User.class)
                .execute();
        Log.d("DEBUG", "Objects read from the db:" + result.size());
        return result;
    }
}
