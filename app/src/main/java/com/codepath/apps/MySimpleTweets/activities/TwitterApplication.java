package com.codepath.apps.MySimpleTweets.activities;

import android.content.Context;

import com.codepath.apps.MySimpleTweets.R;
import com.codepath.apps.MySimpleTweets.net.TwitterClient;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest client.
 *
 *     TwitterClient client = TwitterApplication.getRestClient();
 *     // use client to send requests to API
 *
 */
public class TwitterApplication extends com.activeandroid.app.Application {
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
//		CalligraphyConfig.initDefault("fonts/Roboto-Thin.ttf");
		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
				.setDefaultFontPath("fonts/Roboto-Light.ttf")
				.setFontAttrId(R.attr.fontPath)
				.build());
		TwitterApplication.context = this;
	}

	public static TwitterClient getRestClient() {
		return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, TwitterApplication.context);
	}
}