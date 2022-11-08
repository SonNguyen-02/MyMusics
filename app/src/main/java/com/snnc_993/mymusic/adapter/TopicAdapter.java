package com.snnc_993.mymusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.snnc_993.mymusic.R;
import com.snnc_993.mymusic.activity.ISendDataToDetail;
import com.snnc_993.mymusic.activity.MainActivity;
import com.snnc_993.mymusic.model.TopicModel;

import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {

    private final Context context;
    private final List<TopicModel> topicModelList;
    private ISendDataToDetail mISendDataToDetail;

    public TopicAdapter(Context context, List<TopicModel> topicModelList) {
        this.context = context;
        this.topicModelList = topicModelList;
        if(context instanceof MainActivity){
            mISendDataToDetail = (ISendDataToDetail) context;
        }
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        TopicModel topic = topicModelList.get(position);
        if(topic == null){
            return;
        }
        Glide.with(context).load(topic.getImg()).into(holder.imgThumb);
        holder.tvName.setText(topic.getName());
        if(mISendDataToDetail != null){
            holder.itemView.setOnClickListener(view -> mISendDataToDetail.sendDataListener(topic, ISendDataToDetail.Action.SHOW_MODAL));
        }
    }

    @Override
    public int getItemCount() {
        if(topicModelList != null){
            return topicModelList.size();
        }
        return 0;
    }

    public static class TopicViewHolder extends RecyclerView.ViewHolder{

        ImageView imgThumb;
        TextView tvName;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.img_thumb);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }
}
