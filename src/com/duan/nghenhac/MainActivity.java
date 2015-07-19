package com.duan.nghenhac;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.duan.Model.Menu_Item;
import com.duan.adapter.MyAdapterMenu;

public class MainActivity extends Activity {
	ListView myListView;
	ArrayList<Menu_Item> myArray = new ArrayList<Menu_Item>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		myListView = (ListView) findViewById(R.id.left_drawer);
		Menu_Item item1 = new Menu_Item();
		item1.setName("Bảng Xếp Hạng");
		item1.setImage(R.drawable.charst);
		Menu_Item item2 = new Menu_Item();
		item2.setName("Bài Yêu Thích");
		item2.setImage(R.drawable.mylist);
		myArray.add(item1);
		myArray.add(item2);
		MyAdapterMenu adapter = new MyAdapterMenu(MainActivity.this,
				R.layout.menu_item_list, myArray);
		myListView.setAdapter(adapter);
		myListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					Intent intent = new Intent(MainActivity.this,
							ListChartActivity.class);
					startActivity(intent);
					break;
				case 1:
					Intent intent1 = new Intent(MainActivity.this,
							MyListActivity.class);
					startActivity(intent1);
					break;
				default:
					break;
				}

			}
		});
	}
}
