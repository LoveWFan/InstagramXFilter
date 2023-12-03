

#include <gtc/matrix_transform.hpp>
#include "GLTransitionExample_4.h"
#include "../util/GLUtils.h"

GLTransitionExample_4::GLTransitionExample_4()
{

	m_MVPMatLoc = GL_NONE;

    for (int i = 0; i < BF_IMG_NUM; ++i) {
        m_TextureIds[i] = GL_NONE;
    }
	m_VaoId = GL_NONE;

	m_AngleX = 0;
	m_AngleY = 0;

	m_ScaleX = 1.0f;
	m_ScaleY = 1.0f;

    m_frameIndex = 0;
	m_loopCount = 0;
}

GLTransitionExample_4::~GLTransitionExample_4()
{
    for (int i = 0; i < BF_IMG_NUM; ++i) {
        NativeImageUtil::FreeNativeImage(&m_RenderImages[i]);
    }
}

void GLTransitionExample_4::Init()
{
	if(m_ProgramObj)
		return;
    for (int i = 0; i < BF_IMG_NUM; ++i) {
        glGenTextures(1, &m_TextureIds[i]);
        glBindTexture(GL_TEXTURE_2D, m_TextureIds[i]);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glBindTexture(GL_TEXTURE_2D, GL_NONE);
    }

    char vShaderStr[] =
            "#version 300 es\n"
            "layout(location = 0) in vec4 a_position;\n"
            "layout(location = 1) in vec2 a_texCoord;\n"
            "uniform mat4 u_MVPMatrix;\n"
            "out vec2 v_texCoord;\n"
            "void main()\n"
            "{\n"
            "    gl_Position = u_MVPMatrix * a_position;\n"
            "    v_texCoord = a_texCoord;\n"
            "}";

	char fShaderStr[] =
	        "#version 300 es\n"
            "precision mediump float;\n"
            "in vec2 v_texCoord;\n"
            "layout(location = 0) out vec4 outColor;\n"
            "uniform sampler2D u_texture0;\n"
            "uniform sampler2D u_texture1;\n"
            "uniform float u_offset;\n"
            "uniform vec2 u_texSize;\n"
            "\n"
            "// Number of total bars/columns\n"
            "const int bars = 20;\n"
            "\n"
            "// Multiplier for speed ratio. 0 = no variation when going down, higher = some elements go much faster\n"
            "const float amplitude = 2.0;\n"
            "\n"
            "// Further variations in speed. 0 = no noise, 1 = super noisy (ignore frequency)\n"
            "const float noise = 0.1;\n"
            "\n"
            "// Speed variation horizontally. the bigger the value, the shorter the waves\n"
            "const float frequency = 0.5;\n"
            "\n"
            "// How much the bars seem to \"run\" from the middle of the screen first (sticking to the sides). 0 = no drip, 1 = curved drip\n"
            "const float dripScale = 0.5;\n"
            "\n"
            "\n"
            "// The code proper --------\n"
            "\n"
            "float rand(int num) {\n"
            "    return fract(mod(float(num) * 67123.313, 12.0) * sin(float(num) * 10.3) * cos(float(num)));\n"
            "}\n"
            "\n"
            "float wave(int num) {\n"
            "    float fn = float(num) * frequency * 0.1 * float(bars);\n"
            "    return cos(fn * 0.5) * cos(fn * 0.13) * sin((fn+10.0) * 0.3) / 2.0 + 0.5;\n"
            "}\n"
            "\n"
            "float drip(int num) {\n"
            "    return sin(float(num) / float(bars - 1) * 3.141592) * dripScale;\n"
            "}\n"
            "\n"
            "float pos(int num) {\n"
            "    return (noise == 0.0 ? wave(num) : mix(wave(num), rand(num), noise)) + (dripScale == 0.0 ? 0.0 : drip(num));\n"
            "}\n"
            "\n"
            "vec4 getFromColor(vec2 uv) {\n"
            "    return texture(u_texture0, uv);\n"
            "}\n"
            "\n"
            "vec4 getToColor(vec2 uv) {\n"
            "    return texture(u_texture1, uv);\n"
            "}\n"
            "\n"
            "vec4 transition(vec2 uv) {\n"
            "    int bar = int(uv.x * (float(bars)));\n"
            "    float scale = 1.0 + pos(bar) * amplitude;\n"
            "    float phase = u_offset * scale;\n"
            "    float posY = uv.y / vec2(1.0).y;\n"
            "    vec2 p;\n"
            "    vec4 c;\n"
            "    if (phase + posY < 1.0) {\n"
            "        p = vec2(uv.x, uv.y + mix(0.0, vec2(1.0).y, phase)) / vec2(1.0).xy;\n"
            "        c = getFromColor(p);\n"
            "    } else {\n"
            "        p = uv.xy / vec2(1.0).xy;\n"
            "        c = getToColor(p);\n"
            "    }\n"
            "\n"
            "    // Finally, apply the color\n"
            "    return c;\n"
            "}\n"
            "\n"
            "void main()\n"
            "{\n"
            "    outColor = transition(v_texCoord);\n"
            "}";

	m_ProgramObj = GLUtils::CreateProgram(vShaderStr, fShaderStr);
	if (m_ProgramObj)
	{
		m_MVPMatLoc = glGetUniformLocation(m_ProgramObj, "u_MVPMatrix");
	}
	else
	{
		LOGCATE("GLTransitionExample_4::Init create program fail");
	}

	GLfloat verticesCoords[] = {
			-1.0f,  1.0f, 0.0f,  // Position 0
			-1.0f, -1.0f, 0.0f,  // Position 1
			1.0f,  -1.0f, 0.0f,  // Position 2
			1.0f,   1.0f, 0.0f,  // Position 3
	};

	GLfloat textureCoords[] = {
			0.0f,  0.0f,        // TexCoord 0
			0.0f,  1.0f,        // TexCoord 1
			1.0f,  1.0f,        // TexCoord 2
			1.0f,  0.0f         // TexCoord 3
	};

	GLushort indices[] = { 0, 1, 2, 0, 2, 3 };

	// Generate VBO Ids and load the VBOs with data
	glGenBuffers(3, m_VboIds);
	glBindBuffer(GL_ARRAY_BUFFER, m_VboIds[0]);
	glBufferData(GL_ARRAY_BUFFER, sizeof(verticesCoords), verticesCoords, GL_STATIC_DRAW);

	glBindBuffer(GL_ARRAY_BUFFER, m_VboIds[1]);
	glBufferData(GL_ARRAY_BUFFER, sizeof(textureCoords), textureCoords, GL_STATIC_DRAW);

	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_VboIds[2]);
	glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(indices), indices, GL_STATIC_DRAW);

	// Generate VAO Id
	glGenVertexArrays(1, &m_VaoId);
	glBindVertexArray(m_VaoId);

	glBindBuffer(GL_ARRAY_BUFFER, m_VboIds[0]);
	glEnableVertexAttribArray(0);
	glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 3 * sizeof(GLfloat), (const void *)0);
	glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);

	glBindBuffer(GL_ARRAY_BUFFER, m_VboIds[1]);
	glEnableVertexAttribArray(1);
	glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE, 2 * sizeof(GLfloat), (const void *)0);
	glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);

	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_VboIds[2]);

	glBindVertexArray(GL_NONE);
	glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

    for (int i = 0; i < BF_IMG_NUM; ++i) {
        glActiveTexture(GL_TEXTURE0 + i);
        glBindTexture(GL_TEXTURE_2D, m_TextureIds[i]);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, m_RenderImages[i].width, m_RenderImages[i].height, 0, GL_RGBA, GL_UNSIGNED_BYTE, m_RenderImages[i].ppPlane[0]);
        glBindTexture(GL_TEXTURE_2D, GL_NONE);
    }
}

