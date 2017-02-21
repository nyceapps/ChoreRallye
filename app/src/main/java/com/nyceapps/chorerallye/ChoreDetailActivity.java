package com.nyceapps.chorerallye;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.mikelau.croperino.Croperino;
import com.mikelau.croperino.CroperinoConfig;

import java.io.File;

import static com.nyceapps.chorerallye.Constants.EXTRA_MESSAGE_FILE_STRING;
import static com.nyceapps.chorerallye.Constants.EXTRA_MESSAGE_NAME;
import static com.nyceapps.chorerallye.Constants.EXTRA_MESSAGE_UID;
import static com.nyceapps.chorerallye.Constants.EXTRA_MESSAGE_VALUE;
import static com.nyceapps.chorerallye.Constants.REQUEST_CODE_CAPTURE_IMAGE_FROM_CAMERA;

public class ChoreDetailActivity extends AppCompatActivity {
    private String uid;
    private String choreFileString;

    private boolean cameraPhotoWasChosen;
    private File tempCameraFile;

    private ImageView choreImageImageView;
    private EditText choreNameEditText;
    private EditText choreValueEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chore_detail);

        Intent intent = getIntent();
        uid = intent.getStringExtra(EXTRA_MESSAGE_UID);

        choreImageImageView = (ImageView) findViewById(R.id.chore_image);
        choreFileString = intent.getStringExtra(EXTRA_MESSAGE_FILE_STRING);
        BitmapDrawable choreBitmapDrawable = Utils.convertStringToBitmapDrawable(choreFileString, this);
        if (choreBitmapDrawable != null) {
            choreImageImageView.setImageDrawable(choreBitmapDrawable);
        }
        choreImageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        String choreName = intent.getStringExtra(EXTRA_MESSAGE_NAME);
        setTitle(choreName);
        choreNameEditText = (EditText) findViewById(R.id.chore_name);
        choreNameEditText.setText(choreName);
        choreNameEditText.addTextChangedListener(new TextWatcher() {
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

        int choreValue = intent.getIntExtra(EXTRA_MESSAGE_VALUE, -1);
        choreValueEditText = (EditText) findViewById(R.id.chore_value);
        if (choreValue > 0) {
            choreValueEditText.setText(String.valueOf(choreValue));
        }

        cameraPhotoWasChosen = false;
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
                        choreImageImageView.setImageURI(memberImageUri);
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
                intent.putExtra(EXTRA_MESSAGE_NAME, choreNameEditText.getText().toString());
                intent.putExtra(EXTRA_MESSAGE_VALUE, Integer.valueOf(choreValueEditText.getText().toString()));
                if (cameraPhotoWasChosen && tempCameraFile != null) {
                    String cameraFileString = Utils.convertFileToString(tempCameraFile);
                    intent.putExtra(EXTRA_MESSAGE_FILE_STRING, cameraFileString);
                } else {
                    intent.putExtra(EXTRA_MESSAGE_FILE_STRING, choreFileString);
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
