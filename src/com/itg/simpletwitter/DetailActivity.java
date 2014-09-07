/**
 * 
 */
package com.itg.simpletwitter;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * @author DUONGNX
 * 
 */
public class DetailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_item);
		Bundle extras = getIntent().getExtras();
		String content = extras.getString("text_content");
		TextView tv = (TextView) findViewById(R.id.text_item);
		tv.setText(content);

	}
}
