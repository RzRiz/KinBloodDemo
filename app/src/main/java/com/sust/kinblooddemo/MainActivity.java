package com.sust.kinblooddemo;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private TextInputLayout password, confirmPassword;
    private EditText fullName, phoneNumber, email;
    private String full_Name, email_, password_, confirm_Password;
    private StringBuilder phone_Number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fullName = findViewById(R.id.et_fullname);
        phoneNumber = findViewById(R.id.et_phoneNumber);
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        confirmPassword = findViewById(R.id.et_confirmPassword);
        Button buttonSignUp = findViewById(R.id.btn_signUp);

        fullName.addTextChangedListener(textWatcher);
        phoneNumber.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);
        password.getEditText().addTextChangedListener(textWatcher);
        confirmPassword.getEditText().addTextChangedListener(textWatcher);


        buttonSignUp.setOnClickListener(v -> {

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

                Intent intent = new Intent(MainActivity.this, Otp.class);
                intent.putStringArrayListExtra("data", data);
                startActivity(intent);

            } else {
                Toast.makeText(MainActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
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
        password_ = password.getEditText().getText().toString().trim();
        confirm_Password = confirmPassword.getEditText().getText().toString().trim();
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

    public void signuptologin(View view){
        finish();
    }
}