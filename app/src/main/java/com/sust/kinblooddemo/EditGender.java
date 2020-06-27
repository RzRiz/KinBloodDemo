package com.sust.kinblooddemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class EditGender extends AppCompatActivity {

    private RadioGroup radioGroupGender;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_gender);

        radioGroupGender = findViewById(R.id.rg_gender);
        Button updateGender = findViewById(R.id.btn_update_gender);
        progressBar = findViewById(R.id.progress_gender);
        progressBar.setVisibility(View.GONE);

        updateGender.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            int i = radioGroupGender.getCheckedRadioButtonId();
            if (i == -1) {
                finish();
            }
            else{
                RadioButton radioButtonGender = findViewById(i);
                String gender_ = radioButtonGender.getText().toString();
                Map<String, Object> gender = new HashMap<>();
                gender.put("gender", gender_);
                Home.DOCUMENT_REFERENCE
                        .set(gender, SetOptions.merge()).addOnSuccessListener(aVoid -> {
                            Toast.makeText(EditGender.this, "Gender update successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditGender.this, Profile.class);
                            intent.putExtra("newGender", gender_);
                            setResult(10, intent);
                            finish();
                        }).addOnFailureListener(e -> {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(EditGender.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            }
        });
    }
}
