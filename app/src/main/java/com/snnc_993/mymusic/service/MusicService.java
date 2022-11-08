package com.snnc_993.mymusic.service;

import static com.snnc_993.mymusic.application.MyApplication.CHANNEL_ID;
import static com.snnc_993.mymusic.config.Constant.*;
import static com.snnc_993.mymusic.utils.Helper.convertListSongFromJson;
import static com.snnc_993.mymusic.utils.Helper.convertSongFromJson;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.snnc_993.mymusic.R;
import com.snnc_993.mymusic.activity.MainActivity;
import com.snnc_993.mymusic.broadcast.MyReceiver;
import com.snnc_993.mymusic.model.SongModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicService extends Service {

    private int currentModePlay = NORMAL_MODE;

    private static int position;

    private static MediaPlayer media;

    private boolean isPlaying;

    private SongModel mCurrentSong;

    private long lastTimeInitSong;

    private static final List<SongModel> listSong = new ArrayList<>();
    private static final List<String> listSongId = new ArrayList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(@NonNull Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            int action = bundle.getInt(KEY_ACTION_MUSIC);
            if (action == STOP_SERVICE) {
                stopMusicService();
                return START_NOT_STICKY;
            }
            if (action == UPDATE_VIEW) {
                updateView();
                updateAnim(true);
                return START_NOT_STICKY;
            }
            if (action == PLAY_SONG || System.currentTimeMillis() - lastTimeInitSong > 300) {
                handleAction(action, bundle);
                lastTimeInitSong = System.currentTimeMillis();
            }
        }
        return START_NOT_STICKY;
    }

    private void stopMusicService() {
        isPlaying = false;
        updateView();
        updateAnim(false);
        stopSelf();
    }

    private void handleAction(int action, @NonNull Bundle bundle) {

        switch (action) {
            case FIRST_OPEN_APP:
                // mở bài hát lần cuối cùng mở
                initData();
                songInit(false);
                break;
            case PLAY_NEW_SONG:
                SongModel song = (SongModel) bundle.getSerializable(KEY_SONG_OBJ);
                if (song != null) {
                    if (!listSongId.contains(song.getId())) {
                        position = 0;
                        addSong(song, 0);
                    }
                    playNewSong(song);
                }
                break;
            case PLAY_NEW_LIST_SONG:
                String listSongJson = bundle.getString(KEY_PLAY_NEW_LIST_SONG);
                List<SongModel> newListSong = convertListSongFromJson(listSongJson);
                if (!newListSong.isEmpty()) {
                    position = bundle.getInt(KEY_POS_NEW_SONG);
                    listSong.clear();
                    listSong.addAll(newListSong);
                    listSongId.clear();
                    for (SongModel s : listSong) {
                        listSongId.add(s.getId());
                    }
                    playNewSong(listSong.get(position));
                    saveDataToSharedPre(KEY_LAST_LIST_SONG, listSongJson);
                }
                break;
            case PREV_SONG:
                prevSong();
                break;
            case NEXT_SONG:
                nextSong(false);
                break;
            case PLAY_SONG:
                playSong();
                break;
            case PAUSE_SONG:
                pauseSong();
                break;
            case SEEK_SONG:
                int timeSeek = bundle.getInt(KEY_TIME_SEEK_SONG);
                seekSong(timeSeek);
                return;
            case SHUFFLE_SONG:
                shuffleMode();
                break;
            case REPEAT_SONG:
                repeatMode();
                break;
            case SAVE_LIST_SONG:
                saveListSong();
            case ADD_SONG:
                addSong((SongModel) bundle.getSerializable(KEY_SONG_OBJ));
                break;
        }
        updateView();
    }


    private void initData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PRE_NAME, MODE_PRIVATE);
        currentModePlay = sharedPreferences.getInt(KEY_LAST_MODE_PLAY, NORMAL_MODE);
        position = sharedPreferences.getInt(KEY_LAST_POS_SONG, -1);
        String songJson = sharedPreferences.getString(KEY_LAST_SONG, "");
        String lastListSongJson = sharedPreferences.getString(KEY_LAST_LIST_SONG, "");
        mCurrentSong = convertSongFromJson(songJson);
        List<SongModel> list = convertListSongFromJson(lastListSongJson);
        for (SongModel song : list) {
            listSongId.add(song.getId());
//            Log.e("ddd", "initData: " + song.getId() + " " + song.getName());
        }

        listSong.addAll(list);
    }

    private void playNewSong(@NonNull SongModel song) {
        if (mCurrentSong == null || !song.getId().equals(mCurrentSong.getId())) {
            mCurrentSong = song;
            songInit(true);
        } else {
            playSong();
        }
    }

    private void prevSong() {
        if (listSong.size() > position && position >= 0) {
            if (position - 1 < 0) {
                playNewSong(listSong.get(listSong.size() - 1));
                position = listSong.size() - 1;
            } else {
                playNewSong(listSong.get(--position));
            }
        }
    }

    private String lastSong;

    private void nextSong(boolean isAutoNext) {
        lastSong = mCurrentSong.getName();
        if (listSong.size() > position && position >= 0) {
            switch (currentModePlay) {
                case NORMAL_MODE:
                    if (position + 1 >= listSong.size()) {
                        if (isAutoNext) {
                            stopMusicService();
                        } else {
                            position = 0;
                            playNewSong(listSong.get(0));
                        }
                    } else {
                        playNewSong(listSong.get(++position));
                    }
                    break;
                case SHUFFLE_MODE:
                    if (listSong.size() > 2) {
                        position = getRandIndex();
                        playNewSong(listSong.get(position));
                    } else {
                        Toast.makeText(this, "Danh sách phải lớn hơn 2 bài", Toast.LENGTH_SHORT).show();
                        currentModePlay = NORMAL_MODE;
                        saveDataToSharedPre(KEY_LAST_MODE_PLAY, NORMAL_MODE);
                        nextSong(false);
                    }
                    break;
                case REPEAT_ONE_MODE:
                    if (isAutoNext) {
                        seekSong(1);
                        break;
                    }
                case REPEAT_MODE:
                    if (position + 1 >= listSong.size()) {
                        position = 0;
                        playNewSong(listSong.get(0));
                    } else {
                        playNewSong(listSong.get(++position));
                    }
                    break;
            }
        }
    }

    private void playSong() {
        if (media != null && mCurrentSong != null) {
            if (!isPlaying) {
                media.start();
                isPlaying = true;
                updateAnim(true);
                sendNotificationMusic(mCurrentSong);
                saveDataToSharedPre(KEY_LAST_POS_SONG, position);
            }
        } else {
            initData();
            songInit(true);
        }
    }

    private void pauseSong() {
        if (isPlaying) {
            media.pause();
            isPlaying = false;
            updateAnim(false);
            if (mCurrentSong != null) {
                sendNotificationMusic(mCurrentSong);
            }
        }
    }

    private void shuffleMode() {
        if (currentModePlay == SHUFFLE_MODE) {
            currentModePlay = NORMAL_MODE;
            saveDataToSharedPre(KEY_LAST_MODE_PLAY, NORMAL_MODE);
            Toast.makeText(this, "Chế độ phát bình thường", Toast.LENGTH_SHORT).show();
        } else {
            currentModePlay = SHUFFLE_MODE;
            saveDataToSharedPre(KEY_LAST_MODE_PLAY, SHUFFLE_MODE);
            Toast.makeText(this, "Chế độ phát ngẫu nhiên", Toast.LENGTH_SHORT).show();
        }
    }

    private void repeatMode() {
        switch (currentModePlay) {
            case NORMAL_MODE:
            case SHUFFLE_MODE:
                currentModePlay = REPEAT_MODE;
                saveDataToSharedPre(KEY_LAST_MODE_PLAY, REPEAT_MODE);
                Toast.makeText(this, "Danh sách phát hiện tại đang lặp lại", Toast.LENGTH_SHORT).show();
                break;
            case REPEAT_MODE:
                currentModePlay = REPEAT_ONE_MODE;
                saveDataToSharedPre(KEY_LAST_MODE_PLAY, REPEAT_ONE_MODE);
                Toast.makeText(this, "Chế độ phát lặp lại 1 bài hát", Toast.LENGTH_SHORT).show();
                break;
            case REPEAT_ONE_MODE:
                currentModePlay = NORMAL_MODE;
                saveDataToSharedPre(KEY_LAST_MODE_PLAY, NORMAL_MODE);
                Toast.makeText(this, "Chế độ phát bình thường", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void seekSong(int time) {
        if (media != null) {
            isPlaying = true;
            media.seekTo(time);
            media.start();
            updateAnim(true);
        }
    }

    private void saveListSong() {
        Gson gson = new Gson();
        JsonArray jsonArray = gson.toJsonTree(listSong).getAsJsonArray();
        saveDataToSharedPre(KEY_LAST_LIST_SONG, jsonArray.toString());
    }

    public void addSong(SongModel song) {
        addSong(song, -1);
    }

    public void addSong(SongModel song, int index) {
        if (song != null && !listSong.contains(song)) {
            if (index >= 0) {
                listSong.add(index, song);
            } else {
                listSong.add(song);
            }
            listSongId.add(song.getId());
            saveListSong();
        }
    }


    private void songInit(boolean playNow) {
        updateAnim(false);

        if (mCurrentSong == null) {
            return;
        }
        if (media != null) {
            media.release();
            Log.e("ddd", "songInit: release " + lastSong);
        }
        isPlaying = false;
        media = new MediaPlayer();
        media.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            media.setDataSource(mCurrentSong.getAudio());
            Log.e("ddd", "songInit: prepare " + mCurrentSong.getName());
            media.setOnPreparedListener(mediaPlayer -> {
                Log.e("ddd", "songInit: success " + mCurrentSong.getName());
                if (playNow) {
                    playSong();
                    updateView();
                    updateAnim(true);
                }
                media.setOnCompletionListener(mediaPlayer1 -> {
                    Log.e("ddd", "songInit: compelete " + mCurrentSong.getName());
                    nextSong(true);
                    updateView();
                });
            });
            media.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("ddd", "songInit: total thread ===== " + Thread.activeCount() + " =====");

        Gson gson = new Gson();
        saveDataToSharedPre(KEY_LAST_SONG, gson.toJson(mCurrentSong));
    }

    private void updateView() {
        // send back activity to update view
        Bundle bundles = new Bundle();
        bundles.putBoolean(KEY_IS_PLAYING, isPlaying);
        bundles.putSerializable(KEY_CURRENT_SONG, mCurrentSong);
        sendActionToActivity(bundles, SEND_TO_ACTIVITY);
    }

    private void updateAnim(boolean loadAudioSuccess) {
        // send back activity to update view
        Bundle bundles = new Bundle();
        bundles.putBoolean(KEY_IS_LOAD_SUCCESS, loadAudioSuccess);
        sendActionToActivity(bundles, SEND_STATUS_LOAD_AUDIO);
    }

    private void sendNotificationMusic(SongModel song) {
        Intent intent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, getFlag());

        RemoteViews remoteCollapsed = getRemoteViewCollapsed(song);

        RemoteViews remoteExpanded = getRemoteViewExpanded(song);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_app_small)
                .setCustomContentView(remoteCollapsed)
                .setCustomBigContentView(remoteExpanded)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setSound(getSound(), AudioManager.STREAM_NOTIFICATION)
                .build();
        loadImage(song.getImg(), image -> {
            if (image != null) {
                remoteCollapsed.setImageViewBitmap(R.id.img_song_notification, image);
                remoteExpanded.setImageViewBitmap(R.id.img_song_notification, image);
            }
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(1, notification);
            startForeground(1, notification);
        });
    }

    @NonNull
    private RemoteViews getRemoteViewCollapsed(SongModel song) {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_music_play_notification_collapsed);

        remoteViews.setOnClickPendingIntent(R.id.img_next, getPendingIntent(this, NEXT_SONG));
        if (isPlaying) {
            remoteViews.setImageViewResource(R.id.img_play_pause, R.drawable.ic_pause);
            remoteViews.setOnClickPendingIntent(R.id.img_play_pause, getPendingIntent(this, PAUSE_SONG));
        } else {
            remoteViews.setImageViewResource(R.id.img_play_pause, R.drawable.ic_play);
            remoteViews.setOnClickPendingIntent(R.id.img_play_pause, getPendingIntent(this, PLAY_SONG));
        }
        remoteViews.setTextViewText(R.id.text_song_name, song.getName());
        remoteViews.setTextViewText(R.id.text_author_name, song.getSingerName());

        return remoteViews;
    }

    @NonNull
    private RemoteViews getRemoteViewExpanded(SongModel song) {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_music_play_notification_expanded);

        remoteViews.setOnClickPendingIntent(R.id.img_prev, getPendingIntent(this, PREV_SONG));
        remoteViews.setOnClickPendingIntent(R.id.img_next, getPendingIntent(this, NEXT_SONG));
        remoteViews.setOnClickPendingIntent(R.id.img_cancel, getPendingIntent(this, STOP_SERVICE));

        if (isPlaying) {
            remoteViews.setImageViewResource(R.id.img_play_pause, R.drawable.ic_pause);
            remoteViews.setOnClickPendingIntent(R.id.img_play_pause, getPendingIntent(this, PAUSE_SONG));
        } else {
            remoteViews.setImageViewResource(R.id.img_play_pause, R.drawable.ic_play);
            remoteViews.setOnClickPendingIntent(R.id.img_play_pause, getPendingIntent(this, PLAY_SONG));
        }
        remoteViews.setTextViewText(R.id.text_song_name, song.getName());
        remoteViews.setTextViewText(R.id.text_author_name, song.getSingerName());

        return remoteViews;
    }

    private PendingIntent getPendingIntent(Context context, int action) {
        Intent intent = new Intent(this, MyReceiver.class);
        intent.putExtra(KEY_ACTION_MUSIC, action);
        return PendingIntent.getBroadcast(context, action, intent, getFlag());
    }

    private void sendActionToActivity(Bundle bundle, String action) {
        Intent intent = new Intent(action);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private int getRandIndex() {
        Random random = new Random();
        int num = random.nextInt(listSong.size() - 1);
        return num != position ? num : getRandIndex();
    }

    private void saveDataToSharedPre(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PRE_NAME, MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void saveDataToSharedPre(String key, int value) {
        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PRE_NAME, MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getPosition() {
        return position;
    }

    public static MediaPlayer getMedia() {
        return media;
    }

    @NonNull
    public static String getListSongId() {
        StringBuilder sb = new StringBuilder();
        for (String s : listSongId) {
            sb.append(s).append(",");
        }
        return sb.toString();
    }

    public static List<SongModel> getListSong() {
        return listSong;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (media != null) {
            media.release();
            media = null;
        }
    }

    private int getFlag() {
        int flag = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flag |= PendingIntent.FLAG_IMMUTABLE;
        }
        return flag;
    }

    private Uri getSound() {
        return Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.empty_sound);
    }

    private void loadImage(String url, LoadImageListener listener) {
        Glide.with(this)
                .asBitmap()
                .load(url)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        listener.onLoadDone(null);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        listener.onLoadDone(resource);
                        return false;
                    }
                }).submit();
    }

    private interface LoadImageListener {
        void onLoadDone(Bitmap image);
    }

}
