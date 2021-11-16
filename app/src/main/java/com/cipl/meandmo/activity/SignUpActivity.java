package com.cipl.meandmo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.drawable.DrawableCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.ciyashop.library.apicall.PostApi;
import com.ciyashop.library.apicall.URLS;
import com.ciyashop.library.apicall.interfaces.OnResponseListner;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.cipl.meandmo.R;
import com.cipl.meandmo.customview.edittext.EditTextRegular;
import com.cipl.meandmo.customview.textview.TextViewBold;
import com.cipl.meandmo.customview.textview.TextViewLight;
import com.cipl.meandmo.customview.textview.TextViewRegular;
import com.cipl.meandmo.javaclasses.SyncWishList;
import com.cipl.meandmo.model.LogIn;
import com.cipl.meandmo.utils.BaseActivity;
import com.cipl.meandmo.utils.Config;
import com.cipl.meandmo.utils.Constant;
import com.cipl.meandmo.utils.CustomToast;
import com.cipl.meandmo.utils.RequestParamUtils;
import com.cipl.meandmo.utils.Utils;
import com.rilixtech.CountryCodePicker;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends BaseActivity implements OnResponseListner {


    private static final String TAG = SignUpActivity.class.getSimpleName();

    @BindView(R.id.etUsername)
    EditTextRegular etUsername;

    @BindView(R.id.etEmail)
    EditTextRegular etEmail;

    @BindView(R.id.etContact)
    EditTextRegular etContact;

    @BindView(R.id.etPass)
    EditTextRegular etPass;

    @BindView(R.id.etConfirmPass)
    EditTextRegular etConfirmPass;

    @BindView(R.id.ivLogo)
    ImageView ivLogo;

    @BindView(R.id.tvAlreadyAccount)
    TextViewLight tvAlreadyAccount;

    @BindView(R.id.tvSignInNow)
    TextViewBold tvSignInNow;

    @BindView(R.id.tvSignUp)
    TextViewBold tvSignUp;

    @BindView(R.id.ccp)
    CountryCodePicker ccp;

    @BindView(R.id.ivBlackBackButton)
    ImageView ivBlackBackButton;

    @BindView(R.id.spSelectRegtype)
    Spinner spSelectRegtype;

    @BindView(R.id.ll_gst)
    LinearLayout ll_gst;

    @BindView(R.id.etGstnumber)
    EditTextRegular etGstnumber;

    AlertDialog alertDialog;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private CustomToast toast;
    ArrayList<String> list = new ArrayList<>();
    private String signuptype = "";
    private int signuptypeid = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_ups);
        ButterKnife.bind(this);
        toast = new CustomToast(this);
        mAuth = FirebaseAuth.getInstance();
        setScreenLayoutDirection();
        setThemeColor();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.e(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.e(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }
                    Toast.makeText(SignUpActivity.this, getString(R.string.invalid_phone_number), Toast.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Toast.makeText(SignUpActivity.this, getString(R.string.quoto_exceeded), Toast.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

               /* // Show a message and update the UI
                // [START_EXCLUDE]
                updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]*/
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.e(TAG, "onCodeSent:" + verificationId);
                if (alertDialog != null) {
                    alertDialog.show();
                }
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
            }

        };
        setSpinner();
    }

    private void setSpinner() {
        list.clear();
        list.add("--- Select ---");
        list.add("Customer");
        list.add("Wholesaler");
        ArrayAdapter<String> SpinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_drop_down_items, list);
        spSelectRegtype.setAdapter(SpinnerAdapter);

        spSelectRegtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 2) {
                    ll_gst.setVisibility(View.VISIBLE);
                } else {
                    ll_gst.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();

                            if (alertDialog != null) {
                                alertDialog.dismiss();
                            }
                            registerUser();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                // [END_EXCLUDE]
                            }
                           /* // [START_EXCLUDE silent]
                            // Update UI
                            updateUI(STATE_SIGNIN_FAILED);
                            // [END_EXCLUDE]*/
                        }
                    }
                });
    }

    @OnClick(R.id.tvSignInNow)
    public void tvSignInNowClick() {
        Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.ivBlackBackButton)
    public void ivBackClick() {
        onBackPressed();
    }


    public void setThemeColor() {

        if (Constant.APPLOGO != null && !Constant.APPLOGO.equals("")) {
            Picasso.get().load(Constant.APPLOGO).error(R.drawable.logo).into(ivLogo);
        }
        Drawable mDrawable = getResources().getDrawable(R.drawable.login);
        mDrawable.setColorFilter(new
                PorterDuffColorFilter(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)), PorterDuff.Mode.OVERLAY));
        tvSignInNow.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        Drawable unwrappedDrawable = tvSignUp.getBackground();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, (Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR))));

        setTextViewDrawableColor(etUsername, (Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR))));
        setTextViewDrawableColor(etEmail, (Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR))));
        setTextViewDrawableColor(etConfirmPass, (Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR))));
        setTextViewDrawableColor(etPass, (Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR))));
        ivBlackBackButton.setColorFilter((Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR))));
    }

    @OnClick(R.id.tvSignUp)
    public void tvSignUpClick() {
        signuptype = String.valueOf(spSelectRegtype.getSelectedItem());
        signuptypeid = (int) spSelectRegtype.getSelectedItemId();

        Log.e(TAG, "tvSignUpClick: " + signuptype);
        Log.e(TAG, "tvSignUpClick: " + signuptypeid);

        if (etUsername.getText().toString().length() == 0) {
            Toast.makeText(this, R.string.enter_username, Toast.LENGTH_SHORT).show();
        } else if (etEmail.getText().toString().length() == 0) {
            Toast.makeText(this, R.string.enter_email_address, Toast.LENGTH_SHORT).show();
        } else if (!Utils.isValidEmail(etEmail.getText().toString())) {
            Toast.makeText(this, R.string.enter_valid_email_address, Toast.LENGTH_SHORT).show();
        } else if (etContact.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.enter_contact_number, Toast.LENGTH_SHORT).show();
        } else if (etPass.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.enter_password, Toast.LENGTH_SHORT).show();
        } else if (etConfirmPass.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.enter_confirm_password, Toast.LENGTH_SHORT).show();
        } else if (signuptypeid == 0) {
            Toast.makeText(this, R.string.select_reg_type, Toast.LENGTH_SHORT).show();
        } else if (signuptypeid == 2 && etGstnumber.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.enter_gst_number, Toast.LENGTH_SHORT).show();
        } else if (etPass.getText().toString().equals(etConfirmPass.getText().toString())) {
            if (Config.OTPVerification) {
                String number = ccp.getSelectedCountryCodeWithPlus() + etContact.getText().toString().trim();
                Log.e("Otp :-", number);
                ShowDialogForOTP(number);
            } else {
                registerUser();
            }
        } else {
            Toast.makeText(this, R.string.password_and_confirm_password_not_matched, Toast.LENGTH_SHORT).show();
        }

    }

    private void ShowDialogForOTP(final String number) {

        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SignUpActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_otp_verification, null);
        dialogBuilder.setView(dialogView);

        final EditTextRegular etOTP = (EditTextRegular) dialogView.findViewById(R.id.etOTP);
        TextViewRegular tvVerificationText = dialogView.findViewById(R.id.tvVerificationText);

        TextViewRegular tvDone = (TextViewRegular) dialogView.findViewById(R.id.tvDone);

        TextViewRegular tvResend = (TextViewRegular) dialogView.findViewById(R.id.tvResend);

        tvVerificationText.setText(getResources().getString(R.string.please_type_verification_code_sent_to_in) + etContact.getText().toString());
        alertDialog = dialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        tvDone.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvResend.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = etOTP.getText().toString();
                if (etOTP.getText().toString().length() == 0) {
                    toast.showToast(getString(R.string.enter_verificiation_code));
                    toast.showBlackbg();
//                    etOTP.setError("Enter Verification Code");
                    return;
                }
                verifyPhoneNumberWithCode(mVerificationId, code);
            }
        });

        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(SignUpActivity.this,
                        getString(R.string.otp_sent_again) + number, Toast.LENGTH_SHORT).show();

                resendVerificationCode(number, mResendToken);
            }
        });

    /*    rvProductVariation = (RecyclerView) dialogView.findViewById(R.id.rvProductVariation);
        TextViewRegular tvDone = (TextViewRegular) dialogView.findViewById(R.id.tvDone);
        TextViewRegular tvCancel = (TextViewRegular) dialogView.findViewById(R.id.tvCancel);*/

    }

    private void verifyPhoneNumberWithCode(String mVerificationId, String code) {

        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    private void resendVerificationCode(String number, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    public void registerUser() {
        if (Utils.isInternetConnected(this)) {
            showProgress("");
            PostApi postApi = new PostApi(this, "create_customer", this, getlanuage());
            JSONObject object = new JSONObject();
            try {

                object.put(RequestParamUtils.email, etEmail.getText().toString());
                object.put(RequestParamUtils.username, etUsername.getText().toString());
                object.put(RequestParamUtils.mobile, ccp.getSelectedCountryCodeWithPlus() + etContact.getText().toString().trim());
                object.put(RequestParamUtils.PASSWORD, etPass.getText().toString());
                object.put(RequestParamUtils.deviceType, Constant.DEVICE_TYPE);

                if (signuptype.equalsIgnoreCase("Customer")) {
                    object.put(RequestParamUtils.user_role, Constant.customer);
                    object.put(RequestParamUtils.Gst_number, "");
                } else if (signuptype.equalsIgnoreCase("Wholesaler")) {
                    object.put(RequestParamUtils.user_role, Constant.retailcustomer);
                    object.put(RequestParamUtils.Gst_number, etGstnumber.getText());
                }
                String token = getPreferences().getString(RequestParamUtils.NOTIFICATION_TOKEN, "");
                object.put(RequestParamUtils.deviceToken, token);

                Log.e(TAG, "registerUser: "+new Gson().toJson(object));
                postApi.callPostApi(new URLS().CREATE_CUSTOMER, object.toString());

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(this, R.string.something_went_wrong_try_after_somtime, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onResponse(final String response, String methodName) {

        if (methodName.equals(RequestParamUtils.createCustomer)) {
            if (response != null && response.length() > 0) {
                try {
                    final LogIn loginRider = new Gson().fromJson(
                            response, new TypeToken<LogIn>() {
                            }.getType());

                    JSONObject jsonObj = new JSONObject(response);
                    String status = jsonObj.getString("status");

                    if (status.equals("error")) {
                        Toast.makeText(getApplicationContext(), jsonObj.getString("message"), Toast.LENGTH_SHORT).show(); //display in long period of time
                        dismissProgress();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //set call here
                                if (loginRider.status.equals("success")) {

                                    SharedPreferences.Editor pre = getPreferences().edit();
                                    pre.putString(RequestParamUtils.CUSTOMER, "");
                                    pre.putString(RequestParamUtils.ID, loginRider.user.id + "");
                                    pre.putString(RequestParamUtils.PASSWORD, etPass.getText().toString());
                                    pre.putString(RequestParamUtils.user_role, loginRider.user.userRole.toString());
                                    pre.putString(RequestParamUtils.Gst_number, loginRider.user.gstNumber.toString());
                                    pre.commit();


                                    new SyncWishList(SignUpActivity.this).syncWishList(getPreferences().getString(RequestParamUtils.ID, ""), false);

//                                    Intent intent = new Intent(SignUpActivity.this, AccountActivity.class);
//                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.enter_proper_detail, Toast.LENGTH_SHORT).show(); //display in long period of time
                                }
                            }
                        });
                        dismissProgress();
                    }
                } catch (Exception e) {
                    Log.e(methodName + "Gson Exception is ", e.getMessage());
                    Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show(); //display in long period of time
                }
            }
        }
    }
}