package com.asmodeusstudio.parade.login;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.asmodeusstudio.parade.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;

/**
 * Parade for FretX
 * Created by pandor on 26/08/17 17:07.
 */

public class RegisterFragment extends Fragment {
    private static final String TAG = "KJKP6_LOGIN_REGISTER";
    private static final String SiteKey = "6LcpzjQUAAAAAD0dkI0uQ54c75TtJ_SVXaCGNaDT";
    private static final String SecretKey  = "6LcpzjQUAAAAACh_yXlvmcmOwMN1jdYD3KpvFO0c";
    private GoogleApiClient mGoogleApiClient;
    private static final int PASSWORD_MIN_LENGTH = 6;
    private LoginInterface loginInterface;
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button registerButton;
    private View root;
    private String email;
    private String username;
    private String password;


    public static RegisterFragment newInstance(LoginInterface loginInterface) {
        RegisterFragment fragment = new RegisterFragment();
        fragment.loginInterface = loginInterface;
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.login_register_fragment, null);
        usernameEditText = (EditText) root.findViewById(R.id.username_edittext);
        emailEditText = (EditText) root.findViewById(R.id.email_edittext);
        passwordEditText = (EditText) root.findViewById(R.id.password_edittext);
        registerButton = (Button) root.findViewById(R.id.register_button);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(SafetyNet.API)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getActivity(), "Connection failed!", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
        mGoogleApiClient.connect();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(getActivity());
                root.requestFocus();

                email = emailEditText.getText().toString();
                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();

                if (!checkInput(email, username, password))
                    return;

                SafetyNet.SafetyNetApi.verifyWithRecaptcha(mGoogleApiClient, SiteKey)
                        .setResultCallback(new ResultCallback<SafetyNetApi.RecaptchaTokenResult>() {
                            @Override
                            public void onResult(SafetyNetApi.RecaptchaTokenResult result) {
                                Status status = result.getStatus();

                                if ((status != null) && status.isSuccess()) {
                                    Log.e(TAG, "Captcha success!");
                                    loginInterface.registerWithEmailAndPassword(email, password);
                                } else {
                                    Log.e(TAG, "Captcha error happened! " + status.getStatusMessage());
                                }
                            }
                        });
            }
        });
    }

    boolean checkInput(String email, String username, String password) {
        if (email.length() == 0)
            return false;
        else if (username.length() == 0)
            return false;
        else if (password.length() < PASSWORD_MIN_LENGTH)
            return false;
        return true;
    }

    //TODO move to keyboard utils
    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
