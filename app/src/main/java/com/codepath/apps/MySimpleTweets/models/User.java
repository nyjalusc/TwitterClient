package com.codepath.apps.MySimpleTweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

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
}
