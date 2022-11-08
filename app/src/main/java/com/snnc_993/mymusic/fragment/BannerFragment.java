package com.snnc_993.mymusic.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.snnc_993.mymusic.R;
import com.snnc_993.mymusic.adapter.BannerAdapter;
import com.snnc_993.mymusic.model.AdsModel;
import com.snnc_993.mymusic.service.APIService;
import com.snnc_993.mymusic.service.DataService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BannerFragment extends Fragment {

    private View mView;
    private ViewPager mViewPager;
    private CircleIndicator mCircleIndicator;
    private BannerAdapter mBannerAdapter;
    private Runnable runnable;
    private Handler handler;
    private static final int TIME_SLIDE_CHANGE = 5000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_banner, container, false);

        initUi();
        getData();

        return mView;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initUi() {
        mViewPager = mView.findViewById(R.id.banner_view_pager);
        mCircleIndicator = mView.findViewById(R.id.circle_indicator);
        View.OnTouchListener touchListener = (view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                view.getParent().requestDisallowInterceptTouchEvent(false);
            }
            return false;
        };
        mViewPager.setOnTouchListener(touchListener);
    }

    private void getData() {
        DataService dataService = APIService.getService();
        Call<List<AdsModel>> callback = dataService.getDataBanner();
        callback.enqueue(new Callback<List<AdsModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<AdsModel>> call, @NonNull Response<List<AdsModel>> response) {
                mBannerAdapter = new BannerAdapter(getActivity(), response.body());
                mViewPager.setAdapter(mBannerAdapter);
                mCircleIndicator.setViewPager(mViewPager);

                handler = new Handler();
                runnable = () -> {
                    int currentItem = mViewPager.getCurrentItem() + 1;
                    if (currentItem >= Objects.requireNonNull(mViewPager.getAdapter()).getCount()) {
                        currentItem = 0;
                    }
                    mViewPager.setCurrentItem(currentItem, true);
                    handler.postDelayed(runnable, TIME_SLIDE_CHANGE);
                };
                handler.postDelayed(runnable, TIME_SLIDE_CHANGE);
            }

            @Override
            public void onFailure(@NonNull Call<List<AdsModel>> call, @NonNull Throwable t) {
                Log.e("ddd", "onFailure: "+t.getMessage(), t);
            }
        });
    }

}
