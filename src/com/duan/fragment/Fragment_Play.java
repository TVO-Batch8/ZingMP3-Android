package com.duan.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.duan.Model.Song;
import com.duan.lazylist.ImageLoader;
import com.duan.lazylist.MemoryCache;
import com.duan.nghenhac.PlayActivity;
import com.duan.nghenhac.R;
import com.duan.nghenhac.RoundImage;

public class Fragment_Play extends Fragment {
	TextView tvSongName, tvArtist;
	Song song;
	public static ImageView image;
	ImageLoader imageLoader;
	RoundImage roundedImage;
	MemoryCache memoryCache = new MemoryCache();
	Animation myAnimation;
	WifiManager wifiManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_play, container, false);
		// Ánh Xạ
		tvSongName = (TextView) view.findViewById(R.id.tvSongName);
		tvArtist = (TextView) view.findViewById(R.id.tvArtist);
		image = (ImageView) view.findViewById(R.id.imgSinger);
		// End Ánh Xạ
		imageLoader = new ImageLoader(getActivity());
		song = (Song) getArguments().getSerializable("Object");
		tvSongName.setText(song.getTitle());
		tvArtist.setText(song.getArtist());

		wifiManager = (WifiManager) getActivity().getSystemService(
				Context.WIFI_SERVICE);
		setSong(song);

		return view;
	}

	public class myTash extends AsyncTask<Void, Void, Void> {
		Bitmap bitmap;
		Song song;

		public myTash(Song song) {
			this.song = song;
		}

		@Override
		protected Void doInBackground(Void... params) {

			bitmap = getBitmapFromURL(song.getAvatarArtist());

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (bitmap == null) {
				bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.zing);
			}

			roundedImage = new RoundImage(bitmap);
			image.setImageDrawable(roundedImage);
			super.onPostExecute(result);
		}

	}

	public void setSong(Song song) {
		tvSongName.setText(song.getTitle());
		tvArtist.setText(song.getArtist());
		// imageLoader.DisplayImage(song.getAvatarArtist(), image);
		myTash aa = new myTash(song);
		aa.execute();
		ConnectivityManager manager = (ConnectivityManager) getActivity()
				.getSystemService(PlayActivity.CONNECTIVITY_SERVICE);

		// For 3G check
		boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.isConnectedOrConnecting();
		// For WiFi Check
		boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnectedOrConnecting();
		if (!is3g && !isWifi) {
			Toast.makeText(getActivity(),
					"Không thể kết nối tới máy chủ 2", 0).show();

		} else {
			startAnimation(image);
		}

	}

	public void startAnimation(ImageView image) {
		myAnimation = AnimationUtils
				.loadAnimation(getActivity(), R.anim.rotate);
		myAnimation.setDuration(10000);
		myAnimation.setStartOffset(0);
		image.startAnimation(myAnimation);

	}

	public void stopAnimation(ImageView image) {
		image.clearAnimation();
	}

	public static Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
