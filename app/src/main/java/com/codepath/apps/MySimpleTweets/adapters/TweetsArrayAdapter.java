package com.codepath.apps.MySimpleTweets.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.apps.MySimpleTweets.R;
import com.codepath.apps.MySimpleTweets.activities.ProfileActivity;
import com.codepath.apps.MySimpleTweets.activities.TimelineActivity;
import com.codepath.apps.MySimpleTweets.activities.TwitterApplication;
import com.codepath.apps.MySimpleTweets.models.Tweet;
import com.codepath.apps.MySimpleTweets.net.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

// Taking the tweet object and turning them into views that will be displayed in the list
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    private TwitterClient client;

    // ViewHolder Pattern
    private static class ViewHolder {
        ImageView ivProfileImage;
        TextView tvRelativeTime;
        TextView tvName;
        TextView tvScreenName;
        TextView tvBody;
        ImageView ivImage;
        ImageView ivRetweet;
        TextView tvRetweetCount;
        ImageView ivFavorites;
        TextView tvFavoritesCount;
        ImageView ivReply;
        TextView tvRetweetedUser;
        RelativeLayout rlRetweetHolder;
        RelativeLayout rlFavoritesHolder;
        RelativeLayout rlRetweetInfoRow;
        long id;
    }

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, 0, tweets);
        client = TwitterApplication.getRestClient();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1. Get the tweet
        Tweet tweet = getItem(position);
        ViewHolder viewHolder = new ViewHolder();
        // 2. Inflate the template and get refs to subviews
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
            viewHolder.ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
            viewHolder.tvRelativeTime = (TextView) convertView.findViewById(R.id.tvRelativeTime);
            viewHolder.tvRetweetedUser = (TextView) convertView.findViewById(R.id.tvRetweetedUser);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
            viewHolder.ivRetweet = (ImageView) convertView.findViewById(R.id.ivRetweet);
            viewHolder.tvRetweetCount = (TextView) convertView.findViewById(R.id.tvRetweetCount);
            viewHolder.ivFavorites = (ImageView) convertView.findViewById(R.id.ivFavorites);
            viewHolder.tvFavoritesCount = (TextView) convertView.findViewById(R.id.tvFavoritesCount);
            viewHolder.ivReply = (ImageView) convertView.findViewById(R.id.ivReply);
            viewHolder.rlRetweetHolder = (RelativeLayout) convertView.findViewById(R.id.rlRetweetHolder);
            viewHolder.rlFavoritesHolder = (RelativeLayout) convertView.findViewById(R.id.rlFavoritesHolder);
            viewHolder.rlRetweetInfoRow = (RelativeLayout) convertView.findViewById(R.id.rlRetweetInfoRow);
            viewHolder.id = tweet.getUid();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Check if there is an original tweet associated with the tweet
        // If true then this tweet is a retweet; we need to set the views by using values of
        // original tweet otherwise use the current tweet.
        if (tweet.hasOriginalTweet()) {
            viewHolder.rlRetweetInfoRow.setVisibility(View.VISIBLE);
            viewHolder.tvRetweetedUser.setText(tweet.getUser().getName() + " retweeted");
            setupViews(viewHolder, tweet.getOriginalTweet());
        } else {
            viewHolder.rlRetweetInfoRow.setVisibility(View.GONE);
            setupViews(viewHolder, tweet);
        }

        setupTweetFooter(viewHolder, tweet);
        setupViewListerners(viewHolder, tweet);
        return convertView;
    }

    private void setupViews(ViewHolder viewHolder, Tweet tweet) {
        viewHolder.ivProfileImage.setImageResource(0);
        Picasso.with(getContext())
                .load(tweet.getUser().getProfileImageUrl())
                .error(R.drawable.ic_error)
                .fit()
                .into(viewHolder.ivProfileImage);
        // This will be used in the click listener to figure out the name of the user
        viewHolder.ivProfileImage.setTag(tweet.getUser().getScreenName());

        viewHolder.tvRelativeTime.setText(tweet.getRelativeTime());
        viewHolder.tvBody.setText(tweet.getBody());
        viewHolder.tvName.setText(tweet.getUser().getName());
        viewHolder.tvScreenName.setText(tweet.getUser().getScreenName());

        // Not all tweets have images; Check if its null before setting the view
        if (tweet.getImage() == null) {
            viewHolder.ivImage.setVisibility(View.GONE);
        } else {
            viewHolder.ivImage.setVisibility(View.VISIBLE);
            viewHolder.ivImage.setImageResource(0);
            Picasso.with(getContext())
                    .load(tweet.getImage().getSmallImageUrl())
                    .error(R.drawable.ic_error)
                    .fit()
                    .into(viewHolder.ivImage);
        }
    }

    private void setupViewListerners(final ViewHolder viewHolder, final Tweet tweet) {
        // View listener for Retweets
        viewHolder.rlRetweetHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ImageView ivRetweet = (ImageView) view.findViewById(R.id.ivRetweet);
                final TextView tvRetweetCount = (TextView) view.findViewById(R.id.tvRetweetCount);
                if (tweet.isRetweeted()) {
                    client.postDeleteRetweet(tweet.getRetweetidStr(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                tweet.setRetweetIdStr(null);
                                tweet.setRetweeted(false);
                                tweet.setRetweetCount(response.getInt("retweet_count") - 1);
                                tweet.save();
                                // Update the view UI to show grey retweet logo and grey retweet count text
                                ivRetweet.setImageResource(R.drawable.ic_retweet_grey);
                                tvRetweetCount.setTextColor(getContext().getResources().getColor(R.color.grey));
                                tvRetweetCount.setTypeface(null, Typeface.NORMAL);
                                // Update the new retweet count
                                tvRetweetCount.setText(tweet.getRetweetCount());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } else {
                    client.postRetweet(tweet.getUidStr(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                // Retweets are just like tweets. I am only keeping the id_str of the new retweet
                                // so that when i delete it i should know which retweet i am deleting.
                                tweet.setRetweetIdStr(response.getString("id_str"));
                                tweet.setRetweeted(true);
                                tweet.setRetweetCount(response.getInt("retweet_count"));
                                tweet.save();
                                // Update the view UI to show green retweet logo and green colored tweet count text
                                ivRetweet.setImageResource(R.drawable.ic_retweet_green);
                                tvRetweetCount.setTextColor(getContext().getResources().getColor(R.color.green));
                                tvRetweetCount.setTypeface(null, Typeface.BOLD);
                                // Update the new retweet count
                                tvRetweetCount.setText(tweet.getRetweetCount());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            }
        });

        // View listener for Favorites
        viewHolder.rlFavoritesHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final ImageView ivFavorites = (ImageView) view.findViewById(R.id.ivFavorites);
                final TextView tvFavoritesCount = (TextView) view.findViewById(R.id.tvFavoritesCount);
                if (tweet.isFavorited()) {
                    client.postUndoFavorite(tweet.getUid(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                int favoritesCount = response.getInt("favorite_count");
                                tweet.setFavoriteCount(favoritesCount);
                                tweet.setFavorited(false);
                                tweet.save();
                                // Update the view UI to show grey favorite logo and grey colored tweet count text
                                ivFavorites.setImageResource(R.drawable.ic_fav_grey);
                                tvFavoritesCount.setTextColor(getContext().getResources().getColor(R.color.grey));
                                tvFavoritesCount.setTypeface(null, Typeface.NORMAL);
                                // Update the new retweet count
                                tvFavoritesCount.setText(tweet.getFavoritesCount());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    client.postMarkFavorite(tweet.getUid(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                int favoritesCount = response.getInt("favorite_count");
                                tweet.setFavoriteCount(favoritesCount);
                                tweet.setFavorited(true);
                                tweet.save();
                                // Update the view
                                ivFavorites.setImageResource(R.drawable.ic_fav_yellow);
                                tvFavoritesCount.setTextColor(getContext().getResources().getColor(R.color.yellow));
                                tvFavoritesCount.setTypeface(null, Typeface.BOLD);
                                // Update the new retweet count
                                tvFavoritesCount.setText(tweet.getFavoritesCount());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

        // View listener for reply button
        viewHolder.ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Accessing activity method from an adapter
                ((TimelineActivity) getContext()).launchComposeActivityForReply(tweet);
            }
        });

        // View listener for showing the profile of user
        viewHolder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the profile activity directly from the fragment
                Intent i = new Intent(getContext(), ProfileActivity.class);
                i.putExtra("user", tweet.getUser());
                ((TimelineActivity) getContext()).startActivity(i);
            }
        });
    }

    // Sets up view elements such as reply, retweet and favorite
    private void setupTweetFooter(ViewHolder viewHolder, Tweet tweet) {
        viewHolder.ivRetweet.setImageResource(0);
        viewHolder.tvRetweetCount.setText(tweet.getRetweetCount());
        if (tweet.isRetweeted()) {
            Log.d("DEBUG", "Found Retweet");
            Log.d("DEBUG", tweet.getBody());
            viewHolder.ivRetweet.setImageResource(R.drawable.ic_retweet_green);
            viewHolder.tvRetweetCount.setTextColor(getContext().getResources().getColor(R.color.green));
            viewHolder.tvRetweetCount.setTypeface(null, Typeface.BOLD);
        } else {
            viewHolder.ivRetweet.setImageResource(R.drawable.ic_retweet_grey);
            viewHolder.tvRetweetCount.setTextColor(getContext().getResources().getColor(R.color.grey));
            viewHolder.tvRetweetCount.setTypeface(null, Typeface.NORMAL);
        }

        viewHolder.ivFavorites.setImageResource(0);
        viewHolder.tvFavoritesCount.setText(tweet.getFavoritesCount());
        if (tweet.isFavorited()) {
            Log.d("DEBUG", "Found a favorite");
            Log.d("DEBUG", tweet.getBody());
            viewHolder.ivFavorites.setImageResource(R.drawable.ic_fav_yellow);
            viewHolder.tvFavoritesCount.setTextColor(getContext().getResources().getColor(R.color.yellow));
            viewHolder.tvFavoritesCount.setTypeface(null, Typeface.BOLD);
        } else {
            viewHolder.ivFavorites.setImageResource(R.drawable.ic_fav_grey);
            viewHolder.tvFavoritesCount.setTextColor(getContext().getResources().getColor(R.color.grey));
            viewHolder.tvFavoritesCount.setTypeface(null, Typeface.NORMAL);
        }
    }

}
