package com.snnc_993.mymusic.model;

import static com.snnc_993.mymusic.config.Config.IMG_DIR_THEME;

public class TopicModel extends CardModel{

    @Override
    public String getImg() {
        return IMG_DIR_THEME + super.getImg();
    }

}
