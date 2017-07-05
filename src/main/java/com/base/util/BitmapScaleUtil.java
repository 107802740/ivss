package com.base.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class BitmapScaleUtil {
    private final float sampleSizeW = 720;
    private final float sampleSizeH = 1280;

    public Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();

        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了

        newOpts.inJustDecodeBounds = true;

        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

        int w = newOpts.outWidth;

        int h = newOpts.outHeight;
        if (w <= 0 || h <= 0) {
            return null;
        }

        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为

        float hh = sampleSizeH;
        float ww = sampleSizeW;
        if (w > h) {
            hh = sampleSizeW;
            ww = sampleSizeH;
        }

        // 缩放比，只用高或者宽其中一个数据进行计算即可

        int be = 1;// be=1表示不缩放

        int inSampleSizeW = (int) Math.ceil(w / ww);
        int inSampleSizeH = (int) Math.ceil(h / hh);
        Log.d("Test", inSampleSizeW + ":" + inSampleSizeH);

        be = inSampleSizeW > inSampleSizeH ? inSampleSizeW : inSampleSizeH;

        if (be <= 1)

            be = 1;

        newOpts.inSampleSize = be;// 设置缩放比例
        newOpts.inJustDecodeBounds = false;

        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        bitmap = compressImage(bitmap);

        return bitmap;// 压缩好比例大小后再进行质量压缩

    }

    public File getimage2(String srcPath) {

        BitmapFactory.Options newOpts = new BitmapFactory.Options();

        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了

        newOpts.inJustDecodeBounds = true;

        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

        newOpts.inJustDecodeBounds = false;

        int w = newOpts.outWidth;

        int h = newOpts.outHeight;

        // 现在主流手机比较多是720*1280分辨率，所以高和宽我们设置为

        float hh = sampleSizeH;

        float ww = sampleSizeW;
        if (w > h) {
            hh = sampleSizeW;
            ww = sampleSizeH;
        }

        // 缩放比，只用高或者宽其中一个数据进行计算即可

        int be = 1;// be=1表示不缩放

        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放

            be = (int) (newOpts.outWidth / ww);

        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放

            be = (int) (newOpts.outHeight / hh);

        }

        if (be <= 1)

            be = 1;

        newOpts.inSampleSize = 2;// 设置缩放比例

        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        File file = compressImage2(bitmap);

        return file;// 压缩好比例大小后再进行质量压缩

    }

    public Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中

        int options = 100;
        int maxSize = 200 * 1024;
        while (baos.toByteArray().length > maxSize) { // 循环判断如果压缩后图片是否大于300kb,大于继续压缩
            Log.d("Test", "baos.toByteArray().length1 ="
                    + baos.toByteArray().length + ":" + image.getWidth() + ":"
                    + image.getHeight());
            baos.reset();// 重置baos即清空baos

            options -= 10;// 每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中

        }
        Bitmap bitmap = null;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(
                baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(inputStream);
        return bitmap;

    }

    public File compressImage2(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中

        int options = 100;
        int maxSize = 200 * 1024;
        while (baos.toByteArray().length > maxSize) { // 循环判断如果压缩后图片是否大于300kb,大于继续压缩
            baos.reset();// 重置baos即清空baos

            options -= 10;// 每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中

        }
        Bitmap bitmap = null;
        File file = null;
        try {
            file = new File(Environment.getExternalStorageDirectory().getPath()
                    + "/IVSS");
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(Environment.getExternalStorageDirectory().getPath()
                    + "/IVSS/pic_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, options, fos);
        } catch (FileNotFoundException e) {
            file = null;
            e.printStackTrace();
        }
        return file;

    }
}
