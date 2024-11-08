package com.duyth10.intentservice;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutionException;

public class QRFragment extends Fragment {
    private BarcodeScanner barcodeScanner;
    private Handler handler;
    private String receiverData;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_q_r, container, false);  // Đảm bảo layout hợp lệ
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("qrFragment", "Starting DataProcessingService...");

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                Bundle bundle = msg.getData();
                String processedData = bundle.getString("processedData");
                if (processedData != null) {
                    Log.d("qrFragment", "Received data from service: " + processedData);
                    updateTextFromMainApp(processedData);
                } else {
                    Log.d("qrFragment", "No data received from service.");
                }
            }
        };

        String data = DataProcessingService.processedData;
        if (data != null) {
            Log.d("QRFragment", "Received data from service: " + data);
            updateTextFromMainApp(data);
        }
//        Messenger messenger = new Messenger(handler);

//        // Tạo Intent để khởi động Service và truyền Messenger
//        Intent serviceIntent = new Intent(getContext(), DataProcessingService.class);
//        serviceIntent.putExtra("messenger", messenger);  // Truyền Messenger cho Service
//        getContext().startService(serviceIntent);

        // Nhận dữ liệu từ MainApp
        // Khởi tạo barcode scanner
        barcodeScanner = BarcodeScanning.getClient();




        // Setup camera
        setupCamera();

        // Nút quay lại
        view.findViewById(R.id.iconBack).setOnClickListener(v -> navigateBackToMainApp());
    }

    private void setupCamera() {
        ProcessCameraProvider cameraProvider;
        try {
            cameraProvider = ProcessCameraProvider.getInstance(requireContext()).get();
            startCamera(cameraProvider);
        } catch (ExecutionException | InterruptedException e) {
            Log.e("QRFragment", "Error starting camera", e);
        }
    }
    @OptIn(markerClass = ExperimentalGetImage.class)
    private void startCamera(ProcessCameraProvider cameraProvider) {
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview.Builder().build();
        PreviewView previewView = (PreviewView) requireView().findViewById(R.id.previewView);

        // Sử dụng phương thức getSurfaceProvider() của PreviewView
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        // Set up image analysis for QR scanning
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(requireContext()), imageProxy -> {
            if (imageProxy.getImage() != null) {
                InputImage inputImage = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());
                barcodeScanner.process(inputImage)
                        .addOnSuccessListener(barcodes -> {
                            for (Barcode barcode : barcodes) {
                                String rawValue = barcode.getRawValue();
                                if (rawValue != null) {
                                    Log.d("QRFragment", "QR Code scanned: " + rawValue);

                                    // Gửi cả QR Data và receiverData tới service
                             //       sendQRDataToService(rawValue);

                                    onQRCodeScanned();
                                    break; // Chỉ xử lý 1 mã QR nếu có nhiều mã
                                }
                            }
                        })
                        .addOnFailureListener(e -> Log.e("QRFragment", "Error scanning QR code", e))
                        .addOnCompleteListener(task -> imageProxy.close()); // Đóng imageProxy sau khi hoàn tất xử lý
            } else {
                imageProxy.close(); // Đảm bảo đóng hình ảnh nếu null
            }
        });

        // Bắt đầu camera
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
    }

    void updateTextFromMainApp(String data) {
        if (isAdded() && getView() != null) {
            TextView textView = getView().findViewById(R.id.textFromMainApp);
            if (textView != null) {
                textView.setText(data);  // Cập nhật UI với dữ liệu nhận được
            }
        }
    }

    private void navigateBackToMainApp() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.duyth10.dellhieukieugi", "com.duyth10.dellhieukieugi.MainActivity"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void onQRCodeScanned() {
        // Khởi tạo TransactionResultFragment và truyền dữ liệu
        TransactionResultFragment transactionResultFragment = new TransactionResultFragment();
        // Sử dụng FragmentManager để thay thế fragment
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, transactionResultFragment);
        transaction.addToBackStack(null);  // Thêm vào back stack nếu muốn quay lại
        transaction.commit();
    }
}
