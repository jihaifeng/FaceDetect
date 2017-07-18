package com.jihf.facedetect;

import android.app.Application;
import com.blankj.utilcode.util.Utils;
import com.jihf.facedetect.baidu.BaiduFaceHelper;
import com.jihf.facedetect.facePlus.FaceppHelper;
import com.jihf.facedetect.http.OkGoHelper;
import com.jihf.facedetect.youtu.YouTuHelper;

/**
 * Func：
 * Desc:
 * Author：JHF
 * Data：2017-07-04 09:28
 * Mail：jihaifeng@raiyi.com
 */

public class MyApp extends Application {
  public static MyApp instance;

  public static MyApp getInstance() {
    if (null == instance) {
      synchronized (MyApp.class) {
        if (null == instance) {
          instance = new MyApp();
        }
      }
    }
    return instance;
  }

  @Override public void onCreate() {
    super.onCreate();
    initConfig();
  }

  private void initConfig() {
    OkGoHelper.getInstance().init(this);
    YouTuHelper.getInstance().init();
    BaiduFaceHelper.getInstance().init();
    FaceppHelper.getInstance().init();
    Utils.init(this);
  }
}
