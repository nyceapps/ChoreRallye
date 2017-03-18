package com.nyceapps.chorerallye.member;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.mikelau.croperino.Croperino;
import com.mikelau.croperino.CroperinoConfig;
import com.nyceapps.chorerallye.R;
import com.nyceapps.chorerallye.main.ScanQRCodeActivity;
import com.nyceapps.chorerallye.main.Utils;

import java.io.File;

import static com.nyceapps.chorerallye.main.Constants.EXTRA_MESSAGE_FILE_STRING;
import static com.nyceapps.chorerallye.main.Constants.EXTRA_MESSAGE_NAME;
import static com.nyceapps.chorerallye.main.Constants.EXTRA_MESSAGE_ORIGINAL_NAME;
import static com.nyceapps.chorerallye.main.Constants.EXTRA_MESSAGE_UID;
import static com.nyceapps.chorerallye.main.Constants.REQUEST_CODE_CAPTURE_IMAGE_FROM_CAMERA;
import static com.nyceapps.chorerallye.main.Constants.REQUEST_CODE_PERMISSION_REQUEST_CAMERA;

public class MemberDetailActivity extends AppCompatActivity {
    private String uid;
    private String originalName;
    private String memberFileString;

    private boolean cameraPhotoWasChosen;
    private File tempCameraFile;

    private ViewGroup mainLayout;
    private ImageView memberImageImageView;
    private EditText memberNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail);

        mainLayout = (ViewGroup) findViewById(R.id.member_detail_main_layout);

        Intent intent = getIntent();
        uid = intent.getStringExtra(EXTRA_MESSAGE_UID);
        originalName = intent.getStringExtra(EXTRA_MESSAGE_ORIGINAL_NAME);

        memberImageImageView = (ImageView) findViewById(R.id.member_image);
        memberFileString = intent.getStringExtra(EXTRA_MESSAGE_FILE_STRING);
        BitmapDrawable memberBitmapDrawable = Utils.convertStringToBitmapDrawable(memberFileString, this);
        if (memberBitmapDrawable != null) {
            memberImageImageView.setImageDrawable(memberBitmapDrawable);
        }
        memberImageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MemberDetailActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    chooseImage();
                } else {
                    requestCameraPermission();
                }
            }
        });

        String memberName = intent.getStringExtra(EXTRA_MESSAGE_NAME);
        setTitle(memberName);
        memberNameEditText = (EditText) findViewById(R.id.member_name);
        memberNameEditText.setText(memberName);
        memberNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setTitle(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cameraPhotoWasChosen = false;
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
            Snackbar.make(mainLayout, "Camera access is required to display the camera preview.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override public void onClick(View view) {
                    ActivityCompat.requestPermissions(MemberDetailActivity.this, new String[] {
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
            chooseImage();
        } else {
            Snackbar.make(mainLayout, "Camera permission request was denied.", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void chooseImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            tempCameraFile = Utils.createCameraFile(this);
            if (tempCameraFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", tempCameraFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, REQUEST_CODE_CAPTURE_IMAGE_FROM_CAMERA);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CAPTURE_IMAGE_FROM_CAMERA:
                    if (tempCameraFile != null) {
                        Croperino.runCropImage(tempCameraFile, this, true, 1, 1, R.color.colorPrimary, R.color.white);
                    }
                    break;
                case CroperinoConfig.REQUEST_CROP_PHOTO:
                    if (tempCameraFile != null) {
                        cameraPhotoWasChosen = true;
                        Uri memberImageUri = Uri.fromFile(tempCameraFile);
                        memberImageImageView.setImageURI(memberImageUri);
                    }
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_detail:
                Intent intent = new Intent();
                intent.putExtra(EXTRA_MESSAGE_UID, uid);
                intent.putExtra(EXTRA_MESSAGE_NAME, memberNameEditText.getText().toString());
                intent.putExtra(EXTRA_MESSAGE_ORIGINAL_NAME, originalName);
                if (cameraPhotoWasChosen && tempCameraFile != null) {
                    String cameraFileString = Utils.convertFileToString(tempCameraFile);
                    intent.putExtra(EXTRA_MESSAGE_FILE_STRING, cameraFileString);
                } else {
                    intent.putExtra(EXTRA_MESSAGE_FILE_STRING, memberFileString);
                }
                if (tempCameraFile != null) {
                    tempCameraFile.delete();
                }
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }

        return true;
    }
}
