package com.snnc_993.mymusic.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.snnc_993.mymusic.R;
import com.snnc_993.mymusic.activity.MainActivity;
import com.snnc_993.mymusic.adapter.CardAdapter;
import com.snnc_993.mymusic.model.AlbumModel;
import com.snnc_993.mymusic.service.APIService;
import com.snnc_993.mymusic.service.DataService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumFragment extends Fragment {

    private RecyclerView rcvCard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        rcvCard = view.findViewById(R.id.rcv_card);
        initData();

        return view;
    }


    private void initData() {
        DataService dataService = APIService.getService();
        Call<List<AlbumModel>> callback = dataService.getAllAlbum();
        callback.enqueue(new Callback<List<AlbumModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<AlbumModel>> call, @NonNull Response<List<AlbumModel>> response) {
                if (response.body() != null) {
                    CardAdapter adapter = new CardAdapter(getContext(), new ArrayList<>(response.body()), false);
                    rcvCard.setAdapter(adapter);

                    GridLayoutManager manager = new GridLayoutManager(getContext(), MainActivity.TOTAL_ITEM_CARD);
                    rcvCard.setLayoutManager(manager);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AlbumModel>> call, @NonNull Throwable t) {
            }
        });
    }
}
