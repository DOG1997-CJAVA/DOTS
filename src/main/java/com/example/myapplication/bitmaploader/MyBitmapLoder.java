package com.example.myapplication.bitmaploader;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import androidx.collection.LruCache;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class MyBitmapLoder {

    private static MyBitmapLoder mBitmapLoader;
    private LruCache<String, Bitmap> mCache;
    private DiskLruCache mDiskLruCache;
    private static final String TAG = "MyBitmapLoader";

    private static final String DISK_FILE_PATH = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
    private static final long DISK_MAX_SIZE = 100 * 1024 * 1024;

    /**
     * 内存缓存的大小
     */
    private int mCacheSize;

    /**
     * 动态的获取到app版本号，优点在于灵活
     * 因为版本号不一致就会删除所有相关缓存
     *
     * @param context
     * @return
     */
    public int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public MyBitmapLoder() {
        long maxSize = Runtime.getRuntime().maxMemory();
        mCacheSize = (int) (maxSize / 8);
        mCache = new LruCache<>(mCacheSize);
        try {
            File file = new File(DISK_FILE_PATH);
            if (!file.exists()) {
                boolean mkdirs = file.mkdirs();
                if (!mkdirs) {
                    throw new IOException("yapple.e " + DISK_FILE_PATH + " cant be create");
                }
            }
            mDiskLruCache = DiskLruCache.open(file,1, 1, DISK_MAX_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        long maxSize = Runtime.getRuntime().maxMemory();
        mCacheSize = (int) (maxSize / 8);
        mCache = new LruCache<>(mCacheSize);
        try {
            File file = new File(DISK_FILE_PATH);
            if (!file.exists()) {
                boolean mkdirs = file.mkdirs();
                if (!mkdirs) {
                    throw new IOException("yapple.e " + DISK_FILE_PATH + " cant be create");
                }
            }
            mDiskLruCache = DiskLruCache.open(file,1, 1, DISK_MAX_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MyBitmapLoder getInstance() {
        if (mBitmapLoader == null) {
            synchronized (MyBitmapLoder.class) {
                if (mBitmapLoader == null) {
                    mBitmapLoader = new MyBitmapLoder();
                }
            }
        }
        return mBitmapLoader;
    }

    public int getmCacheSize() {
        return mCacheSize;
    }

    /**
     * 修改内存缓存的大小
     */
    public void setmCacheSize(int mCacheSize) {
        this.mCacheSize = mCacheSize;
        mCache.resize(mCacheSize);
    }

    /**
     * 将bitmap保存到缓存中 直接将bitmap作为参数进行保存，
     * @param key 通过key value形式保存bitmap，key可以是URL等
     */
    public void putBitmapToCache(String key, Bitmap bitmap) {
        if (key != null && bitmap != null) {
            mCache.put(key, bitmap);
            Log.e(TAG, "将bitmap保存到内存中");
            try {
                int bytes = bitmap.getByteCount();
                ByteBuffer buffer = ByteBuffer.allocate(bytes);
                bitmap.copyPixelsToBuffer(buffer);
                DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                OutputStream outputStream = editor.newOutputStream(0);
                editor.commit();
                outputStream.write(buffer.array());
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从本地获取图片
     * 当内存中存在时，直接取内存中的bitmap，当内存中不存在时，则会从磁盘中获取。
     * 如果都不存在，则返回null；
     */
    public Bitmap getBitmapFromLocal(String key) {
        Bitmap bitmap = mCache.get(key);
        if (bitmap == null) {
            Log.e(TAG, "内存中无bitmap,从硬盘获取");
            bitmap = getBitmapFromDisk(key);
        }
        return bitmap;
    }



    public Bitmap getBitmapFromDisk(String key) {
        try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
            if (snapshot != null) {
                InputStream inputStream = snapshot.getInputStream(0);
                return BitmapFactory.decodeStream(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
