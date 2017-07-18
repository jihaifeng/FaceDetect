package com.jihf.facedetect.http;

/**
 * Func：
 * Desc:
 * Author：JHF
 * Data：2017-06-20 16:16
 * Mail：jihaifeng@raiyi.com
 */

public interface HttpListener {

  public void onSuccess(String data);

  public void onDataError(String msg);

  public void onNetError(String msg);
}
