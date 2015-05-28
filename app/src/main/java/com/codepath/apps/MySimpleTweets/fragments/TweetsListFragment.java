package com.codepath.apps.MySimpleTweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codepath.apps.MySimpleTweets.R;
import com.codepath.apps.MySimpleTweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.MySimpleTweets.models.Tweet;

import java.util.ArrayList;

public class TweetsListFragment extends Fragment {

    protected ArrayList<Tweet> tweets;
    protected TweetsArrayAdapter aTweets;
    protected ListView lvTweets;
    protected SwipeRefreshLayout swipeContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweets_list, container, false);
        lvTweets = (ListView) view.findViewById(R.id.lvTweets);
        lvTweets.setAdapter(aTweets);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweets = new ArrayList<>();
        // First read from the database and populate the UI with tweets
//        tweets.addAll(Tweet.getAllTweets());
        aTweets = new TweetsArrayAdapter(getActivity(), tweets);
    }

    public ArrayList<Tweet> getTweets() {
        return tweets;
    }

    public TweetsArrayAdapter getTweetsAdapter() {
        return aTweets;
    }
}