void GLTransitionExample_4::LoadImage(NativeImage *pImage)
{
	LOGCATE("GLTransitionExample_4::LoadImage pImage = %p", pImage->ppPlane[0]);
}

void GLTransitionExample_4::Draw(int screenW, int screenH)
{
	LOGCATE("GLTransitionExample_4::Draw()");

	if(m_ProgramObj == GL_NONE || m_TextureIds[0] == GL_NONE) return;

    m_frameIndex ++;

	UpdateMVPMatrix(m_MVPMatrix, m_AngleX, m_AngleY, (float)screenW / screenH);

	// Use the program object
	glUseProgram (m_ProgramObj);

	glBindVertexArray(m_VaoId);

	glUniformMatrix4fv(m_MVPMatLoc, 1, GL_FALSE, &m_MVPMatrix[0][0]);

	float offset = (m_frameIndex % BF_LOOP_COUNT) * 1.0f / BF_LOOP_COUNT;

	if(m_frameIndex % BF_LOOP_COUNT == 0)
		m_loopCount ++;

	glActiveTexture(GL_TEXTURE0);
	glBindTexture(GL_TEXTURE_2D, m_TextureIds[m_loopCount % BF_IMG_NUM]);
	GLUtils::setInt(m_ProgramObj, "u_texture0", 0);

	glActiveTexture(GL_TEXTURE1);
	glBindTexture(GL_TEXTURE_2D, m_TextureIds[(m_loopCount + 1) % BF_IMG_NUM]);
	GLUtils::setInt(m_ProgramObj, "u_texture1", 1);

	GLUtils::setVec2(m_ProgramObj, "u_texSize", m_RenderImages[0].width, m_RenderImages[0].height);
	GLUtils::setFloat(m_ProgramObj, "u_offset", offset);

	glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, (const void *)0);

}

