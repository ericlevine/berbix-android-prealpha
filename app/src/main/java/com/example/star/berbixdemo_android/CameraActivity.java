package com.example.star.berbixdemo_android;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;

public class CameraActivity extends Activity {

    private SurfaceView cameraView = null;

    private CameraSource camera = null;

    private static final int RC_HANDLE_CAMERA_PERM = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.berbix_fragment_id_capture);

        initCameraInstance();

        if (camera != null) {
            cameraView = findViewById(R.id.cameraView);
            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    if (ActivityCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestCameraPermission();
                    } else {
                        try {
                            camera.start(cameraView.getHolder());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {

                }
            });
        }
    }

    private void requestCameraPermission() {

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RC_HANDLE_CAMERA_PERM) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    camera.start(cameraView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
            }
        }

    }

    public void initCameraInstance(){
        try {
//            BarcodeDetector barcodeDetector =
//                    new BarcodeDetector.Builder(this)
//                            .setBarcodeFormats(Barcode.PDF417)
//                            .build();
//
//            barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
//                @Override
//                public void release() {
//
//                }
//
//                @Override
//                public void receiveDetections(Detector.Detections<Barcode> detections) {
//                    final SparseArray<Barcode> barcodes = detections.getDetectedItems();
//
//                    if (barcodes.size() != 0) {
//                        System.out.println(barcodes.valueAt(0).displayValue);
//                    }
//                }
//            });

            FaceDetector faceDetector = new FaceDetector.Builder(this).setClassificationType(FaceDetector.ALL_CLASSIFICATIONS).build();

            camera = new CameraSource.Builder(this, faceDetector).setRequestedPreviewSize(1600, 1024)
                    .setRequestedFps(15.0f).setFacing(CameraSource.CAMERA_FACING_FRONT).setAutoFocusEnabled(true).build();

            faceDetector.setProcessor(new Detector.Processor<Face>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<Face> detections) {
                    final SparseArray<Face> barcodes = detections.getDetectedItems();

                    if (barcodes.size() != 0) {
                        System.out.println(barcodes.valueAt(0).getPosition().x);
                    }
                }
            });
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            e.printStackTrace();
        }
    }
}
