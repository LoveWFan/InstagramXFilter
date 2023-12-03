package com.poney.gpuimage.activity;

import static android.opengl.GLSurfaceView.RENDERMODE_CONTINUOUSLY;
import static android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.poney.gpuimage.R;
import com.poney.gpuimage.glrender.GPUNativeRender;
import com.poney.gpuimage.view.GPUGLSurfaceView;
import com.poney.gpuimage.view.GPUGLSurfaceViewRender;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class GLEffectActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener {
    public static final int IMAGE_FORMAT_RGBA = 0x01;
    private GPUGLSurfaceViewRender mGLRender = new GPUGLSurfaceViewRender();
    private ViewGroup mRootView;
    private GPUGLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gleffect);
        mRootView = (ViewGroup) findViewById(R.id.rootView);
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        mGLRender.init();
    }

    @Override
    public void onGlobalLayout() {
        mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mGLSurfaceView = new GPUGLSurfaceView(this, mGLRender);
        mRootView.addView(mGLSurfaceView, lp);
        mGLSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);


        initFirstTransition();
    }

    private void initFirstTransition() {
        mGLRender.setParamsInt(GPUNativeRender.SAMPLE_TYPE, GPUNativeRender.SAMPLE_TYPE_KEY_TRANSITIONS_1, 0);
        Bitmap tmp;
        loadRGBAImage(R.drawable.lye, 0);
        loadRGBAImage(R.drawable.lye4, 1);
        loadRGBAImage(R.drawable.lye5, 2);
        loadRGBAImage(R.drawable.lye6, 3);
        loadRGBAImage(R.drawable.lye7, 4);
        tmp = loadRGBAImage(R.drawable.lye8, 5);
        mGLSurfaceView.setAspectRatio(tmp.getWidth(), tmp.getHeight());
        mGLSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);

        mGLSurfaceView.requestRender();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGLRender.unInit();
        /*
         * Once the EGL context gets destroyed all the GL buffers etc will get destroyed with it,
         * so this is unnecessary.
         * */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        mRootView.removeView(mGLSurfaceView);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mGLSurfaceView = new GPUGLSurfaceView(this, mGLRender);
        mRootView.addView(mGLSurfaceView, lp);


        mGLSurfaceView.setRenderMode(RENDERMODE_WHEN_DIRTY);

        if (mRootView.getWidth() != mGLSurfaceView.getWidth()
                || mRootView.getHeight() != mGLSurfaceView.getHeight()) {
            mGLSurfaceView.setAspectRatio(mRootView.getWidth(), mRootView.getHeight());
        }


        Bitmap tmp;
        if (id == R.id.action_shader0) {
            mGLRender.setParamsInt(GPUNativeRender.SAMPLE_TYPE, GPUNativeRender.SAMPLE_TYPE_KEY_TRANSITIONS_1, 0);
        } else if (id == R.id.action_shader1) {
            mGLRender.setParamsInt(GPUNativeRender.SAMPLE_TYPE, GPUNativeRender.SAMPLE_TYPE_KEY_TRANSITIONS_2, 0);

        } else if (id == R.id.action_shader2) {
            mGLRender.setParamsInt(GPUNativeRender.SAMPLE_TYPE, GPUNativeRender.SAMPLE_TYPE_KEY_TRANSITIONS_3, 0);

        } else if (id == R.id.action_shader3) {
            mGLRender.setParamsInt(GPUNativeRender.SAMPLE_TYPE, GPUNativeRender.SAMPLE_TYPE_KEY_TRANSITIONS_4, 0);

        }

        loadRGBAImage(R.drawable.lye, 0);
        loadRGBAImage(R.drawable.lye4, 1);
        loadRGBAImage(R.drawable.lye5, 2);
        loadRGBAImage(R.drawable.lye6, 3);
        loadRGBAImage(R.drawable.lye7, 4);
        tmp = loadRGBAImage(R.drawable.lye8, 5);
        mGLSurfaceView.setAspectRatio(tmp.getWidth(), tmp.getHeight());
        mGLSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);

        mGLSurfaceView.requestRender();

        return true;
    }

    private Bitmap loadRGBAImage(int resId, int index) {
        InputStream is = this.getResources().openRawResource(resId);
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(is);
            if (bitmap != null) {
                int bytes = bitmap.getByteCount();
                ByteBuffer buf = ByteBuffer.allocate(bytes);
                bitmap.copyPixelsToBuffer(buf);
                byte[] byteArray = buf.array();
                mGLRender.setImageDataWithIndex(index, IMAGE_FORMAT_RGBA, bitmap.getWidth(), bitmap.getHeight(), byteArray);
            }
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }


}