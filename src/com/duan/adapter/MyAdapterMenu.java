package com.duan.adapter;

import java.util.ArrayList;

import com.duan.Model.Menu_Item;
import com.duan.nghenhac.R;
import com.duan.nghenhac.R.id;
import com.duan.nghenhac.R.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapterMenu extends ArrayAdapter<Menu_Item> {
	Context context;
	ArrayList<Menu_Item> menuitems;
	int image;

	public MyAdapterMenu(Context context, int textViewResourceId,
			ArrayList<Menu_Item> menuitems) {

		super(context, textViewResourceId, menuitems);
		this.context = context;
		this.menuitems = menuitems;
	}

	public class holder {
		TextView tvItem;
		ImageView image;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		holder mHolder;
		LayoutInflater inf = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// View rowview = inf.inflate(R.layout.item_list, null);
		if (convertView == null) {
			convertView = inf.inflate(R.layout.menu_item_list, null);
			mHolder = new holder();
			mHolder.tvItem = (TextView) convertView.findViewById(R.id.tvItem);
			mHolder.image = (ImageView) convertView.findViewById(R.id.imgIcon);
			convertView.setTag(mHolder);
		} else {
			mHolder = (holder) convertView.getTag();
		}
		mHolder.tvItem.setText(menuitems.get(position).getName());
		mHolder.image.setImageResource(menuitems.get(position).getImage());
		return convertView;
	}
}
