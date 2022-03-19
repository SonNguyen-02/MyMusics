package com.snnc_993.mymusic.broadcast;

import static com.snnc_993.mymusic.config.Constant.KEY_ACTION_MUSIC;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.snnc_993.mymusic.service.MusicService;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, @NonNull Intent intent) {
        int actionMusic = intent.getIntExtra(KEY_ACTION_MUSIC, -1);
        // Dùng để tương tác giữa foreground service vs notification
        Intent intentService = new Intent(context, MusicService.class);
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_ACTION_MUSIC, actionMusic);
        intentService.putExtras(bundle);
        context.startService(intentService);
    }
}
