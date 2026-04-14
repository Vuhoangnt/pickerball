package com.example.pickerball.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.example.pickerball.R;

import java.io.File;

/** Ảnh: `drawable://ten_file` hoặc `file:///đường_dẫn` (ảnh chép từ máy). */
public final class SanImageHelper {

    private SanImageHelper() {}

    public static int resolveDrawableRes(Context ctx, String duongDan) {
        if (duongDan == null || duongDan.isEmpty()) return 0;
        if (duongDan.startsWith("drawable://")) {
            String name = duongDan.substring("drawable://".length());
            int id = ctx.getResources().getIdentifier(name, "drawable", ctx.getPackageName());
            return id > 0 ? id : 0;
        }
        return 0;
    }

    /** Đường dẫn file tuyệt đối, hoặc null nếu không phải ảnh máy. */
    public static String filePathFromDuongDan(String duongDan) {
        if (duongDan == null || duongDan.isEmpty()) return null;
        if (duongDan.startsWith("file://")) return duongDan.substring("file://".length());
        if (duongDan.startsWith("/")) return duongDan;
        return null;
    }

    public static int placeholder() {
        return R.drawable.ic_ball;
    }

    public static void loadInto(Context ctx, ImageView iv, String duongDan) {
        loadInto(ctx, iv, duongDan, 0);
    }

    /**
     * @param maxSidePx &gt; 0 để giảm kích thước bitmap (thumbnail); 0 = decode mặc định.
     */
    public static void loadInto(Context ctx, ImageView iv, String duongDan, int maxSidePx) {
        if (duongDan == null || duongDan.isEmpty()) {
            iv.setImageResource(placeholder());
            return;
        }
        int resId = resolveDrawableRes(ctx, duongDan);
        if (resId != 0) {
            iv.setImageResource(resId);
            return;
        }
        String path = filePathFromDuongDan(duongDan);
        if (path != null) {
            File f = new File(path);
            if (f.isFile()) {
                Bitmap bmp = decodeSampled(path, maxSidePx);
                if (bmp != null) {
                    iv.setImageBitmap(bmp);
                    return;
                }
            }
        }
        iv.setImageResource(placeholder());
    }

    private static Bitmap decodeSampled(String path, int maxSidePx) {
        if (maxSidePx <= 0) {
            return BitmapFactory.decodeFile(path);
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        int w = opts.outWidth;
        int h = opts.outHeight;
        if (w <= 0 || h <= 0) return null;
        int inSampleSize = 1;
        int maxDim = Math.max(w, h);
        while (maxDim / inSampleSize > maxSidePx) {
            inSampleSize *= 2;
        }
        opts = new BitmapFactory.Options();
        opts.inSampleSize = inSampleSize;
        return BitmapFactory.decodeFile(path, opts);
    }
}
