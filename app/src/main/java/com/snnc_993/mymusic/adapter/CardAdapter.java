package com.snnc_993.mymusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.snnc_993.mymusic.R;
import com.snnc_993.mymusic.activity.ISendDataToDetail;
import com.snnc_993.mymusic.activity.MainActivity;
import com.snnc_993.mymusic.model.AlbumModel;
import com.snnc_993.mymusic.model.CardModel;
import com.snnc_993.mymusic.model.SongModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardItemViewHolder> {

    private final Context context;
    private final List<CardModel> cardModelList;
    private final boolean isLinearLayoutManager;
    private final ISendDataToDetail mISendDataToDetail;

    public CardAdapter(Context context, List<CardModel> cardModelList, boolean isLinearLayoutManager) {
        this.context = context;
        this.cardModelList = cardModelList;
        this.isLinearLayoutManager = isLinearLayoutManager;
        mISendDataToDetail = (ISendDataToDetail) context;
    }

    @NonNull
    @Override
    public CardItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new CardItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardItemViewHolder holder, int position) {
        CardModel card = cardModelList.get(position);
        if (card == null) {
            return;
        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.itemCard.getLayoutParams();
        int margin = (int) context.getResources().getDimension(R.dimen.space_view);

        if (isLinearLayoutManager) {
            if (position == 0) {
                // margin left cho item đầu tiên
                layoutParams.leftMargin = margin;
            }
        } else {
            // margin top cho item
            layoutParams.topMargin = margin;
            // Độ dài tối đa của 1 cell in grid layout
            int itemLength = (MainActivity.WIDTH_DEVICE - (margin * (1 + MainActivity.TOTAL_ITEM_CARD))) / MainActivity.TOTAL_ITEM_CARD;
            layoutParams.width = itemLength;
            holder.imgThumb.getLayoutParams().height = itemLength;
        }
        // viết api trả data
        Picasso.with(context).load(card.getImg()).placeholder(R.drawable.ic_logo).into(holder.imgThumb);
        holder.titleCard.setText(card.getName());

        if (card instanceof AlbumModel) {
            holder.subTitleCard.setText(((AlbumModel) card).getSingerName());
            holder.subTitleCard.setVisibility(View.VISIBLE);
        }
        if (card instanceof SongModel) {
            holder.subTitleCard.setText(((SongModel) card).getSingerName());
            holder.subTitleCard.setVisibility(View.VISIBLE);
        }

        // set view click listener
        holder.itemCard.setOnClickListener(view -> {
            if(card instanceof SongModel){
                mISendDataToDetail.sendDataListener(card, ISendDataToDetail.Action.PLAY);
            }else{
                mISendDataToDetail.sendDataListener(card, ISendDataToDetail.Action.SHOW_MODAL);
            }
        });

        holder.btnPlayCard.setOnClickListener(view -> mISendDataToDetail.sendDataListener(card, ISendDataToDetail.Action.PLAY));
    }

    @Override
    public int getItemCount() {
        if (cardModelList != null) {
            return cardModelList.size();
        }
        return 0;
    }

    public static class CardItemViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout itemCard, btnPlayCard;
        ImageView imgThumb;
        TextView titleCard, subTitleCard;

        public CardItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCard = itemView.findViewById(R.id.rl_item_card);
            imgThumb = itemView.findViewById(R.id.img_thumb);
            btnPlayCard = itemView.findViewById(R.id.rl_play_card);
            titleCard = itemView.findViewById(R.id.tv_title_card);
            subTitleCard = itemView.findViewById(R.id.tv_sub_title_card);
        }
    }


}
