package com.berbix.berbixverify;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.berbix.berbixverify.activities.BerbixFlowActivity;
import com.berbix.berbixverify.response.BerbixPhotoIDStatusResponse;
import com.berbix.berbixverify.response.BerbixPhotoIdPayload;
import com.berbix.berbixverify.response.BerbixResponse;

public class BerbixAuthFlow extends BerbixAPIManager.BerbixAPIAdapter {

    public interface BerbixAuthFlowAdapter {
        void receiveCode(String code);
    }

    public BerbixAuthFlow(BerbixAuthFlowAdapter adapter) {
        this.adapter = adapter;
    }

    private final BerbixAuthFlowAdapter adapter;

    private Context context = null;
    private BerbixPhotoIdPayload idParam = null;
    private BerbixResponse sessionResponse = null;

    public BerbixFlowActivity activity = null;

    void startAuthFlow(Context context) {
        Intent authIntent = new Intent(context, BerbixFlowActivity.class);
        context.startActivity(authIntent);
        this.context = context;

        if (sessionResponse == null) {
            BerbixStateManager.getApiManager().createSession(null);
        }
    }

    void createSession(final Runnable callback) {
        BerbixStateManager.getApiManager().createSession(new BerbixAPIManager.ResponseConsumer() {
            @Override
            public void consume(BerbixResponse response) {
                sessionResponse = response;
                callback.run();
            }
        });
    }

    void nextStep(BerbixResponse response) {
        BerbixFlowActivity.dismissProgressDialog();

        if (activity == null) {
            return;
        }

        if (response.next != null) {
            if (response.next.type.equals("verification")) {
                if (response.next.payload.type.equals("phone")) {
                    activity.verifyPhone();
                } else if (response.next.payload.type.equals("email")) {
                    activity.verifyEmail();
                } else if (response.next.payload.type.equals("photoid")) {
                    idParam = response.next.payload.photoIdDetails;
                    if (response.next.payload.photoIdDetails.idTypes.equals("card_or_passport")) {
                        activity.chooseIDType();
                    } else {
                        activity.captureID(null, response.next.payload.photoIdDetails);
                    }
                } else if (response.next.payload.type.equals("details")) {
                    activity.verifyDetail(response.next.payload.photoIdDetails);
                } else if (response.next.payload.type.equals("idscan")) {
                    activity.startPhotoIDScan();
                }
            } else if (response.next.type.equals("done")) {
                activity.finish();
                adapter.receiveCode(response.next.payload.code);
            }
        }
    }

    void phoneSubmitted(BerbixResponse response) {
        BerbixFlowActivity.dismissProgressDialog();
        activity.verifyCode(response.id, true);
    }

    void emailSubmitted(BerbixResponse response) {
        BerbixFlowActivity.dismissProgressDialog();
        activity.verifyCode(response.id, false);
    }

    void photoUploaded(BerbixPhotoIDStatusResponse response) {
        BerbixFlowActivity.dismissProgressDialog();

        if (response.next != null) {
            nextStep(response);
        } else {
            activity.updateCaptureFragmentState(response);
        }
    }

    void startIDCapture(BerbixPhotoIDStatusResponse response) {
        BerbixFlowActivity.dismissProgressDialog();

        activity.captureID(response, idParam);
    }

    void failed(String error) {
        BerbixFlowActivity.dismissProgressDialog();

        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
    }

    public void setActivity(BerbixFlowActivity activity) {
        this.activity = activity;

        if (sessionResponse != null) {
            nextStep(sessionResponse);
        }
    }
}
