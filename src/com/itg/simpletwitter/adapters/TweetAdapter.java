/**
 * 
 */
package com.itg.simpletwitter.adapters;

import java.util.ArrayList;

import com.itg.simpletwitter.R;
import com.itg.simpletwitter.models.Tweets;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author DUONGNX
 * 
 */
public class TweetAdapter extends BaseAdapter {

	private ArrayList<Tweets> datas;
	private Context mContext;
	private LayoutInflater mLayoutInflater;

	public TweetAdapter(Context context) {
		this.mContext = context;
		this.mLayoutInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {

		return datas.size();
	}

	@Override
	public Object getItem(int position) {

		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	class ViewHolder {
		public TextView text;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View itemView;
		if (convertView == null) {
			itemView = mLayoutInflater.inflate(R.layout.list_item, parent,
					false);
		} else {
			itemView = convertView;
		}
		TextView tv = (TextView) itemView.findViewById(R.id.text_item);
		tv.setText(datas.get(position).getText());
		return itemView;
	}

	public void addData(ArrayList<Tweets> items) {
		this.datas = items;
	}

	public void clear() {
		datas.clear();
	}
}
