package com.duan.nghenhac;

import java.util.ArrayList;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.duan.Model.Song;
import com.duan.database.Database;
import com.duan.fragment.Fragment_Play;
import com.duan.fragment.Fragment_SongList;

public class PlayActivity extends FragmentActivity implements OnClickListener {
	private FragmentManager fragmentManager = getFragmentManager();
	private FragmentTransaction fragmentTransaction = fragmentManager
			.beginTransaction();
	public static Fragment_Play fragPlay = new Fragment_Play();
	public static Fragment_SongList fragSongList = new Fragment_SongList();
	LinearLayout myLayout;
	Song song;
	String link;

	public static ArrayList<Song> mArraySong = new ArrayList<Song>();
	//
	MyAdapter myadapter;
	ViewPager myviewpager;
	//
	ImageButton btPlay, btNext, btPrevious, btPause;
	public static TextView tvRunTime, tvTotalTime, tvSongName;
	public static SeekBar mSeekBar;
	public static int index;
	WifiManager wifiManager;
	Database mDB;
	private SQLiteDatabase mDb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		myLayout = (LinearLayout) findViewById(R.id.layoutPlay);
		tvRunTime = (TextView) findViewById(R.id.tvRunTime);
		tvTotalTime = (TextView) findViewById(R.id.tvTotalTime);
		mSeekBar = (SeekBar) findViewById(R.id.seekBar1);
		//
		index = getIntent().getExtras().getInt("index");
		btPlay = (ImageButton) findViewById(R.id.imgPlay);
		btNext = (ImageButton) findViewById(R.id.imgJumpNext);
		btPrevious = (ImageButton) findViewById(R.id.imgJumpBack);
		btPause = (ImageButton) findViewById(R.id.imgPause);
		mArraySong = (ArrayList<Song>) getIntent().getExtras().get("Array");
		Log.d("PlayIndex", "" + index);
		//
		mDB = new Database(PlayActivity.this);
		mDb = mDB.getWritableDatabase();
		// Truyền dữ liệu qua Fragment
		Bundle data = new Bundle();
		data.putSerializable("Object", mArraySong.get(index));
		Bundle data1 = new Bundle();
		data1.putSerializable("Array", mArraySong);
		fragSongList.setArguments(data1);
		fragPlay.setArguments(data);
		myviewpager = (ViewPager) findViewById(R.id.viewpager);
		myadapter = new MyAdapter(getSupportFragmentManager());
		myviewpager.setAdapter(myadapter);
		Intent i = new Intent(getApplicationContext(), MyService.class);
		i.setAction(MyService.ACTION_PLAY);
		i.putExtra("index", index);
		startService(i);
		btPlay.setOnClickListener(this);
		btNext.setOnClickListener(this);
		btPrevious.setOnClickListener(this);
		btPause.setOnClickListener(this);

	}

	class MyAdapter extends FragmentPagerAdapter {

		public MyAdapter(android.support.v4.app.FragmentManager fm) {
			super(fm);

		}

		@Override
		public Fragment getItem(int arg0) {
			switch (arg0) {

			case 0:
				return fragPlay;

			case 1:
				return fragSongList;
			}
			return null;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 2;
		}

	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(getApplicationContext(), MyService.class);
		intent.putExtra("index", index);
		ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

		// For 3G check
		boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.isConnectedOrConnecting();
		// For WiFi Check
		boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnectedOrConnecting();
		wifiManager = (WifiManager) getApplicationContext().getSystemService(
				Context.WIFI_SERVICE);
		// if (!wifiManager.isWifiEnabled()) {
		// Toast.makeText(getApplicationContext(),
		// "Không thể kết nối tới máy chủ 2", 0).show();
		// }
		if (!is3g && !isWifi) {
			Toast.makeText(getApplicationContext(), "Kiểm tra wifi hoặc 3g", 0)
					.show();
			fragPlay.stopAnimation(fragPlay.image);
		} else {
			switch (v.getId()) {
			case R.id.imgPlay:
				intent.setAction(MyService.ACTION_PLAY);
				startService(intent);
				break;
			case R.id.imgJumpNext:
				intent.setAction(MyService.ACTION_NEXT);
				startService(intent);

				break;
			case R.id.imgPause:
				intent.setAction(MyService.ACTION_PAUSE);
				startService(intent);
				break;
			case R.id.imgJumpBack:
				intent.setAction(MyService.ACTION_PREVIOUS);
				startService(intent);
				break;
			default:
				break;
			}
		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.inback, R.anim.outback);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		if (id == R.id.action_like) {
			if (mDB.checkInsert(mArraySong.get(index).getTitle()) == false) {
				mDB.insertItem(mArraySong.get(index).getTitle(), mArraySong
						.get(index).getArtist(), mArraySong.get(index)
						.getLinkPlay320(), mArraySong.get(index)
						.getAvatarArtist(), mArraySong.get(index).getId());
				Toast.makeText(
						PlayActivity.this,
						"Lưu bài hát " + mArraySong.get(index).getTitle()
								+ " vào playlist", 0).show();
			} else
				Toast.makeText(PlayActivity.this,
						"Đã có bài hát này trong playlist", 0).show();

			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
