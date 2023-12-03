
#ifndef NDK_OPENGLES_3_0_GLTRANSITIONEXAMPLE_H
#define NDK_OPENGLES_3_0_GLTRANSITIONEXAMPLE_H

#include <detail/type_mat.hpp>
#include <detail/type_mat4x4.hpp>
#include "GLSampleBase.h"

#define BF_IMG_NUM    6
#define BF_LOOP_COUNT 200

class GLTransitionExample : public GLSampleBase
{
public:
	GLTransitionExample();

	virtual ~GLTransitionExample();

	virtual void LoadImage(NativeImage *pImage);

    virtual void LoadMultiImageWithIndex(int index, NativeImage *pImage);

	virtual void Init();
	virtual void Draw(int screenW, int screenH);

	virtual void Destroy();

	virtual void UpdateTransformMatrix(float rotateX, float rotateY, float scaleX, float scaleY);

	void UpdateMVPMatrix(glm::mat4 &mvpMatrix, int angleX, int angleY, float ratio);

private:
	GLuint m_TextureIds[BF_IMG_NUM];
	GLint m_MVPMatLoc;
	GLuint m_VaoId;
	GLuint m_VboIds[3];
	NativeImage m_RenderImages[BF_IMG_NUM];
    glm::mat4 m_MVPMatrix;

	int m_AngleX;
	int m_AngleY;
	float m_ScaleX;
	float m_ScaleY;

	int m_frameIndex;
	int m_loopCount;
};


#endif //NDK_OPENGLES_3_0_GLTRANSITIONEXAMPLE_H
