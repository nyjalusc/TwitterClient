package com.codepath.apps.MySimpleTweets.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.MySimpleTweets.R;
import com.codepath.apps.MySimpleTweets.fragments.TweetsListFragment;
import com.codepath.apps.MySimpleTweets.models.Tweet;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TimelineActivity extends ActionBarActivity {

    private final int REQUEST_CODE = 20;
    private TweetsListFragment tweetsListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        // Initializes the toolbar
        initToolbar();
        // Get access to the fragment from layout
        if (savedInstanceState == null) {
            tweetsListFragment = (TweetsListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_timeline);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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

    public void launchComposeActivityForReply(Tweet tweet) {
        Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
        i.putExtra("tweet", tweet);
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
            tweetsListFragment.getTweets().add(0, tweet);
            tweetsListFragment.getTweetsAdapter().notifyDataSetChanged();
        }
    }
}
