package com.codepath.apps.MySimpleTweets.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.MySimpleTweets.R;
import com.codepath.apps.MySimpleTweets.fragments.UserTimelineFragment;
import com.codepath.apps.MySimpleTweets.models.User;
import com.codepath.apps.MySimpleTweets.net.TwitterClient;
import com.squareup.picasso.Picasso;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * This activity expects the parent activity to pass the User object through the intent.
 */
public class ProfileActivity extends ActionBarActivity {

    private TwitterClient client;
    private TextView tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        client = TwitterApplication.getRestClient();
        User user = (User) getIntent().getSerializableExtra("user");
        initToolbar(user);

        // Set the username on the toolbar
        populateProfileHeader(user);

        if (savedInstanceState == null) {
            // Get the screen name
            // Create user timeline fragment; and pass the data in the fragment
            UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(user);
            // Display the user fragment within the activity
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, userTimelineFragment);
            ft.commit();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void populateProfileHeader(User user) {
        // tvUserName gets initialized from initToolbar()
        tvUserName.setText(user.getScreenName());

        ImageView ivBannerImage = (ImageView) findViewById(R.id.ivBannerImage);
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        TextView tvName = (TextView) findViewById(R.id.tvName);
//        TextView tvTagline = (TextView) findViewById(R.id.tvTagline);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        TextView tvTweetsCount = (TextView) findViewById(R.id.tvTweetsCount);

        // Set the banner image
        ivBannerImage.setImageResource(0);
        if (user.getProfileBannerUrl() == null) {
            ivBannerImage.setImageResource(R.color.blankBannerColor);
        } else {
            Picasso.with(this).load(user.getProfileBannerUrl()).into(ivBannerImage);
        }

        // Set the profile image
        ivProfileImage.setImageResource(0);
        Picasso.with(this).load(user.getProfileImageUrl()).into(ivProfileImage);
        tvName.setText(user.getName());
//        tvTagline.setText(user.getTagLine());
        tvFollowers.setText(user.getFollowersCount());
        tvFollowing.setText(user.getFriendsCount());
        tvTweetsCount.setText(user.getTweetsCount());
    }

    // Toolbar is a replacement to the older actionbar
    private void initToolbar(User user) {
        // Set a toolbar to replace the action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor(user.getProfileBackgroundColor()));

        // Set the home icon on toolbar
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_up_menu);
        // Get the reference to textView from the toolbar
        tvUserName = (TextView) findViewById(R.id.tvUserName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
