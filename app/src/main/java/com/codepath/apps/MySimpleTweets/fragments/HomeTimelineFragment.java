package com.codepath.apps.MySimpleTweets.fragments;

import android.util.Log;
import android.view.View;

import com.codepath.apps.MySimpleTweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class HomeTimelineFragment extends TimelineFragment {

    @Override
    protected void populateTimeline(final boolean clearDb, final boolean appendEnd) {
        progressWheel.setVisibility(View.VISIBLE);
        if (!networkCheck()) {
            progressWheel.setVisibility(View.INVISIBLE);
            return;
        }
        client.getHomeTimeline(endpointKeyMap, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                parsedResponse = Tweet.fromJSONArray(response);
                Log.d("DEBUG", "HomeTimeline: Value of Parsed response it set: " + parsedResponse.size());
                appendTweets(parsedResponse, appendEnd);
                progressWheel.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", "HomeTimeline Failed!!");
                progressWheel.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void loadFromDb() {
        aTweets.addAll(Tweet.getAllTweets());
    }
}
