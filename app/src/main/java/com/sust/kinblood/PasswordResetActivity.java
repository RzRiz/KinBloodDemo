package com.sust.kinblood;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        editText = findViewById(R.id.activity_password_reset_resetEmail_TextInputEditText);
        Button button = findViewById(R.id.activity_password_reset_sendEmail_Button);

        button.setOnClickListener(v -> {
            String email = editText.getText().toString();
            if (email.isEmpty()){
                editText.setError("Field cannot be empty");
                editText.requestFocus();
                return;
            }
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnSuccessListener(aVoid -> {
                Toast.makeText(PasswordResetActivity.this, "Password reset link has been sent to the email", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> Toast.makeText(PasswordResetActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}
