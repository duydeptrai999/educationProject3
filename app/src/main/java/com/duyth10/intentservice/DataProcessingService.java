    package com.duyth10.intentservice;

    import android.annotation.SuppressLint;
    import android.app.IntentService;
    import android.app.Notification;
    import android.app.NotificationChannel;
    import android.app.NotificationManager;
    import android.content.Intent;
    import android.os.Build;
    import android.os.Bundle;
    import android.os.Message;
    import android.os.Messenger;
    import android.os.PowerManager;
    import android.os.RemoteException;
    import android.os.ResultReceiver;
    import android.util.Log;

    import androidx.core.app.NotificationCompat;

    public class DataProcessingService extends IntentService {

        public static String processedData;
        private  String receiverData;


        private PowerManager.WakeLock wakeLock;

        public DataProcessingService() {
            super("DataProcessingService");
        }

        @Override
        public void onCreate() {
            super.onCreate();
            Log.d("DataProcessingService", "IntentService Created");

            // Tạo NotificationChannel trước khi tạo notification
            createNotificationChannel();

            // Khởi tạo notification
            Notification notification = new NotificationCompat.Builder(this, "data_processing_channel")
                    .setContentTitle("Data Processing Service")
                    .setContentText("Service is running...")
                    .setSmallIcon(R.drawable.baseline_notifications)  // Đ qảm bảo icon tồn tại
                    .build();

            // Gọi startForeground để tránh bị hệ thống ngắt
            this.startForeground(1, notification);
            Log.d("DataProcessingService", "Start Foregorund");

        }



        @Override
        protected void onHandleIntent(Intent intent) {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DataProcessingService::WakelockTag");
            wakeLock.acquire();

            try {
                if (intent != null) {
                    // Nhận dữ liệu từ QRFragment
                     receiverData = intent.getStringExtra("dataMain");
                    String qrData = intent.getStringExtra("qrData");
                    processedData = intent.getStringExtra("dataMain");

                    Log.d("DataProcessingService", "Received receiverData: " + receiverData);
                    Log.d("DataProcessingService", "Received QR data: " + qrData);



                    // Gửi lại dữ liệu qua Messenger
                    Messenger messenger = intent.getParcelableExtra("messenger");
                    if (messenger != null) {
                        Message msg = Message.obtain();
                        Bundle bundle = new Bundle();

                        bundle.putString("processedData", receiverData);

                        msg.setData(bundle);

                        try {
                            messenger.send(msg);  // Gửi message về QRFragment
                            Log.d("DataProcessingService", "Message sent to QRFragment");
                        } catch (RemoteException e) {
                            Log.e("DataProcessingService", "Error sending message", e);
                        }
                    }
                }
            } finally {
                // Giải phóng WakeLock
                if (wakeLock != null && wakeLock.isHeld()) {
                    wakeLock.release();
                }
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.d("DataProcessingService", "IntentService Destroyed");
        }


        private void createNotificationChannel() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  // Đảm bảo chỉ tạo trên Android 8.0 (Oreo) trở lên
                CharSequence name = "Data Processing Service Channel";
                String description = "Channel for Data Processing Service";
                int importance = NotificationManager.IMPORTANCE_HIGH;  // Đặt độ ưu tiên cao để đảm bảo hiển thị
                NotificationChannel channel = new NotificationChannel("data_processing_channel", name, importance);
                channel.setDescription(description);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                }
            }
        }


    }
