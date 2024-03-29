package com.berbix.berbixverify.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.berbix.berbixverify.BerbixStateManager;
import com.berbix.berbixverify.response.BerbixPhotoIDStatusResponse;
import com.berbix.berbixverify.response.BerbixPhotoIdPayload;
import com.berbix.berbixverify.R;
import com.kaopiz.kprogresshud.KProgressHUD;

public class BerbixFlowActivity extends AppCompatActivity {

    public static KProgressHUD cProgressDialog = null;

    private BerbixIDCaptureFragment captureIDFragment = null;

    private static final int RC_HANDLE_CAMERA_PERM = 2;

    public static void dismissProgressDialog() {
        if (cProgressDialog != null) {
            cProgressDialog.dismiss();
            cProgressDialog = null;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.berbix_activity_main);

        BerbixInitializationFragment initFragment = new BerbixInitializationFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragmentContainer, initFragment);
        ft.commit();

        BerbixStateManager.getAuthFlow().setActivity(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RC_HANDLE_CAMERA_PERM) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureIDFragment.startCamera();
            } else {
            }
        }

    }

    public void verifyPhone() {
        BerbixPhoneInputFragment phoneFragment = new BerbixPhoneInputFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragmentContainer, phoneFragment);
        ft.commit();
    }

    public void verifyEmail() {
        BerbixEmailInputFragment emailFragment = new BerbixEmailInputFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragmentContainer, emailFragment);
        ft.commit();
    }

    public void verifyCode(long parentId, boolean isPhone) {
        BerbixVerifyCodeFragment verifyFragment = new BerbixVerifyCodeFragment();

        verifyFragment.parentId = parentId;
        verifyFragment.isPhoneVerification = isPhone;

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragmentContainer, verifyFragment);
        ft.commit();
    }

    public void startPhotoIDScan() {
        BerbixPhotoIDScanFragment scanFragment = new BerbixPhotoIDScanFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragmentContainer, scanFragment);
        ft.commit();
    }

    public void chooseIDType() {
        BerbixChooseIdTypeFragment chooseIdTypeFragment = new BerbixChooseIdTypeFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragmentContainer, chooseIdTypeFragment);
        ft.commit();
    }

    public void captureID(BerbixPhotoIDStatusResponse idStatus, BerbixPhotoIdPayload idParam) {
        captureIDFragment = new BerbixIDCaptureFragment();
        captureIDFragment.param = idParam;
        captureIDFragment.idStatus = idStatus;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragmentContainer, captureIDFragment);
        ft.commit();
    }

    public void verifyDetail(BerbixPhotoIdPayload idParam) {
        BerbixDetailsFragment detailsFragment = new BerbixDetailsFragment();
        detailsFragment.param = idParam;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragmentContainer, detailsFragment);
        ft.commit();
    }

    public void updateCaptureFragmentState(BerbixPhotoIDStatusResponse response) {
        this.captureIDFragment.updateState(response);
    }

}
