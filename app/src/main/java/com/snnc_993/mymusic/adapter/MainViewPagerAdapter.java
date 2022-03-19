package com.snnc_993.mymusic.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.snnc_993.mymusic.fragment.AlbumFragment;
import com.snnc_993.mymusic.fragment.HomeFragment;
import com.snnc_993.mymusic.fragment.PlaylistFragment;
import com.snnc_993.mymusic.fragment.TopicFragment;

public class MainViewPagerAdapter extends FragmentStateAdapter {


    public MainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new PlaylistFragment();
            case 2:
                return new AlbumFragment();
            case 3:
                return new TopicFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
