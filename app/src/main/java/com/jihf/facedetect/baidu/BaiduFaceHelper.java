package com.jihf.facedetect.baidu;

import android.graphics.Bitmap;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.baidu.aip.face.AipFace;
import com.jihf.facedetect.Config.BaiDuConfig;
import com.jihf.facedetect.baidu.bean.BaiduFaceBean;
import com.jihf.facedetect.baidu.bean.ResultBean;
import com.jihf.facedetect.bean.FaceDetailBean;
import com.jihf.facedetect.utils.BitmapUtils;
import com.jihf.facedetect.utils.MathUtils;
import com.jihf.facedetect.youtu.FaceDetectListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONObject;

/**
 * Func：
 * Desc:
 * Author：JHF
 * Data：2017-07-07 08:53
 * Mail：jihaifeng@raiyi.com
 */

public class BaiduFaceHelper {
  private static final String TAG = BaiduFaceHelper.class.getSimpleName().trim();
  private static BaiduFaceHelper instance;
  private AipFace aipFace;

  public static BaiduFaceHelper getInstance() {
    if (null == instance) {
      synchronized (BaiduFaceHelper.class) {
        if (null == instance) {
          instance = new BaiduFaceHelper();
        }
      }
    }
    return instance;
  }

  public void init() {
    // 初始化一个FaceClient
    aipFace = new AipFace(BaiDuConfig.BAIDU_APP_ID, BaiDuConfig.BAIDU_API_KEY, BaiDuConfig.BAIDU_SECRET_KEY);
    // 可选：设置网络连接参数
    aipFace.setConnectionTimeoutInMillis(2000);
    aipFace.setSocketTimeoutInMillis(60000);
  }

  public void DetectFace(Bitmap bitmap, FaceDetectListener<List<FaceDetailBean>> listener) {
    checkInit();
    // 自定义参数定义
    HashMap<String, String> options = new HashMap<>();
    options.put("max_face_num", "5");
    options.put("face_fields", "age,beauty,expression,gender,glasses,race");
    // 参数为本地图片路径
    byte[] bt = BitmapUtils.bitmap2Bytes(bitmap);
    JSONObject object = aipFace.detect(bt, options);
    Log.i(TAG, "DetectFace: " + object.toString());
    pareResponse(object, listener);
  }

  private void pareResponse(JSONObject object, FaceDetectListener<List<FaceDetailBean>> listener) {
    if (null == listener) {
      return;
    }
    if (null == object) {
      listener.onError("没获取到数据");
      return;
    }
    String resposeData = object.toString();
    BaiduFaceBean baiduFaceBean = JSON.parseObject(resposeData, BaiduFaceBean.class);
    if (null == baiduFaceBean) {
      listener.onError("数据解析异常");
      return;
    }
    if (baiduFaceBean.result_num < 1) {
      listener.onError("数据返回异常");
      return;
    }
    List<FaceDetailBean> beanList = covResult(baiduFaceBean);
    if (null == beanList || beanList.size() == 0) {
      listener.onError("数据缺失");
      return;
    }
    listener.onSuccess(beanList);
  }

  private List<FaceDetailBean> covResult(BaiduFaceBean baiduFaceBean) {
    List<ResultBean> resultBeanList = baiduFaceBean.result;
    if (null == resultBeanList || resultBeanList.size() == 0) {
      return null;
    }
    List<FaceDetailBean> detailBeanList = new ArrayList<>();
    for (ResultBean faceBean : resultBeanList) {
      if (null != faceBean) {
        FaceDetailBean detailBean = new FaceDetailBean();
        detailBean.age = MathUtils.totalMoney(faceBean.age);
        detailBean.beauty = MathUtils.totalMoney(faceBean.beauty);
        detailBean.expression = faceBean.expression;
        detailBean.gender = faceBean.gender.equals("female") ? 0 : 100;
        detailBean.glass = faceBean.glasses != 0;
        detailBeanList.add(detailBean);
      }
    }
    return detailBeanList;
  }

  private void checkInit() {
    if (null == aipFace) {
      init();
    }
  }
}
