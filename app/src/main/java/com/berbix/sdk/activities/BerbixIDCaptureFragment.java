package com.berbix.sdk.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.berbix.sdk.BerbixStateManager;
import com.berbix.sdk.bitmap.BerbixBitmapUtil;
import com.berbix.sdk.response.BerbixPhotoIDStatusResponse;
import com.berbix.sdk.response.BerbixPhotoIdPayload;
import com.example.star.berbixdemo_android.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.File;
import java.io.IOException;

public class BerbixIDCaptureFragment extends Fragment implements View.OnClickListener {

    private static double CARD_RATIO = 1.6;
    private static double PASSPORT_RATIO = 1.3;

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

    private long barcodeDetectedAt = 0;
    private boolean capturing = false;

    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private static final int NOT_DONE = 0;

    private enum Step {
        FRONT,
        BACK,
        SELFIE,
        LIVENESS,
        DONE
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.berbix_fragment_id_capture, container, false);
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

        captureButton.setOnClickListener(this);
        retakeButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);

        cameraView = view.findViewById(R.id.cameraView);

        refreshStatus();
    }

    private Step getStep() {
        if (frontStatus() == NOT_DONE) {
            return Step.FRONT;
        } else if (idStatus.backStatus == 0 && idStatus.idType.equals("card")) {
            return Step.BACK;
        } else if (idStatus.selfieStatus == 0 && param.selfieMatch) {
            return Step.SELFIE;
        } else if (idStatus.livenessStatus == 0 && param.livenessCheck) {
            return Step.LIVENESS;
        }
        return Step.DONE;
    }

    private Detector getDetector() {
        Step step = getStep();
        if (step == Step.BACK) {
            return new BarcodeDetector.Builder(getActivity())
                    .setBarcodeFormats(Barcode.PDF417)
                    .build();
        } else {
            return new FaceDetector.Builder(getActivity())
                    .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                    .build();
        }
    }

    private int cameraDirection() {
        switch (getStep()) {
            case SELFIE:
            case LIVENESS:
                return CameraSource.CAMERA_FACING_FRONT;
            default:
                return CameraSource.CAMERA_FACING_BACK;
        }
    }

    private double aspectRatio() {
        if (idStatus != null) {
            if ("card".equals(idStatus.idType)) {
                return CARD_RATIO;
            } else {
                return PASSPORT_RATIO;
            }
        } else if (param != null) {
            if ("passport".equals(param.idTypes)) {
                return PASSPORT_RATIO;
            } else {
                return CARD_RATIO;
            }
        }
        return CARD_RATIO;
    }

    void initCameraInstance(){
        if (camera != null) {
            camera.stop();
            camera.release();
        }

        Detector detector = getDetector();
        int cameraPosition = cameraDirection();

        camera = new CameraSource.Builder(getActivity(), detector)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f)
                .setFacing(cameraPosition)
                .setAutoFocusEnabled(true)
                .build();

        if (detector != null) {
            if (detector instanceof BarcodeDetector) {
                detector.setProcessor(new Detector.Processor<Barcode>() {
                    @Override
                    public void release() {

                    }

                    @Override
                    public void receiveDetections(Detector.Detections<Barcode> detections) {
                        if (detections.getDetectedItems().size() > 0) {
                            detectedBarcode = detections.getDetectedItems().valueAt(0);

                            long now = System.currentTimeMillis();
                            if (now - barcodeDetectedAt < 500) {
                                capturePhoto();
                            }
                            barcodeDetectedAt = now;
                        }
                    }
                });
            } else {
                detector.setProcessor(new Detector.Processor<Face>() {
                    @Override
                    public void release() {

                    }

                    @Override
                    public void receiveDetections(Detector.Detections<Face> detections) {
                        if (detections.getDetectedItems().size() > 0) {
                            detectedFace = detections.getDetectedItems().valueAt(0);
                        }
                    }
                });
            }
        }

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

    public void refreshStatus() {
        captureButton.setVisibility(View.VISIBLE);
        retakeButton.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
        tempImageView.setVisibility(View.GONE);

        Step step = getStep();
        if (step == Step.FRONT) {
            commandTitleLabel.setText("Front");
            commandSummaryLabel.setText("Please put the ID inside the rectangle below:");

            overlayView.setVisibility(View.VISIBLE);
        } else if (step == Step.BACK) {
            overlayView.setVisibility(View.VISIBLE);
            commandTitleLabel.setText("Back");

            if (idStatus.equals("pdf417")) {
                commandSummaryLabel.setText("Please put the barcode in the back of your ID inside the rectangle below:");
            } else {
                commandSummaryLabel.setText("Please put the ID inside the rectangle below:");
            }
        } else if (step == Step.SELFIE) {
            overlayView.setVisibility(View.VISIBLE);
            commandTitleLabel.setText("Selfie");
            commandSummaryLabel.setVisibility(View.GONE);
        } else if (step == Step.LIVENESS) {
            overlayView.setVisibility(View.VISIBLE);
            commandTitleLabel.setText("Liveness Check");
            commandSummaryLabel.setText("Please " + idStatus.livenessChallenge);
            commandSummaryLabel.setVisibility(View.VISIBLE);
        }

        initCameraInstance();
    }

    public void updateState(BerbixPhotoIDStatusResponse idStatus) {
        this.idStatus = idStatus;
        this.refreshStatus();
    }

    void capturePhoto() {
        if (capturing) {
            return;
        }

        capturing = true;

        camera.takePicture(null, new CameraSource.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes) {
                capturing = false;

                camera.stop();
                capturedPhoto = BerbixBitmapUtil.fixOrientation(bytes);
                Bitmap scaled = Bitmap.createScaledBitmap(capturedPhoto, capturedPhoto.getWidth() / 2, capturedPhoto.getHeight() / 2, false);
                tempImageView.setImageBitmap(scaled);
                tempImageView.setVisibility(View.VISIBLE);

                captureButton.setVisibility(View.GONE);
                retakeButton.setVisibility(View.VISIBLE);
                submitButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private long verificationId() {
        if (idStatus == null) {
            return 0;
        }
        return idStatus.id;
    }

    private int frontStatus() {
        if (idStatus == null) {
            return NOT_DONE;
        }
        return idStatus.frontStatus;
    }

    void submitPhoto() {
        BerbixFlowActivity.cProgressDialog = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Uploading...")
                .setCancellable(false)
                .setDimAmount(0.5f)
                .show();

        File scaled = BerbixBitmapUtil.saveToJpg(getActivity(), capturedPhoto, "scaled.jpg");

        long id = this.verificationId();
        Step step = getStep();

        if (step == Step.FRONT) {
            Bitmap cropped = BerbixBitmapUtil.cropBitmap(capturedPhoto, aspectRatio());
            File croppedFile = BerbixBitmapUtil.saveToJpg(getActivity(), cropped, "file.jpg");
            BerbixStateManager.getApiManager().uploadPhotoId(id, "front", croppedFile, scaled, null);
        } else if (step == Step.BACK) {
            if (detectedBarcode == null) {
                BerbixFlowActivity.dismissProgressDialog();
                Toast.makeText(getActivity(), "No Barcode Detected.", Toast.LENGTH_LONG).show();
                return;
            }

            Bitmap cropped = BerbixBitmapUtil.cropBitmap(capturedPhoto, aspectRatio());
            File croppedFile = BerbixBitmapUtil.saveToJpg(getActivity(), cropped, "file.jpg");

            Log.e("barcode bounding box", detectedBarcode.getBoundingBox().toString());

            DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
            Bitmap barcodeBmp = BerbixBitmapUtil.cropDetected(capturedPhoto, detectedBarcode.getBoundingBox(), cameraView.getWidth(), cameraView.getHeight(), displayMetrics.density);
            File barcode = BerbixBitmapUtil.saveToPng(getActivity(), barcodeBmp, "barcode.png");

            BerbixStateManager.getApiManager().uploadPhotoId(id, "back", croppedFile, scaled, barcode);
        } else if (step == Step.SELFIE) {
            if (detectedFace == null) {
                BerbixFlowActivity.dismissProgressDialog();
                Toast.makeText(getActivity(), "No Face Detected.", Toast.LENGTH_LONG).show();
                return;
            }

            DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
            Bitmap faceBmp = BerbixBitmapUtil.cropFace(capturedPhoto, detectedFace, displayMetrics.widthPixels, displayMetrics.heightPixels, displayMetrics.density);
            File face = BerbixBitmapUtil.saveToJpg(getActivity(), faceBmp, "barcode.jpg");

            BerbixStateManager.getApiManager().uploadPhotoId(id, "selfie", null, face, null);
        } else if (step == Step.LIVENESS) {
            if (detectedFace == null) {
                BerbixFlowActivity.dismissProgressDialog();
                Toast.makeText(getActivity(), "No Face Detected.", Toast.LENGTH_LONG).show();
                return;
            }

            DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
            Bitmap faceeBmp = BerbixBitmapUtil.cropFace(capturedPhoto, detectedFace, cameraView.getWidth(), cameraView.getHeight(), displayMetrics.density);
            File face = BerbixBitmapUtil.saveToJpg(getActivity(), faceeBmp, "barcode.jpg");

            BerbixStateManager.getApiManager().uploadPhotoId(id, "liveness", null, face, null);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.captureButton) {
            capturePhoto();
        } else if (v.getId() == R.id.submitButton) {
            submitPhoto();
        } else if (v.getId() == R.id.retakeButton) {
            refreshStatus();
        }
    }

}
