package com.nyceapps.chorerallye;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import static com.nyceapps.chorerallye.Constants.EXTRA_MESSAGE_VALUE;
import static com.nyceapps.chorerallye.Constants.REQUEST_CODE_PERMISSION_REQUEST_CAMERA;

public class ScanQRCodeActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {
    private ViewGroup mainLayout;

    private QRCodeReaderView qrCodeDecoderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr_code);

        mainLayout = (ViewGroup) findViewById(R.id.qr_code_main_layout);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initQRCodeReaderView();
        } else {
            requestCameraPermission();
        }
    }

    private void initQRCodeReaderView() {
        View content = getLayoutInflater().inflate(R.layout.content_scan_qr_code, mainLayout, true);

        qrCodeDecoderView = (QRCodeReaderView) findViewById(R.id.qr_code);
        qrCodeDecoderView.setOnQRCodeReadListener(this);

        qrCodeDecoderView.setQRDecodingEnabled(true);
        qrCodeDecoderView.setAutofocusInterval(2000L);
        qrCodeDecoderView.setBackCamera();

        qrCodeDecoderView.startCamera();
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_MESSAGE_VALUE, text);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
            Snackbar.make(mainLayout, "Camera access is required to display the camera preview.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override public void onClick(View view) {
                    ActivityCompat.requestPermissions(ScanQRCodeActivity.this, new String[] {
                            android.Manifest.permission.CAMERA
                    }, REQUEST_CODE_PERMISSION_REQUEST_CAMERA);
                }
            }).show();
        } else {
            Snackbar.make(mainLayout, "Permission is not available. Requesting camera permission.",
                    Snackbar.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.CAMERA
            }, REQUEST_CODE_PERMISSION_REQUEST_CAMERA);
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        if (requestCode != REQUEST_CODE_PERMISSION_REQUEST_CAMERA) {
            return;
        }

        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(mainLayout, "Camera permission was granted.", Snackbar.LENGTH_SHORT).show();
            initQRCodeReaderView();
        } else {
            Snackbar.make(mainLayout, "Camera permission request was denied.", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override protected void onResume() {
        super.onResume();

        if (qrCodeDecoderView != null) {
            qrCodeDecoderView.startCamera();
        }
    }

    @Override protected void onPause() {
        super.onPause();

        if (qrCodeDecoderView != null) {
            qrCodeDecoderView.stopCamera();
        }
    }
}
