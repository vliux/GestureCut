package org.vliux.android.gesturecut.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import org.vliux.android.gesturecut.util.AppLog;

/**
 * Created by vliux on 8/7/13.
 */
public class ImageUtil {
    public static final String TAG = ImageUtil.class.getSimpleName();
    public static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;

    public static void recycleBitmap(Bitmap bitmap) {
        if (null == bitmap) {
            return;
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1 && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        bitmap = null;
    }

    public static BitmapFactory.Options optionSave() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        return options;
    }

    public static BitmapFactory.Options optionDefault() {
        return new BitmapFactory.Options();
    }

    public static Bitmap decodeSampledBitmap(String filePath, int reqWidth, int reqHeight, BitmapFactory.Options options) {
        // First decode with inJustDecodeBounds=true to check dimensions
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    public static Bitmap decodeSampledBitmap(AssetManager am, String assetFile, int reqWidth, int reqHeight,
                                             BitmapFactory.Options options) {
        // First decode with inJustDecodeBounds=true to check dimensions
        options.inJustDecodeBounds = true;
        try {
            InputStream is = am.open(assetFile);
            BitmapFactory.decodeStream(is, null, options);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        try {
            InputStream is = am.open(assetFile);
            Bitmap bmp = BitmapFactory.decodeStream(am.open(assetFile), null, options);
            is.close();
            return bmp;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap createScaledBitmap(Bitmap src, int width, int height) {
        AppLog.logd("msg", "ruibo: createScaledBitmap " + width + "x" + height);
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        float scaleX = (float) width / srcWidth;
        float scaleY = (float) height / srcHeight;
        float scale = Math.max(scaleX, scaleY);
        float scaledWidth = scale * srcWidth;
        float scaledHeight = scale * srcHeight;
        float dx = (width - scaledWidth) / 2;
        float dy = (height - scaledHeight) / 2;
        Bitmap ret = Bitmap.createBitmap(width, height, src.getConfig());
        Canvas canvas = new Canvas(ret);
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        matrix.postTranslate(dx, dy);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(src, matrix, paint);
        return ret;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static final int QUALITY_GOOD = 90;
    public static final int QUALITY_OK = 60;

    /**
     *
     * @param bmp
     * @param dir
     * @param fileName
     * @param quality
     *            jpeg quality, 0-100
     * @return
     * @throws IOException
     */
    public static String saveBmp(Bitmap bmp, File dir, String fileName, int quality) throws IOException {
        return saveBmp(bmp, dir, fileName, quality, DEFAULT_COMPRESS_FORMAT);
    }

    /**
     *
     * @param bmp
     * @param dir
     * @param fileName
     * @param quality
     *            jpeg quality, 0-100
     * @param compressFormat
     * @return
     * @throws IOException
     */
    public static String saveBmp(Bitmap bmp, File dir, String fileName, int quality,
                                 Bitmap.CompressFormat compressFormat) throws IOException {
        if (null == fileName || fileName.length() <= 0) {
            fileName = String.valueOf(new Date().getTime()) + ".png";
        }
        if (null == compressFormat) {
            compressFormat = DEFAULT_COMPRESS_FORMAT;
        }

        File saveFile = new File(dir.getAbsolutePath(), fileName);
        String path = saveFile.getCanonicalPath();
        AppLog.logi(TAG, "saving cropped bitmap file to " + path);
        OutputStream fOut = null;
        BufferedOutputStream bfOut = null;
        if (quality < 0) {
            quality = 90; // default
        }

        try {
            fOut = new FileOutputStream(saveFile);
            bfOut = new BufferedOutputStream(fOut);
            bmp.compress(compressFormat, quality, bfOut);
            bfOut.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            AppLog.loge(TAG, "unable to open file " + path);
            throw new IOException();
        } catch (IOException e) {
            e.printStackTrace();
            AppLog.loge(TAG, "failed to save image to file " + path);
            throw e;
        } finally {
            if (null != bfOut) {
                bfOut.close();
            }
        }
        return path;

    }

    public static Bitmap screenShot(View srcView) {
        if (null == srcView) {
            return null;
        }// end if

        int srcWidth = srcView.getWidth();
        int srcHeight = srcView.getHeight();
        Bitmap srcBitmap = null;
        try {
            srcBitmap = Bitmap.createBitmap(srcWidth, srcHeight, Config.ARGB_8888);
        } catch (Exception e) {
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
        Canvas srcCanvas = new Canvas(srcBitmap);
        srcCanvas.drawColor(0xffffffff);
        srcView.draw(srcCanvas);
        return srcBitmap;
    }

    public static void recycleImageViewBitmap(ImageView imageView){
        if(null == imageView){
            return;
        }

        Drawable drawable = imageView.getDrawable();
        if(drawable instanceof BitmapDrawable){
            BitmapDrawable bitmapDrawable = (BitmapDrawable)drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if(null != bitmap && !bitmap.isRecycled()){
                bitmap.recycle();
            }
        }
    }
}
