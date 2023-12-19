package com.minbio.erp.auth.adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.minbio.erp.R;
import com.minbio.erp.auth.LoginActivity;
import com.minbio.erp.network.Api;
import com.minbio.erp.network.ResponseCallBack;
import com.minbio.erp.network.RetrofitClient;
import com.minbio.erp.utils.AppUtils;
import com.minbio.erp.utils.Constants;
import com.minbio.erp.utils.SharedPreference;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;


@SuppressLint("NewApi")
@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback implements ResponseCallBack {

    private static final String TAG = FingerprintHandler.class.getSimpleName();
    private Context context;
    private CancellationSignal cancellationSignal;

    private LoginActivity loginActivity;


    public FingerprintHandler(Context context, LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
        this.context = context;
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
        AppUtils.Companion.showToast((AppCompatActivity) context, errString.toString(), false);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        super.onAuthenticationHelp(helpCode, helpString);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        AppUtils.Companion.hideKeyboard((AppCompatActivity) context);
        if (SharedPreference.getBoolean(context, Constants.isFirstLogin)) {
            callLoginApi();
        } else
            AppUtils.Companion.showToast((AppCompatActivity) context, context.getResources().getString(R.string.loginAtLeastOne), false);

    }

    private void callLoginApi() {
        AppUtils.Companion.showDialog(context);
        Api api = RetrofitClient.INSTANCE.getClientNoToken(context).create(Api.class);
        Call<ResponseBody> call = api.login(
                SharedPreference.getSimpleString(context, Constants.fingerPrintUserSiren),
                SharedPreference.getSimpleString(context, Constants.fingerPrintUserEmail),
                SharedPreference.getSimpleString(context, Constants.fingerPrintUserPassword)
        );
        RetrofitClient.INSTANCE.apiCall(call, this, "Login");
    }

    public void StopListener() {
        try {
            if (cancellationSignal != null)
                cancellationSignal.cancel();
            cancellationSignal = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
    }

    public void completeFingerAuthentication(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            cancellationSignal = new CancellationSignal();
            fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
        } catch (SecurityException ex) {
            Log.d(TAG, "An error occurred:\n" + ex.getMessage());
        } catch (Exception ex) {
            Log.d(TAG, "An error occurred\n" + ex.getMessage());
        }
    }

    @Override
    public void onSuccess(@NotNull JSONObject jsonObject, @NotNull String tag) {
        if (tag.equals("Login")) {
            loginActivity.handleLoginResponse(jsonObject, true);
        }
    }

    @Override
    public void onError(@NotNull JSONObject jsonObject, @NotNull String tag) {
        try {
            AppUtils.Companion.dismissDialog();
            AppUtils.Companion.showToast((AppCompatActivity) context, jsonObject.getString("message"), false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onException(@Nullable String message, @NotNull String tag) {
        AppUtils.Companion.dismissDialog();
        AppUtils.Companion.showToast((AppCompatActivity) context, message, false);
    }

}
