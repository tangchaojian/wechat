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

package com.tcj.sunshine.boxing.model.task.impl;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore.Images;
import android.text.TextUtils;

import com.tcj.sunshine.boxing.model.BoxingManager;
import com.tcj.sunshine.boxing.model.callback.IMediaTaskCallback;
import com.tcj.sunshine.boxing.model.config.BoxingConfig;
import com.tcj.sunshine.boxing.model.entity.impl.ImageMedia;
import com.tcj.sunshine.boxing.model.task.IMediaTask;
import com.tcj.sunshine.boxing.utils.BoxingExecutor;
import com.tcj.sunshine.boxing.utils.BoxingLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.collection.ArrayMap;

/**
 * A Task to load photos.
 *
 * @author ChenSL
 */
@WorkerThread
public class ImageTask implements IMediaTask<ImageMedia> {
    private static final String CONJUNCTION_SQL = "=? or";
    private static final String SELECTION_IMAGE_MIME_TYPE = Images.Media.MIME_TYPE + CONJUNCTION_SQL + " " + Images.Media.MIME_TYPE + CONJUNCTION_SQL + " " + Images.Media.MIME_TYPE + CONJUNCTION_SQL + " " + Images.Media.MIME_TYPE + "=?";
    private static final String SELECTION_IMAGE_MIME_TYPE_WITHOUT_GIF = Images.Media.MIME_TYPE + CONJUNCTION_SQL + " " + Images.Media.MIME_TYPE + CONJUNCTION_SQL + " " + Images.Media.MIME_TYPE + "=?";
    private static final String SELECTION_ID = Images.Media.BUCKET_ID + "=? and (" + SELECTION_IMAGE_MIME_TYPE + " )";
    private static final String SELECTION_ID_WITHOUT_GIF = Images.Media.BUCKET_ID + "=? and (" + SELECTION_IMAGE_MIME_TYPE_WITHOUT_GIF + " )";

    private static final String IMAGE_JPEG = "image/jpeg";
    private static final String IMAGE_PNG = "image/png";
    private static final String IMAGE_JPG = "image/jpg";
    private static final String IMAGE_GIF = "image/gif";
    private static final String[] SELECTION_ARGS_IMAGE_MIME_TYPE = {IMAGE_JPEG, IMAGE_PNG, IMAGE_JPG, IMAGE_GIF};
    private static final String[] SELECTION_ARGS_IMAGE_MIME_TYPE_WITHOUT_GIF = {IMAGE_JPEG, IMAGE_PNG, IMAGE_JPG};

    private static final String DESC = " desc";

    private BoxingConfig mPickerConfig;
    private Map<String, String> mThumbnailMap;

    public ImageTask() {
        this.mThumbnailMap = new ArrayMap<>();
        this.mPickerConfig = BoxingManager.getInstance().getBoxingConfig();
    }

    @Override
    public void load(@NonNull final ContentResolver cr, final int page, final String id,
                     @NonNull final IMediaTaskCallback<ImageMedia> callback) {
        buildThumbnail(cr);
        buildAlbumList(cr, id, page, callback);
    }

    private void buildThumbnail(ContentResolver cr) {
        String[] projection = {Images.Thumbnails.IMAGE_ID, Images.Thumbnails.DATA};
        queryThumbnails(cr, projection);
    }

