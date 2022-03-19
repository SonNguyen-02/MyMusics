package com.snnc_993.mymusic.model;

import static com.snnc_993.mymusic.config.Config.IMG_DIR_ALBUM;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AlbumModel extends CardModel{

    @SerializedName("singer_name")
    @Expose
    private String singerName;

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    @Override
    public String getImg() {
        return IMG_DIR_ALBUM + super.getImg();
    }

}
