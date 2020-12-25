package com.tcj.sunshine.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tcj.sunshine.album.R;
import com.tcj.sunshine.boxing.model.entity.AlbumEntity;
import com.tcj.sunshine.boxing.model.entity.BaseMedia;
import com.tcj.sunshine.tools.ScreenUtils;
import com.tcj.sunshine.tools.StringUtils;
import com.tcj.sunshine.view.common.BlibliIBoxingMediaLoader;

import java.util.List;

/**
 * 相册菜单适配器
 */
public class AlbumMenuAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<AlbumEntity> list;
    private OnAlbumMenuListener listener;
    private BlibliIBoxingMediaLoader mLoader;

    private LayoutInflater mInflater;

    private OnItemClickListener clickListener;
    private int width;

    public AlbumMenuAdapter(Context context, List<AlbumEntity> list, OnAlbumMenuListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        this.mInflater = LayoutInflater.from(context);
        this.mLoader = new BlibliIBoxingMediaLoader();
        this.clickListener = new OnItemClickListener();
        this.width = ScreenUtils.dip2px(50);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlbumMenuViewHolder(mInflater.inflate(R.layout.adapter_menu, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        AlbumMenuViewHolder holder = (AlbumMenuViewHolder)viewHolder;
        AlbumEntity item = list.get(position);
        if (item != null && item.hasImages()) {
            String menuName = item.mBucketName;
            if(StringUtils.isEmpty(menuName)) {
                menuName = "所有图片";
                item.mBucketName = menuName;
            }

            BaseMedia media = item.mImageList.get(0);
            holder.mTvName.setText(menuName + " (" + item.mCount + ")");

            if (media != null) {
                mLoader.displayThumbnail(holder.mIvImage, media.getPath(), width, width);
            }

            if(item.mIsSelected) {
                holder.mIvSelected.setVisibility(View.VISIBLE);
            }else {
                holder.mIvSelected.setVisibility(View.GONE);
            }

            holder.mClickView.setTag(R.id.tag1, position);
            holder.mClickView.setTag(R.id.tag2, item);
            holder.mClickView.setOnClickListener(clickListener);
        }
    }

    private class CameraViewHolder extends RecyclerView.ViewHolder {

        private View mClickView;
        private ImageView mIvCamera;

        public CameraViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mClickView = itemView.findViewById(R.id.mClickView);
            this.mIvCamera = itemView.findViewById(R.id.mIvCamera);
        }
    }

    private class AlbumMenuViewHolder extends RecyclerView.ViewHolder {

        private View mClickView;
        private ImageView mIvImage;
        private TextView mTvName;
        private ImageView mIvSelected;

        public AlbumMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mClickView = itemView.findViewById(R.id.mClickView);
            this.mIvImage = itemView.findViewById(R.id.mIvImage);
            this.mTvName = itemView.findViewById(R.id.mTvName);
            this.mIvSelected = itemView.findViewById(R.id.mIvSelected);
        }
    }


    private class OnItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(listener == null)return;

            v.setEnabled(false);
            v.postDelayed(()-> v.setEnabled(true), 500);

            int position = (int) v.getTag(R.id.tag1);
            AlbumEntity item = (AlbumEntity)v.getTag(R.id.tag2);

            if(listener != null) listener.onClick(v, position, item);
        }
    }

    public interface OnAlbumMenuListener {

        /**
         * 点击
         * @param position
         */
        public void onClick(View view, int position, AlbumEntity item);
    }

}
