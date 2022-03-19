package com.snnc_993.mymusic.model;


import static com.snnc_993.mymusic.config.Config.IMG_DIR_CATEGORY;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryModel extends CardModel{

    @SerializedName("theme_id")
    @Expose
    private String themeId;

    public String getThemeId() {
        return themeId;
    }

    public void setThemeId(String themeId) {
        this.themeId = themeId;
    }

    @Override
    public String getImg() {
        return IMG_DIR_CATEGORY + super.getImg();
    }

}
