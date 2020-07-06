package com.sust.kinblooddemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EditPersonalInfo extends AppCompatActivity implements View.OnClickListener, DialoguePassword.DialoguePasswordListener {

    private EditText name, phoneNumber, otp, email;
    private String verificationId_, oldEmail_, newEmail_, password_;
    private StringBuilder newPhoneNumber_;
    private TextInputLayout oldPassword, newPassword, confirmNewPassword;
    private ArrayList<String> ara = new ArrayList<>();
    private Button verifyOtp;
    private int trigger = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_personal_info);


        for (int i = 0; i < 4; i++) {
            ara.add("");
        }

        name = findViewById(R.id.et_change_name);
        phoneNumber = findViewById(R.id.et_edit_phone_number);
        otp = findViewById(R.id.et_edit_otp);
        email = findViewById(R.id.et_edit_emailEP);
        oldPassword = findViewById(R.id.et_edit_old_password);
        newPassword = findViewById(R.id.et_edit_new_password);
        confirmNewPassword = findViewById(R.id.et_edit_confirm_new_password);
        Button updateName = findViewById(R.id.btn_change_name);
        Button updatePhoneNumber = findViewById(R.id.btn_update_phone_number);
        verifyOtp = findViewById(R.id.btn_verify_otp);
        Button updateEmail = findViewById(R.id.btn_update_email);
        Button updatePassword = findViewById(R.id.btn_update_password);

        oldEmail_ = getIntent().getStringExtra("oldEmail");

        name.addTextChangedListener(textWatcher);
        phoneNumber.addTextChangedListener(textWatcher);
        otp.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);
        oldPassword.getEditText().addTextChangedListener(textWatcher);
        newPassword.getEditText().addTextChangedListener(textWatcher);
        confirmNewPassword.getEditText().addTextChangedListener(textWatcher);

        updateName.setOnClickListener(this);
        updatePhoneNumber.setOnClickListener(this);
        verifyOtp.setOnClickListener(this);
        updateEmail.setOnClickListener(this);
        updatePassword.setOnClickListener(this);

        verifyOtp.setEnabled(false);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_change_name) {
            String name_ = name.getText().toString().trim();
            if (name_.isEmpty()) {
                name.setError("Field cannot be empty");
                name.requestFocus();
            } else {
                Map<String, Object> name = new HashMap<>();
                name.put("fullName", name_);
                HomeActivity.DOCUMENT_REFERENCE
                        .update(name).addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditPersonalInfo.this, "Name update successful", Toast.LENGTH_SHORT).show();
                    ara.add(0, name_);
                }).addOnFailureListener(e -> {
                    Toast.makeText(EditPersonalInfo.this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        } else if (v.getId() == R.id.btn_update_phone_number) {
            trigger = 1;
            newPhoneNumber_ = new StringBuilder(phoneNumber.getText().toString().trim());
            if (phoneValidate()) {
                openDialog();
            }
        } else if (v.getId() == R.id.btn_verify_otp) {
            String code_ = otp.getText().toString().trim();
            if (code_.isEmpty() || code_.length() < 6) {
                otp.setError("Please enter a valid OTP");
                otp.requestFocus();
            } else {
                verifyCode(code_);
            }
        } else if (v.getId() == R.id.btn_update_email) {
            trigger = 2;
            newEmail_ = email.getText().toString().trim();
            if (!newEmail_.isEmpty()) {
                if (validate()) {
                    openDialog();
                }
            } else {
                email.setError("Field cannot be empty");
                email.requestFocus();
            }
        } else if (v.getId() == R.id.btn_update_password) {
            String oldPassword_ = oldPassword.getEditText().getText().toString().trim();
            String newPassword_ = newPassword.getEditText().getText().toString().trim();
            String confirmNewPassword_ = confirmNewPassword.getEditText().getText().toString().trim();

            if (!oldPassword_.isEmpty() && !newPassword_.isEmpty() && !confirmNewPassword_.isEmpty()) {
                if (newPassword_.length() < 6) {
                    newPassword.setError("Password must contain at least 6 characters");
                } else {
                    if (newPassword_.equals(confirmNewPassword_)) {
                        EmailAuthCredential credential = (EmailAuthCredential) EmailAuthProvider.getCredential(oldEmail_, oldPassword_);
                        HomeActivity.FIREBASE_USER.reauthenticate(credential).addOnSuccessListener(authResult -> HomeActivity.FIREBASE_USER
                                .updatePassword(newPassword_)
                                .addOnSuccessListener(aVoid -> {
                                    Map<String, Object> password = new HashMap<>();
                                    password.put("password", newPassword_);
                                    HomeActivity.DOCUMENT_REFERENCE
                                            .update(password)
                                            .addOnSuccessListener(aVoid1 -> {
                                                Toast.makeText(EditPersonalInfo.this, "Password update successful", Toast.LENGTH_LONG).show();
                                                ara.add(3, newPassword_);
                                            }).addOnFailureListener(e -> {
                                        Toast.makeText(EditPersonalInfo.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(EditPersonalInfo.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                })).addOnFailureListener(e -> {
                            Toast.makeText(EditPersonalInfo.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        newPassword.setError(" ");
                        confirmNewPassword.setError("Passwords do not match");
                    }
                }
            } else {
                Toast.makeText(EditPersonalInfo.this, "All the fields should be filled in order to update password", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openDialog() {
        DialoguePassword dialoguePassword = new DialoguePassword();
        dialoguePassword.show(getSupportFragmentManager(), "dialoguePassword");
    }

    private boolean phoneValidate() {
        if (newPhoneNumber_.length() == 0) {
            phoneNumber.setError("Field cannot be empty");
            phoneNumber.requestFocus();
        }
        while (newPhoneNumber_.charAt(0) != '1') {
            newPhoneNumber_.deleteCharAt(0);
            if (newPhoneNumber_.length() == 0) {
                phoneNumber.setError("Please enter a valid phone number");
                phoneNumber.requestFocus();
                return false;
            }
        }
        int i = newPhoneNumber_.length();
        for (int j = 0; j < i; j++) {
            if (!(newPhoneNumber_.charAt(j) >= '0' && newPhoneNumber_.charAt(j) <= '9')) {
                newPhoneNumber_.deleteCharAt(j);
                j--;
                i--;
            }
        }
        if (newPhoneNumber_.length() != 10) {
            phoneNumber.setError("Please enter a valid phone number");
            phoneNumber.requestFocus();
            return false;
        }
        return true;
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
            verificationId_ = s;
            verifyOtp.setEnabled(true);
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
            otp.setError(e.getMessage());
            otp.requestFocus();
        }
    };

    private void verifyCode(String codeByUser) {
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId_, codeByUser);
        EmailAuthCredential emailAuthCredential = (EmailAuthCredential) EmailAuthProvider.getCredential(oldEmail_, password_);
        HomeActivity.FIREBASE_USER.reauthenticate(emailAuthCredential).addOnSuccessListener(aVoid -> HomeActivity.FIREBASE_USER.updatePhoneNumber(phoneAuthCredential).addOnSuccessListener(aVoid1 -> {

            Map<String, Object> phoneNumber = new HashMap<>();
            phoneNumber.put("phoneNumber", newPhoneNumber_.toString());

            HomeActivity.DOCUMENT_REFERENCE
                    .update(phoneNumber)
                    .addOnSuccessListener(aVoid11 -> {
                        Toast.makeText(EditPersonalInfo.this, "Phone number update successful", Toast.LENGTH_SHORT).show();
                        ara.add(1, newPhoneNumber_.toString());
                    }).addOnFailureListener(e -> Toast.makeText(EditPersonalInfo.this, e.getMessage(), Toast.LENGTH_LONG).show());
        }).addOnFailureListener(e -> Toast.makeText(EditPersonalInfo.this, e.getMessage(), Toast.LENGTH_LONG).show()))
                .addOnFailureListener(e -> Toast.makeText(EditPersonalInfo.this, e.getMessage(), Toast.LENGTH_LONG).show());
    }

    public boolean validate() {
        if (!Patterns.EMAIL_ADDRESS.matcher(newEmail_).matches()) {
            email.setError("Please enter a valid email address");
            email.requestFocus();
            return false;
        }
        if (oldEmail_.equals(newEmail_)) {
            Toast.makeText(this, "Old and new emails are the same", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            name.setError(null);
            phoneNumber.setError(null);
            otp.setError(null);
            email.setError(null);
            oldPassword.setError(null);
            newPassword.setError(null);
            confirmNewPassword.setError(null);
        }
    };

    @Override
    public void applyText(String password) {
        Log.d("applytext", "triggered");

        if (password.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "Please enter a valid password", Toast.LENGTH_SHORT).show();
        } else {
            password_ = password;
            if (trigger == 1) {
                sendVerification(newPhoneNumber_.toString());
            } else if (trigger == 2) {
                startEmailUpdatingProcess();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("ara", ara);
        setResult(1, intent);
        finish();
    }

    public void startEmailUpdatingProcess() {
        EmailAuthCredential credential = (EmailAuthCredential) EmailAuthProvider.getCredential(oldEmail_, password_);
        HomeActivity.FIREBASE_USER.reauthenticate(credential).addOnSuccessListener(aVoid -> HomeActivity.FIREBASE_USER.updateEmail(newEmail_).addOnSuccessListener(aVoid1 -> {
            Map<String, Object> email = new HashMap<>();
            email.put("email", newEmail_);
            HomeActivity.DOCUMENT_REFERENCE
                    .update(email).addOnSuccessListener(aVoid11 -> {
                Toast.makeText(EditPersonalInfo.this, "Email update successful", Toast.LENGTH_LONG).show();
                ara.add(2, newEmail_);
            }).addOnFailureListener(e -> Toast.makeText(EditPersonalInfo.this, e.getMessage(), Toast.LENGTH_LONG).show());
            }).addOnFailureListener(e -> Toast.makeText(EditPersonalInfo.this, e.getMessage(), Toast.LENGTH_LONG).show())
            ).addOnFailureListener(e -> Toast.makeText(EditPersonalInfo.this, e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
