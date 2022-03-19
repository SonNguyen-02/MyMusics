package com.snnc_993.mymusic.service;

import com.snnc_993.mymusic.model.AdsModel;
import com.snnc_993.mymusic.model.AlbumModel;
import com.snnc_993.mymusic.model.CardModel;
import com.snnc_993.mymusic.model.CategoryModel;
import com.snnc_993.mymusic.model.PlaylistModel;
import com.snnc_993.mymusic.model.SongModel;
import com.snnc_993.mymusic.model.TopicModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface DataService {

    @GET("song_banner")
    Call<List<AdsModel>> getDataBanner();

    @GET("rand_category_for_current_day")
    Call<List<CategoryModel>> getRandCategory();

    @GET("rand_album_for_current_day")
    Call<List<AlbumModel>> getRandAlbum();

    @GET("rand_playlist_for_current_day")
    Call<List<PlaylistModel>> getRandPlaylist();

    @GET("newly_released_music")
    Call<List<SongModel>> getNewlyReleasedMusic();

    @GET("all_playlist")
    Call<List<PlaylistModel>> getAllPlayList();

    @GET("all_album")
    Call<List<AlbumModel>> getAllAlbum();

    @GET("all_topic")
    Call<List<TopicModel>> getAllTopic();

    @FormUrlEncoded
    @POST("all_category_in_topic")
    Call<List<CategoryModel>> getAllCategoryInTopic(@Field("id") int topicId);

    @FormUrlEncoded
    @POST("all_song_from")
    Call<List<SongModel>> getAllSongFrom(@Field("type") String type, @Field("id") int id);

    @FormUrlEncoded
    @POST("get_suggest_song")
    Call<List<SongModel>> getSuggestSong(@Field("song_json") String songJson, @Field("list_current_song_id") String listCurrentSongId);

}
