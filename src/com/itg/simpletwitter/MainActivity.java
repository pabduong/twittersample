package com.itg.simpletwitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.itg.simpletwitter.adapters.TweetAdapter;
import com.itg.simpletwitter.models.Tweets;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity {

	private String TAG = "MainActivity";

	private static final String consumer_key = "675D62fFnMtz5Y4dgQxzQ";
	private static final String consumer_secret = "WHCR88uRVPUGv7yNjpXrwyLNJVBiLIQ4Ms0RSTU";
	private static final String access_token_url = "https://api.twitter.com/oauth2/token?grant_type=client_credentials";
	private static final String user_timeline_url = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=twitterapi&count=5";

	private String access_token = null;
	private ListView mListView;
	private TweetAdapter mAdapter = null;
	ArrayList<Tweets> tweetItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mListView = (ListView) findViewById(R.id.lv_twitter);
		mListView.setOnItemClickListener(onItemClick);
		new AccessTwitter().execute();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	private void getAccessToken() {
		HttpURLConnection connection = null;
		try {
			JSONObject response = null;
			String authorizationString = consumer_key + ":" + consumer_secret;

			URL myURL = new URL(access_token_url);
			connection = (HttpURLConnection) myURL.openConnection();

			String basicAuth = "Basic "
					+ new String(new Base64().encode(authorizationString
							.getBytes()));
			connection.setRequestProperty("Authorization", basicAuth);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			if (connection.getResponseCode() == HttpStatus.SC_OK) {
				String responseString = readStream(connection.getInputStream());
				response = new JSONObject(responseString);

				access_token = response.optString("access_token");
				Log.d("duongnx", "responseString:" + access_token);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	private void getTweets() {
		HttpURLConnection connection = null;
		try {
			JSONArray response = null;

			URL myURL = new URL(user_timeline_url);
			connection = (HttpURLConnection) myURL.openConnection();

			String headerStr = "Bearer " + access_token;
			connection.setRequestProperty("Authorization", headerStr);
			connection.setRequestMethod("GET");

			if (connection.getResponseCode() == HttpStatus.SC_OK) {
				String responseString = readStream(connection.getInputStream());
				response = new JSONArray(responseString);
				parseTweets(response);
				Log.d("duongnx", "responseString:" + responseString);
			}

		} catch (Exception e) {
			Log.d("duongnx", "Exception::" + e.getMessage());
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	private class AccessTwitter extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			getAccessToken();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (access_token != null) {
				new TweetsTwitter().execute();
			}
		}
	}

	private class TweetsTwitter extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			getTweets();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (tweetItems == null) {
				return;
			}
			if (mAdapter == null) {
				mAdapter = new TweetAdapter(MainActivity.this);
			}
			mAdapter.addData(tweetItems);
			mListView.setAdapter(mAdapter);
		}
	}

	private String readStream(InputStream in) {
		BufferedReader reader = null;
		StringBuffer response = new StringBuffer();
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return response.toString();
	}

	private void parseTweets(JSONArray arr) {

		tweetItems = new ArrayList<Tweets>();
		for (int i = 0; i < arr.length(); i++) {
			try {
				JSONObject obj = arr.getJSONObject(i);
				Tweets item = new Tweets();
				item.setCreatedDate(obj.optString("created_at"));
				item.setId(obj.optString("id"));
				item.setText(obj.getString("text"));

				tweetItems.add(item);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		Log.d("duongnx", "parseTweets::success");
	}

	private OnItemClickListener onItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Intent i = new Intent(MainActivity.this, DetailActivity.class);
			i.putExtra("text_content", tweetItems.get(arg2).getText());
			startActivity(i);
		}
	};
}
