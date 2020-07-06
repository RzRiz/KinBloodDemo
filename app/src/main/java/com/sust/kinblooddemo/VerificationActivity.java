package com.sust.kinblooddemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class VerificationActivity extends AppCompatActivity {

    private EditText editTextOtp;
    private String verificationId;
    private ArrayList<String> data;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        editTextOtp = findViewById(R.id.et_otp);
        progressDialog = new ProgressDialog(this);
        Button buttonVerify = findViewById(R.id.btn_verify);

        data = getIntent().getStringArrayListExtra("data");

        assert data != null;
        sendVerification(data.get(1));

        buttonVerify.setOnClickListener(v -> verifyCode(editTextOtp.getText().toString().trim()));

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
            editTextOtp.setError(e.getMessage());
        }
    };
    private void verifyCode(String codeByUser) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, codeByUser);
        signInWithCredentials(credential);
    }

    private void signInWithCredentials(PhoneAuthCredential credential) {
        progressDialog.setMessage("Signing In");
        progressDialog.show();
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final AuthCredential authCredential = EmailAuthProvider.getCredential(data.get(2), data.get(3));
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> Objects.requireNonNull(firebaseAuth.getCurrentUser()).linkWithCredential(authCredential)
                        .addOnSuccessListener(authResult1 -> {
                            SignupHelper signupHelper = new SignupHelper(data.get(0),data.get(1),data.get(2),data.get(3), "negative", 0, "app");
                            FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getCurrentUser().getUid()).set(signupHelper)
                                    .addOnSuccessListener(aVoid -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(VerificationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(VerificationActivity.this, Decision.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }).addOnFailureListener(e -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(VerificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }).addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(VerificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        })).addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(VerificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
    }
}
