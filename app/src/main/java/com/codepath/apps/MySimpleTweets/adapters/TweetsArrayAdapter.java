package com.codepath.apps.MySimpleTweets.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.MySimpleTweets.R;
import com.codepath.apps.MySimpleTweets.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.List;

// Taking the tweet object and turning them into views that will be displayed in the list
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

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
    }

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, 0, tweets);
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
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
            viewHolder.ivRetweet = (ImageView) convertView.findViewById(R.id.ivRetweet);
            viewHolder.tvRetweetCount = (TextView) convertView.findViewById(R.id.tvRetweetCount);
            viewHolder.ivFavorites = (ImageView) convertView.findViewById(R.id.ivFavorites);
            viewHolder.tvFavoritesCount = (TextView) convertView.findViewById(R.id.tvFavoritesCount);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 3. Set values on subviews
        // Remove old image and set a new one
        viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);
        Picasso.with(getContext())
                .load(tweet.getUser().getProfileImageUrl())
                .error(R.drawable.abc_ab_share_pack_holo_dark)
                .into(viewHolder.ivProfileImage);
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
                    .error(R.drawable.abc_ab_share_pack_holo_dark)
                    .into(viewHolder.ivImage);
        }

        setupTweetFooter(viewHolder, tweet);
        return convertView;
    }

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

    private void setupViewListeners(ViewHolder viewHolder) {
    }
}