    private void queryThumbnails(ContentResolver cr, String[] projection) {
        Cursor cur = null;
        try {
            cur = Images.Thumbnails.queryMiniThumbnails(cr, Images.Thumbnails.EXTERNAL_CONTENT_URI,
                    Images.Thumbnails.MINI_KIND, projection);
            if (cur != null && cur.moveToFirst()) {
                do {
                    String imageId = cur.getString(cur.getColumnIndex(Images.Thumbnails.IMAGE_ID));
                    String imagePath = cur.getString(cur.getColumnIndex(Images.Thumbnails.DATA));
                    mThumbnailMap.put(imageId, imagePath);
                } while (cur.moveToNext() && !cur.isLast());
            }
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
    }

    private List<ImageMedia> buildAlbumList(ContentResolver cr, String bucketId, int page,
                                            @NonNull final IMediaTaskCallback<ImageMedia> callback) {
        List<ImageMedia> result = new ArrayList<>();
        String columns[] = getColumns();
        Cursor cursor = null;
        try {
            boolean isDefaultAlbum = TextUtils.isEmpty(bucketId);
            boolean isNeedPaging = mPickerConfig == null || mPickerConfig.isNeedPaging();
            boolean isNeedGif = mPickerConfig != null && mPickerConfig.isNeedGif();
            int totalCount = getTotalCount(cr, bucketId, columns, isDefaultAlbum, isNeedGif);

            String imageMimeType = isNeedGif ? SELECTION_IMAGE_MIME_TYPE : SELECTION_IMAGE_MIME_TYPE_WITHOUT_GIF;
            String[] args = isNeedGif ? SELECTION_ARGS_IMAGE_MIME_TYPE : SELECTION_ARGS_IMAGE_MIME_TYPE_WITHOUT_GIF;
            String order = isNeedPaging ? Images.Media.DATE_MODIFIED + DESC + " LIMIT "
                    + page * IMediaTask.PAGE_LIMIT + " , " + IMediaTask.PAGE_LIMIT : Images.Media.DATE_MODIFIED + DESC;
            String selectionId = isNeedGif ? SELECTION_ID : SELECTION_ID_WITHOUT_GIF;
            cursor = query(cr, bucketId, columns, isDefaultAlbum, isNeedGif, imageMimeType, args, order, selectionId);
            addItem(totalCount, result, cursor, callback);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    private void addItem(final int allCount, final List<ImageMedia> result, Cursor cursor, @NonNull final IMediaTaskCallback<ImageMedia> callback) {
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String picPath = cursor.getString(cursor.getColumnIndex(Images.Media.DATA));
                if (callback.needFilter(picPath)) {
                    BoxingLog.d("path:" + picPath + " has been filter");
                } else {
                    String id = cursor.getString(cursor.getColumnIndex(Images.Media._ID));
                    String size = cursor.getString(cursor.getColumnIndex(Images.Media.SIZE));
                    String mimeType = cursor.getString(cursor.getColumnIndex(Images.Media.MIME_TYPE));
                    int width = 0;
                    int height = 0;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        width = cursor.getInt(cursor.getColumnIndex(Images.Media.WIDTH));
                        height = cursor.getInt(cursor.getColumnIndex(Images.Media.HEIGHT));
                    }

                    //更新时间
                    String mDateModified = cursor.getString(cursor.getColumnIndex(Images.Media.DATE_MODIFIED));

                    ImageMedia imageItem = new ImageMedia.Builder(id, picPath).setThumbnailPath(mThumbnailMap.get(id))
                            .setSize(size).setMimeType(mimeType).setHeight(height).setWidth(width).setDateModified(mDateModified).build();
                    if (!result.contains(imageItem)) {
                        result.add(imageItem);
                    }
                }
            } while (!cursor.isLast() && cursor.moveToNext());
            postMedias(result, allCount, callback);
        } else {
            postMedias(result, 0, callback);
        }
        clear();
    }

    private void postMedias(final List<ImageMedia> result, final int count, @NonNull final IMediaTaskCallback<ImageMedia> callback) {
        BoxingExecutor.getInstance().runUI(new Runnable() {
            @Override
            public void run() {
                callback.postMedia(result, count);
            }
        });
    }

    private Cursor query(ContentResolver cr, String bucketId, String[] columns, boolean isDefaultAlbum,
                         boolean isNeedGif, String imageMimeType, String[] args, String order, String selectionId) {
        Cursor resultCursor;
        if (isDefaultAlbum) {
            resultCursor = cr.query(Images.Media.EXTERNAL_CONTENT_URI, columns, imageMimeType,
                    args, order);
        } else {
            if (isNeedGif) {
                resultCursor = cr.query(Images.Media.EXTERNAL_CONTENT_URI, columns, selectionId,
                        new String[]{bucketId, args[0], args[1], args[2], args[3]}, order);
            } else {
                resultCursor = cr.query(Images.Media.EXTERNAL_CONTENT_URI, columns, selectionId,
                        new String[]{bucketId, args[0], args[1], args[2]}, order);
            }
        }
        return resultCursor;
    }

    @NonNull
    private String[] getColumns() {
        String[] columns;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            columns = new String[]{Images.Media._ID, Images.Media.DATA, Images.Media.SIZE, Images.Media.MIME_TYPE, Images.Media.WIDTH, Images.Media.HEIGHT, Images.Media.DATE_MODIFIED};
        } else {
            columns = new String[]{Images.Media._ID, Images.Media.DATA, Images.Media.SIZE, Images.Media.MIME_TYPE, Images.Media.DATE_MODIFIED};
        }
        return columns;
    }

    private int getTotalCount(ContentResolver cr, String bucketId, String[] columns, boolean isDefaultAlbum, boolean isNeedGif) {
        Cursor allCursor = null;
        int result = 0;
        try {
            if (isDefaultAlbum) {
                allCursor = cr.query(Images.Media.EXTERNAL_CONTENT_URI, columns,
                        SELECTION_IMAGE_MIME_TYPE, SELECTION_ARGS_IMAGE_MIME_TYPE,
                        Images.Media.DATE_MODIFIED + DESC);
            } else {
                if (isNeedGif) {
                    allCursor = cr.query(Images.Media.EXTERNAL_CONTENT_URI, columns, SELECTION_ID,
                            new String[]{bucketId, IMAGE_JPEG, IMAGE_PNG, IMAGE_JPG, IMAGE_GIF}, Images.Media.DATE_MODIFIED + DESC);
                } else {
                    allCursor = cr.query(Images.Media.EXTERNAL_CONTENT_URI, columns, SELECTION_ID_WITHOUT_GIF,
                            new String[]{bucketId, IMAGE_JPEG, IMAGE_PNG, IMAGE_JPG}, Images.Media.DATE_MODIFIED + DESC);
                }
            }
            if (allCursor != null) {
                result = allCursor.getCount();
            }
        } finally {
            if (allCursor != null) {
                allCursor.close();
            }
        }
        return result;
    }

    private void clear() {
        if (mThumbnailMap != null) {
            mThumbnailMap.clear();
        }
    }

}
