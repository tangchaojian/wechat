package com.tcj.sunshine.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tcj.sunshine.album.R;
import com.tcj.sunshine.boxing.model.entity.BaseMedia;
import com.tcj.sunshine.boxing.model.entity.impl.VideoMedia;
import com.tcj.sunshine.player.VideoPlayer;
import com.tcj.sunshine.tools.GlideUtils;
import com.tcj.sunshine.tools.LogUtils;
import com.tcj.sunshine.tools.ScreenUtils;
import com.tcj.sunshine.tools.StringUtils;
import com.tcj.sunshine.ui.imageview.UIZoomImageView;
import com.tcj.sunshine.ui.imageview.attacher.AlbumViewAttacher;

/**
 * 相册图片/视频预览fragment
 */
public class BoxingPreviewFragment extends Fragment {

    private View view;
    private UIZoomImageView mZoomImageView;
    private VideoPlayer mVideoPlayer;

    private BaseMedia media;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Bundle args = this.getArguments();
        if(args != null){
            this.media = args.getParcelable("media");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.view = inflater.inflate(R.layout.fragment_boxing_preview, container, false);
        this.initUI();
        return this.view;
    }

    private void initUI(){
        this.mZoomImageView = this.view.findViewById(R.id.mZoomImageView);
        this.mVideoPlayer = this.view.findViewById(R.id.mVideoPlayer);

        if(media.getType() == BaseMedia.TYPE.VIDEO) {
            //视频
            this.mZoomImageView.setVisibility(View.GONE);
            this.mVideoPlayer.setVisibility(View.VISIBLE);
            String path = media.getPath();
            this.mVideoPlayer.setPath(path);
            this.mVideoPlayer.setLooping(false);
            this.mVideoPlayer.setAutoPlay(false);
            this.mVideoPlayer.setFullScreenIconVisible(false);
            this.mVideoPlayer.setPlayIcon(R.mipmap.icon_video_player_play_circle);
            int width = ScreenUtils.dip2px(30f);
            this.mVideoPlayer.setHandleIconSize(width, width);
            this.mVideoPlayer.start();

        }else {
            //图片
            this.mVideoPlayer.release();
            this.mZoomImageView.setVisibility(View.VISIBLE);
            this.mVideoPlayer.setVisibility(View.GONE);
            String path = "file://" + media.getPath();
            GlideUtils.loadImage(path, mZoomImageView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(media.getType() == BaseMedia.TYPE.VIDEO) {
            //视频
            if(mVideoPlayer != null){
                mVideoPlayer.stop();
            }
        }else {
            //图片
            if (mZoomImageView != null) {
                mZoomImageView.zoomTo(AlbumViewAttacher.DEFAULT_MIN_SCALE, 0, 0);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.i("sunshine-album", "onStop");

    }
}
