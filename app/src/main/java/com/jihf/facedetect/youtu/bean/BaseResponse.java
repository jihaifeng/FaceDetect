package com.jihf.facedetect.youtu.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Func：
 * Desc:
 * Author：JHF
 * Data：2017-07-05 14:19
 * Mail：jihaifeng@raiyi.com
 */

public class BaseResponse implements Serializable {
  public String session_id;
  public long image_height;
  public long image_width;
  public int errorcode;
  public String errormsg;
  public List<FaceBean> face;
}
