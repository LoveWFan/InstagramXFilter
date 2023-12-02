/*
 * Copyright (C) 2018 CyberAgent, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.poney.gpuimage.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.hardware.Camera.Size;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.util.Log;

import com.poney.gpuimage.filter.base.GPUImageParams;
import com.poney.gpuimage.gl.GLImage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class OpenGL30Utils {
    public static final int IMAGE_FORMAT_RGBA = 0x01;
    public static final int IMAGE_FORMAT_NV21 = 0x02;
    public static final int IMAGE_FORMAT_NV12 = 0x03;
    public static final int IMAGE_FORMAT_I420 = 0x04;
    public static final int IMAGE_FORMAT_YUYV = 0x05;
    public static final int IMAGE_FORMAT_GARY = 0x06;

    public static final int NO_TEXTURE = -1;

    public static int loadTexture(final Context context, final String name) {
        return loadTexture(context, name, NO_TEXTURE);
    }

    public static int loadTexture(final Context context, final String name, final int usedTexId) {
        final int[] textureHandle = new int[1];
        // Read in the resource
        final Bitmap bitmap = getImageFromAssetsFile(context, name);
        if (usedTexId == NO_TEXTURE) {
            GLES30.glGenTextures(1, textureHandle, 0);
            // Bind to the texture in OpenGL
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);


        } else {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, usedTexId);
            GLUtils.texSubImage2D(GLES30.GL_TEXTURE_2D, 0, 0, 0, bitmap);
            textureHandle[0] = usedTexId;
        }
        // Recycle the bitmap, since its data has been loaded into OpenGL.
        bitmap.recycle();
        return textureHandle[0];
    }

    private static Bitmap getImageFromAssetsFile(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static int loadTexture(final Bitmap img, final int usedTexId) {
        return loadTexture(img, usedTexId, true);
    }

    public static int loadTexture(final Bitmap img, final int usedTexId, final boolean recycle) {
        int textures[] = new int[1];
        if (usedTexId == NO_TEXTURE) {
            GLES30.glGenTextures(1, textures, 0);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[0]);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);

            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, img, 0);
        } else {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, usedTexId);
            GLUtils.texSubImage2D(GLES30.GL_TEXTURE_2D, 0, 0, 0, img);
            textures[0] = usedTexId;
        }
        if (recycle) {
            img.recycle();
        }
        return textures[0];
    }

    public static int loadTexture(final IntBuffer data, final int width, final int height, final int usedTexId) {
        int textures[] = new int[1];
        if (usedTexId == NO_TEXTURE) {
            GLES30.glGenTextures(1, textures, 0);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[0]);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, width, height,
                    0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, data);
        } else {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, usedTexId);
            GLES30.glTexSubImage2D(GLES30.GL_TEXTURE_2D, 0, 0, 0, width,
                    height, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, data);
            textures[0] = usedTexId;
        }
        return textures[0];
    }

    public static int loadTextureAsBitmap(final IntBuffer data, final Size size, final int usedTexId) {
        Bitmap bitmap = Bitmap
                .createBitmap(data.array(), size.width, size.height, Config.ARGB_8888);
        return loadTexture(bitmap, usedTexId);
    }

    public static int loadShader(final String strSource, final int iType) {
        int[] compiled = new int[1];
        int iShader = GLES30.glCreateShader(iType);
        GLES30.glShaderSource(iShader, strSource);
        GLES30.glCompileShader(iShader);
        GLES30.glGetShaderiv(iShader, GLES30.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.d("Load Shader Failed", "Compilation\n" + GLES30.glGetShaderInfoLog(iShader));
            return 0;
        }
        return iShader;
    }

    public static int loadProgram(final String strVSource, final String strFSource) {
        int iVShader;
        int iFShader;
        int iProgId;
        int[] link = new int[1];
        iVShader = loadShader(strVSource, GLES30.GL_VERTEX_SHADER);
        if (iVShader == 0) {
            Log.d("Load Program", "Vertex Shader Failed");
            return 0;
        }
        iFShader = loadShader(strFSource, GLES30.GL_FRAGMENT_SHADER);
        if (iFShader == 0) {
            Log.d("Load Program", "Fragment Shader Failed");
            return 0;
        }

        iProgId = GLES30.glCreateProgram();

        GLES30.glAttachShader(iProgId, iVShader);
        GLES30.glAttachShader(iProgId, iFShader);

        GLES30.glLinkProgram(iProgId);

        GLES30.glGetProgramiv(iProgId, GLES30.GL_LINK_STATUS, link, 0);
        if (link[0] <= 0) {
            Log.d("Load Program", "Linking Failed");
            return 0;
        }
        GLES30.glDeleteShader(iVShader);
        GLES30.glDeleteShader(iFShader);
        return iProgId;
    }

    public static float rnd(final float min, final float max) {
        float fRandNum = (float) Math.random();
        return min + (max - min) * fRandNum;
    }

    public static String readShaderFromRawResource(final int resourceId) {
        final InputStream inputStream = GPUImageParams.sContext.getResources().openRawResource(
                resourceId);
        final InputStreamReader inputStreamReader = new InputStreamReader(
                inputStream);
        final BufferedReader bufferedReader = new BufferedReader(
                inputStreamReader);

        String nextLine;
        final StringBuilder body = new StringBuilder();

        try {
            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            return null;
        }
        return body.toString();
    }

    public static float range(int percentage, float start, float end) {
        return (end - start) * percentage / 100.0f + start;
    }

    public static int range(int percentage, int start, int end) {
        return (end - start) * percentage / 100 + start;
    }

    private GLImage loadRGBAImage(Context context, int resId) {
        GLImage glImage = null;
        InputStream is = context.getResources().openRawResource(resId);
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(is);
            if (bitmap != null) {
                int bytes = bitmap.getByteCount();
                ByteBuffer buf = ByteBuffer.allocate(bytes);
                bitmap.copyPixelsToBuffer(buf);
                byte[] byteArray = buf.array();
                int[] pixels = new int[byteArray.length];
                for (int i = 0; i < pixels.length; ++i) {
                    pixels[i] = byteArray[i];
                }
                int[][] ppPlane = new int[3][];
                ppPlane[0] = pixels;

                glImage = new GLImage(bitmap.getWidth(), bitmap.getHeight(), IMAGE_FORMAT_RGBA, ppPlane);
            }
        } finally {
            try {
                if (bitmap != null) {
                    bitmap.recycle();
                }

                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return glImage;
    }


    public static void setInt(int programId, String name, int value) {
        GLES30.glUniform1i(GLES30.glGetUniformLocation(programId, name), value);
    }

    public static void setFloat(int programId, String name, float value) {
        GLES30.glUniform1f(GLES30.glGetUniformLocation(programId, name), value);
    }


    public static void setVec2(int programId, String name, FloatBuffer value) {
        GLES30.glUniform2fv(GLES30.glGetUniformLocation(programId, name), 1, value);
    }

    public static void setVec2(int programId, String name, float x, float y) {
        GLES30.glUniform2f(GLES30.glGetUniformLocation(programId, name), x, y);
    }

}
