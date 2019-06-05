package com.berbix.sdk.response;

import com.google.gson.annotations.SerializedName;

public class BerbixPhotoIDStatusResponse extends BerbixResponse {

    @SerializedName("front_status")
    public int frontStatus = 0;

    @SerializedName("back_status")
    public int backStatus = 0;

    @SerializedName("selfie_status")
    public int selfieStatus = 0;

    @SerializedName("liveness_status")
    public int livenessStatus = 0;

    @SerializedName("id_collection_type")
    public String idType;

    @SerializedName("back_format")
    public String backFormat;

    @SerializedName("liveness_challenge")
    public String livenessChallenge;

    @SerializedName("has_review_label")
    public Boolean hasReviewLabel;

    @SerializedName("selfie_consent")
    public String selfieConsent;

}