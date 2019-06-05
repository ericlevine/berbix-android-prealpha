package com.berbix.sdk.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.berbix.sdk.BerbixSDK;
import com.example.star.berbixdemo_android.R;
import com.kaopiz.kprogresshud.KProgressHUD;

public class BerbixVerifyCodeFragment extends Fragment {

    public long parentId = 0;

    public boolean isPhoneVerification = true;

    private EditText verifyCodeField = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.berbix_fragment_code, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        verifyCodeField = view.findViewById(R.id.verifyCodeField);
        final Button verifyButton = view.findViewById(R.id.verifyButton);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCode();
            }
        });
    }

    private void verifyCode() {
        if (verifyCodeField.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "Please enter the verification code you received.", Toast.LENGTH_LONG).show();
            return;
        }

        BerbixAuthActivity.cProgressDialog = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Verifying Code...")
                .setCancellable(false)
                .setDimAmount(0.5f)
                .show();

        if (isPhoneVerification) {
            BerbixSDK.shared.api().verifyPhoneCode(parentId, verifyCodeField.getText().toString());
        } else {
            BerbixSDK.shared.api().verifyEmailCode(parentId, verifyCodeField.getText().toString());
        }
    }
}
