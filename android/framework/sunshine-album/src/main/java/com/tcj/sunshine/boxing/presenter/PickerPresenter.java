/*
 *  Copyright (C) 2017 Bilibili
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tcj.sunshine.boxing.presenter;

import android.content.ContentResolver;
import android.text.TextUtils;

import com.tcj.sunshine.boxing.model.BoxingManager;
import com.tcj.sunshine.boxing.model.callback.IAlbumTaskCallback;
import com.tcj.sunshine.boxing.model.callback.IAudioTaskCallback;
import com.tcj.sunshine.boxing.model.callback.IMediaTaskCallback;
import com.tcj.sunshine.boxing.model.callback.IVideoTaskCallback;
import com.tcj.sunshine.boxing.model.entity.AlbumEntity;
import com.tcj.sunshine.boxing.model.entity.BaseMedia;
import com.tcj.sunshine.boxing.model.entity.impl.ImageMedia;
import com.tcj.sunshine.boxing.model.task.IAudioTask;
import com.tcj.sunshine.boxing.model.task.IMediaTask;
import com.tcj.sunshine.boxing.model.task.IVideoTask;
import com.tcj.sunshine.view.dialog.LoadingDialog;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A presenter implement {@link PickerContract.Presenter}.
 *
 * @author ChenSL
 */
public class PickerPresenter implements PickerContract.Presenter {
    private PickerContract.View mTasksView;

    private int mTotalPage;
    private int mCurrentPage;
    private boolean mIsLoadingNextPage;

    private String mCurrentAlbumId;
    private LoadMediaCallback mLoadMediaCallback;
    private LoadAudioCallback mLoadAudioCallback;
    private LoadVideoCallback mLoadVideoCallback;
    private LoadAlbumCallback mLoadAlbumCallback;


    public PickerPresenter(PickerContract.View tasksView) {
        this.mTasksView = tasksView;
        this.mTasksView.setPresenter(this);
        this.mLoadMediaCallback = new LoadMediaCallback(this);
        this.mLoadAudioCallback = new LoadAudioCallback(this);
        this.mLoadVideoCallback = new LoadVideoCallback(this);
        this.mLoadAlbumCallback = new LoadAlbumCallback(this);
    }

    @Override
    public void loadMedias(int page, String albumId) {
        mCurrentAlbumId = albumId;
        if (page == 0) {
            mTasksView.clearMedia();
            mCurrentPage = 0;
        }
        ContentResolver cr = mTasksView.getAppCr();
        BoxingManager.getInstance().loadMedia(cr, page, albumId, mLoadMediaCallback);
    }

    @Override
    public void loadVideos(int page, String albumId) {
        mCurrentAlbumId = albumId;
        if (page == 0) {
            mTasksView.clearMedia();
            mCurrentPage = 0;
        }
        ContentResolver cr = mTasksView.getAppCr();
        BoxingManager.getInstance().loadVideo(cr, page, albumId, mLoadVideoCallback);
    }

    @Override
    public void loadAudios(int page, String albumId) {
        mCurrentAlbumId = albumId;
        if (page == 0) {
            mTasksView.clearMedia();
            mCurrentPage = 0;
        }
        ContentResolver cr = mTasksView.getAppCr();
        BoxingManager.getInstance().loadAudio(cr, page, albumId, mLoadAudioCallback);
    }

    @Override
    public void loadAlbums() {
        ContentResolver cr = mTasksView.getAppCr();
        BoxingManager.getInstance().loadAlbum(cr, mLoadAlbumCallback);
    }

    @Override
    public void destroy() {
        mTasksView = null;
    }

    @Override
    public boolean hasNextPage() {
        return mCurrentPage < mTotalPage;
    }

    @Override
    public boolean canLoadNextPage() {
        return !mIsLoadingNextPage;
    }

    @Override
    public void onLoadNextPage() {
        mCurrentPage++;
        mIsLoadingNextPage = true;
        loadMedias(mCurrentPage, mCurrentAlbumId);
    }

