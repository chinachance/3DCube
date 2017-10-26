package com.haohao.framwork.a3dcube;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;
import android.view.MotionEvent;

import com.haohao.framwork.a3dcube.utils.FaceUtil;

import org.greenrobot.eventbus.EventBus;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Ma1 on 2017/4/10.
 */

public class MyAnimationView extends GLSurfaceView implements GLSurfaceView.Renderer {


    private boolean kaiguan = false;

    //跳转页面id
    public int tiaoId;

    private Cube cube;

    private float xrot;
    private float yrot;

    private float z = -5.0f;            //到屏幕的深度

    private int filter = 0;                //纹理的过滤器

    /**
     * 启用灯光
     */
    private boolean light = true;
    private float[] lightAmbient = {1.0f, 1.0f, 1.0f, 1.0f};
    private float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};
    private float[] lightPosition = {0.0f, 0.0f, 1.0f, 1.0f};

    /**
     * 光值的缓冲区
     */
    private FloatBuffer lightAmbientBuffer;
    private FloatBuffer lightDiffuseBuffer;
    private FloatBuffer lightPositionBuffer;

    /**
     * 这些变量存储以前的X和Y
     */
    private float oldX;
    private float oldY;

    //触摸旋转角度
    private final float TOUCH_SCALE = 0.1f;


    private Context context;
    private float mDx;
    private float mDy;
    private float mRotate;

    public MyAnimationView(Context context) {
        super(context);

        //设置这个渲染器
        this.setRenderer(this);
        //请求焦点,否则按钮没有反应
        this.requestFocus();
        this.setFocusableInTouchMode(true);


        this.context = context;

        ByteBuffer byteBuf = ByteBuffer.allocateDirect(lightAmbient.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        lightAmbientBuffer = byteBuf.asFloatBuffer();
        lightAmbientBuffer.put(lightAmbient);
        lightAmbientBuffer.position(0);

        byteBuf = ByteBuffer.allocateDirect(lightDiffuse.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        lightDiffuseBuffer = byteBuf.asFloatBuffer();
        lightDiffuseBuffer.put(lightDiffuse);
        lightDiffuseBuffer.position(0);

        byteBuf = ByteBuffer.allocateDirect(lightPosition.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        lightPositionBuffer = byteBuf.asFloatBuffer();
        lightPositionBuffer.put(lightPosition);
        lightPositionBuffer.position(0);


        cube = new Cube(getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        //光照
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbientBuffer);        //设置环境光
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuseBuffer);        //设置的漫射光
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPositionBuffer);    //位置的光
        gl.glEnable(GL10.GL_LIGHT0);

        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, lightAmbientBuffer);
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, lightDiffuseBuffer);
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, lightPositionBuffer);
        gl.glEnable(GL10.GL_LIGHT1);

        gl.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
        gl.glDisable(GL10.GL_DITHER);                //禁用抖动
        gl.glEnable(GL10.GL_TEXTURE_2D);            //纹理映射
        gl.glShadeModel(GL10.GL_SMOOTH);            //光滑的材质
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);    //黑色背景
        gl.glClearDepthf(1.0f);                    //深度缓冲设置
        gl.glEnable(GL10.GL_DEPTH_TEST);            //启用深度测试
        gl.glDepthFunc(GL10.GL_LEQUAL);            //深度测试的类型


        //角度计算
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) {                        //P防止除以零
            height = 1;
        }

        gl.glViewport(0, 0, width, height);    //重置当前视口
        gl.glMatrixMode(GL10.GL_PROJECTION);    //选择投影矩阵
        gl.glLoadIdentity();                    //重置投影矩阵

        //计算窗口的长宽比
        GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 100.0f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);    //选择Modelview矩阵

        gl.glLoadIdentity();                    //重置Modelview矩阵
        Log.v("hao", "MyAnimationView onSurfaceChanged()");

    }


    private boolean touming;

    @Override
    public void onDrawFrame(GL10 gl) {
        //清晰的屏幕和深度缓冲
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();                    //重置当前Modelview矩阵
        //检查光标志被设置为启用/禁用照明
        if (light) {
            gl.glClear(GL10.GL_DEPTH_BUFFER_BIT);
            gl.glEnable(GL10.GL_LIGHTING);
        } else {
            gl.glClear(GL10.GL_DEPTH_BUFFER_BIT);
            gl.glDisable(GL10.GL_LIGHTING);
        }


        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        // 设置透明显示
        gl.glEnable(GL10.GL_ALPHA_TEST);
        gl.glAlphaFunc(GL10.GL_GREATER, 0.1f);
        gl.glDisable(GL10.GL_DEPTH_TEST);
        gl.glDepthRangef(0.5f, 1.0f);

        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthRangef(0.5f, 1.0f);

        gl.glTranslatef(0.0f, 0.0f, z);
        gl.glScalef(0.8f, 0.8f, 0.8f);

        //x角度变，绕y轴旋转；y角度变，绕x轴旋转
        //左右滑动，yrot变；上下滑动，xrot变
        gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f);


        //画立方体
        cube.draw(gl, filter);

    }


    private int upperArea;
    float downX;
    float downY;
    boolean onclick = true;
    boolean twoD;
    private float xClick;
    private float yClick;
    private float upRotateX;
    private float upRotateY;

    private float xrotateNext;
    private float yrotateNext;


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downX = event.getX();
            downY = event.getY();
            Log.v("hao1", "downX:  " + downX + " downY: " + downY);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

            mDx = x - oldX;
            mDy = y - oldY;


            upperArea = this.getHeight() / 10;


            if (y < upperArea) {
                z -= mDx * TOUCH_SCALE / 2;

                //旋转的轴
            } else {

            }


        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.v("hao", "ACTION_UP");
            final float upX = event.getX();
            float upY = event.getY();


            Log.v("hao", "upRotateX: " + upRotateX + "  upRotateY: " + upRotateY);

            float vx = upX - downX;
            float vy = upY - downY;

            Log.v("hao", "upX:   " + upX + "   downX:   " + downX + "  upY:     " + upY + "  downY:   " + downY);

            Log.v("hao", "vx    : " + vx + "    vy :      " + vy);

            if (downX >= 130 && downX <= 950 && downY >= 470 && downY <= 1280) {

                //4.17 点击页面跳转
                if (Math.abs(vx) < 5 && Math.abs(vy) < 5) {

                    Log.v("hao", "mDx    :" + mDx + "      mDy:      " + mDy);
                    Log.v("hao", "xrot    :" + xrot + "      yrot:      " + yrot);

                    //设置点击事件
                    int face = FaceUtil.faceNine(xrot, yrot, upRotateX, upRotateY);

                    //确定点击时的xrot,yrot
                    Log.v("hao", "face:   " + face);
                    if (face == 1) {
                        xClick = xrot;
                        yClick = yrot;
                        //正面点击
                        sendMessage(1);
                        Log.v("hao", "sendMessage(1);");
                    } else if (face == 2) {
                        xClick = xrot;
                        yClick = yrot;
                        //上面点击
                        sendMessage(6);
                        Log.v("hao", "sendMessage(6);");
                    } else if (face == 3) {
                        xClick = xrot;
                        yClick = yrot;
                        //后面点击
                        sendMessage(3);
                        Log.v("hao", "sendMessage(3);");
                    } else if (face == 4) {
                        xClick = xrot;
                        yClick = yrot;
                        //下面点击
                        sendMessage(5);
                        Log.v("hao", "sendMessage(5);");
                    } else if (face == 5) {
                        xClick = xrot;
                        yClick = yrot;
                        //左面点击
                        sendMessage(2);
                        Log.v("hao", "sendMessage(2);");
                    } else if (face == 6) {
                        xClick = xrot;
                        yClick = yrot;
                        //右面点击
                        sendMessage(4);
                        Log.v("hao", "sendMessage(4);");
                    } else {

                        if (xrot - xClick != 0 && yrot - yClick != 0) {
                            twoD = true;
                        }
                        if (xrot - xClick == 0 || yrot - yClick == 0) {
                            twoD = false;
                        }
                        //点击还原使不透明
                        Log.v("hao", "yrot:  " + yrot + "  yClick: " + yClick);
                        //还原动画
                        ValueAnimator anim;
                        if (xrot - xClick == 30) {
                            //向上30
                            if (Math.abs(upRotateX) == 180) {
                                anim = ValueAnimator.ofFloat(0, -300);
                            } else {
                                anim = ValueAnimator.ofFloat(0, 300);
                            }

                            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    mRotate = (float) animation.getAnimatedValue() / 5 / 60;
                                    Log.v("haohao", "rotate:  " + mRotate * 5 + "cube.mFaceWidth-upX+136  " + (cube.mFaceWidth - upX + 136));
                                    xrot -= mRotate;
                                    Log.v("hao", "rotate:   " + mRotate + "    xrot:   " + xrot);

                                }
                            });

                            anim.setDuration(1000);
                            anim.start();
                            anim.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    Log.v("hao", " onAnimationEnd()");
                                    if (Math.abs(xrot % 30) < 2.0) {
                                        xrot = doDegreeTwo(xrot);
                                    } else if (Math.abs(xrot % 30) > 28.0 && Math.abs(Math.abs(xrot % 30) - 30) < 2.0) {
                                        xrot = doDegreeTwo(xrot);
                                    }
                                    Log.v("hao", "xrot):" + xrot);
                                    if (!twoD) {
                                        sendMessage(memberNum);
                                    }

                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                        } else if (xrot - xClick == -30) {
                            //向下30
                            if (Math.abs(upRotateX) == 180) {
                                anim = ValueAnimator.ofFloat(0, -300);
                            } else {
                                anim = ValueAnimator.ofFloat(0, 300);
                            }

                            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    mRotate = (float) animation.getAnimatedValue() / 5 / 60;
                                    Log.v("haohao", "rotate:  " + mRotate * 5 + "cube.mFaceWidth-upX+136  " + (cube.mFaceWidth - upX + 136));
                                    xrot += mRotate;
                                    Log.v("hao", "rotate:   " + mRotate + "    xrot:   " + xrot);

                                }
                            });

                            anim.setDuration(1000);
                            anim.start();
                            anim.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    Log.v("hao", " onAnimationEnd()");
                                    if (Math.abs(xrot % 30) < 2.0) {
                                        xrot = doDegreeTwo(xrot);
                                    } else if (Math.abs(xrot % 30) > 28.0 && Math.abs(Math.abs(xrot % 30) - 30) < 2.0) {
                                        xrot = doDegreeTwo(xrot);
                                    }
                                    Log.v("hao", "xrot):" + xrot);
                                    if (!twoD) {
                                        sendMessage(memberNum);
                                    }
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                        } else if (xrot - xClick == 60) {
                            //向上60
                            if (Math.abs(upRotateX) == 180) {
                                anim = ValueAnimator.ofFloat(0, -600);
                            } else {
                                anim = ValueAnimator.ofFloat(0, 600);
                            }

                            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    mRotate = (float) animation.getAnimatedValue() / 5 / 60;
                                    Log.v("haohao", "rotate:  " + mRotate * 5 + "cube.mFaceWidth-upX+136  " + (cube.mFaceWidth - upX + 136));
                                    xrot -= mRotate;
                                    Log.v("hao", "rotate:   " + mRotate + "    xrot:   " + xrot);

                                }
                            });

                            anim.setDuration(1000);
                            anim.start();
                            anim.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    Log.v("hao", " onAnimationEnd()");
                                    if (Math.abs(xrot % 30) < 2.0) {
                                        xrot = doDegreeTwo(xrot);
                                    } else if (Math.abs(xrot % 30) > 28.0 && Math.abs(Math.abs(xrot % 30) - 30) < 2.0) {
                                        xrot = doDegreeTwo(xrot);
                                    }
                                    Log.v("hao", "xrot):" + xrot);
                                    if (!twoD) {
                                        sendMessage(memberNum);
                                    }
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                        } else if (xrot - xClick == -60) {
                            //向下60
                            if (Math.abs(upRotateX) == 180) {
                                anim = ValueAnimator.ofFloat(0, -600);
                            } else {
                                anim = ValueAnimator.ofFloat(0, 600);
                            }

                            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    mRotate = (float) animation.getAnimatedValue() / 5 / 60;
                                    Log.v("haohao", "rotate:  " + mRotate * 5 + "cube.mFaceWidth-upX+136  " + (cube.mFaceWidth - upX + 136));
                                    xrot += mRotate;
                                    Log.v("hao", "rotate:   " + mRotate + "    xrot:   " + xrot);

                                }
                            });

                            anim.setDuration(1000);
                            anim.start();
                            anim.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    Log.v("hao", " onAnimationEnd()");
                                    if (Math.abs(xrot % 30) < 2.0) {
                                        xrot = doDegreeTwo(xrot);
                                    } else if (Math.abs(xrot % 30) > 28.0 && Math.abs(Math.abs(xrot % 30) - 30) < 2.0) {
                                        xrot = doDegreeTwo(xrot);
                                    }
                                    Log.v("hao", "xrot):" + xrot);
                                    if (!twoD) {
                                        sendMessage(memberNum);
                                    }
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                        }

                        if (yrot - yClick == 30) {
                            Log.v("hao", "向左30");
                            //向左30
                            if (Math.abs(upRotateX) == 180) {
                                anim = ValueAnimator.ofFloat(0, -300);
                            } else {
                                anim = ValueAnimator.ofFloat(0, 300);
                            }

                            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    mRotate = (float) animation.getAnimatedValue() / 5 / 60;
                                    yrot += mRotate;

                                }
                            });

                            anim.setDuration(1000);
                            anim.start();
                            anim.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    Log.v("hao", " onAnimationEnd()");
                                    if (Math.abs(yrot % 30) < 2.0) {
                                        yrot = doDegreeTwo(yrot);
                                    } else if (Math.abs(yrot % 30) > 28.0 && Math.abs(Math.abs(yrot % 30) - 30) < 2.0) {
                                        yrot = doDegreeTwo(yrot);
                                    }
                                    Log.v("hao", "yrot" + yrot);
                                    sendMessage(memberNum);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                        } else if (yrot - yClick == -30) {
                            //向右30
                            if (Math.abs(upRotateX) == 180) {
                                anim = ValueAnimator.ofFloat(0, -300);
                            } else {
                                anim = ValueAnimator.ofFloat(0, 300);
                            }

                            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    mRotate = (float) animation.getAnimatedValue() / 5 / 60;
                                    yrot += mRotate;

                                }
                            });

                            anim.setDuration(1000);
                            anim.start();
                            anim.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    Log.v("hao", " onAnimationEnd()");
                                    if (Math.abs(yrot % 30) < 2.0) {
                                        yrot = doDegreeTwo(yrot);
                                    } else if (Math.abs(yrot % 30) > 28.0 && Math.abs(Math.abs(yrot % 30) - 30) < 2.0) {
                                        yrot = doDegreeTwo(yrot);
                                    }
                                    Log.v("hao", "yrot" + yrot);
                                    sendMessage(memberNum);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                        } else if (yrot - yClick == 60) {
                            //向左60
                            if (Math.abs(upRotateX) == 180) {
                                anim = ValueAnimator.ofFloat(0, -600);
                            } else {
                                anim = ValueAnimator.ofFloat(0, 600);
                            }

                            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    mRotate = (float) animation.getAnimatedValue() / 5 / 60;
                                    yrot -= mRotate;

                                }
                            });

                            anim.setDuration(1000);
                            anim.start();
                            anim.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    Log.v("hao", " onAnimationEnd()");
                                    if (Math.abs(yrot % 30) < 2.0) {
                                        yrot = doDegreeTwo(yrot);
                                    } else if (Math.abs(yrot % 30) > 28.0 && Math.abs(Math.abs(yrot % 30) - 30) < 2.0) {
                                        yrot = doDegreeTwo(yrot);
                                    }
                                    Log.v("hao", "yrot" + yrot);
                                    sendMessage(memberNum);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                        } else if (yrot - yClick == -60) {
                            //向右60
                            if (Math.abs(upRotateX) == 180) {
                                anim = ValueAnimator.ofFloat(0, -600);
                            } else {
                                anim = ValueAnimator.ofFloat(0, 600);
                            }

                            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    mRotate = (float) animation.getAnimatedValue() / 5 / 60;
                                    yrot += mRotate;

                                }
                            });

                            anim.setDuration(1000);
                            anim.start();
                            anim.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    Log.v("hao", " onAnimationEnd()");
                                    if (Math.abs(yrot % 30) < 2.0) {
                                        yrot = doDegreeTwo(yrot);
                                    } else if (Math.abs(yrot % 30) > 28.0 && Math.abs(Math.abs(yrot % 30) - 30) < 2.0) {
                                        yrot = doDegreeTwo(yrot);
                                    }
                                    Log.v("hao", "yrot" + yrot);
                                    sendMessage(memberNum);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                        }

                    }


                } else {
                    //旋转动画
                    upRotateX = xrot;
                    upRotateY = yrot;
                    if (Math.abs(vx) > Math.abs(vy)) {

                            if (vx > 0) {
                                Log.v("hao", "向右滑动");
                                //向右滑动
                                //                        downYrote += 90;
                                //                        yrot = downYrote;
                                //发送消息，面不动
                                //                            sendRotateMessage(7,0,false);
                                ValueAnimator anim;
                                if (!touming) {
                                    //固定旋转90度
                                    if (Math.abs(upRotateX) == 180) {
                                        anim = ValueAnimator.ofFloat(0, -900);
                                    } else {
                                        anim = ValueAnimator.ofFloat(0, 900);
                                    }

                                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        @Override
                                        public void onAnimationUpdate(ValueAnimator animation) {
                                            mRotate = (float) animation.getAnimatedValue() / 5 / 60;
                                            Log.v("haohao", "rotate:  " + mRotate * 5 + "cube.mFaceWidth-upX+136  " + (cube.mFaceWidth - upX + 136));
                                            yrot += mRotate;
                                            Log.v("hao", "rotate:   " + mRotate + "    yrot:   " + yrot);

                                        }
                                    });

                                    anim.setDuration(1000);
                                    anim.start();
                                    anim.addListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            Log.v("hao", " onAnimationEnd()");
                                            yrot = doDegree(yrot);
                                            Log.v("hao", "yrot" + yrot);
                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animation) {

                                        }
                                    });
                                    Log.v("hao", "修改后yrot:  " + yrot);
                                } else {
                                    //向右旋转30度
                                    //上下左右只能旋转60度
                                    if (yrot < (yClick + 60)) {
                                        if (Math.abs(upRotateX) == 180) {
                                            anim = ValueAnimator.ofFloat(0, -300);
                                        } else {
                                            anim = ValueAnimator.ofFloat(0, 300);
                                        }
                                        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                            @Override
                                            public void onAnimationUpdate(ValueAnimator animation) {
                                                mRotate = (float) animation.getAnimatedValue() / 5 / 60;
                                                Log.v("haohao", "rotate:  " + mRotate * 5 + "cube.mFaceWidth-upX+136  " + (cube.mFaceWidth - upX + 136));
                                                yrot += mRotate;
                                                Log.v("hao", "rotate:   " + mRotate + "    yrot:   " + yrot);

                                            }
                                        });


                                        anim.setDuration(1000);
                                        anim.start();
                                        anim.addListener(new Animator.AnimatorListener() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {

                                            }

                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                Log.v("hao", " onAnimationEnd()");
                                                if (Math.abs(yrot % 30) < 2.0) {
                                                    yrot = doDegreeTwo(yrot);
                                                } else if (Math.abs(yrot % 30) > 28.0 && Math.abs(Math.abs(yrot % 30) - 30) < 2.0) {
                                                    yrot = doDegreeTwo(yrot);
                                                }
                                                Log.v("hao", "yrot" + yrot);
                                            }

                                            @Override
                                            public void onAnimationCancel(Animator animation) {

                                            }

                                            @Override
                                            public void onAnimationRepeat(Animator animation) {

                                            }
                                        });
                                    }
                                }

                            } else {
                                Log.v("hao", "向左滑动");
                                //发送消息，面不动
                                //                            sendRotateMessage(7,0,false);
                                ValueAnimator anim;
                                if (!touming) {
                                    //固定90度
                                    if (Math.abs(upRotateX) == 180) {
                                        anim = ValueAnimator.ofFloat(0, -900);
                                    } else {
                                        anim = ValueAnimator.ofFloat(0, 900);
                                    }
                                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        @Override
                                        public void onAnimationUpdate(ValueAnimator animation) {
                                            mRotate = (float) animation.getAnimatedValue() / 5 / 60;
                                            Log.v("haohao", "rotate:  " + mRotate * 5 + "cube.mFaceWidth-upX+136  " + (cube.mFaceWidth - upX + 136));
                                            yrot -= mRotate;
                                            Log.v("hao", "rotate:   " + mRotate + "    yrot:   " + yrot);

                                        }
                                    });

                                    anim.setDuration(1000);
                                    anim.start();
                                    anim.addListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            Log.v("hao", " onAnimationEnd()");
                                            yrot = doDegree(yrot);
                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animation) {

                                        }
                                    });
                                    Log.v("hao", "修改后yrot:  " + yrot);
                                    Log.v("hao", "修改后yrot:  " + yrot);
                                } else {
                                    //向左旋转30度
                                    if (yrot > (yClick - 60)) {
                                        if (Math.abs(upRotateX) == 180) {
                                            anim = ValueAnimator.ofFloat(0, -300);
                                        } else {
                                            anim = ValueAnimator.ofFloat(0, 300);
                                        }

                                        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                            @Override
                                            public void onAnimationUpdate(ValueAnimator animation) {
                                                mRotate = (float) animation.getAnimatedValue() / 5 / 60;
                                                Log.v("haohao", "rotate:  " + mRotate * 5 + "cube.mFaceWidth-upX+136  " + (cube.mFaceWidth - upX + 136));
                                                yrot -= mRotate;
                                                Log.v("hao", "rotate:   " + mRotate + "    yrot:   " + yrot);

                                            }
                                        });

                                        anim.setDuration(1000);
                                        anim.start();
                                        anim.addListener(new Animator.AnimatorListener() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {

                                            }

                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                Log.v("hao", " onAnimationEnd()");
                                                if (Math.abs(yrot % 30) < 2.0) {
                                                    yrot = doDegreeTwo(yrot);
                                                } else if (Math.abs(yrot % 30) > 28.0 && Math.abs(Math.abs(yrot % 30) - 30) < 2.0) {
                                                    yrot = doDegreeTwo(yrot);
                                                }
                                                Log.v("hao", "yrot" + yrot);
                                            }

                                            @Override
                                            public void onAnimationCancel(Animator animation) {

                                            }

                                            @Override
                                            public void onAnimationRepeat(Animator animation) {

                                            }
                                        });
                                    }

                                }
                            }
                    } else if (Math.abs(vy) > Math.abs(vx)) {
                        if (vy > 0) {
                            Log.v("hao", "向下滑动");

//                            yrotateNext = yrot;
//                            xrotateNext = xrot + 90;
//                            //发送消息，面翻转，使最前边的面都是正面
//                            int nine = FaceUtil.faceNine(xrotateNext, yrotateNext, upRotateX, upRotateY);
//                            //得到翻转度数
//                            float rotateF = FaceUtil.rotate(xrotateNext, yrotateNext, upRotateX, upRotateY);
//                            //true翻转4个面，false翻转2个面
//                            boolean fourFace = FaceUtil.four(xrotateNext, yrotateNext, upRotateX, upRotateY);
//                            sendRotateMessage(nine,rotateF,fourFace);


                            //向下滑动
                            //没特效变换
                            //                        downXrote += 90;
                            //                        xrot = downXrote;
                            //用动画变换
                            ValueAnimator anim = ValueAnimator.ofFloat(0, 900);
                            if (!touming) {
                                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        mRotate = (float) animation.getAnimatedValue() / 5 / 60;
                                        Log.v("haohao", "rotate:  " + mRotate * 5 + "cube.mFaceWidth-upX+136  " + (cube.mFaceWidth - upX + 136));
                                        xrot += mRotate;
                                        Log.v("hao", "rotate:   " + mRotate + "    xrot:   " + xrot);
                                    }
                                });

                                anim.setDuration(1000);


                                anim.start();
                                anim.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        Log.v("hao", " onAnimationEnd()");
                                        xrot = doDegree(xrot);
                                        //发送消息，面翻转后，使其他面的值为false（leftZ,topZ,fontyZ）
                                        int nine = FaceUtil.faceNine(xrot, yrot, upRotateX, upRotateY);
                                        float rotateF = FaceUtil.rotate(xrot, yrot, upRotateX, upRotateY);
                                        boolean fourFace = FaceUtil.four(xrot, yrot, upRotateX, upRotateY);
                                        sendRotateMessage(nine,rotateF,fourFace);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                });
                                Log.v("hao", "修改后xrot:  " + xrot);
                            } else {
                                //向下旋转30度
                                if (xrot < (xClick + 60)) {
                                    if (Math.abs(upRotateX) == 180) {
                                        anim = ValueAnimator.ofFloat(0, -300);
                                    } else {
                                        anim = ValueAnimator.ofFloat(0, 300);
                                    }

                                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        @Override
                                        public void onAnimationUpdate(ValueAnimator animation) {
                                            mRotate = (float) animation.getAnimatedValue() / 5 / 60;
                                            Log.v("haohao", "rotate:  " + mRotate * 5 + "cube.mFaceWidth-upX+136  " + (cube.mFaceWidth - upX + 136));
                                            xrot += mRotate;
                                            Log.v("hao", "rotate:   " + mRotate + "    xrot:   " + xrot);

                                        }
                                    });

                                    anim.setDuration(1000);
                                    anim.start();
                                    anim.addListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            Log.v("hao", " onAnimationEnd()");
                                            if (Math.abs(xrot % 30) < 2.0) {
                                                xrot = doDegreeTwo(xrot);
                                            } else if (Math.abs(xrot % 30) > 28.0 && Math.abs(Math.abs(xrot % 30) - 30) < 2.0) {
                                                xrot = doDegreeTwo(xrot);
                                            }
                                            Log.v("hao", "xrot):" + xrot);

                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animation) {

                                        }
                                    });
                                }
                            }

                        } else {
                            Log.v("hao", "向上滑动");
//                            yrotateNext = yrot;
//                            xrotateNext = xrot - 90;
//                            //发送消息，面翻转，使最前边的面都是正面
//                            int nine = FaceUtil.faceNine(xrotateNext, yrotateNext, upRotateX, upRotateY);
//                            //得到翻转度数
//                            float rotateF = FaceUtil.rotate(xrotateNext, yrotateNext, upRotateX, upRotateY);
//                            //true翻转4个面，false翻转2个面
//                            boolean fourFace = FaceUtil.four(xrotateNext, yrotateNext, upRotateX, upRotateY);
//                            Log.v("zhao", "fourFace: "+fourFace+"  rotateF:  "+rotateF+"  face:  "+nine);
//                            sendRotateMessage(nine,rotateF,fourFace);
                            //向上滑动
                            //                        downXrote -= 90;
                            //                        xrot = downXrote;
                            ValueAnimator anim = ValueAnimator.ofFloat(0, 900);
                            if (!touming) {
                                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        mRotate = (float) animation.getAnimatedValue() / 5 / 60;
                                        Log.v("haohao", "rotate:  " + mRotate * 5 + "cube.mFaceWidth-upX+136  " + (cube.mFaceWidth - upX + 136));
                                        xrot -= mRotate;
                                        Log.v("hao", "rotate:   " + mRotate + "    xrot:   " + xrot);

                                    }
                                });

                                anim.setDuration(1000);
                                anim.start();
                                anim.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        Log.v("hao", " onAnimationEnd()");
                                        xrot = doDegree(xrot);
                                        //发送消息，面翻转后，使其他面的值为false（leftZ,topZ,fontyZ）
                                        int nine = FaceUtil.faceNine(xrot, yrot, upRotateX, upRotateY);
                                        float rotateF = FaceUtil.rotate(xrot, yrot, upRotateX, upRotateY);
                                        boolean fourFace = FaceUtil.four(xrot, yrot, upRotateX, upRotateY);
                                        Log.v("zhao", "fourFace: "+fourFace+"  rotateF:  "+rotateF+"  face:  "+nine);

                                        sendRotateMessage(nine,rotateF,fourFace);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                });
                                Log.v("hao", "修改后xrot:  " + xrot);
                            } else {
                                //向上旋转30度
                                if (xrot > (xClick - 60)) {
                                    if (Math.abs(upRotateX) == 180) {
                                        anim = ValueAnimator.ofFloat(0, -300);
                                    } else {
                                        anim = ValueAnimator.ofFloat(0, 300);
                                    }

                                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        @Override
                                        public void onAnimationUpdate(ValueAnimator animation) {
                                            mRotate = (float) animation.getAnimatedValue() / 5 / 60;
                                            Log.v("haohao", "rotate:  " + mRotate * 5 + "cube.mFaceWidth-upX+136  " + (cube.mFaceWidth - upX + 136));
                                            xrot -= mRotate;
                                            Log.v("hao", "rotate:   " + mRotate + "    xrot:   " + xrot);

                                        }
                                    });

                                    anim.setDuration(1000);
                                    anim.start();
                                    anim.addListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            Log.v("hao", " onAnimationEnd()");
                                            if (Math.abs(xrot % 30) < 2.0) {
                                                xrot = doDegreeTwo(xrot);
                                            } else if (Math.abs(xrot % 30) > 28.0 && Math.abs(Math.abs(xrot % 30) - 30) < 2.0) {
                                                xrot = doDegreeTwo(xrot);
                                            }
                                            Log.v("hao", "xrot):" + xrot);
                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animation) {

                                        }
                                    });
                                }
                            }
                        }

                    }
                }
            }
        }


        upperArea = this.getHeight() / 10;
        int lowerArea = this.getHeight() - upperArea;


        if (y > lowerArea) {
            if (light) {
                light = false;
            } else {
                light = true;
            }
        }

        //        if(xrot < 0 && xrot%180) {
        //
        //        }
        oldX = x;
        oldY = y;
        //        Log.v("hao", " oldX =            " + oldX + "              oldY:              " + oldY);
        return true;
    }

    private float doDegree(float y) {
        int a = (int) (y / 90);
        if (a == 0) {

            if (y >= 0) {
                if (Math.abs(y) > Math.abs(y - 90)) {
                    y = 90;
                } else {
                    y = 0;
                }

            } else if (y < 0) {
                if (Math.abs(y) > Math.abs(90 + y)) {
                    y = -90;
                } else {
                    y = 0;
                }
            }

        } else if (y > 0) {
            if (Math.abs(a * 90 - y) > Math.abs((a + 1) * 90 - y)) {
                y = (a + 1) * 90;
            } else {
                y = a * 90;
            }
        } else if (y < 0) {
            if (Math.abs(a * 90 - y) > Math.abs((a - 1) * 90 - y)) {
                y = (a - 1) * 90;
            } else {
                y = a * 90;
            }
        }

        return y;

    }

    private float doDegreeTwo(float y) {
        int a = (int) (y / 30);
        if (a == 0) {

            if (y >= 0) {
                if (Math.abs(y) > Math.abs(y - 30)) {
                    y = 30;
                } else {
                    y = 0;
                }

            } else if (y < 0) {
                if (Math.abs(y) > Math.abs(30 + y)) {
                    y = -30;
                } else {
                    y = 0;
                }
            }

        } else if (y > 0) {
            if (Math.abs(a * 30 - y) > Math.abs((a + 1) * 30 - y)) {
                y = (a + 1) * 30;
            } else {
                y = a * 30;
            }
        } else if (y < 0) {
            if (Math.abs(a * 30 - y) > Math.abs((a - 1) * 30 - y)) {
                y = (a - 1) * 30;
            } else {
                y = a * 30;
            }
        }

        return y;

    }

    int i = 1;
    int memberNum;

    private void sendMessage(int number) {
        if (onclick) {
            memberNum = number;
        }

        Log.v("hao", "初始： onclick" + onclick + "   memberNum:  " + memberNum + "   number: " + number);
        if (i % 2 == 1 && memberNum == number) {
            if (onclick) {
                EventBus.getDefault().post(new MessageEvent(number, 0, false));
                touming = true;
            }
            onclick = false;
            i++;
            Log.v("hao", "初始： onclick" + onclick + "   memberNum:  " + memberNum + "   number: " + number);
        } else if (i % 2 == 0 && number == memberNum) {
            EventBus.getDefault().post(new MessageEvent(number * 10, 0, false));
            Log.v("hao", " sendMessage()   " + number * 10);
            onclick = true;
            touming = false;
            i++;
        }

        Log.v("hao", "最后i:" + i);
    }

    private void sendRotateMessage(int number, float message, boolean four) {
        EventBus.getDefault().post(new MessageEvent(number * 100, message, four));
    }
}
