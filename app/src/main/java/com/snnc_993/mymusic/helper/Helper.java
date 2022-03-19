package com.snnc_993.mymusic.helper;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.snnc_993.mymusic.model.SongModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Helper {

    public static SongModel convertSongFromJson(String strJson){
        Gson gson = new Gson();
        return gson.fromJson(strJson, SongModel.class);
    }

    @NonNull
    public static List<SongModel> convertListSongFromJson(String strJson) {
        List<SongModel> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(strJson);
            JSONObject jsonObject;
            SongModel song;
            Gson gson = new Gson();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                song = gson.fromJson(jsonObject.toString(), SongModel.class);
                list.add(song);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

}
