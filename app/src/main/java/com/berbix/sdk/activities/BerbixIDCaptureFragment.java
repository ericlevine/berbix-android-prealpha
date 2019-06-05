package com.berbix.sdk.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
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

import com.berbix.sdk.BerbixSDK;
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

    void initCameraInstance(){
        if (camera != null) {
            camera.stop();
            camera.release();
        }

        Detector detector = null;
        int cameraPosition = 0;

        if (idStatus.frontStatus == 0) {
            cameraPosition = CameraSource.CAMERA_FACING_BACK;
        } else if (idStatus.backStatus == 0 && idStatus.idType.equals("card")) {
            cameraPosition = CameraSource.CAMERA_FACING_BACK;
        } else if ((idStatus.selfieStatus == 0 && param.selfieMatch) || (idStatus.livenessStatus == 0 && param.livenessCheck)){
            cameraPosition = CameraSource.CAMERA_FACING_FRONT;
        }

        if (cameraPosition == CameraSource.CAMERA_FACING_BACK) {
            detector = new BarcodeDetector.Builder(getActivity())
                    .setBarcodeFormats(Barcode.PDF417)
                    .build();
        } else {
            detector = new FaceDetector.Builder(getActivity())
                    .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                    .build();
        }

        camera = new CameraSource.Builder(getActivity(), detector)
                .setRequestedPreviewSize(640, 480)
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

                            if (idStatus.backStatus == 0 && idStatus.idType.equals("card")) {
                                if (idStatus.backFormat.equals("pdf417")) {
                                    capturePhoto();
                                }
                            }
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
            };

            cameraView.getHolder().addCallback(surfaceCallback);
        } else {
            try {
                camera.start(cameraView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void refreshStatus() {
        captureButton.setVisibility(View.VISIBLE);
        retakeButton.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
        tempImageView.setVisibility(View.GONE);

        if (idStatus.frontStatus == 0) {
            commandTitleLabel.setText("Front");
            commandSummaryLabel.setText("Please put the ID inside the rectangle below:");

            overlayView.setVisibility(View.VISIBLE);
        } else if (idStatus.backStatus == 0 && idStatus.idType.equals("card")) {
            overlayView.setVisibility(View.VISIBLE);
            commandTitleLabel.setText("Back");

            if (idStatus.equals("pdf417")) {
                commandSummaryLabel.setText("Please put the barcode in the back of your ID inside the rectangle below:");
            } else {
                commandSummaryLabel.setText("Please put the ID inside the rectangle below:");
            }
        } else if (idStatus.selfieStatus == 0 && param.selfieMatch) {
            overlayView.setVisibility(View.VISIBLE);
            commandTitleLabel.setText("Selfie");
            commandSummaryLabel.setVisibility(View.GONE);
        } else if (idStatus.livenessStatus == 0 && param.livenessCheck) {
            overlayView.setVisibility(View.VISIBLE);
            commandTitleLabel.setText("Liveness Check");
            commandSummaryLabel.setText("Please " + idStatus.livenessChallenge);
            commandSummaryLabel.setVisibility(View.VISIBLE);
        }

        initCameraInstance();
    }

    void capturePhoto() {
        camera.takePicture(null, new CameraSource.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes) {
                camera.stop();
                capturedPhoto = BerbixBitmapUtil.fixOrientation(bytes);
//                if (detectedBarcode != null) {
//                    DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
//                    tempImageView.setImageBitmap(BerbixBitmapUtil.cropDetected(capturedPhoto, detectedBarcode.getBoundingBox(), cameraView.getMeasuredWidth(), cameraView.getMeasuredHeight(), displayMetrics.density));
//                } else {
//                    tempImageView.setImageBitmap(BerbixBitmapUtil.cropBitmap(capturedPhoto));
//                }
                tempImageView.setImageBitmap(capturedPhoto);
                tempImageView.setVisibility(View.VISIBLE);

                captureButton.setVisibility(View.GONE);
                retakeButton.setVisibility(View.VISIBLE);
                submitButton.setVisibility(View.VISIBLE);
            }
        });
    }

    void submitPhoto() {
        BerbixAuthActivity.cProgressDialog = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Uploading...")
                .setCancellable(false)
                .setDimAmount(0.5f)
                .show();

        File file = BerbixBitmapUtil.saveToFile(getActivity(), capturedPhoto, "scaled.jpg");

        if (idStatus.frontStatus == 0) {
            Bitmap cropped = BerbixBitmapUtil.cropBitmap(capturedPhoto);
            File scaled = BerbixBitmapUtil.saveToFile(getActivity(), cropped, "file.jpg");
            BerbixSDK.shared.api().uploadPhotoId(idStatus.id, "front", file, scaled, null);
        } else if (idStatus.backStatus == 0 && idStatus.idType.equals("card")) {
            if (detectedBarcode == null) {
                BerbixAuthActivity.dismissProgressDialog();
                Toast.makeText(getActivity(), "No Barcode Detected.", Toast.LENGTH_LONG).show();
                return;
            }

            Bitmap cropped = BerbixBitmapUtil.cropBitmap(capturedPhoto);
            File scaled = BerbixBitmapUtil.saveToFile(getActivity(), cropped, "file.jpg");

            DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
            Bitmap barcodeBmp = BerbixBitmapUtil.cropDetected(capturedPhoto, detectedBarcode.getBoundingBox(), cameraView.getWidth(), cameraView.getHeight(), displayMetrics.density);
            File barcode = BerbixBitmapUtil.saveToFile(getActivity(), barcodeBmp, "barcode.jpg");

            BerbixSDK.shared.api().uploadPhotoId(idStatus.id, "back", file, scaled, barcode);
        } else if (idStatus.selfieStatus == 0 && param.selfieMatch) {
            if (detectedFace == null) {
                BerbixAuthActivity.dismissProgressDialog();
                Toast.makeText(getActivity(), "No Face Detected.", Toast.LENGTH_LONG).show();
                return;
            }

            DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
            Bitmap faceBmp = BerbixBitmapUtil.cropFace(capturedPhoto, detectedFace, cameraView.getWidth(), cameraView.getHeight(), displayMetrics.density);
            File face = BerbixBitmapUtil.saveToFile(getActivity(), faceBmp, "barcode.jpg");

            BerbixSDK.shared.api().uploadPhotoId(idStatus.id, "selfie", null, face, null);
        } else if (idStatus.livenessStatus == 0 && param.livenessCheck) {
            if (detectedFace == null) {
                BerbixAuthActivity.dismissProgressDialog();
                Toast.makeText(getActivity(), "No Face Detected.", Toast.LENGTH_LONG).show();
                return;
            }

            DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
            Bitmap faceeBmp = BerbixBitmapUtil.cropFace(capturedPhoto, detectedFace, cameraView.getWidth(), cameraView.getHeight(), displayMetrics.density);
            File face = BerbixBitmapUtil.saveToFile(getActivity(), faceeBmp, "barcode.jpg");

            BerbixSDK.shared.api().uploadPhotoId(idStatus.id, "liveness", null, face, null);
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
