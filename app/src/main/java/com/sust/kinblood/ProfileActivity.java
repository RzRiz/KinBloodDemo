package com.sust.kinblood;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView fullName, email, phoneNumber, currentAddress, homeDistrict, occupation, institute, donateTimes, birthDate, gender, bloodGroup, lastDonated;
    private String fullName_, email_, phoneNumber_, currentAddress_, homeDistrict_, occupation_, institute_, donorStatus_, birthDate_, gender_, bloodGroup_, donateTimes_, lastDonated_;
    private SignupHelper signupHelper;
    private RegistrationHelper registrationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView home = findViewById(R.id.activity_profile_home_ImageView);
        fullName = findViewById(R.id.activity_profile_edit_name_TextView);
        email = findViewById(R.id.activity_profile_edit_email_TextView);
        phoneNumber = findViewById(R.id.activity_profile_edit_phone_number_TextView);
        currentAddress = findViewById(R.id.activity_profile_edit_current_address_TextView);
        homeDistrict = findViewById(R.id.activity_profile_edit_home_district_TextView);
        occupation = findViewById(R.id.activity_profile_edit_occupation_TextView);
        institute = findViewById(R.id.activity_profile_edit_institute_TextView);
        donateTimes = findViewById(R.id.activity_profile_edit_times_donated_TextView);
        birthDate = findViewById(R.id.activity_profile_edit_birth_date_TextView);
        gender = findViewById(R.id.activity_profile_edit_gender_TextView);
        bloodGroup = findViewById(R.id.activity_profile_edit_blood_group_TextView);
        lastDonated = findViewById(R.id.activity_profile_edit_last_donated_TextView);

        TextView editPersonalInfo = findViewById(R.id.activity_profile_edit_personal_info_TextView);
        TextView editBloodInfo = findViewById(R.id.activity_profile_edit_blood_info_TextView);
        TextView editOtherInfo = findViewById(R.id.activity_profile_edit_other_info_TextView);

        editPersonalInfo.setOnClickListener(this);
        editBloodInfo.setOnClickListener(this);
        editOtherInfo.setOnClickListener(this);

        home.setOnClickListener(view -> {
            startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            finish();
        });

        HomeActivity.DOCUMENT_REFERENCE
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    donorStatus_ = documentSnapshot.getString("donorStatus");
                    if (donorStatus_ != null) {
                        if (donorStatus_.equals("positive") || donorStatus_.equals("negative")) {
                            signupHelper = documentSnapshot.toObject(SignupHelper.class);
                            if (signupHelper != null) {
                                fullName_ = signupHelper.getFullName();
                                email_ = signupHelper.getEmail();
                                phoneNumber_ = signupHelper.getPhoneNumber();

                                fullName.setText(fullName_);
                                email.setText(email_);
                                phoneNumber.setText(phoneNumber_);
                            }
                        }
                        if (donorStatus_.equals("positive")) {
                            registrationHelper = documentSnapshot.toObject(RegistrationHelper.class);
                            if (registrationHelper != null) {
                                currentAddress_ = registrationHelper.getCurrentAddress();
                                homeDistrict_ = registrationHelper.getHomeDistrict();
                                occupation_ = registrationHelper.getOccupation();
                                institute_ = registrationHelper.getInstitute();
                                int bDay = registrationHelper.getbDay(), bMonth = registrationHelper.getbMonth(), bYear = registrationHelper.getbYear();
                                birthDate_ = bDay + " / " + bMonth + " / " + bYear;
                                gender_ = registrationHelper.getGender();
                                bloodGroup_ = registrationHelper.getBloodGroup();
                                donateTimes_ = String.valueOf(registrationHelper.getDonateTimes());
                                int dDay = registrationHelper.getdDay(), dMonth = registrationHelper.getdMonth(), dYear = registrationHelper.getdYear();
                                lastDonated_ = dDay + " / " + dMonth + " / " + dYear;

                                currentAddress.setText(currentAddress_);
                                homeDistrict.setText(homeDistrict_);
                                occupation.setText(occupation_);
                                institute.setText(institute_);
                                birthDate.setText(birthDate_);
                                gender.setText(gender_);
                                bloodGroup.setText(bloodGroup_);
                                donateTimes.setText(donateTimes_);
                                lastDonated.setText(lastDonated_);
                            }
                        }
                    }
                }).addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.activity_profile_edit_personal_info_TextView) {
            Intent intent = new Intent(ProfileActivity.this, EditPersonalInfo.class);
            intent.putExtra("oldEmail", email_);
            startActivityForResult(intent, 1);
        }

        else if (v.getId() == R.id.activity_profile_edit_blood_info_TextView) {
            if (donorStatus_.equals("negative")){
                Toast.makeText(this, "You have to be a donor to edit this field", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(ProfileActivity.this, EditBloodInfo.class);
            intent.putExtra("oldLastDonated", lastDonated_);
            startActivityForResult(intent, 2);
        }

        else if (v.getId() == R.id.activity_profile_edit_other_info_TextView) {
            if (donorStatus_.equals("negative")){
                Toast.makeText(this, "You have to be a donor to edit this field", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(ProfileActivity.this, EditOtherInfo.class);
            intent.putExtra("oldBirthDay", birthDate_);
            startActivityForResult(intent, 3);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("test", "onactivity");
        if (resultCode == 1 && data != null){
            ArrayList<String> ara = data.getStringArrayListExtra("ara");
            assert ara != null;
            if (!ara.get(0).equals("")){
                fullName_ = ara.get(0);
                fullName.setText(fullName_);
            }
            if (!ara.get(1).equals("")){
                phoneNumber_ = ara.get(1);
                phoneNumber.setText(phoneNumber_);
            }
            if (!ara.get(2).equals("")){
                email_ = ara.get(2);
                email.setText(email_);
            }
        }
        else if (resultCode == 2 && data != null){
            donateTimes.setText("0");
            lastDonated.setText("N/A");
        }
        else if (resultCode == 3 && data != null){
            donateTimes_ = data.getStringExtra("newDonatetimes");
            lastDonated_ = data.getStringExtra("newLastDonated");
            donateTimes.setText(donateTimes_);
            lastDonated.setText(lastDonated_);
        }
        else if (resultCode == 4 && data != null){
            ArrayList<String> ara2 = data.getStringArrayListExtra("data");
            assert ara2 != null;
            if (!ara2.get(0).equals("")){
                occupation_ = ara2.get(0);
                occupation.setText(occupation_);
            }
            if (!ara2.get(1).equals("")){
                institute_ = ara2.get(1);
                institute.setText(institute_);
            }
            if (!ara2.get(2).equals("")){
                currentAddress_ = ara2.get(2);
                currentAddress.setText(currentAddress_);
            }
            if (!ara2.get(3).equals("")){
                homeDistrict_ = ara2.get(3);
                homeDistrict.setText(homeDistrict_);
            }
        }
    }
}