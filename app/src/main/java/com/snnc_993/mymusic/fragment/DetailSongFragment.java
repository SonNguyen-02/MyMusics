package com.snnc_993.mymusic.fragment;

import static com.snnc_993.mymusic.config.Constant.KEY_CURRENT_SONG;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.snnc_993.mymusic.R;
import com.snnc_993.mymusic.activity.PlayMusicActivity;
import com.snnc_993.mymusic.adapter.SongAdapter;
import com.snnc_993.mymusic.config.Constant;
import com.snnc_993.mymusic.model.SongModel;
import com.snnc_993.mymusic.service.APIService;
import com.snnc_993.mymusic.service.DataService;
import com.snnc_993.mymusic.service.MusicService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailSongFragment extends Fragment {

    private RecyclerView rcvListSuggestSong;
    private SongModel mSong;

    BroadcastReceiver receiverFromMusicService = new BroadcastReceiver() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                SongModel song = (SongModel) bundle.getSerializable(KEY_CURRENT_SONG);
                if (song != null && song != mSong && MusicService.getMedia() != null) {
                    mSong = song;
                    initData(mSong);
                }
            }
        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mSong = ((PlayMusicActivity) requireActivity()).getSong();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suggest_song, container, false);
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiverFromMusicService, new IntentFilter(Constant.SEND_TO_ACTIVITY));
        rcvListSuggestSong = view.findViewById(R.id.rcv_list_suggest_song);

        initData(mSong);

        return view;
    }

    public void initData(SongModel song) {
        Gson gson = new Gson();
        String songJson = gson.toJson(song);
        DataService dataService = APIService.getService();
        Call<List<SongModel>> callback = dataService.getSuggestSong(songJson, MusicService.getListSongId());
        callback.enqueue(new Callback<List<SongModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<SongModel>> call, @NonNull Response<List<SongModel>> response) {
                if(response.body() != null){
                    SongAdapter adapter = new SongAdapter(getContext(), new ArrayList<>(response.body()), SongAdapter.TYPE_SUGGEST);
                    rcvListSuggestSong.setAdapter(adapter);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                    rcvListSuggestSong.setLayoutManager(layoutManager);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SongModel>> call, @NonNull Throwable t) {
                Log.e("ddd", "onFailure: ", t);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiverFromMusicService);
    }
}
