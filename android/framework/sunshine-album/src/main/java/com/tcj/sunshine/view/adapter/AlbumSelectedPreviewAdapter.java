package com.tcj.sunshine.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.tcj.sunshine.album.R;
import com.tcj.sunshine.boxing.model.entity.BaseMedia;
import com.tcj.sunshine.tools.ScreenUtils;
import com.tcj.sunshine.view.activity.BoxingPreviewActivity;
import com.tcj.sunshine.view.common.BlibliIBoxingMediaLoader;

import java.util.List;


/**
 *  相册预览选中图片适配器
 */
public class AlbumSelectedPreviewAdapter extends RecyclerView.Adapter<AlbumSelectedPreviewAdapter.AlbumSelectedViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private List<BaseMedia> list;
    private OnAlbumSelectedListener listener;
    private int type;
    private int width;
    private int height;

    private BlibliIBoxingMediaLoader mLoader;

    public AlbumSelectedPreviewAdapter(Context context, List<BaseMedia> list, OnAlbumSelectedListener listener, int type) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(this.context);
        this.listener = listener;
        this.type = type;
        this.width = this.height = ScreenUtils.dip2px(42);
        this.mLoader = new BlibliIBoxingMediaLoader();
    }


    @Override
    public int getItemCount() {
        return this.list.size();
    }

    @Override
    public void onBindViewHolder(AlbumSelectedViewHolder holder, int position) {
        BaseMedia entity = this.list.get(position);
        if(this.type == BoxingPreviewActivity.TYPE_PREVIEW_ALL){

            if(entity.isCurrent()){
                holder.mClickLayout.setBackgroundResource(R.drawable.shape_box_rect_selected_green);
            }else {
                holder.mClickLayout.setBackgroundResource(R.drawable.transparent);
            }

            holder.mUnableView.setVisibility(View.GONE);
        }else {
            if(entity.getIsSelected() == 1){
                holder.mUnableView.setVisibility(View.GONE);
                if(entity.isCurrent()){
                    holder.mClickLayout.setBackgroundResource(R.drawable.shape_box_rect_selected_green);
                }else {
                    holder.mClickLayout.setBackgroundResource(R.drawable.transparent);
                }
            }else {
                holder.mUnableView.setVisibility(View.VISIBLE);
                holder.mClickLayout.setBackgroundResource(R.drawable.transparent);
            }
        }

        String path = entity.getPath();
        this.mLoader.displayThumbnail(holder.mIvImage, path, width, width);


        holder.mClickLayout.setOnClickListener(new OnViewClickListner(position));
    }


    @Override
    public AlbumSelectedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.inflater.inflate(R.layout.adapter_album_selected_preview, parent, false);
        AlbumSelectedViewHolder holder = new AlbumSelectedViewHolder(view);
        return holder;
    }


    private class OnViewClickListner implements View.OnClickListener {

        private int position;

        public OnViewClickListner(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if(listener != null){
                listener.onSelected(position);
            }
        }
    }

    public interface OnAlbumSelectedListener {

        public void onSelected(int position);
    }

    public class AlbumSelectedViewHolder extends RecyclerView.ViewHolder {

        public View mClickLayout;
        public ImageView mIvImage;
        public View mUnableView;

        public AlbumSelectedViewHolder(View view) {
            super(view);

            this.mClickLayout = view.findViewById(R.id.mClickLayout);
            this.mIvImage = view.findViewById(R.id.mIvImage);
            this.mUnableView = view.findViewById(R.id.mUnableView);
        }
    }

}