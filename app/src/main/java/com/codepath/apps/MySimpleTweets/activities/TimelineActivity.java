package com.codepath.apps.MySimpleTweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.MySimpleTweets.Helpers.DbHelper;
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
    private final int REQUEST_CODE = 20;
    // List of Tweets received in the last GET request
    private ArrayList<Tweet> parsedResponse;
    private SwipeRefreshLayout swipeContainer;


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
        setupSwipeRefresh();
    }

    // Initialize properties
    private void init() {
        initToolbar();
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        tweets = new ArrayList<>();

        // First read from the database and populate the UI with tweets
        Log.d("DEBUG", "READING FROM DB");
        tweets.addAll(Tweet.getAllTweets());
        aTweets = new TweetsArrayAdapter(this, tweets);
        lvTweets.setAdapter(aTweets);
        connectivityChecker = new ConnectivityChecker();
        // Singleton client
        client = TwitterApplication.getRestClient();
        // initialize Endpoint keymap store
        initEndpointKeyMap();
    }

    // Toolbar is a replacement to the older actionbar
    private void initToolbar() {
        // Set a toolbar to replace the action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Set the home icon on toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_twitter_home);
    }

    // Initialize Endpoint KeyMap
    private void initEndpointKeyMap() {
        endpointKeyMap = new HashMap<>();
        endpointKeyMap.put(TimelineParams.COUNT.toString(), DEFAULT_COUNT + "");
        endpointKeyMap.put(TimelineParams.MAX_ID.toString(), null);
        endpointKeyMap.put(TimelineParams.SINCE_ID.toString(), null);
    }

    private void setupSwipeRefresh() {
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.

                fetchTimelineAsync();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    // Returns the first visible tweet
    private Tweet getFirstTweet() {
        for (int i = 0; i < tweets.size(); i++) {
            Tweet tweet = aTweets.getItem(i);
            if (tweet.getUid() != 0L) {
                return tweet;
            }
        }
        return null;
    }

    // When Doing a refresh use since_id param to fetch new tweets. If the number of received tweets < COUNT
    // then we have all the latest tweets, otherwise we will receive COUNT number of tweets which means that
    // there might be other new unprocessed tweets. So we reset the view to update it with new fresh tweets.
    private void fetchTimelineAsync() {
        // Use since_id param to fetch new tweets
        endpointKeyMap.put(TimelineParams.SINCE_ID.toString(), getFirstTweet().getUid() + "");
        // reset max_id to null
        endpointKeyMap.put(TimelineParams.MAX_ID.toString(), null);
        client.getHomeTimeline(endpointKeyMap, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // If during refresh we get DEFAULT_COUNT number of tweets then we clear the DB and
                // force the user to navigate and repopulate the DB. This will make the logic much more
                // simpler. User is guaranteed fresh data every single time. This situation can arise
                // when the application is alive but is not used by the user for quite some time.
                if (response.length() >= DEFAULT_COUNT) {
                    DbHelper.clearDb();
                }
                parsedResponse = Tweet.fromJSONArray(response);
                Log.d("DEBUG", "Number of Tweets fetched in Refresh: " + parsedResponse.size());
                if (parsedResponse.size() >= DEFAULT_COUNT) {
                    resetAdapterWithNewTweets(parsedResponse);
                } else {
                    appendTweets(parsedResponse);
                }
                // Signal that refresh has ended
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", throwable.toString());
            }
        });
    }

    // Resets the UI with fresh new tweets
    private void resetAdapterWithNewTweets(ArrayList<Tweet> newTweets) {
        aTweets.clear();
        tweets.clear();
        tweets.addAll(newTweets);
        aTweets.notifyDataSetChanged();
    }

    // Append new tweets at the beginning of the list
    private void appendTweets(ArrayList<Tweet> newTweets) {
        for (int i = newTweets.size() - 1; i >= 0; i--) {
            // Keep adding tweet objects in the front of the list
            tweets.add(0, newTweets.get(i));
        }
        // Update the adapter
        aTweets.notifyDataSetChanged();
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
                setValueOfEndpointParams();
                populateTimeline();
            }
        });
    }

    // Sends request and fills the listview by creating the tweet objects from json
    private void populateTimeline() {
        client.getHomeTimeline(endpointKeyMap, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // If the request succeeds clear the database first
                if (endpointKeyMap.get(TimelineParams.MAX_ID.toString()) == null) {
                    // Clear the database
                    DbHelper.clearDb();
                }
                parsedResponse = Tweet.fromJSONArray(response);
                // This if block is intentionally kept separate from the above if block
                // it is to reduce to delay on UI thread. Because UI refreshes as soon as
                // contents in the adapter change. So, clear and refilling the adapter should
                // happen back to back.
                if (endpointKeyMap.get(TimelineParams.MAX_ID.toString()) == null) {
                    // This should happen only if the first request request is successfull
                    // Keep both the data-structures in sync; This will come in handy to later add
                    // one tweet in the beginning of the list
                    aTweets.clear();
                    tweets.clear();
                }
                aTweets.addAll(parsedResponse);
                tweets.addAll(parsedResponse);
                Log.d("DEBUG", aTweets.getCount() + "");
                // After finishing the first request it is important to set the endpoint params
                // correctly to issue subsequent requests
                setValueOfEndpointParams();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                Log.d("DEBUG", errorResponse.toString());
            }
        });

    }

    // Sets the value of MAX_ID to one less than the oldest tweet processed
    private void setValueOfEndpointParams() {
        int lastIndex = parsedResponse.size() - 1;
        if (lastIndex <= 0) {
            return;
        }
        long lastProcessedTweetId = parsedResponse.get(lastIndex).getUid();
        // max_id is set as the upper bound for the next request
        // In order to ge the id of the next unprocessed tweet we need to subtract 1 from id because
        // max_id is inclusive of the range
        // More details here: https://dev.twitter.com/rest/public/timelines
        long maxId = lastProcessedTweetId - 1;
        endpointKeyMap.put(TimelineParams.MAX_ID.toString(), maxId + "");
        endpointKeyMap.put(TimelineParams.SINCE_ID.toString(), null);
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
        if (id == R.id.action_compose) {
            launchComposeActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void launchComposeActivity() {
        Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
        startActivityForResult(i, REQUEST_CODE);
        // Apply Bottom-up transition
        overridePendingTransition(R.animator.slide_in_up, R.animator.slide_out_up);
    }

    // Result from the compose activity needs to be added to the adapter to update the view
    // This is a hack to quickly update the view without waiting for the tweet to appear in the
    // Twitter's api
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Tweet tweet = (Tweet) data.getSerializableExtra("tweet");
            tweets.add(0, tweet);
            aTweets.notifyDataSetChanged();
        }
    }
}
