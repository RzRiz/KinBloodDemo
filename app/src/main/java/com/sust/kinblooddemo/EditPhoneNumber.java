package com.sust.kinblooddemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.SetOptions;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EditPhoneNumber extends AppCompatActivity {

    private EditText phoneNumber, otp;
    private String verificationId;
    private StringBuilder newPhoneNumber_;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phone_number);

        phoneNumber = findViewById(R.id.et_edit_phone_number);
        otp = findViewById(R.id.et_edit_otp);
        Button updatePhoneNumber = findViewById(R.id.btn_update_phone_number);
        Button verifyOtp = findViewById(R.id.btn_verify_otp);
        progressBar = findViewById(R.id.progress_phone_number);
        progressBar.setVisibility(View.GONE);

        updatePhoneNumber.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            newPhoneNumber_ = new StringBuilder(phoneNumber.getText().toString().trim());
            if (phoneValidate()){
                sendVerification(newPhoneNumber_.toString());
            }
        });

        verifyOtp.setOnClickListener(v -> verifyCode(otp.getText().toString().trim()));
    }

    private boolean phoneValidate() {
        if (newPhoneNumber_.length() == 0){
            finish();
        }
        while (newPhoneNumber_.charAt(0) != '1') {
            newPhoneNumber_.deleteCharAt(0);
            if (newPhoneNumber_.length() == 0){
                progressBar.setVisibility(View.INVISIBLE);
                phoneNumber.setError("Please enter a valid phone number");
                phoneNumber.requestFocus();
                return false;
            }
        }
        int i = newPhoneNumber_.length();
        for (int j = 0; j < i; j++) {
            if(!(newPhoneNumber_.charAt(j) >= '0' && newPhoneNumber_.charAt(j) <= '9')){
                newPhoneNumber_.deleteCharAt(j);
                j--;
                i--;
            }
        }
        if (newPhoneNumber_.length() != 10){
            progressBar.setVisibility(View.INVISIBLE);
            phoneNumber.setError("Please enter a valid phone number");
            phoneNumber.requestFocus();
            return false;
        }
        return  true;
    }

    private void sendVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+880" + phoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            progressBar.setVisibility(View.INVISIBLE);
            otp.setError(e.getMessage());
            otp.requestFocus();
        }
    };
    private void verifyCode(String codeByUser) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, codeByUser);
        Home.FIREBASE_USER.reauthenticate(credential).addOnSuccessListener(aVoid -> Home.FIREBASE_USER.updatePhoneNumber(credential).addOnSuccessListener(aVoid1 -> {

            Map<String, Object> phoneNumber = new HashMap<>();
            phoneNumber.put("phoneNumber", newPhoneNumber_);

            Home.DOCUMENT_REFERENCE
                    .set(phoneNumber, SetOptions.merge())
                    .addOnSuccessListener(aVoid11 -> {
                        Toast.makeText(EditPhoneNumber.this, "Phone number update successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditPhoneNumber.this, Profile.class);
                        intent.putExtra("newPhoneNumber", newPhoneNumber_.toString());
                        setResult(4, intent);
                        finish();
                    }).addOnFailureListener(e -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(EditPhoneNumber.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(EditPhoneNumber.this, e.getMessage(), Toast.LENGTH_LONG).show();
        })).addOnFailureListener(e -> {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(EditPhoneNumber.this, e.getMessage(), Toast.LENGTH_LONG).show();
        });

    }
}
