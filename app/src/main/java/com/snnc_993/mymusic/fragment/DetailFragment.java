package com.snnc_993.mymusic.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.snnc_993.mymusic.R;
import com.snnc_993.mymusic.activity.MainActivity;
import com.snnc_993.mymusic.adapter.CardAdapter;
import com.snnc_993.mymusic.adapter.SongAdapter;
import com.snnc_993.mymusic.config.Constant;
import com.snnc_993.mymusic.model.AlbumModel;
import com.snnc_993.mymusic.model.CardModel;
import com.snnc_993.mymusic.model.CategoryModel;
import com.snnc_993.mymusic.model.SongModel;
import com.snnc_993.mymusic.model.TopicModel;
import com.snnc_993.mymusic.service.APIService;
import com.snnc_993.mymusic.service.DataService;
import com.snnc_993.mymusic.utils.SimpleRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailFragment extends Fragment {

    private final CardModel card;
    private View view;
    private View overlay;
    private ImageView imgBackground;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private ImageView imgBackHome;
    private TextView tvTitleToolbar;
    private RelativeLayout rlDetailHeader;
    private ImageView imgCard;
    private TextView titleCard;
    private TextView subTitleCard;
    private Button btnPlayRand;
    private List<CardModel> mListCard;
    private List<SongModel> mListSong;

    private RecyclerView rcvSong;

    public DetailFragment(List<CardModel> list, CardModel card) {
        this.mListCard = list;
        this.card = card;
    }

    public DetailFragment(CardModel card, List<SongModel> list) {
        this.mListSong = list;
        this.card = card;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail, container, false);
        initUi();
        initData();
        initToolBar();
        initToolBarAnimation();
        imgBackHome.setOnClickListener(view -> ((MainActivity) requireActivity()).removeFragmentFromMainFrame());
        return view;
    }

    private void initUi() {
        overlay = view.findViewById(R.id.overlay);
        imgBackground = view.findViewById(R.id.img_background);
        mAppBarLayout = view.findViewById(R.id.app_bar_layout);
        mToolbar = view.findViewById(R.id.toolbar);
        imgBackHome = view.findViewById(R.id.img_back_home);
        tvTitleToolbar = view.findViewById(R.id.tv_title_toolbar);
        if (card instanceof TopicModel) {
            rlDetailHeader = view.findViewById(R.id.rl_detail_header_with_list_category);
            imgCard = view.findViewById(R.id.img_bg_topic);
        } else {
            rlDetailHeader = view.findViewById(R.id.rl_detail_header_with_list_song);
            imgCard = view.findViewById(R.id.img_thumb);
        }
        titleCard = view.findViewById(R.id.tv_title_card);
        subTitleCard = view.findViewById(R.id.tv_sub_title_card);
        btnPlayRand = view.findViewById(R.id.btn_play_rand);
        rcvSong = view.findViewById(R.id.rcv_song);
        rcvSong.setHasFixedSize(true);
    }

    private void initToolBar() {
        ((AppCompatActivity) requireActivity()).setSupportActionBar(mToolbar);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
        }
    }

    private void initToolBarAnimation() {
        mAppBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int maxOffset = appBarLayout.getHeight() - (int) requireContext().getResources().getDimension(R.dimen.custom_action_bar_size);
            float alpha = (float) Math.abs(verticalOffset) * 100 / maxOffset;
            if (rlDetailHeader.getAlpha() != alpha) {
                rlDetailHeader.setAlpha(1 - (float) Math.abs(verticalOffset) / maxOffset);
                if (Math.abs(verticalOffset) == maxOffset) {
                    if (tvTitleToolbar.getText().toString().isEmpty()) {
                        tvTitleToolbar.setText(card.getName());
                    }
                } else {
                    if (!tvTitleToolbar.getText().toString().isEmpty()) {
                        tvTitleToolbar.setText("");
                    }
                }
            }
        });
    }

    private void initData() {
        rlDetailHeader.setVisibility(View.VISIBLE);
        Glide.with(this).load(card.getImg()).into(imgCard);
        if (card instanceof TopicModel) {
            CollapsingToolbarLayout collapsingToolbarLayout = view.findViewById(R.id.collapsingToolbarLayout);
            collapsingToolbarLayout.setContentScrimColor(Color.parseColor(Constant.COLOR_PRIMARY));
            overlay.setBackgroundResource(R.drawable.custom_overlay_card_white);
            ((RelativeLayout.LayoutParams) rcvSong.getLayoutParams()).leftMargin = (int) getResources().getDimension(R.dimen.space_view);
            ((RelativeLayout.LayoutParams) rcvSong.getLayoutParams()).bottomMargin = (int) getResources().getDimension(R.dimen.space_view);

            CardAdapter adapter = new CardAdapter(getContext(), mListCard, false);
            rcvSong.setAdapter(adapter);
            GridLayoutManager manager = new GridLayoutManager(getContext(), MainActivity.TOTAL_ITEM_CARD);
            rcvSong.setLayoutManager(manager);
            return;
        }
        Glide.with(this)
                .asBitmap()
                .load(card.getImg())
                .placeholder(R.drawable.custom_overlay_black)
                .addListener(new SimpleRequest(imgBackground))
                .submit();

        titleCard.setText(card.getName());
        if (card instanceof AlbumModel) {
            subTitleCard.setText(((AlbumModel) card).getSingerName());
            subTitleCard.setVisibility(View.VISIBLE);
        }

        if (mListSong == null || mListSong.isEmpty()) {
            view.findViewById(R.id.tv_no_have_song).setVisibility(View.VISIBLE);
            rcvSong.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        } else {
            SongAdapter adapter = new SongAdapter(getContext(), mListSong);
            rcvSong.setAdapter(adapter);
            LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            rcvSong.setLayoutManager(manager);
        }

    }

    private void initCategory() {
        DataService dataService = APIService.getService();
        Call<List<CategoryModel>> callback = dataService.getAllCategoryInTopic(Integer.parseInt(card.getId()));

        callback.enqueue(new Callback<List<CategoryModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoryModel>> call, @NonNull Response<List<CategoryModel>> response) {
                if (response.body() != null) {
                    CardAdapter adapter = new CardAdapter(getContext(), new ArrayList<>(response.body()), false);
                    rcvSong.setAdapter(adapter);

                    GridLayoutManager manager = new GridLayoutManager(getContext(), MainActivity.TOTAL_ITEM_CARD);
                    rcvSong.setLayoutManager(manager);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoryModel>> call, @NonNull Throwable t) {
                Log.e("ddd", "onFailure: ", t);
            }
        });
    }
}
