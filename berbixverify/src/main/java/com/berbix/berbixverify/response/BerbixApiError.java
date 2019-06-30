package com.berbix.berbixverify.response;

import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BerbixApiError {
    @SerializedName("code")
    public long code;

    @SerializedName("readable")
    public String error;

    public static BerbixApiError parseError(Response<?> response, Retrofit retrofit) {
        Converter<ResponseBody, BerbixApiError> converter = retrofit.responseBodyConverter(BerbixApiError.class, new Annotation[0]);

        BerbixApiError error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            error = new BerbixApiError();
        }

        return error;
    }
}
