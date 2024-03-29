package com.poney.xfilter;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.poney.gpuimage.activity.CameraActivity;
import com.poney.gpuimage.activity.GLEffectActivity;
import com.poney.gpuimage.activity.GalleryActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.button_gallery)
    Button buttonGallery;
    @BindView(R.id.button_camera)
    Button buttonCamera;

    @BindView(R.id.button_effect)
    Button buttonEffect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        requestPermissions();
    }

    private void requestPermissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        Disposable disposable = rxPermissions.request(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .subscribe(permission -> {
                    if (permission) {
                    } else {
                        Toast.makeText(MainActivity.this, "请授予相关权限", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @OnClick({R.id.button_gallery, R.id.button_camera, R.id.button_effect})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_gallery:
                startActivity(new Intent(this, GalleryActivity.class));
                break;
            case R.id.button_camera:
                startActivity(new Intent(this, CameraActivity.class));
                break;
            case R.id.button_effect:
                startActivity(new Intent(this, GLEffectActivity.class));
                break;
        }
    }
}