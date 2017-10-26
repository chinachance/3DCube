package com.haohao.framwork.a3dcube;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLUtils;
import android.util.Log;
import android.view.View;

import com.haohao.framwork.a3dcube.view.EmptyView;
import com.haohao.framwork.a3dcube.view.HistogramView;
import com.haohao.framwork.a3dcube.view.LineGraphicView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Ma1 on 2017/4/10.
 */

public class Cube extends View {

    //4.11
    /**
     * 缓冲区的顶点
     */
    private FloatBuffer vertexBuffer;
    /**
     * 缓冲纹理坐标
     */
    private FloatBuffer textureBuffer;
    /**
     * 缓冲指数
     */
    private ByteBuffer indexBuffer;
    /**
     * 缓冲法线
     */
    private FloatBuffer normalBuffer;

    /**
     * 结构指针
     */
    private int[] textures = new int[3];

    /**
     * 最初的顶点定义
     */
    private float vertices[] = {
            //Vertices according to faces
            -1.0f, -1.0f, 1.0f, //v0
            1.0f, -1.0f, 1.0f,    //v1
            -1.0f, 1.0f, 1.0f,    //v2
            1.0f, 1.0f, 1.0f,    //v3

            1.0f, -1.0f, 1.0f,    // ...
            1.0f, -1.0f, -1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, -1.0f,

            1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,

            -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, 1.0f,
            -1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, 1.0f,

            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,

            -1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
    };

    /**
     * 最初的法线照明计算
     */
    private float normals[] = {
            // Normals
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,

            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,

            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,

            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,

            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,

            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
    };

    /**
     * 最初的纹理坐标 (u, v)
     */
    private float texture[] = {
            //映射为顶点坐标
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,

            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,

            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,

            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,

            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,

            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };

    /**
     * 初始指标定义
     */
    private byte indices[] = {

            0, 1, 3, 0, 3, 2,        // Face front
            4, 5, 7, 4, 7, 6,        // Face right
            8, 9, 11, 8, 11, 10,    // ...
            12, 13, 15, 12, 15, 14,
            16, 17, 19, 16, 19, 18,
            20, 21, 23, 20, 23, 22,
    };

    /**
     * The Cube constructor.
     * <p>
     * Initiate the buffers.
     */
    private int[] imageFileIDs = {  // Image file IDs
            R.mipmap.m1,
            R.mipmap.m2,
            R.mipmap.m3,
            R.mipmap.m1,
            R.mipmap.m2,
            R.mipmap.m7
    };
    private int[] textureIDs = new int[6];
    private int[] tIDs = new int[6];

    private Bitmap[] bitmap = new Bitmap[6];
    private Bitmap[] bitmapEmpty = new Bitmap[6];
    private FloatBuffer texBuffer;
    public float mFaceWidth;
    public float mFaceHeight;
    private Context mContext;
    private Bitmap bit1;
    private Bitmap bit2;
    private Bitmap bit3;
    private Bitmap bit4;
    private Bitmap bit5;
    private Bitmap bit6;
    LineGraphicView v1;
    ArrayList<Double> yList;
    private final EmptyView mEmptyView;

    public Cube(Context context) {
        super(context);
        mContext = context;
        //注册事件
        EventBus.getDefault().register(this);

        mEmptyView = new EmptyView(getContext());
        mEmptyView.setBackgroundColor(Color.TRANSPARENT);
        //end

        ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuf.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);


