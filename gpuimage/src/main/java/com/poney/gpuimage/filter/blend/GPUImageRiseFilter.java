package com.poney.gpuimage.filter.blend;

import android.opengl.GLES20;

import com.poney.gpuimage.R;
import com.poney.gpuimage.filter.base.GPUImageBlendFilter;
import com.poney.gpuimage.filter.base.GPUImageParams;
import com.poney.gpuimage.utils.OpenGlUtils;

public class GPUImageRiseFilter extends GPUImageBlendFilter {
	private int[] inputTextureHandles = {-1,-1,-1};
	private int[] inputTextureUniformLocations = {-1,-1,-1};
	
	public GPUImageRiseFilter(){
		super(NO_FILTER_VERTEX_SHADER, OpenGlUtils.readShaderFromRawResource(R.raw.rise));
	}
	
	public void onDestroy() {
        super.onDestroy();
        GLES20.glDeleteTextures(inputTextureHandles.length, inputTextureHandles, 0);
        for(int i = 0; i < inputTextureHandles.length; i++)
        	inputTextureHandles[i] = -1;
    }
	
	public void onDrawArraysAfter(){
		for(int i = 0; i < inputTextureHandles.length
				&& inputTextureHandles[i] != OpenGlUtils.NO_TEXTURE; i++){
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i+3));
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		}
	}
	  
	public void onDrawArraysPre(){
		for(int i = 0; i < inputTextureHandles.length 
				&& inputTextureHandles[i] != OpenGlUtils.NO_TEXTURE; i++){
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i+3) );
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, inputTextureHandles[i]);
			GLES20.glUniform1i(inputTextureUniformLocations[i], (i+3));
		}
	}
	
	public void onInit(){
		super.onInit();
		for(int i=0; i < inputTextureUniformLocations.length; i++){
			inputTextureUniformLocations[i] = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture"+(2+i));
		}
	}
	
	public void onInitialized(){
		super.onInitialized();
	    runOnDraw(new Runnable(){
		    public void run(){
		    	inputTextureHandles[0] = OpenGlUtils.loadTexture(GPUImageParams.sContext, "filter/blackboard1024.png");
				inputTextureHandles[1] = OpenGlUtils.loadTexture(GPUImageParams.sContext, "filter/overlaymap.png");
				inputTextureHandles[2] = OpenGlUtils.loadTexture(GPUImageParams.sContext, "filter/risemap.png");
		    }
	    });
	}
}