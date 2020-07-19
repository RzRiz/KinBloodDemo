package com.sust.kinblood;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.Objects;

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

        ImageView home= findViewById(R.id.activity_edit_blood_info_home_ImageView);
        radioGroupDonatedBefore = findViewById(R.id.activity_edit_blood_info_donated_before_RadioGroup);
        timesDonated = findViewById(R.id.activity_edit_blood_info_donateTimes_EditText);
        lastDonated = findViewById(R.id.activity_edit_blood_info_lastDonatedBlood_Button);
        Button updateDonationInfo = findViewById(R.id.activity_edit_blood_info_update_Button);

        home.setOnClickListener(view -> startActivity(new Intent(EditBloodInfo.this, HomeActivity.class)));

        updateDonationInfo.setOnClickListener(this);
        lastDonated.setOnClickListener(this);

        String oldLastDonated_ = getIntent().getStringExtra("oldLastDonated");
        lastDonated.setText(oldLastDonated_);

        radioGroupDonatedBefore.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.activity_edit_blood_info_donatePositive_RadioButton) {
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

        if (v.getId() == R.id.activity_edit_blood_info_lastDonatedBlood_Button){
            DatePickerDialog datePickerDialog = new DatePickerDialog(EditBloodInfo.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    dateSetListenerd, year, month, day);
            Objects.requireNonNull(datePickerDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        }
        else if (v.getId() == R.id.activity_edit_blood_info_update_Button){
            checkId = radioGroupDonatedBefore.getCheckedRadioButtonId();
            if (checkId == -1){
                Toast.makeText(this, "Have you donated blood before?", Toast.LENGTH_LONG).show();
            } else {
                if (checkId == R.id.activity_edit_blood_info_donateNegative_RadioButton){
                    DonationInfoHelper donationInfoHelper = new DonationInfoHelper(0, 0, 0, 0);
                    HomeActivity.DOCUMENT_REFERENCE
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
                        HomeActivity.DOCUMENT_REFERENCE
                                .set(donationInfoHelper, SetOptions.merge()).addOnSuccessListener(aVoid -> {
                            Toast.makeText(EditBloodInfo.this, "Donation information update successful", Toast.LENGTH_SHORT).show();
                            date = dDay + " / " + dMonth + " / " + dYear;
                        }).addOnFailureListener(e -> Toast.makeText(EditBloodInfo.this, e.getMessage(), Toast.LENGTH_LONG).show());
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
        Intent intent = new Intent(EditBloodInfo.this, ProfileActivity.class);
        if (checkId == R.id.activity_edit_blood_info_donateNegative_RadioButton){
            intent.putExtra("donationInfo", "negative");
            setResult(2, intent);
        }
        else {
            intent.putExtra("newLastDonated", date);
            intent.putExtra("newDonatetimes", timesDonated_);
            setResult(3, intent);
        }
        finish();
    }
}
