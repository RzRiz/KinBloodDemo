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

public class EditName extends AppCompatActivity {

    private EditText name;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);

        name = findViewById(R.id.et_edit_name);
        Button updateName = findViewById(R.id.btn_update_name);
        progressBar = findViewById(R.id.progress_name);
        progressBar.setVisibility(View.GONE);

        updateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String name_ = name.getText().toString().trim();
                if (name_.isEmpty()){
                    finish();
                }
                else {
                    Map<String, Object> name = new HashMap<>();
                    name.put("fullName", name_);
                    Profile.DOCUMENT_REFERENCE
                            .set(name, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditName.this, "Name update successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditName.this, Profile.class);
                            intent.putExtra("newName", name_);
                            setResult(3, intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(EditName.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}
