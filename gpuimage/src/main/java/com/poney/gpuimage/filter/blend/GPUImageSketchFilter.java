package com.poney.gpuimage.filter.blend;

import android.opengl.GLES20;

import com.poney.gpuimage.R;
import com.poney.gpuimage.filter.base.GPUImageBlendFilter;
import com.poney.gpuimage.utils.OpenGlUtils;


public class GPUImageSketchFilter extends GPUImageBlendFilter {
	
	private int mSingleStepOffsetLocation;
	//0.0 - 1.0
	private int mStrengthLocation;
	
	public GPUImageSketchFilter(){
		super(NO_FILTER_VERTEX_SHADER, OpenGlUtils.readShaderFromRawResource(R.raw.sketch));
	}
	
	public void onInit() {
        super.onInit();
        mSingleStepOffsetLocation = GLES20.glGetUniformLocation(getProgram(), "singleStepOffset");
        mStrengthLocation = GLES20.glGetUniformLocation(getProgram(), "strength");
    }
    
    public void onDestroy() {
        super.onDestroy();
    }
    
    private void setTexelSize(final float w, final float h) {
		setFloatVec2(mSingleStepOffsetLocation, new float[] {1.0f / w, 1.0f / h});
	}

    public void onInitialized(){
        super.onInitialized();
        setFloat(mStrengthLocation, 0.5f);
    }

	@Override
    public void onOutputSizeChanged(final int width, final int height) {
        super.onOutputSizeChanged(width, height);
        setTexelSize(width, height);
    }
}
