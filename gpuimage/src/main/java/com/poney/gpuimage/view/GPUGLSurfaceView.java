package com.poney.gpuimage.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * @desc:
 */
public class GPUGLSurfaceView extends GLSurfaceView {
    private int mRatioWidth = 0;
    private int mRatioHeight = 0;
    private final GPUGLSurfaceViewRender mGLRender;

    public GPUGLSurfaceView(Context context, GPUGLSurfaceViewRender glRender) {
        this(context, glRender, null);
    }

    public GPUGLSurfaceView(Context context, GPUGLSurfaceViewRender glRender, AttributeSet attrs) {
        super(context, attrs);
        this.setEGLContextClientVersion(2);
        mGLRender = glRender;
        /*If no setEGLConfigChooser method is called,
        then by default the view will choose an RGB_888 surface with a depth buffer depth of at least 16 bits.*/
        setEGLConfigChooser(8, 8, 8, 8, 16, 8);
        setRenderer(mGLRender);
        setRenderMode(RENDERMODE_WHEN_DIRTY);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
            }
        }
    }

    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }

        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }
}
