package com.itg.simpletwitter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

	private String TAG = "MainActivity";
	long timestamp_at_entry;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Calendar c = Calendar.getInstance();
		timestamp_at_entry = c.getTimeInMillis();

		Log.d(TAG, "time=" + timestamp_at_entry);
	}

	@Override
	protected void onResume() {
		super.onResume();
		new AccessTwitter().execute();
	}

	private class AccessTwitter extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			String oauth_signature_method = "HMAC-SHA1";
			String oauth_consumer_key = "675D62fFnMtz5Y4dgQxzQ";
			String twitter_secret = "pDNDAL8Pza6TeF9uewRid1UkNlGJogP78xUpc9bXBRE";
			String uuid_string = UUID.randomUUID().toString();
			uuid_string = uuid_string.replaceAll("-", "");
			String oauth_nonce = uuid_string;

			String oauth_timestamp = (new Long(timestamp_at_entry / 1000))
					.toString();
			String parameter_string = "oauth_consumer_key="
					+ oauth_consumer_key + "&oauth_nonce=" + oauth_nonce
					+ "&oauth_signature_method=" + oauth_signature_method
					+ "&oauth_timestamp=" + oauth_timestamp
					+ "&oauth_version=1.0";
			Log.d(TAG, "parameter_string=" + parameter_string);
			HttpClient httpclient = new DefaultHttpClient();
			try {
				String signature_base_string = "POST&https%3A%2F%2Fapi.twitter.com%2Foauth%2Frequest_token&"
						+ URLEncoder.encode(parameter_string, "UTF-8");

				Log.d(TAG, "signature_base_string=" + signature_base_string);
				String oauth_signature = "";
				oauth_signature = computeSignature(signature_base_string,
						twitter_secret + "&");
				Log.d(TAG,
						"oauth_signature="
								+ URLEncoder.encode(oauth_signature, "UTF-8"));
				String authorization_header_string = "OAuth oauth_consumer_key=\""
						+ oauth_consumer_key
						+ "\",oauth_signature_method=\"HMAC-SHA1\",oauth_timestamp=\""
						+ oauth_timestamp
						+ "\",oauth_nonce=\""
						+ oauth_nonce
						+ "\",oauth_version=\"1.0\",oauth_signature=\""
						+ URLEncoder.encode(oauth_signature, "UTF-8") + "\"";
				Log.d(TAG, "authorization_header_string="
						+ authorization_header_string);

				String oauth_token = "";
				HttpPost httppost = new HttpPost(
						"https://api.twitter.com/oauth/request_token");
				httppost.setHeader("Authorization", authorization_header_string);
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = httpclient.execute(httppost,
						responseHandler);
				oauth_token = responseBody.substring(
						responseBody.indexOf("oauth_token=") + 12,
						responseBody.indexOf("&oauth_token_secret="));
				Log.d(TAG, "response:" + responseBody);
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
				Log.e(TAG, e.getMessage());
			} catch (ClientProtocolException cpe) {
				Log.e(TAG, cpe.getMessage());
			} catch (IOException ioe) {
				Log.e(TAG, ioe.getMessage());
			} finally {
				httpclient.getConnectionManager().shutdown();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
	}

	private static String computeSignature(String baseString, String keyString)
			throws GeneralSecurityException, UnsupportedEncodingException {

		SecretKey secretKey = null;

		byte[] keyBytes = keyString.getBytes();
		secretKey = new SecretKeySpec(keyBytes, "HmacSHA1");

		Mac mac = Mac.getInstance("HmacSHA1");

		mac.init(secretKey);

		byte[] text = baseString.getBytes();

		return new String(Base64.encodeBase64(mac.doFinal(text))).trim();
	}
}
