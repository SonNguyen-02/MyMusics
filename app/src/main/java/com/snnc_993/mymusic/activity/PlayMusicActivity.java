package com.snnc_993.mymusic.activity;


import static com.snnc_993.mymusic.config.Constant.ADD_SONG;
import static com.snnc_993.mymusic.config.Constant.KEY_ACTION_MUSIC;
import static com.snnc_993.mymusic.config.Constant.KEY_CURRENT_SONG;
import static com.snnc_993.mymusic.config.Constant.KEY_DATA_TO_PLAY_MUSIC;
import static com.snnc_993.mymusic.config.Constant.KEY_IS_LOAD_SUCCESS;
import static com.snnc_993.mymusic.config.Constant.KEY_IS_PLAYING;
import static com.snnc_993.mymusic.config.Constant.KEY_LAST_MODE_PLAY;
import static com.snnc_993.mymusic.config.Constant.KEY_SONG_OBJ;
import static com.snnc_993.mymusic.config.Constant.KEY_TIME_SEEK_SONG;
import static com.snnc_993.mymusic.config.Constant.NEXT_SONG;
import static com.snnc_993.mymusic.config.Constant.NORMAL_MODE;
import static com.snnc_993.mymusic.config.Constant.PAUSE_SONG;
import static com.snnc_993.mymusic.config.Constant.PLAY_NEW_SONG;
import static com.snnc_993.mymusic.config.Constant.PLAY_SONG;
import static com.snnc_993.mymusic.config.Constant.PREV_SONG;
import static com.snnc_993.mymusic.config.Constant.REPEAT_MODE;
import static com.snnc_993.mymusic.config.Constant.REPEAT_ONE_MODE;
import static com.snnc_993.mymusic.config.Constant.REPEAT_SONG;
import static com.snnc_993.mymusic.config.Constant.SEEK_SONG;
import static com.snnc_993.mymusic.config.Constant.SEND_STATUS_AUDIO;
import static com.snnc_993.mymusic.config.Constant.SEND_STATUS_LOAD_AUDIO;
import static com.snnc_993.mymusic.config.Constant.SEND_TO_ACTIVITY;
import static com.snnc_993.mymusic.config.Constant.SHARED_PRE_NAME;
import static com.snnc_993.mymusic.config.Constant.SHUFFLE_MODE;
import static com.snnc_993.mymusic.config.Constant.SHUFFLE_SONG;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.snnc_993.mymusic.R;
import com.snnc_993.mymusic.adapter.PlayMusicViewPagerAdapter;
import com.snnc_993.mymusic.adapter.SongAdapter;
import com.snnc_993.mymusic.adapter.SongSpecialAdapter;
import com.snnc_993.mymusic.fragment.PlaylistSongFragment;
import com.snnc_993.mymusic.model.SongModel;
import com.snnc_993.mymusic.service.MusicService;
import com.snnc_993.mymusic.transformer.ZoomOutPageTransformer;
import com.snnc_993.mymusic.utils.SimpleRequest;
import com.snnc_993.mymusic.view.TextThumbSeekBar;

import me.relex.circleindicator.CircleIndicator3;

public class PlayMusicActivity extends AppCompatActivity implements SongAdapter.IOnClickSongItem, SongAdapter.IOnClickAddSong, SongSpecialAdapter.IOnClickSongItem {

    private SongModel mSong;
    private boolean isPlaying;
    private boolean isOpenPlaylist;
    private RelativeLayout rlDefaultFrame;
    private ImageView imgExpandActivity, imgMenu;
    private TextView tvSongName, tvSingerName;
    private ImageView imgBackground;
    private ViewPager2 mViewPager2;
    private CircleIndicator3 mCircleIndicator3;
    private TextThumbSeekBar mSeekBar;
    private ImageView imgShuffle, imgRepeat, imgNext, imgPrev, imgPlayPause;


