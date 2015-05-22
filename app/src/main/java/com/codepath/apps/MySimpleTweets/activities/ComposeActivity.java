package com.codepath.apps.MySimpleTweets.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.MySimpleTweets.R;
import com.codepath.apps.MySimpleTweets.models.User;
import com.codepath.apps.MySimpleTweets.net.TwitterClient;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        init();
//        getUserDetail();
        setupListener();
    }

    private void setupListener() {
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Update the Characters count in the toolbar
                if (count > 0) {
                    remainingChars--;
                } else {
                    remainingChars++;
                }
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
    }

    // Toolbar is a replacement to the older actionbar
    private void initToolbar() {
        // Set a toolbar to replace the action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Set the home icon on toolbar
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setIcon(R.drawable.ic_twitter_home);

        // Add a textview to the toolbar
        View actionbar_count = getLayoutInflater().inflate(R.layout.actionbar_view, null);
        tvCharCount = (TextView) actionbar_count.findViewById(R.id.tvCharCount);
        tvCharCount.setText(remainingChars + "");
        toolbar.addView(actionbar_count);
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
        Picasso.with(this).load(currentUser.getProfileImageUrl()).error(R.drawable.abc_ab_share_pack_holo_dark).into(ivProfileImage);

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

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
