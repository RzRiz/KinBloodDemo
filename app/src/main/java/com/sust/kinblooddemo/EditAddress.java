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

public class EditAddress extends AppCompatActivity {

    private EditText address;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);

        address = findViewById(R.id.et_edit_address);
        Button updateAddress = findViewById(R.id.btn_update_address);
        progressBar = findViewById(R.id.progress_address);
        progressBar.setVisibility(View.GONE);

        updateAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String address_ = address.getText().toString();
                if (address_.isEmpty()){
                    finish();
                }
                else {
                    Map<String, Object> address = new HashMap<>();
                    address.put("address", address_);
                    Profile.DOCUMENT_REFERENCE
                            .set(address, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditAddress.this, "Address update successful", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(EditAddress.this, Profile.class);
                            intent.putExtra("newAddress", address_);
                            setResult(5, intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(EditAddress.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}
