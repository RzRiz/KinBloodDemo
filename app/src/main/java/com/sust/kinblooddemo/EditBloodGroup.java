package com.sust.kinblooddemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class EditBloodGroup extends AppCompatActivity {

    private RadioGroup radioGroupPositive, radioGroupNegetive;
    private RadioButton radioButtonBg;
    private int bG;
    private boolean isChecking = true;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_blood_group);

        radioGroupPositive = findViewById(R.id.rg_positive);
        radioGroupNegetive = findViewById(R.id.rg_negative);
        progressBar = findViewById(R.id.progress_blood_group);
        progressBar.setVisibility(View.GONE);
        Button updateBloodGroup = findViewById(R.id.btn_update_blood_group);

        radioGroupPositive.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                bG = 1;
                if (checkedId != -1 && isChecking) {
                    isChecking = false;
                    radioGroupNegetive.clearCheck();
                    radioButtonBg = findViewById(checkedId);
                }
                isChecking = true;
            }
        });
        radioGroupNegetive.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                bG = 1;
                if (checkedId != -1 && isChecking) {
                    isChecking = false;
                    radioGroupPositive.clearCheck();
                    radioButtonBg = findViewById(checkedId);
                }
                isChecking = true;
            }
        });

        updateBloodGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (bG != 1){
                    finish();
                }
                else {
                    String bloodGroup_ = radioButtonBg.getText().toString();
                    Map<String, Object> bloodGroup = new HashMap<>();
                    bloodGroup.put("bloodGroup", bloodGroup_);
                    Profile.DOCUMENT_REFERENCE
                            .set(bloodGroup, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditBloodGroup.this, "Blood group update successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditBloodGroup.this, Profile.class);
                            intent.putExtra("newBloodGroup", bloodGroup_);
                            setResult(9, intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(EditBloodGroup.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
