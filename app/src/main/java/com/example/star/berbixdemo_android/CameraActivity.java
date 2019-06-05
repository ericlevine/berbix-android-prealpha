package com.example.star.berbixdemo_android;

import android.app.Activity;
import android.os.Bundle;
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
                    try {
                        camera.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
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

            camera = new CameraSource.Builder(this, faceDetector).setRequestedPreviewSize(640, 480).setFacing(CameraSource.CAMERA_FACING_FRONT).setAutoFocusEnabled(true).build();

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
