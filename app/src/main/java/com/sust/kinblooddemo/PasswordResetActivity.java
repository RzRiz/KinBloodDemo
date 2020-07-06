package com.sust.kinblooddemo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        editText = findViewById(R.id.et_reset_email);
        Button button = findViewById(R.id.btn_send_link);

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
