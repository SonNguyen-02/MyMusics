package com.snnc_993.mymusic.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.snnc_993.mymusic.R;
import com.snnc_993.mymusic.adapter.CardAdapter;
import com.snnc_993.mymusic.adapter.HomePageAdapter;
import com.snnc_993.mymusic.model.AlbumModel;
import com.snnc_993.mymusic.model.CategoryModel;
import com.snnc_993.mymusic.model.PlaylistModel;
import com.snnc_993.mymusic.model.RowCardModel;
import com.snnc_993.mymusic.model.SongModel;
import com.snnc_993.mymusic.service.APIService;
import com.snnc_993.mymusic.service.DataService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private View view;
    private RecyclerView rcvRowCard;
    private HomePageAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        rcvRowCard = view.findViewById(R.id.rcv_list_card);
        adapter = new HomePageAdapter(getContext());
        rcvRowCard.setAdapter(adapter);

        initRowCardModelList();
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rcvRowCard.setLayoutManager(manager);
//        getData();
        return view;
    }

    private void initRowCardModelList() {
        DataService dataService = APIService.getService();

        Call<List<AlbumModel>> callbackAlbum = dataService.getRandAlbum();
        callbackAlbum.enqueue(new Callback<List<AlbumModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<AlbumModel>> call, @NonNull Response<List<AlbumModel>> response) {
                if (response.body() != null) {
                    adapter.addRowCarModel(new RowCardModel("Có thể bạn muốn nghe", new ArrayList<>(response.body())));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AlbumModel>> call, @NonNull Throwable t) {
            }
        });

        Call<List<PlaylistModel>> callbackPlaylist = dataService.getRandPlaylist();
        callbackPlaylist.enqueue(new Callback<List<PlaylistModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<PlaylistModel>> call, @NonNull Response<List<PlaylistModel>> response) {
                if (response.body() != null) {
                    adapter.addRowCarModel(new RowCardModel("Top 100", new ArrayList<>(response.body())));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PlaylistModel>> call, @NonNull Throwable t) {
            }
        });

        Call<List<CategoryModel>> callbackCategory = dataService.getRandCategory();
        callbackCategory.enqueue(new Callback<List<CategoryModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoryModel>> call, @NonNull Response<List<CategoryModel>> response) {
                if (response.body() != null) {
                    adapter.addRowCarModel(new RowCardModel("Thể loại được ưa thích", new ArrayList<>(response.body())));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoryModel>> call, @NonNull Throwable t) {
            }
        });

        Call<List<SongModel>> callbackNewSong = dataService.getNewlyReleasedMusic();
        callbackNewSong.enqueue(new Callback<List<SongModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<SongModel>> call, @NonNull Response<List<SongModel>> response) {
                if (response.body() != null) {
                    adapter.addRowCarModel(new RowCardModel("Mới phát hành", new ArrayList<>(response.body())));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SongModel>> call, @NonNull Throwable t) {
            }
        });


    }

    private void getData() {
        DataService dataService = APIService.getService();
        Call<List<PlaylistModel>> callback = dataService.getRandPlaylist();
        callback.enqueue(new Callback<List<PlaylistModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<PlaylistModel>> call, @NonNull Response<List<PlaylistModel>> response) {
                if (response.body() != null) {
                    CardAdapter adapter = new CardAdapter(getContext(), new ArrayList<>(response.body()), true);
                    rcvRowCard.setAdapter(adapter);

                    LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
                    rcvRowCard.setLayoutManager(manager);
                }
            }

            @Override
            public void onFailure(Call<List<PlaylistModel>> call, Throwable t) {
            }
        });
    }
}
