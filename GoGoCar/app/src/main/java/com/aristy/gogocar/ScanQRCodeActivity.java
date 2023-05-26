package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_QRCODE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class ScanQRCodeActivity extends AppCompatActivity {

    public static final String QR_CODE_VALUE = "qr_code";

    private final int CAMERA_REQUEST_CODE = 2;

    private SurfaceView scanSurfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);

        scanSurfaceView = findViewById(R.id.scan_surface_view);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG_QRCODE, "onResume: ");

        initBarcodeDetector();
        initCameraSource();
        initScanSurfaceView();

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Log.d(TAG_QRCODE, "Camera has been released");
            }

            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                // When detection
                SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() > 0){
                    for (int i = 0; i < barcodes.size(); i++) {
                        if (! barcodes.valueAt(i).displayValue.isEmpty()) {
                            onQRCodeScanned(barcodes.valueAt(i).displayValue);
                        }
                    }
                }
            }
        });
    }

    /**
     * Close & return the result
     * @param qrCodeValue QR Code value
     */
    private void onQRCodeScanned(String qrCodeValue){
        Log.d(TAG_QRCODE, "onQRCodeScanned: " + qrCodeValue);

        Intent intent = new Intent();
        intent.putExtra(QR_CODE_VALUE, qrCodeValue);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void initBarcodeDetector(){
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        Log.d(TAG_QRCODE, "initBarcodeDetector: OK");
    }

    private void initCameraSource(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        Log.d(TAG_QRCODE, "initCameraSource: height=" + screenHeight + "px, width=" + screenWidth + "px");

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(screenHeight, screenWidth)
                .setAutoFocusEnabled(true)
                .build();
        Log.d(TAG_QRCODE, "initCameraSource: OK");
    }

    private void initScanSurfaceView(){
        Log.d(TAG_QRCODE, "initScanSurfaceView:");
        scanSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(ScanQRCodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    try {
                        cameraSource.start(scanSurfaceView.getHolder());
                        Log.d(TAG_QRCODE, "surfaceCreated: OK");
                    } catch (IOException e) {
                        Log.e(TAG_QRCODE, "surfaceCreated: ERROR", e);
                    }
                } else {
                    ActivityCompat.requestPermissions(ScanQRCodeActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {}

            // When the surface view is destroy
            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                cameraSource.release();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (cameraPermissionGranted(requestCode, grantResults)){
            Log.d(TAG_QRCODE, "onRequestPermissionsResult: granted");

            Intent intent = new Intent(this, ScanQRCodeActivity.class);
            setResult(Activity.RESULT_CANCELED, intent);
            finish();
            overridePendingTransition(0, 0);
            // Start the Scan QRCODE Activity
            // Issue when restart: return last state of the activity (when ask permission)
            //startActivity(intent);
            //overridePendingTransition(0, 0);
        } else {
            // If user do not authorize, return to the last screen
            Log.d(TAG_QRCODE, "onRequestPermissionsResult: not granted");
            finish();
            overridePendingTransition(0, 0);
        }

    }

    private boolean cameraPermissionGranted(int requestCode, int[] grantResults){
        return requestCode == CAMERA_REQUEST_CODE && grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }
}