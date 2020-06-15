package com.sust.kinblooddemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;


public class EditEmail extends AppCompatActivity {

    private EditText email;
    private String oldEmail_, newEmail_, password_;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);

        progressBar = findViewById(R.id.progress_email);
        progressBar.setVisibility(View.GONE);
        email = findViewById(R.id.et_edit_emailEP);
        Button updateEmail = findViewById(R.id.btn_update_email);

        oldEmail_ = getIntent().getStringExtra("oldEmail");
        password_ = getIntent().getStringExtra("password");

        updateEmail.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            newEmail_ = email.getText().toString().trim();
            if (!newEmail_.isEmpty()) {
                if (!validate()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }
                EmailAuthCredential credential = (EmailAuthCredential) EmailAuthProvider.getCredential(oldEmail_, password_);
                Profile.FIREBASE_USER.reauthenticate(credential).addOnSuccessListener(aVoid -> Profile.FIREBASE_USER.updateEmail(newEmail_).addOnSuccessListener(aVoid1 -> {
                    Map<String, Object> email = new HashMap<>();
                    email.put("email", newEmail_);
                    Profile.DOCUMENT_REFERENCE
                            .set(email, SetOptions.merge()).addOnSuccessListener(aVoid11 -> {
                                Toast.makeText(EditEmail.this, "Email update successful", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(EditEmail.this, Profile.class);
                                intent.putExtra("newEmail", newEmail_);
                                setResult(1, intent);
                                finish();
                            }).addOnFailureListener(e -> {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(EditEmail.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                }).addOnFailureListener(e -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(EditEmail.this, e.getMessage(), Toast.LENGTH_LONG).show();
                })).addOnFailureListener(e -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(EditEmail.this, e.getMessage(), Toast.LENGTH_LONG).show();
                });

            } else {
                finish();
            }
        });
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

}
