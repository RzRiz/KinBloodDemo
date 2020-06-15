package com.sust.kinblooddemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;

public class DonarRegistration extends AppCompatActivity implements View.OnClickListener {

    private RadioGroup radioGroupPositive, radioGroupNegetive, radioGroupGender, radioGroupDonatedBefore;
    private boolean isChecking = true;
    private RadioButton radioButtonBg;
    private EditText address, occupation, institute, donateTimes;
    private Button birthDay, lastDonated;
    private String address_, occupation_, institute_, blood_Group, gender_, donatedBefore_;
    private DatePickerDialog.OnDateSetListener dateSetListenerb, dateSetListenerd;
    private Calendar calendar = Calendar.getInstance();
    private int year = calendar.get(Calendar.YEAR), month = calendar.get(Calendar.MONTH), day = calendar.get(Calendar.DAY_OF_MONTH), bG = 0, bYear = 0, bMonth = 0, bDay = 0, dTimes = 0, dYear = 0, dMonth = 0, dDay = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donar__registration);

        radioGroupPositive = findViewById(R.id.rgPositive);
        radioGroupNegetive = findViewById(R.id.rgNegetive);
        radioGroupGender = findViewById(R.id.rgGender);
        radioGroupDonatedBefore = findViewById(R.id.rgDonated_before);
        address = findViewById(R.id.et_address);
        occupation = findViewById(R.id.et_occupation);
        institute = findViewById(R.id.et_institute);
        donateTimes = findViewById(R.id.et_donate_times);
        birthDay = findViewById(R.id.btn_bday);
        lastDonated = findViewById(R.id.btn_ldblood);
        Button register = findViewById(R.id.btn_regester);

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

        birthDay.setOnClickListener(this);
        lastDonated.setOnClickListener(this);
        register.setOnClickListener(this);

        radioGroupDonatedBefore.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbDonate_Positive) {
                    donateTimes.setEnabled(true);
                    lastDonated.setEnabled(true);
                } else {
                    donateTimes.setEnabled(false);
                    lastDonated.setEnabled(false);
                }
            }
        });
    }


    public boolean getData() {
        address_ = address.getText().toString().trim();
        if (address_.isEmpty()) {
            address.setError("Field cannot be empty");
            address.requestFocus();
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
        RadioButton radioButtonDonatedBefore = findViewById(j);
        donatedBefore_ = radioButtonDonatedBefore.getText().toString();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_bday) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(DonarRegistration.this,
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
            DatePickerDialog datePickerDialog = new DatePickerDialog(DonarRegistration.this,
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
            RegistrationHelper registrationHelper = new RegistrationHelper(address_, gender_
                    , blood_Group, occupation_, institute_, "positive", bDay, bMonth
                    , bYear, dTimes, dDay, dMonth, dYear, donatedBefore_);
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseFirestore.getInstance().collection("Users")
                    .document(firebaseAuth.getCurrentUser().getUid())
                    .set(registrationHelper, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(DonarRegistration.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DonarRegistration.this, AfterDonarReg.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DonarRegistration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DonarRegistration.this);
        builder.setTitle("Information").setMessage("Are you sure you dont want to be a donar?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(DonarRegistration.this, Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}