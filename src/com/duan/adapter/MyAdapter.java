package com.duan.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.duan.Model.Song;
import com.duan.lazylist.ImageLoader;
import com.duan.nghenhac.R;
import com.duan.nghenhac.R.id;
import com.duan.nghenhac.R.layout;

public class MyAdapter extends ArrayAdapter {
	ArrayList<Song> menuitems;
	public ImageLoader imageLoader;
	Context context;

	public MyAdapter(Context context, int textViewResourceId,
			ArrayList<Song> menuitems) {

		super(context, textViewResourceId, menuitems);
		this.context = context;
		this.menuitems = menuitems;
		imageLoader = new ImageLoader(context);
	}

	public class holder {
		TextView tvTitle, tvArtist;
		ImageView image;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		holder mHolder;
		LayoutInflater inf = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// View rowview = inf.inflate(R.layout.item_list, null);
		Log.d("url",""+menuitems.get(position).getAvatarArtist().toString());
		String url = menuitems.get(position).getAvatarArtist().toString();
		if (convertView == null) {
			convertView = inf.inflate(R.layout.item_list, null);
			mHolder = new holder();
			mHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
			mHolder.tvArtist = (TextView) convertView
					.findViewById(R.id.tvArtist);
			mHolder.image = (ImageView) convertView
					.findViewById(R.id.imageView1);
			convertView.setTag(mHolder);
		} else {
			mHolder = (holder) convertView.getTag();
		}
		mHolder.tvTitle.setText(menuitems.get(position).getTitle().toString());
		mHolder.tvArtist
				.setText(menuitems.get(position).getArtist().toString());
		imageLoader.DisplayImage(url, mHolder.image);
		return convertView;
	}

}
