package com.example.pickerball.util;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/** Sao chép ảnh chọn từ thiết bị vào bộ nhớ nội bộ app; DB lưu dạng `file:///...`. */
public final class SanMediaStorage {

    private SanMediaStorage() {}

    public static String copyFromUri(Context ctx, Uri uri) throws IOException {
        File dir = new File(ctx.getFilesDir(), "san_gallery");
        if (!dir.isDirectory() && !dir.mkdirs()) {
            throw new IOException("Không tạo được thư mục ảnh");
        }
        String name = "sg_" + System.currentTimeMillis() + ".jpg";
        File out = new File(dir, name);
        try (InputStream in = ctx.getContentResolver().openInputStream(uri);
             FileOutputStream os = new FileOutputStream(out)) {
            if (in == null) throw new IOException("Không đọc được ảnh");
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) >= 0) {
                os.write(buf, 0, n);
            }
        }
        return "file://" + out.getAbsolutePath();
    }
}