void GLTransitionExample_4::Destroy()
{
	if (m_ProgramObj)
	{
		glDeleteProgram(m_ProgramObj);
		glDeleteBuffers(3, m_VboIds);
		glDeleteVertexArrays(1, &m_VaoId);
		glDeleteTextures(BF_IMG_NUM, m_TextureIds);
	}
}

void GLTransitionExample_4::UpdateMVPMatrix(glm::mat4 &mvpMatrix, int angleX, int angleY, float ratio)
{
	LOGCATE("GLTransitionExample_4::UpdateMVPMatrix angleX = %d, angleY = %d, ratio = %f", angleX, angleY, ratio);
	angleX = angleX % 360;
	angleY = angleY % 360;

	//转化为弧度角
	float radiansX = static_cast<float>(MATH_PI / 180.0f * angleX);
	float radiansY = static_cast<float>(MATH_PI / 180.0f * angleY);


	// Projection matrix
	glm::mat4 Projection = glm::ortho(-1.0f, 1.0f, -1.0f, 1.0f, 0.1f, 100.0f);
	//glm::mat4 Projection = glm::frustum(-ratio, ratio, -1.0f, 1.0f, 4.0f, 100.0f);
	//glm::mat4 Projection = glm::perspective(45.0f,ratio, 0.1f,100.f);

	// View matrix
	glm::mat4 View = glm::lookAt(
			glm::vec3(0, 0, 4), // Camera is at (0,0,1), in World Space
			glm::vec3(0, 0, 0), // and looks at the origin
			glm::vec3(0, 1, 0)  // Head is up (set to 0,-1,0 to look upside-down)
	);

	// Model matrix
	glm::mat4 Model = glm::mat4(1.0f);
	Model = glm::scale(Model, glm::vec3(m_ScaleX, m_ScaleY, 1.0f));
	Model = glm::rotate(Model, radiansX, glm::vec3(1.0f, 0.0f, 0.0f));
	Model = glm::rotate(Model, radiansY, glm::vec3(0.0f, 1.0f, 0.0f));
	Model = glm::translate(Model, glm::vec3(0.0f, 0.0f, 0.0f));

	mvpMatrix = Projection * View * Model;

}

void GLTransitionExample_4::UpdateTransformMatrix(float rotateX, float rotateY, float scaleX, float scaleY)
{
	GLSampleBase::UpdateTransformMatrix(rotateX, rotateY, scaleX, scaleY);
	m_AngleX = static_cast<int>(rotateX);
	m_AngleY = static_cast<int>(rotateY);
	m_ScaleX = scaleX;
	m_ScaleY = scaleY;
}

void GLTransitionExample_4::LoadMultiImageWithIndex(int index, NativeImage *pImage) {
	LOGCATE("GLTransitionExample_4::LoadMultiImageWithIndex pImage = %p,[w=%d,h=%d,f=%d]", pImage->ppPlane[0], pImage->width, pImage->height, pImage->format);
	if (pImage && index >=0 && index < BF_IMG_NUM)
	{
		m_RenderImages[index].width = pImage->width;
		m_RenderImages[index].height = pImage->height;
		m_RenderImages[index].format = pImage->format;
		NativeImageUtil::CopyNativeImage(pImage, &m_RenderImages[index]);
		//NativeImageUtil::DumpNativeImage(&m_GrayImage, "/sdcard/DCIM", "GLTransitionExample_4");
    }
}
