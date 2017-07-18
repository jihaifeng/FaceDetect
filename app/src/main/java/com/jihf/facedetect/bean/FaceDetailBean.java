package com.jihf.facedetect.bean;

/**
 * Func：
 * Desc:
 * Author：JHF
 * Data：2017-07-05 14:29
 * Mail：jihaifeng@raiyi.com
 */

public class FaceDetailBean {

  public int age; // 年龄 [0~100]
  public int gender; // 性别 [0/(female)~100(male)]  | male、female
  public boolean glass; // 是否有眼镜 [true,false]   | 是否带眼镜，0-无眼镜，1-普通眼镜，2-墨镜
  public int expression; // 微笑[0(normal)~50(smile)~100(laugh)]  | 表情，0，不笑；1，微笑；2，大笑。
  public int beauty; // 魅力 [0~100] | 美丑打分，范围0-1，越大表示越美
}
