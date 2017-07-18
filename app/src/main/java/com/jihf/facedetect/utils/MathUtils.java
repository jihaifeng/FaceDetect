package com.jihf.facedetect.utils;

import java.math.BigDecimal;

/**
 * Func：
 * Desc:
 * Author：JHF
 * Data：2017-07-07 10:13
 * Mail：jihaifeng@raiyi.com
 */

public class MathUtils {
  /**
   * 四舍五入保留两位
   *
   * @param money
   *
   * @return
   */
  public static int totalMoney(double money) {
    return Integer.valueOf(new BigDecimal(money).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
  }
}
