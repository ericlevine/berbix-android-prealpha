package com.berbix.sdk.response;

import com.google.gson.annotations.SerializedName;

public class BerbixPhotoIdPayload {
    // Details
    @SerializedName("given_name")
    public String givenName;

    @SerializedName("middle_name")
    public String middleName;

    @SerializedName("family_name")
    public String familyName;

    @SerializedName("date_of_birth")
    public String birthday;

    @SerializedName("expiry_date")
    public String expiryDate;

    //  Photo Id
    @SerializedName("id_types")
    public String idTypes;

    @SerializedName("selfie_match")
    public Boolean selfieMatch;

    @SerializedName("barcode_timeout")
    public int barcodeTimeout = 0;

    @SerializedName("has_phone")
    public Boolean hasPhone;

    @SerializedName("seeded_phone")
    public String seededPhone;

    @SerializedName("liveness_check")
    public Boolean livenessCheck;
}