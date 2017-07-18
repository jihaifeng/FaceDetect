package com.jihf.facedetect.baidu.bean;

public class ResultBean {
  /**
   * location : {"left":199,"top":82,"width":177,"height":161}
   * face_probability : 1
   * rotation_angle : 12
   * yaw : -6.370698928833
   * pitch : 9.9795160293579
   * roll : 11.600520133972
   * age : 26.331811904907
   * beauty : 39.799674987793
   * expression : 1
   * expression_probablity : 0.99958294630051
   * gender : female
   * gender_probability : 0.99997508525848
   * glasses : 0
   * glasses_probability : 0.99999976158142
   * race : yellow
   * race_probability : 0.99999964237213
   */

  public LocationBean location;
  public int face_probability;
  public int rotation_angle;
  public double yaw;
  public double pitch;
  public double roll;
  public double age;
  public double beauty;
  public int expression;
  public double expression_probablity;
  public String gender;
  public double gender_probability;
  public int glasses;
  public double glasses_probability;
  public String race;
  public double race_probability;
}