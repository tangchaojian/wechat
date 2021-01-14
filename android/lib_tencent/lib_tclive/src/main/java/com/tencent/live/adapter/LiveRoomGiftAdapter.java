package com.tencent.live.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tcj.sunshine.tools.GlideUtils;
import com.tcj.sunshine.ui.viewgroup.TapRelativeLayout;
import com.tencent.live.R;
import com.tencent.live.entity.LiveGiftEntity;
import com.tencent.live.listener.OnItemClickListener;

import java.util.List;

public class LiveRoomGiftAdapter extends BaseAdapter {

    private Context context;
    private List<LiveGiftEntity> list;
    private OnItemClickListener<LiveGiftEntity> listener;
    private LayoutInflater inflater;

    public LiveRoomGiftAdapter(Context context, List<LiveGiftEntity> list, OnItemClickListener<LiveGiftEntity> listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LiveGiftEntity item = list.get(position);
        ViewHolder holder;
        if(convertView == null) {
            View itemView = this.inflater.inflate(R.layout.adapter_live_room_gift, null);
            holder = new ViewHolder();
            holder.mClickView = itemView.findViewById(R.id.rl_item_click);
            holder.mIvIcon = itemView.findViewById(R.id.iv_icon);
            holder.mTvName = itemView.findViewById(R.id.tv_name);
            holder.mTvGiftPrice = itemView.findViewById(R.id.tv_gift_price);
            itemView.setTag(holder);
            convertView = itemView;
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        GlideUtils.loadImage(item.getGiftImgUrl(), holder.mIvIcon);
        holder.mTvName.setText(item.getGiftName());
        holder.mTvGiftPrice.setText(item.getGiftPrice());

        holder.mClickView.setOnTapListener(new TapRelativeLayout.OnTapListener() {
            @Override
            public void onTap() {

            }

            @Override
            public void onDoubleTap() {
                if(listener != null) {
                    listener.onItemClick(holder.mClickView, position, item);
                }
            }
        });


        return convertView;
    }

    private static class ViewHolder {
        TapRelativeLayout mClickView;
        ImageView mIvIcon;
        TextView mTvName;
        TextView mTvGiftPrice;
    }

}
