package com.duan.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.duan.Model.Song;
import com.duan.adapter.MyAdapter;
import com.duan.nghenhac.MyService;
import com.duan.nghenhac.PlayActivity;
import com.duan.nghenhac.R;

public class Fragment_SongList extends Fragment {
	ListView mListView;
	ArrayList<Song> mArray = new ArrayList<Song>();
	WifiManager wifiManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_list, container, false);
		mListView = (ListView) view.findViewById(R.id.fmListView);
		mArray = (ArrayList<Song>) getArguments().getSerializable("Array");
		MyAdapter adapter = new MyAdapter(getActivity(), R.layout.item_list,
				mArray);
		mListView.setAdapter(adapter);
		ConnectivityManager manager = (ConnectivityManager) getActivity()
				.getSystemService(PlayActivity.CONNECTIVITY_SERVICE);

		// For 3G check
		boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.isConnectedOrConnecting();
		// For WiFi Check
		boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnectedOrConnecting();
		if (!is3g && !isWifi) {
			Toast.makeText(getActivity(), "Không thể kết nối tới máy chủ 2", 0)
					.show();

		} else {
			mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent intent = new Intent(getActivity(), MyService.class);
					intent.setAction(MyService.ACTION_PLAY);
					intent.putExtra("index", position);
					getActivity().startService(intent);
					PlayActivity.fragPlay.setSong(mArray.get(position));

				}
			});
		}
		return view;
	}
}