        byteBuf = ByteBuffer.allocateDirect(texture.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        textureBuffer = byteBuf.asFloatBuffer();
        textureBuffer.put(texture);
        textureBuffer.position(0);


        byteBuf = ByteBuffer.allocateDirect(normals.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        normalBuffer = byteBuf.asFloatBuffer();
        normalBuffer.put(normals);
        normalBuffer.position(0);


        indexBuffer = ByteBuffer.allocateDirect(indices.length);
        indexBuffer.put(indices);
        indexBuffer.position(0);

    }

    private boolean touming;
    private boolean toumingT;
    private boolean toumingF;
    private boolean toumingL;
    //面为正
    private boolean fontZ;
    private boolean leftZ;
    private boolean topZ;
    private boolean fourFace;


    private void initChartView2() {
        HistogramView view = new HistogramView(getContext());
        HistogramView view1 = new HistogramView(getContext());
        HistogramView view2 = new HistogramView(getContext());
        if (!touming) {
            setbackground(view, false);
            setbackground(view1, false);
            setbackground(view2, false);
            bit4 = getViewBitmap(view);
            bit5 = getViewBitmap(view1);
            bit6 = getViewBitmap(view2);
        } else {
            if (toumingL) {
                setbackground(view, true);
                bit4 = getViewBitmap(view);
                bit5 = getViewBitmap(mEmptyView);
                bit6 = getViewBitmap(mEmptyView);
            } else if (toumingF) {
                setbackground(view1, true);
                bit5 = getViewBitmap(view);
                bit4 = getViewBitmap(mEmptyView);
                bit6 = getViewBitmap(mEmptyView);
            } else if (toumingT) {
                setbackground(view2, true);
                bit6 = getViewBitmap(view);
                bit5 = getViewBitmap(mEmptyView);
                bit4 = getViewBitmap(mEmptyView);
            }
        }

    }

    private void initChartView1() {
        v1 = new LineGraphicView(getContext());
        LineGraphicView v2 = new LineGraphicView(getContext());
        LineGraphicView v3 = new LineGraphicView(getContext());


        yList = new ArrayList<>();
        yList.add(2.103);
        yList.add(4.05);
        yList.add(6.60);
        yList.add(3.08);
        yList.add(4.32);
        yList.add(2.0);
        yList.add(5.0);

        ArrayList<String> xRawDatas = new ArrayList<>();
        xRawDatas.add("05-19");
        xRawDatas.add("05-20");
        xRawDatas.add("05-21");
        xRawDatas.add("05-22");
        xRawDatas.add("05-23");
        xRawDatas.add("05-24");
        xRawDatas.add("05-25");
        xRawDatas.add("05-26");
        v1.setData(yList, xRawDatas, 8, 2);
        v2.setData(yList, xRawDatas, 8, 2);
        v3.setData(yList, xRawDatas, 8, 2);


        if (!touming) {
            setbackground(v1, false);
            setbackground(v2, false);
            setbackground(v3, false);
            bit1 = getViewBitmap(v1);
            bit2 = getViewBitmap(v2);
            bit3 = getViewBitmap(v3);
        } else {
            if (toumingF) {
                setbackground(v1, true);
                bit1 = getViewBitmap(v1);
                bit2 = getViewBitmap(mEmptyView);
                bit3 = getViewBitmap(mEmptyView);
            } else if (toumingL) {
                setbackground(v2, true);
                bit1 = getViewBitmap(mEmptyView);
                bit2 = getViewBitmap(v2);
                bit3 = getViewBitmap(mEmptyView);
            } else if (toumingT) {
                setbackground(v3, true);
                bit1 = getViewBitmap(mEmptyView);
                bit2 = getViewBitmap(mEmptyView);
                bit3 = getViewBitmap(v3);
            }
        }


    }

    private void setbackground(View v, boolean istouming) {
        if (istouming) {
            v.setBackgroundColor(Color.TRANSPARENT);
        } else {
            v.setBackgroundColor(Color.BLACK);
        }
    }

    private void bitmapDrawFace() {
        //初始化表格
        initChartView1();
        initChartView2();
        for (int face = 0; face < 6; face++) {
            if (face == 0) {
                this.bitmap[face] = bit1;
            } else if (face == 1) {
                this.bitmap[face] = bit4;
            } else if (face == 2) {
                this.bitmap[face] = bit5;
            } else if (face == 3) {
                this.bitmap[face] = bit2;
            } else if (face == 4) {
                this.bitmap[face] = bit3;
            } else if (face == 5) {
                this.bitmap[face] = bit6;
            }

            Log.v("hao", "bitmap[face]:      " + this.bitmap[face]);
            int imgWidth = this.bitmap[face].getWidth();
            int imgHeight = this.bitmap[face].getHeight();
            mFaceWidth = 2.0f;
            mFaceHeight = 2.0f;
            // Adjust for aspect ratio
            if (imgWidth > imgHeight) {
                mFaceHeight = mFaceHeight * imgHeight / imgWidth;
            } else {
                mFaceWidth = mFaceWidth * imgWidth / imgHeight;
            }
            float faceLeft = -mFaceWidth / 2;
            float faceRight = -faceLeft;
            float faceTop = mFaceHeight / 2;
            float faceBottom = -faceTop;
            float[] vertices = {
                    faceLeft, faceBottom, 0.0f,  // 0. left-bottom-front
                    faceRight, faceBottom, 0.0f,  // 1. right-bottom-front
                    faceLeft, faceTop, 0.0f,  // 2. left-top-front
                    faceRight, faceTop, 0.0f,  // 3. right-top-front
            };
            vertexBuffer.put(vertices);  // Populate
        }
        vertexBuffer.position(0);    // Rewind

        // Allocate texture buffer. An float has 4 bytes. Repeat for 6 faces.
        float[] texCoords = {
                0.0f, 1.0f,  // A. left-bottom
                1.0f, 1.0f,  // B. right-bottom
                0.0f, 0.0f,  // C. left-top
                1.0f, 0.0f   // D. right-top
        };
        ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4 * 6);
        tbb.order(ByteOrder.nativeOrder());
        texBuffer = tbb.asFloatBuffer();
        for (int face = 0; face < 6; face++) {
            texBuffer.put(texCoords);
        }
        texBuffer.position(0);   // Rewind


    }


