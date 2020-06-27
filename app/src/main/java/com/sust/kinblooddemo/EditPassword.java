package com.sust.kinblooddemo;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class EditPassword extends AppCompatActivity {

    private TextInputLayout oldPasswordEP, newPassword,confirmNewPassword;
    private String oldPasswordP_, oldPasswordEP_, newPassword_,confirmNewPassword_, email_;
    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        oldPasswordEP = findViewById(R.id.et_edit_old_password);
        newPassword = findViewById(R.id.et_edit_new_password);
        confirmNewPassword = findViewById(R.id.et_edit_confirm_new_password);
        Button updatePassword = findViewById(R.id.btn_update_password);
        progressBar = findViewById(R.id.progress_password);
        progressBar.setVisibility(View.GONE);

        oldPasswordP_ = getIntent().getStringExtra("oldPassword");
        email_ = getIntent().getStringExtra("email");


        oldPasswordEP.getEditText().addTextChangedListener(textWatcher);
        newPassword.getEditText().addTextChangedListener(textWatcher);
        confirmNewPassword.getEditText().addTextChangedListener(textWatcher);

        updatePassword.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            oldPasswordEP_ = oldPasswordEP.getEditText().getText().toString().trim();
            newPassword_ = newPassword.getEditText().getText().toString().trim();
            confirmNewPassword_ = confirmNewPassword.getEditText().getText().toString().trim();

            if (!oldPasswordEP_.isEmpty() || !newPassword_.isEmpty() || !confirmNewPassword_.isEmpty()){
                if (oldPasswordEP_.isEmpty() || newPassword_.isEmpty() || confirmNewPassword_.isEmpty()){
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(EditPassword.this, "All the fields should be filled in order to update password", Toast.LENGTH_LONG).show();
                }
                else {
                    if (oldPasswordEP_.equals(oldPasswordP_)){
                        if (newPassword_.length() < 6){
                            progressBar.setVisibility(View.INVISIBLE);
                            newPassword.setError("Password must contain at least 6 characters");
                        }
                        else{
                            if (newPassword_.equals(confirmNewPassword_)){
                                EmailAuthCredential credential = (EmailAuthCredential) EmailAuthProvider.getCredential(email_, oldPasswordP_);
                                Home.FIREBASE_USER.reauthenticate(credential).addOnSuccessListener(authResult -> Home.FIREBASE_USER
                                        .updatePassword(newPassword_)
                                        .addOnSuccessListener(aVoid -> {
                                            Map<String, Object> password = new HashMap<>();
                                            password.put("password", newPassword_);

                                            Home.DOCUMENT_REFERENCE
                                                    .set(password, SetOptions.merge())
                                                    .addOnSuccessListener(aVoid1 -> {
                                                        Toast.makeText(EditPassword.this, "Password update successful", Toast.LENGTH_LONG).show();
                                                        Intent intent = new Intent(EditPassword.this, Profile.class);
                                                        intent.putExtra("newPassword", newPassword_);
                                                        setResult(2, intent);
                                                        finish();
                                                    }).addOnFailureListener(e -> {
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(EditPassword.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                                    });
                                        }).addOnFailureListener(e -> {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(EditPassword.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        })).addOnFailureListener(e -> {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(EditPassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                            else{
                                progressBar.setVisibility(View.INVISIBLE);
                                newPassword.setError(" ");
                                confirmNewPassword.setError("Passwords do not match");
                            }
                        }
                    }
                    else {
                        progressBar.setVisibility(View.INVISIBLE);
                        oldPasswordEP.setError("Wrong password");
                    }
                }
            }
            else {
                finish();
            }
        });
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
            oldPasswordEP.setError(null);
            newPassword.setError(null);
            confirmNewPassword.setError(null);
        }
    };
}
