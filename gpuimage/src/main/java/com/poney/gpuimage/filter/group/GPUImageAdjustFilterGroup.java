package com.poney.gpuimage.filter.group;

import com.poney.gpuimage.filter.adjust.common.GPUImageBrightnessFilter;
import com.poney.gpuimage.filter.adjust.common.GPUImageContrastFilter;
import com.poney.gpuimage.filter.adjust.common.GPUImageExposureFilter;
import com.poney.gpuimage.filter.adjust.common.GPUImageHueFilter;
import com.poney.gpuimage.filter.adjust.common.GPUImageSaturationFilter;
import com.poney.gpuimage.filter.adjust.common.GPUImageSharpenFilter;
import com.poney.gpuimage.filter.base.GPUImageFilter;

import java.util.ArrayList;
import java.util.List;

import static com.poney.gpuimage.utils.OpenGlUtils.range;


public class GPUImageAdjustFilterGroup extends GPUImageFilterGroup {

    private int mContrastProgress = 50;
    private int mSaturationProgress = 50;
    private int mExposureProgress = 50;
    private int mSharpnessProgress = 50;
    private int mBrightnessProgress = 50;
    private int mHueProgress = 50;

    public GPUImageAdjustFilterGroup() {
        super(initFilters());
    }

    private static List<GPUImageFilter> initFilters() {
        List<GPUImageFilter> filters = new ArrayList<GPUImageFilter>();
        filters.add(new GPUImageContrastFilter());
        filters.add(new GPUImageBrightnessFilter());
        filters.add(new GPUImageExposureFilter());
        filters.add(new GPUImageHueFilter());
        filters.add(new GPUImageSaturationFilter());
        filters.add(new GPUImageSharpenFilter());
        return filters;
    }

    public void setSharpness(final int progress) {
        mSharpnessProgress = progress;

        ((GPUImageSharpenFilter) filters.get(5)).setSharpness(range(progress, -4.0f, 4.0f));
    }

    public void setHue(final int progress) {
        mHueProgress = progress;
        ((GPUImageHueFilter) filters.get(3)).setHue(range(progress, -180.0f, 180.0f));
    }

    public void setBrightness(final int progress) {
        mBrightnessProgress = progress;
        ((GPUImageBrightnessFilter) filters.get(1)).setBrightness(range(progress, -1.0f, 1.0f));
    }

    public void setContrast(final int progress) {
        mContrastProgress = progress;

        ((GPUImageContrastFilter) filters.get(0)).setContrast(range(progress, 0.0f, 2.0f));
    }

    public void setSaturation(final int progress) {
        mSaturationProgress = progress;
        ((GPUImageSaturationFilter) filters.get(4)).setSaturation(range(progress, 0.0f, 2.0f));
    }

    public void setExposure(final int progress) {
        mExposureProgress = progress;
        ((GPUImageExposureFilter) filters.get(2)).setExposure(range(progress, -2.0f, 2.0f));
    }

    public int getContrastProgress() {
        return mContrastProgress;
    }

    public int getSaturationProgress() {
        return mSaturationProgress;
    }

    public int getExposureProgress() {
        return mExposureProgress;
    }

    public int getSharpnessProgress() {
        return mSharpnessProgress;
    }

    public int getBrightnessProgress() {
        return mBrightnessProgress;
    }

    public int getHueProgress() {
        return mHueProgress;
    }


}
