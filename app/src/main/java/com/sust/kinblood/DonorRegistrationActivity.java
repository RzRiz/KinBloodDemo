package com.sust.kinblood;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.hitomi.cmlibrary.CircleMenu;

import java.util.Calendar;
import java.util.Objects;

public class DonorRegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private DocumentReference DOCUMENT_REFERENCE_COUNTER;
    private DocumentReference DOCUMENT_REFERENCE_DATA;

    private Dialog toastMessageDialog, toastMessageDialog2, toastMessageDialog3;
    private LinearLayout linearLayout;
    private ScrollView scrollView;
    private RadioGroup radioGroupGender, radioGroupDonatedBefore;
    private EditText currentAddress, homeDistrict, occupation, institute, donateTimes;
    private Button birthDay, lastDonated;
    private ProgressBar progressBar;
    private String currentAddress_, homeDistrict_, occupation_, institute_, blood_group, gender_;
    private DatePickerDialog.OnDateSetListener dateSetListenerb, dateSetListenerd;
    private Calendar calendar = Calendar.getInstance();
    private int year = calendar.get(Calendar.YEAR), month = calendar.get(Calendar.MONTH), day = calendar.get(Calendar.DAY_OF_MONTH), bG = 0, bYear = 0, bMonth = 0, bDay = 0, dTimes = 0, dYear = 0, dMonth = 0, dDay = 0;
    private String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_registration);

        toastMessageDialog = new Dialog(DonorRegistrationActivity.this);
        toastMessageDialog2 = new Dialog(DonorRegistrationActivity.this);
        toastMessageDialog3 = new Dialog(DonorRegistrationActivity.this);

        linearLayout = findViewById(R.id.activity_donor_registration_LinearLayout);
        scrollView = findViewById(R.id.activity_donor_registration_scrollView);

        ImageView home= findViewById(R.id.activity_donor_registration_home_ImageView);
        CircleMenu circleMenu = findViewById(R.id.activity_donor_registration_CircleMenu);
        radioGroupGender = findViewById(R.id.activity_donor_registration_Gender_RadioGroup);
        radioGroupDonatedBefore = findViewById(R.id.activity_donor_registration_donated_before_RadioGroup);
        currentAddress = findViewById(R.id.activity_donor_registration_current_address_EditText);
        homeDistrict = findViewById(R.id.activity_donor_registration_home_district_EditText);
        occupation = findViewById(R.id.activity_donor_registration_occupation_EditText);
        institute = findViewById(R.id.activity_donor_registration_institute_EditText);
        donateTimes = findViewById(R.id.activity_donor_registration_donateTimes_EditText);
        birthDay = findViewById(R.id.activity_donor_registration_birthDay_Button);
        lastDonated = findViewById(R.id.activity_donor_registration_lastDonatedBlood_Button);
        progressBar = findViewById(R.id.activity_donor_registration_ProgressBar);
        progressBar.setVisibility(View.INVISIBLE);
        TextView selectedBloodGroup = findViewById(R.id.activity_donor_registration_selectedBlood_TextView);
        Button register = findViewById(R.id.activity_donor_registration_register_Button);
        Button cancelRegistration = findViewById(R.id.activity_donor_registration_cancel_registration_Button);

        FirebaseFirestore DATABASE_REFERENCE = FirebaseFirestore.getInstance();
        DOCUMENT_REFERENCE_DATA = DATABASE_REFERENCE
                .collection("Users")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        DOCUMENT_REFERENCE_COUNTER = DATABASE_REFERENCE
                .collection("Counters")
                .document("Total");

        String donorStatus = getIntent().getStringExtra("donorStatus");
        String bloodGroup_ = getIntent().getStringExtra("bloodGroup");

        if (donorStatus != null) {
            if (donorStatus.equals("positive")) {
                scrollView.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
            } else if (donorStatus.equals("negative")) {
                scrollView.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.GONE);
            }
        } else {
            scrollView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
        }

        home.setOnClickListener(view -> startActivity(new Intent(DonorRegistrationActivity.this, HomeActivity.class)));

        circleMenu.setMainMenu(Color.parseColor("#EEF4DC"), R.drawable.ic_blood, R.drawable.ic_app_logo_40dp)
                .addSubMenu(Color.parseColor("#D32F2F"), R.drawable.a_positive)
                .addSubMenu(Color.parseColor("#D32F2F"), R.drawable.a_negative)
                .addSubMenu(Color.parseColor("#D32F2F"), R.drawable.b_positive)
                .addSubMenu(Color.parseColor("#D32F2F"), R.drawable.b_negative)
                .addSubMenu(Color.parseColor("#D32F2F"), R.drawable.ab_positive)
                .addSubMenu(Color.parseColor("#D32F2F"), R.drawable.ab_negative)
                .addSubMenu(Color.parseColor("#D32F2F"), R.drawable.o_positive)
                .addSubMenu(Color.parseColor("#D32F2F"), R.drawable.o_negative)
                .setOnMenuSelectedListener(index -> {
                    bG = 1;
                    blood_group = bloodGroups[index];
                    Toast.makeText(DonorRegistrationActivity.this, blood_group + " selected", Toast.LENGTH_LONG).show();
                    selectedBloodGroup.setText(blood_group);
                });

        cancelRegistration.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            if (isOnline()) {
                DOCUMENT_REFERENCE_DATA.update("currentAddress", FieldValue.delete(),
                        "homeDistrict", FieldValue.delete(),
                        "gender", FieldValue.delete(),
                        "bloodGroup", FieldValue.delete(),
                        "occupation", FieldValue.delete(),
                        "institute", FieldValue.delete(),
                        "donorStatus", "negative",
                        "bDay", FieldValue.delete(),
                        "bMonth", FieldValue.delete(),
                        "bYear", FieldValue.delete(),
                        "donateTimes", 0,
                        "dDay", FieldValue.delete(),
                        "dMonth", FieldValue.delete(),
                        "dYear", FieldValue.delete())
                        .addOnSuccessListener(aVoid -> {
                            if (bloodGroup_ != null) {
                                DOCUMENT_REFERENCE_COUNTER.update(bloodGroup_, FieldValue.increment(-1), "allTotal", FieldValue.increment(-1))
                                        .addOnSuccessListener(aVoid1 -> {
                                            scrollView.setVisibility(View.VISIBLE);
                                            linearLayout.setVisibility(View.GONE);
                                            showOopsMessage();
                                        }).addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
                            }
                        }).addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());

                progressBar.setVisibility(View.INVISIBLE);
                showOopsMessage();
                scrollView.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                showToastMessage("No Internet Connection!");
            }
        });
        birthDay.setOnClickListener(this);
        lastDonated.setOnClickListener(this);
        register.setOnClickListener(this);

        radioGroupDonatedBefore.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.activity_donor_registration_donatePositive_RadioButton) {
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
        if (j == R.id.activity_donor_registration_donatePositive_RadioButton && donate_Times.isEmpty()) {
            donateTimes.setError("Field cannot be empty");
            donateTimes.requestFocus();
            return false;
        }
        if (j == R.id.activity_donor_registration_donatePositive_RadioButton && Integer.parseInt(donate_Times) == 0) {
            donateTimes.setError("Please Enter a valid number");
            donateTimes.requestFocus();
            return false;
        }
        if (j == R.id.activity_donor_registration_donatePositive_RadioButton) {
            dTimes = Integer.parseInt(donate_Times);
        }
        if (j == R.id.activity_donor_registration_donatePositive_RadioButton && (dDay == 0 || dMonth == 0 || dYear == 0)) {
            Toast.makeText(this, "When did you last donate blood?", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.activity_donor_registration_birthDay_Button) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(DonorRegistrationActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    dateSetListenerb, year, month, day);
            Objects.requireNonNull(datePickerDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
        if (v.getId() == R.id.activity_donor_registration_lastDonatedBlood_Button) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(DonorRegistrationActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    dateSetListenerd, year, month, day);
            Objects.requireNonNull(datePickerDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
        if (v.getId() == R.id.activity_donor_registration_register_Button) {
            if (getData()) {
                progressBar.setVisibility(View.VISIBLE);
                if (isOnline()) {
                    RegistrationHelper registrationHelper = new RegistrationHelper(currentAddress_, homeDistrict_, gender_
                            , blood_group, occupation_, institute_, "positive", bDay, bMonth
                            , bYear, dTimes, dDay, dMonth, dYear);
                    DOCUMENT_REFERENCE_DATA
                            .set(registrationHelper, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> DOCUMENT_REFERENCE_COUNTER.update(blood_group, FieldValue.increment(1), "allTotal", FieldValue.increment(1))
                                    .addOnSuccessListener(aVoid1 -> {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        showCongratulationsMessage();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()))
                            .addOnFailureListener(e -> Toast.makeText(DonorRegistrationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    showToastMessage("No Internet Connection!");
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DonorRegistrationActivity.this);
        builder.setTitle("Confirmation").setMessage("Are you sure you don't want to be a donor?")
                .setPositiveButton("Yes", (dialog, which) -> super.onBackPressed())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showCongratulationsMessage() {
        toastMessageDialog2.setContentView(R.layout.item_toast_message2);
        toastMessageDialog2.setCanceledOnTouchOutside(false);

        TextView continueButton = toastMessageDialog2.findViewById(R.id.btn_continue);

        continueButton.setOnClickListener(view -> {
            toastMessageDialog2.dismiss();
            finish();
        });

        toastMessageDialog2.show();
        Objects.requireNonNull(toastMessageDialog2.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void showOopsMessage() {
        toastMessageDialog3.setContentView(R.layout.item_toast_message3);
        toastMessageDialog3.setCanceledOnTouchOutside(false);

        TextView continueButton = toastMessageDialog3.findViewById(R.id.btn_continue2);

        continueButton.setOnClickListener(view -> {
            toastMessageDialog3.dismiss();
            finish();
        });

        toastMessageDialog3.show();
        Objects.requireNonNull(toastMessageDialog3.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void showToastMessage(final String message) {
        toastMessageDialog.setContentView(R.layout.item_toast_message);
        toastMessageDialog.setCanceledOnTouchOutside(true);

        TextView toast_TextView = toastMessageDialog.findViewById(R.id.item_toast_message_toast_TextView);
        toast_TextView.setText(message);

        toastMessageDialog.show();
        Objects.requireNonNull(toastMessageDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        new Handler().postDelayed(() -> toastMessageDialog.cancel(), 5000);
    }
}