package com.codepath.apps.MySimpleTweets.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.MySimpleTweets.Helpers.RelativeDate;
import com.codepath.apps.MySimpleTweets.R;
import com.codepath.apps.MySimpleTweets.models.Tweet;
import com.codepath.apps.MySimpleTweets.models.User;
import com.codepath.apps.MySimpleTweets.net.TwitterClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

public class ComposeActivity extends ActionBarActivity {

    private TwitterClient client;
    private User currentUser;
    private EditText etMessage;
    private TextView tvCharCount;
    private int remainingChars;
    private static final int TWEET_CHAR_LIMIT = 140;
    private RelativeDate relativeDate;
    private String tweetText;
    private String inReplyTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        init();
        Tweet tweet = (Tweet) getIntent().getSerializableExtra("tweet");
        if (tweet != null) {
            inReplyTo = tweet.getUidStr();
            prepareMessage(tweet);
        }
        setupListener();
    }

    private void prepareMessage(Tweet tweet) {
        String replyTo = tweet.getUser().getScreenName();
        // Included space for better formatting
        etMessage.setText(replyTo + " ");
        // Moves the cursor at the end of the String
        etMessage.setSelection(etMessage.getText().length());
        remainingChars = TWEET_CHAR_LIMIT - replyTo.length();
        tvCharCount.setText(remainingChars + "");
    }

    // Listener to update the textview that shows the numbers of characters left in a tweet message
    private void setupListener() {
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Update the Characters count in the toolbar
                // count = 1 when a char key is pressed; 0 if backspace is pressed
                // count can only only have two values: 0 or 1
                remainingChars = (count > 0) ? remainingChars - 1 : remainingChars + 1;
                tvCharCount.setText(remainingChars + "");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void init() {
        remainingChars = TWEET_CHAR_LIMIT;
        initToolbar();
        client = new TwitterClient(this);
        etMessage = (EditText) findViewById(R.id.etMessage);
        relativeDate = new RelativeDate();
    }

    // Toolbar is a replacement to the older actionbar
    private void initToolbar() {
        // Set a toolbar to replace the action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Set the home icon on toolbar
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setIcon(R.drawable.ic_twitter_home);

        // Add a textview to the toolbar
        tvCharCount = (TextView) findViewById(R.id.tvCharCount);
        tvCharCount.setText(remainingChars + "");
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
                setViewValues(currentUser);
            }
        });
    }

    private void setViewValues(User currentUser) {
        // Get the references and set the values for views
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        ivProfileImage.setImageResource(android.R.color.transparent);
//        Picasso.with(this).load(currentUser.getProfileImageUrl()).error(R.drawable.abc_ab_share_pack_holo_dark).into(ivProfileImage);
        Picasso.with(this).load(currentUser.getProfileImageUrl()).into(ivProfileImage);

        // Set the Name
        TextView tvName = (TextView) findViewById(R.id.tvName);
        tvName.setText(currentUser.getName());

        // Set the screenName
        TextView tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        tvScreenName.setText(currentUser.getScreenName());


        etMessage.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etMessage, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {
            Log.d("DEBUG", "Clicked");
            postTweet();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void postTweet() {
        tweetText = etMessage.getText().toString();
        if (inReplyTo == null) {
            client.postTweet(tweetText, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    // Create a tweet object and pass it to the parent activity
                    // to update the listview; This is a hack to quickly update the listview
                    // without waiting for the current tweet to show up in Twitter's API
                    Tweet tweet = constructTweet(tweetText);
                    finishActivity(tweet);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.d("DEBUG FAILED", statusCode + "");
                }
            });
        } else {
            client.postReply(tweetText, inReplyTo, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    // Create a tweet object and pass it to the parent activity
                    // to update the listview; This is a hack to quickly update the listview
                    // without waiting for the current tweet to show up in Twitter's API
                    Tweet tweet = constructTweet(tweetText);
                    finishActivity(tweet);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.d("DEBUG FAILED", statusCode + "");
                }
            });
        }
    }

    // Closes the activity and passes the data back to the parent activity
    private void finishActivity(Tweet tweet) {
        Intent data = new Intent();
        data.putExtra("tweet", tweet);
        data.putExtra("code", 200);
        // Activity finished ok, return the data
        setResult(RESULT_OK, data); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent
        // Apply Bottom-up transition
        overridePendingTransition(R.animator.slide_out_up, R.animator.slide_in_up);
    }

    private Tweet constructTweet(String status) {
        Tweet tweet = new Tweet();
        tweet.setBody(status);
        tweet.setUser(currentUser);
        tweet.setCreatedAt(relativeDate.createDateInTwitterFormat());
        return tweet;
    }
}
