package com.sust.kinblooddemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private Dialog toastMessageDialog;
    private EditText email, password;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toastMessageDialog = new Dialog(LoginActivity.this);

        Intent intent = getIntent();

        final String error_message = intent.getStringExtra("error_message");

        if(error_message != null && !error_message.isEmpty()) {
            showToastMessage(error_message);
        }

        email = findViewById(R.id.activity_login_email_TextInputEditText);
        password = findViewById(R.id.activity_login_password_TextInputEditText);
        Button login = findViewById(R.id.activity_login_login_Button);
        TextView forgotPassword = findViewById(R.id.activity_login_forgotPassword_TextView);
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.activity_login_ProgressBar);
        progressBar.setVisibility(View.INVISIBLE);

        forgotPassword.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, PasswordResetActivity.class)));

        login.setOnClickListener(v -> {

            if (isOnline()){
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
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                        }).addOnFailureListener(e -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
            } else {
                showToastMessage("No Internet Connection!");
            }
        });
    }

    private void closeSoftKeyBoard() {
        View v = this.getCurrentFocus();
        if (v != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public void loginToRegistration(View view) {
        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()){
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("Confirm exit").setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", (dialog, which) -> finish())
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            super.onBackPressed();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void showToastMessage(final String message) {
        toastMessageDialog.setContentView(R.layout.item_toast_message);
        toastMessageDialog.setCanceledOnTouchOutside(true);

        TextView toast_TextView = toastMessageDialog.findViewById(R.id.item_toast_message_toast_TextView);
        toast_TextView.setText(message);

        toastMessageDialog.show();
        Objects.requireNonNull(toastMessageDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        new Handler().postDelayed(() -> toastMessageDialog.cancel(), 5000);
    }
}
