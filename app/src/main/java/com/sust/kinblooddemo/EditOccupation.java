package com.sust.kinblooddemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class EditOccupation extends AppCompatActivity {

    private EditText occupation;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_occupation);

        occupation = findViewById(R.id.et_edit_occupation);
        Button updateOccupation = findViewById(R.id.btn_update_occupation);
        progressBar = findViewById(R.id.progress_occupation);
        progressBar.setVisibility(View.GONE);

        updateOccupation.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String occupation_ = occupation.getText().toString().trim();
            if (occupation_.isEmpty()){
                finish();
            }
            else {
                Map<String, Object> occupation = new HashMap<>();
                occupation.put("occupation", occupation_);
                Home.DOCUMENT_REFERENCE
                        .set(occupation, SetOptions.merge()).addOnSuccessListener(aVoid -> {
                            Toast.makeText(EditOccupation.this, "Occupation update successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditOccupation.this, Profile.class);
                            intent.putExtra("newOccupation", occupation_);
                            setResult(6, intent);
                            finish();
                        }).addOnFailureListener(e -> {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(EditOccupation.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            }
        });
    }
}
