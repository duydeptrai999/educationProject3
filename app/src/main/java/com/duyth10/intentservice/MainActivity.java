package com.duyth10.intentservice;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.duyth10.intentservice.DataProcessingService;
import com.duyth10.intentservice.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String[] permissions = {
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Kiểm tra quyền
        checkPermission();

        // Xử lý intent được truyền vào
//        Intent intent = getIntent();
        handleIntent(getIntent());

        // Cài đặt màu cho thanh trạng thái
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.lavender));

        // Thêm fragment nếu chưa có
        if (savedInstanceState == null) {
            QRFragment exampleFragment = new QRFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, exampleFragment);
            transaction.commit();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }



    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void handleIntent(Intent intent) {
        if (intent != null) {
            // Lấy dữ liệu từ Intent
            String textFromMain = intent.getStringExtra("data");

            Log.d("MainActivity", "Received textFromMain: " + textFromMain);

            // Tạo Intent để khởi động IntentService
            Intent serviceIntent = new Intent(this, DataProcessingService.class);
            serviceIntent.putExtra("dataMain", textFromMain);


         //   startService(serviceIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getApplicationContext().startForegroundService(serviceIntent);
            }
        }
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            // Lấy dữ liệu từ Intent
            String textFromMain = intent.getStringExtra("data");

            Log.d("MainActivity", "Received textFromMain: " + textFromMain);

            // Tạo Intent để khởi động IntentService
            Intent serviceIntent = new Intent(this, DataProcessingService.class);
            serviceIntent.putExtra("dataMain", textFromMain);


            //   startService(serviceIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getApplicationContext().startForegroundService(serviceIntent);
            }
        }
    }

    private void checkPermission() {
        // Danh sách lưu các quyền chưa được cấp
        List<String> permissionsToRequest = new ArrayList<>();

        // Kiểm tra từng quyền
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            // Chuyển danh sách các quyền chưa được cấp sang mảng
            String[] permissionsArray = permissionsToRequest.toArray(new String[0]);
            // Yêu cầu những quyền chưa được cấp
            ActivityCompat.requestPermissions(this, permissionsArray, 1);
        }
    }
}
