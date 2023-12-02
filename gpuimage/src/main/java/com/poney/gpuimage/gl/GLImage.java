package com.poney.gpuimage.gl;

/**
 * @Auther: 贝天
 * @datetime: 12/1/23
 * @desc:
 */
public class GLImage {
    public int width;
    public int height;
    public int format;
    public int[][] ppPlane;

    public GLImage(int width, int height, int format,int [][] ppPlane) {
        this.width = width;
        this.height = height;
        this.format = format;
        this.ppPlane = ppPlane;
    }
}
