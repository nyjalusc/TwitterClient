package com.codepath.apps.MySimpleTweets.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.MySimpleTweets.R;
import com.codepath.apps.MySimpleTweets.fragments.HomeTimelineFragment;
import com.codepath.apps.MySimpleTweets.fragments.MentionsTimelineFragment;
import com.codepath.apps.MySimpleTweets.fragments.TweetsListFragment;
import com.codepath.apps.MySimpleTweets.models.Tweet;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TimelineActivity extends ActionBarActivity {

    private final int REQUEST_CODE = 20;
    private TweetsListFragment tweetsListFragment;
    private TweetsPagerAdapter tweetsPagerAdapter;
    private ViewPager vpPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        // Initializes the toolbar
        initToolbar();
        setupViewPager();
    }

    private void setupViewPager() {
        tweetsPagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager());
        // Get the viewpager
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        // Set the viewpager
        vpPager.setAdapter(tweetsPagerAdapter);
        // Find the pager sliding tabs
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the tabstrip to the viewpager
        tabStrip.setViewPager(vpPager);
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

            for(int i = 0; i < tweetsPagerAdapter.getCount(); i++) {
                // Check by fragment title; Not sure if it is the most elegant method but it works for the time being
                if (tweetsPagerAdapter.getPageTitle(i).toString().equals("Home")) {
                    // FragmentManager is used here to find the fragment by using the tag that was set by
                    // FragmentPagerAdapter at the time of instantiating the fragment
                    tweetsListFragment = (TweetsListFragment) getSupportFragmentManager().findFragmentByTag(getTag(i));
                    tweetsListFragment.getTweets().add(0, tweet);
                    tweetsListFragment.getTweetsAdapter().notifyDataSetChanged();
                }
            }
        }
    }

    /**
     * This method is used for generating the tag to find the fragment tag that is autogenerated
     * by the FragmentPagerAdapter at the time of instantiating a fragment. Checkout
     * FragmentPagerAdapter::makeFragmentName() to see how a tag is generated
     * @param position
     * @return
     */
    private String getTag(int position) {
        return "android:switcher:" + vpPager.getId() + ":" + position;
    }

    public void onProfileView(MenuItem item) {
        // Launch the profile view
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    // Returns the order of the fragment in the viewpager
    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = {"Home", "Mentions"};

        // Adapter gets the fragment manager to add or remove the fragments from activity
        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // The order and creation of fragments within the pager
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new HomeTimelineFragment();
            } else if(position == 1) {
                return new MentionsTimelineFragment();
            }
            return null;
        }

        // Returns the tab titles
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        // Returns the number of fragments to swipe
        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }
}
