package com.huawei.mlkit.sample.activity.table;


import android.annotation.SuppressLint;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.huawei.hms.mlsdk.fr.MLFormRecognitionConstant;
import com.huawei.hms.mlsdk.fr.MLFormRecognitionTablesAttribute;
import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.activity.adapter.ItemAdapter;
import com.huawei.mlkit.sample.util.BitmapUtils;


import java.io.File;

import java.io.IOException;


import jxl.Workbook;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import static com.huawei.mlkit.sample.util.CommonUtils.PERMISSION_CODE_STORAGE;
import static com.huawei.mlkit.sample.util.CommonUtils.STORAGE_PERMISSION;


public class TableRecognitionActivity extends AppCompatActivity {
    private static final String TAG = "TableRecognitionActivity";

    ImageView imageView;
    RecyclerView recyclerView;
    ItemAdapter adapter;
    FloatingActionButton fab;
    private TextView text_error;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_recognition);

        imageView = findViewById(R.id.image);
        recyclerView = findViewById(R.id.rv);
        fab = findViewById(R.id.fab);
        text_error = findViewById(R.id.error);

        Uri u = getIntent().getParcelableExtra("uri");
        String str = getIntent().getStringExtra("json");
        Bitmap bitmap = BitmapUtils.tableGetBitmap(this,u);
        imageView.setImageBitmap(bitmap);

        adapter = new ItemAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,RecyclerView.VERTICAL));
        recyclerView.setAdapter(adapter);
        try {
            Gson gson = new Gson();
            MLFormRecognitionTablesAttribute attribute = gson.fromJson(str,MLFormRecognitionTablesAttribute.class);
            if (attribute.getRetCode()== MLFormRecognitionConstant.SUCCESS){
                for (int i = 0; i < attribute.getTablesContent().getTableAttributes().get(0).getTableCellAttributes().size(); i++) {
                    adapter.list.add(attribute.getTablesContent().getTableAttributes().get(0).getTableCellAttributes().get(i));
                }
                adapter.notifyDataSetChanged();
            }else if (attribute.getRetCode()== MLFormRecognitionConstant.FAILED){
                text_error.setText(getString(R.string.teble_error));
            }

        } catch (RuntimeException e) {
            Log.e(TAG,e.getMessage());
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(TableRecognitionActivity.this,STORAGE_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        createExcel();
                    } catch (IOException e) {
                        Log.e(TAG,e.getMessage());
                    } catch (WriteException e) {
                        Log.e(TAG,e.getMessage());
                    }
                } else {
                    ActivityCompat.requestPermissions(TableRecognitionActivity.this,new String[]{STORAGE_PERMISSION},PERMISSION_CODE_STORAGE);
                }
            }
        });
    }


    @SuppressLint("LongLogTag")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE_STORAGE && grantResults!=null&&grantResults.length == 1&&grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                createExcel();
            } catch (IOException e) {
                Log.e(TAG,e.getMessage());
            } catch (WriteException e) {
                Log.e(TAG,e.getMessage());
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void createExcel() throws IOException, WriteException {
        WritableWorkbook workbook = null;
        String fileName = getExternalCacheDir().getPath()+File.separator+"TableRecognition.xls";
        File file = new File(fileName);
        if (!file.exists()) {
            file.delete();
            file.createNewFile();
        }
        workbook = Workbook.createWorkbook(file);
        WritableSheet sheet = workbook.createSheet("sheet 1", 0);
        for (int i = 0; i < adapter.list.size(); i++) {
            Label l = new Label(adapter.list.get(i).getStartCol(),adapter.list.get(i).getStartRow(),adapter.list.get(i).getTextInfo());
            sheet.addCell(l);
            sheet.mergeCells(adapter.list.get(i).getStartCol(),adapter.list.get(i).getStartRow(),adapter.list.get(i).getEndCol(),adapter.list.get(i).getEndRow());
        }
        workbook.write();
        workbook.close();

        Toast.makeText(this,"create table successfully,location\n "+fileName,Toast.LENGTH_SHORT).show();
    }
}
