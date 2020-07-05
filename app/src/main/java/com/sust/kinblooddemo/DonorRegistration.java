package com.sust.kinblooddemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;

public class DonorRegistration extends AppCompatActivity implements View.OnClickListener {

    private RadioGroup radioGroupPositive, radioGroupNegetive, radioGroupGender, radioGroupDonatedBefore;
    private boolean isChecking = true;
    private RadioButton radioButtonBg;
    private EditText currentAddress, homeDistrict, occupation, institute, donateTimes;
    private Button birthDay, lastDonated;
    private String currentAddress_, homeDistrict_, occupation_, institute_, blood_Group, gender_;
    private DatePickerDialog.OnDateSetListener dateSetListenerb, dateSetListenerd;
    private Calendar calendar = Calendar.getInstance();
    private int year = calendar.get(Calendar.YEAR), month = calendar.get(Calendar.MONTH), day = calendar.get(Calendar.DAY_OF_MONTH), bG = 0, bYear = 0, bMonth = 0, bDay = 0, dTimes = 0, dYear = 0, dMonth = 0, dDay = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_registration);

        radioGroupPositive = findViewById(R.id.rgPositive);
        radioGroupNegetive = findViewById(R.id.rgNegetive);
        radioGroupGender = findViewById(R.id.rgGender);
        radioGroupDonatedBefore = findViewById(R.id.rgDonated_before);
        currentAddress = findViewById(R.id.et_current_address);
        homeDistrict = findViewById(R.id.et_home_district);
        occupation = findViewById(R.id.et_occupation);
        institute = findViewById(R.id.et_institute);
        donateTimes = findViewById(R.id.et_donate_times);
        birthDay = findViewById(R.id.btn_bday);
        lastDonated = findViewById(R.id.btn_ldblood);
        Button register = findViewById(R.id.btn_regester);

        radioGroupPositive.setOnCheckedChangeListener((group, checkedId) -> {
            bG = 1;
            if (checkedId != -1 && isChecking) {
                isChecking = false;
                radioGroupNegetive.clearCheck();
                radioButtonBg = findViewById(checkedId);
            }
            isChecking = true;
        });
        radioGroupNegetive.setOnCheckedChangeListener((group, checkedId) -> {
            bG = 1;
            if (checkedId != -1 && isChecking) {
                isChecking = false;
                radioGroupPositive.clearCheck();
                radioButtonBg = findViewById(checkedId);
            }
            isChecking = true;
        });

        birthDay.setOnClickListener(this);
        lastDonated.setOnClickListener(this);
        register.setOnClickListener(this);

        radioGroupDonatedBefore.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbDonate_Positive) {
                donateTimes.setEnabled(true);
                lastDonated.setEnabled(true);
            } else {
                donateTimes.setEnabled(false);
                lastDonated.setEnabled(false);
            }
        });
    }


    public boolean getData() {
        currentAddress_ = currentAddress.getText().toString();
        if (currentAddress_.isEmpty()) {
            currentAddress.setError("Field cannot be empty");
            currentAddress.requestFocus();
            return false;
        }
        homeDistrict_ = homeDistrict.getText().toString();
        if (homeDistrict_.isEmpty()) {
            homeDistrict.setError("Field cannot be empty");
            homeDistrict.requestFocus();
            return false;
        }
        if (bYear == 0 || bMonth == 0 || bDay == 0) {
            Toast.makeText(this, "Please enter your birth date", Toast.LENGTH_SHORT).show();
            return false;
        }
        int i = radioGroupGender.getCheckedRadioButtonId();
        if (i == -1) {
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        RadioButton radioButtonGender = findViewById(i);
        gender_ = radioButtonGender.getText().toString().trim();
        if (bG == 0) {
            Toast.makeText(this, "Please select your blood group", Toast.LENGTH_SHORT).show();
            return false;
        }
        blood_Group = radioButtonBg.getText().toString().trim();
        occupation_ = occupation.getText().toString().trim();
        if (occupation_.isEmpty()) {
            occupation.setError("Field cannot be empty");
            occupation.requestFocus();
            return false;
        }
        institute_ = institute.getText().toString().trim();
        if (institute_.isEmpty()) {
            institute.setError("Field cannot be empty");
            institute.requestFocus();
            return false;
        }
        int j = radioGroupDonatedBefore.getCheckedRadioButtonId();
        if (j == -1) {
            Toast.makeText(this, "Have you donated blood before?", Toast.LENGTH_SHORT).show();
            return false;
        }
        String donate_Times = donateTimes.getText().toString().trim();
        if (j == R.id.rbDonate_Positive && donate_Times.isEmpty()) {
            donateTimes.setError("Field cannot be empty");
            donateTimes.requestFocus();
            return false;
        }
        if (j == R.id.rbDonate_Positive && Integer.parseInt(donate_Times) == 0) {
            donateTimes.setError("Please Enter a valid number");
            donateTimes.requestFocus();
            return false;
        }
        if (j == R.id.rbDonate_Positive) {
            dTimes = Integer.parseInt(donate_Times);
        }
        if (j == R.id.rbDonate_Positive && (dDay == 0 || dMonth == 0 || dYear == 0)) {
            Toast.makeText(this, "When did you last donate blood", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_bday) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(DonorRegistration.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    dateSetListenerb, year, month, day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        }
        dateSetListenerb = (view, year, month, dayOfMonth) -> {
            month++;
            String date = dayOfMonth + "/" + month + "/" + year;
            birthDay.setText(date);
            bYear = year;
            bMonth = month;
            bDay = dayOfMonth;
        };
        if (v.getId() == R.id.btn_ldblood) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(DonorRegistration.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    dateSetListenerd, year, month, day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        }
        dateSetListenerd = (view, year, month, dayOfMonth) -> {
            month++;
            String date = dayOfMonth + "/" + month + "/" + year;
            lastDonated.setText(date);
            dYear = year;
            dMonth = month;
            dDay = dayOfMonth;
        };
        if (v.getId() == R.id.btn_regester) {
            if (!getData()) {
                return;
            }
            RegistrationHelper registrationHelper = new RegistrationHelper(currentAddress_, homeDistrict_, gender_
                    , blood_Group, occupation_, institute_, "positive", bDay, bMonth
                    , bYear, dTimes, dDay, dMonth, dYear);
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseFirestore.getInstance().collection("Users")
                    .document(firebaseAuth.getCurrentUser().getUid())
                    .set(registrationHelper, SetOptions.merge()).addOnSuccessListener(aVoid -> {
                        Toast.makeText(DonorRegistration.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(DonorRegistration.this, AfterDonorReg.class));
                        finish();
                    }).addOnFailureListener(e -> Toast.makeText(DonorRegistration.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DonorRegistration.this);
        builder.setTitle("Confirmation").setMessage("Are you sure you don't want to be a donor?")
                .setPositiveButton("Yes", (dialog, which) -> super.onBackPressed())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}