package com.berbix.sdk;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.berbix.sdk.activities.BerbixAuthActivity;
import com.berbix.sdk.response.BerbixPhotoIDStatusResponse;
import com.berbix.sdk.response.BerbixPhotoIdPayload;
import com.berbix.sdk.response.BerbixResponse;

public class BerbixAuthFlow extends BerbixApiAdapter {

    Context context = null;
    BerbixPhotoIdPayload idParam = null;

    public BerbixAuthActivity authActivity = null;

    void startAuthFlow() {
        Intent authIntent = new Intent(context, BerbixAuthActivity.class);
        context.startActivity(authIntent);
    }

    void nextStep(BerbixResponse response) {
        BerbixAuthActivity.dismissProgressDialog();

        if (response.next != null) {
            if (response.next.type.equals("verification")) {
                if (response.next.payload.type.equals("phone")) {
                    authActivity.verifyPhone();
                } else if (response.next.payload.type.equals("email")) {
                    authActivity.verifyEmail();
                } else if (response.next.payload.type.equals("photoid")) {
                    idParam = response.next.payload.photoIdDetails;
                    if (response.next.payload.photoIdDetails.idTypes.equals("card_or_passport")) {
                        authActivity.chooseIDType();
                    } else {
                        BerbixSDK.shared.api().startPhotoIDVerification(null);
                    }
                }
            } else if (response.next.type.equals("done")) {
                authActivity.finish();
                BerbixSDK.shared.adapter().authorized(response.next.code);
            }
        }
    }

    void phoneSubmitted(BerbixResponse response) {
        BerbixAuthActivity.dismissProgressDialog();
        authActivity.verifyCode(response.id, true);
    }

    void emailSubmitted(BerbixResponse response) {
        BerbixAuthActivity.dismissProgressDialog();
        authActivity.verifyCode(response.id, false);
    }

    void photoUploaded(BerbixPhotoIDStatusResponse response) {
        BerbixAuthActivity.dismissProgressDialog();

        if (response.next != null && response.next.type.equals("verification")) {
            authActivity.verifyDetail(response);
        } else {
            authActivity.captureIDFragment.idStatus = response;
            authActivity.captureIDFragment.refreshStatus();
        }
    }

    void startIDCapture(BerbixPhotoIDStatusResponse response) {
        BerbixAuthActivity.dismissProgressDialog();

        authActivity.captureID(response, idParam);
    }

    void failed(String error) {
        BerbixAuthActivity.dismissProgressDialog();

        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
    }
}
