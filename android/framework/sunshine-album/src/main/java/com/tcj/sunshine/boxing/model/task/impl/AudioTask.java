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
import android.provider.MediaStore;

import com.tcj.sunshine.boxing.model.callback.IAudioTaskCallback;
import com.tcj.sunshine.boxing.model.entity.impl.AudioMedia;
import com.tcj.sunshine.boxing.model.task.IAudioTask;
import com.tcj.sunshine.boxing.model.task.IMediaTask;
import com.tcj.sunshine.boxing.utils.BoxingExecutor;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

/**
 * A Task to load {@link AudioMedia} in database.
 *
 * @author ChenSL
 */
@WorkerThread
public class AudioTask implements IAudioTask<AudioMedia> {

    private final static String[] MEDIA_COL = new String[]{
            MediaStore.Audio.Media.DATA,                    //歌曲文件的路径
            MediaStore.Audio.Media._ID,                     //歌曲ID
            MediaStore.Audio.Media.TITLE,                   //歌曲的名称
            MediaStore.Audio.Media.ALBUM,                   //歌曲的专辑名
            MediaStore.Audio.Media.ARTIST,                  //歌曲的歌手名
            MediaStore.Audio.Media.MIME_TYPE,               //歌曲文件的类型
            MediaStore.Audio.Media.SIZE,                    //歌曲文件的大小
            MediaStore.Audio.Media.DURATION,                //歌曲的总播放时长
    };


    @Override
    public void load(final ContentResolver cr, final int page, String id, final IAudioTaskCallback<AudioMedia> callback) {
        loadVideos(cr, page, callback);
    }

    private void loadVideos(ContentResolver cr, int page, @NonNull final IAudioTaskCallback<AudioMedia> callback) {
        final List<AudioMedia> audioMedias = new ArrayList<>();
        final Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MEDIA_COL, null, null,
                MediaStore.Images.Media.DATE_MODIFIED + " desc" + " LIMIT " + page * IMediaTask.PAGE_LIMIT + " , " + IMediaTask.PAGE_LIMIT);
        try {
            int count = 0;
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getCount();
                do {
                    String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    String id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                    String type = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE));
                    String size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                    String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    AudioMedia audio = new AudioMedia.Builder(id, data)
                            .setTitle(title)
                            .setDuration(duration)
                            .setSize(size)
                            .setArtist(artist)
                            .setAlbum(album)
                            .setMimeType(type).build();
                    audioMedias.add(audio);

                } while (!cursor.isLast() && cursor.moveToNext());
                postMedias(callback, audioMedias, count);
            } else {
                postMedias(callback, audioMedias, 0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    private void postMedias(@NonNull final IAudioTaskCallback<AudioMedia> callback, final List<AudioMedia> audioMedias, final int count) {
        BoxingExecutor.getInstance().runUI(new Runnable() {
            @Override
            public void run() {
                callback.postMedia(audioMedias, count);
            }
        });
    }

}
