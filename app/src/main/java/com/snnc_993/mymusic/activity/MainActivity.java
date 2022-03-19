package com.snnc_993.mymusic.activity;

import static com.snnc_993.mymusic.config.Constant.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.snnc_993.mymusic.R;
import com.snnc_993.mymusic.config.Constant;
import com.snnc_993.mymusic.fragment.DetailFragment;
import com.snnc_993.mymusic.fragment.MainFragment;
import com.snnc_993.mymusic.model.AlbumModel;
import com.snnc_993.mymusic.model.CardModel;
import com.snnc_993.mymusic.model.CategoryModel;
import com.snnc_993.mymusic.model.PlaylistModel;
import com.snnc_993.mymusic.model.SongModel;
import com.snnc_993.mymusic.model.TopicModel;
import com.snnc_993.mymusic.service.APIService;
import com.snnc_993.mymusic.service.DataService;
import com.snnc_993.mymusic.service.MusicService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ISendDataToDetail {

    public static final int TOTAL_ITEM_CARD = 2;
    public static int HEIGHT_DEVICE;
    public static int WIDTH_DEVICE;

    private int layerCount = 0;
    private boolean isBackPress;
    private long lastTimeAdd;

    private DrawerLayout mDrawerLayout;
    private RelativeLayout controlLayout;
    private SeekBar mSeekBar;
    private ImageView imgThumb;
    private TextView tvSongName;
    private TextView tvSingerName;
    private ImageView imgLikes, imgPlayPause, imgNext;
    private boolean isPlaying;
    private SongModel mCurrentSong;

    BroadcastReceiver receiverFromMusicService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                isPlaying = bundle.getBoolean(KEY_IS_PLAYING);
                if (isPlaying) {
                    imgPlayPause.setImageResource(R.drawable.ic_pause);
                } else {
                    imgPlayPause.setImageResource(R.drawable.ic_play);
                }
                SongModel song = (SongModel) bundle.getSerializable(KEY_CURRENT_SONG);
                if (song != null && MusicService.getMedia() != null) {
                    if (mCurrentSong != song) {
                        mCurrentSong = song;
                        tvSongName.setText(mCurrentSong.getName());
                        tvSingerName.setText(mCurrentSong.getSingerName());
                        if (tvSingerName.getVisibility() != View.VISIBLE) {
                            tvSingerName.setVisibility(View.VISIBLE);
                        }
                        Picasso.with(MainActivity.this).load(mCurrentSong.getImg()).into(imgThumb);
                    }
                    updateCurrentTime();
                }
            }
        }
    };

    BroadcastReceiver receiverWhenLoadAudio = new BroadcastReceiver() {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUi();

        LocalBroadcastManager.getInstance(this).registerReceiver(receiverFromMusicService, new IntentFilter(Constant.SEND_TO_ACTIVITY));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverWhenLoadAudio, new IntentFilter(Constant.SEND_STATUS_LOAD_AUDIO));

        controlLayout.setOnClickListener(view -> {
            if (mCurrentSong != null) {
                Intent intent = new Intent(this, PlayMusicActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(KEY_DATA_TO_PLAY_MUSIC, mCurrentSong);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        imgLikes.setOnClickListener(view -> {

        });

        imgPlayPause.setOnClickListener(view -> {
            if (mCurrentSong == null) {
                Toast.makeText(MainActivity.this, "Hãy chọn list bài để play", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isPlaying) {
                startForegroundService(PAUSE_SONG);
            } else {
                startForegroundService(PLAY_SONG);
            }
        });

        imgNext.setOnClickListener(view -> {
            startForegroundService(NEXT_SONG);
        });

        if (MusicService.getMedia() == null) {
            // khoi tao bai hat cuoi khi chay lai app
            startForegroundService();
        } else {
            // neu service dang chay thi update view
            startForegroundService(UPDATE_VIEW);
        }
    }


    private void startAnim() {
        stopAnim();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                imgThumb.animate().rotationBy(360).withEndAction(this).setDuration(10000).setInterpolator(new LinearInterpolator()).start();
            }
        };
        imgThumb.animate().rotationBy(360).withEndAction(runnable).setDuration(10000).setInterpolator(new LinearInterpolator()).start();
    }

    private void stopAnim() {
        imgThumb.animate().cancel();
    }

    private void startForegroundService() {
        startForegroundService(FIRST_OPEN_APP);
    }

    private void startForegroundService(int action) {
        startForegroundService(action, null);
    }

    private void startForegroundService(int action, SongModel mSong) {
        Intent intent = new Intent(this, MusicService.class);
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_ACTION_MUSIC, action);
        bundle.putSerializable(KEY_SONG_OBJ, mSong);
        intent.putExtras(bundle);
        startService(intent);
    }

    private void startNewListSong(int position, String listSongJson) {
        Intent intent = new Intent(this, MusicService.class);
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_ACTION_MUSIC, Constant.PLAY_NEW_LIST_SONG);
        bundle.putInt(KEY_POS_NEW_SONG, position);
        bundle.putString(KEY_PLAY_NEW_LIST_SONG, listSongJson);
        intent.putExtras(bundle);
        startService(intent);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initUi() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        HEIGHT_DEVICE = metrics.heightPixels;
        WIDTH_DEVICE = metrics.widthPixels;
        mDrawerLayout = findViewById(R.id.drawer_layout);
        controlLayout = findViewById(R.id.control_layout);
        mSeekBar = findViewById(R.id.sb_music);
        mSeekBar.setOnTouchListener((v, event) -> true);
        imgThumb = findViewById(R.id.img_thumb);
        tvSongName = findViewById(R.id.tv_song_name);
        tvSingerName = findViewById(R.id.tv_singer_name);
        imgLikes = findViewById(R.id.img_likes);
        imgPlayPause = findViewById(R.id.img_play_pause);
        imgNext = findViewById(R.id.img_next);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new MainFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (layerCount == 0) {
                if (isBackPress) {
                    super.onBackPressed();
                    isBackPress = false;
                } else {
                    Toast.makeText(this, "Nhấn back thêm lần nữa để thoát.", Toast.LENGTH_SHORT).show();
                    isBackPress = true;
                }
            } else {
                removeFragmentFromMainFrame();
                isBackPress = false;
            }
        }
    }

    @Override
    public void sendDataListener(CardModel card, Enum<Action> action) {
        if (checkChangeScreen()) {
            if (action == Action.PLAY) {
                if (card instanceof SongModel) {
                    startForegroundService(PLAY_NEW_SONG, (SongModel) card);
                    goToPlayMusicActivity((SongModel) card);
                } else {
                    initListSong(card, list -> {
                        if (list != null && !list.isEmpty()) {
                            sendDataListener(list, 0);
                        } else {
                            String str = "List này chưa có bài hát. Hãy chọn list khác!";
                            Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            if (action == Action.SHOW_MODAL) {
                if (card instanceof TopicModel) {
                    initCategory(Integer.parseInt(card.getId()), list -> {
                        addFragmentToMainFrame(new DetailFragment(list, card), "Category");
                    });
                } else {
                    initListSong(card, list -> {
                        addFragmentToMainFrame(new DetailFragment(card, list), "Detail");
                    });
                }
            }
        }
    }

    @Override
    public void sendDataListener(List<SongModel> mListSong, int index) {
        lastTimeAdd = System.currentTimeMillis();
        Gson gson = new Gson();
        JsonArray jsonArray = gson.toJsonTree(mListSong).getAsJsonArray();
        String strJson = jsonArray.toString();
        startNewListSong(index, strJson);

        goToPlayMusicActivity(mListSong.get(index));
    }

    private void goToPlayMusicActivity(SongModel song) {
        Intent intent = new Intent(this, PlayMusicActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_DATA_TO_PLAY_MUSIC, song);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private boolean checkChangeScreen() {
        if (System.currentTimeMillis() - lastTimeAdd < 800) {
            return false;
        }
        lastTimeAdd = System.currentTimeMillis();
        return true;
    }

    private void updateCurrentTime() {
        if (MusicService.getMedia().isPlaying()) {
            mSeekBar.setMax(MusicService.getMedia().getDuration());
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // update seek bar
                if (MusicService.getMedia() != null) {
                    mSeekBar.setProgress(MusicService.getMedia().getCurrentPosition());
                    handler.postDelayed(this, 500);
                } else {
                    imgPlayPause.setImageResource(R.drawable.ic_play);
                    stopAnim();
                    mSeekBar.setProgress(0);
                }
            }
        }, 100);
    }

    private void addFragmentToMainFrame(Fragment fragment, String name) {
        if (layerCount == 0) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        layerCount++;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_right_in, R.anim.anim_right_out, R.anim.anim_left_in, R.anim.anim_left_out);
        transaction.add(R.id.main_frame, fragment);
        transaction.addToBackStack(name);
        transaction.commit();
    }

    public void removeFragmentFromMainFrame() {
        layerCount--;
        getSupportFragmentManager().popBackStack();
        if (layerCount == 0) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    public interface CallbackInitSong {
        void callback(List<SongModel> list);
    }

    public static void initListSong(CardModel mCard, CallbackInitSong cb) {
        if (mCard != null) {
            String type = "";
            if (mCard instanceof AlbumModel) {
                type = "album";
            }
            if (mCard instanceof CategoryModel) {
                type = "category";
            }
            if (mCard instanceof PlaylistModel) {
                type = "playlist";
            }
            DataService dataService = APIService.getService();
            Call<List<SongModel>> callback = dataService.getAllSongFrom(type, Integer.parseInt(mCard.getId()));

            callback.enqueue(new Callback<List<SongModel>>() {
                @Override
                public void onResponse(@NonNull Call<List<SongModel>> call, @NonNull Response<List<SongModel>> response) {
                    cb.callback(response.body());
                }

                @Override
                public void onFailure(@NonNull Call<List<SongModel>> call, @NonNull Throwable t) {
                    cb.callback(null);
                }
            });
        }
    }

    private interface CallbackInitCategory {
        void callback(List<CardModel> list);
    }

    private void initCategory(int id, CallbackInitCategory cb) {
        DataService dataService = APIService.getService();
        Call<List<CategoryModel>> callback = dataService.getAllCategoryInTopic(id);
        callback.enqueue(new Callback<List<CategoryModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoryModel>> call, @NonNull Response<List<CategoryModel>> response) {
                List<CardModel> list = null;
                if (response.body() != null) {
                    list = new ArrayList<>(response.body());
                }
                cb.callback(list);
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoryModel>> call, @NonNull Throwable t) {
                cb.callback(null);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverFromMusicService);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverWhenLoadAudio);
    }
}