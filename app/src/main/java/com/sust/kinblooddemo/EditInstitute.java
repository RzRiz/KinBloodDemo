package com.sust.kinblooddemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class EditInstitute extends AppCompatActivity {

    private EditText institute;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_institute);

        institute = findViewById(R.id.et_edit_institute);
        Button updateInstitute = findViewById(R.id.btn_update_institute);
        progressBar = findViewById(R.id.progress_institute);
        progressBar.setVisibility(View.GONE);

        updateInstitute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String institute_ = institute.getText().toString().trim();
                if (institute_.isEmpty()){
                    finish();
                }
                else {
                    Map<String, Object> institute = new HashMap<>();
                    institute.put("institute", institute_);
                    Profile.DOCUMENT_REFERENCE
                            .set(institute, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditInstitute.this, "Institute update successful", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(EditInstitute.this, Profile.class);
                            intent.putExtra("newInstitute", institute_);
                            setResult(7, intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(EditInstitute.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}
