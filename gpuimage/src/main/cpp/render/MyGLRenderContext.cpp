//
// Created by Pony on 2019/7/9.
//


#include <GLTransitionExample.h>
#include <GLTransitionExample_2.h>
#include <GLTransitionExample_3.h>
#include <GLTransitionExample_4.h>

#include "MyGLRenderContext.h"
#include "LogUtil.h"

MyGLRenderContext *MyGLRenderContext::m_pContext = nullptr;

MyGLRenderContext::MyGLRenderContext() {
    m_pCurSample = new GLTransitionExample();
    m_pBeforeSample = nullptr;

}

MyGLRenderContext::~MyGLRenderContext() {
    if (m_pCurSample) {
        delete m_pCurSample;
        m_pCurSample = nullptr;
    }

    if (m_pBeforeSample) {
        delete m_pBeforeSample;
        m_pBeforeSample = nullptr;
    }

}


void MyGLRenderContext::SetParamsInt(int paramType, int value0, int value1) {
    LOGCATE("MyGLRenderContext::SetParamsInt paramType = %d, value0 = %d, value1 = %d", paramType,
            value0, value1);

    if (paramType == SAMPLE_TYPE) {
        m_pBeforeSample = m_pCurSample;

        LOGCATE("MyGLRenderContext::SetParamsInt 0 m_pBeforeSample = %p", m_pBeforeSample);

        switch (value0) {

            case SAMPLE_TYPE_KEY_TRANSITIONS_1:
                m_pCurSample = new GLTransitionExample();
                break;
            case SAMPLE_TYPE_KEY_TRANSITIONS_2:
                m_pCurSample = new GLTransitionExample_2();
                break;
            case SAMPLE_TYPE_KEY_TRANSITIONS_3:
                m_pCurSample = new GLTransitionExample_3();
                break;
            case SAMPLE_TYPE_KEY_TRANSITIONS_4:
                m_pCurSample = new GLTransitionExample_4();
                break;

            default:
                m_pCurSample = nullptr;
                break;
        }

        LOGCATE("MyGLRenderContext::SetParamsInt m_pBeforeSample = %p, m_pCurSample=%p",
                m_pBeforeSample, m_pCurSample);
    }
}

void MyGLRenderContext::SetParamsFloat(int paramType, float value0, float value1) {
    LOGCATE("MyGLRenderContext::SetParamsFloat paramType=%d, value0=%f, value1=%f", paramType,
            value0, value1);
    if (m_pCurSample) {
        switch (paramType) {

            default:
                break;

        }
    }

}

void MyGLRenderContext::SetParamsShortArr(short *const pShortArr, int arrSize) {
    LOGCATE("MyGLRenderContext::SetParamsShortArr pShortArr=%p, arrSize=%d, pShortArr[0]=%d",
            pShortArr, arrSize, pShortArr[0]);
    if (m_pCurSample) {
        m_pCurSample->LoadShortArrData(pShortArr, arrSize);
    }

}

void
MyGLRenderContext::UpdateTransformMatrix(float rotateX, float rotateY, float scaleX, float scaleY) {
    LOGCATE("MyGLRenderContext::UpdateTransformMatrix [rotateX, rotateY, scaleX, scaleY] = [%f, %f, %f, %f]",
            rotateX, rotateY, scaleX, scaleY);
    if (m_pCurSample) {
        m_pCurSample->UpdateTransformMatrix(rotateX, rotateY, scaleX, scaleY);
    }
}

void MyGLRenderContext::SetImageDataWithIndex(int index, int format, int width, int height,
                                              uint8_t *pData) {
    LOGCATE("MyGLRenderContext::SetImageDataWithIndex index=%d, format=%d, width=%d, height=%d, pData=%p",
            index, format, width, height, pData);
    NativeImage nativeImage;
    nativeImage.format = format;
    nativeImage.width = width;
    nativeImage.height = height;
    nativeImage.ppPlane[0] = pData;

    switch (format) {
        case IMAGE_FORMAT_NV12:
        case IMAGE_FORMAT_NV21:
            nativeImage.ppPlane[1] = nativeImage.ppPlane[0] + width * height;
            break;
        case IMAGE_FORMAT_I420:
            nativeImage.ppPlane[1] = nativeImage.ppPlane[0] + width * height;
            nativeImage.ppPlane[2] = nativeImage.ppPlane[1] + width * height / 4;
            break;
        default:
            break;
    }

    if (m_pCurSample) {
        m_pCurSample->LoadMultiImageWithIndex(index, &nativeImage);
    }

}

void MyGLRenderContext::SetImageData(int format, int width, int height, uint8_t *pData) {
    LOGCATE("MyGLRenderContext::SetImageData format=%d, width=%d, height=%d, pData=%p", format,
            width, height, pData);
    NativeImage nativeImage;
    nativeImage.format = format;
    nativeImage.width = width;
    nativeImage.height = height;
    nativeImage.ppPlane[0] = pData;

    switch (format) {
        case IMAGE_FORMAT_NV12:
        case IMAGE_FORMAT_NV21:
            nativeImage.ppPlane[1] = nativeImage.ppPlane[0] + width * height;
            break;
        case IMAGE_FORMAT_I420:
            nativeImage.ppPlane[1] = nativeImage.ppPlane[0] + width * height;
            nativeImage.ppPlane[2] = nativeImage.ppPlane[1] + width * height / 4;
            break;
        default:
            break;
    }

    if (m_pCurSample) {
        m_pCurSample->LoadImage(&nativeImage);
    }

}

void MyGLRenderContext::OnSurfaceCreated() {
    LOGCATE("MyGLRenderContext::OnSurfaceCreated");
    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
}

void MyGLRenderContext::OnSurfaceChanged(int width, int height) {
    LOGCATE("MyGLRenderContext::OnSurfaceChanged [w, h] = [%d, %d]", width, height);
    glViewport(0, 0, width, height);
    m_ScreenW = width;
    m_ScreenH = height;
}

void MyGLRenderContext::OnDrawFrame() {
    LOGCATE("MyGLRenderContext::OnDrawFrame");
    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

    if (m_pBeforeSample) {
        m_pBeforeSample->Destroy();
        delete m_pBeforeSample;
        m_pBeforeSample = nullptr;
    }

    if (m_pCurSample) {
        m_pCurSample->Init();
        m_pCurSample->Draw(m_ScreenW, m_ScreenH);
    }
}

MyGLRenderContext *MyGLRenderContext::GetInstance() {
    LOGCATE("MyGLRenderContext::GetInstance");
    if (m_pContext == nullptr) {
        m_pContext = new MyGLRenderContext();
    }
    return m_pContext;
}

void MyGLRenderContext::DestroyInstance() {
    LOGCATE("MyGLRenderContext::DestroyInstance");
    if (m_pContext) {
        delete m_pContext;
        m_pContext = nullptr;
    }

}



