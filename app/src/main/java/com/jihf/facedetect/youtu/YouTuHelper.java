package com.jihf.facedetect.youtu;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.jihf.facedetect.Config.YouTuConfig;
import com.jihf.facedetect.bean.FaceDetailBean;
import com.jihf.facedetect.youtu.bean.BaseResponse;
import com.jihf.facedetect.youtu.bean.FaceBean;
import com.jihf.facedetect.youtu.sign.Base64Util;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Func：
 * Desc:
 * Author：JHF
 * Data：2017-07-05 09:15
 * Mail：jihaifeng@raiyi.com
 */

public class YouTuHelper {
  private static final String TAG = YouTuHelper.class.getSimpleName().trim();
  private static YouTuHelper instance;
  private Youtu faceYoutu;

  public static YouTuHelper getInstance() {
    if (null == instance) {
      synchronized (YouTuHelper.class) {
        if (null == instance) {
          instance = new YouTuHelper();
        }
      }
    }
    return instance;
  }

  public YouTuHelper init() {
    //优图开放平台初始化
    faceYoutu = new Youtu(YouTuConfig.APP_ID, YouTuConfig.SECRET_ID, YouTuConfig.SECRET_KEY, YouTuConfig.YOUTU_BASEURL);
    return this;
  }

  /*!
     * 人脸属性分析 检测给定图片(Image)中的所有人脸(Face)的位置和相应的面部属性。位置包括(x, y, w, h)，
     * 面部属性包括性别(gender), 年龄(age), 表情(expression), 眼镜(glass)和姿态(pitch，roll，yaw).
     *
     * @param bitmap 人脸图片
     * @param mode 检测模式 0/1 正常/大脸模式
     * @return 请求json结果
    */
  public void DetectFace(Bitmap bitmap, int mode, FaceDetectListener<List<FaceDetailBean>> listener) {
    checkInit();
    JSONObject data = new JSONObject();
    String respose = null;

    try {
      String imageData = Base64Util.bitmapToBase64(bitmap);
      data.put("image", imageData);
      data.put("mode", mode);
      respose = faceYoutu.SendRequest(data, YouTuConfig.YOUTU_DETECTFACE);
    } catch (IOException | JSONException | KeyManagementException | NoSuchAlgorithmException e) {
      Log.i(TAG, "DetectFace: " + e.getMessage());
      e.printStackTrace();
    }
    Log.i(TAG, "DetectFaceUrl: " + respose);
    pareResponse(respose, listener);
  }

  /*!
   * 人脸属性分析 检测给定图片(Image)中的所有人脸(Face)的位置和相应的面部属性。位置包括(x, y, w, h)，
   * 面部属性包括性别(gender), 年龄(age), 表情(expression), 眼镜(glass)和姿态(pitch，roll，yaw).
   *
   * @param url 人脸图片url
   * @param mode 检测模式 0/1 正常/大脸模式
   * @return 请求json结果
  */
  public void DetectFace(String url, int mode, FaceDetectListener<List<FaceDetailBean>> listener) {
    checkInit();
    JSONObject data = new JSONObject();
    String respose = null;
    try {
      data.put("url", url);
      data.put("mode", mode);
      respose = faceYoutu.SendRequest(data, YouTuConfig.YOUTU_DETECTFACE);
    } catch (JSONException | IOException | KeyManagementException | NoSuchAlgorithmException e) {
      Log.i(TAG, "DetectFaceUrl: " + e.getMessage());
      e.printStackTrace();
    }
    Log.i(TAG, "DetectFaceUrl: " + respose);
    pareResponse(respose, listener);
  }

  private void pareResponse(String resposeData, FaceDetectListener<List<FaceDetailBean>> listener) {
    if (null == listener) {
      return;
    }
    if (TextUtils.isEmpty(resposeData)) {
      listener.onError("没获取到数据");
      return;
    }
    BaseResponse response = JSON.parseObject(resposeData, BaseResponse.class);
    if (null == response) {
      listener.onError("数据解析异常");
      return;
    }
    if (response.errorcode != 0) {
      listener.onError("数据返回异常");
      return;
    }
    List<FaceDetailBean> beanList = covResult(response);
    if (null == beanList || beanList.size() == 0) {
      listener.onError("数据缺失");
      return;
    }
    listener.onSuccess(beanList);
  }

  private List<FaceDetailBean> covResult(BaseResponse response) {
    List<FaceBean> faceBeen = response.face;
    if (null == faceBeen || faceBeen.size() == 0) {
      return null;
    }
    List<FaceDetailBean> detailBeanList = new ArrayList<>();
    for (FaceBean faceBean : faceBeen) {
      if (null != faceBean) {
        FaceDetailBean detailBean = new FaceDetailBean();
        detailBean.age = faceBean.age;
        detailBean.beauty = faceBean.beauty;
        detailBean.expression = faceBean.expression;
        detailBean.gender = faceBean.gender;
        detailBean.glass = faceBean.glass;
        detailBeanList.add(detailBean);
      }
    }
    return detailBeanList;
  }

  private void checkInit() {
    if (null == faceYoutu) {
      init();
    }
  }
}