    private void bitmapDrawEmptyFace() {
        for (int face = 0; face < 6; face++) {
            bitmapEmpty[face] = getViewBitmap(mEmptyView);
            Log.v("hao", "bitmap[face]:      " + this.bitmap[face]);
            int imgWidth = this.bitmapEmpty[face].getWidth();
            int imgHeight = this.bitmapEmpty[face].getHeight();
            mFaceWidth = 2.0f;
            mFaceHeight = 2.0f;
            if (imgWidth > imgHeight) {
                mFaceHeight = mFaceHeight * imgHeight / imgWidth;
            } else {
                mFaceWidth = mFaceWidth * imgWidth / imgHeight;
            }
            float faceLeft = -mFaceWidth / 2;
            float faceRight = -faceLeft;
            float faceTop = mFaceHeight / 2;
            float faceBottom = -faceTop;

            float[] vertices = {
                    faceLeft, faceBottom, 0.0f,  // 0. left-bottom-front
                    faceRight, faceBottom, 0.0f,  // 1. right-bottom-front
                    faceLeft, faceTop, 0.0f,  // 2. left-top-front
                    faceRight, faceTop, 0.0f,  // 3. right-top-front
            };
            vertexBuffer.put(vertices);  // Populate
        }
        vertexBuffer.position(0);    // Rewind
        float[] texCoords = {
                0.0f, 1.0f,  // A. left-bottom
                1.0f, 1.0f,  // B. right-bottom
                0.0f, 0.0f,  // C. left-top
                1.0f, 0.0f   // D. right-top
        };
        ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4 * 6);
        tbb.order(ByteOrder.nativeOrder());
        texBuffer = tbb.asFloatBuffer();
        for (int face = 0; face < 6; face++) {
            texBuffer.put(texCoords);
        }
        texBuffer.position(0);   // Rewind


    }


    private float cubeHalfSize = 1.0f;
    private boolean front;
    private boolean right;
    private boolean left;
    private boolean back;
    private boolean top;
    private boolean buttom;

    private boolean load = true;

    public void draw(GL10 gl, int filter) {

        if (load) {
            bitmapDrawFace();
            loadGLTexture(gl);
            load = false;
            //加载一个空白立方体，使边框白色显出
            bitmapDrawEmptyFace();
            loadEmptyGLTexture(gl);
        }

        //根据纹理过滤集绑定纹理
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[filter]);

        gl.glFrontFace(GL10.GL_CCW);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer);


        // front
        gl.glPushMatrix();
        gl.glTranslatef(0f, 0f, cubeHalfSize * 3 / 4);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[0]);
        gl.glTranslatef(0f, 0f, -cubeHalfSize * 7 / 20);
        if (front) {
            gl.glTranslatef(0f, 0f, -cubeHalfSize / 4);
        }
        if (!front) {
            gl.glTranslatef(0f, 0f, cubeHalfSize / 4);
        }
        if (back) {
            gl.glTranslatef(0f, 0f, -cubeHalfSize * 7 / 20);
            gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
        }
        if (!back) {
            gl.glTranslatef(0f, 0f, cubeHalfSize * 7 / 20);
        }
        if (fontZ) {
            gl.glRotatef(rotateF, 0.0f, 0.0f, 1.0f);
        }
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

        gl.glPopMatrix();

        //空白面
        gl.glPushMatrix();
        gl.glTranslatef(0f, 0f, cubeHalfSize);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, tIDs[0]);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        gl.glPopMatrix();

        // left
        gl.glPushMatrix();

        gl.glRotatef(270.0f, 0f, 1f, 0f);
        gl.glTranslatef(0f, 0f, cubeHalfSize * 3 / 4);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[1]);
        gl.glTranslatef(0f, 0f, -cubeHalfSize * 7 / 20);
        if (left) {
            gl.glTranslatef(0f, 0f, -cubeHalfSize / 4);
        }
        if (!left) {
            gl.glTranslatef(0f, 0f, cubeHalfSize / 4);
        }
        if (right) {
            gl.glTranslatef(0f, 0f, -cubeHalfSize * 7 / 20);
            gl.glRotatef(180.0f, 0.0f, 2.0f, 0.0f);
        }
        if (!right) {
            gl.glTranslatef(0f, 0f, cubeHalfSize * 7 / 20);
        }
        if (leftZ) {
            gl.glRotatef(rotateF, 0.0f, 0.0f, 1.0f);
        }
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
        gl.glPopMatrix();


        //
        gl.glPushMatrix();

        gl.glRotatef(270.0f, 0f, 1f, 0f);
        gl.glTranslatef(0f, 0f, cubeHalfSize);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, tIDs[1]);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
        gl.glPopMatrix();

        // back
        gl.glPushMatrix();

        gl.glRotatef(180.0f, 0f, 1f, 0f);
        gl.glTranslatef(0f, 0f, cubeHalfSize * 3 / 4);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[2]);
        gl.glTranslatef(0f, 0f, -cubeHalfSize * 7 / 20);
        if (back) {
            gl.glTranslatef(0f, 0f, -cubeHalfSize / 4);
        }
        if (!back) {
            gl.glTranslatef(0f, 0f, cubeHalfSize / 4);
        }
        if (front) {
            gl.glTranslatef(0f, 0f, -cubeHalfSize * 7 / 20);
            gl.glRotatef(180.0f, 0.0f, 2.0f, 0.0f);
        }
        if (!front) {
            gl.glTranslatef(0f, 0f, cubeHalfSize * 7 / 20);
        }
        if (fontZ) {
            gl.glRotatef(rotateF, 0.0f, 0.0f, 1.0f);
        }
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 8, 4);
        gl.glPopMatrix();

        //
        gl.glPushMatrix();

        gl.glRotatef(180.0f, 0f, 1f, 0f);
        gl.glTranslatef(0f, 0f, cubeHalfSize);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, tIDs[2]);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 8, 4);
        gl.glPopMatrix();

        // right
        gl.glPushMatrix();

        gl.glRotatef(90.0f, 0f, 1f, 0f);
        gl.glTranslatef(0f, 0f, cubeHalfSize * 3 / 4);
        gl.glTranslatef(0f, 0f, -cubeHalfSize * 7 / 20);
        if (right) {
            gl.glTranslatef(0f, 0f, -cubeHalfSize / 4);
        }
        if (!right) {
            gl.glTranslatef(0f, 0f, cubeHalfSize / 4);
        }
        if (left) {
            gl.glTranslatef(0f, 0f, -cubeHalfSize * 7 / 20);
            gl.glRotatef(180.0f, 0.0f, 2.0f, 0.0f);
        }
        if (!left) {
            gl.glTranslatef(0f, 0f, cubeHalfSize * 7 / 20);
        }
        if (leftZ) {
            gl.glRotatef(rotateF, 0.0f, 0.0f, 1.0f);
        }
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[3]);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 12, 4);
        gl.glPopMatrix();

        //
        gl.glPushMatrix();

        gl.glRotatef(90.0f, 0f, 1f, 0f);
        gl.glTranslatef(0f, 0f, cubeHalfSize);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, tIDs[3]);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 12, 4);
        gl.glPopMatrix();

        // top
        gl.glPushMatrix();

        gl.glRotatef(270.0f, 1f, 0f, 0f);
        gl.glTranslatef(0f, 0f, cubeHalfSize * 3 / 4);
        gl.glTranslatef(0f, 0f, -cubeHalfSize * 7 / 20);
        if (top) {
            gl.glTranslatef(0f, 0f, -cubeHalfSize / 4);
        }
        if (!top) {
            gl.glTranslatef(0f, 0f, cubeHalfSize / 4);
        }
        if (buttom) {
            gl.glTranslatef(0f, 0f, -cubeHalfSize * 7 / 20);
            gl.glRotatef(180.0f, 0.0f, 2.0f, 0.0f);
        }
        if (!buttom) {
            gl.glTranslatef(0f, 0f, cubeHalfSize * 7 / 20);
        }
        if (topZ) {
            gl.glRotatef(rotateF, 0.0f, 0.0f, 1.0f);
        }
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[4]);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 16, 4);
        gl.glPopMatrix();

        //
        gl.glPushMatrix();

        gl.glRotatef(270.0f, 1f, 0f, 0f);
        gl.glTranslatef(0f, 0f, cubeHalfSize);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, tIDs[4]);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 16, 4);
        gl.glPopMatrix();

        // bottom
        gl.glPushMatrix();

        gl.glRotatef(90.0f, 1f, 0f, 0f);
        gl.glTranslatef(0f, 0f, cubeHalfSize * 3 / 4);
        gl.glTranslatef(0f, 0f, -cubeHalfSize * 7 / 20);
        if (buttom) {
            gl.glTranslatef(0f, 0f, -cubeHalfSize / 4);
        }
        if (!buttom) {
            gl.glTranslatef(0f, 0f, cubeHalfSize / 4);
        }
        if (top) {
            gl.glTranslatef(0f, 0f, -cubeHalfSize * 7 / 20);
            gl.glRotatef(180.0f, 0.0f, 2.0f, 0.0f);
        }
        if (!top) {
            gl.glTranslatef(0f, 0f, cubeHalfSize * 7 / 20);
        }
        if (topZ) {
            gl.glRotatef(rotateF, 0.0f, 0.0f, 1.0f);
        }
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[5]);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 20, 4);
        gl.glPopMatrix();

        //
        gl.glPushMatrix();

        gl.glRotatef(90.0f, 1f, 0f, 0f);
        gl.glTranslatef(0f, 0f, cubeHalfSize);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, tIDs[5]);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 20, 4);
        gl.glPopMatrix();

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

    }

    float rotateF;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoonEvent(MessageEvent event) {


        if (event.getMessageId() == 1) {
            //正面点击
            front = true;
            load = true;
            touming = true;
            toumingF = true;
        } else if (event.getMessageId() == 2) {
            //左面点击
            left = true;
            load = true;
            toumingL = true;
            touming = true;
        } else if (event.getMessageId() == 3) {
            //后面点击
            back = true;
            load = true;
            toumingF = true;
            touming = true;
        } else if (event.getMessageId() == 4) {
            //右面点击
            right = true;
            load = true;
            toumingL = true;
            touming = true;
        } else if (event.getMessageId() == 5) {
            //下面点击
            buttom = true;
            load = true;
            toumingT = true;
            touming = true;
        } else if (event.getMessageId() == 6) {
            //上面点击
            top = true;
            load = true;
            toumingT = true;
            touming = true;
        } else if (event.getMessageId() == 10) {
            front = false;
            load = true;
            touming = false;
            toumingF = false;
        } else if (event.getMessageId() == 20) {
            left = false;
            load = true;
            touming = false;
            toumingL = false;
        } else if (event.getMessageId() == 30) {
            back = false;
            load = true;
            touming = false;
            toumingF = false;
        } else if (event.getMessageId() == 40) {
            right = false;
            load = true;
            touming = false;
            toumingL = false;
        } else if (event.getMessageId() == 50) {
            buttom = false;
            load = true;
            touming = false;
            toumingT = false;
        } else if (event.getMessageId() == 60) {
            top = false;
            load = true;
            touming = false;
            toumingT = false;
        } else if (event.getMessageId() == 100 || event.getMessageId() == 300) {
            fontZ = true;
            leftZ = false;
            topZ = false;
            rotateF = event.getMessage();
            fourFace = event.getFourFace();
            if (fourFace) {
                leftZ = true;
            }
            Log.v("hao", "sendmessage: 100");
        } else if (event.getMessageId() == 500 || event.getMessageId() == 600) {
            leftZ = true;
            fontZ = false;
            topZ = false;
            rotateF = event.getMessage();
            fourFace = event.getFourFace();
            if (fourFace) {
                fontZ = true;
            }
            Log.v("hao", "sendmessage: 200   " + fourFace);
        } else if (event.getMessageId() == 200 || event.getMessageId() == 400) {
            topZ = true;
            fontZ = false;
            leftZ = false;
            rotateF = event.getMessage();
            fourFace = event.getFourFace();
            Log.v("hao", "sendmessage: 500");
        } else if (event.getMessageId() == 700) {
            fontZ = false;
            leftZ = false;
            topZ = false;
            Log.v("hao", "sendmessage: 700");

        }
        Log.v("hao", "fonZ: " + fontZ + " leftZ: " + leftZ + "  topZ: " + topZ + " fourface；  " + fourFace + "  rotateF:  " + rotateF);
    }

    public void loadGLTexture(GL10 gl) {

        gl.glGenTextures(6, textureIDs, 0); // Generate texture-ID array for 6 IDs

        // Generate OpenGL texture images
        for (int face = 0; face < 6; face++) {
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[face]);

            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
            // Build Texture from loaded bitmap for the currently-bind texture ID
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap[face], 0);


        }
    }


    public void loadEmptyGLTexture(GL10 gl) {

        gl.glGenTextures(6, tIDs, 0); // Generate texture-ID array for 6 IDs

        // Generate OpenGL texture images
        for (int face = 0; face < 6; face++) {
            gl.glBindTexture(GL10.GL_TEXTURE_2D, tIDs[face]);

            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
            // Build Texture from loaded bitmap for the currently-bind texture ID
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmapEmpty[face], 0);

            //            bitmap[face].recycle();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //取消注册事件
        EventBus.getDefault().unregister(this);
        bit1.recycle();
        bit2.recycle();
    }


    private Bitmap getViewBitmap(View addViewContent) {

        addViewContent.setDrawingCacheEnabled(true);

        addViewContent.measure(
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        addViewContent.layout(0, 0,
                addViewContent.getMeasuredWidth(),
                addViewContent.getMeasuredHeight());

        addViewContent.buildDrawingCache();
        Bitmap cacheBitmap = addViewContent.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        return bitmap;

    }


}
