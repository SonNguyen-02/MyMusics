package com.snnc_993.mymusic.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.snnc_993.mymusic.R;
import com.snnc_993.mymusic.activity.PlayMusicActivity;
import com.snnc_993.mymusic.model.SongModel;

import java.util.List;

public class SongSpecialAdapter extends RecyclerView.Adapter<SongSpecialAdapter.SongViewHolder> {

    private final Context context;
    private final List<SongModel> mListSong;
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    private IOnClickSongItem mIOnClickSongItem;
    private final IOnItemDelete mIOnItemDelete;
    private StartDragListener dragListener;

    public SongSpecialAdapter(Context context, List<SongModel> mListSong, IOnItemDelete mIOnItemDelete) {
        this.context = context;
        this.mListSong = mListSong;

        this.mIOnItemDelete = mIOnItemDelete;
        if (context instanceof PlayMusicActivity) {
            mIOnClickSongItem = (IOnClickSongItem) context;
        }
    }

    public interface IOnClickSongItem {
        void onClickSongItem(SongModel song);
    }

    public interface IOnItemDelete {
        void onItemDeleted();
    }

    public interface StartDragListener {
        void requestDrag(RecyclerView.ViewHolder viewHolder);
    }

    public void setDragListener(StartDragListener dragListener) {
        this.dragListener = dragListener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_sw_delete, parent, false);
        return new SongViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        SongModel song = mListSong.get(position);
        if (song == null) {
            return;
        }
        binderHelper.bind(holder.swrLayout, song.getId());

        holder.tvSongName.setText(song.getName());
        holder.tvSingerName.setText(song.getSingerName());
        Glide.with(context).load(song.getImg()).placeholder(R.drawable.ic_logo).into(holder.imgThumb);

        if (mIOnClickSongItem != null) {
            holder.rlItem.setOnClickListener(view -> mIOnClickSongItem.onClickSongItem(song));
        }
        if (mIOnItemDelete != null) {
            holder.imgDelete.setOnClickListener(view -> {
                mListSong.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                mIOnItemDelete.onItemDeleted();
            });
        }

        holder.imgMenu.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                dragListener.requestDrag(holder);
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        if (mListSong != null) {
            return mListSong.size();
        }
        return 0;
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {

        SwipeRevealLayout swrLayout;
        RelativeLayout rlLayout, rlItem;
        ImageView imgThumb, imgMenu, imgDelete;
        TextView tvSongName, tvSingerName;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            swrLayout = itemView.findViewById(R.id.swr_layout);
            rlLayout = itemView.findViewById(R.id.rl_layout);
            rlItem = itemView.findViewById(R.id.rl_item_song);
            imgThumb = itemView.findViewById(R.id.img_thumb);
            imgMenu = itemView.findViewById(R.id.img_menu_song);
            imgDelete = itemView.findViewById(R.id.img_delete);
            tvSongName = itemView.findViewById(R.id.tv_song_name);
            tvSingerName = itemView.findViewById(R.id.tv_singer_name);
        }
    }
}
