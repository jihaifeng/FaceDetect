package com.jihf.facedetect.facePlus.bean;

public class AttributesBean {
  /**
   * emotion : {"sadness":7.155,"neutral":87.423,"disgust":0.984,"anger":0.093,"surprise":0.049,"fear":0.139,
   * "happiness":4.157}
   * gender : {"value":"Female"}
   * age : {"value":26}
   * eyestatus : {"left_eye_status":{"normal_glass_eye_open":0.011,"no_glass_eye_close":0,"occlusion":0,
   * "no_glass_eye_open":99.989,"normal_glass_eye_close":0,"dark_glasses":0},
   * "right_eye_status":{"normal_glass_eye_open":0.028,"no_glass_eye_close":0,"occlusion":0,
   * "no_glass_eye_open":99.971,"normal_glass_eye_close":0,"dark_glasses":0.001}}
   * glass : {"value":"None"}
   * headpose : {"yaw_angle":-2.5280323,"pitch_angle":10.779465,"roll_angle":1.9606519}
   * smile : {"threshold":30.1,"value":18.348}
   * facequality : {"threshold":70.1,"value":89.599}
   * ethnicity : {"value":"White"}
   */

  public EmotionBean emotion;
  public EyestatusBean eyestatus;

  public FaceResultBean age;
  public FaceResultBean gender;

  public FaceResultBean smile;
  public FaceResultBean facequality;
  public FaceResultBean ethnicity;
}

