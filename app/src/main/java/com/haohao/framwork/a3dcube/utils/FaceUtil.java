package com.haohao.framwork.a3dcube.utils;

import android.util.Log;

/**
 * Created by Ma1 on 2017/4/26.
 */

public class FaceUtil {

    public static int faceNine(float xrot, float yrot, float upRotateX, float upRotateY) {
        xrot = xrot % 360;
        yrot = yrot % 360;
        upRotateX = upRotateX % 360;
        upRotateY = upRotateY % 360;
        Log.v("hao", "upRotateX - xrot : " + (upRotateX - xrot));
        Log.v("hao", "upRotateY - yrot : " + (upRotateY - yrot));
        if (upRotateX - xrot == 0) {
            //左右滑动时
            if (xrot == 0 || xrot == 360) {
                Log.v("hao", "左右滑动时");
                if (yrot == 0 || yrot == 360) {
                    return 1;
                } else if (yrot == 90 || yrot == -270) {
                    return 5;
                } else if (yrot == 180 || yrot == -180) {
                    return 3;
                } else if (yrot == 270 || yrot == -90) {
                    return 6;
                }
            } else if (xrot == -90 || xrot == 270) {
                if (yrot == 0 || yrot == 360) {
                    return 4;
                } else if (yrot == 90 || yrot == -270) {
                    return 5;
                } else if (yrot == 180 || yrot == -180) {
                    return 2;
                } else if (yrot == 270 || yrot == -90) {
                    return 6;
                }
            } else if (xrot == -180 || xrot == 180) {
                if (yrot == 0 || yrot == 360) {
                    return 3;
                } else if (yrot == 90 || yrot == -270) {
                    return 5;
                } else if (yrot == 180 || yrot == -180) {
                    return 1;
                } else if (yrot == 270 || yrot == -90) {
                    return 6;
                }
            } else if (xrot == -270 || xrot == 90) {
                if (yrot == 0 || yrot == 360) {
                    return 2;
                } else if (yrot == 90 || yrot == -270) {
                    return 5;
                } else if (yrot == 180 || yrot == -180) {
                    return 4;
                } else if (yrot == 270 || yrot == -90) {
                    return 6;
                }
            }
        }
        if (upRotateY - yrot == 0) {
            //上下滑动时
            Log.v("hao", "上下滑动时");
            if (yrot == 0 || yrot == 360) {
                if (xrot == 0 || xrot == 360) {
                    return 1;
                } else if (xrot == -90 || xrot == 270) {
                    return 4;
                } else if (xrot == -180 || xrot == 180) {
                    return 3;
                } else if (xrot == -270 || xrot == 90) {
                    return 2;
                }
            } else if (yrot == -90 || yrot == -270) {
                if (xrot == 0 || xrot == 360) {
                    return 6;
                } else if (xrot == -90 || xrot == 270) {
                    return 4;
                } else if (xrot == -180 || xrot == 180) {
                    return 5;
                } else if (xrot == -270 || xrot == 90) {
                    return 2;
                }
            } else if (yrot == -180 || yrot == 180) {
                if (xrot == 0 || xrot == 360) {
                    return 3;
                } else if (xrot == -90 || xrot == 270) {
                    return 4;
                } else if (xrot == -180 || xrot == 180) {
                    return 1;
                } else if (xrot == -270 || xrot == 90) {
                    return 2;
                }
            } else if (yrot == -270 || yrot == 90) {
                if (xrot == 0 || xrot == 360) {
                    return 5;
                } else if (xrot == -90 || xrot == 270) {
                    return 4;
                } else if (xrot == -180 || xrot == 180) {
                    return 6;
                } else if (xrot == -270 || xrot == 90) {
                    return 2;
                }
            }
        }

        return 0;
    }

    static float y;

    public static float rotate(float xrot, float yrot, float upRotateX, float upRotateY) {
        xrot = xrot % 360;
        yrot = yrot % 360;
        if ((xrot == -90 && yrot == 270) || (xrot == 270 && yrot == -90)) {
            y = -(xrot / Math.abs(xrot)) * Math.abs(yrot);

        } else {
            y = (xrot / Math.abs(xrot)) * Math.abs(yrot);
        }

        if (upRotateY - yrot == 0) {
            //上下滑动时
            Log.v("hao", "上下滑动时");
            if (yrot == 0 || yrot == 360) {
                if (xrot == 0 || xrot == 360) {
                    return 0;
                } else if (xrot == -90 || xrot == 270) {
                    return 0;
                } else if (xrot == -180 || xrot == 180) {
                    return xrot;
                } else if (xrot == -270 || xrot == 90) {
                    return 0;
                }
            } else if (yrot == -90 || yrot == -270) {
                if (xrot == 0 || xrot == 360) {
                    return 0;
                } else if (xrot == -90 || xrot == 270) {
                    return y;
                } else if (xrot == -180 || xrot == 180) {
                    return xrot;
                } else if (xrot == -270 || xrot == 90) {
                    return y;
                }
            } else if (yrot == -180 || yrot == 180) {
                if (xrot == 0 || xrot == 360) {
                    return 0;
                } else if (xrot == -90 || xrot == 270) {
                    return y;
                } else if (xrot == -180 || xrot == 180) {
                    return xrot;
                } else if (xrot == -270 || xrot == 90) {
                    return y;
                }
            } else if (yrot == -270 || yrot == 90) {
                if (xrot == 0 || xrot == 360) {
                    return 0;
                } else if (xrot == -90 || xrot == 270) {
                    return y;
                } else if (xrot == -180 || xrot == 180) {
                    return xrot;
                } else if (xrot == -270 || xrot == 90) {
                    return y;
                }
            }
        }
        return 0;
    }

    //翻转4个面为true，2个面为false
    public static boolean four(float xrot, float yrot, float upRotateX, float upRotateY) {
        if (upRotateY - yrot == 0) {
            //上下滑动时
            Log.v("hao", "上下滑动时");
            if (yrot == 0 || yrot == 360) {
                if (xrot == -180 || xrot == 180) {
                    return true;
                }
            } else if (yrot == -90 || yrot == -270) {
                if (xrot == -180 || xrot == 180) {
                    return true;
                }
            } else if (yrot == -180 || yrot == 180) {
                if (xrot == -180 || xrot == 180) {
                    return true;
                }
            } else if (yrot == -270 || yrot == 90) {
                if (xrot == -180 || xrot == 180) {
                    return true;
                }
            }
        }
        return false;
    }
}
