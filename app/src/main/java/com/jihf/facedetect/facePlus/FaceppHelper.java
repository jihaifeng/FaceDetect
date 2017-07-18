package com.jihf.facedetect.facePlus;

import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.jihf.facedetect.Config.FaceConfig;
import com.jihf.facedetect.bean.FaceDetailBean;
import com.jihf.facedetect.facePlus.bean.AttributesBean;
import com.jihf.facedetect.facePlus.bean.EyeBean;
import com.jihf.facedetect.facePlus.bean.FaceppBean;
import com.jihf.facedetect.facePlus.bean.FacesBean;
import com.jihf.facedetect.http.HttpListener;
import com.jihf.facedetect.http.OkGoHelper;
import com.jihf.facedetect.utils.MathUtils;
import com.jihf.facedetect.youtu.FaceDetectListener;
import com.lzy.okgo.model.HttpParams;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Func：
 * Desc:
 * Author：JHF
 * Data：2017-07-07 14:36
 * Mail：jihaifeng@raiyi.com
 */

public class FaceppHelper implements HttpListener {
  private static FaceppHelper instance;
  private HttpParams params;
  private FaceDetectListener<List<FaceDetailBean>> listener;

  public static FaceppHelper getInstance() {
    if (null == instance) {
      synchronized (FaceppHelper.class) {
        if (null == instance) {
          instance = new FaceppHelper();
        }
      }
    }
    return instance;
  }

  public void init() {
    params = new HttpParams();
    params.put("api_key", FaceConfig.FACE_API_KEY);
    params.put("api_secret", FaceConfig.FACE_API_SECRET);
    /**
     是否检测并返回人脸五官和轮廓的83个关键点。
     1:检测
     0:不检测
     注：默认值为0
     */
    params.put("return_landmark", 0);
     /*
     是否检测并返回根据人脸特征判断出的年龄，性别，微笑、人脸质量等属性，需要将需要检测的属性组织成一个用逗号分隔的字符串。目前支持：
     gender, age, smiling, headpose, facequality, blur, eyestatus, emotion, ethnicity
     属性的顺序没有要求。
    该字段的默认值为 none ，表示不检测属性。
    关于各属性的详细描述，参见下文“返回值”说明的 "attributes" 部分。
    */
    params.put("return_attributes", "gender,age,smiling,facequality,eyestatus,emotion,ethnicity");
  }

  public void detectFaceFile(File imgFile, FaceDetectListener<List<FaceDetailBean>> listener) {
    checkInit();
    this.listener = listener;
    params.removeFile("image_file");
    List<File> files = new ArrayList<>();
    files.add(imgFile);
    params.putFileParams("image_file", files);
    OkGoHelper.getInstance().post(FaceConfig.FACE_BASE_URL + FaceConfig.FACE_DETECT_PATH, params, this);
  }

  public void detectFaceUrl(String url, FaceDetectListener<List<FaceDetailBean>> listener) {
    checkInit();
    this.listener = listener;
    params.put("image_url", url);
    OkGoHelper.getInstance().post(FaceConfig.FACE_BASE_URL + FaceConfig.FACE_DETECT_PATH, params, this);
  }

  public void detectFaceBase64(String imgBase64, FaceDetectListener<List<FaceDetailBean>> listener) {
    checkInit();
    this.listener = listener;
    params.put("image_base64", imgBase64);
    OkGoHelper.getInstance().post(FaceConfig.FACE_BASE_URL + FaceConfig.FACE_DETECT_PATH, params, this);
  }

  private void checkInit() {
    if (null == params) {
      init();
    }
  }

  @Override public void onSuccess(String data) {
    if (null == listener) {
      return;
    }
    FaceppBean faceppBean = null;
    try {
      faceppBean = JSON.parseObject(data, FaceppBean.class);
    } catch (Exception e) {
      listener.onError("数据解析异常");
      e.printStackTrace();
    }
    if (null == faceppBean) {
      listener.onError("数据解析异常");
      return;
    }
    if (!TextUtils.isEmpty(faceppBean.error_message)) {
      listener.onError(faceppBean.error_message);
      return;
    }
    List<FacesBean> beanList = faceppBean.faces;
    if (null == beanList || beanList.size() == 0) {
      listener.onError("数据丢失");
      return;
    }
    List<FaceDetailBean> detailBeanList = new ArrayList<>();
    for (FacesBean bean : beanList) {
      if (null != bean) {
        AttributesBean attributesBean = bean.attributes;
        if (null != attributesBean) {
          FaceDetailBean detailBean = new FaceDetailBean();
          if (null != attributesBean.age) {
            detailBean.age = MathUtils.totalMoney(Double.valueOf(attributesBean.age.value.toString()));
          }
          if (null != attributesBean.gender) {
            detailBean.gender = attributesBean.gender.value.toString().equals("female") ? 0 : 100;
          }
          if (null != attributesBean.facequality) {
            detailBean.beauty = MathUtils.totalMoney(Double.valueOf(attributesBean.facequality.value.toString()));
          }
          if (null != attributesBean.smile) {
            detailBean.expression = MathUtils.totalMoney(Double.valueOf(attributesBean.smile.value.toString()));
          }
          if (null != attributesBean.eyestatus) {
            EyeBean eyeBean = attributesBean.eyestatus.left_eye_status;
            detailBean.glass = null != eyeBean && (eyeBean.no_glass_eye_open < 50 || eyeBean.no_glass_eye_close < 50);
          }
          detailBeanList.add(detailBean);
        }
      }
    }
    listener.onSuccess(detailBeanList);
  }

  @Override public void onDataError(String msg) {
    if (null == listener) {
      return;
    }
    listener.onError(msg);
  }

  @Override public void onNetError(String msg) {
    if (null == listener) {
      return;
    }
    listener.onError(msg);
  }
}
