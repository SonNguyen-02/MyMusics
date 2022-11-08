package com.snnc_993.mymusic.fragment;

import static com.snnc_993.mymusic.config.Constant.KEY_CURRENT_SONG;
import static com.snnc_993.mymusic.config.Constant.KEY_IS_LOAD_SUCCESS;
import static com.snnc_993.mymusic.config.Constant.SEND_STATUS_AUDIO;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.snnc_993.mymusic.R;
import com.snnc_993.mymusic.activity.PlayMusicActivity;
import com.snnc_993.mymusic.config.Constant;
import com.snnc_993.mymusic.model.SongModel;
import com.snnc_993.mymusic.service.MusicService;

public class MainSongFragment extends Fragment {

    private View view;
    private ImageView imgSong, imgLikes;
    private LinearLayout llPlaylist;
    private SongModel mSong;
    private boolean isPlaying;

    BroadcastReceiver receiverFromMusicService = new BroadcastReceiver() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                SongModel song = (SongModel) bundle.getSerializable(KEY_CURRENT_SONG);
                if (song != null && song != mSong && MusicService.getMedia() != null) {
                    mSong = song;
                    Glide.with(context).load(mSong.getImg()).into(imgSong);
                }
            }
        }
    };

    BroadcastReceiver receiverStatusAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                boolean isLoadSuccess = bundle.getBoolean(KEY_IS_LOAD_SUCCESS);
                if (isLoadSuccess) {
                    startAnim();
                } else {
                    stopAnim();
                }
            }
        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mSong = ((PlayMusicActivity) requireActivity()).getSong();
        isPlaying = ((PlayMusicActivity) requireActivity()).isPlaying();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_song, container, false);
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiverFromMusicService, new IntentFilter(Constant.SEND_TO_ACTIVITY));
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiverStatusAudio, new IntentFilter(SEND_STATUS_AUDIO));
        initUi();

        llPlaylist.setOnClickListener(view -> ((PlayMusicActivity) requireActivity()).showPlaylistFragment());

        if (mSong != null) {
            Glide.with(this).load(mSong.getImg()).into(imgSong);
            if (isPlaying) {
                startAnim();
            }
        }
        return view;
    }

    private void startAnim() {
        stopAnim();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                imgSong.animate().rotationBy(360).withEndAction(this).setDuration(10000).setInterpolator(new LinearInterpolator()).start();
            }
        };
        imgSong.animate().rotationBy(360).withEndAction(runnable).setDuration(10000).setInterpolator(new LinearInterpolator()).start();
    }

    private void stopAnim() {
        imgSong.animate().cancel();
    }

    private void initUi() {
        imgSong = view.findViewById(R.id.img_song);
        imgLikes = view.findViewById(R.id.img_likes);
        llPlaylist = view.findViewById(R.id.ll_playlist);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiverFromMusicService);
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiverStatusAudio);
    }
}
