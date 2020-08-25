package com.mlkit.sample.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.mlkit.sample.R;

public class DocDetectionResultActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout docBack;
    private ImageView desImage;
    private Bitmap resultBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_detection_result);

        docBack = findViewById(R.id.doc_back);
        desImage = findViewById(R.id.des_image);
        docBack.setOnClickListener(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        byte[] bytes = bundle.getByteArray("bitmap");

        resultBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        desImage.setImageBitmap(resultBitmap);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.doc_back:
                finish();
                overridePendingTransition(0,R.anim.pic_out);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (resultBitmap != null) {
            resultBitmap.recycle();
        }
    }
}