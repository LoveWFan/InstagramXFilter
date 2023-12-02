package com.poney.gpuimage.view;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @datetime: 12/1/23
 * @desc:
 */
public class GPUGLSurfaceViewRender implements GLSurfaceView.Renderer {


    private int screenW;
    private int screenH;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        screenW = width;
        screenH = height;
        GLES30.glViewport(0, 0, screenW, screenH);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
    }
}
