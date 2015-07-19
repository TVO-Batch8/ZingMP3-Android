package com.duan.database;

import java.util.ArrayList;

import com.duan.Model.Song;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper {

	private static final String DB_NAME = "MyList.db";
	private static final int DB_VERSION = 1;
	private static final String TABLE_NAME = "MYLIST";
	private static final String ID = "_id";
	private static final String COLUMN_TITLE = "Title";
	private static final String COLUMN_ARTIST = "Artist";
	private static final String COLUMN_LINKPLAY320 = "LinkPlay";
	private static final String COLUMN_AVATA_ARTIST = "Avata_Artist";
	private static final String COLUMN_ID_Song = "ID";

	public Database(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		Log.d("Database", "Create database");
	}

	public Database(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_NAME + " ( " + ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TITLE
				+ " TEXT, " + COLUMN_ARTIST + " TEXT, " + COLUMN_LINKPLAY320
				+ " TEXT, " + COLUMN_AVATA_ARTIST + " TEXT, " + COLUMN_ID_Song
				+ " TEXT );");
		Log.d("Database", "Create database 1");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public void insertItem(String title, String artist, String linkPlay,
			String avatar, String id) {
		SQLiteDatabase db = this.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_TITLE, title);
		values.put(COLUMN_ARTIST, artist);
		values.put(COLUMN_LINKPLAY320, linkPlay);
		values.put(COLUMN_AVATA_ARTIST, avatar);
		values.put(COLUMN_ID_Song, id);
		db.insert(TABLE_NAME, null, values);
		Log.d("Insert", "Done");
	}

	public void deleteSong(String id) {
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(TABLE_NAME, COLUMN_ID_Song + " = " + "'" + id + "'", null);
	}

	public boolean checkInsert(String title) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db
				.query(TABLE_NAME, null, null, null, null, null, null);
		// if (cursor.getCount() == 0) {
		// return false;
		// }
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				String NameChar;
				NameChar = cursor
						.getString(cursor.getColumnIndex(COLUMN_TITLE))
						.toString();
				if (NameChar.equals(title)) {
					return true;
				}
			} while (cursor.moveToNext());
		}
		return false;
	}

	public ArrayList<Song> listIdInfo1() {
		ArrayList<Song> ds_id = new ArrayList<Song>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db
				.query(TABLE_NAME, null, null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			do {
				Song song = new Song();
				song.setTitle(cursor.getString(cursor
						.getColumnIndex(COLUMN_TITLE)));
				song.setArtist(cursor.getString(cursor
						.getColumnIndex(COLUMN_ARTIST)));
				song.setLinkPlay320(cursor.getString(cursor
						.getColumnIndex(COLUMN_LINKPLAY320)));
				song.setAvatarArtist(cursor.getString(cursor
						.getColumnIndex(COLUMN_AVATA_ARTIST)));
				song.setId(cursor.getString(cursor
						.getColumnIndex(COLUMN_ID_Song)));

				ds_id.add(song);
			} while (cursor.moveToNext());
		}
		return ds_id;
	}

}
