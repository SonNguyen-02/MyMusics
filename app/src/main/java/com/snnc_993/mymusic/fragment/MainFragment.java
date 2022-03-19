package com.snnc_993.mymusic.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.snnc_993.mymusic.R;
import com.snnc_993.mymusic.activity.MainActivity;
import com.snnc_993.mymusic.adapter.MainViewPagerAdapter;

public class MainFragment extends Fragment {

    private View view;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private BottomNavigationView mTopNavView;
    private ViewPager2 mViewPager2;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        initUi();

        // set adapter
        MainViewPagerAdapter mHomeViewPagerAdapter = new MainViewPagerAdapter(requireActivity());
        mViewPager2.setAdapter(mHomeViewPagerAdapter);

        // set toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        // toggle btn for toolbar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(requireActivity(), mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        // set item default check
        mNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);

        // set item of left menu click
        mNavigationView.setNavigationItemSelectedListener(this::onItemSelected);

        mTopNavView.setOnItemSelectedListener(this::onItemSelected);

        // đồng bộ giữa top nav & left menu
        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        mTopNavView.getMenu().findItem(R.id.top_home).setChecked(true);
                        mNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
                        break;
                    case 1:
                        mTopNavView.getMenu().findItem(R.id.top_playlist).setChecked(true);
                        mNavigationView.getMenu().findItem(R.id.nav_playlist).setChecked(true);
                        break;
                    case 2:
                        mTopNavView.getMenu().findItem(R.id.top_album).setChecked(true);
                        mNavigationView.getMenu().findItem(R.id.nav_album).setChecked(true);
                        break;
                    case 3:
                        mTopNavView.getMenu().findItem(R.id.top_topic).setChecked(true);
                        mNavigationView.getMenu().findItem(R.id.nav_topic).setChecked(true);
                        break;
                }
            }
        });

        return view;
    }

    private void initUi() {
        mDrawerLayout = requireActivity().findViewById(R.id.drawer_layout);
        mNavigationView = requireActivity().findViewById(R.id.navigation_view);
        mTopNavView = view.findViewById(R.id.top_nav);
        mViewPager2 = view.findViewById(R.id.view_pager2);
    }

    @SuppressLint("NonConstantResourceId")
    private boolean onItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
            case R.id.top_home:
                mViewPager2.setCurrentItem(0);
                break;
            case R.id.nav_playlist:
            case R.id.top_playlist:
                mViewPager2.setCurrentItem(1);
                break;
            case R.id.nav_album:
            case R.id.top_album:
                mViewPager2.setCurrentItem(2);
                break;
            case R.id.nav_topic:
            case R.id.top_topic:
                mViewPager2.setCurrentItem(3);
                break;
        }
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

}
