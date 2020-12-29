package com.huawei.mlkit.sample.activity.table;


import android.content.ContentValues;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.JsonObject;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.fr.MLFormRecognitionAnalyzer;
import com.huawei.hms.mlsdk.fr.MLFormRecognitionAnalyzerFactory;
import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.util.BitmapUtils;

import static com.huawei.mlkit.sample.util.CommonUtils.CAMERA_PERMISSION;
import static com.huawei.mlkit.sample.util.CommonUtils.PERMISSION_CODE_CAMERA;
import static com.huawei.mlkit.sample.util.CommonUtils.PERMISSION_CODE_STORAGE;
import static com.huawei.mlkit.sample.util.CommonUtils.REQUEST_PIC;
import static com.huawei.mlkit.sample.util.CommonUtils.REQUEST_TAKE_PHOTO_CODE;
import static com.huawei.mlkit.sample.util.CommonUtils.STORAGE_PERMISSION;


public class TableRecognitionStartActivity extends AppCompatActivity implements View.OnClickListener{

    Button btn;
    ImageView imageView;
    int checkedItem = 0;

    Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_recognition_start);
        findView();
    }

    private void findView(){
        btn = findViewById(R.id.btn);
        imageView = findViewById(R.id.image);
        btn.setOnClickListener(this);
        imageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn:
                if (uri != null) {
                    BitmapFactory.Options newOpts = new BitmapFactory.Options();
                    final Bitmap b = BitmapUtils.getBitmapFromUri(this,uri,newOpts);
                    MLFrame frame = MLFrame.fromBitmap(b);
                    MLFormRecognitionAnalyzer analyzer = MLFormRecognitionAnalyzerFactory.getInstance().getFormRecognitionAnalyzer();
                    Task<JsonObject> task = analyzer.asyncAnalyseFrame(frame);

                    task.addOnSuccessListener(new OnSuccessListener<JsonObject>() {
                        @Override
                        public void onSuccess(JsonObject jsonObject) {
                            Intent i = new Intent(TableRecognitionStartActivity.this,TableRecognitionActivity.class);
                            i.putExtra("uri",uri);
                            i.putExtra("json",jsonObject.toString());
                            startActivity(i);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {

                        }
                    });
                } else {
                    Toast.makeText(this, "No picture", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.image:
                showAlertDialog();
                break;
            default:
                break;
        }
    }

    private void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select);
        final String[] choice = {getResources().getString(R.string.camera),getResources().getString(R.string.gallery)};

        builder.setSingleChoiceItems(choice, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkedItem = which;

                if (which == 0) {
                    if (ContextCompat.checkSelfPermission(TableRecognitionStartActivity.this,CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                        gotoCamera();
                    } else {
                        ActivityCompat.requestPermissions(TableRecognitionStartActivity.this,new String[]{CAMERA_PERMISSION},PERMISSION_CODE_CAMERA);
                    }
                } else if (which == 1){
                    if (ContextCompat.checkSelfPermission(TableRecognitionStartActivity.this,STORAGE_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                        gotoGallery();
                    } else {
                        ActivityCompat.requestPermissions(TableRecognitionStartActivity.this,new String[]{STORAGE_PERMISSION},PERMISSION_CODE_STORAGE);
                    }
                }
                dialog.dismiss();
            }
        });

        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE_STORAGE && grantResults!=null&&grantResults.length == 1&&grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            gotoGallery();
        } else if (requestCode == PERMISSION_CODE_CAMERA && grantResults!=null&&grantResults.length == 1&&grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            gotoCamera();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void gotoGallery(){
        Intent i = new Intent();
        i.setAction(Intent.ACTION_PICK);
        i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(i,REQUEST_PIC);
    }

    private void gotoCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        ContentValues values = new ContentValues();
        Uri photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        uri = photoUri;
        startActivityForResult(intent, REQUEST_TAKE_PHOTO_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_PIC && resultCode == RESULT_OK && data.getData() != null){
            Bitmap bitmap = BitmapUtils.tableGetBitmap(this,data.getData());
            imageView.setImageBitmap(bitmap);
            uri = data.getData();
        } else if (requestCode == REQUEST_TAKE_PHOTO_CODE&& resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapUtils.tableGetBitmap(this,uri);
            imageView.setImageBitmap(bitmap);
            imageView.setImageBitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}