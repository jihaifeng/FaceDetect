package com.jihf.facedetect.youtu;

/**
 * Func：
 * Desc:
 * Author：JHF
 * Data：2017-07-05 14:28
 * Mail：jihaifeng@raiyi.com
 */

public interface FaceDetectListener<T> {
  void onSuccess(T data);

  void onError(String errorMsg);
}
