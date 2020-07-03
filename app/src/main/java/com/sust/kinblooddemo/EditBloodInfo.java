package com.sust.kinblooddemo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;


public class EditBloodInfo extends AppCompatActivity implements View.OnClickListener {

    private RadioGroup radioGroupDonatedBefore;
    private EditText timesDonated;
    private Button lastDonated;
    private DatePickerDialog.OnDateSetListener dateSetListenerd;
    private Calendar calendar = Calendar.getInstance();
    private int year = calendar.get(Calendar.YEAR);
    private int month = calendar.get(Calendar.MONTH);
    private int day = calendar.get(Calendar.DAY_OF_MONTH);
    private int dDay, dMonth, dYear, checkId;
    private String timesDonated_, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_blood_info);


        radioGroupDonatedBefore = findViewById(R.id.rg_donated_before);
        timesDonated = findViewById(R.id.et_edit_times_donated);
        lastDonated = findViewById(R.id.btn_last_donated);
        Button updateDonationInfo = findViewById(R.id.btn_update_donation_info);

        updateDonationInfo.setOnClickListener(this);
        lastDonated.setOnClickListener(this);

        String oldLastDonated_ = getIntent().getStringExtra("oldLastDonated");
        lastDonated.setText(oldLastDonated_);


        radioGroupDonatedBefore.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_donate_positive) {
                timesDonated.setEnabled(true);
                lastDonated.setEnabled(true);
            } else {
                timesDonated.setEnabled(false);
                lastDonated.setEnabled(false);
                timesDonated.setText("0");
                lastDonated.setText("N/A");
            }
        });

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_last_donated){
            DatePickerDialog datePickerDialog = new DatePickerDialog(EditBloodInfo.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    dateSetListenerd, year, month, day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        }
        else if (v.getId() == R.id.btn_update_donation_info){
            checkId = radioGroupDonatedBefore.getCheckedRadioButtonId();
            if (checkId == -1){
                Toast.makeText(this, "Have you donated blood before?", Toast.LENGTH_LONG).show();
            } else {
                if (checkId == R.id.rb_donate_negative){
                    DonationInfoHelper donationInfoHelper = new DonationInfoHelper(0, 0, 0, 0);
                    Home.DOCUMENT_REFERENCE
                            .set(donationInfoHelper, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> Toast.makeText(EditBloodInfo.this, "Donation information update successful", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(EditBloodInfo.this, e.getMessage(), Toast.LENGTH_LONG).show());
                }
                else {
                    timesDonated_ = timesDonated.getText().toString();
                    if (timesDonated_.isEmpty()){
                        timesDonated.setError("Field cannot be empty");
                        timesDonated.requestFocus();
                    }
                    else if (Integer.parseInt(timesDonated_) == 0){
                        timesDonated.setError("You have donated before so this value cannot be zero");
                        timesDonated.requestFocus();
                    }
                    else if(dDay == 0){
                        Toast.makeText(EditBloodInfo.this, "Please set your last donation date", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        int timesdonated = Integer.parseInt(timesDonated_);
                        DonationInfoHelper donationInfoHelper = new DonationInfoHelper(dDay, dMonth, dYear, timesdonated);
                        Home.DOCUMENT_REFERENCE
                                .set(donationInfoHelper, SetOptions.merge()).addOnSuccessListener(aVoid -> {
                            Toast.makeText(EditBloodInfo.this, "Donation information update successful", Toast.LENGTH_SHORT).show();
                            date = dDay + " / " + dMonth + " / " + dYear;
                        }).addOnFailureListener(e -> {
                            Toast.makeText(EditBloodInfo.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        });
                    }
                }
            }
        }
        dateSetListenerd = (view, year, month, dayOfMonth) -> {
            month++;
            String date = dayOfMonth + "/" + month + "/" + year;
            lastDonated.setText(date);
            dDay = dayOfMonth;
            dMonth = month;
            dYear = year;
        };
    }

    @Override
    public void onBackPressed() {
        if (checkId == R.id.rb_donate_negative){
            Intent intent = new Intent(EditBloodInfo.this, Profile.class);
            intent.putExtra("donationInfo", "negative");
            setResult(2, intent);
        }
        else {
            Intent intent = new Intent(EditBloodInfo.this, Profile.class);
            intent.putExtra("newLastDonated", date);
            intent.putExtra("newDonatetimes", timesDonated_);
            setResult(3, intent);
        }
        finish();
    }
}
