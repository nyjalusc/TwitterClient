package com.codepath.apps.MySimpleTweets.fragments;

import android.util.Log;

import com.codepath.apps.MySimpleTweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class MentionsTimelineFragment extends TimelineFragment {

    // Sends request and fills the listview by creating the tweet objects from json
    // NOTE: It is the responsibility of the caller to correctly configure the params before
    // calling this method
    @Override
    protected void populateTimeline(final boolean clearDb, final boolean appendEnd) {
        client.getMentionsTimeline(endpointKeyMap, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if (response.length() == 0)  {
                    return;
                }
                parsedResponse = Tweet.fromJSONArray(response);
                Log.d("DEBUG", "MentionsTimeline: Value of Parsed response it set: " + parsedResponse.size());
                appendTweets(parsedResponse, appendEnd);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("DEBUG", "Failed");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("DEBUG", "Failed");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", "Failed");
            }
        });
    }

    // Load all mentions tweet from the database
    @Override
    protected void loadFromDb() {
       synchronized (this) {
           String currentUserName = getCurrentUserName();
           aTweets.addAll(Tweet.getAllMentionsTweet(currentUserName));
       }
    }
}
