package com.duan.nghenhac;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.duan.Model.Song;
import com.duan.adapter.MyAdapter;
import com.duan.database.Database;

public class MyListActivity extends Activity {
	ListView myListView;
	ArrayList<Song> mArray = new ArrayList<Song>();
	Database mDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_list);
	
		myListView = (ListView) findViewById(R.id.mylist);
		try {
			mDB = new Database(MyListActivity.this);
			mArray = mDB.listIdInfo1();
		} catch (Exception e) {
			Toast.makeText(MyListActivity.this, "Bạn chưa yêu thích bài nào", 0)
					.show();
		}
		if (mArray.size() > 0) {
			MyAdapter adapter = new MyAdapter(MyListActivity.this,
					R.layout.item_list, mArray);
			myListView.setAdapter(adapter);
			Log.d("avatar", "" + mArray.get(0).getAvatarArtist());
			myListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent intent = new Intent(MyListActivity.this,
							PlayActivity.class);
					intent.putExtra("Array", mArray);
					intent.putExtra("index", position);
					startActivity(intent);

				}
			});
		}
		registerForContextMenu(myListView);

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// menu.setHeaderTitle("Animal Choose");
		// menu.add(0, v.getId(), 0, "Xóa Bài Này");
		// menu.add(0, v.getId(), 0, "Hủy");
		MenuInflater inf = getMenuInflater();
		inf.inflate(R.menu.menu_listview, menu);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int vitri = (int) myListView.getItemIdAtPosition(info.position);
		Song song = (Song) myListView.getItemAtPosition(vitri);
		switch (item.getItemId()) {
		case R.id.xoa_item:
			Toast.makeText(getApplicationContext(),
					"Xóa Thành Công " + song.getId(), Toast.LENGTH_SHORT)
					.show();
			mDB.deleteSong(song.getId());
			data();
			break;
		}
		return true;

	}

	public void data() {
		try {
			mDB = new Database(MyListActivity.this);
			mArray = mDB.listIdInfo1();
			MyAdapter adapter = new MyAdapter(MyListActivity.this,
					R.layout.item_list, mArray);
			myListView.setAdapter(adapter);
		} catch (Exception e) {
			Toast.makeText(MyListActivity.this, "Bạn chưa yêu thích bài nào", 0)
					.show();
		}
	}

}
