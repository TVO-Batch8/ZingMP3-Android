package com.duan.nghenhac;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.RemoteControlClient;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.duan.Model.Song;
import com.duan.broadcast.MusicIntentReceiver;
import com.google.gson.annotations.Until;

@SuppressLint("NewApi")
public class MyService extends Service implements OnSeekBarChangeListener,
		OnSeekCompleteListener, OnCompletionListener, OnPreparedListener,
		OnAudioFocusChangeListener {
	public static final String ACTION_PLAY = "duan.son.action.PLAY";
	public static final String ACTION_PAUSE = "duan.son.action.PAUSE";
	public static final String ACTION_NEXT = "duan.son.action.NEXT";
	public static final String ACTION_PREVIOUS = "duan.son.action.PREVIOUS";
	public static final String ACTION_TOGGLE_PLAYBACK = "duan.son.action.TOGGLE_PLAYBACK";
	// public static final String ACTION_STOP = "action_stop";
	//
	private MediaPlayer mMediaPlayer = null;
	// private MediaController mController;
	// Xử lý việc cập nhật giao diện (thời gian và progress bar ...)
	private Handler mHandler = new Handler();
	private Utilities utils;
	private int seekForwardTime = 5000; // 5 giây
	private int seekBackwardTime = 5000; // 5 giây
	public static int currentSongIndex = -1;
	public static int songindexForPause = 0;
	ArrayList<Song> mArray = new ArrayList<Song>();
	//
	private WeakReference<SeekBar> songProgressBar;
	private WeakReference<TextView> songCurrentDurationTime;
	private WeakReference<TextView> songTotalDurationTime;
	//
	NotificationManager mNotificationManager;
	Notification mNotification = null;
	int NOTIFICATION_ID = 1;
	// TODO
	ComponentName mMediaButtonReceiverComponent;
	AudioManager mAudioManager;
	RemoteControlClientCompat mRemoteControlClientCompat;
	WifiLock mWifiLock;
	Bitmap mDummyAlbumArt;
	WifiManager wifiManager;
	Bitmap mybitmap;

	// AudioFocusHelper mAudioFocusHelper = null;
	//
	// enum AudioFocus {
	// NoFocusNoDuck, // we don't have audio focus, and can't duck
	// NoFocusCanDuck, // we don't have focus, but can play at a low volume
	// // ("ducking")
	// Focused // we have full audio focus
	// }
	//
	// AudioFocus mAudioFocus = AudioFocus.NoFocusNoDuck;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			String action = intent.getAction();
			initUI();

			mArray = PlayActivity.mArraySong;
			int index = PlayActivity.index;
			Log.d("index", "" + index);
			try {
				index = (Integer) intent.getExtras().get("index");
				Log.d("index", "" + index);
			} catch (Exception e) {

			}

			if (action.equals(ACTION_PAUSE)) {
				pauseMusic(index);
			}
			if (action.equals(ACTION_TOGGLE_PLAYBACK)) {
				toggle(index);
			}

			if (action.equals(ACTION_PLAY)) {
				mMediaPlayer.reset();
				setUpAsForeground(mArray.get(index).getTitle());
				playMusic(mArray.get(index).getLinkPlay320());
				lockscreen(index);
				updateProgressBar();
				PlayActivity.index = index;

			} else if (action.equals(ACTION_NEXT)) {
				mMediaPlayer.reset();
				if (index >= mArray.size() - 1)
					index = -1;
				playNextSong(mArray.get(index + 1).getLinkPlay320(), index + 1);
			} else if (action.equals(ACTION_PREVIOUS)) {
				previous(index);

			}
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "onStartCommand 1", 0)
					.show();
		}

		return START_NOT_STICKY;// không cho service khởi động lại khi bị kill
								// bất thường
	}

	@Override
	public void onCreate() {
		super.onCreate();
		initUI();
		utils = new Utilities();
		mArray = PlayActivity.mArraySong;
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.reset();
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mMediaPlayer.setOnSeekCompleteListener(this);
		mMediaPlayer.setOnCompletionListener(this);
		mMediaPlayer.setOnPreparedListener(this);
		mMediaButtonReceiverComponent = new ComponentName(this,
				MusicIntentReceiver.class);
		mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		mWifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
				.createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
		mWifiLock.acquire();
		mDummyAlbumArt = BitmapFactory.decodeResource(getResources(),
				R.drawable.zing);
	}

	public void previous(int index) {
		mMediaPlayer.reset();
		if (index == 0) {
			try {
				Log.d("index", "" + index);
				index = mArray.size();
				playMusic(mArray.get(index - 1).getLinkPlay320());
				updateProgressBar();
				setUpAsForeground(mArray.get(index - 1).getTitle());
				PlayActivity.index = index - 1;
				lockscreen(index);
			} catch (Exception e) {
				Log.d("index", "" + index);
			}
		} else {
			playMusic(mArray.get(index - 1).getLinkPlay320());
			updateProgressBar();
			PlayActivity.index = index - 1;
			setUpAsForeground(mArray.get(index).getTitle());
			lockscreen(index - 1);
		}
	}

	public void toggle(int index) {
		if (mMediaPlayer.isPlaying()) {
			mMediaPlayer.pause();
			stopForeground(true);
			PlayActivity.fragPlay.stopAnimation(PlayActivity.fragPlay.image);
			if (mRemoteControlClientCompat != null) {
				mRemoteControlClientCompat
						.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);

			}
		} else {
			mMediaPlayer.start();
			PlayActivity.fragPlay.startAnimation(PlayActivity.fragPlay.image);
			setUpAsForeground(mArray.get(index).getTitle());
			if (mRemoteControlClientCompat != null) {
				mRemoteControlClientCompat
						.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);

			}
		}
	}

	public void pauseMusic(int index) {
		if (mMediaPlayer.isPlaying()) {
			mMediaPlayer.pause();
			PlayActivity.fragPlay.stopAnimation(PlayActivity.fragPlay.image);

			if (mRemoteControlClientCompat != null) {
				mRemoteControlClientCompat
						.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
			}
			stopForeground(true);
		} else {
			mMediaPlayer.start();
			PlayActivity.fragPlay.startAnimation(PlayActivity.fragPlay.image);
			setUpAsForeground(mArray.get(index).getTitle());

			if (mRemoteControlClientCompat != null) {
				mRemoteControlClientCompat
						.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);

			}
		}
	}

	// @SuppressWarnings("deprecation")
	public void playMusic(String data) {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		// For 3G check
		boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.isConnectedOrConnecting();
		// For WiFi Check
		boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnectedOrConnecting();
		wifiManager = (WifiManager) getApplicationContext().getSystemService(
				Context.WIFI_SERVICE);
		if (!is3g && !isWifi) {
			Toast.makeText(getApplicationContext(),
					"Kiểm tra lại wifi hoặc 3G ", 0).show();
		} else {
			try {
				mMediaPlayer.setDataSource(data);
				mMediaPlayer.prepare();
				mMediaPlayer.setWakeMode(getApplicationContext(),
						PowerManager.PARTIAL_WAKE_LOCK);// xử lý khi rơi vào
														// trạng // // thái ngủ
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getApplicationContext(),
						"Không thể kết nối tới máy chủ 1", 0).show();
			} catch (IOException e) {
				e.printStackTrace();
				Log.d("Wifi", "Mất wifi");
			}
			mMediaPlayer.start();

		}
	}

	public void playNextSong(String data, int index) {
		try {
			if (index > mArray.size() - 1) {
				index = 0;
			}
			playMusic(data);
			PlayActivity.fragPlay.setSong(mArray.get(index));
			updateProgressBar();
			setUpAsForeground(mArray.get(index).getTitle());
			PlayActivity.index = index;
			lockscreen(index);
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Play next song ", 0)
					.show();
		}

	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		try {
			mMediaPlayer.reset();
			int index = PlayActivity.index + 1;
			playNextSong(mArray.get(index).getLinkPlay320(), index);
			// if (index > mArray.size() - 1)
			// index = 0;
			// playMusic(mArray.get(index).getLinkPlay320());
			// updateProgressBar();
			// PlayActivity.fragPlay.setSong(mArray.get(index));
			// lockscreen(index);
			// PlayActivity.index = index;
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Completion 1", 0).show();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void initUI() {
		songCurrentDurationTime = new WeakReference<TextView>(
				PlayActivity.tvRunTime);
		songTotalDurationTime = new WeakReference<TextView>(
				PlayActivity.tvTotalTime);
		songProgressBar = new WeakReference<SeekBar>(PlayActivity.mSeekBar);
		songProgressBar.get().setOnSeekBarChangeListener(this);
	}

	public void updateProgressBar() {
		mHandler.postDelayed(mUpdateTimeTask, 1000);
	}

	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			long totalDuration = 0;
			try {
				totalDuration = mMediaPlayer.getDuration();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
			long currentDuration = 0;
			try {
				currentDuration = mMediaPlayer.getCurrentPosition();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
			// Hiển thị tổng thời gian
			songTotalDurationTime.get().setText(
					"" + utils.milliSecondsToTimer(totalDuration));
			// Hiển thị thời gian hoàn thành
			songCurrentDurationTime.get().setText(
					"" + utils.milliSecondsToTimer(currentDuration));

			// Thực hiện việc cập nhật progress bar
			int progress = (int) (utils.getProgressPercentage(currentDuration,
					totalDuration));
			// Log.d("Progress", ""+progress);
			songProgressBar.get().setProgress(progress);
			// Chạy lại sau 0,1s
			mHandler.postDelayed(this, 1000);
		}
	};

	@Override
	public void onSeekComplete(MediaPlayer mp) {
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

		mHandler.removeCallbacks(mUpdateTimeTask);

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = mMediaPlayer.getDuration();
		int currentPosition = utils.progressToTimer(seekBar.getProgress(),
				totalDuration);
		// Thực hiện về trước sau theo số giây
		mMediaPlayer.seekTo(currentPosition);
		// Cập nhật lại thời gian ProgressBar
		updateProgressBar();

	}

	//
	// void updateNotification(String text) {
	// PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),
	// 0,
	// new Intent(getApplicationContext(), ListChartActivity.class),
	// PendingIntent.FLAG_UPDATE_CURRENT);
	// mNotification.setLatestEventInfo(getApplicationContext(),
	// "Bạn Đang Nghe Bài Hát", text, pi);
	// mNotificationManager.notify(NOTIFICATION_ID, mNotification);
	// }

	void setUpAsForeground(String text) {
		PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),
				0, new Intent(getApplicationContext(), MainActivity.class),
				PendingIntent.FLAG_UPDATE_CURRENT);
		mNotification = new Notification();
		mNotification.tickerText = text;
		mNotification.icon = R.drawable.music;
		mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
		mNotification.setLatestEventInfo(getApplicationContext(),
				"Bạn Đang Nghe Bài Hát", text, pi);
		startForeground(NOTIFICATION_ID, mNotification);
	}

	public class getBitmapBackground extends AsyncTask<Void, Void, Void> {
		Bitmap bitmap;
		String url;
		int index;

		public getBitmapBackground(String url, Bitmap myBitmap, int index) {
			this.url = url;
			this.bitmap = myBitmap;
			this.index = index;
		}

		@Override
		protected Void doInBackground(Void... params) {
			bitmap = getBitmapFromURL(url);
			Log.d("bitmap", "" + bitmap);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (bitmap == null) {
				bitmap = mDummyAlbumArt;
			}
			mRemoteControlClientCompat
					.editMetadata(true)
					.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST,
							mArray.get(index).getArtist())
					.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM,
							mArray.get(index).getArtist())
					.putString(MediaMetadataRetriever.METADATA_KEY_TITLE,
							mArray.get(index).getTitle())
					.putBitmap(
							RemoteControlClientCompat.MetadataEditorCompat.METADATA_KEY_ARTWORK,
							bitmap).apply();
		}
	}

	public void lockscreen(int index) {
		MediaButtonHelper.registerMediaButtonEventReceiverCompat(mAudioManager,
				mMediaButtonReceiverComponent);
		// Use the remote control APIs (if available) to set the playback
		// state
		if (mRemoteControlClientCompat == null) {
			Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
			intent.setComponent(mMediaButtonReceiverComponent);
			mRemoteControlClientCompat = new RemoteControlClientCompat(
					PendingIntent.getBroadcast(this /* context */, 0 /*
																	 * requestCode
																	 * , ignored
																	 */,
							intent /* intent */, 0 /* flags */));
			RemoteControlHelper.registerRemoteControlClient(mAudioManager,
					mRemoteControlClientCompat);
		}
		mRemoteControlClientCompat
				.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
		mRemoteControlClientCompat
				.setTransportControlFlags(RemoteControlClient.FLAG_KEY_MEDIA_PAUSE

						| RemoteControlClient.FLAG_KEY_MEDIA_NEXT
						| RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS
						| RemoteControlClient.FLAG_KEY_MEDIA_PLAY);
		getBitmapBackground aa = new getBitmapBackground(mArray.get(index)
				.getAvatarArtist(), mybitmap, index);
		aa.execute();
		// update remote controls
		mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
				AudioManager.AUDIOFOCUS_GAIN);
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

	@Override
	public void onPrepared(MediaPlayer mp) {

	}

	@Override
	public void onAudioFocusChange(int focusChange) {
		switch (focusChange) {
		case AudioManager.AUDIOFOCUS_GAIN:
			// resume playback
			if (!mMediaPlayer.isPlaying())
				mMediaPlayer.start();
			// mMediaPlayer.setVolume(1.0f, 1.0f);
			break;

		case AudioManager.AUDIOFOCUS_LOSS:
			// Lost focus for an unbounded amount of time: stop playback and
			// release media player
			if (mMediaPlayer.isPlaying())
				mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
			break;

		case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
			// Lost focus for a short time, but we have to stop
			// playback. We don't release the media player because playback
			// is likely to resume
			if (mMediaPlayer.isPlaying())
				mMediaPlayer.pause();
			break;

		case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
			// Lost focus for a short time, but it's ok to keep playing
			// at an attenuated level
			if (mMediaPlayer.isPlaying())
				mMediaPlayer.setVolume(0.1f, 0.1f);
			break;
		}

	}

}