    @Override
    public void checkSelectedMedia(List<BaseMedia> allMedias, List<BaseMedia> selectedMedias) {
        if (allMedias == null || allMedias.size() == 0) {
            return;
        }
        Map<String, ImageMedia> map = new HashMap<>(allMedias.size());
        for (BaseMedia allMedia : allMedias) {
            if (!(allMedia instanceof ImageMedia)) {
                return;
            }
            ImageMedia media = (ImageMedia) allMedia;
            media.setSelected(false);
            map.put(media.getPath(), media);
        }
        if (selectedMedias == null || selectedMedias.size() < 0) {
            return;
        }
        for (BaseMedia media : selectedMedias) {
            if (map.containsKey(media.getPath())) {
                map.get(media.getPath()).setSelected(true);
            }
        }
    }

    private static class LoadMediaCallback implements IMediaTaskCallback<BaseMedia> {
        private WeakReference<PickerPresenter> mWr;

        LoadMediaCallback(PickerPresenter presenter) {
            mWr = new WeakReference<>(presenter);
        }

        private PickerPresenter getPresenter() {
            return mWr.get();
        }

        @Override
        public void postMedia(List<BaseMedia> medias, int count) {
            PickerPresenter presenter = getPresenter();
            if (presenter == null) {
                return;
            }
            PickerContract.View view = presenter.mTasksView;
            if (view != null) {
                view.showMedia(medias, count);
            }
            presenter.mTotalPage = count / IMediaTask.PAGE_LIMIT;
            presenter.mIsLoadingNextPage = false;
        }

        @Override
        public boolean needFilter(String path) {
            return TextUtils.isEmpty(path) || !(new File(path).exists());
        }
    }

    private static class LoadVideoCallback implements IVideoTaskCallback<BaseMedia> {
        private WeakReference<PickerPresenter> mWr;

        LoadVideoCallback(PickerPresenter presenter) {
            mWr = new WeakReference<>(presenter);
        }

        private PickerPresenter getPresenter() {
            return mWr.get();
        }

        @Override
        public void postMedia(List<BaseMedia> medias, int count) {
            PickerPresenter presenter = getPresenter();
            if (presenter == null) {
                return;
            }
            PickerContract.View view = presenter.mTasksView;
            if (view != null) {
                view.showVideo(medias, count);
            }
            presenter.mTotalPage = count / IAudioTask.PAGE_LIMIT;
            presenter.mIsLoadingNextPage = false;
        }

        @Override
        public boolean needFilter(String path) {
            return TextUtils.isEmpty(path) || !(new File(path).exists());
        }
    }


    private static class LoadAudioCallback implements IAudioTaskCallback<BaseMedia> {
        private WeakReference<PickerPresenter> mWr;

        LoadAudioCallback(PickerPresenter presenter) {
            mWr = new WeakReference<>(presenter);
        }

        private PickerPresenter getPresenter() {
            return mWr.get();
        }

        @Override
        public void postMedia(List<BaseMedia> medias, int count) {
            PickerPresenter presenter = getPresenter();
            if (presenter == null) {
                return;
            }
            PickerContract.View view = presenter.mTasksView;
            if (view != null) {
                view.showAudio(medias, count);
            }
            presenter.mTotalPage = count / IAudioTask.PAGE_LIMIT;
            presenter.mIsLoadingNextPage = false;
        }

        @Override
        public boolean needFilter(String path) {
            return TextUtils.isEmpty(path) || !(new File(path).exists());
        }
    }


    private static class LoadAlbumCallback implements IAlbumTaskCallback {
        private WeakReference<PickerPresenter> mWr;

        LoadAlbumCallback(PickerPresenter presenter) {
            mWr = new WeakReference<>(presenter);
        }

        private PickerPresenter getPresenter() {
            return mWr.get();
        }

        @Override
        public void postAlbumList(List<AlbumEntity> list) {
            PickerPresenter presenter = getPresenter();
            if (presenter == null) {
                return;
            }
            if (presenter.mTasksView != null) {
                presenter.mTasksView.showAlbum(list);
            }
        }
    }

}
