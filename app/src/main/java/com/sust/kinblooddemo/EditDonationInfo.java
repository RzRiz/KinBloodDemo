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
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.google.firebase.firestore.SetOptions;
import java.util.Calendar;


public class EditDonationInfo extends AppCompatActivity {

    private RadioGroup radioGroupDonatedBefore;
    private EditText timesDonated;
    private Button lastDonated;
    private int checkId;
    private DatePickerDialog.OnDateSetListener dateSetListenerd;
    private Calendar calendar = Calendar.getInstance();
    private int year = calendar.get(Calendar.YEAR), month = calendar.get(Calendar.MONTH), day = calendar.get(Calendar.DAY_OF_MONTH), dDay, dMonth, dYear, timesdonated;
    private String timesDonated_;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_donation_info);

        radioGroupDonatedBefore = findViewById(R.id.rg_donated_before);
        timesDonated = findViewById(R.id.et_edit_times_donated);
        lastDonated = findViewById(R.id.btn_last_donated);
        Button updateDonationInfo = findViewById(R.id.btn_update_donation_info);
        progressBar = findViewById(R.id.progress_donation_info);
        progressBar.setVisibility(View.GONE);

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

        lastDonated.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(EditDonationInfo.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    dateSetListenerd, year, month, day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });

        dateSetListenerd = (view, year, month, dayOfMonth) -> {
            month++;
            String date = dayOfMonth + "/" + month + "/" + year;
            lastDonated.setText(date);
            dDay = dayOfMonth;
            dMonth = month;
            dYear = year;
        };

        updateDonationInfo.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            checkId = radioGroupDonatedBefore.getCheckedRadioButtonId();
            if (checkId == -1){
                finish();
            } else {
                if (checkId == R.id.rb_donate_negative){
                    DonationInfoHelper donationInfoHelper = new DonationInfoHelper(0, 0, 0, 0, "No");
                    Home.DOCUMENT_REFERENCE
                            .set(donationInfoHelper, SetOptions.merge()).addOnSuccessListener(aVoid -> {
                                Toast.makeText(EditDonationInfo.this, "Donation information update successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EditDonationInfo.this, Profile.class);
                                intent.putExtra("newLastDonated", "N/A");
                                intent.putExtra("newDonatetimes", "0");
                                intent.putExtra("newDonatedBefore", "No");
                                setResult(8, intent);
                                finish();
                            }).addOnFailureListener(e -> {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(EditDonationInfo.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                }
                else {
                    timesDonated_ = timesDonated.getText().toString();
                    if (timesDonated_.isEmpty()){
                        progressBar.setVisibility(View.INVISIBLE);
                        timesDonated.setError("Field cannot be empty");
                        timesDonated.requestFocus();
                    }
                    else if (timesDonated_.equals("0")){
                        progressBar.setVisibility(View.INVISIBLE);
                        timesDonated.setError("You have donated before so this value cannot be zero");
                        timesDonated.requestFocus();
                    }
                    else if(dDay == 0){
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(EditDonationInfo.this, "Please set your last donation date", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        timesdonated = Integer.parseInt(timesDonated_);
                        DonationInfoHelper donationInfoHelper = new DonationInfoHelper(dDay, dMonth, dYear, timesdonated, "Yes");
                        Home.DOCUMENT_REFERENCE
                                .set(donationInfoHelper, SetOptions.merge()).addOnSuccessListener(aVoid -> {
                                    Toast.makeText(EditDonationInfo.this, "Donation information update successful", Toast.LENGTH_SHORT).show();
                                    String date = dDay + " / " + dMonth + " / " + dYear;
                                    Intent intent = new Intent(EditDonationInfo.this, Profile.class);
                                    intent.putExtra("newLastDonated", date);
                                    intent.putExtra("newDonatetimes", timesDonated_);
                                    intent.putExtra("newDonatedBefore", "Yes");
                                    setResult(8, intent);
                                    finish();
                                }).addOnFailureListener(e -> {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(EditDonationInfo.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    }
                }
            }
        });

    }
}
