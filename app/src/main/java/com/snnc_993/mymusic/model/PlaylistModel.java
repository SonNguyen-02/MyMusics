package com.snnc_993.mymusic.model;

import static com.snnc_993.mymusic.config.Config.IMG_DIR_PLAYLIST;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaylistModel extends CardModel{

    @SerializedName("img_background")
    @Expose
    private String imgBackground;

    public String getImgBackground() {
        return IMG_DIR_PLAYLIST + imgBackground;
    }

    public void setImgBackground(String imgBackground) {
        this.imgBackground = imgBackground;
    }

    @Override
    public String getImg() {
        return IMG_DIR_PLAYLIST + super.getImg();
    }

}