    BroadcastReceiver receiverFromMusicService = new BroadcastReceiver() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                isPlaying = bundle.getBoolean(KEY_IS_PLAYING);
                if (isPlaying) {
                    imgPlayPause.setImageResource(R.drawable.ic_pause_big);
                } else {
                    imgPlayPause.setImageResource(R.drawable.ic_play_big);
                }
                SongModel song = (SongModel) bundle.getSerializable(KEY_CURRENT_SONG);
                if (song != null && MusicService.getMedia() != null) {

                    if (mSong != song) {
                        mSong = song;
                        Glide.with(getApplicationContext())
                                .asBitmap()
                                .load(mSong.getImg())
                                .placeholder(R.drawable.custom_overlay_black)
                                .addListener(new SimpleRequest(imgBackground))
                                .submit();
                        tvSongName.setText(mSong.getName());
                        tvSingerName.setText(mSong.getSingerName());
                    }
                    initSeekbar();
                    initMode();
                }
            }
        }
    };

    BroadcastReceiver receiverWhenLoadAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                sendStatusInitSong(bundle);
                boolean isLoadSuccess = bundle.getBoolean(KEY_IS_LOAD_SUCCESS);
                if (isLoadSuccess) {
                    imgPlayPause.setImageResource(R.drawable.ic_pause_big);
                } else {
                    imgPlayPause.setImageResource(R.drawable.ic_play_big);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            initUi();
            LocalBroadcastManager.getInstance(this).registerReceiver(receiverFromMusicService, new IntentFilter(SEND_TO_ACTIVITY));
            LocalBroadcastManager.getInstance(this).registerReceiver(receiverWhenLoadAudio, new IntentFilter(SEND_STATUS_LOAD_AUDIO));

            imgExpandActivity.setOnClickListener(v -> finish());

            mSong = (SongModel) bundle.getSerializable(KEY_DATA_TO_PLAY_MUSIC);

            if (MusicService.getMedia() != null && MusicService.getMedia().isPlaying()) {
                isPlaying = true;
                imgPlayPause.setImageResource(R.drawable.ic_pause_big);
            }
            imgShuffle.setOnClickListener(view -> startForegroundService(SHUFFLE_SONG));
            imgRepeat.setOnClickListener(view -> startForegroundService(REPEAT_SONG));
            imgNext.setOnClickListener(view -> startForegroundService(NEXT_SONG));
            imgPrev.setOnClickListener(view -> startForegroundService(PREV_SONG));
            imgPlayPause.setOnClickListener(view -> {
                if (isPlaying) {
                    startForegroundService(PAUSE_SONG);
                } else {
                    startForegroundService(PLAY_SONG);
                }
            });

            initData();
            initMode();
            initSeekbar();
        }
    }

    public void startForegroundService(int action) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_ACTION_MUSIC, action);
        startService(bundle);
    }

    private void startForegroundService(int action, SongModel mSong) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_ACTION_MUSIC, action);
        bundle.putSerializable(KEY_SONG_OBJ, mSong);
        startService(bundle);
    }

    private void seekSong(int time) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_ACTION_MUSIC, SEEK_SONG);
        bundle.putSerializable(KEY_TIME_SEEK_SONG, time);
        startService(bundle);
    }

    private void startService(Bundle bundle) {
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtras(bundle);
        startService(intent);
    }

    private void sendStatusInitSong(Bundle bundle) {
        Intent intent = new Intent(SEND_STATUS_AUDIO);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void initData() {
        if (mSong != null) {
            tvSongName.setText(mSong.getName());
            tvSingerName.setText(mSong.getSingerName());
            PlayMusicViewPagerAdapter adapter = new PlayMusicViewPagerAdapter(PlayMusicActivity.this);
            mViewPager2.setAdapter(adapter);
            mViewPager2.setPageTransformer(new ZoomOutPageTransformer());
            mViewPager2.setCurrentItem(1);
            mCircleIndicator3.setViewPager(mViewPager2);
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(mSong.getImg())
                    .placeholder(R.drawable.custom_overlay_black)
                    .addListener(new SimpleRequest(imgBackground))
                    .submit();
        }
    }

    private void initUi() {
        rlDefaultFrame = findViewById(R.id.rl_default_frame);
        imgExpandActivity = findViewById(R.id.img_expand_activity);
        imgMenu = findViewById(R.id.img_menu);
        tvSongName = findViewById(R.id.tv_song_name);
        tvSingerName = findViewById(R.id.tv_singer_name);
        imgBackground = findViewById(R.id.img_background);
        mViewPager2 = findViewById(R.id.view_pager2);
        mCircleIndicator3 = findViewById(R.id.circle_indicator_3);
        mSeekBar = findViewById(R.id.seek_bar);
        imgShuffle = findViewById(R.id.img_shuffle);
        imgRepeat = findViewById(R.id.img_repeat);
        imgNext = findViewById(R.id.img_next);
        imgPrev = findViewById(R.id.img_prev);
        imgPlayPause = findViewById(R.id.img_play_pause);
    }

    private void initMode() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PRE_NAME, MODE_PRIVATE);
        int mode = sharedPreferences.getInt(KEY_LAST_MODE_PLAY, NORMAL_MODE);
        switch (mode) {
            case NORMAL_MODE:
                imgShuffle.setImageResource(R.drawable.ic_shuffle_white);
                imgRepeat.setImageResource(R.drawable.ic_repeat_white);
                break;
            case SHUFFLE_MODE:
                imgShuffle.setImageResource(R.drawable.ic_shuffle_active);
                imgRepeat.setImageResource(R.drawable.ic_repeat_white);
                break;
            case REPEAT_MODE:
                imgShuffle.setImageResource(R.drawable.ic_shuffle_white);
                imgRepeat.setImageResource(R.drawable.ic_repeat_active);
                break;
            case REPEAT_ONE_MODE:
                imgShuffle.setImageResource(R.drawable.ic_shuffle_white);
                imgRepeat.setImageResource(R.drawable.ic_repeat_one_active);
                break;
        }
    }

    public SongModel getSong() {
        return mSong;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initSeekbar() {
        if (MusicService.getMedia() != null) {
            if (MusicService.getMedia().isPlaying()) {
                mSeekBar.setMax(MusicService.getMedia().getDuration());
            }
            mSeekBar.setOnTouchListener((view, motionEvent) -> {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    seekSong(mSeekBar.getProgress());
                }
                return false;
            });
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // update seek bar
                    if (MusicService.getMedia() != null) {
                        mSeekBar.setProgress(MusicService.getMedia().getCurrentPosition());
                        handler.postDelayed(this, 500);
                    } else {
                        imgPlayPause.setImageResource(R.drawable.ic_play_big);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(KEY_IS_LOAD_SUCCESS, false);
                        sendStatusInitSong(bundle);
                        mSeekBar.setProgress(0);
                    }
                }
            }, 100);

        }
    }

    public void showPlaylistFragment() {
        if (!isOpenPlaylist) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.anim_right_in, R.anim.anim_right_out, R.anim.anim_left_in, R.anim.anim_left_out);
            transaction.add(R.id.rl_main_frame, new PlaylistSongFragment());
            transaction.addToBackStack(PlaylistSongFragment.class.getName());
            transaction.commit();
            new Handler().postDelayed(() -> rlDefaultFrame.setVisibility(View.GONE), 150);
            isOpenPlaylist = true;
        }
    }

    public void hidePlaylistFragment() {
        if (isOpenPlaylist) {
            new Handler().postDelayed(() -> rlDefaultFrame.setVisibility(View.VISIBLE), 150);
            getSupportFragmentManager().popBackStack();
            isOpenPlaylist = false;
        }
    }

    @Override
    public void onClickSongItem(SongModel song) {
        startForegroundService(PLAY_NEW_SONG, song);
    }

    @Override
    public void onClickAddSong(SongModel song) {
        Toast.makeText(this, "Đã thêm vào danh sách", Toast.LENGTH_SHORT).show();
        startForegroundService(ADD_SONG, song);
    }

    @Override
    public void onBackPressed() {
        if (isOpenPlaylist) {
            hidePlaylistFragment();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverFromMusicService);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverWhenLoadAudio);
    }


}