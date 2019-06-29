package com.berbix.sdk.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.berbix.sdk.BerbixSDK;
import com.berbix.sdk.BerbixStateManager;
import com.example.star.berbixdemo_android.R;
import com.kaopiz.kprogresshud.KProgressHUD;

public class BerbixChooseIdTypeFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.berbix_fragment_id_type, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BerbixFlowActivity.cProgressDialog = KProgressHUD.create(getActivity())
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel("Preparing...")
                        .setCancellable(false)
                        .setDimAmount(0.5f)
                        .show();

                if (v.getId() == R.id.cardButton) {
                    BerbixStateManager.getApiManager().startPhotoIDVerification("card");
                } else {
                    BerbixStateManager.getApiManager().startPhotoIDVerification("passport");
                }
            }
        };

        view.findViewById(R.id.cardButton).setOnClickListener(listener);
        view.findViewById(R.id.passportButton).setOnClickListener(listener);
    }
}
