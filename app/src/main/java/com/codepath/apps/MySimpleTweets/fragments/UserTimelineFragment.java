package com.codepath.apps.MySimpleTweets.fragments;


import android.os.Bundle;
import android.view.View;

import com.codepath.apps.MySimpleTweets.models.Tweet;
import com.codepath.apps.MySimpleTweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class UserTimelineFragment extends TimelineFragment {
    private User user;

    @Override
    protected void populateTimeline(final boolean clearDb, final boolean appendEnd) {
        progressWheel.setVisibility(View.VISIBLE);
        if (!networkCheck()) {
            progressWheel.setVisibility(View.INVISIBLE);
            return;
        }
        // Add two extra params "user_id" and "screen_name" which are unique to usertimeline endpoint
        endpointKeyMap.put("screen_name", user.getScreenName().substring(1));
        endpointKeyMap.put("user_id", user.getUid() + "");
        client.getUserTimeline(endpointKeyMap, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                parsedResponse = Tweet.fromJSONArray(response);
                appendTweets(parsedResponse, appendEnd);
                progressWheel.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                progressWheel.setVisibility(View.INVISIBLE);
            }
        });
    }

    // Load all mentions tweet from the database
    @Override
    protected void loadFromDb() {
        user = (User) getArguments().getSerializable("user");
        aTweets.addAll(Tweet.getTweetsForuser(user));
    }

    // Creates a new fragment given an screenName
    public static UserTimelineFragment newInstance(User user) {
        UserTimelineFragment userFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        userFragment.setArguments(args);
        return userFragment;
    }
}
