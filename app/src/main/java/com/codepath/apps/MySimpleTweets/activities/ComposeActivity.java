package com.codepath.apps.MySimpleTweets.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.MySimpleTweets.R;
import com.codepath.apps.MySimpleTweets.models.User;
import com.codepath.apps.MySimpleTweets.net.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

public class ComposeActivity extends ActionBarActivity {

    private TwitterClient client;
    private User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        init();
        getUserDetail();
    }

    private void init() {
        initToolbar();
        client = new TwitterClient(this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose, menu);
        getUserDetail();
        return true;
    }

    private void getUserDetail() {
        client.getCurrentUserDetails(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                currentUser = User.fromJSON(response);
                Log.d("DEBUG", currentUser.getScreenName());
                Log.d("DEBUG", currentUser.getName());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
