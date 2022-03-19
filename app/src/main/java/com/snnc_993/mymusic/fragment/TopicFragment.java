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
import com.snnc_993.mymusic.adapter.TopicAdapter;
import com.snnc_993.mymusic.model.TopicModel;
import com.snnc_993.mymusic.service.APIService;
import com.snnc_993.mymusic.service.DataService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopicFragment extends Fragment {

    private RecyclerView rcvTopic;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topic, container, false);
        rcvTopic = view.findViewById(R.id.rcv_topic);
        initData();

        return view;
    }

    private void initData() {
        DataService dataService = APIService.getService();
        Call<List<TopicModel>> callback = dataService.getAllTopic();
        callback.enqueue(new Callback<List<TopicModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<TopicModel>> call, @NonNull Response<List<TopicModel>> response) {

                TopicAdapter adapter = new TopicAdapter(getContext(), response.body());
                rcvTopic.setAdapter(adapter);

                GridLayoutManager manager = new GridLayoutManager(getContext(), MainActivity.TOTAL_ITEM_CARD);
                rcvTopic.setLayoutManager(manager);
            }

            @Override
            public void onFailure(@NonNull Call<List<TopicModel>> call, @NonNull Throwable t) {
            }
        });
    }
}
