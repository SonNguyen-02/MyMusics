package com.snnc_993.mymusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.snnc_993.mymusic.R;
import com.snnc_993.mymusic.activity.MainActivity;
import com.snnc_993.mymusic.fragment.BannerFragment;
import com.snnc_993.mymusic.fragment.MainFragment;
import com.snnc_993.mymusic.model.RowCardModel;

import java.util.ArrayList;
import java.util.List;

public class HomePageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private List<RowCardModel> rowCardModelList;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public HomePageAdapter(Context context) {
        this.context = context;
        this.rowCardModelList = new ArrayList<>();
    }

    public void setRowCardModelList(List<RowCardModel> rowCardModelList) {
        this.rowCardModelList = rowCardModelList;
        notifyDataSetChanged();
    }

    public void addRowCarModel(RowCardModel rowCardModel) {
        rowCardModelList.add(rowCardModel);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_card, parent, false);
            return new RowCardViewHolder(view);
        }
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_home, parent, false);
            return new HeaderViewHolder(view);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        if (holder instanceof HeaderViewHolder) {
//            if(context instanceof MainActivity){
//                ((MainActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_banner, new BannerFragment()).commit();
//            }
//        }
        if (holder instanceof RowCardViewHolder) {
            // Tru 1 vi thg header k co trong list
            RowCardModel rowCardModel = rowCardModelList.get(position - 1);
            if (rowCardModel == null) {
                return;
            }
            final RowCardViewHolder rowCardViewHolder = (RowCardViewHolder) holder;

            rowCardViewHolder.title.setText(rowCardModel.getTitle());

            CardAdapter adapter = new CardAdapter(context, rowCardModel.getCardModelList(), true);
            rowCardViewHolder.rcvRandCard.setAdapter(adapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
            rowCardViewHolder.rcvRandCard.setLayoutManager(layoutManager);

            // Set ưu tiên vuốt ngang cho recycle bị lồng
            rowCardViewHolder.rcvRandCard.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                private float preX = 0f;
                private float preY = 0f;
                @Override
                public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                    int y_BUFFER = 10;
                    switch (e.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            rv.getParent().requestDisallowInterceptTouchEvent(true);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            // check vuốt sang trái và đang ở last item của recycleView => active parent scroll
                            if (e.getX() - preX < 0 && layoutManager.findLastCompletelyVisibleItemPosition() == rowCardModel.getCardModelList().size() - 1) {
                                rv.getParent().requestDisallowInterceptTouchEvent(false);
                                break;
                            }
                            // check vuốt sang phải và đang ở first item của recycleView => active parent scroll
                            if (e.getX() - preX > 0 && layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                                rv.getParent().requestDisallowInterceptTouchEvent(false);
                                break;
                            }
                            // check vuốt sang => block parent scroll
                            if (Math.abs(e.getX() - preX) > Math.abs(e.getY() - preY)) {
                                rv.getParent().requestDisallowInterceptTouchEvent(true);
                            // check vuốt dọc => active parent scroll
                            } else if (Math.abs(e.getY() - preY) > y_BUFFER) {
                                rv.getParent().requestDisallowInterceptTouchEvent(false);
                            }
                    }
                    preX = e.getX();
                    preY = e.getY();
                    return false;
                }

                @Override
                public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                }

                @Override
                public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (rowCardModelList != null) { // vi co header nen phai cong 1
            return rowCardModelList.size() + 1;
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static class RowCardViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        RecyclerView rcvRandCard;

        public RowCardViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title_row_card);
            rcvRandCard = itemView.findViewById(R.id.rcv_card);
        }

    }
}
