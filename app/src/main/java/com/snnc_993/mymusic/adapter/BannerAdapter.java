package com.snnc_993.mymusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.snnc_993.mymusic.R;
import com.snnc_993.mymusic.model.AdsModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BannerAdapter extends PagerAdapter {

    Context context;
    List<AdsModel> mAdsModelList;

    public BannerAdapter(Context context, List<AdsModel> mAdsModelList) {
        this.context = context;
        this.mAdsModelList = mAdsModelList;
    }

//    @NonNull
//    @Override
//    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_banner, parent, false);
//        Log.e("ddd", "onCreateViewHolder: " + view);
//        return new BannerViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
//        AdsModel ads = ads;
//        Log.e("ddd", "onBindVH");
//        Picasso.with(context).load(ads.getAdsImg()).into(holder.imgBackground);
//        Picasso.with(context).load(ads.getSongImg()).into(holder.imgSongIcon);
//        holder.titleBanner.setText(ads.getSongName());
//        holder.contentBanner.setText(ads.getAdsContent());
//    }
//
//    @Override
//    public int getItemCount() {
//        if (mAdsModelList != null) {
//            return mAdsModelList.size();
//        }
//        return 0;
//    }
//
//    public static class BannerViewHolder extends RecyclerView.ViewHolder {
//
//        ImageView imgBackground;
//        ImageView imgSongIcon;
//        TextView titleBanner;
//        TextView contentBanner;
//
//        public BannerViewHolder(@NonNull View itemView) {
//            super(itemView);
//            Log.e("ddd", "Banner VH construct");
//            imgBackground = itemView.findViewById(R.id.iv_bg_banner);
//            imgSongIcon = itemView.findViewById(R.id.iv_ic_banner);
//            titleBanner = itemView.findViewById(R.id.tv_title_banner);
//            contentBanner = itemView.findViewById(R.id.tv_content_banner);
//        }
//    }

    @Override
    public int getCount() {
        if (mAdsModelList != null) {
            return mAdsModelList.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_banner, container, false);

        ImageView imgBackground = view.findViewById(R.id.img_bg_banner);
        ImageView imgSongIcon = view.findViewById(R.id.img_ic_banner);
        TextView titleBanner = view.findViewById(R.id.tv_title_banner);
        TextView contentBanner = view.findViewById(R.id.tv_content_banner);

        AdsModel ads = mAdsModelList.get(position);
        Picasso.with(context).load(ads.getAdsImg()).into(imgBackground);
        Picasso.with(context).load(ads.getSongImg()).into(imgSongIcon);
        titleBanner.setText(ads.getSongName());
        contentBanner.setText(ads.getAdsContent());

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
