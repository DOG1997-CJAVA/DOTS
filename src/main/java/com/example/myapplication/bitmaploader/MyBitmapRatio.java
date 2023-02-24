package com.example.myapplication.bitmaploader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MyBitmapRatio {

    /**
     * 压缩加载Bitmap
     *
     * @param resources 以Resources为图片来源加载Bitmap
     * @param pixWidth  需要显示的宽
     * @param pixHeight 需要显示的高
     * @return 压缩后的Bitmap
     */
    public Bitmap ratioBitmap(Resources resources, int ResId, int pixWidth, int pixHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        /*
          inJustDecodeBounds设置为true，只加载原始图片的宽和高，
          我们先获取原始图片的高和宽，从而计算缩放比例
         */
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        BitmapFactory.decodeResource(resources, ResId, options);
        int originalWidth = options.outWidth;
        int originalHeight = options.outHeight;

        options.inSampleSize = getSimpleSize(originalWidth, originalHeight, pixWidth, pixHeight);
        /*
          inJustDecodeBounds设置为false, 真正的去加载Bitmap
         */
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(resources, ResId, options);
    }

    /**
     * 获取压缩比例，如果原图宽比高长，则按照宽来压缩，反之则按照高来压缩
     *
     * @return 压缩比例，原图和压缩后图的比例
     */
    private static int getSimpleSize(int originalWidth, int originalHeight, int pixWidth, int pixHeight) {
        int simpleSize = 1;
        if (originalWidth > originalHeight && originalWidth > pixWidth) {
            simpleSize = originalWidth / pixWidth;
        } else if (originalHeight > originalWidth && originalHeight > pixHeight) {
            simpleSize = originalHeight / pixHeight;
        }
        if (simpleSize <= 0) {
            simpleSize = 1;
        }
        return simpleSize;
    }


}
