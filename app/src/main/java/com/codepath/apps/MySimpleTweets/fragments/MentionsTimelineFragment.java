package com.codepath.apps.MySimpleTweets.fragments;

import android.util.Log;
import android.view.View;

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
        progressWheel.setVisibility(View.VISIBLE);
        if (!networkCheck()) {
            progressWheel.setVisibility(View.INVISIBLE);
            return;
        }
        client.getMentionsTimeline(endpointKeyMap, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if (response.length() == 0)  {
                    progressWheel.setVisibility(View.INVISIBLE);
                    return;
                }
                parsedResponse = Tweet.fromJSONArray(response);
                Log.d("DEBUG", "MentionsTimeline: Value of Parsed response it set: " + parsedResponse.size());
                appendTweets(parsedResponse, appendEnd);
                progressWheel.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("DEBUG", "Failed");
                throwable.printStackTrace();
                progressWheel.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("DEBUG", "Failed");
                progressWheel.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", "Failed");
                progressWheel.setVisibility(View.INVISIBLE);
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
