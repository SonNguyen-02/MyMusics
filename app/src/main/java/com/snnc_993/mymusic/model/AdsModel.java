package com.snnc_993.mymusic.model;

import static com.snnc_993.mymusic.config.Config.IMG_DIR_ADS;
import static com.snnc_993.mymusic.config.Config.IMG_DIR_SONG;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdsModel {

    @SerializedName("adsId")
    @Expose
    private String adsId;
    @SerializedName("adsContent")
    @Expose
    private String adsContent;
    @SerializedName("adsImg")
    @Expose
    private String adsImg;
    @SerializedName("songId")
    @Expose
    private String songId;
    @SerializedName("songName")
    @Expose
    private String songName;
    @SerializedName("songImg")
    @Expose
    private String songImg;

    public String getAdsId() {
        return adsId;
    }

    public void setAdsId(String adsId) {
        this.adsId = adsId;
    }

    public String getAdsContent() {
        return adsContent;
    }

    public void setAdsContent(String adsContent) {
        this.adsContent = adsContent;
    }

    public String getAdsImg() {
        return IMG_DIR_ADS + adsImg;
    }

    public void setAdsImg(String adsImg) {
        this.adsImg = adsImg;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongImg() {
        return IMG_DIR_SONG + songImg;
    }

    public void setSongImg(String songImg) {
        this.songImg = songImg;
    }

    @NonNull
    @Override
    public String toString() {
        return "AdsModel{" +
                "adsId='" + adsId + '\'' +
                ", adsContent='" + adsContent + '\'' +
                ", adsImg='" + adsImg + '\'' +
                ", songId='" + songId + '\'' +
                ", songName='" + songName + '\'' +
                ", songImg='" + songImg + '\'' +
                '}';
    }
}