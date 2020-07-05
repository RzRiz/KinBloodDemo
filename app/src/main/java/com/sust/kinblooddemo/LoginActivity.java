package com.sust.kinblooddemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.et_loginEmail);
        password = findViewById(R.id.et_loginPassword);
        Button login = findViewById(R.id.btn_login);
        TextView forgotPassword = findViewById(R.id.tv_forgot_password);
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progress_login);
        progressBar.setVisibility(View.INVISIBLE);

        forgotPassword.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, PasswordReset.class)));

        login.setOnClickListener(v -> {
            closeSoftKeyBoard();
            progressBar.setVisibility(View.VISIBLE);
            String email_ = email.getText().toString().trim();
            String password_ = password.getText().toString().trim();

            if (TextUtils.isEmpty(email_)) {
                progressBar.setVisibility(View.INVISIBLE);
                email.setError("Field cannot be empty");
                email.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(password_)) {
                progressBar.setVisibility(View.INVISIBLE);
                password.setError("Field cannot be empty");
                password.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email_).matches()) {
                progressBar.setVisibility(View.INVISIBLE);
                email.setError("Please enter a valid email address");
                email.requestFocus();
                return;
            }
            if (password_.length() < 6) {
                progressBar.setVisibility(View.INVISIBLE);
                password.setError("Please enter a valid password");
                password.requestFocus();
                return;
            }
            firebaseAuth.signInWithEmailAndPassword(email_, password_)
                    .addOnSuccessListener(authResult -> {
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, Home.class));
                        finish();
                    }).addOnFailureListener(e -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }

    private void closeSoftKeyBoard() {
        View v = this.getCurrentFocus();
        if (v != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public void logintoreg(View view) {
        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Confirm exit").setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
