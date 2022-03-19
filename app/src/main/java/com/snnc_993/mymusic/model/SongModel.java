package com.snnc_993.mymusic.model;

import static com.snnc_993.mymusic.config.Config.AUDIO_URL;
import static com.snnc_993.mymusic.config.Config.IMG_DIR_SONG;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SongModel extends CardModel {

    @SerializedName("album_id")
    @Expose
    private String albumId;
    @SerializedName("category_id")
    @Expose
    private String categoryId;
    @SerializedName("playlist_id")
    @Expose
    private String playlistId;
    @SerializedName("singer_name")
    @Expose
    private String singerName;
    @SerializedName("audio")
    @Expose
    private String audio;
    @SerializedName("likes")
    @Expose
    private String likes;
    @SerializedName("status")
    @Expose
    private String status;

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getAudio() {
        return AUDIO_URL + audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    @Override
    public String getImg() {
        return IMG_DIR_SONG + super.getImg();
    }
}
