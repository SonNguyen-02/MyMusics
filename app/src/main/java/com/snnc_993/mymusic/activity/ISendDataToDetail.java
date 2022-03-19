package com.snnc_993.mymusic.activity;

import com.snnc_993.mymusic.model.CardModel;
import com.snnc_993.mymusic.model.SongModel;

import java.util.List;

public interface ISendDataToDetail {

    enum Action{
        SHOW_MODAL, PLAY
    }

    void sendDataListener(CardModel card, Enum<Action> action);

    void sendDataListener(List<SongModel> mListSong, int index);

}
