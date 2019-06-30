package com.berbix.berbixverify.activities;

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

import com.berbix.berbixverify.BerbixStateManager;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.berbix.berbixverify.R;

public class BerbixEmailInputFragment extends Fragment {
    private EditText emailField = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.berbix_fragment_email, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailField = view.findViewById(R.id.emailField);
        final Button verifyButton = view.findViewById(R.id.verifyButton);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyEmail();
            }
        });
    }

    private void verifyEmail() {
        if (emailField.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "Please enter your email address.", Toast.LENGTH_LONG).show();
            return;
        }

        BerbixFlowActivity.cProgressDialog = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Sending Code...")
                .setCancellable(false)
                .setDimAmount(0.5f)
                .show();

        BerbixStateManager.getApiManager().verifyEmail(emailField.getText().toString());
    }
}
