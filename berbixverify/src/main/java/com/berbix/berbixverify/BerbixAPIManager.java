package com.berbix.berbixverify;

import android.util.Log;

import com.berbix.berbixverify.response.BerbixApiError;
import com.berbix.berbixverify.response.BerbixPhotoIDStatusResponse;
import com.berbix.berbixverify.response.BerbixResponse;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

public class BerbixAPIManager {

    private final BerbixAPIAdapter adapter;
    private final String baseURL;
    private final BerbixConfiguration config;
    private final BerbixEnvironment environment;

    private String accessToken = null;

    BerbixAPIManager(BerbixAPIAdapter adapter, BerbixConfiguration config, BerbixEnvironment environment, String baseURL) {
        this.adapter = adapter;
        this.config = config;
        this.environment = environment;
        this.baseURL = baseURL;
    }

    private String getBaseURL() {
        if (this.baseURL != null) {
            return this.baseURL;
        }
        if (environment == BerbixEnvironment.SANDBOX) {
            return "https://api.sandbox.berbix.com/v0/";
        } else if (environment == BerbixEnvironment.STAGING) {
            return "https://api.staging.berbix.com/v0/";
        } else {
            return "https://api.berbix.com/v0/";
        }
    }

    private Retrofit client() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100,TimeUnit.SECONDS).build();

        return new Retrofit.Builder()
                .baseUrl(getBaseURL())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private BerbixAPIEndpoints apiEndpoints() {
        return client().create(BerbixAPIEndpoints.class);
    }

    public void createSession(final ResponseConsumer consumer) {
        apiEndpoints().createSession(config).enqueue(new Callback<BerbixResponse>() {
            @Override
            public void onResponse(Call<BerbixResponse> call, Response<BerbixResponse> response) {
                if (response.isSuccessful()) {
                    BerbixResponse resp = response.body();
                    if (resp != null && resp.isSuccessful()) {
                        BerbixStateManager.getApiManager().accessToken = "Bearer " + resp.token;

                        if (resp.next != null) {
                            adapter.nextStep(resp);
                        }

                        if (consumer != null) {
                            consumer.consume(response.body());
                        }
                    } else {
                        adapter.failed(resp.error);
                    }
                } else {
                    adapter.failed("Something went wrong.");
                }
            }

            @Override
            public void onFailure(Call<BerbixResponse> call, Throwable t) {
                Log.e("call failure", t.getLocalizedMessage());
                adapter.failed("Could not connect to server.");
            }
        });
    }

    // Phone Verification
    public void verifyPhone(String phoneNumber) {
        apiEndpoints().startPhoneVerification(accessToken, phoneNumber).enqueue(new Callback<BerbixResponse>() {
            @Override
            public void onResponse(Call<BerbixResponse> call, Response<BerbixResponse> response) {
                if (response.isSuccessful()) {
                    BerbixResponse resp = response.body();
                    if (resp != null && resp.isSuccessful()) {
                        adapter.phoneSubmitted(resp);
                    } else {
                        adapter.failed(resp.error);
                    }
                } else {
                    adapter.failed("Something went wrong.");
                }
            }

            @Override
            public void onFailure(Call<BerbixResponse> call, Throwable t) {
                adapter.failed("Could not connect to server.");
            }
        });
    }

    public void verifyPhoneCode(long parentId, String code) {
        apiEndpoints().verifyPhoneCode(accessToken, parentId, code).enqueue(new Callback<BerbixResponse>() {
            @Override
            public void onResponse(Call<BerbixResponse> call, Response<BerbixResponse> response) {
                if (response.isSuccessful()) {
                    BerbixResponse resp = response.body();
                    if (resp != null && resp.isSuccessful()) {
                        if (resp.next != null) {
                            adapter.nextStep(resp);
                        } else {

                        }
                    } else {
                        adapter.failed(resp.error);
                    }
                } else {
                    adapter.failed("Something went wrong.");
                }
            }

            @Override
            public void onFailure(Call<BerbixResponse> call, Throwable t) {
                adapter.failed("Could not connect to server.");
            }
        });
    }

    // Email Verification
    public void verifyEmail(String email) {
        apiEndpoints().startEmailVerification(accessToken, email).enqueue(new Callback<BerbixResponse>() {
            @Override
            public void onResponse(Call<BerbixResponse> call, Response<BerbixResponse> response) {
                if (response.isSuccessful()) {
                    BerbixResponse resp = response.body();
                    if (resp != null && resp.isSuccessful()) {
                        adapter.emailSubmitted(resp);
                    } else {
                        adapter.failed(resp.error);
                    }
                } else {
                    adapter.failed("Something went wrong.");
                }
            }

            @Override
            public void onFailure(Call<BerbixResponse> call, Throwable t) {
                adapter.failed("Could not connect to server.");
            }
        });
    }

    public void verifyEmailCode(long parentId, String code) {
        apiEndpoints().verifyEmailCode(accessToken, parentId, code).enqueue(new Callback<BerbixResponse>() {
            @Override
            public void onResponse(Call<BerbixResponse> call, Response<BerbixResponse> response) {
                if (response.isSuccessful()) {
                    BerbixResponse resp = response.body();
                    if (resp != null && resp.isSuccessful()) {
                        if (resp.next != null) {
                            adapter.nextStep(resp);
                        } else {

                        }
                    } else {
                        adapter.failed(resp.error);
                    }
                } else {
                    adapter.failed("Something went wrong.");
                }
            }

            @Override
            public void onFailure(Call<BerbixResponse> call, Throwable t) {
                adapter.failed("Could not connect to server.");
            }
        });
    }

    public void submitPhotoIDScan(String payload) {
        apiEndpoints().submitPhotoIDScan(accessToken, payload).enqueue(new Callback<BerbixResponse>() {
            @Override
            public void onResponse(Call<BerbixResponse> call, Response<BerbixResponse> response) {
                if (response.isSuccessful()) {
                    BerbixResponse resp = response.body();
                    if (resp != null && resp.isSuccessful()) {
                        if (resp.next != null) {
                            adapter.nextStep(resp);
                        } else {

                        }
                    } else {
                        adapter.failed(resp.error);
                    }
                } else {
                    adapter.failed("Something went wrong.");
                }
            }

            @Override
            public void onFailure(Call<BerbixResponse> call, Throwable t) {
                adapter.failed("Could not connect to server.");
            }
        });
    }

    // Photo ID
    public void startPhotoIDVerification(String idType) {
        BerbixIDType type = null;
        if (idType != null) {
            type = new BerbixIDType();
            type.idType = idType;
        }

        apiEndpoints().startIDVerification(accessToken, type).enqueue(new Callback<BerbixPhotoIDStatusResponse>() {
            @Override
            public void onResponse(Call<BerbixPhotoIDStatusResponse> call, Response<BerbixPhotoIDStatusResponse> response) {
                if (response.isSuccessful()) {
                    BerbixPhotoIDStatusResponse resp = response.body();
                    if (resp != null && resp.isSuccessful()) {
                        adapter.startIDCapture(resp);
                    } else {
                        adapter.failed(resp.error);
                    }
                } else {
                    adapter.failed("Something went wrong.");
                }
            }

            @Override
            public void onFailure(Call<BerbixPhotoIDStatusResponse> call, Throwable t) {
                adapter.failed("Could not connect to server.");
            }
        });
    }

    public Call<BerbixPhotoIDStatusResponse> startUploadCall(
            long parentId,
            MultipartBody.Part filePart,
            MultipartBody.Part scaledPart,
            MultipartBody.Part barcodePart,
            MultipartBody.Part sidePart,
            MultipartBody.Part exifPart) {

        if (parentId > 0) {
            return apiEndpoints().uploadID(accessToken, parentId, filePart, scaledPart, barcodePart, sidePart, exifPart);
        } else {
            return apiEndpoints().startIDUpload(accessToken, filePart, scaledPart, barcodePart, sidePart, exifPart);
        }
    }

    public void uploadPhotoId(long parentId, String side,
                              File file, File scaled, File barcode) {

        MultipartBody.Part filePart = null;
        MultipartBody.Part scaledPart = null;
        MultipartBody.Part barcodePart = null;

        MultipartBody.Part sidePart = MultipartBody.Part.createFormData("side", side);
        MultipartBody.Part exif = MultipartBody.Part.createFormData("exif", "{}");

        if (file != null) {
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            filePart = MultipartBody.Part.createFormData("file", "file.jpg", reqFile);
        }

        if (scaled != null) {
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), scaled);
            scaledPart = MultipartBody.Part.createFormData("scaled", "scaled.jpg", reqFile);
        }

        if (barcode != null) {
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), barcode);
            barcodePart = MultipartBody.Part.createFormData("barcode", "barcode.png", reqFile);
        }

        startUploadCall(parentId, filePart, scaledPart, barcodePart, sidePart, exif).enqueue(new Callback<BerbixPhotoIDStatusResponse>() {
            @Override
            public void onResponse(Call<BerbixPhotoIDStatusResponse> call, Response<BerbixPhotoIDStatusResponse> response) {
                if (response.isSuccessful()) {
                    BerbixPhotoIDStatusResponse resp = response.body();
                    if (resp != null && resp.isSuccessful()) {
                        adapter.photoUploaded(resp);
                    } else {
                        adapter.failed(resp.error);
                    }
                } else {
                    BerbixApiError error = BerbixApiError.parseError(response, client());
                    adapter.failed(error.error);
                }
            }

            @Override
            public void onFailure(Call<BerbixPhotoIDStatusResponse> call, Throwable t) {
                adapter.failed("Could not connect to server.");
            }
        });
    }

    public void submitDetail( String givenName, String middleName, String familyName, String birthday, String expiryDate) {
        apiEndpoints().submitDetail(accessToken, givenName, middleName, familyName, birthday, expiryDate).enqueue(new Callback<BerbixResponse>() {
            @Override
            public void onResponse(Call<BerbixResponse> call, Response<BerbixResponse> response) {
                if (response.isSuccessful()) {
                    BerbixResponse resp = response.body();
                    if (resp != null && resp.isSuccessful()) {
                        adapter.nextStep(resp);
                    } else {
                        adapter.failed(resp.error);
                    }
                } else {
                    BerbixApiError error = BerbixApiError.parseError(response, client());
                    adapter.failed(error.error);
                }
            }

            @Override
            public void onFailure(Call<BerbixResponse> call, Throwable t) {
                adapter.failed("Could not connect to server.");
            }
        });
    }

    static abstract class BerbixAPIAdapter {
        abstract void nextStep(BerbixResponse response);
        abstract void phoneSubmitted(BerbixResponse response);
        abstract void emailSubmitted(BerbixResponse response);
        abstract void photoUploaded(BerbixPhotoIDStatusResponse response);
        abstract void startIDCapture(BerbixPhotoIDStatusResponse response);
        abstract void failed(String error);
    }

    interface ResponseConsumer {
        void consume(BerbixResponse response);
    }

    interface BerbixAPIEndpoints {
        // Request method and URL specified in the annotation

        @Headers("Content-Type: application/json")
        @POST("session")
        Call<BerbixResponse> createSession(@Body BerbixConfiguration config);

        @FormUrlEncoded
        @POST("phone-verification")
        Call<BerbixResponse> startPhoneVerification(@Header("Authorization") String accessToken, @Field("number") String phoneNumber);

        @FormUrlEncoded
        @POST("phone-verification/{parent-id}")
        Call<BerbixResponse> verifyPhoneCode(@Header("Authorization") String accessToken, @Path("parent-id") long parentId, @Field("code") String code);

        @FormUrlEncoded
        @POST("email-verification")
        Call<BerbixResponse> startEmailVerification(@Header("Authorization") String accessToken, @Field("email") String email);

        @FormUrlEncoded
        @POST("email-verification/{parent-id}")
        Call<BerbixResponse> verifyEmailCode(@Header("Authorization") String accessToken, @Path("parent-id") long parentId, @Field("code") String code);

        @FormUrlEncoded
        @POST("photo-id-scan-verification")
        Call<BerbixResponse> submitPhotoIDScan(@Header("Authorization") String accessToken, @Field("payload") String payload);

        @Headers("Content-Type: application/json")
        @POST("photo-id-verification")
        Call<BerbixPhotoIDStatusResponse> startIDVerification(@Header("Authorization") String accessToken, @Body BerbixIDType idType);

        @Multipart
        @POST("photo-id-verification")
        Call<BerbixPhotoIDStatusResponse> startIDUpload(@Header("Authorization") String accessToken,
                                                        @Part MultipartBody.Part file,
                                                        @Part MultipartBody.Part scaled,
                                                        @Part MultipartBody.Part barcode,
                                                        @Part MultipartBody.Part side,
                                                        @Part MultipartBody.Part exif);

        @Multipart
        @POST("photo-id-verification/{parent-id}")
        Call<BerbixPhotoIDStatusResponse> uploadID(@Header("Authorization") String accessToken,
                                                   @Path("parent-id") long parentId,
                                                   @Part MultipartBody.Part file,
                                                   @Part MultipartBody.Part scaled,
                                                   @Part MultipartBody.Part barcode,
                                                   @Part MultipartBody.Part side,
                                                   @Part MultipartBody.Part exif);

        @FormUrlEncoded
        @POST("details-verification")
        Call<BerbixResponse> submitDetail(@Header("Authorization") String accessToken,
                                          @Field("given_name") String givenName,
                                          @Field("middle_name") String middleName,
                                          @Field("family_name") String familyName,
                                          @Field("date_of_birth") String birthday,
                                          @Field("expiry_date") String expiryDate);
    }
}
