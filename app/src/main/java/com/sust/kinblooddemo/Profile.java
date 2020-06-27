package com.sust.kinblooddemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;


public class Profile extends AppCompatActivity implements View.OnClickListener {

    private TextView fullName, email, phoneNumber, address, occupation, institute, donateTimes, birthDate, gender, bloodGroup, lastDonated, donatedBefore;
    private EditText password;
    private String fullName_, email_, phoneNumber_, password_, address_, occupation_, institute_, donarstatus_, birthDate_, gender_, bloodGroup_, donatedBefore_, donateTimes_, lastDonated_;
    private SignupHelper signupHelper;
    private RegistrationHelper registrationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // ************************...............................Hook Text Views..................................****************************

        TextView editName = findViewById(R.id.tv_edit_name);
        TextView editEmail = findViewById(R.id.tv_edit_email);
        TextView editPhoneNumber = findViewById(R.id.tv_edit_phone_number);
        TextView editPassword = findViewById(R.id.tv_edit_password);
        TextView editAddress = findViewById(R.id.tv_edit_address);
        TextView editBdate = findViewById(R.id.tv_edit_birth_date);
        TextView editGender = findViewById(R.id.tv_edit_gender);
        TextView editBloodGroup = findViewById(R.id.tv_edit_blood_group);
        TextView editOccupation = findViewById(R.id.tv_edit_occupation);
        TextView editInstitute = findViewById(R.id.tv_edit_institute);
        TextView editDonationInfo = findViewById(R.id.tv_edit_donation_info);


        //  *************************.............................Hook Text Views.....................................******************************

        //  *************************.............................Hook Edit Texts.....................................******************************

        fullName = findViewById(R.id.tv_edit_nameN);
        email = findViewById(R.id.tv_edit_emailN);
        phoneNumber = findViewById(R.id.tv_edit_phone_numberN);
        password = findViewById(R.id.et_edit_password);
        address = findViewById(R.id.tv_edit_addressN);
        occupation = findViewById(R.id.tv_edit_occupationN);
        institute = findViewById(R.id.tv_edit_instituteN);
        donateTimes = findViewById(R.id.tv_edit_times_donatedN);
        birthDate = findViewById(R.id.tv_edit_birth_dateN);
        gender = findViewById(R.id.tv_edit_genderN);
        bloodGroup = findViewById(R.id.tv_edit_blood_groupN);
        lastDonated = findViewById(R.id.tv_edit_last_donatedN);
        donatedBefore = findViewById(R.id.tv_edit_donated_beforeN);

        //  *************************.............................Hook Edit Texts.....................................******************************


        editName.setOnClickListener(this);
        editEmail.setOnClickListener(this);
        editPhoneNumber.setOnClickListener(this);
        editPassword.setOnClickListener(this);
        editAddress.setOnClickListener(this);
        editBdate.setOnClickListener(this);
        editGender.setOnClickListener(this);
        editBloodGroup.setOnClickListener(this);
        editOccupation.setOnClickListener(this);
        editInstitute.setOnClickListener(this);
        editDonationInfo.setOnClickListener(this);


