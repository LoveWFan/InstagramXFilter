package com.poney.gpuimage.filter.blend;

import android.opengl.GLES20;

import com.poney.gpuimage.R;
import com.poney.gpuimage.filter.base.GPUImageBlendFilter;
import com.poney.gpuimage.utils.OpenGlUtils;


public class GPUImageCrayonFilter extends GPUImageBlendFilter {
	
	private int mSingleStepOffsetLocation;
	//1.0 - 5.0
	private int mStrengthLocation;
	
	public GPUImageCrayonFilter(){
		super(NO_FILTER_VERTEX_SHADER, OpenGlUtils.readShaderFromRawResource(R.raw.crayon));
	}
	
	public void onInit() {
        super.onInit();
        mSingleStepOffsetLocation = GLES20.glGetUniformLocation(getProgram(), "singleStepOffset");
        mStrengthLocation = GLES20.glGetUniformLocation(getProgram(), "strength");
        setFloat(mStrengthLocation, 2.0f);
    }
    
    public void onDestroy() {
        super.onDestroy();
    }

    public void onInitialized(){
        super.onInitialized();
        setFloat(mStrengthLocation, 0.5f);
    }

    private void setTexelSize(final float w, final float h) {
		setFloatVec2(mSingleStepOffsetLocation, new float[] {1.0f / w, 1.0f / h});
	}
	
	@Override
    public void onOutputSizeChanged(final int width, final int height) {
        super.onOutputSizeChanged(width, height);
        setTexelSize(width, height);
    }
}
