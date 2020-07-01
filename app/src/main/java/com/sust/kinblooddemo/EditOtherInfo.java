package com.sust.kinblooddemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditOtherInfo extends AppCompatActivity implements View.OnClickListener {

    private EditText occupation, institute, currentAddress, homeDistrict;
    private ArrayList<String> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_other_info);


        for(int i = 0; i < 4; i++){
            data.add("");
        }

        occupation = findViewById(R.id.et_edit_occupation);
        institute = findViewById(R.id.et_edit_institute);
        currentAddress = findViewById(R.id.et_edit_current_address);
        homeDistrict = findViewById(R.id.et_edit_home_district);
        Button updateOccupation = findViewById(R.id.btn_update_occupation);
        Button updateInstitute = findViewById(R.id.btn_update_institute);
        Button updateCurrentAddress = findViewById(R.id.btn_update_current_address);
        Button updateHomeDistrict = findViewById(R.id.btn_update_home_district);

        updateCurrentAddress.setOnClickListener(this);
        updateOccupation.setOnClickListener(this);
        updateInstitute.setOnClickListener(this);
        updateHomeDistrict.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_update_occupation){
            String occupation_ = occupation.getText().toString().trim();
            if (occupation_.isEmpty()){
                occupation.setError("Field cannot be empty");
                occupation.requestFocus();
            }
            else {
                Map<String, Object> occupation = new HashMap<>();
                occupation.put("occupation", occupation_);
                Home.DOCUMENT_REFERENCE
                        .update(occupation).addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditOtherInfo.this, "Occupation update successful", Toast.LENGTH_SHORT).show();
                    data.add(0, occupation_);
                }).addOnFailureListener(e -> Toast.makeText(EditOtherInfo.this, e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }
        else if (v.getId() == R.id.btn_update_institute){
            String institute_ = institute.getText().toString().trim();
            if (institute_.isEmpty()){
                institute.setError("Field cannot be empty");
                institute.requestFocus();
            }
            else {
                Map<String, Object> institute = new HashMap<>();
                institute.put("institute", institute_);
                Home.DOCUMENT_REFERENCE
                        .update(institute).addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditOtherInfo.this, "Institute update successful", Toast.LENGTH_LONG).show();
                    data.add(1, institute_);
                }).addOnFailureListener(e -> Toast.makeText(EditOtherInfo.this, e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }
        else if (v.getId() == R.id.btn_update_current_address){
            String currentAddress_ = currentAddress.getText().toString();
            if (currentAddress_.isEmpty()){
                currentAddress.setError("Field cannot be empty");
                currentAddress.requestFocus();
            }
            else {
                Map<String, Object> address = new HashMap<>();
                address.put("address", currentAddress_);
                Home.DOCUMENT_REFERENCE
                        .update(address).addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditOtherInfo.this, "Current address update successful", Toast.LENGTH_LONG).show();
                    data.add(2, currentAddress_);
                }).addOnFailureListener(e -> Toast.makeText(EditOtherInfo.this, e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }
        else if (v.getId() == R.id.btn_update_home_district){
            String homeDistrict_ = homeDistrict.getText().toString();
            if (homeDistrict_.isEmpty()){
                homeDistrict.setError("Field cannot be empty");
                homeDistrict.requestFocus();
            }
            else {
                Map<String, Object> homeDistrict = new HashMap<>();
                homeDistrict.put("homeDistrict", homeDistrict_);
                Home.DOCUMENT_REFERENCE
                        .update(homeDistrict).addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditOtherInfo.this, "Home district update successful", Toast.LENGTH_LONG).show();
                    data.add(3, homeDistrict_);
                }).addOnFailureListener(e -> Toast.makeText(EditOtherInfo.this, e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("data", data);
        setResult(4, intent);
        finish();
    }
}