        Home.DOCUMENT_REFERENCE
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    donarstatus_ = documentSnapshot.getString("donarstatus");
                    if (donarstatus_.equals("positive") || donarstatus_.equals("negetive")) {
                        signupHelper = documentSnapshot.toObject(SignupHelper.class);
                        fullName_ = signupHelper.getFullName();
                        email_ = signupHelper.getEmail();
                        phoneNumber_ = signupHelper.getPhoneNumber();
                        password_ = signupHelper.getPassword();

                        fullName.setText(fullName_);
                        email.setText(email_);
                        phoneNumber.setText(phoneNumber_);
                        password.setText(password_);
                    }
                    if (donarstatus_.equals("positive")) {
                        registrationHelper = documentSnapshot.toObject(RegistrationHelper.class);
                        address_ = registrationHelper.getAddress();
                        occupation_ = registrationHelper.getOccupation();
                        institute_ = registrationHelper.getInstitute();
                        int bDay = registrationHelper.getbDay(), bMonth = registrationHelper.getbMonth(), bYear = registrationHelper.getbYear();
                        birthDate_ = bDay + " / " + bMonth + " / " + bYear;
                        gender_ = registrationHelper.getGender();
                        bloodGroup_ = registrationHelper.getBloodGroup();
                        donateTimes_ = String.valueOf(registrationHelper.getDonateTimes());
                        int dDay = registrationHelper.getdDay(), dMonth = registrationHelper.getdMonth(), dYear = registrationHelper.getdYear();
                        lastDonated_ = dDay + " / " + dMonth + " / " + dYear;
                        donatedBefore_ = registrationHelper.getDonatedBefore();

                        address.setText(address_);
                        occupation.setText(occupation_);
                        institute.setText(institute_);
                        birthDate.setText(birthDate_);
                        gender.setText(gender_);
                        bloodGroup.setText(bloodGroup_);
                        donateTimes.setText(donateTimes_);
                        lastDonated.setText(lastDonated_);
                        donatedBefore.setText(donatedBefore_);
                    }
                }).addOnFailureListener(e -> Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_edit_name) {
            Intent intent = new Intent(Profile.this, EditName.class);
            startActivityForResult(intent, 3);
        }
        else if (v.getId() == R.id.tv_edit_email) {
             Intent intent = new Intent(Profile.this, EditEmail.class);
             intent.putExtra("oldEmail", email_);
             intent.putExtra("password", password_);
             startActivityForResult(intent, 1);
        }
        else if (v.getId() == R.id.tv_edit_phone_number) {
            Intent intent = new Intent(Profile.this, EditPhoneNumber.class);
            startActivityForResult(intent, 4);
        }
        else if (v.getId() == R.id.tv_edit_address) {
            if (donarstatus_.equals("negetive")){
                Toast.makeText(this, "You have to be a donar to edit this field", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(Profile.this, EditAddress.class);
            startActivityForResult(intent, 5);
        }
        else if (v.getId() == R.id.tv_edit_occupation) {
            if (donarstatus_.equals("negetive")){
                Toast.makeText(this, "You have to be a donar to edit this field", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(Profile.this, EditOccupation.class);
            startActivityForResult(intent, 6);
        }
        else if (v.getId() == R.id.tv_edit_institute) {
            if (donarstatus_.equals("negetive")){
                Toast.makeText(this, "You have to be a donar to edit this field", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(Profile.this, EditInstitute.class);
            startActivityForResult(intent, 7);
        }
        else if (v.getId() == R.id.tv_edit_donation_info) {
            if (donarstatus_.equals("negetive")){
                Toast.makeText(this, "You have to be a donar to edit this field", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(Profile.this, EditDonationInfo.class);
            intent.putExtra("oldLastDonated", lastDonated_);
            startActivityForResult(intent, 8);
        }

        else if (v.getId() == R.id.tv_edit_blood_group) {
            if (donarstatus_.equals("negetive")){
                Toast.makeText(this, "You have to be a donar to edit this field", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(Profile.this, EditBloodGroup.class);
            startActivityForResult(intent, 9);
        }
        else if (v.getId() == R.id.tv_edit_gender) {
            if (donarstatus_.equals("negetive")){
                Toast.makeText(this, "You have to be a donar to edit this field", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(Profile.this, EditGender.class);
            startActivityForResult(intent, 10);
        }
        else if (v.getId() == R.id.tv_edit_birth_date) {
            if (donarstatus_.equals("negetive")){
                Toast.makeText(this, "You have to be a donar to edit this field", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(Profile.this, EditBirthDate.class);
            intent.putExtra("oldBirthDay", birthDate_);
            startActivityForResult(intent, 0);
        }
        else if (v.getId() == R.id.tv_edit_password){
            Intent intent = new Intent(Profile.this, EditPassword.class);
            intent.putExtra("email", email_);
            intent.putExtra("oldPassword", password_);
            startActivityForResult(intent, 2);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && data != null){
            birthDate_ = data.getStringExtra("newBirthDay");
            birthDate.setText(birthDate_);
        }
        else if (requestCode == 1 && data != null){
            email_ = data.getStringExtra("newEmail");
            email.setText(email_);
        }
        else if (requestCode == 2 && data != null){
            password_ = data.getStringExtra("newPassword");
            password.setText(password_);
            password.setTransformationMethod(null);
        }
        else if (requestCode == 3 && data != null){
            fullName_ = data.getStringExtra("newName");
            fullName.setText(fullName_);
        }
        else if (requestCode == 4 && data != null){
            phoneNumber_ = data.getStringExtra("newPhoneNumber");
            phoneNumber.setText(phoneNumber_);
        }
        else if (requestCode == 5 && data != null){
            address_ = data.getStringExtra("newAddress");
            address.setText(address_);
        }
        else if (requestCode == 6 && data != null){
            occupation_ = data.getStringExtra("newOccupation");
            occupation.setText(occupation_);
        }
        else if (requestCode == 7 && data != null){
            institute_ = data.getStringExtra("newInstitute");
            institute.setText(institute_);
        }
        else if (requestCode == 8 && data != null){
            donatedBefore_ = data.getStringExtra("newDonatedBefore");
            donateTimes_ = data.getStringExtra("newDonatetimes");
            lastDonated_ = data.getStringExtra("newLastDonated");
            donatedBefore.setText(donatedBefore_);
            donateTimes.setText(donateTimes_);
            lastDonated.setText(lastDonated_);
        }
        else if (requestCode == 9 && data != null){
            bloodGroup_ = data.getStringExtra("newBloodGroup");
            bloodGroup.setText(bloodGroup_);
        }
        else if (requestCode == 10 && data != null){
            gender_ = data.getStringExtra("newGender");
            gender.setText(gender_);
        }
    }
}
