package com.jihf.facedetect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.jihf.facedetect.baidu.BaiduFaceHelper;
import com.jihf.facedetect.bean.FaceDetailBean;
import com.jihf.facedetect.facePlus.FaceppHelper;
import com.jihf.facedetect.utils.BitmapUtils;
import com.jihf.facedetect.utils.FileProvider7;
import com.jihf.facedetect.youtu.FaceDetectListener;
import com.jihf.facedetect.youtu.YouTuHelper;
import com.yanzhenjie.album.Album;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener, FaceDetectListener<List<FaceDetailBean>> {
  private static final String TAG = MainActivity.class.getSimpleName().trim();
  private Button btnTengxun;
  private Button btnBaidu;
  private Button btnFace;
  private TextView tvDesc;
  private String desc;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initView();
  }

  private void initView() {
    btnTengxun = getView(R.id.btn_tengxun);
    btnTengxun.setOnClickListener(this);
    btnBaidu = getView(R.id.btn_baidu);
    btnBaidu.setOnClickListener(this);
    btnFace = getView(R.id.btn_face);
    btnFace.setOnClickListener(this);
    tvDesc = getView(R.id.tv_desc);
  }

  @SuppressWarnings ("unchecked") private <E extends View> E getView(int id) {
    try {
      return (E) findViewById(id);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_tengxun:
        desc = "---------腾讯YouTu识别---------";
        selectFromAlum(999);
        break;
      case R.id.btn_baidu:
        desc = "---------百度识别---------";
        selectFromAlum(666);
        break;
      case R.id.btn_face:
        desc = "---------Face++识别---------";
        //selectFromAlum(333);
        //try {
        //  Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.yangzi);
        //  String data = Base64Util.bitmapToBase64(bitmap);
        //  OkGo.post("");
        //} catch (IOException e) {
        //  e.printStackTrace();
        //}
        break;
    }
  }

  private void selectFromAlum(int requestCode) {
    Album.albumRadio(this).title("图库") // 配置title。
        .columnCount(3) // 相册展示列数，默认是2列。
        .camera(true) // 是否有拍照功能。
        .start(requestCode); // 999是请求码，返回时onActivityResult()的第一个参数。
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
    if (resultCode == RESULT_OK) { // Successfully.
      if (requestCode == 999) {
        // 腾讯
        new Thread(new Runnable() {
          @Override public void run() {
            ArrayList<String> pathList = Album.parseResult(data);
            Uri uri = FileProvider7.getUriForFile(MainActivity.this, new File(pathList.get(0)));
            Bitmap selectedImage = BitmapUtils.covBitmapFrmUriWithOpt(MainActivity.this, uri);
            YouTuHelper.getInstance().DetectFace(selectedImage, 0, MainActivity.this);
          }
        }).start();
      } else if (requestCode == 666) {
        // 百度
        new Thread(new Runnable() {
          @Override public void run() {
            ArrayList<String> pathList = Album.parseResult(data);
            Uri uri = FileProvider7.getUriForFile(MainActivity.this, new File(pathList.get(0)));
            Bitmap bitmap = BitmapUtils.covBitmapFrmUriWithOpt(MainActivity.this, uri);
            BaiduFaceHelper.getInstance().DetectFace(bitmap, MainActivity.this);
          }
        }).start();
      } else if (requestCode == 333) {
        // Face++
        ArrayList<String> pathList = Album.parseResult(data);
        File file = new File(pathList.get(0));
        Log.i(TAG, "onActivityResult: " + file);
        FaceppHelper.getInstance().detectFaceFile(file, new FaceDetectListener<List<FaceDetailBean>>() {
          @Override public void onSuccess(List<FaceDetailBean> data) {
            if (null == data || data.size() == 0) {
              updateDesc("数据转换异常");
              return;
            }
            for (FaceDetailBean detailBean : data) {
              desc += "\n\n颜值得分："
                  + detailBean.beauty
                  + "\n年龄："
                  + detailBean.age
                  + "\n是否戴眼镜："
                  + detailBean.glass
                  + "\n表情："
                  + detailBean.expression
                  + "\n性别："
                  + detailBean.gender;
            }
            updateDesc(desc);
          }

          @Override public void onError(String errorMsg) {
            updateDesc(errorMsg);
          }
        });
      }
    } else if (resultCode == RESULT_CANCELED) { // User canceled.
      // 用户取消了操作。
    }
  }

  @Override public void onSuccess(List<FaceDetailBean> data) {
    Looper.prepare();
    if (null == data || data.size() == 0) {
      updateDesc("数据转换异常");
      return;
    }
    for (FaceDetailBean detailBean : data) {
      desc += "\n\n颜值得分："
          + detailBean.beauty
          + "\n年龄："
          + detailBean.age
          + "\n是否戴眼镜："
          + detailBean.glass
          + "\n表情："
          + detailBean.expression
          + "\n性别："
          + detailBean.gender;
    }
    updateDesc(desc);
    Looper.loop();
  }

  private void updateDesc(final String s) {
    runOnUiThread(new Runnable() {
      @Override public void run() {
        tvDesc.setText(TextUtils.isEmpty(s) ? "数据获取失败\n" : s + "\n");
      }
    });
  }

  @Override public void onError(String errorMsg) {
    Looper.prepare();
    updateDesc(errorMsg);
    Looper.loop();
  }
}
