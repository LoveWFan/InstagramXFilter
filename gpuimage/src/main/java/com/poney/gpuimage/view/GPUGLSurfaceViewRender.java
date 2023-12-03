package com.poney.gpuimage.view;

import android.opengl.GLSurfaceView;
import android.util.Log;

import com.poney.gpuimage.glrender.GPUNativeRender;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @datetime: 12/1/23
 * @desc:
 */
public class GPUGLSurfaceViewRender implements GLSurfaceView.Renderer {
    private static final String TAG = "GPUGLSurfaceViewRender";
    private int mSampleType;


    private GPUNativeRender mNativeRender;

    public GPUGLSurfaceViewRender() {
        mNativeRender = new GPUNativeRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mNativeRender.native_OnSurfaceCreated();
        Log.e(TAG, "onSurfaceCreated() called with: GL_VERSION = [" + gl.glGetString(GL10.GL_VERSION) + "]");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mNativeRender.native_OnSurfaceChanged(width, height);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mNativeRender.native_OnDrawFrame();

    }

    public void init() {
        mNativeRender.native_Init();
    }

    public void unInit() {
        mNativeRender.native_UnInit();
    }

    public void setParamsInt(int paramType, int value0, int value1) {
        if (paramType == GPUNativeRender.SAMPLE_TYPE) {
            mSampleType = value0;
        }
        mNativeRender.native_SetParamsInt(paramType, value0, value1);
    }


    public void setImageData(int format, int width, int height, byte[] bytes) {
        mNativeRender.native_SetImageData(format, width, height, bytes);
    }

    public void setImageDataWithIndex(int index, int format, int width, int height, byte[] bytes) {
        mNativeRender.native_SetImageDataWithIndex(index, format, width, height, bytes);
    }

    public void setAudioData(short[] audioData) {
        mNativeRender.native_SetAudioData(audioData);
    }

    public int getSampleType() {
        return mSampleType;
    }

    public void updateTransformMatrix(float rotateX, float rotateY, float scaleX, float scaleY) {
        mNativeRender.native_UpdateTransformMatrix(rotateX, rotateY, scaleX, scaleY);
    }

}
