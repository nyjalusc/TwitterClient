package com.codepath.apps.MySimpleTweets.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.MySimpleTweets.R;
import com.codepath.apps.MySimpleTweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.MySimpleTweets.interfaces.EndlessScrollListener;
import com.codepath.apps.MySimpleTweets.models.Tweet;
import com.codepath.apps.MySimpleTweets.net.ConnectivityChecker;
import com.codepath.apps.MySimpleTweets.net.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class TimelineActivity extends ActionBarActivity {

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;
    private ConnectivityChecker connectivityChecker;
    private HashMap<String, String> endpointKeyMap;
    private int DEFAULT_COUNT = 100;
    // List of Tweets received in the last GET request
    private ArrayList<Tweet> parsedResponse;

    // Add new params to this class
    public enum TimelineParams {
        COUNT {
            public String toString() {
                return "count";
            }
        },

        MAX_ID {
            public String toString() {
                return "max_id";
            }
        },

        SINCE_ID {
            public String toString() {
                return "since_id";
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        init();
        setupViewListeners();
        populateTimeline();
    }

    // Initialize properties
    private void init() {
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(this, tweets);
        lvTweets.setAdapter(aTweets);
        connectivityChecker = new ConnectivityChecker();
        // Singleton client
        client = TwitterApplication.getRestClient();
        // initialize Endpoint keymap store
        initEndpointKeyMap();
    }

    // Initialize Endpoint KeyMap
    private void initEndpointKeyMap() {
        endpointKeyMap = new HashMap<>();
        endpointKeyMap.put(TimelineParams.COUNT.toString(), DEFAULT_COUNT + "");
        endpointKeyMap.put(TimelineParams.MAX_ID.toString(), null);
        endpointKeyMap.put(TimelineParams.SINCE_ID.toString(), null);
    }

    // Check for network connectivity
    private boolean networkCheck() {
        if (!connectivityChecker.isNetworkAvailable(this)) {
//            showAlertDialog("No internet", "It looks like you have lost network connectivity");
            Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setupViewListeners() {
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Check for internet connectivity
                if (!networkCheck()) {
                    return;
                }
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                populateTimeline();
            }
        });
    }

//    // Invokes the searchClient to execute the request
//    private void loadTweets() {
//        // Execute GET request
//        client.getHomeTimeline(endpointKeyMap, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                // Load the models in adapter; this will also modify the data in underlying datasource
//                // No need to use notifyDataSetChanged() if using this method
//                parsedResponse = Tweet.fromJSONArray(response);
//                aTweets.addAll(parsedResponse);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
////                showAlertDialog("Sorry!", "Failed to fetch results");
//            }
//        });
//    }

    // Sends request and fills the listview by creating the tweet objects from json
    private void populateTimeline() {
        client.getHomeTimeline(endpointKeyMap, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                parsedResponse = Tweet.fromJSONArray(response);
                aTweets.addAll(parsedResponse);
                Log.d("DEBUG", aTweets.toString());
                // After finishing the first request it is important to set the endpoint params
                // correctly to issue subsequent requests
                setValueOfEndpointParams();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });

    }

    private void setValueOfEndpointParams() {
//        timelineParams.since_id = aTweets.getItem(0).getUid();
        int lastIndex = parsedResponse.size() - 1;
        long lastProcessedTweet = parsedResponse.get(lastIndex).getUid();
        // max_id is set as the upper bound for the next request
        // In order to ge the id of the next unprocessed tweet we need to subtract 1 from id
        // More details here: https://dev.twitter.com/rest/public/timelines
        long maxId = lastProcessedTweet - 1;
        endpointKeyMap.put(TimelineParams.MAX_ID.toString(), maxId + "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
