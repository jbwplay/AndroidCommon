package com.androidbase.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

import com.androidbase.utils.javaio.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;

import static android.graphics.Bitmap.CompressFormat;
import static android.graphics.Bitmap.createBitmap;

/**
 * 使用条件为AndroidQ版本以下或者私有文件存储目录
 */
public class ImageUtils {

    public static Bitmap originalImg(File file) {
        Bitmap originalBmp = null;
        try {
            originalBmp = BitmapFactory.decodeFile(file.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return originalBmp;
    }

    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidht = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidht, scaleHeight);
        Bitmap newbmp = createBitmap(bitmap, 0, 0, width, height, matrix, true);
        if (bitmap != null && bitmap != newbmp && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        return newbmp;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, float degrees) {
        Bitmap newbmp = null;
        try {
            Matrix m = new Matrix();
            m.setRotate(degrees % 360);
            newbmp = createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, false);
            if (bitmap != null && bitmap != newbmp && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newbmp;
    }

    public static Bitmap drawable2Bitmap(final Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        Bitmap bitmap;
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable
                    .getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Drawable bitmap2Drawable(Context context, Bitmap bitmap) {
        BitmapDrawable mBitmapDrawable = null;
        try {
            if (bitmap == null) {
                return null;
            }
            mBitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mBitmapDrawable;
    }

    public static byte[] bitmap2Bytes(final Bitmap bitmap, final Bitmap.CompressFormat format) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap bytes2Bitmap(final byte[] bytes) {
        return (bytes == null || bytes.length == 0) ? null : BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static Bitmap view2Bitmap(final View view) {
        if (view == null) {
            return null;
        }
        Bitmap ret = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(ret);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return ret;
    }

    public static Bitmap decodeBitmap(String path, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight || (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
            // 官方文档inSampleSize这个属性最好是2的倍数，这样处理更快，效率更高
        }
        return inSampleSize;
    }

    public static String saveBitmap2File(String dirstrString, Bitmap bitmap) {
        FileOutputStream fOut = null;
        File imageFile = null;
        FileUtils.createOrExistsFolder(new File(dirstrString));
        File file = new File(dirstrString, MiscUtils.getRandomUUIDToString() + ".jpg");
        String filepath = file.getAbsolutePath();
        try {
            imageFile = new File(filepath);
            fOut = new FileOutputStream(imageFile);
            bitmap.compress(CompressFormat.PNG, 100, fOut);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        try {
            fOut.flush();
            fOut.close();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return filepath;
    }

    public static int getExifOrientation(String filepath) {
        int degree = 0;
        try {
            ExifInterface exif = new ExifInterface(filepath);
            if (exif != null) {
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
                if (orientation != -1) {
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            degree = 90;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            degree = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            degree = 270;
                            break;
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return degree;
    }

    /**
     * 流获取图片类型
     *
     * @param is 图片输入流
     * @return 图片类型
     */
    public static String getImageType(final InputStream is) {
        if (is == null) {
            return null;
        }
        try {
            byte[] bytes = new byte[8];
            return is.read(bytes, 0, 8) != -1 ? getImageType(bytes) : null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取图片类型
     *
     * @param bytes bitmap的前8字节
     * @return 图片类型
     */
    public static String getImageType(final byte[] bytes) {
        if (isJPEG(bytes)) {
            return "JPEG";
        }
        if (isGIF(bytes)) {
            return "GIF";
        }
        if (isPNG(bytes)) {
            return "PNG";
        }
        if (isBMP(bytes)) {
            return "BMP";
        }
        return null;
    }

    private static boolean isJPEG(final byte[] b) {
        return b.length >= 2 && (b[0] == (byte) 0xFF) && (b[1] == (byte) 0xD8);
    }

    private static boolean isGIF(final byte[] b) {
        return b.length >= 6 && b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8' && (b[4] == '7' || b[4] == '9') && b[5] == 'a';
    }

    private static boolean isPNG(final byte[] b) {
        return b.length >= 8 && (b[0] == (byte) 137 && b[1] == (byte) 80 && b[2] == (byte) 78 && b[3] == (byte) 71 && b[4] == (byte) 13 && b[5] == (byte) 10 && b[6] == (byte) 26 && b[7] == (byte) 10);
    }

    private static boolean isBMP(final byte[] b) {
        return b.length >= 2 && (b[0] == 0x42) && (b[1] == 0x4d);
    }

    /**
     * 判断bitmap对象是否为空
     *
     * @param src 源图片
     * @return {@code true}: 是<br>{@code false}: 否
     */
    private static boolean isEmptyBitmap(final Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }

    /**
     * renderScript模糊图片
     * <p>API大于17</p>
     *
     * @param src    源图片
     * @param radius 模糊半径(0...25)
     * @return 模糊后的图片
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap renderScriptBlur(final Bitmap src, @FloatRange(from = 0, to = 25, fromInclusive = false) final float radius) {
        return renderScriptBlur(src, radius, false);
    }

    /**
     * renderScript模糊图片
     * <p>API大于17</p>
     *
     * @param src     源图片
     * @param radius  模糊半径(0...25)
     * @param recycle 是否回收
     * @return 模糊后的图片
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap renderScriptBlur(final Bitmap src, @FloatRange(from = 0, to = 25, fromInclusive = false) final float radius, final boolean recycle) {
        if (src == null || src.getWidth() == 0 || src.getHeight() == 0) {
            return null;
        }
        RenderScript rs = null;
        Bitmap ret = recycle ? src : src.copy(src.getConfig(), true);
        try {
            rs = RenderScript.create(AndroidUtils.getContext());
            rs.setMessageHandler(new RenderScript.RSMessageHandler());
            Allocation input = Allocation.createFromBitmap(rs, ret, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            Allocation output = Allocation.createTyped(rs, input.getType());
            ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            blurScript.setInput(input);
            blurScript.setRadius(radius);
            blurScript.forEach(output);
            output.copyTo(ret);
        } finally {
            if (rs != null) {
                rs.destroy();
            }
        }
        return ret;
    }

    /**
     * 按质量压缩
     *
     * @param src     源图片
     * @param quality 质量
     * @return 质量压缩后的图片
     */
    public static Bitmap compressByQuality(final Bitmap src, @IntRange(from = 0, to = 100) final int quality) {
        return compressByQuality(src, quality, false);
    }

    /**
     * 按质量压缩
     *
     * @param src     源图片
     * @param quality 质量
     * @param recycle 是否回收
     * @return 质量压缩后的图片
     */
    public static Bitmap compressByQuality(final Bitmap src, @IntRange(from = 0, to = 100) final int quality, final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] bytes = baos.toByteArray();
        if (recycle && !src.isRecycled()) {
            src.recycle();
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * 按质量压缩
     *
     * @param src         源图片
     * @param maxByteSize 允许最大值字节数
     * @return 质量压缩压缩过的图片
     */
    public static Bitmap compressByQuality(final Bitmap src, final long maxByteSize) {
        return compressByQuality(src, maxByteSize, false);
    }

    /**
     * 按质量压缩
     *
     * @param src         源图片
     * @param maxByteSize 允许最大值字节数
     * @param recycle     是否回收
     * @return 质量压缩压缩过的图片
     */
    public static Bitmap compressByQuality(final Bitmap src, final long maxByteSize, final boolean recycle) {
        if (isEmptyBitmap(src) || maxByteSize <= 0) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(CompressFormat.JPEG, 100, baos);
        byte[] bytes;
        if (baos.size() <= maxByteSize) {// 最好质量的不大于最大字节，则返回最佳质量
            bytes = baos.toByteArray();
        } else {
            baos.reset();
            src.compress(CompressFormat.JPEG, 0, baos);
            if (baos.size() >= maxByteSize) { // 最差质量不小于最大字节，则返回最差质量
                bytes = baos.toByteArray();
            } else {
                // 二分法寻找最佳质量
                int st = 0;
                int end = 100;
                int mid = 0;
                while (st < end) {
                    mid = (st + end) / 2;
                    baos.reset();
                    src.compress(CompressFormat.JPEG, mid, baos);
                    int len = baos.size();
                    if (len == maxByteSize) {
                        break;
                    } else if (len > maxByteSize) {
                        end = mid - 1;
                    } else {
                        st = mid + 1;
                    }
                }
                if (end == mid - 1) {
                    baos.reset();
                    src.compress(CompressFormat.JPEG, st, baos);
                }
                bytes = baos.toByteArray();
            }
        }
        if (recycle && !src.isRecycled()) {
            src.recycle();
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * 按采样大小压缩
     *
     * @param src       源图片
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return 按采样率压缩后的图片
     */
    public static Bitmap compressBySampleSize(final Bitmap src, final int maxWidth, final int maxHeight) {
        return compressBySampleSize(src, maxWidth, maxHeight, false);
    }

    /**
     * 按采样大小压缩
     *
     * @param src       源图片
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @param recycle   是否回收
     * @return 按采样率压缩后的图片
     */
    public static Bitmap compressBySampleSize(final Bitmap src, final int maxWidth, final int maxHeight, final boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        if (recycle && !src.isRecycled()) {
            src.recycle();
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

}
