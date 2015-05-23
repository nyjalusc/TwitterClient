package com.codepath.apps.MySimpleTweets.adapters;

import android.content.Context;
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
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 3. Set values on subviews
        // Remove old image and set a new one
        viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);
        if (tweet.getUser() == null) {
            Log.d("DEBUG", "Found null user");
        }
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).error(R.drawable.abc_ab_share_pack_holo_dark).into(viewHolder.ivProfileImage);
        viewHolder.tvRelativeTime.setText(tweet.getRelativeTime());
        viewHolder.tvBody.setText(tweet.getBody());
        viewHolder.tvName.setText(tweet.getUser().getName());
        viewHolder.tvScreenName.setText(tweet.getUser().getScreenName());

        return convertView;
    }
}
