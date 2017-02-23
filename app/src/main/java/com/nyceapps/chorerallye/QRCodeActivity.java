package com.nyceapps.chorerallye;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import net.glxn.qrgen.android.QRCode;

import static com.nyceapps.chorerallye.Constants.EXTRA_MESSAGE_VALUE;

public class QRCodeActivity extends AppCompatActivity {
    private static final String TAG = QRCodeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        Intent intent = getIntent();
        String householdId = intent.getStringExtra(EXTRA_MESSAGE_VALUE);

        if (!TextUtils.isEmpty(householdId)) {
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            int width = display.getWidth();
            int height = display.getHeight();
            int smallerDimension = width < height ? width : height;
            smallerDimension = smallerDimension * 7 / 8;

            Bitmap qrCodeBitmap = QRCode.from(householdId).withSize(smallerDimension, smallerDimension).bitmap();

            ImageView qrCodeImageView = (ImageView) findViewById(R.id.qr_code);
            qrCodeImageView.setImageBitmap(qrCodeBitmap);
        }
    }
}
