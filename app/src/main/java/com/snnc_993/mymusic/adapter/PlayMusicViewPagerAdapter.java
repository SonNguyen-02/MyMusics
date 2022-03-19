package com.snnc_993.mymusic.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.snnc_993.mymusic.fragment.DetailSongFragment;
import com.snnc_993.mymusic.fragment.MainSongFragment;

public class PlayMusicViewPagerAdapter extends FragmentStateAdapter {


    public PlayMusicViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new MainSongFragment();
        }
        return new DetailSongFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }


}
