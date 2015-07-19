package com.duan.Model;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Serializable {
	String title, artist, linkPlay320, avatarArtist, id,avatarArtistB;

//	public String getAvatarArtistB() {
//		return avatarArtistB;
//	}
//
//	public void setAvatarArtistB(String avatarArtistB) {
//		this.avatarArtistB = avatarArtistB;
//	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getLinkPlay320() {
		return linkPlay320;
	}

	public void setLinkPlay320(String linkPlay320) {
		this.linkPlay320 = linkPlay320;
	}

	public String getAvatarArtist() {
		return avatarArtist;
	}

	public void setAvatarArtist(String avatarArtist) {
		this.avatarArtist = avatarArtist;
	}
}
