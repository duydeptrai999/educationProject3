package com.duyth10.intentservice;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TransactionResultFragment extends Fragment {

    private String qrData;
    private String textFromMain;

    private TextView qrDataTextView;
    private TextView mainAppDataTextView;

    // Handler to receive messages from the service
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String qrData = bundle.getString("qrData");
            String processedData = bundle.getString("dataMain");

            Log.d("transaction","data main"+processedData);
            Log.d("transaction","data main"+processedData);

            // Cập nhật UI với dữ liệu nhận được
            updateUIWithQRData(qrData, processedData);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_transaction_result, container, false);

        // Initialize views
        qrDataTextView = rootView.findViewById(R.id.qrDataTextView);
        mainAppDataTextView = rootView.findViewById(R.id.textMonney);



        // Update the UI with initial data
        updateUI();

        // Start the service and pass the messenger to receive data
        startDataProcessingService();

        return rootView;
    }

    // Method to update UI with the received data
    private void updateUI() {
        if (!TextUtils.isEmpty(qrData)) {
            qrDataTextView.setText(qrData);
        }

        if (!TextUtils.isEmpty(textFromMain)) {
            mainAppDataTextView.setText(textFromMain);
        }
    }

    // Method to start the service and pass the messenger
    private void startDataProcessingService() {
        Intent serviceIntent = new Intent(requireContext(), DataProcessingService.class);

        // Pass the handler's messenger to the service
        Messenger messengerTransaction = new Messenger(handler);
        serviceIntent.putExtra("messengerTransactionFragment", messengerTransaction);

        // Start the service
        requireActivity().startService(serviceIntent);
    }

    // Method to update UI with data from the service
    public void updateUIWithQRData(String qrData, String processedData) {
        // Cập nhật dữ liệu trong UI
        if (qrData != null) {
            qrDataTextView.setText(qrData);
        }
        if (processedData != null) {
            mainAppDataTextView.setText(processedData);
        }
    }
}
