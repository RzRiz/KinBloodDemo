package com.sust.kinblooddemo;


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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Objects;


public class RegistrationActivity extends AppCompatActivity {

    private Dialog toastMessageDialog;
    private TextInputLayout password, confirmPassword;
    private EditText fullName, phoneNumber, email;
    private String full_Name, email_, password_, confirm_Password;
    private StringBuilder phone_Number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        toastMessageDialog = new Dialog(RegistrationActivity.this);

        fullName = findViewById(R.id.activity_registration_fullName_TextInputEditText);
        phoneNumber = findViewById(R.id.activity_registration_phoneNumber_TextInputEditText);
        email = findViewById(R.id.activity_registration_email_TextInputEditText);
        password = findViewById(R.id.activity_registration_password_TextInputLayout);
        confirmPassword = findViewById(R.id.activity_registration_confirmPassword_TextInputLayout);
        Button buttonSignUp = findViewById(R.id.activity_registration_signUp_Button);

        fullName.addTextChangedListener(textWatcher);
        phoneNumber.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);
        Objects.requireNonNull(password.getEditText()).addTextChangedListener(textWatcher);
        Objects.requireNonNull(confirmPassword.getEditText()).addTextChangedListener(textWatcher);

        buttonSignUp.setOnClickListener(v -> {

            if (isOnline()){
                getData();

                if (!validate()) {
                    return;
                }

                if (password_.equals(confirm_Password)) {
                    ArrayList<String> data = new ArrayList<>();

                    data.add(full_Name);
                    data.add(phone_Number.toString());
                    data.add(email_);
                    data.add(password_);

                    Intent intent = new Intent(RegistrationActivity.this, VerificationActivity.class);
                    intent.putStringArrayListExtra("data", data);
                    startActivity(intent);

                } else {
                    Toast.makeText(RegistrationActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                }
            } else {
                showToastMessage("No Internet Connection!");
            }
        });
    }


    private boolean validate() {
        if (full_Name.isEmpty()) {
            fullName.setError("Field cannot be empty!");
            fullName.requestFocus();
            return false;
        }
        if (!phoneValidate()){
            return false;
        }
        if (email_.isEmpty()) {
            email.setError("Email cannot be empty!");
            email.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email_).matches()) {
            email.setError("Please enter a valid email address");
            email.requestFocus();
            return false;
        }
        if (password_.isEmpty()) {
            password.setError("Field cannot be empty!");
            password.requestFocus();
            return false;
        }
        if (password_.length() < 6) {
            password.setError("Password must contain at least 6 characters");
            password.requestFocus();
            return false;
        }
        if (confirm_Password.isEmpty()) {
            confirmPassword.setError("Field cannot be empty!");
            confirmPassword.requestFocus();
            return false;
        }
        return true;
    }

    private boolean phoneValidate() {
        if (phone_Number.length() == 0){
            phoneNumber.setError("Field cannot be empty!");
            phoneNumber.requestFocus();
            return false;
        }
        while (phone_Number.charAt(0) != '1') {
            phone_Number.deleteCharAt(0);
            if (phone_Number.length() == 0){
                phoneNumber.setError("Please enter a valid phone number");
                phoneNumber.requestFocus();
                return false;
            }
        }
        int i = phone_Number.length();
        for (int j = 0; j < i; j++) {
            if(!(phone_Number.charAt(j) >= '0' && phone_Number.charAt(j) <= '9')){
                phone_Number.deleteCharAt(j);
                j--;
                i--;
            }
        }
        if (phone_Number.length() != 10){
            phoneNumber.setError("Please enter a valid phone number");
            phoneNumber.requestFocus();
            return false;
        }
        return  true;
    }

    private void getData() {

        full_Name = fullName.getText().toString().trim();
        phone_Number = new StringBuilder(phoneNumber.getText().toString().trim());
        email_ = email.getText().toString().trim();
        password_ = Objects.requireNonNull(password.getEditText()).getText().toString().trim();
        confirm_Password = Objects.requireNonNull(confirmPassword.getEditText()).getText().toString().trim();
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
            fullName.setError(null);
            phoneNumber.setError(null);
            email.setError(null);
            password.setError(null);
            confirmPassword.setError(null);
        }
    };

    public void signUpToLogin(View view){
        finish();
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