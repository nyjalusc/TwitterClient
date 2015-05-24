package com.codepath.apps.MySimpleTweets.models;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

@Table(name = "Image")
public class Image extends Model implements Serializable{
    // Unique imgId of the media element
    @Column(name = "imgId", unique = true, index = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long imgId;
    @Column(name = "url")
    private String url;

    public String getLargeImageUrl() {
        return url + ":large";
    }

    public String getSmallImageUrl() {
        return url + ":small";
    }
    // Returns a single image object
    public static Image fromJSON(JSONObject json) {
        try {
            JSONArray media = json.getJSONArray("media");
            Image image = new Image();
            for (int i = 0; i < media.length(); i++) {
                JSONObject item = media.getJSONObject(i);
                // Check if it is a photo
                if (item.getString("type").equals("photo")) {
                    image.imgId = item.getLong("id");
                    image.url = item.getString("media_url");
                    return saveIfNewImage(image);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Save user object if it doesn't already exist
    private static Image saveIfNewImage(Image image) {
        Image existingImage =
                new Select().from(Image.class).where("imgId= ?", image.imgId).executeSingle();
        if (existingImage != null) {
            // The existing user's imgId will be stored in the foreign column of parent table.
            return existingImage;
        }
        image.save();
        return image;
    }
}