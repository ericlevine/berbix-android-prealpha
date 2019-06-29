package com.berbix.sdk.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.berbix.sdk.BerbixStateManager;
import com.berbix.sdk.response.BerbixPhotoIDStatusResponse;
import com.berbix.sdk.response.BerbixPhotoIdPayload;
import com.example.star.berbixdemo_android.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.face.Face;

import java.io.IOException;

public class BerbixPhotoIDScanFragment extends Fragment implements View.OnClickListener {

    public BerbixPhotoIDStatusResponse idStatus = null;
    public BerbixPhotoIdPayload param = null;

    private SurfaceView cameraView = null;
    private SurfaceHolder.Callback surfaceCallback = null;
    private CameraSource camera = null;

    private Button retakeButton = null;
    private Button submitButton = null;
    private ImageView captureButton = null;
    private LinearLayout overlayView = null;
    private TextView commandTitleLabel = null;
    private TextView commandSummaryLabel = null;

    private ImageView tempImageView = null;
    private Bitmap capturedPhoto = null;

    private Barcode detectedBarcode = null;
    private Face detectedFace = null;

    private boolean submitting = false;

    private static final int RC_HANDLE_CAMERA_PERM = 2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.berbix_fragment_id_scan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        captureButton = view.findViewById(R.id.captureButton);
        retakeButton = view.findViewById(R.id.retakeButton);
        submitButton = view.findViewById(R.id.submitButton);
        overlayView = view.findViewById(R.id.overlayLayout);
        commandTitleLabel = view.findViewById(R.id.commandTitleLabel);
        commandSummaryLabel = view.findViewById(R.id.commandSummaryLabel);
        tempImageView = view.findViewById(R.id.tempImageView);

        captureButton.setVisibility(View.VISIBLE);
        retakeButton.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
        tempImageView.setVisibility(View.GONE);

        captureButton.setOnClickListener(this);
        retakeButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);

        cameraView = view.findViewById(R.id.cameraView);

        initCameraInstance();
    }

    void initCameraInstance(){
        if (camera != null) {
            camera.stop();
            camera.release();
        }

        Detector detector = new BarcodeDetector.Builder(getActivity())
                .setBarcodeFormats(Barcode.PDF417)
                .build();
        int cameraPosition = CameraSource.CAMERA_FACING_BACK;

        camera = new CameraSource.Builder(getActivity(), detector)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f)
                .setFacing(cameraPosition)
                .setAutoFocusEnabled(true)
                .build();

        detector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                if (!submitting) {
                    if (detections.getDetectedItems().size() > 0) {
                        submitting = true;
                        Barcode detectedBarcode = detections.getDetectedItems().valueAt(0);
                        BerbixStateManager.getApiManager().submitPhotoIDScan(detectedBarcode.rawValue);
                    }
                }
            }
        });

        if (surfaceCallback == null) {
            surfaceCallback = new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    startCamera();
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {

                }
            };

            cameraView.getHolder().addCallback(surfaceCallback);
        } else {
            startCamera();
        }

    }

    public void startCamera() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        } else {
            try {
                camera.start(cameraView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, RC_HANDLE_CAMERA_PERM);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.captureButton) {
            //capturePhoto();
        } else if (v.getId() == R.id.submitButton) {
            //submitPhoto();
        } else if (v.getId() == R.id.retakeButton) {
            //refreshStatus();
        }
    }

}
