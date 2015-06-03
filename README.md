# TwitterClient
**Simple Tweets** is an android app that allows a user to view his Twitter timeline and post a new tweet. The app utilizes [Twitter REST API](https://dev.twitter.com/rest/public).

Time spent: 35 hours spent in total

## User Stories

The following **required** functionality is completed:

* [x]	User can **sign in to Twitter** using OAuth login
* [x]	User can **view tweets from their home timeline**
  * [x] User is displayed the username, name, and body for each tweet
  * [x] User is displayed the [relative timestamp](https://gist.github.com/nesquena/f786232f5ef72f6e10a7) for each tweet "8m", "7h"
  * [x] User can view more tweets as they scroll with [infinite pagination](http://guides.codepath.com/android/Endless-Scrolling-with-AdapterViews). Number of tweets is unlimited.
    However there are [Twitter Api Rate Limits](https://dev.twitter.com/rest/public/rate-limiting) in place.
* [x] User can **compose and post a new tweet**
  * [x] User can click a “Compose” icon in the Action Bar on the top right
  * [x] User can then enter a new tweet and post this to twitter
  * [x] User is taken back to home timeline with **new tweet visible** in timeline

The following **optional** features are implemented:

* [x] User can **see a counter with total number of characters left for tweet** on compose tweet page
* [x] User can **click a link within a tweet body** on tweet details view. The click will launch the web browser with relevant page opened.
* [x] User can **pull down to refresh tweets timeline**
* [x] User can **open the twitter app offline and see last loaded tweets**. Persisted in SQLite tweets are refreshed on every application launch. While "live data" is displayed when app can get it from Twitter API, it is also saved for use in offline mode.
* [x] User can **select "reply" from detail view to respond to a tweet**
* [x] Improve the user interface and theme the app to feel "twitter branded"

The following **additional** features are implemented:
* [x] Used a custom toolbar instead of the stock actionbar
* [x] User can see embedded images within home timeline stream
* [x] Used Calligraphy library for custom fonts
* [x] Played with activity transitions. Applied "bottom-up" transition when going from Main Activity => Compose Activity

## Update TwitterClient 0.2
* [x] User can switch between Timeline and Mention views using tabs.
  * [x] User can view their home timeline tweets.
  * [x] User can view the recent mentions of their username.
* [x] User can navigate to view their own profile
  * [x] User can see picture, # of followers, # of following, and tweets on their profile.
* [x] User can click on the profile image in any tweet to see another user's profile.
  * [x] User can see picture, # of followers, # of following, and tweets of clicked user.
  * [x] Profile view should include that user's timeline
* [x] User can infinitely paginate any of these timelines (home, mentions, user) by scrolling to the bottom

The following advanced user stories were also implemented:
* [x] Robust error handling, check if internet is available, handle error cases, network failures
* [x] When a network request is sent, user sees an indeterminate progress indicator
* [x] User can see see a banner image on user profile
* [x] User can "reply" to any tweet on their home timeline
  * [x] The user that wrote the original tweet is automatically "@" replied in compose
* [x] User can take favorite (and unfavorite) or reweet (and un-retweet) actions on a tweet
* [x] Improve the user interface and theme the app to feel twitter branded

## Gif Walkthrough 
![alt tag](https://github.com/nyjalusc/TwitterClient/blob/master/walkthrough2.gif)

Link to the high-res video can be found [here](https://cloud.app.box.com/s/f91zq1rip67orjszodmpcjdr2hdpnwek).

Older version Walkthrough can be found [here](https://github.com/nyjalusc/TwitterClient/blob/master/walkthrough.gif)

## Open-source libraries used

- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Picasso](http://square.github.io/picasso/) - Image loading and caching library for Android
- [Calligraphy](https://github.com/chrisjenx/Calligraphy) - Custom fonts
- [ActiveAndroid](http://www.activeandroid.com/) - Persist tweets in SQLite for offline viewing
- [Material-ish Progress](https://github.com/pnikosis/materialish-progress) - Material design inspired progressbar
