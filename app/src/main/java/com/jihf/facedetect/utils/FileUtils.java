package com.jihf.facedetect.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Func：
 * Desc:
 * Author：JHF
 * Data：2017-07-05 11:37
 * Mail：jihaifeng@raiyi.com
 */

public class FileUtils {
  public static Bitmap getBitmapFromUri(Context context, Uri uri) {
    try {
      // 读取uri所在的图片
      Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
      return bitmap;
    } catch (Exception e) {
      Log.e("[Android]", e.getMessage());
      Log.e("[Android]", "目录为：" + uri);
      e.printStackTrace();
      return null;
    }
  }

  public static byte[] readFileByBytes(String filePath) throws IOException {
    File file = new File(filePath);
    if (!file.exists()) {
      throw new FileNotFoundException(filePath);
    } else {
      ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
      BufferedInputStream in = null;

      try {
        in = new BufferedInputStream(new FileInputStream(file));
        short bufSize = 1024;
        byte[] buffer = new byte[bufSize];
        boolean len = false;

        int len1;
        while (-1 != (len1 = in.read(buffer, 0, bufSize))) {
          bos.write(buffer, 0, len1);
        }

        byte[] var7 = bos.toByteArray();
        return var7;
      } finally {
        try {
          if (in != null) {
            in.close();
          }
        } catch (IOException var14) {
          var14.printStackTrace();
        }

        bos.close();
      }
    }
  }
}
