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
import com.berbix.sdk.response.BerbixPhotoIdPayload;
import com.example.star.berbixdemo_android.R;
import com.kaopiz.kprogresshud.KProgressHUD;

public class BerbixDetailsFragment extends Fragment {

    public BerbixPhotoIdPayload param = null;

    private EditText givenNameField = null;
    private EditText middleNameField = null;
    private EditText familyNameField = null;
    private EditText birthdayField = null;
    private EditText expiryDateField = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.berbix_fragment_phone, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        givenNameField = view.findViewById(R.id.givenNameField);
        middleNameField = view.findViewById(R.id.middleNameField);
        familyNameField = view.findViewById(R.id.familyNameField);
        birthdayField = view.findViewById(R.id.birthdayField);
        expiryDateField = view.findViewById(R.id.expiryDateField);

        final Button submitButton = view.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitDetails();
            }
        });

        givenNameField.setText(param.givenName);
        familyNameField.setText(param.familyName);
        birthdayField.setText(param.birthday);
        expiryDateField.setText(param.expiryDate);

    }

    private void submitDetails() {
        if (givenNameField.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "Please enter your given name.", Toast.LENGTH_LONG).show();
            return;
        } else if (familyNameField.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "Please enter your family name.", Toast.LENGTH_LONG).show();
            return;
        } else if (birthdayField.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "Please enter your birthday.", Toast.LENGTH_LONG).show();
            return;
        } else if (expiryDateField.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "Please enter expiry date.", Toast.LENGTH_LONG).show();
            return;
        }

        BerbixAuthActivity.cProgressDialog = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Submitting...")
                .setCancellable(false)
                .setDimAmount(0.5f)
                .show();
        BerbixSDK.shared.api().submitDetail(givenNameField.getText().toString(),
                middleNameField.getText().toString(),
                familyNameField.getText().toString(),
                birthdayField.getText().toString(),
                expiryDateField.getText().toString());

    }
}
