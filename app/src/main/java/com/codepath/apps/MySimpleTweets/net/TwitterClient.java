package com.codepath.apps.MySimpleTweets.net;

import android.content.Context;
import android.util.Log;

import com.codepath.apps.MySimpleTweets.activities.TimelineActivity.TimelineParams;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import java.util.HashMap;
import java.util.Set;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1/";
	public static final String REST_CONSUMER_KEY = "sACNpXq6KZHYP8uo0wa72gZDT";
	public static final String REST_CONSUMER_SECRET = "EsAVcafSmHQC1zVPIsbIU4V7CB9B4XiS1A8p0d13DoPxHA4iL2";
	public static final String REST_CALLBACK_URL = "oauth://njsimpletweets";

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
	public void getInterestingnessList(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("?nojsoncallback=1&method=flickr.interestingness.getList");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("format", "json");
		client.get(apiUrl, params, handler);
	}

	// METHOD == ENDPOINT

	// Home Timeline - Gets us the home timeline
	public void getHomeTimeline(HashMap<String, String> endpointKeyMap, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		Set<String> keys = endpointKeyMap.keySet();
		for (String key : keys) {
			if (key.equals(TimelineParams.COUNT.toString())) {
				Log.d("DEBUG COUNT", endpointKeyMap.get(key).toString());
				params.put(key, Integer.parseInt(endpointKeyMap.get(key)));
			} else {
				String value = endpointKeyMap.get(key);
				if (value != null) {
					Log.d("DEBUG", endpointKeyMap.get(key).toString());
					params.put(key, Long.parseLong(endpointKeyMap.get(key)));
				}
			}
		}
		Log.d("DEBUG", "executing");
		// Execute the request
		getClient().get(apiUrl, params, handler);
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}