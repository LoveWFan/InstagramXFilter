package com.poney.gpuimage.gl;

import android.opengl.GLES30;
import android.renderscript.Matrix4f;

import com.poney.gpuimage.R;
import com.poney.gpuimage.utils.OpenGL30Utils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * @datetime: 12/1/23
 * @desc:
 */
public class GLContext {
    private static final int BF_IMG_NUM = 6;
    private static final int BF_LOOP_COUNT = 200;
    int[] m_TextureIds = new int[BF_IMG_NUM];
    private static final String TAG = "GLContext";

    private static GLContext instance;
    private int m_MVPMatLoc;
    private int[] m_VboIds;
    private int[] m_VaoId;
    private GLImage[] m_RenderImages;
    private int program;
    private int m_frameIndex;
    private int m_AngleX;
    private int m_AngleY;
    private int m_loopCount;
    private FloatBuffer m_MVPMatrix;

    private GLContext() {
    }

    public static GLContext getInstance() {
        if (instance == null) {
            synchronized (GLContext.class) {
                if (instance == null) {
                    instance = new GLContext();
                }
            }
        }
        return instance;
    }

    public void init() {
        for (int i = 0; i < BF_IMG_NUM; ++i) {
            GLES30.glGenTextures(1, m_TextureIds, i);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, m_TextureIds[i]);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, GLES30.GL_NONE);
        }

        program = OpenGL30Utils.loadProgram(
                OpenGL30Utils.readShaderFromRawResource(R.raw.gltransition_vertex),
                OpenGL30Utils.readShaderFromRawResource(R.raw.gltransition_fragment));
        if (program > 0) {
            m_MVPMatLoc = GLES30.glGetUniformLocation(program, "u_MVPMatrix");
        }

        float[] verticesCoords = {
                -1.0f, 1.0f, 0.0f,  // Position 0
                -1.0f, -1.0f, 0.0f,  // Position 1
                1.0f, -1.0f, 0.0f,  // Position 2
                1.0f, 1.0f, 0.0f,  // Position 3
        };

        float[] textureCoords = {
                0.0f, 0.0f,        // TexCoord 0
                0.0f, 1.0f,        // TexCoord 1
                1.0f, 1.0f,        // TexCoord 2
                1.0f, 0.0f         // TexCoord 3
        };

        int[] indices = {0, 1, 2, 0, 2, 3};

        // Generate VBO Ids and load the VBOs with data
        GLES30.glGenBuffers(3, IntBuffer.wrap(m_VboIds));
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, m_VboIds[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, verticesCoords.length * 4, FloatBuffer.wrap(verticesCoords), GLES30.GL_STATIC_DRAW);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, m_VboIds[1]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, textureCoords.length * 4, FloatBuffer.wrap(textureCoords), GLES30.GL_STATIC_DRAW);

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, m_VboIds[2]);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, indices.length * 4, IntBuffer.wrap(indices), GLES30.GL_STATIC_DRAW);

        // Generate VAO Id
        GLES30.glGenVertexArrays(1, IntBuffer.wrap(m_VaoId));
        GLES30.glBindVertexArray(m_VaoId[0]);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, m_VboIds[0]);
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 3 * 4, 0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, GLES30.GL_NONE);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, m_VboIds[1]);
        GLES30.glEnableVertexAttribArray(1);
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 2 * 4, 0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, GLES30.GL_NONE);

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, m_VboIds[2]);

        GLES30.glBindVertexArray(GLES30.GL_NONE);
        GLES30.glPixelStorei(GLES30.GL_UNPACK_ALIGNMENT, 1);

        for (int i = 0; i < BF_IMG_NUM; ++i) {
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0 + i);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, m_TextureIds[i]);
            GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, m_RenderImages[i].width, m_RenderImages[i].height, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, IntBuffer.wrap(m_RenderImages[i].ppPlane[0]));
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, GLES30.GL_NONE);
        }
    }

    public void draw(int screenW, int screenH) {
        if (program == 0 || m_TextureIds[0] == 0) return;
        m_frameIndex++;

        UpdateMVPMatrix(m_MVPMatrix, m_AngleX, m_AngleY, (float) screenW / screenH);

        // Use the program object
        GLES30.glUseProgram(program);

        GLES30.glBindVertexArray(m_VaoId[0]);

        GLES30.glUniformMatrix4fv(m_MVPMatLoc, 1, false, m_MVPMatrix);

        float offset = (m_frameIndex % BF_LOOP_COUNT) * 1.0f / BF_LOOP_COUNT;

        if (m_frameIndex % BF_LOOP_COUNT == 0)
            m_loopCount++;

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, m_TextureIds[m_loopCount % BF_IMG_NUM]);
        OpenGL30Utils.setInt(program, "u_texture0", 0);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, m_TextureIds[(m_loopCount + 1) % BF_IMG_NUM]);
        OpenGL30Utils.setInt(program, "u_texture1", 1);

        OpenGL30Utils.setVec2(program, "u_texSize", m_RenderImages[0].width, m_RenderImages[0].height);
        OpenGL30Utils.setFloat(program,  "u_offset", offset);


        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_SHORT, 0);
    }

    private void UpdateMVPMatrix(FloatBuffer mMvpMatrix, int angleX, int angleY, float ratio) {

    }


}
