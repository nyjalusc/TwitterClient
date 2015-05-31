package com.codepath.apps.MySimpleTweets.fragments;

import com.codepath.apps.MySimpleTweets.Helpers.DbHelper;
import com.codepath.apps.MySimpleTweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class HomeTimelineFragment extends TimelineFragment {

    // Sends request and fills the listview by creating the tweet objects from json
    // NOTE: It is the responsibility of the caller to correctly configure the params before
    // calling this method
    @Override
    protected void populateTimeline(final boolean clearDb, final boolean appendEnd) {
        client.getHomeTimeline(endpointKeyMap, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // This is true when the application loads for the first time.
                // Done to fill the view and DB with latest tweet objects
                // (DEFAULT_COUNT - 5) is used to handle the case where endpoint returns 99 objects even though
                // the COUNT in request was 100
                if (response.length() >= (DEFAULT_COUNT - 5) && clearDb) {
                    DbHelper.clearDb();
                }
                parsedResponse = Tweet.fromJSONArray(response);
                // This if block is intentionally kept separate from the above if block.
                // It is to reduce to delay on UI thread, because UI refreshes as soon as
                // contents in the adapter change. So, clear and refilling the adapter should
                // happen back to back.
                if (parsedResponse.size() >= (DEFAULT_COUNT - 5) && clearDb) {
                    resetAdapterWithNewTweets(parsedResponse);
                } else {
                    appendTweets(parsedResponse, appendEnd);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            }
        });
    }

    @Override
    protected void loadFromDb() {
        aTweets.addAll(Tweet.getAllTweets());
    }
}
