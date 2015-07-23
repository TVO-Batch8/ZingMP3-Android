package com.duan.nghenhac;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONArray;
import org.json.JSONObject;

import com.duan.Model.Song;
import com.duan.adapter.MyAdapter;
import com.google.gson.Gson;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends Activity {
	public static String URL_STRING = "http://api.mp3.zing.vn/api/chart-detail?jsondata=";
	public static String ZING_MP3_API_PRIVATE_KEY = "c9c2a7f66b677012b763512da77040b3";
	public static String ZING_MP3_API_PUBLIC_KEY = "4c3d549977f7943bd9cc6d33f656bb5c1c87d2c0";

	String x;
	// ArrayList<HashMap<String, String>> menuitems = new
	// ArrayList<HashMap<String, String>>();
	TextView tv;
	ListView lv;
	ArrayList<Song> myArray = new ArrayList<Song>();
	String idBXH;
	ProgressDialog myProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		lv = (ListView) findViewById(R.id.listView1);
		idBXH = getIntent().getExtras().getString("id");
		x = bxhChiTiet(idBXH);
		Tash myTash = new Tash();
		myTash.execute();
	}

	public class Tash extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			myProgress = ProgressDialog.show(DetailActivity.this,
					"Đang truy vấn", "Đang Load dữ liệu từ sever...");
		}

		@Override
		protected Void doInBackground(Void... params) {
			URL jsonURL;
			URLConnection jc;
			try {
				InputStream is;
				String stringurl = x;
				Log.d("json", stringurl);
				jsonURL = new URL(stringurl);
				jc = jsonURL.openConnection();
				is = jc.getInputStream();

				// doc du lieu
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "utf-8"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
					Log.d("line", sb.toString());
				}
				is.close();
				String jsonTxt = sb.toString(); // doc StringBuilder vao chuoi
				Log.d("Chitiet", jsonTxt);
				JSONArray items = new JSONArray(jsonTxt);
				for (int i = 0; i < items.length(); i++) {
					// try {
					Song song = new Song();

					JSONObject item = items.getJSONObject(i);
					String title = item.getString("Title");
					String artist = item.getString("Artist");
					String linkPlay320 = item.getString("LinkPlay320");
					String id = item.getString("ID");
					JSONArray artistDetail = item.getJSONArray("ArtistDetail");
					JSONObject avatar = artistDetail.getJSONObject(0);
					String avatarArtist = avatar.getString("ArtistAvatar");
					String avatarArtistB = avatarArtist.replace("94_94",
							"165_165");
					song.setArtist(artist);
					song.setAvatarArtist(avatarArtistB);
					song.setLinkPlay320(linkPlay320);
					song.setTitle(title);
					song.setId(id);
					Log.d("AvatarArtistB", "" + avatarArtistB);
					myArray.add(song);
				}
				// } catch (Exception e) {
				//
				// }
				// }// end for
				// Log.d("soluong", menuitems.size() + "");
			} catch (Exception e) {
				e.printStackTrace();
				Log.d("loi", e.toString());
				publishProgress();

			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
			Toast.makeText(DetailActivity.this, "Kết nối sever thất bại ",
					Toast.LENGTH_SHORT).show();
			myProgress.dismiss();
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			myProgress.dismiss();
			MyAdapter adapter = new MyAdapter(getApplicationContext(),
					R.layout.item_list, myArray);
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent intent = new Intent(getApplicationContext(),
							PlayActivity.class);
					intent.putExtra("Array", myArray);
					intent.putExtra("index", position);
					startActivity(intent);
					overridePendingTransition(R.anim.in, R.anim.out);

				}
			});

		}
	}

	public static String bxhChiTiet(String idbxh) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", idbxh);
		// Việt Nam IWZ9Z08I,video IWZ9Z08W
		// Âu mỹ :IWZ9Z0BW,video IWZ9Z0BU
		// Hàn Quốc :IWZ9Z0BO,video IWZ9Z0BZ
		Gson gson = new Gson();
		String json = gson.toJson(map);
		Log.d("json", json);
		String signature = "";
		String data = "";
		try {
			String s1 = new String(Base64.encode(json.getBytes(),
					Base64.DEFAULT));
			Log.d("s1", s1);
			data = URLEncoder.encode(s1, "UTF-8");
			Log.d("data", data);
			signature = computeSignature(data, ZING_MP3_API_PRIVATE_KEY);
			Log.d("signature", signature);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		// // } catch (Exception e) {
		// // e.printStackTrace();
		// // }
		return URL_STRING + data + "&publicKey=" + ZING_MP3_API_PUBLIC_KEY
				+ "&signature=" + signature;
	}

	public static String computeSignature(String baseString, String keyString)
			throws GeneralSecurityException, UnsupportedEncodingException {
		SecretKeySpec keySpec = new SecretKeySpec(keyString.getBytes(),
				"HmacMD5");
		Mac mac = Mac.getInstance("HmacMD5");
		mac.init(keySpec);
		byte[] rawHmac = mac.doFinal(baseString.getBytes());
		return new String(Hex.encodeHex(rawHmac));
	}

}
