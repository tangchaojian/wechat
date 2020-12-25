package com.tcj.sunshine.tools;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import com.tcj.sunshine.lib.BuildConfig;
import com.tcj.sunshine.lib.common.SunshineFileProvider;

import java.io.File;

/**
 * Uri工具类
 */
public final class UriUtils {

    private UriUtils() {}


    /**
     * 文件转uri
     * @param file
     * @return
     */
    public static Uri fileToUri(@NonNull File file) {
        String authority = BuildConfig.FILE_PROVIDER_NAME;
        return fileToUri(file, authority);
    }


    /**
     * 文件转uri
     * @param file      文件
     * @param authority FileProvider的authority
     */
    public static Uri fileToUri(@NonNull final File file, String authority) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return SunshineFileProvider.getUriForFile(ContextUtils.getContext(), authority, file);
        } else {
            return Uri.fromFile(file);
        }
    }

    /**
     * uri转File
     */
    public static File uriToFile(@NonNull final Uri uri) {
        String authority = uri.getAuthority();
        String scheme = uri.getScheme();
        Context context = ContextUtils.getContext();
        if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            String path = uri.getPath();
            if (path != null) return new File(path);
            return null;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && DocumentsContract.isDocumentUri(context, uri)) {
            if ("com.android.externalstorage.documents".equals(authority)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return new File(Environment.getExternalStorageDirectory() + "/" + split[1]);
                }
                return null;
            } else if ("com.android.providers.downloads.documents".equals(authority)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id)
                );
                return getFileFromUri(contentUri, 2);
            } else if ("com.android.providers.media.documents".equals(authority)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                } else {
                    return null;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getFileFromUri(contentUri, selection, selectionArgs, 4);
            } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
                return getFileFromUri(uri, 5);
            } else {
                return null;
            }
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            return getFileFromUri(uri, 7);
        } else {
            return null;
        }
    }

    private static File getFileFromUri(final Uri uri, final int code) {
        return getFileFromUri(uri, null, null, code);
    }

    private static File getFileFromUri(final Uri uri, final String selection, final String[] selectionArgs, final int code) {

        Context context = ContextUtils.getContext();
        final Cursor cursor = context.getContentResolver().query(uri,
                new String[]{"_data"},
                selection,
                selectionArgs,
                null);

        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                final int columnIndex = cursor.getColumnIndex("_data");
                if (columnIndex > -1) {
                    return new File(cursor.getString(columnIndex));
                } else {
                    Log.d("UriUtils", uri.toString() + " parse failed(columnIndex: " + columnIndex + " is wrong). -> " + code);
                    return null;
                }
            } else {
                Log.d("UriUtils", uri.toString() + " parse failed(moveToFirst return false). -> " + code);
                return null;
            }
        } catch (Exception e) {
            return null;
        } finally {
            cursor.close();
        }
    }
}
