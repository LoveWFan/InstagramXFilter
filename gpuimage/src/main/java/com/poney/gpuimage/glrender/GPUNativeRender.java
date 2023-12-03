

package com.poney.gpuimage.glrender;

public class GPUNativeRender {
    public static final int SAMPLE_TYPE = 200;


    public static final int SAMPLE_TYPE_KEY_TRANSITIONS_1 = SAMPLE_TYPE + 1;
    public static final int SAMPLE_TYPE_KEY_TRANSITIONS_2 = SAMPLE_TYPE + 2;
    public static final int SAMPLE_TYPE_KEY_TRANSITIONS_3 = SAMPLE_TYPE + 3;
    public static final int SAMPLE_TYPE_KEY_TRANSITIONS_4 = SAMPLE_TYPE + 4;


    static {
        System.loadLibrary("opengl-render");
    }

    public native void native_Init();

    public native void native_UnInit();

    public native void native_SetParamsInt(int paramType, int value0, int value1);

    public native void native_SetParamsFloat(int paramType, float value0, float value1);

    public native void native_UpdateTransformMatrix(float rotateX, float rotateY, float scaleX, float scaleY);

    public native void native_SetImageData(int format, int width, int height, byte[] bytes);

    public native void native_SetImageDataWithIndex(int index, int format, int width, int height, byte[] bytes);

    public native void native_SetAudioData(short[] audioData);

    public native void native_OnSurfaceCreated();

    public native void native_OnSurfaceChanged(int width, int height);

    public native void native_OnDrawFrame();
}
