package com.tcj.sunshine.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tcj.sunshine.album.R;
import com.tcj.sunshine.boxing.model.BoxingManager;
import com.tcj.sunshine.boxing.model.config.BoxingConfig;
import com.tcj.sunshine.boxing.model.entity.BaseMedia;
import com.tcj.sunshine.boxing.model.entity.impl.VideoMedia;
import com.tcj.sunshine.tools.ScreenUtils;
import com.tcj.sunshine.tools.StringUtils;
import com.tcj.sunshine.view.common.BlibliIBoxingMediaLoader;

import java.util.List;

/**
 * Created by Stefan Lau on 2019/11/21.
 */
public class AlbumAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<BaseMedia> list;
    private OnAlbumListener listener;
    private BlibliIBoxingMediaLoader mLoader;
    private int width = 0;
    private boolean showCheckView = true;

    private LayoutInflater mInflater;
    private BoxingConfig mMediaConfig;
    private int cameraResId = 0;

    private OnItemClickListener clickListener;

    public static final int CAMERA_TYPE = 0;
    public static final int NORMAL_TYPE = 1;

    public AlbumAdapter(Context context, List<BaseMedia> list, OnAlbumListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        this.mInflater = LayoutInflater.from(context);
        this.mLoader = new BlibliIBoxingMediaLoader();
        this.mMediaConfig = BoxingManager.getInstance().getBoxingConfig();
        this.cameraResId = mMediaConfig.getCameraRes();
        this.clickListener = new OnItemClickListener();
        this.showCheckView = mMediaConfig.getMode() != BoxingConfig.Mode.SINGLE_IMG;
        int gap = ScreenUtils.dip2px(5.0f);
        this.width = (ScreenUtils.getScreenWidth() - 5 * gap) / 4;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (CAMERA_TYPE == viewType) {
            return new CameraViewHolder(mInflater.inflate(R.layout.adapter_album_camera, parent, false));
        }
        return new AlbumViewHolder(mInflater.inflate(R.layout.adapter_album, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof CameraViewHolder) {
            //照相机
            CameraViewHolder holder = (CameraViewHolder) viewHolder;
            holder.mClickView.setOnClickListener(clickListener);

            if(cameraResId > 0){
                holder.mIvCamera.setImageResource(cameraResId);
            }else {
                holder.mIvCamera.setImageResource(R.mipmap.icon_album_camera);
            }

            holder.mClickView.setOnClickListener(clickListener);

        }else {
            AlbumViewHolder holder = (AlbumViewHolder)viewHolder;
            BaseMedia media = list.get(position);

            if(media.getType() == BaseMedia.TYPE.VIDEO) {
                //视频
                String path = media.getPath();
                this.mLoader.displayThumbnail(holder.mDraweeView, path, width, width);
                VideoMedia video = (VideoMedia) media;
                if(!StringUtils.isEmpty(video.getDuration())) {
                    holder.mTvTime.setText(video.getDuration());
                    holder.mTvTime.setVisibility(View.VISIBLE);
                }else {
                    holder.mTvTime.setVisibility(View.GONE);
                }
                holder.mIvPlayer.setVisibility(View.VISIBLE);

            }else {
                //图片

                String path = media.getPath();
                this.mLoader.displayThumbnail(holder.mDraweeView, path, width, width);

                holder.mIvPlayer.setVisibility(View.GONE);
                holder.mTvTime.setVisibility(View.GONE);
            }

            if(media.getIsSelected() == 1) {
                //选中了
                holder.mCheckBox.setChecked(true);
                holder.mTvNumber.setText(media.getNum());
                holder.mTvNumber.setVisibility(View.VISIBLE);
            }else {
                //没选中
                holder.mCheckBox.setChecked(false);
                holder.mTvNumber.setText("");
                holder.mTvNumber.setVisibility(View.GONE);
            }

            if(showCheckView) {
                holder.mCheckLayout.setVisibility(View.VISIBLE);
            }else {
                holder.mCheckLayout.setVisibility(View.GONE);
            }

            holder.mCheckLayout.setTag(R.id.tag1, position);
            holder.mCheckLayout.setTag(R.id.tag2, media);

            holder.mDraweeView.setTag(R.id.tag1, position);
            holder.mDraweeView.setTag(R.id.tag2, media);

            holder.mCheckLayout.setOnClickListener(clickListener);
            holder.mDraweeView.setOnClickListener(clickListener);
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

    private class AlbumViewHolder extends RecyclerView.ViewHolder {

        private ImageView mDraweeView;
        private ImageView mIvPlayer;
        private TextView mTvTime;
        private View mCheckLayout;
        private CheckBox mCheckBox;
        private TextView mTvNumber;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mDraweeView = itemView.findViewById(R.id.mDraweeView);
            this.mIvPlayer = itemView.findViewById(R.id.mIvPlayer);
            this.mTvTime = itemView.findViewById(R.id.mTvTime);
            this.mCheckLayout = itemView.findViewById(R.id.mCheckLayout);
            this.mCheckBox = itemView.findViewById(R.id.mCheckBox);
            this.mTvNumber = itemView.findViewById(R.id.mTvNumber);
        }
    }


    private class OnItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(listener == null)return;

            v.setEnabled(false);
            v.postDelayed(()-> v.setEnabled(true), 500);

            if (v.getId() == R.id.mDraweeView) {
                //点击查看大图
                int position = (int) v.getTag(R.id.tag1);
                BaseMedia media = (BaseMedia)v.getTag(R.id.tag2);
                listener.onClick(v, position, media);
            }else if(v.getId() == R.id.mCheckLayout) {
                //选中，或者取消
                int position = (int) v.getTag(R.id.tag1);
                BaseMedia media = (BaseMedia)v.getTag(R.id.tag2);
                listener.onChecked(v, position, media);
            }else if(v.getId() == R.id.mClickView) {
                //拍照
                listener.camera();
            }
        }
    }

    public interface OnAlbumListener {

        /**
         * 调用摄像头拍照
         */
        public void camera();

        /**选中取消
         * @param position
         */
        public void onChecked(View view, int position, BaseMedia media);

        /**
         * 点击
         * @param position
         */
        public void onClick(View view, int position, BaseMedia media);
    }

}
