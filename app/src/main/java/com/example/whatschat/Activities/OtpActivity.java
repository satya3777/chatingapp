package com.example.whatschat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import  com.example.whatschat.databinding.ActivityOtpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OnOtpCompletionListener;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {
    //binding is used to redirect or take data from first page
    ActivityOtpBinding binding;
    // used for authantication purpose
    FirebaseAuth auth;
    String verificationId;

    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //instance
        auth = FirebaseAuth.getInstance();
        //receiving ph no. from usr input
        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        //to show progress
        dialog = new ProgressDialog(this);
        dialog.setMessage("OTP Sending...");
        dialog.setCancelable(false);
        dialog.show();

        getSupportActionBar().hide();
        binding.phonelbl.setText("Verify" + phoneNumber);
        // giving options and rules to verification
        PhoneAuthOptions options= PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(OtpActivity.this)
                //verification pass or fail
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(@NonNull String verifyId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verifyId, forceResendingToken);
                        dialog.dismiss();
                        verificationId = verifyId;
                        InputMethodManager im=(InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        im.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                        binding.otpView.requestFocus();
                    }
                }).build();
               //to verify now
            //this comment will read options and and verify for us

            PhoneAuthProvider.verifyPhoneNumber(options);
        //to check weather the otp is right or wrong
        binding.otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                //defined user //automatic checkup
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,otp);

                auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    //to give toast
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent intent= new Intent(OtpActivity.this,setProfile.class);
                            startActivity(intent);
                            finishAffinity();
                        }
                        else{
                            Toast.makeText(OtpActivity.this, "failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }
}